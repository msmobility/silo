package de.tum.bgu.msm.io;

import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.events.DisabilityEvent;
import de.tum.bgu.msm.io.input.PersonReader;
import de.tum.bgu.msm.models.disability.DefaultDisabilityStrategy;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PersonReaderMucDisability implements PersonReader {

    private final static Logger logger = Logger.getLogger(PersonReaderMucDisability.class);
    private final HouseholdDataManager householdDataManager;
    private final DefaultDisabilityStrategy strategy;

    public PersonReaderMucDisability(HouseholdDataManager householdDataManager, DefaultDisabilityStrategy strategy) {
        this.householdDataManager = householdDataManager;
        this.strategy = strategy;
    }

    @Override
    public void readData(String path) {
        logger.info("Reading person micro data from ascii file");

        PersonFactoryMucDisability ppFactory = new PersonFactoryMucDisability();
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
            int posOccupation = SiloUtil.findPositionInArray("occupation", header);
            int posWorkplace = SiloUtil.findPositionInArray("workplace", header);
            int posIncome = SiloUtil.findPositionInArray("income", header);
            int posDriver = SiloUtil.findPositionInArray("driversLicense", header);
            int posDisability = SiloUtil.findPositionInArray("disability", header);

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
                boolean license = Boolean.parseBoolean(lineElements[posDriver]);

                //todo temporary assign driving license since this is not in the current SP version
                //boolean license = MicroDataManager.obtainLicense(gender, age);
                Household household = householdDataManager.getHouseholdFromId(hhid);
                if (household == null) {
                    throw new RuntimeException("Person " + id + " refers to non existing household " + hhid + "!");
                }
                PersonMucDisability pp = ppFactory.createPerson(id, age, gender, occupation, pr, workplace, income);

                //TODO: fix hardcoded nationality
                pp.setNationality(Nationality.GERMAN);

                householdDataManager.addPerson(pp);
                householdDataManager.addPersonToHousehold(pp, household);
                pp.setDriverLicense(license);
               if (Integer.parseInt(lineElements[posDisability]) != 0){
                    Disability disability = checkDisability(pp);
                    pp.setDisability(disability);
                } else {
                    Disability disability = Disability.valueOf(lineElements[posDisability]);
                    pp.setDisability(disability);
                }

                int posSchoolId = SiloUtil.findPositionInArray("schoolId", header);
                int schoolId = Integer.parseInt(lineElements[posSchoolId]);
                if (schoolId == 0) {
                    pp.setSchoolId(-1);
                } else {
                    pp.setSchoolId(schoolId);
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

    public Disability checkDisability(Person person) {
        // check if the person will get a disability on the current year
        if (SiloUtil.getRandomNumberAsDouble() < strategy.calculateBaseYearDisabilityProbability(person)) {
            if (SiloUtil.getRandomNumberAsDouble() < strategy.calculateDisabilityType(person)) {
                return Disability.PHYSICAL;
            } else {
                return Disability.MENTAL;
            }
        }
        return Disability.WITHOUT;
    }
}
