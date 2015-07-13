package com.pb.sawdust.util.array;


import com.pb.sawdust.util.exceptions.RuntimeWrappingException;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.ClassInspector;
import com.pb.sawdust.calculator.Function1;

import java.util.*;
import java.lang.reflect.Array;


/**
 * The {@code ArrayUtil} class provides a variety of static convenience methods for dealing with arrays.
 *
 * @author crf
 *         Started: Jun 6, 2007 - 1:59:28 PM
 */
public class ArrayUtil {

    //class should not be instantiated
    private ArrayUtil() {}

    ///////////////////Primitive to object////////////////////////////////

    /**
     * Get a {@code Double[]} from a primitive {@code double[]}.
     *
     * @param inputArray
     *        The input array.
     *
     * @return an object array corresponding to {@code inputArray}.
     */
    public static Double[] toDoubleArray(double[] inputArray) {
        Double[] returnArray = new Double[inputArray.length];
        for (int i = 0; i < inputArray.length; i++)
            returnArray[i] = inputArray[i];
        return returnArray;
    }

    /**
     * Get a {@code Float[]} from a primitive {@code float[]}.
     *
     * @param inputArray
     *        The input array.
     *
     * @return an object array corresponding to {@code inputArray}.
     */
    public static Float[] toFloatArray(float[] inputArray) {
        Float[] returnArray = new Float[inputArray.length];
        for (int i = 0; i < inputArray.length; i++)
            returnArray[i] = inputArray[i];
        return returnArray;
    }

    /**
     * Get a {@code Long[]} from a primitive {@code long[]}.
     *
     * @param inputArray
     *        The input array.
     *
     * @return an object array corresponding to {@code inputArray}.
     */
    public static Long[] toLongArray(long[] inputArray) {
        Long[] returnArray = new Long[inputArray.length];
        for (int i = 0; i < inputArray.length; i++)
            returnArray[i] = inputArray[i];
        return returnArray;
    }

    /**
     * Get a {@code Integer[]} from a primitive {@code int[]}.
     *
     * @param inputArray
     *        The input array.
     *
     * @return an object array corresponding to {@code inputArray}.
     */
    public static Integer[] toIntegerArray(int[] inputArray) {
        Integer[] returnArray = new Integer[inputArray.length];
        for (int i = 0; i < inputArray.length; i++)
            returnArray[i] = inputArray[i];
        return returnArray;
    }

    /**
     * Get a {@code Short[]} from a primitive {@code short[]}.
     *
     * @param inputArray
     *        The input array.
     *
     * @return an object array corresponding to {@code inputArray}.
     */
    public static Short[] toShortArray(short[] inputArray) {
        Short[] returnArray = new Short[inputArray.length];
        for (int i = 0; i < inputArray.length; i++)
            returnArray[i] = inputArray[i];
        return returnArray;
    }

    /**
     * Get a {@code Byte[]} from a primitive {@code byte[]}.
     *
     * @param inputArray
     *        The input array.
     *
     * @return an object array corresponding to {@code inputArray}.
     */
    public static Byte[] toByteArray(byte[] inputArray) {
        Byte[] returnArray = new Byte[inputArray.length];
        for (int i = 0; i < inputArray.length; i++)
            returnArray[i] = inputArray[i];
        return returnArray;
    }

    /**
     * Get a {@code Boolean[]} from a primitive {@code boolean[]}.
     *
     * @param inputArray
     *        The input array.
     *
     * @return an object array corresponding to {@code inputArray}.
     */
    public static Boolean[] toBooleanArray(boolean[] inputArray) {
        Boolean[] returnArray = new Boolean[inputArray.length];
        for (int i = 0; i < inputArray.length; i++)
            returnArray[i] = inputArray[i];
        return returnArray;
    }

    /**
     * Get a {@code Character[]} from a primitive {@code char[]}.
     *
     * @param inputArray
     *        The input array.
     *
     * @return an object array corresponding to {@code inputArray}.
     */
    public static Character[] toCharacterArray(char[] inputArray) {
        Character[] returnArray = new Character[inputArray.length];
        for (int i = 0; i < inputArray.length; i++)
            returnArray[i] = inputArray[i];
        return returnArray;
    }

    /**
     * Get an object array from an input array. If the input array is a primitive array, then the array is transferred
     * to its object equivalent, otherwise the input array is returned.
     *
     * @param array
     *        The input array.
     *
     * @return the object array representation of {@code array}.
     *
     * @throws IllegalArgumentException if {@code array} is not an array instance.
     */
    public static Object[] toObjectArray(Object array) {
        JavaType type = JavaType.getComponentType(array);
        switch (type) {
            case BOOLEAN : return toBooleanArray((boolean[]) array);
            case BYTE : return toByteArray((byte[]) array);
            case CHAR : return toCharacterArray((char[]) array);
            case DOUBLE : return toDoubleArray((double[]) array);
            case FLOAT : return toFloatArray((float[]) array);
            case INT : return toIntegerArray((int[]) array);
            case LONG : return toLongArray((long[]) array);
            case SHORT : return toShortArray((short[]) array);
            default : return (Object[]) array;
        }
    }

    ////////////////////////////Object to primitive//////////////////////////////////

    /**
     * Get a primitive {@code double[]} from an input {@code Double[]}.
     *
     * @param inputArray
     *        The input array.
     *
     * @param nullValue
     *        The value to place in the output array whenever {@code null} values are found in {@code inputArray}.
     *
     * @return a primitive array corresponding to {@code inputArray}.
     */
    public static double[] toPrimitive(Double[] inputArray, double nullValue) {
        double[] returnArray = new double[inputArray.length];
        int counter = 0;
        for (Double d : inputArray)
            returnArray[counter++] = d == null ? nullValue : d;
        return returnArray;
    }

    /**
     * Get a primitive {@code double[]} from an input {@code Double[]}. A default value of {@code 0.0d} will be used
     * when {@code null} values are found.
     *
     * @param inputArray
     *        The input array.
     *
     * @return a primitive array corresponding to {@code inputArray}.
     */
    public static double[] toPrimitive(Double[] inputArray) {
        return toPrimitive(inputArray,0.0d);
    }

    /**
     * Get a primitive {@code float[]} from an input {@code Float[]}.
     *
     * @param inputArray
     *        The input array.
     *
     * @param nullValue
     *        The value to place in the output array whenever {@code null} values are found in {@code inputArray}.
     *
     * @return a primitive array corresponding to {@code inputArray}.
     */
    public static float[] toPrimitive(Float[] inputArray, float nullValue) {
        float[] returnArray = new float[inputArray.length];
        int counter = 0;
        for (Float f : inputArray)
            returnArray[counter++] = f == null ? nullValue : f;
        return returnArray;
    }

    /**
     * Get a primitive {@code float[]} from an input {@code Float[]}. A default value of {@code 0.0f} will be used
     * when {@code null} values are found.
     *
     * @param inputArray
     *        The input array.
     *
     * @return a primitive array corresponding to {@code inputArray}.
     */
    public static float[] toPrimitive(Float[] inputArray) {
        return toPrimitive(inputArray,0.0f);
    }

    /**
     * Get a primitive {@code long[]} from an input {@code Long[]}.
     *
     * @param inputArray
     *        The input array.
     *
     * @param nullValue
     *        The value to place in the output array whenever {@code null} values are found in {@code inputArray}.
     *
     * @return a primitive array corresponding to {@code inputArray}.
     */
    public static long[] toPrimitive(Long[] inputArray, long nullValue) {
        long[] returnArray = new long[inputArray.length];
        int counter = 0;
        for (Long l : inputArray)
            returnArray[counter++] = l == null ? nullValue : l;
        return returnArray;
    }

    /**
     * Get a primitive {@code long[]} from an input {@code Long[]}. A default value of {@code 0L} will be used
     * when {@code null} values are found.
     *
     * @param inputArray
     *        The input array.
     *
     * @return a primitive array corresponding to {@code inputArray}.
     */
    public static long[] toPrimitive(Long[] inputArray) {
        return toPrimitive(inputArray,0L);
    }

