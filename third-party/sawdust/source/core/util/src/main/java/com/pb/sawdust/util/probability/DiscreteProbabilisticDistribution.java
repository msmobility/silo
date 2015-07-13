package com.pb.sawdust.util.probability;

/**
 * The {@code DiscreteProbabilisticDistribution} ...
 *
 * @author crf
 *         Started 4/12/12 9:21 AM
 */
public interface DiscreteProbabilisticDistribution {
    int getLength();
    double getCumulativeValue(int point);
    double getProbability(int point);
    double[] getCumulativeDistribution();
    double[] getDistribution();
}
