package com.pb.sawdust.tensor.decorators.id.primitive.size;

import com.pb.sawdust.tensor.decorators.primitive.size.IntD9TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.size.IntD9Tensor;
import com.pb.sawdust.tensor.decorators.primitive.size.IntD8Tensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.BaseIndex;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.Tensor;


import java.util.List;
import java.util.Iterator;

/**
 * The {@code IdIntD9TensorShell} class is a wrapper which sets a {@code IntD9TensorShell} as an {@code IdTensor<I>} (or,
 * more specifically, an {@code IdIntD9Tensor}).
 *
 * @author crf <br/>
 *         Started: Jan 16, 2009 9:47:43 AM
 *         Revised: Dec 14, 2009 12:35:34 PM
 */
public class IdIntD9TensorShell<I> extends IntD9TensorShell implements IdIntD9Tensor<I> {
    private final IntD9Tensor tensor;
    private final Index<I> index;

    IdIntD9TensorShell(IntD9Tensor tensor, Index<I> index) {
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
    public IdIntD9TensorShell(IntD9Tensor tensor) {
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
     * @throws IllegalArgumentException if {@code ids.size() != 9}, if the length of each array in {@code ids} 
     *                                  does not match its respective dimension's size in {@code tensor}, or if any array 
     *                                  in {@code ids} contains repeated elements.
     */
    @SafeVarargs
    @SuppressWarnings({"unchecked","varargs"})
    public IdIntD9TensorShell(IntD9Tensor tensor, I[] ... ids) {
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
     * @throws IllegalArgumentException if {@code ids.size() != 9}, if the length of each array in {@code ids} 
     *                                  does not match its respective dimension's size in {@code tensor}, or if any array 
     *                                  in {@code ids} contains repeated elements.
     */
    public IdIntD9TensorShell(IntD9Tensor tensor, List<List<I>> ids) {
        this(tensor,new BaseIndex<I>(tensor,ids));
    }

    public Index<I> getIndex() {
        return index;
    }

    public int getCellById(I d0id, I d1id, I d2id, I d3id, I d4id, I d5id, I d6id, I d7id, I d8id) {
        return tensor.getCell(index.getIndex(0,d0id),index.getIndex(1,d1id),index.getIndex(2,d2id),index.getIndex(3,d3id),index.getIndex(4,d4id),index.getIndex(5,d5id),index.getIndex(6,d6id),index.getIndex(7,d7id),index.getIndex(8,d8id));
    }

    public void setCellById(int value, I d0id, I d1id, I d2id, I d3id, I d4id, I d5id, I d6id, I d7id, I d8id) {
        tensor.setCell(value,index.getIndex(0,d0id),index.getIndex(1,d1id),index.getIndex(2,d2id),index.getIndex(3,d3id),index.getIndex(4,d4id),index.getIndex(5,d5id),index.getIndex(6,d6id),index.getIndex(7,d7id),index.getIndex(8,d8id));
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public Integer getValueById(I ... ids) {
        TensorImplUtil.checkIndicesLength(this,ids);
        return getCellById(ids[0],ids[1],ids[2],ids[3],ids[4],ids[5],ids[6],ids[7],ids[8]);
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public int getCellById(I ... ids) {
        TensorImplUtil.checkIndicesLength(this,ids);
        return getCellById(ids[0],ids[1],ids[2],ids[3],ids[4],ids[5],ids[6],ids[7],ids[8]);
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public void setCellById(int value, I ... ids) {
        TensorImplUtil.checkIndicesLength(this,ids);
        setCellById(value,ids[0],ids[1],ids[2],ids[3],ids[4],ids[5],ids[6],ids[7],ids[8]);
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public void setValueById(Integer value, I ... ids) {
        setCellById(value,ids);
    }

    public Integer getValueById(I d0id, I d1id, I d2id, I d3id, I d4id, I d5id, I d6id, I d7id, I d8id) {
        return getCellById(d0id,d1id,d2id,d3id,d4id,d5id,d6id,d7id,d8id);
    }

    public void setValueById(Integer value, I d0id, I d1id, I d2id, I d3id, I d4id, I d5id, I d6id, I d7id, I d8id) {
        setCellById(value,d0id,d1id,d2id,d3id,d4id,d5id,d6id,d7id,d8id);
    }

    public Iterator<Tensor<Integer>> iterator() {
        return new Iterator<Tensor<Integer>>() {
            private final Iterator<Tensor<Integer>> iterator = IdIntD9TensorShell.super.iterator();
            
            public boolean hasNext() {
                return iterator.hasNext();
            }

            public Tensor<Integer> next() {
                return new IdIntD8TensorShell<I>((IntD8Tensor) iterator.next());
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
