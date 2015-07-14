package com.pb.sawdust.tensor.decorators.primitive.size;

import com.pb.sawdust.tensor.decorators.size.D1TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.ShortTensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.decorators.id.primitive.IdShortTensor;
import com.pb.sawdust.util.abacus.IterableAbacus;
import com.pb.sawdust.util.array.ShortTypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArrayFactory;
import com.pb.sawdust.tensor.alias.vector.primitive.ShortVector;

/**
 * The {@code ShortD1TensorShell} class is a wrapper which sets a 1-dimensional {@code ShortTensor} as a {@code Vector} (or,
 * more specifically, a {@code ShortVector}).
 *
 * @author crf <br/>
 *         Started: Sat Oct 25 21:35:12 2008
 *         Revised: Dec 14, 2009 12:35:23 PM
 */
public class ShortD1TensorShell extends D1TensorShell<Short> implements ShortVector {
    private final ShortTensor tensor;

    /**
     * Constructor specifying tensor to wrap. The tensor must be 1-dimensional or an exception will be thrown.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @throws IllegalArgumentException if {@code tensor} is not 1 dimension in size.
     */
    public ShortD1TensorShell(ShortTensor tensor) {
        super(tensor);
        this.tensor = tensor;
    }

    /**
     * {@inheritDoc}
     *
     * This method just calls {@code ShortTensor.getCell(index)}, 
     * and should be overridden if any efficiencies over that method can be made.
     */
    public short getCell(int index) {
        return tensor.getCell(index);
    }

    public short getCell(int ... indices) {
        if (indices.length != 1)
            throw new IllegalArgumentException("ShortD1Tensor is 1 dimension in size, getValue passed with " + indices.length + " indices.");
        return getCell(indices[0]);
    }

    public Short getValue(int ... indices) {
        return getCell(indices);
    }

    public Short getValue(int index) {
        return getCell(index);
    }

    /**
     * {@inheritDoc}
     *
     * This method just calls {@code ShortTensor.setCell(short,index)}, 
     * and should be overridden if any efficiencies over that method can be made.
     */
    public void setCell(short value, int index) {
        tensor.setCell(value,index);
    }

    public void setCell(short value, int ... indices) {
        if (indices.length != 1)
            throw new IllegalArgumentException("ShortD1Tensor is 1 dimension in size, getValue passed with " + indices.length + " indices.");
        setCell(value,indices[0]);
    }

    public void setValue(Short value, int ... indices) {
        setCell(value,indices);
    }

    public void setValue(Short value, int index) {
        setCell(value,index);
    }
    
    public ShortTypeSafeArray getTensorValues(Class<Short> type) {
        return getTensorValues();
    }

    public ShortTypeSafeArray getTensorValues() {
       @SuppressWarnings("unchecked") //getType requirements in tensor make this ok
        ShortTypeSafeArray array = TypeSafeArrayFactory.shortTypeSafeArray(getDimensions());
        for (int[] index : IterableAbacus.getIterableAbacus(getDimensions()))
            array.set(getCell(index),index);
        return array;
    }

    public void setTensorValues(ShortTypeSafeArray valuesArray) {
        TensorImplUtil.setTensorValues(this,valuesArray);
    }

    public <I> IdShortTensor<I> getReferenceTensor(Index<I> index) {
        return (IdShortTensor<I>) super.getReferenceTensor(index);
    }

    protected ShortTensor getComposedTensor(Index<?> index) {
        return TensorImplUtil.getComposedTensor(this,index); 
    }
}