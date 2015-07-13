package com.pb.sawdust.tensor.index;

import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.util.ContainsMetadataImpl;
import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.util.array.ArrayUtil;

import java.util.*;

/**
 * The {@code ReorderingIndex} ...
 *
 * @author crf
 *         Started 8/31/11 9:27 AM
 */
public class ReorderingIndex<I> extends ContainsMetadataImpl<String> implements Index<I> {
    private final int[] dimensionMap;
    private final List<List<I>> ids;
    private final int[] dimensions;
    private final Index<I> sourceIndex;

    private void checkInputs(Index<I> originalIndex, int[] dimensionsMap) {
        if (dimensionsMap.length != originalIndex.size())
            throw new IllegalArgumentException(String.format("Dimension map size (%d) is not equal to original index size (%d).", dimensionsMap.length, originalIndex.size()));
        Set<Integer> unusedDims = new HashSet<Integer>();
        for (int i : range(dimensionsMap.length))
            unusedDims.add(i);
        for (int i : dimensionsMap)
            unusedDims.remove(i);
        if (unusedDims.size() > 0)
            throw new IllegalArgumentException("Dimension map does not contain all of indices from 0 to " + (dimensionsMap.length-1) + ": " + Arrays.toString(dimensionsMap));
    }

    public ReorderingIndex(Index<I> sourceIndex, int[] dimensionMap) {
        checkInputs(sourceIndex,dimensionMap);
        List<List<I>> originalIds = sourceIndex.getIndexIds();
        List<List<I>> newIds = new LinkedList<List<I>>();
        for (int i : dimensionMap)
            newIds.add(Collections.unmodifiableList(originalIds.get(i)));
        ids = Collections.unmodifiableList(newIds);
        this.dimensionMap = ArrayUtil.copyArray(dimensionMap);
        this.sourceIndex = sourceIndex;
        dimensions = new int[dimensionMap.length];
        for (int i : range(dimensionMap.length))
            dimensions[i] = sourceIndex.size(dimensionMap[i]);
    }

    @Override
    public int size() {
        return dimensionMap.length;
    }

    @Override
    public int size(int dimension) {
        try {
            return dimensions[dimension];
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Dimension out of bounds: " + dimension);
        }
    }

    @Override
    public int[] getDimensions() {
        return ArrayUtil.copyArray(dimensions);
    }

    @Override
    public List<List<I>> getIndexIds() {
        return ids;
    }

    @Override
    public int getIndex(int dimension, I id) {
        checkDimension(dimension);
        return sourceIndex.getIndex(dimensionMap[dimension],id);
    }

    @Override
    @SuppressWarnings("unchecked") //strange cast to get around generic array creation, but ok here
    public int[] getIndices(I... ids) {
        if (ids.length != this.ids.size())
            throw new IllegalArgumentException("Index Id count must equal dimension count: expected " + this.ids.size() + ", found " + ids.length);
        Object[] newIds = new Object[ids.length];
        for (int i : range(dimensionMap.length))
            newIds[dimensionMap[i]] = ids[i];
        return sourceIndex.getIndices((I[]) newIds);
    }

    @Override
    public I getIndexId(int dimension, int index) {
        checkDimension(dimension);
        return sourceIndex.getIndexId(dimensionMap[dimension],index);
    }

    @Override
    public List<I> getIndexIds(int... indices) {
        if (indices.length != ids.size())
            throw new IllegalArgumentException("Index count must equal dimension count: expected " + ids.size() + ", found " + indices.length);
        int[] newIndices = new int[indices.length];
        for (int i : range(newIndices.length))
            newIndices[dimensionMap[i]] = indices[i];
        return sourceIndex.getIndexIds(newIndices);
    }

    @Override
    public int getIndex(int dimension, int index) {
        checkIndex(dimension,index);
        return sourceIndex.getIndex(dimensionMap[dimension],index);
    }

    @Override
    public int[] getIndices(int... indices) {
        if (indices.length != dimensions.length)
            throw new IllegalArgumentException("Number of indices must match number of dimensions: expected " + dimensions.length + ", found " + indices.length);
        int[] baseIndices = new int[dimensionMap.length];
        for (int i : range(baseIndices.length))
            baseIndices[dimensionMap[i]] = indices[i];
        return sourceIndex.getIndices(baseIndices);
    }

    @Override
    public boolean isValidFor(Tensor tensor) {
        return sourceIndex.isValidFor(tensor);
    }

    private void checkDimension(int dimension) {
        if (dimension >= dimensions.length || dimension < 0)
            throw new IllegalArgumentException("Dimension out of bounds (max dimension=" + (size()-1) + "): " + dimension);
    }

    private void checkIndex(int dimension, int index) {
        boolean check;
        try {
            check = index < 0 || index >= dimensions[dimension];
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Dimension out of bounds (max dimension index=" + (size()-1) + "): " + dimension);
        }
        if (check)
            throw new IndexOutOfBoundsException("Index out of bounds for dimension (" + dimension + ") of size " + dimensions[dimension] + ": " + index);
    }
}
