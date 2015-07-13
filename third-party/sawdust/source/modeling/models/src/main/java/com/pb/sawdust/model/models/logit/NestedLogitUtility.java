package com.pb.sawdust.model.models.logit;

import com.pb.sawdust.calculator.NumericFunctions;
import com.pb.sawdust.calculator.tensor.CellWiseTensorCalculation;
import com.pb.sawdust.calculator.tensor.DefaultCellWiseTensorCalculation;
import com.pb.sawdust.model.models.trace.CalculationTrace;
import com.pb.sawdust.model.models.utility.SimpleLinearUtility;
import com.pb.sawdust.model.models.Choice;
import com.pb.sawdust.model.models.provider.CompositeDataProvider;
import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.model.models.provider.ExpandableDataProvider;
import com.pb.sawdust.model.models.provider.hub.DataProviderHub;
import com.pb.sawdust.model.models.utility.Utility;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.alias.vector.primitive.DoubleVector;
import com.pb.sawdust.util.collections.LinkedSetList;
import com.pb.sawdust.util.collections.SetList;

import java.util.*;

/**
 * The {@code NestedLogitUtility} class is used to represent a utility used in a nested logit model. A {@code NestedLogitUtility}
 * represents the utility of a particular nest choice, and includes the nested model as part of its structure.  If the
 * nest choice is a bottom-level (final) choice, then the nested model will be empty ({@link NestedLogitModel#EMPTY_NEST}).
 * <p>
 * As an example, the following represents a simplified subnest of the structure shown in {@link NestedLogitModel}'s documentation:
 * <p>
 * <pre><code>
 *                   Motorized
 *           +-----------------------+
 *           |                       |
 *           |                       |
 *        Transit                  Drive
 *                           +-----------------+
 *                           |                 |
 *                           |                 |
 *                       Drive Alone      Shared Ride
 * </code></pre>
 * <p>
 * The representation of the <code>Drive</code> nest choice's utility as a {@code NestedLogitUtility} would contain the
 * choice-specific utility parameters/variables, as well as the {@code NestedLogitModel} representing the choice between
 * <code>Drive Alone</code> and <code>Shared Ride</code> and the logsum parameter that applies to it.  The (nested logit)
 * utilities in that nested model (one each for <code>Drive Alone</code> and <code>Shared Ride</code>) would contain their
 * choices's respective utility parameters/variables, as well as {@code NestedLogitModel#EMPTY_NEST} as its nested model,
 * indicating that they are bottom-level choices.  As another visual explanation of this, the following shows the nest
 * structure as a psuedo-object diagram, showing the relationships between the various utility components:
 * <p>
 * <pre><code>
 *           +-----> {Motorized Nested Logit Model} <-----+
 *           |                                            |
 *           |                                            |
 *    {Transit Utility}                            {Drive Utility}
 *           ^                                            ^
 *           |                                            |
 *     {EMPTY_NEST}                  +-----> {Drive Nested Logit Model} <-----+
 *                                   |                                        |
 *                                   |                                        |
 *                         {Drive Alone Utility}                     {Shared Ride Utility}
 *                                   ^                                        ^
 *                                   |                                        |
 *                             {EMPTY_NEST}                             {EMPTY_NEST}
 * </code></pre>
 * <p>
 * The nested model is effectively treated as one of the variables for the utility, with the logsums of the model providing
 * the variable values and the logsum parameter being the coefficient. The nested model's logsums will be computed automatically
 * when this utility is calculated, and do not (should not) be provided through a data provider.
 *
 * @param <C>
 *        The type of the model choices.
 *
 * @author crf <br/>
 *         Started Jul 24, 2010 2:52:33 PM
 */
public class NestedLogitUtility<C extends Choice> extends SimpleLinearUtility {
    /**
     * The name used to represent the nested model as a variable.
     */
    protected static final String NESTED_LOGIT_MODEL_NAME = "_NESTED_MODEL_";

    /**
     * The default name assigned to nested logit utilities.
     */
    protected static final String DEFAULT_NESTED_LOGIT_UTILITY_NAME = "nested logit utility";

    private static List<Double> getCoefficientList(double logsumParameter, List<Double> coefficients) {
        List<Double> fullCoefficients = new LinkedList<Double>(coefficients);
        fullCoefficients.add(logsumParameter);
        return fullCoefficients;
    }

    private static SetList<String> getVariableList(List<String> variables) {
        SetList<String> fullVariableList = new LinkedSetList<String>(variables);
        fullVariableList.add(NESTED_LOGIT_MODEL_NAME);
        return fullVariableList;
    }

