package com.pb.sawdust.tensor.decorators.id.size;

import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.decorators.id.IdTensor;
import com.pb.sawdust.tensor.decorators.size.D3Tensor;

import java.util.Iterator;

/**
 * The {@code IdD3Tensor} class combines the {@code D3Tensor} and {@code IdTensor} interfaces. It adds a number of
 * methods extending {@code D3Tensor} methods so that dimensional indices can be referenced by ids.
 *
 * @param <T>
 *        The type this tensor holds.
 *
 * @param <I>
 *        The type of id used to reference dimensional indices.
 *
 * @author crf <br/>
 *         Started: Jan 15, 2009 8:21:40 AM
 *         Revised: Dec 14, 2009 12:35:26 PM
 */
public interface IdD3Tensor<T,I> extends D3Tensor<T>,IdTensor<T,I> {

    /**
     * Get the cell in this tensor at the specified location, referenced by id. This method should be more efficient
     * than {@code getCell(I[])}.
     *
     * @param d0id
     *        The id of dimension 0 of the tensor element.
     *
     * @param d1id
     *        The id of dimension 1 of the tensor element.
     *
     * @param d2id
     *        The id of dimension 2 of the tensor element.
     *
     * @return the tensor value at the specified location.
     *
     * @throws IllegalArgumentException if any id is not valid in its respective dimension.
     */
    T getValueById(I d0id, I d1id, I d2id);

    /**
     * Set the cell in this tensor at the specified location, referenced by id. This method should be more efficient
     * than {@code setCell(T,I[])}.does not require forming an array from the index arguments, and dimension bounds checking is not required.
     *
     * @param value
     *        The value to set the cell to.
     *
     * @param d0id
     *        The id of dimension 0 of the tensor element.
     *
     * @param d1id
     *        The id of dimension 1 of the tensor element.
     *
     * @param d2id
     *        The id of dimension 2 of the tensor element.
     *
     * @throws IllegalArgumentException if any id is not valid in its respective dimension.
     */
    void setValueById(T value, I d0id, I d1id, I d2id);

    /**
     * {@inheritDoc}
     *
     * The tensors this iterator loops over are guaranteed to be {@code IdD2Tensor<T,I>} tensors.
     * 
     */
    Iterator<Tensor<T>> iterator();
}
