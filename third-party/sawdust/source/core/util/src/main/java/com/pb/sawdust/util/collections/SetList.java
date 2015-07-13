package com.pb.sawdust.util.collections;

import java.util.*;

/**
 * A {@code SetList} is a list which can contain no repeated values. Though it implements both the {@code List} and
 * {@code Set} interface, it is intended to be used as a list which is constrained to be a set, rather than a set which
 * is a list. For the latter, one may look to a {@code java.util.LinkedHashSet}.
 * <p>
 * Because element ordering is defined, and because elements cannot be repeated, some required methods create possibly
 * problematic situations. For example, if an object, {@code A}, exists at the beginning of a given {@code SetList}, and
 * then {@code A} is added to the setlist again using {@code add(E)}, then one of two possible actions seems viable:
 * <ol>
 *     <li>Add {@code A} to the end of the liat, and remove the reference at the beginning (because only one reference may
 *         exist in the setlist).</li>
 *     <li>Don't add {@code A} to the list, and retain the reference at the beginning of the list.</li>
 * </ol>
 * When faced with such a dilemma, a setlist will defer to not changing its current state; <i>i.e.</i> the second option.
 * If the first option is preferred (or something analogous to it, in the case of other list methods), new {@code force*}
 * methods have been specified, which force the method to complete its intended operation.
 *
 * @param <E>
 *        The type of the elements held by this collection.
 *
 * @author crf <br/>
 *         Started: Jun 13, 2008 4:07:17 PM
 */
public interface SetList<E> extends List<E>,Set<E> {

    /**
     * Shift an element from its current index to the specified index.
     *
     * @param element
     *        The element to move.
     *
     * @param index
     *        The index to move the element to.
     *
     * @return {@code true} if this setlist changed as a result of this operation, {@code false} otherwise. If {@code element}
     *         is already at {@code index}, then {@code false} will be returned.
     *
     * @throws IllegalArgumentException if the element does not exist in this setlist.
     */
    boolean shift(E element, int index);

    /**
     * {@inheritDoc}
     *
     * If the element already exists in the setlist, then {@code false} will be returned. To force the element to be added
     * (<i>i.e.</i> shift the element from its current index to the end of the setlist), use {@code forceAdd(E)}).
     */
    boolean add(E element);

    /**
     * Appends the element to the end of this setlist. If the element already exists in the list, then it will be removed
     * from its current position and placed at the end of the list (unless it already was at the end of the setlist, in
     * which case nothing is done).
     *
     * @param element
     *        The element to be added to the setlist.
     *
     * @return {@code true} if this setlist has changed as a result of this operation, {@code false} otherwise.
     */
    boolean forceAdd(E element);

    /**
     * {@inheritDoc}
     *
     * If the element already exists in the setlist, then the setlist will be unchanged. To force the element to be added
     * (<i>i.e.</i> shift the element from its current index to the specified index), use {@code forceAdd(int,E)}).
     */
    void add(int index, E element);

    /**
     * Insert an element at the specified index. Any elements at or beyond the index will be shifted appropriately If
     * the element already exists in the list, then it will be removed from its current position and placed at the
     * specified index (unless it already was at that index, in which case nothing is done).
     *
     * @param index
     *        The index at which to add the element.
     *
     * @param element
     *        The element to be added.
     */
    void forceAdd(int index, E element);

    /**
     * {@inheritDoc}
     *
     * If any of the elements already exists in the setlist, then their initial positions will be retained. To force the
     * elements to be added (<i>i.e.</i> put all elements at the end of the list), use {@code forceAddAll(int,Collection)}).
     * If the collection contains an object multiple times, the first instance of the element in the collection will be
     * used for positioning.
     *
     * @throws IllegalArgumentException if any of the elements in {@code c} already exist in this setlist.
     */
    boolean addAll(int index, Collection<? extends E> c);

    /**
     * Insert all of the elements in a collection to the setlist at the specified index.  All of the elements in the
     * setlist at or beyond the index will be shifted appropriately. The if the ordering of the collection is not well
     * defined, then neither will the order in which the elements will be added to the list. If an element in the collection
     * already exists in the list, then it will moved from its current position to the new index. If the collection contains
     * an object multiple times, the last instance of the element in the collection will be used for positioning.
     *
     * @param index
     *        The index at which to add the elements.
     *
     * @param c
     *        The collection containing the elements to add.
     *
     * @return {@code true} if the setlist changed as a result of this operation, {@code false} otherwise.
     */
    boolean forceAddAll(int index, Collection<? extends E> c);

    /**
     * {@inheritDoc}
     *
     * If any of the elements already exists in the setlist, then their initial positions will be retained. To force the
     * elements to be added (<i>i.e.</i> put all elements at the end of the list), use {@code forceAddAll(Collection)}).
     * If the collection contains an object multiple times, the first instance of the element in the collection will be
     * used for positioning.
     *
     * @throws IllegalArgumentException if any of the elements in {@code c} already exist in this setlist.
     */
    boolean addAll(Collection<? extends E> c);

    /**
     * Insert all of the elements in a collection to the end of the setlist.  The if the ordering of the collection is not well
     * defined, then neither will the order in which the elements will be added to the list. If an element in the collection
     * already exists in the list, then it will moved from its current position to the new index. If the collection contains
     * an object multiple times, the last instance of the element in the collection will be used for positioning.
     *
     * @param c
     *        The collection containing the elements to add.
     *
     * @return {@code true} if the setlist changed as a result of this operation, {@code false} otherwise.
     */
    boolean forceAddAll(Collection<? extends E> c);

    /**
     * {@inheritDoc}
     *
     * If the element already exists in the setlist, then then the setlist will be unchanged and {@code null} will be
     * returned. To force the element to be added (<i>i.e.</i> shift the element from its current index to the specified
     * index, replacing the element currently at that index), use {@code forceSet(int,E)}).
     */
    E set(int index, E element);

    /**
     * Set the element at a specified element in this setlist. The element currently at the specified index will be
     * removed and returned by this method. If {@code element} already exists in this setlist, it will be moved to the
     * newly specified index.
     *
     * @param index
     *        The index at which to set the element.
     *
     * @param element
     *        The element to set at the specified index.
     *
     * @return the element replaced by {@code element}.
     *
     * @throws IllegalArgumentException if {@code index} is out of range: (<tt>index &lt; 0 || index &gt;= size()</tt>)
     */
    E forceSet(int index, E element);

    /**
     * Compares the specified object with this setlist for equality. The equality requirements are those defined in
     * {@code java.util.List}.
     */
    boolean equals(Object o);

    /**
     * Returns the hash code value for this setlist. The hashing algorithm is that defined in {@code java.util.List}.
     */
    int hashCode();
}
