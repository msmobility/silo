package com.pb.sawdust.tensor.decorators.primitive.size;

import com.pb.sawdust.tensor.decorators.primitive.LongTensorTest;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.util.array.TypeSafeArray;
import static com.pb.sawdust.util.Range.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author crf <br/>
 *         Started: Jan 26, 2009 7:42:25 PM
 */
public abstract class LongD3TensorTest extends LongTensorTest {
    LongD3Tensor pdTensor;

    abstract protected LongD3Tensor getTensor(TypeSafeArray<Long> data);
    
    @Before
    public void beforeTest() {
        super.beforeTest();
        pdTensor = (LongD3Tensor) tensor;
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
        assertAlmostEquals(data.getValue(index),pdTensor.getValue(index[0],index[1],index[2]));
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
        Long value = getRandomElement();
        pdTensor.setValue(value,index[0],index[1],index[2]);
        assertAlmostEquals(value,pdTensor.getValue(index));
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

    @Test
    public void testGetCellD() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        assertAlmostEquals(pData.get(index),pdTensor.getCell(index[0],index[1],index[2]));
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testGetCellInvalidIndexTooBigD() {
        int[] index = new int[dimensions.length];
        int randomIndex = random.nextInt(dimensions.length);
        index[randomIndex] = dimensions[randomIndex];
        pdTensor.getCell(index[0],index[1],index[2]);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testGetCellInvalidIndexTooSmallD() {
        int[] index = new int[dimensions.length];
        index[random.nextInt(dimensions.length)] = -1;
        pdTensor.getCell(index[0],index[1],index[2]);
    }

    @Test
    public void testSetCellD() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        long value = getRandomElement();
        pdTensor.setCell(value,index[0],index[1],index[2]);
        assertAlmostEquals(value,pdTensor.getCell(index));
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testSetCellInvalidIndexTooBigD() {
        int[] index = new int[dimensions.length];
        int randomIndex = random.nextInt(dimensions.length);
        index[randomIndex] = dimensions[randomIndex];
        pdTensor.setCell(getRandomElement(),index[0],index[1],index[2]);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testSetCellInvalidIndexTooSmallD() {
        int[] index = new int[dimensions.length];
        index[random.nextInt(dimensions.length)] = -1;
        pdTensor.setCell(getRandomElement(),index[0],index[1],index[2]);
    }

    @Test
    public void testIteratorType() {
        for (Tensor<Long> t : tensor)
            assertTrue(t instanceof LongD2Tensor);
    }
}
