package de.tum.bgu.msm.utils;

import cern.colt.Arrays;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

/**
 * @author Nico
 */
public final class Sampler<T> {

    private double sum;
    private int index = 0;

    private final T[] objects;
    private final double[] probabilities;

    private final Random random;

    public static <T> Sampler<T> getEvenlyDistributedSampler(T[] objects, double probability) {
            double[] probabilities = new double[objects.length];
            java.util.Arrays.fill(probabilities, probability);
            return new Sampler<>(objects, probabilities);
    }

    public static <T> Sampler<T> getEvenlyDistributedSampler(List<T> objects, double probability) {
        double[] probabilities = new double[objects.size()];
        java.util.Arrays.fill(probabilities, probability);
        return new Sampler(objects.toArray(), probabilities);
    }

    public Sampler(int expected, Class<T> klass) {
        this(expected, klass, new Random());
    }

    public Sampler(T[] objects, double[] probabilities) {
        this(objects, probabilities, new Random());
    }

    public Sampler(List<T> objects, double[] probabilities) {
        this((T[]) objects.toArray(), probabilities, new Random());
    }

    public Sampler(int expected, Class<T> klass, Random random) {
        objects = (T[]) Array.newInstance(klass, expected);
        probabilities = new double[expected];
        this.random = random;
    }

    public Sampler(List<T> objects, double[] probabilities, Random random) {
        this((T[]) objects.toArray(), probabilities, random);
    }

    public Sampler(T[] objects, double[] probabilities, Random random) {
        this.objects = objects;
        this.probabilities = probabilities;
        this.random = random;
        for(double d: probabilities) {
            this.sum += d;
        }
    }

    public synchronized void incrementalAdd(T object, double probability) {
        objects[index] = object;
        probabilities[index] = probability;
        index++;
        sum += probability;
    }

    public void updateProbabilities(BiFunction<T,Double,Double> function) {
        sum = 0;
        for (int i = 0; i < objects.length; i++) {
            sum += probabilities[i] = function.apply(objects[i], probabilities[i]);
        }
    }


    public synchronized T sampleObject() throws SampleException {
        double selPos = sum * random.nextDouble();
        double sum = 0;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            if (sum > selPos) {
                return objects[i];
            }
        }
        throw new SampleException("Could not sampleObject an object from \n"
                + Arrays.toString(objects) + "\n with  probabilities \n"
                + Arrays.toString(probabilities) + "\n sum of probs: " + sum);
    }

    public int sampleIndex() throws SampleException {
        double selPos = sum * random.nextDouble();
        double sum = 0;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            if (sum > selPos) {
                return i;
            }
        }
        throw new SampleException("Could not sampleObject an object from \n"
                + Arrays.toString(objects) + "\n with  probabilities \n"
                + Arrays.toString(probabilities) + "\n sum of probs: " + sum);
    }

    public double getCumulatedProbability() {
        return sum;
    }
}
