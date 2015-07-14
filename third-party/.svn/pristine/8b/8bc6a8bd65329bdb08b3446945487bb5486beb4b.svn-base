package com.pb.sawdust.tensor.decorators.primitive.size;

import com.pb.sawdust.tensor.decorators.size.D9TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.BooleanTensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.decorators.id.primitive.IdBooleanTensor;
import com.pb.sawdust.util.abacus.IterableAbacus;
import com.pb.sawdust.util.array.BooleanTypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArrayFactory;


/**
 * The {@code BooleanD9TensorShell} class is a wrapper which sets a 9-dimensional {@code BooleanTensor} as a {@code D9Tensor} (or,
 * more specifically, a {@code BooleanD9Tensor}).
 *
 * @author crf <br/>
 *         Started: Sat Oct 25 21:35:12 2008
 *         Revised: Dec 14, 2009 12:35:34 PM
 */
public class BooleanD9TensorShell extends D9TensorShell<Boolean> implements BooleanD9Tensor {
    private final BooleanTensor tensor;

    /**
     * Constructor specifying tensor to wrap. The tensor must be 9-dimensional or an exception will be thrown.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @throws IllegalArgumentException if {@code tensor} is not 9 dimension in size.
     */
    public BooleanD9TensorShell(BooleanTensor tensor) {
        super(tensor);
        this.tensor = tensor;
    }

    /**
     * {@inheritDoc}
     *
     * This method just calls {@code BooleanTensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index)}, 
     * and should be overridden if any efficiencies over that method can be made.
     */
    public boolean getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
        return tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
    }

    public boolean getCell(int ... indices) {
        if (indices.length != 9)
            throw new IllegalArgumentException("BooleanD9Tensor is 9 dimension in size, getValue passed with " + indices.length + " indices.");
        return getCell(indices[0],indices[1],indices[2],indices[3],indices[4],indices[5],indices[6],indices[7],indices[8]);
    }

    public Boolean getValue(int ... indices) {
        return getCell(indices);
    }

    public Boolean getValue(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
        return getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
    }

    /**
     * {@inheritDoc}
     *
     * This method just calls {@code BooleanTensor.setCell(boolean,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index)}, 
     * and should be overridden if any efficiencies over that method can be made.
     */
    public void setCell(boolean value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
        tensor.setCell(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
    }

    public void setCell(boolean value, int ... indices) {
        if (indices.length != 9)
            throw new IllegalArgumentException("BooleanD9Tensor is 9 dimension in size, getValue passed with " + indices.length + " indices.");
        setCell(value,indices[0],indices[1],indices[2],indices[3],indices[4],indices[5],indices[6],indices[7],indices[8]);
    }

    public void setValue(Boolean value, int ... indices) {
        setCell(value,indices);
    }

    public void setValue(Boolean value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
        setCell(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
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