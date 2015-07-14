package com.pb.sawdust.tensor;

import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.decorators.id.IdTensor;
import com.pb.sawdust.util.ContainsMetadataImpl;

import java.util.*;

/**
 * The {@code ComposableTensor} class provides a partial implementation of the {@code Tensor} interface covering the
 * building of reference tensors.
 *
 * @param <T>
 *        The type held by this tensor.
 *
 * @author crf <br/>
 *         Started: Jun 27, 2009 9:36:10 AM
 */
public abstract class ComposableTensor<T> extends ContainsMetadataImpl<String> implements Tensor<T> {

    /**
     * Get a reference tensor for this tensor using the specified index. This method is used by {@code  getReferenceTensor(Index)}
     * and thus should not call that method. This method is also used when calling this tensor's iterator. This method
     * should try to implement as many interfaces as the returned tensor matches, so as to fulfill the aforementioned
     * method's contracts (implementation-specific situations may make certain interface implementations unnecessary).
     *
     * @param index
     *        The index for the returned tensor. It should be assumed that this index will be valid for this tensor.
     *
     * @return a tensor referring to this tensor and using {@code index}.
     */
    protected Tensor<T> getComposedTensor(Index<?> index) {
        return TensorImplUtil.getComposedTensor(this,index);
    }

    public <I> IdTensor<T,I> getReferenceTensor(Index<I> index) {
        if (!index.isValidFor(this))
            throw new IllegalArgumentException("Index invalid for this tensor.");
        return TensorImplUtil.idTensorCaster(getComposedTensor(index));
    }

    public Iterator<Tensor<T>> iterator() {
        return new Iterator<Tensor<T>>() {
            Iterator<? extends Index<?>> it = TensorImplUtil.getIndexIterator(getIndex());

            public boolean hasNext() {
                return it.hasNext();
            }

            public Tensor<T> next() {
                if (!it.hasNext())
                    throw new NoSuchElementException();
                return getComposedTensor(it.next());
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
