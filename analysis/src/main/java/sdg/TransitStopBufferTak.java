package sdg;

import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingDataImpl;
import de.tum.bgu.msm.data.geo.DefaultGeoData;
import de.tum.bgu.msm.io.DwellingReaderTak;
import de.tum.bgu.msm.io.GeoDataReaderTak;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateFilter;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.core.utils.collections.QuadTree;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.matsim.pt.transitSchedule.TransitScheduleFactoryImpl;
import org.matsim.pt.transitSchedule.api.TransitStopFacility;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.util.HashSet;
import java.util.Set;

public class TransitStopBufferTak {

    private static String[] scenarios = {
            "D:\\silo_kagawa\\microData\\dd_2010.csv",
            "Z:\\projects\\2018\\DAAD Japan\\Scenarios\\kgw\\base_scenario\\microData\\dd_2050.csv",
            "Z:\\projects\\2018\\DAAD Japan\\Scenarios\\kgw\\draconic\\microData\\dd_2050.csv",
            "Z:\\projects\\2018\\DAAD Japan\\Scenarios\\kgw\\oneCar\\microData\\dd_2050.csv",
            "Z:\\projects\\2018\\DAAD Japan\\Scenarios\\kgw\\scenario_1\\microData\\dd_2050.csv",
            "Z:\\projects\\2018\\DAAD Japan\\Scenarios\\kgw\\scenario_2\\microData\\dd_2050.csv",
            "Z:\\projects\\2018\\DAAD Japan\\Scenarios\\kgw\\scenario_3\\microData\\dd_2050.csv",
            "Z:\\projects\\2018\\DAAD Japan\\Scenarios\\kgw\\scenario_4\\microData\\dd_2050.csv"
    };

    public static void main(String[] args) throws TransformException, FactoryException {

        int total = 0;
        int access = 0;

        Set<TransitStopFacility> stops = new HashSet<>();
        TransitScheduleFactoryImpl factory = new TransitScheduleFactoryImpl();

        CoordinateReferenceSystem wgs84 = CRS.decode("EPSG:4326");
        CoordinateReferenceSystem utm53 = CRS.decode("EPSG:6690");
        MathTransform transform = CRS.findMathTransform(wgs84, utm53, true);

        for (SimpleFeature feature : ShapeFileReader.getAllFeatures("Z:\\projects\\2018\\DAAD Japan\\Scenarios\\kgw\\transitStopsShape\\shape\\busStop.shp")) {
            final Point defaultGeometry = (Point) feature.getDefaultGeometry();
            defaultGeometry.apply(new InvertCoordinateFilter());
            final Point transformed = (Point) JTS.transform(defaultGeometry, transform);
            stops.add(factory.createTransitStopFacility(Id.create(feature.getID(), TransitStopFacility.class), new Coord(transformed.getX(), transformed.getY()), false));
        }

        for (SimpleFeature feature : ShapeFileReader.getAllFeatures("Z:\\projects\\2018\\DAAD Japan\\Scenarios\\kgw\\transitStopsShape\\shape\\railst.shp")) {
            final Point defaultGeometry = (Point) feature.getDefaultGeometry();
            defaultGeometry.apply(new InvertCoordinateFilter());
            final Point transformed = (Point) JTS.transform(defaultGeometry, transform);
            stops.add(factory.createTransitStopFacility(Id.create(feature.getID(), TransitStopFacility.class), new Coord(transformed.getX(), transformed.getY()), false));
        }

        final DefaultGeoData geoData = new DefaultGeoData();
        GeoDataReaderTak geoDataReaderTak = new GeoDataReaderTak(geoData);
        geoDataReaderTak.readZoneCsv("Z:\\projects\\2018\\DAAD Japan\\Scenarios\\kgw\\aux_files\\zoneSystem_KGW.csv");
        geoDataReaderTak.readZoneShapefile("Z:\\projects\\2018\\DAAD Japan\\Scenarios\\kgw\\aux_files\\zones_KGW.shp");


        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for (TransitStopFacility stopFacility : stops) {
            double x = stopFacility.getCoord().getX();
            double y = stopFacility.getCoord().getY();

            if (x < minX) {
                minX = x;
            }
            if (y < minY) {
                minY = y;
            }
            if (x > maxX) {
                maxX = x;
            }
            if (y > maxY) {
                maxY = y;
            }
        }
        QuadTree<TransitStopFacility> stopsQT = new QuadTree<>(minX, minY, maxX, maxY);
        for (TransitStopFacility stopFacility : stops) {
            double x = stopFacility.getCoord().getX();
            double y = stopFacility.getCoord().getY();
            stopsQT.put(x, y, stopFacility);
        }


        for(String result: scenarios) {
            final DwellingDataImpl realEstate = new DwellingDataImpl();
            new DwellingReaderTak(realEstate).readData(result);

            for (Dwelling dwelling : realEstate.getDwellings()) {
                if (dwelling.getResidentId() > 0) {
                    total++;
                    Coordinate coordinate = dwelling.getCoordinate();
                    if(coordinate == null) {
                        coordinate = new Coordinate(((MultiPolygon)geoData.getZones().get(dwelling.getZoneId()).getZoneFeature().getDefaultGeometry()).getCentroid().getCoordinate());
                    }
                    final double distance = coordinate.distance(CoordUtils.createGeotoolsCoordinate(stopsQT.getClosest(coordinate.x, coordinate.y).getCoord()));
                    if (distance < 500) {
                        access++;
                    }
                }
            }

            System.out.println(result + "| Share: " + (double) access / (double) total);
        }
    }

    /**
     * JTS uses x and y the other way around
     */
    private static class InvertCoordinateFilter implements CoordinateFilter {
        @Override
        public void filter(Coordinate coord) {
            double oldX = coord.x;
            coord.x = coord.y;
            coord.y = oldX;
        }

    }
}
