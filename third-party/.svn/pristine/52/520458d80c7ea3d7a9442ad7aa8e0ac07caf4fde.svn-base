package com.pb.sawdust.tensor.alias.matrix.id;

import com.pb.sawdust.tensor.alias.matrix.primitive.FloatMatrix;
import com.pb.sawdust.tensor.decorators.id.primitive.size.IdFloatD2Tensor;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code IdFloatMatrix} interface provides an alternate name for 2-dimensional id tensors holding {@code float}s.
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 9:22:03 AM
 */
public interface IdFloatMatrix<I> extends IdFloatD2Tensor<I>,FloatMatrix {
    
    /**
     * {@inheritDoc}
     * 
     * The tensors this iterator loops over are guaranteed to be {@code IDFloatVector}s.
     * 
     */
    Iterator<Tensor<Float>> iterator();
}
