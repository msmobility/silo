package com.pb.sawdust.geography;

import com.pb.sawdust.geography.tensor.GeographicBooleanMatrix;
import com.pb.sawdust.geography.tensor.GeographicDoubleMatrix;

/**
 * The {@code GeographicMapping} interface specifies a structure for defining some spatial relationships between the elements
 * in two {@code Geography}s. Specifically, it contains information on which elements "overlap," and by how much. This
 * overlay information can be used to transfer data from one geography to another. This transfer relationship is expressed
 * in terms of a "from" geography, and a "to" geography.
 *
 * @param <F>
 *        The type of the "from" geography.
 *
 * @param <T>
 *        The type of the "to" geography.
 *
 * @author crf
 *         Started 10/17/11 11:38 AM
 */
public interface GeographicMapping<F extends GeographyElement<?>,T extends GeographyElement<?>> {
    /**
     * Get the "from" geography for this mapping.
     *
     * @return this mapping's "from" geography.
     */
    Geography<?,F> getFromGeography();

    /**
     * Get the "to" geography for this mapping.
     *
     * @return this mapping's "to" geography.
     */
    Geography<?,T> getToGeography();

    /**
     * Get the overlay matrix for this mapping, which specifies how much each element in the "from" geography overlaps
     * with the elements in the "to" geography. The first dimension (index 0) of the returned matrix will have one entry
     * for each element in the "from" geography (with a corresponding {@code GeographyElement} used as the id), and the
     * second dimension (index 1) will have one entry for each element in the "to" geography. For a given cell corresponding
     * to "from" geography element <code>f</code> and "to" geography element <code>t</code>, the value of the cell will
     * indicate what percentage of <code>f</code> overlaps with <code>t</code>.
     *
     * @return the matrix specifying the manner in which the "from" geography overlays with the "to" geography in this mapping.
     */
    GeographicDoubleMatrix<F,T> getOverlay();

    /**
     * Get the usage overlay for this mapping, which specifies whether a given element in the "from" geography overlaps
     * with one from the "to" geography. This matrix is analogous to that returned by {@link #getOverlay()}, only the cells
     * are <code>true</code> if the overlay matrix cell is greater than 0, <code>false</code> otherwise.
     *
     * @return the matrix specifying which elements in the "from" and "to" geographies overlap with each other.
     */
    GeographicBooleanMatrix<F,T> getUsageOverlay();
}
