package com.pb.sawdust.util.collections.cache;

import com.pb.sawdust.util.collections.LinkedSetList;
import com.pb.sawdust.util.collections.SetDeque;
import com.pb.sawdust.util.collections.SetList;

import java.util.*;

/**
 * The {@code DefinedCache} provides a cache which is explicitly cleaned via method calls.  That is, the only way to
 * remove key-value pairs from the cache is through the {@link #pullKey(Object)} method. Key-value pairs are removed
 * lazily: calling {@link #pullKey(Object)} queues a key-value pair for removal, but only when the cache is cleaned
 * will the pair be removed.
 *
 * @param <K>
 *        The type of keys for this cache.
 *
 * @param <V>
 *        The type of values for this cache.
 *
 * @author crf <br/>
 *         Jul 24, 2010 12:49:42 PM
 */
public class DefinedCache<K,V> extends AbstractRefCache<K,V> {
    private SetList<K> pulledKeys = new LinkedSetList<K>();

    @Override
    protected K clipCache(SetDeque<K> keys) {
        if (pulledKeys.size() > 0) {
            K key = pulledKeys.remove(0);
            keys.remove(key);
            return key;
        }
        return null;
    }

    /**
     * Queue a key-value pair for removal for removal from this cache.  The pair will not be immediately removed; instead,
     * the removal will occur the next time the cache is cleaned.
     *
     * @param key
     *        The key to remove.
     *
     * @return {@code true} if {@code key} is a key in this cache and has not already been slated for pulling, {@code false} otherwise.
     */
    public boolean pullKey(K key) { //won't immediately pull value, just queue it up; also, returns false if pulled key alredy in set
        return containsKey(key) && pulledKeys.add(key);
    }
}
