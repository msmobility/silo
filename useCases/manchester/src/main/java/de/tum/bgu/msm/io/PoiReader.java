package de.tum.bgu.msm.io;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.ZoneMCR;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PoiReader {

    private static final Logger logger = LogManager.getLogger(PoiReader.class);
    private DataContainer dataContainer;

    public PoiReader(DataContainer dataContainer) {
        this.dataContainer = dataContainer;
    }

    public void readData(String path) {
        logger.info("Reading poi data from csv file");

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            recString = in.readLine();

            String[] header = recString.split(",");
            int posId = SiloUtil.findPositionInArray("id", header);
            int posZoneId = SiloUtil.findPositionInArray("zone", header);
            int posCoordX = SiloUtil.findPositionInArray("X",header);
            int posCoordY = SiloUtil.findPositionInArray("Y",header);

            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id = Integer.parseInt(lineElements[posId]);
                int zoneId = Integer.parseInt(lineElements[posZoneId]);
                double coordX = Double.parseDouble(lineElements[posCoordX]);
                double coordY = Double.parseDouble(lineElements[posCoordY]);

                ZoneMCR zone = (ZoneMCR) dataContainer.getGeoData().getZones().get(zoneId);
                zone.addMicroDestinations(id, new Coordinate(coordX, coordY));
            }

        } catch (IOException e) {
            logger.fatal("IO Exception caught reading poi file: " + path);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " poi.");
    }
}
