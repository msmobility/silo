package com.pb.sawdust.popsynth.em;

import com.pb.sawdust.util.probability.Weight;

import java.util.*;

/**
 * The {@code BalanceDimension} represents one dimension of a balance procedure, and calculates the control totals and updated
 * balance element weights for it. A balance dimension is made up of a number of discrete categories, each of which is assigned
 * a constraint value and a number of balance elements; the goal of the balance procedure is to have the sum (across all
 * elements) of the product of the elements' weights and participation rates match the constraint values. The balance dimension
 * holds the procedure for updating the balance element weights to match these control totals (for a given iteration of
 * the balance procedure).
 *
 * @param <T>
 *        The type of the category this balance dimension represents.
 *
 * @author crf
 *         Started 9/30/11 5:46 AM
 */
public class BalanceDimension<T> {
    private final BalanceDimensionClassifier<T> classifier;
    private final Set<Integer> elementIds;
    private final Map<T,List<Weight>> weights;
    private final Map<T,List<Double>> participations;

    /**
     * Constructor specifying the classifier for the dimension and the balance elements which will be matched against the
     * constraints.
     *
     * @param classifier
     *        The classifier for this dimension. The classifier holds procedures for calculating the constraint (target)
     *        totals and the balance element participation rates for each dimension category.
     *
     * @param elements
     *        The balance elements.
     */
    public BalanceDimension(BalanceDimensionClassifier<T> classifier, Iterable<? extends BalanceElement> elements) {
        this.classifier = classifier;
        elementIds = new HashSet<>();
        weights = new HashMap<>();
        participations = new HashMap<>();
        for (T t : getCategorySet()) {
            weights.put(t,new LinkedList<Weight>());
            participations.put(t,new LinkedList<Double>());
        }
        addElements(elements);
    }

    /**
     * Get the set of all categories for this dimension.
     *
     * @return this dimension's categories.
     */
    public Set<T> getCategorySet() {
        return classifier.getClassificationCategories();
    }

    private void addElements(Iterable<? extends BalanceElement> elements) {
        for (BalanceElement element : elements) {
            if (elementIds.contains(element.getId()))
                continue; //already have element
            elementIds.add(element.getId());
            Map<T,Double> pMap = classifier.getParticipationMap(element);
            Weight weight = element.getWeight();
            for (T t : getCategorySet()) {
                double participation = pMap.get(t);
                if (participation > 0.0) {
                    participations.get(t).add(participation);
                    weights.get(t).add(weight);
                }
            }
        }
    }

    /**
     * Get the control totals for this dimension. That is, get a mapping from each category value to the value that the
     * balance elements currently "sum up" to; the goal of the balance procedure is to match this value to the constraint
     * (target) values of the dimension. Each balance element's contribution to the control total is the product of its
     * participation rate in the category and its current weight.
     *
     * @return the category to control total mapping for this dimension.
     */
    public Map<T,Double> getControlTotals() {
        Map<T,Double> controlTotals = new HashMap<>();
        for (T t : getCategorySet()) {
            double controlTotal = 0.0;
            Iterator<Weight> wit = weights.get(t).iterator();
            Iterator<Double> pit = participations.get(t).iterator();
            while (wit.hasNext())
                controlTotal += pit.next()*wit.next().getWeight();
            controlTotals.put(t,controlTotal);
        }
        return controlTotals;
    }


    /**
     * Update the weights of the balance elements for a given set of target values.
     *
     * @param targets
     *        A mapping from the category to the target value.
     *
     * @param weightLimitFactor
     *        A factor used to limit the size of the weights for each balance element. Each balance element's weight will
     *        not exceed the product of its participation rate and this factor. This is useful for preventing the assignment
     *        of excessive weights during the balance procedure' iterations.
     */
    public void updateWeights(Map<T,Double> targets, double weightLimitFactor) {
        Map<T,Double> controls = getControlTotals();
        for (T t : controls.keySet()) {
            double control = controls.get(t);
            if (control == 0.0)
                continue;
            double balanceFactor = targets.get(t) / control;
            Iterator<Weight> wit = weights.get(t).iterator();
            Iterator<Double> pit = participations.get(t).iterator();
            while (wit.hasNext())
                updateWeight(wit.next(),pit.next(),balanceFactor,weightLimitFactor);
        }
    }

    private void updateWeight(Weight weight, double participation, double balanceFactor, double weightLimitFactor) {
        weight.setWeight(Math.min(getNewWeight(weight,participation,balanceFactor),participation*weightLimitFactor));
    }

    /**
     * Get a new weight value for a balance element given its current weight, participation rate, and balance factor.
     * The balance factor is the ratio of target value to the control total value (the goal of the balance procedure is to
     * match the control totals to the targets). The default implementation returns the product of the weight and the
     * balance factor exponentiated by the participation. That is:
     * <pre><code>
     *     weight*balanceFactor<sup>participation</sup>
     * </code></pre>
     *
     * @param weight
     *        The balance element's current weight value.
     *
     * @param participation
     *        The balance element's participation rate for the category.
     *
     * @param balanceFactor
     *        The ratio of the target value to the control total (sum across all balance elements in this dimension) for
     *        the category.
     *
     * @return the new weight for the balance element.
     */
    protected double getNewWeight(Weight weight, double participation, double balanceFactor) {
        return Math.pow(balanceFactor,participation) * weight.getWeight();
    }
}
