package com.pb.sawdust.util.array;

import com.pb.sawdust.util.test.TestBase;
import com.pb.sawdust.util.Range;
import com.pb.sawdust.calculator.Function1;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Iterator;
import java.util.Arrays;

/**
 * @author crf <br/>
 *         Started: Jul 21, 2008 7:59:57 AM
 */
public class ArrayUtilTest extends TestBase {

    public static void main(String ... args) {
        TestBase.main();
    }

    @Test
    public void testPrimitiveToDouble() {
        double value1 = 1.0d;
        double value2 = 2.0d;
        double value3 = 3.0d;
        double[] primitiveArray = new double[] {value1,value2,value3};
        Double[] objectArray = new Double[] {value1,value2,value3};
        assertArrayEquals(objectArray,ArrayUtil.toDoubleArray(primitiveArray));
    }

    @Test
    public void testPrimitivetoFloat() {
        float value1 = 1.0f;
        float value2 = 2.0f;
        float value3 = 3.0f;
        float[] primitiveArray = new float[] {value1,value2,value3};
        Float[] objectArray = new Float[] {value1,value2,value3};
        assertArrayEquals(objectArray,ArrayUtil.toFloatArray(primitiveArray));
    }

    @Test
    public void testPrimitiveToLong() {
        long value1 = 1L;
        long value2 = 2L;
        long value3 = 3L;
        long[] primitiveArray = new long[] {value1,value2,value3};
        Long[] objectArray = new Long[] {value1,value2,value3};
        assertArrayEquals(objectArray,ArrayUtil.toLongArray(primitiveArray));
    }

    @Test
    public void testPrimitiveToInt() {
        int value1 = 1;
        int value2 = 2;
        int value3 = 3;
        int[] primitiveArray = new int[] {value1,value2,value3};
        Integer[] objectArray = new Integer[] {value1,value2,value3};
        assertArrayEquals(objectArray,ArrayUtil.toIntegerArray(primitiveArray));
    }

    @Test
    public void testPrimitiveToShort() {
        short value1 = 1;
        short value2 = 2;
        short value3 = 3;
        short[] primitiveArray = new short[] {value1,value2,value3};
        Short[] objectArray = new Short[] {value1,value2,value3};
        assertArrayEquals(objectArray,ArrayUtil.toShortArray(primitiveArray));
    }

    @Test
    public void testPrimitiveToByte() {
        byte value1 = 1;
        byte value2 = 2;
        byte value3 = 3;
        byte[] primitiveArray = new byte[] {value1,value2,value3};
        Byte[] objectArray = new Byte[] {value1,value2,value3};
        assertArrayEquals(objectArray,ArrayUtil.toByteArray(primitiveArray));
    }

    @Test
    public void testPrimitiveToCharacter() {
        char value1 = 'a';
        char value2 = 'b';
        char value3 = 'c';
        char[] primitiveArray = new char[] {value1,value2,value3};
        Character[] objectArray = new Character[] {value1,value2,value3};
        assertArrayEquals(objectArray,ArrayUtil.toCharacterArray(primitiveArray));
    }

    @Test
    public void testPrimitiveToBoolean() {
        boolean[] primitiveArray = new boolean[random.nextInt(20)];
        Boolean[] objectArray = new Boolean[primitiveArray.length];
        for (int i : Range.range(primitiveArray.length)) {
            primitiveArray[i] = random.nextBoolean();
            objectArray[i] = primitiveArray[i];
        }
        assertArrayEquals(objectArray,ArrayUtil.toBooleanArray(primitiveArray));
    }

    @Test
    public void testToObjectPrimitiveDouble() {
        double value1 = 1.0d;
        double value2 = 2.0d;
        double value3 = 3.0d;
        double[] primitiveArray = new double[] {value1,value2,value3};
        Double[] objectArray = new Double[] {value1,value2,value3};
        assertArrayAlmostEquals(objectArray,ArrayUtil.toObjectArray(primitiveArray));
    }

    @Test
    public void testToObjectPrimitiveFloat() {
        float value1 = 1.0f;
        float value2 = 2.0f;
        float value3 = 3.0f;
        float[] primitiveArray = new float[] {value1,value2,value3};
        Float[] objectArray = new Float[] {value1,value2,value3};
        assertArrayAlmostEquals(objectArray,ArrayUtil.toObjectArray(primitiveArray));
    }

    @Test
    public void testToObjectPrimitiveLong() {
        long value1 = 1L;
        long value2 = 2L;
        long value3 = 3L;
        long[] primitiveArray = new long[] {value1,value2,value3};
        Long[] objectArray = new Long[] {value1,value2,value3};
        assertArrayAlmostEquals(objectArray,ArrayUtil.toObjectArray(primitiveArray));
    }

    @Test
    public void testToObjectPrimitiveInt() {
        int value1 = 1;
        int value2 = 2;
        int value3 = 3;
        int[] primitiveArray = new int[] {value1,value2,value3};
        Integer[] objectArray = new Integer[] {value1,value2,value3};
        assertArrayAlmostEquals(objectArray,ArrayUtil.toObjectArray(primitiveArray));
    }

    @Test
    public void testToObjectPrimitiveShort() {
        short value1 = 1;
        short value2 = 2;
        short value3 = 3;
        short[] primitiveArray = new short[] {value1,value2,value3};
        Short[] objectArray = new Short[] {value1,value2,value3};
        assertArrayAlmostEquals(objectArray,ArrayUtil.toObjectArray(primitiveArray));
    }

    @Test
    public void testToObjectPrimitiveByte() {
        byte value1 = 1;
        byte value2 = 2;
        byte value3 = 3;
        byte[] primitiveArray = new byte[] {value1,value2,value3};
        Byte[] objectArray = new Byte[] {value1,value2,value3};
        assertArrayAlmostEquals(objectArray,ArrayUtil.toObjectArray(primitiveArray));
    }

    @Test
    public void testToObjectPrimitiveCharacter() {
        char value1 = 'a';
        char value2 = 'b';
        char value3 = 'c';
        char[] primitiveArray = new char[] {value1,value2,value3};
        Character[] objectArray = new Character[] {value1,value2,value3};
        assertArrayAlmostEquals(objectArray,ArrayUtil.toObjectArray(primitiveArray));
    }

    @Test
    public void testToObjectPrimitiveBoolean() {
        boolean[] primitiveArray = new boolean[random.nextInt(20)];
        Boolean[] objectArray = new Boolean[primitiveArray.length];
        for (int i : Range.range(primitiveArray.length)) {
            primitiveArray[i] = random.nextBoolean();
            objectArray[i] = primitiveArray[i];
        }
        assertArrayEquals(objectArray,ArrayUtil.toObjectArray(primitiveArray));
    }

    @Test
    public void testToObjectObjectArray() {
        char value1 = 'a';
        int value2 = 4;
        String value3 = "cfg";
        Object[] objectArray = new Object[] {value1,value2,value3};
        assertArrayEquals(objectArray,ArrayUtil.toObjectArray(objectArray));
    }

