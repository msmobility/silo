package de.tum.bgu.msm.syntheticPopulationGenerator.bangkok.allocation;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypes;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingUtils;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.job.JobFactoryMuc;
import de.tum.bgu.msm.data.job.JobMuc;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.run.data.dwelling.BangkokDwellingTypes;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReadPopulation {

    private static final Logger logger = Logger.getLogger(ReadPopulation.class);
    private final DataContainer dataContainer;
    private HashMap<Person, Integer> educationalLevel;

    ReadPopulation(DataContainer dataContainer, HashMap<Person, Integer> educationalLevel){
        this.dataContainer = dataContainer;
        this.educationalLevel = educationalLevel;
    }

    public void run(){
        logger.info("   Running module: read population");
        readHouseholdData(Properties.get().main.startYear);
        readPersonData(Properties.get().main.startYear);
        checkMarriedPersons();
        readDwellingData(Properties.get().main.startYear);
        readJobData(Properties.get().main.startYear);
    }


    private void readHouseholdData(int year) {
        logger.info("Reading household micro data from ascii file");

        HouseholdDataManager householdData = dataContainer.getHouseholdDataManager();
        HouseholdFactory householdFactory = householdData.getHouseholdFactory();
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
            int posAutos = SiloUtil.findPositionInArray("autos",header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id         = Integer.parseInt(lineElements[posId]);
                int dwellingID = Integer.parseInt(lineElements[posDwell]);
                int autos      = Integer.parseInt(lineElements[posAutos]);

                Household household = householdFactory.createHousehold(id, dwellingID, autos);  // this automatically puts it in id->household map in Household class
                householdData.addHousehold(household);
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

        HouseholdDataManager householdData = dataContainer.getHouseholdDataManager();
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
            int posOccupation = SiloUtil.findPositionInArray("occupation",header);
            int posWorkplace = SiloUtil.findPositionInArray("workplace",header);
            int posIncome = SiloUtil.findPositionInArray("income",header);
            int posLicense = SiloUtil.findPositionInArray("driversLicense",header);


            // read line
            PersonFactoryMuc ppFactory = new PersonFactoryMuc();;
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id         = Integer.parseInt(lineElements[posId]);
                int hhid       = Integer.parseInt(lineElements[posHhId]);
                int age        = Integer.parseInt(lineElements[posAge]);
                Gender gender     = Gender.valueOf(Integer.parseInt(lineElements[posGender]));
                String relShp  = lineElements[posRelShp].replace("\"", "");
                PersonRole pr  = PersonRole.valueOf(relShp.toUpperCase());
                Occupation occupation = Occupation.valueOf(Integer.parseInt(lineElements[posOccupation]));
                int workplace  = Integer.parseInt(lineElements[posWorkplace]);
                int income     = Integer.parseInt(lineElements[posIncome]);
                PersonMuc pp = (PersonMuc) ppFactory.createPerson(id, age, gender, occupation, pr, workplace, income); //this automatically puts it in id->person map in Person class
                householdData.addPerson(pp);
                householdData.addPersonToHousehold(pp, householdData.getHouseholdFromId(hhid));
                String licenseStr = lineElements[posLicense];
                boolean license = false;
                if (licenseStr.equals("true")){
                    license = true;
                }
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
        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        String fileName = Properties.get().main.baseDirectory + Properties.get().realEstate.dwellingsFileName;
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
            int posYear    = SiloUtil.findPositionInArray("yearBuilt",header);
            int posCoordX = -1;
            int posCoordY = -1;
            try {
                posCoordX = SiloUtil.findPositionInArray("coordX", header);
                posCoordY = SiloUtil.findPositionInArray("coordY", header);
            } catch (Exception e) {
                logger.warn("No coords given in dwelling input file. Models using microlocations will not work.");
            }

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id        = Integer.parseInt(lineElements[posId]);
                int zoneId      = Integer.parseInt(lineElements[posZone]);
                int hhId      = Integer.parseInt(lineElements[posHh]);
                String tp     = lineElements[posType].replace("\"", "");
                BangkokDwellingTypes.DwellingTypeBangkok type = BangkokDwellingTypes.DwellingTypeBangkok.valueOf(tp);
                int price     = Integer.parseInt(lineElements[posCosts]);
                int area      = Integer.parseInt(lineElements[posRooms]);
                int quality   = Integer.parseInt(lineElements[posQuality]);
                int yearBuilt = Integer.parseInt(lineElements[posYear]);
                Coordinate coordinate = null;
                if (posCoordX >= 0 && posCoordY >= 0) {
                    try {
                        coordinate = new Coordinate(Double.parseDouble(lineElements[posCoordX]), Double.parseDouble(lineElements[posCoordY]));
                    } catch (Exception e) {
                    }
                }
                Dwelling dd = DwellingUtils.getFactory().createDwelling(id, zoneId, coordinate, hhId, type, area, quality, price, yearBuilt);   // this automatically puts it in id->dwelling map in Dwelling class
                realEstate.addDwelling(dd);
                if (id == SiloUtil.trackDd) {
                    SiloUtil.trackWriter.println("Read dwelling with following attributes from " + fileName);
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop dwelling file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " dwellings.");
    }


    private void readJobData(int year) {
        logger.info("Reading job micro data from ascii file");

        JobDataManager jobDataManager = dataContainer.getJobDataManager();
        JobFactoryMuc jobFactory = (JobFactoryMuc) dataContainer.getJobDataManager().getFactory();
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
            int posCoordX = SiloUtil.findPositionInArray("CoordX", header);
            int posCoordY = SiloUtil.findPositionInArray("CoordY", header);
            int posStartTime = SiloUtil.findPositionInArray("startTime", header);
            int posDuration = SiloUtil.findPositionInArray("duration", header);


            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id      = Integer.parseInt(lineElements[posId]);
                int zoneId    = Integer.parseInt(lineElements[posZone]);
                int worker  = Integer.parseInt(lineElements[posWorker]);
                String type = lineElements[posType].replace("\"", "");
                Coordinate coordinate = null;
                if (posCoordX >= 0 && posCoordY >= 0) {
                    try {
                        coordinate = new Coordinate(Double.parseDouble(lineElements[posCoordX]), Double.parseDouble(lineElements[posCoordY]));
                    } catch (Exception e) {
                    }
                }
                JobMuc jj = jobFactory.createJob(id, zoneId, coordinate, worker, type);
                int startTime = Integer.parseInt(lineElements[posStartTime]);
                int duration = Integer.parseInt(lineElements[posDuration]);
                jj.setJobWorkingTime(startTime, duration);
                jobDataManager.addJob(jj);
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

    private void checkMarriedPersons(){
        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()){
            Map<Integer, Person> marriedMales = new LinkedHashMap<>();
            Map<Integer, Person> marriedFemales = new LinkedHashMap<>();
            int id = 0;
            int femid = 0;
            for (Person pp : hh.getPersons().values()){
                if (pp.getRole().equals(PersonRole.MARRIED) && pp.getGender().equals(Gender.MALE)){
                    marriedMales.put(id++, pp);
                } else if (pp.getRole().equals(PersonRole.MARRIED) && pp.getGender().equals(Gender.FEMALE)) {
                    marriedFemales.put(femid++, pp);
                }
            }
            if (id != femid){
                int idMarriedFem = 1;
                for (Person pp : marriedMales.values()){
                    if (marriedFemales.get(idMarriedFem) != null){
                        marriedFemales.remove(idMarriedFem);
                        idMarriedFem++;
                        } else {
                           pp.setRole(PersonRole.SINGLE);
                        }
                    }
                for (Person pp: marriedFemales.values()){
                    pp.setRole(PersonRole.SINGLE);
                }
            }

        }
    }

}
