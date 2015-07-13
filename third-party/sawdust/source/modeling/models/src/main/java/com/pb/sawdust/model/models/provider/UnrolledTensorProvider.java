package com.pb.sawdust.model.models.provider;

import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;
import com.pb.sawdust.tensor.alias.vector.primitive.DoubleVector;
import com.pb.sawdust.tensor.decorators.primitive.DoubleTensor;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.index.AbstractIndex;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.StandardIndex;
import com.pb.sawdust.util.Range;
import com.pb.sawdust.util.abacus.Abacus;
import com.pb.sawdust.util.abacus.IterableAbacus;
import com.pb.sawdust.util.array.ArrayUtil;

import java.util.*;

/**
 * The {@code UnrolledTensorProvider} ...
 *
 * @author crf
 *         Started 1/23/13 5:24 PM
 */
public class UnrolledTensorProvider extends AbstractDataProvider {
    private final DoubleTensor tensor;
    private final int length;
    private final String variable;

    public UnrolledTensorProvider(TensorFactory factory, DoubleTensor tensor, String variable) {
        super(factory);
        this.tensor = tensor;
        length = getElementLength(tensor);
        this.variable = variable;
    }

    public UnrolledTensorProvider(int id, TensorFactory factory, DoubleTensor tensor, String variable) {
        super(id,factory);
        this.tensor = tensor;
        length = getElementLength(tensor);
        this.variable = variable;
    }

    private int getElementLength(DoubleTensor tensor) {
        long length = TensorUtil.getElementCount(tensor);
        if (length > Integer.MAX_VALUE)
            throw new IllegalArgumentException("Total element count exceeds allowable length for data provider; specify a subtensor instead: " + length);
        return (int) length;
    }

    @Override
    public int getDataLength() {
        return length;
    }

    @Override
    public boolean hasVariable(String variable) {
        return this.variable.equals(variable);
    }

    @Override
    public Set<String> getVariables() {
        Set<String> variables = new HashSet<>();
        variables.add(variable);
        return variables;
    }

    @Override
    public double[] getVariableData(String variable) {
        return getVariableData(variable,0,length);
    }

    @Override
    public double[] getVariableData(String variable, int start, int end) {
        if (end <= start)
            throw new IllegalArgumentException("Data must have a strictly positive range (start=" + start + ", end=" + end + ")");
        if (end > getDataLength()  || start < 0)
            throw new IllegalArgumentException(String.format("Data (start: %d, end: %d) out of bounds for provider of length %d",start,end,getDataLength()));
        int length = end-start;
        double[] data = new double[length];
        Abacus abacus = new Abacus(tensor.getDimensions());
        abacus.setAbacusAtPosition(start);
        int position = 0;
        while (position < length)
            data[position++] = tensor.getCell(abacus.next());
        return data;
    }

    public static void rollIntoTensor(DoubleVector data, DoubleTensor tensor) {
        if (data.size(0) != TensorUtil.getElementCount(tensor))
            throw new IllegalArgumentException(String.format("Data (%d) and tensor (%d) do not have equal elements",data.size(0),TensorUtil.getElementCount(tensor)));
        int counter = 0;
        for (int[] index : IterableAbacus.getIterableAbacus(tensor.getDimensions()))
            tensor.setCell(data.getCell(counter++),index);
    }

    public static DoubleTensor asRolledTensor(final DoubleVector data, int[] dimensions) {
        return data.getReferenceTensor(
            new StandardIndex(dimensions) {

                public boolean isValidFor(Tensor tensor) {
                    return Arrays.equals(data.getDimensions(),tensor.getDimensions());
                }

                public int[] getIndices(int ... indices) {
                    int location = 0;
                    for (int i = 0; i < indices.length; i++)
                        location += indices[i]*dimensions[i];
                    return new int[] {location};
                }
            }
        );
    }

    public static DoubleMatrix asRolledMatrix(final DoubleVector data, final int dim0, final int dim1) {
        if (dim0*dim1 != data.size(0))
            throw new IllegalArgumentException(String.format("Invalid matrix size (%d,%d) for vector of length %d",dim0,dim1,data.size(0)));
        return (DoubleMatrix) data.getReferenceTensor(
            new StandardIndex(dim0,dim1) {

                public boolean isValidFor(Tensor tensor) {
                    return Arrays.equals(data.getDimensions(),tensor.getDimensions());
                }

                public int[] getIndices(int ... indices) {
                    return new int[] {indices[0]*dim1 + indices[1]};
                }
            }
        );
    }
}
