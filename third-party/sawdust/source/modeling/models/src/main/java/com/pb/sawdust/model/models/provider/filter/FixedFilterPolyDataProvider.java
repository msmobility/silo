package com.pb.sawdust.model.models.provider.filter;

import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.model.models.provider.hub.PolyDataProvider;
import com.pb.sawdust.model.models.provider.hub.SubPolyDataProvider;
import com.pb.sawdust.tensor.alias.matrix.id.IdDoubleMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;
import com.pb.sawdust.tensor.alias.vector.primitive.BooleanVector;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.index.SliceIndex;
import com.pb.sawdust.tensor.slice.SliceUtil;
import com.pb.sawdust.util.collections.SetList;

import java.util.Set;

/**
 * The {@code FixedFilterPolyDataProvider} ...
 *
 * @author crf <br/>
 *         Started 3/8/11 6:34 AM
 */
public class FixedFilterPolyDataProvider<K> extends FixedFilterDataProviderHub<K> implements PolyDataProvider<K> {
    private final PolyDataProvider<K> provider;

    public FixedFilterPolyDataProvider(TensorFactory factory, PolyDataProvider<K> provider, BooleanVector filter) {
        super(factory, provider, filter);
        this.provider = provider;
    }

    @Override
    public Set<String> getPolyDataVariables() {
        return provider.getPolyDataVariables();
    }

    @Override
    @SuppressWarnings("unchecked") //slice index should keep ids as Ks, so will be ok here
    public IdDoubleMatrix<? super K> getPolyData(String variable) {
        DoubleMatrix mat =  provider.getPolyData(variable);
        return (IdDoubleMatrix<K>) mat.getReferenceTensor(SliceIndex.getSliceIndex(mat.getIndex(),filter.getFilteredSlice(null),SliceUtil.fullSlice(mat.size(1))));
    }

    @Override
    public DataProvider getFullProvider(K key) {
        return new FixedFilterDataProvider(factory,provider.getFullProvider(key),filter);
    }

    @Override
    public SetList<K> getDataKeys() {
        return provider.getDataKeys();
    }

    @Override
    public PolyDataProvider<K> getSubDataHub(int start, int end) {
        return new SubPolyDataProvider<K>(this,start,end);
    }
}
