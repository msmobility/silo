package de.tum.bgu.msm.schools;

import de.tum.bgu.msm.data.MicroLocation;
import de.tum.bgu.msm.utils.SiloUtil;
import org.locationtech.jts.geom.Coordinate;

import java.io.PrintWriter;

public class SchoolsWriter {

    private final SchoolData schoolData;

    public SchoolsWriter(SchoolData dataContainer) {
        this.schoolData = dataContainer;
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
        for (School ee : schoolData.getSchools()) {
            pws.print(ee.getId());
            pws.print(",");
            pws.print(ee.getZoneId());
            pws.print(",");
            pws.print(ee.getType());
            pws.print(",");
            pws.print(ee.getCapacity());
            pws.print(",");
            pws.print(ee.getOccupancy());
            Coordinate coordinate = ((MicroLocation) ee).getCoordinate();
            pws.print(",");
            pws.print(coordinate.x);
            pws.print(",");
            pws.print(coordinate.y);

            pws.print(",");
            pws.print(ee.getStartTimeInSeconds());
            pws.print(",");
            pws.print(ee.getStudyTimeInSeconds());
            pws.println();
        }
        pws.close();
    }
}
