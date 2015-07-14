package com.pb.sawdust.tensor.decorators.id.primitive.size;

import com.pb.sawdust.tensor.alias.scalar.id.IdLongScalar;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.decorators.primitive.size.LongD0Tensor;
import com.pb.sawdust.tensor.decorators.size.AbstractD0Tensor;
import com.pb.sawdust.tensor.decorators.id.primitive.IdLongTensor;
import com.pb.sawdust.util.array.LongTypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArray;
import com.pb.sawdust.util.JavaType;

import java.util.List;

/**
 * The {@code IdLongD0TensorShell} class is a wrapper which sets a {@code LongD0Tensor} as an {@code IdTensor<I>} (or, more
 * spceifically an {@code IdLongD0Tensor}).
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 11:39:14 PM
 */
public class IdLongD0TensorShell<I> extends AbstractD0Tensor<Long> implements IdLongScalar<I> {
    private final LongD0Tensor scalar;
    private final Index<I> index;

    private IdLongD0TensorShell(LongD0Tensor scalar, Index<I> index) {
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
    public IdLongD0TensorShell(LongD0Tensor scalar) {
        this(scalar,(Index<I>) scalar.getIndex());
    }

    private IdLongD0TensorShell(LongD0Tensor scalar, boolean ignored) {
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
    public IdLongD0TensorShell(LongD0Tensor scalar, List<List<I>> ids) {
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
    @SafeVarargs
    @SuppressWarnings({"unchecked","varargs"})   //Index id type doesn't matter for scalars, so we can cast such an index as needed
    public IdLongD0TensorShell(LongD0Tensor scalar, I[] ... ids) {
        this(scalar,checkIds(ids.length == 0));
    }

    private static boolean checkIds(boolean ok) {
        if (!ok)
            throw new IllegalArgumentException("Scalar is dimensionless so id list must be empty.");
        return ok;
    }

    @Override
    public long getCellById() {
        return getCell();
    }

    @Override
    public void setCellById(long value) {
        setCell(value);
    }

    @Override
    public long getCell() {
        return scalar.getCell();
    }

    @Override
    public void setCell(long value) {
        scalar.setCell(value);
    }

    @Override
    public long getCell(int ... indices) {
        return scalar.getCell(indices);
    }

    @Override
    public void setCell(long value, int ... indices) {
        scalar.setCell(value,indices);
    }

    @Override
    public Long getValue() {
        return scalar.getValue();
    }

    @Override
    public void setValue(Long value) {
        scalar.setValue(value);
    }

    @Override
    public JavaType getType() {
        return scalar.getType();
    }

    @Override
    public Long getValue(int ... indices) {
        return scalar.getValue(indices);
    }

    @Override
    public void setValue(Long value, int ... indices) {
        scalar.setValue(value,indices);
    }

    @Override
    public void setTensorValues(LongTypeSafeArray valuesArray) {
        scalar.setTensorValues(valuesArray);
    }

    @Override
    public LongTypeSafeArray getTensorValues(Class<Long> type) {
        return scalar.getTensorValues(type);
    }

    @Override
    public void setTensorValues(TypeSafeArray<? extends Long> typeSafeArray) {
        scalar.setTensorValues(typeSafeArray);
    }

    @Override
    public void setTensorValues(Tensor<? extends Long> tensor) {
        scalar.setTensorValues(tensor);
    }

    @Override
    public LongTypeSafeArray getTensorValues() {
        return scalar.getTensorValues();
    }

    @Override
    public <J> IdLongTensor<J> getReferenceTensor(Index<J> index) {
        return scalar.getReferenceTensor(index);
    }

    @Override
    public Index<I> getIndex() {
        return index;
    }

    @Override
    @SuppressWarnings({"unchecked", "varargs"})
    public Long getValueById(I ... ids) {
        if (ids.length != 0)
            throw new IllegalArgumentException("Scalar has no dimensionality (referred to with " + ids.length + " dimensions).");
        return getValue();
    }

    @Override
    @SuppressWarnings({"unchecked", "varargs"})
    public void setValueById(Long value, I... ids) {
        if (ids.length != 0)
            throw new IllegalArgumentException("Scalar has no dimensionality (referred to with " + ids.length + " dimensions).");
        setValue(value);
    }

    @Override
    @SuppressWarnings({"unchecked", "varargs"})
    public long getCellById(I... ids) {
        if (ids.length != 0)
            throw new IllegalArgumentException("Scalar has no dimensionality (referred to with " + ids.length + " dimensions).");
        return getCell();
    }

    @Override
    @SuppressWarnings({"unchecked", "varargs"})
    public void setCellById(long value, I... ids) {
        if (ids.length != 0)
            throw new IllegalArgumentException("Scalar has no dimensionality (referred to with " + ids.length + " dimensions).");
        setCell(value);
    }

    @Override
    public Long getValueById() {
        return getValue();
    }

    @Override
    public void setValueById(Long value) {
        setValue(value);
    }
}
