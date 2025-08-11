package de.tum.bgu.msm.io;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.health.data.ActivityLocation;
import de.tum.bgu.msm.health.data.DataContainerHealth;
import de.tum.bgu.msm.schools.*;
import de.tum.bgu.msm.util.parseMEL;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SchoolReaderMEL implements SchoolReader {

    private final static Logger logger = LogManager.getLogger(SchoolReaderMEL.class);
    private final SchoolData schoolData;
    private final DataContainer dataContainer;

    public SchoolReaderMEL(DataContainer dataContainer) {
        this.schoolData = ((DataContainerWithSchools) dataContainer).getSchoolData();
        this.dataContainer = dataContainer;
    }

    @Override
    public void readData(String fileName) {

        logger.info("Reading school micro data from ascii file ({})", fileName);
        SchoolFactory factory = SchoolUtils.getFactory();
        String recString = "";
        int recCount = 0;

        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = parseMEL.stringParse(recString.split(","));
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
            int combinedTypeCount = 0;
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                int id = parseMEL.intParse(lineElements[posId]);
                int zoneId = parseMEL.zoneParse(lineElements[posZone]);
                String typeString;
                if (lineElements[posType].equals("\"1,2\"")) {
                    typeString = "4"; // MEL uses 1,2 for primary and secondary schools, we use 4 for schools
                    combinedTypeCount++;
                } else {
                    typeString = lineElements[posType];
                }
                int type = parseMEL.intParse(typeString);
                int capacity = parseMEL.intParse(lineElements[posCapacity]);
                int occupancy = parseMEL.intParse(lineElements[posOccupancy]);

                Coordinate coordinate = new Coordinate(Double.parseDouble(lineElements[posCoordX]), Double.parseDouble(lineElements[posCoordY]));

                School ss = factory.createSchool(id, type, capacity, occupancy, coordinate, zoneId);
                schoolData.addSchool(ss);

                ActivityLocation activityLocation = new ActivityLocation(("ss" + id), coordinate);
                ((DataContainerHealth) dataContainer).getActivityLocations().put(("ss" + id), activityLocation);
            }

            if (combinedTypeCount > 0) {
                logger.warn("{} schools with combined type (\"1,2\"; i.e. primary and secondary, combined) identified; SILO requires distinct types; tenatively, a new type \"4\" will be used instead for combined schools.", combinedTypeCount);
            }

        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop school file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " schools.");
    }

}
