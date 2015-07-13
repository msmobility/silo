package com.pb.sawdust.tensor.index;

import com.pb.sawdust.util.Range;
import com.pb.sawdust.util.array.ArrayUtil;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * The {@code StandardByOneIndex} class is an index whose ids are 1-based sequential integers (as opposed to the 0-based
 * indexing used by the {@code StandardIndex}).
 *
 * @author crf <br/>
 *         Started 1/24/11 4:34 PM
 */
public class StandardOneBasedIndex extends AbstractIndex<Integer> {

    /**
     * Constructor specifying the dimensions of this index.
     *
     * @param dimensions
     *        The dimensions of this index.
     *
     * @throws IllegalArgumentException if any of the elements in {@code dimensions} is less than one.
     */
    public StandardOneBasedIndex(int ... dimensions) {
        super(dimensions);
    }

    public List<List<Integer>> getIndexIds() {
        List<List<Integer>> ids = new LinkedList<List<Integer>>();
        for (int dimension : dimensions)
            ids.add(Arrays.asList(ArrayUtil.toIntegerArray(new Range(1,dimension+1).getRangeArray())));
        return ids;
    }

    public int getIndex(int dimension, Integer id) {
        try {
            return getIndex(dimension,id-1);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Index id not found " + id);
        }
    }

    public int[] getIndices(Integer ... ids) {
        int[] indices = new int[ids.length];
        for (int i = 0; i < ids.length; i++)
            indices[i] = ids[i]-1;
        try {
            return getIndices(indices);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public Integer getIndexId(int dimension, int index) {
        return getIndex(dimension,index)+1;
    }

    public List<Integer> getIndexIds(int ... indices) {
        List<Integer> ids = new LinkedList<Integer>();
        for (int i : getIndices(indices))
            ids.add(i+1);
        return ids;
    }
}
