package com.pb.sawdust.model.models.utility;

import com.pb.sawdust.calculator.NumericFunctionN;
import com.pb.sawdust.calculator.NumericFunctions;
import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.model.models.provider.VariableCalculation;
import com.pb.sawdust.model.models.trace.CalculationTrace;
import com.pb.sawdust.model.models.trace.ConstantTrace;
import com.pb.sawdust.model.models.trace.LabelTrace;
import com.pb.sawdust.util.collections.SetList;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.pb.sawdust.util.Range.range;

/**
 * The {@code AbstractLinearUtility} class provides a skeletal {@code LinearUtility} implementation. Its primary
 * function is to implement a default calculation trace method.
 *
 * @author crf <br/>
 *         Started Sep 17, 2010 1:59:52 PM
 */
public abstract class AbstractLinearUtility implements LinearUtility {
    public static final String DEFAULT_LINEAR_UTLITY_NAME = "linear utility";

    private final String name;

    /**
     * Constructor specifying the utility name. This name is used in the calculation trace.
     *
     * @param name
     *        The name of the utility.
     */
    public AbstractLinearUtility(String name) {
        this.name = name;
    }

    /**
     * Get the name of this utility.
     *
     * @return this utility's name.
     */
    public String getName() {
        return name;
    }


    public Set<String> getVariableSet() {
        return getVariables();
    }

    /**
     * Get the calculation trace element for one variable in the utility equation. This method may be overidden to
     * create different calculation trace forms.
     *
     * @param variableName
     *        The name of the variable being traced.
     *
     * @param coefficient
     *        The coefficient value of the variable being traced.
     *
     * @param variable
     *        The value of the variable being traced.
     *
     * @param subData
     *        The data for the observation being traced, in the form of a (single entry) data provider.
     *
     * @return a calculation trace element describing the traced calculation.
     */
//    protected CalculationTrace.CalculationTraceElement getCalculationTraceElement(String variableName, double coefficient, double variable, DataProvider subData) {
//        return new CalculationTrace.CalculationTraceElement("[" + variableName + " coefficient]*" + variableName,coefficient + "*" + variable,coefficient*variable);
//    }
    protected CalculationTrace getCalculationTraceElement(String variableName, double coefficient, double variable, DataProvider subData) {
        CalculationTrace trace = new CalculationTrace("[" + variableName + " coefficient]*" + variableName,coefficient + "*" + variable,coefficient*variable);
        CalculationTrace variableTrace = subData.getVariableTrace(variableName,0);
        if (variableTrace != null) {
            trace.addTraceElement(new ConstantTrace("[" + variableName + " coefficient]",coefficient));
            trace.addTraceElement("*",variableTrace);
        }
        return trace;
    }

    public CalculationTrace traceCalculation(DataProvider data, int observation) {
        DataProvider subDataProvider = data.getSubData(observation,observation+1);
        CalculationTrace trace = new CalculationTrace(getName(),getUtilities(subDataProvider).getCell(0));
        SetList<String> variables = getVariables();
        for (int i : range(variables.size())) {
            String variable = variables.get(i);
            if (i > 0)
                trace.addTraceElement(new LabelTrace("+"));
            CalculationTrace traceElement = getCalculationTraceElement(variable,getCoefficients().getCell(i),subDataProvider.getVariableData(variable)[0],subDataProvider);
            trace.addTraceElement(traceElement);
        }
        return trace;
    }

    public VariableCalculation getCalculation() {
        List<String> args = new LinkedList<String>();
        List<NumericFunctionN> functions = new LinkedList<NumericFunctionN>();
        boolean first = true;
        for (String variable : getVariables()) {
            args.add(variable);
            functions.add(NumericFunctions.PARAMETER); //variable
//            functions.add(NumericFunctions.constant(getCoefficient(variable)));
//            functions.add(NumericFunctions.MULTIPLY);
            functions.add(NumericFunctions.collapsedFunction(NumericFunctions.MULTIPLY,getCoefficient(variable),true));
            if (first)
                first = false;
            else
                functions.add(NumericFunctions.ADD); //add previous result
        }
        return new VariableCalculation(getName(),NumericFunctions.compositeNumericFunction(functions),args) {
            public CalculationTrace getVariableTrace(DataProvider provider, int observation) {
                return traceCalculation(provider,observation);
            }
        };
    }


}
