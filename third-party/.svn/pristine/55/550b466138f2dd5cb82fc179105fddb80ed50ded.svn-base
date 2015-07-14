package com.pb.sawdust.util.collections;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;

/**
 * The {@code GenericInjectiveMap} provides a basic implementation of an injective map. It enforces the
 * restrictions of the injective map by holding the mappings internally in two maps: one going from keys to
 * values, the other going from values to keys. This class generally should be used as a base class for more specific
 * injective map implementations.
 *
 * @param <K>
 *        The type of the keys held by this map.
 *
 * @param <V>
 *        The type of the values held by this map.
 *
 * @author crf <br/>
 *         Started: Jun 21, 2008 12:29:27 AM
 */
public class GenericInjectiveMap<K,V> implements InjectiveMap<K,V> {
    /**
     * The map holding the key-to-value mappings.
     */
    protected Map<K,V> keyValueMap;

    /**
     * The map holding the value-to-key mappings.
     */
    protected Map<V,K> valueKeyMap;

    /**
     * Constructor specifying the two maps which will be used to hold the key-value/value-key pairs. If the
     * mappings are not empty, then all of the key-value pairs (in both maps) will be attempted to be loaded.
     * There is no requirement that the two maps have to be reflections of each other before being processed
     * in this class. However, if a value exists more that once in either maps, then an exception will be thrown.
     * <p>
     * Note that since the maps entered in this constructor are used directly in this class, care must be taken to
     * avoid allowing outside access to these objects, otherwise the internal state of this injective map may be open to
     * compromise.
     *
     * @param keyValueMap
     *        The map to use for mapping the keys to values in this map.
     *
     * @param valueKeyMap
     *        The map to use for mapping the values to keys in this map.
     *
     * @throws IllegalArgumentException if a given object exists as a value more than once in either value.
     */
    public GenericInjectiveMap(Map<K,V> keyValueMap, Map<V,K> valueKeyMap) {
        this.keyValueMap = keyValueMap;
        this.valueKeyMap = valueKeyMap;
        if (keyValueMap.size() > 0 || valueKeyMap.size() > 0) {
            Map<K,V> mappings = new HashMap<K,V>(keyValueMap);
            Map<V,K> reverseMappings = new HashMap<V,K>(valueKeyMap);
            keyValueMap.clear();
            valueKeyMap.clear();
            putAll(mappings);
            for (V value : reverseMappings.keySet()) {
                put(reverseMappings.get(value),value);
            }
        }
    }

    public InjectiveMap<V,K> inverse() {
        //intellij says this is an unchecked assignment, but it isn't (and javac confirms this)
        return new GenericInjectiveMap<V,K>(valueKeyMap,keyValueMap);
    }

    public int size() {
        return keyValueMap.size();
    }

    public boolean isEmpty() {
        return keyValueMap.isEmpty();
    }

    public boolean containsKey(Object key) {
        return keyValueMap.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return valueKeyMap.containsKey(value);
    }

    public V get(Object key) {
        return keyValueMap.get(key);
    }

    public K getKey(V value) {
        return valueKeyMap.get(value);
    }

    public V put(K key, V value) {
        if (valueKeyMap.containsKey(value))
            throw new IllegalArgumentException("Value already defined in InjectiveMap: " + value);
        return forcePut(key,value);
    }

    public V forcePut(K key, V value) {
        if (valueKeyMap.containsKey(value)) {
            keyValueMap.remove(valueKeyMap.remove(value));
        }
        valueKeyMap.put(value,key);
        return keyValueMap.put(key,value);
    }

    public V remove(Object key) {
        V removedValue = keyValueMap.remove(key);
        valueKeyMap.remove(removedValue);
        return removedValue;
    }

    public K removeValue(Object value) {
        K removedKey = valueKeyMap.remove(value);
        keyValueMap.remove(removedKey);
        return removedKey;
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        Map<V,K> im = new HashMap<V,K>();
        for (K key : m.keySet()) {
            V value = m.get(key);
            if (valueKeyMap.containsKey(value))
                throw new IllegalArgumentException("Value already defined in InjectiveMap: " + value);
            else if (im.containsKey(value))
                throw new IllegalArgumentException("Multiple value instances in map argument to putAll: " + value);
            else
                im.put(value,key);
        }
        keyValueMap.putAll(m);
        valueKeyMap.putAll(im);
    }

    public void forcePutAll(Map<? extends K, ? extends V> m) {
        for (K key : m.keySet())
            forcePut(key,m.get(key));
    }

    public void clear() {
        keyValueMap.clear();
        valueKeyMap.clear();
    }

    public Set<K> keySet() {
        return keyValueMap.keySet();
    }

    public Set<V> values() {
        return valueKeyMap.keySet();
    }

    public Set<Entry<K,V>> entrySet() {
        return keyValueMap.entrySet();
    }

    public int hashCode() {
        return keyValueMap.hashCode();
    }

    public boolean equals(Object o) {
        if (o == this)
	        return true;
        if (!(o instanceof InjectiveMap))
            return false;
        return keyValueMap.equals(o);
    }
}


