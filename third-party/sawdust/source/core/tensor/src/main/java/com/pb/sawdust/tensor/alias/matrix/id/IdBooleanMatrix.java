package com.pb.sawdust.tensor.alias.matrix.id;

import com.pb.sawdust.tensor.alias.matrix.primitive.BooleanMatrix;
import com.pb.sawdust.tensor.decorators.id.primitive.size.IdBooleanD2Tensor;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code IdBooleanMatrix} interface provides an alternate name for 2-dimensional id tensors holding {@code boolean}s.
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 9:22:03 AM
 */
public interface IdBooleanMatrix<I> extends IdBooleanD2Tensor<I>,BooleanMatrix {
    
    /**
     * {@inheritDoc}
     * 
     * The tensors this iterator loops over are guaranteed to be {@code IdBooleanVector}s.
     * 
     */
    Iterator<Tensor<Boolean>> iterator();
}
