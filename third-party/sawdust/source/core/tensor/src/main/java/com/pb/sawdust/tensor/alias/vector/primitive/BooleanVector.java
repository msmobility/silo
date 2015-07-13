package com.pb.sawdust.tensor.alias.vector.primitive;

import com.pb.sawdust.tensor.decorators.primitive.size.BooleanD1Tensor;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code IdBooleanVector} interface provides an alternate name for 1-dimensional tensors holding {@code boolean}s.
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 9:22:03 AM
 */
public interface BooleanVector extends BooleanD1Tensor {
    
    /**
     * {@inheritDoc}
     * 
     * The tensors this iterator loops over are guaranteed to be {@code BooleanScalar}s.
     * 
     */
    Iterator<Tensor<Boolean>> iterator();
}
