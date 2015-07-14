package com.pb.sawdust.tensor.decorators.primitive.size;

import com.pb.sawdust.tensor.decorators.primitive.DoubleTensor;
import com.pb.sawdust.tensor.decorators.id.primitive.IdDoubleTensor;
import com.pb.sawdust.tensor.index.Index;

/**
 * The {@code DoubleD0TensorShell} class is a wrapper which sets a rank 0 (scalar) {@code DoubleTensor} as a
 * {@code D0Tensor} (or, more specifically, a {@code DoubleD0Tensor}).
 *
 * @author crf <br/>
 *         Started: Jun 25, 2009 2:16:12 PM
 */
public class DoubleD0TensorShell extends AbstractDoubleD0Tensor {
    private final DoubleTensor tensor;

    /**
     * Constructor specifying tensor to wrap.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @throws IllegalArgumentException if {@code tensor.size() != 0}.
     */
    public DoubleD0TensorShell(DoubleTensor tensor) {
        if (tensor.size() != 0)
            throw new IllegalArgumentException("Wrapped tensor must be of rank 0.");
        this.tensor = tensor;
    }

    public double getCell() {
        return tensor.getCell();
    }

    public void setCell(double value) {
        tensor.setCell(value);
    }

    public <I> IdDoubleTensor<I> getReferenceTensor(Index<I> index) {
        return tensor.getReferenceTensor(index);
    }
}
