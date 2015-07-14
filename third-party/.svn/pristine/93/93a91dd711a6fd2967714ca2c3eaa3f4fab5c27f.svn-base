package com.pb.sawdust.tensor.read;

import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.array.TypeSafeArray;

import java.util.List;
import java.util.Map;

/**
 * The {@code TensorReader} interface provides a framework for reading tensors.  Implementing classes will provide information
 * and data for reconstructing stored {@code Tensor} instances, including data and indices.  This interface is used in
 * concert with the {@code TensorFactory} interface to construct stored tensors. In general, most {@code TensorReader}
 * implementations should have complementing implementations of {@code TensorWriter}.
 * <p>
 * Because of the (often) large memory demands for matrices, this interface has been devised such that it encourages
 * the implementing reader to write data directly into a tensor, as opposed to an independent data structure (from which
 * the data would then be copied into the tensor). With this is mind, the standard use of a reader implementation would
 * be the following:
 * <p>
 * <ol>
 *     <li>
 *         Create an empty tensor of the correct type and dimensionality.
 *     </li>
 *     <li>
 *         Fill in the data in the tensor.
 *     </li>
 *     <li>
 *         If necessary, create a reference tensor from the index specified by this reader.
 *     </li>
 *     <li>
 *         Fill in pertinent tensor metadata.
 *     </li>
 * </ol>
 * <p>
 * It is important to emphasize that copying data can be an expensive operation, so the second step above (filling in the
 * tensor) should write directly to the tensor. If the index information is read with the data, then it should be cached
 * to avoid reduntant reads. The tensor data can also be cached if necessary, but this will create a situation where data
 * is being copied, so preserving the above order of construction is important to maintain performance. Additionally,
 * since the empty tensor is to be created before the data is read, it is preferrable to have the determination of the
 * dimensionality and type of the tensor be independent of reading the data.
 *
 * @param <T>
 *        The type held by the tensor written by this writer.
 *
 * @param <I>
 *        The type of id for the tensor read in by this writer.
 *
 * @author crf <br/>
 *         Started: Dec 5, 2009 2:53:08 PM
 */
public interface TensorReader<T,I> {

    /**
     * Get the type held by the tensor read by this reader.
     *
     * @return the type held by the tensor read by this reader.
     */
    JavaType getType();

    /**
     * Get the dimensions of the tensor read by this reader.
     *
     * @return the dimensions of the tensor read by this reader.
     */
    int[] getDimensions();
//
//    /**
//     * Get the data for the tensor read in by this reader.
//     *
//     * @return the data for the tensor read in by the reader.
//     */
//    @Deprecated
//    TypeSafeArray<T> getData();

    /**
     * Get a list of ids (one list for each dimension) for the tensor read in by this reader. If the tensor has no ids
     * (<i>i.e.</i> is using standard/default indexing) then this method should return {@code null}.
     *
     * @return the ids for the tensor read in by this reader, or {@code null} if the tensor does not have ids.
     */
    List<List<I>> getIds();

    /**
     * Get the metadata for the tensor read in by this reader.
     *
     * @return the metadata for the tensor read in by this reader.
     */
    Map<String,Object> getTensorMetadata();

    /**
     * Fill in a tensor with the data provided by this reader.
     *
     * @param tensor
     *        The input tensor to fill.
     *
     * @return {@code tensor} if <code>buildIndex == false</code>, otherwise a reference to {@code tensor} using the
     *         index constructed from this reader.
     *
     * @throws IllegalArgumentException if the dimensionality of this reader and {@code tensor} are not equal, or if the
     *                                  type of this reader does not match that of {@code tensor}.
     */
    Tensor<T> fillTensor(Tensor<T> tensor);
}
