package com.pb.sawdust.tensor.decorators.id.primitive.size;

import com.pb.sawdust.tensor.alias.vector.primitive.DoubleVector;
import com.pb.sawdust.tensor.decorators.id.primitive.IdDoubleTensor;
import com.pb.sawdust.tensor.decorators.primitive.size.DoubleD1Tensor;
import com.pb.sawdust.tensor.alias.vector.id.IdVector;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code IdDoubleVector} class combines the {@code Double1Tensor} and {@code IdTensor} interfaces. It adds a
 * number of methods extending {@code DoubleVector} methods so that dimensional indices can be referenced by ids.
 *
 * @param <I>
 *        The type of id used to reference dimensional indices.
 *
 * @author crf <br/>
 *         Started: Jan 14, 2009 11:00:16 PM
 *         Revised: Dec 14, 2009 12:35:24 PM
 */
public interface IdDoubleD1Tensor<I> extends DoubleVector,IdDoubleTensor<I>,IdVector<Double,I> {
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
    double getCellById(I id);

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
    void setCellById(double value, I id);

    /**
     * {@inheritDoc}
     *
     * The tensors this iterator loops over are guaranteed to be {@code IddoubleD0Tensor<I>} tensors.
     * 
     */
    Iterator<Tensor<Double>> iterator();
}
