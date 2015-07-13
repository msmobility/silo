package com.pb.sawdust.tensor.decorators.primitive.size;

import com.pb.sawdust.tensor.alias.scalar.primitive.BooleanScalar;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.decorators.size.AbstractD0Tensor;
import com.pb.sawdust.util.array.BooleanTypeSafeArray;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.array.TypeSafeArrayFactory;
import com.pb.sawdust.util.JavaType;

/**
 * The {@code AbstractBooleanD0Tensor} class provides a skeletal implementation of the {@code BooleanD0Tensor} interface.
 *
 * @author crf <br/>
 *         Started: Jun 22, 2009 1:23:58 PM
 */
public abstract class AbstractBooleanD0Tensor extends AbstractD0Tensor<Boolean> implements BooleanScalar {

    /**
     * Constructor which will create a scalar using a default index.
     */
    protected AbstractBooleanD0Tensor() {
    }

    /**
     * Constructor specifying the index to use.
     *
     * @param index
     *        The index to use for the scalar.
     *
     * @throws IllegalArgumentException if {@code index.size() != 0}. 
     */
    protected AbstractBooleanD0Tensor(Index<?> index) {
        super(index);
    }

    @Override
    public JavaType getType() {
        return JavaType.BOOLEAN;
    }

    @Override
    public Boolean getValue() {
        return getCell();
    }

    @Override
    public void setValue(Boolean value) {
        setCell(value);
    }

    @Override
    public boolean getCell(int... indices) {
        if (indices.length != 0)
            throw new IllegalArgumentException("Scalar has no dimensionality (referred to with " + indices.length + " dimensions).");
        return getValue();
    }

    @Override
    public void setCell(boolean value, int... indices) {
        if (indices.length != 0)
            throw new IllegalArgumentException("Scalar has no dimensionality (referred to with " + indices.length + " dimensions).");
        setValue(value);
    }

    @Override
    public void setTensorValues(BooleanTypeSafeArray valuesArray) {
        if (!ArrayUtil.isOfDimension(valuesArray.getArray(),new int[] {1}))
            throw new IllegalArgumentException("TypeSafeArray for setting a scalar must contain only one value.");
        setCell(valuesArray.get(0));
    }

    @Override
    public BooleanTypeSafeArray getTensorValues(Class<Boolean> c) {
        return getTensorValues();
    }

    @Override
    public BooleanTypeSafeArray getTensorValues() {
        BooleanTypeSafeArray array = TypeSafeArrayFactory.booleanTypeSafeArray(1);
        array.set(getCell(),0);
        return array;
    }
}
