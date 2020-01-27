package de.tum.bgu.msm;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.matsim.core.utils.gis.ShapeFileWriter;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.util.*;

/**
 * @author dziemke
 */
public class CreateShapefile {
    public static void main(String[] args) {
        String shapeFileName = "useCases/fabiland/input/base/input/zonesShapefile/fabiland-full.shp";

        GeometryFactory geometryFactory = new GeometryFactory();

        Map<Integer, Polygon> polygons = new HashMap<>();

        int sideLengthHorizonal = 5000;
        int sideLengthVertical = 5000;
        int centroidOffset = 5000;
        int id = 1;

        int centerY = 10000;
        for (int y = 1; y <=5; y++) {
            int centerX = -10000;
            for (int x = 1; x <=5; x++) {
                Coordinate coordSW = new Coordinate(centerX - sideLengthHorizonal/2, centerY - sideLengthVertical/2);
                Coordinate coordSE = new Coordinate(centerX + sideLengthHorizonal/2, centerY - sideLengthVertical/2);
                Coordinate coordNE = new Coordinate(centerX + sideLengthHorizonal/2, centerY + sideLengthVertical/2);
                Coordinate coordNW = new Coordinate(centerX - sideLengthHorizonal/2, centerY + sideLengthVertical/2);
                Coordinate[] squareCoordinates = new Coordinate[]{coordSW, coordSE, coordNE, coordNW, coordSW};

                Polygon square = geometryFactory.createPolygon(squareCoordinates);
                polygons.put(id, square);

                id++;
                centerX = centerX + centroidOffset;
            }
            centerY = centerY - centroidOffset;
        }

        Collection<SimpleFeature> features = createFeaturesFromPolygons(polygons);
        ShapeFileWriter.writeGeometries(features, shapeFileName);
    }

    static Collection<SimpleFeature> createFeaturesFromPolygons(Map<Integer, Polygon> polygons) {
        Collection<SimpleFeature> features = new LinkedList<>();

        SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();

        b.setSRS( "EPSG:31468" );
        b.setName("fabiland");
        b.add("the_geom", Polygon.class); // Fails if it is not called "the_geom"
        b.add("Id", Integer.class);
        SimpleFeatureType type = b.buildFeatureType();
        SimpleFeatureBuilder fbuilder = new SimpleFeatureBuilder(type);

        for (Integer id : polygons.keySet()) {
            fbuilder.add(polygons.get(id));
            SimpleFeature feature = fbuilder.buildFeature(id.toString());
            feature.setAttribute("Id", id);
            features.add(feature);
        }
        return features;
    }
}