package com.pb.sawdust.tensor.read.id;

import com.pb.sawdust.util.ClassInspector;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * The {@code StandardIdReader} class provides a basic id reader for reading in index ids. It allows a different id
 * transfer class for each index dimension.
 *
 * @author crf <br/>
 *         Started 1/24/11 3:53 PM
 */
public class StandardIdReader<S,I> implements IdReader<S,I> {
    private final List<IdTransfer<S,? extends I>> transfers;
    private final int size;

    /**
     * Constructor specifying the id transfer function for each index dimension. The length of the list of id transfers
     * determines the size (number of dimensions) of the tensor this reader applies to.
     *
     * @param transfers
     *        The list of id transfers for each dimension in the index.
     */
    public StandardIdReader(List<IdTransfer<S,? extends I>> transfers) {
        this.transfers = new LinkedList<IdTransfer<S,? extends I>>(transfers);
        size = this.transfers.size();
    }

    @Override
    public List<I> getIds(List<S> sourceIds) {
        if (sourceIds.size() != size)
            throw new IllegalArgumentException(String.format("Incorrect size for id transfer; found %d, expected %d",sourceIds.size(),size));
        List<I> ids = new LinkedList<I>();
        Iterator<S> it = sourceIds.iterator();
        for (IdTransfer<S,? extends I> transfer : transfers)
            ids.add(transfer.getId(it.next()));
        return ids;
    }

    @Override
    public void getIds(S[] sourceIds, int startPoint, I[] idSink) {
        if ((sourceIds.length - startPoint) < idSink.length)
            throw new IllegalArgumentException(String.format("Source (%d) array starting at %d and must have enough elements to transfer to sink (%d) id array",sourceIds.length,startPoint,idSink.length));
        for (IdTransfer<S,? extends I> transfer : transfers) {
            idSink[startPoint] = transfer.getId(sourceIds[startPoint]);
            startPoint++;
        }
    }

    @Override
    public I getId (S sourceId, int index) {
        return transfers.get(index).getId(sourceId);
    }

    public I[] getIdSink(S[] exampleSource) {
        Class[] o = new Class[exampleSource.length];
        int counter = 0;
        for (IdTransfer<S,? extends I> transfer : transfers)
            o[counter++] = transfer.getIdClass();
        @SuppressWarnings("unchecked") //can't verify that lowest common class will match I, but suppressing since generally this should be ok (unless sources part of unusual object hierarchy)
        I[] ids = (I[]) Array.newInstance(ClassInspector.getLowestCommonClass(o),exampleSource.length);
        return ids;
    }
}
