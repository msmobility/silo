package de.tum.bgu.msm.models.relocation.moves;

public class DefaultDwellingProbabilityStrategy implements DwellingProbabilityStrategy {

    /**
     * This is the scale factor for the exponential function. Higher values will make differences
     * in utility have a stronger impact on the probability. Lower values will cause the probabilities
     * to be more similar to each other.
     */
    private final double beta;

    public DefaultDwellingProbabilityStrategy(double beta) {
        this.beta = beta;
    }

    public DefaultDwellingProbabilityStrategy() {
        this(0.5);
    }

    public double calculateSelectDwellingProbability(double dwellingUtility) {
        return Math.exp(beta * dwellingUtility);
    }
}
