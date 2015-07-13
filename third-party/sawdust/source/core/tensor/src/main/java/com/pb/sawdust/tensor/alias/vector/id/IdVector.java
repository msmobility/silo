package com.pb.sawdust.tensor.alias.vector.id;

import com.pb.sawdust.tensor.alias.vector.Vector;
import com.pb.sawdust.tensor.decorators.id.size.IdD1Tensor;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code IdVector} interface provides an alternate name for 1-dimensional id tensors.
 *
 * @author crf <br/>
 *         Started: Jun 19, 2009 4:10:02 PM
 */
public interface IdVector<T,I> extends IdD1Tensor<T,I>,Vector<T> {

    /**
     * {@inheritDoc}
     *
     * The tensors this iterator loops over are guaranteed to be {@code IdScalar<T,I>} instances.
     *
     */
    Iterator<Tensor<T>> iterator();
}
