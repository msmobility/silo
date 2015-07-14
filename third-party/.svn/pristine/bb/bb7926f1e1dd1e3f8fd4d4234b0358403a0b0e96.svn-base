package com.pb.sawdust.tensor.decorators.id.primitive.size;

import com.pb.sawdust.tensor.alias.matrix.primitive.IntMatrix;
import com.pb.sawdust.tensor.decorators.id.primitive.IdIntTensor;
import com.pb.sawdust.tensor.decorators.primitive.size.IntD2Tensor;
import com.pb.sawdust.tensor.alias.matrix.id.IdMatrix;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code IdIntMatrix} class combines the {@code Int2Tensor} and {@code IdTensor} interfaces. It adds a
 * number of methods extending {@code IntMatrix} methods so that dimensional indices can be referenced by ids.
 *
 * @param <I>
 *        The type of id used to reference dimensional indices.
 *
 * @author crf <br/>
 *         Started: Jan 14, 2009 11:00:16 PM
 *         Revised: Dec 14, 2009 12:35:24 PM
 */
public interface IdIntD2Tensor<I> extends IntMatrix,IdIntTensor<I>,IdMatrix<Integer,I> {
    /**
     * Get the cell in this tensor at the specified location. This method should be more efficient than the other {@code getCell}
     * and {@code getValue} methods using ids in the interfaces being extended.
     *
     * @param d0id
     *        The id of dimension 0 of the tensor element.
     *
     * @param d1id
     *        The id of dimension 1 of the tensor element.
     *
     * @return the tensor value at the specified location.
     *
     * @throws IllegalArgumentException if any id is not valid in its respective dimension.
     */
    int getCellById(I d0id, I d1id);

    /**
     * Set the value of a cell in this tensor at the specified location, specified by id.  This method should be more
     * efficient than the other {@code setCell} and {@code getValue} methods using ids in the interfaces being extended.
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
     * @throws IllegalArgumentException if any id is not valid in its respective dimension.
     */
    void setCellById(int value, I d0id, I d1id);

    /**
     * {@inheritDoc}
     *
     * The tensors this iterator loops over are guaranteed to be {@code IdintD1Tensor<I>} tensors.
     * 
     */
    Iterator<Tensor<Integer>> iterator();
}
