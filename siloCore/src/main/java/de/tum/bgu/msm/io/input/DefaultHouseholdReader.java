package de.tum.bgu.msm.io.input;

import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DefaultHouseholdReader implements HouseholdReader{

    private final static Logger logger = Logger.getLogger(DefaultHouseholdReader.class);
    private final HouseholdDataManager householdData;
    private final HouseholdFactory factory;

    public DefaultHouseholdReader(HouseholdDataManager householdData, HouseholdFactory factory) {
        this.householdData = householdData;
        this.factory = factory;
    }

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

                Household hh = factory.createHousehold(id, dwellingID, autos);  // this automatically puts it in id->household map in Household class
                householdData.addHousehold(hh);
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
