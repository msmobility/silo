package de.tum.bgu.msm.syntheticPopulationGenerator.manchester.microlocation;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.dwelling.DwellingFactory;
import de.tum.bgu.msm.data.dwelling.DwellingFactoryImpl;
import de.tum.bgu.msm.data.job.*;
import de.tum.bgu.msm.health.DwellingFactoryMCR;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GenerateJobMicrolocation {

    private static final Logger logger = LogManager.getLogger(GenerateJobMicrolocation.class);
    
    private final DataContainer dataContainer;
    private final DataSetSynPop dataSetSynPop;
    private final Map<Integer, Coordinate> jobCoord = new HashMap<>();
    private final Map<Integer, Map<Integer, Double>> zone2JobId2WeightMap = new HashMap<>();
    private final Map<Integer, Double> zoneLocationJobFactor = new HashMap<>();

    public GenerateJobMicrolocation(DataContainer dataContainer, DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;
        this.dataContainer = dataContainer;
    }

    public void run() {
        logger.info("   Running module: job microlocation");
        logger.info("   Start parsing jobs information to hashmap");
        readJobFile(PropertiesSynPop.get().main.microJobsFileName);
        calculateLocationJobFactor();
        logger.info("   Start Selecting the job to allocate the job");
        //Select the job to allocate the microlocation
        int errorjob = 0;
        for (Job jj: dataContainer.getJobDataManager().getJobs()) {
            int zoneID = jj.getZoneId();
            if (zone2JobId2WeightMap.get(zoneID)==null || zone2JobId2WeightMap.get(zoneID).isEmpty()){
                float coordX = dataSetSynPop.getTazAttributes().get(zoneID).get("popCentroid_x");
                float coordY = dataSetSynPop.getTazAttributes().get(zoneID).get("popCentroid_y");
                ((JobMCR)jj).setCoordinate(new Coordinate(coordX, coordY));
                ((JobMCR)jj).setMicrolocationType("zoneCentroid");
                ((JobMCR)jj).setMicroBuildingId(zoneID);
                errorjob++;
                continue;
            }

            int selectedJobID = SiloUtil.select(zone2JobId2WeightMap.get(zoneID));

            double remainingWeight = zone2JobId2WeightMap.get(zoneID).get(selectedJobID)- zoneLocationJobFactor.get(zoneID);
            if (remainingWeight > 0) {
                zone2JobId2WeightMap.get(zoneID).put(selectedJobID, remainingWeight);
            } else {
                zone2JobId2WeightMap.get(zoneID).remove(selectedJobID);
            }
            ((JobMCR)jj).setCoordinate(new Coordinate(jobCoord.get(selectedJobID)));
            ((JobMCR)jj).setMicrolocationType("poi");
            ((JobMCR)jj).setMicroBuildingId(selectedJobID);
        }
        logger.warn( errorjob +"   jobs cannot find specific building location. Their coordinates are assigned randomly in TAZ" );
        logger.info("   Finished job microlocation.");
    }

    private void calculateLocationJobFactor() {

        //total jobs in zone
        Map<Integer, Integer> zoneJobCounts = new HashMap<>();
        for(Job jj : dataContainer.getJobDataManager().getJobs()){
            zoneJobCounts.merge(jj.getZoneId(), 1, Integer::sum);
        }


        for (int zone : dataSetSynPop.getTazs()){
            Map<Integer, Double> jobs = zone2JobId2WeightMap.getOrDefault(zone, Collections.emptyMap());

            // Sum up the weights of all jobs in the zone
            double totalWeight = 0.;
            if(!jobs.isEmpty()){
                totalWeight = jobs.values().stream().mapToDouble(Double::doubleValue).sum();
            }

            int jobCounts = zoneJobCounts.getOrDefault(zone, 0);

            if(jobCounts == 0){
                continue;
            }

            if(totalWeight == 0 & jobCounts >0){
                logger.warn("Zone "+ zone + " has " + jobCounts + " job but no weights in the micro destination file.");
            }

            zoneLocationJobFactor.put(zone, totalWeight/jobCounts);
        }
    }

    private void readJobFile(String fileName) {
        //parse buildings information to hashmap
        logger.info("Reading job micro data from csv file");
        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posType = SiloUtil.findPositionInArray("type",header);
            int posId = SiloUtil.findPositionInArray("id", header);
            int posZone = SiloUtil.findPositionInArray("zone", header);
            int posWeight = SiloUtil.findPositionInArray("WEIGHT", header);

            int posCoordX = -1;
            int posCoordY = -1;
            try {
                posCoordX = SiloUtil.findPositionInArray("X", header);
                posCoordY = SiloUtil.findPositionInArray("Y", header);
            } catch (Exception e) {
                logger.warn("No coords given in job input file. Models using microlocations will not work.");
            }

            int noCoordCounter = 0;


            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                String type = lineElements[posType];
                if (type.equals("public_open_space")){
                    continue;
                }
                int id = Integer.parseInt(lineElements[posId]);
                int zone = Integer.parseInt(lineElements[posZone]);
                double weight = Double.parseDouble(lineElements[posWeight]);

                Coordinate coordinate = null;
                if (posCoordX >= 0 && posCoordY >= 0) {
                    try {
                        coordinate = new Coordinate(Double.parseDouble(lineElements[posCoordX]), Double.parseDouble(lineElements[posCoordY]));
                    } catch (Exception e) {
                        noCoordCounter++;
                    }
                }

                jobCoord.put(id,coordinate);
                //put all buildings with the same zoneID into one building list
                zone2JobId2WeightMap.computeIfAbsent(zone, k -> new HashMap<>()).put(id, weight);

            }
            if(noCoordCounter > 0) {
                logger.warn("There were " + noCoordCounter + " micro job without coordinates.");
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading micro job file: " + fileName, new RuntimeException());
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">", new RuntimeException());
        }
        logger.info("Finished reading " + recCount + " jobs.");

    }
}