    /**
     * Get a primitive {@code int[]} from an input {@code Integer[]}.
     *
     * @param inputArray
     *        The input array.
     *
     * @param nullValue
     *        The value to place in the output array whenever {@code null} values are found in {@code inputArray}.
     *
     * @return a primitive array corresponding to {@code inputArray}.
     */
    public static int[] toPrimitive(Integer[] inputArray, int nullValue) {
        int[] returnArray = new int[inputArray.length];
        int counter = 0;
        for (Integer i : inputArray)
            returnArray[counter++] = i == null ? nullValue : i;
        return returnArray;
    }

    /**
     * Get a primitive {@code int[]} from an input {@code Integer[]}. A default value of {@code 0} will be used
     * when {@code null} values are found.
     *
     * @param inputArray
     *        The input array.
     *
     * @return a primitive array corresponding to {@code inputArray}.
     */
    public static int[] toPrimitive(Integer[] inputArray) {
        return toPrimitive(inputArray,0);
    }

    /**
     * Get a primitive {@code short[]} from an input {@code Short[]}.
     *
     * @param inputArray
     *        The input array.
     *
     * @param nullValue
     *        The value to place in the output array whenever {@code null} values are found in {@code inputArray}.
     *
     * @return a primitive array corresponding to {@code inputArray}.
     */
    public static short[] toPrimitive(Short[] inputArray, short nullValue) {
        short[] returnArray = new short[inputArray.length];
        int counter = 0;
        for (Short s : inputArray)
            returnArray[counter++] = s == null ? nullValue : s;
        return returnArray;
    }

    /**
     * Get a primitive {@code short[]} from an input {@code Short[]}. A default value of {@code 0} will be used
     * when {@code null} values are found.
     *
     * @param inputArray
     *        The input array.
     *
     * @return a primitive array corresponding to {@code inputArray}.
     */
    public static short[] toPrimitive(Short[] inputArray) {
        return toPrimitive(inputArray,(short) 0);
    }

    /**
     * Get a primitive {@code byte[]} from an input {@code Byte[]}.
     *
     * @param inputArray
     *        The input array.
     *
     * @param nullValue
     *        The value to place in the output array whenever {@code null} values are found in {@code inputArray}.
     *
     * @return a primitive array corresponding to {@code inputArray}.
     */
    public static byte[] toPrimitive(Byte[] inputArray, byte nullValue) {
        byte[] returnArray = new byte[inputArray.length];
        int counter = 0;
        for (Byte b : inputArray)
            returnArray[counter++] = b == null ? nullValue : b;
        return returnArray;
    }

    /**
     * Get a primitive {@code byte[]} from an input {@code Byte[]}. A default value of {@code 0} will be used
     * when {@code null} values are found.
     *
     * @param inputArray
     *        The input array.
     *
     * @return a primitive array corresponding to {@code inputArray}.
     */
    public static byte[] toPrimitive(Byte[] inputArray) {
        return toPrimitive(inputArray,(byte) 0);
    }

    /**
     * Get a primitive {@code boolean[]} from an input {@code Boolean[]}.
     *
     * @param inputArray
     *        The input array.
     *
     * @param nullValue
     *        The value to place in the output array whenever {@code null} values are found in {@code inputArray}.
     *
     * @return a primitive array corresponding to {@code inputArray}.
     */
    public static boolean[] toPrimitive(Boolean[] inputArray, boolean nullValue) {
        boolean[] returnArray = new boolean[inputArray.length];
        int counter = 0;
        for (Boolean b : inputArray)
            returnArray[counter++] = b == null ? nullValue : b;
        return returnArray;
    }

    /**
     * Get a primitive {@code boolean[]} from an input {@code Boolean[]}. A default value of {@code false} will be used
     * when {@code null} values are found.
     *
     * @param inputArray
     *        The input array.
     *
     * @return a primitive array corresponding to {@code inputArray}.
     */
    public static boolean[] toPrimitive(Boolean[] inputArray) {
        return toPrimitive(inputArray,false);
    }

    /**
     * Get a primitive {@code char[]} from an input {@code Character[]}.
     *
     * @param inputArray
     *        The input array.
     *
     * @param nullValue
     *        The value to place in the output array whenever {@code null} values are found in {@code inputArray}.
     *
     * @return a primitive array corresponding to {@code inputArray}.
     */
    public static char[] toPrimitive(Character[] inputArray, char nullValue) {
        char[] returnArray = new char[inputArray.length];
        int counter = 0;
        for (Character c : inputArray)
            returnArray[counter++] = c == null ? nullValue : c;
        return returnArray;
    }

    /**
     * Get a primitive {@code char[]} from an input {@code Character[]}. A default value of {@code '\u0000'} will be used
     * when {@code null} values are found.
     *
     * @param inputArray
     *        The input array.
     *
     * @return a primitive array corresponding to {@code inputArray}.
     */
    public static char[] toPrimitive(Character[] inputArray) {
        return toPrimitive(inputArray,'\u0000');
    }


    ////////////////////////////Object to object/////////////////////////////////

    /**
     * Convert an input number array to a {@code Double[]}. {@code null} values will be retained.
     *
     * @param inputArray
     *        The input array.
     *
     * @param <T>
     *        The component type of the input number array.
     *
     * @return an array corresponding to {@code inputArray}.
     */
    public static <T extends Number> Double[] toDoubleArray(T[] inputArray) {
        Double[] returnArray = new Double[inputArray.length];
        int counter = 0;
        for (Number n : inputArray)
            returnArray[counter++] = n == null ? null : n.doubleValue();
        return returnArray;
    }

    /**
     * Convert an input number array to a {@code Float[]}. {@code null} values will be retained.
     *
     * @param inputArray
     *        The input array.
     *
     * @param <T>
     *        The component type of the input number array.
     *
     * @return an array corresponding to {@code inputArray}.
     */
    public static <T extends Number> Float[] toFloatArray(T[] inputArray) {
        Float[] returnArray = new Float[inputArray.length];
        int counter = 0;
        for (Number n : inputArray)
            returnArray[counter++] = n == null ? null : n.floatValue();
        return returnArray;
    }

    /**
     * Convert an input number array to a {@code Long[]}. {@code null} values will be retained.
     *
     * @param inputArray
     *        The input array.
     *
     * @param <T>
     *        The component type of the input number array.
     *
     * @return an array corresponding to {@code inputArray}.
     */
    public static <T extends Number> Long[] toLongArray(T[] inputArray) {
        Long[] returnArray = new Long[inputArray.length];
        int counter = 0;
        for (Number n : inputArray)
            returnArray[counter++] = n == null ? null : n.longValue();
        return returnArray;
    }

    /**
     * Convert an input number array to a {@code Integer[]}. {@code null} values will be retained.
     *
     * @param inputArray
     *        The input array.
     *
     * @param <T>
     *        The component type of the input number array.
     *
     * @return an array corresponding to {@code inputArray}.
     */
    public static <T extends Number> Integer[] toIntegerArray(T[] inputArray) {
        Integer[] returnArray = new Integer[inputArray.length];
        int counter = 0;
        for (Number n : inputArray)
            returnArray[counter++] = n == null ? null : n.intValue();
        return returnArray;
    }

    /**
     * Convert an input number array to a {@code Short[]}. {@code null} values will be retained.
     *
     * @param inputArray
     *        The input array.
     *
     * @param <T>
     *        The component type of the input number array.
     *
     * @return an array corresponding to {@code inputArray}.
     */
    public static <T extends Number> Short[] toShortArray(T[] inputArray) {
        Short[] returnArray = new Short[inputArray.length];
        int counter = 0;
        for (Number n : inputArray)
            returnArray[counter++] = n == null ? null : n.shortValue();
        return returnArray;
    }

    /**
     * Convert an input number array to a {@code Byte[]}. {@code null} values will be retained.
     *
     * @param inputArray
     *        The input array.
     *
     * @param <T>
     *        The component type of the input number array.
     *
     * @return an array corresponding to {@code inputArray}.
     */
    public static <T extends Number> Byte[] toByteArray(T[] inputArray) {
        Byte[] returnArray = new Byte[inputArray.length];
        int counter = 0;
        for (Number n : inputArray)
            returnArray[counter++] = n == null ? null : n.byteValue();
        return returnArray;
    }
    
    /////////////////////Primitive arrays from collections///////////////

    /**
     * Get a primitive array containing all of the elements in the specified collection.
     * 
     * @param collection
     *        The collection.
     *        
     * @return a {@code byte} array holding all of the elements of {@code collection}.
     */
    public static byte[] toByteArray(Collection<Byte> collection) {
        byte[] array = new byte[collection.size()];
        int counter = 0;
        for (byte b : collection)
            array[counter++] = b;
        return array;
    }     

