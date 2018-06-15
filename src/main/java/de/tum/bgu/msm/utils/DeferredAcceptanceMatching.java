package de.tum.bgu.msm.utils;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import javafx.util.Pair;

import java.util.*;

public class DeferredAcceptanceMatching {

    public static Map<Integer, Integer> match(Collection<Integer> set1, Collection<Integer> set2, DoubleMatrix2D preferences) {
        Map<Integer, Integer> matches;
        do {
            matches = iteration(set1, set2, preferences);
        } while (matches.size() < set1.size());
        return matches;
    }

    private static Map<Integer, Integer> iteration(Collection<Integer> set,
                                                                 Collection<Integer> set2,
                                                                 DoubleMatrix2D preferences) {
        Map<Integer, Integer> matches = new HashMap<>();
        Map<Integer, List<Pair<Integer, Double>>> offers = new HashMap<>();
        for (int id: set) {
            double[] max = preferences.viewRow(id).getMaxLocation();
            if (offers.containsKey((int) max[1])) {
                offers.get((int) max[1]).add(new Pair<>(id, max[0]));
            } else {
                List<Pair<Integer, Double>> list = new ArrayList<>();
                list.add(new Pair<>(id, max[0]));
                offers.put((int) max[1], list);
            }
        }

        for (Integer id : set2) {
            if (offers.containsKey(id)) {
                List<Pair<Integer, Double>> personalOffers = offers.get(id);
                if (!personalOffers.isEmpty()) {
                    Pair<Integer, Double> maxOffer = personalOffers.stream().max(Comparator.comparing(Pair::getValue)).get();
                    matches.put(id, maxOffer.getKey());
                    personalOffers.remove(maxOffer);
                    for (Pair<Integer, Double> offer : personalOffers) {
                        preferences.setQuick(offer.getKey(), id, 0);
                    }
                }
            }
        }
        return matches;
    }
}
