package de.tum.bgu.msm.syntheticPopulationGenerator.munich.allocation;

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ReadPopulation {

    private static final Logger logger = Logger.getLogger(ReadPopulation.class);
    private final SiloDataContainer dataContainer;

    public ReadPopulation(SiloDataContainer dataContainer){
        this.dataContainer = dataContainer;
    }

    public void run(){
        logger.info("   Running module: read population");
        readHouseholdData(Properties.get().main.startYear);
        readPersonData(Properties.get().main.startYear);
        readDwellingData(Properties.get().main.startYear);
        readJobData(Properties.get().main.startYear);
    }


    private void readHouseholdData(int year) {
        logger.info("Reading household micro data from ascii file");

        HouseholdDataManager householdData = dataContainer.getHouseholdData();
        String fileName = Properties.get().main.baseDirectory + Properties.get().householdData.householdFileName;
        fileName += "_" + year + ".csv";

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
                int taz        = Integer.parseInt(lineElements[posTaz]);
                int autos      = Integer.parseInt(lineElements[posAutos]);

                householdData.createHousehold(id, dwellingID, autos);  // this automatically puts it in id->household map in Household class
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


    private void readPersonData(int year) {
        logger.info("Reading person micro data from ascii file");

        HouseholdDataManager householdData = dataContainer.getHouseholdData();
        String fileName = Properties.get().main.baseDirectory +  Properties.get().householdData.personFileName;
        fileName += "_" + year + ".csv";

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId = SiloUtil.findPositionInArray("id", header);
            int posHhId = SiloUtil.findPositionInArray("hhid",header);
            int posAge = SiloUtil.findPositionInArray("age",header);
            int posGender = SiloUtil.findPositionInArray("gender",header);
            int posRelShp = SiloUtil.findPositionInArray("relationShip",header);
            int posRace = SiloUtil.findPositionInArray("race",header);
            int posOccupation = SiloUtil.findPositionInArray("occupation",header);
            int posWorkplace = SiloUtil.findPositionInArray("workplace",header);
            int posIncome = SiloUtil.findPositionInArray("income",header);
            int posNationality = SiloUtil.findPositionInArray("nationality",header);
            int posEducation = SiloUtil.findPositionInArray("education",header);
            int posHomeZone = SiloUtil.findPositionInArray("homeZone",header);
            int posWorkZone = SiloUtil.findPositionInArray("workZone",header);
            int posLicense = SiloUtil.findPositionInArray("driversLicense",header);
            int posSchoolDE = SiloUtil.findPositionInArray("schoolDE",header);
            int posSchoolTAZ = SiloUtil.findPositionInArray("schoolTAZ",header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id         = Integer.parseInt(lineElements[posId]);
                int hhid       = Integer.parseInt(lineElements[posHhId]);
                int age        = Integer.parseInt(lineElements[posAge]);
                int gender     = Integer.parseInt(lineElements[posGender]);
                String relShp  = lineElements[posRelShp].replace("\"", "");
                PersonRole pr  = PersonRole.valueOf(relShp.toUpperCase());
                String strRace = lineElements[posRace].replace("\"", "");
                Race race = Race.valueOf(strRace);
                int occupation = Integer.parseInt(lineElements[posOccupation]);
                int workplace  = Integer.parseInt(lineElements[posWorkplace]);
                int income     = Integer.parseInt(lineElements[posIncome]);
                Person pp = householdData.createPerson(id, age, gender, race, occupation, workplace, income); //this automatically puts it in id->person map in Person class
                pp.setRole(pr);
                householdData.addPersonToHousehold(pp, householdData.getHouseholdFromId(hhid));
                String nationality = lineElements[posNationality];
                Nationality nat = Nationality.german;
                if (nationality.equals("other")){
                    nat = Nationality.other;
                }
                int education = Integer.parseInt(lineElements[posEducation]);int workZone = Integer.parseInt(lineElements[posWorkZone]);
                String licenseStr = lineElements[posLicense];
                boolean license = false;
                if (licenseStr.equals("true")){
                    license = true;
                }
                int schoolDE = Integer.parseInt(lineElements[posSchoolDE]);
                int schoolTAZ = Integer.parseInt(lineElements[posSchoolTAZ]);
                pp.setNationality(nat);
                pp.setEducationLevel(education);
                pp.setJobTAZ(workZone);
                pp.setSchoolPlace(schoolTAZ);
                pp.setSchoolType(schoolDE);
                pp.setDriverLicense(license);
                if (id == SiloUtil.trackPp) {
                    SiloUtil.trackWriter.println("Read person with following attributes from " + fileName);
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " persons.");
    }


    private void readDwellingData(int year) {
        // read dwelling micro data from ascii file

        logger.info("Reading dwelling micro data from ascii file");
        RealEstateDataManager realEstate = dataContainer.getRealEstateData();
        String fileName = Properties.get().main.baseDirectory + Properties.get().realEstate.dwellingsFile;
        fileName += "_" + year + ".csv";

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId      = SiloUtil.findPositionInArray("id", header);
            int posZone    = SiloUtil.findPositionInArray("zone",header);
            int posHh      = SiloUtil.findPositionInArray("hhId",header);
            int posType    = SiloUtil.findPositionInArray("type",header);
            int posRooms   = SiloUtil.findPositionInArray("bedrooms",header);
            int posQuality = SiloUtil.findPositionInArray("quality",header);
            int posCosts   = SiloUtil.findPositionInArray("monthlyCost",header);
            int posRestr   = SiloUtil.findPositionInArray("restriction",header);
            int posYear    = SiloUtil.findPositionInArray("yearBuilt",header);
            int posFloor   = SiloUtil.findPositionInArray("floor",header);
            int posBuilding= SiloUtil.findPositionInArray("building",header);
            int posUse     = SiloUtil.findPositionInArray("usage",header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id        = Integer.parseInt(lineElements[posId]);
                int zone      = Integer.parseInt(lineElements[posZone]);
                int hhId      = Integer.parseInt(lineElements[posHh]);
                String tp     = lineElements[posType].replace("\"", "");
                DwellingType type = DwellingType.valueOf(tp);
                int price     = Integer.parseInt(lineElements[posCosts]);
                int area      = Integer.parseInt(lineElements[posRooms]);
                int quality   = Integer.parseInt(lineElements[posQuality]);
                float restrict  = Float.parseFloat(lineElements[posRestr]);
                int yearBuilt = Integer.parseInt(lineElements[posYear]);
                Dwelling dd = realEstate.createDwelling(id, zone, hhId, type, area, quality, price, restrict, yearBuilt);   // this automatically puts it in id->dwelling map in Dwelling class
                if (id == SiloUtil.trackDd) {
                    SiloUtil.trackWriter.println("Read dwelling with following attributes from " + fileName);
                }
                int floor = Integer.parseInt(lineElements[posFloor]);
                int building = Integer.parseInt(lineElements[posBuilding]);
                int use = Integer.parseInt(lineElements[posUse]);
                dd.setFloorSpace(floor);
                dd.setBuildingSize(building);
                dd.setUsage(Dwelling.Usage.valueOf(use));
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop dwelling file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " dwellings.");
    }


    private void readJobData(int year) {
        logger.info("Reading job micro data from ascii file");

        JobDataManager jobDataManager = dataContainer.getJobData();
        String fileName = Properties.get().main.baseDirectory + Properties.get().jobData.jobsFileName;
        fileName += "_" + year + ".csv";

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId = SiloUtil.findPositionInArray("id", header);
            int posZone = SiloUtil.findPositionInArray("zone",header);
            int posWorker = SiloUtil.findPositionInArray("personId",header);
            int posType = SiloUtil.findPositionInArray("type",header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id      = Integer.parseInt(lineElements[posId]);
                int zone    = Integer.parseInt(lineElements[posZone]);
                int worker  = Integer.parseInt(lineElements[posWorker]);
                String type = lineElements[posType].replace("\"", "");
                jobDataManager.createJob(id, zone, worker, type);
                if (id == SiloUtil.trackJj) {
                    SiloUtil.trackWriter.println("Read job with following attributes from " + fileName);
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop job file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " jobs.");
    }

}