    /**
     * Get a primitive array containing all of the elements in the specified collection.
     * 
     * @param collection
     *        The collection.
     *        
     * @return a {@code short} array holding all of the elements of {@code collection}.
     */
    public static short[] toShortArray(Collection<Short> collection) {
        short[] array = new short[collection.size()];
        int counter = 0;
        for (short b : collection)
            array[counter++] = b;
        return array;
    }

    /**
     * Get a primitive array containing all of the elements in the specified collection.
     * 
     * @param collection
     *        The collection.
     *        
     * @return a {@code int} array holding all of the elements of {@code collection}.
     */
    public static int[] toIntArray(Collection<Integer> collection) {
        int[] array = new int[collection.size()];
        int counter = 0;
        for (int b : collection)
            array[counter++] = b;
        return array;
    }

    /**
     * Get a primitive array containing all of the elements in the specified collection.
     * 
     * @param collection
     *        The collection.
     *        
     * @return a {@code long} array holding all of the elements of {@code collection}.
     */
    public static long[] toLongArray(Collection<Long> collection) {
        long[] array = new long[collection.size()];
        int counter = 0;
        for (long b : collection)
            array[counter++] = b;
        return array;
    }

    /**
     * Get a primitive array containing all of the elements in the specified collection.
     * 
     * @param collection
     *        The collection.
     *        
     * @return a {@code float} array holding all of the elements of {@code collection}.
     */
    public static float[] toFloatArray(Collection<Float> collection) {
        float[] array = new float[collection.size()];
        int counter = 0;
        for (float b : collection)
            array[counter++] = b;
        return array;
    }

    /**
     * Get a primitive array containing all of the elements in the specified collection.
     * 
     * @param collection
     *        The collection.
     *        
     * @return a {@code double} array holding all of the elements of {@code collection}.
     */
    public static double[] toDoubleArray(Collection<Double> collection) {
        double[] array = new double[collection.size()];
        int counter = 0;
        for (double b : collection)
            array[counter++] = b;
        return array;
    }

    /**
     * Get a primitive array containing all of the elements in the specified collection.
     * 
     * @param collection
     *        The collection.
     *        
     * @return a {@code char} array holding all of the elements of {@code collection}.
     */
    public static char[] toCharArray(Collection<Character> collection) {
        char[] array = new char[collection.size()];
        int counter = 0;
        for (char b : collection)
            array[counter++] = b;
        return array;
    }

    /**
     * Get a primitive array containing all of the elements in the specified collection.
     * 
     * @param collection
     *        The collection.
     *        
     * @return a {@code boolean} array holding all of the elements of {@code collection}.
     */
    public static boolean[] toBooleanArray(Collection<Boolean> collection) {
        boolean[] array = new boolean[collection.size()];
        int counter = 0;
        for (boolean b : collection)
            array[counter++] = b;
        return array;
    }

    /////////////////////Array properties/////////////////////////////
    /**
     * Get the base component type for an array. The base component type of an array is the final type that the array
     * holds: for one dimensional arrays, this is the same as its component type; for multidimensional arrays this is
     * the type that the <i>multidimensional</i> holds.  For example, the base component type of {@code byte[][][]} is
     * {@code byte} (calling {@code Class.getComponentType} would return {@code byte[][]} as the componennt type).
     *
     * @param array
     *        The array in question.
     *
     * @return the base component type of {@code array}.
     *
     * @throws IllegalArgumentException if {@code array} is not an array.
     */
    public static Class<?> getBaseComponentType(Object array) {
        JavaType type = JavaType.getBaseComponentType(array);
        if (type != JavaType.OBJECT)
            return type.getClassEquivalent();
        String arrayClass = array.getClass().getName();
        try {
            return Class.forName(arrayClass.substring(arrayClass.lastIndexOf('[') + 2).replace(";",""));
        } catch (ClassNotFoundException e) {
            throw new RuntimeWrappingException(e);
        }
    }

    /**
     * Get the number of dimensions of an array. For example, a {@code byte[][][]} array has a dimension of 3. An array
     * declared as {@code X} dimensional but composed of arrays will be seen to have {@code X} dimensions (<i>e.g.</i> an
     * {@code Object[]} array filled with {@code byte[][]} arrays will have dimension 1, not 3).
     *
     * @param array
     *        The array in question.
     *
     * @return the number of dimensions of {@code array}.
     *
     * @throws IllegalArgumentException if {@code array} is not an array.
     */
    public static int getDimensionCount(Object array) {
        //this only counts if it is a true multidimensional array
//        if (array == null)
//            throw new IllegalArgumentException("Input array cannot be null");
        Class<?> arrayClass = array.getClass();
        if (!arrayClass.isArray())
            throw new IllegalArgumentException("Array parameter must be an array.");
        return arrayClass.getName().lastIndexOf('[') + 1;
    }

    /**
     * Get the dimensions of an array. The returned array has one entry for each dimension of the input array, and each
     * entry indicates the length in the array of that dimension. This method is "naive" in the sense that it only
     * uses the first element in each array dimension to determine dimension. That is, is assumes rectangularity and
     * will return a "not strictly correct" result if the input array is jagged. If any dimension has a length of zero,
     * then all subsequent dimensions are assumed to be of zero length (though an array may be declared as
     * {@code byte[2][0][3]} such an array is effectively {@code byte[2][0][0]}). If an array has not been fully
     * declared/filled (<i>e.g.</i> {@code byte[4][5][]} an exception will be thrown.
     *
     * @param array
     *        The array in question.
     *
     * @return an array describing the dimensionality of {@code array}.
     *
     * @throws IllegalArgumentException if {@code array} is not an array, is {@code null} or has not been fully declared
     *                                  or filled.
     */
    public static int[] getDimensions(Object array) {
        //simple, only descends using first element
        int dimensionCount = getDimensionCount(array);
        int[] dimensions = new int[dimensionCount];
        Object subMatrix = array;
        for (int i = 0; i < dimensionCount; i++) {
            int length;
            try {
                length = Array.getLength(subMatrix);
            } catch (NullPointerException e) {
                throw new IllegalArgumentException("Input array not fully declared/filled; cannot determine dimensions.");
            }
            if (length != 0) {
                dimensions[i] = length;
                subMatrix = Array.get(subMatrix,0);
            } else {
                Arrays.fill(dimensions,i,dimensionCount,0);
            }
        }
        return dimensions;
    }

    /**
     * Determine whether an input array is jagged. An array is determined to be jagged if it is not rectangular; that is,
     * if it composed of arrays of non-identical lengths. This method only checks up to the declared dimensions of a
     * given array: if an array is declared as {@code Object[][]} but is filled with {@code byte[][]} arrays, it only
     * determines whether the {@code Object[][]} array is jagged, not its component arrays.
     *
     * @param array
     *        The array in question.
     *
     * @return {@code true} if the array is jagged, {@code false} if it is rectangular.
     *
     * @throws IllegalArgumentException if {@code array} is not an array, is {@code null} or has not been fully declared
     *                                  or filled. 
     */
    public static boolean isJagged(Object array) {
        return !isOfDimension(array,getDimensions(array));
    }

    /**
     * Determine if an array has a given dimensionality. The dimensionality is defined by an array of integers whose
     * length specifies the number of dimensions and whose elements specify the size of each dimension. For an array to
     * have the specified dimensionality, it must be rectangular ({@code isJagged(array) == false}).
     *
     * @param array
     *        The array in question.
     *
     * @param expectedDimensions
     *        The expected dimensionality of the input array.
     *
     * @return {@code true} if {@code array} has dimensionality {@code expectedDimensions}, {@code false} otherwise
     *
     * @throws IllegalArgumentException if {@code array}.
     */
    public static boolean isOfDimension(Object array, int[] expectedDimensions) {
        if (!array.getClass().isArray())
            throw new IllegalArgumentException("Input object must be an array.");
        return !isJagged(array,expectedDimensions,0);
    }

