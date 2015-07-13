package com.pb.sawdust.geography.tensor;

import com.pb.sawdust.geography.Geography;
import com.pb.sawdust.geography.GeographyElement;
import com.pb.sawdust.tensor.alias.matrix.Matrix;
import com.pb.sawdust.tensor.decorators.id.size.IdD2TensorShell;
import com.pb.sawdust.tensor.factory.TensorFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * The {@code GeographicMatrixShell} class provides a simple {@code GeographicMatrix} implementation via a matrix wrapper
 * class.
 *
 * @param <G>
 *        The type of the {@code GeographyElement} used as ids in this matrix's first dimension.
 *
 * @param <H>
 *        The type of the {@code GeographyElement} used as ids in this matrix's second dimension.
 *
 * @param <T>
 *        The type contained by this vector.
 *
 * @author crf
 *         Started 10/17/11 12:20 PM
 */
public class GeographicMatrixShell<G extends GeographyElement<?>,H extends GeographyElement<?>,T> extends IdD2TensorShell<T,GeographyElement<?>> implements GeographicMatrix<G,H,T> {
    private final Geography<?,G> fromGeography;
    private final Geography<?,H> toGeography;

    /**
     * Constructor used to build a default matrix from a pair of geographies.
     *
     * @param fromGeography
     *        The geography defining the elements to be used for first dimension ids.
     *
     * @param toGeography
     *        The geography defining the elements to be used for second dimension ids.
     *
     * @param factory
     *        The tensor factory used to build the underlying matrix.
     */
    public GeographicMatrixShell(Geography<?,G> fromGeography, Geography<?,H> toGeography, TensorFactory factory) {
        this(fromGeography,toGeography,factory.<T>matrix(fromGeography.getGeography().size(),toGeography.getGeography().size()));
    }

    /**
     * Constructor specifying the underlying matrix and the geographies from which the id elements will be defined. The
     * order of the elements as returned by <code>fromGeography.getGeography()</code> and <code>toGeography.getGeography()</code>
     * are assumed to be the same as the ordering of the associated values in {@code matrix}.
     *
     * @param fromGeography
     *        The geography defining the elements to be used for first dimension ids.
     *
     * @param toGeography
     *        The geography defining the elements to be used for second dimension ids.
     *
     * @param matrix
     *        The underlying matrix holding the data values.
     *
     * @throws IllegalArgumentException if the size of {@code fromGeography} is not equal the length of {@code matrix.size(0)},
     *                                  or the size of {@code toGeography} is not equal the length of {@code matrix.size(1)},.
     */
    @SuppressWarnings("unchecked")
    public GeographicMatrixShell(Geography<?,G> fromGeography, Geography<?,H> toGeography, Matrix<T> matrix) {
        super(matrix,Arrays.asList((List<GeographyElement<?>>) new LinkedList<>(fromGeography.getGeography()),(List<GeographyElement<?>>) new LinkedList<>(toGeography.getGeography())));
        this.fromGeography = fromGeography;
        this.toGeography = toGeography;
    }

    @Override
    public Geography<?,G> getFromGeography() {
        return fromGeography;
    }

    @Override
    public Geography<?,H> getToGeography() {
        return toGeography;
    }
}
