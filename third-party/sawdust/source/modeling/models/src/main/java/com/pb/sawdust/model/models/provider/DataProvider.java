package com.pb.sawdust.model.models.provider;

import com.pb.sawdust.model.models.trace.CalculationTrace;
import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;
import com.pb.sawdust.tensor.factory.TensorFactory;

import java.util.List;
import java.util.Set;

/**
 * The {@code DataProvider} class is a framework through which {@code double} variable data can be accessed.  A data
 * provider can be (abstractly) thought of as a data table holding {@code double} data, which provides access to the data
 * by columns.
 * <p>
 * Each data provider has a length associated with it, which defines the size of the returned variable data.  A provider
 * cannot be empty, but can be unintialized.  If a provider is uninitalized, attempts to get variable data will result in
 * an {@code IllegalStateException} being thrown; otherwise, the returned variable data will have at least one observation.
 * The observations should always be identically ordered for returned data, even across separate method calls.
 * <p>
 * A data provider allows data for variables to be accessed individually (as a {@code double[]} array) or as a collection
 * (in the form of a {@code DoubleMatrix}). The ordering of the variables in the returned matrix is defined by the caller.
 * That is, the data provider will have the flexibility to place the variables in any order.
 * <p>
 * A data provider is partitionable: it can provide an equivalent {@code DataProvider} holding a (contiguous) subset of
 * variable observations.  This functionality allows the data to be split up for use in distributed applications.  Because
 * the splitting process might be recursive, it is important that the implementation partioning process is efficient.
 *
 * @author crf <br/>
 *         Started Jul 24, 2010 8:10:19 AM
 */
public interface DataProvider extends IdData {
    /**
     * The data length to use if a provider has not been initialized with data. This value is negative.
     */
    final int UNINITIALIZED_DATA_LENGTH = -1;

    /**
     * Get the length of the data from this provider.  Because a {@code DataProvider} cannot provide empty data, this
     * method will never return zero.  If the provider is not initialized with data, then this method will return
     * {@link #UNINITIALIZED_DATA_LENGTH}.
     *
     * @return the length of this provider's data.
     */
    int getDataLength();

    /**
     * Determine if this provider contains data for a specified data.
     *
     * @param variable
     *        The variable in question.
     *
     * @return {@code true} if this provider has data for {@code variable}, {@code false} otherwise.
     */
    boolean hasVariable(String variable);

    /**
     * Get all of the variables this provider has data for.
     *
     * @return the set of variables for which data is available from this provider.
     */
    Set<String> getVariables();

    /**
     * Get the data for a specified variable.
     *
     * @param variable
     *        The variable to get the data for.
     *
     * @return the data for {@code variable}.
     *
     * @throws IllegalArgumentException if this provider does not have data for {@code variable}.
     * @throws IllegalStateException if this provider has not been initialized with any data yet.
     */
    double[] getVariableData(String variable);

    /**
     * Get a contiguous subset of the data held in this provider.
     *
     * @param start
     *        The starting (inclusive) observation for the sub-data.
     *
     * @param end
     *        The ending (exclusive) observation for the sub-data.
     *
     * @return a data provider for observations from {@code start} up to {@code end}.
     *
     * @throws IllegalArgumentException if <code>end &lt;= start</code> or if {@code start} and/or {@code end} are out of
     *                                  this provider's data bounds (<i>i.e.</i> if either are less than zero or greater
     *                                  than the data length).
     */
    DataProvider getSubData(int start, int end);

    /**
     * Get the index to the original source data provider's observation where this data provider starts. If this data
     * provider is the source (<i>i.e.</i> is not a sub-data provider) then this method will return zero.  Otherwise, it
     * returns the observation index in the original data that this sub-data's coverage starts at. This method is useful
     * for joining together work forked through sub-data constructions.
     *
     * @return the index in the source data provider where this provider's observations start.
     */
    int getAbsoluteStartIndex();

    /**
     * Get a matrix holding data for a series of variables.  Each variable is contained in a single matrix column (the
     * matrix's second dimension) and the variable (column) ordering will be the same as the order the variables are
     * passed to this method. A single variable can be repeated more than once.
     *
     * @param variables
     *        The variables to get the data for.
     *
     * @return a matrix holding the data for {@code variables}.
     *
     * @throws IllegalArgumentException if this provider does not have data for any variable in {@code variables}.
     * @throws IllegalStateException if this provider has not been initialized with any data yet.
     */
    DoubleMatrix getData(List<String> variables);

    /**
     * Get a matrix holding data for a series of variables, specifying the factory to construct the output tensor.  Each
     * variable is contained in a single matrix column (the matrix's second dimension) and the variable (column) ordering
     * will be the same as the order the variables are passed to this method. A single variable can be repeated more than once.
     * <p>
     * The specified {@code TensorFactory} will be used to construct the output tensor, but will not necessarily (and
     * probably should not) use it for any intermediate tensors that may be needed.
     *
     * @param variables
     *        The variables to get the data for.
     *
     * @param factory
     *        The tensor factory to use to construct the output tensor.
     *
     * @return a matrix holding the data for {@code variables}.
     *
     * @throws IllegalArgumentException if this provider does not have data for any variable in {@code variables}.
     * @throws IllegalStateException if this provider has not been initialized with any data yet.
     */
    DoubleMatrix getData(List<String> variables, TensorFactory factory);

    /**
     * Get the data for a variable in a specified (contiguous) subset of observations.
     *
     * @param variable
     *        The variable to get the data for.
     *
     * @param start
     *        The starting (inclusive) observation for the data.
     *
     * @param end
     *        The ending (exclusive) observation for the data.
     *
     * @return the data for {@code variable} for observations from {@code start} up to {@code end}.
     *
     * @throws IllegalStateException if this provider has not been initialized with any data yet.
     * @throws IllegalArgumentException if this provider does not have data for {@code variable}, if <code>end &lt;= start</code>
     *                                  or if {@code start} and/or {@code end} are out of this provider's data bounds
     *                                  (<i>i.e.</i> if either are less than zero or greater than the data length).
     */
    double[] getVariableData(String variable, int start, int end);

    /**
     * Get the calculation trace for a variable on a given observation in the provider.  If no sub-calculation is performed
     * to get this variable (that is, if the trace would just wrap a constant value) then this method should return an
     * instance of a {@code ConstantTrace}.
     *
     * @param variable
     *        The variable to get the data for.
     *
     * @param observation
     *        The observation to perform the trace on.
     *
     * @return the calculation trace for {@code variable} on {@code observation}.
     *
     * @throws IllegalArgumentException if this provider does not have data for {@code variable}, or if {@code observation}
     *                                  is out of this provider's data bounds (<i>i.e.</i> is less than zero or greater
     *                                  than or equal to  the data length).
     */
    CalculationTrace getVariableTrace(String variable, int observation);
}
