package com.pb.sawdust.tensor.decorators.id.primitive.size;

import com.pb.sawdust.tensor.alias.scalar.id.IdFloatScalar;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.decorators.primitive.size.FloatD0Tensor;
import com.pb.sawdust.tensor.decorators.size.AbstractD0Tensor;
import com.pb.sawdust.tensor.decorators.id.primitive.IdFloatTensor;
import com.pb.sawdust.util.array.FloatTypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArray;
import com.pb.sawdust.util.JavaType;

import java.util.List;

/**
 * The {@code IdFloatD0TensorShell} class is a wrapper which sets a {@code FloatD0Tensor} as an {@code IdTensor<I>} (or, more
 * spceifically an {@code IdFloatD0Tensor}).
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 11:39:14 PM
 */
public class IdFloatD0TensorShell<I> extends AbstractD0Tensor<Float> implements IdFloatScalar<I> {
    private final FloatD0Tensor scalar;
    private final Index<I> index;

    private IdFloatD0TensorShell(FloatD0Tensor scalar, Index<I> index) {
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
    public IdFloatD0TensorShell(FloatD0Tensor scalar) {
        this(scalar,(Index<I>) scalar.getIndex());
    }

    private IdFloatD0TensorShell(FloatD0Tensor scalar, boolean ignored) {
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
    public IdFloatD0TensorShell(FloatD0Tensor scalar, List<List<I>> ids) {
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
    @SuppressWarnings({"unchecked","varargs"}) //Index id type doesn't matter for scalars, so we can cast such an index as needed
    public IdFloatD0TensorShell(FloatD0Tensor scalar, I[] ... ids) {
        this(scalar,checkIds(ids.length == 0));
    }

    private static boolean checkIds(boolean ok) {
        if (!ok)
            throw new IllegalArgumentException("Scalar is dimensionless so id list must be empty.");
        return ok;
    }

    @Override
    public float getCellById() {
        return getCell();
    }

    @Override
    public void setCellById(float value) {
        setCell(value);
    }

    @Override
    public float getCell() {
        return scalar.getCell();
    }

    @Override
    public void setCell(float value) {
        scalar.setCell(value);
    }

    @Override
    public float getCell(int ... indices) {
        return scalar.getCell(indices);
    }

    @Override
    public void setCell(float value, int ... indices) {
        scalar.setCell(value,indices);
    }

    @Override
    public Float getValue() {
        return scalar.getValue();
    }

    @Override
    public void setValue(Float value) {
        scalar.setValue(value);
    }

    @Override
    public JavaType getType() {
        return scalar.getType();
    }

    @Override
    public Float getValue(int ... indices) {
        return scalar.getValue(indices);
    }

    @Override
    public void setValue(Float value, int ... indices) {
        scalar.setValue(value,indices);
    }

    @Override
    public void setTensorValues(FloatTypeSafeArray valuesArray) {
        scalar.setTensorValues(valuesArray);
    }

    @Override
    public FloatTypeSafeArray getTensorValues(Class<Float> type) {
        return scalar.getTensorValues(type);
    }

    @Override
    public void setTensorValues(TypeSafeArray<? extends Float> typeSafeArray) {
        scalar.setTensorValues(typeSafeArray);
    }

    @Override
    public void setTensorValues(Tensor<? extends Float> tensor) {
        scalar.setTensorValues(tensor);
    }

    @Override
    public FloatTypeSafeArray getTensorValues() {
        return scalar.getTensorValues();
    }

    @Override
    public <J> IdFloatTensor<J> getReferenceTensor(Index<J> index) {
        return scalar.getReferenceTensor(index);
    }

    @Override
    public Index<I> getIndex() {
        return index;
    }

    @Override
    @SuppressWarnings({"unchecked", "varargs"})
    public Float getValueById(I ... ids) {
        if (ids.length != 0)
            throw new IllegalArgumentException("Scalar has no dimensionality (referred to with " + ids.length + " dimensions).");
        return getValue();
    }

    @Override
    @SuppressWarnings({"unchecked", "varargs"})
    public void setValueById(Float value, I... ids) {
        if (ids.length != 0)
            throw new IllegalArgumentException("Scalar has no dimensionality (referred to with " + ids.length + " dimensions).");
        setValue(value);
    }

    @Override
    @SuppressWarnings({"unchecked", "varargs"})
    public float getCellById(I... ids) {
        if (ids.length != 0)
            throw new IllegalArgumentException("Scalar has no dimensionality (referred to with " + ids.length + " dimensions).");
        return getCell();
    }

    @Override
    @SuppressWarnings({"unchecked", "varargs"})
    public void setCellById(float value, I... ids) {
        if (ids.length != 0)
            throw new IllegalArgumentException("Scalar has no dimensionality (referred to with " + ids.length + " dimensions).");
        setCell(value);
    }

    @Override
    public Float getValueById() {
        return getValue();
    }

    @Override
    public void setValueById(Float value) {
        setValue(value);
    }
}
