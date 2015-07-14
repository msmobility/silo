package com.pb.sawdust.tensor.decorators.id.primitive.size;

import com.pb.sawdust.tensor.alias.vector.primitive.LongVector;
import com.pb.sawdust.tensor.decorators.id.primitive.IdLongTensor;
import com.pb.sawdust.tensor.decorators.primitive.size.LongD1Tensor;
import com.pb.sawdust.tensor.alias.vector.id.IdVector;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code IdLongVector} class combines the {@code Long1Tensor} and {@code IdTensor} interfaces. It adds a
 * number of methods extending {@code LongVector} methods so that dimensional indices can be referenced by ids.
 *
 * @param <I>
 *        The type of id used to reference dimensional indices.
 *
 * @author crf <br/>
 *         Started: Jan 14, 2009 11:00:16 PM
 *         Revised: Dec 14, 2009 12:35:23 PM
 */
public interface IdLongD1Tensor<I> extends LongVector,IdLongTensor<I>,IdVector<Long,I> {
    /**
     * Get the cell in this tensor at the specified location. This method should be more efficient than the other {@code getCell}
     * and {@code getValue} methods using ids in the interfaces being extended.
     *
     * @param id
     *        The id of the tensor element.
     *
     * @return the tensor value at the specified location.
     *
     * @throws IllegalArgumentException if any id is not valid in its respective dimension.
     */
    long getCellById(I id);

    /**
     * Set the value of a cell in this tensor at the specified location, specified by id.  This method should be more
     * efficient than the other {@code setCell} and {@code getValue} methods using ids in the interfaces being extended.
     *
     * @param value
     *        The value to set the cell to.
     *
     * @param id
     *        The id of the tensor element.
     *
     * @throws IllegalArgumentException if any id is not valid in its respective dimension.
     */
    void setCellById(long value, I id);

    /**
     * {@inheritDoc}
     *
     * The tensors this iterator loops over are guaranteed to be {@code IdlongD0Tensor<I>} tensors.
     * 
     */
    Iterator<Tensor<Long>> iterator();
}
