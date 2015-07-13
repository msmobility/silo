package com.pb.sawdust.tensor.decorators.id.primitive.size;

import com.pb.sawdust.tensor.decorators.primitive.size.IntD1TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.size.IntD1Tensor;
import com.pb.sawdust.tensor.decorators.primitive.size.IntD0Tensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.BaseIndex;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.alias.vector.id.IdIntVector;

import java.util.List;
import java.util.Iterator;

/**
 * The {@code IdIntD1TensorShell} class is a wrapper which sets a {@code IntD1TensorShell} as an {@code IdTensor<I>} (or,
 * more specifically, an {@code IdIntVector}).
 *
 * @author crf <br/>
 *         Started: Jan 16, 2009 9:47:43 AM
 *         Revised: Dec 14, 2009 12:35:23 PM
 */
public class IdIntD1TensorShell<I> extends IntD1TensorShell implements IdIntVector<I> {
    private final IntD1Tensor tensor;
    private final Index<I> index;

    IdIntD1TensorShell(IntD1Tensor tensor, Index<I> index) {
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
    public IdIntD1TensorShell(IntD1Tensor tensor) {
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
     * @throws IllegalArgumentException if {@code ids.size() != 1}, if the length of each array in {@code ids} 
     *                                  does not match its respective dimension's size in {@code tensor}, or if any array 
     *                                  in {@code ids} contains repeated elements.
     */
    @SafeVarargs
    @SuppressWarnings({"unchecked","varargs"})
    public IdIntD1TensorShell(IntD1Tensor tensor, I[] ... ids) {
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
     * @throws IllegalArgumentException if {@code ids.size() != 1}, if the length of each array in {@code ids} 
     *                                  does not match its respective dimension's size in {@code tensor}, or if any array 
     *                                  in {@code ids} contains repeated elements.
     */
    public IdIntD1TensorShell(IntD1Tensor tensor, List<List<I>> ids) {
        this(tensor,new BaseIndex<I>(tensor,ids));
    }

    public Index<I> getIndex() {
        return index;
    }

    public int getCellById(I id) {
        return tensor.getCell(index.getIndex(0,id));
    }

    public void setCellById(int value, I id) {
        tensor.setCell(value,index.getIndex(0,id));
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public Integer getValueById(I ... ids) {
        TensorImplUtil.checkIndicesLength(this,ids);
        return getCellById(ids[0]);
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public int getCellById(I ... ids) {
        TensorImplUtil.checkIndicesLength(this,ids);
        return getCellById(ids[0]);
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public void setCellById(int value, I ... ids) {
        TensorImplUtil.checkIndicesLength(this,ids);
        setCellById(value,ids[0]);
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public void setValueById(Integer value, I ... ids) {
        setCellById(value,ids);
    }

    public Integer getValueById(I id) {
        return getCellById(id);
    }

    public void setValueById(Integer value, I id) {
        setCellById(value,id);
    }

    public Iterator<Tensor<Integer>> iterator() {
        return new Iterator<Tensor<Integer>>() {
            private final Iterator<Tensor<Integer>> iterator = IdIntD1TensorShell.super.iterator();
            
            public boolean hasNext() {
                return iterator.hasNext();
            }

            public Tensor<Integer> next() {
                return new IdIntD0TensorShell<I>((IntD0Tensor) iterator.next());
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
