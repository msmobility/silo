package de.tum.bgu.msm.utils;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.matrices.Matrices;
import org.matsim.api.core.v01.TransportMode;

import java.util.Collection;

public class TravelTimeUtil {

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

    public static DoubleMatrix2D getPeakTravelTimeMatrix(String mode, TravelTimes travelTimes, Collection<Zone> zones) {
        if (travelTimes instanceof SkimTravelTimes) {
            return ((SkimTravelTimes) travelTimes).getMatrixForMode(mode);
        }
        // The following lines can go once the skim-based case (above) remains the only in this method
        // MATSim-based accessibilities will be provided "directly", nk/dz, july'18
        final DoubleMatrix2D matrix = Matrices.doubleMatrix2D(zones, zones);
        for (Zone origin : zones) {
            for (Zone destination : zones) {
                matrix.setQuick(origin.getZoneId(), destination.getZoneId(), travelTimes.getTravelTime(origin, destination, TIME_OF_DAY, mode));
            }
        }
        return matrix;
    }
}
