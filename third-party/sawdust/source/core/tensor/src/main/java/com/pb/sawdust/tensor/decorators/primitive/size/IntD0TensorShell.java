package com.pb.sawdust.tensor.decorators.primitive.size;

import com.pb.sawdust.tensor.decorators.primitive.IntTensor;
import com.pb.sawdust.tensor.decorators.id.primitive.IdIntTensor;
import com.pb.sawdust.tensor.index.Index;

/**
 * The {@code IntD0TensorShell} class is a wrapper which sets a rank 0 (scalar) {@code IntTensor} as a
 * {@code D0Tensor} (or, more specifically, a {@code IntD0Tensor}).
 *
 * @author crf <br/>
 *         Started: Jun 25, 2009 2:16:12 PM
 */
public class IntD0TensorShell extends AbstractIntD0Tensor {
    private final IntTensor tensor;

    /**
     * Constructor specifying tensor to wrap.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @throws IllegalArgumentException if {@code tensor.size() != 0}.
     */
    public IntD0TensorShell(IntTensor tensor) {
        if (tensor.size() != 0)
            throw new IllegalArgumentException("Wrapped tensor must be of rank 0.");
        this.tensor = tensor;
    }

    public int getCell() {
        return tensor.getCell();
    }

    public void setCell(int value) {
        tensor.setCell(value);
    }

    public <I> IdIntTensor<I> getReferenceTensor(Index<I> index) {
        return tensor.getReferenceTensor(index);
    }
}
