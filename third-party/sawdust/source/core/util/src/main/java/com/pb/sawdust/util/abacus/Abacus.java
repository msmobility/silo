package com.pb.sawdust.util.abacus;

import com.pb.sawdust.util.Range;

import java.util.NoSuchElementException;
import java.util.Iterator;

/**
 * The {@code Abacus} class provides an integer rolling counter (similar to an analog odometer). Abacus values are
 * represented as arrays whose individual elements represent counters whose relative "value" increases from the last
 * array element to the first (or the opposite, if a reverse flag has been set).  Whenever an abacus is incremented,
 * its lowest value entry is incremented by one, and if that new value exceeds the maximum for that entry, it "rolls
 * over" - it gets set to zero and the next entry is incremented. As an example, if a three-dimensional abacus with
 * dimensions 8,5,3 has the following position:
 * <pre>
 *     <code>
 *         [0,4,2]
 *     </code>
 * </pre>
 * then incrementing the abacus will lead to the following state:
 * <pre>
 *     <code>
 *         [1,0,0]
 *     </code>
 * </pre>
 * <p>
 * {@code Abacus} implements the {@code Iterator<int[]>} interface, and uses the {@code next()} method to increment.
 * Once an abacus has "maxxed out" (reached a position where a increment would force the highest value entry to
 * rollover) calls to {@code next()} will throw an {@code NoSuchElementEzception}; that is, an abacus will not cycle
 * infinitely. Because there is no way to reset or decrement an abacus, it is intended for one-time use only. If a
 * multi-use abacus is desired, the static {@code getMultiAbacus} methods will return an {@code Iterable<int[]>} whose
 * {@code iterator()} method will use a fresh abacus each time (this object also has the advantage that it can be used
 * in "for each" calls).
 * <p>
 * An abacus is particularly useful for cycling through rectangular multi-dimensional arrays. For a given array with
 * ({@code int[]}) dimensions {@code d}, an abacus created with the dimensions {@code d} will cycle through every array
 * index position before completing.
 *
 * @author crf <br/>
 *         Started: Jan 6, 2009 8:46:33 PM
 */
public class Abacus implements Iterator<int[]> {
    //all of these are package-protected to allow access to others in this package
    final int[] counter;
    final int[] dimensions;
    final int hasNextPoint;
    final Range range;
    boolean incremented;
    boolean hasNext;

    /**
     * Constructor for a standard (non-reversed) abacus specifying its dimensions.
     *
     * @param dimensions
     *        The dimensions of the abacus. Each entry specifies the value at which a "rollover" occurs.
     *
     * @throws IllegalArgumentException if {@code dimensions.length == 0}, or if any entry in {@code dimensions} is less
     *                                  than one.
     */
    public Abacus(int ... dimensions) {
        this(false,dimensions);
    }

    /**
     * Constructor specifying the dimensions of the abacus and its value precedence direction. If the {@code reverse}
     * flag is set to {@code false}, then a "standard" abacus, where decreasing array indexes indicate increasing
     * value precedence, will be built; if {@code reverse} is set to {@code true}, then the lower the array index,
     * the lower the value precedence.
     *
     * @param reverse
     *        Flag indicating whether the value precedence should be reversed or not in the abacus.
     *
     * @param dimensions
     *        The dimensions of the abacus. Each entry specifies the value at which a "rollover" occurs.
     *
     * @throws IllegalArgumentException if {@code dimensions.length == 0}, or if any entry in {@code dimensions} is less
     *                                  than one.
     */
    public Abacus(boolean reverse, int ... dimensions) {
        int dl = dimensions.length;
        counter = new int[dl];
        this.dimensions = new int[dl];
        int counter = 0;
        if (dimensions.length == 0)
            throw new IllegalArgumentException("Abacus constructor must specify at least one dimension.");
        for (int d : dimensions)
            if (d < 1)
                throw new IllegalArgumentException("Dimensions must be greater than zero: " + d);
            else
                this.dimensions[counter++] = d;
        if (reverse) {
            range = new Range(dimensions.length);
            hasNextPoint = dl-1;
        } else {
            range = new Range(dimensions.length-1,-1);
            hasNextPoint = 0;
        }
        incremented = true;
        hasNext = true;
    }

    Abacus(Range range, int hasNextPoint, int[] dimensions) {
        this.dimensions = dimensions;
        this.hasNextPoint = hasNextPoint;
        this.range = range;
        counter = new int[dimensions.length];
        incremented = true;
        hasNext = true;
    }

    /**
     * Indicates whether this abacus has reached its maximum value or not. If this returns {@code false}, the subsequent
     * calls to {@code next()} will cause a {@code NoSuchElementException} to be thrown.
     *
     * @return {@code true} if the abacus has not reached its maximum value, or {@code false} if it has.
     */
    public boolean hasNext() {
        if (!incremented)
            increment();
        return hasNext;
    }

