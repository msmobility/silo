package com.pb.sawdust.tensor.decorators.primitive.size;

import com.pb.sawdust.tensor.decorators.size.D1TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.DoubleTensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.decorators.id.primitive.IdDoubleTensor;
import com.pb.sawdust.util.abacus.IterableAbacus;
import com.pb.sawdust.util.array.DoubleTypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArrayFactory;
import com.pb.sawdust.tensor.alias.vector.primitive.DoubleVector;

/**
 * The {@code DoubleD1TensorShell} class is a wrapper which sets a 1-dimensional {@code DoubleTensor} as a {@code Vector} (or,
 * more specifically, a {@code DoubleVector}).
 *
 * @author crf <br/>
 *         Started: Sat Oct 25 21:35:12 2008
 *         Revised: Dec 14, 2009 12:35:24 PM
 */
public class DoubleD1TensorShell extends D1TensorShell<Double> implements DoubleVector {
    private final DoubleTensor tensor;

    /**
     * Constructor specifying tensor to wrap. The tensor must be 1-dimensional or an exception will be thrown.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @throws IllegalArgumentException if {@code tensor} is not 1 dimension in size.
     */
    public DoubleD1TensorShell(DoubleTensor tensor) {
        super(tensor);
        this.tensor = tensor;
    }

    /**
     * {@inheritDoc}
     *
     * This method just calls {@code DoubleTensor.getCell(index)}, 
     * and should be overridden if any efficiencies over that method can be made.
     */
    public double getCell(int index) {
        return tensor.getCell(index);
    }

    public double getCell(int ... indices) {
        if (indices.length != 1)
            throw new IllegalArgumentException("DoubleD1Tensor is 1 dimension in size, getValue passed with " + indices.length + " indices.");
        return getCell(indices[0]);
    }

    public Double getValue(int ... indices) {
        return getCell(indices);
    }

    public Double getValue(int index) {
        return getCell(index);
    }

    /**
     * {@inheritDoc}
     *
     * This method just calls {@code DoubleTensor.setCell(double,index)}, 
     * and should be overridden if any efficiencies over that method can be made.
     */
    public void setCell(double value, int index) {
        tensor.setCell(value,index);
    }

    public void setCell(double value, int ... indices) {
        if (indices.length != 1)
            throw new IllegalArgumentException("DoubleD1Tensor is 1 dimension in size, getValue passed with " + indices.length + " indices.");
        setCell(value,indices[0]);
    }

    public void setValue(Double value, int ... indices) {
        setCell(value,indices);
    }

    public void setValue(Double value, int index) {
        setCell(value,index);
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