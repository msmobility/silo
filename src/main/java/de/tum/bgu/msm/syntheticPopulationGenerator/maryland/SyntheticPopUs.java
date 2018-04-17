package de.tum.bgu.msm.syntheticPopulationGenerator.maryland;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.maryland.MstmZone;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.models.autoOwnership.maryland.MaryLandCarOwnershipModel;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.data.maryland.GeoDataMstm;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.syntheticPopulationGenerator.SyntheticPopI;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;

import javax.sql.rowset.spi.TransactionalWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Generates a simple synthetic population for the MSTM Study Area
 * @author Rolf Moeckel (NCSG, University of Maryland)
 * Created on Nov 22, 2013 in Wheaton, MD
 *
 */

public class SyntheticPopUs implements SyntheticPopI {

    protected static final String PROPERTIES_RUN_SP                  = "run.synth.pop.generator";
    protected static final String PROPERTIES_PUMS_FILES              = "pums.records";
    protected static final String PROPERTIES_PARTLY_COVERED_PUMAS    = "partly.covered.pumas";
    protected static final String PROPERTIES_AGE_DISTRIBUTION_90PLUS = "age.distribution.90.plus";
    protected static final String PROPERTIES_HOUSEHOLD_CONTROL_TOTAL = "household.control.total";
    protected static final String PROPERTIES_HOUSEHOLD_DISTRIBUTION  = "household.distribution";
    protected static final String PROPERTIES_VACANCY_RATES           = "vacancy.rate.by.type";
    protected static final String PROPERTIES_COUNTY_VACANCY_RATES    = "county.vacancy.rates";
    protected static final String PROPERTIES_VALIDATE_SYNTH_POP      = "validate.synth.pop";
    //    protected static final String PROPERTIES_FILENAME_HH_VALIDATION  = "file.name.hh.validation";
    //    protected static final String PROPERTIES_FILENAME_DD_VALIDATION  = "file.name.dd.validation";

    protected transient Logger logger = Logger.getLogger(SyntheticPopUs.class);
    protected int[] pumas;
    protected int[] simplifiedPumas;
    protected HashMap<Integer, int[]> tazByPuma;
    // For reasons that are not explained in the documentation, some of the PUMA work zones were aggregated to the
    // next higher level. Keep PUMA work zones separate from more detailed PUMA zones.
    protected HashMap<Integer, int[]> tazByWorkZonePuma;
    protected HashMap<String, Integer> householdTarget;
    protected TableDataSet hhDistribution;
    protected HashMap<Integer, int[]> vacantJobsByZone;
    protected HashMap<Integer, Integer> jobErrorCounter;

    private ResourceBundle rb;
    private GeoDataMstm geoData;
    private Accessibility accessibility;
    private RealEstateDataManager realEstateDataManager;
    private HouseholdDataManager householdDataManager;
    private JobDataManager jobData;


    public SyntheticPopUs(ResourceBundle rb) {
        // constructor
        this.rb = rb;
    }


    public void runSP () {
        // main method to run the synthetic population generator

        if (!ResourceUtil.getBooleanProperty(rb, PROPERTIES_RUN_SP)) return;

        logger.info("Generating synthetic populations of household/persons, dwellings and jobs");
        identifyUniquePUMAzones();
        readControlTotals();
        SiloDataContainer dataContainer = SiloDataContainer.createEmptySiloDataContainer(Implementation.MARYLAND);
        jobData = dataContainer.getJobData();
        createJobs();
        geoData = (GeoDataMstm) dataContainer.getGeoData();
        geoData.readData();
        SkimTravelTimes skimTravelTimes = new SkimTravelTimes();
        accessibility = new Accessibility(dataContainer, skimTravelTimes);                        // read in travel times and trip length frequency distribution

        final String transitSkimFile = Properties.get().accessibility.transitSkimFile(Properties.get().main.startYear);
        skimTravelTimes.readSkim(TransportMode.pt, transitSkimFile,
                    Properties.get().accessibility.transitPeakSkim, Properties.get().accessibility.skimFileFactorTransit);

        final String carSkimFile = Properties.get().accessibility.autoSkimFile(Properties.get().main.startYear);
        skimTravelTimes.readSkim(TransportMode.car, carSkimFile,
                    Properties.get().accessibility.autoPeakSkim, Properties.get().accessibility.skimFileFactorCar);

        accessibility.initialize();
        processPums();

        realEstateDataManager = dataContainer.getRealEstateData();
        householdDataManager = dataContainer.getHouseholdData();

        generateAutoOwnership(dataContainer);
        SummarizeData.summarizeAutoOwnershipByCounty(accessibility, dataContainer);
        addVacantDwellings();
        if (ResourceUtil.getBooleanProperty(rb, PROPERTIES_VALIDATE_SYNTH_POP)) validateHHandDD();
        logger.info ("  Total number of households created " + householdDataManager.getHouseholds().size());
        logger.info ("  Total number of persons created    " + householdDataManager.getPersons().size());
        logger.info ("  Total number of dwellings created  " + realEstateDataManager.getDwellings().size());
        logger.info ("  Total number of jobs created       " + jobData.getJobs().size());
        calculateVacancyRate();
        if (!jobErrorCounter.isEmpty()) {
            logger.warn("  Could not find sufficient number of jobs in these PUMA zones (note that most of these " +
                    "zones are outside the MSTM area):");
            Set<Integer> list = jobErrorCounter.keySet();
            for (Integer puma: list) logger.warn("  -> " + puma + " is missing " + jobErrorCounter.get(puma) + " jobs.");
        } else {
            logger.info("  Succeeded in assigning job to every worker.");
        }
//        summarizeVacantJobsByRegion();
//        summarizeByPersonRelationship();
        SummarizeData.writeOutSyntheticPopulation(Properties.get().main.implementation.BASE_YEAR, dataContainer);
//        writeSyntheticPopulation();
        logger.info("  Completed generation of synthetic population");
    }


