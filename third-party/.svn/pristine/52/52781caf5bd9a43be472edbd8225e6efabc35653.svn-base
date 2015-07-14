package com.pb.sawdust.geography.tensor;

import com.pb.sawdust.geography.Geography;
import com.pb.sawdust.geography.GeographyElement;
import com.pb.sawdust.tensor.alias.vector.primitive.BooleanVector;
import com.pb.sawdust.tensor.decorators.id.primitive.size.IdBooleanD1TensorShell;
import com.pb.sawdust.tensor.factory.TensorFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * The {@code GeographicBooleanVector} is a {@code GeographicVector} holding {@code boolean}s.
 *
 * @param <G>
 *        The type of the {@code GeographyElement} used as ids in this vector.
 *
 * @author crf
 *         Started 10/17/11 12:36 PM
 */
public class GeographicBooleanVector<G extends GeographyElement<?>> extends IdBooleanD1TensorShell<G> implements GeographicVector<G,Boolean> {
    private final Geography<?,G> geography;

    /**
     * Constructor used to build a default vector from a geography.
     *
     * @param geography
     *        The geography defining the elements to be used for ids.
     *
     * @param factory
     *        The tensor factory used to build the underlying vector.
     */
    public GeographicBooleanVector(Geography<?,G> geography, TensorFactory factory) {
        this(geography,factory.booleanVector(geography.getGeography().size()));
    }

    /**
     * Constructor specifying the underlying vector and the geography from which the id elements will be defined. The
     * order of the elements as returned by <code>geography.getGeography()</code> is assumed to be the same as the ordering
     * of the associated values in {@code vector}.
     *
     * @param geography
     *        The geography defining the elements to be used for ids.
     *
     * @param vector
     *        The underlying vector holding the data values.
     *
     * @throws IllegalArgumentException if the size of {@code geography} is not equal the length of {@code vector}.
     */
    public GeographicBooleanVector(Geography<?,G> geography, BooleanVector vector) {
        super(vector,Arrays.asList((List<G>) new LinkedList<>(geography.getGeography())));
        this.geography = geography;
    }

    @Override
    public Geography<?,G> getGeography() {
        return geography;
    }
}
