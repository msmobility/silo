package com.pb.sawdust.model.models.provider;

import com.pb.sawdust.calculator.NumericFunction1;
import com.pb.sawdust.calculator.NumericFunctionN;
import com.pb.sawdust.calculator.NumericFunctions;
import com.pb.sawdust.model.models.trace.CalculationTrace;
import com.pb.sawdust.model.models.trace.ConstantTrace;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.format.TextFormat;

import java.util.*;

/**
 * The {@code VariableCalculation} class provides a structure for specifying a calculated variable.
 *
 * @author crf <br/>
 *         Started 2/14/11 9:57 AM
 */
public class VariableCalculation {
    private final String name;
    private final List<String> arguments;
    private final NumericFunctionN function;

    /**
     * Constructor specifying the variable name, function, and argument names. The argument names will (generally) refer
     * to other variables available in a data provider.
     *
     * @param name
     *        The variable name.
     *
     * @param function
     *        The function used to calculate the variable values.
     *
     * @param arguments
     *        The argument names corresponding to the arguments in {@code function}.
     *
     * @throws IllegalArgumentException if {@code arguments.size()} is not equal to the number of arguments in {@code function}.
     */
    public VariableCalculation(String name, NumericFunctionN function, List<String> arguments) {
        this.name = name;
        this.function = function;
        if (function == null && arguments == null) {
            this.arguments = arguments;
        } else {
            this.arguments = Collections.unmodifiableList(new LinkedList<String>(arguments));
            if (function.getArgumentCount() != arguments.size())
                throw new IllegalArgumentException(String.format("Function (%s) argument count (%d) does not match actual argument count (%d): %s",function.toString(),function.getArgumentCount(),arguments.size(),arguments));
        }
    }

    /**
     * Constructor for a non-calculated variable. This is useful for cases where a {@code VariableCalculation} instance
     * is required for a variable, but where that variable is not calculated.
     *
     * @param name
     *        The name of the variable.
     */
    public VariableCalculation(String name) {
        this(name,null,null);
    }

    /**
     * Get the name of the variable.
     *
     * @return the variable's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the argument names for the variable calculation.
     *
     * @return the variable calculation's argument names.
     */
    public List<String> getArguments() {
        return arguments;
    }

    /**
     * Get the function used to calculate the variable values.
     *
     * @return the variable calculation's function.
     */
    public NumericFunctionN getFunction() {
        return function;
    }

