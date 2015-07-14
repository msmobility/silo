package com.pb.sawdust.util;

import java.util.*;
import java.lang.reflect.Array;

import static com.pb.sawdust.util.Range.*;

/**
 * The {@code RandomDeluxe} class extends {@code java.util.Random} adding a variety of utility methods, including
 * those for use this Strings and arrays.
 *
 * @author crf <br/>
 *         Started: Sep 22, 2008 4:25:27 PM
 */
public class RandomDeluxe extends Random {
    private static final long serialVersionUID = -7168525157360901203L;

    /**
     * Constructor which will use a randomly generated seed.
     */
    public RandomDeluxe() {
        super();
    }

    /**
     * Constructor specifying the random seed.
     *
     * @param seed
     *        The initial random seed.
     */
    public RandomDeluxe(long seed) {
        super(seed);
    }

    /* ************Chars and Strings*********** */

    /**
     * Get a random character whose unicode value is between two specified bounds.
     *
     * @param minUnicodeValue
     *        The minimum (inclusive) unicode value.
     *
     * @param maxUnicodeValue
     *        The maximum (inclusive) unicode value.
     *
     * @return a random character between the two specified bounds.
     */
    public char nextChar(int minUnicodeValue, int maxUnicodeValue) {
        return (char) (minUnicodeValue + nextInt(maxUnicodeValue - minUnicodeValue + 1));
    }

    /**
     * Get a random character whose unicode value is between {@code 0} and a specified upper bound.
     *
     * @param maxUnicodeValue
     *        The maximum (inclusive) unicode value.
     *
     * @return a random character whose unicode value is between 0 and the upper bound.
     */
    public char nextChar(int maxUnicodeValue) {
        return nextChar(0,maxUnicodeValue);
    }

    /**
     * Get a random ascii text character. Non-text characters (such as backspaces) are excluded.
     *
     * @return a random ascii text character.
     */
    public char nextAsciiChar() {
        return nextChar(32,127);
    }

    /**
     * Get a random alpha-numeric text character. This includes <code>[a-zA-Z0-9]</code>.
     *
     * @return a random alpha-numeric text character.
     */
    public char nextAlphaNumericChar() {
        int i = nextInt(62);
        if (i < 10)
            return (char) (i + 48);
        else if (i < 36)
            return (char) (i + 55);
        else
            return (char) (i + 61);
    }

    /**
     * Get an array of random characters whose unicode value is between two specified bounds.
     *
     * @param nChars
     *        The number of characters to return.
     *
     * @param minUnicodeValue
     *        The minimum (inclusive) unicode value.
     *
     * @param maxUnicodeValue
     *        The maximum (inclusive) unicode value.
     *
     * @return an array of random characters between the two specified bounds.
     */
    public char[] nextChars(int nChars,int minUnicodeValue, int maxUnicodeValue) {
        char[] chars = new char[nChars];
        for (int i : range(nChars))
            chars[i] = nextChar(minUnicodeValue,maxUnicodeValue);
        return chars;
    }

    /**
     * Get an array of random characters whose unicode value is between {@code 0} and a specified upper bound.
     *
     * @param nChars
     *        The number of characters to return.
     *
     * @param maxUnicodeValue
     *        The maximum (inclusive) unicode value.
     *
     * @return an array of random characters whose unicode values are between 0 and the upper bound.
     */
    public char[] nextChars(int nChars,int maxUnicodeValue) {
        char[] chars = new char[nChars];
        for (int i : range(nChars))
            chars[i] = nextChar(maxUnicodeValue);
        return chars;
    }

    /**
     * Get an array of random ascii text characters. Non-text characters (such as backspaces) are excluded.
     *
     * @param nChars
     *        The number of characters to return.
     *
     * @return an array of random ascii characters.
     */
    public char[] nextAsciiChars(int nChars) {
        char[] chars = new char[nChars];
        for (int i : range(nChars))
            chars[i] = nextAsciiChar();
        return chars;
    }

    /**
     * Get a random ascii string of a specified length.
     *
     * @param length
     *        The sixe of the string to return.
     *
     * @return a random ascii string with {@code length} characters.
     */
    public String nextAsciiString(int length) {
        return new String(nextAsciiChars(length));
    }

    /**
     * Get an array of random alpha-numeric text characters. This includes <code>[a-zA-Z0-9]</code>.
     *
     * @param nChars
     *        The number of characters to return.
     *
     * @return an array of random alpha-numeric characters.
     */
    public char[] nextAlphaNumericChars(int nChars) {
        char[] chars = new char[nChars];
        for (int i : range(nChars))
            chars[i] = nextAlphaNumericChar();
        return chars;
    }

