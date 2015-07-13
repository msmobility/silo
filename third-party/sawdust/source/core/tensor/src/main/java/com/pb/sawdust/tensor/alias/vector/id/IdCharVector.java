package com.pb.sawdust.tensor.alias.vector.id;

import com.pb.sawdust.tensor.alias.vector.primitive.CharVector;
import com.pb.sawdust.tensor.decorators.id.primitive.size.IdCharD1Tensor;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code IdCharVector} interface provides an alternate name for 1-dimensional id tensors holding {@code char}s.
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 9:22:03 AM
 */
public interface IdCharVector<I> extends IdCharD1Tensor<I>,CharVector {
    
    /**
     * {@inheritDoc}
     * 
     * The tensors this iterator loops over are guaranteed to be {@code IdCharScalar}s.
     * 
     */
    Iterator<Tensor<Character>> iterator();
}
