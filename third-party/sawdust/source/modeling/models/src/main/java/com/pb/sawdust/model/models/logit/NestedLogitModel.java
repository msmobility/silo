package com.pb.sawdust.model.models.logit;

import com.pb.sawdust.calculator.NumericFunctions;
import com.pb.sawdust.model.models.provider.filter.DataFilter;
import com.pb.sawdust.model.models.trace.CalculationTrace;
import com.pb.sawdust.model.models.trace.ConstantTrace;
import com.pb.sawdust.model.models.trace.LabelTrace;
import com.pb.sawdust.model.models.utility.Utility;
import com.pb.sawdust.model.models.Choice;
import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.model.models.provider.IdData;
import com.pb.sawdust.model.models.provider.SimpleDataProvider;
import com.pb.sawdust.model.models.provider.hub.DataProviderHub;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.UniformTensor;
import com.pb.sawdust.tensor.alias.vector.primitive.DoubleVector;
import com.pb.sawdust.util.RandomDeluxe;
import com.pb.sawdust.util.ThreadTimer;
import com.pb.sawdust.util.collections.LinkedSetList;
import com.pb.sawdust.util.collections.SetList;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * The {@code NestedLogitModel} class provides the functionality for working with nested logit discete choice models.
 * A nested logit model is a logit model which allows groups of choices to be correlated, providing an additional  level
 * of structure on a logit model. The canonical nested logit model is a mode choice model, which might be structured as
 * follows:
 * <p>
 * <pre><code>
 *                                                             Mode
 *                                  +--------------------------------------------------------+
 *                                  |                                                        |
 *                                  |                                                        |
 *                              Motorized                                             Non-Motorized
 *           +------------------------------------------+                               +--------+
 *           |                                          |                               |        |
 *           |                                          |                               |        |
 *        Transit                                     Drive                            Walk   Bicycle
 *     +-----------+                  +-----------------+-----------------+
 *     |           |                  |                 |                 |
 *     |           |                  |                 |                 |
 *    Bus       Light Rail        Drive Alone      Shared Ride 2     Shared Ride 3+
 * </code></pre>
 * <p>
 * The "nests" are <code>Motorized/Non-Motorized</code> and <code>Transit/Drive</code>. The
 * final model choices are <code>Bus</code>, <code>Light Rail</code>, <code>Drive Alone</code>, <code>Shared Ride 2</code>,
 * <code>Shared Ride 3+</code>, <code>Walk</code>, and <code>Bicycle</code>.
 * <p>
 * The nested logit structure is built through a series of {@link com.pb.sawdust.model.models.logit.NestedLogitUtility}
 * instances. Complete details on this structure representation can be found in that class, but at a basic level the
 * nested logit utility for a given nest's choice holds standard utility variable/coefficient information, along with the
 * nested model (an instance of this class) that the nest choice represents. A bottom-level (final) choice contains an
 * empty nest ({@link #EMPTY_NEST}) for its nested model.
 * <p>
 * Because this is derived from {@code LogitModel}, this class uses an internal cache to store various steps in the model
 * calculations tied to {@code DataProvider} instances. This cache is only cleared (fully or partially) when one the
 * {@code clearCache} methods are called.  Depending on the size of the input data, this cache has the potential to grow
 * quite large, so care must be taken to monitor its use to avoid inadvertent memory leaks.
 * <p>
 * The methods to get the logsums, exponentiated utilities, and probabilities return the results for the top-level nest
 * defined by the {@code NestedLogitUtility}s the model is constructed with. To have access to these data values for
 * sub-nests, those methods should be called on the nested models held in the utilities (which can be retrieved from
 * {@link NestedLogitUtility#getNestedModel()}).
 *
 * @param <C>
 *        The type of the model choices.
 *
 * @author crf <br/>
 *         Started Jul 24, 2010 2:47:23 PM
 */
public class NestedLogitModel<C extends Choice> extends LogitModel<C> {
    /**
     * A {@code NestedLogitModel} instance representing an empty nest. This is used to indicate a bottom-level (final)
     * choice in a {@code NestedLogitUtility} instance, and is not intended to be used for another purpose. It is parameterized
     * such that it can be cast to any {@code NestedLogitModel} (with or without type parameters, though an unchecked warning
     * may need to be (validly) suppressed.
     */
    @SuppressWarnings("unchecked") //undeclared generics to allow for general usage of empty model
    public static final NestedLogitModel<? extends Choice> EMPTY_NEST = new EmptyNestedLogitModel();

    /**
     * Constructor specifying the model name, the utility structure, and the tensor factory used to build results.
     *
     * @param name
     *        The name of the model.
     *
     * @param utilities
     *        The top-level choices/nests mapped to their respective utilities.
     *
     * @param factory
     *        The tensor factory used to build results.
     *
     * @throws IllegalArgumentException if any of the choices in the model (across and through nests) is repeated (choices
     *                                  must be unique).
     */
    public NestedLogitModel(String name, Map<C,NestedLogitUtility<C>> utilities, TensorFactory factory) {
        super(name,utilities,factory);
        checkInitialInputs(utilities,null);
    }

    /**
     * Constructor specifying the model's utility structure, and the tensor factory used to build results.  A default
     * empty string will be used for the model name.
     *
     * @param utilities
     *        The top-level choices/nests mapped to their respective utilities.
     *
     * @param factory
     *        The tensor factory used to build results.
     *
     * @throws IllegalArgumentException if any of the choices in the model (across and through nests) is repeated (choices
     *                                  must be unique).
     */
    public NestedLogitModel(Map<C,NestedLogitUtility<C>> utilities, TensorFactory factory) {
        super(utilities,factory);
        checkInitialInputs(utilities,null);
    }

    /**
     * Constructor specifying the model name, the utility structure, the availability filters, and the tensor factory used
     * to build results. The availability filters are used to determine whether, for a given observation, a choice is available
     * or not. If a given choice's filter returns {@code false} for a given observation in a data provider, then that
     * choice will be unavailable and its exponentiated utility assigned a value of <code>0.0</code>.
     *
     * @param name
     *        The name of the model.
     *
     * @param utilities
     *        The top-level choices/nests mapped to their respective utilities.
     *
     * @param availabilityFilters
     *        A mapping from model choices to the availability filters. If a choice is missing from the mapping, then it
     *        is assumed that it is always available.
     *
     * @param factory
     *        The tensor factory used to build results.
     *
     * @throws IllegalArgumentException if any of the choices in the model (across and through nests) is repeated (choices
     *                                  must be unique), or if any choice in keys for {@code availabilityFilters} is not
     *                                  found in the keys for {@code utilities}.
     */
    public NestedLogitModel(String name, Map<C,NestedLogitUtility<C>> utilities, Map<C,DataFilter> availabilityFilters, TensorFactory factory) {
        super(name,utilities,availabilityFilters,factory);
        checkInitialInputs(utilities,availabilityFilters);
    }

    /**
     * Constructor specifying the model utility structure, the availability filters, and the tensor factory used
     * to build results.  A default empty string will be used for the model name. The availability filters are used to
     * determine whether, for a given observation, a choice is available or not. If a given choice's filter returns
     * {@code false} for a given observation in a data provider, then that choice will be unavailable and its exponentiated
     * utility assigned a value of <code>0.0</code>.
     *
     * @param utilities
     *        The top-level choices/nests mapped to their respective utilities.
     *
     * @param availabilityFilters
     *        A mapping from model choices to the availability filters. If a choice is missing from the mapping, then it
     *        is assumed that it is always available.
     *
     * @param factory
     *        The tensor factory used to build results.
     *
     * @throws IllegalArgumentException if any of the choices in the model (across and through nests) is repeated (choices
     *                                  must be unique), or if any choice in keys for {@code availabilityFilters} is not
     *                                  found in the keys for {@code utilities}.
     */
    public NestedLogitModel(Map<C,NestedLogitUtility<C>> utilities, Map<C,DataFilter> availabilityFilters, TensorFactory factory) {
        super(utilities,availabilityFilters,factory);
        checkInitialInputs(utilities,availabilityFilters);
    }

    private void checkInitialInputs(Map<C,NestedLogitUtility<C>> utilities, Map<C,DataFilter> availabilityFilters) {
        Set<C> choices = new HashSet<C>(utilities.keySet());
        for (C c : utilities.keySet()) {
            LogitModel<C> m = utilities.get(c).getNestedModel();
            Set<C> subChoices = m.getChoices();
            if (m instanceof NestedLogitModel)
                subChoices = ((NestedLogitModel<C>) m).getFullChoices();
            for (C choice : subChoices)
                if (!choices.add(choice))
                    throw new IllegalArgumentException(String.format("Duplicated choice in nested logit model '%s': %s",getName(),choice.toString()));
        }
    }

    private DoubleVector getExponentiatedUtilities(C choice, DataProviderHub<C> data, IdData sourceData) {
        if (!getChoices().contains(choice))
            throw new IllegalArgumentException(String.format("Choice not available from model '%s': %s",getName(),choice.toString()));
        Map<C,DoubleVector> utilityMap = getUtilityMap(sourceData);
        if (!utilityMap.containsKey(choice)) {
            @SuppressWarnings("unchecked") //think I can suppress this: todo: check to verify
            DoubleVector v = (DoubleVector) cwtc.calculate(((NestedLogitUtility<C>) getUtilities().get(choice)).getUtilities(choice,data),NumericFunctions.EXP);
            utilityMap.put(choice,v);
        }
        return utilityMap.get(choice);
    }

    @SuppressWarnings("unchecked") //casting to <C> parameterization is valid because everything will stay internally consistent
    private Map<C,DoubleVector> getNestedProbabilities(DataProvider data, boolean filterNests) {
        Map<C,DoubleVector> probabilities = getProbabilities(data);
        Map<C,? extends Utility> utilities = getUtilities();
        for (C choice : utilities.keySet()) {
            Utility utility = utilities.get(choice);
            LogitModel<C> nestedModel = (LogitModel<C>) EMPTY_NEST;
            if (utility instanceof NestedLogitUtility)
                nestedModel = ((NestedLogitUtility<C>) utility).getNestedModel();
            if (nestedModel == EMPTY_NEST)  //bottom-level choice, no need to go further
                continue;
            DoubleVector probs = filterNests ? probabilities.remove(choice) : probabilities.get(choice); //get nest probability - remove it if we only want bottom-level choices
            //if nested model is itself a nested model, get its nested probabilities (they are independant of what's above
            //  in the nesting tree); caching should make this fairly efficient
            Map<C,DoubleVector> subProbabilities = (nestedModel instanceof NestedLogitModel) ? ((NestedLogitModel<C>) nestedModel).getNestedProbabilities(data,filterNests) : nestedModel.getProbabilities(data);
            for (C subChoice : subProbabilities.keySet()) //multiply probability through to subsequent choices
                subProbabilities.put(subChoice,(DoubleVector) cwtc.calculate(subProbabilities.get(subChoice),probs, NumericFunctions.MULTIPLY));
            probabilities.putAll(subProbabilities);
        }
        return probabilities;
    }

    @SuppressWarnings("unchecked") //casting to <C> parameterization is valid because everything will stay internally consistent
    private Map<C,DoubleVector> getNestedProbabilities(DataProviderHub<C> data, boolean filterNests) {
        Map<C,DoubleVector> probabilities = getProbabilities(data);
        Map<C,? extends Utility> utilities = getUtilities();
        for (C choice : utilities.keySet()) {
            Utility utility = utilities.get(choice);
            LogitModel<C> nestedModel = (LogitModel<C>) EMPTY_NEST;
            if (utility instanceof NestedLogitUtility)
                nestedModel = ((NestedLogitUtility<C>) utility).getNestedModel();
            if (nestedModel == EMPTY_NEST)  //bottom-level choice, no need to go further
                continue;
            DoubleVector probs = filterNests ? probabilities.remove(choice) : probabilities.get(choice); //get nest probability - remove it if we only want bottom-level choices
            //if nested model is itself a nested model, get its nested probabilities (they are independant of what's above
            //  in the nesting tree); caching should make this fairly efficient
            Map<C,DoubleVector> subProbabilities = (nestedModel instanceof NestedLogitModel) ? ((NestedLogitModel<C>) nestedModel).getNestedProbabilities(data,filterNests) : nestedModel.getProbabilities(data);
            for (C subChoice : subProbabilities.keySet()) //multiply probability through to subsequent choices
                subProbabilities.put(subChoice,(DoubleVector) cwtc.calculate(subProbabilities.get(subChoice),probs, NumericFunctions.MULTIPLY));
            probabilities.putAll(subProbabilities);
        }
        return probabilities;
    }

    /**
     * Get the probabilities for a specified choice using the specified data. This choice can be either a final-level choice,
     * or a nest.  The returned probabilities are not conditional, but fully realized, and are ordered in the same way
     * as the input data.
     *
     * @param choice
     *        The choice/nest to get the probabilities for.
     *
     * @param data
     *        The data provider to use to calculate the probabilities.
     *
     * @return the probabilities for {@code choice} on {@code data}.
     *
     * @throws IllegalArgumentException if {@code data} does not contain all of the variables required by this model's
     *                                  utility functions.
     */
    public DoubleVector getProbabilities(C choice, DataProvider data) {
        if (getChoices().contains(choice))
            return super.getProbabilities(choice,data);
        Map<C,DoubleVector> probs = getNestedProbabilities(data,false);
        if (!probs.containsKey(choice))
            throw new IllegalArgumentException("Choice not found in nested logit model: " + choice);
        return probs.get(choice);
    }

    /**
     * Get the probabilities for the bottom level (final) choices using the specified data. The returned probabilities are
     * not conditional, but fully realized, and are ordered in the same way as the input data.
     *
     * @param data
     *        The data provider to use to calculate the probabilities.
     *
     * @return a mapping from each bottom-level model choice to its probabilities on {@code data}.
     *
     * @throws IllegalArgumentException if {@code data} does not contain all of the variables required by this model's
     *                                  utility functions.
     */
    public Map<C,DoubleVector> getFinalProbabilities(DataProvider data) {
        return getNestedProbabilities(data,true);
    }

    /**
     * Get the probabilities for a specified choice using the specified data. This choice can be either a final-level choice,
     * or a nest.  The returned probabilities are not conditional, but fully realized, and are ordered in the same way
     * as the input data. The data provider hub must provide data for every choice in this model.
     *
     * @param choice
     *        The choice/nest to get the probabilities for.
     *
     * @param data
     *        The data provider to use to calculate the probabilities.
     *
     * @return the probabilities for {@code choice} on {@code data}.
     *
     * @throws IllegalArgumentException if {@code data} does not contain all of the variables required by this model's
     *                                  utility functions, or if {@code data} does not provide data for every (nested)
     *                                  choice in this model.
     */
    public DoubleVector getProbabilities(C choice, DataProviderHub<C> data) {         
        if (getChoices().contains(choice))
            return super.getProbabilities(choice,data);
        Map<C,DoubleVector> probs = getNestedProbabilities(data,false);
        if (!probs.containsKey(choice))
            throw new IllegalArgumentException("Choice not found in nested logit model: " + choice);
        return probs.get(choice);
    }

    /**
     * Get the probabilities for the bottom level (final) choices using the specified data. The returned probabilities are
     * not conditional, but fully realized, and are ordered in the same way as the input data.
     *
     * @param data
     *        The data provider to use to calculate the probabilities.
     *
     * @return a mapping from each bottom-level model choice to its probabilities on {@code data}.
     *
     * @throws IllegalArgumentException if {@code data} does not contain all of the variables required by this model's
     *                                  utility functions, or if {@code data} does not provide data for every (nested)
     *                                  choice in this model.
     */
    public Map<C,DoubleVector> getFinalProbabilities(DataProviderHub<C> data) {
        return getNestedProbabilities(data,true);
    }

    /**
     * Get a set of all choices held in this nested logit model. This includes all of the choices at the current nest,
     * as well as any subsequent nests.
     *
     * @return all of the choices in this model.
     */
    public Set<C> getFullChoices() {
        Set<C> choices = new HashSet<C>(getChoices());
        @SuppressWarnings("unchecked") //this is what we constructed this with
        Map<C,NestedLogitUtility<C>> uts = (Map<C,NestedLogitUtility<C>>) getUtilities();
        for (C choice : uts.keySet()) {
            LogitModel<C> ut = uts.get(choice).getNestedModel();
            if (ut == EMPTY_NEST || !(ut instanceof NestedLogitModel))
                continue;
            choices.addAll(((NestedLogitModel<C>) ut).getFullChoices());
        }
        return choices;
    }

    /**
     * Get the set of final (bottom-level) choices in this nested logit model. This choice set is the same as the keys
     * for the mapping returned by {@link #getFinalProbabilities(com.pb.sawdust.model.models.provider.DataProvider)}.
     * This method may often be of greater practical interest than {@link #getChoices()}, which returns the choices for
     * the top-level nest in the model.
     *
     * @return the final choices for this model.
     */
    public Set<C> getFinalChoices() {
        Set<C> choices = new HashSet<C>();
        @SuppressWarnings("unchecked") //this is what we constructed this with
        Map<C,NestedLogitUtility<C>> uts = (Map<C,NestedLogitUtility<C>>) getUtilities();
        for (C choice : uts.keySet()) {
            LogitModel<C> ut = uts.get(choice).getNestedModel();
            if (ut == EMPTY_NEST)
                choices.add(choice);
            else if (!(ut instanceof NestedLogitModel))
                continue;
            else
                choices.addAll(((NestedLogitModel<C>) ut).getFinalChoices());
        }
        return choices;
    }


    public LogitModel<C> getModelForNest(C nestChoice) {
        LogitModel<C> model = getModelForNestInternal(nestChoice);
        if (model == null) {
            throw new IllegalArgumentException("Nested choice not found: " + nestChoice);
        }
        return model;
    }

    private LogitModel<C> getModelForNestInternal(C nestChoice) {
        @SuppressWarnings("unchecked") //this is how this was constructed, so ok
        Map<C,NestedLogitUtility<C>> utilities = (Map<C,NestedLogitUtility<C>>) getUtilities();
        if (utilities.containsKey(nestChoice))
            return utilities.get(nestChoice).getNestedModel();
        LogitModel<C> model = null;
        for (C choice : utilities.keySet()) {
            LogitModel<C> nestedModel = utilities.get(choice).getNestedModel();
            if (nestedModel instanceof NestedLogitModel) {
                model = ((NestedLogitModel<C>) nestedModel).getModelForNestInternal(nestChoice);
                if (model != null)
                    break;
            }
        }
        return model;
    }



    public CalculationTrace traceCalculationOld(C choice, DataProvider data, int observation) {
        if (!getFullChoices().contains(choice))
            throw new IllegalArgumentException("Invalid choice for this model: " + choice);
        if (getChoices().contains(choice))
            return super.traceCalculation(choice,data,observation);
        Map<C,? extends Utility> utilityMap = getUtilities();
        for (C c : utilityMap.keySet()) {
            @SuppressWarnings("unchecked") //utilities will be nested logit utilities, so this is correct
            LogitModel<C> model = ((NestedLogitUtility<C>) utilityMap.get(c)).getNestedModel();
            if ((model instanceof NestedLogitModel && ((NestedLogitModel) model).getFullChoices().contains(choice)) || model.getChoices().contains(choice))
                return model.traceCalculation(choice,data,observation);
        }
        throw new IllegalStateException("should not be here...");
    }

    public CalculationTrace traceCalculation(C choice, DataProvider data, int observation) {
        List<CalculationTrace> traceList = subTraceCalculation(choice,data,observation);
        CalculationTrace endTrace = traceList.remove(traceList.size() - 1);
        CalculationTrace trace = new CalculationTrace("Conditional probability for choice " + choice,endTrace.getResolvedTrace(),endTrace.getResult());
        for (CalculationTrace t : endTrace.getTraceElements())
            trace.addTraceElement(t);
        traceList.add(trace);
        StringBuilder symbolic = new StringBuilder();
        StringBuilder resolved = new StringBuilder();
        double result = 1.0;
        boolean first = true;
        for (CalculationTrace calculationTrace : traceList) {
            if (first) {
                first = false;
            } else {
                symbolic.append(" * ");
                resolved.append(" * ");
            }
            symbolic.append("(").append(calculationTrace.getSymbolicTrace()).append(")");
            resolved.append(calculationTrace.getResolvedTrace());
            result *= calculationTrace.getResult();
        }
        CalculationTrace subTrace = new CalculationTrace(symbolic.toString(),resolved.toString(),result);
        first = true;
        for (CalculationTrace t : traceList) {
            if (first) {
                subTrace.addTraceElement(t);
                first = false;
            } else {
                subTrace.addTraceElement("*",t);
            }
        }
        CalculationTrace finalTrace = new CalculationTrace("Probability for choice " + choice," " + result,result);
        finalTrace.addTraceElement(subTrace);
        return finalTrace;
    }

    private List<CalculationTrace> subTraceCalculation(C choice, DataProvider data, int observation) {
        if (!getFullChoices().contains(choice))
            throw new IllegalArgumentException("Invalid choice for this model: " + choice);
        List<CalculationTrace> traceList = new LinkedList<>();
        if (getChoices().contains(choice)) {
            traceList.add(super.traceCalculation(choice,data,observation));
        } else {
            @SuppressWarnings("unchecked") //utilities must be nested logit utilities, so this is correct, in spirit
            Map<C,NestedLogitUtility<C>> utilities = (Map<C,NestedLogitUtility<C>>) getUtilities();
            for (C subChoice : utilities.keySet()) {
                LogitModel<C> m = utilities.get(subChoice).getNestedModel();
                NestedLogitModel<C> nm = m instanceof NestedLogitModel ? (NestedLogitModel<C>) m : null;
                Set<C> choices = nm == null ? m.getChoices() : nm.getFullChoices();
                if (choices.contains(choice)) {
                    traceList.add(new CalculationTrace("Conditional probability for choice " + subChoice,getProbabilities(subChoice,data).getCell(observation)));
                    if (nm == null)
                        traceList.add(m.traceCalculation(choice,data,observation));
                    else
                        traceList.addAll(nm.subTraceCalculation(choice,data,observation));
                    break;
                }
            }
        }
        return traceList;
    }

    private static final class EmptyNestedLogitModel<C extends Choice> extends NestedLogitModel<C> {
        private EmptyNestedLogitModel() {
            super("empty_nest",new HashMap<C,NestedLogitUtility<C>>(),ArrayTensor.getFactory());
        }

        public DoubleVector getExpsums(DataProvider data) {
            return (DoubleVector) UniformTensor.getFactory().initializedDoubleTensor(1.0,data.getDataLength());
        }

        public DoubleVector getExpsums(DataProviderHub data) {
            return (DoubleVector) UniformTensor.getFactory().initializedDoubleTensor(1.0,data.getDataLength());
        }

    }

    private static enum TestChoices implements Choice {
        AUTO,
        TRANSIT,
        NON_MOTORIZED,
        AUTO1,
        AUTO2,
        BUS,
        PREMIUM,
        LIGHT_RAIL,
        STREETCAR;

        public String getChoiceIdentifier() {
            return name();
        }
    }
    
    public static void main(String ... args) {
//        LinearAlgebra.LinearAlgebraLibrary.setLibrary(LinearAlgebra.LinearAlgebraLibrary.DEFAULT);
        //nesting structure:
        // auto-transit-non-motorized
        //a1-a2   bus-pr  
        //            lr-sc
//        double[] consta = {1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0};
//        double[] ttime = {2.0,4.0,2.0,4.0,2.0,4.0,2.0,4.0,2.0,4.0};
        ThreadTimer timer = new ThreadTimer(TimeUnit.MILLISECONDS);

        timer.startTimer();
        RandomDeluxe rd = new RandomDeluxe();
        //int length = 501;
        int length = 1000;
        double[] consta = new double[length];
        Arrays.fill(consta,1.0);
        double[] ttime = rd.nextDoubles(length);
        System.out.println("Random: " + timer.resetTimer());

        TensorFactory factory = ArrayTensor.getFactory();
//        DataSet ds = new SqliteMemoryDataSet("test");
//        DataSet ds = new HsqldbMemoryDataSet("test");
//        DataTable dt = ds.addTable(new SqlTableSchema("test_table"));
//        dt.addColumn("constant",consta,DataType.DOUBLE);
//        dt.addColumn("lrtime",ttime,DataType.DOUBLE);
//        dt.addColumn("sctime",ttime,DataType.DOUBLE);
//        dt.addColumn("btime",ttime,DataType.DOUBLE);
//        dt.addColumn("a1time",ttime,DataType.DOUBLE);
//        dt.addColumn("a2time",ttime,DataType.DOUBLE);
//        dt.addColumn("nmtime",ttime,DataType.DOUBLE);

        Map<String,double[]> testData = new HashMap<String,double[]>();
        testData.put("constant",consta);
        testData.put("lrtime",ttime);
        testData.put("sctime",ttime);
        testData.put("btime",ttime);
        testData.put("a1time",ttime);
        testData.put("a2time",ttime);
        testData.put("nmtime",ttime);
        
        SetList<String> lrv = new LinkedSetList<String>("constant","lrtime");
        List<Double> lrc = Arrays.asList(2.0,1.0);
        NestedLogitUtility<TestChoices> lru = new NestedLogitUtility<TestChoices>(lrv, lrc, factory);
        SetList<String> scv = new LinkedSetList<String>("constant","sctime");
        List<Double> scc = Arrays.asList(2.0,1.0);
        NestedLogitUtility<TestChoices> scu = new NestedLogitUtility<TestChoices>(scv, scc, factory);
        SetList<String> bv = new LinkedSetList<String>("constant","btime");
        List<Double> bc = Arrays.asList(2.0,1.0);
        NestedLogitUtility<TestChoices> bu = new NestedLogitUtility<TestChoices>(bv, bc, factory);
        SetList<String> a1v = new LinkedSetList<String>("constant","a1time");
        List<Double> a1c = Arrays.asList(2.0,1.0);
        NestedLogitUtility<TestChoices> a1u = new NestedLogitUtility<TestChoices>(a1v, a1c, factory);
        SetList<String> a2v = new LinkedSetList<String>("constant","a2time");
        List<Double> a2c = Arrays.asList(2.0,1.0);
        NestedLogitUtility<TestChoices> a2u = new NestedLogitUtility<TestChoices>(a2v, a2c, factory);
        SetList<String> nmv = new LinkedSetList<String>("constant","nmtime");
        List<Double> nmc = Arrays.asList(2.0,1.0);
        NestedLogitUtility<TestChoices> nmu = new NestedLogitUtility<TestChoices>(nmv, nmc, factory);

        Map<TestChoices,NestedLogitUtility<TestChoices>> prmp = new HashMap<TestChoices,NestedLogitUtility<TestChoices>>();
        prmp.put(TestChoices.LIGHT_RAIL,lru);
        prmp.put(TestChoices.STREETCAR,scu);
        NestedLogitModel<TestChoices> prm = new NestedLogitModel<TestChoices>("Premium Transit",prmp,factory);
        double prls = 0.5;
        NestedLogitUtility<TestChoices> pru = new NestedLogitUtility<TestChoices>(prm, prls, factory);

        Map<TestChoices,NestedLogitUtility<TestChoices>> tmp = new HashMap<TestChoices,NestedLogitUtility<TestChoices>>();
        tmp.put(TestChoices.PREMIUM,pru);
        tmp.put(TestChoices.BUS,bu);
        NestedLogitModel<TestChoices> tm = new NestedLogitModel<TestChoices>("Transit",tmp,factory);
        double tls = 0.5;
        NestedLogitUtility<TestChoices> tu = new NestedLogitUtility<TestChoices>(tm, tls, factory);

        Map<TestChoices,NestedLogitUtility<TestChoices>> amp = new HashMap<TestChoices,NestedLogitUtility<TestChoices>>();
        amp.put(TestChoices.AUTO1,a1u);
        amp.put(TestChoices.AUTO2,a2u);
        NestedLogitModel<TestChoices> am = new NestedLogitModel<TestChoices>("Auto",amp,factory);
        double als = 0.5;
        NestedLogitUtility<TestChoices> au = new NestedLogitUtility<TestChoices>(am, als, factory);

        Map<TestChoices,NestedLogitUtility<TestChoices>> mmp = new HashMap<TestChoices,NestedLogitUtility<TestChoices>>();
        mmp.put(TestChoices.AUTO,au);
        mmp.put(TestChoices.TRANSIT,tu);
        mmp.put(TestChoices.NON_MOTORIZED,nmu);
        NestedLogitModel<TestChoices> mm = new NestedLogitModel<TestChoices>("Mode",mmp,factory);
        System.out.println("Setup: " + timer.resetTimer());

        DataProvider dp = new SimpleDataProvider(testData,factory);
//        DataProvider dp = new DataTableDataProvider(dt,factory);
        System.out.println("Data Provider: " + timer.endTimer());
        timer.startTimer();


        Map<TestChoices,DoubleVector> probs = mm.getFinalProbabilities(dp);
        System.out.println("Model: " + timer.resetTimer());
        DoubleVector t = null;
        for (TestChoices c : probs.keySet()) {
            System.out.printf("%15s: %s\n",c,TensorUtil.toString(probs.get(c)));
            if (t == null)
                t = (DoubleVector) TensorUtil.copyOf(probs.get(c),factory);
            else
                t = (DoubleVector) mm.cwtc.calculate(t,probs.get(c),NumericFunctions.ADD);
        }
        System.out.printf("%15s: %s\n","Total",TensorUtil.toString(t));
        System.out.println("Print: " + timer.resetTimer());


        Map<TestChoices,DoubleVector> probas = mm.getNestedProbabilities(dp,false);
        for (TestChoices c : probas.keySet())
            System.out.printf("%15s: %s\n",c,TensorUtil.toString(probas.get(c)));

        System.out.println("Choice " + TestChoices.PREMIUM + ": " + TensorUtil.toString(mm.getProbabilities(TestChoices.PREMIUM,dp)));
        System.out.println("Choice " + TestChoices.TRANSIT + ": " + TensorUtil.toString(mm.getProbabilities(TestChoices.TRANSIT,dp)));
        System.out.println("Choice " + TestChoices.NON_MOTORIZED + ": " + TensorUtil.toString(mm.getProbabilities(TestChoices.NON_MOTORIZED,dp)));

        System.out.println(mm.getUtilities());
        System.out.println(mm.getProbabilities(dp));
        System.out.println(TensorUtil.toString(mm.getLogsums(dp)));

        System.out.println(mm.traceCalculation(dp,1).toString());

    }
}
