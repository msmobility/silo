package com.pb.sawdust.tensor.alias.matrix;

import com.pb.sawdust.tensor.decorators.size.D2Tensor;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code Matrix} interface provides an alternate name for 2-dimensional tensors.
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 9:10:45 AM
 */
public interface Matrix<T> extends D2Tensor<T> {


    /**
     * {@inheritDoc}
     *
     * The tensors this iterator loops over are guaranteed to be {@code Vector<T>} instances.
     *
     */
    Iterator<Tensor<T>> iterator();
}
