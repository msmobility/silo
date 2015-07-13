package com.pb.sawdust.tensor.decorators.primitive.size;

import com.pb.sawdust.tensor.decorators.primitive.LongTensor;
import com.pb.sawdust.tensor.decorators.size.D8Tensor;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code LongD8Tensor} interface combines the {@code LongTensor} and {@code D8Tensor} interfaces. The notes and caveats
 * specified in those interfaces apply here as well.
 *
 * @author crf <br/>
 *         Started: Sat Oct 25 21:35:12 2008
 *         Revised: Dec 14, 2009 12:35:32 PM
 */
public interface LongD8Tensor extends LongTensor, D8Tensor<Long> {
    /**
     * Get the cell in this tensor at the specified location. This method should be more efficient than the other {@code getCell} and {@code getValue} 
     * methods in the interfaces being extended.
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
     * @param d5index
     *        The index of dimension 5 of the tensor element.
     *
     * @param d6index
     *        The index of dimension 6 of the tensor element.
     *
     * @param d7index
     *        The index of dimension 7 of the tensor element.
     *
     * @return the tensor value at the specified location.
     *
     * @throws IndexOutOfBoundsException if any index in {@code remainingIndices} is less than zero or greater than or
     *                                   equal to the size of its corresponding dimension.
     */
    long getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index);
    
    /**
     * Set the value of a cell in this tensor at the specified location.  This method should be more efficient than the other {@code setCell} and 
     * {@code getValue} methods in the interfaces being extended.
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
     * @param d5index
     *        The index of dimension 5 of the tensor element.
     *
     * @param d6index
     *        The index of dimension 6 of the tensor element.
     *
     * @param d7index
     *        The index of dimension 7 of the tensor element.
     *
     * @throws IndexOutOfBoundsException if any index in {@code remainingIndices} is less than zero or greater than or
     *                                   equal to the size of its corresponding dimension.
     */
    void setCell(long value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index);
    
    /**
     * {@inheritDoc}
     * 
     * The tensors this iterator loops over are guaranteed to be {@code LongD7Tensor} tensors.
     * 
     */
    Iterator<Tensor<Long>> iterator();
}
