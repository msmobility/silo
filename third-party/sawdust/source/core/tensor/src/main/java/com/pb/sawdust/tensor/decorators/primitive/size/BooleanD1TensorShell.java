package com.pb.sawdust.tensor.decorators.primitive.size;

import com.pb.sawdust.tensor.decorators.size.D1TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.BooleanTensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.decorators.id.primitive.IdBooleanTensor;
import com.pb.sawdust.util.abacus.IterableAbacus;
import com.pb.sawdust.util.array.BooleanTypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArrayFactory;
import com.pb.sawdust.tensor.alias.vector.primitive.BooleanVector;

/**
 * The {@code BooleanD1TensorShell} class is a wrapper which sets a 1-dimensional {@code BooleanTensor} as a {@code Vector} (or,
 * more specifically, a {@code BooleanVector}).
 *
 * @author crf <br/>
 *         Started: Sat Oct 25 21:35:12 2008
 *         Revised: Dec 14, 2009 12:35:24 PM
 */
public class BooleanD1TensorShell extends D1TensorShell<Boolean> implements BooleanVector {
    private final BooleanTensor tensor;

    /**
     * Constructor specifying tensor to wrap. The tensor must be 1-dimensional or an exception will be thrown.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @throws IllegalArgumentException if {@code tensor} is not 1 dimension in size.
     */
    public BooleanD1TensorShell(BooleanTensor tensor) {
        super(tensor);
        this.tensor = tensor;
    }

    /**
     * {@inheritDoc}
     *
     * This method just calls {@code BooleanTensor.getCell(index)}, 
     * and should be overridden if any efficiencies over that method can be made.
     */
    public boolean getCell(int index) {
        return tensor.getCell(index);
    }

    public boolean getCell(int ... indices) {
        if (indices.length != 1)
            throw new IllegalArgumentException("BooleanD1Tensor is 1 dimension in size, getValue passed with " + indices.length + " indices.");
        return getCell(indices[0]);
    }

    public Boolean getValue(int ... indices) {
        return getCell(indices);
    }

    public Boolean getValue(int index) {
        return getCell(index);
    }

    /**
     * {@inheritDoc}
     *
     * This method just calls {@code BooleanTensor.setCell(boolean,index)}, 
     * and should be overridden if any efficiencies over that method can be made.
     */
    public void setCell(boolean value, int index) {
        tensor.setCell(value,index);
    }

    public void setCell(boolean value, int ... indices) {
        if (indices.length != 1)
            throw new IllegalArgumentException("BooleanD1Tensor is 1 dimension in size, getValue passed with " + indices.length + " indices.");
        setCell(value,indices[0]);
    }

    public void setValue(Boolean value, int ... indices) {
        setCell(value,indices);
    }

    public void setValue(Boolean value, int index) {
        setCell(value,index);
    }
    
    public BooleanTypeSafeArray getTensorValues(Class<Boolean> type) {
        return getTensorValues();
    }

    public BooleanTypeSafeArray getTensorValues() {
       @SuppressWarnings("unchecked") //getType requirements in tensor make this ok
        BooleanTypeSafeArray array = TypeSafeArrayFactory.booleanTypeSafeArray(getDimensions());
        for (int[] index : IterableAbacus.getIterableAbacus(getDimensions()))
            array.set(getCell(index),index);
        return array;
    }

    public void setTensorValues(BooleanTypeSafeArray valuesArray) {
        TensorImplUtil.setTensorValues(this,valuesArray);
    }

    public <I> IdBooleanTensor<I> getReferenceTensor(Index<I> index) {
        return (IdBooleanTensor<I>) super.getReferenceTensor(index);
    }

    protected BooleanTensor getComposedTensor(Index<?> index) {
        return TensorImplUtil.getComposedTensor(this,index); 
    }
}