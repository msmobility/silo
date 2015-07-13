package com.pb.sawdust.model.models.logit;

import com.pb.sawdust.calculator.NumericFunctions;
import com.pb.sawdust.calculator.tensor.CellWiseTensorCalculation;
import com.pb.sawdust.calculator.tensor.DefaultCellWiseTensorCalculation;
import com.pb.sawdust.model.models.AbstractDiscreteChoiceModel;
import com.pb.sawdust.model.models.provider.filter.DataFilter;
import com.pb.sawdust.model.models.provider.filter.FilteredDataProvider;
import com.pb.sawdust.model.models.trace.CalculationTrace;
import com.pb.sawdust.model.models.trace.ConstantTrace;
import com.pb.sawdust.model.models.trace.LabelTrace;
import com.pb.sawdust.model.models.utility.Utility;
import com.pb.sawdust.model.models.Choice;
import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.model.models.provider.IdData;
import com.pb.sawdust.model.models.provider.hub.DataProviderHub;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.alias.vector.primitive.BooleanVector;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.alias.vector.primitive.DoubleVector;
import com.pb.sawdust.tensor.index.SliceIndex;
import com.pb.sawdust.util.collections.cache.Cache;
import com.pb.sawdust.util.collections.cache.DefinedCache;

import java.util.*;

/**
 * The {@code LogitModel} class provides the functionality for working with logit discete choice models.  A logit model
 * is a discrete choice model where the probability of choice <code><i>i</i></code> is given by
 * <p>
 * <pre><code>
 *     exp(U<sub><i>i</i></sub>) / &#x3A3;<sub><i>i</i></sub>(exp(U<sub><i>i</i></sub>))
 * </code></pre>
 *<p>
 * where U<sub><i>i</i></sub> is the choice's utility.
 * <p>
 * This class uses an internal cache to store various steps in the logit model calculations tied to {@code DataProvider}
 * instances. This cache is only cleared (fully or partially) when one the {@code clearCache} methods are called.  Depending
 * on the size of the input data, this cache has the potential to grow quite large, so care must be taken to monitor its
 * use to avoid inadvertent memory leaks.
 *
 * @param <C>
 *        The type of the model choices.
 *
 * @author crf <br/>
 *         Started Jul 24, 2010 10:34:11 AM
 */
public class LogitModel<C extends Choice> extends AbstractDiscreteChoiceModel<C> {
    /**
     * The utility value indicating that a given choice is unavailable.
     */
    public static final double UNAVAILABLE_UTILITY = -9999;

    /**
     * Get the standard name for a utility applied to a given choice. This method returns the following:
     * <p>
     * <pre><code>
     *     "utility for choice [" + choice +  "]"
     * </code></pre>
     *
     * @param choice
     *        The choice the utility is applied to.
     *
     * @param <C>
     *        The type of the choice.
     *
     * @return the name of the utility.
     */
    public static <C extends Choice> String getUtilityName(C choice) {
        return "utility for choice [" + choice +  "]";
    }

    private final Map<C,Utility> utilities;
    final TensorFactory factory;
    final CellWiseTensorCalculation cwtc;
    private final Cache<Integer,Map<C,DoubleVector>> utilityCache;
    private final String name;
    private final Map<C,DataFilter> availabilityFilters;

    /**
     * Constructor specifying model name, the choice utilities, and the tensor factory used to build results.
     *
     * @param name
     *        The name of the model.
     *
     * @param utilities
     *        A mapping from the model choices to the choice utility function.
     *
     * @param factory
     *        The tensor factory used to build results.
     */
    public LogitModel(String name, Map<C,? extends Utility> utilities, TensorFactory factory) {
        this(name,utilities,null,factory);
    }

    /**
     * Constructor specifying the choice utilities and the tensor factory used to build results. An empty string will
     * be used for the name of the model.
     *
     * @param utilities
     *        A mapping from the model choices to the choice utility function.
     *
     * @param factory
     *        The tensor factory used to build results.
     */
    public LogitModel(Map<C,? extends Utility> utilities, TensorFactory factory) {
        this("",utilities,factory);
    }

