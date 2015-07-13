package com.pb.sawdust.tensor.group;

import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.IndexFactory;
import com.pb.sawdust.tensor.read.TensorGroupReader;

/**
 * The {@code AutoKeyTensorGroup} ...
 *
 * @author crf <br/>
 *         Started Mar 3, 2010 12:43:50 PM
 */
public abstract class AutoKeyTensorGroup<T,I> extends StandardTensorGroup<T,I> {

    public AutoKeyTensorGroup(int[] dimensions) {
        super(dimensions);
    }

    public AutoKeyTensorGroup(TensorGroupReader<T,I> reader, TensorFactory tFactory, IndexFactory iFactory) {
        super(reader,tFactory,iFactory);
    }

    abstract public String formTensorKey(Tensor<?> tensor);
    abstract public String formIndexKey(Index<?> index);

    @Override
    public void addTensor(String tensorKey, Tensor<T> tensor) {
        String key = formTensorKey(tensor);
        if (!tensorKey.equals(key))
            throw new IllegalArgumentException(String.format("Explicit tensor key (%s) does not match tensor autokey (%s).",tensorKey,key));
        super.addTensor(tensorKey,tensor);
    }

    @Override
    public void addIndex(String indexKey, Index<I> index) {
        String key = formIndexKey(index);
        if (!indexKey.equals(key))
            throw new IllegalArgumentException(String.format("Explicit index key (%s) does not match index autokey (%s).",indexKey,key));
        super.addIndex(indexKey,index);
    }

    public void addTensor(Tensor<T> tensor) {
        super.addTensor(formTensorKey(tensor),tensor);
    }

    public void addIndex(Index<I> index) {
        super.addIndex(formIndexKey(index),index);
    }
}
