package de.tum.bgu.msm.models.realEstate.pricing;

import de.tum.bgu.msm.data.dwelling.Dwelling;

public interface PricingStrategy {

    boolean isPriceUpdateAllowed(Dwelling dd);

    double getPriceChangeRate(double vacancyRateAtThisRegion, double structuralVacancyRate);
}
