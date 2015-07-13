package com.pb.sawdust.tensor.decorators.id.primitive;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import com.pb.sawdust.tensor.decorators.id.IdTensorTest;
import com.pb.sawdust.tensor.Tensor;
import static com.pb.sawdust.util.Range.range;
import com.pb.sawdust.util.array.BooleanTypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArrayFactory;
import com.pb.sawdust.util.JavaType;

/**
 * @author crf <br/>
 *         Started: Jul 23, 2009 6:42:35 PM
 */
public abstract class IdBooleanTensorTest<I>  extends IdTensorTest<Boolean,I> {
    protected IdBooleanTensor<I> pTensor;
    protected BooleanTypeSafeArray pData;

    @Before
    @SuppressWarnings("unchecked") //cast is ok
    public void beforeTest() {
        super.beforeTest();
        pTensor = (IdBooleanTensor<I>) tensor;
        pData = (BooleanTypeSafeArray) data;
    }

    protected JavaType getJavaType() {
        return JavaType.BOOLEAN;
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
        boolean value = getRandomElement();
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
        for (Tensor<Boolean> t : tensor)
            assertTrue(t instanceof IdBooleanTensor);
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
        boolean value = getRandomElement();
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
        BooleanTypeSafeArray newValues = TypeSafeArrayFactory.booleanTypeSafeArray(dimensions);
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        boolean value = getRandomElement();
        newValues.set(value,index);
        pTensor.setTensorValues(newValues);
        assertEquals(value,pTensor.getCell(index));
    }
}
