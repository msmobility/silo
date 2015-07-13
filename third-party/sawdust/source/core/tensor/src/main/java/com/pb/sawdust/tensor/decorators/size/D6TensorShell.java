package com.pb.sawdust.tensor.decorators.size;

import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.TensorImplUtil;



/**
 * The {@code D6TensorShell} provides an basic shell implementation of {@code D6Tensor} which wraps a tensor with {@code 6} dimensions.
 *
 * @author crf <br/>
 *         Started: Oct 24, 2008 10:05:14 PM
 *         Revised: Dec 14, 2009 12:35:30 PM
 */
public class D6TensorShell<T> extends AbstractDnTensorShell<T> implements D6Tensor<T>{

    /**
     * Constructor specifying the tensor to wrap.
     *
     * @param tensor
     *        The 6-dimensional tensor to wrap.
     *
     * @throws IllegalArgumentException if {@code tensor.size{} != 6}.
     */
    public D6TensorShell(Tensor<T> tensor) {
        super(tensor);
    }

    protected int getSize() {
        return 6;
    }

    public T getValue(int ... indices) {
        TensorImplUtil.checkIndicesLength(this,indices);
        return getValue(indices[0],indices[1],indices[2],indices[3],indices[4],indices[5]);
    }

    public T getValue(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
        return tensor.getValue(d0index,d1index,d2index,d3index,d4index,d5index);
    }

    public void setValue(T value, int ... indices) {
        TensorImplUtil.checkIndicesLength(this,indices);
        setValue(value,indices[0],indices[1],indices[2],indices[3],indices[4],indices[5]);
    }
    
    public void setValue(T value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
        tensor.setValue(value,d0index,d1index,d2index,d3index,d4index,d5index);
    }
}
