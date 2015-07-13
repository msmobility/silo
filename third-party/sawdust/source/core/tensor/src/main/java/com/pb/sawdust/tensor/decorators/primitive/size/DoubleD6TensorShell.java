package com.pb.sawdust.tensor.decorators.primitive.size;

import com.pb.sawdust.tensor.decorators.size.D6TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.DoubleTensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.decorators.id.primitive.IdDoubleTensor;
import com.pb.sawdust.util.abacus.IterableAbacus;
import com.pb.sawdust.util.array.DoubleTypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArrayFactory;


/**
 * The {@code DoubleD6TensorShell} class is a wrapper which sets a 6-dimensional {@code DoubleTensor} as a {@code D6Tensor} (or,
 * more specifically, a {@code DoubleD6Tensor}).
 *
 * @author crf <br/>
 *         Started: Sat Oct 25 21:35:12 2008
 *         Revised: Dec 14, 2009 12:35:30 PM
 */
public class DoubleD6TensorShell extends D6TensorShell<Double> implements DoubleD6Tensor {
    private final DoubleTensor tensor;

    /**
     * Constructor specifying tensor to wrap. The tensor must be 6-dimensional or an exception will be thrown.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @throws IllegalArgumentException if {@code tensor} is not 6 dimension in size.
     */
    public DoubleD6TensorShell(DoubleTensor tensor) {
        super(tensor);
        this.tensor = tensor;
    }

    /**
     * {@inheritDoc}
     *
     * This method just calls {@code DoubleTensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index)}, 
     * and should be overridden if any efficiencies over that method can be made.
     */
    public double getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
        return tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index);
    }

    public double getCell(int ... indices) {
        if (indices.length != 6)
            throw new IllegalArgumentException("DoubleD6Tensor is 6 dimension in size, getValue passed with " + indices.length + " indices.");
        return getCell(indices[0],indices[1],indices[2],indices[3],indices[4],indices[5]);
    }

    public Double getValue(int ... indices) {
        return getCell(indices);
    }

    public Double getValue(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
        return getCell(d0index,d1index,d2index,d3index,d4index,d5index);
    }

    /**
     * {@inheritDoc}
     *
     * This method just calls {@code DoubleTensor.setCell(double,d0index,d1index,d2index,d3index,d4index,d5index)}, 
     * and should be overridden if any efficiencies over that method can be made.
     */
    public void setCell(double value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
        tensor.setCell(value,d0index,d1index,d2index,d3index,d4index,d5index);
    }

    public void setCell(double value, int ... indices) {
        if (indices.length != 6)
            throw new IllegalArgumentException("DoubleD6Tensor is 6 dimension in size, getValue passed with " + indices.length + " indices.");
        setCell(value,indices[0],indices[1],indices[2],indices[3],indices[4],indices[5]);
    }

    public void setValue(Double value, int ... indices) {
        setCell(value,indices);
    }

    public void setValue(Double value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
        setCell(value,d0index,d1index,d2index,d3index,d4index,d5index);
    }
    
    public DoubleTypeSafeArray getTensorValues(Class<Double> type) {
        return getTensorValues();
    }

    public DoubleTypeSafeArray getTensorValues() {
       @SuppressWarnings("unchecked") //getType requirements in tensor make this ok
        DoubleTypeSafeArray array = TypeSafeArrayFactory.doubleTypeSafeArray(getDimensions());
        for (int[] index : IterableAbacus.getIterableAbacus(getDimensions()))
            array.set(getCell(index),index);
        return array;
    }

    public void setTensorValues(DoubleTypeSafeArray valuesArray) {
        TensorImplUtil.setTensorValues(this,valuesArray);
    }

    public <I> IdDoubleTensor<I> getReferenceTensor(Index<I> index) {
        return (IdDoubleTensor<I>) super.getReferenceTensor(index);
    }

    protected DoubleTensor getComposedTensor(Index<?> index) {
        return TensorImplUtil.getComposedTensor(this,index); 
    }
}