    /**
     * Increment the abacus and return the result.
     *
     * @return the incremented result.
     *
     * @throws NoSuchElementException if the abacus has already reached its maximum value.
     */
    public int[] next() {
        if (!hasNext())
            throw new NoSuchElementException();
        incremented = false;
        return counter;
    }

    /**
     * This method is not supported and throws an {@code UnsupportedOperationException}.
     *
     * @throws UnsupportedOperationException if this method is called.
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }

    void increment() {
        for (int i : range) {
            counter[i]++;
            if (counter[i] == dimensions[i])
                if (i == hasNextPoint)
                    hasNext = false;
                else
                    counter[i] = 0;
            else
                break;
        }
        incremented = true;
    }

    /**
     * Get the total number of unique states of this abacus. This may not be equal to the length of the abacus, in the
     * case that the abacus returns duplicate states at different points in its iteration.
     *
     * @return the number of unique states of this abacus.
     */
    public long getStateCount() {
        long states = 1L;
        for (int i : dimensions)
            states *= i;
        return states;
    }

    /**
     * Get a new abacus with the same structure as this one set at its first positional state. This method does not affect
     * this abacus.
     *
     * @return a new clone of this abacus with its position state set to that in which now iterations have been performed.
     */
    public Abacus freshClone() {
        return new Abacus(range,hasNextPoint,dimensions);
    }

    /**
     * Get the array returned by this abacus after a given number of iterations. This method is more efficient than
     * explicitly iterating through a fresh clone of this abacus, and thus can be useful in quickly getting a single position
     * in this abacus's iteration cycle.
     *
     * @param iteratorPosition
     *        The iteration position of interest.
     *
     * @return the abacus point at {@code iterationPosition}.
     *
     * @throws IllegalArgumentException if {@code iterationPosition} is less than 0 or greater or equal to the length of
     *                                  this abacus.
     */
    public int[] getAbacusPoint(long iteratorPosition) {
        if (iteratorPosition < 0)
            throw new IllegalArgumentException("Negative iterator position: " + iteratorPosition);
        int[] stepSizes = new int[dimensions.length];
        int lastPosition = -1;
        for (int i : range) {
            stepSizes[i] = lastPosition == -1 ? 1 : dimensions[lastPosition]*stepSizes[lastPosition];
            lastPosition = i;
        }
        int[] point = new int[dimensions.length];
        for (int i : Range.reverseOf(range)) {
            point[i] = (int) (iteratorPosition / stepSizes[i]);
            iteratorPosition %= stepSizes[i];
        }
        if (point[lastPosition] >= dimensions[lastPosition])
            throw new IllegalArgumentException("Iterator position beyond bounds of abacus.");
        return point;
    }

    /**
     * Set this abacus's state to that which it would have after a given number of iterations. The iterator position argument
     * refers to the number of iterations on a fresh clone, not the current state of this abacus.
     *
     * @param iteratorPosition
     *        The position to set this abacus at.
     *
     * @throws IllegalArgumentException if {@code iterationPosition} is less than 0 or greater or equal to the length of
     *                                  this abacus.
     */
    public void setAbacusAtPosition(long iteratorPosition) {
        setAbacusPointUnchecked(getAbacusPoint(iteratorPosition));
    }

    /**
     * Set this abacus's state as if its last iteration step returned the specified element point.
     *
     * @param point
     *        The point to set the abacus at.
     *
     * @throws IllegalArgumentException if {@code point.length} does not equal the dimensional length of this abacus,
     *                                  or if any value in {@code point} is less than 0 or greater than the maximum abacus
     *                                  value in its respective dimension.
     *
     */
    public void setAbacusPoint(int[] point) {
        if (point.length != counter.length)
            throw new IllegalArgumentException("Abacus point must have an element for each dimension.");
        for (int i : range)
            if (point[i] < 0 || point[i] >= dimensions[i])
                throw new IllegalArgumentException("Abacus point element (dimension " + i + ") out of bounds:" + point[i]);
        setAbacusPointUnchecked(point);
    }

    private void setAbacusPointUnchecked(int[] point) {
        System.arraycopy(point,0,counter,0,counter.length);
        incremented = true;
        hasNext = true;
    }

    /**
     * Get the total number of times this abacus can be iterated. That is, the number of times {@code next()} can be called
     * on a fresh clone of this abacus before {@code hasNext()} returns {@code false}.
     *
     * @return the number of times this abacus can be iterated.
     */
    public long getLength() {
        return getStateCount();
    }
}