    @Test
    public void testToPrimitiveDouble() {
        double value1 = 1.0d;
        double value2 = 2.0d;
        double value3 = 3.0d;
        double[] primitiveArray = new double[] {value1,value2,value3};
        Double[] objectArray = new Double[] {value1,value2,value3};
        assertArrayAlmostEquals(primitiveArray,ArrayUtil.toPrimitive(objectArray));
    }

    @Test
    public void testToPrimitiveFloat() {
        float value1 = 1.0f;
        float value2 = 2.0f;
        float value3 = 3.0f;
        float[] primitiveArray = new float[] {value1,value2,value3};
        Float[] objectArray = new Float[] {value1,value2,value3};
        assertArrayAlmostEquals(primitiveArray,ArrayUtil.toPrimitive(objectArray));
    }

    @Test
    public void testToPrimitiveLong() {
        long value1 = 1L;
        long value2 = 2L;
        long value3 = 3L;
        long[] primitiveArray = new long[] {value1,value2,value3};
        Long[] objectArray = new Long[] {value1,value2,value3};
        assertArrayEquals(primitiveArray,ArrayUtil.toPrimitive(objectArray));
    }

    @Test
    public void testToPrimitiveInt() {
        int value1 = 1;
        int value2 = 2;
        int value3 = 3;
        int[] primitiveArray = new int[] {value1,value2,value3};
        Integer[] objectArray = new Integer[] {value1,value2,value3};
        assertArrayEquals(primitiveArray,ArrayUtil.toPrimitive(objectArray));
    }

    @Test
    public void testToPrimitiveShort() {
        short value1 = 1;
        short value2 = 2;
        short value3 = 3;
        short[] primitiveArray = new short[] {value1,value2,value3};
        Short[] objectArray = new Short[] {value1,value2,value3};
        assertArrayEquals(primitiveArray,ArrayUtil.toPrimitive(objectArray));
    }

    @Test
    public void testToPrimitiveByte() {
        byte value1 = 1;
        byte value2 = 2;
        byte value3 = 3;
        byte[] primitiveArray = new byte[] {value1,value2,value3};
        Byte[] objectArray = new Byte[] {value1,value2,value3};
        assertArrayEquals(primitiveArray,ArrayUtil.toPrimitive(objectArray));
    }

    @Test
    public void testToPrimitiveChar() {
        char value1 = 'a';
        char value2 = 'b';
        char value3 = 'c';
        char[] primitiveArray = new char[] {value1,value2,value3};
        Character[] objectArray = new Character[] {value1,value2,value3};
        assertArrayEquals(primitiveArray,ArrayUtil.toPrimitive(objectArray));
    }

    @Test
    public void testToPrimitiveDoubleDefaultNull() {
        Double[] nullArray = new Double[] {null};
        assertAlmostEquals(0.0d,ArrayUtil.toPrimitive(nullArray)[0]);
    }

    @Test
    public void testToPrimitiveDoubleNull() {
        Double[] nullArray = new Double[] {null};
        double nullValue = 1.3d;
        assertAlmostEquals(nullValue,ArrayUtil.toPrimitive(nullArray,nullValue)[0]);
    }

    @Test
    public void testToPrimitiveFloatDefaultNull() {
        Float[] nullArray = new Float[] {null};
        assertAlmostEquals(0.0f,ArrayUtil.toPrimitive(nullArray)[0]);
    }

    @Test
    public void testToPrimitiveFloatNull() {
        Float[] nullArray = new Float[] {null};
        float nullValue = 1.3f;
        assertAlmostEquals(nullValue,ArrayUtil.toPrimitive(nullArray,nullValue)[0]);
    }

    @Test
    public void testToPrimitiveLongDefaultNull() {
        Long[] nullArray = new Long[] {null};
        assertEquals(0L,ArrayUtil.toPrimitive(nullArray)[0]);
    }

    @Test
    public void testToPrimitiveLongNull() {
        Long[] nullArray = new Long[] {null};
        long nullValue = 1L;
        assertEquals(nullValue,ArrayUtil.toPrimitive(nullArray,nullValue)[0]);
    }

    @Test
    public void testToPrimitiveIntDefaultNull() {
        Integer[] nullArray = new Integer[] {null};
        assertEquals(0,ArrayUtil.toPrimitive(nullArray)[0]);
    }

    @Test
    public void testToPrimitiveIntNull() {
        Integer[] nullArray = new Integer[] {null};
        int nullValue = 1;
        assertEquals(nullValue,ArrayUtil.toPrimitive(nullArray,nullValue)[0]);
    }

    @Test
    public void testToPrimitiveShortDefaultNull() {
        Short[] nullArray = new Short[] {null};
        assertEquals((short) 0,ArrayUtil.toPrimitive(nullArray)[0]);
    }

    @Test
    public void testToPrimitiveShortNull() {
        Short[] nullArray = new Short[] {null};
        short nullValue = 1;
        assertEquals(nullValue,ArrayUtil.toPrimitive(nullArray,nullValue)[0]);
    }

    @Test
    public void testToPrimitiveByteDefaultNull() {
        Byte[] nullArray = new Byte[] {null};
        assertEquals((byte) 0,ArrayUtil.toPrimitive(nullArray)[0]);
    }

    @Test
    public void testToPrimitiveByteNull() {
        Byte[] nullArray = new Byte[] {null};
        byte nullValue = 1;
        assertEquals(nullValue,ArrayUtil.toPrimitive(nullArray,nullValue)[0]);
    }

    @Test
    public void testToPrimitiveBooleanDefaultNull() {
        Boolean[] nullArray = new Boolean[] {null};
        assertEquals(false,ArrayUtil.toPrimitive(nullArray)[0]);
    }

    @Test
    public void testToPrimitiveBooleanNull() {
        Boolean[] nullArray = new Boolean[] {null};
        boolean nullValue = true;
        assertEquals(nullValue,ArrayUtil.toPrimitive(nullArray,nullValue)[0]);
    }

    @Test
    public void testToPrimitiveCharDefaultNull() {
        Character[] nullArray = new Character[] {null};
        assertEquals('\u0000', ArrayUtil.toPrimitive(nullArray)[0]);
    }

    @Test
    public void testToPrimitiveCharNull() {
        Character[] nullArray = new Character[] {null};
        char nullValue = 'a';
        assertEquals(nullValue,ArrayUtil.toPrimitive(nullArray,nullValue)[0]);
    }

    @Test
    public void testToDoubleArray() {
        int value1 = 1;
        int value2 = 2;
        Double[] targetArray = new Double[] {(double) value1,(double) value2};
        Integer[] sourceArray = new Integer[] {value1,value2};
        assertArrayEquals(targetArray,ArrayUtil.toDoubleArray(sourceArray));
    }

