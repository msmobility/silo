package com.pb.sawdust.tensor.write;

import com.pb.sawdust.tensor.group.TensorGroup;

import java.util.Collection;

/**
 * @author crf <br/>
 *         Started: Dec 15, 2009 2:30:34 PM
 */
public interface TensorGroupWriter<T,I> {
    void writeTensorGroup(TensorGroup<? extends T,? extends I> group);
}
