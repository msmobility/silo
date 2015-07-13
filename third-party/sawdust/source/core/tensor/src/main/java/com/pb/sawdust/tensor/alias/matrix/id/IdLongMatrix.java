package com.pb.sawdust.tensor.alias.matrix.id;

import com.pb.sawdust.tensor.alias.matrix.primitive.LongMatrix;
import com.pb.sawdust.tensor.decorators.id.primitive.size.IdLongD2Tensor;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code IdLongMatrix} interface provides an alternate name for 2-dimensional id tensors holding {@code long}s.
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 9:22:03 AM
 */
public interface IdLongMatrix<I> extends IdLongD2Tensor<I>,LongMatrix {
    
    /**
     * {@inheritDoc}
     * 
     * The tensors this iterator loops over are guaranteed to be {@code IdLongVector}s.
     * 
     */
    Iterator<Tensor<Long>> iterator();
}
