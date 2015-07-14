package com.pb.sawdust.tensor.decorators.primitive.size;

import com.pb.sawdust.tensor.decorators.primitive.BooleanTensor;
import com.pb.sawdust.tensor.alias.vector.Vector;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code BooleanD1Tensor} interface combines the {@code BooleanTensor} and {@code Vector} interfaces. The notes and caveats
 * specified in those interfaces apply here as well.
 *
 * @author crf <br/>
 *         Started: Sat Oct 25 21:35:12 2008
 *         Revised: Dec 14, 2009 12:35:24 PM
 */
public interface BooleanD1Tensor extends BooleanTensor, Vector<Boolean> {
    /**
     * Get the cell in this tensor at the specified location. This method should be more efficient than the other {@code getCell} and {@code getValue} 
     * methods in the interfaces being extended.
     *
     * @param index
     *        The index of the tensor element.
     *
     * @return the tensor value at the specified location.
     *
     * @throws IndexOutOfBoundsException if any index in {@code remainingIndices} is less than zero or greater than or
     *                                   equal to the size of its corresponding dimension.
     */
    boolean getCell(int index);
    
    /**
     * Set the value of a cell in this tensor at the specified location.  This method should be more efficient than the other {@code setCell} and 
     * {@code getValue} methods in the interfaces being extended.
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
    void setCell(boolean value, int index);
    
    /**
     * {@inheritDoc}
     * 
     * The tensors this iterator loops over are guaranteed to be {@code BooleanD0Tensor} tensors.
     * 
     */
    Iterator<Tensor<Boolean>> iterator();
}
