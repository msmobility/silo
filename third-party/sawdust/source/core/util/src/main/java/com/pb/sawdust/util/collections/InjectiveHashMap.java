package com.pb.sawdust.util.collections;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code InjectiveHashMap} is an injective map whose mappings are maintained in hash maps.
 *
 * @param <K>
 *        The type of the keys held by this map.
 *
 * @param <V>
 *        The type of the values held by this map.
 *
 * @author crf <br/>
 *         Started: Jun 21, 2008 1:18:30 AM
 */
public class InjectiveHashMap<K,V> extends GenericInjectiveMap<K,V> {

    /**
     * Constructor for an empty {@code InjectiveHashMap}.
     */
    public InjectiveHashMap() {
        super(new HashMap<K,V>(),new HashMap<V,K>());
    }

    /**
     * Constructor for an empty {@code InjectiveHashMap} with a specified initial capacity.
     *
     * @param initialCapacity
     *        The initial capacity to use for the map's underlying maps.
     */
    public InjectiveHashMap(int initialCapacity) {
        super(new HashMap<K,V>(initialCapacity),new HashMap<V,K>(initialCapacity));
    }

    /**
     * Constructor for an empty {@code InjectiveHashMap} with a specified initial capacity and load factor.
     *
     * @param initialCapacity
     *        The initial capacity to use for the map's underlying maps.
     *
     * @param loadFactor
     *        The load factor to use for the map's underlying maps.
     */
    public InjectiveHashMap(int initialCapacity, int loadFactor) {
        super(new HashMap<K,V>(initialCapacity,loadFactor),new HashMap<V,K>(initialCapacity,loadFactor));
    }

    /**
     * Constructor for an {@code InjectiveHashMap} which contains all of the mappings of the specified map. If a given
     * value appears more than once, an exception will be thrown (as this does not conform to the injective map's
     * requirements that both the keys and values be sets).
     *
     * @param map
     *        The map whose mappings will be placed in the new map.
     */
    public InjectiveHashMap(Map<? extends K,? extends V> map) {
        this();
        putAll(map);
    }
}
