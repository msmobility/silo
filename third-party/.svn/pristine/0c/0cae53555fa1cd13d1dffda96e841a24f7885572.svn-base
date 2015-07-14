package com.pb.sawdust.geography.tensor;

import com.pb.sawdust.geography.Geography;
import com.pb.sawdust.geography.GeographyElement;
import com.pb.sawdust.tensor.decorators.id.size.IdD1Tensor;

/**
 * The {@code GeographicVector} interface specifies a vector whose ids are the elements in a {@code Geography}. The size
 * of the vector will be the same as the size of the geography; that is, every element in the geography will have a corresponding,
 * unique cell in the vector.
 *
 * @param <G>
 *        The type of the {@code GeographyElement} used as ids in this vector.
 *
 * @param <T>
 *        The type contained by this vector.
 *
 * @author crf
 *         Started 11/5/11 9:17 AM
 */
public interface GeographicVector<G extends GeographyElement<?>,T> extends IdD1Tensor<T,G>,GeographyContainer<G> {
    /**
     * {@inheritDoc}
     *
     * The {@code GeographyElement}s held by the {@code Geography} returned by this method will be used for the ids in
     * this vector. The ordering of the ids in this vector should be the same as the ordering of the elements in the
     * collection returned by {Geography.getGeography()}.
     */
    Geography<?,G> getGeography();
}