    private final LogitModel<C> nestedModel;
    private double scale = 1.0;
    private final boolean scaleUtility;
    private final List<Double> scaleElements;
    private final CellWiseTensorCalculation cwtc;

    /**
     * Constructor specifying the variables and coefficients for the utility, the nested model and its logsum parameter,
     * and the tensor factory used to build results. Variables with coefficients equal to zero will not be included in the
     * utility function.
     *
     * @param variables
     *        The (nest-specific) variables for this utility.
     *
     * @param coefficients
     *        The coefficients for {@code variables}.  There must be one coefficient for each variable, and they are
     *        expected to be in the same order.
     *
     * @param nestedModel
     *        The model nested in this utility.
     *
     * @param logsumParameter
     *        The logsum parameter associated with {@code nestedModel}.
     *
     * @param factory
     *        The tensor factory used to build results.
     *
     * @throws IllegalArgumentException if the size of {@code variables} does not equal {@code coefficients}.
     */
    public NestedLogitUtility(SetList<String> variables, List<Double> coefficients, LogitModel<C> nestedModel, double logsumParameter, TensorFactory factory) {
        this(variables,coefficients,nestedModel,logsumParameter,true,factory);
    }

    /**
     * Constructor for a bottom-level choice specifying the variables and coefficients for the utility, as well as the tensor
     * factory used to build results and whether or not utility scaling should be applied. Variables with coefficients equal
     * to zero will not be included in the utility function.
     *
     * @param variables
     *        The (nest-specific) variables for this utility.
     *
     * @param coefficients
     *        The coefficients for {@code variables}.  There must be one coefficient for each variable, and they are
     *        expected to be in the same order.
     *
     * @param factory
     *        The tensor factory used to build results.
     *
     * @param scaleUtility
     *        If {@code true}, then the utility equation will be scaled by the product of all of the logsum parameters
     *        of the nests within which it is contained.
     *
     * @throws IllegalArgumentException if the size of {@code variables} does not equal {@code coefficients}, or if {@code variables}
     *                                  is empty.
     */
    @SuppressWarnings("unchecked") //EMPTY_NEST can be cast to any NestedLogitModel, so it is valid
    public NestedLogitUtility(SetList<String> variables, List<Double> coefficients, TensorFactory factory, boolean scaleUtility) {
        this(variables,coefficients,true,factory,scaleUtility);
    }

    /**
     * Constructor for a bottom-level choice specifying the variables and coefficients for the utility, as well as the tensor
     * factory used to build results. The utility <i>will not</i> be scaled by the logsums of its containing nests. Variables
     * with coefficients equal to zero will not be included in the utility function.
     *
     * @param variables
     *        The (nest-specific) variables for this utility.
     *
     * @param coefficients
     *        The coefficients for {@code variables}.  There must be one coefficient for each variable, and they are
     *        expected to be in the same order.
     *
     * @param factory
     *        The tensor factory used to build results.
     *
     * @throws IllegalArgumentException if the size of {@code variables} does not equal {@code coefficients}, or if {@code variables}
     *                                  is empty.
     */
    public NestedLogitUtility(SetList<String> variables, List<Double> coefficients, TensorFactory factory) {
        this(variables,coefficients,factory,false);
    }

    /**
     * Constructor for a utility without choice-specific variables specifying the the nested model and its logsum parameter,
     * as well as the tensor factory used to build results. Variables with coefficients equal to zero will not be included
     * in the utility function.
     *
     * @param nestedModel
     *        The model nested in this utility.
     *
     * @param logsumParameter
     *        The logsum parameter associated with {@code nestedModel}.
     *
     * @param factory
     *        The tensor factory used to build results.
     */
    public NestedLogitUtility(LogitModel<C> nestedModel, double logsumParameter, TensorFactory factory) {
        this(new LinkedSetList<String>(), new LinkedList<Double>(), nestedModel, logsumParameter, factory);
    }

    /**
     * Constructor specifying the variables and coefficients for the utility, the nested model and its logsum parameter,
     * and the tensor factory used to build results.
     *
     * @param variables
     *        The (nest-specific) variables for this utility.
     *
     * @param coefficients
     *        The coefficients for {@code variables}.  There must be one coefficient for each variable, and they are
     *        expected to be in the same order.
     *
     * @param nestedModel
     *        The model nested in this utility.
     *
     * @param logsumParameter
     *        The logsum parameter associated with {@code nestedModel}.
     *
     * @param skipZeroCoefficients
     *        If {@code true}, then variables with coefficients equal to zero will not be included in the utility function.
     *
     * @param factory
     *        The tensor factory used to build results.
     *
     * @throws IllegalArgumentException if the size of {@code variables} does not equal {@code coefficients}.
     */
    public NestedLogitUtility(SetList<String> variables, List<Double> coefficients, LogitModel<C> nestedModel, double logsumParameter, boolean skipZeroCoefficients, TensorFactory factory) {
        super(DEFAULT_NESTED_LOGIT_UTILITY_NAME,getVariableList(variables),getCoefficientList(logsumParameter,coefficients),factory,skipZeroCoefficients);
        this.nestedModel = nestedModel;
        scaleUtility = false;
        scaleElements = null;
        passDownScaleUpdate(logsumParameter);
        cwtc = null; //not used
    }

