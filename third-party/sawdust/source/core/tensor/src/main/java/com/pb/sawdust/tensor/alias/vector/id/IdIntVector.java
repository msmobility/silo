package com.pb.sawdust.tensor.alias.vector.id;

import com.pb.sawdust.tensor.alias.vector.primitive.IntVector;
import com.pb.sawdust.tensor.decorators.id.primitive.size.IdIntD1Tensor;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code IdIntVector} interface provides an alternate name for 1-dimensional id tensors holding {@code int}s.
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 9:22:03 AM
 */
public interface IdIntVector<I> extends IdIntD1Tensor<I>,IntVector {

    /**
     * {@inheritDoc}
     *
     * The tensors this iterator loops over are guaranteed to be {@code IdIntScalar}s.
     *
     */
    Iterator<Tensor<Integer>> iterator();
}