    private static boolean isJagged(Object matrix, int[] expectedDimensions, int dimension) {
        if (dimension >= expectedDimensions.length)
            return matrix != null && matrix.getClass().isArray(); //reached dimensional bounds - if array then we are jagged
        if (matrix == null || !matrix.getClass().isArray() || Array.getLength(matrix) != expectedDimensions[dimension])
            return true; //not an array, or array not matching expected dimension
        for (int i = 0; i < expectedDimensions[dimension]; i++)
            if (isJagged( Array.get(matrix,i),expectedDimensions,dimension+1))
                return true;
        return false;
    }

    /////////////////////Setters////////////////////////////////
    /**
     * Fill a (possibly multidimensional) array with a specified value.
     *
     * @param array
     *        The array to fill.
     *
     * @param value
     *        The value to fill {@code array} with.
     *
     * @throws IllegalArgumentException if {@code array} is not an array.
     * @throws ArrayStoreException if {@code value} cannot be validly placed in {@code array}.
     */
    public static void deepFill(Object array, Object value) {
        deepFill(array,value,getDimensionCount(array));
    }

    private static void deepFill(Object array, Object value, int left) {
        left--;
        if (left == 0)
            Arrays.fill((Object[]) array,value);
        else
            for (int i = 0; i < Array.getLength(array); i++)
                deepFill(Array.get(array,i),value,left);
    }

    /**
     * Fill a (possibly multidimensional) byte array with a specified value.
     *
     * @param byteArray
     *        The array to fill.
     *
     * @param value
     *        The value to fill {@code byteArray} with.
     *
     * @throws IllegalArgumentException if {@code byteArray} is not a byte array.
     */
    public static void deepPrimitiveFill(Object byteArray, byte value) {
        Class<?> baseComponentType = getBaseComponentType(byteArray);
        if (baseComponentType != byte.class)
            throw new IllegalArgumentException("Array must be a byte array");
        deepPrimitiveFill(byteArray,value,getDimensionCount(byteArray));
    }

    private static void deepPrimitiveFill(Object array, byte value, int left) {
        left--;
        if (left == 0)
            Arrays.fill((byte[]) array,value);
        else
            for (int i = 0; i < Array.getLength(array); i++)
                deepPrimitiveFill(Array.get(array,i),value,left);
    }

    /**
     * Fill a (possibly multidimensional) short array with a specified value.
     *
     * @param shortArray
     *        The array to fill.
     *
     * @param value
     *        The value to fill {@code shortArray} with.
     *
     * @throws IllegalArgumentException if {@code shortArray} is not a short array.
     */
    public static void deepPrimitiveFill(Object shortArray, short value) {
        Class<?> baseComponentType = getBaseComponentType(shortArray);
        if (baseComponentType != short.class)
            throw new IllegalArgumentException("Array must be a short array");
        deepPrimitiveFill(shortArray,value,getDimensionCount(shortArray));
    }

    private static void deepPrimitiveFill(Object shortArray, short value, int left) {
        left--;
        if (left == 0)
            Arrays.fill((short[]) shortArray,value);
        else
            for (int i = 0; i < Array.getLength(shortArray); i++)
                deepPrimitiveFill(Array.get(shortArray,i),value,left);
    }

    /**
     * Fill a (possibly multidimensional) int array with a specified value.
     *
     * @param intArray
     *        The array to fill.
     *
     * @param value
     *        The value to fill {@code intArray} with.
     *
     * @throws IllegalArgumentException if {@code intArray} is not a int array.
     */
    public static void deepPrimitiveFill(Object intArray, int value) {
        Class<?> baseComponentType = getBaseComponentType(intArray);
        if (baseComponentType != int.class)
            throw new IllegalArgumentException("Array must be a int array");
        deepPrimitiveFill(intArray,value,getDimensionCount(intArray));
    }

    private static void deepPrimitiveFill(Object intArray, int value, int left) {
        left--;
        if (left == 0)
            Arrays.fill((int[]) intArray,value);
        else
            for (int i = 0; i < Array.getLength(intArray); i++)
                deepPrimitiveFill(Array.get(intArray,i),value,left);
    }

    /**
     * Fill a (possibly multidimensional) long array with a specified value.
     *
     * @param longArray
     *        The array to fill.
     *
     * @param value
     *        The value to fill {@code longArray} with.
     *
     * @throws IllegalArgumentException if {@code longArray} is not a long array.
     */
    public static void deepPrimitiveFill(Object longArray, long value) {
        Class<?> baseComponentType = getBaseComponentType(longArray);
        if (baseComponentType != long.class)
            throw new IllegalArgumentException("Array must be a long array");
        deepPrimitiveFill(longArray,value,getDimensionCount(longArray));
    }

    private static void deepPrimitiveFill(Object longArray, long value, int left) {
        left--;
        if (left == 0)
            Arrays.fill((long[]) longArray,value);
        else
            for (int i = 0; i < Array.getLength(longArray); i++)
                deepPrimitiveFill(Array.get(longArray,i),value,left);
    }

    /**
     * Fill a (possibly multidimensional) float array with a specified value.
     *
     * @param floatArray
     *        The array to fill.
     *
     * @param value
     *        The value to fill {@code floatArray} with.
     *
     * @throws IllegalArgumentException if {@code floatArray} is not a float array.
     */
    public static void deepPrimitiveFill(Object floatArray, float value) {
        Class<?> baseComponentType = getBaseComponentType(floatArray);
        if (baseComponentType != float.class)
            throw new IllegalArgumentException("Array must be a float array");
        deepPrimitiveFill(floatArray,value,getDimensionCount(floatArray));
    }

    private static void deepPrimitiveFill(Object floatArray, float value, int left) {
        left--;
        if (left == 0)
            Arrays.fill((float[]) floatArray,value);
        else
            for (int i = 0; i < Array.getLength(floatArray); i++)
                deepPrimitiveFill(Array.get(floatArray,i),value,left);
    }

    /**
     * Fill a (possibly multidimensional) double array with a specified value.
     *
     * @param doubleArray
     *        The array to fill.
     *
     * @param value
     *        The value to fill {@code doubleArray} with.
     *
     * @throws IllegalArgumentException if {@code doubleArray} is not a double array.
     */
    public static void deepPrimitiveFill(Object doubleArray, double value) {
        Class<?> baseComponentType = getBaseComponentType(doubleArray);
        if (baseComponentType != double.class)
            throw new IllegalArgumentException("Array must be a double array");
        deepPrimitiveFill(doubleArray,value,getDimensionCount(doubleArray));
    }

    private static void deepPrimitiveFill(Object doubleArray, double value, int left) {
        left--;
        if (left == 0)
            Arrays.fill((double[]) doubleArray,value);
        else
            for (int i = 0; i < Array.getLength(doubleArray); i++)
                deepPrimitiveFill(Array.get(doubleArray,i),value,left);
    }

    /**
     * Fill a (possibly multidimensional) boolean array with a specified value.
     *
     * @param booleanArray
     *        The array to fill.
     *
     * @param value
     *        The value to fill {@code booleanArray} with.
     *
     * @throws IllegalArgumentException if {@code booleanArray} is not a boolean array.
     */
    public static void deepPrimitiveFill(Object booleanArray, boolean value) {
        Class<?> baseComponentType = getBaseComponentType(booleanArray);
        if (baseComponentType != boolean.class)
            throw new IllegalArgumentException("Array must be a boolean array");
        deepPrimitiveFill(booleanArray,value,getDimensionCount(booleanArray));
    }

    private static void deepPrimitiveFill(Object booleanArray, boolean value, int left) {
        left--;
        if (left == 0)
            Arrays.fill((boolean[]) booleanArray,value);
        else
            for (int i = 0; i < Array.getLength(booleanArray); i++)
                deepPrimitiveFill(Array.get(booleanArray,i),value,left);
    }

    /**
     * Fill a (possibly multidimensional) char array with a specified value.
     *
     * @param charArray
     *        The array to fill.
     *
     * @param value
     *        The value to fill {@code charArray} with.
     *
     * @throws IllegalArgumentException if {@code charArray} is not a char array.
     */
    public static void deepPrimitiveFill(Object charArray, char value) {
        Class<?> baseComponentType = getBaseComponentType(charArray);
        if (baseComponentType != char.class)
            throw new IllegalArgumentException("Array must be a char array");
        deepPrimitiveFill(charArray,value,getDimensionCount(charArray));
    }

    private static void deepPrimitiveFill(Object charArray, char value, int left) {
        left--;
        if (left == 0)
            Arrays.fill((char[]) charArray,value);
        else
            for (int i = 0; i < Array.getLength(charArray); i++)
                deepPrimitiveFill(Array.get(charArray,i),value,left);
    }

