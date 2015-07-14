package com.pb.sawdust.tensor.slice;

import com.pb.sawdust.util.array.ArrayUtil;

import java.util.Iterator;
import java.util.Arrays;

/**
 * The {@code BaseSlice} is a basic slice implementation for general use. It only requires that its corresponding
 * reference indices be passed into the constructor.
 *
 * @author crf <br/>
 *         Started: Feb 8, 2009 1:50:00 PM
 */
public class BaseSlice implements Slice {
    private int maxIndex;
    private int[] indices;

    /**
     * Constructor specifying the reference indices that the slice will be built out of.
     *
     * @param indices
     *        The reference indices this slice is built out of. There should be one reference index for each element
     *        in the slice.
     *
     * @throws IllegalArgumentException if the length of {@code indices} is zero or if any of the values in {@code indices}
     *                                  is less than zero.
     */
    public BaseSlice(int ... indices) {
        if (indices.length == 0)
            throw new IllegalArgumentException("At least one index required to create a slice.");
        this.indices = indices;
        maxIndex = 0;
        for (int index : indices)
            if (index < 0)
                throw new IllegalArgumentException("Slice indices must be non-negative: " + index);
            else
                maxIndex = Math.max(maxIndex,index);
    }

    public int getSize() {
        return indices.length;
    }

    public int getValueAt(int index) {
        try {
            return indices[index];
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Index out of bounds (slice size=" + indices.length + "): " + index);
        }
    }

    public int[] getSliceIndices() {
        return ArrayUtil.copyArray(indices);
    }

    public int getMaxIndex() {
        return maxIndex;
    }

    @SuppressWarnings("unchecked") //iterator on int[] will be Iterator<Integer>
    public Iterator<Integer> iterator() {
        return (Iterator<Integer>) ArrayUtil.getIterator(indices);
    }

    public String toString() {
        return "BaseSlice(" + Arrays.toString(indices) + ")";
    }
}
