package com.pb.sawdust.model.builder;

import com.pb.sawdust.tensor.decorators.primitive.DoubleTensor;

/**
 * The {@code TensorSource} ...
 *
 * @author crf
 *         Started 6/5/12 9:13 AM
 */
public interface TensorSource {
    DoubleTensor getTensor();
}
