package com.pb.sawdust.tensor.alias.vector.id;

import com.pb.sawdust.tensor.alias.vector.primitive.DoubleVector;
import com.pb.sawdust.tensor.decorators.id.primitive.size.IdDoubleD1Tensor;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code IdDoubleVector} interface provides an alternate name for 1-dimensional id tensors holding {@code double}s.
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 9:22:03 AM
 */
public interface IdDoubleVector<I> extends IdDoubleD1Tensor<I>,DoubleVector {
    
    /**
     * {@inheritDoc}
     * 
     * The tensors this iterator loops over are guaranteed to be {@code IdDoubleScalar}s.
     * 
     */
    Iterator<Tensor<Double>> iterator();
}
