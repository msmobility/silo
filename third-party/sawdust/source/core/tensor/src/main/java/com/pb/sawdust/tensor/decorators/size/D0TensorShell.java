package com.pb.sawdust.tensor.decorators.size;

import com.pb.sawdust.tensor.decorators.id.IdTensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.Tensor;

/**
 * The {@code D0TensorShell} class is a wrapper which sets a rank 0 (scalar) {@code Tensor} as a
 * {@code D0Tensor} (or, more specifically, a {@code D0Tensor}).
 *
 * @author crf <br/>
 *         Started: Jun 25, 2009 2:16:12 PM
 */
public class D0TensorShell<T> extends AbstractD0Tensor<T> {
    private final Tensor<T> tensor;

    /**
     * Constructor specifying tensor to wrap.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @throws IllegalArgumentException if {@code tensor.size() != 0}.
     */
    public D0TensorShell(Tensor<T> tensor) {
        if (tensor.size() != 0)
            throw new IllegalArgumentException("Wrapped tensor must be of rank 0.");
        this.tensor = tensor;
    }

    public T getValue() {
        return tensor.getValue();
    }

    public void setValue(T value) {
        tensor.setValue(value);
    }

    public <I> IdTensor<T,I> getReferenceTensor(Index<I> index) {
        return tensor.getReferenceTensor(index);
    }
}
