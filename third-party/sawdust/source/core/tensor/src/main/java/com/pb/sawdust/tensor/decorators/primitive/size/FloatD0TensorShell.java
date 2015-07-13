package com.pb.sawdust.tensor.decorators.primitive.size;

import com.pb.sawdust.tensor.decorators.primitive.FloatTensor;
import com.pb.sawdust.tensor.decorators.id.primitive.IdFloatTensor;
import com.pb.sawdust.tensor.index.Index;

/**
 * The {@code FloatD0TensorShell} class is a wrapper which sets a rank 0 (scalar) {@code FloatTensor} as a
 * {@code D0Tensor} (or, more specifically, a {@code FloatD0Tensor}).
 *
 * @author crf <br/>
 *         Started: Jun 25, 2009 2:16:12 PM
 */
public class FloatD0TensorShell extends AbstractFloatD0Tensor {
    private final FloatTensor tensor;

    /**
     * Constructor specifying tensor to wrap.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @throws IllegalArgumentException if {@code tensor.size() != 0}.
     */
    public FloatD0TensorShell(FloatTensor tensor) {
        if (tensor.size() != 0)
            throw new IllegalArgumentException("Wrapped tensor must be of rank 0.");
        this.tensor = tensor;
    }

    public float getCell() {
        return tensor.getCell();
    }

    public void setCell(float value) {
        tensor.setCell(value);
    }

    public <I> IdFloatTensor<I> getReferenceTensor(Index<I> index) {
        return tensor.getReferenceTensor(index);
    }
}
