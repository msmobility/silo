package com.pb.sawdust.tensor.index;

import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.util.ContainsMetadataImpl;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.Range;
import static com.pb.sawdust.util.Range.*;

import java.util.*;

/**
 * The {@code CollapsingIndex} class provides an index implementation where one or more dimensions have been "collapsed."
 * A collapsed dimension is one whose size is set to one, and thus is a constant as far as index points are concerned.
 * Because of this, the dimension can be effectively removed from the index. As an example, given an index with
 * dimensionality:
 * <pre><code>
 *     [2,5,3,6]
 * </code></pre>
 * one can create a collapsing index where the zero dimension is collapsed to point {@code 0} and the three dimension
 * is collapsed to point {@code 5}.  This results in an index with dimensionality:
 * <pre><code>
 *     [5,3]
 * </code></pre>
 * Calling {@code getIndices(3,1)} will return {@code [0,3,1,5]}.
 * <p>
 * This index can be used to create reference matrices which have collapsed a parent tensor's dimensions, such as are
 * used for the {@code Tensor} interface's {@code iterator()} method.
 *
 * @author crf <br/>
 *         Started: Jan 11, 2009 2:09:28 PM
 */
public class CollapsingIndex<I> extends ContainsMetadataImpl<String> implements Index<I> {
    private final Index<I> index;
    private final int[] dimensions;
    private final int[] unCollapsedIndices;
    private final int[] referenceIndexSource;
    private final ThreadLocal<int[]> referenceIndex; //will hold int[] with constants and spaces to be filled in when called on

    /**
     * Constructor specifying the source index to collapse. All dimensions with length one are automatically collapsed.
     *
     * @param startIndex
     *        The source index to collapse.
     */
    public CollapsingIndex(Index<I> startIndex) {
        List<Integer> unci = new LinkedList<Integer>();
        index = startIndex;
        int[] od = index.getDimensions();
        referenceIndexSource = new int[index.size()];
        for (int d = 0; d < od.length; d++)
            if (od[d] > 1)
                unci.add(d);
            else
                //referenceIndexSource[d] = index.getIndex(d,0);
                referenceIndexSource[d] = 0;
        unCollapsedIndices = new int[unci.size()];
        int counter = 0;
        dimensions = new int[unci.size()];
        for (int i : unci) {
            dimensions[counter] = index.size(i);
            unCollapsedIndices[counter++] = i;
        }
        referenceIndex = new ThreadLocal<int[]>() {
            public int[] initialValue() {
                return ArrayUtil.copyArray(referenceIndexSource);
            }
        };
    }

    /**
     * Constructor for an index which collapse specified size-1 dimensions.
     *
     * @param sourceIndex
     *        The source index to collapse.
     *
     * @param specifyDimensions
     *        Ignored parameter indicating this constructor specifies dimensions to collapse.
     *
     * @param dimensionsToCollapse
     *        The dimensions to collapse.
     *
     * @throws IllegalArgumentException if any of the dimensions in {@code dimensionsToCollapse} is not of size 1.
     */
    public CollapsingIndex(Index<I> sourceIndex, boolean specifyDimensions, int ... dimensionsToCollapse) {
        Set<Integer> uncollapsedIndices = new TreeSet<Integer>(Arrays.asList(ArrayUtil.toIntegerArray(Range.range(sourceIndex.size()).getRangeArray())));
        for (int d : dimensionsToCollapse)
            if (d < 0 || d > sourceIndex.size())
                throw new IllegalArgumentException("Dimension to collapse out of bounds (max " + sourceIndex.size() + "): " + d);
            else if (!(sourceIndex.size(d) == 1))
                throw new IllegalArgumentException("Dimension to collapse must be of size one; dimension " + d + " is of size " + sourceIndex.size(d));
            else
                uncollapsedIndices.remove(d);
        index = sourceIndex;
        dimensions = new int[index.size() - dimensionsToCollapse.length];
        unCollapsedIndices = ArrayUtil.toPrimitive(uncollapsedIndices.toArray(new Integer[uncollapsedIndices.size()]));
        for (int i : Range.range(unCollapsedIndices.length))
            dimensions[i] = sourceIndex.size(unCollapsedIndices[i]);
        referenceIndexSource = new int[index.size()];
        referenceIndex = new ThreadLocal<int[]>() {
            public int[] initialValue() {
                return ArrayUtil.copyArray(referenceIndexSource);
            }
        };
    }

    private void checkIndex(Index<?> index, int dimension, int reducedDimensionPoint) {
        if (reducedDimensionPoint < 0)
            throw new IllegalArgumentException("Reduced dimension point must be greater than zero: " + reducedDimensionPoint);
        if (index.size(dimension) <= reducedDimensionPoint)
            throw new IllegalArgumentException("Reduced dimension point out of bounds, max " + index.size(dimension) + ", found " + reducedDimensionPoint);
    }

