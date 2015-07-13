package com.pb.sawdust.model.models.provider.tensor;

import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.util.ContainsMetadataImpl;
import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.util.collections.SetList;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * The {@code PolyDataTensorIndex} ...
 *
 * @author crf
 *         Started 4/9/12 2:16 PM
 */
public class PolyDataTensorIndex<K> extends ContainsMetadataImpl<String> implements Index<Object> {
    private final PolyDataIndexProvider indexProvider;
    private final SetList<K> columnKeys;
    private final ThreadLocal<int[]> indices;

    public PolyDataTensorIndex(PolyDataIndexProvider indexProvider, SetList<K> columnKeys) {
        this.indexProvider = indexProvider;
        this.columnKeys = columnKeys;
        indices = new ThreadLocal<int[]>() {
            public int[] initialValue() {
                return new int[PolyDataTensorIndex.this.indexProvider.getDimensionCount()];
            }
        };
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public int size(int dimension) {
        switch (dimension) {
            case 0 : return indexProvider.getIndexLength();
            case 1 : return columnKeys.size();
            default : throw new IllegalArgumentException("Dimension out of bounds: " + dimension);
        }
    }

    @Override
    public int[] getDimensions() {
        return new int[] {size(0),size(1)};
    }

    @Override
    public List<List<Object>> getIndexIds() {
        List<List<Object>> indexIds = new LinkedList<>();
        indexIds.add(Arrays.<Object>asList(range(size(0))));
        indexIds.add((new LinkedList<Object>(columnKeys)));
        return indexIds;
    }

    @Override
    public int getIndex(int dimension, Object id) {
        if (dimension < 0 || dimension > size())
            throw new IllegalArgumentException("Dimension out of bound (size = " + size() + "): " + dimension);
        if (dimension == 0) {
            Integer i = (Integer) id;
            checkIndex(dimension,i);
            return i;
        } else {
            return columnKeys.indexOf(id);
        }
    }

    @Override
    public int[] getIndices(Object ... ids) {
        if (ids.length != size())
            throw new IllegalArgumentException("Index Id count must equal dimension count: expected " + size() + ", found " + ids.length);
        int[] index = new int[ids.length];
        return new int[] {getIndex(0,ids[0]),getIndex(1,ids[1])};
    }

    @Override
    public Object getIndexId(int dimension, int index) {
        checkIndex(dimension,index);
        return dimension == 0 ? index : columnKeys.get(index);
    }

    private void checkIndices(int ... indices) {
        if (indices.length != size())
            throw new IllegalArgumentException("Number of indices must match number of dimensions: expected " + size() + ", found " + indices.length);
        for (int i = 0; i < size(); i++)
            if (indices[i] < 0 || indices[i] > size(i))
                throw new IllegalArgumentException(String.format("Index point (%d) out of bounds for dimension of size %d",indices[i],size(i)));
    }

    private void checkIndex(int dimension, int index) {
        if (dimension < 0 || dimension > size())
            throw new IllegalArgumentException("Dimension out of bound (size = " + size() + "): " + dimension);
        if (index < 0 || index > size(dimension))
            throw new IllegalArgumentException(String.format("Index point (%d) out of bounds for dimension of size %d",index,size(dimension)));
    }

    @Override
    public List<Object> getIndexIds(int ... indices) {
        checkIndices(indices);
        return Arrays.asList(indices[0],columnKeys.get(indices[1]));
    }

    @Override
    public int getIndex(int dimension, int index) {
        checkIndex(dimension,index);
        return 0; //different underlying dimensionality
    }

    @Override
    public int[] getIndices(int ... indices) {
        checkIndices(indices);
        int[] ind = this.indices.get();
        PolyDataIndexProvider indexProvider = this.indexProvider.getSpecifiedProvider(indices[1]);
        for (int i : range(ind.length))
            ind[i] = indexProvider.getIndex(i,indices[i]);
        return ind;
    }

    @Override
    public boolean isValidFor(Tensor tensor) {
        //does not fully check dimensions, but that will usually be ok
        return tensor.size() == indexProvider.getDimensionCount();
    }
}
