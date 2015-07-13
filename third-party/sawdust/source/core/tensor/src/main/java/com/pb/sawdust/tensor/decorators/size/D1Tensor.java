package com.pb.sawdust.tensor.decorators.size;

import com.pb.sawdust.tensor.Tensor;
import java.util.Iterator;

/**
 * The {@code D1Tensor} interface specifies a tensor with {@code 1} dimensions.
 *
 * @param <T>
 *        The type this tensor holds.
 *
 * @author crf <br/>
 *         Started: Oct 20, 2008 6:55:24 PM
 *         Revised: Dec 14, 2009 12:35:24 PM
 */
public interface D1Tensor<T> extends Tensor<T> {

    /**
     * Gets the number of dimensions in this tensor.
     *
     * @return {@code 1}, the number of dimensions in this tensor.
     */
    int size();
    
    /**
     * Get the cell in this tensor at the specified location. This method should be more efficient than {@code getCell(int[])}, as it
     * does not require forming an array from the index arguments, and dimension bounds checking is not required.
     *
     * @param index
     *        The index of the tensor element.
     *
     * @return the tensor value at the specified location.
     *
     * @throws IndexOutOfBoundsException if any index in {@code remainingIndices} is less than zero or greater than or
     *                                   equal to the size of its corresponding dimension.
     */
    T getValue(int index);
    
    /**
     * Set the cell in this tensor at the specified location. This method should be more efficient than {@code setCell(T,int[])}, as it
     * does not require forming an array from the index arguments, and dimension bounds checking is not required.
     *
     * @param value
     *        The value to set the cell to.
     *
     * @param index
     *        The index of the tensor element.
     *
     * @throws IndexOutOfBoundsException if any index in {@code remainingIndices} is less than zero or greater than or
     *                                   equal to the size of its corresponding dimension.
     */
    void setValue(T value, int index);
    
    /**
     * {@inheritDoc}
     * 
     * The tensors this iterator loops over are guaranteed to be {@code D0Tensor<T>} tensors.
     * 
     */
    Iterator<Tensor<T>> iterator();
}
