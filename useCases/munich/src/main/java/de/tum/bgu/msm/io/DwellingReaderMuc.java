package de.tum.bgu.msm.io;

import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypes;
import de.tum.bgu.msm.io.input.DwellingReader;
import de.tum.bgu.msm.data.dwelling.DwellingTypes;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DwellingReaderMuc implements DwellingReader {

    private final static Logger logger = Logger.getLogger(DwellingReaderMuc.class);

    private final DwellingData dwellingData;

    private final DwellingTypes dwellingTypes;
    private final DwellingFactory dwellingFactory;

    public DwellingReaderMuc(DwellingData realEstate) {
        this(realEstate, new DefaultDwellingTypes(), new DwellingFactoryImpl());
    }

    public DwellingReaderMuc(DwellingData realEstate,
                                 DwellingFactory dwellingFactory) {
        this(realEstate, new DefaultDwellingTypes(), dwellingFactory);
    }

    public DwellingReaderMuc(DwellingData realEstate,
                                 DwellingTypes dwellingTypes) {
        this(realEstate, dwellingTypes, new DwellingFactoryImpl());
    }

    public DwellingReaderMuc(DwellingData realEstate, DwellingTypes dwellingTypes,
                                 DwellingFactory dwellingFactory) {
        this.dwellingData = realEstate;
        this.dwellingTypes = dwellingTypes;
        this.dwellingFactory = dwellingFactory;
    }

    public DwellingReaderMuc(RealEstateDataManager realEstateDataManager) {
        this(realEstateDataManager.getDwellingData(), realEstateDataManager.getDwellingTypes(), realEstateDataManager.getDwellingFactory());
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
            int posId      = SiloUtil.findPositionInArray("id", header);
            int posZone    = SiloUtil.findPositionInArray("zone",header);
            int posHh      = SiloUtil.findPositionInArray("hhID",header);
            int posType    = SiloUtil.findPositionInArray("type",header);
            int posRooms   = SiloUtil.findPositionInArray("bedrooms",header);
            int posQuality = SiloUtil.findPositionInArray("quality",header);
            int posCosts   = SiloUtil.findPositionInArray("monthlyCost",header);
            int posYear    = SiloUtil.findPositionInArray("yearBuilt",header);

            int posCoordX = -1;
            int posCoordY = -1;

            posCoordX = SiloUtil.findPositionInArray("coordX", header);
            posCoordY = SiloUtil.findPositionInArray("coordY", header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id        = Integer.parseInt(lineElements[posId]);
                int zoneId      = Integer.parseInt(lineElements[posZone]);
                int hhId      = Integer.parseInt(lineElements[posHh]);
                String tp     = lineElements[posType].replace("\"", "");
                DwellingType type = dwellingTypes.valueOf(tp);
                int price     = Integer.parseInt(lineElements[posCosts]);
                int area      = Integer.parseInt(lineElements[posRooms]);
                int quality   = Integer.parseInt(lineElements[posQuality]);
                int yearBuilt = Integer.parseInt(lineElements[posYear]);

                Coordinate coordinate = new Coordinate(Double.parseDouble(lineElements[posCoordX]), Double.parseDouble(lineElements[posCoordY]));

                Dwelling dwelling = dwellingFactory.createDwelling(id, zoneId, coordinate, hhId, type, area, quality, price, yearBuilt);
                dwellingData.addDwelling(dwelling);
                if (id == SiloUtil.trackDd) {
                    SiloUtil.trackWriter.println("Read dwelling with following attributes from " + path);
                    SiloUtil.trackWriter.println(dwelling.toString());
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop dwelling file: " + path);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " dwellings.");
    }
}
