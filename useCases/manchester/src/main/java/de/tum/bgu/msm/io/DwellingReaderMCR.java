package de.tum.bgu.msm.io;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.health.DwellingFactoryMCR;
import de.tum.bgu.msm.health.NoiseDwellingMCR;
import de.tum.bgu.msm.health.PersonFactoryMCRHealth;
import de.tum.bgu.msm.io.input.DwellingReader;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DwellingReaderMCR implements DwellingReader {

    private final static Logger logger = LogManager.getLogger(DwellingReaderMCR.class);
    private final DwellingData dwellingData;

    private final DwellingTypes dwellingTypes;
    private final DwellingFactoryMCR dwellingFactory;
    private DataContainer dataContainer;

    public DwellingReaderMCR(RealEstateDataManager realEstate, DataContainer dataContainer) {
        this(realEstate.getDwellingData(), realEstate.getDwellingTypes(), realEstate.getDwellingFactory());
        this.dataContainer = dataContainer;
    }

    public DwellingReaderMCR(DwellingData realEstate, DwellingTypes dwellingTypes,
                             DwellingFactory dwellingFactory) {
        this.dwellingData = realEstate;
        this.dwellingTypes = dwellingTypes;
        this.dwellingFactory = new DwellingFactoryMCR(dwellingFactory);
    }

    @Override
    public void readData(String path) {
        logger.info("Reading dwelling micro data from ascii file");

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            recString = in.readLine();

            String[] header = recString.split(",");
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
                String[] lineElements = recString.split(",");
                int id = Integer.parseInt(lineElements[posId]);
                int zoneId = Integer.parseInt(lineElements[posZone]);
                int hhId = Integer.parseInt(lineElements[posHh]);
                String tp = lineElements[posType].replace("\"", "");
                DwellingType type = dwellingTypes.valueOf(tp);
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

                NoiseDwellingMCR dwelling = dwellingFactory.createDwelling(id, zoneId, coordinate, hhId, type, area, quality, price, yearBuilt);
                dwellingData.addDwelling(dwelling);
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
