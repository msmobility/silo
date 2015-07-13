package com.pb.sawdust.tensor.decorators.primitive.size;

import com.pb.sawdust.tensor.decorators.size.D1TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.IntTensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.decorators.id.primitive.IdIntTensor;
import com.pb.sawdust.util.abacus.IterableAbacus;
import com.pb.sawdust.util.array.IntTypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArrayFactory;
import com.pb.sawdust.tensor.alias.vector.primitive.IntVector;

/**
 * The {@code IntD1TensorShell} class is a wrapper which sets a 1-dimensional {@code IntTensor} as a {@code Vector} (or,
 * more specifically, a {@code IntVector}).
 *
 * @author crf <br/>
 *         Started: Sat Oct 25 21:35:12 2008
 *         Revised: Dec 14, 2009 12:35:23 PM
 */
public class IntD1TensorShell extends D1TensorShell<Integer> implements IntVector {
    private final IntTensor tensor;

    /**
     * Constructor specifying tensor to wrap. The tensor must be 1-dimensional or an exception will be thrown.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @throws IllegalArgumentException if {@code tensor} is not 1 dimension in size.
     */
    public IntD1TensorShell(IntTensor tensor) {
        super(tensor);
        this.tensor = tensor;
    }

    /**
     * {@inheritDoc}
     *
     * This method just calls {@code IntTensor.getCell(index)}, 
     * and should be overridden if any efficiencies over that method can be made.
     */
    public int getCell(int index) {
        return tensor.getCell(index);
    }

    public int getCell(int ... indices) {
        if (indices.length != 1)
            throw new IllegalArgumentException("IntD1Tensor is 1 dimension in size, getValue passed with " + indices.length + " indices.");
        return getCell(indices[0]);
    }

    public Integer getValue(int ... indices) {
        return getCell(indices);
    }

    public Integer getValue(int index) {
        return getCell(index);
    }

    /**
     * {@inheritDoc}
     *
     * This method just calls {@code IntTensor.setCell(int,index)}, 
     * and should be overridden if any efficiencies over that method can be made.
     */
    public void setCell(int value, int index) {
        tensor.setCell(value,index);
    }

    public void setCell(int value, int ... indices) {
        if (indices.length != 1)
            throw new IllegalArgumentException("IntD1Tensor is 1 dimension in size, getValue passed with " + indices.length + " indices.");
        setCell(value,indices[0]);
    }

    public void setValue(Integer value, int ... indices) {
        setCell(value,indices);
    }

    public void setValue(Integer value, int index) {
        setCell(value,index);
    }
    
    public IntTypeSafeArray getTensorValues(Class<Integer> type) {
        return getTensorValues();
    }

    public IntTypeSafeArray getTensorValues() {
       @SuppressWarnings("unchecked") //getType requirements in tensor make this ok
        IntTypeSafeArray array = TypeSafeArrayFactory.intTypeSafeArray(getDimensions());
        for (int[] index : IterableAbacus.getIterableAbacus(getDimensions()))
            array.set(getCell(index),index);
        return array;
    }

    public void setTensorValues(IntTypeSafeArray valuesArray) {
        TensorImplUtil.setTensorValues(this,valuesArray);
    }

    public <I> IdIntTensor<I> getReferenceTensor(Index<I> index) {
        return (IdIntTensor<I>) super.getReferenceTensor(index);
    }

    protected IntTensor getComposedTensor(Index<?> index) {
        return TensorImplUtil.getComposedTensor(this,index); 
    }
}