package com.pb.sawdust.tensor.decorators.primitive.size;

import com.pb.sawdust.tensor.decorators.primitive.FloatTensor;
import com.pb.sawdust.tensor.alias.matrix.Matrix;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code FloatD2Tensor} interface combines the {@code FloatTensor} and {@code Matrix} interfaces. The notes and caveats
 * specified in those interfaces apply here as well.
 *
 * @author crf <br/>
 *         Started: Sat Oct 25 21:35:12 2008
 *         Revised: Dec 14, 2009 12:35:25 PM
 */
public interface FloatD2Tensor extends FloatTensor, Matrix<Float> {
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
     * @return the tensor value at the specified location.
     *
     * @throws IndexOutOfBoundsException if any index in {@code remainingIndices} is less than zero or greater than or
     *                                   equal to the size of its corresponding dimension.
     */
    float getCell(int d0index, int d1index);
    
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
     * @throws IndexOutOfBoundsException if any index in {@code remainingIndices} is less than zero or greater than or
     *                                   equal to the size of its corresponding dimension.
     */
    void setCell(float value, int d0index, int d1index);
    
    /**
     * {@inheritDoc}
     * 
     * The tensors this iterator loops over are guaranteed to be {@code FloatD1Tensor} tensors.
     * 
     */
    Iterator<Tensor<Float>> iterator();
}
