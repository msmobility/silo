package com.pb.sawdust.tensor.decorators.id.size;

import com.pb.sawdust.tensor.decorators.size.D5TensorShell;
import com.pb.sawdust.tensor.decorators.size.D4Tensor;
import com.pb.sawdust.tensor.decorators.size.D5Tensor;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.BaseIndex;


import java.util.List;
import java.util.Iterator;


/**
 * The {@code IdD5TensorShell} class is a wrapper which sets a {@code D5Tensor} as an {@code IdTensor<I>} (or,
 * more specifically, an {@code IdD5Tensor}).
 *
 * @author crf <br/>
 *         Started: Jan 16, 2009 8:53:36 AM
 *         Revised: Dec 14, 2009 12:35:29 PM
 */
public class IdD5TensorShell<T,I> extends D5TensorShell<T> implements IdD5Tensor<T,I> {
    private final D5Tensor<T> tensor;
    private final Index<I> index;

    IdD5TensorShell(D5Tensor<T> tensor, Index<I> index) {
        super(tensor);
        this.tensor = tensor;
        this.index = index;
    }  

    /**
     * Constructor which will wrap a tensor, using its current index for the ids. Note that this constructor is
     * inherently unsafe, as there is no way to check that the type of the tensor's index matches the type declared
     * when this constructor is called. Therefore, it should be used with care or in situations where the index type
     * can be verified.
     *
     * @param tensor
     *        The tensor to wrap.
     */
    @SuppressWarnings("unchecked") //warning is duly noted and clearly stated in the documentation
    public IdD5TensorShell(D5Tensor<T> tensor) {
        this(tensor,(Index<I>) tensor.getIndex());
    }

    /**
     * Constructor specifying the tensor to wrap, as well as the ids to use to reference the indices.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @param ids
     *        The ids to reference the indices with.
     *
     * @throws IllegalArgumentException if {@code ids.length != 5}, if the length of each array in {@code ids} 
     *                                  does not match its respective dimension's size in {@code tensor}, or if any array 
     *                                  in {@code ids} contains repeated elements.
     */
    @SafeVarargs
    @SuppressWarnings({"unchecked","varargs"})
    public IdD5TensorShell(D5Tensor<T> tensor, I[] ... ids) {
        this(tensor,new BaseIndex<I>(tensor,ids));
    } 

    /**
     * Constructor specifying the tensor to wrap, as well as the ids to use to reference the indices.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @param ids
     *        The ids to reference the indices with.
     *
     * @throws IllegalArgumentException if {@code ids.size() != 5}, if the length of each array in {@code ids} 
     *                                  does not match its respective dimension's size in {@code tensor}, or if any array 
     *                                  in {@code ids} contains repeated elements.
     */
    public IdD5TensorShell(D5Tensor<T> tensor, List<List<I>> ids) {
        this(tensor,new BaseIndex<I>(tensor,ids));
    }

    public Index<I> getIndex() {
        return index;
    }

    public T getValueById(I d0id, I d1id, I d2id, I d3id, I d4id) {
        return tensor.getValue(index.getIndex(0,d0id),index.getIndex(1,d1id),index.getIndex(2,d2id),index.getIndex(3,d3id),index.getIndex(4,d4id));
    }

    public void setValueById(T value, I d0id, I d1id, I d2id, I d3id, I d4id) {
        tensor.setValue(value,index.getIndex(0,d0id),index.getIndex(1,d1id),index.getIndex(2,d2id),index.getIndex(3,d3id),index.getIndex(4,d4id));
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public T getValueById(I ... ids) {
        TensorImplUtil.checkIndicesLength(this,ids);
        return getValueById(ids[0],ids[1],ids[2],ids[3],ids[4]);
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public void setValueById(T value, I ... ids) {
        TensorImplUtil.checkIndicesLength(this,ids);
        setValueById(value,ids[0],ids[1],ids[2],ids[3],ids[4]);
    }

    public Iterator<Tensor<T>> iterator() {
        return new Iterator<Tensor<T>>() {
            private final Iterator<Tensor<T>> iterator = IdD5TensorShell.super.iterator();
            
            public boolean hasNext() {
                return iterator.hasNext();
            }

            public Tensor<T> next() {
                return new IdD4TensorShell<T,I>((D4Tensor<T>) iterator.next());
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