    @Test
    public void testToFloatArray() {
        int value1 = 1;
        int value2 = 2;
        Float[] targetArray = new Float[] {(float) value1,(float) value2};
        Integer[] sourceArray = new Integer[] {value1,value2};
        assertArrayEquals(targetArray,ArrayUtil.toFloatArray(sourceArray));
    }

    @Test
    public void testToLongArray() {
        double value1 = 1.4d;
        double value2 = 2.4d;
        Long[] targetArray = new Long[] {(long) value1,(long) value2};
        Double[] sourceArray = new Double[] {value1,value2};
        assertArrayEquals(targetArray,ArrayUtil.toLongArray(sourceArray));
    }

    @Test
    public void testToIntegerArray() {
        double value1 = 1.4d;
        double value2 = 2.4d;
        Integer[] targetArray = new Integer[] {(int) value1,(int) value2};
        Double[] sourceArray = new Double[] {value1,value2};
        assertArrayEquals(targetArray,ArrayUtil.toIntegerArray(sourceArray));
    }

    @Test
    public void testToShortArray() {
        double value1 = 1.4d;
        double value2 = 2.4d;
        Short[] targetArray = new Short[] {(short) value1,(short) value2};
        Double[] sourceArray = new Double[] {value1,value2};
        assertArrayEquals(targetArray,ArrayUtil.toShortArray(sourceArray));
    }

    @Test
    public void testToByteArray() {
        double value1 = 1.4d;
        double value2 = 2.4d;
        Byte[] targetArray = new Byte[] {(byte) value1,(byte) value2};
        Double[] sourceArray = new Double[] {value1,value2};
        assertArrayEquals(targetArray,ArrayUtil.toByteArray(sourceArray));
    }

    @Test
    public void testCopyArrayOneDimension() {
        Object[] array = new Object[] {1,new Object(), true, "dfjs",null};
        assertArrayEquals(array,ArrayUtil.copyArray(array));
    }

    @Test
    public void testCopyArrayOneDimensionDifferentObject() {
        Object[] array = new Object[] {1,new Object(), true, "dfjs",null};
        assertFalse(array == ArrayUtil.copyArray(array));
    }

    @Test
    public void testCopyArrayMultiDimension() {
        Object[] subArray1 = new Object[] {1,2,3};
        Object[] subArray2a = new Object[] {3,4,5};
        Object[] subArray2b = new Object[] {6,7,8};
        Object[] subArray2 = new Object[] {subArray2a,subArray2b};
        Object[] subArray3 = new Object[] {9,9,0};
        Object[] array = new Object[] {subArray1,subArray2,subArray3};
        assertArrayEquals(array,ArrayUtil.copyArray(array));
    }

