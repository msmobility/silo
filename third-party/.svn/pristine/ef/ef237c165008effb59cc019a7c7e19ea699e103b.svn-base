package com.pb.sawdust.tensor.decorators.id.primitive.size;

import com.pb.sawdust.tensor.decorators.id.primitive.IdByteTensorTest;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.StandardIndex;
import com.pb.sawdust.util.array.TypeSafeArray;
import com.pb.sawdust.util.array.ByteTypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArrayFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

/**
 * @author crf <br/>
 *         Started: Jul 24, 2009 8:02:17 AM
 */
public abstract class IdByteD0TensorTest<I> extends IdByteTensorTest<I> {
    IdByteD0Tensor<I> pdTensor;

    @Before
    public void beforeTest() {
        super.beforeTest();
        pdTensor = (IdByteD0Tensor<I>) pTensor;
        dimensions = new int[0];
    }

    @Test @Ignore public void testGetValueByIdBadId() {}
    @Test @Ignore public void testSetValueByIdBadId() {}
    @Test @Ignore public void testGetValueByIdTooFewIds() {}
    @Test @Ignore public void testSetValueByIdTooFewIds() {}

    //remaining indices for D0 makes no sense - have to clear out "expected" by reannotating as Test
    @Ignore @Test public void testDimensionSize() {}
    @Ignore @Test public void testGetValueInvalidDimensionsTooSmall() {}
    @Ignore @Test public void testSetValueInvalidDimensionsTooSmall() {}
    @Ignore @Test public void testSetValueInvalidIndexTooSmall() {}
    @Ignore @Test public void testSetValueInvalidIndexTooBig() {}
    @Ignore @Test public void testGetValueInvalidIndexTooBig() {}
    @Ignore @Test public void testGetValueInvalidIndexTooSmall() {}

    @Test
    public void testGetCellByIdD() {
        assertEquals(pData.get(0), pdTensor.getCellById());
    }

    @Test
    public void testSetCellByIdD() {
        byte value = getRandomElement();
        pdTensor.setCellById(value);
        assertEquals(value,pdTensor.getCell());
    }

    @Test
    public void testIteratorType() {
        for (Tensor<Byte> t : tensor)
            assertTrue(t instanceof IdByteD0Tensor);
    }   

    @Test
    public void testGetValueByIdD() {
        assertEquals(data.getValue(0), pdTensor.getValueById());
    }

    @Test
    public void testSetValueByIdD() {
        Byte value = getRandomElement();
        pdTensor.setValueById(value);
        assertEquals(value,idTensor.getValue());
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
        Byte value = getRandomElement();
        pdTensor.setValue(value);
        assertEquals(value,pdTensor.getValue());
    }


    @Test
    public void testGetCellById() {
        int[] index = new int[dimensions.length];
        assertEquals(pData.get(0),pTensor.getCellById(getIdsFromIndices(index)));
    }

    @Test @Ignore public void testGetCellByIdTooFewIds() {}
    @Test @Ignore public void testGetCellByIdBadId() {}

    @Test
    public void testSetCellById() {
        int[] index = new int[dimensions.length];
        byte value = getRandomElement();
        pTensor.setCellById(value,getIdsFromIndices(index));
        assertEquals(value,pTensor.getCell());
    }

    @Test @Ignore public void testSetCellByIdTooFewIds() {}
    @Test @Ignore public void testSetCellByIdBadId() {}

    @Test
    public void testGetCell() {
        int[] index = new int[dimensions.length];
        assertEquals(pData.get(0),pTensor.getCell(index));
    }

    @Test @Ignore public void testGetCellInvalidDimensionsTooSmall() {}
    @Test @Ignore public void testSetCellInvalidDimensionsTooSmall() {}
    @Test @Ignore public void testGetCellInvalidIndexTooBig() {}
    @Test @Ignore public void testGetCellInvalidIndexTooSmall() {}
    @Test @Ignore public void testSetCellInvalidIndexTooBig() {}
    @Test @Ignore public void testSetCellInvalidIndexTooSmall() {}

    @Test
    public void testSetTensorValuesPrimitiveArray() {
        ByteTypeSafeArray newValues = TypeSafeArrayFactory.byteTypeSafeArray(1);
        byte value = getRandomElement();
        newValues.set(value,0);
        pTensor.setTensorValues(newValues);
        assertEquals(value,pTensor.getCell());
    }

    @Test
    public void testGetValueById() {
        int[] index = new int[dimensions.length];
        assertEquals(data.getValue(0),idTensor.getValueById(getIdsFromIndices(index)));
    }

    @Test
    public void testGetValue() {
        int[] index = new int[dimensions.length];
        assertEquals(data.getValue(0), tensor.getValue(index));
    }

    @Test
    public void testSetTensorValuesArray() {
        @SuppressWarnings("unchecked") //doesn't matter here, just a test
        TypeSafeArray<Byte> newValues = (TypeSafeArray<Byte>) TypeSafeArrayFactory.typeSafeArray(javaType,1);
        Byte value = getRandomElement();
        newValues.setValue(value,0);
        tensor.setTensorValues(newValues);
        assertEquals(value, tensor.getValue());
    }

    @Test
    public void testSetTensorTensor() {
        @SuppressWarnings("unchecked") //doesn't matter here, just a test
        Tensor<Byte> newTensor = getTensor((TypeSafeArray<Byte>) TypeSafeArrayFactory.typeSafeArray(javaType,1));
        Byte value = getRandomElement();
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
}
