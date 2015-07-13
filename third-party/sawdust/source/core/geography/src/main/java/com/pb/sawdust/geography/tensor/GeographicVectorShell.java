package com.pb.sawdust.geography.tensor;

import com.pb.sawdust.geography.Geography;
import com.pb.sawdust.geography.GeographyElement;
import com.pb.sawdust.tensor.alias.vector.Vector;
import com.pb.sawdust.tensor.decorators.id.size.IdD1TensorShell;
import com.pb.sawdust.tensor.factory.TensorFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * The {@code GeographicVectorShell} class provides a simple {@code GeographicVector} implementation via a vector wrapper class.
 *
 * @param <G>
 *        The type of the {@code GeographyElement} used as ids in this vector.
 *
 * @param <T>
 *        The type contained by this vector.
 *
 * @author crf
 *         Started 10/17/11 11:44 AM
 */
public class GeographicVectorShell<G extends GeographyElement<?>,T> extends IdD1TensorShell<T,G> implements GeographicVector<G,T> {
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
    public GeographicVectorShell(Geography<?,G> geography, TensorFactory factory) {
        this(geography,factory.<T>vector(geography.getGeography().size()));
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
    public GeographicVectorShell(Geography<?,G> geography, Vector<T> vector) {
        super(vector,Arrays.asList((List<G>) new LinkedList<>(geography.getGeography())));
        this.geography = geography;
    }

    @Override
    public Geography<?,G> getGeography() {
        return geography;
    }
}
