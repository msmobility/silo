package de.tum.bgu.msm.io;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.job.JobFactory;
import de.tum.bgu.msm.data.job.JobMCR;
import de.tum.bgu.msm.io.input.JobReader;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class JobReaderMCR implements JobReader {

    private final static Logger logger = LogManager.getLogger(JobReaderMCR.class);
    private final JobDataManager jobData;
    private final DataContainer dataContainer;

    public JobReaderMCR(JobDataManager jobDataManager, DataContainer dataContainer) {
        this.jobData = jobDataManager;
        this.dataContainer = dataContainer;
    }

    @Override
    public void readData(String fileName) {

        logger.info("Reading job micro data from ascii file");
        JobFactory factory = jobData.getFactory();
        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId = SiloUtil.findPositionInArray("id", header);
            int posZone = SiloUtil.findPositionInArray("zone", header);
            int posWorker = SiloUtil.findPositionInArray("personId", header);
            int posType = SiloUtil.findPositionInArray("type", header);

            int posMicrolocationType = -1;
            int posMicroBuildingId = -1;
            int posCoordX = -1;
            int posCoordY = -1;
            try {
                posMicrolocationType = SiloUtil.findPositionInArray("microlocationType", header);
                posMicroBuildingId = SiloUtil.findPositionInArray("microBuildingID", header);
                posCoordX = SiloUtil.findPositionInArray("coordX", header);
                posCoordY = SiloUtil.findPositionInArray("coordY", header);
            } catch (Exception e) {
                logger.warn("No coords given in dwelling input file. Models using microlocations will not work.");
            }

            int noCoordCounter = 0;


            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id = Integer.parseInt(lineElements[posId]);
                int zoneId = Integer.parseInt(lineElements[posZone]);
                int worker = Integer.parseInt(lineElements[posWorker]);
                String type = lineElements[posType].replace("\"", "");

                Coordinate coordinate = null;
                if (posCoordX >= 0 && posCoordY >= 0) {
                    try {
                        coordinate = new Coordinate(Double.parseDouble(lineElements[posCoordX]), Double.parseDouble(lineElements[posCoordY]));
                    } catch (Exception e) {
                        noCoordCounter++;
                    }
                }else{
                    coordinate = dataContainer.getGeoData().getZones().get(zoneId).getRandomCoordinate(SiloUtil.getRandomObject());
                }

                Job jj = factory.createJob(id, zoneId, coordinate, worker, type);

                if(posMicrolocationType>=0){
                    ((JobMCR)jj).setMicrolocationType(lineElements[posMicrolocationType]);
                }else {
                    ((JobMCR)jj).setMicrolocationType("null");
                }

                if(posMicroBuildingId>=0){
                    ((JobMCR)jj).setMicroBuildingId(Long.parseLong(lineElements[posMicroBuildingId]));
                }else {
                    ((JobMCR)jj).setMicroBuildingId(-1);
                }


                jobData.addJob(jj);
                if (id == SiloUtil.trackJj) {
                    SiloUtil.trackWriter.println("Read job with following attributes from " + fileName);
                    SiloUtil.trackWriter.println(jj.toString());
                }
            }
            if(noCoordCounter > 0) {
                logger.warn("There were " + noCoordCounter + " jobs without coordinates.");
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop job file: " + fileName, new RuntimeException());
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">", new RuntimeException());
        }
        logger.info("Finished reading " + recCount + " jobs.");
    }
}
