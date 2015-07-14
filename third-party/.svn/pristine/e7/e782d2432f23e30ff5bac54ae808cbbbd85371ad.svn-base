package com.pb.sawdust.tensor.index;

import com.pb.sawdust.tensor.slice.Slice;
import java.util.List;
import java.util.LinkedList;

/**
 * The {@code SliceIndex} class is used to build indices from {@code Slice}s.
 *
 * @see Slice
 *
 * @author crf <br/>
 *         Started: Jan 10, 2009 10:11:00 PM
 */
public class SliceIndex<I> extends BaseIndex<I> {

    private static <I> List<List<I>> getIds(Index<I> index, Slice ... slices) {
        List<List<I>> indices = new LinkedList<List<I>>();
        int counter = 0;
        for (Slice slice : slices) {
            List<I> dimensionIndices = new LinkedList<I>();
            for (int i = 0; i < slice.getSize(); i++)
                dimensionIndices.add(index.getIndexId(counter,slice.getValueAt(i)));
            indices.add(dimensionIndices);
            counter++;
        }
        return indices;
    }

    private static int[][] getReferenceIndex(Index<?> index, Slice ... slices) {
        if (slices.length != index.size())
            throw new IllegalArgumentException("Slice count must be equal to base dimension size");
        int[][] referenceIndex = new int[slices.length][];
        for (int i = 0; i < slices.length; i++)
            referenceIndex[i] = slices[i].getSliceIndices();
        return referenceIndex;
    }

    private SliceIndex(int[][] referenceIndex, List<List<I>> ids) {
        super(referenceIndex,ids);
    }

    @SafeVarargs
    @SuppressWarnings({"unchecked", "varargs"})
    private SliceIndex(int[][] referenceIndex, I[] ... ids) {
        super(referenceIndex,ids);
    }

    private static class IdlessSliceIndex extends SliceIndex<Integer> implements IdlessIndex {
        private IdlessSliceIndex(Index<?> index, Slice ... slices) {
            super(getReferenceIndex(index,slices),getDefaultIds(slices));
        }

        private static Integer[][] getDefaultIds(Slice ... slices) {
            Integer[][] ids = new Integer[slices.length][];
            for (int i = 0; i < ids.length; i++) {
                Slice slice = slices[i];
                Integer[] id = new Integer[slice.getSize()];
                for (int j = 0; j < id.length; j++)
                    id[j] = j;
                ids[i] = id;
            }
            return ids;
        }
    }

    /**
     * Factory method to get a slice index from a source index and slices. The source index is used to generate the
     * reference indices and ids for the new index.
     *
     * @param index
     *        The source index holding the ids which (by transferring through the slices) will be used in the returned index.
     *
     * @param slices
     *        The slices from which the index will be built from.
     *
     * @param <I>
     *        The type of the ids.
     *
     * @return a slice index based on {@code slices} and {@code index}.
     *
     * @throws IllegalArgumentException if the length of {@code slices} is not equal to the number of dimensions in
     *                                  {@code index}, or if any of the slices have repeated indices in any dimension
     *                                  (as doing so would cause the ids in that dimension to be repeated).
     */
    public static <I> SliceIndex<I> getSliceIndex(Index<I> index, Slice ... slices) {
        return new SliceIndex<I>(getReferenceIndex(index,slices),getIds(index,slices));
    }

    /**
     * Factory method to get a slice index from a source index, slices, and ids to use in the index. The source index is
     * used to generate the reference indices.
     *
     * @param index
     *        The source index.
     *
     * @param newIds
     *        The ids to use in the new index.
     *
     * @param slices
     *        The slices from which the index will be built from.
     *
     * @param <I>
     *        The type of the ids.
     *
     * @return a slice index based on {@code slices}, {@code index}, and {@code ids}.
     *
     * @throws IllegalArgumentException if the length of {@code slices} is not equal to the number of dimensions in
     *                                  {@code index}, if the number sublists in {@code ids} is not equal to the
     *                                  number of dimensions in {@code index}, if the number of ids in each sublist
     *                                  in {@code ids} does not equal the size of its respective dimension (slice size),
     *                                  or if any of the ids in a given dimension are repeated.
     */
    public static <I> SliceIndex<I> getSliceIndex(Index<?> index, List<List<I>> newIds, Slice ... slices) {
        return new SliceIndex<I>(getReferenceIndex(index,slices),newIds);
    }

    /**
     * Factory method to get a slice index from a source index, slices, and ids to use in the index. The source index is
     * used to generate the reference indices.
     *
     * @param index
     *        The source index.
     *
     * @param newIds
     *        The ids to use in the new index.
     *
     * @param slices
     *        The slices from which the index will be built from.
     *
     * @param <I>
     *        The type of the ids.
     *
     * @return a slice index based on {@code slices}, {@code index}, and {@code ids}.
     *
     * @throws IllegalArgumentException if the length of {@code slices} is not equal to the number of dimensions in
     *                                  {@code index}, if the number subarrays in {@code ids} is not equal to the
     *                                  number of dimensions in {@code index}, if the number of ids in each subarray
     *                                  in {@code ids} does not equal the size of its respective dimension (slice size),
     *                                  or if any of the ids in a given dimension are repeated..
     */
    public static <I> SliceIndex<I> getSliceIndex(Index<?> index, I[][] newIds, Slice ... slices) {
        return new SliceIndex<I>(getReferenceIndex(index,slices),newIds);
    }

    /**
     * Factory method to get a slice index from a source index and slices, using the default ids (which are the {@code Integer})
     * equivalents of the indices.  The source index is used to generate the reference indices.
     *
     * @param index
     *        The source index.
     *
     * @param slices
     *        The slices from which the index will be built from.
     *
     * @return a slice index based on {@code slices} and {@code index} using default ids.
     *
     * @throws IllegalArgumentException if the length of {@code slices} is not equal to the number of dimensions in
     *                                  {@code index}. 
     */
    public static SliceIndex<Integer> getDefaultIdSliceIndex(Index<?> index, Slice ... slices) {
        return new IdlessSliceIndex(index,slices);
    }
}
