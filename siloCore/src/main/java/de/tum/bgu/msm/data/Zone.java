package de.tum.bgu.msm.data;

import de.tum.bgu.msm.data.development.Development;
import org.geotools.api.feature.simple.SimpleFeature;
import org.locationtech.jts.geom.Coordinate;

import java.util.Map;
import java.util.Random;

public interface Zone extends Location, Id {

    Region getRegion();

    /**
     * @return the area of the zone in acres
     */
    float getArea_sqmi();

	void setZoneFeature(SimpleFeature zoneFeature);

	SimpleFeature getZoneFeature();

	Coordinate getRandomCoordinate(Random random);

	Development getDevelopment();

    void setDevelopment(Development development);

    Map<String, Object> getAttributes();

    Coordinate getPopCentroidCoord();
}
