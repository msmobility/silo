package de.tum.bgu.msm.health.noise;

import de.tum.bgu.msm.data.dwelling.Dwelling;

public interface NoiseDwelling extends Dwelling {

    void setNoiseImmision(double lden);

    double getNoiseImmission();
}