    /**
     * Set the value of an array at the specified position. This method will work for both object and primitive arrays
     * (the object value will be unboxed if the array holds primitives).
     *
     * @param array
     *        The array.
     *
     * @param value
     *        The new value.
     *
     * @param index
     *        The index position of the new value.
     *
     * @param <T>
     *        The object type of the value.
     *
     * @throws IllegalArgumentException if {@code array}'s length is not equal to {@code index.length}, or if {@code array}
     *                                  stores primitives but {@code value} cannot be unwrapped appropriately.
     * @throws ArrayStoreException if {@code value} cannot be set in {@code array}.
     *
     */
    public static <T> void setValue(Object array, T value, int ... index) {
        setValue(0,array,value,index);
    }

    private static <T> void setValue(int position, Object array, T value, int ... dimensions) {
        if (!array.getClass().isArray())
            throw new IllegalArgumentException(String.format("Object accessed with dimension %s not an array: %s",Arrays.toString(Arrays.copyOfRange(dimensions,position,dimensions.length)),array));
        if (position == (dimensions.length-1))
            Array.set(array,dimensions[position],value);
        else
            setValue(position+1,Array.get(array,dimensions[position]),value,dimensions);
    }

    /**
     * Get the value of an array at the specified position.
     *
     * @param array
     *        The array.
     *
     * @param index
     *        The index position of the new value.
     *
     * @param <T>
     *        The object type held by the array.
     *
     * @return the value of {@code array} at position {@code index}.
     *
     * @throws IllegalArgumentException if {@code array}'s length is not equal to {@code index.length}.
     *
     */
    public static <T> T getValue(Object array, int ... index) {
        return getValue(0,array,index);
    }

    @SuppressWarnings("unchecked") //cannot ensure array will hold T, but if not it is a user error, so suppressing
    private static <T> T getValue(int position, Object array, int ... dimensions) {
        if (!array.getClass().isArray())
            throw new IllegalArgumentException(String.format("Object accessed with dimension %s not an array: %s",Arrays.toString(Arrays.copyOfRange(dimensions,position,dimensions.length)),array));
        return (T) (position == (dimensions.length-1) ? Array.get(array,dimensions[position]) : getValue(position+1,Array.get(array,dimensions[position]),dimensions));
    }
    //todo: add primitive versions of these get methods

    /////////////////////Reflective copier/////////////////////////////

    /**
     * Convenience method to copy a {@code byte} array.
     *
     * @param array
     *        The input array.
     *
     * @return an array identical in size and contents to {@code array}.
     */
    public static byte[] copyArray(byte[] array) {
        return Arrays.copyOf(array,array.length);
    }

    /**
     * Convenience method to copy a {@code short} array.
     *
     * @param array
     *        The input array.
     *
     * @return an array identical in size and contents to {@code array}.
     */
    public static short[] copyArray(short[] array) {
        return Arrays.copyOf(array,array.length);
    }

    /**
     * Convenience method to copy a {@code int} array.
     *
     * @param array
     *        The input array.
     *
     * @return an array identical in size and contents to {@code array}.
     */
    public static int[] copyArray(int[] array) {
        return Arrays.copyOf(array,array.length);
    }

    /**
     * Convenience method to copy a {@code long} array.
     *
     * @param array
     *        The input array.
     *
     * @return an array identical in size and contents to {@code array}.
     */
    public static long[] copyArray(long[] array) {
        return Arrays.copyOf(array,array.length);
    }

    /**
     * Convenience method to copy a {@code float} array.
     *
     * @param array
     *        The input array.
     *
     * @return an array identical in size and contents to {@code array}.
     */
    public static float[] copyArray(float[] array) {
        return Arrays.copyOf(array,array.length);
    }

    /**
     * Convenience method to copy a {@code double} array.
     *
     * @param array
     *        The input array.
     *
     * @return an array identical in size and contents to {@code array}.
     */
    public static double[] copyArray(double[] array) {
        return Arrays.copyOf(array,array.length);
    }

    /**
     * Convenience method to copy a {@code char} array.
     *
     * @param array
     *        The input array.
     *
     * @return an array identical in size and contents to {@code array}.
     */
    public static char[] copyArray(char[] array) {
        return Arrays.copyOf(array,array.length);
    }

    /**
     * Convenience method to copy a {@code boolean} array.
     *
     * @param array
     *        The input array.
     *
     * @return an array identical in size and contents to {@code array}.
     */
    public static boolean[] copyArray(boolean[] array) {
        return Arrays.copyOf(array,array.length);
    }

    /**
     * Copy an array. This method descends as deeply into the array and recursively copies any arrays that it finds.
     * Though the copied arrays are newly instantiated, the components of the array are copied using
     * {@code System.arrayCopy(...)}, which means that only an object reference, not a newly allocated object, is copied.
     * In other words, this method performs a "deep" copy with respect to arrays, but a "shallow" copy with respect to
     * array components.
     *
     * @param array
     *        The array to copy.
     *
     * @param <T>
     *        The component type of the input and output arrays.
     *
     * @return a copy of {@code array}.
     */
    @SuppressWarnings("unchecked") //copyArray(Object) will definitely return an array of same type as input
    public static <T> T[] copyArray(T[] array) {
        return (T[]) (copyArray((Object) array));
    }

    /**
     * Copy an array. This method descends as deeply into the array and recursively copies any arrays that it finds.
     * Though the copied arrays are newly instantiated, the components of the array are copied using
     * {@code System.arrayCopy(...)}, which means that, in the case of objects, only an object reference, not a newly
     * allocated object, is copied. In other words, this method performs a "deep" copy with respect to arrays, but a
     * "shallow" copy with respect to array components.
     *
     * @param array
     *        The array to copy.
     *
     * @return a copy of {@code array}.
     *
     * @throws IllegalArgumentException if {@code array} is not an array.
     */
    public static Object copyArray(Object array) {
        Class arrayClass = array.getClass();
        if (!arrayClass.isArray())
            throw new IllegalArgumentException("Parameter array must be an array!");
        int arrayLength = Array.getLength(array);
        Class componentType = arrayClass.getComponentType();
        Object newArray = Array.newInstance(arrayClass.getComponentType(),arrayLength);
        if (componentType.isArray())
            //loop over elements and copy arrays as needed
            for (int i = 0; i < arrayLength; i++) {
                Object element = Array.get(array,i);
                Array.set(newArray,i,element == null ? null : copyArray(element));
            }
        else if (componentType == Object.class)
            //loop over elements and see if we have any arrays
            for (int i = 0; i < arrayLength; i++) {
                Object element = Array.get(array,i);
                Array.set(newArray,i,element != null && element.getClass().isArray() ? copyArray(element) : element);
            }
        else
            System.arraycopy(array,0,newArray,0,arrayLength);
        return newArray;
    }

    /**
     * Copy an array's contents to another array. This method descends into the source array and copies each element
     * into the corresponding position in the destination array. Thus, in the case of multidimensional arrays, the array
     * elements are copied, but not the composing sub-arrays. Thus, the source and destination arrays should be of the
     * same shape; an exception will be thrown if the destination array is too small (or non-existent) in any of the
     * source array's dimensions, but not if the destination array is too big.
     *
     * @param srcArray
     *        The source array.
     *
     * @param destArray
     *        The destination array.
     *
     * @throws ArrayStoreException if {@code destArray} is not an array in any dimension where {@code srcArray} is, or
     *                             if the elements in {@code srcArray} cannot be held by {@code destArray}.
     * @throws IndexOutOfBoundsException if {@code destArray}'s length in any dimension is smaller than {@code srcArray}.
     */
    public static void copyArray(Object srcArray, Object destArray) {
        copyArray(srcArray,destArray,getDimensionCount(srcArray));
    }

    private static void copyArray(Object srcArray, Object destArray, int dim) {
        int length = Array.getLength(srcArray);
        switch (dim) {
            case 1 : System.arraycopy(srcArray,0,destArray,0,length); break;
            default : {
                for (int i = 0; i < length; i++)
                    copyArray(((Object[]) srcArray)[i],((Object[]) destArray)[i],dim-1);
            }
        }
    }

