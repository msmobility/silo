package de.tum.bgu.msm.io;

import com.vividsolutions.jts.geom.Coordinate;
import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.RealEstateDataManager;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingFactory;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.data.dwelling.DwellingUtils;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DefaultDwellingReader implements DwellingReader {

    private final static Logger logger = Logger.getLogger(DefaultDwellingReader.class);
    private final RealEstateDataManager realEstate;

    public DefaultDwellingReader(RealEstateDataManager realEstate) {
        this.realEstate= realEstate;
    }

    @Override
    public void readData(String path) {
        DwellingFactory factory = DwellingUtils.getFactory();
        logger.info("Reading dwelling micro data from ascii file");
        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            recString = in.readLine();

            String[] header = recString.split(",");
            int posId      = SiloUtil.findPositionInArray("id", header);
            int posZone    = SiloUtil.findPositionInArray("zone",header);
            int posHh      = SiloUtil.findPositionInArray("hhId",header);
            int posType    = SiloUtil.findPositionInArray("type",header);
            int posRooms   = SiloUtil.findPositionInArray("bedrooms",header);
            int posQuality = SiloUtil.findPositionInArray("quality",header);
            int posCosts   = SiloUtil.findPositionInArray("monthlyCost",header);
            int posRestr   = SiloUtil.findPositionInArray("restriction",header);
            int posYear    = SiloUtil.findPositionInArray("yearBuilt",header);

            int posCoordX = -1;
            int posCoordY = -1;

            if(Properties.get().main.implementation == Implementation.MUNICH) {
                posCoordX = SiloUtil.findPositionInArray("coordX", header);
                posCoordY = SiloUtil.findPositionInArray("coordY", header);
            }

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id        = Integer.parseInt(lineElements[posId]);
                int zoneId      = Integer.parseInt(lineElements[posZone]);
                int hhId      = Integer.parseInt(lineElements[posHh]);
                String tp     = lineElements[posType].replace("\"", "");
                DwellingType type = DwellingType.valueOf(tp);
                int price     = Integer.parseInt(lineElements[posCosts]);
                int area      = Integer.parseInt(lineElements[posRooms]);
                int quality   = Integer.parseInt(lineElements[posQuality]);
                float restrict  = Float.parseFloat(lineElements[posRestr]);
                int yearBuilt = Integer.parseInt(lineElements[posYear]);

                Coordinate coordinate = null;
                //TODO: remove it when we implement interface
                if (Properties.get().main.implementation == Implementation.MUNICH) {
                    coordinate = new Coordinate(Double.parseDouble(lineElements[posCoordX]), Double.parseDouble(lineElements[posCoordY]));
                }
                Dwelling dwelling = factory.createDwelling(id, zoneId, coordinate, hhId, type, area, quality, price, restrict, yearBuilt);
                realEstate.addDwelling(dwelling);
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
