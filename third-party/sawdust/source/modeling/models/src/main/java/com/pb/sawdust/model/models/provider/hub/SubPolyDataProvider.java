package com.pb.sawdust.model.models.provider.hub;

import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.tensor.alias.matrix.id.IdDoubleMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.SliceIndex;
import com.pb.sawdust.tensor.slice.SliceUtil;
import com.pb.sawdust.util.collections.SetList;

import java.util.Set;

/**
 * The {@code SubPolyDataProvider} class is a poly data provider which is a (contiguous) partition of a {@code PolyDataProvider}.
 * Its intended use is to fulfill the requirements of {@link PolyDataProvider#getSubDataHub(int, int)} in a simple and
 * efficient manner.
 *
 * @author crf <br/>
 *         Started Sep 14, 2010 9:32:07 PM
 */
public class SubPolyDataProvider<K> extends SubDataProviderHub<K> implements PolyDataProvider<K> {
    private PolyDataProvider<K> polyProvider;

    /**
     * Constructor specifying the base data provider hub and the bounds of the partition.
     *
     * @param provider
     *        The base poly data provider.
     *
     * @param start
     *        The start of the partition (inclusive).
     *
     * @param end
     *        The end of the partition (exclusive).
     *
     * @throws IllegalArgumentException if <code>end &lt;= start</code> or if {@code start} and/or {@code end} are out of
     *                                  {@code provider}'s data bounds (<i>i.e.</i> if either are less than zero or greater
     *                                  than the poly data provider's length).
     */
    public SubPolyDataProvider(PolyDataProvider<K> provider, int start, int end) {
        super(provider,start,end);
        polyProvider = provider;
    }

    public Set<String> getPolyDataVariables() {
        return polyProvider.getPolyDataVariables();
    }

    @SuppressWarnings("unchecked") //reference tensor will be of correct type
    public IdDoubleMatrix<? super K> getPolyData(String variable) {
//        IdDoubleMatrix<? super K> m = polyProvider.getPolyData(variable);
        DoubleMatrix m = polyProvider.getPolyData(variable);
        Index<?> index = SliceIndex.getSliceIndex(m.getIndex(),SliceUtil.range(start,end), SliceUtil.fullSlice(m.size(1)));
        return (IdDoubleMatrix<? super K>) m.getReferenceTensor(index);
    }

    public PolyDataProvider<K> getSubDataHub(int s, int e) {
        return (PolyDataProvider<K>) super.getSubDataHub(s,e);
    }

    public DataProvider getFullProvider(K key) {
        return polyProvider.getFullProvider(key).getSubData(start,end);
    }

    @Override
    public SetList<K> getDataKeys() {
        return polyProvider.getDataKeys();
    }
}
