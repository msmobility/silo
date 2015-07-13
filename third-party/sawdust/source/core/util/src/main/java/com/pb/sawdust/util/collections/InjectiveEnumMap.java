package com.pb.sawdust.util.collections;

import java.util.Map;
import java.util.EnumMap;
import java.util.NoSuchElementException;

/**
 * The {@code InjectiveEnumMap} is an injective map whose key values are constants from a specific {@code enum} instance.
 * More specifically, an {@code java.util.EnumMap} is used to hold the key-to-value mappings, so all of the advantages
 * (and quirks) of that class are inherited here. As the value-to-key mapping is not defined in this class, this class
 * should generally be used to construct more specific injective maps using enums as their key type.
 *
 * @param <K>
 *        The type of the keys held by this map.
 *
 * @param <V>
 *        The ({@code enum}) type of the values held by this map.
 *
 * @author crf <br/>
 *         Started: Jun 23, 2008 1:01:47 PM
 *
 * @see com.pb.sawdust.util.collections.InjectiveEnumHashMap
 * @see com.pb.sawdust.util.collections.InjectiveBiEnumMap
 */
public class InjectiveEnumMap<K extends Enum<K>,V> extends GenericInjectiveMap<K,V> {
    private final Class<K> keyType;

    /**
     * Constructor specifying the {@code enum} class to use for the key, as well as the map to use for the value-to-key
     * mappings. The {@code valueMap} is used directly in this class, so care should be taken to avoid allowing outside
     * access to this objects, otherwise the internal state of this injective map may be open to compromise.
     *
     * @param keyType
     *        The enum class whose entries are to be used as the keys for this map.
     *
     * @param valueMap
     *        The map to use for the value-to-key mappings inside this map.
     */
    InjectiveEnumMap(Class<K> keyType, Map<V,K> valueMap) {
        super(new EnumMap<K,V>(keyType),valueMap);
        this.keyType = keyType;
    }

    /**
     * Get the enum class whose elements this map uses for its keys.
     *
     * @return the enum class restricting this map's keys.
     */
    public Class<K> getKeyType() {
        return keyType;
    }

    /**
     * Determine the enum class which whose elements are used as the keys for a specified map. If the map is not
     * an {@code InjectiveEnumMap} instance, then the map must be non-empty (have at least one entry) so that the
     * enum type of the map can be inferred.
     *
     * @param map
     *        The input map from which the key enum class will be determined.
     *
     * @return the enum class whose elements are used as keys in {@code map}.
     *
     * @throws IllegalArgumentException if map is empty and not an {@code InjectiveEnumMap} instance.
     */
    protected static <K extends Enum<K>> Class<K> determineEnumClassFromMap(Map<K,?> map) {
        try {
            return (map instanceof InjectiveEnumMap) ?
                        ((InjectiveEnumMap<K,?>) map).getKeyType() :
                        map.keySet().iterator().next().getDeclaringClass();
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Non-InjectiveEnumMap input map must not be empty so that enum key class can be determined.");
        }
    }

}