    /**
     * Determine whether the variable is actually calculated or not.
     *
     * @return {@code true} if the variable is calculated, {@code false} if not.
     */
    public boolean isCalculated() {
        return arguments == null;
    }

    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof VariableCalculation))
            return false;
        VariableCalculation vc = (VariableCalculation) o;
        return name.equals(vc.name) &&
               function.equals(vc.function) &&
               arguments.equals(vc.arguments);
    }

    private List<CalculationTrace> getSubVariableTrace(String symbolicString, TextFormat symbolicVariable, int current, List<CalculationTrace> variableTraces) {
        List<CalculationTrace> traces = new LinkedList<CalculationTrace>();
        if (current == variableTraces.size()) {
            traces.add(new CalculationTrace(symbolicString));
        } else {
            //have to make format string safe as a regex
            boolean first = true;
            String format = symbolicVariable.getFormat(current+1);
            boolean tail = symbolicString.endsWith(format); //split function will "hide" trailing argument
            for (String s : symbolicString.split(format.replace("$","\\$").replace("+","\\+").replace("(","\\(").replace(")", "\\)"))) {
                if (first)
                    first = false;
                else
                    traces.add(variableTraces.get(current));
                if (!s.equals(""))
                    traces.addAll(getSubVariableTrace(s,symbolicVariable,current+1,variableTraces));
            }
            if (tail)
                traces.add(variableTraces.get(current));
        }
        return traces;
    }

    /**
     * Get the calculation trace for this variable calculation.
     *
     * @param provider
     *        The data provider used to calculate the variable value.
     *
     * @param observation
     *        The observation number (in the data provider) for the calculation trace.
     *
     * @return the calculation trace for observation number {@code observation} of this variable in {@code provider}.
     */
    public CalculationTrace getVariableTrace(DataProvider provider, int observation) {
        if (arguments == null)
            return new ConstantTrace(name,provider.getVariableData(name)[observation]);
        double[] args = new double[arguments.size()];
        int counter = 0;
        for (String variable : arguments)
            args[counter++] = provider.getVariableData(variable)[observation];
        double value = function.applyDouble(args);

        CalculationTrace trace = new CalculationTrace(name + " (calculated)","" + value,value);

        TextFormat symbolicTextFormat = new TextFormat(TextFormat.Conversion.STRING);
        TextFormat resolvedTextFormat = new TextFormat(TextFormat.Conversion.STRING);
        String symbolicFormat = function.getSymbolicFormat(symbolicTextFormat);
        String resolvedFormat = function.getSymbolicFormat(resolvedTextFormat);
        CalculationTrace calcTrace = new CalculationTrace(String.format(symbolicFormat,(Object[]) arguments.toArray(new String[arguments.size()])),
                                                          String.format(resolvedFormat,(Object[]) ArrayUtil.toDoubleArray(args)),value);

        List<CalculationTrace> argTraces = new ArrayList<CalculationTrace>();
        for (String argument : arguments)
            argTraces.add(provider.getVariableTrace(argument,observation));

        for (CalculationTrace subTrace : getSubVariableTrace(symbolicFormat,symbolicTextFormat,0,argTraces))
            calcTrace.addTraceElement(subTrace);

        trace.addTraceElement(calcTrace);
        return trace;
    }

    //todo: generalize this
//    public static VariableCalculation compose(NumericFunctions.NumericFunctionN function, List<VariableCalculation> calculations) {
//        if (function.getArgumentCount() != calculations.size())
//            throw new IllegalArgumentException(String.format("Function argument count (%d) must equal calculation count (%d).",function.getArgumentCount(),calculations.size()));
//
//    }

    /**
     * Compose a one-argument function with an existing {@code VariableCalculation}. That is, if the function is <code>f(x)</code>
     * and the variable calculation is <code>v(...)</code> then this method will return the variable calculation representing
     * <code>f(v(...))</code>.
     *
     * @param function
     *        The one-argument function.
     *
     * @param calculation
     *        The variable calculation.
     *
     * @return the variable calculation representing the composition of {@code function} with {@code calculation}.
     */
    public static VariableCalculation compose(final NumericFunction1 function, final VariableCalculation calculation) {
        return new VariableCalculation(calculation.getName(),NumericFunctions.compose(function,calculation.getFunction()),calculation.getArguments()) {
            public CalculationTrace getVariableTrace(DataProvider provider, int observation) {
                TextFormat symbolicTextFormat = new TextFormat(TextFormat.Conversion.STRING);
                TextFormat resolvedTextFormat = new TextFormat(TextFormat.Conversion.STRING);
                String symbolicString = function.getSymbolicFormat(symbolicTextFormat);
                CalculationTrace subTrace = calculation.getVariableTrace(provider,observation);
                CalculationTrace ct = new CalculationTrace(String.format(symbolicString,calculation.getName()),
                                                           String.format(function.getSymbolicFormat(resolvedTextFormat),subTrace.getResult()),
                                                           function.apply((double) subTrace.getResult()));
                boolean first = true;
                for (String s : symbolicString.split(symbolicTextFormat.getFormat(1).replace("$","\\$").replace("+","\\+").replace("(","\\(").replace(")", "\\)"))) {
                    if (first)
                        first = false;
                    else
                        ct.addTraceElement(subTrace);
                    ct.addTraceElement(new CalculationTrace(s));
                }
                return ct;
            }
        };

    }
}
