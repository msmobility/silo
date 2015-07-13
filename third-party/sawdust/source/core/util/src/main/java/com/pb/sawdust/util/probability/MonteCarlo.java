package com.pb.sawdust.util.probability;

import static com.pb.sawdust.util.Range.*;

import java.util.*;

/**
 * The {@code MonteCarlo} class provides the basic functionality for doing Monte Carlo choice analysis. Given a distribution,
 * it will provide random choices from that distribution based on a random number generator.
 *
 * @author crf
 *         Started 9/28/11 11:05 AM
 */
public class MonteCarlo {
    private final DiscreteProbabilisticDistribution distribution;
    private final int startingPoint; //needed to skip zero-probability starting points
    private final Random random;

    public MonteCarlo(DiscreteProbabilisticDistribution distribution, Random random) {
        this.distribution = distribution;
        this.random = random;
        int startingPoint = -1;
        for (int i : range(distribution.getLength())) {
            if (distribution.getProbability(i) != 0.0) {
                startingPoint = i;
                break;
            }
        }
        this.startingPoint = startingPoint;
    }

    public MonteCarlo(DiscreteProbabilisticDistribution distribution) {
        this(distribution,new Random());
    }

    /**
     * Get the distribution used by this monte carlo instance.
     * 
     * @return this monte carlo's distribution.
     */
    public DiscreteProbabilisticDistribution getDistribution() {
        return distribution;
    }

    /**
     * Get the number of elements (choices) in this instance's distribution.
     *
     * @return the length of this instance's distribution.
     */
    public int getDistributionSize() {
        return distribution.getLength();
    }

    /**
     * Get the normalized distribution for this instance. The value in each cell is the proportion that the cell's index
     * will be returned from a call to {@code draw()} if an infinite number of draws were performed.
     *
     * @return this instance's normalized distribution.
     */
    public double[] getNormalizedDistribution() {
        return distribution.getDistribution();
    }

    /**
     * Make a random draw. The returned value will be between {@code 0} (inclusive) and {@code getDistributionSize()} (exclusive),
     * and will be based on the distribution specified when construction this instance.
     *
     * @return a Monte Carlo choice from this instance's distribution.
     */
    public int draw() {
        double d = random.nextDouble();
        for (int i : range(startingPoint,getDistributionSize()))
            if (d <= distribution.getCumulativeValue(i))
                return i;
        //should never get here...
        throw new IllegalStateException(String.format("Cant' draw for random number %f with distribution:\n    %s",d,Arrays.toString(distribution.getCumulativeDistribution())));
    }

    /**
     * Make a draw excluding a set of choices. This can be used to create drawing "without replacement" behavior by feeding
     * to this method those choices which have already been drawn.
     *
     * @param usedChoices
     *        The choices which have already been used.
     *
     * @return a Monte Carlo choice from this instance's distribution, excluding {@code usedChoices}.
     *
     * @throws IllegalArgumentException if all of the choices in this instance are contained in {@code usedChoices}, or if
     *                                  any of the choices in {@code usedChoices} is not a valid choice for this Monte Carlo
     *                                  instance.
     */
    public int drawWithoutReplacement(Set<Integer> usedChoices) {
        return removeChoices(usedChoices).draw();
    }

    /**
     * Get a {@code MonteCarlo} instance equivalent to this instance with a specified choice removed.
     *
     * @param choice
     *        The choice to remove.
     *
     * @return a new {@code MonteCarlo} with {@code choice} removed. Note that the returned instance will have a distribution
     *         length equal to this one, only with the removed choice's probability set to <code>0.0</code>.
     *
     * @throws IllegalArgumentException if {@code choice} is not a choice in this instance.
     */
    public MonteCarlo removeChoice(int choice) {
        Set<Integer> r = new HashSet<>();
        r.add(choice);
        return removeChoices(r);
    }

    /**
     * Get a {@code MonteCarlo} instance equivalent to this instance with a set of specified choices removed.
     *
     * @param choices
     *        The choices to remove.
     *
     * @return a new {@code MonteCarlo} with {@code choices} removed. Note that the returned instance will have a distribution
     *         length equal to this one, only with the removed choices's probabilities set to <code>0.0</code>.
     *
     * @throws IllegalArgumentException if any choice in {@code choices} is not a choice in this instance.
     */

    public MonteCarlo removeChoices(Set<Integer> choices) {
        return new MonteCarlo((this.distribution instanceof AdjustingDiscreteProbabilisticDistribution) ? (AdjustingDiscreteProbabilisticDistribution) this.distribution : new AdjustingDiscreteProbabilisticDistribution(this.distribution.getCumulativeDistribution()).removePoints(choices),random);
    }

