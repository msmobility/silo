package de.tum.bgu.msm.io;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.MicroLocation;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobMuc;
import de.tum.bgu.msm.io.output.JobWriter;
import de.tum.bgu.msm.utils.SiloUtil;
import org.locationtech.jts.geom.Coordinate;

import java.io.PrintWriter;

public class JobWriterMuc implements JobWriter {

    private final DataContainer dataContainer;

    public JobWriterMuc(DataContainer dataContainer) {
        this.dataContainer = dataContainer;
    }

    @Override
    public void writeJobs(String path) {
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
        for (Job jj : dataContainer.getJobDataManager().getJobs()) {
            pwj.print(jj.getId());
            pwj.print(",");
            pwj.print(jj.getZoneId());
            pwj.print(",");
            pwj.print(jj.getWorkerId());
            pwj.print(",\"");
            pwj.print(jj.getType());
            pwj.print("\"");

            Coordinate coordinate = ((MicroLocation) jj).getCoordinate();
            pwj.print(",");
            pwj.print(coordinate.x);
            pwj.print(",");
            pwj.print(coordinate.y);

            pwj.print(",");
            pwj.print(((JobMuc)jj).getStartTimeInSeconds());
            pwj.print(",");
            pwj.print(((JobMuc)jj).getWorkingTimeInSeconds());

            pwj.println();
            if (jj.getId() == SiloUtil.trackJj) {
                SiloUtil.trackingFile("Writing jj " + jj.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(jj.toString());
            }
        }
        pwj.close();
    }
}

