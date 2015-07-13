package com.pb.sawdust.tensor.decorators.primitive.size;

import com.pb.sawdust.tensor.decorators.primitive.FloatTensor;
import com.pb.sawdust.tensor.decorators.size.D0Tensor;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code FloatD0Tensor} interface combines the {@code FloatTensor} and {@code D0Tensor} interfaces. The notes and caveats
 * specified in those interfaces apply here as well.
 *
 * @see com.pb.sawdust.tensor.alias.scalar.primitive.FloatScalar
 *
 * @author crf <br/>
 *         Started: Sat Oct 25 21:35:12 2008
 *         Revised: Jun 16, 2009 3:17:18 PM
 */
public interface FloatD0Tensor extends FloatTensor, D0Tensor<Float> {
    /**
     * Get the value of this tensor.
     *
     * @return the tensor value at the specified location.
     */
    float getCell();
    
    /**
     * Set the value of this tensor.
     *
     * @param value
     *        The value to set the tensor to.
     */
    void setCell(float value);
    
    /**
     * {@inheritDoc}
     * 
     * The returned iterator will iterate exactly once, returning this tensor.
     * 
     */
    Iterator<Tensor<Float>> iterator();
}
