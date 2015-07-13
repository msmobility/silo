package com.pb.sawdust.util.collections.iterators;

import java.util.Iterator;

/**
 * The {@code IterableIterator} class provides a simple wrapper to make an iterator iterable. It is inteded to be used
 * one time: once it is used once, the (wrapped) iterator is spoiled.  Attempts to use it multiple times will lead to
 * an exception being thrown.
 *
 * @author crf <br/>
 *         Started: Dec 15, 2009 10:45:12 AM
 */
public class IterableIterator<T> implements Iterable<T> {
    private final Iterator<T> iterator;
    private boolean spoiled = false;

    /**
     * Constructor specifying the iterator to wrap.
     *
     * @param iterator
     *        The iterator to wrap.
     */
    public IterableIterator(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if this method is called more than one time.
     */
    @Override
    public Iterator<T> iterator() {
        if (spoiled)
            throw new IllegalStateException("Cannot request iterator twice from an IterableIterator.");
        spoiled = true;
        return iterator;
    }
}
