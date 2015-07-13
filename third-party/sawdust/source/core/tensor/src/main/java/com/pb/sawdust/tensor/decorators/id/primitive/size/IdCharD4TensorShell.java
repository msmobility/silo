package com.pb.sawdust.tensor.decorators.id.primitive.size;

import com.pb.sawdust.tensor.decorators.primitive.size.CharD4TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.size.CharD4Tensor;
import com.pb.sawdust.tensor.decorators.primitive.size.CharD3Tensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.BaseIndex;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.Tensor;


import java.util.List;
import java.util.Iterator;

/**
 * The {@code IdCharD4TensorShell} class is a wrapper which sets a {@code CharD4TensorShell} as an {@code IdTensor<I>} (or,
 * more specifically, an {@code IdCharD4Tensor}).
 *
 * @author crf <br/>
 *         Started: Jan 16, 2009 9:47:43 AM
 *         Revised: Dec 14, 2009 12:35:28 PM
 */
public class IdCharD4TensorShell<I> extends CharD4TensorShell implements IdCharD4Tensor<I> {
    private final CharD4Tensor tensor;
    private final Index<I> index;

    IdCharD4TensorShell(CharD4Tensor tensor, Index<I> index) {
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
    public IdCharD4TensorShell(CharD4Tensor tensor) {
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
     * @throws IllegalArgumentException if {@code ids.size() != 4}, if the length of each array in {@code ids} 
     *                                  does not match its respective dimension's size in {@code tensor}, or if any array 
     *                                  in {@code ids} contains repeated elements.
     */
    @SafeVarargs
    @SuppressWarnings({"unchecked","varargs"})
    public IdCharD4TensorShell(CharD4Tensor tensor, I[] ... ids) {
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
     * @throws IllegalArgumentException if {@code ids.size() != 4}, if the length of each array in {@code ids} 
     *                                  does not match its respective dimension's size in {@code tensor}, or if any array 
     *                                  in {@code ids} contains repeated elements.
     */
    public IdCharD4TensorShell(CharD4Tensor tensor, List<List<I>> ids) {
        this(tensor,new BaseIndex<I>(tensor,ids));
    }

    public Index<I> getIndex() {
        return index;
    }

    public char getCellById(I d0id, I d1id, I d2id, I d3id) {
        return tensor.getCell(index.getIndex(0,d0id),index.getIndex(1,d1id),index.getIndex(2,d2id),index.getIndex(3,d3id));
    }

    public void setCellById(char value, I d0id, I d1id, I d2id, I d3id) {
        tensor.setCell(value,index.getIndex(0,d0id),index.getIndex(1,d1id),index.getIndex(2,d2id),index.getIndex(3,d3id));
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public Character getValueById(I ... ids) {
        TensorImplUtil.checkIndicesLength(this,ids);
        return getCellById(ids[0],ids[1],ids[2],ids[3]);
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public char getCellById(I ... ids) {
        TensorImplUtil.checkIndicesLength(this,ids);
        return getCellById(ids[0],ids[1],ids[2],ids[3]);
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public void setCellById(char value, I ... ids) {
        TensorImplUtil.checkIndicesLength(this,ids);
        setCellById(value,ids[0],ids[1],ids[2],ids[3]);
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public void setValueById(Character value, I ... ids) {
        setCellById(value,ids);
    }

    public Character getValueById(I d0id, I d1id, I d2id, I d3id) {
        return getCellById(d0id,d1id,d2id,d3id);
    }

    public void setValueById(Character value, I d0id, I d1id, I d2id, I d3id) {
        setCellById(value,d0id,d1id,d2id,d3id);
    }

    public Iterator<Tensor<Character>> iterator() {
        return new Iterator<Tensor<Character>>() {
            private final Iterator<Tensor<Character>> iterator = IdCharD4TensorShell.super.iterator();
            
            public boolean hasNext() {
                return iterator.hasNext();
            }

            public Tensor<Character> next() {
                return new IdCharD3TensorShell<I>((CharD3Tensor) iterator.next());
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
