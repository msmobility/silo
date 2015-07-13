package com.pb.sawdust.tensor.decorators.primitive.size;

import com.pb.sawdust.tensor.decorators.size.D2TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.DoubleTensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.decorators.id.primitive.IdDoubleTensor;
import com.pb.sawdust.util.abacus.IterableAbacus;
import com.pb.sawdust.util.array.DoubleTypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArrayFactory;
import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;

/**
 * The {@code DoubleD2TensorShell} class is a wrapper which sets a 2-dimensional {@code DoubleTensor} as a {@code Matrix} (or,
 * more specifically, a {@code DoubleMatrix}).
 *
 * @author crf <br/>
 *         Started: Sat Oct 25 21:35:12 2008
 *         Revised: Dec 14, 2009 12:35:25 PM
 */
public class DoubleD2TensorShell extends D2TensorShell<Double> implements DoubleMatrix {
    private final DoubleTensor tensor;

    /**
     * Constructor specifying tensor to wrap. The tensor must be 2-dimensional or an exception will be thrown.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @throws IllegalArgumentException if {@code tensor} is not 2 dimension in size.
     */
    public DoubleD2TensorShell(DoubleTensor tensor) {
        super(tensor);
        this.tensor = tensor;
    }

    /**
     * {@inheritDoc}
     *
     * This method just calls {@code DoubleTensor.getCell(d0index,d1index)}, 
     * and should be overridden if any efficiencies over that method can be made.
     */
    public double getCell(int d0index, int d1index) {
        return tensor.getCell(d0index,d1index);
    }

    public double getCell(int ... indices) {
        if (indices.length != 2)
            throw new IllegalArgumentException("DoubleD2Tensor is 2 dimension in size, getValue passed with " + indices.length + " indices.");
        return getCell(indices[0],indices[1]);
    }

    public Double getValue(int ... indices) {
        return getCell(indices);
    }

    public Double getValue(int d0index, int d1index) {
        return getCell(d0index,d1index);
    }

    /**
     * {@inheritDoc}
     *
     * This method just calls {@code DoubleTensor.setCell(double,d0index,d1index)}, 
     * and should be overridden if any efficiencies over that method can be made.
     */
    public void setCell(double value, int d0index, int d1index) {
        tensor.setCell(value,d0index,d1index);
    }

    public void setCell(double value, int ... indices) {
        if (indices.length != 2)
            throw new IllegalArgumentException("DoubleD2Tensor is 2 dimension in size, getValue passed with " + indices.length + " indices.");
        setCell(value,indices[0],indices[1]);
    }

    public void setValue(Double value, int ... indices) {
        setCell(value,indices);
    }

    public void setValue(Double value, int d0index, int d1index) {
        setCell(value,d0index,d1index);
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