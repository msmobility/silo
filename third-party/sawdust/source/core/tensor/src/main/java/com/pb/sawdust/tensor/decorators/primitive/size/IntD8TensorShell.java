package com.pb.sawdust.tensor.decorators.primitive.size;

import com.pb.sawdust.tensor.decorators.size.D8TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.IntTensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.decorators.id.primitive.IdIntTensor;
import com.pb.sawdust.util.abacus.IterableAbacus;
import com.pb.sawdust.util.array.IntTypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArrayFactory;


/**
 * The {@code IntD8TensorShell} class is a wrapper which sets a 8-dimensional {@code IntTensor} as a {@code D8Tensor} (or,
 * more specifically, a {@code IntD8Tensor}).
 *
 * @author crf <br/>
 *         Started: Sat Oct 25 21:35:12 2008
 *         Revised: Dec 14, 2009 12:35:32 PM
 */
public class IntD8TensorShell extends D8TensorShell<Integer> implements IntD8Tensor {
    private final IntTensor tensor;

    /**
     * Constructor specifying tensor to wrap. The tensor must be 8-dimensional or an exception will be thrown.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @throws IllegalArgumentException if {@code tensor} is not 8 dimension in size.
     */
    public IntD8TensorShell(IntTensor tensor) {
        super(tensor);
        this.tensor = tensor;
    }

    /**
     * {@inheritDoc}
     *
     * This method just calls {@code IntTensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index)}, 
     * and should be overridden if any efficiencies over that method can be made.
     */
    public int getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
        return tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
    }

    public int getCell(int ... indices) {
        if (indices.length != 8)
            throw new IllegalArgumentException("IntD8Tensor is 8 dimension in size, getValue passed with " + indices.length + " indices.");
        return getCell(indices[0],indices[1],indices[2],indices[3],indices[4],indices[5],indices[6],indices[7]);
    }

    public Integer getValue(int ... indices) {
        return getCell(indices);
    }

    public Integer getValue(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
        return getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
    }

    /**
     * {@inheritDoc}
     *
     * This method just calls {@code IntTensor.setCell(int,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index)}, 
     * and should be overridden if any efficiencies over that method can be made.
     */
    public void setCell(int value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
        tensor.setCell(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
    }

    public void setCell(int value, int ... indices) {
        if (indices.length != 8)
            throw new IllegalArgumentException("IntD8Tensor is 8 dimension in size, getValue passed with " + indices.length + " indices.");
        setCell(value,indices[0],indices[1],indices[2],indices[3],indices[4],indices[5],indices[6],indices[7]);
    }

    public void setValue(Integer value, int ... indices) {
        setCell(value,indices);
    }

    public void setValue(Integer value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
        setCell(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
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