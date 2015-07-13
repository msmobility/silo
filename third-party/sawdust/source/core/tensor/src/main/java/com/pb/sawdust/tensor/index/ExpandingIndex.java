package com.pb.sawdust.tensor.index;

import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.util.ContainsMetadataImpl;
import static com.pb.sawdust.util.Range.*;

import java.util.*;

/**
 * The {@code ExpandingIndex} factory class is used to create indices which expand a given index for use with higher-dimensional
 * tensors. This is done by appending size 1 dimensions to the index. For example, take an index for a 4-dimension tensor
 * with size <code>[2,5,3,4]</code>. The 4-dimension tensor could be treated as a 6-dimension tensor without any additions
 * to the underlying data by using an index with size <code>[2,5,3,4,1,1]</code>.
 * <p>
 * If the expanding index is to use the same (non-trivial) index ids as the underlying index, then ids for the additional
 * dimensions must be specified. If they are not, then the expanding index acts like a {@link com.pb.sawdust.tensor.index.StandardIndex},
 * using the {@code int} index indices for its ids.
 *
 * @author crf <br/>
 *         Started: Sep 29, 2009 9:31:56 PM
 */
public class ExpandingIndex {
    private ExpandingIndex() {} //no public constructor

    /**
     * Get an expanding index with the specified additional dimensions. The returned index will behave like a standard
     * index, using index indices for its ids.
     *
     * @param index
     *        The source index.
     *
     * @param additionalDimensions
     *        The number of additional dimensions to add.
     *
     * @return an expanding index with <code>index.size() + additionalDimensions</code> dimensions.
     */
    public static Index<Integer> getStandardExpandingIndex(Index<?> index, int additionalDimensions) {
        Map<Integer,Integer> expansionDims = new HashMap<Integer,Integer>();
        for (int i : range(index.size(),index.size()+additionalDimensions))
            expansionDims.put(i,1);
        return MirrorIndex.getStandardMirrorIndex(index,expansionDims);
    }


    /**
     * Get an expanding index using the underlying index's ids. The number of additional ids is determined by the number
     * of additional ids.
     *
     * @param index
     *        The source index.
     *
     * @param additionalIds
     *        The ids for the additional diemnsions.
     *
     * @return an expanding index with <code>index.size() + additionalIds.length</code> dimensions.
     */
    @SuppressWarnings("unchecked") //as I list is ok, the I ... will ensure this
    public static <I> Index<I> getExpandingIndex(Index<I> index, I ... additionalIds) {
        Map<Integer,List<I>> expansionDims = new HashMap<Integer,List<I>>();
        int baseSize = index.size();
        for (int i : range(additionalIds.length))
            expansionDims.put(baseSize+i,Arrays.asList(additionalIds[i]));
        return MirrorIndex.getMirrorIndex(index,expansionDims);
    }
}
