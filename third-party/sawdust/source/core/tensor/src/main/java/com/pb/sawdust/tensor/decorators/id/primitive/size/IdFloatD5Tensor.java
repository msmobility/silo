package com.pb.sawdust.tensor.decorators.id.primitive.size;


import com.pb.sawdust.tensor.decorators.id.primitive.IdFloatTensor;
import com.pb.sawdust.tensor.decorators.primitive.size.FloatD5Tensor;
import com.pb.sawdust.tensor.decorators.id.size.IdD5Tensor;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code IdFloatD5Tensor} class combines the {@code Float5Tensor} and {@code IdTensor} interfaces. It adds a
 * number of methods extending {@code FloatD5Tensor} methods so that dimensional indices can be referenced by ids.
 *
 * @param <I>
 *        The type of id used to reference dimensional indices.
 *
 * @author crf <br/>
 *         Started: Jan 14, 2009 11:00:16 PM
 *         Revised: Dec 14, 2009 12:35:28 PM
 */
public interface IdFloatD5Tensor<I> extends FloatD5Tensor,IdFloatTensor<I>,IdD5Tensor<Float,I> {
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
     * @param d2id
     *        The id of dimension 2 of the tensor element.
     *
     * @param d3id
     *        The id of dimension 3 of the tensor element.
     *
     * @param d4id
     *        The id of dimension 4 of the tensor element.
     *
     * @return the tensor value at the specified location.
     *
     * @throws IllegalArgumentException if any id is not valid in its respective dimension.
     */
    float getCellById(I d0id, I d1id, I d2id, I d3id, I d4id);

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
     * @param d2id
     *        The id of dimension 2 of the tensor element.
     *
     * @param d3id
     *        The id of dimension 3 of the tensor element.
     *
     * @param d4id
     *        The id of dimension 4 of the tensor element.
     *
     * @throws IllegalArgumentException if any id is not valid in its respective dimension.
     */
    void setCellById(float value, I d0id, I d1id, I d2id, I d3id, I d4id);

    /**
     * {@inheritDoc}
     *
     * The tensors this iterator loops over are guaranteed to be {@code IdfloatD4Tensor<I>} tensors.
     * 
     */
    Iterator<Tensor<Float>> iterator();
}
