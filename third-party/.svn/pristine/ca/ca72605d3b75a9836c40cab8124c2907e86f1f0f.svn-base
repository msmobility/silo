package com.pb.sawdust.tensor.alias.matrix.id;

import com.pb.sawdust.tensor.alias.matrix.primitive.ShortMatrix;
import com.pb.sawdust.tensor.decorators.id.primitive.size.IdShortD2Tensor;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code IdShortMatrix} interface provides an alternate name for 2-dimensional id tensors holding {@code short}s.
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 9:22:03 AM
 */
public interface IdShortMatrix<I> extends IdShortD2Tensor<I>,ShortMatrix {
    
    /**
     * {@inheritDoc}
     * 
     * The tensors this iterator loops over are guaranteed to be {@code IdShortVector}s.
     * 
     */
    Iterator<Tensor<Short>> iterator();
}
