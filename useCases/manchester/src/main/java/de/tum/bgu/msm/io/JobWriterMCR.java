package de.tum.bgu.msm.io;

import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.job.JobMCR;
import de.tum.bgu.msm.io.output.JobWriter;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;

public class JobWriterMCR implements JobWriter {

    private final static Logger logger = LogManager.getLogger(JobWriterMCR.class);

    private final JobDataManager jobDataManager;

    public JobWriterMCR(JobDataManager dataContainer) {
        this.jobDataManager = dataContainer;
    }

    @Override
    public void writeJobs(String path) {
        logger.info("  Writing job file to " + path);
        PrintWriter pwj = SiloUtil.openFileForSequentialWriting(path, false);
        pwj.print("id,zone,personId,type");
        pwj.print(",");
        pwj.print("microlocationType");
        pwj.print(",");
        pwj.print("microBuildingID");
        pwj.print(",");
        pwj.print("coordX");
        pwj.print(",");
        pwj.print("coordY");
        pwj.println();
        for (Job jj : jobDataManager.getJobs()) {
            pwj.print(jj.getId());
            pwj.print(",");
            pwj.print(jj.getZoneId());
            pwj.print(",");
            pwj.print(jj.getWorkerId());
            pwj.print(",");
            pwj.print(jj.getType());
            pwj.print(",");
            if(jj.getCoordinate() != null) {
                pwj.print(((JobMCR)jj).getMicrolocationType());
                pwj.print(",");
                pwj.print(((JobMCR)jj).getMicroBuildingId());
                pwj.print(",");
                pwj.print(jj.getCoordinate().x);
                pwj.print(",");
                pwj.print(jj.getCoordinate().y);
            } else {
                pwj.print("NULL,NULL,NULL,NULL");
            }

            pwj.println();
            if (jj.getId() == SiloUtil.trackJj) {
                SiloUtil.trackingFile("Writing jj " + jj.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(jj.toString());
            }
        }
        pwj.close();
    }
}

