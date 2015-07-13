package com.pb.sawdust.tensor.decorators.primitive.size;

import com.pb.sawdust.tensor.decorators.primitive.DoubleTensorTest;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.StandardIndex;
import com.pb.sawdust.util.array.TypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArrayFactory;
import com.pb.sawdust.util.array.DoubleTypeSafeArray;
import static com.pb.sawdust.util.Range.range;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;

/**
 * @author crf <br/>
 *         Started: Jul 5, 2009 12:58:13 PM
 */
public abstract class DoubleD0TensorTest extends DoubleTensorTest {
    DoubleD0Tensor pdTensor;

    abstract protected DoubleD0Tensor getTensor(TypeSafeArray<Double> data);

    @Before
    public void beforeTest() {
        super.beforeTest();
        pdTensor = (DoubleD0Tensor) tensor;
        dimensions = new int[0];
    }

    //remaining indices for D0 makes no sense - have to clear out "expected" by reannotating as Test
    @Ignore @Test public void testDimensionSize() {}
    @Ignore @Test public void testGetValueInvalidDimensionsTooSmall() {}
    @Ignore @Test public void testSetValueInvalidDimensionsTooSmall() {}
    @Ignore @Test public void testSetValueInvalidIndexTooSmall() {}
    @Ignore @Test public void testSetValueInvalidIndexTooBig() {}
    @Ignore @Test public void testGetValueInvalidIndexTooBig() {}
    @Ignore @Test public void testGetValueInvalidIndexTooSmall() {}
    @Ignore @Test public void testGetCellInvalidDimensionsTooSmall() {}
    @Ignore @Test public void testSetCellInvalidDimensionsTooSmall() {}
    @Ignore @Test public void testSetCellInvalidIndexTooSmall() {}
    @Ignore @Test public void testSetCellInvalidIndexTooBig() {}
    @Ignore @Test public void testGetCellInvalidIndexTooBig() {}
    @Ignore @Test public void testGetCellInvalidIndexTooSmall() {}

    @Test
    public void testSetTensorValuesArray() {
        @SuppressWarnings("unchecked") //doesn't matter here, just a test
        TypeSafeArray<Double> newValues = (TypeSafeArray<Double>) TypeSafeArrayFactory.typeSafeArray(javaType,1);
        Double value = getRandomElement();
        newValues.setValue(value,0);
        tensor.setTensorValues(newValues);
        assertEquals(value, tensor.getValue());
    }

    @Test
    public void testSetTensorTensor() {
        @SuppressWarnings("unchecked") //doesn't matter here, just a test
        Tensor<Double> newTensor = getTensor((TypeSafeArray<Double>) TypeSafeArrayFactory.typeSafeArray(javaType,1));
        Double value = getRandomElement();
        newTensor.setValue(value);
        tensor.setTensorValues(newTensor);
        assertEquals(value, tensor.getValue());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetReferenceTensorFailure() {
        int[] dim = new int[dimensions.length+1]; //wrong size
        Arrays.fill(dim,1);
        Index<?> index = new StandardIndex(dim);
        tensor.getReferenceTensor(index);
    }

    @Test
    public void testSetTensorValuesPrimitiveArray() {
        DoubleTypeSafeArray newValues = TypeSafeArrayFactory.doubleTypeSafeArray(1);
        double value = getRandomElement();
        newValues.set(value,0);
        pTensor.setTensorValues(newValues);
        assertAlmostEquals(value,pTensor.getCell());
    }

    @Test
    public void testGetValue() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        assertEquals(data.getValue(0), tensor.getValue(index));
    }

    @Test
    public void TestSizeSpecific() {
        assertEquals(0,pdTensor.size());
    }

    @Test
    public void testGetValueD() {
        assertEquals(data.getValue(0),pdTensor.getValue());
    }

    @Test
    public void testSetValueD() {
        Double value = getRandomElement();
        pdTensor.setValue(value);
        assertEquals(value,pdTensor.getValue());
    }

    @Test
    public void testGetCell() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        assertAlmostEquals(pData.get(0),pTensor.getCell(index));
    }

    @Test
    public void testGetCellD() {
        assertAlmostEquals(pData.get(0),pdTensor.getCell());
    }

    @Test
    public void testSetCellD() {
        double value = getRandomElement();
        pdTensor.setCell(value);
        assertAlmostEquals(value,pdTensor.getCell());
    }

    @Test
    public void testIteratorType() {
        for (Tensor<Double> t : tensor)
            assertTrue(t == tensor);
    }
}
