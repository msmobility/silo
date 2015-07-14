package com.pb.sawdust.model.models.provider;

import com.pb.sawdust.tensor.factory.TensorFactory;

/**
 * The {@code FullCalculationDataProvider} is a calculation data provider which performs the full variable calculation
 * (included nested calculations) every time the variable data is requested. This class is therefore inherently inefficient,
 * but can be useful for debugging or verification purposes when compared against other {@code CalculationProvider} implementations.
 *
 * @author crf <br/>
 *         Started 2/17/11 10:25 PM
 */
public class FullCalculationDataProvider extends SimpleCalculationDataProvider {

    /**
     * Constructor specifying the data source identifier, the base provider, and tensor factory used to build data results.
     * This constructor should only be called if the data provider is equivalent to another (already constructed) provider,
     * and that the equivalence needs to be recognized through the data identifier.
     *
     * @param dataId
     *        The identifier for this provider.
     *
     * @param baseProvider
     *        The base provider that will supply the data used in the variable calculations.
     *
     * @param factory
     *        The tensor factory used to build data results.
     *
     * @throws IllegalArgumentException if {@code id} has not already been allocated via {@code AbstractIdData}.
     */
    public FullCalculationDataProvider(int dataId, DataProvider baseProvider, TensorFactory factory) {
        super(dataId, baseProvider, factory);
    }

    /**
     * Constructor specifying the base provider and tensor factory used to build data results.
     *
     * @param baseProvider
     *        The base provider that will supply the data used in the variable calculations.
     *
     * @param factory
     *        The tensor factory used to build data results.
     */
    public FullCalculationDataProvider(DataProvider baseProvider, TensorFactory factory) {
        super(baseProvider, factory);
    }

    protected double[] getVariableData(VariableCalculation calculation) {
        return super.getVariableData(getResolvedCalculation(calculation.getName()));
    }
}
