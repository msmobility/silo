package com.pb.sawdust.tensor.alias.vector.id;

import com.pb.sawdust.tensor.alias.vector.primitive.ByteVector;
import com.pb.sawdust.tensor.decorators.id.primitive.size.IdByteD1Tensor;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code IdByteVector} interface provides an alternate name for 1-dimensional id tensors holding {@code byte}s.
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 9:22:03 AM
 */
public interface IdByteVector<I> extends IdByteD1Tensor<I>,ByteVector {

    /**
     * {@inheritDoc}
     *
     * The tensors this iterator loops over are guaranteed to be {@code IdByteScalar}s.
     *
     */
    Iterator<Tensor<Byte>> iterator();
}
