package com.pb.sawdust.tensor.decorators.primitive;

import com.pb.sawdust.tensor.AbstractTensor;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.abacus.IterableAbacus;
import com.pb.sawdust.util.array.DoubleTypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArrayFactory;
import com.pb.sawdust.tensor.decorators.id.primitive.IdDoubleTensor;

/**
 * The {@code AbstractDoubleTensor} class provides a skeletal implementation of the {@code DoubleTensor} interface.
 * Extending concrete classes need only implement the {@code getCell(int[])} and {@code setCell(double,int[])} methods.
 *
 * @author crf <br/>
 *         Started: Oct 18, 2008 8:05:31 PM
 *         Revised: Dec 14, 2009 12:35:34 PM
 */
public abstract class AbstractDoubleTensor extends AbstractTensor<Double> implements DoubleTensor {

    /**
     * Constructor specifying the size of the tensor.
     *
     * @param dimensions
     *        The size each dimension of this tensor.
     *
     * @throws IllegalArgumentException if any dimension's size is less than one.
    */
    public AbstractDoubleTensor(int ... dimensions) {
        super(dimensions);
    }

    /**
     * Constructor specifying the index to use for the tensor.
     *
     * @param index
     *        The index to use for this tensor.
    */
    protected AbstractDoubleTensor(Index<?> index) {
        super(index);
    }

    public JavaType getType() {
        return JavaType.DOUBLE;
    }

    public Double getValue(int ... indices) {
        return getCell(indices);
    }

    public void setValue(Double value, int ... indices) {
        setCell(value,indices);
    }
    
    public DoubleTypeSafeArray getTensorValues(Class<Double> type) {
        return getTensorValues();
    }

    public DoubleTypeSafeArray getTensorValues() {
       @SuppressWarnings("unchecked") //getType requirements in tensor make this ok
        DoubleTypeSafeArray array = TypeSafeArrayFactory.doubleTypeSafeArray(getDimensions());
        for (int[] index : IterableAbacus.getIterableAbacus(getDimensions()))
            array.set(getCell(index),index);
        return array;
    }

    public void setTensorValues(DoubleTypeSafeArray valuesArray) {
        TensorImplUtil.setTensorValues(this,valuesArray);
    }

    public <I> IdDoubleTensor<I> getReferenceTensor(Index<I> index) {
        return (IdDoubleTensor<I>) super.getReferenceTensor(index);
    }

    protected DoubleTensor getComposedTensor(Index<?> index) {
        return TensorImplUtil.getComposedTensor(this,index); 
    }
}
