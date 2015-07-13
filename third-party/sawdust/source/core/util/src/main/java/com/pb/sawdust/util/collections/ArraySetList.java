package com.pb.sawdust.util.collections;

import java.util.*;

/**
 * The {@code ArraySetList} is a {@code AbstractSetList} which uses an {@code java.util.ArrayList} as its backing list.
 * As such, the list will be optimized for random, as opposed to sequential, access.
 *
 * @param <E>
 *        The type of the elements held by this collection.
 *
 * @author crf <br/>
 *         Started: Jun 13, 2008 6:47:48 PM
 *
 * @see com.pb.sawdust.util.collections.LinkedSetList
 */
public class ArraySetList<E> extends AbstractSetList<E> {
    /**
     * Constructor for a default setlist. The size of the array list and backing hashset will be of default size.
     */
    public ArraySetList() {
        super();
    }

    /**
     * Constructor specifying the initial size of the setlist. The initial size will be used to initialize both the
     * backing hashset and array list.
     *
     * @param initialSize
     *        The initial size for the setlist.
     */
    public ArraySetList(int initialSize) {
        super(initialSize);
    }

    /**
     * Constructor specifying the initial size and load factor of the setlist. The initial size will be used to
     * initialize both the backing hashset and array list, and the load factor will be used by the hashset.
     *
     * @param initialSize
     *        The initial size for this setlist.
     *
     * @param loadFactor
     *        The load factor for this setlist.
     *
     * @see java.util.HashSet#HashSet(int, float)
     */
    public ArraySetList(int initialSize, int loadFactor) {
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
    public ArraySetList(Collection<? extends E> c) {
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
    public ArraySetList(E ... elements) {
        super(Arrays.asList(elements));
    }

    protected List<E> getBackingList(int initialSize) {
        if (initialSize > -1)
            return new ArrayList<E>(initialSize);
        else
            return new ArrayList<E>();
    }
}
