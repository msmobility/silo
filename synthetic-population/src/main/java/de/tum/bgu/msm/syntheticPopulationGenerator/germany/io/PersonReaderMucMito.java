package de.tum.bgu.msm.syntheticPopulationGenerator.germany.io;

import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.io.input.PersonReader;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PersonReaderMucMito implements PersonReader {

    private final static Logger logger = Logger.getLogger(PersonReaderMucMito.class);
    private final HouseholdDataManager householdDataManager;

    public PersonReaderMucMito(HouseholdDataManager householdDataManager) {
        this.householdDataManager = householdDataManager;
    }

    @Override
    public void readData(String path) {
        logger.info("Reading person micro data from ascii file");

        PersonFactoryMuc ppFactory = new PersonFactoryMuc();
        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId = SiloUtil.findPositionInArray("id", header);
            int posHhId = SiloUtil.findPositionInArray("hhid",header);
            int posAge = SiloUtil.findPositionInArray("age",header);
            int posGender = SiloUtil.findPositionInArray("gender",header);
            int posOccupation = SiloUtil.findPositionInArray("occupation",header);
            int posLicense = SiloUtil.findPositionInArray("driversLicense",header);
            int posWorkplace = SiloUtil.findPositionInArray("workplace",header);
            int posIncome = SiloUtil.findPositionInArray("income",header);

            int posjobType = SiloUtil.findPositionInArray("jobType",header);
            int posdisability = SiloUtil.findPositionInArray("disability",header);
            int posschoolId = SiloUtil.findPositionInArray("schoolId",header);
            int posschoolType = SiloUtil.findPositionInArray("schoolType",header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id         = Integer.parseInt(lineElements[posId]);
                int hhid       = Integer.parseInt(lineElements[posHhId]);
                int age        = Integer.parseInt(lineElements[posAge]);
                Gender gender     = Gender.valueOf(Integer.parseInt(lineElements[posGender]));
                Occupation occupation = Occupation.valueOf(Integer.parseInt(lineElements[posOccupation]));
                int workplace  = Integer.parseInt(lineElements[posWorkplace]);
                int income     = Integer.parseInt(lineElements[posIncome]);
                PersonMuc pp = (PersonMuc) ppFactory.createPerson(id, age, gender, occupation, PersonRole.SINGLE, workplace, income); //this automatically puts it in id->person map in Person class
                householdDataManager.addPerson(pp);
                householdDataManager.addPersonToHousehold(pp, householdDataManager.getHouseholdFromId(hhid));
                String licenseStr = lineElements[posLicense];
                boolean license = false;
                if (licenseStr.equals("true")){
                    license = true;
                }
                pp.setDriverLicense(license);
                String jobType =  lineElements[posjobType];
                pp.setAdditionalAttributes("jobType",jobType);
                String disability = lineElements[posdisability];
                pp.setAdditionalAttributes("disability",disability);
                int schoolId = Integer.parseInt(lineElements[posschoolId]);
                pp.setSchoolId(schoolId);
                int schoolType = Integer.parseInt(lineElements[posschoolType]);
                pp.setAdditionalAttributes("schoolType",schoolType);

                if (id == SiloUtil.trackPp) {
                    SiloUtil.trackWriter.println("Read person with following attributes from " + path);
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + path);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " persons.");
    }
}
