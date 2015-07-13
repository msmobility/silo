package com.pb.sawdust.model.models.utility;

import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.model.models.provider.VariableCalculation;
import com.pb.sawdust.model.models.trace.CalculationTrace;
import com.pb.sawdust.tensor.alias.vector.primitive.DoubleVector;
import com.pb.sawdust.tensor.factory.TensorFactory;

import java.util.List;
import java.util.Set;

/**
 * The {@code Utility} interface is used to represent general utility functions.  A utility is a function of certain variables
 * which is used to quantify the benefit of something. This interface does not concern itself with what the utility
 * function is applied to, and thus it represents a general function acting on variables. Its name and placement in
 * this package associate it with economic (or, more specifically, econometric) models, which is what it is used for.
 * <p>
 * In terms of its contract, the {@code Utility} interface places no restriction on a utility's functional form. It
 * only assumes that it acts on variables, and that each variable has an associated constant coefficient. If the latter
 * statement is not true, then the implementing class may document this and ignore the coefficient for a specified
 * variable.
 * <p>
 * All utilities are expected to provide {@code CalculationTrace}s to facilitate model development and debugging. A
 * calculation trace represents the application of the utility function to a given set of variable values (an observation)
 * from a data provider.
 *
 * @author crf <br/>
 *         Jul 24, 2010 8:07:12 AM
 */
public interface Utility {
    /**
     * Get the variables used in this utility. The order of the variables will match the order of the coefficients returned
     * in {@link #getCoefficients()}.
     *
     * @return the variables used in this utility.
     */
    List<String> getVariables();

    /**
     * Get the set of all unique variables in this utility.  The returned set will contain every variable from the result
     * of {@link #getVariables()}.
     *
     * @return the set of all variables used in this utility.
     */
    Set<String> getVariableSet();

    /**
     * Get the coefficients used in this utility.  There will be one coefficient for each variable from {@link #getVariables()},
     * and the order of the coefficients will correspond to the order of the variables returned by that method.
     *
     * @return the coefficients for the variables in the utility function.
     */
    DoubleVector getCoefficients();

    /**
     * Get the results of the utility equation when applied to a series of data observations.  The data will provide a
     * series of observation values for each variable in the utility equation, and the returned results will be ordered
     * the same as the order of the observations in the data provider.
     *
     * @param data
     *        The source of the data observations.
     *
     * @return the values of the utility function when applied to the data in {@code data}.
     *
     * @throws IllegalArgumentException if any variable in this utility is not provided by {@code data}.
     */
    DoubleVector getUtilities(DataProvider data);

    /**
     * Get the name for this utility.  This will generally be used in debugging/tracing utilities to identify the
     * utility.
     *
     * @return the name of this utility.
     */
    String getName();

    /**
     * Get a calculation trace for the utility function when applied to a given observation in a data provider.
     *
     * @param data
     *        The source of the data observations.
     *
     * @param observation
     *        The (0-based) observation number from {@code data}.
     *
     * @return the trace of the utility function for the variable values ov {@code observation} in {@code data}.
     *
     * @throws IllegalArgumentException if {@code observation} is negative or greater than or equal to the length of
     *                                  {@code data}.
     */
    //CalculationTrace traceCalculation(DataProvider data, int observation);
    CalculationTrace traceCalculation(DataProvider data, int observation);

    /**
     * Get a representation of this utility as a variable calculation. This can be useful for abstracting out the function
     * represented by this utility, but for actual utility calculations, {@link #getUtilities(com.pb.sawdust.model.models.provider.DataProvider)}
     * is preferred.
     *
     * @return the function represented by this utility as a variable calculation.
     */
    VariableCalculation getCalculation();
}
