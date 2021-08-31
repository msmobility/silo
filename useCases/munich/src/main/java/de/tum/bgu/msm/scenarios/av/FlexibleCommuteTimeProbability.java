package de.tum.bgu.msm.scenarios.av;

import de.tum.bgu.msm.data.accessibility.CommutingTimeProbability;
import de.tum.bgu.msm.models.ModelUpdateListener;

public class FlexibleCommuteTimeProbability implements CommutingTimeProbability {
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

    @Override
    public float getCommutingTimeProbability(int minutes, String mode) {
        if (mode.equalsIgnoreCase("av")){
            return (float) Math.exp(-0.0025 * minutes);
        } else {
            return (float) Math.exp(-0.01 * minutes);
        }
    }
}
