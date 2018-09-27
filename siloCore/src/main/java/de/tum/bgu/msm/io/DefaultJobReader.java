package de.tum.bgu.msm.io;

import com.vividsolutions.jts.geom.Coordinate;
import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.JobDataManager;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobFactory;
import de.tum.bgu.msm.data.job.JobUtils;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DefaultJobReader implements JobReader {

    private final static Logger logger = Logger.getLogger(DefaultJobReader.class);
    private final JobDataManager jobData;

    public DefaultJobReader(JobDataManager jobData) {
        this.jobData = jobData;
    }

    @Override
    public void readData(String fileName) {

        logger.info("Reading job micro data from ascii file");
        JobFactory factory = JobUtils.getFactory();
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

            int posCoordX = -1;
            int posCoordY = -1;
            if (Properties.get().main.implementation == Implementation.MUNICH) {
                posCoordX = SiloUtil.findPositionInArray("CoordX", header);
                posCoordY = SiloUtil.findPositionInArray("CoordY", header);
            }

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id = Integer.parseInt(lineElements[posId]);
                int zoneId = Integer.parseInt(lineElements[posZone]);
                int worker = Integer.parseInt(lineElements[posWorker]);
                String type = lineElements[posType].replace("\"", "");

                Coordinate coordinate = null;
                //TODO: remove it when we implement interface
                if (Properties.get().main.implementation == Implementation.MUNICH) {
                    coordinate = new Coordinate(Double.parseDouble(lineElements[posCoordX]), Double.parseDouble(lineElements[posCoordY]));
                }

                Job jj = factory.createJob(id, zoneId, coordinate, worker, type);
                jobData.addJob(jj);
                if (id == SiloUtil.trackJj) {
                    SiloUtil.trackWriter.println("Read job with following attributes from " + fileName);
                    SiloUtil.trackWriter.println(jj.toString());
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop job file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " jobs.");
    }
}
