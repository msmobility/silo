package utils;

import de.tum.bgu.msm.health.data.Trip;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class TripSelector {
    /**
     * Randomly selects a subset of trips from the given map.
     *
     * @param mitoTripsAll The original map of trips (key: Integer, value: Trip)
     * @param subsetSize   The desired size of the subset (must be <= original map size)
     * @return A new map containing the randomly selected subset of trips
     * @throws IllegalArgumentException if subsetSize is larger than original map size
     */
    public static Map<Integer, Trip> selectRandomSubset(Map<Integer, Trip> mitoTripsAll, int subsetSize) {
        if (subsetSize > mitoTripsAll.size()) {
            throw new IllegalArgumentException("Subset size cannot be larger than original map size");
        }

        // Convert the keys to an array for random selection
        Set<Integer> keys = mitoTripsAll.keySet();
        Integer[] keyArray = keys.toArray(new Integer[0]);

        Random random = new Random();
        Map<Integer, Trip> subset = new HashMap<>();

        // Select random keys until we reach the desired subset size
        while (subset.size() < subsetSize) {
            int randomIndex = random.nextInt(keyArray.length);
            Integer randomKey = keyArray[randomIndex];

            // Only add if not already in the subset to avoid duplicates
            if (!subset.containsKey(randomKey)) {
                subset.put(randomKey, mitoTripsAll.get(randomKey));
            }
        }

        return subset;
    }
}