    /**
     * Constructor for collapsing an index by one dimension.
     *
     * @param sourceIndex
     *        The source index to collapse.
     *
     * @param dimension
     *        The (zero-based) dimension to collapse.
     *
     * @param reducedDimensionPoint
     *        The (zero-based) point in {@code dimension} to which the index will be collaped to.
     *
     * @throws IllegalArgumentException if {@code dimension} is less than zero or greater than one less then the number
     *                                  of dimensions in {@code sourceIndex}, or if {@code reducedDimensionPoint} is less
     *                                  than zero or greater than one less than the size of {@code dimension}.
     */
    public CollapsingIndex(Index<I> sourceIndex, int dimension, int reducedDimensionPoint) {
        checkIndex(sourceIndex,dimension,reducedDimensionPoint);
        index = sourceIndex;
        dimensions = new int[index.size()-1];
        System.arraycopy(index.getDimensions(),0,dimensions,0,dimension);
        System.arraycopy(index.getDimensions(),dimension+1,dimensions,dimension,dimensions.length-dimension);
        unCollapsedIndices = new Range(dimensions.length).getRangeArray();
        for (int i = dimension; i < unCollapsedIndices.length; i++)
            unCollapsedIndices[i]++;
        referenceIndexSource = new int[index.size()];
        referenceIndexSource[dimension] = reducedDimensionPoint;
        referenceIndex = new ThreadLocal<int[]>() {
            public int[] initialValue() {
                return ArrayUtil.copyArray(referenceIndexSource);
            }
        };
    }

    /**
     * Constructor for collapsing multiple dimensions in an index.
     *
     * @param sourceIndex
     *        The source index to collapse.
     *
     * @param dimensionsToCollapse
     *        A mapping from the dimensions to collapse to the (respective) index point they should be collapsed to.
     *
     * @throws IllegalArgumentException if any of the keys in {@code dimensionsToCollapse} is less than zero or greater
     *                                  than one less then the number of dimensions in {@code sourceIndex}, or if any of
     *                                  the values in {@code reducedDimensionPoint} is less than zero or greater than one
     *                                  less than the size of its respective dimension.
     */
    public CollapsingIndex(Index<I> sourceIndex, Map<Integer,Integer> dimensionsToCollapse) {
        for (int dimension : dimensionsToCollapse.keySet())
            checkIndex(sourceIndex,dimension,dimensionsToCollapse.get(dimension));
        index = sourceIndex;
        dimensions = new int[index.size()-dimensionsToCollapse.size()];
        unCollapsedIndices = new int[dimensions.length];
        int counter = 0;
        for (int i : range(sourceIndex.size()))
            if (!dimensionsToCollapse.containsKey(i))
                unCollapsedIndices[counter++] = i;
        referenceIndexSource = new int[index.size()];
        for (int dimension : dimensionsToCollapse.keySet())
            referenceIndexSource[dimension] = dimensionsToCollapse.get(dimension);
        referenceIndex = new ThreadLocal<int[]>() {
            public int[] initialValue() {
                return ArrayUtil.copyArray(referenceIndexSource);
            }
        };
    }

    /**
     * Constructor for collapsing the last dimension in an index. This method is useful for implementing the iterator
     * in the {@code Tensor} interface.
     *
     * @param sourceIndex
     *        The source index to collapse.
     *
     * @param reducedDimensionPoint
     *        The (zero-based) point in {@code sourceIndex}'s last dimension to which the index will be collaped to.
     *
     * @throws IllegalArgumentException if {@code reducedDimensionPoint} is less than zero or greater than one less than
     *                                  the size of the last dimension in {@code sourceIndex}.
     */
    public CollapsingIndex(Index<I> sourceIndex, int reducedDimensionPoint) {
        //this(sourceIndex,sourceIndex.size()-1,reducedDimensionPoint)
        checkIndex(sourceIndex,sourceIndex.size()-1,reducedDimensionPoint);
        index = sourceIndex;
        dimensions = new int[index.size()-1];
        System.arraycopy(index.getDimensions(),0,dimensions,0,dimensions.length);
        unCollapsedIndices = new Range(dimensions.length).getRangeArray();
        referenceIndexSource = new int[index.size()];
        referenceIndexSource[dimensions.length] = reducedDimensionPoint;
        referenceIndex = new ThreadLocal<int[]>() {
            public int[] initialValue() {
                return ArrayUtil.copyArray(referenceIndexSource);
            }
        };
    }

