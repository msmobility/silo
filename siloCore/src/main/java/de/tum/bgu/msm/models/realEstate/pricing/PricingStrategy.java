package de.tum.bgu.msm.models.realEstate.pricing;

import de.tum.bgu.msm.data.dwelling.Dwelling;

public interface PricingStrategy {
    double getLowInflectionPoint();

    double getHighInflectionPoint();

    double getLowerSlope();

    double getMainSlope();

    double getHighSlope();

    double getMaximumChange();

    boolean shouldUpdatePrice(Dwelling dd);
}
