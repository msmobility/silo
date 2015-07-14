package com.pb.sawdust.tensor.decorators.id.primitive;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import com.pb.sawdust.tensor.decorators.id.IdTensorTest;
import com.pb.sawdust.tensor.Tensor;
import static com.pb.sawdust.util.Range.range;
import com.pb.sawdust.util.array.LongTypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArrayFactory;
import com.pb.sawdust.util.JavaType;

/**
 * @author crf <br/>
 *         Started: Jul 23, 2009 6:42:35 PM
 */
public abstract class IdLongTensorTest<I>  extends IdTensorTest<Long,I> {
    protected IdLongTensor<I> pTensor;
    protected LongTypeSafeArray pData;

    @Before
    @SuppressWarnings("unchecked") //cast is ok
    public void beforeTest() {
        super.beforeTest();
        pTensor = (IdLongTensor<I>) tensor;
        pData = (LongTypeSafeArray) data;
    }

    protected JavaType getJavaType() {
        return JavaType.LONG;
    }
       
    @Test
    public void testGetCellById() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        assertEquals(pData.get(index),pTensor.getCellById(getIdsFromIndices(index)));
    }

    @Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("unchecked") //only an Object[], but ok to be an I[]
    public void testGetCellByIdTooFewIds() {
        Object[] id = new Object[dimensions.length-1];
        for (int i : range(id.length))
            id[i] = ids.get(i).get(0);
        pTensor.getCellById((I[]) id);
    }

    @Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("unchecked") //only an Object[], but ok to be an I[]
    public void testGetCellByIdTooManyIds() {
        Object[] id = new Object[dimensions.length+1];
        for (int i : range(dimensions.length))
            id[i] = ids.get(i).get(0);
        id[dimensions.length] = ids.size() > 0 ? ids.get(0).get(0) : new Object();
        pTensor.getCellById((I[]) id);
    }

    @Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("unchecked") //only an Object[], but ok to be an I[]
    public void testGetCellByIdBadId() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        I[] id = getIdsFromIndices(index);
        id[0] = (I) new Object();
        pTensor.getCellById(id);
    }

    @Test
    public void testSetCellById() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        long value = getRandomElement();
        pTensor.setCellById(value,getIdsFromIndices(index));
        assertEquals(value,pTensor.getCell(index));
    }

    @Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("unchecked") //only an Object[], but ok to be an I[]
    public void testSetCellByIdTooFewIds() {
        Object[] id = new Object[dimensions.length-1];
        for (int i : range(id.length))
            id[i] = ids.get(i).get(0);
        pTensor.setCellById(getRandomElement(),(I[]) id);
    }

    @Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("unchecked") //only an Object[], but ok to be an I[]
    public void testSetCellByIdTooManyIds() {
        Object[] id = new Object[dimensions.length+1];
        for (int i : range(dimensions.length))
            id[i] = ids.get(i).get(0);
        id[dimensions.length] = ids.size() > 0 ? ids.get(0).get(0) : new Object();
        pTensor.setCellById(getRandomElement(),(I[]) id);
    }

    @Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("unchecked") //only an Object[], but ok to be an I[]
    public void testSetCellByIdBadId() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        I[] id = getIdsFromIndices(index);
        id[0] = (I) new Object();
        pTensor.setCellById(getRandomElement(),id);
    }

    @Test
    public void testIteratorType() {
        for (Tensor<Long> t : tensor)
            assertTrue(t instanceof IdLongTensor);
    }

    @Test
    public void testGetCell() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        assertEquals(pData.get(index),pTensor.getCell(index));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetCellInvalidDimensionsTooBig() {
        int[] index = new int[dimensions.length+1];
        pTensor.getCell(index);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetCellInvalidDimensionsTooSmall() {
        int[] index = new int[dimensions.length-1];
        pTensor.getCell(index);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testGetCellInvalidIndexTooBig() {
        int[] index = new int[dimensions.length];
        int randomIndex = random.nextInt(dimensions.length);
        index[randomIndex] = dimensions[randomIndex];
        pTensor.getCell(index);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testGetCellInvalidIndexTooSmall() {
        int[] index = new int[dimensions.length];
        index[random.nextInt(dimensions.length)] = -1;
        pTensor.getCell(index);
    }

    @Test
    public void testSetCell() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        long value = getRandomElement();
        pTensor.setCell(value,index);
        assertEquals(value,pTensor.getCell(index));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSetCellInvalidDimensionsTooBig() {
        int[] index = new int[dimensions.length+1];
        pTensor.setCell(getRandomElement(),index);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSetCellInvalidDimensionsTooSmall() {
        int[] index = new int[dimensions.length-1];
        pTensor.setCell(getRandomElement(),index);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testSetCellInvalidIndexTooBig() {
        int[] index = new int[dimensions.length];
        int randomIndex = random.nextInt(dimensions.length);
        index[randomIndex] = dimensions[randomIndex];
        pTensor.setCell(getRandomElement(),index);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testSetCellInvalidIndexTooSmall() {
        int[] index = new int[dimensions.length];
        index[random.nextInt(dimensions.length)] = -1;
        pTensor.setCell(getRandomElement(),index);
    }

    @Test
    public void testSetTensorValuesPrimitiveArray() {
        LongTypeSafeArray newValues = TypeSafeArrayFactory.longTypeSafeArray(dimensions);
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        long value = getRandomElement();
        newValues.set(value,index);
        pTensor.setTensorValues(newValues);
        assertEquals(value,pTensor.getCell(index));
    }
}
