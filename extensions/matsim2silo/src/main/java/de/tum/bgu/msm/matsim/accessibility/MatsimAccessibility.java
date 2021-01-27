package de.tum.bgu.msm.matsim.accessibility;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.tum.bgu.msm.data.accessibility.Accessibility;
import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.matsim.api.core.v01.Id;
import org.matsim.contrib.accessibility.FacilityDataExchangeInterface;
import org.matsim.core.utils.collections.Tuple;
import org.matsim.facilities.ActivityFacility;

import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix1D;

/**
 * @author dziemke
 **/
public class MatsimAccessibility implements Accessibility, FacilityDataExchangeInterface {
	private static final Logger logger = Logger.getLogger(MatsimAccessibility.class);

	private final GeoData geoData;

	private Map<Tuple<ActivityFacility, Double>, Map<String,Double>> accessibilitiesMap = new HashMap<>();

	private Map<Id<ActivityFacility>, Double> autoAccessibilities = new HashMap<>();
	private Map<Id<ActivityFacility>, Double> transitAccessibilities = new HashMap<>();
	private IndexedDoubleMatrix1D regionalAccessibilities;
	
	public MatsimAccessibility(GeoData geoData) {
        this.geoData = geoData;
    }
	
	// FacilityDataExchangeInterface methods
	@Override
	public void setFacilityAccessibilities(ActivityFacility measurePoint, Double timeOfDay, String mode, double accessibility){
		if (timeOfDay == 8 * 60. * 60.) { // TODO Find better way for this check
			Tuple<ActivityFacility, Double> key = new Tuple<>(measurePoint, timeOfDay);
			if (!accessibilitiesMap.containsKey(key)) {
				Map<String,Double> accessibilitiesByMode = new HashMap<>();
				accessibilitiesMap.put(key, accessibilitiesByMode);
			}
			accessibilitiesMap.get(key).put(mode, accessibility);
		}
	}
		
	@Override
	public void finish() { }

	// Accessibility interface methods
	@Override
    public void calculateHansenAccessibilities(int year) {
		logger.info("Prepare accessibility data structure for SILO.");
		for (Tuple<ActivityFacility, Double> tuple : accessibilitiesMap.keySet()) {
			if (tuple.getSecond() == 8 * 60. * 60.) { // TODO Need to make this more flexible
				autoAccessibilities.put(tuple.getFirst().getId(), accessibilitiesMap.get(tuple).get("freespeed"));
			}
		}
		logger.info("Scaling zone accessibilities");
        scaleAccessibility(autoAccessibilities);
        scaleAccessibility(transitAccessibilities);

        logger.info("Calculating regional accessibilities");
        regionalAccessibilities.assign(calculateRegionalAccessibility(geoData.getRegions().values(), autoAccessibilities));
    }
	
    @Override
    public double getAutoAccessibilityForZone(Zone zone) {
    	Id<ActivityFacility> afId = Id.create(zone.getId(), ActivityFacility.class);
		double autoAccessibility = autoAccessibilities.get(afId);
    	// logger.info("Auto accessibility of zone " + zone + " is " + autoAccessibility);
		return autoAccessibility;
    }
    
    @Override
    public double getTransitAccessibilityForZone(Zone zone) {
    	// logger.warn("Transit accessibilities not yet properly implemented.");
    	//
    	// TODO Remove the quick fix (using auto instead of pt accessibilities...)
    	return autoAccessibilities.get(Id.create(zone.getId(), ActivityFacility.class)); // TODO Put transit accessibilities here!
    	//
    }

    @Override
    public double getRegionalAccessibility(Region region) {
    	return regionalAccessibilities.getIndexed(region.getId());
    }
    
    // Other methods
    private static void scaleAccessibility(Map<Id<ActivityFacility>, Double> accessibility) {
		double highestAccessibility = Double.MIN_VALUE; // TODO Rather use minus infinity
		for (double value : accessibility.values()) {
			if (value > highestAccessibility) {
				highestAccessibility = value;
			}
		}
        final double scaleFactor = 100.0 / highestAccessibility;
        for (Id<ActivityFacility> measurePointId : accessibility.keySet()) {
        	accessibility.put(measurePointId, accessibility.get(measurePointId) * scaleFactor);
        }
    }
	
	private static IndexedDoubleMatrix1D calculateRegionalAccessibility(Collection<Region> regions, Map<Id<ActivityFacility>, Double> autoAccessibilities) {
		final IndexedDoubleMatrix1D matrix = new IndexedDoubleMatrix1D(regions);
        for (Region region : regions) {
        	double regionalAccessibilitySum = 0.;
        	for (Zone zone : region.getZones()) {
        		Id<ActivityFacility> measurePointId = Id.create(zone.getId(), ActivityFacility.class);
        		regionalAccessibilitySum = regionalAccessibilitySum + autoAccessibilities.get(measurePointId);
        	}
        	matrix.setIndexed(region.getId(), regionalAccessibilitySum / region.getZones().size());
        }
        return matrix;
    }

	@Override
	public void setup() {
        this.regionalAccessibilities = new IndexedDoubleMatrix1D(geoData.getRegions().values());
	}

	@Override
	public void prepareYear(int year) {
		Log.warn("Preparing year in accessibilities.");
        calculateHansenAccessibilities(year);		
	}

	@Override
	public void endYear(int year) { }

	@Override
	public void endSimulation() { }
}