    /**
     * Convenience method to create a randomized selection (with replacement) of objects from a distribution.
     *
     * @param distribution
     *        The basis distribution. The keys are the choices, and the values are their relative weights.
     *
     * @param numberOfDraws
     *        Number of draws to perform. The sum of the returned values will equal this number.
     *
     * @param random
     *        The random number generator to use to perform the draws.
     *
     * @param <T>
     *        The type of the objects for which the sample is being built.
     *
     * @return a mapping from each object (key) in {@code distribution} to the number of times it was placed in the random
     *         sample.
     *
     * @throws IllegalArgumentException if any value in {@code distribution} is less than zero, or if all values in {@code distribution}
     *                                  are equal to zero.
     */
    public static <T> Map<T,Integer> chooseMonteCarlo(Map<T,Double> distribution, int numberOfDraws, Random random) {
        Map<T,Integer> result = new HashMap<>();
        @SuppressWarnings("unchecked") //this is correct, in spirit
        T[] objects = (T[]) new Object[distribution.size()];
        double[] dist = new double[distribution.size()];
        int[] res = new int[dist.length];
        int counter = 0;
        for (T t : distribution.keySet()) {
            objects[counter] = t;
            dist[counter++] = distribution.get(t);
            result.put(t,0); //initialize, in case we don't draw any of these
        }
        MonteCarlo monteCarlo = getMonteCarloFromDistribution(dist,random);
        for (int i : range(numberOfDraws))
            res[monteCarlo.draw()]++;
        for (int i : range(res.length))
            result.put(objects[i],res[i]);
        return result;
    }

    /**
     * Convenience method to create a randomized selection (with replacement) of objects from a distribution. A default
     * random-number generator will be used to build the sample.
     *
     * @param distribution
     *        The basis distribution. The keys are the choices, and the values are their relative weights.
     *
     * @param numberOfDraws
     *        Number of draws to perform. The sum of the returned values will equal this number.
     *
     * @param <T>
     *        The type of the objects for which the sample is being built.
     *
     * @return a mapping from each object (key) in {@code distribution} to the number of times it was placed in the random
     *         sample.
     *
     * @throws IllegalArgumentException if any value in {@code distribution} is less than zero, or if all values in {@code distribution}
     *                                  are equal to zero.
     */
    public static <T> Map<T,Integer> chooseMonteCarlo(Map<T,Double> distribution, int numberOfDraws) {
        return chooseMonteCarlo(distribution,numberOfDraws,new Random());
    }


    /**
     * Factory method specifying the distribution and random number generator. The distribution need only be <i>relative</i>
     * (that is, it does not need to sum to one), though its contents must all be non-negative and must sum to a non-zero
     * value.
     *
     * @param distribution
     *        A list of values specifying the relative distribution.
     *
     * @param random
     *        The random number generator to use when performing draws.
     *
     * @throws IllegalArgumentException if any value in {@code distribution} is less than zero, or if all values in {@code distribution}
     *                                  are equal to zero.
     */
    public static MonteCarlo getMonteCarloFromDistribution(double[] distribution, Random random) {
        return new MonteCarlo(new SimpleDiscreteProbabilisticDistribution(distribution),random);
    }

    /**
     * Factory method specifying the distribution and random number generator seed. The distribution need only be <i>relative</i>
     * (that is, it does not need to sum to one), though its contents must all be non-negative and must sum to a non-zero
     * value.
     *
     * @param distribution
     *        A list of values specifying the relative distribution.
     *
     * @param randomSeed
     *        The seed to use for the random number generator used when performing draws.
     *
     * @throws IllegalArgumentException if any value in {@code distribution} is less than zero, or if all values in {@code distribution}
     *                                  are equal to zero.
     */
    public static MonteCarlo getMonteCarloFromDistribution(double[] distribution, long randomSeed) {
        return getMonteCarloFromDistribution(distribution,new Random(randomSeed));
    }

    /**
     * Factory method specifying the distribution. The distribution need only be <i>relative</i> (that is, it does not need
     * to sum to one), though its contents must all be non-negative and must sum to a non-zero value. A default random
     * number generator will be used for draws.
     *
     * @param distribution
     *        A list of values specifying the relative distribution.
     *
     * @throws IllegalArgumentException if any value in {@code distribution} is less than zero, or if all values in {@code distribution}
     *                                  are equal to zero.
     */
    public static MonteCarlo getMonteCarloFromDistribution(double[] distribution) {
        return getMonteCarloFromDistribution(distribution,new Random());
    }

    /**
     * Factory method specifying a uniform distribution with a specified length and random number generator.
     *
     * @param length
     *        The number of elements in the distribution.
     *
     * @param random
     *        The random number generator to use when performing draws.
     *
     * @throws NegativeArraySizeException if {@code length} is less than zero.
     */
    public static MonteCarlo getUniformMonteCarlo(int length, Random random) {
        return getMonteCarloFromDistribution(getUniformDist(length),random);
    }

    /**
     * Factory method specifying a uniform distribution with a specified length and a random number generator seed.
     *
     * @param length
     *        The number of elements in the distribution.
     *
     * @param randomSeed
     *        The seed to use for the random number generator used when performing draws.
     *
     * @throws NegativeArraySizeException if {@code length} is less than zero.
     */
    public static MonteCarlo getUniformMonteCarlo(int length, int randomSeed) {
        return getMonteCarloFromDistribution(getUniformDist(length),randomSeed);
    }

    /**
     * Factory method specifying a uniform distribution with a specified length. A default random number generator will be used.
     *
     * @param length
     *        The number of elements in the distribution.
     *
     * @throws NegativeArraySizeException if {@code length} is less than zero.
     */
    public static MonteCarlo getUniformMonteCarlo(int length) {
        return getMonteCarloFromDistribution(getUniformDist(length));
    }

    private static double[] getUniformDist(int length) {
        double[] dist = new double[length];
        Arrays.fill(dist,1.0);
        return dist;
    }
}
