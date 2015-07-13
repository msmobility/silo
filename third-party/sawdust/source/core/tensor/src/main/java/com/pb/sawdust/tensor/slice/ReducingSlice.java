package com.pb.sawdust.tensor.slice;

import com.pb.sawdust.util.collections.cache.Cache;
import com.pb.sawdust.util.collections.cache.BoundedCountCache;

/**
 * The {@code ReducingSlice} provides a slice which holds only one element. It is called a <i>reducing</i> slice because
 * an dimension holding one element is effectively constant and can be collapsed out of an index/tensor, thereby
 * reducing the dimensionality of that index/tensor by one.
 *
 * @author crf <br/>
 *         Started: Feb 8, 2009 12:37:37 PM
 */
public class ReducingSlice extends BaseSlice {
    private static Cache<Integer,ReducingSlice> sliceCache = new BoundedCountCache<Integer,ReducingSlice>(3500);

    /**
     * Factory method to get a reducing slice which specifies the reference index point for the slice.
     *
     * @param indexPoint
     *        The reference index point for the slice.
     *
     * @return a reducing slice holding {@code indexPoint}.
     *
     * @throws IllegalArgumentException if {@code indexPoint} is less than zero.
     */
    public static ReducingSlice reducingSlice(int indexPoint) {
        if (indexPoint < 0)
            throw new IllegalArgumentException("Reducing slice index point must be zero or greater: " + indexPoint);
        ReducingSlice slice = sliceCache.get(indexPoint);
        if (slice == null) 
            sliceCache.put(indexPoint,slice = new ReducingSlice(indexPoint));
        return slice;
    }

    private ReducingSlice(int indexPoint) {
        super(indexPoint);
    }

    public String toString() {
        return "ReducingSlice(point=" + getMaxIndex() + ")";
    }
}
