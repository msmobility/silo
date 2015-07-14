package com.pb.sawdust.tensor.decorators.id.primitive.size;

import com.pb.sawdust.tensor.decorators.primitive.size.FloatD6TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.size.FloatD6Tensor;
import com.pb.sawdust.tensor.decorators.primitive.size.FloatD5Tensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.BaseIndex;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.Tensor;


import java.util.List;
import java.util.Iterator;

/**
 * The {@code IdFloatD6TensorShell} class is a wrapper which sets a {@code FloatD6TensorShell} as an {@code IdTensor<I>} (or,
 * more specifically, an {@code IdFloatD6Tensor}).
 *
 * @author crf <br/>
 *         Started: Jan 16, 2009 9:47:43 AM
 *         Revised: Dec 14, 2009 12:35:30 PM
 */
public class IdFloatD6TensorShell<I> extends FloatD6TensorShell implements IdFloatD6Tensor<I> {
    private final FloatD6Tensor tensor;
    private final Index<I> index;

    IdFloatD6TensorShell(FloatD6Tensor tensor, Index<I> index) {
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
    public IdFloatD6TensorShell(FloatD6Tensor tensor) {
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
     * @throws IllegalArgumentException if {@code ids.size() != 6}, if the length of each array in {@code ids} 
     *                                  does not match its respective dimension's size in {@code tensor}, or if any array 
     *                                  in {@code ids} contains repeated elements.
     */
    @SafeVarargs
    @SuppressWarnings({"unchecked","varargs"})
    public IdFloatD6TensorShell(FloatD6Tensor tensor, I[] ... ids) {
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
     * @throws IllegalArgumentException if {@code ids.size() != 6}, if the length of each array in {@code ids} 
     *                                  does not match its respective dimension's size in {@code tensor}, or if any array 
     *                                  in {@code ids} contains repeated elements.
     */
    public IdFloatD6TensorShell(FloatD6Tensor tensor, List<List<I>> ids) {
        this(tensor,new BaseIndex<I>(tensor,ids));
    }

    public Index<I> getIndex() {
        return index;
    }

    public float getCellById(I d0id, I d1id, I d2id, I d3id, I d4id, I d5id) {
        return tensor.getCell(index.getIndex(0,d0id),index.getIndex(1,d1id),index.getIndex(2,d2id),index.getIndex(3,d3id),index.getIndex(4,d4id),index.getIndex(5,d5id));
    }

    public void setCellById(float value, I d0id, I d1id, I d2id, I d3id, I d4id, I d5id) {
        tensor.setCell(value,index.getIndex(0,d0id),index.getIndex(1,d1id),index.getIndex(2,d2id),index.getIndex(3,d3id),index.getIndex(4,d4id),index.getIndex(5,d5id));
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public Float getValueById(I ... ids) {
        TensorImplUtil.checkIndicesLength(this,ids);
        return getCellById(ids[0],ids[1],ids[2],ids[3],ids[4],ids[5]);
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public float getCellById(I ... ids) {
        TensorImplUtil.checkIndicesLength(this,ids);
        return getCellById(ids[0],ids[1],ids[2],ids[3],ids[4],ids[5]);
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public void setCellById(float value, I ... ids) {
        TensorImplUtil.checkIndicesLength(this,ids);
        setCellById(value,ids[0],ids[1],ids[2],ids[3],ids[4],ids[5]);
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public void setValueById(Float value, I ... ids) {
        setCellById(value,ids);
    }

    public Float getValueById(I d0id, I d1id, I d2id, I d3id, I d4id, I d5id) {
        return getCellById(d0id,d1id,d2id,d3id,d4id,d5id);
    }

    public void setValueById(Float value, I d0id, I d1id, I d2id, I d3id, I d4id, I d5id) {
        setCellById(value,d0id,d1id,d2id,d3id,d4id,d5id);
    }

    public Iterator<Tensor<Float>> iterator() {
        return new Iterator<Tensor<Float>>() {
            private final Iterator<Tensor<Float>> iterator = IdFloatD6TensorShell.super.iterator();
            
            public boolean hasNext() {
                return iterator.hasNext();
            }

            public Tensor<Float> next() {
                return new IdFloatD5TensorShell<I>((FloatD5Tensor) iterator.next());
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
