package com.pb.sawdust.util.probability;

import static com.pb.sawdust.util.Range.*;

/**
 * The {@code AbstractDiscreteProbabilisticDistribution} ...
 *
 * @author crf
 *         Started 4/12/12 9:43 AM
 */
public abstract class AbstractDiscreteProbabilisticDistribution implements DiscreteProbabilisticDistribution {

    protected void checkPoint(int point) {
        if (point >= getLength() || point < 0)
            throw new IllegalArgumentException(String.format("Point out of bounds for distribution of length %d: %d",getLength(),point));
    }

    @Override
    public double getProbability(int point) {
        checkPoint(point);
        return getCumulativeValue(point) - (point == 0 ? 0.0 : getCumulativeValue(point-1));
    }

    @Override
    public double[] getCumulativeDistribution() {
        double[] cumulativeDistribution = new double[getLength()];
        for (int i : range(cumulativeDistribution.length))
            cumulativeDistribution[i] = getCumulativeValue(i);
        return cumulativeDistribution;
    }

    @Override
    public double[] getDistribution() {
        double[] cumulativeDistribution = new double[getLength()];
        double last = 0.0;
        for (int i : range(cumulativeDistribution.length)) {
            double current = getCumulativeValue(i);
            cumulativeDistribution[i] = current - last;
            last = current;
        }
        return cumulativeDistribution;
    }
}