    /**
     * Get a random alpha-numeric string of a specified length. This includes <code>[a-zA-Z0-9]</code>.
     *
     * @param length
     *        The sixe of the string to return.
     *
     * @return a random alpha-numeric string with {@code length} characters.
     */
    public String nextAlphaNumericString(int length) {
        return new String(nextAlphaNumericChars(length));
    }

    /* *****************extra primitives************* */
    /**
     * Get a random byte between 0 and {@code Byte.MAX_VALUE}.
     *
     * @return a random byte.
     */
    public byte nextByte() {
        return (byte) nextInt(Byte.MAX_VALUE+1);
    }

    /**
     * Get a random short between 0 and {@code Short.MAX_VALUE}.
     *
     * @return a random short.
     */
    public short nextShort() {
        return (short) nextInt(Short.MAX_VALUE+1);
    }

    /* *****************ints******************** */
    /**
     * Get a random integer between two bounds. The bounds are allowed to be non-positive, but must have a
     * positive difference.
     *
     * @param min
     *        The minimum (inclusive) integer value.
     *
     * @param max
     *        The maximum (exclusive) integer value.
     *
     * @return a random integer in {@code [min,max)}.
     *
     * @throws IllegalArgumentException if {@code max <= min}.
     */
    public int nextInt(int min, int max) {
        if (min >= max)
            throw new IllegalArgumentException(String.format("Minimum (%d) must be smaller than maximum (%d).",min,max));
        if (max - min < 0) //overflow
            return nextInt(max) - nextInt(min == Integer.MIN_VALUE ? Integer.MAX_VALUE : -min); //maybe not psuedorandom enough, but ok for now
        return nextInt(max - min) + min;
    }

    /* ***************arrays ***************** */
    /**
     * Get an array of integers from 0 to a specified number, randomly ordered. This method's name comes from the fact
     * that it returns an array of random indices (if iterated through linearly) for an array of a given length.
     *
     * @param size
     *        The (exclusive) upper bound for the random integers.
     *
     * @return an array of integers on {@code [0,size)}, randomly ordered.
     */
    public int[] getRandomIndices(int size) {
        List<Integer> indices = new ArrayList<Integer>(size);
        int[] randomIndices = new int[size];
        Range r = range(size);
        for (int i : r)
            indices.add(i);
        for (int i : r)
            randomIndices[i] = indices.remove(nextInt(size-i));
        return randomIndices;
    }

    /**
     * Get an array containing the same elements as an input array, only with the elements randomly ordered.
     *
     * @param inputArray
     *        The input array.
     *
     * @param <T>
     *        The type of the input (and output) array.
     * 
     * @return an array with the same elements as {@code inputArray}, randomly distributed.
     */
    public <T> T[] randomizeArray(T[] inputArray) {
        int size = inputArray.length;
        List<Integer> indices = new ArrayList<Integer>();
        @SuppressWarnings("unchecked") //ok because we put in a T, so we'll get a T
        T[] outputArray = (T[]) Array.newInstance(inputArray.getClass().getComponentType(),size);
        for (int i : range(size))
            indices.add(i);
        for (int i : range(size))
            outputArray[i] = inputArray[indices.remove(nextInt(size-i))];
        return outputArray;
    }

    /**
     * Get a {@code boolean} array containing the same elements as an input array, only with the elements randomly ordered.
     *
     * @param inputArray
     *        The input array.
     *
     * @return an array with the same elements as {@code inputArray}, randomly distributed.
     */
    public boolean[] randomizeArray(boolean[] inputArray) {
        int size = inputArray.length;
        boolean[] outputArray = new boolean[size];
        int[] randomIndices = getRandomIndices(size);
        for (int i : range(size))
            outputArray[i] = inputArray[randomIndices[i]];
        return outputArray;
    }

    /**
     * Get a {@code byte} array containing the same elements as an input array, only with the elements randomly ordered.
     *
     * @param inputArray
     *        The input array.
     *
     * @return an array with the same elements as {@code inputArray}, randomly distributed.
     */
    public byte[] randomizeArray(byte[] inputArray) {
        int size = inputArray.length;
        byte[] outputArray = new byte[size];
        int[] randomIndices = getRandomIndices(size);
        for (int i : range(size))
            outputArray[i] = inputArray[randomIndices[i]];
        return outputArray;
    }

