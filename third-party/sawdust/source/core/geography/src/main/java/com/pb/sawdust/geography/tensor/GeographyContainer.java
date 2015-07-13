package com.pb.sawdust.geography.tensor;

import com.pb.sawdust.geography.Geography;
import com.pb.sawdust.geography.GeographyElement;

/**
 * The {@code GeographyContainer} interface specifies a class which involves a {@code Geography} in some way. It specifies
 * the means by which the associated {@code Geography} may be obtained.
 *
 * @param <G>
 *        The type of the {@code GeographyElement} contained by the {@code Geography} associated with the implementing class.
 *
 * @author crf
 *         Started 10/17/11 4:47 PM
 */
public interface GeographyContainer<G extends GeographyElement<?>> {
    /**
     * Get the geography associated with this class.
     *
     * @return the associated geography.
     */
    Geography<?,G> getGeography();
}
