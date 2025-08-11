package de.tum.bgu.msm.io;

import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.io.input.HouseholdReader;
import de.tum.bgu.msm.util.parseMEL;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class HouseholdReaderMEL implements HouseholdReader {

    private final static Logger logger = LogManager.getLogger(de.tum.bgu.msm.io.input.DefaultHouseholdReader.class);
    private final HouseholdDataManager householdData;
    private final HouseholdFactory factory;

    public HouseholdReaderMEL(HouseholdDataManager householdData, HouseholdFactory factory) {
        this.householdData = householdData;
        this.factory = factory;
    }

    public void readData(String fileName) {
        logger.info("Reading household micro data from ascii file ({})", fileName);

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.replace("\"", "").split(",");
            int posId = SiloUtil.findPositionInArray("id", header);
            int posDwell;
            if (!Arrays.asList(header).contains("dwelling")){
                logger.info("Household file does not contain dwelling information; using household id as dwelling id.");
                posDwell = posId;
            } else {
                posDwell = SiloUtil.findPositionInArray("dwelling", header);
            }
            int posAutos = SiloUtil.findPositionInArray("autos", header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id = parseMEL.intParse(lineElements[posId]);
                int dwellingID = parseMEL.intParse(lineElements[posDwell]);
                int autos = Integer.parseInt(lineElements[posAutos]);

                Household hh = factory.createHousehold(id, dwellingID, autos);  // this automatically puts it in id->household map in Household class
                householdData.addHousehold(hh);
                if (id == SiloUtil.trackHh) {
                    SiloUtil.trackWriter.println("Read household with following attributes from " + fileName);
                    SiloUtil.trackWriter.println(hh.toString());
                }
            }
        } catch (IOException e) {
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
            throw new RuntimeException("IO Exception caught reading synpop household file: " + fileName);
        }
        logger.info("Finished reading " + recCount + " households.");

    }
}
