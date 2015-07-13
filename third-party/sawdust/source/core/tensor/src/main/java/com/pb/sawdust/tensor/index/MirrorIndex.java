package com.pb.sawdust.tensor.index;

import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.util.ContainsMetadataImpl;

import java.lang.reflect.Array;
import java.util.*;

/**
 * The {@code MirrorIndex} provides an index which expands a base index to a greater number of dimension.  That is, the
 * base index is reflected across new dimensions.  For example, a base index with dimensions
 * <p>
 * <pre><code>
 *     [2,4]
 * </code></pre>
 * <p>
 * can be expanded using this class to an index with dimensions
 * <p>
 * <pre><code>
 *     [2,1,4,3]
 * </code></pre>
 * <p>
 * where the base index's dimensions (0 and 1) hae been transferred to the mirror index's 0 and 2 dimensions.
 * <p>
 * In when accessing the reference indices from a mirror index, the new dimensions effectively do not matter (as long as
 * they are within bounds) as the base index is reflected across them.  Also, note that the {@link #getIndices(int...)}
 * method will return an index array using the base index's dimensionality, and that {@link #getIndex(int, int)} will
 * return reference indices using the mirror index's dimensionality. This inconsistency is required to allow correct
 * referencing of the base index (and corresponding tensor), as well as maintaining both methods. The key point is that
 *
 * @author crf <br/>
 *         Started Sep 2, 2010 6:50:56 PM
 */
public class MirrorIndex<I> extends ContainsMetadataImpl<String>implements Index<I> {
    private final Index<I> index;
    private final Index<?> baseIndex;
    private final int baseIndexLength;

    private final int[] coreDimensions;
    private final int[] expansionDimensions;
    private final int[] dimensions;

    @SuppressWarnings("unchecked") //I will be Integer when we are calling this, so no worries
    private MirrorIndex(Index<?> index, Map<Integer,Integer> newDimensions) {
        baseIndexLength = index.size();
        coreDimensions = new int[index.size()];
        expansionDimensions = new int[newDimensions.size()];
        int[] newDims = new int[index.size()+newDimensions.size()];
        int counter = 0;
        for (int d : newDimensions.keySet()) {
            if (d < 0 || d >= newDims.length)
                throw new IllegalArgumentException(String.format("New dimension out of bounds: %d (old dimensions: %s, new dimensions: %s)",d,Arrays.toString(index.getDimensions()),newDimensions.keySet()));
            int size = newDimensions.get(d);
            if (size < 1)
                throw new IllegalArgumentException(String.format("New dimension (%d) size must be strictly positive: %d",d,size));
            newDims[d] = size;
            expansionDimensions[counter++] = d;
        }
        counter = 0;
        int coreCounter = 0;
        for (int i : index.getDimensions()) {
            while (newDimensions.containsKey(counter))
                counter++;
            newDims[counter] = i;
            coreDimensions[coreCounter++] = counter++;
        }
        baseIndex = index;
        this.index = (Index<I>) new StandardIndex(newDims);
        dimensions = this.index.getDimensions();
    }

    private MirrorIndex(Index<I> index, Map<Integer,List<I>> newDimensions, boolean ignored) {
        baseIndexLength = index.size();
        coreDimensions = new int[index.size()];
        expansionDimensions = new int[newDimensions.size()];
        List<List<I>> ids = new LinkedList<List<I>>(index.getIndexIds());
        int newSize = index.size() + newDimensions.size();
        int counter = 0;
        for (int d : new TreeSet<Integer>(newDimensions.keySet())) { //must be sorted for correct adding
            if (d < 0 || d >= newSize)
                throw new IllegalArgumentException(String.format("New dimension out of bounds: %d (old dimensions: %s, new dimensions: %s)",d,Arrays.toString(index.getDimensions()),newDimensions.keySet()));
            List<I> newIds = newDimensions.get(d);
//            if (newIds.size() == 0)
//                throw new IllegalArgumentException(String.format("New dimension (%d) size must be geater than zero.",d));
            ids.add(d,newIds);
            expansionDimensions[counter++] = d;
        }
        counter = 0;
        int coreCounter = 0;
        while (coreCounter < baseIndexLength) {
            while (newDimensions.containsKey(counter))
                counter++;
            coreDimensions[coreCounter++] = counter++;
        }
        baseIndex = index;
        this.index = new BaseIndex<I>(ids);
        dimensions = this.index.getDimensions();
    }

    @Override
    public int size() {
        return index.size();
    }

    @Override
    public int size(int dimension) {
        return index.size(dimension);
    }

    @Override
    public int[] getDimensions() {
        return index.getDimensions();
    }

    @Override
    public List<List<I>> getIndexIds() {
        return index.getIndexIds();
    }

    @Override
    public int getIndex(int dimension, I id) {
        return index.getIndex(dimension,id);
    }

