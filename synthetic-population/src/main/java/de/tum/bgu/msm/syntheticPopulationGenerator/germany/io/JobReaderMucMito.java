package de.tum.bgu.msm.syntheticPopulationGenerator.germany.io;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.job.JobFactoryMuc;
import de.tum.bgu.msm.data.job.JobMuc;
import de.tum.bgu.msm.io.input.JobReader;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class JobReaderMucMito implements JobReader {

    private final static Logger logger = Logger.getLogger(JobReaderMucMito.class);
    private final JobDataManager jobDataManager;
    private final JobFactoryMuc jobFactory;

    public JobReaderMucMito(JobDataManager jobDataManager, JobFactoryMuc jobFactory) {
        this.jobDataManager = jobDataManager;
        this.jobFactory = jobFactory;
    }

    @Override
    public void readData(String fileName) {

        TableDataSet jobDuration = SiloUtil.readCSVfile("C:/models/silo/germany/input/jobDurationDistributions.csv");
        TableDataSet jobStart = SiloUtil.readCSVfile("C:/models/silo/germany/input/jobStartTimeDistributions.csv");
        int[] times = jobStart.getColumnAsInt("time");
        int[] durations = jobDuration.getColumnAsInt("time");
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

                JobMuc jj = jobFactory.createJob(id, zoneId, coordinate, worker, type);
                int startTime = times[SiloUtil.select(jobStart.getColumnAsFloat(type))];
                int duration = durations[SiloUtil.select(jobDuration.getColumnAsFloat(type))];
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