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
                int workplace  = (int) Double.parseDouble(lineElements[posWorkplace]);
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
                pp.setAttribute("jobType",jobType);
                String disability = lineElements[posdisability];
                pp.setAttribute("disability",disability);
                int schoolId = Integer.parseInt(lineElements[posschoolId]);
                pp.setSchoolId(schoolId);
                int schoolType = Integer.parseInt(lineElements[posschoolType]);
                pp.setAttribute("schoolType",schoolType);

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

    public int readDataWithStateAndReassignIds(String path, int finalPpIdPreviousState, int finalHhIdPreviousState, boolean generate) {
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
                if (generate) {
                    String[] lineElements = recString.split(",");
                    int id = Integer.parseInt(lineElements[posId]);
                    int hhid = Integer.parseInt(lineElements[posHhId]);
                    int age = Integer.parseInt(lineElements[posAge]);
                    Gender gender = Gender.valueOf(Integer.parseInt(lineElements[posGender]));
                    Occupation occupation = Occupation.valueOf(Integer.parseInt(lineElements[posOccupation]));
                    int workplace = Integer.parseInt(lineElements[posWorkplace]);
                    int income = Integer.parseInt(lineElements[posIncome]) * 12;
                    int correlativeId = id + finalPpIdPreviousState;
                    PersonMuc pp = (PersonMuc) ppFactory.createPerson(correlativeId, age, gender, occupation, PersonRole.SINGLE, workplace, income); //this automatically puts it in id->person map in Person class
                    householdDataManager.addPerson(pp);
                    int householdId = hhid + finalHhIdPreviousState;
                    householdDataManager.addPersonToHousehold(pp, householdDataManager.getHouseholdFromId(householdId));
                    String licenseStr = lineElements[posLicense];
                    boolean license = false;
                    if (licenseStr.equals("true")) {
                        license = true;
                    }
                    pp.setDriverLicense(license);
                    String jobType = lineElements[posjobType];
                    pp.setAttribute("jobType", jobType);
                    String disability = lineElements[posdisability];
                    pp.setAttribute("disability", disability);
                    int schoolId = Integer.parseInt(lineElements[posschoolId]);
                    pp.setSchoolId(schoolId);
                    int schoolType = Integer.parseInt(lineElements[posschoolType]);
                    pp.setAttribute("schoolType", schoolType);
                    pp.setAttribute("originalId", id);
                    pp.setAttribute("workZone", 0);
                    if (id == SiloUtil.trackPp) {
                        SiloUtil.trackWriter.println("Read person with following attributes from " + path);
                    }
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + path);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " persons.");
        return recCount;
    }

    public int readDataWithState(String path, boolean haveState, boolean haveWorkZone) {
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

            int posState = 0;
            int posOriginalId = 0;
            if (haveState){
                posState = SiloUtil.findPositionInArray("state", header);
                posOriginalId = SiloUtil.findPositionInArray("originalId", header);
            }
            int posWorkZone = 0;
            int posWorkCommute = 0;
            int posSchoolPlace = 0;
            int posEduCommute = 0;
            if (haveWorkZone){
                posWorkZone = SiloUtil.findPositionInArray("workZone", header);
                posWorkCommute = SiloUtil.findPositionInArray("commuteDistanceKm", header);
                posSchoolPlace = SiloUtil.findPositionInArray("schoolPlace", header);
                posEduCommute = SiloUtil.findPositionInArray("commuteEduDistanceKm", header);
            }

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                    String[] lineElements = recString.split(",");
                    int id = Integer.parseInt(lineElements[posId]);
                    int hhid = Integer.parseInt(lineElements[posHhId]);
                    int age = Integer.parseInt(lineElements[posAge]);
                    Gender gender = Gender.valueOf(Integer.parseInt(lineElements[posGender]));
                    Occupation occupation = Occupation.valueOf(Integer.parseInt(lineElements[posOccupation]));
                    int workplace = Integer.parseInt(lineElements[posWorkplace]);
                    int income = Integer.parseInt(lineElements[posIncome]);
                    PersonMuc pp = (PersonMuc) ppFactory.createPerson(id, age, gender, occupation, PersonRole.SINGLE, workplace, income); //this automatically puts it in id->person map in Person class
                    householdDataManager.addPerson(pp);
                    householdDataManager.addPersonToHousehold(pp, householdDataManager.getHouseholdFromId(hhid));
                    String licenseStr = lineElements[posLicense];
                    boolean license = false;
                    if (licenseStr.equals("true")) {
                        license = true;
                    }
                    pp.setDriverLicense(license);
                    String jobType = lineElements[posjobType];
                    pp.setAttribute("jobType", jobType);
                    String disability = lineElements[posdisability];
                    pp.setAttribute("disability", disability);
                    int schoolId = Integer.parseInt(lineElements[posschoolId]);
                    pp.setSchoolId(schoolId);
                    pp.setAttribute("schoolId", schoolId);
                    int schoolType = Integer.parseInt(lineElements[posschoolType]);
                    pp.setAttribute("schoolType", schoolType);
                    if (haveState) {
                        int originalId = Integer.parseInt(lineElements[posOriginalId]);
                        String state = (lineElements[posState]);
                        pp.setAttribute("originalId", originalId);
                        pp.setAttribute("state", state);
                    } else {
                        pp.setAttribute("originalId", id);
                        pp.setAttribute("state", "");
                    }
                    if (haveWorkZone) {
                        int workzone = Integer.parseInt(lineElements[posWorkZone]);
                        pp.setAttribute("workZone", workzone);
                        double commuteDistace = Double.parseDouble(lineElements[posWorkCommute]);
                        pp.setAttribute("commuteDistance", commuteDistace);
                        int schoolPlace = Integer.parseInt(lineElements[posSchoolPlace]);
                        pp.setAttribute("schoolPlace", schoolPlace);
                        double commuteEduDistance = Double.parseDouble(lineElements[posEduCommute]);
                        pp.setAttribute("commuteEduDistance", commuteEduDistance);
                    } else {
                        pp.setAttribute("workZone", 0);
                        pp.setAttribute("commuteDistance", 0);
                        pp.setAttribute("schoolPlace", 0);
                        pp.setAttribute("commuteEduDistance", 0);
                    }
                    if (id == SiloUtil.trackPp) {
                        SiloUtil.trackWriter.println("Read person with following attributes from " + path);
                    }

            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + path);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " persons.");
        return recCount;
    }

    public int readDataWithStateSave(String path, int finalPpIdPreviousState, int finalHhIdPreviousState, boolean save) {
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
                int correlativeId = id + finalPpIdPreviousState;
                PersonMuc pp = (PersonMuc) ppFactory.createPerson(correlativeId, age, gender, occupation, PersonRole.SINGLE, workplace, income); //this automatically puts it in id->person map in Person class
                householdDataManager.addPerson(pp);
                int householdId = hhid + finalHhIdPreviousState;
                householdDataManager.addPersonToHousehold(pp, householdDataManager.getHouseholdFromId(householdId));
                String licenseStr = lineElements[posLicense];
                boolean license = false;
                if (licenseStr.equals("true")){
                    license = true;
                }
                pp.setDriverLicense(license);
                String jobType =  lineElements[posjobType];
                pp.setAttribute("jobType",jobType);
                String disability = lineElements[posdisability];
                pp.setAttribute("disability",disability);
                int schoolId = Integer.parseInt(lineElements[posschoolId]);
                pp.setSchoolId(schoolId);
                int schoolType = Integer.parseInt(lineElements[posschoolType]);
                pp.setAttribute("schoolType",schoolType);
                pp.setAttribute("originalId", id);
                if (id == SiloUtil.trackPp) {
                    SiloUtil.trackWriter.println("Read person with following attributes from " + path);
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + path);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " persons.");
        return recCount;
    }
}
