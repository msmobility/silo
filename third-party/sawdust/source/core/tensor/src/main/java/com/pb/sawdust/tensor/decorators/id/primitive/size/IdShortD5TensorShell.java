package com.pb.sawdust.tensor.decorators.id.primitive.size;

import com.pb.sawdust.tensor.decorators.primitive.size.ShortD5TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.size.ShortD5Tensor;
import com.pb.sawdust.tensor.decorators.primitive.size.ShortD4Tensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.BaseIndex;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.Tensor;


import java.util.List;
import java.util.Iterator;

/**
 * The {@code IdShortD5TensorShell} class is a wrapper which sets a {@code ShortD5TensorShell} as an {@code IdTensor<I>} (or,
 * more specifically, an {@code IdShortD5Tensor}).
 *
 * @author crf <br/>
 *         Started: Jan 16, 2009 9:47:43 AM
 *         Revised: Dec 14, 2009 12:35:28 PM
 */
public class IdShortD5TensorShell<I> extends ShortD5TensorShell implements IdShortD5Tensor<I> {
    private final ShortD5Tensor tensor;
    private final Index<I> index;

    IdShortD5TensorShell(ShortD5Tensor tensor, Index<I> index) {
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
    public IdShortD5TensorShell(ShortD5Tensor tensor) {
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
     * @throws IllegalArgumentException if {@code ids.size() != 5}, if the length of each array in {@code ids} 
     *                                  does not match its respective dimension's size in {@code tensor}, or if any array 
     *                                  in {@code ids} contains repeated elements.
     */
    @SafeVarargs
    @SuppressWarnings({"unchecked","varargs"})
    public IdShortD5TensorShell(ShortD5Tensor tensor, I[] ... ids) {
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
    public IdShortD5TensorShell(ShortD5Tensor tensor, List<List<I>> ids) {
        this(tensor,new BaseIndex<I>(tensor,ids));
    }

    public Index<I> getIndex() {
        return index;
    }

    public short getCellById(I d0id, I d1id, I d2id, I d3id, I d4id) {
        return tensor.getCell(index.getIndex(0,d0id),index.getIndex(1,d1id),index.getIndex(2,d2id),index.getIndex(3,d3id),index.getIndex(4,d4id));
    }

    public void setCellById(short value, I d0id, I d1id, I d2id, I d3id, I d4id) {
        tensor.setCell(value,index.getIndex(0,d0id),index.getIndex(1,d1id),index.getIndex(2,d2id),index.getIndex(3,d3id),index.getIndex(4,d4id));
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public Short getValueById(I ... ids) {
        TensorImplUtil.checkIndicesLength(this,ids);
        return getCellById(ids[0],ids[1],ids[2],ids[3],ids[4]);
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public short getCellById(I ... ids) {
        TensorImplUtil.checkIndicesLength(this,ids);
        return getCellById(ids[0],ids[1],ids[2],ids[3],ids[4]);
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public void setCellById(short value, I ... ids) {
        TensorImplUtil.checkIndicesLength(this,ids);
        setCellById(value,ids[0],ids[1],ids[2],ids[3],ids[4]);
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public void setValueById(Short value, I ... ids) {
        setCellById(value,ids);
    }

    public Short getValueById(I d0id, I d1id, I d2id, I d3id, I d4id) {
        return getCellById(d0id,d1id,d2id,d3id,d4id);
    }

    public void setValueById(Short value, I d0id, I d1id, I d2id, I d3id, I d4id) {
        setCellById(value,d0id,d1id,d2id,d3id,d4id);
    }

    public Iterator<Tensor<Short>> iterator() {
        return new Iterator<Tensor<Short>>() {
            private final Iterator<Tensor<Short>> iterator = IdShortD5TensorShell.super.iterator();
            
            public boolean hasNext() {
                return iterator.hasNext();
            }

            public Tensor<Short> next() {
                return new IdShortD4TensorShell<I>((ShortD4Tensor) iterator.next());
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
