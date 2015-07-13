package com.pb.sawdust.util;

import net.jcip.annotations.Immutable;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The {@code Range} class is used to create iterable integer sequences. Sequences can be generated in both ascending and
 * descending directions, with user-defined step sizes. This class is inteded to be used as a convenience class, probably
 * in combination with a static import.  As an example, the following idiom for looping over an array's
 * indices:
 * <pre><tt>
 *     for(int i = 0; i < array.length; i++) {
 *         //do something with array[i]
 *     }
 * </tt></pre>
 * can be replaced with:
 * <pre><tt>
 *     for(int i : (new Range(array.length))) {
 *         //do something with array[i]
 *     }
 * </tt></pre>
 * Multiple iterators may be safely retrieved and used from an instance of this class concurrently, however, each
 * individual iterator is not thread safe and should not be accessed by multiple threads.
 * <p>
 * It is noted that while {@code Range}'s iterator is performant, it will usually be slower than a well constructed
 * (3-argument) for loop.  Thus, for performance critical sections of code, a traditional for loop is preferable. 
 * @author crf <br/>
 *         Started: Jun 18, 2008 9:51:21 AM
 */
@Immutable
public class Range implements Iterable<Integer> {
    private final int start; //inclusive
    private final int end; //exclusive
    private final int stepSize;
    private final boolean positiveStep;

    /**
     * Constructor specifying the start (inclusive), end (exclusive), and step size of the range. Mathematically, this
     * will create a sequence of numbers over <tt>[start,end)</tt> with distance between each number in the sequence
     * equal to {@code stepSize}.
     *
     * @param start
     *        The start of the sequence.
     *
     * @param end
     *        The end of the sequence.
     *
     * @param stepSize
     *        The sequence step size.
     *
     * @throws IllegalArgumentException if <tt>stepSize == 0</tt>, if (<tt>start &lt; end && stepSize &lt; 0</tt>), or if
     *                                  (<tt>start &gt; end && stepSize &gt; 0</tt>).
     */
    public Range(int start, int end, int stepSize) {
        if (stepSize == 0)
            throw new IllegalArgumentException("Step size must be non-zero.");
        if (start < end && stepSize < 1)
            throw new IllegalArgumentException("If start < end, then stepSize must be greater than zero");
        if (start > end && stepSize > -1)
            throw new IllegalArgumentException("If start > end, then stepSize must be less than zero");
        this.start = start;
        this.end = end;
        this.stepSize = stepSize;
        positiveStep = stepSize > 0;
    }

    /**
     * Construct a range with the specified start and end and a unitary step size. If <tt>start &lt;= end</tt>, then the
     * step size will be 1, otherwise it will be -1.
     *
     * @param start
     *        The start of the sequence.
     *
     * @param end
     *        The end of the sequence.
     */
    public Range(int start, int end) {
        this(start,end,start > end ? -1 : 1);
    }

    /**
     * Construct a range starting at 0 with a specified end and unitary step size. If <tt>0 &lt;= end</tt>, then the step
     * size will be 1, otherwise it will be -1.
     *
     * @param end
     *        The end of the sequence.
     */
    public Range(int end) {
        this(0,end);
    }

    private abstract class RangeIterator implements Iterator<Integer> {
        protected int currentValue = start;

        public Integer next() {
            if (!hasNext())
                throw new NoSuchElementException();
            int nextValue = currentValue;
            currentValue += stepSize;
            return nextValue;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public Iterator<Integer> iterator() {
        if (positiveStep)
            return new RangeIterator() {
                public boolean hasNext() {
                    return currentValue < end;
                }
            };
        else
            return new RangeIterator() {
                public boolean hasNext() {
                    return  currentValue > end;
                }
            };
    }

    /**
     * Get the range of integers which, when iterated over, would match that returned by this object's iterator.
     *
     * @return the integers in this range ordered in an array.
     */
    public int[] getRangeArray() {
        int shift = stepSize >>> 31 == 0 ? -1 : 1; //this is the sign function * -1
        int[] rangeArray = new int[((end + shift - start) / stepSize) + 1];
        int counter = 0;
        for (int i : this)
            rangeArray[counter++] = i;
        return rangeArray;
    }

    /**
     * Static convenience method for constructing a range with a specified start, end, and step size. This is intended
     * to be used with static class import, allowing calls to {@code Range.range(...)} to be changed to {@code range(...)}.
     *
     * @param start
     *        The start of the sequence.
     *
     * @param end
     *        The end of the sequence.
     *
     * @param stepSize
     *        The sequence step size.
     *
     * @return a new range object with {@code start}, {@code end}, and {@code stepSize}.
     *
     * @throws IllegalArgumentException if <tt>stepSize == 0</tt>, if <tt>(start < end && stepSize < 0)</tt>, or if
     *                                  <tt>(start > end && stepSize > 0)</tt>.
     *
     * @see com.pb.sawdust.util.Range#Range(int, int, int)
     */
    public static Range range(int start, int end, int stepSize) {
        return new Range(start,end,stepSize);
    }


    /**
     * Static convenience method for constructing a range with a specified start, end, and unitary step size. If
     * <tt>start &lt;= end</tt>, then the step size will be 1, otherwise it will be -1. This is intended to be used with
     * static class import, allowing calls to {@code Range.range(...)} to be changed to {@code range(...)}.
     *
     * @param start
     *        The start of the sequence.
     *
     * @param end
     *        The end of the sequence.
     *
     * @return a new range object with {@code start}, {@code end}, and unitary step size.
     *
     * @throws IllegalArgumentException if <tt>stepSize == 0</tt>, if (<tt>start &lt; end && stepSize &lt; 0</tt>), or if
     *                                  (<tt>start &gt; end && stepSize &gt; 0</tt>).
     *
     * @see com.pb.sawdust.util.Range#Range(int, int)
     */
    public static Range range(int start, int end) {
        return new Range(start,end);
    }


    /**
     * Static convenience method for constructing a range starting at 0 with a specified  end and unitary step size. If
     * <tt>0 &lt;= end</tt>, then the step size will be 1, otherwise it will be -1. This is intended to be used with static
     * class import, allowing calls to {@code Range.range(...)} to be changed to {@code range(...)}.
     *
     * @param end
     *        The start of the sequence.
     *
     * @return a new range object starting at 0, ending at {@code end}, and unitary step size.
     *
     * @throws IllegalArgumentException if <tt>stepSize == 0</tt>, if <tt>(start < end && stepSize < 0)</tt>, or if
     *                                  <tt>(start > end && stepSize > 0)</tt>.
     *
     * @see com.pb.sawdust.util.Range#Range(int, int)
     */
    public static Range range(int end) {
        return new Range(end);
    }

    /**
     * Get a range which is the reverse of the specified range. Specifically, the arrays returned by {@code getRangeArray()}
     * called on the input and output {@code Range}s will contain the same elements with opposite ordering.
     *
     * @param range
     *        The input range.
     *
     * @return a range which is the reverse of {@code range}.
     */
    public static Range reverseOf(Range range) {
        int shift = range.stepSize >>> 31 == 0 ? -1 : 1; //this is the sign function * -1
        return new Range(((range.end + shift - range.start) / range.stepSize)*range.stepSize + range.start,range.start+shift,range.stepSize*-1);
    }
}
