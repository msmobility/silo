package com.pb.sawdust.popsynth.em;

import java.util.LinkedList;
import java.util.List;

/**
 * The {@code CompositeBalanceGroup} class provides a {@code Balancer} implementation which is just a collection of
 * {@code Balancer}s. This class is useful for creating balancers that can be compared across different geographies (if
 * one geography element maps to three elements of a different geography, then the balancer of the former should be matched
 * with a composite of the latter's three representative balancers in the balance procedure).
 *
 * @author crf
 *         Started 10/8/11 7:14 PM
 */
public class CompositeBalancer implements Balancer {
    private final List<Balancer> balancers;
    private final String associationName;

    /**
     * Constructor specifying a name for the composition, as well as the balancers that it comprises.
     *
     * @param associationName
     *        The name for the composition. This will be used to represent the group in its convergence information.
     *
     * @param balancers
     *        The balancers that will be held by the balance group.
     */
    public CompositeBalancer(String associationName, List<? extends Balancer> balancers) {
        this.balancers = new LinkedList<>(balancers);
        this.associationName = associationName;
    }

    @Override
    public void updateWeights() {
        for (Balancer balancer : balancers)
            balancer.updateWeights();
    }

    @Override
    public void updateControlsAndTargets() {
        for (Balancer balancer : balancers)
            balancer.updateControlsAndTargets();
    }

    @Override
    public void balance() {
        while (!getConvergenceInformation().meetsStoppingCriteria())
            updateWeights();
    }

    @Override
    public ConvergenceInformation getConvergenceInformation() {
        List<ConvergenceInformation> ciList = new LinkedList<>();
        for (Balancer balancer : balancers)
            ciList.add(balancer.getConvergenceInformation());
        return new CompositeConvergenceInformation(ciList,associationName);
    }
}
