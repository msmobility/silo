package com.pb.sawdust.popsynth.em;

/**
 * The {@code Balancer} interface provides a structure through which the weights of a collection of balance elements
 * are balanced across various dimensions.
 *
 * @author crf
 *         Started 10/8/11 7:11 PM
 */
public interface Balancer {
    /**
     * Update the weights for all dimensions given the current controls and targets. The current targets will be updated
     * after all of the weights have been computed.
     */
    void updateWeights();

    /**
     * Update the controls and targets given the current weights. This method is useful for situations where the balance
     * element weights are modified outside of this class (such as with a discretization step).
     */
    void updateControlsAndTargets();

    /**
     * Update the balance element weights until the stopping criteria is met. This method should just call {@link #updateWeights()}
     * continuously until {@code getConvergenceInformation().meetsStoppingCriteria() == true}.
     */
    void balance();

    /**
     * Get the convergence information for this group.
     *
     * @return this balance group's convergence information.
     */
    ConvergenceInformation getConvergenceInformation();
}
