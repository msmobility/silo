package com.pb.sawdust.tensor.index;

import com.pb.sawdust.tensor.read.IndexReader;
import static com.pb.sawdust.util.Range.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The {@code IndexFactory} provides a framework for constructing {@code Index} instances from {@code IndexReader}s.
 * It specifies only one method, which can be overridden by extending methods.
 *
 * @author crf <br/>
 *         Started Mar 3, 2010 8:07:05 PM
 */
public class IndexFactory {
    /**
     * Construct an index from an index reader.  Depending on what is read in, a different index will be constructed:
     * <ul>
     *     <li>
     *         If {@code reader.getReferenceIndex() == null && reader.getIndexIds() == null} then a {@code StandardIndex}
     *         based on {@code reader.getBaseDimensions()} will be constructed.
     *     </li>
     *     <li>
     *         If {@code reader.getReferenceIndex() == null && reader.getIndexIds() != null} then a {@code BaseIndex}
     *         based on {@code reader.getIndexIds()} will be constructed.
     *     </li>
     *     <li>
     *         If {@code reader.getReferenceIndex().length > reader.getBaseDimensions().length} then an {@code ExpandingIndex}
     *         will be created, with ids if {@code reader.getIndexIds() != null}.  If the "tail" dimensions in the reference
     *         index are not all of size 1, then an exception will be thrown.
     *     </li>
     *     <li>
     *         If {@code reader.getReferenceIndex().length < reader.getBaseDimensions().length} then an exception will be
     *         thrown, as collapsing indices aren't supported with {@code IndexReader}s at the moment.
     *     </li>
     *     <li>
     *         If {@code reader.getReferenceIndex().length == reader.getBaseDimensions().length} then a {@code BaseIndex}
     *         will be constructed, with ids if {@code reader.getIndexIds() != null}..
     *     </li>
     * </ul>
     *
     * @param reader
     *        The index reader to get the index data from.
     *
     * @param <I>
     *        The type of the index's id.
     *
     * @return the index read from {@code reader}.
     *
     * @throws IllegalStateException if an expanding index is specified with "tail" dimension sizes greater than one,
     *                               or if a collapsing index is specified.
     */
    @SuppressWarnings("unchecked") //can't guarantee I, but nothing can be done with a parameterized method like this
    public <I> Index<I> index(IndexReader<I> reader) {
        Map<String,Object> metadata = reader.getIndexMetadata();
        int[] dimensions = reader.getBaseDimensions();
        List<List<I>> ids = reader.getIndexIds();
        int[][] referenceIndex = reader.getReferenceIndex();

        Index<I> index;
        if (referenceIndex == null) {
            //just pass through for dimensions
            index = ids == null ? (Index<I>) new StandardIndex(dimensions) : new BaseIndex<I>(ids);
        } else {
            if (referenceIndex.length > dimensions.length) {
                for (int i : range(dimensions.length,referenceIndex.length))
                    if (referenceIndex[i].length != 1)
                        throw new IllegalStateException("Reference indices longer than base dimensions but extra dimensions not of size 1:\n" +
                                                        "\t reference: " + Arrays.toString(dimensions) + "\n" +
                                                        "\t base dimensions: " + Arrays.deepToString(referenceIndex));
                if (ids == null) {
                    index = (Index<I>) ExpandingIndex.getStandardExpandingIndex(new BaseIndex<I>(Arrays.copyOf(referenceIndex,dimensions.length),ids),referenceIndex.length-dimensions.length);
                } else {
                    List<I> additionalIds = new LinkedList<I>();
                    while (ids.size() > dimensions.length)
                        additionalIds.add(ids.remove(dimensions.length).get(0));
                    index = ExpandingIndex.getExpandingIndex(new BaseIndex<I>(Arrays.copyOf(referenceIndex,dimensions.length),ids),(I[]) additionalIds.toArray());
                }
            } else if (referenceIndex.length < dimensions.length) {
                //todo: collapsing index
                throw new IllegalStateException("Collapsing indices not supported by index factory at this moment.");
            } else {
                index = ids == null ? new BaseIndex<I>(referenceIndex) : new BaseIndex<I>(referenceIndex,ids);
            }
        }

        for (String key : metadata.keySet())
            index.setMetadataValue(key,metadata.get(key));
        return index;
    }
}
