package com.pb.sawdust.popsynth.em;

import com.pb.sawdust.tabledata.DataRow;

import java.util.*;

/**
 * The {@code BaseBalancer} ...
 *
 * @author crf
 *         Started 9/30/11 6:22 AM
 */
public class BaseBalancer implements Balancer {
    private final List<String> dimensionNames;
    private final Map<String,BalanceDimension<?>> balanceDimensions;
    private final Map<String,Map<?,Double>> targets;
    private final ConvergenceInformation convergenceInformation;
    private final double weightLimitFactor;

    @SuppressWarnings("unchecked") //ok because we are transferring all unknown type arguments to ?
    public BaseBalancer(String associationName, Map<BalanceDimensionClassifier,Double> criteria, int maximumIterations, Iterable<? extends BalanceElement> elements, DataRow targetData, double weightLimitFactor) {
        balanceDimensions = new HashMap<>();
        targets = new HashMap<>();
        dimensionNames = new LinkedList<>();

        Map<String,Double> convergenceCriteria = new HashMap<>();
        for (BalanceDimensionClassifier classifier : criteria.keySet()) {
            String name = classifier.getDimensionName();
            dimensionNames.add(name);
            balanceDimensions.put(name,new BalanceDimension(classifier,elements));
            targets.put(name,buildTargets(classifier,targetData));
            convergenceCriteria.put(name,criteria.get(classifier));
        }
        this.weightLimitFactor = weightLimitFactor;
        convergenceInformation = new BaseConvergenceInformation(associationName,targets,convergenceCriteria,maximumIterations);
    }

    public BaseBalancer(String associationName, Collection<BalanceDimensionClassifier> classifiers, double criteria, int maximumIterations, Iterable<? extends BalanceElement> elements, DataRow targetData, double weightLimitFactor) {
        this(associationName,buildCriteria(classifiers,criteria),maximumIterations,elements,targetData,weightLimitFactor);
    }

    @SuppressWarnings("unchecked") //cast to <?> is valid here, can't construct at first because it is then unmodifiable
    private static Map<BalanceDimensionClassifier,Double> buildCriteria(Collection<BalanceDimensionClassifier> classifiers, double criteria) {
        Map<BalanceDimensionClassifier,Double> m = new HashMap<>();
        for (BalanceDimensionClassifier classifier : classifiers)
            m.put(classifier,criteria);
        return m;
    }

    @SuppressWarnings("unchecked") //we don't know about a specific T, but targets will match up to balance dimensions
    public void updateWeights() {
        for (String dimensionName : dimensionNames) {
            BalanceDimension bd = balanceDimensions.get(dimensionName);
            Map<?,Double> target = targets.get(dimensionName);
            bd.updateWeights(target,weightLimitFactor);
            convergenceInformation.updateDimension(dimensionName,bd.getControlTotals());
        }
    }

    public void updateControlsAndTargets() {
        for (String dimensionName : dimensionNames)
            convergenceInformation.updateDimension(dimensionName,balanceDimensions.get(dimensionName).getControlTotals());
    }

    public void balance() {
        while (!convergenceInformation.meetsStoppingCriteria())
            updateWeights();
    }

    public ConvergenceInformation getConvergenceInformation() {
        return convergenceInformation;
    }

    private <T> Map<T,Double> buildTargets(BalanceDimensionClassifier<T> classifier, DataRow targetData) {
        return new LinkedHashMap<>(classifier.getTargetMap(targetData)); //to preserve whatever ordering might be present in elements
    }

}
