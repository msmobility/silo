package com.pb.sawdust.util.collections;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code InjectiveEnumHashMap} is an injective map whose key values are constants from a specific {@code enum}
 * instance and whose value-to-key mapping is maintained in a hash map.
 *
 * @param <K>
 *        The ({@code enum}) type of the keys held by this map.
 *
 * @param <V>
 *        The type of the values held by this map.
 *
 * @author crf <br/>
 *         Started: Jun 21, 2008 8:38:36 AM
 */
public class InjectiveEnumHashMap<K extends Enum<K>,V> extends InjectiveEnumMap<K,V> {

    /**
     * Constructor specifying the {@code enum} class to use for the key.
     *
     * @param keyType
     *        The enum class whose entries are to be used as the keys for this map.
     */
    public InjectiveEnumHashMap(Class<K> keyType) {
        super(keyType,new HashMap<V,K>());
    }

    /**
     * Constructor which will create a map identical to a specified input map. The constructed map's keys will be
     * restricted to the same enum constants as the input map, and it will be initialized with the same key-value
     * mappings as the input map.
     *
     * @param map
     *        The input map to be copied.
     */
    public InjectiveEnumHashMap(InjectiveEnumMap<K,? extends V> map) {
        this(map.getKeyType());
        putAll(map);
    }

    /**
     * Constructor which will create an injective map with the key and value types, as well as mappings, as a
     * specified input map. If the input map is not an {@code InjectiveEnumMap} instance, then it must be non-empty
     * so that the key type may be inferred. The constructor will also throw an exception if a given value
     * exists more than once in the input map (which violates the injective map constraint that both the keys and values
     * must be in sets).
     *
     * @param map
     *        The input map to base the new injective map on.
     *
     * @throws IllegalArgumentException if map is empty and not an {@code InjectiveEnumMap} instance., or if {@code map}
     *                                  has more than one key mapping to a single value.
     */
    public InjectiveEnumHashMap(Map<K,? extends V> map) {
        this(determineEnumClassFromMap(map));
        putAll(map);
    }

}
