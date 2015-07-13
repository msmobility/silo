package com.pb.sawdust.geography.tensor;

import com.pb.sawdust.geography.Geography;
import com.pb.sawdust.geography.GeographyElement;
import com.pb.sawdust.tensor.decorators.id.size.IdD2Tensor;

/**
 * The {@code GeographicMatrix} interface specifies a matrix whose ids are the elements in {@code Geography}s. The size
 * of the matrix dimensions will be the same as the size of each geography; that is, every element pair from the two geographies
 * will have a corresponding, unique cell in the matrix.
 *
 * @param <G>
 *        The type of the {@code GeographyElement} used as ids in the matrix's first dimension.
 *
 * @param <H>
 *        The type of the {@code GeographyElement} used as ids in the matrix's second dimension.
 *
 * @param <T>
 *        The type contained by this matrix.
 *
 * @author crf
 *         Started 11/5/11 9:39 AM
 */
public interface GeographicMatrix<G extends GeographyElement<?>,H extends GeographyElement<?>,T> extends IdD2Tensor<T,GeographyElement<?>>,GeographyMap<G,H> {
    /**
     * {@inheritDoc}
     *
     * The {@code GeographyElement}s held by the {@code Geography} returned by this method will be used for the ids in
     * the first dimension of this matrix. The ordering of the ids in this matrix should be the same as the ordering of
     * the elements in the collection returned by {Geography.getGeography()}.
     */
    Geography<?,G> getFromGeography();

    /**
     * {@inheritDoc}
     *
     * The {@code GeographyElement}s held by the {@code Geography} returned by this method will be used for the ids in
     * the second dimension of this matrix. The ordering of the ids in this matrix should be the same as the ordering of
     * the elements in the collection returned by {Geography.getGeography()}.
     */
    Geography<?,H> getToGeography();
}
