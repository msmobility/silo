package de.tum.bgu.msm.models.realEstate.pricing;

public interface PricingStrategy {
    double getLowInflectionPoint();

    double getHighInflectionPoint();

    double getLowerSlope();

    double getMainSlope();

    double getHighSlope();

    double getMaximumChange();
}
