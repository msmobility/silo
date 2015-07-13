package com.pb.sawdust.tensor.read.id;

import java.util.List;

/**
 * The {@code IdReader} interface specifies a framework for reading index ids. It is meant mainly as a transfer framework,
 * through which ids read from some source as a type {@code S} can be transferred to type {@code I}. This interface is
 * meant to be able to read ids spanning all dimensions of an index, as opposed to just reading a single id in isolation
 * (see {@link IdTransfer} for such functionality). This interface includes some array-based methods which provide performance
 * benefits at the cost of the use of generic arrays.
 *
 * @param <S> The source id type.
 *
 * @param <I> The actual id type.
 *
 * @author crf <br/>
 *         Started 1/24/11 3:25 PM
 */
public interface IdReader<S,I> {
    /**
     * Get the ids corresponding to the specified source ids.
     *
     * @param sourceIds
     *        The source ids.
     *
     * @return the ids corresponding to {@code sourceIds}.
     */
    List<I> getIds(List<S> sourceIds);

    /**
     * Get the ids corresponding to the source ids in an input array, and place them in a provided id sink array.
     *
     * @param sourceIds
     *        The array holding the source ids. The ids must be contigous, but do not need to fill the entire array.
     *
     * @param startPoint
     *        The starting (0-based) index in {@code sourceIds} for the source ids.
     *
     * @param idSink
     *        The array in which the corresponding ids will be placed.
     *
     * @throws IllegalArgumentException if there are not enough source ids to transfer to the sink array. That is, if
     *                                  <code>sourceIds.length-startPoint < idSink.length</code>.
     */
    void getIds(S[] sourceIds, int startPoint, I[] idSink);

    /**
     * Get the id corresponding to the specified source id at a given index.
     *
     * @param sourceId
     *        The source id.
     *
     * @param index
     *        The (0-based) id index.
     *
     * @return the id corresponding to {@code sourceId} at {@code index}.
     */
    I getId (S sourceId, int index);

    /**
     * Get an array which can be used as an id sink in {@link #getIds(java.util.List)}. The example source will hold
     * the correct types for each index position, but may not be valid source ids; that is, they may not be parsable,
     * so any attempts to build the id sink should avoid using {@link #getId(Object, int)} if possible.
     *
     * @param exampleSource
     *        An example source id array.
     *
     * @return an array which can be used as an id sink.
     */
    I[] getIdSink(S[] exampleSource);
}
