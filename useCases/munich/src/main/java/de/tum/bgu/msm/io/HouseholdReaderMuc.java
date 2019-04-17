package de.tum.bgu.msm.io;

import de.tum.bgu.msm.data.household.HouseholdData;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdFactoryMuc;
import de.tum.bgu.msm.data.household.HouseholdMuc;
import de.tum.bgu.msm.io.input.HouseholdReader;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class HouseholdReaderMuc implements HouseholdReader {

    private final static Logger logger = Logger.getLogger(HouseholdReaderMuc.class);
    private final HouseholdDataManager hhData;
    private final HouseholdFactoryMuc factory;

    public HouseholdReaderMuc(HouseholdDataManager hhData, HouseholdFactoryMuc factory) {
        this.hhData = hhData;
        this.factory = factory;
    }

    @Override
    public void readData(String fileName) {
        logger.info("Reading household micro data from ascii file");
        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId = SiloUtil.findPositionInArray("id", header);
            int posDwell = SiloUtil.findPositionInArray("dwelling", header);
            int posAutos = SiloUtil.findPositionInArray("autos", header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id = Integer.parseInt(lineElements[posId]);
                int dwellingID = Integer.parseInt(lineElements[posDwell]);
                int autos = Integer.parseInt(lineElements[posAutos]);

                HouseholdMuc hh = factory.createHousehold(id, dwellingID, autos);
                hhData.addHousehold(hh);
                if (id == SiloUtil.trackHh) {
                    SiloUtil.trackWriter.println("Read household with following attributes from " + fileName);
                    SiloUtil.trackWriter.println(hh.toString());
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " households.");

    }
}
