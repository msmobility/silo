package com.pb.sawdust.util;

import java.util.Set;

/**
 * The {@code ContainsMetadata} interface specifies that an implementing class contains metadata. Metadata, with respect
 * to this interface, refers to extra data contained as key-value pairs; it is essentially a very simplified map. The key
 * type can be specified by the implementer, but the value type is left as generic as possible (as {@code Object}).
 *
 * @param <K>
 *        The type of the metadata key.
 *
 * @author crf <br/>
 *         Started: Dec 14, 2009 11:30:19 AM
 */
public interface ContainsMetadata<K> {

    /**
     * Get the number of metadata elements.
     *
     * @return the number of elements in the metadata.
     */
    int metadataSize();

    /**
     * Get a set of all metadata keys.
     *
     * @return a set of all metadata keys.
     */
    Set<K> getMetadataKeys();

    /**
     * Determine if the metadata contains a specified key.
     *
     * @param key
     *        The metadata key.
     *
     * @return {@code true} if the key is contained in the metadata, {@code false} otherwise.
     */
    boolean containsMetadataKey(K key);

    /**
     * Get a metadata value.
     *
     * @param key
     *        The metadata key.
     *
     * @return the metadata value corresponding to {@code key}.
     *
     * @throws IllegalArgumentException if {@code key} is not found in the metadata.
     */
    Object getMetadataValue(K key);

    /**
     * Set a metadata value.
     *
     * @param key
     *        The metadata key.
     *
     * @param value
     *        The metadata value;
     */
    void setMetadataValue(K key, Object value);

    /**
     * Remove a key-value pair from the metadata.
     *
     * @param key
     *        The key to remove.
     *
     * @return the removed value (corresponding to {@code key}).
     *
     * @throws IllegalArgumentException if {@code key} is not found in the metadata.
     **/
    Object removeMetadataElement(K key);
}
