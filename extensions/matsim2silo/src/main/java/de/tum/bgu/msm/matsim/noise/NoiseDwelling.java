package de.tum.bgu.msm.matsim.noise;

import de.tum.bgu.msm.data.dwelling.Dwelling;

public interface NoiseDwelling extends Dwelling {

    void setNoiseImmision(double lden);

    double getNoiseImmission();

}