    @Test
    public void testCopyArrayMultiDimensionDifferentObject() {
        Object[] subArray1 = new Object[] {1,2,3};
        Object[] subArray2a = new Object[] {3,4,5};
        Object[] subArray2b = new Object[] {6,7,8};
        Object[] subArray2 = new Object[] {subArray2a,subArray2b};
        Object[] subArray3 = new Object[] {9,9,0};
        Object[] array = new Object[] {subArray1,subArray2,subArray3};
        assertFalse(((Object[]) array[1])[0] == ((Object[]) (ArrayUtil.copyArray(array))[1])[0]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCopyArrayFailure() {
        ArrayUtil.copyArray(new Object());
    }

    @Test
    public void testBuildArray() {
        Object a = 1;
        Object b = "dsf";
        Object c = true;
        Object d = new Object();
        Object[] sourceArray = new Object[] {a,b,c,d};
        Object[] targetArray = new Object[] {c,b,a};
        int[] indices = new int[] {2,1,0};
        assertArrayEquals(targetArray, ArrayUtil.buildArray(sourceArray,indices));
    }

    @Test(expected=ArrayIndexOutOfBoundsException.class)
    public void testBuildArrayFailureIndexLow() {
        Object[] sourceArray = new Object[2];
        int[] indices = new int[] {-1};
        ArrayUtil.buildArray(sourceArray,indices);
    }

    @Test(expected=ArrayIndexOutOfBoundsException.class)
    public void testBuildArrayFailureIndexHigh() {
        Object[] sourceArray = new Object[2];
        int[] indices = new int[] {2};
        ArrayUtil.buildArray(sourceArray,indices);
    }

    @Test
    public void testBuildArrayPrimitive() {
        int a = 1;
        int b = 3;
        int c = 9;
        int d = 5;
        int[] sourceArray = new int[] {a,b,c,d};
        int[] targetArray = new int[] {c,b,a};
        int[] indices = new int[] {2,1,0};
        assertArrayEquals(targetArray, ArrayUtil.buildArray(sourceArray,indices));
    }

    @Test(expected=ArrayIndexOutOfBoundsException.class)
    public void testBuildArrayPrimitiveFailureIndexLow() {
        int[] sourceArray = new int[2];
        int[] indices = new int[] {-1};
        ArrayUtil.buildArray(sourceArray,indices);
    }

    @Test(expected=ArrayIndexOutOfBoundsException.class)
    public void testBuildArrayPrimitiveFailureIndexHigh() {
        int[] sourceArray = new int[2];
        int[] indices = new int[] {2};
        ArrayUtil.buildArray(sourceArray,indices);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBuildArrayFailure() {
        int[] indices = new int[0];
        ArrayUtil.buildArray(new Object(),indices);
    }

    @Test
    public void testBuildArrayDegenerate() {
        Object[] sourceArray = new Object[5];
        int[] indices = new int[0];
        assertArrayEquals(new Object[0], ArrayUtil.buildArray(sourceArray,indices));
    }

    @Test
    public void testArrayIterator() {
        Object[] array = new Object[] {1,new Object(), true, "dfjs",null};
        Object[] iteratorArray = new Object[array.length];
        int counter = 0;
        Iterator<Object> it = ArrayUtil.getIterator(array);
        while (it.hasNext())
            iteratorArray[counter++] = it.next();
        assertArrayEquals(array,iteratorArray);
    }

    @Test
    public void testPrimitiveArrayIteratorByte() {
        byte[] array = new byte[random.nextInt(20)];
        for (int i : Range.range(array.length))
            array[i] = random.nextByte();
        byte[] iteratorArray = new byte[array.length];
        int counter = 0;
        Iterator it = ArrayUtil.getIterator(array);
        while (it.hasNext())
            iteratorArray[counter++] = (Byte) it.next();
        assertArrayEquals(array,iteratorArray);
    }  

    @Test
    public void testPrimitiveArrayIteratorShort() {
        short[] array = new short[random.nextInt(20)];
        for (int i : Range.range(array.length))
            array[i] = random.nextShort();
        short[] iteratorArray = new short[array.length];
        int counter = 0;
        Iterator it = ArrayUtil.getIterator(array);
        while (it.hasNext())
            iteratorArray[counter++] = (Short) it.next();
        assertArrayEquals(array,iteratorArray);
    }

    @Test
    public void testPrimitiveArrayIteratorInt() {
        int[] array = new int[] {1,2,3,4};
        int[] iteratorArray = new int[array.length];
        int counter = 0;
        Iterator it = ArrayUtil.getIterator(array);
        while (it.hasNext())
            iteratorArray[counter++] = (Integer) it.next();
        assertArrayEquals(array,iteratorArray);
    }            

    @Test
    public void testPrimitiveArrayIteratorLong() {
        long[] array = new long[random.nextInt(20)];
        for (int i : Range.range(array.length))
            array[i] = random.nextLong();
        long[] iteratorArray = new long[array.length];
        int counter = 0;
        Iterator it = ArrayUtil.getIterator(array);
        while (it.hasNext())
            iteratorArray[counter++] = (Long) it.next();
        assertArrayEquals(array,iteratorArray);
    }

    @Test
    public void testPrimitiveArrayIteratorFloat() {
        float[] array = new float[random.nextInt(20)];
        for (int i : Range.range(array.length))
            array[i] = random.nextFloat();
        float[] iteratorArray = new float[array.length];
        int counter = 0;
        Iterator it = ArrayUtil.getIterator(array);
        while (it.hasNext())
            iteratorArray[counter++] = (Float) it.next();
        assertArrayAlmostEquals(array,iteratorArray);
    }

    @Test
    public void testPrimitiveArrayIteratorDouble() {
        double[] array = new double[random.nextInt(20)];
        for (int i : Range.range(array.length))
            array[i] = random.nextDouble();
        double[] iteratorArray = new double[array.length];
        int counter = 0;
        Iterator it = ArrayUtil.getIterator(array);
        while (it.hasNext())
            iteratorArray[counter++] = (Double) it.next();
        assertArrayAlmostEquals(array,iteratorArray);
    }     

    @Test
    public void testPrimitiveArrayIteratorBoolean() {
        boolean[] array = new boolean[random.nextInt(20)];
        for (int i : Range.range(array.length))
            array[i] = random.nextBoolean();
        boolean[] iteratorArray = new boolean[array.length];
        int counter = 0;
        Iterator it = ArrayUtil.getIterator(array);
        while (it.hasNext())
            iteratorArray[counter++] = (Boolean) it.next();
        assertArrayAlmostEquals(array,iteratorArray);
    }             

    @Test
    public void testPrimitiveArrayIteratorChar() {
        char[] array = new char[random.nextInt(20)];
        for (int i : Range.range(array.length))
            array[i] = random.nextAsciiChar();
        char[] iteratorArray = new char[array.length];
        int counter = 0;
        Iterator it = ArrayUtil.getIterator(array);
        while (it.hasNext())
            iteratorArray[counter++] = (Character) it.next();
        assertArrayEquals(array,iteratorArray);
    }                 

    @Test
    public void testObjectArrayIterator() {
        String[] array = new String[random.nextInt(20)];
        for (int i : Range.range(array.length))
            array[i] = random.nextAsciiString(random.nextInt(5,30));
        String[] iteratorArray = new String[array.length];
        int counter = 0;
        Iterator it = ArrayUtil.getIterator((Object) array);
        while (it.hasNext())
            iteratorArray[counter++] = (String) it.next();
        assertArrayEquals(array,iteratorArray);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testArrayIteratorFailure() {
        ArrayUtil.getIterator(new Object());
    }

    @Test
    public void testThreadSaferArrayIterator() {
        Object[] array = new Object[] {1,new Object(), true, "dfjs",null};
        Object[] iteratorArray = new Object[array.length];
        int counter = 0;
        Iterator<Object> it = ArrayUtil.getThreadSaferIterator(array);
        while (it.hasNext())
            iteratorArray[counter++] = it.next();
        assertArrayEquals(array,iteratorArray);
    }

    @Test
    public void testThreadSaferObjectArrayIterator() {
        String[] array = new String[random.nextInt(20)];
        for (int i : Range.range(array.length))
            array[i] = random.nextAsciiString(random.nextInt(5,30));
        String[] iteratorArray = new String[array.length];
        int counter = 0;
        Iterator it = ArrayUtil.getThreadSaferIterator((Object) array);
        while (it.hasNext())
            iteratorArray[counter++] = (String) it.next();
        assertArrayEquals(array,iteratorArray);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testThreadSaferArrayIteratorFailure() {
        ArrayUtil.getThreadSaferIterator(new Object());
    }

    @Test
    public void testApply() {
        int randomSize = random.nextInt(1,20);
        Integer[] values = new Integer[randomSize];
        Integer[] squares = new Integer[randomSize];
        for (int i : Range.range(randomSize)) {
            int value = random.nextInt();
            values[i] = value;
            squares[i] = value*value;
        }
        assertArrayEquals(squares, ArrayUtil.apply(values,getSquare()));
    }

    @Test(expected= IllegalArgumentException.class)
    public void testApplyFailure() {
        ArrayUtil.apply(new Integer[0],getSquare());
    }

    @Test
    public void testApplySpecifyArray() {
        int randomSize = random.nextInt(20);
        Integer[] values = new Integer[randomSize];
        Integer[] squares = new Integer[randomSize];
        for (int i : Range.range(randomSize)) {
            int value = random.nextInt();
            values[i] = value;
            squares[i] = value*value;
        }
        Number[] results = new Number[randomSize];
        ArrayUtil.apply(values,getSquare(),results);
        assertArrayEquals(squares,results);
    }

    @Test
    public void testApplySpecifySubTypeArray() {
        int randomSize = random.nextInt(20);
        Integer[] values = new Integer[randomSize];
        Integer[] squares = new Integer[randomSize];
        for (int i : Range.range(randomSize)) {
            int value = random.nextInt();
            values[i] = value;
            squares[i] = value*value;
        }
        Integer[] results = new Integer[randomSize];
        ArrayUtil.apply(values,getSquare(),results);
        assertArrayEquals(squares,results);
    }

    @Test
    public void testApplySpecifyArrayType() {
        int randomSize = random.nextInt(20);
        Integer[] values = new Integer[randomSize];
        Integer[] squares = new Integer[randomSize];
        for (int i : Range.range(randomSize)) {
            int value = random.nextInt();
            values[i] = value;
            squares[i] = value*value;
        }
        assertArrayEquals(squares, ArrayUtil.apply(values,getSquare(),new Number[0]));
    }

    @Test
    public void testApplySpecifyArraySubType() {
        int randomSize = random.nextInt(20);
        Integer[] values = new Integer[randomSize];
        Integer[] squares = new Integer[randomSize];
        for (int i : Range.range(randomSize)) {
            int value = random.nextInt();
            values[i] = value;
            squares[i] = value*value;
        }
        assertArrayEquals(squares, ArrayUtil.apply(values,getSquare(),new Integer[0]));
    }

    private Function1<Integer,Number> getSquare() {
        return new Function1<Integer,Number>() {
            public Number apply(Integer value) {
                return value*value;
            }
        };
    }

    @Test
    public void testGetBaseComponentPrimitive() {
        Object t = null;
        int a = random.nextInt(8);
        switch (a) {
            case 0 : t = new boolean[0][];break;
            case 1 : t = new byte[0][];break;
            case 2 : t = new short[0][];break;
            case 3 : t = new int[0][];break;
            case 4 : t = new long[0][];break;
            case 5 : t = new double[0][];break;
            case 6 : t = new float[0][];break;
            case 7 : t = new char[0][];break;
        }
       switch (a) {
            case 0 : assertEquals(boolean.class,ArrayUtil.getBaseComponentType(t)); break;
            case 1 : assertEquals(byte.class,ArrayUtil.getBaseComponentType(t)); break;
            case 2 : assertEquals(short.class,ArrayUtil.getBaseComponentType(t)); break;
            case 3 : assertEquals(int.class,ArrayUtil.getBaseComponentType(t)); break;
            case 4 : assertEquals(long.class,ArrayUtil.getBaseComponentType(t)); break;
            case 5 : assertEquals(double.class,ArrayUtil.getBaseComponentType(t)); break;
            case 6 : assertEquals(float.class,ArrayUtil.getBaseComponentType(t)); break;
            case 7 : assertEquals(char.class,ArrayUtil.getBaseComponentType(t)); break;
       }
    }

    @Test
    public void testGetBaseComponent() {
        assertEquals(String.class,ArrayUtil.getBaseComponentType(new String[0][]));
    }

    @Test
    public void testGetBaseComponentConfusion() {
        assertEquals(Object.class,ArrayUtil.getBaseComponentType(new Object[]{new String[0],new Byte[0]}));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetBaseComponentFailure() {
        ArrayUtil.getBaseComponentType("not an array");
    }

    @Test
    public void testGetDimensionCount() {
        assertEquals(3,ArrayUtil.getDimensionCount(new Object[0][][]));
    }

    @Test
    public void testGetDimensionCountConfusion() {
        assertEquals(1,ArrayUtil.getDimensionCount(new Object[]{new String[0],new Byte[0]}));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetDimensionCountFailure() {
        ArrayUtil.getDimensionCount("not an array");
    }

    @Test
    public void testGetDimensions() {
        assertArrayEquals(new int[] {1,3,2},ArrayUtil.getDimensions(new Object[1][3][2]));
    }

    @Test
    public void testGetDimensionsConfusion() {
        assertArrayEquals(new int[] {1},ArrayUtil.getDimensions(new Object[] {new boolean[4]}));
    }

    @Test
    public void testGetDimensionsJagged() {
        Object[][] array = new Object[2][];
        array[0] = new Object[4];
        array[1] = new Object[1];
        assertArrayEquals(new int[] {2,4},ArrayUtil.getDimensions(array));
    }

    @Test
    public void testGetDimensionsEmpty() {
        assertArrayEquals(new int[] {1,0,0},ArrayUtil.getDimensions(new Object[1][0][0]));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetDimensionsNotArray() {
        ArrayUtil.getDimensions("not an array");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetDimensionsNotFullyDefined() {
        ArrayUtil.getDimensions(new Object[3][]);
    }

    @Test
    public void testIsJagged() {
        Object[][] array = new Object[2][];
        array[0] = new Object[4];
        array[1] = new Object[1];
        assertTrue(ArrayUtil.isJagged(array));
    }

    @Test
    public void testIsNotJagged() {
        assertFalse(ArrayUtil.isJagged(new Object[2][3]));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIsJaggedNotArray() {
        ArrayUtil.isJagged("not an array");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIsJaggedNotFullyDefined() {
        ArrayUtil.isJagged(new Object[3][]);
    }

    @Test
    public void testIsOfDimension() {
        assertTrue(ArrayUtil.isOfDimension(new Object[1][3][2],new int[] {1,3,2}));
    }

    @Test
    public void testIsOfDimensionJagged() {
        Object[][] array = new Object[2][];
        array[0] = new Object[4];
        array[1] = new Object[1];
        assertFalse(ArrayUtil.isOfDimension(array,new int[] {2,4}));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIsOfDimensionNotArray() {
        ArrayUtil.isOfDimension("not an array",new int[] {1});
    }

    @Test
    public void testConcatenateByteArray() {
        byte[][] arrays = new byte[random.nextInt(3,8)][];
        int size = 0;
        for (int i : Range.range(arrays.length)) {
            byte[] array = new byte[random.nextInt(8)];
            for (int j : Range.range(array.length))
                array[j] = random.nextByte();
            arrays[i] = array;
            size += array.length;
        }
        byte[] array = new byte[size];
        int location = 0;
        for (byte[] ar : arrays) {
            System.arraycopy(ar,0,array,location,ar.length);
            location += ar.length;
        }
        assertArrayAlmostEquals(array,ArrayUtil.concatenateArrays(arrays));
    }

    @Test
    public void testConcatenateShortArray() {
        short[][] arrays = new short[random.nextInt(3,8)][];
        int size = 0;
        for (int i : Range.range(arrays.length)) {
            short[] array = new short[random.nextInt(8)];
            for (int j : Range.range(array.length))
                array[j] = random.nextShort();
            arrays[i] = array;
            size += array.length;
        }
        short[] array = new short[size];
        int location = 0;
        for (short[] ar : arrays) {
            System.arraycopy(ar,0,array,location,ar.length);
            location += ar.length;
        }
        assertArrayAlmostEquals(array,ArrayUtil.concatenateArrays(arrays));
    }

    @Test
    public void testConcatenateIntArray() {
        int[][] arrays = new int[random.nextInt(3,8)][];
        int size = 0;
        for (int i : Range.range(arrays.length)) {
            int[] array = new int[random.nextInt(8)];
            for (int j : Range.range(array.length))
                array[j] = random.nextInt();
            arrays[i] = array;
            size += array.length;
        }
        int[] array = new int[size];
        int location = 0;
        for (int[] ar : arrays) {
            System.arraycopy(ar,0,array,location,ar.length);
            location += ar.length;
        }
        assertArrayAlmostEquals(array,ArrayUtil.concatenateArrays(arrays));
    }

    @Test
    public void testConcatenateLongArray() {
        long[][] arrays = new long[random.nextInt(3,8)][];
        int size = 0;
        for (int i : Range.range(arrays.length)) {
            long[] array = new long[random.nextInt(8)];
            for (int j : Range.range(array.length))
                array[j] = random.nextLong();
            arrays[i] = array;
            size += array.length;
        }
        long[] array = new long[size];
        int location = 0;
        for (long[] ar : arrays) {
            System.arraycopy(ar,0,array,location,ar.length);
            location += ar.length;
        }
        assertArrayAlmostEquals(array,ArrayUtil.concatenateArrays(arrays));
    }

    @Test
    public void testConcatenateFloatArray() {
        float[][] arrays = new float[random.nextInt(3,8)][];
        int size = 0;
        for (int i : Range.range(arrays.length)) {
            float[] array = new float[random.nextInt(8)];
            for (int j : Range.range(array.length))
                array[j] = random.nextFloat();
            arrays[i] = array;
            size += array.length;
        }
        float[] array = new float[size];
        int location = 0;
        for (float[] ar : arrays) {
            System.arraycopy(ar,0,array,location,ar.length);
            location += ar.length;
        }
        assertArrayAlmostEquals(array,ArrayUtil.concatenateArrays(arrays));
    }

    @Test
    public void testConcatenateDoubleArray() {
        double[][] arrays = new double[random.nextInt(3,8)][];
        int size = 0;
        for (int i : Range.range(arrays.length)) {
            double[] array = new double[random.nextInt(8)];
            for (int j : Range.range(array.length))
                array[j] = random.nextDouble();
            arrays[i] = array;
            size += array.length;
        }
        double[] array = new double[size];
        int location = 0;
        for (double[] ar : arrays) {
            System.arraycopy(ar,0,array,location,ar.length);
            location += ar.length;
        }
        assertArrayAlmostEquals(array,ArrayUtil.concatenateArrays(arrays));
    }

    @Test
    public void testConcatenateBooleanArray() {
        boolean[][] arrays = new boolean[random.nextInt(3,8)][];
        int size = 0;
        for (int i : Range.range(arrays.length)) {
            boolean[] array = new boolean[random.nextInt(8)];
            for (int j : Range.range(array.length))
                array[j] = random.nextBoolean();
            arrays[i] = array;
            size += array.length;
        }
        boolean[] array = new boolean[size];
        int location = 0;
        for (boolean[] ar : arrays) {
            System.arraycopy(ar,0,array,location,ar.length);
            location += ar.length;
        }
        assertArrayAlmostEquals(array,ArrayUtil.concatenateArrays(arrays));
    }

    @Test
    public void testConcatenateCharArray() {
        char[][] arrays = new char[random.nextInt(3,8)][];
        int size = 0;
        for (int i : Range.range(arrays.length)) {
            char[] array = new char[random.nextInt(8)];
            for (int j : Range.range(array.length))
                array[j] = random.nextAsciiChar();
            arrays[i] = array;
            size += array.length;
        }
        char[] array = new char[size];
        int location = 0;
        for (char[] ar : arrays) {
            System.arraycopy(ar,0,array,location,ar.length);
            location += ar.length;
        }
        assertArrayAlmostEquals(array,ArrayUtil.concatenateArrays(arrays));
    }

    @Test
    public void testConcatenateObjectArray() {
        String[][] arrays = new String[random.nextInt(3,8)][];
        int size = 0;
        for (int i : Range.range(arrays.length)) {
            String[] array = new String[random.nextInt(8)];
            for (int j : Range.range(array.length))
                array[j] = random.nextAsciiString(random.nextInt(4,15));
            arrays[i] = array;
            size += array.length;
        }
        String[] array = new String[size];
        int location = 0;
        for (String[] ar : arrays) {
            System.arraycopy(ar,0,array,location,ar.length);
            location += ar.length;
        }
        assertArrayAlmostEquals(array,ArrayUtil.concatenateArrays(String.class,arrays));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConcatenateEmptyByteArray() {
        ArrayUtil.concatenateArrays(new byte[0][]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConcatenateEmptyShortArray() {
        ArrayUtil.concatenateArrays(new short[0][]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConcatenateEmptyIntArray() {
        ArrayUtil.concatenateArrays(new int[0][]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConcatenateEmptyLongArray() {
        ArrayUtil.concatenateArrays(new long[0][]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConcatenateEmptyFloatArray() {
        ArrayUtil.concatenateArrays(new float[0][]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConcatenateEmptyDoubleArray() {
        ArrayUtil.concatenateArrays(new double[0][]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConcatenateEmptyBooleanArray() {
        ArrayUtil.concatenateArrays(new boolean[0][]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConcatenateEmptyCharArray() {
        ArrayUtil.concatenateArrays(new char[0][]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConcatenateEmptyObjectArray() {
        ArrayUtil.concatenateArrays(String.class);
    }

    @Test
    public void testDeepFillObject() {
        String[][] array = new String[3][];
        array[0] = new String[random.nextInt(2,6)];
        array[1] = new String[random.nextInt(2,6)];
        array[2] = new String[random.nextInt(2,6)];
        String r = random.nextAsciiString(5);
        String[][] compArray = new String[3][];
        compArray[0] = new String[array[0].length];
        compArray[1] = new String[array[1].length];
        compArray[2] = new String[array[2].length];
        Arrays.fill(compArray[0],r);
        Arrays.fill(compArray[1],r);
        Arrays.fill(compArray[2],r);
        ArrayUtil.deepFill(array,r);
        assertArrayEquals(compArray,array);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDeepFillObjectNotArray() {
        ArrayUtil.deepFill("","");
    }

    @Test(expected=ArrayStoreException.class)
    public void testDeepFillObjectInvalidType() {
        String[][] array = new String[3][3];
        ArrayUtil.deepFill(array,4);
    }

    @Test
    public void testDeepPrimitiveFillByte() {
        byte[][] array = new byte[3][];
        array[0] = new byte[random.nextInt(2,6)];
        array[1] = new byte[random.nextInt(2,6)];
        array[2] = new byte[random.nextInt(2,6)];
        byte r = random.nextByte();
        byte[][] compArray = new byte[3][];
        compArray[0] = new byte[array[0].length];
        compArray[1] = new byte[array[1].length];
        compArray[2] = new byte[array[2].length];
        Arrays.fill(compArray[0],r);
        Arrays.fill(compArray[1],r);
        Arrays.fill(compArray[2],r);
        ArrayUtil.deepPrimitiveFill(array,r);
        assertArrayEquals(compArray,array);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDeepPrimitiveFillByteNotArray() {
        ArrayUtil.deepPrimitiveFill("",random.nextByte());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDeepPrimitiveFillByteNotByteArray() {
        ArrayUtil.deepPrimitiveFill(new int[8],random.nextByte());
    }

    @Test
    public void testDeepPrimitiveFillShort() {
        short[][] array = new short[3][];
        array[0] = new short[random.nextInt(2,6)];
        array[1] = new short[random.nextInt(2,6)];
        array[2] = new short[random.nextInt(2,6)];
        short r = random.nextShort();
        short[][] compArray = new short[3][];
        compArray[0] = new short[array[0].length];
        compArray[1] = new short[array[1].length];
        compArray[2] = new short[array[2].length];
        Arrays.fill(compArray[0],r);
        Arrays.fill(compArray[1],r);
        Arrays.fill(compArray[2],r);
        ArrayUtil.deepPrimitiveFill(array,r);
        assertArrayEquals(compArray,array);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDeepPrimitiveFillShortNotArray() {
        ArrayUtil.deepPrimitiveFill("",random.nextShort());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDeepPrimitiveFillShortNotShortArray() {
        ArrayUtil.deepPrimitiveFill(new int[8],random.nextShort());
    }

    @Test
    public void testDeepPrimitiveFillInt() {
        int[][] array = new int[3][];
        array[0] = new int[random.nextInt(2,6)];
        array[1] = new int[random.nextInt(2,6)];
        array[2] = new int[random.nextInt(2,6)];
        int r = random.nextInt();
        int[][] compArray = new int[3][];
        compArray[0] = new int[array[0].length];
        compArray[1] = new int[array[1].length];
        compArray[2] = new int[array[2].length];
        Arrays.fill(compArray[0],r);
        Arrays.fill(compArray[1],r);
        Arrays.fill(compArray[2],r);
        ArrayUtil.deepPrimitiveFill(array,r);
        assertArrayEquals(compArray,array);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDeepPrimitiveFillIntNotArray() {
        ArrayUtil.deepPrimitiveFill("",random.nextInt());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDeepPrimitiveFillIntNotIntArray() {
        ArrayUtil.deepPrimitiveFill(new boolean[8],random.nextInt());
    }

    @Test
    public void testDeepPrimitiveFillLong() {
        long[][] array = new long[3][];
        array[0] = new long[random.nextInt(2,6)];
        array[1] = new long[random.nextInt(2,6)];
        array[2] = new long[random.nextInt(2,6)];
        long r = random.nextLong();
        long[][] compArray = new long[3][];
        compArray[0] = new long[array[0].length];
        compArray[1] = new long[array[1].length];
        compArray[2] = new long[array[2].length];
        Arrays.fill(compArray[0],r);
        Arrays.fill(compArray[1],r);
        Arrays.fill(compArray[2],r);
        ArrayUtil.deepPrimitiveFill(array,r);
        assertArrayEquals(compArray,array);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDeepPrimitiveFillLongNotArray() {
        ArrayUtil.deepPrimitiveFill("",random.nextLong());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDeepPrimitiveFillLongNotLongArray() {
        ArrayUtil.deepPrimitiveFill(new int[8],random.nextLong());
    }

    @Test
    public void testDeepPrimitiveFillFloat() {
        float[][] array = new float[3][];
        array[0] = new float[random.nextInt(2,6)];
        array[1] = new float[random.nextInt(2,6)];
        array[2] = new float[random.nextInt(2,6)];
        float r = random.nextFloat();
        float[][] compArray = new float[3][];
        compArray[0] = new float[array[0].length];
        compArray[1] = new float[array[1].length];
        compArray[2] = new float[array[2].length];
        Arrays.fill(compArray[0],r);
        Arrays.fill(compArray[1],r);
        Arrays.fill(compArray[2],r);
        ArrayUtil.deepPrimitiveFill(array,r);
        assertArrayAlmostEquals(compArray,array);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDeepPrimitiveFillFloatNotArray() {
        ArrayUtil.deepPrimitiveFill("",random.nextFloat());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDeepPrimitiveFillFloatNotFloatArray() {
        ArrayUtil.deepPrimitiveFill(new int[8],random.nextFloat());
    }

    @Test
    public void testDeepPrimitiveFillDouble() {
        double[][] array = new double[3][];
        array[0] = new double[random.nextInt(2,6)];
        array[1] = new double[random.nextInt(2,6)];
        array[2] = new double[random.nextInt(2,6)];
        double r = random.nextDouble();
        double[][] compArray = new double[3][];
        compArray[0] = new double[array[0].length];
        compArray[1] = new double[array[1].length];
        compArray[2] = new double[array[2].length];
        Arrays.fill(compArray[0],r);
        Arrays.fill(compArray[1],r);
        Arrays.fill(compArray[2],r);
        ArrayUtil.deepPrimitiveFill(array,r);
        assertArrayAlmostEquals(compArray,array);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDeepPrimitiveFillDoubleNotArray() {
        ArrayUtil.deepPrimitiveFill("",random.nextDouble());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDeepPrimitiveFillDoubleNotDoubleArray() {
        ArrayUtil.deepPrimitiveFill(new int[8],random.nextDouble());
    }

    @Test
    public void testDeepPrimitiveFillChar() {
        char[][] array = new char[3][];
        array[0] = new char[random.nextInt(2,6)];
        array[1] = new char[random.nextInt(2,6)];
        array[2] = new char[random.nextInt(2,6)];
        char r = random.nextAsciiChar();
        char[][] compArray = new char[3][];
        compArray[0] = new char[array[0].length];
        compArray[1] = new char[array[1].length];
        compArray[2] = new char[array[2].length];
        Arrays.fill(compArray[0],r);
        Arrays.fill(compArray[1],r);
        Arrays.fill(compArray[2],r);
        ArrayUtil.deepPrimitiveFill(array,r);
        assertArrayEquals(compArray,array);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDeepPrimitiveFillCharNotArray() {
        ArrayUtil.deepPrimitiveFill("",random.nextAsciiChar());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDeepPrimitiveFillCharNotCharArray() {
        ArrayUtil.deepPrimitiveFill(new int[8],random.nextAsciiChar());
    }

    @Test
    public void testDeepPrimitiveFillBoolean() {
        boolean[][] array = new boolean[3][];
        array[0] = new boolean[random.nextInt(2,6)];
        array[1] = new boolean[random.nextInt(2,6)];
        array[2] = new boolean[random.nextInt(2,6)];
        boolean r = random.nextBoolean();
        boolean[][] compArray = new boolean[3][];
        compArray[0] = new boolean[array[0].length];
        compArray[1] = new boolean[array[1].length];
        compArray[2] = new boolean[array[2].length];
        Arrays.fill(compArray[0],r);
        Arrays.fill(compArray[1],r);
        Arrays.fill(compArray[2],r);
        ArrayUtil.deepPrimitiveFill(array,r);
        assertArrayEquals(compArray,array);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDeepPrimitiveFillBooleanNotArray() {
        ArrayUtil.deepPrimitiveFill("",random.nextBoolean());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDeepPrimitiveFillBooleanNotBooleanArray() {
        ArrayUtil.deepPrimitiveFill(new int[8],random.nextBoolean());
    }

    @Test
    public void testGetReverseByte() {
        byte[] array = new byte[random.nextInt(20)];
        byte[] reverse = new byte[array.length];
        for (int i : Range.range(array.length)) {
            array[i] = random.nextByte();
            reverse[array.length - i - 1] = array[i];
        }
        assertArrayEquals(reverse,ArrayUtil.getReverse(array));
    }

    @Test
    public void testReverseByte() {
        byte[] array = new byte[random.nextInt(20)];
        byte[] reverse = new byte[array.length];
        for (int i : Range.range(array.length)) {
            array[i] = random.nextByte();
            reverse[array.length - i - 1] = array[i];
        }
        ArrayUtil.reverse(array);
        assertArrayEquals(reverse,array);
    }

    @Test
    public void testGetReverseShort() {
        short[] array = new short[random.nextInt(20)];
        short[] reverse = new short[array.length];
        for (int i : Range.range(array.length)) {
            array[i] = random.nextShort();
            reverse[array.length - i - 1] = array[i];
        }
        assertArrayEquals(reverse,ArrayUtil.getReverse(array));
    }

    @Test
    public void testReverseShort() {
        short[] array = new short[random.nextInt(20)];
        short[] reverse = new short[array.length];
        for (int i : Range.range(array.length)) {
            array[i] = random.nextShort();
            reverse[array.length - i - 1] = array[i];
        }
        ArrayUtil.reverse(array);
        assertArrayEquals(reverse,array);
    }

    @Test
    public void testGetReverseInt() {
        int[] array = new int[random.nextInt(20)];
        int[] reverse = new int[array.length];
        for (int i : Range.range(array.length)) {
            array[i] = random.nextInt();
            reverse[array.length - i - 1] = array[i];
        }
        assertArrayEquals(reverse,ArrayUtil.getReverse(array));
    }

    @Test
    public void testReverseInt() {
        int[] array = new int[random.nextInt(20)];
        int[] reverse = new int[array.length];
        for (int i : Range.range(array.length)) {
            array[i] = random.nextInt();
            reverse[array.length - i - 1] = array[i];
        }
        ArrayUtil.reverse(array);
        assertArrayEquals(reverse,array);
    }

    @Test
    public void testGetReverseLong() {
        long[] array = new long[random.nextInt(20)];
        long[] reverse = new long[array.length];
        for (int i : Range.range(array.length)) {
            array[i] = random.nextLong();
            reverse[array.length - i - 1] = array[i];
        }
        assertArrayEquals(reverse,ArrayUtil.getReverse(array));
    }

    @Test
    public void testReverseLong() {
        long[] array = new long[random.nextInt(20)];
        long[] reverse = new long[array.length];
        for (int i : Range.range(array.length)) {
            array[i] = random.nextLong();
            reverse[array.length - i - 1] = array[i];
        }
        ArrayUtil.reverse(array);
        assertArrayEquals(reverse,array);
    }

    @Test
    public void testGetReverseFloat() {
        float[] array = new float[random.nextInt(20)];
        float[] reverse = new float[array.length];
        for (int i : Range.range(array.length)) {
            array[i] = random.nextFloat();
            reverse[array.length - i - 1] = array[i];
        }
        assertArrayAlmostEquals(reverse,ArrayUtil.getReverse(array));
    }

    @Test
    public void testReverseFloat() {
        float[] array = new float[random.nextInt(20)];
        float[] reverse = new float[array.length];
        for (int i : Range.range(array.length)) {
            array[i] = random.nextFloat();
            reverse[array.length - i - 1] = array[i];
        }
        ArrayUtil.reverse(array);
        assertArrayAlmostEquals(reverse,array);
    }

    @Test
    public void testGetReverseDouble() {
        double[] array = new double[random.nextInt(20)];
        double[] reverse = new double[array.length];
        for (int i : Range.range(array.length)) {
            array[i] = random.nextDouble();
            reverse[array.length - i - 1] = array[i];
        }
        assertArrayAlmostEquals(reverse,ArrayUtil.getReverse(array));
    }

    @Test
    public void testReverseDouble() {
        double[] array = new double[random.nextInt(20)];
        double[] reverse = new double[array.length];
        for (int i : Range.range(array.length)) {
            array[i] = random.nextDouble();
            reverse[array.length - i - 1] = array[i];
        }
        ArrayUtil.reverse(array);
        assertArrayAlmostEquals(reverse,array);
    }

    @Test
    public void testGetReverseBoolean() {
        boolean[] array = new boolean[random.nextInt(20)];
        boolean[] reverse = new boolean[array.length];
        for (int i : Range.range(array.length)) {
            array[i] = random.nextBoolean();
            reverse[array.length - i - 1] = array[i];
        }
        assertArrayAlmostEquals(reverse,ArrayUtil.getReverse(array));
    }

    @Test
    public void testReverseBoolean() {
        boolean[] array = new boolean[random.nextInt(20)];
        boolean[] reverse = new boolean[array.length];
        for (int i : Range.range(array.length)) {
            array[i] = random.nextBoolean();
            reverse[array.length - i - 1] = array[i];
        }
        ArrayUtil.reverse(array);
        assertArrayAlmostEquals(reverse,array);
    }

    @Test
    public void testGetReverseChar() {
        char[] array = new char[random.nextInt(20)];
        char[] reverse = new char[array.length];
        for (int i : Range.range(array.length)) {
            array[i] = random.nextAsciiChar();
            reverse[array.length - i - 1] = array[i];
        }
        assertArrayEquals(reverse,ArrayUtil.getReverse(array));
    }

    @Test
    public void testReverseChar() {
        char[] array = new char[random.nextInt(20)];
        char[] reverse = new char[array.length];
        for (int i : Range.range(array.length)) {
            array[i] = random.nextAsciiChar();
            reverse[array.length - i - 1] = array[i];
        }
        ArrayUtil.reverse(array);
        assertArrayEquals(reverse,array);
    }

    @Test
    public void testGetReverseObjectArray() {
        String[] array = new String[random.nextInt(20)];
        String[] reverse = new String[array.length];
        for (int i : Range.range(array.length)) {
            array[i] = random.nextAsciiString(random.nextInt(5,30));
            reverse[array.length - i - 1] = array[i];
        }
        assertArrayEquals(reverse,ArrayUtil.getReverse(array,String.class));
    }

    @Test
    public void testGetReverseObject() {
        String[] array = new String[random.nextInt(20)];
        String[] reverse = new String[array.length];
        for (int i : Range.range(array.length)) {
            array[i] = random.nextAsciiString(random.nextInt(5,30));
            reverse[array.length - i - 1] = array[i];
        }
        assertArrayEquals(reverse,(String[]) ArrayUtil.getReverse(array));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetReverseObjectNotArray() {
        Object array = "hi";
        ArrayUtil.getReverse(array);
    }

    @Test
    public void testReverseObject() {
        String[] array = new String[random.nextInt(20)];
        String[] reverse = new String[array.length];
        for (int i : Range.range(array.length)) {
            array[i] = random.nextAsciiString(random.nextInt(5,30));
            reverse[array.length - i - 1] = array[i];
        }
        ArrayUtil.reverse(array);
        assertArrayEquals(reverse,array);
    }

    //todo: copy array tests
    
}
