package edu.umd.ncsg.transportModel;

import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.utils.collections.Tuple;
import org.opengis.feature.simple.SimpleFeature;

import com.pb.common.matrix.Matrix;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import edu.umd.ncsg.data.geoData;

/**
 * @author dziemke
 */
public class SiloMatsimUtils {
	private final static Logger log = Logger.getLogger(SiloMatsimUtils.class);
	
	public final static Random random = MatsimRandom.getRandom();
	private final static GeometryFactory geometryFactory = new GeometryFactory();

	
	public static final Coord getRandomCoordinateInGeometry(SimpleFeature feature) {
		Geometry geometry = (Geometry) feature.getDefaultGeometry();
		Envelope envelope = geometry.getEnvelopeInternal();
		while (true) {
			Point point = getRandomPointInEnvelope(envelope);
			if (point.within(geometry)) {
				return new Coord(point.getX(), point.getY());
			}
		}
	}
	
	
	public static final Point getRandomPointInEnvelope(Envelope envelope) {
		double x = envelope.getMinX() + random.nextDouble() * envelope.getWidth();
		double y = envelope.getMinY() + random.nextDouble() * envelope.getHeight();
		return geometryFactory.createPoint(new Coordinate(x,y));
	}
	
	
	public static final Matrix convertTravelTimesToAccessibilityMatrix(String name, int dimensions, Map<Tuple<Integer, Integer>, Float> travelTimesMap) {
		Matrix matrix = new Matrix(name, name, dimensions, dimensions);
		for (int i = 0; i < dimensions; i++) {
			int originFipsPuma5 = geoData.getPUMAofZone(i);

			for (int j = 0; j < dimensions; j++) {
				int destinationFipsPuma5 = geoData.getPUMAofZone(j);

				Tuple<Integer, Integer> zone2Zone = new Tuple<Integer, Integer>(originFipsPuma5, destinationFipsPuma5);
				matrix.setValueAt(i + 1, j + 1, travelTimesMap.get(zone2Zone));
				System.out.println("Tuple = " + zone2Zone + " ; travel time = " + travelTimesMap.get(zone2Zone));
			}
		}
		return matrix;
	}
}