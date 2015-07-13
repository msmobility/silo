package com.pb.sawdust.tensor.alias.scalar.impl;

import com.pb.sawdust.tensor.decorators.primitive.AbstractFloatTensor;
import com.pb.sawdust.tensor.decorators.primitive.size.AbstractFloatD0Tensor;
import com.pb.sawdust.tensor.decorators.id.primitive.IdFloatTensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.TensorImplUtil;

/**
 * The {@code FloatScalarImpl} class provides the default {@code FloatScalar} implementation.
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 8:14:56 PM
 */
public class FloatScalarImpl extends AbstractFloatD0Tensor {
    private float value;

    /**
     * Constructor placing a default {@code float} as the scalar value.
     */
    public FloatScalarImpl() {
    }

    /**
     * Constructor specifying the value of the scalar.
     *
     * @param value
     *        The value to set the scalar to.
     */
    public FloatScalarImpl(float value) {
        this.value = value;
    }

    /**
     * Constructor specifying the value and index of the scalar. This constructor should only be used for calls to
     * {@code getReferenceTensor(Index)}.
     *
     * @param value
     *        The value to set the scalar to.
     *
     * @param index
     *        The index to use with this scalar.
     *
     * @throws IllegalArgumentException if {@code index.size() != 0}.
     */
    protected FloatScalarImpl(float value, Index<?> index) {
        super(index);
        this.value = value;
    }

    @Override
    public float getCell() {
        return value;
    }

    @Override
    public void setCell(float value) {
        this.value = value;
    }

    @Override
    @SuppressWarnings("unchecked") //idTensorCaster will turn this to an IdFloatTensor, for sure
    public <I> IdFloatTensor<I> getReferenceTensor(Index<I> index) {
        if (!index.isValidFor(this))
            throw new IllegalArgumentException("Index invalid for this tensor.");
        return (IdFloatTensor<I>) TensorImplUtil.idTensorCaster(new ComposedTensor(index));
    }

    private class ComposedTensor extends AbstractFloatTensor {

        public ComposedTensor(Index<?> index) {
            super(index);
        }

        @Override
        public float getCell(int... indices) {
            return FloatScalarImpl.this.getCell();
        }

        @Override
        public void setCell(float value, int... indices) {
            FloatScalarImpl.this.setCell(value);
        }
    }
}
