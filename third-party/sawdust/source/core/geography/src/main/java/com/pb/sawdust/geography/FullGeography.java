package com.pb.sawdust.geography;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * The {@code FullGeography} represents a geography with one element which encompasses all of the elements from another
 * geography.
 *
 * @param <I>
 *        The type of the identifier of the elements this geography encompasses.
 *
 * @param <G>
 *        The type of the elements this geography encompasses.
 *
 * @author crf
 *         Started 10/18/11 10:48 AM
 */
public class FullGeography<I,G extends GeographyElement<I>> extends Geography<String,FullGeography.EncompassingGeographyElement<I,G>> {

    /**
     * The {@code EncompassingGeographyElement} class is used as the single {@code GeographicElement} contained by a
     * {@code FullGeography} instance.
     *
     * @param <I>
     *        The type of the identifier of the elements in the geography this element encompasses.
     *
     * @param <G>
     *        The type of the elements this element encompasses.
     */
    public static class EncompassingGeographyElement<I,G extends GeographyElement<I>> extends NamedGeographyElement {
        private final Collection<G> elements;

        private static <I,G extends GeographyElement<I>> double sumSizes(Collection<G> elements) {
            double size = 0.0;
            for (G element : elements)
                size += element.getSize();
            return size;
        }

        private EncompassingGeographyElement(Collection<G> elements) {
            this("encompassing geography including " + elements.iterator().next().getIdentifier(),elements);
        }

        private EncompassingGeographyElement(String name, Collection<G> elements) {
            super(name,sumSizes(elements));
            this.elements = Collections.unmodifiableCollection(elements);
        }

        /**
         * Get the elements this element encompasses.
         *
         * @return the elements encompassed by this element.
         */
        public Collection<G> getBaseElements() {
            return elements;
        }
    }

    /**
     * Constructor specifying the elements this geography encompasses, as well as the name to use for the encompassing element's
     * identifier.
     *
     * @param elementName
     *        The identifier name for the encompassing geographic element.
     *
     * @param elements
     *        The elements this geography will encompass.
     */
    public FullGeography(String elementName, Collection<G> elements) {
        super(Arrays.asList(new EncompassingGeographyElement<I,G>(elementName,elements)));
    }

    /**
     * Constructor specifying the elements this geography encompasses. A default identifier name for the encompassing
     * element will be used.
     *
     * @param elements
     *        The elements this geography will encompass.
     */
    public FullGeography(Collection<G> elements) {
        super(Arrays.asList(new EncompassingGeographyElement<I,G>(elements)));
    }

    /**
     * Constructor specifying the geography which will be encompassed, as well as the name to use for the encompassing element's
     * identifier.
     *
     * @param elementName
     *        The identifier name for the encompassing geographic element.
     *
     * @param geography
     *        The geography this geography will encompass.
     */
    public FullGeography(String elementName, Geography<I,G> geography) {
        this(elementName,geography.getGeography());
    }

    /**
     * Constructor specifying the geography which will be encompassed. A default identifier name for the encompassing
     * element will be used.
     *
     * @param geography
     *        The geography this geography will encompass.
     */
    public FullGeography(Geography<I,G> geography) {
        this(geography.getGeography());
    }
}
