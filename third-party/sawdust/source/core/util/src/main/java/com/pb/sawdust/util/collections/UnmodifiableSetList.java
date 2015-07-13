package com.pb.sawdust.util.collections;

import java.util.*;

/**
 * The {@code UnmodifiableSetList} ...
 *
 * @author crf <br/>
 *         Started Sep 13, 2010 4:46:39 PM
 */
final class UnmodifiableSetList<E> implements SetList<E> {
    private final SetList<E> setList;

    UnmodifiableSetList(SetList<E> setList) {
        this.setList = setList;
    }

    protected boolean addToSet(E e) {
        throw new UnsupportedOperationException("SetList is unmodifiable.");
    }

    protected boolean removeFromSet(E e) {
        throw new UnsupportedOperationException("SetList is unmodifiable.");
    }



    public boolean forceAdd(E element) {
        throw new UnsupportedOperationException("SetList is unmodifiable.");
    }

    @Override
    public int size() {
        return setList.size();
    }

    @Override
    public boolean isEmpty() {
        return setList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return setList.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private final Iterator<E> iterator = setList.iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public E next() {
                return iterator.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("SetList is unmodifiable.");
            }
        };
    }

    @Override
    public Object[] toArray() {
        return setList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return setList.toArray(a);
    }

    public boolean add(E element) {
        throw new UnsupportedOperationException("SetList is unmodifiable.");
    }

    public boolean remove(Object o) {
        throw new UnsupportedOperationException("SetList is unmodifiable.");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return setList.containsAll(c);
    }

    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException("SetList is unmodifiable.");
    }

    public boolean forceAddAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException("SetList is unmodifiable.");
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException("SetList is unmodifiable.");
    }

    public boolean forceAddAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException("SetList is unmodifiable.");
    }

    public void add(int index, E element) {
        throw new UnsupportedOperationException("SetList is unmodifiable.");
    }

    public void forceAdd(int index, E element) {
        throw new UnsupportedOperationException("SetList is unmodifiable.");
    }

    public E set(int index, E element) {
        throw new UnsupportedOperationException("SetList is unmodifiable.");
    }

    public E forceSet(int index, E element) {
        throw new UnsupportedOperationException("SetList is unmodifiable.");
    }

    public boolean shift(E element, int index) {
        throw new UnsupportedOperationException("SetList is unmodifiable.");
    }

    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("SetList is unmodifiable.");
    }

    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("SetList is unmodifiable.");
    }

    public void clear() {
        throw new UnsupportedOperationException("SetList is unmodifiable.");
    }

    @Override
    public E get(int index) {
        return setList.get(index);
    }

    public E remove(int index) {
        throw new UnsupportedOperationException("SetList is unmodifiable.");
    }

    @Override
    public int indexOf(Object o) {
        return setList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return setList.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return setList.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return setList.listIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return Collections.unmodifiableList(setList.subList(fromIndex, toIndex));
    }
}
