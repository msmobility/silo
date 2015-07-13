package com.pb.sawdust.tensor.alias.matrix.primitive;

import com.pb.sawdust.tensor.decorators.primitive.size.ByteD2Tensor;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code ByteMatrix} interface provides an alternate name for 2-dimensional tensors holding {@code byte}s.
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 9:22:03 AM
 */
public interface ByteMatrix extends ByteD2Tensor {   

    /**
     * {@inheritDoc}
     *
     * The tensors this iterator loops over are guaranteed to be {@code ByteVector}s.
     *
     */
    Iterator<Tensor<Byte>> iterator();
}
