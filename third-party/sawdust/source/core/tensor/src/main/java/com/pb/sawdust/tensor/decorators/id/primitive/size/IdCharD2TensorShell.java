package com.pb.sawdust.tensor.decorators.id.primitive.size;

import com.pb.sawdust.tensor.decorators.primitive.size.CharD2TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.size.CharD2Tensor;
import com.pb.sawdust.tensor.decorators.primitive.size.CharD1Tensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.BaseIndex;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.alias.matrix.id.IdCharMatrix;

import java.util.List;
import java.util.Iterator;

/**
 * The {@code IdCharD2TensorShell} class is a wrapper which sets a {@code CharD2TensorShell} as an {@code IdTensor<I>} (or,
 * more specifically, an {@code IdCharMatrix}).
 *
 * @author crf <br/>
 *         Started: Jan 16, 2009 9:47:43 AM
 *         Revised: Dec 14, 2009 12:35:25 PM
 */
public class IdCharD2TensorShell<I> extends CharD2TensorShell implements IdCharMatrix<I> {
    private final CharD2Tensor tensor;
    private final Index<I> index;

    IdCharD2TensorShell(CharD2Tensor tensor, Index<I> index) {
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
    public IdCharD2TensorShell(CharD2Tensor tensor) {
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
     * @throws IllegalArgumentException if {@code ids.size() != 2}, if the length of each array in {@code ids} 
     *                                  does not match its respective dimension's size in {@code tensor}, or if any array 
     *                                  in {@code ids} contains repeated elements.
     */
    @SafeVarargs
    @SuppressWarnings({"unchecked","varargs"})
    public IdCharD2TensorShell(CharD2Tensor tensor, I[] ... ids) {
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
     * @throws IllegalArgumentException if {@code ids.size() != 2}, if the length of each array in {@code ids} 
     *                                  does not match its respective dimension's size in {@code tensor}, or if any array 
     *                                  in {@code ids} contains repeated elements.
     */
    public IdCharD2TensorShell(CharD2Tensor tensor, List<List<I>> ids) {
        this(tensor,new BaseIndex<I>(tensor,ids));
    }

    public Index<I> getIndex() {
        return index;
    }

    public char getCellById(I d0id, I d1id) {
        return tensor.getCell(index.getIndex(0,d0id),index.getIndex(1,d1id));
    }

    public void setCellById(char value, I d0id, I d1id) {
        tensor.setCell(value,index.getIndex(0,d0id),index.getIndex(1,d1id));
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public Character getValueById(I ... ids) {
        TensorImplUtil.checkIndicesLength(this,ids);
        return getCellById(ids[0],ids[1]);
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public char getCellById(I ... ids) {
        TensorImplUtil.checkIndicesLength(this,ids);
        return getCellById(ids[0],ids[1]);
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public void setCellById(char value, I ... ids) {
        TensorImplUtil.checkIndicesLength(this,ids);
        setCellById(value,ids[0],ids[1]);
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public void setValueById(Character value, I ... ids) {
        setCellById(value,ids);
    }

    public Character getValueById(I d0id, I d1id) {
        return getCellById(d0id,d1id);
    }

    public void setValueById(Character value, I d0id, I d1id) {
        setCellById(value,d0id,d1id);
    }

    public Iterator<Tensor<Character>> iterator() {
        return new Iterator<Tensor<Character>>() {
            private final Iterator<Tensor<Character>> iterator = IdCharD2TensorShell.super.iterator();
            
            public boolean hasNext() {
                return iterator.hasNext();
            }

            public Tensor<Character> next() {
                return new IdCharD1TensorShell<I>((CharD1Tensor) iterator.next());
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
