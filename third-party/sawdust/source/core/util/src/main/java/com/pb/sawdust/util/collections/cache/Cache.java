package com.pb.sawdust.util.collections.cache;

import java.util.Map;

/**
 * A {@code Cache} is a {@code Map} with a defined strategy for removing key-value pairs. This strategy can be useful
 * for reducing memory overhead and/or memory leaks. This interface defines a {@code cleanCache()} method, which can
 * be called to execute the key-value pair removal strategy. The strategy may extend beyond just calling this method
 * to <i>when</i> it is called (such as every 10 seconds or after a {@code set(K,V)} call), which introduces an
 * implementation-specific timing element into the strategy.
 *
 * @param <K>
 *        The type of the keys in this cache.
 *
 * @param <V>
 *        The type of the values in this cache.
 *
 * @author crf <br/>
 *         Started: Dec 29, 2008 10:21:42 PM
 */
public interface Cache<K,V> extends Map<K,V> {

    /**
     * "Clean" the cache by dropping key-value pairs from this cache deemed removable by the cache strategy.
     */
    void cleanCache();
}
