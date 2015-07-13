package com.pb.sawdust.tensor;

import com.pb.sawdust.util.array.*;
import com.pb.sawdust.util.test.TestBase;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.abacus.IterableAbacus;
import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.StandardIndex;
import com.pb.sawdust.tensor.index.BaseIndex;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author crf <br/>
 *         Started: Jul 3, 2009 2:28:14 PM
 */
public abstract class TensorTest<T> extends TestBase {
    protected int[] dimensions;
    protected TypeSafeArray<T> data;
    protected Tensor<T> tensor;
    protected JavaType javaType;

    protected abstract Tensor<T> getTensor(TypeSafeArray<T> data);
    protected abstract TypeSafeArray<T> getData();
    protected abstract JavaType getJavaType();
    protected abstract T getRandomElement();

    @Before
    public void beforeTest() {
        data = getData();
        dimensions = ArrayUtil.getDimensions(data.getArray());
        javaType = getJavaType();
        tensor = getTensor(data);
    }

    @Test
    public void testGetDimensions() {
        assertArrayEquals(dimensions, tensor.getDimensions());
    }

    @Test
    public void testSize() {
        assertEquals(dimensions.length, tensor.size());
    }

    @Test
    public void testDimensionSize() {
        int randomIndex = random.nextInt(dimensions.length);
        assertEquals(dimensions[randomIndex], tensor.size(randomIndex));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDimensionSizeFailureNegative() {
        tensor.size(-1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDimensionSizeFailureTooBig() {
        tensor.size(dimensions.length);
    }

    @Test
    public void testGetType() {
        assertEquals(javaType, tensor.getType());
    }

    @Test
    public void testGetValue() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        assertEquals(data.getValue(index), tensor.getValue(index));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetValueInvalidDimensionsTooBig() {
        int[] index = new int[dimensions.length+1];
        tensor.getValue(index);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetValueInvalidDimensionsTooSmall() {
        int[] index = new int[dimensions.length-1];
        tensor.getValue(index);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testGetValueInvalidIndexTooBig() {
        int[] index = new int[dimensions.length];
        int randomIndex = random.nextInt(dimensions.length);
        index[randomIndex] = dimensions[randomIndex];
        tensor.getValue(index);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testGetValueInvalidIndexTooSmall() {
        int[] index = new int[dimensions.length];
        index[random.nextInt(dimensions.length)] = -1;
        tensor.getValue(index);
    }

    @Test
    public void testSetValue() {
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        T value = getRandomElement();
        tensor.setValue(value,index);
        assertEquals(value, tensor.getValue(index));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSetValueInvalidDimensionsTooBig() {
        int[] index = new int[dimensions.length+1];
        tensor.setValue(getRandomElement(),index);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSetValueInvalidDimensionsTooSmall() {
        int[] index = new int[dimensions.length-1];
        tensor.setValue(getRandomElement(),index);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testSetValueInvalidIndexTooBig() {
        int[] index = new int[dimensions.length];
        int randomIndex = random.nextInt(dimensions.length);
        index[randomIndex] = dimensions[randomIndex];
        tensor.setValue(getRandomElement(),index);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testSetValueInvalidIndexTooSmall() {
        int[] index = new int[dimensions.length];
        index[random.nextInt(dimensions.length)] = -1;
        tensor.setValue(getRandomElement(),index);
    }

    @Test
    @SuppressWarnings("unchecked") //ok, because we know we are supposed to have an array of type T
    public void testGetTensorValues() {
        assertArrayAlmostEquals(data.getArray(), tensor.getTensorValues((Class<T>) ArrayUtil.getBaseComponentType(data.getArray())).getArray());
    }

    @Test
    public void testSetTensorValuesArray() {
        @SuppressWarnings("unchecked") //doesn't matter here, just a test
        TypeSafeArray<T> newValues = (TypeSafeArray<T>) TypeSafeArrayFactory.typeSafeArray(javaType,dimensions);
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        T value = getRandomElement();
        newValues.setValue(value,index);
        tensor.setTensorValues(newValues);
        assertEquals(value, tensor.getValue(index));
    }

    @Test
    public void testSetTensorTensor() {
        @SuppressWarnings("unchecked") //doesn't matter here, just a test
        Tensor<T> newTensor = getTensor((TypeSafeArray<T>) TypeSafeArrayFactory.typeSafeArray(javaType,dimensions));
        int[] index = new int[dimensions.length];
        for (int i : range(index.length))
            index[i] = random.nextInt(dimensions[i]);
        T value = getRandomElement();
        newTensor.setValue(value,index);
        tensor.setTensorValues(newTensor);
        assertEquals(value, tensor.getValue(index));
    }

    @Test
    public void testGetIndex() {
        Index<?> index = new StandardIndex(dimensions);
        assertEquals(index,tensor.getIndex());
    }

    private Index<String> getReferenceIndex() {
        int[][] r = new int[dimensions.length][];
        String[][] id = new String[dimensions.length][];
        for (int i : range(r.length)) {
            int[] rs = new int[random.nextInt(1,10)];
            String[] ids = new String[rs.length];
            for (int j : range(rs.length)) {
                rs[j] = random.nextInt(dimensions[i]);
                ids[j] = random.nextAsciiString(10);
            }
            r[i] = rs;
            id[i] = ids;
        }
        return new BaseIndex<String>(r,id);
    }

    @Test
    public void testGetReferenceTensorDimensions() {
        Index<String> i = getReferenceIndex();
        assertArrayEquals(i.getDimensions(),tensor.getReferenceTensor(i).getDimensions());
    }

    @Test
    public void testGetReferenceTensorValue() {
        Index<String> i = getReferenceIndex();
        Tensor<T> t = tensor.getReferenceTensor(i);
        int[] ind = new int[dimensions.length];
        int[] loc = new int[dimensions.length];
        for (int d : range(dimensions.length))
            loc[d] = i.getIndex(d,ind[d] = random.nextInt(i.size(d)));
        assertEquals(tensor.getValue(loc),t.getValue(ind));
    }

    @Test
    public void testGetReferenceTensorIndex() {
        Index<String> i = getReferenceIndex();
        assertEquals(i,tensor.getReferenceTensor(i).getIndex());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetReferenceTensorFailure() {
        int[] dim = new int[dimensions.length-1]; //wrong size
        Arrays.fill(dim,1);
        Index<?> index = new StandardIndex(dim);
        tensor.getReferenceTensor(index);
    }

    @Test
    public void testIteratorType() {
        for (Tensor<T> t : tensor)
            assertTrue(t instanceof Tensor);
    }

    @Test
    @SuppressWarnings("unchecked") //we are just copying out type safe arrays
    public void testIterator() {
        //special for scalars
        if (dimensions.length ==0) {
            int count = 0;
            for (Tensor<T> t : tensor) {
                assertEquals(tensor,t);
                count++;
            }
            assertEquals(1,count);
            return;
        }
        int[] subarraySize = new int[dimensions.length == 1 ? 1 : dimensions.length-1];
        if (dimensions.length == 1)
            subarraySize[0] = 1;
        else
            System.arraycopy(dimensions,0,subarraySize,0,subarraySize.length);
        Class<T> type = (Class<T>) ArrayUtil.getBaseComponentType(data.getArray());
        TypeSafeArray<T> subArray;
        if (data instanceof ByteTypeSafeArray)
            subArray = (TypeSafeArray<T>) TypeSafeArrayFactory.byteTypeSafeArray(subarraySize);
        else if (data instanceof ShortTypeSafeArray)
            subArray = (TypeSafeArray<T>) TypeSafeArrayFactory.shortTypeSafeArray(subarraySize);
        else if (data instanceof IntTypeSafeArray)
            subArray = (TypeSafeArray<T>) TypeSafeArrayFactory.intTypeSafeArray(subarraySize);
        else if (data instanceof LongTypeSafeArray)
            subArray = (TypeSafeArray<T>) TypeSafeArrayFactory.longTypeSafeArray(subarraySize);
        else if (data instanceof FloatTypeSafeArray)
            subArray = (TypeSafeArray<T>) TypeSafeArrayFactory.floatTypeSafeArray(subarraySize);
        else if (data instanceof DoubleTypeSafeArray)
            subArray = (TypeSafeArray<T>) TypeSafeArrayFactory.doubleTypeSafeArray(subarraySize);
        else if (data instanceof BooleanTypeSafeArray)
            subArray = (TypeSafeArray<T>) TypeSafeArrayFactory.booleanTypeSafeArray(subarraySize);
        else if (data instanceof CharTypeSafeArray)
            subArray = (TypeSafeArray<T>) TypeSafeArrayFactory.charTypeSafeArray(subarraySize);
        else
            subArray = TypeSafeArrayFactory.typeSafeArray(type,subarraySize);
        //int randomEndDimension = random.nextInt(dimensions[dimensions.length-1]);
        int[] getter = new int[dimensions.length];
        int counter = 0;
        for (Tensor<T> t : tensor) {
            if (dimensions.length == 1) {
                subArray.setValue(data.getValue(counter++),0);
            } else {
                getter[dimensions.length-1] = counter++;
                for (int[] i : IterableAbacus.getIterableAbacus(subarraySize)) {
                    System.arraycopy(i,0,getter,0,i.length);
                    subArray.setValue(data.getValue(getter),i);
                }
            }
            assertArrayAlmostEquals(subArray.getArray(),t.getTensorValues(type).getArray());
        }
    }

}
