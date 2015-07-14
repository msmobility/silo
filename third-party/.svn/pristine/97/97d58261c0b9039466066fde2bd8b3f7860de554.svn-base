package com.pb.sawdust.util.collections;

import java.util.Deque;

/**
 * A {@code SetDeque} is a deque which can contain no repeated values. Though it implements both the {@code Deque} and
 * {@code Set} interface, it is intended to be used as a deque which is constrained to be a set, rather than a set which
 * is a deque. The caveats and assumptions mentioned in the {@code SetQueue} interface apply here as well; this
 * interface adds more {@code force*} methods which refer to the {@code Deque} interface.
 *
 * @param <E>
 *        The type of the elements held by this collection.
 *
 * @author crf <br/>
 *         Started: Dec 30, 2008 9:13:58 AM
 */
public interface SetDeque<E> extends SetQueue<E>,Deque<E>  {
    /**
     * {@inheritDoc}
     *
     * If the element already exists in this setdeque, the setdeque will remain unchaged. To force the element to be added
     * to the head of this setdeque, use {@code forceAddFirst(E)}).
     */
    void addFirst(E element);

    /**
     * Inserts the specified element at the front of this setdeque if it is possible to do so immediately without violating
     * capacity restrictions. If the element already exists in this setdeque its previous position  will be removed. When
     * using a capacity-restricted setdeque, it is generally preferable to use method {@code forceOfferFirst(E)}.
     *
     * @param element
     *        The element to be added to the setdeque.
     *
     * @throws IllegalStateException if the element cannot be added at this time due to capacity restrictions.
     */
    void forceAddFirst(E element);

    /**
     * {@inheritDoc}
     *
     * If the element already exists in this setdeque, it will remain unchaged. To force the element to be added
     * to the tail of this setdeque, use {@code forceAddFirst(E)}).
     */
    void addLast(E element);

    /**
     * Inserts the specified element at the end of this setdeque if it is possible to do so immediately without violating
     * capacity restrictions. If the element already exists in this setdeque its previous position  will be removed. When
     * using a capacity-restricted setdeque, it is generally preferable to use method {@code forceOfferLast(E)}.
     *
     * @param element
     *        The element to be added to the setdeque.
     *
     * @throws IllegalStateException if the element cannot be added at this time due to capacity restrictions.
     */
    void forceAddLast(E element);

    /**
     * {@inheritDoc}
     *
     * If the element already exists in this setdeque, the setdeque will remain unchaged. To force the element to be added
     * to the head of this setdeque, use {@code forceOfferFirst(E)}).
     */
    boolean offerFirst(E element);

    /**
     * Inserts the specified element at the front of this setdeque unless it would violate capacity restrictions. If the
     * element already exists in this setdeque its previous position  will be removed. When using a capacity-restricted
     * setdeque, this method is generally preferable to the {@code forceAddFirst(E)} method, which can fail to insert an
     * element only by throwing an exception.
     *
     * @param element
     *        The element to be added to the setdeque.
     *
     * @return {@code true} if this setdeque has changed as a result of this operation, {@code false} otherwise.
     */
    boolean forceOfferFirst(E element);

    /**
     * {@inheritDoc}
     *
     * If the element already exists in this setdeque, it will remain unchaged. To force the element to be offered
     * to the tail of this setdeque, use {@code forceOfferLast(E)}).
     */
    boolean offerLast(E element);

    /**
     * Inserts the specified element at the end of this setdeque unless it would violate capacity restrictions. If the
     * element already exists in this setdeque its previous position  will be removed. When using a capacity-restricted
     * setdeque, this method is generally preferable to the {@code forceAddLast(E)} method, which can fail to insert an
     * element only by throwing an exception.
     *
     * @param element
     *        The element to be added to the setdeque.
     *
     * @return {@code true} if this setdeque has changed as a result of this operation, {@code false} otherwise.
     */
    boolean forceOfferLast(E element);

    /**
     * {@inheritDoc}
     *
     * If the element already exists in this setdeque, the setdeque will remain unchaged. To force the element to be added
     * to the head of this setdeque, use {@code forcePush(E)}).
     */
    void push(E element);

    /**
     * Pushes an element onto the stack represented by this deque (in other words, at the head of this deque) if it is
     * possible to do so immediately without violating capacity restrictions, returning true upon success and throwing
     * an {@code IllegalStateException} if no space is currently available. This method is equivalent to {@code forceAddFirst(E)}.
     *
     * @param element
     *        The element to be added to the setdeque.
     *
     * @throws IllegalStateException if the element cannot be added at this time due to capacity restrictions.
     */
    void forcePush(E element);
}
