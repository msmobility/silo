package com.pb.sawdust.util.collections.cache;

/**
 * The {@code BoundedCountCache} is a cache whose key-value pair count is limited. If the maxiumum size of the cache is
 * reached, then any new elements added will cause the key with least recent reference (and its value) to be dropped.
 * Key referencing is defined in the documentation for {@code AbstractRefCache}.
 *
 * @param <K>
 *        The type of the keys held by this cache.
 *
 * @param <V>
 *        The type of the values held by this cache.
 *
 * @author crf <br/>
 *         Started: Dec 29, 2008 10:05:37 PM
 */
public class BoundedCountCache<K,V> extends BoundedCache<K,V> {

    /**
     * Constructor specifying the cache limit and cleaning priority.
     *
     * @param cacheLimit
     *        The maximum number of key-value pairs held by this cache.
     *
     * @param priority
     *        The cleaning priority that will be used for this cache.
     */
    public BoundedCountCache(int cacheLimit, AbstractRefCache.CacheCleaningPriority priority) {
        super(cacheLimit,priority);
    }

    /**
     * Constructor specifying the cache limit and using a {@link AbstractRefCache.CacheCleaningPriority#KEY_REFERENCE}
     * cache cleaning priority.
     *
     * @param cacheLimit
     *        The maximum number of key-value pairs held by this cache.
     */
    public BoundedCountCache(int cacheLimit) {
        super(cacheLimit);
    }

    protected int getValueSize(V value) {
        return 1;
    }

    protected int getValueSizeFromKey(K key) {
        return 1;
    }
}
