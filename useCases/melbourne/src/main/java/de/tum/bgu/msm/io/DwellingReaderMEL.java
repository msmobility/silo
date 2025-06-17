package de.tum.bgu.msm.io;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.health.DwellingFactoryMEL;
import de.tum.bgu.msm.health.NoiseDwellingMEL;
import de.tum.bgu.msm.health.data.ActivityLocation;
import de.tum.bgu.msm.health.data.DataContainerHealth;
import de.tum.bgu.msm.io.input.DwellingReader;
import de.tum.bgu.msm.util.parseMEL;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DwellingReaderMEL implements DwellingReader {

    private final static Logger logger = LogManager.getLogger(DwellingReaderMEL.class);
    private final DwellingData dwellingData;

    private final DwellingTypes dwellingTypes;
    private final DwellingFactoryMEL dwellingFactory;
    private DataContainer dataContainer;

    public DwellingReaderMEL(RealEstateDataManager realEstate, DataContainer dataContainer) {
        this(realEstate.getDwellingData(), realEstate.getDwellingTypes(), realEstate.getDwellingFactory());
        this.dataContainer = dataContainer;
    }

    public DwellingReaderMEL(DwellingData realEstate, DwellingTypes dwellingTypes,
                             DwellingFactory dwellingFactory) {
        this.dwellingData = realEstate;
        this.dwellingTypes = dwellingTypes;
        this.dwellingFactory = new DwellingFactoryMEL(dwellingFactory);
    }

    @Override
    public void readData(String path) {
        logger.info("Reading dwelling micro data from ascii file ({})", path);

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            recString = in.readLine();

            String[] header = parseMEL.stringParse(recString.split(","));
            logger.info("Reading dwelling record " + recCount + " from " + path);
            int posId = SiloUtil.findPositionInArray("id", header);
            int posZone = SiloUtil.findPositionInArray("zone", header);
            int posHh = SiloUtil.findPositionInArray("hhID", header);
            int posType = SiloUtil.findPositionInArray("type", header);
            int posRooms = SiloUtil.findPositionInArray("bedrooms", header);
            int posQuality = SiloUtil.findPositionInArray("quality", header);
            int posCosts = SiloUtil.findPositionInArray("monthlyCost", header);
            int posYear = SiloUtil.findPositionInArray("yearBuilt", header);

            int posCoordX = -1;
            int posCoordY = -1;
            try {
                posCoordX = SiloUtil.findPositionInArray("coordX", header);
                posCoordY = SiloUtil.findPositionInArray("coordY", header);
            } catch (Exception e) {
                logger.warn("No coords given in dwelling input file. Models using microlocations will not work.");
            }

            int noCoordCounter = 0;
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = parseMEL.stringParse(recString.split(","));
                int id = parseMEL.intParse(lineElements[posId]);
                int zoneId = parseMEL.intParse(lineElements[posZone]);
                int hhId =  parseMEL.intParse(lineElements[posHh]);
                String typeString = parseMEL.stringParse(lineElements[posType]);
                DwellingType type = dwellingTypes.valueOf(typeString.toUpperCase());
                int price = Integer.parseInt(lineElements[posCosts]);
                int area = Integer.parseInt(lineElements[posRooms]);
                int quality = Integer.parseInt(lineElements[posQuality]);
                int yearBuilt = Integer.parseInt(lineElements[posYear]);

                Coordinate coordinate = null;
                if (posCoordX >= 0 && posCoordY >= 0) {
                    try {
                        coordinate = new Coordinate(Double.parseDouble(lineElements[posCoordX]), Double.parseDouble(lineElements[posCoordY]));
                    } catch (Exception e) {
                        noCoordCounter++;
                    }
                }else{
                    coordinate = dataContainer.getGeoData().getZones().get(zoneId).getRandomCoordinate(SiloUtil.getRandomObject());
                }

                NoiseDwellingMEL dwelling = dwellingFactory.createDwelling(id, zoneId, coordinate, hhId, type, area, quality, price, yearBuilt);
                dwellingData.addDwelling(dwelling);

                ActivityLocation activityLocation = new ActivityLocation(("dd"+id),coordinate);
                ((DataContainerHealth) dataContainer).getActivityLocations().put(("dd"+id),activityLocation);

                if (id == SiloUtil.trackDd) {
                    SiloUtil.trackWriter.println("Read dwelling with following attributes from " + path);
                    SiloUtil.trackWriter.println(dwelling.toString());
                }
            }
            if(noCoordCounter > 0) {
                logger.warn("There were " + noCoordCounter + " dwellings without coordinates.");
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop dwelling file: " + path);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " dwellings.");
    }
}
