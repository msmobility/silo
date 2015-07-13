package com.pb.sawdust.tensor.read.id;

import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.List;

/**
 * The {@code UniformIdReader} class provides an id reader which reads all ids using the same id transfer class.
 *
 * @author crf <br/>
 *         Started 1/24/11 3:46 PM
 */
public class UniformIdReader<S,I> implements IdReader<S,I> {
    private final IdTransfer<S,I> transfer;

    /**
     * Constructor specifying the id transfer class used to read the ids for the index.
     *
     * @param transfer
     *        The id transfer class used to read the index ids.
     */
    public UniformIdReader(IdTransfer<S,I> transfer) {
        this.transfer = transfer;
    }

    @Override
    public List<I> getIds(List<S> sourceIds) {
        List<I> ids = new LinkedList<I>();
        for (S sourceId : sourceIds)
            ids.add(transfer.getId(sourceId));
        return ids;
    }

    @Override
    public void getIds(S[] sourceIds, int startPoint, I[] idSink) {
         if ((sourceIds.length - startPoint) < idSink.length)
            throw new IllegalArgumentException(String.format("Source (%d) array starting at %d and must have enough elements to transfer to sink (%d) id array",sourceIds.length,startPoint,idSink.length));
        for (int i = 0; i < idSink.length; i++)
            idSink[i] = transfer.getId(sourceIds[i+startPoint]);
    }

    @Override
    public I getId (S sourceId, int index) {
        return transfer.getId(sourceId);
    }

    @SuppressWarnings("unchecked") //transfer.getIdClass must fulfill this correctly, by its contract
    public I[] getIdSink(S[] exampleSource) {
        return (I[]) Array.newInstance(transfer.getIdClass(),exampleSource.length);
    }
}
