package com.pb.sawdust.tensor.decorators.primitive;

import com.pb.sawdust.tensor.AbstractTensor;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.abacus.IterableAbacus;
import com.pb.sawdust.util.array.FloatTypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArrayFactory;
import com.pb.sawdust.tensor.decorators.id.primitive.IdFloatTensor;

/**
 * The {@code AbstractFloatTensor} class provides a skeletal implementation of the {@code FloatTensor} interface.
 * Extending concrete classes need only implement the {@code getCell(int[])} and {@code setCell(float,int[])} methods.
 *
 * @author crf <br/>
 *         Started: Oct 18, 2008 8:05:31 PM
 *         Revised: Dec 14, 2009 12:35:34 PM
 */
public abstract class AbstractFloatTensor extends AbstractTensor<Float> implements FloatTensor {

    /**
     * Constructor specifying the size of the tensor.
     *
     * @param dimensions
     *        The size each dimension of this tensor.
     *
     * @throws IllegalArgumentException if any dimension's size is less than one.
    */
    public AbstractFloatTensor(int ... dimensions) {
        super(dimensions);
    }

    /**
     * Constructor specifying the index to use for the tensor.
     *
     * @param index
     *        The index to use for this tensor.
    */
    protected AbstractFloatTensor(Index<?> index) {
        super(index);
    }

    public JavaType getType() {
        return JavaType.FLOAT;
    }

    public Float getValue(int ... indices) {
        return getCell(indices);
    }

    public void setValue(Float value, int ... indices) {
        setCell(value,indices);
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
