package com.pb.sawdust.model.models.provider.filter;

import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.alias.vector.primitive.BooleanVector;
import com.pb.sawdust.tensor.slice.Slice;

/**
 * The {@code FixedDataFilter} ...
 *
 * @author crf <br/>
 *         Started 3/5/11 9:16 AM
 */
public class FixedDataFilter extends AbstractDataFilter {
    private final BooleanVector filter;
    private final Slice filteredSlice;
    private final Slice unfilteredSlice;

    public FixedDataFilter(BooleanVector filter) {
        this.filter = (BooleanVector) TensorUtil.copyOf(filter,ArrayTensor.getFactory());
        filteredSlice = super.getFilteredSlice(null);
        unfilteredSlice = super.getUnfilteredSlice(null);
    }

    public FixedDataFilter(DataFilter filter, DataProvider provider) {
        this(filter.getFilter(provider));
    }

    private void checkProvider(DataProvider provider) {
        if (provider != null && provider.getDataLength() != filter.size(0))
            throw new IllegalArgumentException(String.format("Provider length (%d) does not equal fixed data filter's length (%d).",provider.getDataLength(),filter.size(0)));
    }

    @Override
    public BooleanVector getFilter(DataProvider provider) {
        checkProvider(provider);
        return filter;
    }

    @Override
    public Slice getFilteredSlice(DataProvider provider) {
        checkProvider(provider);
        return filteredSlice;
    }

    @Override
    public Slice getUnfilteredSlice(DataProvider provider) {
        checkProvider(provider);
        return unfilteredSlice;
    }
}
