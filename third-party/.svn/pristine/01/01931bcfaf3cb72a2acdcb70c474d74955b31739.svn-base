package com.pb.sawdust.util.array;

/**
 * The {@code TypeSafeArray} interface provides a wrapper for arrays so that they may be parameterized like other
 * generic classes. A type safe array may be multidimensional; because of this, a number of convenience methods (sometimes
 * leveraging the parameterization) for dealing with multidimensional arrays are specified.
 *
 * @param <T>
 *        The type held by the wrapped array.
 *
 * @author crf <br/>
 *         Started: Jan 8, 2009 11:34:34 AM
 */
public interface TypeSafeArray<T> {

    /**
     * Get the array wrapped by this instance.
     *
     * @return the wrapped array.
     */
    public Object getArray();

    /**
     * Get the number of dimensions in the wrapped array.
     *
     * @return the dimension count of the wrapped array.
     */
    public int getArrayDimensions();

    /**
     * Get the value of the wrapped array at a specified location. Each index in the {@code indices} parameter
     * corresponds to a location in the wrapped array's dimensions.
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
    public T getValue(int ... indices);

    /**
     * Set the value of the wrapped array at a specified location. Each index in the {@code indices} parameter
     * corresponds to a location in the wrapped array's dimensions.
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
    public void setValue(T value, int ... indices);
}
