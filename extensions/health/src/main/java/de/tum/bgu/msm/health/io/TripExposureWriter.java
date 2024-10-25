package de.tum.bgu.msm.health.io;

import cern.colt.map.tfloat.OpenIntFloatHashMap;
import de.tum.bgu.msm.data.Day;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.health.data.DataContainerHealth;
import de.tum.bgu.msm.health.data.LinkInfo;
import de.tum.bgu.msm.health.data.Trip;
import de.tum.bgu.msm.util.MitoUtil;
import org.apache.log4j.Logger;
import org.matsim.contrib.emissions.Pollutant;

import java.io.PrintWriter;
import java.util.Map;

public class TripExposureWriter {

    private final static Logger logger = Logger.getLogger(TripExposureWriter.class);
    public void writeMitoTrips(Map<Integer, Trip> mitoTrips, String path) {
        logger.info("  Writing trip health indicators file");
        PrintWriter pwh = MitoUtil.openFileForSequentialWriting(path, false);
        pwh.println("t.id,t.mode,t.matsimTravelTime_s,t.matsimTravelDistance_m,t.activityDuration_min," +
                "t.mmetHours,t.lightInjuryRisk,t.severeInjuryRisk,t.fatalityRisk," +
                "t.links,t.avgConcentrationPm25,t.avgConcentrationNo2," +
                "t.exposurePm25,t.exposureNo2,t.activityExposurePm25,t.activityExposureNo2");

        for (Trip trip : mitoTrips.values()) {
            pwh.print(trip.getId());
            pwh.print(",");
            pwh.print(trip.getTripMode().toString());
            pwh.print(",");
            pwh.print(trip.getMatsimTravelTime());
            pwh.print(",");
            pwh.print(trip.getMatsimTravelDistance());
            pwh.print(",");
            pwh.print(trip.getActivityDuration());
            pwh.print(",");
            pwh.print(trip.getMarginalMetHours());
            pwh.print(",");
            pwh.print(trip.getTravelRiskMap().get("lightInjury"));
            pwh.print(",");
            pwh.print(trip.getTravelRiskMap().get("severeInjury"));
            pwh.print(",");
            pwh.print(trip.getTravelRiskMap().get("fatality"));
            pwh.print(",");
            pwh.print(trip.getMatsimLinkCount());
            pwh.print(",");
            pwh.print(trip.getMatsimConcMetersPm25() / trip.getMatsimTravelDistance());
            pwh.print(",");
            pwh.print(trip.getMatsimConcMetersNo2() / trip.getMatsimTravelDistance());
            pwh.print(",");
            pwh.print(trip.getTravelExposureMap().get("pm2.5"));
            pwh.print(",");
            pwh.print(trip.getTravelExposureMap().get("no2"));
            pwh.print(",");
            pwh.print(trip.getActivityExposureMap().get("pm2.5"));
            pwh.print(",");
            pwh.println(trip.getActivityExposureMap().get("no2"));
        }
        pwh.close();
    }
}
