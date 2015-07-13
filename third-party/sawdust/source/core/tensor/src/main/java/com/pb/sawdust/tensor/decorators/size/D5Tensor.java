package com.pb.sawdust.tensor.decorators.size;

import com.pb.sawdust.tensor.Tensor;
import java.util.Iterator;

/**
 * The {@code D5Tensor} interface specifies a tensor with {@code 5} dimensions.
 *
 * @param <T>
 *        The type this tensor holds.
 *
 * @author crf <br/>
 *         Started: Oct 20, 2008 6:55:24 PM
 *         Revised: Dec 14, 2009 12:35:29 PM
 */
public interface D5Tensor<T> extends Tensor<T> {

    /**
     * Gets the number of dimensions in this tensor.
     *
     * @return {@code 5}, the number of dimensions in this tensor.
     */
    int size();
    
    /**
     * Get the cell in this tensor at the specified location. This method should be more efficient than {@code getCell(int[])}, as it
     * does not require forming an array from the index arguments, and dimension bounds checking is not required.
     *
     * @param d0index
     *        The index of dimension 0 of the tensor element.
     *
     * @param d1index
     *        The index of dimension 1 of the tensor element.
     *
     * @param d2index
     *        The index of dimension 2 of the tensor element.
     *
     * @param d3index
     *        The index of dimension 3 of the tensor element.
     *
     * @param d4index
     *        The index of dimension 4 of the tensor element.
     *
     * @return the tensor value at the specified location.
     *
     * @throws IndexOutOfBoundsException if any index in {@code remainingIndices} is less than zero or greater than or
     *                                   equal to the size of its corresponding dimension.
     */
    T getValue(int d0index, int d1index, int d2index, int d3index, int d4index);
    
    /**
     * Set the cell in this tensor at the specified location. This method should be more efficient than {@code setCell(T,int[])}, as it
     * does not require forming an array from the index arguments, and dimension bounds checking is not required.
     *
     * @param value
     *        The value to set the cell to.
     *
     * @param d0index
     *        The index of dimension 0 of the tensor element.
     *
     * @param d1index
     *        The index of dimension 1 of the tensor element.
     *
     * @param d2index
     *        The index of dimension 2 of the tensor element.
     *
     * @param d3index
     *        The index of dimension 3 of the tensor element.
     *
     * @param d4index
     *        The index of dimension 4 of the tensor element.
     *
     * @throws IndexOutOfBoundsException if any index in {@code remainingIndices} is less than zero or greater than or
     *                                   equal to the size of its corresponding dimension.
     */
    void setValue(T value, int d0index, int d1index, int d2index, int d3index, int d4index);
    
    /**
     * {@inheritDoc}
     * 
     * The tensors this iterator loops over are guaranteed to be {@code D4Tensor<T>} tensors.
     * 
     */
    Iterator<Tensor<T>> iterator();
}
