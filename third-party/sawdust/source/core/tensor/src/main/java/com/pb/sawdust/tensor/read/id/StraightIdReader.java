package com.pb.sawdust.tensor.read.id;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * The {@code StraightIdReader} class provides an id reader which just returns the ids as read.
 *
 * @author crf <br/>
 *         Started 1/24/11 3:52 PM
 */
public class StraightIdReader<S> implements IdReader<S,S> {
    @Override
    public List<S> getIds(List<S> sourceIds) {
        return sourceIds;
    }

    @Override
    public void getIds(S[] sourceIds, int startPoint, S[] idSink) {
         if ((sourceIds.length - startPoint) < idSink.length)
            throw new IllegalArgumentException(String.format("Source (%d) array starting at %d and must have enough elements to transfer to sink (%d) id array",sourceIds.length,startPoint,idSink.length));
        System.arraycopy(sourceIds,startPoint,idSink,0,sourceIds.length);
    }

    @Override
    public S getId (S sourceId, int index) {
        return sourceId;
    }

    @SuppressWarnings("unchecked") //this is correct casting
    public S[] getIdSink(S[] exampleSource) {
        return (S[]) Array.newInstance(exampleSource.getClass().getComponentType(),exampleSource.length);
    }
}
