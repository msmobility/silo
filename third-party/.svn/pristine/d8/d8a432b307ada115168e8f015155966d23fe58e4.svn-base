package com.pb.sawdust.tensor.decorators.primitive.size;

import com.pb.sawdust.tensor.decorators.size.D2TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.LongTensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.decorators.id.primitive.IdLongTensor;
import com.pb.sawdust.util.abacus.IterableAbacus;
import com.pb.sawdust.util.array.LongTypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArrayFactory;
import com.pb.sawdust.tensor.alias.matrix.primitive.LongMatrix;

/**
 * The {@code LongD2TensorShell} class is a wrapper which sets a 2-dimensional {@code LongTensor} as a {@code Matrix} (or,
 * more specifically, a {@code LongMatrix}).
 *
 * @author crf <br/>
 *         Started: Sat Oct 25 21:35:12 2008
 *         Revised: Dec 14, 2009 12:35:25 PM
 */
public class LongD2TensorShell extends D2TensorShell<Long> implements LongMatrix {
    private final LongTensor tensor;

    /**
     * Constructor specifying tensor to wrap. The tensor must be 2-dimensional or an exception will be thrown.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @throws IllegalArgumentException if {@code tensor} is not 2 dimension in size.
     */
    public LongD2TensorShell(LongTensor tensor) {
        super(tensor);
        this.tensor = tensor;
    }

    /**
     * {@inheritDoc}
     *
     * This method just calls {@code LongTensor.getCell(d0index,d1index)}, 
     * and should be overridden if any efficiencies over that method can be made.
     */
    public long getCell(int d0index, int d1index) {
        return tensor.getCell(d0index,d1index);
    }

    public long getCell(int ... indices) {
        if (indices.length != 2)
            throw new IllegalArgumentException("LongD2Tensor is 2 dimension in size, getValue passed with " + indices.length + " indices.");
        return getCell(indices[0],indices[1]);
    }

    public Long getValue(int ... indices) {
        return getCell(indices);
    }

    public Long getValue(int d0index, int d1index) {
        return getCell(d0index,d1index);
    }

    /**
     * {@inheritDoc}
     *
     * This method just calls {@code LongTensor.setCell(long,d0index,d1index)}, 
     * and should be overridden if any efficiencies over that method can be made.
     */
    public void setCell(long value, int d0index, int d1index) {
        tensor.setCell(value,d0index,d1index);
    }

    public void setCell(long value, int ... indices) {
        if (indices.length != 2)
            throw new IllegalArgumentException("LongD2Tensor is 2 dimension in size, getValue passed with " + indices.length + " indices.");
        setCell(value,indices[0],indices[1]);
    }

    public void setValue(Long value, int ... indices) {
        setCell(value,indices);
    }

    public void setValue(Long value, int d0index, int d1index) {
        setCell(value,d0index,d1index);
    }
    
    public LongTypeSafeArray getTensorValues(Class<Long> type) {
        return getTensorValues();
    }

    public LongTypeSafeArray getTensorValues() {
       @SuppressWarnings("unchecked") //getType requirements in tensor make this ok
        LongTypeSafeArray array = TypeSafeArrayFactory.longTypeSafeArray(getDimensions());
        for (int[] index : IterableAbacus.getIterableAbacus(getDimensions()))
            array.set(getCell(index),index);
        return array;
    }

    public void setTensorValues(LongTypeSafeArray valuesArray) {
        TensorImplUtil.setTensorValues(this,valuesArray);
    }

    public <I> IdLongTensor<I> getReferenceTensor(Index<I> index) {
        return (IdLongTensor<I>) super.getReferenceTensor(index);
    }

    protected LongTensor getComposedTensor(Index<?> index) {
        return TensorImplUtil.getComposedTensor(this,index); 
    }
}