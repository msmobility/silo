package com.pb.sawdust.tensor.slice;

import com.pb.sawdust.util.array.ArrayUtil;

import java.util.*;

/**
 * The {@code CompositeSlice} class provides a {@code Slice} implementation composed of other slices. In addition to
 * being composed of slices, this slice is mutable, allowing the addition or removal of slices.
 *
 * @author crf <br/>
 *         Started: Oct 16, 2008 9:49:57 AM
 */
public class CompositeSlice implements Slice {
    private final List<Slice> slices;
    private int size;
    private int maxIndex;
    private List<Integer> slicePoints;

    /**
     * Constructor specifying the slices the slice will be built from.
     *
     * @param slices
     *        The slices to build the slice from.
     * 
     * @throws IllegalArgumentException if {@code slices.length == 0} (at least one slice must be specified).
     */
    public CompositeSlice(Slice ... slices) {
        if (slices.length == 0)
            throw new IllegalArgumentException("CompositeSlice must contain at least one slice.");
        this.slices = new LinkedList<Slice>();
        Collections.addAll(this.slices,slices);
        size = 0;
        maxIndex = 0;
        slicePoints = new LinkedList<Integer>();
        for (Slice slice : slices) {
            size += slice.getSize();
            slicePoints.add(size);
            maxIndex = Math.max(maxIndex,slice.getMaxIndex());
        }
    }

    /**
     * Add a slice to the end of this slice.
     *
     * @param slice
     *        The slice to add.
     */
    public void addSlice(Slice slice) {
        slices.add(slice);
        size += slice.getSize();
        slicePoints.add(size);
        maxIndex = Math.max(maxIndex,slice.getMaxIndex());
    }

    /**
     * Add a slice to the specified point in this slice.
     *
     * @param slice
     *        The slice to add.
     *
     * @param position
     *        The (zero-based) <i>slice</i> position to add this slice to. This slice will be placed in relation to the
     *        other slices already contained in this slice.
     *
     * @throws IllegalArgumentException if {@code position} is less than zero or greater than the number of slices
     *                                  currently in this slice.
     */
    public void addSlice(Slice slice, int position) {
        try {
            slices.add(position,slice);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Slice position out of bounds (max " + slices.size() + "): " + position);
        }
        int addSize = slice.getSize();
        slicePoints.add(position,position == 0 ? 0 : slicePoints.get(position-1));
        for (int i=position; i<slicePoints.size(); i++)
            slicePoints.set(i,slicePoints.get(i)+addSize);
        size += addSize;
        maxIndex = Math.max(maxIndex,slice.getMaxIndex());
    }

    /**
     * Remove the specified slice from this slice. If the slice occurs more than once in this composite slice, then the
     * first instance of the slice is removed.
     *
     * @param slice
     *        The slice to remove from this slice.
     *
     * @throws IllegalArgumentException if {@code slice} is not found in this slice.
     *
     * @throws IllegalStateException if {@code slice} is the last slice in this slice (a composite slice must be composed
     *                               of at least one slice).
     */
    public void removeSlice(Slice slice) {
        if (slices.size() == 1 && slices.contains(slice))
                throw new IllegalStateException("CompositeSlice must contain at least one slice, cannot remove last remaining slice.");
        try {
            removeSlice(slices.indexOf(slice));
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Slice not found in composite slice: " + slice);
        }
    }

    /**
     * Remove the slice at the specified position.
     *
     * @param position
     *        The (zero-based) position of the slice to remove from this slice.
     *
     * @throws IllegalArgumentException if {@code position} is less than zero or greater than one less than the number
     *                                  of slices in this slice.
     *
     * @throws IllegalStateException if there is only one slice left in this slice (a composite slice must be composed
     *                               of at least one slice).
     */
    public void removeSlice(int position) {
        if (slices.size() == 1)
            throw new IllegalStateException("CompositeSlice must contain at least one slice, cannot remove last remaining slice.");
        int removeSize;
        try {
            removeSize = slices.remove(position).getSize();
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Slice position out of bounds (max " + (slices.size() - 1) + "): " + position);
        }
        slicePoints.remove(position);
        for (int i = position; i < slicePoints.size(); i++)
            slicePoints.set(i,slicePoints.get(i)-removeSize);
        size -= removeSize;
        maxIndex = 0;
        for (Slice s : slices)
            maxIndex = Math.max(maxIndex,s.getMaxIndex());
    }

    /**
     * Get the slices contained in this slice, in the order that they appear.
     *
     * @return the slices in this slice.
     */
    public Slice[] getSlices() {
        return slices.toArray(new Slice[slices.size()]);
    }


    public int getSize() {
        return size;
    }

    public int getValueAt(int index) {
        int counter = 0;
        int last = 0;
        for (int i : slicePoints) {
            if (index < i)
                return slices.get(counter).getValueAt(index - last);
            last = i;
            counter++;
        }
        throw new IllegalArgumentException("CompositeSlice ndex out of bounds (slice size=" + size + "): " + index);
    }

    public int[] getSliceIndices() {
        List<Integer> indices = new LinkedList<Integer>();
        for (int index : this)
            indices.add(index);
        return ArrayUtil.toPrimitive(indices.toArray(new Integer[indices.size()]));
    }

    public int getMaxIndex() {
        return maxIndex;
    }

    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            private Iterator<Slice> slicesIterator = slices.iterator();
            private Iterator<Integer> sliceIterator = slicesIterator.next().iterator();

            public boolean hasNext() {
                return sliceIterator.hasNext() || slicesIterator.hasNext();
            }

            public Integer next() {
                if (!sliceIterator.hasNext()) {
                    if (!slicesIterator.hasNext())
                        throw new NoSuchElementException();
                    else
                        sliceIterator = slicesIterator.next().iterator();
                }
                return sliceIterator.next();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("CompositeSlice:");
        for (Slice slice : slices)
            sb.append("\n\t" + slice.toString());
        return sb.toString();
    }
}
