package com.pb.sawdust.tensor.decorators.id.primitive.size;

import com.pb.sawdust.tensor.decorators.id.primitive.IdCharTensorTest;
import com.pb.sawdust.tensor.Tensor;
import static com.pb.sawdust.util.Range.range;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author crf <br/>
 *         Started: Jul 24, 2009 8:02:17 AM
 */
public abstract class IdCharD3TensorTest<I> extends IdCharTensorTest<I> {
    IdCharD3Tensor<I> pdTensor;

    @Before
    public void beforeTest() {
        super.beforeTest();
        pdTensor = (IdCharD3Tensor<I>) pTensor;
    }

    @Test
    public void testGetCellByIdD() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        I[] ids = getIdsFromIndices(index);
        assertEquals(pData.get(index), pdTensor.getCellById(ids[0],ids[1],ids[2]));
    }

    @Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("unchecked") //only an Object[], but ok to be an I[]
    public void testGetCellByIdBadIdD() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        I[] ids = getIdsFromIndices(index);
        ids[random.nextInt(index.length)] = (I) new Object();
        pdTensor.getCellById(ids[0],ids[1],ids[2]);
    }

    @Test
    public void testSetCellByIdD() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        char value = getRandomElement();
        I[] ids = getIdsFromIndices(index);
        pdTensor.setCellById(value,ids[0],ids[1],ids[2]);
        assertEquals(value,pdTensor.getCell(index[0],index[1],index[2]));
    }

    @Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("unchecked") //only an Object[], but ok to be an I[]
    public void testSetCellByIdBadIdD() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        I[] ids = getIdsFromIndices(index);
        ids[random.nextInt(index.length)] = (I) new Object();
        pdTensor.setCellById(getRandomElement(),ids[0],ids[1],ids[2]);
    }

    @Test
    public void testIteratorType() {
        for (Tensor<Character> t : tensor)
            assertTrue(t instanceof IdCharD2Tensor);
    }   

    @Test
    public void testGetValueByIdD() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        I[] ids = getIdsFromIndices(index);
        assertEquals(data.getValue(index), pdTensor.getValueById(ids[0],ids[1],ids[2]));
    }

    @Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("unchecked") //only an Object[], but ok to be an I[]
    public void testGetValueByIdBadIdD() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        I[] ids = getIdsFromIndices(index);
        ids[random.nextInt(index.length)] = (I) new Object();
        pdTensor.getValueById(ids[0],ids[1],ids[2]);
    }

    @Test
    public void testSetValueByIdD() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        Character value = getRandomElement();
        I[] ids = getIdsFromIndices(index);
        pdTensor.setValueById(value,ids[0],ids[1],ids[2]);
        assertEquals(value,idTensor.getValue(index[0],index[1],index[2]));
    }

    @Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("unchecked") //only an Object[], but ok to be an I[]
    public void testSetValueByIdBadIdD() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        I[] ids = getIdsFromIndices(index);
        ids[random.nextInt(index.length)] = (I) new Object();
        pdTensor.setValueById(getRandomElement(),ids[0],ids[1],ids[2]);
    }

    @Test
    public void TestSizeSpecific() {
        assertEquals(3,pdTensor.size());
    }

    @Test
    public void testGetValueD() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        assertEquals(data.getValue(index),pdTensor.getValue(index[0],index[1],index[2]));
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testGetValueInvalidIndexTooBigD() {
        int[] index = new int[dimensions.length];
        int randomIndex = random.nextInt(dimensions.length);
        index[randomIndex] = dimensions[randomIndex];
        pdTensor.getValue(index[0],index[1],index[2]);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testGetValueInvalidIndexTooSmallD() {
        int[] index = new int[dimensions.length];
        index[random.nextInt(dimensions.length)] = -1;
        pdTensor.getValue(index[0],index[1],index[2]);
    }

    @Test
    public void testSetValueD() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        Character value = getRandomElement();
        pdTensor.setValue(value,index[0],index[1],index[2]);
        assertEquals(value,pdTensor.getValue(index));
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testSetValueInvalidIndexTooBigD() {
        int[] index = new int[dimensions.length];
        int randomIndex = random.nextInt(dimensions.length);
        index[randomIndex] = dimensions[randomIndex];
        pdTensor.setValue(getRandomElement(),index[0],index[1],index[2]);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testSetValueInvalidIndexTooSmallD() {
        int[] index = new int[dimensions.length];
        index[random.nextInt(dimensions.length)] = -1;
        pdTensor.setValue(getRandomElement(),index[0],index[1],index[2]);
    }
}
