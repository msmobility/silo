package com.pb.sawdust.tensor.index;

import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.collections.InjectiveMap;
import com.pb.sawdust.util.collections.InjectiveHashMap;
import com.pb.sawdust.util.Range;
import com.pb.sawdust.tensor.Tensor;

import java.util.*;

/**
 * The {@code BaseIndex} class provides a simple, general purpose index implementation. It extends {@code AbstractIndex}
 * to include support for ids and reference indices.
 *
 * @author crf <br/>
 *         Started: Jan 10, 2009 9:25:13 PM
 */
public class BaseIndex<I> extends AbstractIndex<I> {
    private final List<List<I>> ids;
    private final InjectiveMap<I,Integer>[] idMap;
    private final boolean referenceIndices;
    private final int[] maxReferenceIndices;
//    private final Map<Integer,Integer>[] indexMap;
    private final int[][] indexMap;
    private final Range dimensionIndices;

    private static <I> int[] getDimension(List<List<I>> indices) {
        int[] dimensions = new int[indices.size()];
        int counter = 0;
        for (List<?> l : indices)
            dimensions[counter++] = l.size();
        return dimensions;
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
    public BaseIndex(List<List<I>> ids) {
        super(getDimension(ids));
        maxReferenceIndices = null; //unused here
        dimensionIndices = new Range(ids.size());
        this.ids = new LinkedList<List<I>>();
        referenceIndices = false;
        indexMap = null;
        idMap = new InjectiveMap[ids.size()];
        int counter = 0;
        for (List<I> id : ids) {
            List<I> idList = new LinkedList<I>();
            for (I idElement : id)
                idList.add(idElement);
            this.ids.add(idList);
            int counter2 = 0;
            InjectiveMap<I,Integer> m = new InjectiveHashMap<I,Integer>();
            for (I did : idList)
                if (m.containsKey(did))
                    throw new IllegalArgumentException("Indices within dimension " + counter + " must be unique: " + did);
                else
                    m.put(did,counter2++);
            idMap[counter++] = m;
        }
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
    @SafeVarargs
    @SuppressWarnings({"unchecked", "varargs"})
    public BaseIndex(I[] ... ids) {
        this(IndexUtil.getIdList(ids));
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
    @SuppressWarnings("unchecked") //ok here because we only deal with maps internally
    public BaseIndex(int[][] referenceIndex, List<List<I>> ids) {
        super(getDimension(ids));
        if (referenceIndex.length != ids.size())
            throw new IllegalArgumentException("Reference index length must be equal to ids length");
        maxReferenceIndices = new int[dimensions.length];
        dimensionIndices = new Range(ids.size());
        this.ids = new LinkedList<List<I>>();
        referenceIndices = true;
//        indexMap = new Map[referenceIndex.length];
        indexMap = new int[referenceIndex.length][];
        idMap = new InjectiveMap[ids.size()];
        int counter = 0;
        for (List<I> id : ids) {
            List<I> idList = new LinkedList<I>();
            for (I idElement : id)
                idList.add(idElement);
            this.ids.add(idList);
            int[] reference = referenceIndex[counter];
            if (reference.length != id.size())
                throw new IllegalArgumentException(String.format("Each reference index size (%d) and and id size (%d) do not match for dimension %d.",reference.length,id.size(),counter));
//            Map<Integer,Integer> im = new HashMap<Integer,Integer>();
            InjectiveMap<I,Integer> m = new InjectiveHashMap<I,Integer>();
            int counter2 = 0;
            for (I did : idList)
                if (m.containsKey(did)) {
                    throw new IllegalArgumentException("Indices within dimension " + counter + " must be unique: " + did);
                } else {
                    int ref = reference[counter2];
                    if (ref > maxReferenceIndices[counter])
                        maxReferenceIndices[counter] = ref;
//                    im.put(counter2,ref);
                    m.put(did,counter2++);
                }
//            indexMap[counter] = im;
            indexMap[counter] = ArrayUtil.copyArray(reference);
            idMap[counter++] = m;
        }
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
    @SafeVarargs
    @SuppressWarnings({"unchecked", "varargs"})
    public BaseIndex(int[][] referenceIndex, I[] ... ids) {
        this(referenceIndex,IndexUtil.getIdList(ids));
    }

    private void checkAgainstTensor(Tensor<?> tensor) {
        if (!Arrays.equals(tensor.getDimensions(),getDimensions()))
            throw new IllegalArgumentException("Tensor and index dimensions must match exactly; tensor: " + Arrays.toString(tensor.getDimensions()) + ", index: " + Arrays.toString(getDimensions()));
    }

    /**
     * Constructor specifying the ids to use in the index and a tensor equivalent (in shape/dimensionality) to one the
     * index is supposed to represent. This constructor is useful for instances where an index for a specific tensor
     * needs to be built.
     *
     * @param tensor
     *        A tensor equivalent in dimensionality to the one the index is to represent.
     *
     * @param ids
     *        The ids for this index.
     *
     * @throws IllegalArgumentException if any element in an {@code ids} sublist is repeated (each dimension must have
     *                                  unique ids) or if the dimensionality implied by {@code ids} does not match the
     *                                  dimensionality of {@code tensor}.
     */
    public BaseIndex(Tensor<?> tensor, List<List<I>> ids) {
        this(ids);
        checkAgainstTensor(tensor);
    }

    /**
     * Constructor specifying the ids to use in the index and a tensor equivalent (in shape/dimensionality) to one the
     * index is supposed to represent. This constructor is useful for instances where an index for a specific tensor
     * needs to be built.
     *
     * @param tensor
     *        A tensor equivalent in dimensionality to the one the index is to represent.
     *
     * @param ids
     *        The ids for this index.
     *
     * @throws IllegalArgumentException if any element in an {@code ids} subarray is repeated (each dimension must have
     *                                  unique ids) or if the dimensionality implied by {@code ids} does not match the
     *                                  dimensionality of {@code tensor}.
     */
    @SafeVarargs
    public BaseIndex(Tensor<?> tensor, I[] ... ids) {
        this(ids);
        checkAgainstTensor(tensor);
    }

    protected int[] getMaximumReferenceIndices() {
        return referenceIndices ? maxReferenceIndices : super.getMaximumReferenceIndices();
    }

//    public int[] getIndices(int ... indices) {
//        indices = super.getIndices(indices);
//        if (!referenceIndices)
//            return indices;
//        int[] index = new int[indices.length];
//        for (int i = 0; i < indices.length; i++)
//            index[i] = indexMap[i].get(indices[i]);
//        return index;
//    }

    public int getIndex(int dimension, int index) {
        if (referenceIndices) {
            checkDimension(dimension);
            //return indexMap[dimension].get(super.getIndex(dimension,index));
            return indexMap[dimension][index];
        } else {
            return super.getIndex(dimension,index);
        }
    }

    public List<List<I>> getIndexIds() {
        List<List<I>> ids = new LinkedList<List<I>>();
        for (List<I> id : this.ids)
            ids.add(Collections.unmodifiableList(id));
        return ids;
    }

    public int getIndex(int dimension, I id) {
        checkDimension(dimension);
        try {
            //return referenceIndices ? indexMap[dimension].get(idMap[dimension].get(id)) : idMap[dimension].get(id);
            return idMap[dimension].get(id);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Index id not found " + id);
        }
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public int[] getIndices(I ... ids) {
        if (ids.length != this.ids.size())
            throw new IllegalArgumentException("Index Id count must equal dimension count: expected " + this.ids.size() + ", found " + ids.length);
        int[] index = new int[ids.length];
        for (int i : dimensionIndices) {
            try {
                //index[i] = referenceIndices ? indexMap[i].get(idMap[i].get(ids[i])) : idMap[i].get(ids[i]);
                index[i] = idMap[i].get(ids[i]);
            } catch (NullPointerException e) {
                throw new IllegalArgumentException("Index id not found: " + ids[i]);
            }
        }
        return index;
    }

    public I getIndexId(int dimension, int index) {
        checkDimension(dimension);
        I id = idMap[dimension].getKey(index);
        if (id == null)
            throw new IndexOutOfBoundsException("Index out of bounds for dimension (" + dimension + ") of size " + dimensions[dimension] + ": " + index);
        return id;
    }

    public List<I> getIndexIds(int... indices) {
        if (indices.length != ids.size())
            throw new IllegalArgumentException("Index count must equal dimension count: expected " + ids.size() + ", found " + indices.length);
        List<I> ids = new LinkedList<I>();
        for (int i : dimensionIndices)
            ids.add(getIndexId(i,indices[i]));
        return ids;
    }
}
