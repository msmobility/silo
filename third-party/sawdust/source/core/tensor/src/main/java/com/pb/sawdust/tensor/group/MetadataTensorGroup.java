package com.pb.sawdust.tensor.group;

import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.IndexFactory;
import com.pb.sawdust.tensor.read.TensorGroupReader;

import java.util.Map;

/**
 * The {@code MetadataTensorGroup} ...
 *
 * @author crf <br/>
 *         Started Mar 3, 2010 1:37:50 PM
 */
public class MetadataTensorGroup<T,I> extends AutoKeyTensorGroup<T,I> {
    private final String tensorMetadataKey;
    private final String indexMetadataKey;

    public MetadataTensorGroup(int[] dimensions, String tensorMetadataKey, String indexMetadataKey) {
        super(dimensions);
        this.tensorMetadataKey = tensorMetadataKey;
        this.indexMetadataKey = indexMetadataKey;
    }

    public MetadataTensorGroup(int[] dimensions, String metadataKey) {
        this(dimensions,metadataKey,metadataKey);
    }

    public MetadataTensorGroup(TensorGroupReader<T,I> reader, String metadataKey, String indexMetadataKey, TensorFactory tFactory, IndexFactory iFactory) {
        this(reader.getDimensions(),metadataKey,indexMetadataKey);
        loadTensorGroupFromReader(this,reader,tFactory,iFactory == null ? new IndexFactory() : iFactory);
    }

    public MetadataTensorGroup(TensorGroupReader<T,I> reader, String metadataKey, String indexMetadataKey, TensorFactory tFactory) {
        this(reader,metadataKey,indexMetadataKey,tFactory,null);
    }

    public MetadataTensorGroup(TensorGroupReader<T,I> reader, String metadataKey, TensorFactory tFactory, IndexFactory iFactory) {
        this(reader,metadataKey,metadataKey,tFactory,iFactory);
    }

    public MetadataTensorGroup(TensorGroupReader<T,I> reader, String metadataKey, TensorFactory tFactory) {
        this(reader,metadataKey,metadataKey,tFactory);
    }

    @Override
    public String formTensorKey(Tensor<?> tensor) {
        return (String) tensor.getMetadataValue(tensorMetadataKey);
    }

    @Override
    public String formIndexKey(Index<?> index) {
        return (String) index.getMetadataValue(indexMetadataKey);
    }
}
