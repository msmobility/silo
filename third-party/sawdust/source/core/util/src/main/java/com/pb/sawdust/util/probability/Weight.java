package com.pb.sawdust.util.probability;

/**
 * The {@code Weight} class serves as a mutable container for a {@code double} weight. It provides a means for sharing
 * the primitive weight across objects and threads in such a way that its state (and changes) can be shared properly.
 *
 * @author crf
 *         Started 9/28/11 6:54 AM
 */
public class Weight {
    private volatile double weight;

    /**
     * Constructor setting the initial weight value.
     *
     * @param weight
     *        The initial weight value.
     */
    public Weight(double weight) {
        this.weight = weight;
    }

    /**
     * Get this weight's current value.
     *
     * @return the value of this weight.
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Set this weight's current value.
     *
     * @param weight
     *        The value for the weight.
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String toString() {
        return "Weight: " + weight;
    }
}
