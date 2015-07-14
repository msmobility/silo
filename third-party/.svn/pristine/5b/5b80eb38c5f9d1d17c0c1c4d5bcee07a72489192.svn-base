package com.pb.sawdust.tensor.alias.vector.id;

import com.pb.sawdust.tensor.alias.vector.primitive.LongVector;
import com.pb.sawdust.tensor.decorators.id.primitive.size.IdLongD1Tensor;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code IdLongVector} interface provides an alternate name for 1-dimensional id tensors holding {@code long}s.
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 9:22:03 AM
 */
public interface IdLongVector<I> extends IdLongD1Tensor<I>,LongVector {
    
    /**
     * {@inheritDoc}
     * 
     * The tensors this iterator loops over are guaranteed to be {@code IdLongScalar}s.
     * 
     */
    Iterator<Tensor<Long>> iterator();
}
