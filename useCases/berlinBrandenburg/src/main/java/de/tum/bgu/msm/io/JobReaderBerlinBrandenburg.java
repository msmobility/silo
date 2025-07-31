package de.tum.bgu.msm.io;

import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.job.JobFactoryBerlinBrandenburg;
import de.tum.bgu.msm.data.job.JobBerlinBrandenburg;
import de.tum.bgu.msm.io.input.JobReader;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class JobReaderBerlinBrandenburg implements JobReader {

    private final static Logger logger = LogManager.getLogger(JobReaderBerlinBrandenburg.class);
    private final JobDataManager jobDataManager;
    private final JobFactoryBerlinBrandenburg jobFactory;

    public JobReaderBerlinBrandenburg(JobDataManager jobDataManager, JobFactoryBerlinBrandenburg jobFactory) {
        this.jobDataManager = jobDataManager;
        this.jobFactory = jobFactory;
    }

    @Override
    public void readData(String fileName) {

        logger.info("Reading job micro data from ascii file");
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

            int posCoordX = SiloUtil.findPositionInArray("CoordX", header);
            int posCoordY = SiloUtil.findPositionInArray("CoordY", header);
            int posStartTime = SiloUtil.findPositionInArray("startTime", header);
            int posDuration = SiloUtil.findPositionInArray("duration", header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id = Integer.parseInt(lineElements[posId]);
                int zoneId = Integer.parseInt(lineElements[posZone]);
                int worker = Integer.parseInt(lineElements[posWorker]);
                String type = lineElements[posType].replace("\"", "");

                Coordinate coordinate = new Coordinate(Double.parseDouble(lineElements[posCoordX]), Double.parseDouble(lineElements[posCoordY]));

                JobBerlinBrandenburg jj = jobFactory.createJob(id, zoneId, coordinate, worker, type);
                int startTime = Integer.parseInt(lineElements[posStartTime]);
                int duration = Integer.parseInt(lineElements[posDuration]);
                jj.setJobWorkingTime(startTime, duration);

                jobDataManager.addJob(jj);
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
