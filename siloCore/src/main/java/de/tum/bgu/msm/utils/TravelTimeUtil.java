package de.tum.bgu.msm.utils;

import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;

public class TravelTimeUtil {

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
}