    /**
     * Constructor for a bottom-level choice specifying the variables and coefficients for the utility, as well as the tensor
     * factory used to build results and whether or not utility scaling should be applied.
     *
     * @param variables
     *        The (nest-specific) variables for this utility.
     *
     * @param coefficients
     *        The coefficients for {@code variables}.  There must be one coefficient for each variable, and they are
     *        expected to be in the same order.
     *
     * @param factory
     *        The tensor factory used to build results.
     *
     * @param skipZeroCoefficients
     *        If {@code true}, then variables with coefficients equal to zero will not be included in the utility function.
     *
     * @param scaleUtility
     *        If {@code true}, then the utility equation will be scaled by the product of all of the logsum parameters
     *        of the nests within which it is contained.
     *
     * @throws IllegalArgumentException if the size of {@code variables} does not equal {@code coefficients}, or if {@code variables}
     *                                  is empty.
     */
    @SuppressWarnings("unchecked") //EMPTY_NEST can be cast to any NestedLogitModel, so it is valid
    public NestedLogitUtility(SetList<String> variables, List<Double> coefficients, boolean skipZeroCoefficients, TensorFactory factory, boolean scaleUtility) {
        super(DEFAULT_NESTED_LOGIT_UTILITY_NAME,variables,coefficients,factory,skipZeroCoefficients);
        nestedModel = (LogitModel<C>) NestedLogitModel.EMPTY_NEST;
        this.scaleUtility = scaleUtility;
        scaleElements = new LinkedList<>();
        cwtc = new DefaultCellWiseTensorCalculation(factory);
    }

    /**
     * Constructor for a bottom-level choice specifying the variables and coefficients for the utility, as well as the tensor
     * factory used to build results. The utility <i>will not</i> be scaled by the logsums of its containing nests.
     *
     * @param variables
     *        The (nest-specific) variables for this utility.
     *
     * @param coefficients
     *        The coefficients for {@code variables}.  There must be one coefficient for each variable, and they are
     *        expected to be in the same order.
     *
     * @param skipZeroCoefficients
     *        If {@code true}, then variables with coefficients equal to zero will not be included in the utility function.
     *
     * @param factory
     *        The tensor factory used to build results.
     *
     * @throws IllegalArgumentException if the size of {@code variables} does not equal {@code coefficients}, or if {@code variables}
     *                                  is empty.
     */
    public NestedLogitUtility(SetList<String> variables, List<Double> coefficients, boolean skipZeroCoefficients, TensorFactory factory) {
        this(variables,coefficients,skipZeroCoefficients,factory,false);
    }

    /**
     * Constructor for a utility without choice-specific variables specifying the the nested model and its logsum parameter,
     * as well as the tensor factory used to build results.
     *
     * @param nestedModel
     *        The model nested in this utility.
     *
     * @param logsumParameter
     *        The logsum parameter associated with {@code nestedModel}.
     *
     * @param skipZeroCoefficients
     *        If {@code true}, then variables with coefficients equal to zero will not be included in the utility function.
     *
     * @param factory
     *        The tensor factory used to build results.
     */
    public NestedLogitUtility(LogitModel<C> nestedModel, double logsumParameter, boolean skipZeroCoefficients, TensorFactory factory) {
        this(new LinkedSetList<String>(), new LinkedList<Double>(), nestedModel, logsumParameter, skipZeroCoefficients, factory);
    }

    /**
     * Get the logsum parameter for the nested model held by this utility. If this utility represents a bottom-level (final)
     * choice, then this method will return {@code 0.0}.
     *
     * @return the logsum parameter for the nested model held by this utility.
     */
    public double getNestedModelLogsumParameter() {
        return nestedModel == NestedLogitModel.EMPTY_NEST ? 0.0 : getCoefficients().getCell(getVariables().indexOf(NESTED_LOGIT_MODEL_NAME));
    }

    private void updateScale(double logsum) {
        if (nestedModel == NestedLogitModel.EMPTY_NEST) { //bottom level: apply scale
            if (scaleUtility) {
                scale *= logsum;
                scaleElements.add(0,logsum); //put at beginning, to get trace ordering correct
            }
        } else { //not bottom level, so pass it through where possible
            passDownScaleUpdate(logsum);
        }

    }

