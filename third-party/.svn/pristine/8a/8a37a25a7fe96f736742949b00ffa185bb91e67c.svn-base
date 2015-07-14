package com.pb.sawdust.tensor.alias.scalar.impl;

import com.pb.sawdust.tensor.decorators.primitive.AbstractLongTensor;
import com.pb.sawdust.tensor.decorators.primitive.size.AbstractLongD0Tensor;
import com.pb.sawdust.tensor.decorators.id.primitive.IdLongTensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.TensorImplUtil;

/**
 * The {@code LongScalarImpl} class provides the default {@code LongScalar} implementation.
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 8:14:56 PM
 */
public class LongScalarImpl extends AbstractLongD0Tensor {
    private long value;

    /**
     * Constructor placing a default {@code long} as the scalar value.
     */
    public LongScalarImpl() {
    }

    /**
     * Constructor specifying the value of the scalar.
     *
     * @param value
     *        The value to set the scalar to.
     */
    public LongScalarImpl(long value) {
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
    protected LongScalarImpl(long value, Index<?> index) {
        super(index);
        this.value = value;
    }

    @Override
    public long getCell() {
        return value;
    }

    @Override
    public void setCell(long value) {
        this.value = value;
    }

    @Override
    @SuppressWarnings("unchecked") //idTensorCaster will turn this to an IdLongTensor, for sure
    public <I> IdLongTensor<I> getReferenceTensor(Index<I> index) {
        if (!index.isValidFor(this))
            throw new IllegalArgumentException("Index invalid for this tensor.");
        return (IdLongTensor<I>) TensorImplUtil.idTensorCaster(new ComposedTensor(index));
    }

    private class ComposedTensor extends AbstractLongTensor {

        public ComposedTensor(Index<?> index) {
            super(index);
        }

        @Override
        public long getCell(int... indices) {
            return LongScalarImpl.this.getCell();
        }

        @Override
        public void setCell(long value, int... indices) {
            LongScalarImpl.this.setCell(value);
        }
    }
}
