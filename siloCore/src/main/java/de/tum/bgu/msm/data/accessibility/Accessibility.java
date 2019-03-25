package de.tum.bgu.msm.data.accessibility;

import de.tum.bgu.msm.models.ModelUpdateListener;

public interface Accessibility extends ModelUpdateListener {
    float getCommutingTimeProbability(int minutes);

    double getAutoAccessibilityForZone(int zone);

    double getTransitAccessibilityForZone(int zoneId);

    double getRegionalAccessibility(int region);
}
