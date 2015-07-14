package com.pb.sawdust.model.models.utility;

import com.pb.sawdust.calculator.NumericFunctions;
import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.model.models.provider.VariableCalculation;
import com.pb.sawdust.model.models.trace.CalculationTrace;
import com.pb.sawdust.model.models.trace.ConstantTrace;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.alias.vector.primitive.DoubleVector;
import com.pb.sawdust.util.collections.LinkedSetList;
import com.pb.sawdust.util.collections.SetList;
import com.pb.sawdust.util.collections.UnmodifiableCollections;

import java.util.LinkedList;
import java.util.Set;

/**
 * The {@code EmptyUtility} class provides {@code Utility} implementations which are empty.  The utility functions will
 * thus always evaluate to zero.  It fulfills the {@code Utility} contract <i>except</i> for the {@link #getCoefficients()}
 * method, which returns a single element vector (with a value of zero); the reason that a single element is returned
 * is that an empty {@code Vector} cannot be created.
 *
 * @author crf <br/>
 *         Started Sep 15, 2010 1:00:04 PM
 */
public class EmptyUtility implements Utility {
    private static final TensorFactory factory = ArrayTensor.getFactory();
    private static final SetList<String> variables = UnmodifiableCollections.unmodifiableSetList(new LinkedSetList<String>());
    private static final DoubleVector coefficients = (DoubleVector) factory.doubleTensor(1);
    private static final String name = "Empty Utility";
    private static final CalculationTrace trace = new ConstantTrace(name,0.0);

    /**
     * Get an empty utility.
     *
     * @return an empty utility.
     */
    public static Utility getEmptyUtility() {
        return instance;
    }

    /**
     * Get an empty linear utility.
     *
     * @return an empty linear utility.
     */
    public static LinearUtility getEmptyLinearUtility() {
        return linearInstance;
    }

    private static EmptyUtility instance = new EmptyUtility();
    private static LinearUtility linearInstance = new EmptyLinearUtility(); //really could just call this instance, but to make intentions clear, keep 'em separate

    private EmptyUtility() {}

    public SetList<String> getVariables() {
        return variables;
    }

    public Set<String> getVariableSet() {
        return variables;
    }

    /**
     * This function returns a single element vector containing a zero.  This is inconsistent with the fact that the
     * utility contains no variables, but is necessary because an empty {@code Vector} cannot be created.
     *
     * @return a single element vector containing a zero.
     */
    public DoubleVector getCoefficients() {
        return coefficients;
    }

    public DoubleVector getUtilities(DataProvider data) {
        return (DoubleVector) factory.doubleTensor(data.getDataLength());
    }

    public CalculationTrace traceCalculation(DataProvider data, int observation) {
        return trace;
    }

    public String getName() {
        return name;
    }

    @Override
    public VariableCalculation getCalculation() {
        return new VariableCalculation(name,NumericFunctions.constant(0.0),new LinkedList<String>());
    }

    private static class EmptyLinearUtility extends EmptyUtility implements LinearUtility {  
        public double getCoefficient(String variable) {
            throw new IllegalArgumentException("Variable not found: " + variable);
        }
    }
}
