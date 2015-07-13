package com.pb.sawdust.tensor.alias.scalar.impl;

import com.pb.sawdust.tensor.AbstractTensor;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.decorators.size.AbstractD0Tensor;
import com.pb.sawdust.tensor.decorators.id.IdTensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.util.JavaType;

/**
 * The {@code ScalarImpl} class provides the default {@code Scalar} implementation.
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 9:37:29 AM
 */
public class ScalarImpl<T> extends AbstractD0Tensor<T> {
    private T value;

    /**
     * Constructor specifying the scalar value.
     *
     * @param value
     *        The scalar value.
     */
    public ScalarImpl(T value) {
        this.value = value;
    }

    /**
     * Default constructor, using {@code null} for the scalar value.
     */
    public ScalarImpl() {
        this(null);
    }

    /**
     * Constructor specifying the scalar value and the index to use with it.
     *
     * @param value
     *        The scalar value.
     *
     * @param index
     *        The index to use for the scalar.
     */
    protected ScalarImpl(T value, Index<?> index) {
        super(index);
        this.value = value;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public <I> IdTensor<T,I> getReferenceTensor(Index<I> index) {
        if (!index.isValidFor(this))
            throw new IllegalArgumentException("Index invalid for creating a reference tensor from this scalar.");
        return TensorImplUtil.idTensorCaster(new ComposedTensor(index));
    }

    private class ComposedTensor extends AbstractTensor<T> {

        public ComposedTensor(Index<?> index) {
            super(index);
        }

        @Override
         public JavaType getType() {
            return ScalarImpl.this.getType();
        }

        @Override
        public T getValue(int... indices) {
            return ScalarImpl.this.getValue();
        }

        @Override
        public void setValue(T value, int... indices) {
            ScalarImpl.this.setValue(value);
        }
    }
}
