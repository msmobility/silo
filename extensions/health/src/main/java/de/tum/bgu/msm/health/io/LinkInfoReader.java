package de.tum.bgu.msm.health.io;

import cern.colt.map.tfloat.OpenIntFloatHashMap;
import de.tum.bgu.msm.data.Day;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.health.data.DataContainerHealth;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.emissions.Pollutant;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LinkInfoReader {

    private final static Logger logger = LogManager.getLogger(LinkInfoReader.class);

    public void readData(DataContainerHealth dataContainer, String outputDirectory, Day day){
        readLinkConcentrationData(dataContainer,outputDirectory + "linkConcentration_" + day + ".csv");
        readZoneConcentrationData(dataContainer,outputDirectory + "zoneConcentration_" + day + ".csv");
    }
    private void readLinkConcentrationData(DataContainerHealth dataContainer, String path) {
        logger.info("Reading link concentration data from csv file");

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posLinkId = SiloUtil.findPositionInArray("linkId", header);
            int posPollutant = SiloUtil.findPositionInArray("pollutant", header);
            int posTimebin = SiloUtil.findPositionInArray("timebin", header);
            int posValue = SiloUtil.findPositionInArray("value", header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                Id<Link> linkId = Id.createLinkId(lineElements[posLinkId]);
                Pollutant pollutant  = Pollutant.valueOf(lineElements[posPollutant]);
                int startTime = Integer.parseInt(lineElements[posTimebin]);
                float value = Float.parseFloat(lineElements[posValue]);

                if (dataContainer.getLinkInfo().get(linkId)==null){
                    logger.error("Link " + linkId + " does not exist in Link Info container.");
                }

                Map<Pollutant, OpenIntFloatHashMap> exposure2Pollutant2TimeBin =  (dataContainer).getLinkInfo().get(linkId).getExposure2Pollutant2TimeBin();
                if(exposure2Pollutant2TimeBin.get(pollutant)==null){
                    OpenIntFloatHashMap exposureByTimeBin = new OpenIntFloatHashMap();
                    exposureByTimeBin.put(startTime, value);
                    exposure2Pollutant2TimeBin.put(pollutant, exposureByTimeBin);
                }else {
                    exposure2Pollutant2TimeBin.get(pollutant).put(startTime, value);
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading link concentration file: " + path);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " links with concentration.");
    }

    private void readZoneConcentrationData(DataContainerHealth dataContainer, String path) {
        logger.info("Reading zone concentration data from csv file");

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posZoneId = SiloUtil.findPositionInArray("zoneId", header);
            int posPollutant = SiloUtil.findPositionInArray("pollutant", header);
            int posTimebin = SiloUtil.findPositionInArray("timebin", header);
            int posValue = SiloUtil.findPositionInArray("value", header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int zoneId = Integer.parseInt(lineElements[posZoneId]);
                Pollutant pollutant  = Pollutant.valueOf(lineElements[posPollutant]);
                int startTime = Integer.parseInt(lineElements[posTimebin]);
                float value = Float.parseFloat(lineElements[posValue]);

                Zone zone = dataContainer.getGeoData().getZones().get(zoneId);

                if(dataContainer.getZoneExposure2Pollutant2TimeBin().get(zone)==null){
                    logger.warn("Zone " + zoneId + " does not exist in Zone Info container.");
                }

                Map<Pollutant, OpenIntFloatHashMap> exposure2Pollutant2TimeBin =  dataContainer.getZoneExposure2Pollutant2TimeBin().getOrDefault(zone, new HashMap<>());

                if(exposure2Pollutant2TimeBin.get(pollutant)==null){
                    OpenIntFloatHashMap exposureByTimeBin = new OpenIntFloatHashMap();
                    exposureByTimeBin.put(startTime, value);
                    exposure2Pollutant2TimeBin.put(pollutant, exposureByTimeBin);
                }else {
                    exposure2Pollutant2TimeBin.get(pollutant).put(startTime, value);
                }
                dataContainer.getZoneExposure2Pollutant2TimeBin().put(zone,exposure2Pollutant2TimeBin);

            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading link concentration file: " + path);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " links with concentration.");
    }
}
