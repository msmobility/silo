package com.pb.sawdust.tensor.group;

import com.pb.sawdust.tensor.StandardTensorMetadataKey;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.index.IndexFactory;
import com.pb.sawdust.tensor.read.TensorGroupReader;

/**
 * The {@code NameTensorGroup} ...
 *
 * @author crf <br/>
 *         Started Mar 3, 2010 1:47:11 PM
 */
public class NameTensorGroup<T,I> extends MetadataTensorGroup<T,I> {
    public static final String METADATA_NAME_KEY = StandardTensorMetadataKey.NAME.getKey();

    public NameTensorGroup(int[] dimensions) {
        super(dimensions,METADATA_NAME_KEY);
    }

    public NameTensorGroup(TensorGroupReader<T,I> reader, TensorFactory tensorFactory, IndexFactory indexFactory) {
        super(reader,METADATA_NAME_KEY,tensorFactory,indexFactory);
    }

    public NameTensorGroup(TensorGroupReader<T,I> reader, TensorFactory tensorFactory) {
        super(reader,METADATA_NAME_KEY,tensorFactory);
    }
}
