package ru.practicum.ewm.analyzer.application;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.analyzer.application.config.RecommendationProperties;
import ru.practicum.ewm.analyzer.domain.EventSimilarityRepository;
import ru.practicum.ewm.analyzer.domain.Recommendation;
import ru.practicum.ewm.analyzer.domain.UserInteractionRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationsService {

    private final EventSimilarityRepository similarityRepository;
    private final UserInteractionRepository interactionRepository;

    private final RecommendationProperties properties;

    @Transactional(readOnly = true)
    public List<Recommendation> findSimilarEvents(long eventId, long userId, int maxResults) {
        List<Long> interactedEvents = interactionRepository.findInteractedEvents(userId);
        return similarityRepository.findTopSimilarExcluding(eventId, interactedEvents, maxResults);
    }

    @Transactional(readOnly = true)
    public List<Recommendation> findInteractionsCount(List<Long> eventIds) {
        return interactionRepository.findInteractionsCount(eventIds);
    }

    @Transactional(readOnly = true)
    public List<Recommendation> getUserPredictions(long userId, int maxResults) {

        // Select candidates for suggestion
        List<Long> interactedEvents = interactionRepository.findInteractedEvents(userId);
        List<Long> recentlyInteractedEvents = interactionRepository.findRecentlyInteractedEvents(
            userId, MAX_RECENT_EVENTS);
        List<Long> similarEvents = similarityRepository.findTopSimilarToSet(
            recentlyInteractedEvents, maxResults);

        // Find neighbours for candidates
        Map<Long, List<Recommendation>> neighbourEvents =
            similarityRepository.findNeighbourEventsFrom(
            similarEvents, interactedEvents, MAX_NEIGHBOURS);
        Map<Long, Double> neighbourUserWeights = interactionRepository.findInteractionWeights(
            neighbourEvents.values().stream().flatMap(List::stream).map(Recommendation::getEventId)
                .collect(Collectors.toSet()));

        // Calculate predicted scores based on neighbour scores
        return similarEvents.stream().map(candidateEventId  -> {
            List<Recommendation> neighbours = neighbourEvents.getOrDefault(candidateEventId, List.of());
            if (neighbours.isEmpty()) {
                return new Recommendation(candidateEventId, 0.0); // Nothing to base the prediction on
            }

            // Calculate prediction for candidate
            double weightedSumOfScores = neighbours.stream().mapToDouble(
                neighbour -> neighbour.getScore() * neighbourUserWeights.getOrDefault(
                    neighbour.getEventId(), 0.0)).sum();
            double sumOfSimilarities = neighbours.stream().mapToDouble(Recommendation::getScore).sum();
            double predictedScore = (sumOfSimilarities == 0) ? 0.0 : weightedSumOfScores / sumOfSimilarities;

            return new Recommendation(candidateEventId , predictedScore);
        }).toList();
    }
}
