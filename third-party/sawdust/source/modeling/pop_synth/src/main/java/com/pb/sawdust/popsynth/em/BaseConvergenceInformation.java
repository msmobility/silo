package com.pb.sawdust.popsynth.em;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@code BaseConvergenceInformation} class provides a basic implementation of the {@code ConvergenceInformation} interface.
 *
 * @author crf
 *         Started 9/30/11 6:44 AM
 */
public class BaseConvergenceInformation implements ConvergenceInformation {
    private final Map<String,Map<?,ConvergenceInformationElement>> boundedConvergenceInformation;
    private final Map<String,Map<?,BaseConvergenceInformationElement>> convergenceInformation;
    private final Map<String,Map<?,ConvergenceInformationElement>> boundedPreviousConvergenceInformation;
    private final Map<String,Map<?,BaseConvergenceInformationElement>> previousConvergenceInformation;
    private final Map<String,Double> convergenceCriteria;
    private final int maxUpdates;
    private final Map<String,AtomicInteger> updateCount;
    private final String associationName;

    /**
     * Constructor specifying the information's targets, convergence and stopping criteria, as well as its association name.
     *
     * @param associationName
     *        The association name to use for the convergence information.
     *
     * @param targets
     *        A mapping from the dimension names to a mapping from the dimension categories to their respective target values.
     *        This mapping is used to specify all of the dimensions and dimension categories in the convergence information.
     *
     * @param convergenceCriteria
     *        A mapping from each dimension name to their respective convergence criteria.
     *
     * @param maxUpdates
     *        The maximum number of updates allowed before the stopping criteria is considered to be met (irregardless of
     *        whether or not the convergence criterium has been met).
     *
     * @throws IllegalArgumentException if the keys for {@code targets} and {@code convergenceCriteria} are not equal.
     */
    @SuppressWarnings("unchecked") //aaahhhh, generics fun so we can use one map for two (they are unmodifiable, so it doesn't matter
    public BaseConvergenceInformation(String associationName, Map<String,Map<?,Double>> targets, Map<String,Double> convergenceCriteria, int maxUpdates) {
        if (!convergenceCriteria.keySet().equals(targets.keySet()))
            throw new IllegalArgumentException("Targets and convergence criteria keys sets must be equal:\n  " + targets.keySet() + "\n  " + convergenceCriteria.keySet());
        this.convergenceCriteria = new HashMap<>(convergenceCriteria);
        this.maxUpdates = maxUpdates;
        updateCount = new HashMap<>();

        Map<String,Map<Object,? extends ConvergenceInformationElement>> boundedConvergenceInformation = new HashMap<>();
        Map<String,Map<Object,? extends ConvergenceInformationElement>> previousConvergenceInformation = new HashMap<>();
        for (String dimension : targets.keySet()) {
            updateCount.put(dimension,new AtomicInteger(0));
            Map<Object,ConvergenceInformationElement> bci = new LinkedHashMap<>();
            Map<Object,ConvergenceInformationElement> pci = new LinkedHashMap<>();
            Map<?,Double> targetMap = targets.get(dimension);
            for (Object key : targetMap.keySet()) {
                double target = targetMap.get(key);
                bci.put(key,getConvergenceInformationElement(target));
                pci.put(key,new BaseConvergenceInformationElement(target));
            }
            boundedConvergenceInformation.put(dimension,Collections.unmodifiableMap(bci));
            previousConvergenceInformation.put(dimension,Collections.unmodifiableMap(pci));
        }
        boundedConvergenceInformation = Collections.unmodifiableMap(boundedConvergenceInformation);
        previousConvergenceInformation = Collections.unmodifiableMap(previousConvergenceInformation);
        this.boundedConvergenceInformation = (Map<String,Map<?,ConvergenceInformationElement>>) (Map) boundedConvergenceInformation;
        this.convergenceInformation = (Map<String,Map<?,BaseConvergenceInformationElement>>) (Map) boundedConvergenceInformation;
        this.boundedPreviousConvergenceInformation = (Map<String,Map<?,ConvergenceInformationElement>>) (Map) previousConvergenceInformation;
        this.previousConvergenceInformation = (Map<String,Map<?,BaseConvergenceInformationElement>>) (Map) previousConvergenceInformation;
        this.associationName = associationName;
    }

