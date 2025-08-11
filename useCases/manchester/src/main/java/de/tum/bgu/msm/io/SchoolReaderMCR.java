package de.tum.bgu.msm.io;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.health.data.ActivityLocation;
import de.tum.bgu.msm.health.data.DataContainerHealth;
import de.tum.bgu.msm.schools.*;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SchoolReaderMCR implements SchoolReader {

    private final static Logger logger = LogManager.getLogger(SchoolReaderMCR.class);
    private final SchoolData schoolData;
    private final DataContainer dataContainer;

    public SchoolReaderMCR(DataContainer dataContainer) {
        this.schoolData = ((DataContainerWithSchools) dataContainer).getSchoolData();
        this.dataContainer = dataContainer;
    }

    @Override
    public void readData(String fileName) {

        logger.info("Reading school micro data from ascii file");
        SchoolFactory factory = SchoolUtils.getFactory();
        String recString = "";
        int recCount = 0;

        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId = SiloUtil.findPositionInArray("id", header);
            int posZone = SiloUtil.findPositionInArray("zone", header);
            int posCapacity = SiloUtil.findPositionInArray("capacity", header);
            int posOccupancy = SiloUtil.findPositionInArray("occupancy", header);
            int posType = SiloUtil.findPositionInArray("type", header);

            int posCoordX = -1;
            int posCoordY = -1;
            posCoordX = SiloUtil.findPositionInArray("CoordX", header);
            posCoordY = SiloUtil.findPositionInArray("CoordY", header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id = Integer.parseInt(lineElements[posId]);
                int zoneId = Integer.parseInt(lineElements[posZone]);
                int type = Integer.parseInt(lineElements[posType]);
                int capacity = Integer.parseInt(lineElements[posCapacity]);
                int occupancy = Integer.parseInt(lineElements[posOccupancy]);

                Coordinate coordinate = new Coordinate(Double.parseDouble(lineElements[posCoordX]), Double.parseDouble(lineElements[posCoordY]));

                School ss = factory.createSchool(id, type, capacity, occupancy, coordinate,zoneId);
                schoolData.addSchool(ss);

                ActivityLocation activityLocation = new ActivityLocation(("ss"+id),coordinate);
                ((DataContainerHealth) dataContainer).getActivityLocations().put(("ss"+id),activityLocation);

            }


        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop school file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " schools.");
    }

}
