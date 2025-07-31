package de.tum.bgu.msm.health.io;

import cern.colt.map.tfloat.OpenIntFloatHashMap;
import de.tum.bgu.msm.data.Day;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.health.data.ActivityLocation;
import de.tum.bgu.msm.health.data.DataContainerHealth;
import de.tum.bgu.msm.health.data.LinkInfo;
import de.tum.bgu.msm.util.MitoUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.contrib.emissions.Pollutant;

import java.io.PrintWriter;
import java.util.Map;

public class PollutantConcentrationWriter {

    private final static Logger logger = LogManager.getLogger(PollutantConcentrationWriter.class);

    public void writeData(DataContainerHealth dataContainer, String outputDirectory, Day day){
        writeLinkConcentration(dataContainer,outputDirectory + "linkConcentration_" + day + ".csv");
        writeActivityLocationConcentration(dataContainer,outputDirectory + "locationConcentration_" + day + ".csv");
    }
    private void writeLinkConcentration(DataContainerHealth dataContainer, String path) {
        logger.info("  Writing link concentration file");
        PrintWriter pwh = MitoUtil.openFileForSequentialWriting(path, false);
        pwh.println("linkId,pollutant,timebin,value");
        //order of Set is not fixed
        for (LinkInfo linkInfo : dataContainer.getLinkInfo().values()) {
            for (Pollutant pollutant : linkInfo.getExposure2Pollutant2TimeBin().keySet()) {
                for (int timebin : linkInfo.getExposure2Pollutant2TimeBin().get(pollutant).keys().elements()) {
                    pwh.print(linkInfo.getLinkId());
                    pwh.print(",");
                    pwh.print(pollutant.name());
                    pwh.print(",");
                    pwh.print(timebin);
                    pwh.print(",");
                    pwh.print(linkInfo.getExposure2Pollutant2TimeBin().get(pollutant).get(timebin));
                    pwh.println();
                }
            }
        }

        pwh.close();
    }

    private void writeActivityLocationConcentration(DataContainerHealth dataContainer, String path) {
        logger.info("  Writing activity location concentration file");
        PrintWriter pwh = MitoUtil.openFileForSequentialWriting(path, false);
        pwh.println("id,pollutant,timebin,value");
        //order of Set is not fixed
        for (ActivityLocation activityLocation : dataContainer.getActivityLocations().values()) {
            Map<Pollutant, OpenIntFloatHashMap> exposure2Pollutant2TimeBin =  dataContainer.getActivityLocations().get(activityLocation.getLocationId()).getExposure2Pollutant2TimeBin();
            for (Pollutant pollutant : exposure2Pollutant2TimeBin.keySet()) {
                for (int timebin : exposure2Pollutant2TimeBin.get(pollutant).keys().elements()) {
                    pwh.print(activityLocation.getLocationId());
                    pwh.print(",");
                    pwh.print(pollutant.name());
                    pwh.print(",");
                    pwh.print(timebin);
                    pwh.print(",");
                    pwh.print(exposure2Pollutant2TimeBin.get(pollutant).get(timebin));
                    pwh.println();
                }
            }
        }

        pwh.close();
    }
}
