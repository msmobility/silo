package com.pb.sawdust.util.collections.cache;

import java.lang.ref.WeakReference;

/**
 * The {@code WeakCache} class defines a cache which holds weak references to its keys. A weak reference will be
 * garbage collected when there are no strong (or soft) references to it; it is distinguished from a soft reference by the
 * fact that a soft reference may not be garbage collected until memory gets low. The documentation for
 * {@code ReferenceCache} describes the specifics of the cache behavior.
 *
 * @see java.lang.ref.WeakReference
 *
 * @author crf <br/>
 *         Started 1/19/11 11:39 AM
 */
public class WeakCache<K,V> extends ReferenceCache<K,V,WeakReference<K>,WeakReference<V>> {

    /**
     * Constructor specifying the limit of strong key references maintained by this cache. Note that once a strong reference
     * to a key is no longer maintained, there is a good chance it will be garbage-collected (unless another reference
     * is being maintained externally).
     * 
     * @param strongReferenceLimit
     *        The limit of strong references to maintain.
     *
     * @throws IllegalArgumentException if {@code strongReferenceLimit} is less than one.
     */
    public WeakCache(int strongReferenceLimit) {
        super(strongReferenceLimit);
    }

    @Override
    protected WeakReference<K> getKeyReference(K key) {
        return new WeakReference<K>(key);
    }

    @Override
    protected WeakReference<V> getValueReference(V value) {
        return new WeakReference<V>(value);
    }
}
