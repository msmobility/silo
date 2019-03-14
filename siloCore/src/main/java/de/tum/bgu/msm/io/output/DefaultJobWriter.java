package de.tum.bgu.msm.io.output;

import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobData;
import de.tum.bgu.msm.utils.SiloUtil;

import java.io.PrintWriter;

public class DefaultJobWriter implements JobWriter {

    private final JobData jobData;

    public DefaultJobWriter(JobData jobData) {
        this.jobData = jobData;
    }

    @Override
    public void writeJobs(String path) {
        PrintWriter pwj = SiloUtil.openFileForSequentialWriting(path, false);
        pwj.print("id,zone,personId,type");
        pwj.println();
        for (Job jj : jobData.getJobs()) {
            pwj.print(jj.getId());
            pwj.print(",");
            pwj.print(jj.getZoneId());
            pwj.print(",");
            pwj.print(jj.getWorkerId());
            pwj.print(",\"");
            pwj.print(jj.getType());
            pwj.print("\"");
            pwj.println();
            if (jj.getId() == SiloUtil.trackJj) {
                SiloUtil.trackingFile("Writing jj " + jj.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(jj.toString());
            }
        }
        pwj.close();
    }
}
