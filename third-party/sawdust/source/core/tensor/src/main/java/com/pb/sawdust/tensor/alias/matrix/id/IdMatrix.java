package com.pb.sawdust.tensor.alias.matrix.id;

import com.pb.sawdust.tensor.alias.matrix.Matrix;
import com.pb.sawdust.tensor.decorators.id.size.IdD2Tensor;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code IdMatrix} interface provides an alternate name for 2-dimensional id tensors.
 *
 * @author crf <br/>
 *         Started: Jun 19, 2009 4:08:09 PM
 */
public interface IdMatrix<T,I> extends IdD2Tensor<T,I>,Matrix<T> {

    /**
     * {@inheritDoc}
     *
     * The tensors this iterator loops over are guaranteed to be {@code IdVector<T,I>} instances.
     *
     */
    Iterator<Tensor<T>> iterator();
}
