package com.pb.sawdust.model.models.provider.hub;

import com.pb.sawdust.model.models.provider.AbstractIdData;
import com.pb.sawdust.model.models.provider.CompositeDataProvider;
import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.tensor.factory.TensorFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The {@code SimpleDataProviderHub} class is a basic implementation of {@code DataProviderHub}.  It holds data that is
 * both key-specific and key-unspecific (general).  When a data provider for a given key is requested, the data specific
 * to that key is combined with the key-unspecific data to form an aggregate data provider. This allows for the elimination
 * of redundant variable data spread across different data providers.
 *
 * @author crf <br/>
 *         Started Sep 14, 2010 8:05:07 AM
 */
public class SimpleDataProviderHub<K> extends AbstractIdData implements DataProviderHub<K> {
    protected final TensorFactory factory;
    private final Map<K,DataProvider> providers;
    private final Map<K,CompositeDataProvider> keySpecificProviders;
    private final CompositeDataProvider generalProvider;
    private int dataLength = UNINITIALIZED_HUB_LENGTH;

    /**
     * Constructor specifying the data id, the data keys, and the tensor factory used to build data results.
     *
     * @param dataId
     *        The data id to use for this provider hub.
     *
     * @param keys
     *        The data provider keys to use for the returned instance.
     *
     * @param factory
     *        The tensor factory used to build data results.
     */
    public SimpleDataProviderHub(int dataId, Set<K> keys, TensorFactory factory) {
        super(dataId);
        this.factory = factory;
        generalProvider = new CompositeDataProvider(factory);
        keySpecificProviders = new HashMap<K,CompositeDataProvider>();
        providers = new HashMap<K,DataProvider>();
        for (K key : keys) {
            keySpecificProviders.put(key,new CompositeDataProvider(factory));
            providers.put(key,new CompositeDataProvider(factory,generalProvider,keySpecificProviders.get(key)));
        }
    }

    /**
     * Constructor specifying the data keys and the tensor factory used to build data results.
     *
     * @param keys
     *        The data provider keys to use for the returned instance.
     *
     * @param factory
     *        The tensor factory used to build data results.
     */
    public SimpleDataProviderHub(Set<K> keys, TensorFactory factory) {
        this.factory = factory;
        generalProvider = new CompositeDataProvider(factory);
        keySpecificProviders = new HashMap<K,CompositeDataProvider>();
        providers = new HashMap<K,DataProvider>();
        for (K key : keys) {
            keySpecificProviders.put(key,new CompositeDataProvider(factory));
            providers.put(key,new CompositeDataProvider(factory,generalProvider,keySpecificProviders.get(key)));
        }
    }

    public DataProvider getProvider(K key) {
        if (!providers.containsKey(key))
            throw new IllegalArgumentException("Key not found for data provider hub: " + key);
        return providers.get(key);
    }

    public DataProviderHub<K> getSubDataHub(final int start, final int end) {
        return new SubDataProviderHub<K>(this,start,end);
    }

    void checkDataLength(int length) {
        if (dataLength == UNINITIALIZED_HUB_LENGTH)
            dataLength = length;
        else if (dataLength != length)
            throw new IllegalArgumentException(String.format("Provider data length (%d) does not match the data length for this provider hub (%d).",length,dataLength));
    }

    private DataProvider checkProviderLength(DataProvider provider) {
        checkDataLength(provider.getDataLength());
        return provider;
    }

    /**
     * Add a general (key-unspecific) data provider to this provider hub. If this provider hub already contains key-unspecific
     * provider data, then the data in the provider sent to this method will be composited with it.
     *
     * @param provider
     *        The provider to add.
     *
     * @throws IllegalArgumentException if this provider is already initialized, and {@code provider}'s length is not equal
     *                                  to the length of this hub.
     */
    public void addProvider(DataProvider provider) { 
        generalProvider.addProvider(checkProviderLength(provider));
    }

    /**
     * Add a key-specific data provider to this provider hub. If this provider hub already contains provider data for
     * this key, then the data in the provider sent to this method will be composited with it.
     *
     * @param key
     *        The key to the data provider is to be associated with.
     *
     * @param provider
     *        The provider to add.
     *
     * @throws IllegalArgumentException if this provider is already initialized, and {@code provider}'s length is not equal
     *                                  to the length of this hub.
     */
    public void addKeyedProvider(K key, DataProvider provider) {
        if (keySpecificProviders.containsKey(key)) {
            keySpecificProviders.get(key).addProvider(checkProviderLength(provider));
        } else {
            if (dataLength != UNINITIALIZED_HUB_LENGTH && provider.getDataLength() != getDataLength())
                throw new IllegalArgumentException(String.format("Provider length (%d) not equal to provider hub length (%d)",provider.getDataLength(),dataLength));
            keySpecificProviders.put(key,new CompositeDataProvider(factory,provider));
            providers.put(key,new CompositeDataProvider(factory,generalProvider,keySpecificProviders.get(key)));
        }
    }

    /**
     * Add key-specific providers to this provider hub. For each key, if this provider hub already contains provider data
     * for that key, then the data in the provider will be composited with it. The map passed to this method does not need
     * to have a provider for each data key used by this hub.
     *
     * @param providers
     *        A mapping from data keys to the (repsective) providers to add.
     *
     * @throws IllegalArgumentException if this provider is already initialized, and the length of any provider in {@code provider}
     *                                  is not equal to the length of this hub.
     */
    public void addKeyedProviders(Map<K,DataProvider> providers) {
        for (K key: providers.keySet())
            addKeyedProvider(key,providers.get(key));
    }

    public int getDataLength() {
        return dataLength;
    }

    public Set<K> getDataKeys() {
        return keySpecificProviders.keySet();
    }

    public int getAbsoluteStartIndex() {
        return 0;
    }

    public DataProvider getSharedProvider() {
        return generalProvider;
    }
}
