package de.tum.bgu.msm.data.munich;

import de.tum.bgu.msm.data.MicroLocation;
import de.tum.bgu.msm.data.ZoneImpl;
import org.matsim.api.core.v01.Coord;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.shape.random.RandomPointsBuilder;

public class MunichZone extends ZoneImpl {

    private final Coord coord;
    private double ptDistance;
    private SimpleFeature zoneFeature;
    public SimpleFeature getZoneFeature() {
        return zoneFeature;
    }
    public void setZoneFeature(SimpleFeature zoneFeature) {
        this.zoneFeature = zoneFeature;
    }

    public MunichZone(int id, int msa, float area, Coord coord, double initialPTDistance) {
        super(id, msa, area);
        this.coord = coord;
        this.ptDistance = initialPTDistance;
    }

    public Coord getCoord() {
        return coord;
    }

    public double getPTDistance() {
        return ptDistance;
    }

    public void setPtDistance(double ptDistance) {
        this.ptDistance = ptDistance;
    }
    
    public MicroLocation getRandomMicroLocation() {
        // alternative and about 10 times faster way to generate random point inside a geometry. Amit Dec'17
        RandomPointsBuilder randomPointsBuilder = new RandomPointsBuilder(new GeometryFactory());
        randomPointsBuilder.setNumPoints(1);
        randomPointsBuilder.setExtent((Geometry) zoneFeature.getDefaultGeometry());
        Coordinate coordinate = randomPointsBuilder.getGeometry().getCoordinates()[0];
        Point p = MGC.coordinate2Point(coordinate);
        return new MicroLocation(p.getX(), p.getY(), this);
    }
}
