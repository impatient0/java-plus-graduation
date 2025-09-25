package ru.practicum.ewm.analyzer.domain;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UserInteractionRepository {

    /**
     * Retrieves a list of event IDs that a specific user has interacted with.
     * This method can be used to understand a user's historical engagement.
     *
     * @param userId The ID of the user whose interactions are to be retrieved.
     * @return A {@link List} of {@code Long} representing the IDs of events the user has interacted with.
     */
    List<Long> findInteractedEvents(long userId);

    /**
     * Retrieves a list of aggregated interaction counts (or sums of maximum interaction weights)
     * for a given collection of events across all users.
     * This can be used to gauge the overall popularity or engagement for each event.
     *
     * @param eventIds A {@link Collection} of event IDs for which to retrieve interaction counts.
     * @return A {@link List} of {@link Recommendation} objects, where each object contains
     *         an event ID and its corresponding aggregated interaction count or sum.
     */
    List<Recommendation> findInteractionsCount(Collection<Long> eventIds);

    /**
     * Retrieves a list of event IDs that a specific user has most recently interacted with,
     * up to a maximum number of results. The events are ordered by recency of interaction.
     *
     * @param userId The ID of the user whose recent interactions are to be retrieved.
     * @param maxResults The maximum number of recently interacted events to return.
     * @return A {@link List} of {@code Long} representing the IDs of events the user has most recently interacted with, ordered by recency.
     */
    List<Long> findRecentlyInteractedEvents(long userId, long maxResults);

    /**
     * Retrieves the maximum interaction weights for a given set of events.
     * This typically represents the highest individual user weight for each event, or an aggregated sum.
     *
     * @param eventIds A {@link Collection} of event IDs for which to retrieve interaction weights.
     * @return A {@link Map} where keys are event IDs and values are their corresponding maximum interaction weights.
     */
    Map<Long, Double> findInteractionWeights(Collection<Long> eventIds);
}