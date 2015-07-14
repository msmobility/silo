package com.pb.sawdust.tensor.alias.vector.id;

import com.pb.sawdust.tensor.alias.vector.primitive.FloatVector;
import com.pb.sawdust.tensor.decorators.id.primitive.size.IdFloatD1Tensor;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code IdFloatVector} interface provides an alternate name for 1-dimensional id tensors holding {@code float}s.
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 9:22:03 AM
 */
public interface IdFloatVector<I> extends IdFloatD1Tensor<I>,FloatVector {
    
    /**
     * {@inheritDoc}
     * 
     * The tensors this iterator loops over are guaranteed to be {@code IdFloatScalar}s.
     * 
     */
    Iterator<Tensor<Float>> iterator();
}
