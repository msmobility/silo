package com.pb.sawdust.tensor.decorators.primitive.size;

import com.pb.sawdust.tensor.decorators.size.D1TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.FloatTensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.decorators.id.primitive.IdFloatTensor;
import com.pb.sawdust.util.abacus.IterableAbacus;
import com.pb.sawdust.util.array.FloatTypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArrayFactory;
import com.pb.sawdust.tensor.alias.vector.primitive.FloatVector;

/**
 * The {@code FloatD1TensorShell} class is a wrapper which sets a 1-dimensional {@code FloatTensor} as a {@code Vector} (or,
 * more specifically, a {@code FloatVector}).
 *
 * @author crf <br/>
 *         Started: Sat Oct 25 21:35:12 2008
 *         Revised: Dec 14, 2009 12:35:23 PM
 */
public class FloatD1TensorShell extends D1TensorShell<Float> implements FloatVector {
    private final FloatTensor tensor;

    /**
     * Constructor specifying tensor to wrap. The tensor must be 1-dimensional or an exception will be thrown.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @throws IllegalArgumentException if {@code tensor} is not 1 dimension in size.
     */
    public FloatD1TensorShell(FloatTensor tensor) {
        super(tensor);
        this.tensor = tensor;
    }

    /**
     * {@inheritDoc}
     *
     * This method just calls {@code FloatTensor.getCell(index)}, 
     * and should be overridden if any efficiencies over that method can be made.
     */
    public float getCell(int index) {
        return tensor.getCell(index);
    }

    public float getCell(int ... indices) {
        if (indices.length != 1)
            throw new IllegalArgumentException("FloatD1Tensor is 1 dimension in size, getValue passed with " + indices.length + " indices.");
        return getCell(indices[0]);
    }

    public Float getValue(int ... indices) {
        return getCell(indices);
    }

    public Float getValue(int index) {
        return getCell(index);
    }

    /**
     * {@inheritDoc}
     *
     * This method just calls {@code FloatTensor.setCell(float,index)}, 
     * and should be overridden if any efficiencies over that method can be made.
     */
    public void setCell(float value, int index) {
        tensor.setCell(value,index);
    }

    public void setCell(float value, int ... indices) {
        if (indices.length != 1)
            throw new IllegalArgumentException("FloatD1Tensor is 1 dimension in size, getValue passed with " + indices.length + " indices.");
        setCell(value,indices[0]);
    }

    public void setValue(Float value, int ... indices) {
        setCell(value,indices);
    }

    public void setValue(Float value, int index) {
        setCell(value,index);
    }
    
    public FloatTypeSafeArray getTensorValues(Class<Float> type) {
        return getTensorValues();
    }

    public FloatTypeSafeArray getTensorValues() {
       @SuppressWarnings("unchecked") //getType requirements in tensor make this ok
        FloatTypeSafeArray array = TypeSafeArrayFactory.floatTypeSafeArray(getDimensions());
        for (int[] index : IterableAbacus.getIterableAbacus(getDimensions()))
            array.set(getCell(index),index);
        return array;
    }

    public void setTensorValues(FloatTypeSafeArray valuesArray) {
        TensorImplUtil.setTensorValues(this,valuesArray);
    }

    public <I> IdFloatTensor<I> getReferenceTensor(Index<I> index) {
        return (IdFloatTensor<I>) super.getReferenceTensor(index);
    }

    protected FloatTensor getComposedTensor(Index<?> index) {
        return TensorImplUtil.getComposedTensor(this,index); 
    }
}