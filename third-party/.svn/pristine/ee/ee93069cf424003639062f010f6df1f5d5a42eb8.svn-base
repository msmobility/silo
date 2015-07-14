package com.pb.sawdust.tensor.read.id;

/**
 * The {@code IdTransfer} interface provides a framework for reading individual index ids. It is meant mainly as a transfer
 * framework, through which ids read from some source as a type {@code S} can be transferred to type {@code I}.
 *
 * @param <S> The source id type.
 *
 * @param <I> The id type this interface transfers to.
 *
 * @author crf <br/>
 *         Started 1/24/11 3:39 PM
 */
public interface IdTransfer<S,I> {
    /**
     * Get the id corresponding to a specified source id.
     *
     * @param sourceId
     *        The source id.
     *
     * @return the id corresponding to {@code sourceId}.
     */
    I getId(S sourceId);

    /**
     * Get the class of the id this class transfers its sources to. This is meant for use with {@link IdReader#getIdSink(Object[])}
     * to provide a valid id sink array. If necessary, {@code Object.class} may be returned, but this may cause issues if
     * the ids are actually a descendent type and are used with (index) methods requiring that descendent type.
     *
     * @return the class of the id this interface transfers to.
     */
    Class<I> getIdClass();
}