    /////////////////////Subarray////////////////////////
    /**
     * Builds a new array from an input array and a list of indices.  The order of the indices determines the order
     * of the elements in the new array, and the indices may repeat.  For example, the following code snippet:
     * <pre><tt>
     *     String[] input = new String{"a","b","c"};
     *     int[] indices = new int[] {2,2,3,2};
     *     String output = buildArray(input,indices);
     * </pre></tt>
     * will create <tt>output</tt> such that it looks like <tt>["b","b","c","b"]</tt>.
     *
     * @param inputArray
     *        The array to build the new array from.
     *
     * @param indices
     *        The indices of {@code inputArray} which specify the order and elements in the new array.
     *
     * @param <T>
     *        The component type of the input and output arrays.
     *
     * @return a new array build from {@code indices} and {@code inputArray}.
     *
     * @throws ArrayIndexOutOfBoundsException if {@code indices} contains any values less than <tt>0</tt> or greater than
     *                                   <tt>1-inputArray.length</tt>.
     */
     public static <T> T[] buildArray(T[] inputArray, int[] indices) {
        @SuppressWarnings("unchecked") //input array has component type T, so we are safe here
        T[] outputArray = (T[]) Array.newInstance(inputArray.getClass().getComponentType(),indices.length);
        int counter = 0;
        for (int column : indices)
            outputArray[counter++] = inputArray[column];
        return outputArray;
    }

    private static <A> A buildPrimitiveArray(A inputArray, int[] indices) {
        //only called if inputArray is primitive array
        @SuppressWarnings("unchecked") //creating an array equal in structure to input array, so ok
        A outputArray = (A) Array.newInstance(inputArray.getClass().getComponentType(),indices.length);
        int counter = 0;
        for (int column : indices)
            Array.set(outputArray,counter++,Array.get(inputArray,column));
        return outputArray;
    }

    /**
     * Builds a new array from an input array and a list of indices. The order of the indices determines the order
     * of the elements in the new array, and the indices may repeat. For example, the following code snippet:
     * <pre><tt>
     *     String[] input = new String{"a","b","c"};
     *     int[] indices = new int[] {2,2,3,2};
     *     String output = buildArray(input,indices);
     * </pre></tt>
     * will create <tt>output</tt> such that it looks like <tt>["b","b","c","b"]</tt>.
     * <p>
     * This method works with both primitive and object arrays, though for object arrays, {@code buildArray(T[], int[])}
     * will be slightly more efficient and generally more typesafe.
     *
     * @param inputArray
     *        The array to build the new array from.
     *
     * @param indices
     *        The indices of {@code inputArray} which specify the order and elements in the new array.
     *
     * @param <A>
     *        The array type of the input and output objects.
     *
     * @return a new array build from {@code indices} and {@code inputArray}.
     *
     * @throws IllegalArgumentException if {@code inputArray} is not an array.
     *
     * @throws IndexOutOfBoundsException if {@code indices} contains any values less than <tt>0</tt> or greater than
     *                                   <tt>1-inputArray.length</tt>.
     */
    public static <A> A buildArray(A inputArray, int[] indices) {
        Class arrayClass = inputArray.getClass();
        if (!arrayClass.isArray())
            throw new IllegalArgumentException("buildArray paramter inputArray must be an array!");
        @SuppressWarnings("unchecked") //ok because build array returns back an Object[], which was put in 
        A builtArray = arrayClass.getComponentType().isPrimitive() ?
                buildPrimitiveArray(inputArray,indices) :
                (A) buildArray((Object[]) inputArray,indices);
        return builtArray;
    }