    /**
     * Get a {@code short} array containing the same elements as an input array, only with the elements randomly ordered.
     *
     * @param inputArray
     *        The input array.
     *
     * @return an array with the same elements as {@code inputArray}, randomly distributed.
     */
    public short[] randomizeArray(short[] inputArray) {
        int size = inputArray.length;
        short[] outputArray = new short[size];
        int[] randomIndices = getRandomIndices(size);
        for (int i : range(size))
            outputArray[i] = inputArray[randomIndices[i]];
        return outputArray;
    }

    /**
     * Get a {@code int} array containing the same elements as an input array, only with the elements randomly ordered.
     *
     * @param inputArray
     *        The input array.
     *
     * @return an array with the same elements as {@code inputArray}, randomly distributed.
     */
    public int[] randomizeArray(int[] inputArray) {
        int size = inputArray.length;
        int[] outputArray = new int[size];
        int[] randomIndices = getRandomIndices(size);
        for (int i : range(size))
            outputArray[i] = inputArray[randomIndices[i]];
        return outputArray;
    }

    /**
     * Get a {@code long} array containing the same elements as an input array, only with the elements randomly ordered.
     *
     * @param inputArray
     *        The input array.
     *
     * @return an array with the same elements as {@code inputArray}, randomly distributed.
     */
    public long[] randomizeArray(long[] inputArray) {
        int size = inputArray.length;
        long[] outputArray = new long[size];
        int[] randomIndices = getRandomIndices(size);
        for (int i : range(size))
            outputArray[i] = inputArray[randomIndices[i]];
        return outputArray;
    }

    /**
     * Get a {@code float} array containing the same elements as an input array, only with the elements randomly ordered.
     *
     * @param inputArray
     *        The input array.
     *
     * @return an array with the same elements as {@code inputArray}, randomly distributed.
     */
    public float[] randomizeArray(float[] inputArray) {
        int size = inputArray.length;
        float[] outputArray = new float[size];
        int[] randomIndices = getRandomIndices(size);
        for (int i : range(size))
            outputArray[i] = inputArray[randomIndices[i]];
        return outputArray;
    }

    /**
     * Get a {@code double} array containing the same elements as an input array, only with the elements randomly ordered.
     *
     * @param inputArray
     *        The input array.
     *
     * @return an array with the same elements as {@code inputArray}, randomly distributed.
     */
    public double[] randomizeArray(double[] inputArray) {
        int size = inputArray.length;
        double[] outputArray = new double[size];
        int[] randomIndices = getRandomIndices(size);
        for (int i : range(size))
            outputArray[i] = inputArray[randomIndices[i]];
        return outputArray;
    }

    /**
     * Get a {@code char} array containing the same elements as an input array, only with the elements randomly ordered.
     *
     * @param inputArray
     *        The input array.
     *
     * @return an array with the same elements as {@code inputArray}, randomly distributed.
     */
    public char[] randomizeArray(char[] inputArray) {
        int size = inputArray.length;
        char[] outputArray = new char[size];
        int[] randomIndices = getRandomIndices(size);
        for (int i : range(size))
            outputArray[i] = inputArray[randomIndices[i]];
        return outputArray;
    }
    
    /**********************random filled arrays******************************/
    /**
     * Get an array filled with random {@code byte}s.
     *  
     * @param size
     *        The size of the returned array.
     * 
     * @return a random {@code byte} array with length {@code size}.
     */
    public byte[] nextBytes(int size) {
        byte[] array = new byte[size];
        for (int i : range(size))
            array[i] = nextByte();
        return array;
    }
    
    /**
     * Get an array filled with random {@code short}s.
     *  
     * @param size
     *        The size of the returned array.
     * 
     * @return a random {@code short} array with length {@code size}.
     */
    public short[] nextShorts(int size) {
        short[] array = new short[size];
        for (int i : range(size))
            array[i] = nextShort();
        return array;
    }
    
    /**
     * Get an array filled with random {@code int}s.
     *  
     * @param size
     *        The size of the returned array.
     * 
     * @return a random {@code int} array with length {@code size}.
     */
    public int[] nextInts(int size) {
        int[] array = new int[size];
        for (int i : range(size))
            array[i] = nextInt();
        return array;
    }
    
    /**
     * Get an array filled with random {@code long}s.
     *  
     * @param size
     *        The size of the returned array.
     * 
     * @return a random {@code long} array with length {@code size}.
     */
    public long[] nextLongs(int size) {
        long[] array = new long[size];
        for (int i : range(size))
            array[i] = nextLong();
        return array;
    }
    
    /**
     * Get an array filled with random {@code float}s.
     *  
     * @param size
     *        The size of the returned array.
     * 
     * @return a random {@code float} array with length {@code size}.
     */
    public float[] nextFloats(int size) {
        float[] array = new float[size];
        for (int i : range(size))
            array[i] = nextFloat();
        return array;
    }
    
