package com.pb.sawdust.util.collections;

/**
 * The {@code UnmodifiableCollections} provides unmodifiable versions of collection classes.
 *
 * @author crf <br/>
 *         Started Sep 13, 2010 5:08:14 PM
 */
public class UnmodifiableCollections {

    /**
     * Get an unmodifiable view of a specified setlist. This method will return a reference to the original setlist, so
     * subsequent modifications to that setlist will be reflected in the unmodifiable copy.
     *
     * @param setList
     *        The source setlist.
     *
     * @param <E>
     *        The type of elements held by the setlist.
     *
     * @return an unmodifiable view of {@code setList}.
     */
    public static <E> SetList<E> unmodifiableSetList(SetList<E> setList) {
        return new UnmodifiableSetList<E>(setList);
    }
}
