package com.pb.sawdust.tensor.decorators.id.size;

import com.pb.sawdust.tensor.decorators.id.IdTensorTest;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.StandardIndex;
import com.pb.sawdust.util.array.TypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArrayFactory;
import static com.pb.sawdust.util.Range.range;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.*;

import java.util.Arrays;

/**
 * @author crf <br/>
 *         Started: Jul 23, 2009 8:09:54 AM
 */
public abstract class IdD0TensorTest<T,I> extends IdTensorTest<T,I> {
    protected IdD0Tensor<T,I> dTensor;

    @Before
    @SuppressWarnings("unchecked") //cast is ok
    public void beforeTest() {
        super.beforeTest();
        dTensor = (IdD0Tensor<T,I>) tensor;
        dimensions = new int[0];
    }

    @Test @Ignore public void testGetValueByIdBadId() {}
    @Test @Ignore public void testSetValueByIdBadId() {}
    @Test @Ignore public void testGetValueByIdTooFewIds() {}
    @Test @Ignore public void testSetValueByIdTooFewIds() {}

    @Test
    public void testGetValueById() {
        int[] index = new int[dimensions.length];
        assertEquals(data.getValue(0),idTensor.getValueById(getIdsFromIndices(index)));
    }

    @Test
    public void testGetValueByIdD() {
        assertEquals(data.getValue(0), dTensor.getValueById());
    }

    @Test
    public void testSetValueByIdD() {
        T value = getRandomElement();
        dTensor.setValueById(value);
        assertEquals(value,idTensor.getValue());
    }

    @Test
    public void testIteratorType() {
        for (Tensor<T> t : tensor)
            assertTrue(t instanceof IdD0Tensor);
    }

    //remaining indices for D0 makes no sense - have to clear out "expected" by reannotating as Test
    @Ignore @Test public void testDimensionSize() {}
    @Ignore @Test public void testGetValueInvalidDimensionsTooSmall() {}
    @Ignore @Test public void testSetValueInvalidDimensionsTooSmall() {}
    @Ignore @Test public void testSetValueInvalidIndexTooSmall() {}
    @Ignore @Test public void testSetValueInvalidIndexTooBig() {}
    @Ignore @Test public void testGetValueInvalidIndexTooBig() {}
    @Ignore @Test public void testGetValueInvalidIndexTooSmall() {}

    @Test
    public void testSetTensorValuesArray() {
        @SuppressWarnings("unchecked") //doesn't matter here, just a test
                TypeSafeArray<T> newValues = (TypeSafeArray<T>) TypeSafeArrayFactory.typeSafeArray(javaType,1);
        T value = getRandomElement();
        newValues.setValue(value,0);
        tensor.setTensorValues(newValues);
        assertEquals(value, tensor.getValue());
    }

    @Test
    public void testSetTensorTensor() {
        @SuppressWarnings("unchecked") //doesn't matter here, just a test
        Tensor<T> newTensor = getTensor((TypeSafeArray<T>) TypeSafeArrayFactory.typeSafeArray(javaType,1));
        T value = getRandomElement();
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
    public void TestSizeSpecific() {
        assertEquals(0,dTensor.size());
    }

    @Test
    public void testGetValue() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        assertEquals(data.getValue(0), tensor.getValue(index));
    }

    @Test
    public void testGetValueD() {
        assertEquals(data.getValue(0),dTensor.getValue());
    }

    @Test
    public void testSetValueD() {
        T value = getRandomElement();
        dTensor.setValue(value);
        assertEquals(value,dTensor.getValue());
    }
}
