package com.pb.sawdust.tensor.index;

import com.pb.sawdust.tensor.Tensor;

import java.util.*;

/**
 * The {@code MixedIndex} is a general index that allows anything to be used as an id. The reason for "mixed" in the title
 * is that some indices have different id types for different dimensions, and this class supports this inherently without
 * casts or parametric wildcards.  It is noted that what is gained in flexibility is lost in type safety; instances of this
 * class act something more like a dynamically-typed form of an index.
 * <p>
 * This class also offers some convenience static methods for changing the ids for a given index.
 * //todo: test this
 *
 * @author crf <br/>
 *         Started Oct 4, 2010 6:03:41 PM
 */
public class MixedIndex extends BaseIndex<Object> {
    private final Index<?> sourceIndex;

    /**
     * Convenience method to get an index based on a sepecified source index with some dimensions's id's replaced.
     *
     * @param sourceIndex
     *        The source index to base the returned index on.
     *
     * @param idMap
     *        A mapping from (0-based) dimension index to that dimension's new ids.
     *
     * @return an index based on {@code sourceIndex} with new ids from {@code idMap}.
     *
     * @throws IllegalArgumentException if any of the keys in {@code idMap} is out of bounds for {@code sourceIndex} (<i>i.e.</i>
     *                                  less than zero or greater than or equal to {@code sourceIndex.getSize()}), or if
     *                                  the length of any of the id lists in {@code idMap} is not the correct length for
     *                                  the corresponding dimension from {@code sourceIndex}.
     */
    public static MixedIndex replaceIds(Index<?> sourceIndex, Map<Integer,List<?>> idMap) {
        //index map is dimension : new ids
        List<List<?>> ids = new ArrayList<List<?>>(sourceIndex.getIndexIds());
        int baseSize = sourceIndex.size();
        for (int i : idMap.keySet())
            if (i < 0  || i >= baseSize)
                throw new IllegalArgumentException(String.format("Id replacement dimension out of bounds for index of size %d: %d",baseSize,i));
            else
                ids.set(i,idMap.get(i));
        return new MixedIndex(IndexUtil.getReferenceIndices(sourceIndex),ids,sourceIndex); //todo: this is inneficient to get full reference - maybe composite this?
    }

    /**
     * Convenience method to get an index based on a sepecified source index with one dimension's id's replaced.
     *
     * @param sourceIndex
     *        The source index to base the returned index on.
     *
     * @param dimension
     *        The (0-based) dimension index for the new ids.
     *
     * @param ids
     *        The new ids for {@code dimension}.
     *
     * @return an index based on {@code sourceIndex} with {@code ids} as the ids for {@code dimension}.
     *
     * @throws IllegalArgumentException if {@code dimension} is out of bounds for {@code sourceIndex} (<i>i.e.</i> less
     *                                  than zero or greater than or equal to {@code sourceIndex.getSize()}), or if
     *                                  the length of {@code ids} is not equal to length of {@code dimension} from {@code sourceIndex}.
     */
    public static MixedIndex replaceIds(Index<?> sourceIndex, int dimension, List<?> ids) {
        Map<Integer,List<?>> m = new HashMap<Integer,List<?>>();
        m.put(dimension,ids);
        return replaceIds(sourceIndex,m);
    }

    @SuppressWarnings("unchecked") //just a really generic list, so no problems
    private static List<List<Object>> caster(List<List<?>> ids) {
        Object idso = ids;
        return (List<List<Object>>) idso;
    }

    /**
     * Constructor specifying the ids to use in the index. The index dimensionality is inferred from the ids, and it
     * is assumed that no reference indices are used.
     *
     * @param ids
     *        The ids for this index.
     *
     * @throws IllegalArgumentException if any of the sublists in {@code ids} is empty or ifany entry in a sublist of
     *                                  {@code ids} is repeated (each dimension must have unique ids).
     */
    @SuppressWarnings("unchecked") //ok here because we only deal with maps internally
    public MixedIndex(List<List<?>> ids) {
        super(caster(ids));
        sourceIndex = null;
    }

    /**
     * Constructor specifying the ids to use in the index. The index dimensionality is inferred from the ids, and it
     * is assumed that no reference indices are used.
     *
     * @param ids
     *        The ids for this index.
     *
     * @throws IllegalArgumentException if any of the arrays in {@code ids} is empty or if any element in an {@code ids}
     *                                  subarray is repeated (each dimension must have unique ids).
     */
    public MixedIndex(Object[] ... ids) {
        super(ids);
        sourceIndex = null;
    }

    /**
     * Constructor specifying the ids to use in the index and the reference indices. The index dimensionality is
     * inferred from the input parameters. The reference indices provide the mapping from this index's indices to the
     * indices returned by calls to {@link #getIndices(int...)} and {@link #getIndex(int,int)}
     *
     * @param referenceIndex
     *        The reference indices.
     *
     * @param ids
     *        The ids for this index.
     *
     * @throws IllegalArgumentException if any of the sublists in {@code ids} is empty, if any entry in a sublist of
     *                                  {@code ids} is repeated (each dimension must have unique ids), if
     *                                  {@code referenceIndex}'s dimensionality (shape) does not match that implied by
     *                                  {@code ids}, or if any of the elements in {@code referenceIndex} are less than zero.
     */
    public MixedIndex(int[][] referenceIndex, List<List<?>> ids) {
        this(referenceIndex,ids,null);
    }

    private MixedIndex(int[][] referenceIndex, List<List<?>> ids, Index<?> sourceIndex) {
        super(referenceIndex,caster(ids));
        this.sourceIndex = sourceIndex;
    }

    /**
     * Constructor specifying the ids to use in the index and the reference indices. The index dimensionality is
     * inferred from the input parameters. The reference indices provide the mapping from this index's indices to the
     * indices returned by calls to {@link #getIndices(int...)} and {@link #getIndex(int,int)}
     *
     * @param referenceIndex
     *        The reference indices.
     *
     * @param ids
     *        The ids for this index.
     *
     * @throws IllegalArgumentException if any of the arrays in {@code ids} is empty or if any element in an {@code ids}
     *                                  subarray is repeated (each dimension must have unique ids), if {@code referenceIndex}'s
     *                                  dimensionality (shape) does not match that implied by {@code ids}, or if any of
     *                                  the elements in {@code referenceIndex} are less than zero.
     */
    public MixedIndex(int[][] referenceIndex, Object[] ... ids) {
        super(referenceIndex,ids);
        sourceIndex = null;
    }

//    public boolean isValidFor(Tensor t) {
//        //this is not correct - source index does not have to be valid for tensor
//        return sourceIndex == null ? super.isValidFor(t) : sourceIndex.isValidFor(t);
//    }
//
//    public int[] getIndices(int ... indices) {
//        return sourceIndex == null ? super.getIndices(indices) : sourceIndex.getIndices(indices);
//    }
}
