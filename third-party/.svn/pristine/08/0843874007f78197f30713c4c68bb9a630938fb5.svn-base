package com.pb.sawdust.tensor.decorators.size;

import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.TensorImplUtil;



/**
 * The {@code D4TensorShell} provides an basic shell implementation of {@code D4Tensor} which wraps a tensor with {@code 4} dimensions.
 *
 * @author crf <br/>
 *         Started: Oct 24, 2008 10:05:14 PM
 *         Revised: Dec 14, 2009 12:35:28 PM
 */
public class D4TensorShell<T> extends AbstractDnTensorShell<T> implements D4Tensor<T>{

    /**
     * Constructor specifying the tensor to wrap.
     *
     * @param tensor
     *        The 4-dimensional tensor to wrap.
     *
     * @throws IllegalArgumentException if {@code tensor.size{} != 4}.
     */
    public D4TensorShell(Tensor<T> tensor) {
        super(tensor);
    }

    protected int getSize() {
        return 4;
    }

    public T getValue(int ... indices) {
        TensorImplUtil.checkIndicesLength(this,indices);
        return getValue(indices[0],indices[1],indices[2],indices[3]);
    }

    public T getValue(int d0index, int d1index, int d2index, int d3index) {
        return tensor.getValue(d0index,d1index,d2index,d3index);
    }

    public void setValue(T value, int ... indices) {
        TensorImplUtil.checkIndicesLength(this,indices);
        setValue(value,indices[0],indices[1],indices[2],indices[3]);
    }
    
    public void setValue(T value,int d0index, int d1index, int d2index, int d3index) {
        tensor.setValue(value,d0index,d1index,d2index,d3index);
    }
}
