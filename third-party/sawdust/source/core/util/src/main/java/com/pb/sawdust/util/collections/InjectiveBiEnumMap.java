package com.pb.sawdust.util.collections;

import java.util.EnumMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * The {@code InjectiveBiEnumMap} class is an injective map in which both keys and values are constants from enums. The
 * enum used for the keys and that used for the values need not be the same.  Internally, the mappings are backed by
 * two {@code java.util.EnumMap} instances, so all of the advantages (and quirks) of those maps are inherited here.
 *
 * @param <K>
 *        The ({@code enum}) type of the keys held by this map.
 *
 * @param <V>
 *        The ({@code enum}) type of the values held by this map.
 *
 * @author crf <br/>
 *         Started: Jun 23, 2008 12:43:24 PM
 */
public class InjectiveBiEnumMap<K extends Enum<K>,V extends Enum<V>> extends InjectiveEnumMap<K,V> {
    private final Class<V> valueType;

    /**
     * Constructor specifying the {@code enum}s which will be used for the keys and values of this injective map.
     *
     * @param keyType
     *        The class specifying the enum to use for the map's keys.
     *
     * @param valueType
     *        The class specifying the enum to use for the map's values.
     */
    public InjectiveBiEnumMap(Class<K> keyType, Class<V> valueType) {
        super(keyType, new EnumMap<V,K>(valueType));
        this.valueType = valueType;
    }

    /**
     * Constructor which will create a map identical to a specified input map. The constructed map's keys and values
     * will be restricted to the same enums as the input map, and it will be initialized with the same key-value mappings
     * as the input map.
     *
     * @param map
     *        The input map to be copied.
     */
    public InjectiveBiEnumMap(InjectiveBiEnumMap<K,V> map) {
        this(map.getKeyType(),map.getValueType());
        putAll(map);
    }

    /**
     * Constructor which will create an injective map with the key and value types, as well as mappings, as a
     * specified input map. If the input map is not an {@code InjectiveBiEnumMap} instance, then it must be non-empty
     * so that the key and value types may be inferred. The constructor will also throw an exception if a given value
     * exists more than once in the input map (which violates the injective map constraint that both the keys and values
     * must be in sets).
     *
     * @param map
     *        The input map to base the new injective map on.
     *
     * @throws IllegalArgumentException if map is empty and not an {@code InjectiveBiEnumMap} instance., or if {@code map}
     *                                  has more than one key mapping to a single value.
     */
    public InjectiveBiEnumMap(Map<K,V> map) {
        this(determineEnumClassFromMap(map),determineValueEnumClassFromMap(map));
        putAll(map);
    }

    /**
     * Get the enum class whose elements this map uses for its values.
     *
     * @return the enum class restricting this map's values.
     */
    public Class<V> getValueType() {
        return valueType;
    }

    private static <V extends Enum<V>> Class<V> determineValueEnumClassFromMap(Map<?,V> map) {
        try {
            return (map instanceof InjectiveBiEnumMap) ?
                        ((InjectiveBiEnumMap<?,V>) map).getValueType() :
                        map.values().iterator().next().getDeclaringClass();
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Non-InjectiveBiEnumMap input map must not be empty so that enum value class can be determined.");
        }
    }

    @SuppressWarnings("unchecked") //this is correct - the recursive type bounds can't be transferred directly
    public InjectiveMap<V,K> inverse() {
        return (InjectiveMap<V,K>) new InjectiveBiEnumMap(valueKeyMap);
    }

}
