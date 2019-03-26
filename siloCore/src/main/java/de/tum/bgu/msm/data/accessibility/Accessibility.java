package de.tum.bgu.msm.data.accessibility;

import de.tum.bgu.msm.models.ModelUpdateListener;

public interface Accessibility extends ModelUpdateListener {
    double getAutoAccessibilityForZone(int zone);

    double getTransitAccessibilityForZone(int zoneId);

    double getRegionalAccessibility(int region);
}