    /**
     * Get a {@code ConvergenceInformationElement} instance for a given target. The value (control total) and convergence
     * criteria will not have been initialized in the returned instance. This is a convenience method to avoid having to
     * implement {@code ConvergenceInformationElement} in extending classes.
     *
     * @param target
     *        The target value.
     *
     * @return a convergence information element for {@code target}.
     */
    protected ConvergenceInformationElement getConvergenceInformationElement(double target) {
        return new BaseConvergenceInformationElement(target);
    }

    @Override
    public String getAssociationName() {
        return associationName;
    }

    @Override
    public Set<String> getDimensionNames() {
        return convergenceInformation.keySet();
    }

    @Override
    public Set<?> getDimensionIndices(String dimensionName) {
        return convergenceInformation.get(dimensionName).keySet();
    }

    @Override
    public Map<?,ConvergenceInformationElement> getConvergenceInformation(String dimension) {
        return boundedConvergenceInformation.get(dimension);
    }

    @Override
    public Map<String,Map<?,ConvergenceInformationElement>> getConvergenceInformation() {
        return boundedConvergenceInformation;
    }

    @Override
    public Map<?,ConvergenceInformationElement> getPreviousUpdateConvergenceInformation(String dimension) {
        return boundedPreviousConvergenceInformation.get(dimension);
    }

    @Override
    public Map<String,Map<?,ConvergenceInformationElement>> getPreviousUpdateConvergenceInformation() {
        return boundedPreviousConvergenceInformation;
    }

    @Override
    public double computeConvergenceMeasure(double target, double value) {
        return Math.abs(target > 0.0 ? value / target - 1.0 : (value - target) / .0000001); //latter ensures numeric feasibility - if target is 0, then value should be too
    }

    @Override
    public double getConvergenceCriterion(String dimensionName) {
        return convergenceCriteria.get(dimensionName);
    }

    @Override
    public void updateDimension(String dimensionName, Map<?,Double> values) {
        Map<?,BaseConvergenceInformationElement> ci = convergenceInformation.get(dimensionName);
        Map<?,BaseConvergenceInformationElement> pci = previousConvergenceInformation.get(dimensionName);
        for (Object key : ci.keySet()) { //assumes a full set to update
            BaseConvergenceInformationElement cie = ci.get(key);
            if (cie.initialized)
                pci.get(key).update(cie.getValue(),cie.getConvergenceMeasure());
            double value = values.get(key);
            cie.update(value,computeConvergenceMeasure(cie.getTarget(),value));
        }
        updateCount.get(dimensionName).incrementAndGet();
    }

    @Override
    public boolean isConverged(String dimensionName) {
        Map<?,BaseConvergenceInformationElement> ci = convergenceInformation.get(dimensionName);
        double criterion = convergenceCriteria.get(dimensionName);
        for (BaseConvergenceInformationElement cie : ci.values())
            if (!cie.initialized || cie.getConvergenceMeasure() > criterion)
                return false;
        return true;
    }

    @Override
    public boolean isConverged() {
        for (String dimension : getDimensionNames())
            if (!isConverged(dimension))
                return false;
        return true;
    }

    @Override
    public boolean meetsStoppingCriteria(String dimensionName) {
        return getUpdateCount(dimensionName) >= maxUpdates || isConverged(dimensionName);
    }

    @Override
    public boolean meetsStoppingCriteria() {
        for (String dimension : getDimensionNames())
            if (!meetsStoppingCriteria(dimension))
                return false;
        return true;
    }

    @Override
    public int getUpdateCount(String dimensionName) {
        return updateCount.get(dimensionName).get();
    }

    private class BaseConvergenceInformationElement implements ConvergenceInformationElement {
        private final double target;
        private volatile double convergenceMeasure;
        private volatile double value;
        private volatile boolean initialized;

        private BaseConvergenceInformationElement(double target) {
            this.target = target;
            initialized = false;
        }

        private void update(double value, double convergenceMeasure) {
            this.value = value;
//            System.out.println(target + " " + value + " " + convergenceMeasure);
            this.convergenceMeasure = convergenceMeasure;
            initialized = true;
        }

        private void checkInitialization() {
            if (!initialized)
                throw new IllegalStateException("Convergence information not initialized yet.");
        }

        @Override
        public double getValue() {
            checkInitialization();
            return value;
        }

        @Override
        public double getTarget() {
            return target;
        }

        @Override
        public double getConvergenceMeasure() {
            checkInitialization();
            return convergenceMeasure;
        }
    }
}
