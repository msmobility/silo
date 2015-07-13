package com.pb.sawdust.util.probability;

import com.pb.sawdust.util.array.ArrayUtil;

import static com.pb.sawdust.util.Range.range;

/**
 * The {@code SimpleDiscreteProbabilisticDistribution} ...
 *
 * @author crf
 *         Started 4/12/12 11:46 AM
 */
public class SimpleDiscreteProbabilisticDistribution extends AbstractDiscreteProbabilisticDistribution {
    private final double[] distribution;
    private final double[] cumulativeDistribution;

    public SimpleDiscreteProbabilisticDistribution(double[] distribution) {
        double sum = 0.0;
        for (double d : distribution) {
            if (d < 0)
                throw new IllegalArgumentException("Distribution cannot contain negative values: " + d);
            else if (Double.isNaN(d) || Double.isInfinite(d))
                throw new IllegalArgumentException("Initial distribution contains invalid value: " + d);
            sum += d;
        }
        if (sum == 0.0)
            throw new IllegalArgumentException("Initial distribution sums to 0.0");
        this.distribution = new double[distribution.length];
        cumulativeDistribution = new double[distribution.length];
        double cumSum = 0.0;
        for (int i : range(distribution.length))
            cumSum = (cumulativeDistribution[i] = (cumSum + (this.distribution[i] = distribution[i]/sum)));
        cumulativeDistribution[cumulativeDistribution.length-1] = 1.0; //fix it at top
        }

    @Override
    public int getLength() {
        return distribution.length;
    }

    @Override
    public double getCumulativeValue(int point) {
        checkPoint(point);
        return cumulativeDistribution[point];
    }

    @Override
    public double getProbability(int point) {
        checkPoint(point);
        return distribution[point];
    }

    @Override
    public double[] getCumulativeDistribution() {
        return ArrayUtil.copyArray(cumulativeDistribution);
    }

    @Override
    public double[] getDistribution() {
        return ArrayUtil.copyArray(distribution);
    }
}