    /**
     * Constructor specifying model name, the choice utilities, the availability filters, and the tensor factory used to
     * build results. The availability filters are used to determine whether, for a given observation, a choice is available
     * or not. If a given choice's filter returns {@code false} for a given observation in a data provider, then that
     * choice will be unavailable and its exponentiated utility assigned a value of <code>0.0</code>.
     *
     * @param name
     *        The name of the model.
     *
     * @param utilities
     *        A mapping from the model choices to the choice utility function.
     *
     * @param availabilityFilters
     *        A mapping from model choices to the availability filters. If a choice is missing from the mapping, then it
     *        is assumed that it is always available.
     *
     * @param factory
     *        The tensor factory used to build results.
     *
     * @throws IllegalArgumentException if any choice in keys for {@code availabilityFilters} is not found in the keys for
     *                                     {@code utilities}.
     */
    public LogitModel(String name, Map<C,? extends Utility> utilities, Map<C,DataFilter> availabilityFilters, TensorFactory factory) {
        Map<C,Utility> ut = new HashMap<C,Utility>();
        ut.putAll(utilities);
        this.utilities = Collections.unmodifiableMap(ut);
        this.factory = factory;
        cwtc = new DefaultCellWiseTensorCalculation(factory);
        utilityCache = new DefinedCache<Integer,Map<C,DoubleVector>>();
        this.name = name;
        this.availabilityFilters = new HashMap<C, DataFilter>();
        if (availabilityFilters != null)
            for (C c : availabilityFilters.keySet()) {
                if (!utilities.containsKey(c))
                    throw new IllegalArgumentException(String.format("Filter choice (%s) not found in utilities.",c));
                this.availabilityFilters.put(c,availabilityFilters.get(c));
            }
    }

    /**
     * Constructor specifying the choice utilities, the availability filters, and the tensor factory used to build results.
     * An empty string will be used for the name of the model.The availability filters are used to determine whether, for
     * a given observation, a choice is available or not. If a given choice's filter returns {@code false} for a given
     * observation in a data provider, then that choice will be unavailable and its exponentiated utility assigned a value
     * of <code>0.0</code>.
     *
     * @param utilities
     *        A mapping from the model choices to the choice utility function.
     *
     * @param availabilityFilters
     *        A mapping from model choices to the availability filters. If a choice is missing from the mapping, then it
     *        is assumed that it is always available.
     *
     * @param factory
     *        The tensor factory used to build results.
     *
     * @throws IllegalArgumentException if any choice in keys for {@code availabilityFilters} is not found in the keys for
     *                                     {@code utilities}.
     */
    public LogitModel(Map<C,? extends Utility> utilities, Map<C,DataFilter> availabilityFilters, TensorFactory factory) {
        this("",utilities,availabilityFilters,factory);
    }

    /**
     * Get the model name.
     *
     * @return the name of this model.
     */
    public String getName() {
        return name;
    }

    Map<C,DoubleVector> getUtilityMap(IdData data) {
        int id = data.getDataId();
        if (!utilityCache.containsKey(id))
            utilityCache.put(id,new HashMap<C,DoubleVector>());
        return utilityCache.get(id);
    }

    /**
     * Get the mapping from this model's choices to their respective utilities.
     *
     * @return the choice to utility mapping for this model.
     */
    protected Map<C,? extends Utility> getUtilities() {
        return utilities;
    }

    public Set<C> getChoices() {
        return utilities.keySet();
    }

