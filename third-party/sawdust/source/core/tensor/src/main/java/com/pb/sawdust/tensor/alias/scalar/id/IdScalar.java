package com.pb.sawdust.tensor.alias.scalar.id;

import com.pb.sawdust.tensor.alias.scalar.Scalar;
import com.pb.sawdust.tensor.decorators.id.size.IdD0Tensor;

/**
 * The {@code IdScalar} interface provides an alternative name for a 0-dimensional id tensor.
 *
 * @author crf <br/>
 *         Started: Jun 17, 2009 8:35:09 AM
 */
public interface IdScalar<T,I> extends IdD0Tensor<T,I>,Scalar<T> {
}
