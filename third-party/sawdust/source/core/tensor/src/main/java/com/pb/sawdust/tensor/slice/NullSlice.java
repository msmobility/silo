package com.pb.sawdust.tensor.slice;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The {@code NullSlice} is a slice representing zero elements.
 *
 * @author crf <br/>
 *         Started 3/25/11 11:57 PM
 */
public class NullSlice implements Slice {
    private static final NullSlice instance = new NullSlice();

    /**
     * Get the null slice.
     *
     * @return the null slice.
     */
    public static NullSlice getNullSlice() {
        return instance;
    }

    private NullSlice() {}

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public int getValueAt(int index) {
        throw new IllegalArgumentException("Null slice has no elements");
    }

    @Override
    public int[] getSliceIndices() {
        return new int[0];
    }

    @Override
    public int getMaxIndex() {
        return -1; //todo: is this correct?
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public Integer next() {
                throw new NoSuchElementException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
