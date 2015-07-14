package com.pb.sawdust.model.models.provider.filter;

import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.tensor.alias.vector.primitive.BooleanVector;
import com.pb.sawdust.tensor.slice.Slice;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code CachedDataFilter} ...
 *
 * @author crf <br/>
 *         Started 3/3/11 10:17 PM
 */
public abstract class CachedDataFilter extends AbstractDataFilter {
    private final Map<Integer,BooleanVector> filterCache;
    private final Map<Integer,Slice> filteredSliceCache;
    private final Map<Integer,Slice> unfilteredSliceCache;

    public CachedDataFilter() {
        filterCache = new HashMap<Integer, BooleanVector>();
        filteredSliceCache = new HashMap<Integer, Slice>();
        unfilteredSliceCache = new HashMap<Integer, Slice>();
    }

    protected abstract BooleanVector getFilterUncached(DataProvider provider);

    @Override
    public BooleanVector getFilter(DataProvider provider) {
        int id = provider.getDataId();
        if (!filterCache.containsKey(id)) {
            BooleanVector filter = getFilterUncached(provider);
            filterCache.put(id,filter);
            filteredSliceCache.put(id,super.getFilteredSlice(provider));
            unfilteredSliceCache.put(id,super.getUnfilteredSlice(provider));
        }
        return filterCache.get(id);
    }

    @Override
    public Slice getFilteredSlice(DataProvider provider) {
        int id = provider.getDataId();
        if (!filteredSliceCache.containsKey(id))
            getFilter(provider);
        return filteredSliceCache.get(id);
    }

    @Override
    public Slice getUnfilteredSlice(DataProvider provider) {
        int id = provider.getDataId();
        if (!unfilteredSliceCache.containsKey(id))
            getFilter(provider);
        return unfilteredSliceCache.get(id);
    }

    public void clearCache() {
        filterCache.clear();
        filteredSliceCache.clear();
        unfilteredSliceCache.clear();
    }
}
