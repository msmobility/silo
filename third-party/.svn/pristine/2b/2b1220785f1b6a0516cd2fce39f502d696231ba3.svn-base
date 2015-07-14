package com.pb.sawdust.util.collections.cache;

import com.pb.sawdust.util.collections.LinkedSetList;
import com.pb.sawdust.util.collections.SetDeque;

import java.lang.ref.Reference;
import java.util.*;

/**
 * The {@code ReferenceCache} class defines a cache which holds references (in the {@code java.lang.ref.Reference} sense)
 * to its keys and values. These references may be garbage collected despite their use in the cache, which can be useful
 * for cache maintenance based on object scoping or memory-sensitive situations. When one of the references is garbage collected,
 * it will be removed (along with its value) from the cache. To prevent aggresive cache cleaning, a specified "strong"
 * cache limit is used which defines the minimum number of key-value pairs in the cache. As long as the key-value pair
 * count is below this limit, none of the cache elements will be removed. Once it is exceeded, then the least-recently
 * referenced keys beyond the cache limit are no longer "protected" by the cache and open for garbage collection (which
 * also may depend on external references).
 *
 * @author crf <br/>
 *         Started 1/19/11 8:06 AM
 */
public abstract class ReferenceCache<K,V,RK extends Reference<K>,RV extends Reference<V>> implements Cache<K,V> {
    private InternalReferenceCache internalCache;

    /**
     * Get the appropriate {@code Reference} object for the specified key.
     *
     * @param key
     *        The key.
     *
     * @return the reference which will wrap {@code key}.
     */
    protected abstract RK getKeyReference(K key);

    /**
     * Get the appropriate {@code Reference} object for the specified value.
     *
     * @param value
     *        The value.
     *
     * @return the reference which will wrap {@code value}.
     */
    protected abstract RV getValueReference(V value);

    /**
     * Constructor specifying the limit of strong key references maintained by this cache.
     *
     * @param strongReferenceLimit
     *        The limit of strong references to maintain.
     *
     * @throws IllegalArgumentException if {@code strongReferenceLimit} is less than one.
     */
    public ReferenceCache(int strongReferenceLimit) {
        internalCache = new InternalReferenceCache(strongReferenceLimit);
    }

    private InternalReference<K,RK> getKeyInternalReference(K key) {
        return new InternalReference<K,RK>(getKeyReference(key));
    }
    
    @Override
    public void cleanCache() {
        internalCache.cleanCache();
    }

    @Override
    public int size() {
        return internalCache.size();
    }

    @Override
    public boolean isEmpty() {
        return internalCache.isEmpty();
    }

    @Override
    @SuppressWarnings("unchecked") //cannot guarantee type K, but if not, a user error, so suppressing
    public boolean containsKey(Object key) {
        return internalCache.containsKey(getKeyInternalReference((K) key));
    }

    @Override
    @SuppressWarnings("unchecked") //cannot guarantee type V, but if not, a user error, so suppressing
    public boolean containsValue(Object value) {
        return internalCache.containsValue(getValueReference((V) value));
    }

    @Override
    @SuppressWarnings("unchecked") //cannot guarantee type K, but if not, a user error, so suppressing
    public V get(Object key) {
        return internalCache.get(getKeyInternalReference((K) key)).get();
    }

    @Override
    public V put(K key, V value) {
        RV rValue = internalCache.put(getKeyInternalReference(key),getValueReference(value));
        return rValue == null ? null : rValue.get();
    }

    @Override
    @SuppressWarnings("unchecked") //cannot guarantee type K, but if not, a user error, so suppressing
    public V remove(Object key) {
        RV rValue = internalCache.remove(getKeyInternalReference((K) key));
        return rValue == null ? null : rValue.get();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        Map<InternalReference<K,RK>,RV> map = new HashMap<InternalReference<K,RK>,RV>();
        for (K k : m.keySet())
            map.put(getKeyInternalReference(k),getValueReference(m.get(k)));
        internalCache.putAll(map);
    }

    @Override
    public void clear() {
        internalCache.clear();
    }

    @Override
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<K>();
        for (InternalReference<K,RK> ir : internalCache.keySet()) 
            keySet.add(ir.reference.get());
        return keySet;
    }

    @Override
    public Collection<V> values() {
        List<V> values = new LinkedList<V>();
        for (RV v : internalCache.values())
            values.add(v.get());
        return values;
    }

    @Override
    public Set<Entry<K,V>> entrySet() {
        Set<Entry<K,V>> entrySet = new HashSet<Entry<K,V>>();
        for (Entry<InternalReference<K,RK>,RV> entry : internalCache.entrySet())
            entrySet.add(new AbstractMap.SimpleEntry<K,V>(entry.getKey().reference.get(),entry.getValue().get()));
        return entrySet;
    }
    
    private class InternalReference<E,R extends Reference<E>> {
        final R reference;
    
        InternalReference(R reference) {
            this.reference = reference;
        }
    
        public boolean equals(Object o) {
            if (o == this)
                return true;
            if (o == null)
                return false;
            if (o instanceof InternalReference) {
                Object oe = ((InternalReference) o).reference.get();
                E te = reference.get();
                return oe != null && te != null && oe.equals(te);
            }
            return false;
        }
    
        public int hashCode() {
            E e = reference.get();
            return e == null ? 0 : e.hashCode();
        }
    
        boolean isDead() {
            return reference.get() == null;
        }
    }
    
    private class InternalReferenceCache extends AbstractRefCache<InternalReference<K,RK>,RV> {
        private final Map<InternalReference<K,RK>,InternalReference<K,RK>> irMap;
        private final int strongReferenceLimit;
        private final StrongReferencer strongReferences;
    
        public InternalReferenceCache(int strongReferenceLimit) {
            if (strongReferenceLimit < 1)
                throw new IllegalArgumentException("Strong reference limit must be at least 1: " + strongReferenceLimit);
            this.strongReferenceLimit = strongReferenceLimit;
            irMap = new HashMap<InternalReference<K,RK>,InternalReference<K,RK>>();
            strongReferences = new StrongReferencer();
        }
    
        protected void keyReferenced(InternalReference<K,RK> key) {
            super.keyReferenced(key);
            K k = key.reference.get();
            if (k != null)
                strongReferences.add(k,internalGet(key).get());
        }
    
        protected InternalReference<K,RK> getKey(InternalReference<K,RK> key) {
            InternalReference<K,RK> ir = irMap.get(key);
            if (ir != null)
                return ir;
            irMap.put(key,key);
            return key;
        }
        
        @Override
        protected InternalReference<K,RK> clipCache(SetDeque<InternalReference<K,RK>> keys) {
            Iterator<InternalReference<K,RK>> it = keys.iterator();
            InternalReference<K,RK> ir = null;
            boolean clip = false;
            while (it.hasNext())
                if (clip = (ir = it.next()).isDead())
                    break;
            if (clip)
                keys.remove(ir);
            else
                ir = null;
            return ir;
        }
    
        private class StrongReferencer {
            private LinkedSetList<K> keyReferences = new LinkedSetList<K>();
            private LinkedSetList<V> valueReferences = new LinkedSetList<V>();
    
            public void add(K key, V value) {
                keyReferences.forceAdd(key);
                valueReferences.forceAdd(value);
                clip();
            }
    
            private void clip() {
                while (keyReferences.size() > strongReferenceLimit) {
                    keyReferences.poll();
                    valueReferences.poll();
                }
            }
        }
    }

}
