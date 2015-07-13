package com.pb.sawdust.util.collections.cache;

import com.pb.sawdust.util.collections.LinkedSetList;
import com.pb.sawdust.util.collections.SetDeque;

import java.util.*;

/**
 * The {@code AbstractRefCache} class provides a basic {@code Cache} implementation which orders its keys based on
 * when they are referenced. This ordering can be used in a strategy to remove ("clean") key-value pairs. A key is
 * considered to be "referenced" whenever it is used in a {@code get}, {@code put}, {@code putAll}, {@code containsKey},
 * or {@code remove} method (calls to non-existent keys (<i>e.g.</i> where {@code containsKey(key) == false}) are not
 * used). The more recently a given key is referenced, the closer to the head of the deque it will be placed.
 * <p>
 * Different levels of "cleaning priority" may be set, which define when the cache cleaning should take place. The default
 * is to clean the cache whenever a valid key is referenced, however strategies also where cache cleaning never (automatically)
 * occurs, where it occurs whenever the cache is modified, or where it occurs whenever a {@code Cache} interface method
 * is called.
 *
 * @param <K>
 *        The type of the keys held by this cache.
 *
 * @param <V>
 *        The type of the values held by this cache.
 *
 * @author crf <br/>
 *         Started: Dec 30, 2008 3:19:52 PM
 */
public abstract class AbstractRefCache<K,V> implements Cache<K,V> {
    private final SetDeque<K> cacheKeys;
    private final Map<K,V> cache;
    private final int cleaningPriority;

    /**
     * Clip a cache key from the cache map. This method is used to clean the cache: it is repeatedly called
     * to determine what key-value pair should be removed from the cache. If no key should be removed, this method
     * will return {@code null}. The cache key returned by this method will be removed from the cache (along with its
     * corresponding value). This method takes the deque of keys as an input, and a cache key returned (for removal)
     * should also have its corresponding key removed from the deque by this method.
     *
     * @param keys
     *        The deque of this cache's keys, ordered by key referencing.
     *
     * @return a cache key to be removed from the cache, or {@code null} if one should not be removed.
     */
    abstract protected K clipCache(SetDeque<K> keys);

    /**
     * Get the setdeque which will be used to hold (and order) the referenced keys in this cache. This setdeque should
     * be empty and a reference to it should be unavailable to it outside of this method's scope.
     * <p>
     * This method is used in the constructor, and should not be used elsewhere. <b>This method should return
     * a new empty instance of the backing setdeque type, not a reference to the setdeque used in this instance.</b> That is,
     * if this method is called repeatedly, a new object should be return, not a reference to one which has already been
     * created. This requirement is to ensure data encapsulation in the {@code Cache} implementation.
     * <p>
     * The default implementation returns a new {@code LinkedSetList<K>}.
     *
     * @return an empty setdeque.
     */
    protected SetDeque<K> getCacheKeyDeque() {
        return new LinkedSetList<K>();
    }

    /**
     * Get the map which will be used to hold key-value mappings in this cache. This map should be empty and a reference '
     * to it should be unavailable to it outside of this method's scope. As the {@code Map} methods in this class
     * generally will pass through to the map returned by this method, any benefits or disadvantages in the map
     * implementation will be inheirited here too.
     * <p>
     * This method is used in the constructor, and should not be used elsewhere. <b>This method should return
     * a new empty instance of the backing map type, not a reference to the map used in this instance.</b> That is,
     * if this method is called repeatedly, a new object should be return, not a reference to one which has already been
     * created. This requirement is to ensure data encapsulation in the {@code Cache} implementation.
     * <p>
     * The default implementation returns a new {@code HashMap<K,V>}.
     *
     * @return an empty map.
     */
    protected Map<K,V> getCacheMap() {
        return new HashMap<K,V>();
    }

    /**
     * Constructor specifying the cleaning priority to use for this cache.
     *
     * @param cleaningPriority
     *        The cleaning priority that will be used for this cache.
     */
    public AbstractRefCache(CacheCleaningPriority cleaningPriority) {
        cacheKeys = getCacheKeyDeque();
        cache = getCacheMap();
        this.cleaningPriority = cleaningPriority.ordinal();
    }


    /**
     * Constructor specifying a {@link AbstractRefCache.CacheCleaningPriority#KEY_REFERENCE}
     * cache cleaning priority.
     */
    public AbstractRefCache() {
        this(CacheCleaningPriority.KEY_REFERENCE);
    }

    /**
     * {@inheritDoc}
     *
     * This method repeatedly calls {@link #clipCache(SetDeque)}, removing the returned cache key from the cache
     * mapping, until {@code null} is returned.
     */
    public void cleanCache() {
        K cacheKey;
        while ((cacheKey = clipCache(cacheKeys)) != null)
            cache.remove(cacheKey);
    }


    //next two methods are needed by reference caching, but in general are a distraction, so I'm making them package
    // protected for the moment; if the need arises they could be made protected

