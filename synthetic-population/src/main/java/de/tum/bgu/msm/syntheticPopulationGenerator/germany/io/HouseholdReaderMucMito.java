package de.tum.bgu.msm.syntheticPopulationGenerator.germany.io;

import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdFactoryMuc;
import de.tum.bgu.msm.data.household.HouseholdMuc;
import de.tum.bgu.msm.io.input.HouseholdReader;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class HouseholdReaderMucMito implements HouseholdReader {

    private final static Logger logger = Logger.getLogger(HouseholdReaderMucMito.class);
    private final HouseholdDataManager hhData;
    private final HouseholdFactoryMuc factory;

    public HouseholdReaderMucMito(HouseholdDataManager hhData, HouseholdFactoryMuc factory) {
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
            int posId    = SiloUtil.findPositionInArray("id", header);
            int posDwell = SiloUtil.findPositionInArray("dwelling",header);
            int posTaz   = SiloUtil.findPositionInArray("zone",header);
            int posAutos = SiloUtil.findPositionInArray("autos",header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id         = Integer.parseInt(lineElements[posId]);
                int dwellingID = Integer.parseInt(lineElements[posDwell]);
                int autos      = Integer.parseInt(lineElements[posAutos]);
                int zone       = Integer.parseInt(lineElements[posTaz]);
                Household household = factory.createHousehold(id, dwellingID, autos);  // this automatically puts it in id->household map in Household class
                hhData.addHousehold(household);
                household.setAttribute("zone", zone);
                if (id == SiloUtil.trackHh) {
                    SiloUtil.trackWriter.println("Read household with following attributes from " + fileName);
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " households.");

    }

    public int readDataWithStateAndReassignIds(String fileName, String state, int finalIdPreviousState, boolean generate) {
        logger.info("Reading household micro data from ascii file");
        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId    = SiloUtil.findPositionInArray("id", header);
            int posDwell = SiloUtil.findPositionInArray("dwelling",header);
            int posTaz   = SiloUtil.findPositionInArray("zone",header);
            int posAutos = SiloUtil.findPositionInArray("autos",header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                if (generate) {
                    
                    String[] lineElements = recString.split(",");
                    int id = Integer.parseInt(lineElements[posId]);
                    int dwellingID = Integer.parseInt(lineElements[posDwell]);
                    int autos = Integer.parseInt(lineElements[posAutos]);
                    int zone = Integer.parseInt(lineElements[posTaz]);
                    int correlativeId = id + finalIdPreviousState;
                    Household household = factory.createHousehold(correlativeId, dwellingID, autos);  // this automatically puts it in id->household map in Household class
                    hhData.addHousehold(household);
                    household.setAttribute("zone", zone);
                    household.setAttribute("state", state);
                    household.setAttribute("originalId", id);
                    if (id == SiloUtil.trackHh) {
                        SiloUtil.trackWriter.println("Read household with following attributes from " + fileName);
                    }
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " households.");
        return recCount;
    }

    public int readDataWithState(String fileName, boolean hasState) {
        logger.info("Reading household micro data from ascii file");
        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId    = SiloUtil.findPositionInArray("id", header);
            int posDwell = SiloUtil.findPositionInArray("dwelling",header);
            int posTaz   = SiloUtil.findPositionInArray("zone",header);
            int posAutos = SiloUtil.findPositionInArray("autos",header);
            int posState = 0;
            int posOriginalId = 0;
            if (hasState){
                posState = SiloUtil.findPositionInArray("state", header);
                posOriginalId = SiloUtil.findPositionInArray("originalId", header);
            }
            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                    String[] lineElements = recString.split(",");
                    int id = Integer.parseInt(lineElements[posId]);
                    int dwellingID = Integer.parseInt(lineElements[posDwell]);
                    int autos = Integer.parseInt(lineElements[posAutos]);
                    int zone = Integer.parseInt(lineElements[posTaz]);
                    Household household = factory.createHousehold(id, dwellingID, autos);  // this automatically puts it in id->household map in Household class
                    hhData.addHousehold(household);
                    household.setAttribute("zone", zone);
                    if (hasState) {
                        int originalId = Integer.parseInt(lineElements[posOriginalId]);
                        String state = (lineElements[posState]);
                        household.setAttribute("state", state);
                        household.setAttribute("originalId", originalId);
                    } else {
                        household.setAttribute("state", "");
                        household.setAttribute("originalId", id);
                    }
                    if (id == SiloUtil.trackHh) {
                        SiloUtil.trackWriter.println("Read household with following attributes from " + fileName);
                    }

            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " households.");
        return recCount;
    }

}
