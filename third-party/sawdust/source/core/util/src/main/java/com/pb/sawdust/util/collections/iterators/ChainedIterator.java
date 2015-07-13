package com.pb.sawdust.util.collections.iterators;

import com.pb.sawdust.calculator.Function1;
import com.pb.sawdust.util.array.ArrayUtil;

import java.util.*;

/**
 * The {@code ChainedIterator} ...
 *
 * @author crf
 *         Started 10/1/11 8:34 AM
 */
public class ChainedIterator<E> implements Iterator<E> {
    private final Iterator<Iterator<E>> iterator;
    private Iterator<E> current;

    @SafeVarargs
    @SuppressWarnings({"unchecked", "varargs"})
    public ChainedIterator(Iterator<E> ... iterators) {
        this(Arrays.asList(iterators));
    }

    @SafeVarargs
    @SuppressWarnings({"unchecked", "varargs"})
    public ChainedIterator(final Iterable<E> ... iterables) {
        this(new IterableIterator<>( new TransformingIterator<Iterable<E>,Iterator<E>>(ArrayUtil.getIterator(iterables),new Function1<Iterable<E>,Iterator<E>>() {
            @Override
            public Iterator<E> apply(Iterable<E> o) {
                return o.iterator();
            }
        })));
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public ChainedIterator(Iterable<Iterator<E>> iterators) {
        List<Iterator<E>> its = new LinkedList<>();
        for (Iterator<E> iterator : iterators)
            its.add(iterator);
        iterator = its.iterator();
        if (iterator.hasNext())
            current = iterator.next();
    }

    public static <E> ChainedIterator<E> chainedIterator(Iterable<? extends Iterable<E>> iterables) {
        return new ChainedIterator<>(new IterableIterator<>( new TransformingIterator<>(iterables.iterator(),new Function1<Iterable<E>,Iterator<E>>() {
            @Override
            public Iterator<E> apply(Iterable<E> o) {
                return o.iterator();
            }
        })));
    }

    @Override
    public boolean hasNext() {
        if (current == null)
            return false; //should be for no iterators, if a passed iterator == null, well then all bets are off
        while (!current.hasNext() && iterator.hasNext())
            current = iterator.next();
        return current.hasNext();
    }

    @Override
    public E next() {
        if (!hasNext())
            throw new NoSuchElementException();
        return current.next();
    }

    @Override
    public void remove() {
        if (current == null)
            throw new IllegalStateException("next() has not been called yet (and no iterators in chain)");
        current.remove();
    }
}
