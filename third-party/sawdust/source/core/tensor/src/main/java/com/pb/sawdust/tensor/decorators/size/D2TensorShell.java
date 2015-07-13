package com.pb.sawdust.tensor.decorators.size;

import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.alias.matrix.Matrix;


/**
 * The {@code D2TensorShell} provides an basic shell implementation of {@code Matrix} which wraps a tensor with {@code 2} dimensions.
 *
 * @author crf <br/>
 *         Started: Oct 24, 2008 10:05:14 PM
 *         Revised: Dec 14, 2009 12:35:25 PM
 */
public class D2TensorShell<T> extends AbstractDnTensorShell<T> implements Matrix<T>{

    /**
     * Constructor specifying the tensor to wrap.
     *
     * @param tensor
     *        The 2-dimensional tensor to wrap.
     *
     * @throws IllegalArgumentException if {@code tensor.size{} != 2}.
     */
    public D2TensorShell(Tensor<T> tensor) {
        super(tensor);
    }

    protected int getSize() {
        return 2;
    }

    public T getValue(int ... indices) {
        TensorImplUtil.checkIndicesLength(this,indices);
        return getValue(indices[0],indices[1]);
    }

    public T getValue(int d0index, int d1index) {
        return tensor.getValue(d0index,d1index);
    }

    public void setValue(T value, int ... indices) {
        TensorImplUtil.checkIndicesLength(this,indices);
        setValue(value,indices[0],indices[1]);
    }
    
    public void setValue(T value,int d0index, int d1index) {
        tensor.setValue(value,d0index,d1index);
    }
}
