package com.pb.sawdust.util.collections;

import java.util.*;

/**
 * The {@code AbstractSetList} class provides a basic implementation of the {@code SetList} interface to lower the
 * burden for programmers. Internally, it maintains a {@code HashSet} to ensure the set properties, and an
 * implementation-specific {@code List} to ensure the list properties.
 *
 * @param <E>
 *        The type of the elements held by this collection.
 *
 * @author crf <br/>
 *         Started: Jun 13, 2008 4:21:25 PM
 */
public abstract class AbstractSetList<E> extends AbstractSet<E> implements SetList<E> {
    private final Set<E> elements;
    private final List<E> orderedElements;

    /**
     * Get the list that will be used to hold the elements in this setlist and ensure list properties. This list should
     * be empty and a reference to it should be unavailable to it outside of this method's scope. As this class
     * generally calls this backing list for all of the methods specified in the {@code List} interface, the
     * specific advantages and disadvantages of the list returned by this method are inherited by this instance.
     * <p>
     * This method is used in the constructor, and should not be used elsewhere. <b>This method should return
     * a new empty instance of the backing list type, not a reference to the list used in this instance.</b> That is,
     * if this method is called repeatedly, a new object should be return, not a reference to one which has already been
     * created. This requirement is to ensure data encapsulation in the {@code SetList} implementation.
     *
     * @param initialSize
     *        The size to initialize the list with. If a value less than 0 is entered, then a list with the default
     *        size should be returned.
     *
     * @return an empty list.
     */
    abstract protected List<E> getBackingList(int initialSize);

    /**
     * Constructor for a default setlist. The size of the backing list and hashset will be of default size.
     */
    public AbstractSetList() {
        elements = new HashSet<E>();
        orderedElements = getBackingList(-1);
    }

    /**
     * Constructor specifying the initial size of the setlist. The initial size will be used to initialize both the
     * backing hashset and list.
     *
     * @param initialSize
     *        The initial size for the setlist.
     */
    public AbstractSetList(int initialSize) {
        elements = new HashSet<E>(initialSize);
        orderedElements = getBackingList(initialSize);
    }

    /**
     * Constructor specifying the initial size and load factor of the setlist. The initial size will be used to
     * initialize both the backing hashset and list, and the load factor will be used by the hashset.
     *
     * @param initialSize
     *        The initial size for this setlist.
     *
     * @param loadFactor
     *        The load factor for this setlist.
     *
     * @see java.util.HashSet#HashSet(int, float)
     */
    public AbstractSetList(int initialSize, int loadFactor) {
        elements = new HashSet<E>(initialSize,loadFactor);
        orderedElements = getBackingList(initialSize);
    }

    /**
     * Constructor which will create a new setlist containing all of the elements in a specified collection. The
     * setlist will be initialized to the size of the collection. The ordering of the elements in the setlist will
     * be the same as the iteration order of the elements in the collection, whether or not the iteration order is
     * well-defined.
     *
     * @param c
     *        The collection holding the elements which the setlist will be initialized with.
     */
    public AbstractSetList(Collection<? extends E> c) {
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
        return orderedElements.size();
    }

    public Iterator<E> iterator() {
        return orderedElements.iterator();
    }

    public Object[] toArray() {
        return orderedElements.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return orderedElements.toArray(a);
    }

    private boolean add(E element, boolean force) {
        if (contains(element))
            return force && shift(element, size());
        else
            return elements.add(element) && orderedElements.add(element);
    }

    public boolean forceAdd(E element) {
        return add(element,true);
    }

    public boolean add(E element) {
        return add(element,false);
    }

    public boolean remove(Object o) {
        return elements.remove(o) && orderedElements.remove(o);
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

    public boolean addAll(int index, Collection<? extends E> c) {
        for (E element : c)
            add(index++,element);
        return c.size() > 0;
    }

    public boolean forceAddAll(int index, Collection<? extends E> c) {
        for (E element : c) {
            boolean contains = contains(c);
            forceAdd(index,element);
            if (!contains)
                index++;
        }
        return true;
    }

    private void add(int index, E element, boolean force) {
        //if element already in set, then shift it, otherwise do as asked
        if (!contains(element)) {
            elements.add(element);
            orderedElements.add(index,element);
        } else if (force) {
            shift(element,index);
        }
    }

    public void add(int index, E element) {
        add(index,element,false);
    }

    public void forceAdd(int index, E element) {
        add(index,element,true);
    }

    private E set(int index, E element, boolean force) {
        if (index < 0 || index >= size())
            throw new IndexOutOfBoundsException("Setlist index out of bounds: " + index);
        if (!force && contains(element))
            return null;
        //if element already in set, old placement removed and added at index (will drop size by one, unless replacing self)
        //otherwise, do as expected
        E oldElement = remove(index);
        forceAdd(index,element);
        return oldElement;
    }

    public E set(int index, E element) {
        return set(index,element,false);
    }

    public E forceSet(int index, E element) {
        return set(index,element,true);
    }

    public boolean shift(E element, int index) {
        if (!elements.contains(element))
            throw new IllegalArgumentException("Element not found");
        if (orderedElements.indexOf(element) == index)
            return false;
        orderedElements.remove(element);
        orderedElements.add(index,element);
        return true;
    }

    public boolean removeAll(Collection<?> c) {
        return elements.removeAll(c) && orderedElements.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return elements.retainAll(c) && orderedElements.retainAll(c);
    }

    public void clear() {
        elements.clear();
        orderedElements.clear();
    }

    public E get(int index) {
        return orderedElements.get(index);
    }

    public E remove(int index) {
        E removedElement = orderedElements.remove(index);
        elements.remove(removedElement);
        return removedElement;
    }

    public int indexOf(Object o) {
        return orderedElements.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return indexOf(o);
    }

    public ListIterator<E> listIterator() {
        return orderedElements.listIterator();
    }

    public ListIterator<E> listIterator(int index) {
        return orderedElements.listIterator(index);
    }

    public List<E> subList(int fromIndex, int toIndex) {
        return orderedElements.subList(fromIndex,toIndex);
    }

    public int hashCode() {
        return orderedElements.hashCode(); //order and elements must be equal to be equal in SetList
    }

    public boolean equals(Object o) {
        return orderedElements.equals(o);
    }

    public String toString() {
        return orderedElements.toString();
    }
}