    private void identifyUniquePUMAzones() {
        // walk through list of zones and collect unique PUMA zone IDs within the study area

        tazByPuma = new HashMap<>();
        ArrayList<Integer> alHomePuma = new ArrayList<>();
        ArrayList<Integer> alWorkPuma = new ArrayList<>();
        for (int taz: geoData.getZoneIdsArray()) {
            int homePuma = geoData.getPUMAofZone(taz);
            int workPuma = geoData.getSimplifiedPUMAofZone(taz);
            if (!alHomePuma.contains(homePuma)) alHomePuma.add(homePuma);
            if (!alWorkPuma.contains(workPuma)) alWorkPuma.add(workPuma);
            if (tazByPuma.containsKey(homePuma)) {
                int[] zones = tazByPuma.get(homePuma);
                int[] newZones = SiloUtil.expandArrayByOneElement(zones, taz);
                tazByPuma.put(homePuma, newZones);
            } else {
                int[] zone = {taz};
                tazByPuma.put(homePuma, zone);
            }
        }
        pumas = SiloUtil.convertIntegerArrayListToArray(alHomePuma);
        simplifiedPumas = SiloUtil.convertIntegerArrayListToArray(alWorkPuma);
    }


    private void readControlTotals () {
        // read control totals of households by size and dwellings

        logger.info("  Reading control total data for households and dwellings");
        TableDataSet pop = SiloUtil.readCSVfile(Properties.get().main.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_HOUSEHOLD_CONTROL_TOTAL));
        householdTarget = new HashMap<>();
        for (int row = 1; row <= pop.getRowCount(); row++) {
            String fips = String.valueOf(pop.getValueAt(row, "Fips"));
            // note: doesn't make much sense to store these data in a HashMap. It's legacy code.
            householdTarget.put(fips, (int) pop.getValueAt(row, "TotalHouseholds"));
        }
        hhDistribution = SiloUtil.readCSVfile(Properties.get().main.baseDirectory +
                ResourceUtil.getProperty(rb, PROPERTIES_HOUSEHOLD_DISTRIBUTION));
        hhDistribution.buildIndex(hhDistribution.getColumnPosition(";SMZ_N"));
    }


    private void createJobs () {
        // method to generate synthetic jobs

        logger.info("  Generating base year jobs");
        TableDataSet jobs = SiloUtil.readCSVfile(Properties.get().jobData.jobControlTotalsFileName);
        new JobType(Properties.get().jobData.jobTypes);

        // jobInventory by [industry][taz]
        float[][] jobInventory = new float[JobType.getNumberOfJobTypes()][geoData.getHighestZonalId() + 1];
        tazByWorkZonePuma = new HashMap<>();  // this HashMap has same content as "HashMap tazByPuma", though is kept separately in case external workzones will be defined

        // read employment data
        // For reasons that are not explained in the documentation, some of the PUMA work zones were aggregated to the
        // next higher level. Keep this information.

        for (int row = 1; row <= jobs.getRowCount(); row++) {
            int taz = (int) jobs.getValueAt(row, "SMZ");
            int pumaOfWorkZone = geoData.getSimplifiedPUMAofZone(taz);
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
        for (int zone: geoData.getZoneIdsArray()) {
            for (int jobTp = 0; jobTp < JobType.getNumberOfJobTypes(); jobTp++) {
                if (jobInventory[jobTp][zone] > 0) {
                    for (int i = 1; i <= jobInventory[jobTp][zone]; i++) {
                        int id = jobData.getNextJobId();
                        jobData.createJob (id, zone, -1, JobType.getJobType(jobTp));
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
                int zone = jj.getZone();
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


    private void processPums() {
        // read PUMS data

        logger.info ("  Reading PUMS data");

        String baseDirectory = Properties.get().main.baseDirectory;
        String partlyCovered = baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_PARTLY_COVERED_PUMAS);
        TableDataSet partlyCoveredPumas = SiloUtil.readCSVfile(partlyCovered);
        int highestPUMA = 5500000;
        float[] pumaScaler = SiloUtil.createArrayWithValue((highestPUMA), 1f);
        for (int row = 1; row <= partlyCoveredPumas.getRowCount(); row++) {
            pumaScaler[(int) partlyCoveredPumas.getValueAt(row, "fullPumaCode")] =
                    partlyCoveredPumas.getValueAt(row, "mstmPop2000") / partlyCoveredPumas.getValueAt(row, "fullPop2000");
        }

        String age90plusFile = baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_AGE_DISTRIBUTION_90PLUS);
        TableDataSet age90plus = SiloUtil.readCSVfile(age90plusFile);
        float[] probAge90plusMale = age90plus.getColumnAsFloat("male");
        float[] probAge90plusFemale = age90plus.getColumnAsFloat("female");

        String[] states = {"MD","DC","DE","PA","VA","WV"};
        int[] stateNumber = {24,11,10,42,51,54};      // FIPS code of String states[]

        jobErrorCounter = new HashMap<>();
        //GeoData geoData = new GeoDataMstm(rb);

        for (int st = 0; st < states.length; st++) {
            String pumsFileName = baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_PUMS_FILES) +
                    states[st] + "/REVISEDPUMS5_" + stateNumber[st] + ".TXT";
            logger.info ("  Creating synthetic population for " + states[st]);
            String recString = "";
            int recCount = 0;
            int hhCount = 0;
            int recInStudyAreaCount = 0;
            try {
                BufferedReader in = new BufferedReader(new FileReader(pumsFileName));
                int hhSize = 0;
                int personCounter = 0;
                // define variables
                int pumaZone = 0;
                int weight = 0;
                int ddType = 0;
                int bedRooms = 0;
                int autos = 0;
                int rent = 0;
                int mortgage = 0;
                int quality = 0;
                int yearBuilt = 0;
                int[] relShp = new int[100];
                int[] gender = new int[100];
                int[] age = new int[100];
                Race[] race = new Race[100];
                int[] occupation = new int[100];
                int[] workPumaZone = new int[100];
                int[] workState = new int[100];
                int[] income = new int[100];
//                boolean[] fullTime = new boolean[100];
                while ((recString = in.readLine()) != null) {
                    recCount++;
                    String recType = recString.substring(0, 1);
                    switch (recType) {
                        case "H":
                            if (hhSize != personCounter) logger.error("Inconsistent PUMS data: Found " + personCounter +
                                    " person(s) in dwelling with " + hhSize + " residents (Record " + (recCount - 1) + ").");
                            hhCount++;
                            hhSize = convertToInteger(recString.substring(105, 107));
                            int vacancy = convertToInteger(recString.substring(110, 111));
                            if ((hhSize != 0 && vacancy != 0) || (hhSize == 0 && vacancy == 0))
                                logger.error("Inconsistent PUMS " + "data: Found hhSize " + hhSize +
                                        " in dwelling with vacancy code " + vacancy + " (rec " + recCount + ")");
                            pumaZone = convertToInteger(recString.substring(9, 11) + recString.substring(13, 18));
                            weight = convertToInteger(recString.substring(101, 105));

                            // some PUMA zones are only partly covered by MSTM study area. Therefore, weight needs
                            // to be reduced by the share of population in this PUMA that is covered by MSTM
                            weight = (int) ((weight * 1f) * pumaScaler[pumaZone] + 0.5);

                            ddType = convertToInteger(recString.substring(114, 116));
                            bedRooms = convertToInteger(recString.substring(123, 124));
                            autos = convertToInteger(recString.substring(133, 134));
                            rent = convertToInteger(recString.substring(161, 165));
                            mortgage = convertToInteger(recString.substring(170, 175));
                            yearBuilt = convertToInteger(recString.substring(117, 118));
                            int completePlumbing = convertToInteger(recString.substring(126, 127));
                            int completeKitchen = convertToInteger(recString.substring(127, 128));
                            quality = guessQuality(completePlumbing, completeKitchen, yearBuilt);
                            personCounter = 0;
                            for (int i = 0; i < gender[i]; i++)
                                gender[i] = 0;   // set gender variable to zero which practically erases previous household
                            break;
                        case "P":
                            relShp[personCounter] = convertToInteger(recString.substring(16, 18));
                            gender[personCounter] = convertToInteger(recString.substring(22, 23));
                            age[personCounter] = convertToInteger(recString.substring(24, 26));
                            if (age[personCounter] >= 90) {
                                if (gender[personCounter] == 1) age[personCounter] = 90 + SiloUtil.select(probAge90plusMale);
                                else age[personCounter] = 90 + SiloUtil.select(probAge90plusFemale);
                            }
                            int hispanic = convertToInteger(recString.substring(27, 29));
                            int singleRace = convertToInteger(recString.substring(37, 38));
                            race[personCounter] = defineRace(hispanic, singleRace);
//                            int school = convertToInteger(recString.substring(48, 49));
                            occupation[personCounter] = convertToInteger(recString.substring(153, 154));
                            workPumaZone[personCounter] = convertToInteger(recString.substring(160, 165));
                            workState[personCounter] = convertToInteger(recString.substring(156,159));
//                            fullTime[personCounter] = false;
//                            int hoursWorked = convertToInteger(recString.substring(240, 242));
//                            if (hoursWorked > 34) fullTime[personCounter] = true;

                            income[personCounter] = Math.max(convertToInteger(recString.substring(296, 303)), 0);  // PUMS reports negative income for loss, which cannot be long-term income
                            personCounter++;
                            break;
                        default:
                            logger.error("Wrong record type in PUMS data in line " + recCount);
                            break;
                    }
                    // "personCounter == hhSize" after all person records for this household have been read
                    if (personCounter == hhSize && checkIfPumaInStudyArea(pumaZone)) {
                        recInStudyAreaCount++;
                        savePumsRecord(pumaZone, weight, hhSize, ddType, bedRooms, autos, rent, mortgage, quality,
                                yearBuilt, gender, age, race, relShp, occupation, workPumaZone, workState, income);
                    }
                }
                logger.info("  Read " + hhCount + " PUMS household records from file: " + pumsFileName);
                logger.info("       " + recInStudyAreaCount + " thereof located in study area");
            } catch (IOException e) {
                logger.fatal("IO Exception caught reading synpop household file: " + pumsFileName);
                logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
            }
        }
    }


    private boolean checkIfPumaInStudyArea(int pumaZone) {
        // check if puma zone is part of study area
        for (int p: pumas) if (pumaZone == p) return true;
        return false;
    }


    private boolean checkIfSimplifiedPumaInStudyArea(int simplifiedPumaZone) {
        // check if puma zone is part of study area
        for (int p: simplifiedPumas) if (simplifiedPumaZone == p) return true;
        return false;
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
                                 int autos, int rent, int mortgage, int quality, int yearBuilt, int[] gender, int[] age,
                                 Race[] race, int[] relShp, int[] occupation, int[] workPumaZone,
                                 int[] workState, int[] income) {
        // add record to PUMS record storage

        if (pumsDdType == 10 || pumsDdType == -999) return;   // skip this record if PUMS dwelling type is 10 (Boat, RV, Van) or -999 (unknown)
        DwellingType ddType = translateDwellingType(pumsDdType);
        for (int count = 0; count < weight; count++) {
            int newDdId = RealEstateDataManager.getNextDwellingId();
            int newHhId;
            if (gender[0] == 0) newHhId = -1;
            else newHhId = householdDataManager.getNextHouseholdId();
            int taz = locateDwelling(pumaZone);

            int price = getDwellingPrice(rent, mortgage);
            int selectedYear = selectYear(yearBuilt);
            realEstateDataManager.createDwelling(newDdId, taz, newHhId, ddType, bedRooms, quality, price, 0, selectedYear);
            if (gender[0] == 0) return;   // this dwelling is empty, do not create household
            Household hh = householdDataManager.createHousehold(newHhId, newDdId, autos);
            for (int s = 0; s < hhSize; s++) {
                int newPpId = householdDataManager.getNextPersonId();

                int occ = translateOccupation(occupation[s]);
                int workplace = -1;
                if (occ == 1) {

                    if (workPumaZone[s]==0 || workState[s] == 0) {
                        // no workplace PUMA provided by PUMS (person did not go to work during week interviewed because of vacation, leave, etc.)
                        workplace = selectWorkplaceByTripLengthFrequencyDistribution(workPumaZone[s], workState[s], taz);
                    } else {
                        // workplace PUMA provided by PUMS
                        // workplace = selectWorkplace(workPumaZone[s], workState[s]);
                        // update: As the distribution of requested workplaces according to PUMS is very different from the available MSTM employment data all jobs are chosen based on trip length frequency distributions
                        workplace = selectWorkplaceByTripLengthFrequencyDistribution(workPumaZone[s], workState[s], taz);
                    }
                    if (workplace != -2) {
                        jobData.getJobFromId(workplace).setWorkerID(newPpId);  // -2 for jobs outside of the study area
                    }
                }
                Person pp = householdDataManager.createPerson(newPpId, age[s], gender[s], race[s], occ, workplace, income[s]);
                householdDataManager.addPersonToHousehold(pp, hh);
            }
            hh.setType();
            hh.determineHouseholdRace();
            definePersonRolesInHousehold(hh, relShp);
            // trace persons, households and dwellings
            for (Person pp: hh.getPersons()) if (pp.getId() == SiloUtil.trackPp) {
                SiloUtil.trackWriter.println("Generated person with following attributes:");
                SiloUtil.trackWriter.println(pp.toString());
            }
            if (newHhId == SiloUtil.trackHh) {
                SiloUtil.trackWriter.println("Generated household with following attributes:");
                SiloUtil.trackWriter.println(hh.toString());
            }
            if (newDdId == SiloUtil.trackDd) {
                SiloUtil.trackWriter.println("Generated dwelling with following attributes:");
                SiloUtil.trackWriter.println(realEstateDataManager.getDwelling(newDdId).toString());
            }

        }
    }


    private int selectYear (int yearBuilt) {
        // select actual year the dwelling was built

        //Ages: 1. 1999 to 2000, 2. 1995 to 1998, 3. 1990 to 1994, 4. 1980 to 1989, 5. 1970 to 1979, 6. 1960 to 1969, 7. 1950 to 1959, 8. 1940 to 1949, 9. 1939 or earlier
        int selectedYear = 0;
        float rnd = SiloUtil.getRandomNumberAsFloat();
        switch (yearBuilt) {
            case 1: selectedYear = (int) (1999 + rnd * 2);
                break;
            case 2: selectedYear = (int) (1995 + rnd * 4);
                break;
            case 3: selectedYear = (int) (1990 + rnd * 5);
                break;
            case 4: selectedYear = (int) (1980 + rnd * 10);
                break;
            case 5: selectedYear = (int) (1970 + rnd * 10);
                break;
            case 6: selectedYear = (int) (1960 + rnd * 10);
                break;
            case 7: selectedYear = (int) (1950 + rnd * 10);
                break;
            case 8: selectedYear = (int) (1940 + rnd * 10);
                break;
            case 9: selectedYear = (int) (1930 + rnd * 10);
                break;
        }
        return selectedYear;
    }


    private DwellingType translateDwellingType (int pumsDdType) {
        // translate 10 PUMA into 6 MetCouncil Dwelling Types

        // todo: consider keeping more dwelling types for MSTM implementation. Available in PUMS:
//        V 01 . A mobile home
//        V 02 . A one-family house detached from any other house
//        V 03 . A one-family house attached to one or more houses
//        V 04 . A building with 2 apartments
//        V 05 . A building with 3 or 4 apartments
//        V 06 . A building with 5 to 9 apartments
//        V 07 . A building with 10 to 19 apartments
//        V 08 . A building with 20 to 49 apartments
//        V 09 . A building with 50 or more apartments
//        V 10 . Boat, RV, van, etc.

        DwellingType type;
        if (pumsDdType == 1) type = DwellingType.MH;
        else if (pumsDdType == 2) type = DwellingType.SFD;
        else if (pumsDdType == 3) type = DwellingType.SFA;
        else if (pumsDdType == 4 || pumsDdType == 5) type = DwellingType.MF234;
        else if (pumsDdType >= 6 && pumsDdType <= 9) type = DwellingType.MF5plus;
        else {
            logger.error("Unknown dwelling type " + pumsDdType + " found in PUMS data.");
            type = null;
        }
        return type;
    }


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


    private int selectWorkplaceByTripLengthFrequencyDistribution (int workPumaZone, int workState, int homeTaz) {
        // for some workers, a workzone is not specified in PUMS data. Select workplace based on trip length frequency distribution

        // todo: A couple of issues deserve further attention:
        // - People working in a RMZ are assigned -2 as workplace. Would be good to select external zones
        // - People living in a RMZ but working in a SMZ are disregarded at this point. Therefore, 917,985 jobs remain vacant in base year
        // - Full-time and part-time employees are not distinguished. Every employee occupies one job, even though jobs should be provided as full-time equivalents in MSTM.
        // - Nobody explicitly works from home, though a couple of people will select a job in their home zone. Should be controlled by number of workers working from home.
        // - School/University locations are not assigned as 'workplace' yet. Note that some worker have a job and go to school at the same time.

        int fullPumaZone = workState * 100000 + workPumaZone;
        if (!checkIfSimplifiedPumaInStudyArea(fullPumaZone) && workPumaZone != 0) return -2;  // person does work in puma zone outside of study area
        int[] zones = geoData.getZoneIdsArray();
        double[] zoneProbability = new double[zones.length];

        for (int zn = 0; zn < zones.length; zn++) {
            if (vacantJobsByZone.containsKey(zones[zn])) {
                int numberOfJobsInThisZone = vacantJobsByZone.get(zones[zn]).length;
                if (numberOfJobsInThisZone > 0) {
                    int distance = (int) (accessibility.getPeakAutoTravelTime(homeTaz, zones[zn]) + 0.5);
                    zoneProbability[zn] = accessibility.getCommutingTimeProbability(distance) * (double) numberOfJobsInThisZone;
                } else {
                    zoneProbability[zn] = 0;
                }
            } else {
                zoneProbability[zn] = 0;
            }
        }

        // in rare cases, no job within the common commute distance is available. Assign job location outside of MSTM area.
        if (SiloUtil.getSum(zoneProbability) == 0) return -2;

        int selectedZoneID = SiloUtil.select(zoneProbability);
        int[] jobsInThisZone = vacantJobsByZone.get(zones[selectedZoneID]);
        int selectedJobIndex = SiloUtil.select(jobsInThisZone.length) - 1;
        int[] newVacancies = SiloUtil.removeOneElementFromZeroBasedArray(jobsInThisZone, selectedJobIndex);
        if (newVacancies.length > 0) {
            vacantJobsByZone.put(zones[selectedZoneID], newVacancies);
        } else {
            vacantJobsByZone.remove(zones[selectedZoneID]);
        }
        return jobsInThisZone[selectedJobIndex];
    }


//    private int selectWorkplace (int pumaZone, int state) {
//        // select a workplace within pumaZone based on PUMS data
//        // Note: Not used as the allocation of work zones in PUMS data appears to be problematic.
//        // Method 'selectWorkplaceByTripLengthFrequencyDistribution()' is used instead.
//
//        if (state > 56) return -2;     // Island area, Puerto Rico, foreign country, or at sea
//        int fullPumaZone = state * 100000 + pumaZone;
//
//        if (!tazByWorkZonePuma.containsKey(fullPumaZone)) return -2;   // works outside MSTM study area
//
//        int [] zonesThisPuma = tazByWorkZonePuma.get(fullPumaZone);
//        double[] weightZone = new double[zonesThisPuma.length];
//        for (int i = 0; i < zonesThisPuma.length; i++) {
//            if (vacantJobsByZone.containsKey(zonesThisPuma[i])) {
//                weightZone[i] = vacantJobsByZone.get(zonesThisPuma[i]).length;
//            } else {
//                weightZone[i] = 0;
//            }
//        }
//        if (SiloUtil.getSum(weightZone) == 0) {
//            if (jobErrorCounter.containsKey(fullPumaZone)) {
//                int count = jobErrorCounter.get(fullPumaZone);
//                jobErrorCounter.put(fullPumaZone, count + 1);
//            } else {
//                jobErrorCounter.put(fullPumaZone, 1);
//            }
//            return -2;                 // make person unemployed because no job could be found
//        }
//        int selectedWorkZoneIndex = SiloUtil.select(weightZone);
//        int[] jobsInThisZone = vacantJobsByZone.get(zonesThisPuma[selectedWorkZoneIndex]);
//        int selectedJobIndex = SiloUtil.select(jobsInThisZone.length - 1);
//        int[] newVacancies = SiloUtil.removeOneElementFromZeroBasedArray(jobsInThisZone, selectedJobIndex);
//        vacantJobsByZone.put(zonesThisPuma[selectedWorkZoneIndex], newVacancies);
//        return jobsInThisZone[selectedJobIndex];
//    }


    private int translateOccupation (int pumsOccupation) {
        // translate PUMS occupation into simpler categories: 0 not employed, not looking for work, 1 employed, 2 unemployed
        switch(pumsOccupation) {
            // 0 . Not in universe (Under 16 years)
            case 0: return 0;
            // 1 . Employed, at work
            case 1: return 1;
            // 2 . Employed, with a job but not at work during week interviewed (on vacation, leave, etc.)
            case 2: return 1;
            // 3 . Unemployed
            case 3: return 2;
            // 4 . Armed Forces, at work
            case 4: return 1;
            // 5 . Armed Forces, with a job but not at work
            case 5: return 1;
            // 6 . Not in labor force
            case 6: return 0;
            default: return 0;
        }
    }


    private void definePersonRolesInHousehold (Household hh, int[] relShp) {
        // define roles as single, married or child

        Person[] pp = hh.getPersons().toArray(new Person[0]);
        HashMap<Integer, Integer> coupleCounter = new HashMap<>();
        coupleCounter.put(1, 0);
        coupleCounter.put(2, 0);
        for (int i = 0; i < pp.length; i++) {
            //      Householder      husband/wife  unmarried Partner
            if (relShp[i] == 1 || relShp[i] == 2 || relShp[i] == 19) {
                int cnt = coupleCounter.get(pp[i].getGender()) + 1;
                coupleCounter.put(pp[i].getGender(), cnt);
            }
        }
        int numberOfCouples = Math.min(coupleCounter.get(1), coupleCounter.get(2));
        int[] marriedPersons = new int[]{numberOfCouples, numberOfCouples};
        if (numberOfCouples > 0) {
            pp[0].setRole(PersonRole.MARRIED);
            marriedPersons[pp[0].getGender()-1] -= 1;
        } else pp[0].setRole(PersonRole.SINGLE);

        for (int i = 1; i < pp.length; i++) {
            if ((relShp[i] == 2 || relShp[i] == 19) && marriedPersons[pp[i].getGender()-1] > 0) {
                pp[i].setRole(PersonRole.MARRIED);
                marriedPersons[pp[i].getGender()-1] -= 1;
                //   natural child     adopted child        step child        grandchild       foster child
            } else if (relShp[i] == 3 || relShp[i] == 4 || relShp[i] == 5 || relShp[i] == 8 || relShp[i] == 20)
                pp[i].setRole(PersonRole.CHILD);
            else pp[i].setRole(PersonRole.SINGLE);
        }
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


    private void generateAutoOwnership (SiloDataContainer dataContainer) {
        // select number of cars for every household
        dataContainer.getJobData().calculateJobDensityByZone();
        MaryLandCarOwnershipModel ao = new MaryLandCarOwnershipModel(dataContainer, accessibility);   // calculate auto-ownership probabilities
        Map<Integer, int[]> households = new HashMap<>();
        for (Household hh: householdDataManager.getHouseholds()) {
            households.put(hh.getId(), null);
        }
    }


    private void addVacantDwellings () {
        // PUMS generates too few vacant dwellings, add vacant dwellings to match vacancy rate

        logger.info("  Adding empty dwellings to match vacancy rate");

        HashMap<String, ArrayList<Integer>> ddPointer = new HashMap<>();
        // summarize vacancy
        int[][][] ddCount = new int [geoData.getHighestZonalId() + 1][DwellingType.values().length][2];
        for (Dwelling dd: realEstateDataManager.getDwellings()) {
            int taz = dd.getZone();
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
            int taz = zone.getId();
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
                    realEstateDataManager.createDwelling(newDdId, dd.getZone(), -1, dd.getType(), dd.getBedrooms(), dd.getQuality(),
                            dd.getPrice(), 0f, dd.getYearBuilt());
                    ddCount[taz][dt.ordinal()][0]++;
                    vacDwellingsModel++;
                    if (newDdId == SiloUtil.trackDd) {
                        SiloUtil.trackWriter.println("Generated vacant dwelling with following attributes:");
                        SiloUtil.trackWriter.println(realEstateDataManager.getDwelling(newDdId).toString());
                    }
                }
            }
        }
    }


    private void validateHHandDD () {
        // compare number of generated households and dwellings with target data
        String dir = Properties.get().main.baseDirectory + "scenOutput/" + Properties.get().main.scenarioName + "/validation/";
        SiloUtil.createDirectoryIfNotExistingYet(dir);
//        String hhFile = dir + rbLandUse.getString(PROPERTIES_FILENAME_HH_VALIDATION);
//        String ddFile = dir + rbLandUse.getString(PROPERTIES_FILENAME_DD_VALIDATION);


        // todo: Continue here!!!
//        logger.info ("  Validating households");
//        int hhByPumaSize[][][] = new int[pumas.length][4][2];  // summary by puma, size and age
//        for (Household hh: Household.getHouseholdArray()) {
//            int taz = Dwelling.getDwelling(hh.getDwellingId()).getZone();
//            int puma = SiloUtil.getPUMAofZone(taz);
//            int hhSize = Math.min(hh.getHhSize(), 4);         // aggregate households >= 4 persons
//            int age = hh.getPersons()[0].getAge();            // get age of householder
//            if (age < 65) hhByPumaSize[SiloUtil.findPositionInArray(puma, pumas)][hhSize - 1][0]++;
//            else          hhByPumaSize[SiloUtil.findPositionInArray(puma, pumas)][hhSize - 1][1]++;
//        }
//
//        PrintWriter pwh = SiloUtil.openFileForSequentialWriting(hhFile);
//        pwh.println("Puma,hhSize,Age,Target,Model");
//        for (int puma = 0; puma < pumas.length; puma++) {
//            for (int hhSize = 1; hhSize <= 4; hhSize++) {
//                for (int age = 0; age <= 1; age++) {
//                    int hhTarget = 0;
//                    if (age == 0) {
//                        for (int i = 1; i <= 3; i++) {
//                            String code = pumas[puma] + "_" + hhSize + "_" + i;
//                            if (householdTarget.containsKey(code)) hhTarget += householdTarget.get(code);
//                        }
//                    } else {
//                        String code = pumas[puma] + "_" + hhSize + "_" + 4;
//                        if (householdTarget.containsKey(code)) hhTarget = householdTarget.get(code);
//                    }
//                    pwh.println(puma + "," + hhSize + "," + age + "," + hhTarget + "," + hhByPumaSize[puma][hhSize - 1][age]);
//                }
//            }
//        }
//        pwh.close();
//        logger.info ("  Validating dwellings");
//        int ddByPumaType[][] = new int[pumas.length][DwellingType.values().length];  // summary by puma and dwelling type
//        for (Dwelling dd: Dwelling.getDwellings()) {
//            int taz = dd.getZone();
//            int puma = SiloUtil.getPUMAofZone(taz);
//            int ddType = dd.getType().ordinal();
//            ddByPumaType[SiloUtil.findPositionInArray(puma, pumas)][ddType]++;
//        }

    }


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


    private void summarizeVacantJobsByRegion () {
        // write out vacant jobs by region
        logger.info("----Vacant Jobs By Region Start----");
        int[] vacantJobsByRegion = new int[SiloUtil.getHighestVal(geoData.getRegionIdsArray())+1];
        for (Zone zone: geoData.getZones().values()) {
            if (vacantJobsByZone.containsKey(zone.getId())) {
                vacantJobsByRegion[zone.getRegion().getId()] +=
                        vacantJobsByZone.get(zone).length;
            }
        }
        for (int region: geoData.getRegionIdsArray()) logger.info("----,"+region+","+vacantJobsByRegion[region]);
        logger.info("----Vacant Jobs By Region End----");
        logger.info("----Vacant Jobs By PUMA Start----");
        int[] vacantJobsByPuma = new int[9999999];
        for (Zone zone: geoData.getZones().values()) {
            if (vacantJobsByZone.containsKey(zone.getId())) {
                vacantJobsByPuma[((MstmZone) zone).getPuma()] +=
                        vacantJobsByZone.get(zone).length;
            }
        }
        for (int i = 0; i < 9999999; i++)
            if (vacantJobsByPuma[i] > 0) logger.info("----,"+i+","+vacantJobsByPuma[i]);
        logger.info("----Vacant Jobs By PUMA End----");
    }


    private void summarizeByPersonRelationship () {
        // summarize number of people by PersonRole (married, single, child)

        int[][] roleCounter = new int[101][3];
        for (Person pp: householdDataManager.getPersons()) {
            if (pp.getGender() == 1) continue;
            int age = Math.min(100, pp.getAge());
            roleCounter[age][pp.getRole().ordinal()]++;
        }
        logger.info("Person role distribution for women:");
        logger.info("age,single,married,child");
        for (int i = 0; i <= 100; i++) logger.info(i + "," + roleCounter[i][0] + "," + roleCounter[i][1] + "," +
                roleCounter[i][2]);
    }
}
