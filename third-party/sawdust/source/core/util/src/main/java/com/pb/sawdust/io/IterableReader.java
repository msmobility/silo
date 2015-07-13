package com.pb.sawdust.io;

/**
 * The {@code IterableReader} interface is used to specify a reader which can iterate over a series of data.
 *
 * @param <D>
 *        The type of data the reader iterates over.
 */
public interface IterableReader<D> extends Iterable<D>, RuntimeCloseable {

    /**
     * Set whether this reader's iterator should close the reader automatically at the end of the iteration cylce.
     *
     * @param closeAtIterationEnd
     *        {@code true} if the reader should be closed at the end of the iteration, {@code false} if it is to be left
     *         open. If {@code false}, then {@code close()} generally should be called explicitly when iteration is finshed.
     */
    void setCloseAtIterationEnd(boolean closeAtIterationEnd);
}
