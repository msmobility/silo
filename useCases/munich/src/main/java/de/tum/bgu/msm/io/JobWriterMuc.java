package de.tum.bgu.msm.io;

import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.job.JobMuc;
import de.tum.bgu.msm.io.output.JobWriter;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.io.PrintWriter;

public class JobWriterMuc implements JobWriter {

    private final static Logger logger = Logger.getLogger(JobWriterMuc.class);

    private final JobDataManager jobDataManager;

    public JobWriterMuc(JobDataManager dataContainer) {
        this.jobDataManager = dataContainer;
    }

    @Override
    public void writeJobs(String path) {
        logger.info("  Writing job file to " + path);
        PrintWriter pwj = SiloUtil.openFileForSequentialWriting(path, false);
        pwj.print("id,zone,personId,type");
        pwj.print(",");
        pwj.print("coordX");
        pwj.print(",");
        pwj.print("coordY");
        pwj.print(",");
        pwj.print("startTime");
        pwj.print(",");
        pwj.print("duration");
        pwj.println();
        for (Job jj : jobDataManager.getJobs()) {
            pwj.print(jj.getId());
            pwj.print(",");
            pwj.print(jj.getZoneId());
            pwj.print(",");
            pwj.print(jj.getWorkerId());
            pwj.print(",\"");
            pwj.print(jj.getType());
            pwj.print("\"");

            Coordinate coordinate = jj.getCoordinate();
            pwj.print(",");
            pwj.print(coordinate.x);
            pwj.print(",");
            pwj.print(coordinate.y);

            pwj.print(",");
            pwj.print(jj.getStartTimeInSeconds().orElse(-1));
            pwj.print(",");
            pwj.print(jj.getWorkingTimeInSeconds().orElse(-1));

            pwj.println();
            if (jj.getId() == SiloUtil.trackJj) {
                SiloUtil.trackingFile("Writing jj " + jj.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(jj.toString());
            }
        }
        pwj.close();
    }
}

