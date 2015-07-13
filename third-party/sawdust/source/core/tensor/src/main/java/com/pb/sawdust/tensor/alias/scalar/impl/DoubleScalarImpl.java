package com.pb.sawdust.tensor.alias.scalar.impl;

import com.pb.sawdust.tensor.decorators.primitive.AbstractDoubleTensor;
import com.pb.sawdust.tensor.decorators.primitive.size.AbstractDoubleD0Tensor;
import com.pb.sawdust.tensor.decorators.id.primitive.IdDoubleTensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.TensorImplUtil;

/**
 * The {@code DoubleScalarImpl} class provides the default {@code DoubleScalar} implementation.
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 8:14:56 PM
 */
public class DoubleScalarImpl extends AbstractDoubleD0Tensor {
    private double value;

    /**
     * Constructor placing a default {@code double} as the scalar value.
     */
    public DoubleScalarImpl() {
    }

    /**
     * Constructor specifying the value of the scalar.
     *
     * @param value
     *        The value to set the scalar to.
     */
    public DoubleScalarImpl(double value) {
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
    protected DoubleScalarImpl(double value, Index<?> index) {
        super(index);
        this.value = value;
    }

    @Override
    public double getCell() {
        return value;
    }

    @Override
    public void setCell(double value) {
        this.value = value;
    }

    @Override
    @SuppressWarnings("unchecked") //idTensorCaster will turn this to an IdDoubleTensor, for sure
    public <I> IdDoubleTensor<I> getReferenceTensor(Index<I> index) {
        if (!index.isValidFor(this))
            throw new IllegalArgumentException("Index invalid for this tensor.");
        return (IdDoubleTensor<I>) TensorImplUtil.idTensorCaster(new ComposedTensor(index));
    }

    private class ComposedTensor extends AbstractDoubleTensor {

        public ComposedTensor(Index<?> index) {
            super(index);
        }

        @Override
        public double getCell(int... indices) {
            return DoubleScalarImpl.this.getCell();
        }

        @Override
        public void setCell(double value, int... indices) {
            DoubleScalarImpl.this.setCell(value);
        }
    }
}
