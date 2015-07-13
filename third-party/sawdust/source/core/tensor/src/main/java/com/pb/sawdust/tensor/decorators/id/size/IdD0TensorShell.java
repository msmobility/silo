package com.pb.sawdust.tensor.decorators.id.size;

import com.pb.sawdust.tensor.alias.scalar.Scalar;
import com.pb.sawdust.tensor.alias.scalar.id.IdScalar;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.decorators.size.AbstractD0Tensor;
import com.pb.sawdust.tensor.decorators.size.D0Tensor;
import com.pb.sawdust.tensor.decorators.id.IdTensor;
import com.pb.sawdust.util.array.TypeSafeArray;
import com.pb.sawdust.util.JavaType;

import java.util.List;

/**
 * The {@code IdD0TensorShell} class is a wrapper which sets a {@code D0Tensor} as an {@code IdTensor<I>} (or, more
 * spceifically an {@code IdD0Tensor<I>}).
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 11:39:14 PM
 */
public class IdD0TensorShell<T,I> extends AbstractD0Tensor<T> implements IdScalar<T,I> {
    private final D0Tensor<T> scalar;
    private final Index<I> index;

    /**
     * Constructor specifying the scalar to wrap.
     *
     * @param scalar
     *        The scalar to wrap.
     *
     * @param index
     *        The index to use for this scalar.
     */
    public IdD0TensorShell(D0Tensor<T> scalar, Index<I> index) {
        super(index);
        this.scalar = scalar;
        this.index = index;
    }

    /**
     * Constructor specifying the scalar to wrap.
     *
     * @param scalar
     *        The scalar to wrap.
     */
    @SuppressWarnings("unchecked") //Index id type doesn't matter for scalars, so we can cast such an index as needed
    public IdD0TensorShell(D0Tensor<T> scalar) {
        this(scalar,(Index<I>) scalar.getIndex());
    }

    private IdD0TensorShell(Scalar<T> scalar, boolean ignored) {
        this(scalar);
    }

    /**
     * Constructor specifying the scalar to wrap.
     *
     * @param scalar
     *        The scalar to wrap.
     *
     * @param ids
     *        The ids to use for this scalar.
     *
     * @throws IllegalArgumentException if {@code ids.size() > 0}.
     */
    @SuppressWarnings("unchecked") //Index id type doesn't matter for scalars, so we can cast such an index as needed
    public IdD0TensorShell(Scalar<T> scalar, List<List<I>> ids) {
        this(scalar,checkIds(ids.size() == 0));
    }

    /**
     * Constructor specifying the scalar to wrap.
     *
     * @param scalar
     *        The scalar to wrap.
     *
     * @param ids
     *        The ids to use for this scalar.
     *
     * @throws IllegalArgumentException if {@code ids.length > 0}.
     */
    @SuppressWarnings("unchecked") //Index id type doesn't matter for scalars, so we can cast such an index as needed
    public IdD0TensorShell(Scalar<T> scalar, I[] ... ids) {
        this(scalar,checkIds(ids.length == 0));
    }

    private static boolean checkIds(boolean ok) {
        if (!ok)
            throw new IllegalArgumentException("Scalar is dimensionless so id list must be empty.");
        return ok;
    }

    @Override
    public T getValue() {
        return scalar.getValue();
    }

    @Override
    public void setValue(T value) {
        scalar.setValue(value);
    }

    @Override
    public JavaType getType() {
        return scalar.getType();
    }

    @Override
    public T getValue(int ... indices) {
        return scalar.getValue(indices);
    }

    @Override
    public void setValue(T value, int ... indices) {
        scalar.setValue(value,indices);
    }

    @Override
    public TypeSafeArray<T> getTensorValues(Class<T> type) {
        return scalar.getTensorValues(type);
    }

    @Override
    public void setTensorValues(TypeSafeArray<? extends T> typeSafeArray) {
        scalar.setTensorValues(typeSafeArray);
    }

    @Override
    public void setTensorValues(Tensor<? extends T> tensor) {
        scalar.setTensorValues(tensor);
    }

    @Override
    public <I> IdTensor<T,I> getReferenceTensor(Index<I> index) {
        return scalar.getReferenceTensor(index);
    }

    @Override
    public Index<I> getIndex() {
        return index;
    }

    @Override
    @SuppressWarnings({"unchecked", "varargs"})
    public T getValueById(I ... ids) {
        if (ids.length != 0)
            throw new IllegalArgumentException("Scalar has no dimensionality (referred to with " + ids.length + " dimensions).");
        return getValue();
    }

    @Override
    @SuppressWarnings({"unchecked", "varargs"})
    public void setValueById(T value, I... ids) {
        if (ids.length != 0)
            throw new IllegalArgumentException("Scalar has no dimensionality (referred to with " + ids.length + " dimensions).");
        setValue(value);
    }

    @Override
    public T getValueById() {
        return getValue();
    }

    @Override
    public void setValueById(T value) {
        setValue(value);
    }
}
