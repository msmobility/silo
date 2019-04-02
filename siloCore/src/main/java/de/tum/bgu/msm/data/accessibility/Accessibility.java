package de.tum.bgu.msm.data.accessibility;

import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.models.ModelUpdateListener;

public interface Accessibility extends ModelUpdateListener {
	/*
	 * Older notes:
	 * mode and time as arguments
	 * need specification for undefined time, e.g. null
	 * undefined = some averaging?
	 * SILO needs peak-hour accessibilities
	 * need to be scaled ... DONE
	 * use Location instead of zone as argument (??)
	 */
	
	// TODO Rename this method to "updateHans..."
	void calculateHansenAccessibilities(int year);
	
    double getAutoAccessibilityForZone(Zone zone);

    double getTransitAccessibilityForZone(Zone zoneId);

    double getRegionalAccessibility(Region region);
}
