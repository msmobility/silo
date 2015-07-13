package com.pb.sawdust.tensor.decorators.size;

import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.ComposableTensor;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.abacus.IterableAbacus;
import com.pb.sawdust.util.array.TypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArrayFactory;

/**
 * The {@code AbstractDnTensorShell} provides a skeletal implementation for the tensor size interfaces. It wraps a tensor whose
 * dimension count is known (expected to be "n") and implements the required tensor methods. The abstract methods in this class
 * are those which are size (<i>i.e.</i> implementation) specific.
 *
 * @author crf <br/>
 *         Started: Oct 21, 2008 11:23:50 PM
 *         Revised: Dec 14, 2009 12:35:34 PM
 */
public abstract class AbstractDnTensorShell<T> extends ComposableTensor<T> {
    final Tensor<T> tensor;
    private final int size;

    /**
     * Get the size (the "n") of this tensor. This method will be used to check that the tensor meets the size 
     * requirements of this class, so this method should not refer to the wrapped tensor.
     * 
     * @return the tensor size represented by this class.
     */
    protected abstract int getSize();

    /**
     * Constructor specifying the tensor to wrap.
     *
     * @param tensor
     *        The(n-dimensional) tensor to wrap.
     * 
     * @throws IllegalArgumentException if the {@code tensor.size()} != getSize()}.
     */
    public AbstractDnTensorShell(Tensor<T> tensor) {
        size = getSize();
        if (tensor.size() != size)
            throw new IllegalArgumentException("D" + size + "Tensor must be " + size + " dimension in size: " + tensor.size());
        this.tensor = tensor;
    }

    public int[] getDimensions() {
        return tensor.getDimensions();
    }
    
    public int size() {
        return size;
    }

    public int size(int dimension) {
        return tensor.size(dimension);
    }

    public JavaType getType() {
        return tensor.getType();
    }

    public TypeSafeArray<T> getTensorValues(Class<T> type) {
       @SuppressWarnings("unchecked") //getType requirements in tensor make this ok
        TypeSafeArray<T> array = (getType() == JavaType.OBJECT) ? TypeSafeArrayFactory.typeSafeArray(type,getDimensions()) : (TypeSafeArray<T>) TypeSafeArrayFactory.typeSafeArray(getType(),getDimensions());
        for (int[] index : IterableAbacus.getIterableAbacus(getDimensions()))
            array.setValue(getValue(index),index);
        return array;
    }

    public void setTensorValues(TypeSafeArray<? extends T> valuesArray) {
        TensorImplUtil.checkSetTensorParameters(this,valuesArray);
        for (int[] index : IterableAbacus.getIterableAbacus(getDimensions())) 
            setValue(valuesArray.getValue(index),index);
    }

    public void setTensorValues(Tensor<? extends T> tensor) {
        TensorImplUtil.checkSetTensorParameters(this,tensor);
        for (int[] index : IterableAbacus.getIterableAbacus(getDimensions()))
            setValue(tensor.getValue(index),index);
    }

    public T getValue(int index) {
        return tensor.getValue(index);
    }

    public Index<?> getIndex() {
        return tensor.getIndex();
    }
}
