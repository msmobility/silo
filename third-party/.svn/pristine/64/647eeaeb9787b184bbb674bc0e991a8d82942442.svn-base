package com.pb.sawdust.tensor.decorators.primitive;

import com.pb.sawdust.tensor.TensorTest;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.array.TypeSafeArrayFactory;
import com.pb.sawdust.util.array.DoubleTypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArray;
import static com.pb.sawdust.util.Range.range;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author crf <br/>
 *         Started: Jan 26, 2009 12:11:35 PM
 */
public abstract class DoubleTensorTest extends TensorTest<Double> {
    protected DoubleTensor pTensor;
    protected DoubleTypeSafeArray pData;

    protected JavaType getJavaType() {
        return JavaType.DOUBLE;
    }

    abstract protected DoubleTypeSafeArray getData();
    abstract protected DoubleTensor getTensor(TypeSafeArray<Double> data);

    @Before
    public void beforeTest() {
        super.beforeTest();
        pTensor = (DoubleTensor) tensor;
        pData = (DoubleTypeSafeArray) data;
    }
    
    @Test
    public void testGetCell() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        assertAlmostEquals(pData.get(index),pTensor.getCell(index));
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
        double value = getRandomElement();
        pTensor.setCell(value,index);
        assertAlmostEquals(value,pTensor.getCell(index));
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
        DoubleTypeSafeArray newValues = TypeSafeArrayFactory.doubleTypeSafeArray(dimensions);
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        double value = getRandomElement();
        newValues.set(value,index);
        pTensor.setTensorValues(newValues);
        assertAlmostEquals(value,pTensor.getCell(index));
    }

    @Test
    public void testIteratorType() {
        for (Tensor<Double> t : tensor)
            assertTrue(t instanceof DoubleTensor);
    }
}
