package com.pb.sawdust.tensor;

import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.StandardIndex;
import com.pb.sawdust.util.array.TypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArrayFactory;
import com.pb.sawdust.util.abacus.IterableAbacus;
import com.pb.sawdust.util.JavaType;

/**
 * The {@code AbstractTensor} class provides a skeletal implementation of the {@code Tensor} interface. It requires only
 * that an index be specified, and that the {@code getType}, {@code getValue(int[])}, and {@code setValue(T,int[])}
 * methods be implemented. However, because all other {@code Tensor} methods are implemented with respect to these methods,
 * certain innefficiencies may be present in some of the default implementations provided in this class. For example,
 * the {@code setTensorValues(Tensor)} method cycles through the input tensor and sets the cells one by one. Depending
 * on the tensor implementation, a more sophisticated algorithm might improve the efficiency of the method drastically.
 * 
 * @author crf <br/>
 *         Started: Jan 11, 2009 4:17:20 PM
 */
public abstract class AbstractTensor<T> extends ComposableTensor<T> {
    /**
     * The index used to reference locations and dimensional information about this tensor.
     */
    protected final Index<?> index;

    /**
     * Cosntructor specifying the tensor index.
     *
     * @param index
     *        The index to be used for this tensor.
     */
    public AbstractTensor(Index<?> index) {
        this.index = index;
    }

    /**
     * Constructor specifying the tensor dimensions. A {@code StandardIndex} will be built from the dimensions to
     * use with this tensor.
     *
     * @param dimensions
     *        The dimensions of the tensor. Each dimension's value specifies the size (length) of the corresponding
     *        tensor dimension.
     *
     * @throws IllegalArgumentException if no dimensions are specified (<i>i.e.</i> if {@code dimensions.length == 0},
     *                                  or if any dimension value is less than 1.
     */
    public AbstractTensor(int ... dimensions) {
        this(new StandardIndex(dimensions));
    }

    public int[] getDimensions() {
        return index.getDimensions();
    }

    public int size(int dimension) {
        return index.size(dimension);
    }

    public int size() {
        return index.size();
    }

    public TypeSafeArray<T> getTensorValues(Class<T> type) {
        @SuppressWarnings("unchecked") //getType rquirements in tensor make this ok
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
    
    public Index<?> getIndex() {
        return index;
    }
}
