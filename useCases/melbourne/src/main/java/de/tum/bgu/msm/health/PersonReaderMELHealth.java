package de.tum.bgu.msm.health;

import de.tum.bgu.msm.data.Ethnic;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.io.input.PersonReader;
import de.tum.bgu.msm.util.parseMEL;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PersonReaderMELHealth implements PersonReader {

    private final static Logger logger = LogManager.getLogger(PersonReaderMELHealth.class);
    private final HouseholdDataManager householdDataManager;

    public PersonReaderMELHealth(HouseholdDataManager householdDataManager) {
        this.householdDataManager = householdDataManager;
    }

    @Override
    public void readData(String path) {
        logger.info("Reading person micro data from ascii file ({})", path);

        PersonFactoryMELHealth ppFactory = new PersonFactoryMELHealth();
        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            recString = in.readLine();

            // read header
            String[] header = parseMEL.stringParse(recString.split(","));
            int posId = SiloUtil.findPositionInArray("id", header);
            int posHhId = SiloUtil.findPositionInArray("hhID",header);
            int posAge = SiloUtil.findPositionInArray("age",header);
            int posGender = SiloUtil.findPositionInArray("gender",header);
            int posRelShp = SiloUtil.findPositionInArray("relationShip",header);
            int posOccupation = SiloUtil.findPositionInArray("occupation",header);
            int posWorkplace = SiloUtil.findPositionInArray("workplace",header);
            int posIncome = SiloUtil.findPositionInArray("income",header);
            int posDriver = SiloUtil.findPositionInArray("driversLicence", header);
            int posSchoolId = SiloUtil.findPositionInArray("schoolId", header);
            int posEthnic = SiloUtil.findPositionInArray("ethnic", header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = parseMEL.stringParse(recString).split(",");
                int id         = parseMEL.intParse(lineElements[posId]);
                int hhid       = parseMEL.intParse(lineElements[posHhId]);
                int age        = parseMEL.intParse(lineElements[posAge]);
                String genderString     = parseMEL.stringParse(lineElements[posGender]);
                Gender gender;
                if ("Female".equalsIgnoreCase(genderString)) {
                    gender = Gender.FEMALE;
                } else if ("Male".equalsIgnoreCase(genderString)) {
                    gender = Gender.MALE;
                } else {
                    throw new IllegalArgumentException("Invalid gender value: " + genderString);
                }
                String relShp  = parseMEL.stringParse(lineElements[posRelShp]);
                // only single, married and child are implemented in person role currently; and ill fit for modern household structures. or at least the Melbourne synthetic population is not a tight fit for this typology.
                if ("LONE_PERSON".equalsIgnoreCase(relShp) || "LONE_PARENT".equalsIgnoreCase(relShp) || "RELATIVE".equalsIgnoreCase(relShp)) {
                    relShp = "SINGLE";
                } else if ("GROUP_HOUSEHOLD".equalsIgnoreCase(relShp)) {
                    relShp = "MARRIED";
                } else if ("U15_CHILD".equalsIgnoreCase(relShp) || "O15_CHILD".equalsIgnoreCase(relShp) || "STUDENT".equalsIgnoreCase(relShp)) {
                    relShp = "CHILD";
                }
                PersonRole pr  = PersonRole.valueOf(relShp.toUpperCase());
                Occupation occupation = Occupation.valueOf(parseMEL.intParse(lineElements[posOccupation]));
                int workplace  = parseMEL.intParse(lineElements[posWorkplace]);
                //todo remove the workplace if they are not employed - temporary if school ids or zones are stored there
                if (!occupation.equals(Occupation.EMPLOYED)){
                    workplace = -1;
                }
                int income     = Integer.parseInt(lineElements[posIncome]);
                boolean license = Boolean.parseBoolean(lineElements[posDriver]);
                //todo temporary assign driving license since this is not in the current SP version
                //boolean license = MicroDataManager.obtainLicense(gender, age);

                Household household = householdDataManager.getHouseholdFromId(hhid);
                if(household == null) {
                    throw new RuntimeException("Person " + id + " refers to non existing household " + hhid + "!");
                }
                PersonHealthMEL pp = ppFactory.createPerson(id, age, gender, occupation,pr, workplace, income);
                householdDataManager.addPerson(pp);
                householdDataManager.addPersonToHousehold(pp, household);
                pp.setDriverLicense(license);
                final String schoolString = parseMEL.stringParse(lineElements[posSchoolId]);
                final int schoolId;
                if ("NA".equalsIgnoreCase(schoolString)) {
                    schoolId = -1; // Use -1 to indicate missing data
                } else {
                    schoolId = Integer.parseInt(schoolString);
                }
                pp.setSchoolId(schoolId);
                final String ethnicString = parseMEL.stringParse(lineElements[posEthnic]);
                final Ethnic ethnic;
                if ("NA".equalsIgnoreCase(ethnicString)) {
                    ethnic = Ethnic.other;
                } else {
                    ethnic = Ethnic.valueOf(lineElements[posEthnic]);
                }
                pp.setEthnic(ethnic);

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
