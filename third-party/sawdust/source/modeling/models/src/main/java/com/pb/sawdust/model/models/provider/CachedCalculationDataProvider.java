package com.pb.sawdust.model.models.provider;

import com.pb.sawdust.tensor.factory.TensorFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@code CachedCalculationDataProvider} class provides an extension of {@code SimpleCalculationDataProvider} which
 * caches its calculations wo that they will never be performed more than once. Providers of this class are implicitly
 * "one-off"; that is, once a variables value has been calculated (for a given observation), then any call to retrieve it
 * again will result in the cached value being returned. This means that if any of the argument variables used when calculating
 * the variable are changed after it has been calculated, there is no way to recalculate and override the cached value.
 * This keeps the functionality of the caching straightforward, but means that special care must be taken if using a (partially)
 * mutable base data provider.
 *
 * @author crf <br/>
 *         Started 2/17/11 12:40 PM
 */
public class CachedCalculationDataProvider extends SimpleCalculationDataProvider {
    /* note that all caching is local to provider/subprovider - each new instance must recalculate
            this is ok if all calculations are one-off, but if provider is reused, then percolating the
            caches around would be desired/useful

            however, this creates all kinds of weirdness/difficulty for (potential) remote providers
            I think that it is preferable to keep it simple and (less) efficient and consider each
            provider to be a one-off thing

            the current solution seems good (ideal?):
                values can be precalculated in the base provider, and subproviders can just get their results from that
                OR
                each subprovider can calculate its own section (all of this will be cached, of course)
     */
    private final Map<String,double[]> cache;

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
    public CachedCalculationDataProvider(int dataId, DataProvider baseProvider, TensorFactory factory) {
        super(dataId,baseProvider,factory);
        cache = new ConcurrentHashMap<>();
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
    public CachedCalculationDataProvider(DataProvider baseProvider, TensorFactory factory) {
        super(baseProvider,factory);
        cache = new ConcurrentHashMap<>();
    }

    protected double[] getVariableData(VariableCalculation calculation) {
        String variable = calculation.getName();
        if (!cache.containsKey(variable))
            cache.put(variable,super.getVariableData(calculation));
        return cache.get(variable);
    }

    /**
     * Calculate all of the (calculated) variables in this provider. If a variable has already been calculated, then
     * it will not be updated (the cached version is preferred). Also, if any calculated variables are added <i>after</i>
     * this method has been called, they will not automatically be calculated (until this method is called again or they
     * are requested though one of the other data requesting functions).
     */
    public void calculateAllVariables() {
        for (String variable : getVariables())
            if (getCalculation(variable).isCalculated())
                getVariableData(variable);
    }

    @Override
    public DataProvider getSubData(int start, int end) {
        return new CachedCalculationSubDataProvider(start,end);
    }

    /**
     * The {@code CachedCalculationSubDataProvider} class extends the {@code SimpleCalculationSubDataProvider} to make use
     * of the caching in its parent class.
     */
    protected class CachedCalculationSubDataProvider extends SimpleCalculationSubDataProvider {
        private final Map<String,double[]> cache;

        /**
         * Constructor specifying the beginning and end of the sub-data provider.
         *
         * @param start
         *        The (inclusive) starting observation number for the provider.
         *
         * @param end
         *        The (exclusive) starting observation number for the provider.
         *
         * @throws IllegalArgumentException if <code>end &lt;= start</code> or if {@code start} and/or {@code end} are out of
         *                                  the parent provider's data bounds (<i>i.e.</i> if either are less than zero or
         *                                  greater than the parent data provider's length).
         */
        CachedCalculationSubDataProvider(int start, int end) {
            super(start,end);
            cache = new HashMap<String, double[]>();
        }

        protected double[] getVariableData(VariableCalculation calculation) {
            String variable = calculation.getName();
            if (!cache.containsKey(variable))
                cache.put(variable,super.getVariableData(calculation));
            return cache.get(variable);
        }
    }
}
