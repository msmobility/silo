package com.pb.sawdust.tensor.decorators.id.primitive;

import com.pb.sawdust.tensor.decorators.primitive.CharTensor;
import com.pb.sawdust.tensor.decorators.id.IdTensor;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**                  
 * The {@code IdCharTensor} class combines the {@code CharTensor} and {@code IdTensor} interfaces. It adds a number
 * of methods extending {@code CharTensor} methods so that dimensional indices can be referenced by ids.
 *
 * @param <I>
 *        The type of id used to reference dimensional indices.
 *
 * @author crf <br/>
 *         Started: Jan 14, 2009 10:07:58 PM
 *         Revised: Dec 14, 2009 12:35:34 PM
 */
public interface IdCharTensor<I> extends CharTensor,IdTensor<Character,I> {

    /**
     * Get the value of the cell at the specified location referenced by ids. This method should be more efficient than {@code getValue(I[])}.
     *
     * @param ids
     *        The index location for each dimension, referenced by id. The number of ids must match the number of dimensions in this tensor.
     *
     * @return the value of the cell at {@code ids}.
     *
     * @throws IllegalArgumentException if the size of {@code ids} does not equal the number of dimensions in this tensor,
     *                                  or if any of {@code ids} does not correspond to an index in that particular dimension.
     */
    @SuppressWarnings({"unchecked", "varargs"})
    char getCellById(I ... ids);

    /**
     * Set the cell at the specified location referenced by ids. This method should be more efficient than {@code setValue(Character,I[])}.
     *
     * @param value
     *        The value to set the cell to.
     *
     * @param ids
     *        The index location for each dimension, referenced by id. The number of ids must match the number of dimension in this tensor.
     *
     * @throws IllegalArgumentException if the size of {@code ids} does not equal the number of dimensions in this tensor,
     *                                  or if any of {@code ids} does not correspond to an index in that particular dimension.
     */
    @SuppressWarnings({"unchecked", "varargs"})
    void setCellById(char value, I ... ids);

    /**
     * {@inheritDoc}
     *
     * The tensors this iterator loops over are guaranteed to be {@code IdCharTensor<I>} tensors.
     */
    Iterator<Tensor<Character>> iterator();
}
