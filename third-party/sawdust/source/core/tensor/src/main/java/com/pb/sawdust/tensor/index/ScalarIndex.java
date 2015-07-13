package com.pb.sawdust.tensor.index;


import java.util.List;
import java.util.LinkedList;

/**
 * The {@code ScalarIndex} is an index for use with {@code Scalar}s. It deals correctly with all of the issues surrounding
 * the non-dimensionality of scalars.
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 6:59:25 PM
 */
public class ScalarIndex<I> extends AbstractIndex<I> {
    private static final ScalarIndex<?> index = new ScalarIndex();

    /**
     * Constructor.
     *
     * @param <I>
 *        The type of the index's id.
     *
     * @return a new scalar index.
     */
    @SuppressWarnings("unchecked") //I never actually used, so doesn't matter
    public static <I> ScalarIndex<I> getScalarIndex() {
        return (ScalarIndex<I>) index;
    }

    private ScalarIndex() {}

    @Override
    public List<List<I>> getIndexIds() {
        return new LinkedList<List<I>>();
    }

    public int[] getDimensions() {
        return dimensions;
    }

    @Override
    public int getIndex(int dimension, I id) {
        throw new IllegalArgumentException("Dimension out of bounds: " + dimension);
    }

    @Override
    @SuppressWarnings({"unchecked", "varargs"})
    public int[] getIndices(I ... ids) {
        if (ids.length != 0)
            throw new IllegalArgumentException("Scalar index has no dimensionality (referred to with " + ids.length + " dimensions).");
        return new int[0];
    }

    @Override
    public I getIndexId(int dimension, int index) {
            throw new IllegalArgumentException("Dimension out of bounds: " + dimension);
    }

    @Override
    public List<I> getIndexIds(int ... indices) {
        if (indices.length != 0)
            throw new IllegalArgumentException("Scalar index count must be empty (length = 0), found " + indices.length);
        return new LinkedList<I>();
    }
}
