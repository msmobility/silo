package com.pb.sawdust.tensor.write;

import com.pb.sawdust.tensor.Tensor;

/**
 * The {@code TensorWriter} interface provides a framework for writing tensors.  That is, implementing classes will provide
 * a means to capture the information held in a tensor outside of the realms of the {@code tensor} package and  sub-packages.
 * Whether "capturing" means outputting to disk, to memory, some other output stream, or something entirely different is
 * implementation specific. In general, most {@code TensorWriter} implementations should have complementing implementations
 * of {@code TensorReader}.
 *
 * @param <T>
 *        The type held by the tensor written by this writer.
 *
 * @author crf <br/>
 *         Started: Dec 5, 2009 2:45:57 PM
 */
public interface TensorWriter<T> {
    /**
     * Write a given tensor.
     *
     * @param tensor
     *        The tensor to write.
     */
    void writeTensor(Tensor<? extends T> tensor);
}
