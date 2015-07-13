package com.pb.sawdust.util.abacus;

import com.pb.sawdust.util.Range;

import java.util.Arrays;

/**
 * The {@code RollingAbacus} class provides an {@code Abacus} implementation with performs multiple iteration cycles. That
 * is, a rolling abacus constructed with parameters equivalent to a given (regular) abacus {@code a} and with a rolling count
 * of {@code n} will have an iteration cycle equivalent to {@code n} complete cycles of {@code a} performed sequentially.
 *
 * @author crf <br/>
 *         Started: Jun 5, 2009 11:25:17 AM
 */
public class RollingAbacus extends Abacus {
    private final long rollingCount;
    private long currentRoll = 1;
    private long length = -1;

    /**
     * Constructor specifying the dimensions of the abacus, its value precedence direction, and its rolling count. If the
     * {@code reverse} flag is set to {@code false}, then a "standard" rolling abacus, where increasing array indexes indicate
     * increasing value precedence, will be built; if {@code reverse} is set to {@code true}, then the higher the array index,
     * the lower the value precedence.
     *
     * @param reverse
     *        Flag indicating whether the value precedence should be reversed or not in the abacus.
     *
     * @param rollingCount
     *        The number of rollover cycles this abacus will perform.
     *
     * @param dimensions
     *        The dimensions of the abacus. Each entry specifies the value at which a "rollover" occurs.
     *
     * @throws IllegalArgumentException if {@code dimensions.length == 0}, if any entry in {@code dimensions} is less
     *                                  than one, or if {@code rollingCount < 1}.
     */
    public RollingAbacus(boolean reverse, long rollingCount, int ... dimensions) {
        super(reverse,dimensions);
        if (rollingCount < 1)
            throw new IllegalArgumentException("Rolling count must be greater than 0.");
        this.rollingCount = rollingCount;
    }

    /**
     * Constructor specifying the dimensions of the abacus and its rolling count. An "standard" (non-reversed) abacus
     * will be constructed.
     *
     * @param rollingCount
     *        The number of rollover cycles this abacus will perform.
     *
     * @param dimensions
     *        The dimensions of the abacus. Each entry specifies the value at which a "rollover" occurs.
     *
     * @throws IllegalArgumentException if {@code dimensions.length == 0}, if any entry in {@code dimensions} is less
     *                                  than one, or if {@code rollingCount < 1}.
     */
    public RollingAbacus(long rollingCount, int ... dimensions) {
        this(false,rollingCount,dimensions);
    }

    RollingAbacus(long rollingCount, Range range, int hasNextPoint, int[] dimensions) {
        super(range,hasNextPoint,dimensions);
        this.rollingCount = rollingCount;
    }

    void increment() {
        super.increment();
        if (!hasNext && currentRoll < rollingCount) {
            Arrays.fill(counter,0);
            currentRoll++;
            hasNext = true;
        }
    }

    public RollingAbacus freshClone() {
        return new RollingAbacus(rollingCount,range,hasNextPoint,dimensions);
    }

    private long calculateLoopLength() {
        long length = 1;
        for (int d : dimensions)
            length *= d;
        return length;
    }

    private void checkAbacusPosition(long iteratorPosition) {
        if (iteratorPosition < 0)
            throw new IllegalArgumentException("Negative iterator position: " + iteratorPosition);
        if (length == -1)
            length = calculateLoopLength();
        if (iteratorPosition / length >= rollingCount)
            throw new IllegalArgumentException("Iterator position beyond bounds of abacus.");
    }

    public int[] getAbacusPoint(long iteratorPosition) {
        checkAbacusPosition(iteratorPosition);
        return super.getAbacusPoint(iteratorPosition % length);
    }

    public void setAbacusAtPosition(long iteratorPosition) {
        checkAbacusPosition(iteratorPosition);
        super.setAbacusAtPosition(iteratorPosition % length);
        currentRoll = iteratorPosition / length;
    }

    public void setAbacusPoint(int[] point) {
        setAbacusPoint(point,0);
    }

    public void setAbacusPoint(int[] point, int roll) {
        super.setAbacusPoint(point);
        currentRoll = roll;
    }

    public long getLength() {
        return getStateCount()*rollingCount;
    }
}
