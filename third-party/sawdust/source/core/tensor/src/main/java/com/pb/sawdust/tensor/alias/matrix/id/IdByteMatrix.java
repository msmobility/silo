package com.pb.sawdust.tensor.alias.matrix.id;

import com.pb.sawdust.tensor.alias.matrix.primitive.ByteMatrix;
import com.pb.sawdust.tensor.decorators.id.primitive.size.IdByteD2Tensor;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code IdByteMatrix} interface provides an alternate name for 2-dimensional id tensors holding {@code byte}s.
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 9:22:03 AM
 */
public interface IdByteMatrix<I> extends IdByteD2Tensor<I>,ByteMatrix {

    /**
     * {@inheritDoc}
     *
     * The tensors this iterator loops over are guaranteed to be {@code IdByteVector}s.
     *
     */
    Iterator<Tensor<Byte>> iterator();
}
