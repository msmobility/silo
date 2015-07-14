package com.pb.sawdust.tensor.decorators.size;

import com.pb.sawdust.tensor.TensorTest;
import com.pb.sawdust.tensor.Tensor;
import static com.pb.sawdust.util.Range.range;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * @author crf <br/>
 *         Started: Jan 25, 2009 9:49:50 PM
 */
public abstract class D2TensorTest<T> extends TensorTest<T> {
    protected D2Tensor<T> dTensor;

    @Before
    public void beforeTest() {
        super.beforeTest();
        dTensor = (D2Tensor<T>) tensor;
    }

    @Test
    public void TestSizeSpecific() {
        assertEquals(2,dTensor.size());
    }

    @Test
    public void testGetValueD() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        assertEquals(data.getValue(index),dTensor.getValue(index[0],index[1]));
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testGetValueInvalidIndexTooBigD() {
        int[] index = new int[dimensions.length];
        int randomIndex = random.nextInt(dimensions.length);
        index[randomIndex] = dimensions[randomIndex];
        dTensor.getValue(index[0],index[1]);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testGetValueInvalidIndexTooSmallD() {
        int[] index = new int[dimensions.length];
        index[random.nextInt(dimensions.length)] = -1;
        dTensor.getValue(index[0],index[1]);
    }

    @Test
    public void testSetValueD() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        T value = getRandomElement();
        dTensor.setValue(value,index[0],index[1]);
        assertEquals(value,dTensor.getValue(index));
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testSetValueInvalidIndexTooBigD() {
        int[] index = new int[dimensions.length];
        int randomIndex = random.nextInt(dimensions.length);
        index[randomIndex] = dimensions[randomIndex];
        dTensor.setValue(getRandomElement(),index[0],index[1]);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testSetValueInvalidIndexTooSmallD() {
        int[] index = new int[dimensions.length];
        index[random.nextInt(dimensions.length)] = -1;
        dTensor.setValue(getRandomElement(),index[0],index[1]);
    }

    @Test
    public void testIteratorType() {
        for (Tensor<T> t : tensor)
            assertTrue(t instanceof D1Tensor);
    }
}
