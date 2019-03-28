package de.tum.bgu.msm.io;

import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.io.input.PersonReader;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PersonReaderCapeTown implements PersonReader {

    private final static Logger logger = Logger.getLogger(PersonReaderCapeTown.class);
    private final HouseholdDataManager householdDataManager;
    private final PersonFactoryCapeTown factory;

    public PersonReaderCapeTown(HouseholdDataManager householdDataManager, PersonFactoryCapeTown factory) {
        this.householdDataManager = householdDataManager;
        this.factory = factory;
    }

    public void readData(String path) {
        logger.info("Reading person micro data from ascii file");

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId = SiloUtil.findPositionInArray("id", header);
            int posHhId = SiloUtil.findPositionInArray("hhID", header);
            int posAge = SiloUtil.findPositionInArray("age", header);
            int posGender = SiloUtil.findPositionInArray("gender", header);
            int posRelShp = SiloUtil.findPositionInArray("relationShip", header);
            int posRace = SiloUtil.findPositionInArray("race",header);
            int posOccupation = SiloUtil.findPositionInArray("occupation", header);
            int posWorkplace = SiloUtil.findPositionInArray("workplace", header);
            int posIncome = SiloUtil.findPositionInArray("income", header);
            int posDriver = SiloUtil.findPositionInArray("driversLicense", header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id = Integer.parseInt(lineElements[posId]);
                int hhid = Integer.parseInt(lineElements[posHhId]);
                int age = Integer.parseInt(lineElements[posAge]);
                Gender gender = Gender.valueOf(Integer.parseInt(lineElements[posGender]));
                String relShp = lineElements[posRelShp].replace("\"", "");
                PersonRole pr = PersonRole.valueOf(relShp.toUpperCase());
                Occupation occupation = Occupation.valueOf(Integer.parseInt(lineElements[posOccupation]));
                int workplace = Integer.parseInt(lineElements[posWorkplace]);
                int income = Integer.parseInt(lineElements[posIncome]);
                String strRace = lineElements[posRace].replace("\"", "").toUpperCase();
                RaceCapeTown race = RaceCapeTown.valueOf(strRace);

                boolean license = Boolean.parseBoolean(lineElements[posDriver]);
                //todo temporary assign driving license since this is not in the current SP version
                //boolean license = MicroDataManager.obtainLicense(gender, age);
                Household household = householdDataManager.getHouseholdFromId(hhid);
                if (household == null) {
                    throw new RuntimeException("Person " + id + " refers to non existing household " + hhid + "!");
                }
                PersonCapeTown pp = factory.createPerson(id, age, gender, occupation, pr, workplace, income);
                pp.setRace(race);
                householdDataManager.addPerson(pp);
                householdDataManager.addPersonToHousehold(pp, household);
                pp.setDriverLicense(license);

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

