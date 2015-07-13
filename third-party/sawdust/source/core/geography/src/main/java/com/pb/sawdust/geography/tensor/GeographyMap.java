package com.pb.sawdust.geography.tensor;

import com.pb.sawdust.geography.Geography;
import com.pb.sawdust.geography.GeographyElement;

/**
 * The {@code GeographyMap} interface specifies a (generic) mapping from one {@code Geography} to another.
 *
 * @param <F>
 *        The type of the {@code GeographyElement} contained by the "from" {@code Geography} associated with the implementing
 *        class.
 *
 * @param <T>
 *        The type of the {@code GeographyElement} contained by the "to" {@code Geography} associated with the implementing
 *        class.
 *
 * @author crf
 *         Started 10/17/11 4:51 PM
 */
public interface GeographyMap<F extends GeographyElement<?>,T extends GeographyElement<?>> {
    /**
     * Get the "from" geography associated with this class.
     *
     * @return the associated "from" geography.
     */
    Geography<?,F> getFromGeography();

    /**
     * Get the "to" geography associated with this class.
     *
     * @return the associated "to" geography.
     */
    Geography<?,T> getToGeography();
}
