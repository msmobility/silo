package de.tum.bgu.msm.transportModel;

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

/**
 * @author dziemke
 */
public class SiloMatsimUtils {
	private final static Logger log = Logger.getLogger(SiloMatsimUtils.class);
	
	private final static GeometryFactory geometryFactory = new GeometryFactory();

	public static final Coord getRandomCoordinateInGeometry(SimpleFeature feature, Random random) {
		Geometry geometry = (Geometry) feature.getDefaultGeometry();
		Envelope envelope = geometry.getEnvelopeInternal();
		while (true) {
			Point point = getRandomPointInEnvelope(envelope, random);
			if (point.within(geometry)) {
				return new Coord(point.getX(), point.getY());
			}
		}
	}
	
	
	public static final Point getRandomPointInEnvelope(Envelope envelope, Random random) {
		double x = envelope.getMinX() + random.nextDouble() * envelope.getWidth();
		double y = envelope.getMinY() + random.nextDouble() * envelope.getHeight();
		return geometryFactory.createPoint(new Coordinate(x,y));
	}
	
	
	public static final Matrix convertTravelTimesToImpedanceMatrix(
			Map<Tuple<Integer, Integer>, Float> travelTimesMap, int rowCount, int columnCount, int year) {
		log.info("Converting MATSim travel times to impedance matrix for " + year + ".");
		String name = "travelTimeMatrix";
		String description = name;
		
		Matrix matrix = new Matrix(name, description, rowCount, columnCount);

		// Do not just increment by 1! Some values are missing. So, do not confuse the array index with the array entry!
		for (int i = 1; i <= rowCount; i++) {
			for (int j = 1; j <= columnCount; j++) {
//				Tuple<Integer, Integer> zone2Zone = new Tuple<Integer, Integer>(zones[i], zones[j]);
//				matrix.setValueAt(zones[i], zones[j], travelTimesMap.get(zone2Zone));
				
				Tuple<Integer, Integer> zone2Zone = new Tuple<Integer, Integer>(i, j);
				if (travelTimesMap.containsKey(zone2Zone)) {
					matrix.setValueAt(i, j, travelTimesMap.get(zone2Zone));
				} else {
					matrix.setValueAt(i, j, 0.f);
				}
			}
		}	
		return matrix;
	}
}