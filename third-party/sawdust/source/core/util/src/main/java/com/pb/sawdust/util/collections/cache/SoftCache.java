package com.pb.sawdust.util.collections.cache;

import java.lang.ref.SoftReference;

/**
 * The {@code SoftCache} class defines a cache which holds soft references to its keys. A soft reference is available
 * for garbage collection when there are no strong references to it; despite this, a soft reference will often not be
 * garbage collected until the virtual machine is running low on memory. The documentation for {@code ReferenceCache}
 * describes the specifics of the cache behavior.
 *
 * @see java.lang.ref.SoftReference
 *
 * @author crf <br/>
 *         Started 1/19/11 11:38 AM
 */
public class SoftCache<K,V> extends ReferenceCache<K,V,SoftReference<K>,SoftReference<V>> {

    /**
     * Constructor specifying the limit of strong key references maintained by this cache. Note that once a strong reference
     * to a key is no longer maintained, there is a chance it will be garbage-collected (unless another reference
     * is being maintained externally).
     *
     * @param strongReferenceLimit
     *        The limit of strong references to maintain.
     *
     * @throws IllegalArgumentException if {@code strongReferenceLimit} is less than one.
     */
    public SoftCache(int strongReferenceLimit) {
        super(strongReferenceLimit);
    }

    @Override
    protected SoftReference<K> getKeyReference(K key) {
        return new SoftReference<K>(key);
    }

    @Override
    protected SoftReference<V> getValueReference(V value) {
        return new SoftReference<V>(value);
    }
}
