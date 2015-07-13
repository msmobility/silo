package com.pb.sawdust.util.collections.iterators;


import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Arrays;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * The {@code ParallelRegistrationIterator} is a {@code ParallelIterator} in which iterators are tied to specific
 * registrants. Specifically, upon instantiation a collection of (object) registrants is specified. To get an
 * iterator, one of the supplied registrants is passed into the {@code getIterator(Object)} method, and the iterator
 * tied to that registrant is returned. If the iterator for a given registrant is already taken, then that iterator
 * is no longer available.
 *
 * @param <E>
 *        The type of elements returned by this class' iterators at each iteration.
 *
 * @author crf <br/>
 *         Started: Jul 10, 2008 10:29:52 PM
 */
@ThreadSafe //see caveats in parent class
public class ParallelRegistrationIterator<E> extends ParallelIterator<E> {
    private final Collection<Object> registrants;

    /**
     * Constructor specifying the registrants in a collection. The collection <i>must</i> support the {@code Collections.remove(Object)}
     * method, otherwise calls to {@code getIterator(Object)} will throw an exception.
     *
     * @param iterator
     *        The "master" iterator; all parallel iterators will mimic this iterator's cycle.
     *
     * @param registrants
     *        The registrants which will be used to access the iterators.
     */
    public ParallelRegistrationIterator(Iterator<E> iterator, Collection<?> registrants) {
        super(iterator,registrants.size());
        this.registrants = new LinkedList<Object>(registrants);
    }

    /**
     * Constructor specifying the registrants.
     *
     * @param iterator
     *        The "master" iterator; all parallel iterators will mimic this iterator's cycle.
     *
     * @param registrants
     *        The registrants which will be used to access the iterators.
     */
    public ParallelRegistrationIterator(Iterator<E> iterator, Object ... registrants) {
        this(iterator,Arrays.asList(registrants));
    }

    /**
     * Not supported. Use {@code getIterator(Object)} instead.
     *
     * @return nothing - not supported.
     *
     * @throws UnsupportedOperationException as this method does not specify a registrant.
     */
    public Iterator<E> getIterator() {
        throw new UnsupportedOperationException("Must use getIterator(Object) to get iterator from ParallelRegistrationIterator.");
    }

    /**
     * Get an iterator tied to a specified registrant. If the iterator tied to this registrant is already taken (<i>i.e.</i>,
     * if <code>isIteratorAvailable(registrant)==false</code>, an exception will be thrown. See {@link ParallelIterator#getIterator()}
     * for further properties of the returned iterator.
     *
     * @param registrant
     *        The registrant the iterator is tied to.
     *
     * @return a parallel iterator.
     *
     * @throws IllegalArgumentException if {@code registrant} is not a valid registrant, or if the iterator tied to it
     *                                  has already been taken with a call to this method.
     */
    @GuardedBy("registrants")
    public Iterator<E> getIterator(Object registrant) {
        synchronized(registrants) {
            if (!isIteratorAvailable(registrant))
                throw new IllegalArgumentException("Registrant not registered or its iterator already taken.");
            registrants.remove(registrant);
            return super.getIterator();
        }
    }

    /**
     * Determine whether a given registrant's iterator is available.
     *
     * @param registrant
     *        The registrant in question.
     *
     * @return {@code true} if {@code registrant}'s iterator has not been taken yet, {@code false} if it has.
     */
    public boolean isIteratorAvailable(Object registrant) {
        return registrants.contains(registrant);
    }
}
