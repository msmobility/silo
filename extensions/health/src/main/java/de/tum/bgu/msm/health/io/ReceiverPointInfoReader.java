package de.tum.bgu.msm.health.io;

import cern.colt.map.tfloat.OpenIntFloatHashMap;
import de.tum.bgu.msm.data.Day;
import de.tum.bgu.msm.health.data.DataContainerHealth;
import de.tum.bgu.msm.health.data.ReceiverPointInfo;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.emissions.Pollutant;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.utils.geometry.CoordUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class ReceiverPointInfoReader {

    private final static Logger logger = LogManager.getLogger(ReceiverPointInfoReader.class);

    public void readConcentrationData(DataContainerHealth dataContainer, String outputDirectory, Day day, String sourceMode){
        String path = outputDirectory + "zoneConcentration_" + day + "_" + sourceMode + ".csv";

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

                String rpId = "zone" + zoneId;
                if (dataContainer.getReceiverPointInfo().get(rpId)==null){
                    logger.error("Zone " + zoneId + " does not exist in Receiver Point container.");
                }

                Map<Pollutant, OpenIntFloatHashMap> exposure2Pollutant2TimeBin =  dataContainer.getReceiverPointInfo().get(rpId).getExposure2Pollutant2TimeBin();
                if(exposure2Pollutant2TimeBin.get(pollutant)==null){
                    OpenIntFloatHashMap exposureByTimeBin = new OpenIntFloatHashMap();
                    exposureByTimeBin.put(startTime/3600, value);
                    exposure2Pollutant2TimeBin.put(pollutant, exposureByTimeBin);
                }else {
                    exposure2Pollutant2TimeBin.get(pollutant).put(startTime/3600, value);
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading zone concentration file: " + path);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " zones with concentration.");
    }

    public void readNoiseLevelData(DataContainerHealth dataContainer, String outputDirectory, Day day){
        String path = outputDirectory + "/" + day +  "/car/noise-analysis/immissions/";

        logger.info("Reading noise level data from csv file");

        double startTime = 3600.;
        double timeBinSize = 3600.;
        double endTime = 24. * 3600.;


        for ( double time = startTime ; time <= endTime ; time = time + timeBinSize ) {

            logger.info("Reading time bin: " + time);

            String fileName = path + "immission" + "_" + time + ".csv";
            String recString = "";
            int recCount = 0;
            try {
                BufferedReader in = new BufferedReader(new FileReader(fileName));
                recString = in.readLine();

                // read header
                String[] header = recString.split(";");
                int posRpId = 0;
                int posValue = 1;

                // read line
                while ((recString = in.readLine()) != null) {
                    recCount++;
                    String[] lineElements = recString.split(";");
                    String rpId = lineElements[posRpId];
                    float value = Float.parseFloat(lineElements[posValue]);

                    if (dataContainer.getReceiverPointInfo().get(rpId)==null){
                        logger.error("Receiver point " + rpId + " does not exist in receiver point container.");
                        continue;
                    }

                    //TODO: check with John how to deal with negative noise level values
                    dataContainer.getReceiverPointInfo().get(rpId).getNoiseLevel2TimeBin().put((int) time/3600, Math.max(0,value));
                }
            } catch (IOException e) {
                logger.fatal("IO Exception caught reading rp noise immission file: " + path);
                logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
            }
            logger.info("Finished reading " + recCount + " receiver points with noise immissions.");

        }
    }

    public void processNdviData(DataContainerHealth dataContainer, Network network) {
        for (ReceiverPointInfo rpInfo : dataContainer.getReceiverPointInfo().values()){
            Link link = NetworkUtils.getNearestLink(network, CoordUtils.createCoord(rpInfo.getCoordinate()));

            if (link!=null){
                rpInfo.setNdvi((Double) link.getAttributes().getAttribute("ndvi"));
            }
        }
    }
}
