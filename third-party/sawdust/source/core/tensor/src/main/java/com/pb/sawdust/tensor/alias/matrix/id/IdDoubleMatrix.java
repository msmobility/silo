package com.pb.sawdust.tensor.alias.matrix.id;

import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;
import com.pb.sawdust.tensor.decorators.id.primitive.size.IdDoubleD2Tensor;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code IdDoubleMatrix} interface provides an alternate name for 2-dimensional id tensors holding {@code double}s.
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 9:22:03 AM
 */
public interface IdDoubleMatrix<I> extends IdDoubleD2Tensor<I>,DoubleMatrix {
    
    /**
     * {@inheritDoc}
     * 
     * The tensors this iterator loops over are guaranteed to be {@code IdDoubleVector}s.
     * 
     */
    Iterator<Tensor<Double>> iterator();
}
