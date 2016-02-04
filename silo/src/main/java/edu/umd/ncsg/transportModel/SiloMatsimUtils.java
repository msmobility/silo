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
	
	
	public static final Matrix convertTravelTimesToImpedanceMatrix(
			Map<Tuple<Integer, Integer>, Float> travelTimesMap, int year) {
		log.info("Converting MATSim travel times to impedance matrix for " + year + ".");
		String name = "travelTimeMatrix";
		String description = name;
		int[] zones = geoData.getZones();
		
		Matrix matrix = new Matrix(name, description, determineMax(zones), determineMax(zones));
		
//		CSVFileWriter matrixFileWriter = new CSVFileWriter("./info/matsimmatrix_" + year +".csv", "\t");
//		
//		matrixFileWriter.writeField("originZoneId");
//		matrixFileWriter.writeField("destinationZoneId");
//		matrixFileWriter.writeField("travelTime");
//		matrixFileWriter.writeNewLine();

		// Do not just increment by 1! Some values are missing. So, do not confuse the array index with the array entry!
		for (int i = 0; i < zones.length; i++) {
			int originFipsPuma5 = geoData.getPUMAofZone(zones[i]);

			for (int j = 0; j < zones.length; j++) {
				int destinationFipsPuma5 = geoData.getPUMAofZone(zones[j]);

				Tuple<Integer, Integer> zone2Zone = new Tuple<Integer, Integer>(originFipsPuma5, destinationFipsPuma5);
				matrix.setValueAt(zones[i], zones[j], travelTimesMap.get(zone2Zone));
//				System.out.println("origin = " + zones[i] + " ; destination = " + zones[j] + " ; " + zone2Zone + " ; travel time = " + travelTimesMap.get(zone2Zone));
			
//				matrixFileWriter.writeField(zones[i]);
//				matrixFileWriter.writeField(zones[j]);
//				matrixFileWriter.writeField(travelTimesMap.get(zone2Zone));
//				matrixFileWriter.writeNewLine();
			}
		}
//		matrixFileWriter.close();
		
		return matrix;
	}
	
	
	/**
	 * Not 100% sure that the values are always in ascending order. So, rather compute the zone array's
	 * maximum like this instead of just taking the last value and assuming it is the maximum
	 */
	private static int determineMax(int[] array){
		int max = Integer.MIN_VALUE;
		for(int i = 0; i < array.length; i++){
			if (array[i] > max){
				max = array[i];
			}
		}
		return max;
	}
}