package de.tum.bgu.msm.health.io;

import cern.colt.map.tfloat.OpenIntFloatHashMap;
import de.tum.bgu.msm.data.Day;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.health.data.DataContainerHealth;
import de.tum.bgu.msm.health.data.LinkInfo;
import de.tum.bgu.msm.util.MitoUtil;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.emissions.Pollutant;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class LinkInfoWriter {

    private final static Logger logger = LogManager.getLogger(LinkInfoWriter.class);

    public void writeData(DataContainerHealth dataContainer, String outputDirectory, Day day, String sourceMode){
        writeLinkExposure(dataContainer,outputDirectory + "linkConcentration_" + day + "_" + sourceMode + ".csv");
        writeZoneExposure(dataContainer,outputDirectory + "zoneConcentration_" + day + "_" + sourceMode + ".csv");
    }
    private void writeLinkExposure(DataContainerHealth dataContainer, String path) {
        logger.info("  Writing link exposure health indicators file");
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

    private void writeZoneExposure(DataContainerHealth dataContainer, String path) {
        logger.info("  Writing zone exposure health indicators file");
        PrintWriter pwh = MitoUtil.openFileForSequentialWriting(path, false);
        pwh.println("zoneId,pollutant,timebin,value");
        //order of Set is not fixed
        for (Zone zone : dataContainer.getZoneExposure2Pollutant2TimeBin().keySet()) {
            Map<Pollutant, OpenIntFloatHashMap> exposure2Pollutant2TimeBin =  ((DataContainerHealth)dataContainer).getZoneExposure2Pollutant2TimeBin().get(zone);
            for (Pollutant pollutant : exposure2Pollutant2TimeBin.keySet()) {
                for (int timebin : exposure2Pollutant2TimeBin.get(pollutant).keys().elements()) {
                    pwh.print(zone.getZoneId());
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