    private DoubleVector getExponentiatedUtilities(C choice, DataProvider data, IdData sourceData) {
        if (!getChoices().contains(choice))
            throw new IllegalArgumentException(String.format("Choice not available from model '%s': %s",name,choice.toString()));
        Map<C,DoubleVector> utilityMap = getUtilityMap(sourceData);
        if (!utilityMap.containsKey(choice)) {
            DataFilter availabilityFilter = availabilityFilters.get(choice);
            DoubleVector v;
            if (availabilityFilter == null) {
                v = (DoubleVector) cwtc.calculate(getUtilities().get(choice).getUtilities(data),NumericFunctions.EXP);
            } else {
                v = factory.doubleVector(data.getDataLength()); //result vector
                DoubleVector r = (DoubleVector) cwtc.calculate(getUtilities().get(choice).getUtilities(new FilteredDataProvider(factory,data,availabilityFilter)),NumericFunctions.EXP); //only available utilities
                v.getReferenceTensor(SliceIndex.getSliceIndex(v.getIndex(),availabilityFilter.getFilteredSlice(data))).setTensorValues(r); //fill in available data
                TensorUtil.fill(v.getReferenceTensor(SliceIndex.getSliceIndex(v.getIndex(),availabilityFilter.getUnfilteredSlice(data))),0.0);
            }
            utilityMap.put(choice,v);
        }
        return utilityMap.get(choice);
    }

    /**
     * Get the exponentiated utilities for a given choice on the provided data. The order of the results in the returned
     * vector will be the same as the data in the data provider.
     *
     * @param choice
     *        The choice to get the exponentiated utilities for.
     *
     * @param data
     *        The data provider to use to calculate the utilities.
     *
     * @return the exponentiated utilities for {@code choice} on {@code data}.
     *
     * @throws IllegalArgumentException if {@code data} does not contain all of the variable required by {@code choice}'s
     *                                  utility function, or if {@code choice} is not a valid choice for this model.
     */
    public DoubleVector getExponentiatedUtilities(C choice, DataProvider data) {
        return getExponentiatedUtilities(choice,data,data);
    }

    /**
     * Get the expsums for this model on the provided data. An expsum is the sum of exponentiated utilities across all
     * choices in the model; it is used as the denominator of the individual choice probability calculations. The order
     * of the expsums in the returned vector will be the same as the data in the data provider.
     *
     * @param data
     *        The data provider to use to calculate the expsums.
     *
     * @return the expsums calculated on {@code data}.
     *
     * @throws IllegalArgumentException if {@code data} does not contain all of the variables required by this model's
     *                                  utility functions.
     */
    public DoubleVector getExpsums(DataProvider data) {
        DoubleVector expsum = (DoubleVector) factory.doubleTensor(data.getDataLength());
        for (C choice : getUtilities().keySet()) {
            expsum = ((DoubleVector) cwtc.calculate(expsum,getExponentiatedUtilities(choice,data),NumericFunctions.ADD));
        }
        return expsum;
    }

    /**
     * Get the logsums for this model on the provided data. An logsum is the logarithm of the model's expsum. The order
     * of the logsums in the returned vector will be the same as the data in the data provider.
     *
     * @param data
     *        The data provider to use to calculate the logsums.
     *
     * @return the logsums calculated on {@code data}.
     *
     * @throws IllegalArgumentException if {@code data} does not contain all of the variables required by this model's
     *                                  utility functions.
     */
    public DoubleVector getLogsums(DataProvider data) {
        return (DoubleVector) cwtc.calculate(getExpsums(data),NumericFunctions.boundedSafeLog(UNAVAILABLE_UTILITY));
    }

    public DoubleVector getProbabilities(C choice, DataProvider data) {
        DoubleVector expsum = getExpsums(data);
        return (DoubleVector) cwtc.calculate(getExponentiatedUtilities(choice,data),expsum,NumericFunctions.ZERO_SAFE_DIVIDE);
    }

    /**
     * Get the exponentiated utilities for a given choice on the provided data. The data provider hub can provide different
     * variable data by choice. The order of the results in the returned vector will be the same as the data in the data
     * provider hub.
     *
     * @param choice
     *        The choice to get the exponentiated utilities for.
     *
     * @param data
     *        The data provider hub to use to calculate the utilities.
     *
     * @return the exponentiated utilities for {@code choice} on {@code data}.
     *
     * @throws IllegalArgumentException if the providers in {@code data} do not contain all of the variable required by
     *                                  {@code choice}'s utility function, or if {@code choice} is not a valid choice for
     *                                  this model or {@code data}.
     */
    public DoubleVector getExponentiatedUtilities(C choice, DataProviderHub<C> data) {
        return getExponentiatedUtilities(choice,data.getProvider(choice),data);
    }

