package com.pb.sawdust.model.models.provider.filter;

import com.pb.sawdust.model.models.provider.AbstractIdData;
import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.model.models.provider.hub.DataProviderHub;
import com.pb.sawdust.model.models.provider.hub.SubDataProviderHub;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.alias.vector.primitive.BooleanVector;
import com.pb.sawdust.tensor.factory.TensorFactory;

import java.util.Arrays;
import java.util.Set;

/**
 * The {@code FixedFilterDataProviderHub} ...
 *
 * @author crf <br/>
 *         Started 3/8/11 6:01 AM
 */
public class FixedFilterDataProviderHub<K> extends AbstractIdData implements DataProviderHub<K> {
    final TensorFactory factory;
    private final DataProviderHub<K> provider;
    final FixedDataFilter filter;
    final int[] index;

    public FixedFilterDataProviderHub(TensorFactory factory, DataProviderHub<K> provider, BooleanVector filter) {
        super();
        if (filter.size(0) != provider.getDataLength())
            throw new IllegalArgumentException(String.format("Filter size (%d) and provider length (%d) must be equal.",filter.size(0),provider.getDataLength()));
        this.provider = provider;
        this.filter = new FixedDataFilter(filter);
        int[] index = new int[filter.size(0)];
        int counter = 0;
        for (int i = 0; i < index.length; i++)
            if (filter.getCell(i))
                index[counter++] = i;
        this.index = Arrays.copyOfRange(index, 0, counter);
        this.factory = factory;
    }

    @Override
    public DataProvider getProvider(K key) {
        return new FixedFilterDataProvider(factory,provider.getProvider(key),filter);
    }

    @Override
    public DataProviderHub<K> getSubDataHub(int start, int end) {
        return new SubDataProviderHub<K>(this,start,end);
    }

    @Override
    public int getAbsoluteStartIndex() {
        return 0;
    }

    @Override
    public int getDataLength() {
        return index.length;
    }

    @Override
    public Set<K> getDataKeys() {
        return provider.getDataKeys();
    }

    @Override
    public DataProvider getSharedProvider() {
        return new FixedFilterDataProvider(factory,provider.getSharedProvider(),filter);
    }
}
