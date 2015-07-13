package com.pb.sawdust.util.collections.iterators;

import com.pb.sawdust.calculator.Function1;

import java.util.Iterator;

/**
 * The {@code TransformingIterator} is an iterator which wraps another iterator in a simple transformation function.
 *
 * @param <F>
 *        The type of the "from" (source) iterator.
 *
 * @param <T>
 *        The type returned by this iterator.
 *
 * @author crf
 *         Started 9/8/11 1:58 PM
 */
public class TransformingIterator<F,T> implements Iterator<T> {
    private final Iterator<? extends F> fromIterator;
    private final Function1<F,T> transformation;

    /**
     * Constructor specifying the iterator to wrap and the transformation function.
     *
     * @param iterator
     *        The iterator to wrap.
     *
     * @param transformation
     *        The transformation function.
     */
    public TransformingIterator(Iterator<? extends F> iterator, Function1<F,T> transformation) {
        this.fromIterator = iterator;
        this.transformation = transformation;
    }


    @Override
    public boolean hasNext() {
        return fromIterator.hasNext();
    }

    @Override
    public T next() {
        return transformation.apply(fromIterator.next());
    }

    @Override
    public void remove() {
        fromIterator.remove();
    }
}