    /**
     * Get the expsums for this model on the provided data. An expsum is the sum of exponentiated utilities across all
     * choices in the model; it is used as the denominator of the individual choice probability calculations. The data provider
     * hub can provide different variable data by choice. The order of the expsums in the returned vector will be the same
     * as the data in the data provider hub.
     *
     * @param data
     *        The data provider hub to use to calculate the expsums.
     *
     * @return the expsums calculated on {@code data}.
     *
     * @throws IllegalArgumentException if the providers in {@code data} do not contain all of the variable required by
     *                                  their respective choice's utility function, or if {@code data} does not have a
     *                                  provider for each choice in this model.
     */
    public DoubleVector getExpsums(DataProviderHub<C> data) {
        DoubleVector expsum = (DoubleVector) factory.doubleTensor(data.getDataLength());
        for (C choice : getUtilities().keySet())
            expsum = ((DoubleVector) cwtc.calculate(expsum,getExponentiatedUtilities(choice,data),NumericFunctions.ADD));
        return expsum;
    }

    /**
     * Get the logsums for this model on the provided data. An logsum is the logarithm of the model's expsum. The data provider
     * hub can provide different variable data by choice. The order of the logsums in the returned vector will be the same
     * as the data in the data provider.
     *
     * @param data
     *        The data provider hub to use to calculate the logsums.
     *
     * @return the logsums calculated on {@code data}.
     *
     * @throws IllegalArgumentException if the providers in {@code data} do not contain all of the variable required by
     *                                  their respective choice's utility function, or if {@code data} does not have a
     *                                  provider for each choice in this model.
     */
    public DoubleVector getLogsums(DataProviderHub<C> data) {
        return (DoubleVector) cwtc.calculate(getExpsums(data),NumericFunctions.LOG);
    }

    public DoubleVector getProbabilities(C choice, DataProviderHub<C> data) {
        DoubleVector expsum = getExpsums(data);
        return (DoubleVector) cwtc.calculate(getExponentiatedUtilities(choice,data),expsum,NumericFunctions.ZERO_SAFE_DIVIDE);
    }

    @Override
    public BooleanVector getAvailabilities(C choice, DataProvider data) {
        if (!getChoices().contains(choice))
            throw new IllegalArgumentException(String.format("Choice not available from model '%s': %s",name,choice.toString()));
        DataFilter availabilityFilter = availabilityFilters.get(choice);
        return availabilityFilter == null ? factory.initializedBooleanVector(true,data.getDataLength()) : availabilityFilter.getFilter(data);
    }

    /**
     * Clear the calculation cache of the results calculated on {@code data}. The identifier for {@code data} is used
     * to identify the appropriate results.
     *
     * @param data
     *        The data whose calculations are to be cleared.
     */
    public void clearCache(IdData data) {
        utilityCache.remove(data.getDataId());
    }

    /**
     * Clear the calculation cache of all results.
     */
    public void clearCache() {
        utilityCache.clear();
    }

    protected Map<C,DataFilter> getAvailabilityFilters() {
        return availabilityFilters;
    }

    public Map<C,CalculationTrace> traceCalculation(DataProvider data, int observation) {
        double expsum = getExpsums(data.getSubData(observation,observation+1)).getCell(0);
        Map<C,CalculationTrace> utilityTrace = new HashMap<C, CalculationTrace>();
        for (C choice : getChoices())
            utilityTrace.put(choice,traceCalculation(choice,data,observation,expsum));
        return utilityTrace;
    }

    public CalculationTrace traceCalculation(C choice, DataProvider data, int observation) {
        return traceCalculation(choice,data,observation,getExpsums(data.getSubData(observation,observation+1)).getCell(0));
    }

