package com.pb.sawdust.util.probability;

import com.pb.sawdust.util.Range;
import com.pb.sawdust.util.array.ArrayUtil;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * The {@code AdjustingDiscreteProbabilisticDistribution} ...
 *
 * @author crf
 *         Started 4/12/12 11:54 AM
 */
public class AdjustingDiscreteProbabilisticDistribution extends AbstractDiscreteProbabilisticDistribution {
    private final double[] baseCumulativeDistribution;
    private final int[] activeIndices;
    private final double adjustment;
    private final boolean useAdjustment;

    public AdjustingDiscreteProbabilisticDistribution(double[] cumulativeDistribution) {
        double last = 0.0;
        for (double d : cumulativeDistribution) {
            if (d < last)
                throw new IllegalArgumentException(String.format("Cumulative distribution must be strictly increasing on [0.0,1.0]: ... %f , %f ...",last,d));
            last = d;
        }
        if (last != 1.0)
            throw new IllegalArgumentException("Final element in cumulative distribution must be 1.0: " + last);
        baseCumulativeDistribution = ArrayUtil.copyArray(cumulativeDistribution);
        activeIndices = new Range(baseCumulativeDistribution.length).getRangeArray();
        useAdjustment = false;
        adjustment = 1.0;
    }

    private AdjustingDiscreteProbabilisticDistribution(double[] baseCumulativeDistribution, int[] activeIndices) {
        this.baseCumulativeDistribution = baseCumulativeDistribution;
        this.activeIndices = activeIndices;
        double adjustment = 0.0;
        try {
            for (int i : activeIndices)
                adjustment += baseCumulativeDistribution[i];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Index out of base cumulative distribution's bounds: " + e.getMessage());
        }
        this.adjustment = adjustment;
        useAdjustment = adjustment != 1.0;
        if (activeIndices.length == 0 || adjustment == 0.0)
            throw new IllegalArgumentException("Distribution must have at least one non-zero point.");
    }

    @Override
    public int getLength() {
        return activeIndices.length;
    }

    @Override
    public double getCumulativeValue(int point) {
        checkPoint(point);
        return useAdjustment ? baseCumulativeDistribution[activeIndices[point]] / adjustment : baseCumulativeDistribution[activeIndices[point]];
    }

    public AdjustingDiscreteProbabilisticDistribution removePoints(Collection<Integer> items) {
        List<Integer> newActiveItems = new LinkedList<>();
        for (int i : Range.range(activeIndices.length))
            if (!items.contains(i))
                newActiveItems.add(i);
        return new AdjustingDiscreteProbabilisticDistribution(baseCumulativeDistribution,ArrayUtil.toIntArray(newActiveItems));
    }
}
