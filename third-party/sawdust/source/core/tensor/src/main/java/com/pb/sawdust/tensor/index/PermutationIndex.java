package com.pb.sawdust.tensor.index;

import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.util.array.ArrayUtil;

/**
 * The {@code PermuationIndex} is a mutable index which allows the indices in a tensor to be rearranged dynamically.
 * When a {@code PermuationIndex} is first instantiated, its indices in each dimension are ordered from 0 to the length
 * of the dimension minus 1. However, calling the {@code permute(int,int,int)} method, two indices in a given dimension
 * are swapped, effectively shifting the arrangement of the tensor data on the fly (if the tensor is two-dimensional (a
 * matrix), then this operation is equivalent to swapping rows or columns). This index can be useful for certain algorithms,
 * where low-overhead permuting of dimensions is desired.
 *
 * @author crf <br/>
 *         Started: Jul 27, 2009 7:45:52 PM
 */
public class PermutationIndex extends StandardIndex {
    private final int[][] index;

    /**
     * Constructor specifying the dimensions of this index.
     *
     * @param dimensions
     *        The dimensions of this index.
     *
     * @throws IllegalArgumentException if any of the elements in {@code dimensions} is less than one.
     */
    public PermutationIndex(int ... dimensions) {
        super(dimensions);
        index = new int[dimensions.length][];
        int counter = 0;
        for (int d : dimensions)
            index[counter++] = range(d).getRangeArray();
    }

    public int getIndex(int dimension, int index) {
        int[] dimIndex;
        try {
            dimIndex = this.index[dimension];
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(getDimensionOutOfBoundsErrorMessage(dimension));
        }
        return dimIndex[index];
    }

    /**
     * Permute this index by swapping two indices in a specified dimension.
     *
     * @param dimension
     *        The dimension to permute.
     *
     * @param index1
     *        The first index to swap.
     *
     * @param index2
     *        The second index to swap.
     *
     * @throws IllegalArgumentException if {@code dimension} is less than zero or greater than one less then the number
     *                                  of dimensions in this index.
     * @throws IndexOutOfBoundsException if {@code index1} or {@code index2} is less than zero or greater than one less
     *                                   than the size of {@code dimension}.
     */
    public void permute(int dimension, int index1, int index2) {
        checkIndex(dimension, index1);
        checkIndex(dimension, index2);
        int temp = index[dimension][index1];
        index[dimension][index1] = index[dimension][index2];
        index[dimension][index2] = temp;
    }

    /**
     * Return the permutation of the indices in a specified dimension. That is, it returns an array holding the current
     * ordering of the indices (as originally defined when this index was constructed).
     *
     * @param dimension
     *        The dimension in question.
     *
     * @return an array holding the current ordering of {@code dimension}'s indices.
     *
     * @throws IllegalArgumentException if {@code dimension} is less than zero or greater than one less then the number
     *                                  of dimensions in this index.
     */
    public int[] getPermutation(int dimension) {
        try {
            return ArrayUtil.copyArray(index[dimension]);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(getDimensionOutOfBoundsErrorMessage(dimension));
        }
    }
}
