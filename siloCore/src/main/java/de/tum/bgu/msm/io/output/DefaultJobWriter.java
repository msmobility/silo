package de.tum.bgu.msm.io.output;

import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobData;
import de.tum.bgu.msm.utils.SiloUtil;

import java.io.PrintWriter;
import java.util.Collection;

public class DefaultJobWriter implements JobWriter {

    private final Collection<Job> jobs;

    public DefaultJobWriter(Collection<Job> jobs) {
        this.jobs = jobs;
    }

    @Override
    public void writeJobs(String path) {
        PrintWriter pwj = SiloUtil.openFileForSequentialWriting(path, false);
        pwj.print("id,zone,personId,type,coordX,coordY");
        pwj.println();
        for (Job jj : jobs) {
            pwj.print(jj.getId());
            pwj.print(",");
            pwj.print(jj.getZoneId());
            pwj.print(",");
            pwj.print(jj.getWorkerId());
            pwj.print(",\"");
            pwj.print(jj.getType());
            pwj.print("\"");
            pwj.print(",");
            if(jj.getCoordinate() != null) {
                pwj.print(jj.getCoordinate().x);
                pwj.print(",");
                pwj.print(jj.getCoordinate().y);
            } else {
                pwj.print("NULL,NULL");
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
