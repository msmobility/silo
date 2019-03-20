package de.tum.bgu.msm.utils;

import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix2D;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;

import java.util.Collection;

public class TravelTimeUtil {

    private final static Logger logger = Logger.getLogger(TravelTimeUtil.class);

    private static final double TIME_OF_DAY = 8 * 60. * 60.;

    public static void updateTransitSkim(SkimTravelTimes travelTimes, int year, Properties properties) {
        final String transitSkimFile = properties.accessibility.transitSkimFile(year);
        travelTimes.readSkim(TransportMode.pt, transitSkimFile,
                properties.accessibility.transitPeakSkim, properties.accessibility.skimFileFactorTransit);
    }

    public static void updateCarSkim(SkimTravelTimes travelTimes, int year, Properties properties) {
        final String carSkimFile = properties.accessibility.autoSkimFile(year);
        travelTimes.readSkim(TransportMode.car, carSkimFile,
                properties.accessibility.autoPeakSkim, properties.accessibility.skimFileFactorCar);
    }

    public static IndexedDoubleMatrix2D getPeakTravelTimeMatrix(String mode, TravelTimes travelTimes, Collection<Zone> zones) {
        if(zones == null || zones.isEmpty()) {
            logger.warn("No zones specified for travel time matrix.");
            throw new IllegalArgumentException("No zones specified for travel time matrix.");
        }
        final IndexedDoubleMatrix2D matrix = new IndexedDoubleMatrix2D(zones, zones);
        if (travelTimes instanceof SkimTravelTimes) {
            return matrix.assign(((SkimTravelTimes) travelTimes).getMatrixForMode(mode).viewPart(0,0, matrix.rows(), matrix.columns()));
        }

        for (Zone origin : zones) {
            for (Zone destination : zones) {
                matrix.setIndexed(origin.getZoneId(), destination.getZoneId(), travelTimes.getTravelTime(origin, destination, TIME_OF_DAY, mode));
            }
        }
        return matrix;
    }
}
