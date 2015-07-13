package com.pb.sawdust.model.models.utility;

import com.pb.sawdust.calculator.tensor.la.mm.DefaultMatrixMultiplication;
import com.pb.sawdust.model.integration.blas.JBlasMatrixMultiplication;
import com.pb.sawdust.model.integration.blas.JCudaMatrixMultiplication;
import com.pb.sawdust.calculator.tensor.la.mm.MatrixMultiplication;
import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.alias.vector.primitive.DoubleVector;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.array.DoubleTypeSafeArray;
import com.pb.sawdust.util.collections.LinkedSetList;
import com.pb.sawdust.util.collections.SetList;
import com.pb.sawdust.util.collections.UnmodifiableCollections;

import java.util.*;

/**
 * The {@code SimpleLinearUtility} class provides a basic, immutable implementation of {@code LinearUtility}.
 *
 * @author crf <br/>
 *         Started Jul 24, 2010 8:03:05 AM
 */
public class SimpleLinearUtility extends AbstractLinearUtility {
    private final DoubleVector coefficients;
    private final MatrixMultiplication mm;
    private final SetList<String> variables;
    private final Map<String,Double> variableToCoefficient;

    /**
     * Constructor specifying the utility function's name, variables, and coefficients.
     *
     * @param name
     *        The utility name.
     *
     * @param variables
     *        The (ordered) variables in the utility.
     *
     * @param coefficients
     *        The coefficients for the utility. The order of the coefficients should correspond to that of {@code variables}.
     *
     * @param factory
     *        The tensor factory to use to construct the tensors returned by this class.
     *
     * @param skipZeroCoefficients
     *        If {@code true}, then variables with coefficients equal to zero will not be included in the utility function.
     *
     * @throws IllegalArgumentException if the size of {@code variables} does not equal {@code coefficients}, or if
     *                                  {@code variables} is empty.
     */
    public SimpleLinearUtility(String name, SetList<String> variables, List<Double> coefficients, TensorFactory factory, boolean skipZeroCoefficients) {
        super(name);
        if (variables.size() != coefficients.size())
            throw new IllegalArgumentException("Variable (" + variables.size() + ") and coefficient (" + coefficients.size() + ") count must be equal.");

        if (skipZeroCoefficients) { //skip any variable whose coefficient == 0.0
            SetList<String> vars = new LinkedSetList<String>();
            List<Double> coeffs = new LinkedList<Double>();
            Iterator<String> varIt = variables.iterator();
            Iterator<Double> coeffIt = coefficients.iterator();
            while (varIt.hasNext()) {
                String var = varIt.next();
                Double coeff = coeffIt.next();
                if (coeff != 0.0) {
                    vars.add(var);
                    coeffs.add(coeff);
                }
            }
            if (variables.size() > 0 && vars.size() == 0) { //all zero, so leave in first so that equation can exist
                vars.add(variables.get(0));
                coeffs.add(coefficients.get(0));
            }
            variables = vars;
            coefficients = coeffs;
        }

        DoubleVector coeff = (DoubleVector) factory.doubleTensor(coefficients.size());
        coeff.setTensorValues(new DoubleTypeSafeArray(ArrayUtil.toPrimitive(coefficients.toArray(new Double[coefficients.size()]))));
        mm = getMatrixMultiplication(factory);
        this.coefficients = (DoubleVector) TensorUtil.unmodifiableTensor(coeff);
        this.variables = UnmodifiableCollections.unmodifiableSetList(variables);
        variableToCoefficient = new HashMap<String,Double>();
        Iterator<Double> cit = coefficients.iterator();
        for (String variable : variables)
            variableToCoefficient.put(variable,cit.next());
    }

    /**
     *
     * Constructor specifying the utility function's variables, and coefficients. {@link #DEFAULT_LINEAR_UTLITY_NAME}
     * will be used for the utility name.
     *
     * @param variables
     *        The (ordered) variables in the utility.
     *
     * @param coefficients
     *        The coefficients for the utility. The order of the coefficients should correspond to that of {@code variables}.
     *
     * @param factory
     *        The tensor factory to use to construct the tensors returned by this class.
     *
     * @param skipZeroCoefficients
     *        If {@code true}, then variables with coefficients equal to zero will not be included in the utility function.
     *
     * @throws IllegalArgumentException if the size of {@code variables} does not equal {@code coefficients}, or if
     *                                  {@code variables} is empty.
     */
    public SimpleLinearUtility(SetList<String> variables, List<Double> coefficients, TensorFactory factory, boolean skipZeroCoefficients) {
        this(DEFAULT_LINEAR_UTLITY_NAME,variables,coefficients,factory,skipZeroCoefficients);
    }

    /**
     * Constructor specifying the utility function's name, variables, and coefficients. If any of the variables has
     * a corresponding coefficient equal to zero, then that variable will not be included in the utility.
     *
     * @param name
     *        The utility name.
     *
     * @param variables
     *        The (ordered) variables in the utility.
     *
     * @param coefficients
     *        The coefficients for the utility. The order of the coefficients should correspond to that of {@code variables}.
     *
     * @param factory
     *        The tensor factory to use to construct the tensors returned by this class.
     *
     * @throws IllegalArgumentException if the size of {@code variables} does not equal {@code coefficients}, or if
     *                                  {@code variables} is empty.
     */
    public SimpleLinearUtility(String name, SetList<String> variables, List<Double> coefficients, TensorFactory factory) {
        this(name,variables,coefficients,factory,true);
    }

    /**
     * Constructor specifying the utility function's variables, and coefficients. {@link #DEFAULT_LINEAR_UTLITY_NAME}
     * will be used for the utility name.  If any of the variables has a corresponding coefficient equal to zero, then
     * that variable will not be included in the utility.
     *
     * @param variables
     *        The (ordered) variables in the utility.
     *
     * @param coefficients
     *        The coefficients for the utility. The order of the coefficients should correspond to that of {@code variables}.
     *
     * @param factory
     *        The tensor factory to use to construct the tensors returned by this class.
     *
     * @throws IllegalArgumentException if the size of {@code variables} does not equal {@code coefficients}, or if
     *                                  {@code variables} is empty.
     */
    public SimpleLinearUtility(SetList<String> variables, List<Double> coefficients, TensorFactory factory) {
        this(variables,coefficients,factory,true);
    }

    private MatrixMultiplication getMatrixMultiplication(TensorFactory factory) {
//        return new DefaultMatrixMultiplication(factory); //todo: need to inject this, I think
        return new JBlasMatrixMultiplication(factory); //todo: need to inject this, I think
//        return new JCudaMatrixMultiplication(factory); //todo: need to inject this, I think
//        return null;
    }

    public SetList<String> getVariables() {
        return variables;
    }

    public DoubleVector getCoefficients() {
        return coefficients;
    }

    public double getCoefficient(String variable) {
        if (!variableToCoefficient.containsKey(variable))
            throw new IllegalArgumentException("Variable not found: " + variable);
        return variableToCoefficient.get(variable);
    }

    public DoubleVector getUtilities(DataProvider data) {
        return mm.multiply(coefficients,data.getData(variables),true);
    }
}
