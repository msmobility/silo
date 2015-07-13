package com.pb.sawdust.util.abacus;

import com.pb.sawdust.util.Range;

/**
 * The {@code LockableAbacus} class provides an abacus whose dimensions may be fixed at a certain value. For example,
 * if a lockable abacus is created with the dimensions 8,5,3 and the second dimension is locked at a value of 3, then the
 * following states will start the iteration sequence of the abacus:
 * <pre>
 *     <code>
 *         [0,3,0]
 *     </code>
 *     <code>
 *         [0,3,1]
 *     </code>
 *     <code>
 *         [0,3,2]
 *     </code>
 *     <code>
 *         [1,3,0]
 *     </code>
 *     <code>
 *         ...
 *     </code>
 * </pre>
 * <p>
 * It is important to note that this class is not immutable, in that its dimensions are locked via a method, rather
 * than at construction time. This allows the convenience of different lock configurations being built from fresh clones
 * of a source lockable abacus. Once a dimension has been locked, the abacus's {@code getLength()} and {@code getState()}
 * return values will change, as the number of iteration states will be reduced by the size of the locked dimension.
 *
 * @author crf <br/>
 *         Started: Jun 4, 2009 12:26:07 PM
 */
public class LockableAbacus extends Abacus {
    private final boolean[] lockedDimensions;

    /**
     * Constructor specifying the dimensions of the lockableabacus and its value precedence direction. If the {@code reverse}
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
    public LockableAbacus(boolean reverse, int ... dimensions) {
        super(reverse,dimensions);
        lockedDimensions = new boolean[dimensions.length];
    }

    /**
     * Constructor for a standard (non-reversed) lockable abacus specifying its dimensions.
     *
     * @param dimensions
     *        The dimensions of the abacus. Each entry specifies the value at which a "rollover" occurs.
     *
     * @throws IllegalArgumentException if {@code dimensions.length == 0}, or if any entry in {@code dimensions} is less
     *                                  than one.
     */
    public LockableAbacus(int ... dimensions) {
        this(false,dimensions);
    }

    private LockableAbacus(Range range, int hasNextPoint, int[] dimensions, boolean[] lockedDimensions, int[] counter) {
        super(range,hasNextPoint,dimensions);
        this.lockedDimensions = lockedDimensions;
        for (int i : range)
            if (lockedDimensions[i])
                this.counter[i] = counter[i];
    }

    /**
     * Lock a specified dimension to a value.
     *
     * @param dimension
     *        The dimension (0-based) to lock. If the dimension is already locked, this lock will replace the previous one.
     *
     * @param value
     *        The value to lock {@code dimension} to.
     *
     * @throws IllegalArgumentException if {@code dimension} is less than 0 or greater than or equal to the number of
     *                                  dimensions in the abacus, or if {@code value} is less than 0 or greater than or
     *                                  equal to the (initial) size of {@code dimension}.
     */
    public void lockDimension(int dimension, int value) {
        if (dimension < 0 || dimension >= dimensions.length)
            throw new IllegalArgumentException(String.format("Dimension (%d) out of bounds for abacus with length %d.",dimension,dimensions.length));
        if (value < 0 || value >= dimensions[dimension])
            throw new IllegalArgumentException(String.format("Dimension lock value (%d) out of bounds for dimension %d in abacus with size %d.",value,dimension,dimensions[dimension]));
        lockedDimensions[dimension] = true;
        counter[dimension] = value;
    }

    void increment() {
        for (int i : range) {
            if (lockedDimensions[i]) {
                if (i == hasNextPoint)
                    hasNext = false;
                continue;
            }
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
     * {@inheritDoc}
     *
     * This method will return a {@code LockableAbacus} with the same dimensions locked (to the same values} as this
     * abacus at the time this method is called. No connection between the clone and this abacus are retained after the
     * clone is constructed (that is, subsequent dimension locks on this abacus are not transmitted to the clone).
     */
    public LockableAbacus freshClone() {
        return new LockableAbacus(range,hasNextPoint,dimensions,lockedDimensions,counter);
    }

    public int[] getAbacusPoint(long iteratorPosition) {
        if (iteratorPosition < 0)
            throw new IllegalArgumentException("Negative iterator position: " + iteratorPosition);
        int[] stepSizes = new int[dimensions.length];
        int lastPosition = -1;
        for (int i : range) {
            if (lockedDimensions[i])
                continue;
            stepSizes[i] = lastPosition == -1 ? 1 : dimensions[lastPosition]*stepSizes[lastPosition];
            lastPosition = i;
        }
        int[] point = new int[dimensions.length];
        for (int i : Range.reverseOf(range)) {
            if (lockedDimensions[i]) {
                point[i] = counter[i];
            } else { 
                point[i] = (int) (iteratorPosition / stepSizes[i]);
                iteratorPosition %= stepSizes[i];
            }
        }
        if (point[lastPosition] >= dimensions[lastPosition])
            throw new IllegalArgumentException("Iterator position beyond bounds of abacus.");
        return point;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if the value of the locked dimensions in {@code point} do not equal the value
     *                                  that those dimensions were locked at.
     */
    public void setAbacusPoint(int[] point) {
        if (point.length != counter.length)
            throw new IllegalArgumentException("Abacus point must have an element for each dimension.");
        for (int i : range)
            if (lockedDimensions[i] && counter[i] != point[i])
                throw new IllegalArgumentException("Abacus point at locked dimension (" + i + ") must be equal to value locked at; " +
                                                   "found " + point[i] + " expected " + counter[i]);
        super.setAbacusPoint(point);
    }

    public long getStateCount() {
        long count = 1L;
        for (int i : range)
            if (!lockedDimensions[i])
                count *= dimensions[i];
        return count;
    }
}
