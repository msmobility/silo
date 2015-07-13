package com.pb.sawdust.tensor.decorators.size;

import com.pb.sawdust.tensor.alias.scalar.Scalar;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.ScalarIndex;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.util.ContainsMetadataImpl;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.array.TypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArrayFactory;
import com.pb.sawdust.util.array.ArrayUtil;

import java.util.Iterator;

/**
 * @author crf <br/>
 *         Started: Jun 22, 2009 4:04:59 PM
 */
public abstract class AbstractD0Tensor<T> extends ContainsMetadataImpl<String> implements Scalar<T> {
    protected final Index<?> index;

    protected AbstractD0Tensor(Index<?> index) {
        if (index.size() != 0)
            throw new IllegalArgumentException("Scalar must use an index with size of zero.");
        this.index = index;
    }

    protected AbstractD0Tensor() {
        this(ScalarIndex.getScalarIndex());
    }

    @Override
    public JavaType getType() {
        return JavaType.OBJECT;
    }

    @Override
    public T getValue(int ... indices) {
        if (indices.length != 0)
            throw new IllegalArgumentException("Scalar has no dimensionality (referred to with " + indices.length + " dimensions).");
        return getValue();
    }

    @Override
    public void setValue(T value, int... indices) {
        if (indices.length != 0)
            throw new IllegalArgumentException("Scalar has no dimensionality (referred to with " + indices.length + " dimensions).");
        setValue(value);
    }

    public TypeSafeArray<T> getTensorValues(Class<T> type) {
        @SuppressWarnings("unchecked") //getType rquirements in tensor make this ok
        TypeSafeArray<T> array = (getType() == JavaType.OBJECT) ? TypeSafeArrayFactory.typeSafeArray(type,1) : (TypeSafeArray<T>) TypeSafeArrayFactory.typeSafeArray(getType(),1);
        array.setValue(getValue(),0);
        return array;
    }

    public void setTensorValues(TypeSafeArray<? extends T> valuesArray) {
        if (!ArrayUtil.isOfDimension(valuesArray.getArray(),new int[] {1}))
            throw new IllegalArgumentException("TypeSafeArray for setting a scalar must contain only one value.");
        setValue(valuesArray.getValue(0));
    }

    public void setTensorValues(Tensor<? extends T> tensor) {
        if (tensor.size() != 0)
            throw new IllegalArgumentException("To set a scalar with a tensor, the tensor must also be a scalar.");
        setValue(tensor.getValue());
    }

    @Override
    public Index<?> getIndex() {
        return index;
    }

    @Override
    public int[] getDimensions() {
        return index.getDimensions();
    }

    @Override
    public int size(int dimension) {
        throw new IllegalArgumentException("Dimension out of bounds: " + dimension);
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Iterator<Tensor<T>> iterator() {
        return new Iterator<Tensor<T>>() {
            private boolean hasNext = true;

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public Tensor<T> next() {
                hasNext = false;
                return AbstractD0Tensor.this;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
