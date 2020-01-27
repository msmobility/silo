package data;

import de.tum.bgu.msm.data.accessibility.CommutingTimeProbability;

public class OnTheFlyCommutingTimeProbability implements CommutingTimeProbability {

    public final static double DEFAULT_BETA = -0.2;

    private final double beta;

    public OnTheFlyCommutingTimeProbability() {
        this.beta = DEFAULT_BETA;
    }

    public OnTheFlyCommutingTimeProbability(double beta) {
        this.beta = beta;
    }

    @Override
    public float getCommutingTimeProbability(int minutes, String mode) {
        return (float) Math.exp(beta * minutes);
    }

    @Override
    public void setup() {

    }

    @Override
    public void prepareYear(int year) {

    }

    @Override
    public void endYear(int year) {

    }

    @Override
    public void endSimulation() {

    }
}
