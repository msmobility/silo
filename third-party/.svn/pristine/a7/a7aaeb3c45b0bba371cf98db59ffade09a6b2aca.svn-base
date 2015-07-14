package com.pb.sawdust.tensor.index;

import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.decorators.id.IdTensor;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.array.TypeSafeArray;

import static com.pb.sawdust.util.Range.*;

import java.util.*;

/**
 * The {@code IndexUtil} class provides convenience methods for dealing with {@code Index} objects.
 *
 * @author crf <br/>
 *         Started: Jan 11, 2009 4:30:18 PM
 */
public class IndexUtil {

    private static <I> int[][] getIndexMap(Index<?> index, List<List<I>> ids) {
        int[][] indices = new int[ids.size()][];
        if (indices.length != index.size())
            throw new IllegalArgumentException("Wrapped index must have same number of dimensions as new ids");
        int counter = 0;
        for (List<?> list : ids) {
            if (list.size() != index.size(counter))
                throw new IllegalArgumentException("Wrapped index dimension (" + counter + ") must have same length as new ids; expected " + index.size(counter) + ", found " + list.size());
            int[] ind = new int[list.size()];
            for (int i = 0; i < ind.length; i++)
                ind[i] = index.getIndex(counter,i);
            indices[counter++] = ind;
        }
        return indices;
    }

    @SafeVarargs
    static <I> List<List<I>> getIdList(I[] ... ids) {
        List<List<I>> idList = new LinkedList<List<I>>();
        for (I[] id : ids)
            idList.add(Arrays.asList(id));
        return idList;
    }

    /**
     * Get a mapping from an index's indices to its underlying reference indices. The first dimension of the returned array
     * corresponds to the dimensions of the index, and the second dimension will hold the reference index value. If the index
     * does not represent a reference, then it will just pass through its index indices.
     *
     * @param index
     *        The index.
     *
     * @return the mapping from the indices of {@code index} to its reference index positions.
     */
    public static int[][] getReferenceIndices(Index<?> index) {
        int[][] indices = new int[index.size()][];
        for (int i : range(indices.length)) {
            int[] refs = new int[index.size(i)];
            for (int j : range(refs.length))
                refs[j] = index.getIndex(i,j);
            indices[i] = refs;
        }
        return indices;
    }

    /**
     * Factory method to get an index based on another index but using new ids.
     *
     * @param index
     *        The source (base) index.
     *
     * @param ids
     *        The ids to use in the new index.
     *
     * @param <I>
     *        The type of the ids in this index.
     *
     * @return an index based on {@code index} using {@code ids} for ids.
     *
     * @throws IllegalArgumentException if the number sublists in {@code ids} is not equal to the number of dimensions
     *                                  in {@code index}, or if the number of ids in each sublist in {@code ids} does not
     *                                  equal the size of its respective dimension (slice size).
     */
    @SuppressWarnings("unchecked") //ok here because we only deal with maps internally
    public static <I> Index<I> getIdIndex(Index<?> index, List<List<I>> ids) {
        return new BaseIndex<I>(getIndexMap(index,ids),ids);
    }

    /**
     * Factory method to get an index based on another index but using new ids.
     *
     * @param index
     *        The source (base) index.
     *
     * @param ids
     *        The ids to use in the new index.
     *
     * @param <I>
     *        The type of the ids in this index.
     *
     * @return an index based on {@code index} using {@code ids} for ids.
     *
     * @throws IllegalArgumentException if the number subarrays in {@code ids} is not equal to the number of dimensions
     *                                  in {@code index}, or if the number of ids in each subarray in {@code ids} does not 
     *                                  equal the size of its respective dimension (slice size).
     */
    @SafeVarargs
    @SuppressWarnings({"unchecked", "varargs"})
    public static <I> Index<I> getIdIndex(Index<?> index, I[] ... ids) {
        List<List<I>> idList = getIdList(ids);
        return getIdIndex(index,idList);
    }

    /**
     * Determine whether a given index is valid for a specified tensor shape.  This method should not be used to satisfy
     * the {@link Index#isValidFor(com.pb.sawdust.tensor.Tensor)} method as it calls this method on the input
     * index object.
     *
     * @param index
     *        The index in question.
     *
     * @param dimensions
     *        The dimensionality (shape of the tensor).
     *
     * @return {@code true} if {@code index} if valid for{@code dimensions}, {@code false} otherwise.
     */
    public static boolean indexValidFor(Index<?> index, int[] dimensions) {
        return index.isValidFor(new StubTensor(dimensions));
    }

    private static class StubTensor implements Tensor<Void> {
        private final int[] dimensions;
        private StubTensor(int[] dimensions) {
            this.dimensions = dimensions;
        }

        public int[] getDimensions() {
            return dimensions;
        }

        public int size(int dimension) {
            try {
                return dimensions[dimension];
            } catch (IndexOutOfBoundsException e) {
                throw new IllegalArgumentException();
            }
        }

        public int size() {
            return dimensions.length;
        }

        public int metadataSize() {return 0;}
        public Set<String> getMetadataKeys() {return null;}
        public boolean containsMetadataKey(String key) {return false;}
        public Object getMetadataValue(String key) {return null;}
        public void setMetadataValue(String key, Object value) {}
        public Object removeMetadataElement(String key) {return null;}
        public JavaType getType() {return null;}
        public Void getValue(int ... indices) {return null;}
        public void setValue(Void value, int ... indices) {}
        public TypeSafeArray<Void> getTensorValues(Class<Void> type) {return null;}
        public void setTensorValues(TypeSafeArray<? extends Void> typeSafeArray) {}
        public void setTensorValues(Tensor<? extends Void> tensor) {}
        public <I> IdTensor<Void, I> getReferenceTensor(Index<I> index) {return null;}
        public Index<?> getIndex() {return null;}
        public Iterator<Tensor<Void>> iterator() {return null;}
    }
}
