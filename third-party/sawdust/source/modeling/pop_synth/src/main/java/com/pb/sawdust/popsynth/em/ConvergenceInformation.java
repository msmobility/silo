package com.pb.sawdust.popsynth.em;

import java.util.Map;
import java.util.Set;

/**
 * The {@code ConvergenceInformation} interface holds and calculates information about the convergence status for a balancing
 * procedure.
 *
 * @author crf
 *         Started 10/8/11 7:21 PM
 */
public interface ConvergenceInformation {
    /**
     * Get a name for the element that this instance is associated with. Generally this will be a geographic element or
     * group of some sort. This is used only for reporting purposes.
     *
     * @return a name for the element associated with this convergence information.
     */
    String getAssociationName();

    /**
     * Get the names of the dimensions which this instance covers.
     *
     * @return the names of this convergence information's dimensions.
     */
    Set<String> getDimensionNames();

    /**
     * The the set of categories for a specified dimension.
     *
     * @param dimensionName
     *        The name of the dimension.
     *
     * @return a set of categories in dimension {@code dimensionName}.
     */
    Set<?> getDimensionIndices(String dimensionName);

    /**
     * Get a mapping from the category names to their respective convergence information for a specified dimension.
     *
     * @param dimension
     *        The dimension.
     *
     * @return a mapping from the category names (indices) for dimension {@code dimension} to their convergence information.
     */
    Map<?,ConvergenceInformationElement> getConvergenceInformation(String dimension);

    /**
     * Get a mapping from each dimension to its category's convergence information. The mapping uses the dimensions's names
     * for it keys, and it values are the same mapping that would be returned from {@link #getConvergenceCriterion}.
     *
     * @return a mapping from this instance's dimensions (names) to their respective convergence information.
     */
    Map<String,Map<?,ConvergenceInformationElement>> getConvergenceInformation();

    /**
     * Get a mapping from the category names for a specified dimension to their respective convergence information for the
     * previous update. The returned map would be the same as that which would have been returned from {@link #getConvergenceInformation(String)}
     * before the last {@link #updateDimension(String,java.util.Map)} call on the dimension. Until two updates have been
     * processed, this method will return the same map as {@code #getConvergenceInformation(String)}.
     * <p>
     * The previous update's convergence information can be useful for reporting when the last update was the result of a
     * procedure outside of the normal balancing procedure (such as a discretization step).
     *
     * @param dimension
     *        The dimension.
     *
     * @return  a mapping from the category names (indices) for dimension {@code dimension} to their previous update's
     *          convergence information.
     */
    Map<?,ConvergenceInformationElement> getPreviousUpdateConvergenceInformation(String dimension);

    /**
     * Get a mapping from each dimension to its category's previous update convergence information. The mapping uses the
     * dimensions's names for it keys, and it values are the same mapping that would be returned from
     * {@link #getPreviousUpdateConvergenceInformation(String)}.
     * <p>
     * The previous update's convergence information can be useful for reporting when the last update was the result of a
     * procedure outside of the normal balancing procedure (such as a discretization step).
     *
     * @return a mapping from this instance's dimensions (names) to their respective previous update convergence information.
     */
    Map<String,Map<?,ConvergenceInformationElement>> getPreviousUpdateConvergenceInformation();

    /**
     * Compute the convergence measure for a given value and target. This measure will be compared with the convergence
     * criterion to determine convergence.
     *
     * @param target
     *        The target.
     *
     * @param value
     *        The current value (control total).
     *
     * @return the convergence measure for value {@code value} and target {@code target}.
     */
    double computeConvergenceMeasure(double target, double value);

    /**
     * Get the convergence criterion for a given dimension.
     *
     * @param dimensionName
     *        The dimension.
     *
     * @return dimension {@code dimensionName}'s convergence criterion.
     */
    double getConvergenceCriterion(String dimensionName);

    /**
     * Update the values (control totals) for a specified dimension.
     *
     * @param dimensionName
     *        The dimension.
     *
     * @param values
     *        A mapping from the dimension's category names to the new values.
     */
    void updateDimension(String dimensionName, Map<?,Double> values);

    /**
     * Determine if a specified dimension is converged or not.
     *
     * @param dimensionName
     *        The dimension.
     *
     * @return {@code true} if dimension {@code dimensionName} is converged, {@code false} if not.
     */
    boolean isConverged(String dimensionName);

    /**
     * Determine whether all dimensions have converged or not.
     *
     * @return {@code true} if all dimensions have converged, {@code false} if not.
     */
    boolean isConverged();

    /**
     * Determine if a specified dimension has met the stopping criteria or not. The stopping criteria is usually that the
     * dimension is converged <i>or</i> some other trigger (such as an update limit) has been met to prevent infinite update
     * loops.
     *
     * @param dimensionName
     *        The dimension.
     *
     * @return {@code true} if dimension {@code dimensionName} has met the stopping criteria, {@code false} if not.
     */
    boolean meetsStoppingCriteria(String dimensionName);

    /**
     * Determine whether all dimensions have met the stopping criteria or not.  The stopping criteria for a given dimension
     * is usually that the dimension is converged <i>or</i> some other trigger (such as an update limit) has been met to
     * prevent infinite update loops.
     *
     * @return {@code true} if all dimensions have met the stopping criteria, {@code false} if not.
     */
    boolean meetsStoppingCriteria();

    /**
     * Get the number of updates processed for a specified dimension.
     *
     * @param dimensionName
     *        The dimension.
     *
     * @return the number of updates recorded for dimension {@code dimension}.
     */
    int getUpdateCount(String dimensionName);

    /**
     * The {@code ConvergenceInformationElement} is a simple container for a single dimension category's convergence information.
     */
    interface ConvergenceInformationElement {
        /**
         * Get the current value (control total) for the category.
         *
         * @return the category's current value.
         */
        double getValue();

        /**
         * Get the target value for the category.
         *
         * @return the category's target value.
         */
        double getTarget();

        /**
         * Get the convergence measure for the category. The convergence measure is what is compared to the dimension's
         * criterion to determine convergence.
         *
         * @return the category's convergence measure.
         */
        double getConvergenceMeasure();
    }
}
