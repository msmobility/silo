package com.pb.sawdust.tensor.decorators.size;

import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.TensorImplUtil;



/**
 * The {@code D8TensorShell} provides an basic shell implementation of {@code D8Tensor} which wraps a tensor with {@code 8} dimensions.
 *
 * @author crf <br/>
 *         Started: Oct 24, 2008 10:05:14 PM
 *         Revised: Dec 14, 2009 12:35:33 PM
 */
public class D8TensorShell<T> extends AbstractDnTensorShell<T> implements D8Tensor<T>{

    /**
     * Constructor specifying the tensor to wrap.
     *
     * @param tensor
     *        The 8-dimensional tensor to wrap.
     *
     * @throws IllegalArgumentException if {@code tensor.size{} != 8}.
     */
    public D8TensorShell(Tensor<T> tensor) {
        super(tensor);
    }

    protected int getSize() {
        return 8;
    }

    public T getValue(int ... indices) {
        TensorImplUtil.checkIndicesLength(this,indices);
        return getValue(indices[0],indices[1],indices[2],indices[3],indices[4],indices[5],indices[6],indices[7]);
    }

    public T getValue(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
        return tensor.getValue(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
    }

    public void setValue(T value, int ... indices) {
        TensorImplUtil.checkIndicesLength(this,indices);
        setValue(value,indices[0],indices[1],indices[2],indices[3],indices[4],indices[5],indices[6],indices[7]);
    }
    
    public void setValue(T value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
        tensor.setValue(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
    }
}
