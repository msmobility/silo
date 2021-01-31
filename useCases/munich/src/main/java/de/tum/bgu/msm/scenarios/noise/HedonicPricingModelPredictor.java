package de.tum.bgu.msm.scenarios.noise;

import de.tum.bgu.msm.data.dwelling.Dwelling;

public interface HedonicPricingModelPredictor {

    double predictPrice(Dwelling dwelling);
}
