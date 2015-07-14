package com.pb.sawdust.geography.tensor;

import com.pb.sawdust.geography.Geography;
import com.pb.sawdust.geography.GeographyElement;
import com.pb.sawdust.tensor.alias.matrix.primitive.BooleanMatrix;
import com.pb.sawdust.tensor.decorators.id.primitive.size.IdBooleanD2TensorShell;
import com.pb.sawdust.tensor.factory.TensorFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * The {@code GeographicBooleanMatrix} is a {@code GeographicMatrix} holding {@code boolean}s.
 *
 * @param <G>
 *        The type of the {@code GeographyElement} used as ids in the matrix's first dimension.
 *
 * @param <H>
 *        The type of the {@code GeographyElement} used as ids in the matrix's second dimension.
 *
 * @author crf
 *         Started 10/17/11 12:39 PM
 */
public class GeographicBooleanMatrix<G extends GeographyElement<?>,H extends GeographyElement<?>> extends IdBooleanD2TensorShell<GeographyElement<?>> implements GeographicMatrix<G,H,Boolean> {
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
    public GeographicBooleanMatrix(Geography<?,G> fromGeography, Geography<?,H> toGeography, TensorFactory factory) {
        this(fromGeography,toGeography,factory.booleanMatrix(fromGeography.getGeography().size(),toGeography.getGeography().size()));
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
    public GeographicBooleanMatrix(Geography<?,G> fromGeography, Geography<?,H> toGeography, BooleanMatrix matrix) {
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
