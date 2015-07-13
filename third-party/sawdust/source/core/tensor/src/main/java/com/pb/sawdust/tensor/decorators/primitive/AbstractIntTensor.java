package com.pb.sawdust.tensor.decorators.primitive;

import com.pb.sawdust.tensor.AbstractTensor;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.abacus.IterableAbacus;
import com.pb.sawdust.util.array.IntTypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArrayFactory;
import com.pb.sawdust.tensor.decorators.id.primitive.IdIntTensor;

/**
 * The {@code AbstractIntTensor} class provides a skeletal implementation of the {@code IntTensor} interface.
 * Extending concrete classes need only implement the {@code getCell(int[])} and {@code setCell(int,int[])} methods.
 *
 * @author crf <br/>
 *         Started: Oct 18, 2008 8:05:31 PM
 *         Revised: Dec 14, 2009 12:35:34 PM
 */
public abstract class AbstractIntTensor extends AbstractTensor<Integer> implements IntTensor {

    /**
     * Constructor specifying the size of the tensor.
     *
     * @param dimensions
     *        The size each dimension of this tensor.
     *
     * @throws IllegalArgumentException if any dimension's size is less than one.
    */
    public AbstractIntTensor(int ... dimensions) {
        super(dimensions);
    }

    /**
     * Constructor specifying the index to use for the tensor.
     *
     * @param index
     *        The index to use for this tensor.
    */
    protected AbstractIntTensor(Index<?> index) {
        super(index);
    }

    public JavaType getType() {
        return JavaType.INT;
    }

    public Integer getValue(int ... indices) {
        return getCell(indices);
    }

    public void setValue(Integer value, int ... indices) {
        setCell(value,indices);
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
