package com.pb.sawdust.tensor.decorators.primitive.size;

import com.pb.sawdust.tensor.decorators.size.D3TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.ShortTensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.decorators.id.primitive.IdShortTensor;
import com.pb.sawdust.util.abacus.IterableAbacus;
import com.pb.sawdust.util.array.ShortTypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArrayFactory;


/**
 * The {@code ShortD3TensorShell} class is a wrapper which sets a 3-dimensional {@code ShortTensor} as a {@code D3Tensor} (or,
 * more specifically, a {@code ShortD3Tensor}).
 *
 * @author crf <br/>
 *         Started: Sat Oct 25 21:35:12 2008
 *         Revised: Dec 14, 2009 12:35:26 PM
 */
public class ShortD3TensorShell extends D3TensorShell<Short> implements ShortD3Tensor {
    private final ShortTensor tensor;

    /**
     * Constructor specifying tensor to wrap. The tensor must be 3-dimensional or an exception will be thrown.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @throws IllegalArgumentException if {@code tensor} is not 3 dimension in size.
     */
    public ShortD3TensorShell(ShortTensor tensor) {
        super(tensor);
        this.tensor = tensor;
    }

    /**
     * {@inheritDoc}
     *
     * This method just calls {@code ShortTensor.getCell(d0index,d1index,d2index)}, 
     * and should be overridden if any efficiencies over that method can be made.
     */
    public short getCell(int d0index, int d1index, int d2index) {
        return tensor.getCell(d0index,d1index,d2index);
    }

    public short getCell(int ... indices) {
        if (indices.length != 3)
            throw new IllegalArgumentException("ShortD3Tensor is 3 dimension in size, getValue passed with " + indices.length + " indices.");
        return getCell(indices[0],indices[1],indices[2]);
    }

    public Short getValue(int ... indices) {
        return getCell(indices);
    }

    public Short getValue(int d0index, int d1index, int d2index) {
        return getCell(d0index,d1index,d2index);
    }

    /**
     * {@inheritDoc}
     *
     * This method just calls {@code ShortTensor.setCell(short,d0index,d1index,d2index)}, 
     * and should be overridden if any efficiencies over that method can be made.
     */
    public void setCell(short value, int d0index, int d1index, int d2index) {
        tensor.setCell(value,d0index,d1index,d2index);
    }

    public void setCell(short value, int ... indices) {
        if (indices.length != 3)
            throw new IllegalArgumentException("ShortD3Tensor is 3 dimension in size, getValue passed with " + indices.length + " indices.");
        setCell(value,indices[0],indices[1],indices[2]);
    }

    public void setValue(Short value, int ... indices) {
        setCell(value,indices);
    }

    public void setValue(Short value, int d0index, int d1index, int d2index) {
        setCell(value,d0index,d1index,d2index);
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