    /**
     * Get the indices (from the source index) that this index collapses.  This should generally not be needed, except
     * when the collapsing index needs to be reconstructed.
     *
     * @return the (source) indices this index collapses.
     */
    public int[] getCollapsedIndices() {
        int[] collapsedIndices = new int[index.size() - unCollapsedIndices.length];
        int counter = 0;
        int unCounter = 0;
        for (int i : range(index.size()))
            if (unCollapsedIndices[unCounter] != i)
                collapsedIndices[counter++] = i;
            else
                unCounter++;
        return collapsedIndices;
    }

    public int size() {
        return dimensions.length;
    }

    public int size(int dimension) {
        try {
            return dimensions[dimension];
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Dimension out of bounds (max dimension=" + (dimensions.length-1) + "): " + dimension);
        }
    }

    public int[] getDimensions() {
        return ArrayUtil.copyArray(dimensions);
    }

    public List<List<I>> getIndexIds() {
        List<List<I>> ids = new LinkedList<List<I>>();
        int counter = 0;
        int indexCounter = 0;
        Iterator<List<I>> it = index.getIndexIds().iterator();
        while (indexCounter < unCollapsedIndices.length) {
            List<I> list = it.next();
            if (unCollapsedIndices[indexCounter] == counter++) {
                indexCounter++;
                ids.add(list);
            }
        }
        return ids;
    }

    private void checkDimension(int dimension) {
        if (dimension >= dimensions.length || dimension < 0)
            throw new IllegalArgumentException("Dimension out of bounds (max dimension=" + (size()-1) + "): " + dimension);
    }

    public int getIndex(int dimension, I id) {
        checkDimension(dimension);
        return index.getIndex(unCollapsedIndices[dimension],id);
    }
    @SuppressWarnings({"unchecked", "varargs"})
    public int[] getIndices(I ... ids) {
        if (ids.length != size())
            throw new IllegalArgumentException("Number of indices must match number of dimensions: expected " + size() + ", found " + ids.length);
        int[] indices = new int[size()];
        for (int i = 0; i < indices.length; i++)
            indices[i] = index.getIndex(unCollapsedIndices[i],ids[i]);
        return indices;
    }

    public I getIndexId(int dimension, int index) {
        checkDimension(dimension);
        return this.index.getIndexId(unCollapsedIndices[dimension],index);
    }

    public List<I> getIndexIds(int ... indices) {
        if (indices.length != size())
            throw new IllegalArgumentException("Index count must equal dimension count: expected " + size() + ", found " + indices.length);
        List<I> ids = new LinkedList<I>();
        for (int i = 0; i < indices.length; i++)
            ids.add( getIndexId(i,indices[i]));
        return ids;
    }

    private void checkIndex(int dimension, int index) {
        if (index < 0 || index >= dimensions[dimension])
            throw new IndexOutOfBoundsException("Index out of bounds for dimension (" + dimension + ") of size " + dimensions[dimension] + ": " + index);
    }

    public int getIndex(int dimension, int index) {
        checkDimension(dimension);
        checkIndex(dimension,index);
        return index;
        //return this.index.getIndex(unCollapsedIndices[dimension],index);
    }

    public int[] getIndices(int ... indices) {
        if (indices.length != size())
            throw new IllegalArgumentException("Index count must equal dimension count: expected " + size() + ", found " + indices.length);        
        int[] index = referenceIndex.get();
        int counter = 0;
        for (int i : unCollapsedIndices)
            checkIndex(counter,index[i] = indices[counter++]);
        return index;
//        return this.index.getIndices(index);
    }

    public boolean isValidFor(Tensor tensor) {
        int[] index = referenceIndex.get();
        int[] td = tensor.getDimensions();
        if (td.length != index.length)
            return false;
        int counter = 0;
        for (int d : unCollapsedIndices)
            index[d] = dimensions[counter++]-1;
        for (int i = 0; i < td.length; i++)
            if (index[i] >= td[i])
                return false;
        return true;
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (o == null || !(o instanceof Index))
            return false;
        Index i = (Index) o;
        if (!Arrays.equals(dimensions,i.getDimensions()))
            return false;
        for (int d : range(dimensions.length))
            for (int j : range(dimensions[d]))
                if (getIndex(d,j) != i.getIndex(d,j) || getIndexId(d,j) != i.getIndexId(d,j))
                    return false;
        return true;
    }

    public int hashCode(){
        int result = 17;
        for (int b : dimensions)
            result = 37*result + b;
        for (int d : range(dimensions.length))
            for (int j : range(dimensions[d]))
                result = 37*(37*result + getIndex(d,j)) + getIndexId(d,j).hashCode();
        return result;
    }
}