    /**
     * Get the actual key for a given key. By default, this just returns its argument, but could be overridden if certain
     * equaility requirements must be met by keys (such as with reference caches).
     *
     * @param key
     *        The key.
     *
     * @return the actual key to be used in this cache.
     */
    K getKey(K key) {
        return key;
    }

    /**
     * Special method to directly access the cache store. This method might be needed to avoid infinite recursion when
     * doing special operations/inderection on caches.
     *
     * @param key
     *        The key; this is assumed to be a key which has already been passed through {@link #getKey(Object)}.
     *
     * @return the value associated with that key.
     */
    V internalGet(K key) {
        return cache.get(key);
    }

    private void cleaner(CacheCleaningPriority priority) {
        if (cleaningPriority >= priority.ordinal())
            cleanCache();
    }

    protected void keyReferenced(K key) {
        cacheKeys.forceAdd(key);
    }

    private void keyReferencedAndClean(K key, CacheCleaningPriority priority) {
        keyReferenced(key);
        cleaner(priority);
    }

    public int size() {
        cleaner(CacheCleaningPriority.ALL);
        return cacheKeys.size();
    }

    public boolean isEmpty() {
        cleaner(CacheCleaningPriority.ALL);
        return cacheKeys.isEmpty();
    }

    @SuppressWarnings("unchecked")
    public boolean containsKey(Object key) {
        K k = getKey((K) key);
        if (cacheKeys.contains(k)) {
            keyReferencedAndClean(k,CacheCleaningPriority.KEY_REFERENCE);
            return true;
        }
        return false;
    }

    public boolean containsValue(Object value) {
        cleaner(CacheCleaningPriority.ALL);
        return cache.containsValue(value);
    }

    @SuppressWarnings("unchecked")
    public V get(Object key) {
        K k = getKey((K) key);
        V value = cache.get(k);
        if (value != null)
            keyReferencedAndClean(k,CacheCleaningPriority.KEY_REFERENCE);
        return value;
    }

    public V put(K key, V value) {
        key = getKey(key);
        value = cache.put(key,value);
        keyReferencedAndClean(key,CacheCleaningPriority.MAP_MODIFICATION);
        return value;
    }

    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        K k = getKey((K) key);
        V value = cache.remove(k);
        if (value != null)
            keyReferencedAndClean(k,CacheCleaningPriority.MAP_MODIFICATION);
        return value;
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K,? extends V> e : m.entrySet()) {
            K k = getKey(e.getKey());
            cache.put(k,e.getValue());
            keyReferenced(k);
        }
        cleaner(CacheCleaningPriority.MAP_MODIFICATION);
    }

    public void clear() {
        cacheKeys.clear();
        cache.clear();
        cleaner(CacheCleaningPriority.MAP_MODIFICATION);
    }

    public Set<K> keySet() {
        return new CacheKeySet();
    }

    public Collection<V> values() {
        return cache.values();
    }

    public Set<Entry<K,V>> entrySet() {
        return cache.entrySet();
    }

    private class CacheKeySet implements Set<K> {

        public int size() {
            return cacheKeys.size();
        }

        public boolean isEmpty() {
            return cacheKeys.isEmpty();
        }

        public boolean contains(Object o) {
            return cacheKeys.contains(o);
        }

        public Iterator<K> iterator() {
            return cacheKeys.iterator();
        }

        public Object[] toArray() {
            return cacheKeys.toArray();
        }

        public <T> T[] toArray(T[] a) {
            return cacheKeys.toArray(a);
        }

        public boolean add(K k) {
            throw new UnsupportedOperationException();
        }

        public boolean remove(Object o) {
            return AbstractRefCache.this.remove(o) != null;
        }

        public boolean containsAll(Collection<?> c) {
            return cacheKeys.containsAll(c);
        }

        public boolean addAll(Collection<? extends K> c) {
            throw new UnsupportedOperationException();
        }

        public boolean retainAll(Collection<?> c) {
            Set<K> keysToDrop = new HashSet<K>();
            for (K key : cacheKeys)
                if (!c.contains(key))
                    keysToDrop.add(key);
            for (K key : keysToDrop)
                remove(key);
            return keysToDrop.size() > 0;
        }

        public boolean removeAll(Collection<?> c) {
            boolean changed = false;
            for (Object o : c)
                changed |= remove(o);
            return changed;
        }

        public void clear() {
            AbstractRefCache.this.clear();
        }
    }

    /**
     * The {@code CacheCleaningPriority} enum is used to define the frequency with which an {@code AbstractRefCache} is
     * cleaned.
     */
    public static enum CacheCleaningPriority {
        /**
         * Never (automatically) clean the cache.
         */
        NEVER,
        /**
         * Clean the cache whenever the cache mapping is modified.
         */
        MAP_MODIFICATION,
        /**
         * Clean the cache whenever a valid key is referenced. A key is valid if it already exists in the map, or will
         * exist after a referencing call (<i>e.g.</i> a {@code put(key,value)} call references {@code key}, which will
         * be valid after the call).
         */
        KEY_REFERENCE,
        /**
         * Clean the cache whenever a {@code Cache} method is called.
         */
        ALL
    }


}
