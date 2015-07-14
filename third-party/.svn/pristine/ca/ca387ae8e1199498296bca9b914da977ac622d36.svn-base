package com.pb.sawdust.util.collections;

import java.util.*;

/**
 * The {@code AbstractSetQueue} class provides a basic implementation of the {@code SetQueue} interface to lower the
 * burden for programmers. Internally, it maintains a {@code HashSet} to ensure the set properties, and an
 * implementation-specific {@code Queue} to ensure the queue properties.
 *
 * @param <E>
 *        The type of the elements held by this collection.
 *
 * @author crf <br/>
 *         Started: Dec 30, 2008 11:22:12 AM
 */
public abstract class AbstractSetQueue<E> extends AbstractSet<E> implements SetQueue<E> {
    private final Set<E> elements;
    private final Queue<E> elementQueue;

    /**
     * Get the list that queue be used to hold the elements in this SetQueue and ensure queue properties. This queue should
     * be empty and a reference to it should be unavailable to it outside of this method's scope. As this class
     * generally calls this backing list for all of the methods specified in the {@code Queue} interface, the
     * specific advantages and disadvantages of the queue returned by this method are inherited by this instance.
     * <p>
     * This method is used in the constructor, and should not be used elsewhere. <b>This method should return
     * a new empty instance of the backing queue type, not a reference to the queue used in this instance.</b> That is,
     * if this method is called repeatedly, a new object should be return, not a reference to one which has already been
     * created. This requirement is to ensure data encapsulation in the {@code SetQueue} implementation.
     *
     * @param initialSize
     *        The size to initialize the queue with. If a value less than 0 is entered, then a queue with the default
     *        size should be returned.
     *
     * @return an empty queue.
     */
    abstract protected Queue<E> getBackingQueue(int initialSize);

    /**
     * Constructor for a default setqueue. The size of the backing queue and hashset will be of default size.
     */
    public AbstractSetQueue() {
        elements = new HashSet<E>();
        elementQueue = getBackingQueue(-1);
    }

    /**
     * Constructor specifying the initial size of the setqueue. The initial size will be used to initialize both the
     * backing hashset and queue.
     *
     * @param initialSize
     *        The initial size for the setqueue.
     */
    public AbstractSetQueue(int initialSize) {
        elements = new HashSet<E>(initialSize);
        elementQueue = getBackingQueue(initialSize);
    }

    /**
     * Constructor specifying the initial size and load factor of the setqueue. The initial size will be used to
     * initialize both the backing hashset and queue, and the load factor will be used by the hashset.
     *
     * @param initialSize
     *        The initial size for this setqueue.
     *
     * @param loadFactor
     *        The load factor for this setqueue.
     *
     * @see java.util.HashSet#HashSet(int, float)
     */
    public AbstractSetQueue(int initialSize, int loadFactor) {
        elements = new HashSet<E>(initialSize,loadFactor);
        elementQueue = getBackingQueue(initialSize);
    }

    /**
     * Constructor which will create a new setqueue containing all of the elements in a specified collection. The
     * setqueue will be initialized to the size of the collection. The ordering of the elements in the setqueue will
     * be the same as the iteration order of the elements in the collection, whether or not the iteration order is
     * well-defined.
     *
     * @param c
     *        The collection holding the elements which the setqueue will be initialized with.
     */
    public AbstractSetQueue(Collection<? extends E> c) {
        this(c.size());
        addAll(c);
    }

    /**
     * Add an element to the backing set in this setlist. This method should only be called if the element is also
     * added to the backing list.
     *
     * @param e
     *        The element to add to this setlist's backing set.
     *
     * @return {@code true} if the backing set changed as a result of this operation (<i>i.e.</i> if {@code e} did not
     *         exist in the backing set).
     */
    protected boolean addToSet(E e) {
        return elements.add(e);
    }

    /**
     * Remove an element from the backing set in this setlist. This method should only be called if the element is also
     * removed from the backing list.
     *
     * @param e
     *        The element to remove from this setlist's backing set.
     *
     * @return {@code true} if the backing set changed as a result of this operation (<i>i.e.</i> if {@code e} existed
     *         in the backing set).
     */
    protected boolean removeFromSet(E e) {
        return elements.remove(e);
    }

    public boolean contains(Object o) {
        return elements.contains(o);
    }

    public int size() {
        return elementQueue.size();
    }

    public Iterator<E> iterator() {
        return elementQueue.iterator();
    }

    public Object[] toArray() {
        return elementQueue.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return elementQueue.toArray(a);
    }

    //todo: should return false if force and element at end of queue
    private boolean add(E element, boolean force) {
        if (contains(element))
            return force && elementQueue.remove(element) && elementQueue.add(element);
        else
            return elements.add(element) && elementQueue.add(element);
    }

    public boolean add(E element) {
        return add(element,false);
    }

    public boolean forceAdd(E element) {
        return add(element,true);
    }

    public boolean addAll(Collection<? extends E> c) {
        for (E element : c)
            add(element);
        return c.size() > 0;
    }

    public boolean forceAddAll(Collection<? extends E> c) {
        for (E element : c)
            forceAdd(element);
        return true;
    }

    //todo: should return false if force and element at end of queue
    private boolean offer(E element, boolean force) {
        if (contains(element))
            return force && elementQueue.remove(element) && elementQueue.add(element);
        else
            return elementQueue.offer(element) && elements.add(element);
    }

    public boolean offer(E element) {
        return offer(element,false);
    }

    public boolean forceOffer(E element) {
        return offer(element,true);
    }

    public E remove() {
        E element = elementQueue.remove();
        elements.remove(element);
        return element;
    }

    public E poll() {
        E element = elementQueue.poll();
        if (element != null)
            elements.remove(element);
        return element;
    }

    public E element() {
        return elementQueue.element();
    }

    public E peek() {
        return elementQueue.peek();
    }

    public boolean remove(Object o) {
        return elements.remove(o) && elementQueue.remove(o);
    }

    public boolean removeAll(Collection<?> c) {
        return elements.removeAll(c) && elementQueue.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return elements.retainAll(c) && elementQueue.retainAll(c);
    }

    public void clear() {
        elements.clear();
        elementQueue.clear();
    }
}
