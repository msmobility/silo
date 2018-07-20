package de.tum.bgu.msm.data;

import org.opengis.feature.simple.SimpleFeature;

public interface Zone extends Id, Location {

    void setRegion(Region region);

    Region getRegion();

    /**
     * @return the zone's metropolitan statistical area
     */
    int getMsa();

    /**
     * @return the area of the zone in acres
     */
    float getArea();

	void setZoneFeature(SimpleFeature zoneFeature);

	SimpleFeature getZoneFeature();

	MicroLocation getRandomMicroLocation();

}
