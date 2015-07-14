package com.pb.sawdust.tensor.read;

import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.IndexFactory;

import java.util.Map;

/**
 * The {@code TensorGroupReader} provides a framework for reading tensor groups.  Implementing classes will provide the
 * information and data needed to reconstruct stored {@code TensorGroup} instances.
 *
 * @param <T>
 *        the type of the tensor held by the tensor group read by this reader.
 *
 * @param <I>
 *        the type of the ids in the indices held by the tensor group read by this reader.
 *
 * @author crf <br/>
 *         Started Feb 8, 2010 1:49:18 PM
 */
public interface TensorGroupReader<T,I> {
    /**
     * If a {@code TensorGroupReader} instance creates a default index which is unnamed, then this constant should be
     * used as the name for this index. If more than one (unnamed) indices are created, then this name <i>may</i> be used,
     * with an appropritate unique identifier appended to it.
     */
    static final String DEFAULT_INDEX_NAME = "default index";

    /**
     * Get the dimensions of the tensor group read by this reader.
     *
     * @return the dimensions of the tensor group read by this reader.
     */
    int[] getDimensions();

    /**
     * Get the mapping from tensor keys to tensors for the tensor group.
     *
     * @param defaultFactory
     *        The default factory to use to construct the tensors in this tensor group.  There is no requirement that
     *        implementations use the specified factory to construct the tensors (<i>e.g.</i> if the tensors have already
     *        been created), but it will be used if necessary.
     *
     * @return the mapping of tensor keys to tensors for the tensor group.
     */

    Map<String,Tensor<T>> getTensorMap(TensorFactory defaultFactory);

    /**
     * Get the mapping from index keys to indices for the tensor group.
     *
     * @param defaultFactory
     *        The default factory to use to construct the indices in this tensor group.  There is no requirement that
     *        implementations use the specified factory to construct the tensors (<i>e.g.</i> if the indices have already
     *        been created), but it will be used if necessary.
     *
     * @return the mapping of index keys to indices for the tensor group.
     */
    Map<String,Index<I>> getIndexMap(IndexFactory defaultFactory);

    /**
     * Get the metadata for the tensor group read in by this reader.
     *
     * @return the metadata for the tensor group read in by this reader.
     */
    Map<String,Object> getTensorGroupMetadata();

    /**
     * Fill in tensors with the data provided by this reader. This method is intended to be used for performance enhancements
     * such that the data provided by this reader can be directly input into a tensor, as opposed to using an intermediate
     * {@code TypeSafeArray}. The keys of the input tensor mapping should match those that would be produced for the
     * correspoding data with {@link #getTensorMap(com.pb.sawdust.tensor.factory.TensorFactory)}, though not all
     * tensors provided by that mapping need be present in the input group passed here.
     *
     * @param tensorGroup
     *        The mapping of the names to the input tensors to fill.
     *
     * @return {@code tensor} if <code>buildIndex == false</code>, otherwise a reference to {@code tensor} using the
     *         index constructed from this reader.
     *
     * @throws IllegalArgumentException if the dimensionality of this reader and any of the tensors in {@code tensorGroup}
     *                                  are not equal, or if the {@code JavaType} of this reader does not match that of any
     *                                  of the tensors in {@code tensorGroup}.
     */
    Map<String,Tensor<T>> fillTensorGroup(Map<String,Tensor<T>> tensorGroup);
}