    /**
     * Get an array filled with random {@code double}s.
     *  
     * @param size
     *        The size of the returned array.
     * 
     * @return a random {@code double} array with length {@code size}.
     */
    public double[] nextDoubles(int size) {
        double[] array = new double[size];
        for (int i : range(size))
            array[i] = nextDouble();
        return array;
    }
    
    /***********************random collection/array elements**********************/
    
    /**
     * Get an array filled with random {@code boolean}s.
     *  
     * @param size
     *        The size of the returned array.
     * 
     * @return a random {@code boolean} array with length {@code size}.
     */
    public boolean[] nextBooleans(int size) {
        boolean[] array = new boolean[size];
        for (int i : range(size))
            array[i] = nextBoolean();
        return array;
    }
    
    private <T> T getNthValue(Iterable<T> values, int n) {
        if (n < 0)
            throw new RuntimeException("n must be greater than or equal to zero"); //not illegal argument exception so it doesn't mask exceptions accidentally
        Iterator<T> it = values.iterator();
        T value = it.next();
        while (n-- > 0)
            value = it.next();
        return value;
    }
    
    /**
     * Get a random value from a collection.
     * 
     * @param values
     *        The collection.
     * 
     * @param <T>
     *        The type held by the collection.
     * 
     * @return a random element from {@code values}.
     * 
     * @throws IllegalArgumentException if {@code values} is empty.
     */
    public <T> T getRandomValue(Collection<T> values) {
        return getNthValue(values,nextInt(values.size())); 
     }                                 

    /**
     * Get a random value from an array.
     * 
     * @param values
     *        The array.
     * 
     * @param <T>
     *        The type held by the array.
     * 
     * @return a random element from {@code values}.
     * 
     * @throws IllegalArgumentException if {@code values} is empty.
     */
    public <T> T getRandomValue(T[] values) {
        return values[nextInt(values.length)]; 
     }                                  

    /**
     * Get a random value from a {@code byte} array.
     * 
     * @param values
     *        The array.
     * 
     * @return a random element from {@code values}.
     * 
     * @throws IllegalArgumentException if {@code values} is empty.
     */
    public byte getRandomValue(byte[] values) {
        return values[nextInt(values.length)]; 
     }                        

    /**
     * Get a random value from a {@code short} array.
     * 
     * @param values
     *        The array.
     * 
     * @return a random element from {@code values}.
     * 
     * @throws IllegalArgumentException if {@code values} is empty.
     */
    public short getRandomValue(short[] values) {
        return values[nextInt(values.length)]; 
     }                

    /**
     * Get a random value from a {@code int} array.
     * 
     * @param values
     *        The array.
     * 
     * @return a random element from {@code values}.
     * 
     * @throws IllegalArgumentException if {@code values} is empty.
     */
    public int getRandomValue(int[] values) {
        return values[nextInt(values.length)]; 
     }                

    /**
     * Get a random value from a {@code long} array.
     * 
     * @param values
     *        The array.
     * 
     * @return a random element from {@code values}.
     * 
     * @throws IllegalArgumentException if {@code values} is empty.
     */
    public long getRandomValue(long[] values) {
        return values[nextInt(values.length)]; 
     }                

    /**
     * Get a random value from a {@code float} array.
     * 
     * @param values
     *        The array.
     * 
     * @return a random element from {@code values}.
     * 
     * @throws IllegalArgumentException if {@code values} is empty.
     */
    public float getRandomValue(float[] values) {
        return values[nextInt(values.length)]; 
     }                

    /**
     * Get a random value from a {@code double} array.
     * 
     * @param values
     *        The array.
     * 
     * @return a random element from {@code values}.
     * 
     * @throws IllegalArgumentException if {@code values} is empty.
     */
    public double getRandomValue(double[] values) {
        return values[nextInt(values.length)]; 
     }                

    /**
     * Get a random value from a {@code char} array.
     * 
     * @param values
     *        The array.
     * 
     * @return a random element from {@code values}.
     * 
     * @throws IllegalArgumentException if {@code values} is empty.
     */
    public char getRandomValue(char[] values) {
        return values[nextInt(values.length)]; 
     }                

    /**
     * Get a random value from a {@code boolean} array.
     * 
     * @param values
     *        The array.
     * 
     * @return a random element from {@code values}.
     * 
     * @throws IllegalArgumentException if {@code values} is empty.
     */
    public boolean getRandomValue(boolean[] values) {
        return values[nextInt(values.length)]; 
     }
}
