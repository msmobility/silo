package com.pb.sawdust.util.array;

import java.lang.reflect.Array;

/**
 * The {@code CharTypeSafeArray} provides a type safe wrapper for (possibly multidimensional) {@code char} arrays.
 * Though this class implements {@code TypeSafeArray<Character>}, efficiencies will be had if method using primitive
 * {@code char}s (instead of object {@code Character}s) are used.
 *
 * @author crf <br/>
 *         Started: Jan 8, 2009 11:27:43 AM
 */
public class CharTypeSafeArray implements TypeSafeArray<Character> {
    private final Object array;
    private final int dimensions;

    /**
     * Constructor specifying the array to wrap. The wrapped array may be multidimensional.
     *
     * @param array
     *        The array to wrap.
     *
     * @throws IllegalArgumentException if {@code array} is not a {@code char} array.
     */
    public CharTypeSafeArray(Object array) {
        if (ArrayUtil.getBaseComponentType(array) != char.class)
            throw new IllegalArgumentException("Array base component must be char, not " + ArrayUtil.getBaseComponentType(array));
        this.array = array;
        this.dimensions= ArrayUtil.getDimensionCount(array);
    }

    /**
     * {@inheritDoc}
     * The returned array will be a primitive {@code char} array, not an object {@code Character} array.
     */
    public Object getArray() {
        return array;
    }

    public int getArrayDimensions() {
        return dimensions;
    }

    /**
     * Get the value of the wrapped array at a specified location.
     *
     * @param indices
     *        The (0-based) location in the array.
     *
     * @return the value at {@code indices}.
     *
     * @throws IllegalArgumentException if the number of indices does not match the number of dimensions of the wrapped
     *                                  array (<i>i.e.</i> if {@code indices.length != getArrayDimensions()}).
     * @throws ArrayIndexOutOfBoundsException if any index in {@code indices} is less than zero or greater than or equal
     *                                        to the size of the wrapped array in the corresponding dimension.
     */
    @SuppressWarnings("unchecked") //type verified by constructor, as is size, so we are ok
    public char get(int ... indices) {
        if (indices.length != dimensions)
            throw new IllegalArgumentException("Index count must match array dimension count: expected " + dimensions + ", found " + indices.length);
        switch (dimensions) {
            case 0 : throw new ArrayIndexOutOfBoundsException("Cannot get value from empty array");
            case 1 : return ((char[]) array)[indices[0]];
            case 2 : return ((char[][]) array)[indices[0]][indices[1]];
            case 3 : return ((char[][][]) array)[indices[0]][indices[1]][indices[2]];
            case 4 : return ((char[][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]];
            case 5 : return ((char[][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]];
            case 6 : return ((char[][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]];
            case 7 : return ((char[][][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]][indices[6]];
            case 8 : return ((char[][][][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]][indices[6]][indices[7]];
            case 9 : return ((char[][][][][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]][indices[6]][indices[7]][indices[8]];
        }
        Object subArray = array;
        int lastValue = indices.length-1;
        for (int i = 0; i < lastValue; i++)
            subArray = Array.get(subArray,indices[i]);
        return ((char[]) subArray)[indices[lastValue]];
    }

    /**
     * Get the "last" single-dimensional array in the wrapped array at a specified position. The {@code indices} parameter
     * specifies an array location in all but the last dimension of the wrapped array; the returned array contains the
     * array elements at that position.
     *
     * @param indices
     *        The location of the "ultimate" array.
     *
     * @return the array elements at {@code indices}.
     *
     * @throws IllegalArgumentException if the number of indices is not equal to one less than the dimension count of
     *                                  the wrapped array (<i>i.e.</i> if {@code indices.length != getArrayDimensions()-1}).
     * @throws ArrayIndexOutOfBoundsException if any index in {@code indices} is less than zero or greater than or equal
     *                                        to the size of the wrapped array in the corresponding dimension.
     */
    @SuppressWarnings("unchecked") //type verified by constructor, as is size, so we are ok
    public char[] getUltimateArray(int ... indices) {
        if (indices.length != dimensions-1)
            throw new IllegalArgumentException("Index count must be one less than aray dimensions: expected " + (dimensions-1) + ", found " + indices.length);
        switch (indices.length) {
            case 0 : return (char[]) array;
            case 1 : return ((char[][]) array)[indices[0]];
            case 2 : return ((char[][][]) array)[indices[0]][indices[1]];
            case 3 : return ((char[][][][]) array)[indices[0]][indices[1]][indices[2]];
            case 4 : return ((char[][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]];
            case 5 : return ((char[][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]];
            case 6 : return ((char[][][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]];
            case 7 : return ((char[][][][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]][indices[6]];
            case 8 : return ((char[][][][][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]][indices[6]][indices[7]];
        }
        Object value = array;
        for (int index : indices)
            value = Array.get(value,index);
        return (char[]) value;
    }

    /**
     * Set the value of the wrapped array at a specified location.
     *
     * @param value
     *        The value to place in the array.
     *
     * @param indices
     *        The (0-based) location in the array.
     *
     * @throws IllegalArgumentException if the number of indices does not match the number of dimensions of the wrapped
     *                                  array (<i>i.e.</i> if {@code indices.length != getArrayDimensions()}).
     * @throws ArrayIndexOutOfBoundsException if any index in {@code indices} is less than zero or greater than or equal
     *                                        to the size of the wrapped array in the corresponding dimension.
     */
    public void set(char value, int ... indices) {
        if (indices.length != dimensions)
            throw new IllegalArgumentException("Index count must match array dimension count: expected " + dimensions + ", found " + indices.length);
        switch (dimensions) {
            case 0 : throw new ArrayIndexOutOfBoundsException("Cannot get value from empty array");
            case 1 : ((char[]) array)[indices[0]] = value; return;
            case 2 : ((char[][]) array)[indices[0]][indices[1]] = value; return;
            case 3 : ((char[][][]) array)[indices[0]][indices[1]][indices[2]] = value; return;
            case 4 : ((char[][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]] = value; return;
            case 5 : ((char[][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]] = value; return;
            case 6 : ((char[][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]] = value; return;
            case 7 : ((char[][][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]][indices[6]] = value; return;
            case 8 : ((char[][][][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]][indices[6]][indices[7]] = value; return;
            case 9 : ((char[][][][][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]][indices[6]][indices[7]][indices[8]] = value; return;
        }
        Object subArray = array;
        int lastValue = indices.length-1;
        for (int i = 0; i < lastValue; i++)
            subArray = Array.get(subArray,indices[i]);
        ((char[]) subArray)[indices[lastValue]] = value;
    }

    public Character getValue(int ... indices) {
        return get(indices);
    }

    public void setValue(Character value, int ... indices) {
        set(value,indices);
    }
}
