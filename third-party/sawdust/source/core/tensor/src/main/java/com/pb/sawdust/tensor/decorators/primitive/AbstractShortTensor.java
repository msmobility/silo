package com.pb.sawdust.tensor.decorators.primitive;

import com.pb.sawdust.tensor.AbstractTensor;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.abacus.IterableAbacus;
import com.pb.sawdust.util.array.ShortTypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArrayFactory;
import com.pb.sawdust.tensor.decorators.id.primitive.IdShortTensor;

/**
 * The {@code AbstractShortTensor} class provides a skeletal implementation of the {@code ShortTensor} interface.
 * Extending concrete classes need only implement the {@code getCell(int[])} and {@code setCell(short,int[])} methods.
 *
 * @author crf <br/>
 *         Started: Oct 18, 2008 8:05:31 PM
 *         Revised: Dec 14, 2009 12:35:33 PM
 */
public abstract class AbstractShortTensor extends AbstractTensor<Short> implements ShortTensor {

    /**
     * Constructor specifying the size of the tensor.
     *
     * @param dimensions
     *        The size each dimension of this tensor.
     *
     * @throws IllegalArgumentException if any dimension's size is less than one.
    */
    public AbstractShortTensor(int ... dimensions) {
        super(dimensions);
    }

    /**
     * Constructor specifying the index to use for the tensor.
     *
     * @param index
     *        The index to use for this tensor.
    */
    protected AbstractShortTensor(Index<?> index) {
        super(index);
    }

    public JavaType getType() {
        return JavaType.SHORT;
    }

    public Short getValue(int ... indices) {
        return getCell(indices);
    }

    public void setValue(Short value, int ... indices) {
        setCell(value,indices);
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
