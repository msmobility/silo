package com.pb.sawdust.tensor.group;

import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.IndexFactory;
import com.pb.sawdust.tensor.index.IndexUtil;
import com.pb.sawdust.tensor.read.TensorGroupReader;
import com.pb.sawdust.util.ContainsMetadataImpl;
import com.pb.sawdust.util.array.ArrayUtil;

import java.util.*;

/**
 * The {@code AbstractTensorGroup} ...
 *
 * @author crf <br/>
 *         Started Mar 3, 2010 12:15:52 PM
 */
public class StandardTensorGroup<T,I> extends ContainsMetadataImpl<String> implements TensorGroup<T,I> {
    private final int[] dimensions;
    private final Map<String,Tensor<T>> tensorMap;
    private final Map<String,Index<I>> indexMap;

    public StandardTensorGroup(int[] dimensions) {
        this.dimensions = ArrayUtil.copyArray(dimensions);
        tensorMap = getTensorMap();
        indexMap = getIndexMap();
    }

    public StandardTensorGroup(TensorGroupReader<T,I> reader, TensorFactory tFactory, IndexFactory iFactory) {
        this(reader.getDimensions());
        loadTensorGroupFromReader(this,reader,tFactory,iFactory);
    }

    protected static <T,I> void loadTensorGroupFromReader(StandardTensorGroup<T,I> tensorGroup, TensorGroupReader<T,I> reader, TensorFactory tFactory, IndexFactory iFactory) {
        Map<String,Tensor<T>> tensorMap = reader.getTensorMap(tFactory);
        reader.fillTensorGroup(tensorMap);
        tensorGroup.tensorMap.putAll(tensorMap);
        tensorGroup.indexMap.putAll(reader.getIndexMap(iFactory));
        Map<String,Object> metadata = reader.getTensorGroupMetadata();
        for (String mkey : metadata.keySet())
            tensorGroup.setMetadataValue(mkey,metadata.get(mkey));

    }

    protected Map<String,Tensor<T>> getTensorMap() {
        return new LinkedHashMap<String,Tensor<T>>();
    }

    protected Map<String,Index<I>> getIndexMap() {
        return new LinkedHashMap<String,Index<I>>();
    }

    @Override
    public int[] getDimensions() {
        return ArrayUtil.copyArray(dimensions);
    }

    @Override
    public void addTensor(String tensorKey, Tensor<T> tensor) {
        if (!Arrays.equals(tensor.getDimensions(),dimensions))
            throw new IllegalArgumentException("Tensor dimensions (" + Arrays.toString(tensor.getDimensions()) +
                                                ") must equal tensor group dimensions (" + Arrays.toString(dimensions) + ")");
        tensorMap.put(tensorKey,tensor);
    }

    @Override
    public void addIndex(String indexKey, Index<I> index) {
        if (!IndexUtil.indexValidFor(index,dimensions))
            throw new IllegalArgumentException("Index dimensions (" + Arrays.toString(index.getDimensions()) +
                                                ") must equal tensor group dimensions (" + Arrays.toString(dimensions) + ")");
        indexMap.put(indexKey,index);
    }

    @Override
    public Tensor<T> getTensor(String tensorKey) {
        if (!tensorMap.containsKey(tensorKey))
            throw new IllegalArgumentException("Tensor key not found: " + tensorKey);
        return tensorMap.get(tensorKey);
    }

    @Override
    public Index<I> getIndex(String indexKey) {
        if (!indexMap.containsKey(indexKey))
            throw new IllegalArgumentException("Index key not found: " + indexKey);
        return indexMap.get(indexKey);
    }

    @Override
    public Set<String> tensorKeySet() {
        return tensorMap.keySet();
    }

    @Override
    public Set<String> indexKeySet() {
        return indexMap.keySet();
    }

    @Override
    public Iterator<Tensor<T>> tensorIterator() {
        return new Iterator<Tensor<T>>() {
            final Iterator<String> tensorKeyIterator = tensorKeySet().iterator();
            @Override
            public boolean hasNext() {
                return tensorKeyIterator.hasNext();
            }

            @Override
            public Tensor<T> next() {
                if (hasNext())
                    return getTensor(tensorKeyIterator.next());
                throw new NoSuchElementException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public Iterator<Index<I>> indexIterator() {
        return new Iterator<Index<I>>() {
            final Iterator<String> indexKeyIterator = indexKeySet().iterator();
            @Override
            public boolean hasNext() {
                return indexKeyIterator.hasNext();
            }

            @Override
            public Index<I> next() {
                if (hasNext())
                    return getIndex(indexKeyIterator.next());
                throw new NoSuchElementException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
