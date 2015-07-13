package com.pb.sawdust.tensor.decorators.primitive;

import com.pb.sawdust.tensor.AbstractTensor;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.abacus.IterableAbacus;
import com.pb.sawdust.util.array.LongTypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArrayFactory;
import com.pb.sawdust.tensor.decorators.id.primitive.IdLongTensor;

/**
 * The {@code AbstractLongTensor} class provides a skeletal implementation of the {@code LongTensor} interface.
 * Extending concrete classes need only implement the {@code getCell(int[])} and {@code setCell(long,int[])} methods.
 *
 * @author crf <br/>
 *         Started: Oct 18, 2008 8:05:31 PM
 *         Revised: Dec 14, 2009 12:35:34 PM
 */
public abstract class AbstractLongTensor extends AbstractTensor<Long> implements LongTensor {

    /**
     * Constructor specifying the size of the tensor.
     *
     * @param dimensions
     *        The size each dimension of this tensor.
     *
     * @throws IllegalArgumentException if any dimension's size is less than one.
    */
    public AbstractLongTensor(int ... dimensions) {
        super(dimensions);
    }

    /**
     * Constructor specifying the index to use for the tensor.
     *
     * @param index
     *        The index to use for this tensor.
    */
    protected AbstractLongTensor(Index<?> index) {
        super(index);
    }

    public JavaType getType() {
        return JavaType.LONG;
    }

    public Long getValue(int ... indices) {
        return getCell(indices);
    }

    public void setValue(Long value, int ... indices) {
        setCell(value,indices);
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
