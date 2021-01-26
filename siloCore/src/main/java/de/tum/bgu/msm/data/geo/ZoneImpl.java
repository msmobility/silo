package de.tum.bgu.msm.data.geo;

import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.development.Development;
import de.tum.bgu.msm.utils.SeededRandomPointsBuilder;
import de.tum.bgu.msm.utils.SiloUtil;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.shape.random.RandomPointsBuilder;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.opengis.feature.simple.SimpleFeature;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ZoneImpl implements Zone {

    private final int id;
    private final float area_sqmi;

    private final Region region;
    
    private SimpleFeature zoneFeature;

    private Development development;

    private final Map<String, Object> attributes = new HashMap<>();

    public ZoneImpl(int id, float area_sqmi, Region region) {
        this.id = id;
        this.area_sqmi = area_sqmi;
        this.region = region;
    }

    @Override
    public int getZoneId() {
        return this.id;
    }

    @Override
    public Region getRegion() {
        return this.region;
    }

    @Override
    public float getArea_sqmi() {
        return area_sqmi;
    }

    @Override
	public SimpleFeature getZoneFeature() {
        return zoneFeature;
    }

    @Override
	public void setZoneFeature(SimpleFeature zoneFeature) {
        this.zoneFeature = zoneFeature;
    }

    @Override
	public Coordinate getRandomCoordinate(Random random) {
        //TODO:this can be optimized by using the same (static) points builder multiple times instead of recreating it
        RandomPointsBuilder randomPointsBuilder = new SeededRandomPointsBuilder(new GeometryFactory(), random);
        randomPointsBuilder.setNumPoints(1);
        randomPointsBuilder.setExtent((Geometry) zoneFeature.getDefaultGeometry());
        Coordinate coordinate = randomPointsBuilder.getGeometry().getCoordinates()[0];
        Point p = MGC.coordinate2Point(coordinate);
        return new Coordinate(p.getX(), p.getY());
    }

    @Override
    public Development getDevelopment() {
        return development;
    }

    @Override
    public void setDevelopment(Development development) {
        this.development = development;
    }

    @Override
    public int getId() {
        return getZoneId();
    }

    @Override
    public String toString() {
        return "Zone " + id + ", region=" + region.getId();
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Zone && ((Zone) o).getId() == this.id;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}