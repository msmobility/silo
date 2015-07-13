package com.pb.sawdust.tensor.decorators.id.size;

import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.decorators.id.IdTensor;
import com.pb.sawdust.tensor.alias.vector.Vector;

import java.util.Iterator;

/**
 * The {@code IdD1Tensor} class combines the {@code Vector} and {@code IdTensor} interfaces. It adds a number of
 * methods extending {@code Vector} methods so that dimensional indices can be referenced by ids.
 *
 * @param <T>
 *        The type this tensor holds.
 *
 * @param <I>
 *        The type of id used to reference dimensional indices.
 *
 * @author crf <br/>
 *         Started: Jan 15, 2009 8:21:40 AM
 *         Revised: Dec 14, 2009 12:35:24 PM
 */
public interface IdD1Tensor<T,I> extends Vector<T>,IdTensor<T,I> {

    /**
     * Get the cell in this tensor at the specified location, referenced by id. This method should be more efficient
     * than {@code getCell(I[])}.
     *
     * @param id
     *        The id of the tensor element.
     *
     * @return the tensor value at the specified location.
     *
     * @throws IllegalArgumentException if any id is not valid in its respective dimension.
     */
    T getValueById(I id);

    /**
     * Set the cell in this tensor at the specified location, referenced by id. This method should be more efficient
     * than {@code setCell(T,I[])}.does not require forming an array from the index arguments, and dimension bounds checking is not required.
     *
     * @param value
     *        The value to set the cell to.
     *
     * @param id
     *        The id of the tensor element.
     *
     * @throws IllegalArgumentException if any id is not valid in its respective dimension.
     */
    void setValueById(T value, I id);

    /**
     * {@inheritDoc}
     *
     * The tensors this iterator loops over are guaranteed to be {@code IdD0Tensor<T,I>} tensors.
     * 
     */
    Iterator<Tensor<T>> iterator();
}
