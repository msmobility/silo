package com.pb.sawdust.util.array;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * The {@code ObjectTypeSafeArray} provides a wrapper for object arrays (possibly multidimensional) which are
 * parameterized with the type of the array.
 *
 * @param <T>
 *        The type held by the wrapped array.
 *
 * @author crf <br/>
 *         Started: Jan 8, 2009 9:59:12 AM
 */
public class ObjectTypeSafeArray<T> implements TypeSafeArray<T> {
    private final Object array;
    private final int dimensions;

    /**
     * Constructor specifying the array to wrap. The wrapped array may be multidimensional.
     *
     * @param array
     *        The array to wrap.
     *
     * @param arrayType
     *        The parameterized type of the array. This need not necessarily be the same as the component type of the
     *        wrapped array; the only requirment is that it is assignable from the component type of the wrapped array.
     *
     * @throws IllegalArgumentException if {@code array} is not an array, or if {@code arrayType} is not assignable from
     *                                  (is a supertype/superinterface of) {@code array}'s component type.
     */
    public ObjectTypeSafeArray(Object array, Class<T> arrayType) {
        if (!arrayType.isAssignableFrom(ArrayUtil.getBaseComponentType(array)))
            throw new IllegalArgumentException("Array base component must be assignable to " + arrayType + ": " + ArrayUtil.getBaseComponentType(array));
        this.array = array;
        this.dimensions= ArrayUtil.getDimensionCount(array);
    }

    public Object getArray() {
        return array;
    }

    public int getArrayDimensions() {
        return dimensions;
    }

    /**
     * Get the value of the wrapped array at a specified location. This method is an alias of {@code getValue(int...)}.
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
    @SuppressWarnings("unchecked") //T type verified by constructor, as is size, so we are ok
    public T get(int ... indices) {
        if (indices.length != dimensions)
            throw new IllegalArgumentException("Index count must match array dimension count: expected " + dimensions + ", found " + indices.length);
        switch (dimensions) {
            case 0 : return null;
            case 1 : return ((T[]) array)[indices[0]];
            case 2 : return ((T[][]) array)[indices[0]][indices[1]];
            case 3 : return ((T[][][]) array)[indices[0]][indices[1]][indices[2]];
            case 4 : return ((T[][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]];
            case 5 : return ((T[][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]];
            case 6 : return ((T[][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]];
            case 7 : return ((T[][][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]][indices[6]];
            case 8 : return ((T[][][][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]][indices[6]][indices[7]];
            case 9 : return ((T[][][][][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]][indices[6]][indices[7]][indices[8]];
            default : return (T) getArrayValue(array,indices);
        }
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
    @SuppressWarnings("unchecked") //T type verified by constructor, as is size, so we are ok
    public T[] getUltimateArray(int ... indices) {
        if (indices.length != dimensions-1)
            throw new IllegalArgumentException("Index count must be one less than aray dimensions: expected " + (dimensions-1) + ", found " + indices.length);
        switch (indices.length) {
            case 0 : return (T[]) array;
            case 1 : return ((T[][]) array)[indices[0]];
            case 2 : return ((T[][][]) array)[indices[0]][indices[1]];
            case 3 : return ((T[][][][]) array)[indices[0]][indices[1]][indices[2]];
            case 4 : return ((T[][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]];
            case 5 : return ((T[][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]];
            case 6 : return ((T[][][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]];
            case 7 : return ((T[][][][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]][indices[6]];
            case 8 : return ((T[][][][][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]][indices[6]][indices[7]];
            default : return (T[]) getArrayValue(array,indices);
        }
    }

    /**
     * Set the value of the wrapped array at a specified location. This method is an alias of {@code setValue(T,int...)}.
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
    @SuppressWarnings("unchecked") //T type verified by constructor, as is size, so we are ok
    public void set(T value, int ... indices) {
        if (indices.length != dimensions)
            throw new IllegalArgumentException("Index count must match array dimension count: expected " + dimensions + ", found " + indices.length);
        switch (dimensions) {
            case 0 : throw new ArrayIndexOutOfBoundsException("Cannot get value from empty array");
            case 1 : ((T[]) array)[indices[0]] = value; return;
            case 2 : ((T[][]) array)[indices[0]][indices[1]] = value; return;
            case 3 : ((T[][][]) array)[indices[0]][indices[1]][indices[2]] = value; return;
            case 4 : ((T[][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]] = value; return;
            case 5 : ((T[][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]] = value; return;
            case 6 : ((T[][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]] = value; return;
            case 7 : ((T[][][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]][indices[6]] = value; return;
            case 8 : ((T[][][][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]][indices[6]][indices[7]] = value; return;
            case 9 : ((T[][][][][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]][indices[6]][indices[7]][indices[8]] = value; return;
        }
        Object subArray = array;
        int lastValue = indices.length-1;
        for (int i = 0; i < lastValue; i++)
            subArray = Array.get(subArray,indices[i]);
        ((T[]) subArray)[indices[lastValue]] = value;
    }

    private Object getArrayValue(Object array, int ... indices) {
        Object value = array;
        for (int index : indices)
            value = Array.get(value,index);
        return value;
    }

    public T getValue(int ... indices) {
        return get(indices);
    }

    public void setValue(T value, int ... indices) {
        set(value,indices);
    }
}
