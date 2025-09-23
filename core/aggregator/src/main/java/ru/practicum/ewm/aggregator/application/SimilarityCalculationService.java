package ru.practicum.ewm.aggregator.application;

import java.time.Instant;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.aggregator.application.config.RecommendationProperties;
import ru.practicum.ewm.aggregator.domain.EventPairMinWeightSumsRepository;
import ru.practicum.ewm.aggregator.domain.EventWeightSumsRepository;
import ru.practicum.ewm.aggregator.domain.UserActionType;
import ru.practicum.ewm.aggregator.domain.UserEventWeightsRepository;
import ru.practicum.ewm.aggregator.infrastructure.kafka.EventSimilarityProducer;
import ru.practicum.ewm.stats.kafka.EventSimilarityAvro;

@Service
@Slf4j
@RequiredArgsConstructor
public class SimilarityCalculationService {

    private final EventPairMinWeightSumsRepository eventPairMinWeightSumsRepo;
    private final EventWeightSumsRepository eventWeightSumsRepo;
    private final UserEventWeightsRepository userEventWeightsRepo;
    private final EventSimilarityProducer producer;

    private final RecommendationProperties recommendationProperties;

    private final Map<Long, Object> userLocks = new ConcurrentHashMap<>();

    public void updateSimilarities(long userId, long eventId, UserActionType actionType) {
        log.info("Attempting to update similarities for user {} and event {} with action type {}", userId, eventId, actionType);

        // Create a unique lock object for each user
        Object userLock = userLocks.computeIfAbsent(userId, k -> new Object());

        // Prevent concurrent processing of actions by the same user
        synchronized (userLock) {
            // Fetch the old weight
            double oldWeight = userEventWeightsRepo.findWeight(userId, eventId);

            // Check if the weight increased
            double newWeight = recommendationProperties.getActionWeight(actionType);
            if (newWeight <= oldWeight) {
                log.info("New weight {} is not greater than old weight {}. No update needed for user {} and event {}", newWeight, oldWeight, userId, eventId);
                return;
            }

            // Update weights
            double weightDelta = newWeight - oldWeight;
            double newWeightSum = eventWeightSumsRepo.findWeightSum(eventId) + weightDelta;

            userEventWeightsRepo.save(userId, eventId, newWeight);
            eventWeightSumsRepo.saveWeightSum(eventId, newWeightSum);

            // Update contributions and recalculate similarities
            Map<Long, Double> userEventWeights = userEventWeightsRepo.findWeightsByUserId(userId);
            log.debug("Retrieved all event weights for user {}: {}", userId, userEventWeights.keySet());

            for (Entry<Long, Double> entry : userEventWeights.entrySet()) {
                long otherEventId = entry.getKey();
                if (otherEventId == eventId) {
                    continue; // Skip self-comparison
                }
                double otherEventWeight = entry.getValue();
                log.debug("Processing other event {} with weight {} for user {}", otherEventId, otherEventWeight, userId);

                // Update the minimal weight for user-eventA-eventB combination
                double oldMin = Math.min(oldWeight, otherEventWeight);
                double newMin = Math.min(newWeight, otherEventWeight);

                if (newMin > oldMin) {
                    long eventA = Math.min(eventId, otherEventId);
                    long eventB = Math.max(eventId, otherEventId);
                    double newMinSum = eventPairMinWeightSumsRepo.updateWithDelta(eventA, eventB,
                        newMin - oldMin);

                    // Calculate new similarity score
                    double otherEventWeightSum = eventWeightSumsRepo.findWeightSum(otherEventId);
                    double similarity = newMinSum / Math.sqrt(newWeightSum * otherEventWeightSum);
                    log.debug("Calculated similarity for pair ({}, {}): {} (newMinSum: {}, newWeightSum: {}, otherEventWeightSum: {})",
                        eventA, eventB, similarity, newMinSum, newWeightSum, otherEventWeightSum);

                    EventSimilarityAvro avroMessage = EventSimilarityAvro.newBuilder()
                        .setEventA(eventA)
                        .setEventB(eventB)
                        .setScore(similarity)
                        .setTimestamp(Instant.now())
                        .build();

                    // Publish similarity to Kafka
                    producer.sendEventSimilarity(avroMessage);
                }
            }
        }
        log.info("Finished updating similarities for user {} and event {}", userId, eventId);
    }
}