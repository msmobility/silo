package com.pb.sawdust.tensor.index;

import com.pb.sawdust.util.ContainsMetadataImpl;
import com.pb.sawdust.util.array.ArrayUtil;
import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.tensor.Tensor;

import java.util.Arrays;

/**
 * The {@code AbstractIndex} provides a skeletal implementation of the {@code Index} interface to lower the implementation
 * burden on programmers. Calls to {@code getIndices(int...)} or {@code getIndex(int,int)} just pass the indices through -
 * to transfer to reference indices, these methods should be overridden. This method does not implement any of the methods
 * referring to ids.
 *
 * @param <I>
 *        The type of the index's id.
 *
 * @author crf <br/>
 *         Started: Jan 10, 2009 9:04:10 PM
 */
public abstract class AbstractIndex<I> extends ContainsMetadataImpl<String> implements Index<I> {
    /**
     * The dimensions of the index. This array should never be modified.
     */
    protected final int[] dimensions;

    private final int[] maximumReferenceIndices;

    /**
     * Constructor specifying the dimensions of the index.
     *
     * @param dimensions
     *        The dimensions of the index.
     *
     * @throws IllegalArgumentException if any of the elements in {@code dimensions} is less than one.
     */
    public AbstractIndex(int ... dimensions) {
        this.dimensions = new int[dimensions.length];
        maximumReferenceIndices = new int[dimensions.length];
        for (int i = 0; i < dimensions.length; i++)
            if (dimensions[i] < 1) {
                throw new IllegalArgumentException("Dimension sizes must be greater than zero: " + Arrays.toString(dimensions));
            } else {
                this.dimensions[i] = dimensions[i];
                maximumReferenceIndices[i] = dimensions[i]-1;
            }
    }

    public int size() {
        return dimensions.length;
    }

    public int size(int dimension) {
        try {
            return dimensions[dimension];
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Dimension out of bounds: " + dimension);
        }
    }

    public int[] getDimensions() {
        return ArrayUtil.copyArray(dimensions);
    }

    /**
     * Get an array of the maximum reference index in each reference dimension. This is used to determine whether this
     * index is valid for a given tensor. If this index is not using reference ids, then this method need not be
     * overridden.
     *
     * @return an array holding the maximum reference index in each reference dimension.
     */
    protected int[] getMaximumReferenceIndices() {
        return maximumReferenceIndices;
    }

    public boolean isValidFor(Tensor tensor) {
        int[] refIndices = getMaximumReferenceIndices();
        int[] dimensions = tensor.getDimensions();

        if (refIndices.length != dimensions.length)
            return false;
        for (int i = 0; i < dimensions.length; i++)
            if (dimensions[i] <= refIndices[i])
                return false;
        return true;
    }

//    /**
//     * Check specified indices to see if they are valid for this index. This method can be used to check calls which
//     * take indices (as {@code int...}) as parameters - it will throw the expected exceptions if necessary.
//     *
//     * @param indices
//     *        The indices in question.
//     *
//     * @throws IllegalArgumentException if the number of indices is not equal to the number of dimensions in this index
//     *
//     * @throws IndexOutOfBoundsException if any value in {@code indices} is less than zero or greater than one less
//     *                                   than the size of its corresponding dimension.
//     */
//    protected final void checkIndices(int ... indices) {
//        checkIndicesLength(indices);
//        for (int i = 0; i < indices.length; i++)
//            checkIndex(i,indices[i]);
//    }

    private void checkIndicesLength(int ... indices) {
        if (indices.length != dimensions.length)
            throw new IllegalArgumentException("Number of indices must match number of dimensions: expected " + dimensions.length + ", found " + indices.length);
    }

    /**
     * Check the specified dimension and corresponding index to see if they are valid for this tensor. This method can
     * be used to check calls which take a dimension and index as parameters - it will throw the expected exceptions if
     * necessary.
     *
     * @param dimension
     *        The dimension in question.
     * \
     * @param index
     *        The index in question.
     *
     * @throws IllegalArgumentException  if {@code dimension} is less than zero or greater than one less then the number
     *                                   of dimensions in this index.
     * @throws IndexOutOfBoundsException if {@code index} is less than zero or greater than one less than the size of {@code dimension}.
     */
    protected final void checkIndex(int dimension, int index) {
        boolean check;
        try {
            check = index < 0 || index >= dimensions[dimension];
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Dimension out of bounds (max dimension index=" + (size()-1) + "): " + dimension);
        }
        if (check) {
            throw new IndexOutOfBoundsException("Index out of bounds for dimension (" + dimension + ") of size " + dimensions[dimension] + ": " + index);
        }
    }

    protected final void checkDimension(int dimension) {
        if (dimension >= dimensions.length || dimension < 0)
            throw new IllegalArgumentException(getDimensionOutOfBoundsErrorMessage(dimension));
    }

    String getDimensionOutOfBoundsErrorMessage(int dimension) {
        return "Dimension out of bounds (max dimension=" + (size()-1) + "): " + dimension;
    }

    public int[] getIndices(int ... indices) {
        checkIndicesLength(indices);
        int[] newIndices = new int[indices.length];
        for (int i = 0; i < indices.length; i++)
            newIndices[i] = getIndex(i,indices[i]);
        return newIndices;
    }

    public int getIndex(int dimension, int index) {
        checkIndex(dimension,index);
        return index;
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (o == null || !(o instanceof Index))
            return false;
        Index i = (Index) o;
        if (!Arrays.equals(dimensions,i.getDimensions()))
            return false;
        for (int d : range(dimensions.length))
            for (int j : range(dimensions[d]))
                if (getIndex(d,j) != i.getIndex(d,j) || getIndexId(d,j) != i.getIndexId(d,j))
                    return false;
        return true;
    }

    public int hashCode(){
        int result = 17;
        for (int b : dimensions)
            result = 37*result + b;
        for (int d : range(dimensions.length))
            for (int j : range(dimensions[d]))
                result = 37*(37*result + getIndex(d,j)) + getIndexId(d,j).hashCode();
        return result;
    }
}
