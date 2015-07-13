package com.pb.sawdust.util.collections.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * The {@code SynchronizedCache} ...
 *
 * @author crf
 *         Started 6/21/12 7:45 AM
 */
public class SynchronizedCache<K,V> implements Cache<K,V> {
    private final Cache<K,V> cache;
    private final Map<K,V> synchronizedMap;

    public SynchronizedCache(Cache<K,V> cache) {
        this.cache = cache;
        synchronizedMap = Collections.synchronizedMap(cache);
    }

    @Override
    public void cleanCache() {
        synchronized (synchronizedMap) {
            cache.cleanCache();
        }
    }

    @Override
    public int size() {
        return synchronizedMap.size();
    }

    @Override
    public boolean isEmpty() {
        return synchronizedMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return synchronizedMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return synchronizedMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return synchronizedMap.get(key);
    }

    @Override
    public V put(K key, V value) {
        return synchronizedMap.put(key,value);
    }

    @Override
    public V remove(Object key) {
        return synchronizedMap.remove(key);
    }

    @Override
    public void putAll(Map<? extends K,? extends V> m) {
        synchronizedMap.putAll(m);
    }

    @Override
    public void clear() {
        synchronizedMap.clear();
    }

    @Override
    public Set<K> keySet() {
        return synchronizedMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return synchronizedMap.values();
    }

    @Override
    public Set<Entry<K,V>> entrySet() {
        return synchronizedMap.entrySet();
    }
}
