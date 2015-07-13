package com.pb.sawdust.tensor.decorators.id.primitive.size;

import com.pb.sawdust.tensor.decorators.id.primitive.IdFloatTensorTest;
import com.pb.sawdust.tensor.Tensor;
import static com.pb.sawdust.util.Range.range;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * @author crf <br/>
 *         Started: Jul 24, 2009 8:02:17 AM
 */
public abstract class IdFloatD2TensorTest<I> extends IdFloatTensorTest<I> {
    IdFloatD2Tensor<I> pdTensor;

    @Before
    public void beforeTest() {
        super.beforeTest();
        pdTensor = (IdFloatD2Tensor<I>) pTensor;
    }

    @Test
    public void testGetCellByIdD() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        I[] ids = getIdsFromIndices(index);
            assertAlmostEquals(pData.get(index), pdTensor.getCellById(ids[0],ids[1]));
    }

    @Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("unchecked") //only an Object[], but ok to be an I[]
    public void testGetCellByIdBadIdD() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        I[] ids = getIdsFromIndices(index);
        ids[random.nextInt(index.length)] = (I) new Object();
        pdTensor.getCellById(ids[0],ids[1]);
    }

    @Test
    public void testSetCellByIdD() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        float value = getRandomElement();
        I[] ids = getIdsFromIndices(index);
        pdTensor.setCellById(value,ids[0],ids[1]);
        assertAlmostEquals(value,pdTensor.getCell(index[0],index[1]));
    }

    @Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("unchecked") //only an Object[], but ok to be an I[]
    public void testSetCellByIdBadIdD() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        I[] ids = getIdsFromIndices(index);
        ids[random.nextInt(index.length)] = (I) new Object();
        pdTensor.setCellById(getRandomElement(),ids[0],ids[1]);
    }

    @Test
    public void testIteratorType() {
        for (Tensor<Float> t : tensor)
            assertTrue(t instanceof IdFloatD1Tensor);
    }   

    @Test
    public void testGetValueByIdD() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        I[] ids = getIdsFromIndices(index);
        assertAlmostEquals(data.getValue(index), pdTensor.getValueById(ids[0],ids[1]));
    }

    @Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("unchecked") //only an Object[], but ok to be an I[]
    public void testGetValueByIdBadIdD() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        I[] ids = getIdsFromIndices(index);
        ids[random.nextInt(index.length)] = (I) new Object();
        pdTensor.getValueById(ids[0],ids[1]);
    }

    @Test
    public void testSetValueByIdD() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        Float value = getRandomElement();
        I[] ids = getIdsFromIndices(index);
        pdTensor.setValueById(value,ids[0],ids[1]);
        assertAlmostEquals(value,idTensor.getValue(index[0],index[1]));
    }

    @Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("unchecked") //only an Object[], but ok to be an I[]
    public void testSetValueByIdBadIdD() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        I[] ids = getIdsFromIndices(index);
        ids[random.nextInt(index.length)] = (I) new Object();
        pdTensor.setValueById(getRandomElement(),ids[0],ids[1]);
    }

    @Test
    public void TestSizeSpecific() {
        assertAlmostEquals(2,pdTensor.size());
    }

    @Test
    public void testGetValueD() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        assertAlmostEquals(data.getValue(index),pdTensor.getValue(index[0],index[1]));
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testGetValueInvalidIndexTooBigD() {
        int[] index = new int[dimensions.length];
        int randomIndex = random.nextInt(dimensions.length);
        index[randomIndex] = dimensions[randomIndex];
        pdTensor.getValue(index[0],index[1]);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testGetValueInvalidIndexTooSmallD() {
        int[] index = new int[dimensions.length];
        index[random.nextInt(dimensions.length)] = -1;
        pdTensor.getValue(index[0],index[1]);
    }

    @Test
    public void testSetValueD() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        Float value = getRandomElement();
        pdTensor.setValue(value,index[0],index[1]);
        assertAlmostEquals(value,pdTensor.getValue(index));
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testSetValueInvalidIndexTooBigD() {
        int[] index = new int[dimensions.length];
        int randomIndex = random.nextInt(dimensions.length);
        index[randomIndex] = dimensions[randomIndex];
        pdTensor.setValue(getRandomElement(),index[0],index[1]);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testSetValueInvalidIndexTooSmallD() {
        int[] index = new int[dimensions.length];
        index[random.nextInt(dimensions.length)] = -1;
        pdTensor.setValue(getRandomElement(),index[0],index[1]);
    }

}