    /////////////////////Concatenation////////////////////
    /**
     * Concatenate a series of byte arrays.
     *
     * @param arrays
     *        The arrays to concatenate.
     *
     * @return an array holding all of the elements, in order, in {@code arrays}.
     */
    public static byte[] concatenateArrays(byte[] ... arrays) {
        if (arrays.length == 0)
            throw new IllegalArgumentException("Concatenate arrays must have at least one array as an argument.");
        int size = 0;
        for (byte[] array : arrays)
            size += array.length;
        byte[] finalArray = new byte[size];
        int position = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array,0,finalArray,position,array.length);
            position += array.length;
        }
        return finalArray;
    }

    /**
     * Concatenate a series of short arrays.
     *
     * @param arrays
     *        The arrays to concatenate.
     *
     * @return an array holding all of the elements, in order, in {@code arrays}.
     */
    public static short[] concatenateArrays(short[] ... arrays) {
        if (arrays.length == 0)
            throw new IllegalArgumentException("Concatenate arrays must have at least one array as an argument.");
        int size = 0;
        for (short[] array : arrays)
            size += array.length;
        short[] finalArray = new short[size];
        int position = 0;
        for (short[] array : arrays) {
            System.arraycopy(array,0,finalArray,position,array.length);
            position += array.length;
        }
        return finalArray;
    }

    /**
     * Concatenate a series of int arrays.
     *
     * @param arrays
     *        The arrays to concatenate.
     *
     * @return an array holding all of the elements, in order, in {@code arrays}.
     */
    public static int[] concatenateArrays(int[] ... arrays) {
        if (arrays.length == 0)
            throw new IllegalArgumentException("Concatenate arrays must have at least one array as an argument.");
        int size = 0;
        for (int[] array : arrays)
            size += array.length;
        int[] finalArray = new int[size];
        int position = 0;
        for (int[] array : arrays) {
            System.arraycopy(array,0,finalArray,position,array.length);
            position += array.length;
        }
        return finalArray;
    }

    /**
     * Concatenate a series of long arrays.
     *
     * @param arrays
     *        The arrays to concatenate.
     *
     * @return an array holding all of the elements, in order, in {@code arrays}.
     */
    public static long[] concatenateArrays(long[] ... arrays) {
        if (arrays.length == 0)
            throw new IllegalArgumentException("Concatenate arrays must have at least one array as an argument.");
        int size = 0;
        for (long[] array : arrays)
            size += array.length;
        long[] finalArray = new long[size];
        int position = 0;
        for (long[] array : arrays) {
            System.arraycopy(array,0,finalArray,position,array.length);
            position += array.length;
        }
        return finalArray;
    }

    /**
     * Concatenate a series of float arrays.
     *
     * @param arrays
     *        The arrays to concatenate.
     *
     * @return an array holding all of the elements, in order, in {@code arrays}.
     */
    public static float[] concatenateArrays(float[] ... arrays) {
        if (arrays.length == 0)
            throw new IllegalArgumentException("Concatenate arrays must have at least one array as an argument.");
        int size = 0;
        for (float[] array : arrays)
            size += array.length;
        float[] finalArray = new float[size];
        int position = 0;
        for (float[] array : arrays) {
            System.arraycopy(array,0,finalArray,position,array.length);
            position += array.length;
        }
        return finalArray;
    }

    /**
     * Concatenate a series of double arrays.
     *
     * @param arrays
     *        The arrays to concatenate.
     *
     * @return an array holding all of the elements, in order, in {@code arrays}.
     */
    public static double[] concatenateArrays(double[] ... arrays) {
        if (arrays.length == 0)
            throw new IllegalArgumentException("Concatenate arrays must have at least one array as an argument.");
        int size = 0;
        for (double[] array : arrays)
            size += array.length;
        double[] finalArray = new double[size];
        int position = 0;
        for (double[] array : arrays) {
            System.arraycopy(array,0,finalArray,position,array.length);
            position += array.length;
        }
        return finalArray;
    }

    /**
     * Concatenate a series of boolean arrays.
     *
     * @param arrays
     *        The arrays to concatenate.
     *
     * @return an array holding all of the elements, in order, in {@code arrays}.
     */
    public static boolean[] concatenateArrays(boolean[] ... arrays) {
        if (arrays.length == 0)
            throw new IllegalArgumentException("Concatenate arrays must have at least one array as an argument.");
        int size = 0;
        for (boolean[] array : arrays)
            size += array.length;
        boolean[] finalArray = new boolean[size];
        int position = 0;
        for (boolean[] array : arrays) {
            System.arraycopy(array,0,finalArray,position,array.length);
            position += array.length;
        }
        return finalArray;
    }

    /**
     * Concatenate a series of char arrays.
     *
     * @param arrays
     *        The arrays to concatenate.
     *
     * @return an array holding all of the elements, in order, in {@code arrays}.
     */
    public static char[] concatenateArrays(char[] ... arrays) {
        if (arrays.length == 0)
            throw new IllegalArgumentException("Concatenate arrays must have at least one array as an argument.");
        int size = 0;
        for (char[] array : arrays)
            size += array.length;
        char[] finalArray = new char[size];
        int position = 0;
        for (char[] array : arrays) {
            System.arraycopy(array,0,finalArray,position,array.length);
            position += array.length;
        }
        return finalArray;
    }

    /**
     * Concatenate a series of object arrays.
     *
     * @param arrayClass
     *        The component type of the output array.
     *
     * @param arrays
     *        The arrays to concatenate.
     *
     * @return an array holding all of the elements, in order, in {@code arrays}.
     */
    @SuppressWarnings("unchecked") //ok, because we specify the array class
    public static <A> A[] concatenateArrays(Class<A> arrayClass, A[] ... arrays) {
        if (arrays.length == 0)
            throw new IllegalArgumentException("Concatenate arrays must have at least one array as an argument.");
        int size = 0;
        for (A[] array : arrays)
            size += array.length;
        A[] finalArray = (A[]) Array.newInstance(arrayClass,size);
        int position = 0;
        for (A[] array : arrays)
            for (A a : array)
                finalArray[position++] = a;
        return finalArray;
    }

    /////////////////////Reversal////////////////////////
    /**
     * Get the reverse of a {@code byte} array. The returned array will contain the same elements as the input in 
     * reverse order.
     * 
     * @param array
     *        The input array.
     * 
     * @return an array which is the reverse of {@code array}.
     */
    public static byte[] getReverse(byte[] array) {
        int len = array.length;
        int len1 = len-1;
        byte[] r = new byte[len];
        for (int i = 0; i < len; i++)
            r[i] = array[len1-i];
        return r;
    }

    /**
     * Reverse the elements of a {@code byte} array.
     * 
     * @param array
     *        The array to reverse.
     */
    public static void reverse(byte[] array) {
        byte temp;
        for (int i = 0,j = array.length-1; i < j; i++,j--) {
            temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }    
    }

    /**
     * Get the reverse of a {@code short} array. The returned array will contain the same elements as the input in 
     * reverse order.
     * 
     * @param array
     *        The input array.
     * 
     * @return an array which is the reverse of {@code array}.
     */
    public static short[] getReverse(short[] array) {
        int len = array.length;   
        int len1 = len-1;
        short[] r = new short[len];
        for (int i = 0; i < len; i++)
            r[i] = array[len1-i];
        return r;
    }

    /**
     * Reverse the elements of a {@code short} array.
     * 
     * @param array
     *        The array to reverse.
     */
    public static void reverse(short[] array) {
        short temp;
        for (int i = 0,j = array.length-1; i < j; i++,j--) {
            temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }    
    }
    
    /**
     * Get the reverse of a {@code int} array. The returned array will contain the same elements as the input in 
     * reverse order.
     * 
     * @param array
     *        The input array.
     * 
     * @return an array which is the reverse of {@code array}.
     */
    public static int[] getReverse(int[] array) {
        int len = array.length; 
        int len1 = len-1;
        int[] r = new int[len];
        for (int i = 0; i < len; i++)
            r[i] = array[len1-i];
        return r;
    }

    /**
     * Reverse the elements of a {@code int} array.
     * 
     * @param array
     *        The array to reverse.
     */
    public static void reverse(int[] array) {
        int temp;
        for (int i = 0,j = array.length-1; i < j; i++,j--) {
            temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }    
    }
    
    /**
     * Get the reverse of a {@code long} array. The returned array will contain the same elements as the input in 
     * reverse order.
     * 
     * @param array
     *        The input array.
     * 
     * @return an array which is the reverse of {@code array}.
     */
    public static long[] getReverse(long[] array) {
        int len = array.length;  
        int len1 = len-1;
        long[] r = new long[len];
        for (int i = 0; i < len; i++)
            r[i] = array[len1-i];
        return r;
    }

    /**
     * Reverse the elements of a {@code long} array.
     * 
     * @param array
     *        The array to reverse.
     */
    public static void reverse(long[] array) {
        long temp;
        for (int i = 0,j = array.length-1; i < j; i++,j--) {
            temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }    
    }
    
    /**
     * Get the reverse of a {@code float} array. The returned array will contain the same elements as the input in 
     * reverse order.
     * 
     * @param array
     *        The input array.
     * 
     * @return an array which is the reverse of {@code array}.
     */
    public static float[] getReverse(float[] array) {
        int len = array.length; 
        int len1 = len-1;
        float[] r = new float[len];
        for (int i = 0; i < len; i++)
            r[i] = array[len1-i];
        return r;
    }

    /**
     * Reverse the elements of a {@code float} array.
     * 
     * @param array
     *        The array to reverse.
     */
    public static void reverse(float[] array) {
        float temp;
        for (int i = 0,j = array.length-1; i < j; i++,j--) {
            temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }    
    }
    
    /**
     * Get the reverse of a {@code double} array. The returned array will contain the same elements as the input in 
     * reverse order.
     * 
     * @param array
     *        The input array.
     * 
     * @return an array which is the reverse of {@code array}.
     */
    public static double[] getReverse(double[] array) {
        int len = array.length;
        int len1 = len-1;
        double[] r = new double[len];
        for (int i = 0; i < len; i++)
            r[i] = array[len1-i];
        return r;
    }

    /**
     * Reverse the elements of a {@code double} array.
     * 
     * @param array
     *        The array to reverse.
     */
    public static void reverse(double[] array) {
        double temp;
        for (int i = 0,j = array.length-1; i < j; i++,j--) {
            temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }    
    }
    
    /**
     * Get the reverse of a {@code char} array. The returned array will contain the same elements as the input in 
     * reverse order.
     * 
     * @param array
     *        The input array.
     * 
     * @return an array which is the reverse of {@code array}.
     */
    public static char[] getReverse(char[] array) {
        int len = array.length;
        int len1 = len-1;
        char[] r = new char[len];
        for (int i = 0; i < len; i++)
            r[i] = array[len1-i];
        return r;
    }

    /**
     * Reverse the elements of a {@code char} array.
     * 
     * @param array
     *        The array to reverse.
     */
    public static void reverse(char[] array) {
        char temp;
        for (int i = 0,j = array.length-1; i < j; i++,j--) {
            temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }    
    }
    
    /**
     * Get the reverse of a {@code boolean} array. The returned array will contain the same elements as the input in 
     * reverse order.
     * 
     * @param array
     *        The input array.
     * 
     * @return an array which is the reverse of {@code array}.
     */
    public static boolean[] getReverse(boolean[] array) {
        int len = array.length; 
        int len1 = len-1;
        boolean[] r = new boolean[len];
        for (int i = 0; i < len; i++)
            r[i] = array[len1-i];
        return r;
    }

    /**
     * Reverse the elements of a {@code boolean} array.
     * 
     * @param array
     *        The array to reverse.
     */
    public static void reverse(boolean[] array) {
        boolean temp;
        for (int i = 0,j = array.length-1; i < j; i++,j--) {
            temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }    
    }

    /**
     * Get the reverse of an array. The returned array will contain the same elements as the input in reverse order and
     * will be of the same type as the input.
     *
     * @param array
     *        The input array.
     *
     * @return an array which is the reverse of {@code array}.
     *
     * @throws IllegalArgumentException if {@code array} is not an array.
     */
    public static Object getReverse(Object array) {   
        int len = Array.getLength(array);
        int len1 = len-1;
        Object a = Array.newInstance(array.getClass().getComponentType(),len);
        for (int i = 0; i < len; i++)
            Array.set(a,i,Array.get(array,len1-i));
        return a;
    }

    /**
     * Get the reverse of an array. The returned array will contain the same elements as the input in reverse order.
     *
     * @param array
     *        The input array.
     *
     * @param componentClass
     *        The class representing the component type of the input and output arrays.
     *
     * @param <E>
     *        The type held by the input and output arrays.
     *
     * @return an array which is the reverse of {@code array}.
     */
    public static <E> E[] getReverse(E[] array, Class<E> componentClass) {
        int len = Array.getLength(array);  
        int len1 = len-1;
        @SuppressWarnings("unchecked") //ok because component class defines array type
        E[] a = (E[]) Array.newInstance(componentClass,len);
        for (int i = 0; i < len; i++)
            Array.set(a,i,Array.get(array,len1-i));
        return a;
    }

    /**
     * Reverse the elements of an array.
     *
     * @param <E>
     *        The type held by the array.
     *
     * @param array
     *        The array to reverse.
     */
    public static <E> void reverse(E[] array) {
        E temp;
        for (int i = 0,j = array.length-1; i < j; i++,j--) {
            temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    /////////////////////Iterators///////////////////////
    /**
     * Get an iterator over an object array. The iterator does not lock/synchronize on the input array, and should not
     * be considered threadsafe.
     *
     * @param array
     *        The array to create the iterator for.
     *
     * @param <E>
     *        The type of element returned by the iterator at each iteration.
     *
     * @return an iterator over {@code array}.
     */
    public static <E> Iterator<E> getIterator(final E[] array) {
        return new Iterator<E>() {
            private int currentPoint = 0;

            public boolean hasNext() {
                return currentPoint < array.length;
            }

            public E next() {
                if (hasNext())
                    return array[currentPoint++];
                throw new NoSuchElementException();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    private static Iterator<Object> getPrimitiveIterator(final Object array, final JavaType arrayType) {
        //only called if array is already checked to be primitive array
        final int arrayLength;
        switch (arrayType) {
            case BOOLEAN : arrayLength = ((boolean[]) array).length; break;
            case BYTE : arrayLength = ((byte[]) array).length; break;
            case SHORT : arrayLength = ((short[]) array).length; break;
            case INT : arrayLength = ((int[]) array).length; break;
            case LONG : arrayLength = ((long[]) array).length; break;
            case FLOAT : arrayLength = ((float[]) array).length; break;
            case DOUBLE : arrayLength = ((double[]) array).length; break;
            case CHAR : arrayLength = ((char[]) array).length; break;
            default : throw new IllegalStateException("Invalid array in iterator; caused by: " + arrayType); //shouldn't get here
        }
        return new Iterator<Object>() {
            private int currentPoint = 0;

            public boolean hasNext() {
                return currentPoint < arrayLength;
            }

            public Object next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                switch (arrayType) {
                    case BOOLEAN : return ((boolean[]) array)[currentPoint++];
                    case BYTE : return ((byte[]) array)[currentPoint++];
                    case SHORT : return ((short[]) array)[currentPoint++];
                    case INT : return ((int[]) array)[currentPoint++];
                    case LONG : return ((long[]) array)[currentPoint++];
                    case FLOAT : return ((float[]) array)[currentPoint++];
                    case DOUBLE : return ((double[]) array)[currentPoint++];
                    case CHAR : return ((char[]) array)[currentPoint++];
                    default : throw new IllegalStateException("Invalid iterator state; caused by : " + arrayType); //shouldn't get here
                }
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Get an iterator over an array. What the iterator iterates over (primitives or objects) is determined internally.
     * The iterator does not lock/synchronize on the input array, and should not be considered threadsafe.
     *
     * @param array
     *        The array to create the iterator for.
     *
     * @return an iterator over {@code array}.
     *
     * @throws IllegalArgumentException if {@code array} is not an array.
     */
    public static Iterator getIterator(Object array) {
        JavaType arrayType;
        try {
            arrayType = JavaType.getComponentType(array);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Input argument to getIterator must be an array!");
        }
        if (arrayType.isPrimitive())
            return getPrimitiveIterator(array,arrayType);
        else
            return getIterator((Object[]) array);
    }

    /**
     * Get an iterator which moderately "thread safer" than that returned by {@code getIterator(Object)}.
     * This method creates an iterator from a copy of the input array, which helps to decouple the calling application
     * from this method.  However, the copy of the array is performed using {@code copyArray(Object)}, which means
     * that the elements of the array are only "shallow" copied. Therefore, unless aceess to mutable array components
     * (outside of arrays - since {@code copyArray(Object)} copies all arrays within an array) is controlled in such
     * a way to make them threadsafe, this iterator cannot be considered fully threadsafe.
     *
     * @param array
     *        The array to create the iterator for.
     *
     * @return an iterator over a copy of {@code array}.
     *
     * @throws IllegalArgumentException if {@code array} is not an array.
     */
    public static Iterator getThreadSaferIterator(Object array) {
        return getIterator(copyArray(array));
    }

    /**
     * Get an iterator which moderately "thread safer" than that returned by {@code getIterator(Object)}.
     * This method creates an iterator from a copy of the input array, which helps to decouple the calling application
     * from this method.  However, the copy of the array is performed using {@code copyArray(Object)}, which means
     * that the elements of the array are only "shallow" copied. Therefore, unless aceess to mutable array components
     * (outside of arrays - since {@code copyArray(T[])} copies all arrays within an array) is controlled in such
     * a way to make them threadsafe, this iterator cannot be considered fully threadsafe.
     *
     * @param array
     *        The array to create the iterator for.
     *
     * @param <E>
     *        The type of element returned by the iterator at each iteration.
     *
     * @return an iterator over a copy of {@code array}.
     */
    public static <E> Iterator<E> getThreadSaferIterator(E[] array) {
        return getIterator(copyArray(array));
    }


    /////////////////////////////Apply functions///////////////////////////
    /**
     * Apply a function to each element in an array of inputs, returning an array of outputs. More precisely, the
     * {@code ith} element of the output array is equal to {@code function.apply(values[i])}. The output array is
     * dynamically generated based on the result of {@code function.apply(Y)}, so the input {@code values} must have at
     * least one value. The returned array will be of type {@code Z extends Y}, where {@code Z} is the least common
     * class of all of the outputs, as defined by {@link  com.pb.sawdust.util.ClassInspector#getLowestCommonClass(Class, Class)}.
     *
     * @param values
     *        An array of values to apply the function to.
     *
     * @param function
     *        The function the values will be appplied to.
     *
     * @param <X>
     *        The supertype of the input of {@code function}.
     *
     * @param <Y>
     *        The type of the output of {@code function}.
     *
     * @param <Z>
     *        The component type of the returned array.
     *
     * @return an array of outputs.
     *
     * @throws IllegalArgumentException if {@code values} does not contain at least one value.
     */
    @SuppressWarnings("unchecked") //all the Z stuff is ok because Z must extend Y, which function returns
    public static <X,Y,Z extends Y> Z[] apply(X[] values, Function1<? super X,Y> function) {
        int valueSize = values.length;
        if (valueSize == 0)
            throw new IllegalArgumentException("Values must have at least one value if output array is not specified.");
        List<Z> outputs = new LinkedList<Z>();
        Class<Z> leastCommonClass = null;
        boolean first = true;
        for (X value : values) {
            Z output = (Z) function.apply(value);
            outputs.add(output);
            if (first) {
                first = false;
                leastCommonClass = (Class<Z>) output.getClass();
            } else {
                leastCommonClass = (Class<Z>) ClassInspector.getLowestCommonClass(leastCommonClass,(Class<Z>) output.getClass());
            }
        }
        return outputs.toArray((Z[]) Array.newInstance(leastCommonClass,valueSize));
    }

    /**
     * Apply a function to each element in an array of inputs, returning an array of outputs. More precisely, the
     * {@code ith} element of the output array is equal to {@code function.apply(values[i])}. The supplied output array
     * will be used if {@code values.length == outputArray.length}, otherwise reflection will be used to dynamically
     * create a correctly sized array.
     *
     * @param values
     *        An array of values to apply the function to.
     *
     * @param function
     *        The function the values will be appplied to.
     *
     * @param outputArray
     *        A representative output array to put the outputs in.
     *
     * @param <X>
     *        The supertype of the input of {@code function}.
     *
     * @param <Y>
     *        The type of the output of {@code function} and the component type of {@code outputArray}.
     *
     * @return an array of outputs.
     */
    public static <X,Y> Y[] apply(X[] values, Function1<? super X,? extends Y> function, Y[] outputArray) {
        int valueSize = values.length;
        if (valueSize != outputArray.length) {
            @SuppressWarnings("unchecked")  //ok because putting in a Y[] to get a Y[]
            Y[] newOutputArray = (Y[]) Array.newInstance(outputArray.getClass().getComponentType(),valueSize);
            outputArray = newOutputArray;  
        }
        for (int i=0; i<valueSize; i++)
            outputArray[i] = function.apply(values[i]);
        return outputArray;
    }
}
