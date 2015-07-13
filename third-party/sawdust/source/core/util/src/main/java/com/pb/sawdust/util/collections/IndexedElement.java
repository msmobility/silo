package com.pb.sawdust.util.collections;

/**
 * The {@code IndexedElement} class is used to wrap an object with an integer index. It provides a means for an order
 * or heirarchy to be placed on an object (element) irrespective of that object's characteristics.
 *
 * @param <E>
 *        The type of the element this {@code IndexedElement} instance holds.
 *
 * @author crf <br/>
 *         Started Aug 14, 2010 6:35:59 PM
 */
public class IndexedElement<E> implements Comparable<IndexedElement<E>> {
    private final E element;
    private final long index;

    /**
     * Constructor specifying the element and its index.
     *
     * @param element
     *        The element.
     *
     * @param index
     *        The index value.
     *
     */
    public IndexedElement(E element, long index) {
        this.element = element;
        this.index = index;
    }

    /**
     * Get the element held by this indexed element.
     *
     * @return the element held by this this indexed element. 
     */
    public E getElement() {
        return element;
    }

    /**
     * Get the the index of this indexed element.
     *
     * @return the index of this indexed element.
     */
    public long getElementIndex() {
        return index;
    }

    @Override
    public int compareTo(IndexedElement<E> o) {
        return (int) (index - o.index);
    }
}
