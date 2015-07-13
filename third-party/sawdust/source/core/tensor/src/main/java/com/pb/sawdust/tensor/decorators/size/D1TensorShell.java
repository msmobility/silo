package com.pb.sawdust.tensor.decorators.size;

import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.alias.vector.Vector;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.decorators.id.IdTensor;

/**
 * The {@code D1TensorShell} provides an basic shell implementation of {@code Vector} which wraps a tensor with {@code 1} dimensions.
 *
 * @author crf <br/>
 *         Started: Oct 24, 2008 10:05:14 PM
 *         Revised: Dec 14, 2009 12:35:24 PM
 */
public class D1TensorShell<T> extends AbstractDnTensorShell<T> implements Vector<T>{

    /**
     * Constructor specifying the tensor to wrap.
     *
     * @param tensor
     *        The 1-dimensional tensor to wrap.
     *
     * @throws IllegalArgumentException if {@code tensor.size{} != 1}.
     */
    public D1TensorShell(Tensor<T> tensor) {
        super(tensor);
    }

    protected int getSize() {
        return 1;
    }

    public T getValue(int ... indices) {
        TensorImplUtil.checkIndicesLength(this,indices);
        return getValue(indices[0]);
    }

    public T getValue(int index) {
        return tensor.getValue(index);
    }

    public void setValue(T value, int ... indices) {
        TensorImplUtil.checkIndicesLength(this,indices);
        setValue(value,indices[0]);
    }
    
    public void setValue(T value,int index) {
        tensor.setValue(value,index);
    }
}
