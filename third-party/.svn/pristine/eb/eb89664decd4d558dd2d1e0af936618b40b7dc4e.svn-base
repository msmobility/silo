package com.pb.sawdust.tensor.slice;

import com.pb.sawdust.util.Range;
import com.pb.sawdust.util.array.ArrayUtil;

import java.util.*;

/**
 * The {@code SliceUtil} class provides convenience functions for creating slices. The intended use of this class is
 * through a static import ({@code import static SliceUtil.*}), after which all of the methods in this class can be
 * accessed directly. By doing this, most necessary slices can be built/composed from this class's methods.
 *
 * @author crf <br/>
 *         Started: Oct 16, 2008 9:42:27 AM
 */
public class SliceUtil {

    private SliceUtil() {}

    /**
     * Get a slice for a single index point (a reducing slice).
     *
     * @param index
     *        The index point.
     *
     * @return a slice holding {@code index} as its only element.
     *
     * @throws IllegalArgumentException if {@code indexPoint} is less than zero.
     */
    public static Slice slice(int index) {
        return ReducingSlice.reducingSlice(index);
    }

    /**
     * Get a slice covering a full dimension of specified size (a full slice).
     *
     * @param size
     *        The size of the slice.
     *
     * @return a slice corresponding to a dimension with size {@code size}.
     *
     * @throws IllegalArgumentException if {@code size} is less than one.
     */
    public static Slice fullSlice(int size) {
        return FullSlice.fullSlice(size);
    }

    /**
     * Get a slice spanning two index points. If the start point is less than the ending point, the slice elements will
     * increase by one at each step, otherwise they will decrease by one. Both the start and end points are contained
     * in the resulting slice.
     *
     * @param start
     *        The starting reference index point (inclusive).
     *
     * @param end
     *        The ending reference index point (inclusive).
     *
     * @return a slice spanning the indices from {@code start} to {@code end}.
     *
     * @throws IllegalArgumentException if {@code start} or {@code end} is less than zero.
     */
    public static Slice span(int start, int end) {
        return range(start,end + (start < end ? 1 : -1));
        //return new BaseSlice(Range.range(start,end + (start < end ? 1 : -1)).getRangeArray());
    }

    /**
     * Get a slice ranging between two index points. If the start point is less than the ending point, the slice elements will
     * increase by one at each step, otherwise they will decrease by one. This is the same as {@link #span(int, int)},
     * except the end point is exluded from the slice.
     *
     * @param start
     *        The starting reference index point (inclusive).
     *
     * @param end
     *        The ending reference index point (exclusive).
     *
     * @return a slice spanning the indices from {@code start} to {@code end}.
     *
     * @throws IllegalArgumentException if {@code start} is less than zero or {@code end} is less than -1.
     */
    public static Slice range(int start, int end) {
        return new BaseSlice(Range.range(start,end).getRangeArray());
    }

    /**
     * Get a slice corresponding to the specified indices.
     *
     * @param indices
     *        The reference indices to be included (in order) in the slice.
     *
     * @return a slice containing {@code indices}.
     *
     * @throws IllegalArgumentException if the length of {@code indices} is zero or if any of the values in {@code indices}
     *                                  is less than zero.
     */
    public static Slice slice(int ... indices) {
        return new BaseSlice(indices);
    }

    /**
     * Get a slice copying a given index a specified number of times.
     *
     * @param index
     *        The index value to copy.
     *
     * @param times
     *        The number of times to copy {@code index}.
     *
     * @return a slice containing {@code index} {@code times} times.
     *
     * @throws IllegalArgumentException if the {@code times} is less than 1, or if {@code index} is less than 0.
     */
    public static Slice copy(int index, int times) {
        if (times < 1)
            throw new IllegalArgumentException("Copy times must be strictly positive: " + times);
        int[] indices = new int[times];
        Arrays.fill(indices,index);
        return new BaseSlice(indices);
    }

    /**
     * Get a slice composed of the specified slices. The returned slice will be a (modifiable) {@code CompositeSlice}.
     *
     * @param slices
     *        The slices that the slice will be composed from.
     *
     * @return a slice composed from {@code slices}.
     *
     * @throws IllegalArgumentException if {@code slices.length == 0} (at least one slice must be specified).
     */
    public static CompositeSlice compositeSlice(Slice ... slices) {
        return new CompositeSlice(slices);
    }

    /**
     * Get a slice composed of the specified slices. The returned slice will be an unmodifiable {@code Slice}.
     *
     * @param slices
     *        The slices that the slice will be composed from.
     *
     * @return a slice composed from {@code slices}.
     *
     * @throws IllegalArgumentException if {@code slices.length == 0} (at least one slice must be specified).
     */
    public static Slice slice(Slice ... slices) {
        if (slices.length == 0)
            throw new IllegalArgumentException("Slice must be built from at least one slice.");
        List<Integer> list = new LinkedList<Integer>();
        for (Slice slice : slices)
            for (int i : slice)
                list.add(i);
        return new BaseSlice(ArrayUtil.toPrimitive(list.toArray(new Integer[list.size()])));
    }

    /**
     * Get a slice based on a series of ids.
     *
     * @param baseIds
     *        The ids from the (base) matrix.
     *
     * @param targetIds
     *        The target ids. All ids in this list must be contained in {@code baseIds}, though they may be reordered or
     *        repeated.
     *
     * @param <I>
     *        The type of the ids.
     *
     * @return a slice which which orders the indices according to {@code targetIds}.
     *
     * @throws IllegalArgumentException if any id in {@code targetIds} is not found in {@code baseIds}.
     */
    public static <I> Slice idSlice(List<I> baseIds, List<I> targetIds) {
        Map<I,Integer> baseMap = new HashMap<I,Integer>();
        int counter = 0;
        for (I id : baseIds)
            baseMap.put(id,counter++);
        int[] targetIndices = new int[targetIds.size()];
        counter = 0;
        for (I id : targetIds)
            if (!baseMap.containsKey(id))
                throw new IllegalArgumentException("Id not found: " + id);
            else
                targetIndices[counter++] = baseMap.get(id);
        return slice(targetIndices);
    }
//    public static <I> Slice idSlice(List<I> baseIds, List<I> targetIds) {
//        Map<I,Integer> targetMap = new HashMap<I,Integer>();
//        int counter = 0;
//        for (I id : targetIds)
//            targetMap.put(id,counter++);
//        int[] targetIndices = new int[targetIds.size()];
//        counter = 0;
//        for (I id : baseIds)
//            if (!targetMap.containsKey(id))
//                throw new IllegalArgumentException("Id not found: " + id);
//            else
//                targetIndices[counter++] = targetMap.get(id);
//        System.out.println(Arrays.toString(targetIndices));
//        return slice(targetIndices);
//    }
}
