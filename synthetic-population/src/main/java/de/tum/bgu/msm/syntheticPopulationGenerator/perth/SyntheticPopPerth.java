package de.tum.bgu.msm.syntheticPopulationGenerator.perth;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobType;
import de.tum.bgu.msm.data.job.JobUtils;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.syntheticPopulationGenerator.SyntheticPopI;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.util.*;

/**
 * Generates a simple synthetic population for the Perth Study Area
 * @author Rolf Moeckel (TUM) & Sonja Stemler (UWA)
 * Created on Oct. 31st, 2018 in Munich
 *
 */

public class

SyntheticPopPerth implements SyntheticPopI {

    protected static final String PROPERTIES_RUN_SP                  = "run.synth.pop.generator";
    protected static final String PROPERTIES_PUMS_FAMILIES           = "pums.families";
    protected static final String PROPERTIES_PUMS_PERSONS            = "pums.persons";
    protected static final String PROPERTIES_PUMS_DWELLINGS          = "pums.dwellings";
    protected static final String PROPERTIES_VACANCY_RATES           = "vacancy.rate.by.type";
    protected static final String PROPERTIES_COUNTY_VACANCY_RATES    = "county.vacancy.rates";
    protected static final String PROPERTIES_VALIDATE_SYNTH_POP      = "validate.synth.pop";

    protected transient Logger logger = Logger.getLogger(SyntheticPopPerth.class);

    private ResourceBundle rb;
    protected HashMap<Integer, int[]> tazByWorkZonePuma;
    protected HouseholdDataManager householdDataManager;
    protected RealEstateDataManager realEstateDataManager;
    protected JobDataManager jobDataManager;
    private JobDataManager jobData;
    protected HashMap<Integer, int[]> vacantJobsByZone;
    private String baseDirectory;
    private PrintWriter pwhh;
    private PrintWriter pwpp;
    private PrintWriter pwdd;
    private PrintWriter pwjj;

    public SyntheticPopPerth(ResourceBundle rb) {
        // constructor
        this.rb = rb;
    }


    public void runSP () {
        // main method to run the synthetic population generator

        if (!ResourceUtil.getBooleanProperty(rb, PROPERTIES_RUN_SP)) return;

        logger.info("Generating synthetic populations of household/persons, dwellings and jobs");
        baseDirectory = Properties.get().main.baseDirectory;
        SiloDataContainer dataContainer = SiloDataContainer.createEmptySiloDataContainer(Implementation.PERTH);
        realEstateDataManager = dataContainer.getRealEstateData();
        householdDataManager = dataContainer.getHouseholdData();
        jobDataManager = dataContainer.getJobData();
        jobData = dataContainer.getJobData();
        //todo: is 2006 the correct year?
        openFilesToWriteSyntheticPopulation(2006);
//        createJobs();
        processMicroData();
//        addVacantDwellings();
//        logger.info ("  Total number of households created " + householdDataManager.getHouseholds().size());
//        logger.info ("  Total number of persons created    " + householdDataManager.getPersons().size());
//        logger.info ("  Total number of dwellings created  " + realEstateDataManager.getDwellings().size());
//        logger.info ("  Total number of jobs created       " + jobData.getJobs().size());
//        calculateVacancyRate();
//        summarizeVacantJobsByRegion();
//        summarizeByPersonRelationship();
//        SummarizeData.writeOutSyntheticPopulation(Properties.get().main.implementation.BASE_YEAR, dataContainer);
        closeFilesForSyntheticPopulation();
        logger.info("  Completed generation of synthetic population");
    }


    private void openFilesToWriteSyntheticPopulation (int year) {
        String filehh = baseDirectory + "/microData/hh_" + year + ".csv";
        String filepp = baseDirectory + "/microData/pp_" + year + ".csv";
        String filedd = baseDirectory + "/microData/dd_" + year + ".csv";
        String filejj = baseDirectory + "/microData/jj_" + year + ".csv";
        pwhh = SiloUtil.openFileForSequentialWriting(filehh, false);
        pwhh.println("id,dwelling,zone,hhSize,autos");
        pwpp = SiloUtil.openFileForSequentialWriting(filepp, false);
        pwpp.println("id,hhid,age,gender,relationShip,race,occupation,workplace,income");
        pwdd = SiloUtil.openFileForSequentialWriting(filedd, false);
        pwdd.println("id,zone,type,hhID,bedrooms,quality,monthlyCost");
        pwjj = SiloUtil.openFileForSequentialWriting(filejj, false);
        pwjj.println("id,zone,personId,type");
    }


    private void closeFilesForSyntheticPopulation() {
        pwhh.close();
        pwpp.close();
        pwdd.close();
        pwjj.close();
    }


    private void createJobs () {
        // method to generate synthetic jobs

        logger.info("  Generating base year jobs");
        TableDataSet jobs = SiloUtil.readCSVfile(Properties.get().jobData.jobControlTotalsFileName);
        new JobType(Properties.get().jobData.jobTypes);

        // jobInventory by [industry][taz]
        // todo: set highest zone id
        final int highestZoneId = 100;
        float[][] jobInventory = new float[JobType.getNumberOfJobTypes()][highestZoneId + 1];
        tazByWorkZonePuma = new HashMap<>();  // this HashMap has same content as "HashMap tazByPuma", though is kept separately in case external workzones will be defined

        // read employment data
        // For reasons that are not explained in the documentation, some of the PUMA work zones were aggregated to the
        // next higher level. Keep this information.

        for (int row = 1; row <= jobs.getRowCount(); row++) {
            int taz = (int) jobs.getValueAt(row, "SMZ");
            //todo relate taz to puma work zone
            int pumaOfWorkZone = taz;
            if (tazByWorkZonePuma.containsKey(pumaOfWorkZone)) {
                int[] list = tazByWorkZonePuma.get(pumaOfWorkZone);
                int[] newList = SiloUtil.expandArrayByOneElement(list, taz);
                tazByWorkZonePuma.put(pumaOfWorkZone, newList);
            } else {
                tazByWorkZonePuma.put(pumaOfWorkZone, new int[]{taz});
            }
            for (int jobTp = 0; jobTp < JobType.getNumberOfJobTypes(); jobTp++) {
                jobInventory[jobTp][taz] = jobs.getValueAt(row, JobType.getJobType(jobTp) + "00");
            }
        }

        // create base year employment
        //todo: get zone array
        for (int zone: new int[5]) {
            for (int jobTp = 0; jobTp < JobType.getNumberOfJobTypes(); jobTp++) {
                if (jobInventory[jobTp][zone] > 0) {
                    for (int i = 1; i <= jobInventory[jobTp][zone]; i++) {
                        int id = jobData.getNextJobId();
                        jobData.addJob(JobUtils.getFactory().createJob (id, zone, null, -1, JobType.getJobType(jobTp)));
                        if (id == SiloUtil.trackJj) {
                            SiloUtil.trackWriter.println("Generated job with following attributes:");
                            SiloUtil.trackWriter.println(jobData.getJobFromId(id).toString());
                        }
                    }
                }
            }
        }
        identifyVacantJobsByZone();
    }


    private void identifyVacantJobsByZone () {
        // populate HashMap with Jobs by zone

        logger.info("  Identifying vacant jobs by zone");
        vacantJobsByZone = new HashMap<>();
        Collection<Job> jobs = jobData.getJobs();
        for (Job jj: jobs) {
            if (jj.getWorkerId() == -1) {
                int id = jj.getId();
                int zone = jj.getZoneId();
                if (vacantJobsByZone.containsKey(zone)) {
                    int[] vacancies = vacantJobsByZone.get(zone);
                    int[] newVacancies = SiloUtil.expandArrayByOneElement(vacancies, id);
                    vacantJobsByZone.put(zone, newVacancies);
                } else {
                    vacantJobsByZone.put(zone, new int[]{id});
                }
            }
        }
    }


    private void processMicroData() {
        // read PUMS data of the Australian Bureau Of Statistics for Population

        logger.info("  Reading Australian PUMS data");

        String pumsFileFamilies = baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_PUMS_FAMILIES);
        TableDataSet pumsFamilies = SiloUtil.readCSVfile(pumsFileFamilies);
        String pumsFilePersons = baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_PUMS_PERSONS);
        TableDataSet pumsPersons = SiloUtil.readCSVfile(pumsFilePersons);
        String pumsFileDwellings = baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_PUMS_DWELLINGS);
        TableDataSet pumsDwellings = SiloUtil.readCSVfile(pumsFileDwellings);

        int hhCount = 0;
        int ppCount = 0;
        int ddCount = 0;
        for (int rowHh = 1; rowHh <= pumsFamilies.getRowCount(); rowHh++) {
            String householdId = pumsFamilies.getStringValueAt(rowHh, "ABSFID Family Record Identifier");
            int hhSize = 0;
            int geographicArea = (int) pumsFamilies.getValueAt(rowHh, "Areaenum Geographic area of enumeration");
            hhCount++;
            boolean personsOfThisHouseholdFound = false;
            int[] age = new int[100];
            int[] sex = new int[100];
            int[] income = new int[100];
            int[] occupation = new int[100];
            int[] race = new int[100];
            int[] industry = new int[100];
            for (int rowPp = 1; rowPp <= pumsPersons.getRowCount(); rowPp++) {
                String personId = pumsPersons.getStringValueAt(rowPp, "ABSFID Family Record Identifier");
                if (personId.equals(householdId)) {
                    personsOfThisHouseholdFound = true;
                    ppCount++;

                    // hier weitere Personen Attribute einfügen
                    int ageGroup = (int) pumsPersons.getValueAt(rowPp, "AGEP Age");
                    age[hhSize] = convertAge(ageGroup);
                    sex[hhSize] = (int) pumsPersons.getValueAt(rowPp, "SEXP Sex");
                    int incomeCode = (int) pumsPersons.getValueAt(rowPp, "INCP Individual Income (weekly)");
                    income[hhSize] = convertIncome(incomeCode);
                    //todo: Check if method below called translateOccupation() can be applied here.
                    occupation[hhSize] = (int) pumsPersons.getValueAt(rowPp, "OCC06P Occupation");
//                    int race = (int) pumsPersons.getValueAt(rowPp, "BPLP Country of Birth of Person");
                    industry[hhSize] = (int) pumsPersons.getValueAt(rowPp, "IND06P Industry of Employment");
//                    int travelmode = (int) pumsPersons.getValueAt(rowPp, "MTWP Method of Travel to Work");
//                    int moved1 = (int) pumsPersons.getValueAt(rowPp, "REGU1P Region of Usual Residence One Year Ago");
//                    int moved5 = (int) pumsPersons.getValueAt(rowPp, "REGU5P Region of Usual Residence Five Years Ago");
//                    int employed = (int) pumsPersons.getValueAt(rowPp, "LFS06P Labour Force Status");
//                    int married = (int) pumsPersons.getValueAt(rowPp, "MSTP Registered Marital Status");
//                    int hoursWorked = (int) pumsPersons.getValueAt(rowPp, "HRSP Hours Worked");
                    hhSize++;
                }
            }
            boolean dwellingOfThisHouseholdFound = false;
            int bedRooms = 0;
            int vacancy = 0;
            int mortgage = 0;
            int rent = 0;
            int type = 0;
            int autos = 0;

            for (int rowDd = 1; rowDd <= pumsDwellings.getRowCount(); rowDd++) {
                String dwellingId = pumsDwellings.getStringValueAt(rowDd, "ABSFID Family Record Identifier");
                if (dwellingId.equals(householdId)) {
                    dwellingOfThisHouseholdFound = true;
                    ddCount++;

                    // hier dwelling attribute einfügen
                    int bedRoomCode = (int) pumsDwellings.getValueAt(rowDd, "BEDD Number of Bedrooms in Private Dwelling");
                    bedRooms = convertBedrooms(bedRoomCode);
                    vacancy = (int) pumsDwellings.getValueAt(rowDd, "DWTD Dwelling Type");
                    int mortgageCode = (int) pumsDwellings.getValueAt(rowDd, "HLRD01 Housing Loan Repayments (monthly) ranges");
                    mortgage = convertMortgage(mortgageCode);
                    int rentCode = (int) pumsDwellings.getValueAt(rowDd, "RNTD01 Rent (weekly) ranges");
                    rent = convertRent(rentCode);
                    type = (int) pumsDwellings.getValueAt(rowDd, "STRD Dwelling Structure");
                    int autoCode = (int) pumsDwellings.getValueAt(rowDd, "VEHD Number of Motor Vehicles");
                    autos = convertAutos(autoCode);
                }
            }
            if (!personsOfThisHouseholdFound)
                logger.error("Could not find any corresponding persons for household with identifier " + householdId + ".");
            if (!dwellingOfThisHouseholdFound)
                logger.error("Could not find any corresponding dwelling for the household with the identifier " + householdId + ".");
            //todo: is the weight 100 correct?
            //todo: is there any quality variable for dwellings? Compare method guessQuality() further below
            savePumsRecord(geographicArea, 100, hhSize, type, bedRooms, autos, rent, mortgage, 4,
                    sex, age, race, occupation, income);
        }

        logger.info("  Read " + hhCount + " PUMS family records from file: " + pumsFileFamilies);
        logger.info("  Read " + ddCount + " PUMS dwelling records from file: " + pumsFileDwellings);
        logger.info("  Read " + ppCount + " PUMS person records from file: " + pumsFilePersons);
    }


    private int convertAge(int ageGroup) {

        // select actual age from bins provided in microdata
        //Ages: 1-25: 0-24 years singly
        //        26: 25-29 years
        //        27: 30–34 years
        //        28: 35–39 years
        //        29: 40–44 years
        //        30: 45–49 years
        //        31: 50–54 years
        //        32: 55–59 years
        //        33: 60–64 years
        //        34: 65–69 years
        //        35: 70–74 years
        //        36: 75–79 years
        //        37: 80–84 years
        //        38: 85 years and over
        int selectedAge = 0;
        float rnd = SiloUtil.getRandomNumberAsFloat();
        if (ageGroup <= 25){
            selectedAge = ageGroup - 1;
        }
        else{
            switch (ageGroup) {
                case 26: selectedAge = (int) (25 + rnd * 5);
                    break;
                case 27: selectedAge = (int) (30 + rnd * 5);
                    break;
                case 28: selectedAge = (int) (35 + rnd * 5);
                    break;
                case 29: selectedAge = (int) (40 + rnd * 5);
                    break;
                case 30: selectedAge = (int) (45 + rnd * 5);
                    break;
                case 31: selectedAge = (int) (50 + rnd * 5);
                    break;
                case 32: selectedAge = (int) (55 + rnd * 5);
                    break;
                case 33: selectedAge = (int) (60 + rnd * 5);
                    break;
                case 34: selectedAge = (int) (65 + rnd * 5);
                    break;
                case 35: selectedAge = (int) (70 + rnd * 5);
                    break;
                case 36: selectedAge = (int) (75 + rnd * 5);
                    break;
                case 37: selectedAge = (int) (80 + rnd * 5);
                    break;
                case 38: selectedAge = (int) (85 + rnd * 15);
                    break;
            }
        }
        return selectedAge;
    }


    private int convertAutos(int autoCode)  {
        // select actual number of autos from indicators provided in ABS microdata
        // 1: None
        // 2: 1 motor vehicle
        // 3: 2 motor vehicles
        // 4: 3 motor vehicles
        // 5: 4 or more motor vehicles
        // 6: Not stated
        // 7: Not applicable
        if (autoCode <= 4){
            return autoCode - 1;
        } else if (autoCode == 5) {
            //todo: I think this will create too many cars. Also, it is irrelevant how many cars you have once you are beyond 4... Why not simply set to 4?
            float rnd = SiloUtil.getRandomNumberAsFloat();
            return (int) (4 + rnd * 5);
        } else {
            return 0;
        }
    }


    private int convertBedrooms(int bedroomCode) {
        // select actual number of from indicators provided in ABS microdata
        //  1: None (includes bedsitters)
        //  2: 1 bedroom
        //  3: 2 bedrooms
        //  4: 3 bedrooms
        //  5: 4 bedrooms
        //  6: 5 or more bedrooms
        //  7: Not stated
        //  8: Not applicable
        if (bedroomCode <= 5){
            return bedroomCode - 1;
        }
        else if (bedroomCode == 6) {
            float rnd = SiloUtil.getRandomNumberAsFloat();
            return (int) (5 + rnd * 5);
        } else {
            return 0;
        }
    }


    private int convertIncome(int incomeCode) {
        // select actual income from bins provided in microdata
        //  1: Negative income
        //  2: Nil income
        //  3: $1–$149
        //  4: $150–$249
        //  5: $250–$399
        //  6: $400–$599
        //  7: $600–$799
        //  8: $800–$999
        //  9: $1,000–$1,299
        // 10: $1,300–$1,599
        // 11: $1,600–$1,999
        // 12: $2,000 or more
        // 13: Not stated
        // 14: Not applicable
        // 15: Overseas visitor
        float rnd = SiloUtil.getRandomNumberAsFloat();
        switch (incomeCode) {
            case 1: return 0;
            case 2: return 0;
            case 3: return (int) (1 + rnd * 150);
            case 4: return (int) (150 + rnd * 100);
            case 5: return (int) (250 + rnd * 150);
            case 6: return (int) (400 + rnd * 200);
            case 7: return (int) (600 + rnd * 200);
            case 8: return (int) (800 + rnd * 200);
            case 9: return (int) (1000 + rnd * 300);
            case 10: return (int) (1300 + rnd * 300);
            case 11: return (int) (1600 + rnd * 400);
            case 12: return (int) (2000 + rnd * 20000);
            case 13: return 0;
            case 14: return 0;
            case 15: return 0;
            default: logger.error("Unknown income code " + incomeCode);
            return 0;
        }
    }


    private int convertMortgage(int mortgageCode) {
        // select actual mortgage from bins provided in microdata
        //  1: $1–$149
        //  2: $150–$249
        //  3: $250–$399
        //  4: $400–$549
        //  5: $550–$649
        //  6: $650–$749
        //  7: $750–$849
        //  8: $850–$949
        //  9: $950–$1,04
        // 10: $1,050–$1,199
        // 11: $1,200–$1,399
        // 12: $1,400–$1,599
        // 13: $1,600–$1,999
        // 14: $2,000–$2,399
        // 15: $2,400–$2,999
        // 16: $3,000–$3,999
        // 17: $4,000 and over
        // 18: Not stated
        // 19: Not applicable

        int selectedMortgage = 0;
        float rnd = SiloUtil.getRandomNumberAsFloat();
        switch (mortgageCode) {
            case 1: selectedMortgage = (int) (1 + rnd * 149);
                break;
            case 2: selectedMortgage = (int) (150 + rnd * 100);
                break;
            case 3: selectedMortgage = (int) (250 + rnd * 150);
                break;
            case 4: selectedMortgage = (int) (400 + rnd * 150);
                break;
            case 5: selectedMortgage = (int) (550 + rnd * 100);
                break;
            case 6: selectedMortgage = (int) (650 + rnd * 100);
                break;
            case 7: selectedMortgage = (int) (750 + rnd * 100);
                break;
            case 8: selectedMortgage = (int) (850 + rnd * 100);
                break;
            case 9: selectedMortgage = (int) (950 + rnd * 100);
                break;
            case 10: selectedMortgage = (int) (1050 + rnd * 150);
                break;
            case 11: selectedMortgage = (int) (1200 + rnd * 200);
                break;
            case 12: selectedMortgage = (int) (1400 + rnd * 200);
                break;
            case 13: selectedMortgage = (int) (1600 + rnd * 400);
                break;
            case 14: selectedMortgage = (int) (2000 + rnd * 400);
                break;
            case 15: selectedMortgage = (int) (2400 + rnd * 600);
                break;
            case 16: selectedMortgage = (int) (3000 + rnd * 1000);
                break;
            case 17: selectedMortgage = (int) (4000 + rnd * 1000000);
                break;
            case 18: selectedMortgage = 0;
            // todo: Check if case 18 ever appears
                break;
            case 19: selectedMortgage = 0;
            // todo: Check if case 19 ever appears
                break;
        }
        return selectedMortgage;
    }


    private int convertRent (int rentCode) {
        // select actual rent from bins provided in microdata
        //  1: $0–$49
        //  2: $50–$74
        //  3: $75–$99
        //  4: $100–$119
        //  5: $120–$139
        //  6: $140–$159
        //  7: $160–$179
        //  8: $180–$199
        //  9: $200–$224
        // 10: $225–$249
        // 11: $250–$274
        // 12: $275–$299
        // 13: $300–$349
        // 14: $350–$449
        // 15: $450–$549
        // 16: $550 and over
        // 17: Not stated
        // 18: Not applicable
        int selectedRent = 0;
        float rnd = SiloUtil.getRandomNumberAsFloat();
        switch (rentCode) {
            case 1: selectedRent = (int) (0 + rnd * 50);
                break;
            case 2: selectedRent = (int) (50 + rnd * 25);
                break;
            case 3: selectedRent = (int) (75 + rnd * 25);
                break;
            case 4: selectedRent = (int) (100 + rnd * 20);
                break;
            case 5: selectedRent = (int) (120 + rnd * 20);
                break;
            case 6: selectedRent = (int) (140 + rnd * 20);
                break;
            case 7: selectedRent = (int) (160 + rnd * 20);
                break;
            case 8: selectedRent = (int) (180 + rnd * 20);
                break;
            case 9: selectedRent = (int) (200 + rnd * 20);
                break;
            case 10: selectedRent = (int) (225 + rnd * 25);
                break;
            case 11: selectedRent = (int) (250 + rnd * 25);
                break;
            case 12: selectedRent = (int) (275 + rnd * 25);
                break;
            case 13: selectedRent = (int) (300 + rnd * 50);
                break;
            case 14: selectedRent = (int) (350 + rnd * 20);
                break;
            case 15: selectedRent = (int) (450 + rnd * 25);
                break;
            case 16: selectedRent = (int) (550 + rnd * 2450);
                break;
            case 17: selectedRent = 0;
            // todo: Check if code 17 ever appears
                break;
            case 18: selectedRent = 0;
            // todo: Check if code 18 ever appears
                break;
        }
        return selectedRent;
    }


    private int guessQuality(int completePlumbing, int completeKitchen, int yearBuilt) {
        // guess quality of dwelling based on plumbing and kitchen
        int quality = Properties.get().main.qualityLevels;
        if (completeKitchen == 2) quality--;
        if (completePlumbing == 2) quality--;
        if (yearBuilt > 0) {
            //Ages: 1. 1999 to 2000, 2. 1995 to 1998, 3. 1990 to 1994, 4. 1980 to 1989, 5. 1970 to 1979, 6. 1960 to 1969, 7. 1950 to 1959, 8. 1940 to 1949, 9. 1939 or earlier
            float[] deteriorationProbability = {0.04f,0.08f,0.12f,0.2f,0.28f,0.36f,0.48f,0.6f,0.8f};
            float prob = deteriorationProbability[yearBuilt-1];
            // attempt drop quality by age two times (to get some spreading of quality levels)
            quality = quality - SiloUtil.select(new double[]{1-prob ,prob});
            quality = quality - SiloUtil.select(new double[]{1-prob, prob});
        }
        quality = Math.max(quality, 1);      // ensure that quality never drops below 1
        return quality;
    }


    private Race defineRace (int hispanic, int singleRace) {
        // define race: 1 white, 2 black, 3 hispanic, 4 other
        if (hispanic > 1) return Race.hispanic;
        if (singleRace == 1) return Race.white;
        else if (singleRace == 2) return Race.black;
        return Race.other;
    }


    private void savePumsRecord (int pumaZone, int weight, int hhSize, int pumsDdType, int bedRooms,
                                 int autos, int rent, int mortgage, int quality, int[] gender, int[] age,
                                 int[] race, int[] occupation, int[] income) {
        // add record to PUMS record storage

        // todo: need to select TAZ within pumaZone
        for (int count = 0; count < weight; count++) {
            //Write Dwellings
            int newDdId = RealEstateDataManager.getNextDwellingId();
            //todo: Please check if there are any vacant dwellings in your dataset; if so, skip writing hh and pp for this records
            int newHhId = householdDataManager.getNextHouseholdId();
            //todo: Please check if you want the next step
            int price = getDwellingPrice(rent, mortgage);
            pwdd.println(newDdId+","+pumaZone+","+pumsDdType+","+newHhId+","+bedRooms+","+quality+","+price);

            //Write Households
            pwhh.println(newHhId+","+newDdId+","+pumaZone+","+hhSize+","+autos);

            //Write Persons
            //todo: Develop plausible method to assign person roles married, single and child
            String[] personRoles = definePersonRolesInHousehold(hhSize);
            for (int s = 0; s < hhSize; s++) {
                int newPpId = householdDataManager.getNextPersonId();
                pwpp.println(newPpId+","+newHhId+","+age[s]+","+gender[s]+","+"RELATIONSHIP_MISSING"+","+race[s]+","+
                        occupation[s]+","+"WORKPLACE_MISSING"+","+income[s]);
            }
        }
    }


