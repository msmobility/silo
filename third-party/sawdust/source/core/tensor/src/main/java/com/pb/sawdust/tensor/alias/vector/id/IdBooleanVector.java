package com.pb.sawdust.tensor.alias.vector.id;

import com.pb.sawdust.tensor.alias.vector.primitive.BooleanVector;
import com.pb.sawdust.tensor.decorators.id.primitive.size.IdBooleanD1Tensor;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code IdBooleanVector} interface provides an alternate name for 1-dimensional id tensors holding {@code boolean}s.
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 9:22:03 AM
 */
public interface IdBooleanVector<I> extends IdBooleanD1Tensor<I>,BooleanVector {
    
    /**
     * {@inheritDoc}
     * 
     * The tensors this iterator loops over are guaranteed to be {@code IdBooleanScalar}s.
     * 
     */
    Iterator<Tensor<Boolean>> iterator();
}