    private void passDownScaleUpdate(double logsum) {
        for (Utility u : nestedModel.getUtilities().values())
            if (u instanceof NestedLogitUtility)
                ((NestedLogitUtility) u).updateScale(logsum);
    }

    public DoubleVector getUtilities(DataProvider data) {
        return scaleUtility ? (DoubleVector) cwtc.calculate(getUnscaledUtilities(data),scale,NumericFunctions.ZERO_SAFE_DIVIDE) : getUnscaledUtilities(data);
    }

    private DoubleVector getUnscaledUtilities(DataProvider data) {
        return super.getUtilities(getNestProvider(data));
    }

    /**
     * Get the results of the utility equation when applied to a series of data observations from a data provider hub for
     * a given choice.  This can be useful when the utility sub-nests have choice-specific data that is best held in a
     * data provider hub. The specified choice is that which this utility belongs to (presumable in a nested logit model).
     * All of the choices in the nested model (including all sub-nest choices if a nested logit model) must be contained
     * as keys for {@code data}.
     *
     * @param choice
     *        The choice to get the exponentiated utilities for.
     *
     * @param data
     *        The data provider hub to use to calculate the utilities.
     *
     * @return the values of the utility function when applied to the data in {@code data} for {@code choice}.
     *
     * @throws IllegalArgumentException if the providers in {@code data} do not contain all of the variable required by
     *                                  {@code choice}'s utility function, if {@code choice} is not a valid choice for
     *                                  this model or {@code data}, or if the choices in this utility's nested model
     *                                  are not valid choices for {@code data}.
     */
    public DoubleVector getUtilities(C choice, DataProviderHub<C> data) {
        return getUtilities(data.getProvider(choice));
    }

    /**
     * Get this utility's nested model. If this utility represents a bottom-level (final) choice, then this method will
     * return {@link NestedLogitModel#EMPTY_NEST}.
     *
     * @return the nested model contained by this utility.
     */
    public  LogitModel<C> getNestedModel() {
        return nestedModel;
    }

    public LogitModel<C> getModelForNest(C nestChoice) {
        if ((nestedModel == NestedLogitModel.EMPTY_NEST) || !(nestedModel instanceof NestedLogitModel))
            throw new IllegalArgumentException("Nest choice not found: " + nestChoice);
        return ((NestedLogitModel<C>) nestedModel).getModelForNest(nestChoice);
    }

    public CalculationTrace traceCalculation(DataProvider data, int observation) {
        CalculationTrace trace = super.traceCalculation(getNestProvider(data),observation);
        if (scaleUtility) {
            DataProvider subDataProvider = data.getSubData(observation,observation+1);
            CalculationTrace trueTrace = new CalculationTrace(getName(),getUnscaledUtilities(subDataProvider).getCell(0));
            for (CalculationTrace ct : trace.getTraceElements())
                trueTrace.addTraceElement(ct);
            trace = new CalculationTrace("utility / scale",trueTrace.getResult() + " / " + scale,trace.getResult());
            trace.addTraceElement(trueTrace);
            CalculationTrace scaleTrace = new CalculationTrace("scale",scale);
            int counter = 1;
            for (double s : scaleElements) {
                CalculationTrace scaleSubTrace = new CalculationTrace("level " + counter++ + " logsum",s);
                if (counter == 2)
                    scaleTrace.addTraceElement(scaleSubTrace);
                else
                    scaleTrace.addTraceElement("*",scaleSubTrace);
            }
            trace.addTraceElement("/",scaleTrace);
        }
        return trace;
    }

    private DataProvider getNestProvider(DataProvider data) {
        if (nestedModel == NestedLogitModel.EMPTY_NEST)
            return data;
        ExpandableDataProvider nest = new ExpandableDataProvider(data.getDataLength(),nestedModel.factory);
        nest.addVariable(NESTED_LOGIT_MODEL_NAME,(double[]) nestedModel.getLogsums(data).getTensorValues().getArray());
        return new NestedLogitDataProvider(data,nest);
    }

    private class NestedLogitDataProvider extends CompositeDataProvider {
        private NestedLogitDataProvider(DataProvider data, DataProvider nestProvider) {
            super(nestedModel.factory,data,nestProvider);
        }

        public CalculationTrace getVariableTrace(String variable, int observation) {
            if (!variable.equals(NESTED_LOGIT_MODEL_NAME))
                return super.getVariableTrace(variable,observation);
            return nestedModel.traceLogsumCalculation(this,observation);

        }
    }

}
