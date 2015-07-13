package com.pb.sawdust.tensor.decorators.id.primitive;

import com.pb.sawdust.tensor.decorators.primitive.AbstractCharTensor;
import com.pb.sawdust.tensor.decorators.primitive.CharTensor;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.BaseIndex;

import java.util.Iterator;
import java.util.List;

/**
 * The {@code IdCharTensorShell} class is a wrapper which sets a {@code CharTensor} as an {@code IdTensor<I>} (or,
 * more specifically, an {@code IdCharTensor}).
 * 
 * @author crf <br/>
 *         Started: Jan 15, 2009 11:25:11 PM
 *         Revised: Dec 14, 2009 12:35:34 PM
 */
public class IdCharTensorShell<I> extends AbstractCharTensor implements IdCharTensor<I> {
    private final CharTensor tensor;
    private final Index<I> index;

    private IdCharTensorShell(CharTensor tensor, Index<I> index) {
        super(index.getDimensions());
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
    public IdCharTensorShell(CharTensor tensor) {
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
     * @throws IllegalArgumentException if {@code ids.length != this.size()}, if the length of each array in {@code ids} 
     *                                  does not match its respective dimension's size in {@code tensor}, or if any array 
     *                                  in {@code ids} contains repeated elements.
     */
    @SafeVarargs
    @SuppressWarnings({"unchecked","varargs"})
    public IdCharTensorShell(CharTensor tensor, I[] ... ids) {
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
     * @throws IllegalArgumentException if {@code ids.size() != this.size()}, if the length of each array in {@code ids} 
     *                                  does not match its respective dimension's size in {@code tensor}, or if any array 
     *                                  in {@code ids} contains repeated elements.
     */
    public IdCharTensorShell(CharTensor tensor, List<List<I>> ids) {
        this(tensor,new BaseIndex<I>(tensor,ids));
    }

    public Index<I> getIndex() {
        return index;
    }

    public char getCell(int ... indices) {
        return tensor.getCell(indices);
    }

    public void setCell(char value, int ... indices) {
        tensor.setCell(value,indices);
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public char getCellById(I ... ids) {
        return getCell(index.getIndices(ids));
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public void setCellById(char value, I ... ids) {
        setCell(value,index.getIndices(ids));
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public Character getValueById(I... ids) {
        return getCellById(ids);
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public void setValueById(Character value, I ... ids) {
        setCellById(value,ids);
    }

    public Iterator<Tensor<Character>> iterator() {
        return new Iterator<Tensor<Character>>() {
            private final Iterator<Tensor<Character>> it = IdCharTensorShell.super.iterator();

            public boolean hasNext() {
                return it.hasNext();
            }

            public Tensor<Character> next() {
                return new IdCharTensorShell<I>((CharTensor) it.next());
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
