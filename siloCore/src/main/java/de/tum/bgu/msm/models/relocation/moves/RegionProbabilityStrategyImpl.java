package de.tum.bgu.msm.models.relocation.moves;

public class RegionProbabilityStrategyImpl implements RegionProbabilityStrategy {

    private final double beta;

    public RegionProbabilityStrategyImpl(double beta) {
        this.beta = beta;
    }

    public RegionProbabilityStrategyImpl() {
        this(0.5);
    }

    @Override
    public double calculateSelectRegionProbability(double util) {
        return Math.exp(beta * util);
    }
}
