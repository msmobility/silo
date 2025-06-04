package de.tum.bgu.msm.health.io;

import de.tum.bgu.msm.health.data.Trip;
import de.tum.bgu.msm.util.MitoUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.util.Map;

public class TripExposureWriter {

    private final static Logger logger = LogManager.getLogger(TripExposureWriter.class);
    public void writeMitoTrips(Map<Integer, Trip> mitoTrips, String path) {
        logger.info("  Writing trip health indicators file");
        PrintWriter pwh = MitoUtil.openFileForSequentialWriting(path, false);
        pwh.println("t.id,t.mode,t.matsimTravelTime_s,t.matsimTravelDistance_m,t.activityDuration_min," +
                "t.mmetHours,t.severFatalInjuryRisk," + // "t.mmetHours,t.lightInjuryRisk,t.severeInjuryRisk,t.fatalityRisk," +
                "t.links," +
                "t.exposurePm25,t.exposureNo2,t.activityExposurePm25,t.activityExposureNo2,"+
                "t.exposureNoise,t.activityExposureNoise,"+
                "t.exposureNdvi,t.activityExposureNdvi");

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
            pwh.print(trip.getTravelRiskMap().get("severeFatalInjury"));
            pwh.print(",");
            //pwh.print(trip.getTravelRiskMap().get("lightInjury"));
            //pwh.print(",");
            //pwh.print(trip.getTravelRiskMap().get("severeInjury"));
            //pwh.print(",");
            //pwh.print(trip.getTravelRiskMap().get("fatality"));
            //pwh.print(",");
            pwh.print(trip.getMatsimLinkCount());
            pwh.print(",");
            pwh.print(trip.getTravelExposureMap().get("pm2.5"));
            pwh.print(",");
            pwh.print(trip.getTravelExposureMap().get("no2"));
            pwh.print(",");
            pwh.print(trip.getActivityExposureMap().get("pm2.5"));
            pwh.print(",");
            pwh.print(trip.getActivityExposureMap().get("no2"));
            pwh.print(",");
            pwh.print(trip.getTravelNoiseExposure());
            pwh.print(",");
            pwh.print(trip.getActivityNoiseExposure());
            pwh.print(",");
            pwh.print(trip.getTravelNdviExposure());
            pwh.print(",");
            pwh.print(trip.getActivityNdviExposure());
            pwh.println();
        }
        pwh.close();
    }
}