    public Map<C,CalculationTrace> traceCalculation(DataProviderHub<C> data, int observation) {
        double expsum = getExpsums(data.getSubDataHub(observation, observation + 1)).getCell(0);
        Map<C,CalculationTrace> utilityTrace = new HashMap<C,CalculationTrace>();
        for (C choice : getChoices())
            utilityTrace.put(choice,traceCalculation(choice,data.getProvider(choice),observation,expsum));
        return utilityTrace;
    }

    public CalculationTrace traceCalculation(C choice, DataProviderHub<C> data, int observation) {
        return traceCalculation(choice,data.getProvider(choice),observation,getExpsums(data.getSubDataHub(observation,observation+1)).getCell(0));

    }

    protected CalculationTrace traceCalculation(C choice, DataProvider data, int observation, double expsum) {
        DataFilter availabilityFilter = availabilityFilters.get(choice);
        if (availabilityFilter != null) {
            boolean available = getAvailabilities(choice,data).getCell(observation);
            if (!available) {
                CalculationTrace trace = new CalculationTrace("Probability for choice " + choice + " (unavailable)",0.0);
                trace.addTraceElement(availabilityFilter.traceFilterCalculation(data,observation));
                return trace; //unavailable, so trace ends here
            }
        }
        CalculationTrace choiceTrace = getUtilities().get(choice).traceCalculation(data,observation);
        double result = choiceTrace.getResult();
        CalculationTrace trace = new CalculationTrace("Probability for choice " + choice,Math.exp(result)/expsum);
        CalculationTrace numerator = new CalculationTrace("exp(" + getUtilityName(choice) + ")","exp(" + result + ")",Math.exp(result));
        CalculationTrace denominator = new ConstantTrace("expsum",expsum);
        trace.addTraceElement(numerator);
        trace.addTraceElement("/",denominator);
        numerator.addTraceElement(new LabelTrace("exp("));
        numerator.addTraceElement(choiceTrace);
        numerator.addTraceElement(new LabelTrace(")"));
        return trace;
    }

    public CalculationTrace traceLogsumCalculation(DataProvider data, int observation) {
        DataProvider subData = data.getSubData(observation,observation+1);
        CalculationTrace trace = new CalculationTrace("Logsum for " + name,"log(" + getExpsums(subData).getCell(0) + ")",getLogsums(subData).getCell(0));
        Map<C, CalculationTrace> utilityTrace = new HashMap<C, CalculationTrace>();
        Map<C,? extends Utility> uts = getUtilities();
        for (C choice : uts.keySet()) {
            DataFilter availabilityFilter = availabilityFilters.get(choice);
            if (availabilityFilter != null) {
                boolean available = availabilityFilter.getFilter(subData).getCell(0);
                if (!available) {
                    CalculationTrace uaTrace = new CalculationTrace("Utility for choice " + choice + " (unavailable)",UNAVAILABLE_UTILITY);
                    trace.addTraceElement(availabilityFilter.traceFilterCalculation(data,observation));
                    utilityTrace.put(choice,uaTrace);
                    continue;
                }
            }
            utilityTrace.put(choice,uts.get(choice).traceCalculation(subData,0));
        }
        trace.addTraceElement(new LabelTrace("log("));
        boolean first = true;
        for (C choice : utilityTrace.keySet()) {
            CalculationTrace subTrace = new CalculationTrace("exp(" + getUtilityName(choice) + ")","exp(" + utilityTrace.get(choice).getResult() + ")",Math.exp(utilityTrace.get(choice).getResult()));
            subTrace.addTraceElement(new LabelTrace("exp("));
            subTrace.addTraceElement(uts.get(choice).traceCalculation(data,observation));
            subTrace.addTraceElement(new LabelTrace(")"));
            if (first)
                first = false;
            else
                trace.addTraceElement(new LabelTrace("+"));
            trace.addTraceElement(subTrace);
        }
        trace.addTraceElement(new LabelTrace(")"));
        return trace;
    }
}
