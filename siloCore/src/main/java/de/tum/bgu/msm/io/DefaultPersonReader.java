package de.tum.bgu.msm.io;

import com.vividsolutions.jts.geom.Coordinate;
import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DefaultPersonReader implements PersonReader{

    private final static Logger logger = Logger.getLogger(DefaultPersonReader.class);
    private final HouseholdDataManager householdData;

    public DefaultPersonReader(HouseholdDataManager householdData) {
        this.householdData = householdData;
    }

    @Override
    public void readData(String path) {    
        logger.info("Reading person micro data from ascii file");

        PersonFactory ppFactory = PersonUtils.getFactory();
        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId = SiloUtil.findPositionInArray("id", header);
            int posHhId = SiloUtil.findPositionInArray("hhID",header);
            int posAge = SiloUtil.findPositionInArray("age",header);
            int posGender = SiloUtil.findPositionInArray("gender",header);
            int posRelShp = SiloUtil.findPositionInArray("relationShip",header);
            int posRace = SiloUtil.findPositionInArray("race",header);
            int posOccupation = SiloUtil.findPositionInArray("occupation",header);
            int posWorkplace = SiloUtil.findPositionInArray("workplace",header);
            int posIncome = SiloUtil.findPositionInArray("income",header);
            int posDriver = SiloUtil.findPositionInArray("driversLicense", header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id         = Integer.parseInt(lineElements[posId]);
                int hhid       = Integer.parseInt(lineElements[posHhId]);
                int age        = Integer.parseInt(lineElements[posAge]);
                Gender gender     = Gender.valueOf(Integer.parseInt(lineElements[posGender]));
                String relShp  = lineElements[posRelShp].replace("\"", "");
                PersonRole pr  = PersonRole.valueOf(relShp.toUpperCase());
                String strRace = lineElements[posRace].replace("\"", "");
                Race race = Race.valueOf(strRace);
                Occupation occupation = Occupation.valueOf(Integer.parseInt(lineElements[posOccupation]));
                int workplace  = Integer.parseInt(lineElements[posWorkplace]);
                int income     = Integer.parseInt(lineElements[posIncome]);
                boolean license = Boolean.parseBoolean(lineElements[posDriver]);
                //todo temporary assign driving license since this is not in the current SP version
                //boolean license = MicroDataManager.obtainLicense(gender, age);
                Household household = householdData.getHouseholdFromId(hhid);
                if(household == null) {
                    throw new RuntimeException("Person " + id + " refers to non existing household " + hhid + "!");
                }
                Person pp = ppFactory.createPerson(id, age, gender, race, occupation, workplace, income);
                householdData.addPerson(pp);
                householdData.addPersonToHousehold(pp, household);
                pp.setRole(pr);
                pp.setDriverLicense(license);

                //TODO: remove it when we implement interface
                if(Properties.get().main.implementation == Implementation.MUNICH){
                    int posSchoolCoordX = SiloUtil.findPositionInArray("schoolCoordX", header);
                    int posSchoolCoordY = SiloUtil.findPositionInArray("schoolCoordY", header);
                    // TODO Currently only instance where we set a zone id to -1. nk/dz, jul'18
                    Coordinate schoolCoord = new Coordinate(
                            Double.parseDouble(lineElements[posSchoolCoordX]),Double.parseDouble(lineElements[posSchoolCoordY]));
                    pp.setSchoolCoordinate(schoolCoord, -1);
                }

                if (id == SiloUtil.trackPp) {
                    SiloUtil.trackWriter.println("Read person with following attributes from " + path);
                    SiloUtil.trackWriter.println(pp.toString());
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + path);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " persons.");
    }
}
