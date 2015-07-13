package com.pb.sawdust.tensor.decorators.id.primitive.size;

import com.pb.sawdust.tensor.alias.scalar.id.IdBooleanScalar;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.decorators.primitive.size.BooleanD0Tensor;
import com.pb.sawdust.tensor.decorators.size.AbstractD0Tensor;
import com.pb.sawdust.tensor.decorators.id.primitive.IdBooleanTensor;
import com.pb.sawdust.util.array.BooleanTypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArray;
import com.pb.sawdust.util.JavaType;

import java.util.List;

/**
 * The {@code IdBooleanD0TensorShell} class is a wrapper which sets a {@code BooleanD0Tensor} as an {@code IdTensor<I>} (or, more
 * spceifically an {@code IdBooleanD0Tensor}).
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 11:39:14 PM
 */
public class IdBooleanD0TensorShell<I> extends AbstractD0Tensor<Boolean> implements IdBooleanScalar<I> {
    private final BooleanD0Tensor scalar;
    private final Index<I> index;

    private IdBooleanD0TensorShell(BooleanD0Tensor scalar, Index<I> index) {
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
    public IdBooleanD0TensorShell(BooleanD0Tensor scalar) {
        this(scalar,(Index<I>) scalar.getIndex());
    }

    private IdBooleanD0TensorShell(BooleanD0Tensor scalar, boolean ignored) {
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
    public IdBooleanD0TensorShell(BooleanD0Tensor scalar, List<List<I>> ids) {
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
    @SuppressWarnings({"unchecked","varargs"})  //Index id type doesn't matter for scalars, so we can cast such an index as needed
    public IdBooleanD0TensorShell(BooleanD0Tensor scalar, I[] ... ids) {
        this(scalar,checkIds(ids.length == 0));
    }

    private static boolean checkIds(boolean ok) {
        if (!ok)
            throw new IllegalArgumentException("Scalar is dimensionless so id list must be empty.");
        return ok;
    }

    @Override
    public boolean getCellById() {
        return getCell();
    }

    @Override
    public void setCellById(boolean value) {
        setCell(value);
    }

    @Override
    public boolean getCell() {
        return scalar.getCell();
    }

    @Override
    public void setCell(boolean value) {
        scalar.setCell(value);
    }

    @Override
    public boolean getCell(int ... indices) {
        return scalar.getCell(indices);
    }

    @Override
    public void setCell(boolean value, int ... indices) {
        scalar.setCell(value,indices);
    }

    @Override
    public Boolean getValue() {
        return scalar.getValue();
    }

    @Override
    public void setValue(Boolean value) {
        scalar.setValue(value);
    }

    @Override
    public JavaType getType() {
        return scalar.getType();
    }

    @Override
    public Boolean getValue(int ... indices) {
        return scalar.getValue(indices);
    }

    @Override
    public void setValue(Boolean value, int ... indices) {
        scalar.setValue(value,indices);
    }

    @Override
    public void setTensorValues(BooleanTypeSafeArray valuesArray) {
        scalar.setTensorValues(valuesArray);
    }

    @Override
    public BooleanTypeSafeArray getTensorValues(Class<Boolean> type) {
        return scalar.getTensorValues(type);
    }

    @Override
    public void setTensorValues(TypeSafeArray<? extends Boolean> typeSafeArray) {
        scalar.setTensorValues(typeSafeArray);
    }

    @Override
    public void setTensorValues(Tensor<? extends Boolean> tensor) {
        scalar.setTensorValues(tensor);
    }

    @Override
    public BooleanTypeSafeArray getTensorValues() {
        return scalar.getTensorValues();
    }

    @Override
    public <J> IdBooleanTensor<J> getReferenceTensor(Index<J> index) {
        return scalar.getReferenceTensor(index);
    }

    @Override
    public Index<I> getIndex() {
        return index;
    }

    @Override
    @SuppressWarnings({"unchecked", "varargs"})
    public Boolean getValueById(I ... ids) {
        if (ids.length != 0)
            throw new IllegalArgumentException("Scalar has no dimensionality (referred to with " + ids.length + " dimensions).");
        return getValue();
    }

    @Override
    @SuppressWarnings({"unchecked", "varargs"})
    public void setValueById(Boolean value, I... ids) {
        if (ids.length != 0)
            throw new IllegalArgumentException("Scalar has no dimensionality (referred to with " + ids.length + " dimensions).");
        setValue(value);
    }

    @Override
    @SuppressWarnings({"unchecked", "varargs"})
    public boolean getCellById(I... ids) {
        if (ids.length != 0)
            throw new IllegalArgumentException("Scalar has no dimensionality (referred to with " + ids.length + " dimensions).");
        return getCell();
    }

    @Override
    @SuppressWarnings({"unchecked", "varargs"})
    public void setCellById(boolean value, I... ids) {
        if (ids.length != 0)
            throw new IllegalArgumentException("Scalar has no dimensionality (referred to with " + ids.length + " dimensions).");
        setCell(value);
    }

    @Override
    public Boolean getValueById() {
        return getValue();
    }

    @Override
    public void setValueById(Boolean value) {
        setValue(value);
    }
}
