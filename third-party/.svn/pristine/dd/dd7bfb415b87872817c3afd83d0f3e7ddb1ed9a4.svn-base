package com.pb.sawdust.tensor.alias.scalar.impl;

import com.pb.sawdust.tensor.decorators.primitive.AbstractCharTensor;
import com.pb.sawdust.tensor.decorators.primitive.size.AbstractCharD0Tensor;
import com.pb.sawdust.tensor.decorators.id.primitive.IdCharTensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.TensorImplUtil;

/**
 * The {@code CharScalarImpl} class provides the default {@code CharScalar} implementation.
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 8:14:56 PM
 */
public class CharScalarImpl extends AbstractCharD0Tensor {
    private char value;

    /**
     * Constructor placing a default {@code char} as the scalar value.
     */
    public CharScalarImpl() {
    }

    /**
     * Constructor specifying the value of the scalar.
     *
     * @param value
     *        The value to set the scalar to.
     */
    public CharScalarImpl(char value) {
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
    protected CharScalarImpl(char value, Index<?> index) {
        super(index);
        this.value = value;
    }

    @Override
    public char getCell() {
        return value;
    }

    @Override
    public void setCell(char value) {
        this.value = value;
    }

    @Override
    @SuppressWarnings("unchecked") //idTensorCaster will turn this to an IdCharTensor, for sure
    public <I> IdCharTensor<I> getReferenceTensor(Index<I> index) {
        if (!index.isValidFor(this))
            throw new IllegalArgumentException("Index invalid for this tensor.");
        return (IdCharTensor<I>) TensorImplUtil.idTensorCaster(new ComposedTensor(index));
    }

    private class ComposedTensor extends AbstractCharTensor {

        public ComposedTensor(Index<?> index) {
            super(index);
        }

        @Override
        public char getCell(int... indices) {
            return CharScalarImpl.this.getCell();
        }

        @Override
        public void setCell(char value, int... indices) {
            CharScalarImpl.this.setCell(value);
        }
    }
}
