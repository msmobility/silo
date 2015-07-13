package com.pb.sawdust.tensor.alias.matrix.primitive;

import com.pb.sawdust.tensor.decorators.primitive.size.BooleanD2Tensor;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code BooleanMatrix} interface provides an alternate name for 2-dimensional tensors holding {@code boolean}s.
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 9:22:03 AM
 */
public interface BooleanMatrix extends BooleanD2Tensor {   
    
    /**
     * {@inheritDoc}
     * 
     * The tensors this iterator loops over are guaranteed to be {@code BooleanVector}s.
     * 
     */
    Iterator<Tensor<Boolean>> iterator();
}
