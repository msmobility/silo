package de.tum.bgu.msm.io;

import de.tum.bgu.msm.data.DataContainerMuc;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.MicroLocation;
import de.tum.bgu.msm.data.school.School;
import de.tum.bgu.msm.utils.SiloUtil;
import org.locationtech.jts.geom.Coordinate;

import java.io.PrintWriter;

public class SchoolsWriter {

    private final DataContainer dataContainer;

    public SchoolsWriter(DataContainer dataContainer) {
        this.dataContainer = dataContainer;
    }

    public void writeSchools(String path) {
        PrintWriter pws = SiloUtil.openFileForSequentialWriting(path, false);
        pws.print("id,zone,type,capacity,occupancy");

        pws.print(",");
        pws.print("coordX");
        pws.print(",");
        pws.print("coordY");
        pws.print(",");
        pws.print("startTime");
        pws.print(",");
        pws.print("duration");

        pws.println();
        for (School ss : ((DataContainerMuc) dataContainer).getSchoolData().getSchools()) {
            pws.print(ss.getId());
            pws.print(",");
            pws.print(ss.getZoneId());
            pws.print(",");
            pws.print(ss.getType());
            pws.print(",");
            pws.print(ss.getCapacity());
            pws.print(",");
            pws.print(ss.getOccupancy());
            Coordinate coordinate = ((MicroLocation) ss).getCoordinate();
            pws.print(",");
            pws.print(coordinate.x);
            pws.print(",");
            pws.print(coordinate.y);

            pws.print(",");
            pws.print(ss.getStartTimeInSeconds());
            pws.print(",");
            pws.print(ss.getStudyTimeInSeconds());
            pws.println();
        }
        pws.close();
    }
}
