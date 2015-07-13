package com.pb.sawdust.model.models.provider.tensor;

import com.pb.sawdust.model.models.provider.AbstractDataProvider;
import com.pb.sawdust.tensor.alias.vector.primitive.DoubleVector;
import com.pb.sawdust.tensor.decorators.primitive.DoubleTensor;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.index.CollapsingIndex;

import static com.pb.sawdust.util.Range.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The {@code VectorDataProvider} class is a data provider implementation which provides data, in order, from a {@code DoubleVector}.
 * Specifically, the data provider only holds one variable (named at construction time) and the data is sourced from a
 * single vector. The length of the provider is the same as the length of the source vector.
 * <p>
 * This class also contains convenience constructors to obtain a vector data provider built from a tensor where all but
 * one dimension is fixed.
 * <p>
 * Note that to use numeric, but non-{@code double}, vectors, the "numeric tensor" methods in {@code com.pb.tensor.TensorUtil}
 * can be employed.
 *
 * @author crf
 *         Started 4/6/12 6:44 AM
 */
public class VectorDataProvider extends AbstractDataProvider {
    private final String variableName;
    private final DoubleVector vector;

    /**
     * Constructor specifying the variable name, the source data vector, and the tensor factory.
     *
     * @param variableName
     *        The name of the variable provided by this tensor provider.
     *
     * @param vector
     *        The vector holding the data.
     *
     * @param factory
     *        The tensor factory used to build data results.
     */
    public VectorDataProvider(String variableName, DoubleVector vector, TensorFactory factory) {
        super(factory);
        this.variableName = variableName;
        this.vector = vector;
    }

    private static DoubleVector buildVector(DoubleTensor tensor, Map<Integer,Integer> dimensionsToCollapse) {
        if (dimensionsToCollapse.size() + 1 != tensor.size())
            throw new IllegalArgumentException("The size of dimensionsToCollapse (" + dimensionsToCollapse.size() + ") must be one less than the size of the source tensor (" + tensor.size() + ")");
        return (DoubleVector) tensor.getReferenceTensor(new CollapsingIndex<>(tensor.getIndex(),dimensionsToCollapse));
    }

    /**
     * Convenience constructor for building a vector data provider from a source data tensor, where all but one dimension
     * in the tensor is fixed at a specified location.
     *
     * @param variableName
     *        The name of the variable provided by this tensor provider.
     *
     * @param tensor
     *        The tensor holding the data.
     *
     * @param dimensionsToCollapse
     *        A mapping from the (0-based) dimension to collapse (fix), to the (0-based) index value to hold that dimension
     *        at. The number of entries in this map must be one less than the size of {@code tensor}.
     *
     * @param factory
     *        The tensor factory used to build data results.
     *
     * @throws IllegalArgumentException if any of the keys in {@code dimensionsToCollapse} does not correspond to a dimension
     *                                  in {@code tensor}, if any of the values in {@code dimensionsToCollapse} is out of
     *                                  bounds for its corresponding dimension (key), or if the size of {@code dimensionsToCollapse}
     *                                  is not equal to one less than <code>tensor.size()</code>.
     */
    public VectorDataProvider(String variableName, DoubleTensor tensor, Map<Integer,Integer> dimensionsToCollapse, TensorFactory factory) {
        this(variableName,buildVector(tensor,dimensionsToCollapse),factory);
    }

    @Override
    public int getDataLength() {
        return vector.size(0);
    }

    @Override
    public boolean hasVariable(String variable) {
        return variableName.equals(variable);
    }

    @Override
    public Set<String> getVariables() {
        Set<String> variable = new HashSet<>();
        variable.add(variableName);
        return variable;
    }

    @Override
    public double[] getVariableData(String variable) {
        if (!hasVariable(variable))
            throw new IllegalArgumentException("Variable not found: " + variable);
        return (double[]) vector.getTensorValues().getArray();
    }

    @Override
    public double[] getVariableData(String variable, int start, int end) {
        if (!hasVariable(variable))
            throw new IllegalArgumentException("Variable not found: " + variable);
        double[] data = new double[end-start];
        int counter = 0;
        for (int i : range(start,end))
            data[counter++] = vector.getCell(i);
        return data;
    }
}
