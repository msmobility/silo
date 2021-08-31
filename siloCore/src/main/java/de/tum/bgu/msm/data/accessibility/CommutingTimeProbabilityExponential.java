package de.tum.bgu.msm.data.accessibility;

import org.matsim.api.core.v01.TransportMode;

public class CommutingTimeProbabilityExponential implements CommutingTimeProbability {


    private final float betaPt;
    private final float betaCar;

    public CommutingTimeProbabilityExponential(float betaCar, float betaPt) {
        this.betaCar = betaCar;
        this.betaPt  =betaPt;
    }


    @Override
    public float getCommutingTimeProbability(int minutes, String mode) {
        if (mode.equals(TransportMode.pt)){
            return (float) Math.exp(betaPt * minutes);
        } else {
            return (float) Math.exp(betaCar * minutes);
        }
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
