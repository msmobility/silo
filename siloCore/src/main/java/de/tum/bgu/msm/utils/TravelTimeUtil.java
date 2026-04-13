package de.tum.bgu.msm.utils;

import de.tum.bgu.msm.data.Id;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.properties.Properties;
import org.matsim.api.core.v01.TransportMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TravelTimeUtil {

    public static void updateTransitSkim(SkimTravelTimes travelTimes, int year, Properties properties) {
        final String transitSkimFile = properties.accessibility.transitSkimFile(year);
        travelTimes.readSkim(TransportMode.pt, transitSkimFile,
                properties.accessibility.transitPeakSkim, properties.accessibility.skimFileFactorTransit);
    }

    public static void updateCarSkim(SkimTravelTimes travelTimes, int year, Properties properties) {
        final String carSkimFile = properties.accessibility.autoSkimFile(year);

        if(carSkimFile.endsWith(".gz")) {
            Collection<? extends Id> zoneLookup = new ArrayList<>();

            travelTimes.readSkimFromCsvGz(TransportMode.car, carSkimFile, properties.accessibility.skimFileFactorCar, zoneLookup);
        } else {
            travelTimes.readSkim(TransportMode.car, carSkimFile,
                    properties.accessibility.autoPeakSkim, properties.accessibility.skimFileFactorCar);

        }
    }
}
