package de.tum.bgu.msm.health.io;

import cern.colt.map.tfloat.OpenIntFloatHashMap;
import de.tum.bgu.msm.health.data.DataContainerHealth;
import de.tum.bgu.msm.health.data.ActivityLocation;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.emissions.Pollutant;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.utils.geometry.CoordUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class ActivityLocationInfoReader {

    private final static Logger logger = LogManager.getLogger(ActivityLocationInfoReader.class);

    public void readConcentrationData(DataContainerHealth dataContainer, String path){

        logger.info("Reading location concentration data from csv file");

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId = SiloUtil.findPositionInArray("id", header);
            int posPollutant = SiloUtil.findPositionInArray("pollutant", header);
            int posTimebin = SiloUtil.findPositionInArray("timebin", header);
            int posValue = SiloUtil.findPositionInArray("value", header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                String locationId = lineElements[posId];
                Pollutant pollutant  = Pollutant.valueOf(lineElements[posPollutant]);
                int startTime = Integer.parseInt(lineElements[posTimebin]);
                float value = Float.parseFloat(lineElements[posValue]);

                if (dataContainer.getActivityLocations().get(locationId)==null){
                    logger.error("Location " + locationId + " does not exist in activity location container.");
                }

                Map<Pollutant, OpenIntFloatHashMap> exposure2Pollutant2TimeBin =  dataContainer.getActivityLocations().get(locationId).getExposure2Pollutant2TimeBin();
                if(exposure2Pollutant2TimeBin.get(pollutant)==null){
                    OpenIntFloatHashMap exposureByTimeBin = new OpenIntFloatHashMap();
                    exposureByTimeBin.put(startTime/3600, value);
                    exposure2Pollutant2TimeBin.put(pollutant, exposureByTimeBin);
                }else {
                    float oldValue = exposure2Pollutant2TimeBin.get(pollutant).get(startTime/3600);
                    exposure2Pollutant2TimeBin.get(pollutant).put(startTime/3600, oldValue + value);
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading location concentration file: " + path);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " locations with concentration.");
    }

    public void readNoiseLevelData(DataContainerHealth dataContainer, String path){

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

                    if (dataContainer.getActivityLocations().get(rpId)==null){
                        logger.error("Receiver point " + rpId + " does not exist in receiver point container.");
                        continue;
                    }

                    //TODO: check with John how to deal with negative noise level values
                    dataContainer.getActivityLocations().get(rpId).getNoiseLevel2TimeBin().put((int) (time/3600-1), Math.max(0,value));
                }
            } catch (IOException e) {
                logger.fatal("IO Exception caught reading rp noise immission file: " + path);
                logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
            }
            logger.info("Finished reading " + recCount + " receiver points with noise immissions.");

        }
    }
}
