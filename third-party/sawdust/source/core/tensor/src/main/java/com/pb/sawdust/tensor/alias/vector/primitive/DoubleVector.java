package com.pb.sawdust.tensor.alias.vector.primitive;

import com.pb.sawdust.tensor.decorators.primitive.size.DoubleD1Tensor;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code IdDoubleVector} interface provides an alternate name for 1-dimensional tensors holding {@code double}s.
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 9:22:03 AM
 */
public interface DoubleVector extends DoubleD1Tensor {   
    
    /**
     * {@inheritDoc}
     * 
     * The tensors this iterator loops over are guaranteed to be {@code DoubleScalar}s.
     * 
     */
    Iterator<Tensor<Double>> iterator();
}
