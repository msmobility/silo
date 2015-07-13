package com.pb.sawdust.util.collections;

import java.util.*;

/**
 * The {@code LinkedSetList} is a {@code AbstractSetList} which uses an {@code java.util.LinkedList} as its backing list.
 * As such, the list will be optimized for sequential, as opposed to random, access. As it is backed by a linked list,
 * it also implements the {@code SetDeque} interface.
 *
 * @param <E>
 *        The type of the elements held by this collection.
 *
 * @author crf <br/>
 *         Started: Jun 13, 2008 6:46:53 PM
 *
 * @see com.pb.sawdust.util.collections.ArraySetList
 */
public class LinkedSetList<E> extends AbstractSetList<E> implements SetDeque<E> {
    private Deque<E> elementDeque;

    /**
     * Constructor for a default setlist. The size of the linked list and backing hashset will be of default size.
     */
    public LinkedSetList() {
        super();
    }

    /**
     * Constructor specifying the initial size of the setlist. The initial size will be used to initialize both the
     * backing hashset and linked list.
     *
     * @param initialSize
     *        The initial size for the setlist.
     */
    public LinkedSetList(int initialSize) {
        super(initialSize);
    }

    /**
     * Constructor specifying the initial size and load factor of the setlist. The initial size will be used to
     * initialize both the backing hashset and linked list, and the load factor will be used by the hashset.
     *
     * @param initialSize
     *        The initial size for this setlist.
     *
     * @param loadFactor
     *        The load factor for this setlist.
     *
     * @see java.util.HashSet#HashSet(int, float)
     */
    public LinkedSetList(int initialSize, int loadFactor) {
        super(initialSize,loadFactor);
    }

    /**
     * Constructor which will create a new setlist containing all of the elements in a specified collection. The
     * setlist will be initialized to the size of the collection. The ordering of the elements in the setlist will
     * be the same as the iteration order of the elements in the collection, whether or not the iteration order is
     * well-defined. If a given object exists more than once in the collection, an exception will be thrown, as this
     * violates the set property of the setlist.
     *
     * @param c
     *        The collection holding the elements which the setlist will be initialized with.
     */
    public LinkedSetList(Collection<? extends E> c) {
        super(c);
    }

    /**
     * Constructor which will create a new setlist containing all of the elements in a specified array. The
     * setlist will be initialized to the size of the array. The ordering of the elements in the setlist will
     * be the same as the array. If a given object exists more than once in the collection, an exception will be thrown,
     * as this violates the set property of the setlist.
     *
     * @param elements
     *        The elements which the setlist will be initialized with.
     */
    @SafeVarargs
    @SuppressWarnings({"unchecked", "varargs"})
    public LinkedSetList(E ... elements) {
        super(Arrays.asList(elements));
    }

    @SuppressWarnings("unchecked") //ok, because a LinkedList is also a List
    protected List<E> getBackingList(int initialSize) {
        if (elementDeque == null)
            return (List<E>) (elementDeque = new LinkedList<E>());
        return new LinkedList<E>(); //outsiders should not be able to access private variables
    }

    public boolean add(E element) {
        return addLast(element,false);
    }

    public boolean forceAdd(E element) {
        return addLast(element,true);
    }

    private void addFirst(E element, boolean force) {
        if (!contains(element)) {
            elementDeque.addFirst(element);
            addToSet(element);
        } else if (force){
            elementDeque.remove(element);
            elementDeque.addFirst(element);
        }
    }

    public void addFirst(E element) {
        addFirst(element,false);
    }

    public void forceAddFirst(E element) {
        addFirst(element,true);
    }

    private boolean addLast(E element, boolean force) {
        if (!contains(element)) {
            elementDeque.addLast(element);
            return addToSet(element);
        } else if (force){
            elementDeque.remove(element);
            elementDeque.addLast(element);
            return true;
        }
        return false;
    }

    public void addLast(E element) {
        addLast(element,false);
    }

    public void forceAddLast(E element) {
        addLast(element,true);
    }

    public void push(E element) {
        addFirst(element);
    }

    public void forcePush(E element) {
        forceAddFirst(element);
    }

    public boolean offer(E element) {
        return offerLast(element);
    }

    public boolean forceOffer(E element) {
        return forceOfferLast(element);
    }

    private boolean offerFirst(E element, boolean force) {
        if (!contains(element)) {
            return elementDeque.offerFirst(element) && addToSet(element);
        } else if (force){
            elementDeque.remove(element);
            return elementDeque.offerFirst(element);
        }
        return false;
    }

    public boolean offerFirst(E element) {
        return offerFirst(element,false);
    }

    public boolean forceOfferFirst(E element) {
        return offerFirst(element,true);
    }

    private boolean offerLast(E element, boolean force) {
        if (!contains(element)) {
            return elementDeque.offerLast(element) && addToSet(element);
        } else if (force){
            elementDeque.remove(element);
            return elementDeque.offerLast(element);
        }
        return false;
    }

    public boolean offerLast(E element) {
        return offerLast(element,false);
    }

    public boolean forceOfferLast(E element) {
        return offerLast(element,true);
    }

    public E removeFirst() {
        E element = elementDeque.removeFirst();
        removeFromSet(element);
        return element;
    }

    public E removeLast() {
        E element = elementDeque.removeLast();
        removeFromSet(element);
        return element;
    }

    public E pollFirst() {
        E element = elementDeque.pollFirst();
        if (element != null)
            removeFromSet(element);
        return element;
    }

    public E pollLast() {
        E element = elementDeque.pollLast();
        if (element != null)
            removeFromSet(element);
        return element;
    }

    public E getFirst() {
        return elementDeque.getFirst();
    }

    public E getLast() {
        return elementDeque.getLast();
    }

    public E peekFirst() {
        return elementDeque.peekFirst();
    }

    public E peekLast() {
        return elementDeque.peekFirst();
    }

    @SuppressWarnings("unchecked") //if o can be removed from deque, then it must be an E
    public boolean removeFirstOccurrence(Object o) {
        return elementDeque.removeFirstOccurrence(o) && removeFromSet((E) o);
    }

    @SuppressWarnings("unchecked") //if o can be removed from deque, then it must be an E
    public boolean removeLastOccurrence(Object o) {
        return elementDeque.removeLastOccurrence(o) && removeFromSet((E) o);
    }

    public E pop() {
        return removeFirst();
    }

    public Iterator<E> descendingIterator() {
        return elementDeque.descendingIterator();
    }

    public E remove() {
        E element = elementDeque.remove();
        removeFromSet(element);
        return element;
    }

    public E poll() {
        E element = elementDeque.poll();
        if (element != null)
            removeFromSet(element);
        return element;
    }

    public E element() {
        return elementDeque.element();
    }

    public E peek() {
        return elementDeque.peek();
    }
}
