package com.pb.sawdust.tensor.decorators.primitive.size;

import com.pb.sawdust.tensor.decorators.size.D6TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.ByteTensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.decorators.id.primitive.IdByteTensor;
import com.pb.sawdust.util.abacus.IterableAbacus;
import com.pb.sawdust.util.array.ByteTypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArrayFactory;


/**
 * The {@code ByteD6TensorShell} class is a wrapper which sets a 6-dimensional {@code ByteTensor} as a {@code D6Tensor} (or,
 * more specifically, a {@code ByteD6Tensor}).
 *
 * @author crf <br/>
 *         Started: Sat Oct 25 21:35:12 2008
 *         Revised: Dec 14, 2009 12:35:29 PM
 */
public class ByteD6TensorShell extends D6TensorShell<Byte> implements ByteD6Tensor {
    private final ByteTensor tensor;

    /**
     * Constructor specifying tensor to wrap. The tensor must be 6-dimensional or an exception will be thrown.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @throws IllegalArgumentException if {@code tensor} is not 6 dimension in size.
     */
    public ByteD6TensorShell(ByteTensor tensor) {
        super(tensor);
        this.tensor = tensor;
    }

    /**
     * {@inheritDoc}
     *
     * This method just calls {@code ByteTensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index)}, 
     * and should be overridden if any efficiencies over that method can be made.
     */
    public byte getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
        return tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index);
    }

    public byte getCell(int ... indices) {
        if (indices.length != 6)
            throw new IllegalArgumentException("ByteD6Tensor is 6 dimension in size, getValue passed with " + indices.length + " indices.");
        return getCell(indices[0],indices[1],indices[2],indices[3],indices[4],indices[5]);
    }

    public Byte getValue(int ... indices) {
        return getCell(indices);
    }

    public Byte getValue(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
        return getCell(d0index,d1index,d2index,d3index,d4index,d5index);
    }

    /**
     * {@inheritDoc}
     *
     * This method just calls {@code ByteTensor.setCell(byte,d0index,d1index,d2index,d3index,d4index,d5index)}, 
     * and should be overridden if any efficiencies over that method can be made.
     */
    public void setCell(byte value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
        tensor.setCell(value,d0index,d1index,d2index,d3index,d4index,d5index);
    }

    public void setCell(byte value, int ... indices) {
        if (indices.length != 6)
            throw new IllegalArgumentException("ByteD6Tensor is 6 dimension in size, getValue passed with " + indices.length + " indices.");
        setCell(value,indices[0],indices[1],indices[2],indices[3],indices[4],indices[5]);
    }

    public void setValue(Byte value, int ... indices) {
        setCell(value,indices);
    }

    public void setValue(Byte value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
        setCell(value,d0index,d1index,d2index,d3index,d4index,d5index);
    }
    
    public ByteTypeSafeArray getTensorValues(Class<Byte> type) {
        return getTensorValues();
    }

    public ByteTypeSafeArray getTensorValues() {
       @SuppressWarnings("unchecked") //getType requirements in tensor make this ok
        ByteTypeSafeArray array = TypeSafeArrayFactory.byteTypeSafeArray(getDimensions());
        for (int[] index : IterableAbacus.getIterableAbacus(getDimensions()))
            array.set(getCell(index),index);
        return array;
    }

    public void setTensorValues(ByteTypeSafeArray valuesArray) {
        TensorImplUtil.setTensorValues(this,valuesArray);
    }

    public <I> IdByteTensor<I> getReferenceTensor(Index<I> index) {
        return (IdByteTensor<I>) super.getReferenceTensor(index);
    }

    protected ByteTensor getComposedTensor(Index<?> index) {
        return TensorImplUtil.getComposedTensor(this,index); 
    }
}