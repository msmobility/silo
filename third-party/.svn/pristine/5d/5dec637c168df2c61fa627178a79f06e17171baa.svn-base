package com.pb.sawdust.tensor.decorators.primitive;

import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.util.array.IntTypeSafeArray;
import com.pb.sawdust.tensor.decorators.id.primitive.IdIntTensor;

import java.util.Iterator;

/**
 * The {@code IntTensor} interface specifies a tensor which holds {@code int} values. Though the tensor methods using {@code Integer}
 * values exist and are valid, new methods defined in this interface using {@code int} values should be used in preference. Likewise, implementations
 * should store values as {@code int}s for (potential) performance reasons.
 *
 * @author crf <br/>
 *         Started: Oct 18, 2008 7:47:40 PM
 *         Revised: Dec 14, 2009 12:35:34 PM
 */
public interface IntTensor extends Tensor<Integer> {
    /**
     * Get the value of the cell at the specified location. This method should be more efficient than {@code getValue(int[])}.
     *
     * @param indices
     *        The index location for each dimension. The number of indices must match the dimension of this tensor.
     *
     * @return the value of the cell at {@code indices}.
     *
     * @throws IllegalArgumentException if the size of {@code indices} does not equal the number of dimensions in this tensor.
     * @throws IndexOutOfBoundsException if any index in {@code remainingIndices} is less than zero or greater than or
     *                                   equal to the size of its corresponding dimension.
     */
    int getCell(int ... indices);
    
    /**
     * Set the cell at the specified location. This method should be more efficient than {@code setValue(Integer,int[])}.
     *
     * @param value
     *        The value to set the cell to.
     *
     * @param indices
     *        The index location for each dimension. The number of indices must match the dimension of this tensor.
     *
     * @throws IllegalArgumentException if the size of {@code indices} does not equal the number of dimensions in this tensor.
     * @throws IndexOutOfBoundsException if any index in {@code remainingIndices} is less than zero or greater than or
     *                                   equal to the size of its corresponding dimension.
     */
    void setCell(int value, int ... indices);

    /**
     * Set the values of the entire tensor. Once this method has completed, this tensor will not refer to
     * {@code valuesArray} for its values; that is, changes to {@code valuesArray} will not affect this tensor's values.
     *
     * @param valuesArray
     *        The array of values to set this tensor to.
     *
     * @throws IllegalArgumentException if dimension of this tensor and {@code valuesArray} do not match.
     */
    public void setTensorValues(IntTypeSafeArray valuesArray);
    
    /**
     * {@inheritDoc}
     * 
     * The {@code type} parameter is ignored in this method call.
     */
    IntTypeSafeArray getTensorValues(Class<Integer> type);

    /**
     * Get the values of this tensor. The returned values array will contain a copy of the values, so subsequent changes
     * to the tensor will not affect the returned array, and vice-versa.
     *
     * @return an array holding the values of this tensor.
    **/
    IntTypeSafeArray getTensorValues();
    
    <I> IdIntTensor<I> getReferenceTensor(Index<I> index);
    
    /**
     * {@inheritDoc}
     * 
     * The tensors this iterator loops over are guaranteed to be {@code IntTensor} tensors.
     */
    Iterator<Tensor<Integer>> iterator();
}
