package com.pb.sawdust.util.collections.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The {@code EmptyIterator} is an iterator with no elements.
 *
 * @author crf
 *         Started 6/6/12 4:35 PM
 */
public class EmptyIterator<T> implements Iterator<T> {
    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public T next() {
        throw new NoSuchElementException();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
