package com.pb.sawdust.tensor.alias.matrix.primitive;

import com.pb.sawdust.tensor.decorators.primitive.size.DoubleD2Tensor;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code DoubleMatrix} interface provides an alternate name for 2-dimensional tensors holding {@code double}s.
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 9:22:03 AM
 */
public interface DoubleMatrix extends DoubleD2Tensor {   
    
    /**
     * {@inheritDoc}
     * 
     * The tensors this iterator loops over are guaranteed to be {@code DoubleVector}s.
     * 
     */
    Iterator<Tensor<Double>> iterator();
}
