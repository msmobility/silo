package com.pb.sawdust.tensor.decorators.id;

import com.pb.sawdust.tensor.TensorTest;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.index.BaseIndex;
import static com.pb.sawdust.util.Range.range;
import com.pb.sawdust.util.array.TypeSafeArray;
import com.pb.sawdust.util.array.ArrayUtil;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;


import java.util.List;

/**
 * @author crf <br/>
 *         Started: Jan 25, 2009 12:19:25 PM
 */
public abstract class IdTensorTest<T,I> extends TensorTest<T> {
    protected IdTensor<T,I> idTensor;
    protected List<List<I>> ids;


    protected abstract IdTensor<T,I> getIdTensor(TypeSafeArray<T> data, List<List<I>> ids);
    protected abstract List<List<I>> getIds(int ... dimensions);

    protected Tensor<T> getTensor(TypeSafeArray<T> data) {
        ids = getIds(ArrayUtil.getDimensions(data.getArray()));
        return getIdTensor(data,ids);
    }

    @Before
    @SuppressWarnings("unchecked")
    public void beforeTest() {
        super.beforeTest();
        idTensor = (IdTensor<T,I>) tensor;
    }

    @SuppressWarnings("unchecked") //only an Object[], but ok to be an I[]
    protected I[] getIdsFromIndices(int ... indices) {
        Object[] id = new Object[indices.length];
        for (int i : range(indices.length))
            id[i] = ids.get(i).get(indices[i]);
        return (I[]) id;
    }

    @Test
    public void testGetValueById() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        assertEquals(data.getValue(index),idTensor.getValueById(getIdsFromIndices(index)));
    }

    @Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("unchecked") //only an Object[], but ok to be an I[]
    public void testGetValueByIdTooFewIds() {
        Object[] id = new Object[dimensions.length-1];
        for (int i : range(id.length))
            id[i] = ids.get(i).get(0);
        idTensor.getValueById((I[]) id);
    }

    @Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("unchecked") //only an Object[], but ok to be an I[]
    public void testGetValueByIdTooManyIds() {
        Object[] id = new Object[dimensions.length+1];
        for (int i : range(dimensions.length))
            id[i] = ids.get(i).get(0);
        id[dimensions.length] = ids.size() > 0 ? ids.get(0).get(0) : new Object();
        idTensor.getValueById((I[]) id);
    }

    @Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("unchecked") //only an Object[], but ok to be an I[]
    public void testGetValueByIdBadId() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        I[] id = getIdsFromIndices(index);
        id[0] = (I) new Object();
        idTensor.getValueById(id);
    }

    @Test
    public void testSetValueById() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        T value = getRandomElement();
        idTensor.setValueById(value,getIdsFromIndices(index));
        assertEquals(value,idTensor.getValue(index));
    }

    @Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("unchecked") //only an Object[], but ok to be an I[]
    public void testSetValueByIdTooFewIds() {
        Object[] id = new Object[dimensions.length-1];
        for (int i : range(id.length))
            id[i] = ids.get(i).get(0);
        idTensor.setValueById(getRandomElement(),(I[]) id);
    }

    @Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("unchecked") //only an Object[], but ok to be an I[]
    public void testSetValueByIdTooManyIds() {
        Object[] id = new Object[dimensions.length+1];
        for (int i : range(dimensions.length))
            id[i] = ids.get(i).get(0);
        id[dimensions.length] = ids.size() > 0 ? ids.get(0).get(0) : new Object();
        idTensor.setValueById(getRandomElement(),(I[]) id);
    }

    @Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("unchecked") //only an Object[], but ok to be an I[]
    public void testSetValueByIdBadId() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        I[] id = getIdsFromIndices(index);
        id[0] = (I) new Object();
        idTensor.setValueById(getRandomElement(),id);
    }

    public void testGetIndex() {
        assertEquals(new BaseIndex<I>(ids),idTensor.getIndex());
    }

    public void testIndexIds() {
        assertEquals(ids,idTensor.getIndex().getIndexIds());
    }

    @Test
    public void testIteratorType() {
        for (Tensor<T> t : tensor)
            assertTrue(t instanceof IdTensor);
    }
}
