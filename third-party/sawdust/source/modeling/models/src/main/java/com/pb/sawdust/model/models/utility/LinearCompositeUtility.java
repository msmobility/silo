package com.pb.sawdust.model.models.utility;

import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.model.models.trace.CalculationTrace;
import com.pb.sawdust.model.models.trace.ConstantTrace;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.alias.vector.primitive.DoubleVector;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.collections.LinkedSetList;
import com.pb.sawdust.util.collections.SetList;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * The {@code LinearCompositeUtility} class provides a way to compose multiple linear utlities into a single utility.
 * If more than one utility share a variable, then those variables' coefficients will be combined into a single
 * coefficient (which is valid since the utilities are linear).
 *
 * @author crf <br/>
 *         Started Sep 14, 2010 6:38:36 AM
 */
public class LinearCompositeUtility extends AbstractLinearUtility {
    private final SimpleLinearUtility linearUtility;
    private final LinearUtility[] utilities; //for calculation trace

    /**
     * Constructor specifying the utilty's name and utilities it is composed from.
     *
     * @param name
     *        The name of the utility.
     *
     * @param factory
     *        The tensor factory to use to construct the tensors returned by this class.
     *
     * @param utilities
     *        The utilities to compose into this utility.
     *
     * @throws IllegalArgumentException if {@code utilities} are empty, or if all of the utilities in {@code utilities}
     *                                  are empty.
     */
    public LinearCompositeUtility(String name, TensorFactory factory, LinearUtility ... utilities) {
        super(name);
        SetList<String> variables = new LinkedSetList<String>();
        List<Double> coefficients = new LinkedList<Double>();
        for (LinearUtility utility : utilities) {
            int index;
            for (String variable : utility.getVariables()) {
                double coefficient = utility.getCoefficient(variable);
                if ((index = variables.indexOf(variable)) > -1) {
                    coefficients.add(index,coefficients.remove(index)+coefficient);
                } else {
                    variables.add(variable);
                    coefficients.add(coefficient);
                }
            }
        }
        linearUtility = new SimpleLinearUtility(variables,coefficients,factory);
        this.utilities = ArrayUtil.copyArray(utilities);
    }

    /**
     * Constructor specifying the utilities the utility will be composed from. {@link #DEFAULT_LINEAR_UTLITY_NAME}
     * will be used for the utility name.
     *
     * @param factory
     *        The tensor factory to use to construct the tensors returned by this class.
     *
     * @param utilities
     *        The utilities to compose into this utility.
     *
     * @throws IllegalArgumentException if {@code utilities} are empty, or if all of the utilities in {@code utilities}
     *                                  are empty.
     */
    public LinearCompositeUtility(TensorFactory factory, LinearUtility ... utilities) {
        this(DEFAULT_LINEAR_UTLITY_NAME,factory,utilities);
    }

    @Override
    public SetList<String> getVariables() {
        return linearUtility.getVariables();
    }

    @Override
    public double getCoefficient(String variable) {
        return linearUtility.getCoefficient(variable);
    }

    @Override
    public DoubleVector getCoefficients() {
        return linearUtility.getCoefficients();
    }

    @Override
    public DoubleVector getUtilities(DataProvider data) {
        return linearUtility.getUtilities(data);
    }

    @Override
    protected CalculationTrace getCalculationTraceElement(String variableName, double coefficient, double variable, DataProvider subData) {
//        List<Double> coefficients = new LinkedList<Double>();
//        for (LinearUtility utility : utilities)
//            if (utility.getVariables().contains(variableName))
//                coefficients.add(utility.getCoefficient(variableName));
//        Iterator<Double> it = coefficients.iterator();
//        StringBuilder coefficientTrace = new StringBuilder();
//        coefficientTrace.append("(").append(it.next());
//        while (it.hasNext())
//            coefficientTrace.append(" + ").append(it.next());
//        coefficientTrace.append(")");
//        return new CalculationTrace.CalculationTraceElement("[" + variableName + " coefficient]*" + variableName,coefficientTrace + "*" + variable,coefficient*variable);

        CalculationTrace trace = new CalculationTrace("[" + variableName + " coefficient]*" + variableName,coefficient + "*" + variable,coefficient*variable);
        List<Double> coefficients = new LinkedList<Double>();
        List<String> utilityNames = new LinkedList<String>();
        for (LinearUtility utility : utilities) {
            if (utility.getVariables().contains(variableName)) {
                coefficients.add(utility.getCoefficient(variableName));
                utilityNames.add(utility.getName());
            }
        }
        CalculationTrace coefficientTrace = new CalculationTrace("[" + variableName + " coefficient]",coefficient) ;
        Iterator<String> utilityNameIt = utilityNames.iterator();
        if (coefficients.size() > 1)
            for (double d : coefficients)
                coefficientTrace.addTraceElement("+",new ConstantTrace("[" + variableName + " coefficient (from utility: " + utilityNameIt.next() + ")]",d));
        CalculationTrace variableTrace = subData.getVariableTrace(variableName,0);
        if (coefficients.size() > 1 || variableTrace != null) {
            trace.addTraceElement(coefficientTrace);
            trace.addTraceElement("*",variableTrace);
        }
        return trace;
    }
}