    @Override
    @SuppressWarnings({"unchecked", "varargs"})
    public int[] getIndices(I ... ids) {
        return index.getIndices(ids);
    }

    @Override
    public I getIndexId(int dimension, int index) {
        return this.index.getIndexId(dimension,index);
    }

    @Override
    public List<I> getIndexIds(int ... indices) {
        return index.getIndexIds(indices);
    }

    @Override
    public int getIndex(int dimension, int index) {
        return this.index.getIndex(dimension,index);
    }

    @Override
    public int[] getIndices(int ... indices) {
        if (indices.length != dimensions.length)
            throw new IllegalArgumentException("Number of indices must match number of dimensions: expected " + dimensions.length + ", found " + indices.length);
        int ind;
        for (int i : expansionDimensions)
            if ((ind = indices[i]) >= dimensions[i] || ind < 0)
                throw new IndexOutOfBoundsException("Index out of bounds for dimension (" + i + ") of size " + dimensions[i] + ": " + ind);
        //do up to nine dims for efficiency
        switch (baseIndexLength) {
            case 0 : return new int[0];
            case 1 : return new int[] {indices[coreDimensions[0]]};
            case 2 : return new int[] {indices[coreDimensions[0]],indices[coreDimensions[1]]};
            case 3 : return new int[] {indices[coreDimensions[0]],indices[coreDimensions[1]],indices[coreDimensions[2]]};
            case 4 : return new int[] {indices[coreDimensions[0]],indices[coreDimensions[1]],indices[coreDimensions[2]],indices[coreDimensions[3]]};
            case 5 : return new int[] {indices[coreDimensions[0]],indices[coreDimensions[1]],indices[coreDimensions[2]],indices[coreDimensions[3]],indices[coreDimensions[4]]};
            case 6 : return new int[] {indices[coreDimensions[0]],indices[coreDimensions[1]],indices[coreDimensions[2]],indices[coreDimensions[3]],indices[coreDimensions[4]],indices[coreDimensions[5]]};
            case 7 : return new int[] {indices[coreDimensions[0]],indices[coreDimensions[1]],indices[coreDimensions[2]],indices[coreDimensions[3]],indices[coreDimensions[4]],indices[coreDimensions[5]],indices[coreDimensions[6]]};
            case 8 : return new int[] {indices[coreDimensions[0]],indices[coreDimensions[1]],indices[coreDimensions[2]],indices[coreDimensions[3]],indices[coreDimensions[4]],indices[coreDimensions[5]],indices[coreDimensions[6]],indices[coreDimensions[7]]};
            case 9 : return new int[] {indices[coreDimensions[0]],indices[coreDimensions[1]],indices[coreDimensions[2]],indices[coreDimensions[3]],indices[coreDimensions[4]],indices[coreDimensions[5]],indices[coreDimensions[6]],indices[coreDimensions[7]],indices[coreDimensions[8]]};
        }
        int[] coreIndices = new int[baseIndexLength];
        for (int i = 0; i < baseIndexLength; i++)
            coreIndices[i] = indices[coreDimensions[i]];
        return baseIndex.getIndices(coreIndices);
    }

    @Override
    public boolean isValidFor(Tensor tensor) {
        //return baseIndex.isValidFor(tensor);
        return Arrays.equals(baseIndex.getDimensions(),tensor.getDimensions());
    }

    public boolean equals(Object o) {
        return o == this || index.equals(o);
    }

    public int hashCode(){
        return index.hashCode();
    }

    /**
     * Get a mirror index with the specified additional dimensions. The returned index will behave like a standard
     * index, using index indices for its ids.
     *
     * @param index
     *        The source index.
     *
     * @param newDimensions
     *        A map joining the new dimensions and their sizes.
     *
     * @return a mirror index with {@code index} reflected across {@code newDimensions}.
     *
     * @throws IllegalArgumentException if any key in {@code newDimensions} is less than zero or <code>&gt;= index.size() + newDimensions.size()</code>,
     *                                  or if any value in {@code newDimensions} is less than 1.
     *
     */
    public static Index<Integer> getStandardMirrorIndex(Index<?> index, Map<Integer,Integer> newDimensions) {
        return new MirrorIndex<Integer>(index,newDimensions);
    }


    /**
     * Get an mirror index using the underlying index's ids and specified ids for the new dimensions.
     *
     * @param index
     *        The source index.
     *
     * @param newDimensions
     *        A map joining the new dimensions and their ids.
     *
     * @return an mirror index with {@code index} reflected across {@code newDimensions}.
     *
     * @throws IllegalArgumentException if any key in {@code newDimensions} is less than zero or <code>&gt;= index.size() + newDimensions.size()</code>,
     *                                  or if any id list in {@code newDimensions} has a size of zero or has repeated elements.
     */
    public static <I> Index<I> getMirrorIndex(Index<I> index, Map<Integer,List<I>> newDimensions) {
        return new MirrorIndex<I>(index,newDimensions,true);
    }
}
