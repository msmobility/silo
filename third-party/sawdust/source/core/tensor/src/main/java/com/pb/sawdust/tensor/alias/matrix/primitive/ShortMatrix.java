package com.pb.sawdust.tensor.alias.matrix.primitive;

import com.pb.sawdust.tensor.decorators.primitive.size.ShortD2Tensor;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code ShortMatrix} interface provides an alternate name for 2-dimensional tensors holding {@code short}s.
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 9:22:03 AM
 */
public interface ShortMatrix extends ShortD2Tensor {   
    
    /**
     * {@inheritDoc}
     * 
     * The tensors this iterator loops over are guaranteed to be {@code ShortVector}s.
     * 
     */
    Iterator<Tensor<Short>> iterator();
}
