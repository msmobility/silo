package com.pb.sawdust.tensor.decorators.id.size;

import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.decorators.id.IdTensor;
import com.pb.sawdust.tensor.decorators.size.D0Tensor;

import java.util.Iterator;

/**
 * The {@code IdD0Tensor} class combines the {@code D0Tensor} and {@code IdTensor} interfaces. Because it is
 * of rank 0, ids are meangless/unused; nonetheless, this interface is specified as a complement to the other {@code Id...}
 * interfaces.
 *
 * @param <T>
 *        The type this tensor holds.
 *
 * @param <I>
 *        The type of id used to reference dimensional indices.
 *
 * @author crf <br/>
 *         Started: Jan 15, 2009 8:21:40 AM
 *         Revised: Jun 16, 2009 3:17:18 PM
 */
public interface IdD0Tensor<T,I> extends D0Tensor<T>,IdTensor<T,I> {

    /**
     * Get the value held in this tensor.
     *
     * @return the tensor value.
     */
    T getValueById();

    /**
     * Set the value this tensor.
     *
     * @param value
     *        The value to set this tensor to.
     */
    void setValueById(T value);

    /**
     * {@inheritDoc}
     *
     * The returned iterator will iterate exactly once, returning this tensor.
     *
     */
    Iterator<Tensor<T>> iterator();
}
