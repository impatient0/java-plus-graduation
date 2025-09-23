package ru.practicum.ewm.aggregator.domain;

import java.util.Map;

public interface EventPairMinWeightSumsRepository {

    /**
     * Retrieves the aggregated sum of the minimum weights between two specified events across all users.
     * For each user, the minimum of their weights for `eventA` and `eventB` is considered,
     * and these minimums are then summed up across all users.
     *
     * @param eventA The ID of the first event.
     * @param eventB The ID of the second event.
     * @return The total sum of the lowest weights between the two events across all users.
     */
    double findSum(long eventA, long eventB);

    /**
     * Saves or updates the aggregated sum of the minimum weights between two specified events across all users.
     * This method stores the pre-calculated sum, which represents the total of the lower weight
     * values for each user across the given pair of events.
     *
     * @param eventA The ID of the first event.
     * @param eventB The ID of the second event.
     * @param sum The calculated sum of the lowest weights between the two events across all users.
     */
    void save(long eventA, long eventB, double sum);

    /**
     * Updates the aggregated sum of minimum weights for a given event pair by adding a delta value.
     * This method atomically updates the existing sum for the pair (eventA, eventB) by adding the provided {@code delta}.
     * It then returns the new, updated sum.
     *
     * @param eventA The ID of the first event in the pair.
     * @param eventB The ID of the second event in the pair.
     * @param delta The value to add to the current aggregated sum.
     * @return The updated aggregated sum of minimum weights for the event pair.
     */
    double updateWithDelta(long eventA, long eventB, double delta);

    /**
     * Retrieves aggregated sums of minimum weights between a given event and all other events across all users.
     * For each other event, the method calculates or retrieves the sum of the lowest weights
     * for each user between the specified event and that "other" event.
     *
     * @param eventId The ID of the primary event for which to find pair sums.
     * @return A {@link Map} where keys are the IDs of the "other" events and values are the
     *         corresponding aggregated sums of minimum weights between {@code eventId} and that other event.
     */
    Map<Long, Double> findAllSums(long eventId);
}