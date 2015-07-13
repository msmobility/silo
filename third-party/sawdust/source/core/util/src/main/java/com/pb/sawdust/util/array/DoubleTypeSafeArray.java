package com.pb.sawdust.util.array;

import java.lang.reflect.Array;

/**
 * The {@code DoubleTypeSafeArray} provides a type safe wrapper for (possibly multidimensional) {@code boolean} arrays.
 * Though this class implements {@code TypeSafeArray<Double>}, efficiencies will be had if method using primitive
 * {@code double}s (instead of object {@code Double}s) are used.
 *
 * @author crf <br/>
 *         Started: Jan 8, 2009 11:27:15 AM
 */
public class DoubleTypeSafeArray implements TypeSafeArray<Double> {
    private final Object array;
    private final int dimensions;

    /**
     * Constructor specifying the array to wrap. The wrapped array may be multidimensional.
     *
     * @param array
     *        The array to wrap.
     *
     * @throws IllegalArgumentException if {@code array} is not a {@code double} array.
     */
    public DoubleTypeSafeArray(Object array) {
        if (ArrayUtil.getBaseComponentType(array) != double.class)
            throw new IllegalArgumentException("Array base component must be double, not " + ArrayUtil.getBaseComponentType(array));
        this.array = array;
        this.dimensions= ArrayUtil.getDimensionCount(array);
    }

    /**
     * {@inheritDoc}
     * The returned array will be a primitive {@code double} array, not an object {@code Double} array.
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
    public double get(int ... indices) {
        if (indices.length != dimensions)
            throw new IllegalArgumentException("Index count must match array dimension count: expected " + dimensions + ", found " + indices.length);
        switch (dimensions) {
            case 0 : throw new ArrayIndexOutOfBoundsException("Cannot get value from empty array");
            case 1 : return ((double[]) array)[indices[0]];
            case 2 : return ((double[][]) array)[indices[0]][indices[1]];
            case 3 : return ((double[][][]) array)[indices[0]][indices[1]][indices[2]];
            case 4 : return ((double[][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]];
            case 5 : return ((double[][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]];
            case 6 : return ((double[][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]];
            case 7 : return ((double[][][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]][indices[6]];
            case 8 : return ((double[][][][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]][indices[6]][indices[7]];
            case 9 : return ((double[][][][][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]][indices[6]][indices[7]][indices[8]];
        }
        Object subArray = array;
        int lastValue = indices.length-1;
        for (int i = 0; i < lastValue; i++)
            subArray = Array.get(subArray,indices[i]);
        return ((double[]) subArray)[indices[lastValue]];
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
    public double[] getUltimateArray(int ... indices) {
        if (indices.length != dimensions-1)
            throw new IllegalArgumentException("Index count must be one less than aray dimensions: expected " + (dimensions-1) + ", found " + indices.length);
        switch (indices.length) {
            case 0 : return (double[]) array;
            case 1 : return ((double[][]) array)[indices[0]];
            case 2 : return ((double[][][]) array)[indices[0]][indices[1]];
            case 3 : return ((double[][][][]) array)[indices[0]][indices[1]][indices[2]];
            case 4 : return ((double[][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]];
            case 5 : return ((double[][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]];
            case 6 : return ((double[][][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]];
            case 7 : return ((double[][][][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]][indices[6]];
            case 8 : return ((double[][][][][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]][indices[6]][indices[7]];
        }
        Object value = array;
        for (int index : indices)
            value = Array.get(value,index);
        return (double[]) value;
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
    public void set(double value, int ... indices) {
        if (indices.length != dimensions)
            throw new IllegalArgumentException("Index count must match array dimension count: expected " + dimensions + ", found " + indices.length);
        switch (dimensions) {
            case 0 : throw new ArrayIndexOutOfBoundsException("Cannot get value from empty array");
            case 1 : ((double[]) array)[indices[0]] = value; return;
            case 2 : ((double[][]) array)[indices[0]][indices[1]] = value; return;
            case 3 : ((double[][][]) array)[indices[0]][indices[1]][indices[2]] = value; return;
            case 4 : ((double[][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]] = value; return;
            case 5 : ((double[][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]] = value; return;
            case 6 : ((double[][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]] = value; return;
            case 7 : ((double[][][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]][indices[6]] = value; return;
            case 8 : ((double[][][][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]][indices[6]][indices[7]] = value; return;
            case 9 : ((double[][][][][][][][][]) array)[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]][indices[6]][indices[7]][indices[8]] = value; return;
        }
        Object subArray = array;
        int lastValue = indices.length-1;
        for (int i = 0; i < lastValue; i++)
            subArray = Array.get(subArray,indices[i]);
        ((double[]) subArray)[indices[lastValue]] = value;
    }

    public Double getValue(int ... indices) {
        return get(indices);
    }

    public void setValue(Double value, int ... indices) {
        set(value,indices);
    }
}
