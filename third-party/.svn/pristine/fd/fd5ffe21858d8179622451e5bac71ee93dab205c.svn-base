package com.pb.sawdust.util.collections.iterators;

import com.pb.sawdust.util.Filter;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.jcip.annotations.NotThreadSafe;

/**
 * The {@code FilteredIterator} class is used to iterate over a collection where certain values are skipped. The
 * values to skip are determined by a {@code Filter} instance, where values rejected by the filter
 * (<tt>Filter.filter(value) = false</tt>) are skipped.
 * <p>
 * This class is not thread safe, even if the underlying iterator is. Specifically, invalid behavior from the
 * {@code next()} and {@code hasNext()} methods may be observed if more than one thread are accessing this iterator
 * concurrently.
 *
 * @param <E>
 *        The type of elements returned by this iterator at each iteration.
 *
 * @author crf <br/>
 *         Started: Jul 9, 2008 5:35:56 PM
 */
@NotThreadSafe
public class FilteredIterator<E> implements Iterator<E> {
    private final Iterator<? extends E> iterator;
    private final Filter<? super E> filter;
    private boolean incremented = false;
    private E next;

    /**
     * Constructor specifying the underlying iterator and filter. The underlying iterator will determine the candidate
     * elements (and their order) for this iterator, while the filter will determine if they actually become part of this
     * iterator's cycle.
     *
     * @param iterator
     *        The underlying iterator for this filtered iterator.
     *
     * @param filter
     *        The filter to use to filter the elements in {@code iterator}.
     */
    public FilteredIterator(Iterator<? extends E> iterator, Filter<? super E> filter) {
        this.iterator = iterator;
        this.filter = filter;
    }

    public boolean hasNext() {
        if (!incremented) {
            while(iterator.hasNext()) {
                next = iterator.next();
                if (filter.filter(next)) {
                    incremented = true;
                    break;
                }
            }
        }
        return incremented;
    }

    public E next() {
        if (hasNext())
            incremented = false;
        else
            throw new NoSuchElementException();
        return next;
    }

    public void remove() {
        iterator.remove();
    }
}