/*
    private int locateDwelling (int pumaZone) {
        // select TAZ within PUMA zone

        int[] zones = tazByPuma.get(pumaZone);
        float[] weights = new float[zones.length];
        for (int i = 0; i < zones.length; i++) weights[i] = hhDistribution.getIndexedValueAt(zones[i], "HH00");
        if (SiloUtil.getSum(weights) == 0) logger.error("No weights found to allocate dwelling. Check method " +
                "<locateDwelling> in <SyntheticPopUs.java>");
        int select = SiloUtil.select(weights);
        return zones[select];
    }
*/


    private int getDwellingPrice (int rent, int mortgage) {
        // calculate price based on rent and mortgage
        int price;
        if (rent > 0 && mortgage > 0) price = (rent + mortgage) / 2;
        else if (rent <= 0 && mortgage > 0) price = mortgage;
        else if (rent > 0 && mortgage <= 0) price = rent;
            // todo: create reasonable price for dwelling
        else price = 500;
        return price;
    }


    private Occupation translateOccupation (int pumsOccupation) {
        // translate PUMS occupation into simpler categories: 0 not employed, not looking for work, 1 employed, 2 unemployed
        switch(pumsOccupation) {
            // 0 . Not in universe (Under 16 years)
            case 0: return Occupation.TODDLER;
            // 1 . Employed, at work
            case 1: return Occupation.EMPLOYED;
            // 2 . Employed, with a job but not at work during week interviewed (on vacation, leave, etc.)
            case 2: return Occupation.EMPLOYED;
            // 3 . Unemployed
            case 3: return Occupation.UNEMPLOYED;
            // 4 . Armed Forces, at work
            case 4: return Occupation.EMPLOYED;
            // 5 . Armed Forces, with a job but not at work
            case 5: return Occupation.EMPLOYED;
            // 6 . Not in labor force
            case 6: return Occupation.RETIREE;
            default: return Occupation.RETIREE;
        }
    }


    private String[] definePersonRolesInHousehold (int hhSize ) {
        // define roles as single, married or child

        String[] personRoles = new String[hhSize];
        for (int person = 0; person < hhSize; person++) personRoles[person] = "single";
        return personRoles;
    }



    private int convertToInteger(String s) {
        // converts s to an integer value, one or two leading spaces are allowed

        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            boolean spacesOnly = true;
            for (int pos = 0; pos < s.length(); pos++) {
                if (!s.substring(pos, pos+1).equals(" ")) spacesOnly = false;
            }
            if (spacesOnly) return -999;
            else {
                logger.fatal("String " + s + " cannot be converted into an integer.");
                return 0;
            }
        }
    }

