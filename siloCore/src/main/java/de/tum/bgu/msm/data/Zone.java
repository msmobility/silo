package de.tum.bgu.msm.data;

import com.vividsolutions.jts.geom.Coordinate;
import de.tum.bgu.msm.data.Id;
import de.tum.bgu.msm.data.Location;
import de.tum.bgu.msm.data.Region;
import org.opengis.feature.simple.SimpleFeature;

public interface Zone extends Location, Id {


    Region getRegion();

    /**
     * @return the zone's metropolitan statistical area
     */
    int getMsa();

    /**
     * @return the area of the zone in acres
     */
    float getArea_sqmi();

	void setZoneFeature(SimpleFeature zoneFeature);

	SimpleFeature getZoneFeature();

	Coordinate getRandomCoordinate();

	Development getDevelopment();

    void setDevelopment(Development development);
}
