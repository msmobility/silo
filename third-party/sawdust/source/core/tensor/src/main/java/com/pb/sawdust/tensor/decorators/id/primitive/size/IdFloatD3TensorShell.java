package com.pb.sawdust.tensor.decorators.id.primitive.size;

import com.pb.sawdust.tensor.decorators.primitive.size.FloatD3TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.size.FloatD3Tensor;
import com.pb.sawdust.tensor.decorators.primitive.size.FloatD2Tensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.BaseIndex;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.Tensor;


import java.util.List;
import java.util.Iterator;

/**
 * The {@code IdFloatD3TensorShell} class is a wrapper which sets a {@code FloatD3TensorShell} as an {@code IdTensor<I>} (or,
 * more specifically, an {@code IdFloatD3Tensor}).
 *
 * @author crf <br/>
 *         Started: Jan 16, 2009 9:47:43 AM
 *         Revised: Dec 14, 2009 12:35:26 PM
 */
public class IdFloatD3TensorShell<I> extends FloatD3TensorShell implements IdFloatD3Tensor<I> {
    private final FloatD3Tensor tensor;
    private final Index<I> index;

    IdFloatD3TensorShell(FloatD3Tensor tensor, Index<I> index) {
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
    public IdFloatD3TensorShell(FloatD3Tensor tensor) {
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
     * @throws IllegalArgumentException if {@code ids.size() != 3}, if the length of each array in {@code ids} 
     *                                  does not match its respective dimension's size in {@code tensor}, or if any array 
     *                                  in {@code ids} contains repeated elements.
     */
    @SafeVarargs
    @SuppressWarnings({"unchecked","varargs"})
    public IdFloatD3TensorShell(FloatD3Tensor tensor, I[] ... ids) {
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
     * @throws IllegalArgumentException if {@code ids.size() != 3}, if the length of each array in {@code ids} 
     *                                  does not match its respective dimension's size in {@code tensor}, or if any array 
     *                                  in {@code ids} contains repeated elements.
     */
    public IdFloatD3TensorShell(FloatD3Tensor tensor, List<List<I>> ids) {
        this(tensor,new BaseIndex<I>(tensor,ids));
    }

    public Index<I> getIndex() {
        return index;
    }

    public float getCellById(I d0id, I d1id, I d2id) {
        return tensor.getCell(index.getIndex(0,d0id),index.getIndex(1,d1id),index.getIndex(2,d2id));
    }

    public void setCellById(float value, I d0id, I d1id, I d2id) {
        tensor.setCell(value,index.getIndex(0,d0id),index.getIndex(1,d1id),index.getIndex(2,d2id));
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public Float getValueById(I ... ids) {
        TensorImplUtil.checkIndicesLength(this,ids);
        return getCellById(ids[0],ids[1],ids[2]);
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public float getCellById(I ... ids) {
        TensorImplUtil.checkIndicesLength(this,ids);
        return getCellById(ids[0],ids[1],ids[2]);
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public void setCellById(float value, I ... ids) {
        TensorImplUtil.checkIndicesLength(this,ids);
        setCellById(value,ids[0],ids[1],ids[2]);
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public void setValueById(Float value, I ... ids) {
        setCellById(value,ids);
    }

    public Float getValueById(I d0id, I d1id, I d2id) {
        return getCellById(d0id,d1id,d2id);
    }

    public void setValueById(Float value, I d0id, I d1id, I d2id) {
        setCellById(value,d0id,d1id,d2id);
    }

    public Iterator<Tensor<Float>> iterator() {
        return new Iterator<Tensor<Float>>() {
            private final Iterator<Tensor<Float>> iterator = IdFloatD3TensorShell.super.iterator();
            
            public boolean hasNext() {
                return iterator.hasNext();
            }

            public Tensor<Float> next() {
                return new IdFloatD2TensorShell<I>((FloatD2Tensor) iterator.next());
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