/*
    private void addVacantDwellings () {
        // PUMS generates too few vacant dwellings, add vacant dwellings to match vacancy rate

        logger.info("  Adding empty dwellings to match vacancy rate");

        HashMap<String, ArrayList<Integer>> ddPointer = new HashMap<>();
        // summarize vacancy
        final int highestZoneId = geoData.getZones().keySet().stream().max(Comparator.naturalOrder()).get();
        int[][][] ddCount = new int [highestZoneId + 1][DwellingType.values().length][2];
        for (Dwelling dd: realEstateDataManager.getDwellings()) {
            int taz = dd.getZoneId();
            int occ = dd.getResidentId();
            ddCount[taz][dd.getType().ordinal()][0]++;
            if (occ > 0) ddCount[taz][dd.getType().ordinal()][1]++;
            // set pointer to this dwelling
            String code = taz + "_" + dd.getType();
            if (ddPointer.containsKey(code)) {
                ArrayList<Integer> dList = ddPointer.get(code);
                dList.add(dd.getId());
                ddPointer.put(code, dList);
            } else {
                ArrayList<Integer> dList = new ArrayList<>();
                dList.add(dd.getId());
                ddPointer.put(code, dList);
            }
        }

        TableDataSet countyLevelVacancies = SiloUtil.readCSVfile(rb.getString(PROPERTIES_COUNTY_VACANCY_RATES));
        countyLevelVacancies.buildIndex(countyLevelVacancies.getColumnPosition("Fips"));
        double[] expectedVacancies = ResourceUtil.getDoubleArray(rb, PROPERTIES_VACANCY_RATES);

        for (Zone zone: geoData.getZones().values()) {
            int taz = zone.getZoneId();
            float vacRateCountyTarget;
            try {
                vacRateCountyTarget = countyLevelVacancies.getIndexedValueAt(((MstmZone) zone).getCounty().getId(), "VacancyRate");
            } catch (Exception e) {
                vacRateCountyTarget = countyLevelVacancies.getIndexedValueAt(99999, "VacancyRate");  // use average value
            }
            int ddInThisTaz = 0;
            for (DwellingType dt: DwellingType.values()) {
                String code = taz + "_" + dt;
                if (!ddPointer.containsKey(code)) continue;
                ddInThisTaz += ddPointer.get(code).size();
            }
            int targetVacantDdThisZone = (int) (ddInThisTaz * vacRateCountyTarget + 0.5);
            for (DwellingType dt: DwellingType.values()) {
                String code = taz + "_" + dt;
                if (!ddPointer.containsKey(code)) continue;
                ArrayList<Integer> dList = ddPointer.get(code);
                if (ddCount[taz][dt.ordinal()][0] == 0) continue; // no values for this zone and dwelling type in modeled data
                float vacRateTargetThisDwellingType = (float) expectedVacancies[dt.ordinal()];
                float targetThisTypeThisZoneAbs = (float) (vacRateTargetThisDwellingType /
                        SiloUtil.getSum(expectedVacancies) * targetVacantDdThisZone);
                float vacDwellingsModel = ((float) (ddCount[taz][dt.ordinal()][0] - ddCount[taz][dt.ordinal()][1]));
                Integer[] ids = dList.toArray(new Integer[dList.size()]);
                while (vacDwellingsModel < SiloUtil.rounder(targetThisTypeThisZoneAbs,0)) {
                    int selected = SiloUtil.select(ids.length) - 1;
                    Dwelling dd = realEstateDataManager.getDwelling(ids[selected]);
                    int newDdId = RealEstateDataManager.getNextDwellingId();
                    Dwelling dwelling = DwellingUtils.getFactory().createDwelling(newDdId, zone.getZoneId(), null, -1, dd.getType(), dd.getBedrooms(), dd.getQuality(),
                            dd.getPrice(), 0f, dd.getYearBuilt());
                    realEstateDataManager.addDwelling(dwelling);
                    ddCount[taz][dt.ordinal()][0]++;
                    vacDwellingsModel++;
                    if (newDdId == SiloUtil.trackDd) {
                        SiloUtil.trackWriter.println("Generated vacant dwelling with following attributes:");
                        SiloUtil.trackWriter.println(realEstateDataManager.getDwelling(newDdId).toString());
                    }
                }
            }
        }
    }*/


    private void calculateVacancyRate () {
        //calculate and log vacancy rate

        int[] ddCount = new int[DwellingType.values().length];
        int[] occCount = new int[DwellingType.values().length];
        for (Dwelling dd: realEstateDataManager.getDwellings()) {
            int id = dd.getResidentId();
            DwellingType tp = dd.getType();
            ddCount[tp.ordinal()]++;
            if (id > 0) occCount[tp.ordinal()]++;
        }
        for (DwellingType tp: DwellingType.values()) {
            float rate = SiloUtil.rounder(((float) ddCount[tp.ordinal()] - occCount[tp.ordinal()]) * 100 /
                    ((float) ddCount[tp.ordinal()]), 2);
            logger.info("  Vacancy rate for " + tp + ": " + rate + "%");
        }
    }
}
