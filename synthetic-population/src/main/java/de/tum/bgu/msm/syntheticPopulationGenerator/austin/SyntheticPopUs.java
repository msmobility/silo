package de.tum.bgu.msm.syntheticPopulationGenerator.austin;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.common.util.ResourceUtil;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.accessibility.AccessibilityImpl;
import de.tum.bgu.msm.data.accessibility.CommutingTimeProbability;
import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.data.geo.GeoDataMstm;
import de.tum.bgu.msm.data.geo.MstmZone;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.job.JobType;
import de.tum.bgu.msm.data.job.JobUtils;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.io.GeoDataReaderMstm;
import de.tum.bgu.msm.models.MaryLandUpdateCarOwnershipModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.run.DataBuilder;
import de.tum.bgu.msm.syntheticPopulationGenerator.SyntheticPopI;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Generates a simple synthetic population for the Austin Study Area
 * @author Rolf Moeckel (NCSG, University of Maryland)
 * Updated for the Austin, TX area by Ty Wellik (University of Texas)
 * Created on Nov 22, 2013 in Wheaton, MD
 * Updated for Austin in February, 2019
 *
 */

public class SyntheticPopUs implements SyntheticPopI {

    protected static final String PROPERTIES_RUN_SP                  = "run.synth.pop.generator";
    protected static final String PROPERTIES_PUMS_FILES              = "pums.records";
    protected static final String PROPERTIES_PARTLY_COVERED_PUMAS    = "partly.covered.pumas";
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
    private final Properties properties;
    private GeoDataMstm geoData;
    private AccessibilityImpl accessibility;
    private CommutingTimeProbability commutingTimeProbability;
    private RealEstateDataManager realEstateData;
    private HouseholdDataManager householdData;
    private JobDataManager jobData;
    private SkimTravelTimes travelTimes;


    public SyntheticPopUs(ResourceBundle rb, Properties properties) {
        this.rb = rb;
        this.properties = properties;
    }


    public void runSP () {
        // main method to run the synthetic population generator

        logger.info("Generating synthetic populations of household/persons, dwellings and jobs");
        DataContainer dataContainer = DataBuilder.buildDataContainer(properties, null);
        geoData = (GeoDataMstm) dataContainer.getGeoData();
        String fileName = properties.main.baseDirectory + properties.geo.zonalDataFile;
        String pathShp = properties.main.baseDirectory + properties.geo.zoneShapeFile;
        GeoDataReaderMstm geoDataReaderMstm = new GeoDataReaderMstm(geoData);
        geoDataReaderMstm.readZoneCsv(fileName);
        geoDataReaderMstm.readZoneShapefile(pathShp);
        geoDataReaderMstm.readCrimeData(Properties.get().main.baseDirectory + Properties.get().geo.countyCrimeFile);

        identifyUniquePUMAzones();
        readControlTotals();

        realEstateData = dataContainer.getRealEstateDataManager();
        householdData = dataContainer.getHouseholdDataManager();
        jobData = dataContainer.getJobDataManager();
        createJobs();
        travelTimes = (SkimTravelTimes) dataContainer.getTravelTimes();
        accessibility = (AccessibilityImpl) dataContainer.getAccessibility();                      // read in travel times and trip length frequency distribution
        commutingTimeProbability = dataContainer.getCommutingTimeProbability();
        
//        final String transitSkimFile = Properties.get().accessibility.transitSkimFile(Properties.get().main.startYear);
//        travelTimes.readSkim(TransportMode.pt, transitSkimFile,
//                    Properties.get().accessibility.transitPeakSkim, Properties.get().accessibility.skimFileFactorTransit);

        final String carSkimFile = Properties.get().accessibility.autoSkimFile(Properties.get().main.baseYear);
        travelTimes.readSkim(TransportMode.car, carSkimFile,
                    Properties.get().accessibility.autoPeakSkim, Properties.get().accessibility.skimFileFactorCar);

        //todo. Add skims for transit because the car ownership model requires transit accessibility instead of auto accessibility.
        travelTimes.readSkim(TransportMode.pt, carSkimFile,
                Properties.get().accessibility.autoPeakSkim, Properties.get().accessibility.skimFileFactorCar);
        accessibility.setup();
        accessibility.calculateHansenAccessibilities(Properties.get().main.baseYear);
        processPums();

        generateAutoOwnership(dataContainer);
        //SummarizeData.summarizeAutoOwnershipByCounty(accessibility, dataContainer);
        addVacantDwellings();
//        if (ResourceUtil.getBooleanProperty(rb, PROPERTIES_VALIDATE_SYNTH_POP)) validateHHandDD();
        logger.info ("  Total number of households created " + householdData.getHouseholds().size());
        logger.info ("  Total number of persons created    " + householdData.getPersons().size());
        logger.info ("  Total number of dwellings created  " + realEstateData.getDwellings().size());
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
        //todo. Copy the summarizeData method from the synthetic population for Maryland
        //SummarizeData.writeOutSyntheticPopulation(2017, dataContainer);
//        writeSyntheticPopulation();
        logger.info("  Completed generation of synthetic population");
    }


    private void identifyUniquePUMAzones() {
        // walk through list of zones and collect unique PUMA zone IDs within the study area

        tazByPuma = new HashMap<>();
        ArrayList<Integer> alHomePuma = new ArrayList<>();
        ArrayList<Integer> alWorkPuma = new ArrayList<>();
        for (Zone zone: geoData.getZones().values()) {
            int homePuma = ((MstmZone)zone).getPuma();
            int workPuma = ((MstmZone)zone).getSimplifiedPuma();
            if (!alHomePuma.contains(homePuma)) {
                alHomePuma.add(homePuma);
            }
            if (!alWorkPuma.contains(workPuma)) {
                alWorkPuma.add(workPuma);
            }
            if (tazByPuma.containsKey(homePuma)) {
                int[] zones = tazByPuma.get(homePuma);
                int[] newZones = SiloUtil.expandArrayByOneElement(zones, zone.getZoneId());
                tazByPuma.put(homePuma, newZones);
            } else {
                int[] zoneArray = {zone.getZoneId()};
                tazByPuma.put(homePuma, zoneArray);
            }
        }
        pumas = SiloUtil.convertIntegerArrayListToArray(alHomePuma);
        simplifiedPumas = SiloUtil.convertIntegerArrayListToArray(alWorkPuma);
    }


    private void readControlTotals () {
        // read control totals of households by size and dwellings

        logger.info("  Reading control total data for households and dwellings");
//        TableDataSet pop = SiloUtil.readCSVfile(Properties.get().main.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_HOUSEHOLD_CONTROL_TOTAL));
        householdTarget = new HashMap<>();
//        for (int row = 1; row <= pop.getRowCount(); row++) {
//            String fips = String.valueOf(pop.getValueAt(row, "Fips"));
//            // note: doesn't make much sense to store these data in a HashMap. It's legacy code.
//            householdTarget.put(fips, (int) pop.getValueAt(row, "TotalHouseholds"));
//        }
        hhDistribution = SiloUtil.readCSVfile(Properties.get().main.baseDirectory +
                ResourceUtil.getProperty(rb, PROPERTIES_HOUSEHOLD_DISTRIBUTION));
        hhDistribution.buildIndex(hhDistribution.getColumnPosition("SMZ_N"));
    }


    private void createJobs () {
        // method to generate synthetic jobs

        logger.info("  Generating base year jobs");
        TableDataSet jobs = SiloUtil.readCSVfile(Properties.get().main.baseDirectory + Properties.get().jobData.jobControlTotalsFileName);
        new JobType(Properties.get().jobData.jobTypes);

        // jobInventory by [industry][taz]
        final int highestZoneId = geoData.getZones().keySet().stream().max(Comparator.naturalOrder()).get();
        float[][] jobInventory = new float[JobType.getNumberOfJobTypes()][highestZoneId + 1];
        tazByWorkZonePuma = new HashMap<>();  // this HashMap has same content as "HashMap tazByPuma", though is kept separately in case external workzones will be defined

        // read employment data
        // For reasons that are not explained in the documentation, some of the PUMA work zones were aggregated to the
        // next higher level. Keep this information.

        for (int row = 1; row <= jobs.getRowCount(); row++) {
            int taz = (int) jobs.getValueAt(row, "SMZ");
            int pumaOfWorkZone =((MstmZone) geoData.getZones().get(taz)).getSimplifiedPuma();
            if (tazByWorkZonePuma.containsKey(pumaOfWorkZone)) {
                int[] list = tazByWorkZonePuma.get(pumaOfWorkZone);
                int[] newList = SiloUtil.expandArrayByOneElement(list, taz);
                tazByWorkZonePuma.put(pumaOfWorkZone, newList);
            } else {
                tazByWorkZonePuma.put(pumaOfWorkZone, new int[]{taz});
            }
            for (int jobTp = 0; jobTp < JobType.getNumberOfJobTypes(); jobTp++) {
                jobInventory[jobTp][taz] = jobs.getValueAt(row, JobType.getJobType(jobTp) + "15");
            }
        }

        // create base year employment
        for (int zone: geoData.getZones().keySet()) {
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
                    partlyCoveredPumas.getValueAt(row, "atxPop2010") / partlyCoveredPumas.getValueAt(row, "fullPop2010");
        }

        String[] states = {"tx"};

        jobErrorCounter = new HashMap<>();

        for (String state : states) {
            Map<Integer, Integer> relationsHipsByPerson = new HashMap<>();
            Map<Long, List<Household>> households = new HashMap<>();

            String pumsHhFileName = baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_PUMS_FILES) +
                    "ss17h" + state + ".csv";
            String pumsPpFileName = baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_PUMS_FILES) +
                    "ss17p" + state + ".csv";
            logger.info("  Creating synthetic population for " + state);
            readHouseholds(pumaScaler, households, pumsHhFileName);
            readPersons(households, pumsPpFileName, relationsHipsByPerson);
            definePersonRolesInHouseholds(households, relationsHipsByPerson);
        }
}

    private void readHouseholds(float[] pumaScaler, Map<Long, List<Household>> householdsBySerial, String pumsHhFileName) {
        int hhCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(pumsHhFileName));
            String[] header = in.readLine().split(",");
            int serialIndex = SiloUtil.findPositionInArray("SERIALNO", header);
            int carsIndex = SiloUtil.findPositionInArray("VEH", header);
            int weightIndex = SiloUtil.findPositionInArray("WGTP", header);
            int stateFipsIndex = SiloUtil.findPositionInArray("ST", header);
            int pumaIndex = SiloUtil.findPositionInArray("PUMA", header);
            int rentIndex = SiloUtil.findPositionInArray("RNTP", header);
            int mortgageIndex = SiloUtil.findPositionInArray("MRGP", header);
            int pumsDtTypeIndex = SiloUtil.findPositionInArray("BLD", header);
            int bedRoomsIndex = SiloUtil.findPositionInArray("BDSP", header);
            int yearIndex = SiloUtil.findPositionInArray("YBL", header);
            int plumbingIndex = SiloUtil.findPositionInArray("PLM", header);
            int kitchenIndex = SiloUtil.findPositionInArray("KIT", header);
            int hhSizeIndex = SiloUtil.findPositionInArray("NP", header);

            String recString;
            while ((recString = in.readLine()) != null) {

                String[] rec = recString.split(",");

                long serial = Long.parseLong(rec[serialIndex]);
                int weight = Integer.parseInt(rec[weightIndex]);
                //TODO: check if puma code is generated correctly below
                int pumaZone = Integer.parseInt(rec[stateFipsIndex] + 0 + rec[pumaIndex]);
                if(!checkIfPumaInStudyArea(pumaZone)){
                    continue;
                }
                //System.out.println("Found household "+hhCount);
                // some PUMA zones are only partly covered by MSTM study area. Therefore, weight needs
                // to be reduced by the share of population in this PUMA that is covered by MSTM
                weight = (int) ((weight * 1f) * pumaScaler[pumaZone] + 0.5);


                int rent;
                try {
                    rent = Integer.parseInt(rec[rentIndex]);
                } catch (Exception e) {
                    logger.debug("Household " + serial + " has no rent. Using value of 0");
                    rent = 0;
                }
                int mortgage;
                try {
                    mortgage = Integer.parseInt(rec[mortgageIndex]);
                } catch (Exception e) {
                    logger.debug("Household " + serial + " has no mortgage. Using value of 0");
                    mortgage = 0;
                }
                int price = getDwellingPrice(rent, mortgage);

                int pumsDdType;
                try {
                	float dwellingType = Float.parseFloat(rec[pumsDtTypeIndex]);
                    //pumsDdType = Integer.parseInt(rec[pumsDtTypeIndex]);
                	pumsDdType = (int) dwellingType;
                } catch (Exception e) {
                    pumsDdType = -999;
                }


                if (pumsDdType == 10 || pumsDdType == -999) {
                    // skip this record if PUMS dwelling type is 10 (Boat, RV, Van) or -999 (unknown)
                    logger.debug("Household " + serial + " lives in Boat/RV/Van or NA. Skipping.");
                    continue;
                }
                DefaultDwellingTypes.DefaultDwellingTypeImpl ddType = translateDwellingType(pumsDdType);
                int bedRooms;
                try {
                	float bedroom = Float.parseFloat(rec[bedRoomsIndex]);
                	bedRooms = (int) bedroom;
                	//bedRooms = Integer.parseInt(rec[bedRoomsIndex]);
                } catch (Exception e) {
                    logger.debug("Household " + serial + " has no valid bedroom number. Skipping.");
                    continue;
                }
                //System.out.println("Found a household");
                int yearBuilt;
                try {
                	float yrBuilt = Float.parseFloat(rec[yearIndex]);
                	yearBuilt = (int) yrBuilt;
                	//yearBuilt = Integer.parseInt(rec[yearIndex]);
                } catch (Exception e) {
                    logger.debug("Household " + serial + " has no valid year built information. " +
                            "Will ignore in quality evaluation.");
                    yearBuilt = 0;
                }


                int completePlumbing;
                try {
                	float plumbing = Float.parseFloat(rec[plumbingIndex]);
                	completePlumbing = (int) plumbing;
                	//completePlumbing = Integer.parseInt(rec[plumbingIndex]);
                } catch (Exception e) {
                    logger.debug("Household " + serial + " has no valid plumbing information. " +
                            "Will assume it is complete.");
                    completePlumbing = 1;
                }

                int completeKitchen;
                try {
                	float kitchen = Float.parseFloat(rec[kitchenIndex]);
                	completeKitchen = (int) kitchen;
                	//completeKitchen = Integer.parseInt(rec[kitchenIndex]);
                } catch (Exception e) {
                    logger.debug("Household " + serial + " has no valid kitchen information. " +
                            "Will assume it is complete.");
                    completeKitchen = 1;
                }
                int quality = guessQuality(completePlumbing, completeKitchen, yearBuilt);


                int autos;
                try {
                	float auto = Float.parseFloat(rec[carsIndex]);
                	autos = (int) auto;
                	//autos = Integer.parseInt(rec[carsIndex]);
                } catch (Exception e) {
                    logger.info("Household " + serial + " has N/A cars. Using value of 0");
                    autos = 0;
                }

               int hhSize = Integer.parseInt(rec[hhSizeIndex]);
                //int hhSize;
                //float hh_sz = Float.parseFloat(rec[hhSizeIndex]);
                //hhSize = (int) hh_sz;

                List<Household> households =new ArrayList<>();
                //System.out.println("Weight " + weight);
                for(int i = 0; i < weight; i++) {
                    //Only Create household if size >0
                    int newHhId;
                    int newDddId = realEstateData.getNextDwellingId();
                    if(hhSize > 0) {
                        newHhId = householdData.getNextHouseholdId();
                        Household hh = householdData.getHouseholdFactory().createHousehold(newHhId, newDddId, autos);
                        households.add(hh);
                        householdData.addHousehold(hh);
                        hhCount++;
                    } else {
                        newHhId = -1;
                    }
                    int taz = locateDwelling(pumaZone);
                    int selectedYear = selectYear(yearBuilt);

                    Dwelling dwelling = DwellingUtils.getFactory().createDwelling(newDddId, taz, null, newHhId, ddType, bedRooms, quality, price, selectedYear);
                    realEstateData.addDwelling(dwelling);
                }
                householdsBySerial.put(serial, households);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Created " + hhCount + " households.");
    }

    private void readPersons(Map<Long, List<Household>> households, String pumsPpFileName, Map<Integer, Integer> relationsHipsByPerson) {
        int ppCounter = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(pumsPpFileName));
            String[] header = in.readLine().split(",");
            int serialIndex = SiloUtil.findPositionInArray("SERIALNO", header);
            int ageIndex = SiloUtil.findPositionInArray("AGEP", header);
            int genderIndex = SiloUtil.findPositionInArray("SEX", header);
            int hispanicIndex = SiloUtil.findPositionInArray("HISP", header);
            int raceIndex = SiloUtil.findPositionInArray("RAC1P", header);
            int occupationIndex = SiloUtil.findPositionInArray("ESR", header);
            int incomeIndex = SiloUtil.findPositionInArray("PINCP", header);
            int workStateIndex = SiloUtil.findPositionInArray("POWSP", header);
            int workPumaZoneIndex = SiloUtil.findPositionInArray("POWPUMA", header);
            int relationshipIndex = SiloUtil.findPositionInArray("RELP", header);


            String recString;
            while ((recString = in.readLine()) != null) {
                String[] rec = recString.split(",");
                long serial = Long.parseLong(rec[serialIndex]);
                int age = Integer.parseInt(rec[ageIndex]);
                int gender = Integer.parseInt(rec[genderIndex]);
                int hispCode = Integer.parseInt(rec[hispanicIndex]);
                int raceCode = Integer.parseInt(rec[raceIndex]);

                Race race = defineRace(hispCode, raceCode);

                int occupationCode;
                try {
                    //occupationCode = Integer.parseInt(rec[occupationIndex]);
                	float occup = Float.parseFloat(rec[occupationIndex]);
                	occupationCode = (int) occup;
                } catch (Exception e) {
                    occupationCode = 0;
                }
                Occupation occ = translateOccupation(occupationCode);

                int income;
                try {
                	//System.out.println(incomeIndex.getClass());
                	float incomes = Float.parseFloat(rec[incomeIndex]);
                	income = (int) incomes;
                	income = Math.max(0, income);
                	//income = Math.max(0, Integer.parseInt(rec[incomeIndex]));
                } catch (Exception e) {
                    income = 0;
                }

                int workState = -1;
                try {
                    //workState = Integer.parseInt(rec[workStateIndex]);
                	float wrkState = Float.parseFloat(rec[workStateIndex]);
                	workState = (int) wrkState;
                } catch (Exception e) {
                    occ = Occupation.UNEMPLOYED;
                }

                int workPumaZone = -1;
                try {
                    //workPumaZone = Integer.parseInt(rec[workPumaZoneIndex]);
                	float wrkZone = Float.parseFloat(rec[workPumaZoneIndex]);
                	workPumaZone = (int) wrkZone;
                } catch (Exception e) {
                    occ = Occupation.UNEMPLOYED;
                }

                int relationship = Integer.parseInt(rec[relationshipIndex]);

                if(households.containsKey(serial)) {

                    for (Household household : households.get(serial)) {
                        int newPpId = householdData.getNextPersonId();

                        int workplace = -1;
                        if (occ == Occupation.EMPLOYED) {
                            Dwelling dd = realEstateData.getDwelling(household.getDwellingId());
                            workplace = selectWorkplaceByTripLengthFrequencyDistribution(workPumaZone, workState, dd.getZoneId());
                        }
                        if (workplace > 0) {
                            jobData.getJobFromId(workplace).setWorkerID(newPpId);  // -2 for jobs outside of the study area
                        }

                        PersonMstm pp = (PersonMstm) householdData.getPersonFactory().createPerson(newPpId, age, Gender.valueOf(gender), occ, null, workplace, income);
                        pp.setRace(race);
                        householdData.addPerson(pp);
                        householdData.addPersonToHousehold(pp, household);
                        relationsHipsByPerson.put(pp.getId(), relationship);
                        ppCounter++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Created " + ppCounter + " persons.");
    }


    private boolean checkIfPumaInStudyArea(int pumaZone) {
        // check if puma zone is part of study area
        for (int p: pumas) {
            if (pumaZone == p) {
                return true;
            }
        }
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
        if (completeKitchen == 2) {
            quality--;
        }
        if (completePlumbing == 2) {
            quality--;
        }

        //Ages:
        // 0: NA
        // 1. <1939
        // 2. 1940 - 1949
        // 3. 1950 - 1959
        // 4. 1960 - 1969
        // 5. 1970 - 1979
        // 6. 1980 - 1989
        // 7. 1990 - 1999
        // 8. 2000 - 2004
        // 9. 2005
        //10. 2006
        //11. 2007
        //12. 2008
        //13. 2009
        //14. 2010
        //15. 2011
        //16. 2012
        //17. 2013
        //18. 2014
        //19. 2015
        //20. 2016
        //21. 2017
        //TODO: Check if year 21 means 2017.
        float[] deteriorationProbability = {0, 0.85f, 0.7f, 0.55f, 0.45f, 0.35f, 0.25f, 0.2f, 0.15f, 0.14f,
                                            0.13f, 0.12f, 0.11f, 0.10f, 0.09f, 0.08f, 0.07f, 0.06f, 0.05f, 0.04f, 0.03f};
        float prob = deteriorationProbability[yearBuilt-1];
        // attempt drop quality by age two times (to get some spreading of quality levels)
        quality = quality - SiloUtil.select(new double[]{1-prob ,prob});
        quality = quality - SiloUtil.select(new double[]{1-prob, prob});

        quality = Math.max(quality, 1);      // ensure that quality never drops below 1
        return quality;
    }


    private Race defineRace (int hispanic, int singleRace) {
        if (hispanic > 1) {
            return Race.hispanic;
        }
        if (singleRace == 1) {
            return Race.white;
        }
        if (singleRace == 2) {
            return Race.black;
        }
        return Race.other;
    }


    private int selectYear (int yearBuilt) {
        // select actual year the dwelling was built

        //Ages:
        // 0: NA
        // 1. <1939
        // 2. 1940 - 1949
        // 3. 1950 - 1959
        // 4. 1960 - 1969
        // 5. 1970 - 1979
        // 6. 1980 - 1989
        // 7. 1990 - 1999
        // 8. 2000 - 2004
        // 9. 2005
        //10. 2006
        //11. 2007
        //12. 2008
        //13. 2009
        //14. 2010
        //15. 2011
        //16. 2012
        //17. 2013
        //18. 2014
        //19. 2015
        //20. 2016

        int selectedYear = 0;
        float rnd = SiloUtil.getRandomNumberAsFloat();
        switch (yearBuilt) {
            case 1: selectedYear = (int) (1930 + rnd * 10);
                break;
            case 2: selectedYear = (int) (1940 + rnd * 10);
                break;
            case 3: selectedYear = (int) (1950 + rnd * 10);
                break;
            case 4: selectedYear = (int) (1960 + rnd * 10);
                break;
            case 5: selectedYear = (int) (1970 + rnd * 10);
                break;
            case 6: selectedYear = (int) (1980 + rnd * 10);
                break;
            case 7: selectedYear = (int) (1990 + rnd * 10);
                break;
            case 8: selectedYear = (int) (2000 + rnd * 5);
                break;
            case 9: selectedYear = 2005;
                break;
            case 10: selectedYear = 2006;
                break;
            case 11: selectedYear = 2007;
                break;
            case 12: selectedYear = 2008;
                break;
            case 13: selectedYear =  2009;
                break;
            case 14: selectedYear =  2010;
                break;
            case 15: selectedYear =  2011;
                break;
            case 16: selectedYear =  2012;
                break;
            case 17: selectedYear =  2013;
                break;
            case 18: selectedYear =  2014;
                break;
            case 19: selectedYear =  2015;
                break;
            case 20: selectedYear =  2016;
                break;
        }
        return selectedYear;
    }


    private DefaultDwellingTypes.DefaultDwellingTypeImpl translateDwellingType (int pumsDdType) {
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

        DefaultDwellingTypes.DefaultDwellingTypeImpl type;
        if (pumsDdType == 1) type = DefaultDwellingTypes.DefaultDwellingTypeImpl.MH;
        else if (pumsDdType == 2) type = DefaultDwellingTypes.DefaultDwellingTypeImpl.SFD;
        else if (pumsDdType == 3) type = DefaultDwellingTypes.DefaultDwellingTypeImpl.SFA;
        else if (pumsDdType == 4 || pumsDdType == 5) type = DefaultDwellingTypes.DefaultDwellingTypeImpl.MF234;
        else if (pumsDdType >= 6 && pumsDdType <= 9) type = DefaultDwellingTypes.DefaultDwellingTypeImpl.MF5plus;
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
        for (int i = 0; i < zones.length; i++) {
            weights[i] = hhDistribution.getIndexedValueAt(zones[i], "HH17");
        }
        if (SiloUtil.getSum(weights) == 0) {
            logger.error("No weights found to allocate dwelling in zone " + pumaZone +". Check method " +
                    "<locateDwelling> in <SyntheticPopUs.java>");
        }
        int select = SiloUtil.select(weights);
        return zones[select];
    }


    private int getDwellingPrice (int rent, int mortgage) {
        // calculate price based on rent and mortgage
        int price;
        if (rent > 0 && mortgage > 0) {
            price = (rent + mortgage) / 2;
        } else if (rent <= 0 && mortgage > 0) {
            price = mortgage;
        } else if (rent > 0 && mortgage <= 0) {
            price = rent;
        } else {
            // todo: create reasonable price for dwelling
            price = 500;
        }
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
        if (!checkIfSimplifiedPumaInStudyArea(fullPumaZone) && workPumaZone != 0) {
            return -2;  // person does work in puma zone outside of study area
        }

        Map<Zone, Double> zoneProbabilities = new HashMap<>();
        for (Zone zone: geoData.getZones().values()) {
            if (vacantJobsByZone.containsKey(zone.getZoneId())) {
                int numberOfJobsInThisZone = vacantJobsByZone.get(zone.getZoneId()).length;
                if (numberOfJobsInThisZone > 0) {
                	Zone homeZone = geoData.getZones().get(homeTaz);
                	Zone destinationZone = zone;
                    int distance = (int) (travelTimes.getTravelTime(homeZone, destinationZone, Properties.get().transportModel.peakHour_s, "car") + 0.5);
                    zoneProbabilities.put(zone, commutingTimeProbability.getCommutingTimeProbability(distance, TransportMode.car) * (double) numberOfJobsInThisZone);
                } else {
                    zoneProbabilities.put(zone, 0.);
                }
            } else {
                zoneProbabilities.put(zone, 0.);
            }
        }

        // in rare cases, no job within the common commute distance is available. Assign job location outside of MSTM area.
        if (SiloUtil.getSum(zoneProbabilities.values()) == 0) {
            return -2;
        }

        Zone selectedZone = SiloUtil.select(zoneProbabilities);
        int[] jobsInThisZone = vacantJobsByZone.get(selectedZone.getZoneId());
        int selectedJobIndex = SiloUtil.select(jobsInThisZone.length) - 1;
        int[] newVacancies = SiloUtil.removeOneElementFromZeroBasedArray(jobsInThisZone, selectedJobIndex);
        if (newVacancies.length > 0) {
            vacantJobsByZone.put(selectedZone.getZoneId(), newVacancies);
        } else {
            vacantJobsByZone.remove(selectedZone.getZoneId());
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


    private void definePersonRolesInHouseholds(Map<Long, List<Household>> households, Map<Integer, Integer> relationships) {
        // define roles as single, married or chil
        for(List<Household> hhs: households.values()) {
            for (Household hh : hhs) {
                Map<Integer, ? extends Person> persons = hh.getPersons();
                Multiset<Integer> coupleCounter = HashMultiset.create();

                Person householder = null;

                for (Person pp : persons.values()) {
                    //      Householder      husband/wife  unmarried Partner
                    if (relationships.containsKey(pp.getId())) {
                        int relShp = relationships.get(pp.getId());
                        if (relShp == 0 || relShp == 1 || relShp == 13) {
                            coupleCounter.add(pp.getGender().ordinal() + 1);
                        }
                    } else {
                        logger.warn("Person " + pp.getId() + " has not been found in relationships. Setting to single.");
                    }
                }
                int numberOfCouples = Math.min(coupleCounter.count(1), coupleCounter.count(2));
                int[] marriedPersons = new int[]{numberOfCouples, numberOfCouples};

                for (Person pp : persons.values()) {
                    if (relationships.containsKey(pp.getId())) {
                        int relShp = relationships.get(pp.getId());
                        if ((relShp == 1 || relShp == 13 || relShp==0) && marriedPersons[pp.getGender().ordinal()] > 0) {
                            pp.setRole(PersonRole.MARRIED);
                            marriedPersons[pp.getGender().ordinal()] -= 1;
                            //   natural child     adopted child        step child        grandchild       foster child
                        } else if (relShp == 2 || relShp == 3 || relShp == 4 || relShp == 7 || relShp == 14) {
                            pp.setRole(PersonRole.CHILD);
                        } else {
                            pp.setRole(PersonRole.SINGLE);
                        }
                    } else {
                        pp.setRole(PersonRole.SINGLE);
                    }
                }
            }
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


    private void generateAutoOwnership (DataContainer dataContainer) {
        // select number of cars for every household
        dataContainer.getJobDataManager().setup();
        MaryLandUpdateCarOwnershipModel ao = new MaryLandUpdateCarOwnershipModel(dataContainer, accessibility, Properties.get(), SiloUtil.provideNewRandom());   // calculate auto-ownership probabilities
        Map<Integer, int[]> households = new HashMap<>();
        for (Household hh: householdData.getHouseholds()) {
            households.put(hh.getId(), null);
        }
    }


    private void addVacantDwellings () {
        // PUMS generates too few vacant dwellings, add vacant dwellings to match vacancy rate

        logger.info("  Adding empty dwellings to match vacancy rate");

        List<DwellingType> dwellingTypes = realEstateData.getDwellingTypes().getTypes();
        HashMap<String, ArrayList<Integer>> ddPointer = new HashMap<>();
        // summarize vacancy
        final int highestZoneId = geoData.getZones().keySet().stream().max(Comparator.naturalOrder()).get();
        int[][][] ddCount = new int [highestZoneId + 1][DefaultDwellingTypes.DefaultDwellingTypeImpl.values().length][2];
        for (Dwelling dd: realEstateData.getDwellings()) {
            int taz = dd.getZoneId();
            int occ = dd.getResidentId();
            ddCount[taz][dwellingTypes.indexOf(dd.getType())][0]++;
            if (occ > 0) ddCount[taz][dwellingTypes.indexOf(dd.getType())][1]++;
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

        TableDataSet countyLevelVacancies = SiloUtil.readCSVfile(Properties.get().main.baseDirectory + rb.getString(PROPERTIES_COUNTY_VACANCY_RATES));
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
            for (DwellingType dt: DefaultDwellingTypes.DefaultDwellingTypeImpl.values()) {
                String code = taz + "_" + dt;
                if (!ddPointer.containsKey(code)) continue;
                ddInThisTaz += ddPointer.get(code).size();
            }
            int targetVacantDdThisZone = (int) (ddInThisTaz * vacRateCountyTarget + 0.5);
            for (DefaultDwellingTypes.DefaultDwellingTypeImpl dt: DefaultDwellingTypes.DefaultDwellingTypeImpl.values()) {
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
                    Dwelling dd = realEstateData.getDwelling(ids[selected]);
                    int newDdId = realEstateData.getNextDwellingId();
                    Dwelling dwelling = DwellingUtils.getFactory().createDwelling(newDdId, zone.getZoneId(), null, -1, dd.getType(), dd.getBedrooms(), dd.getQuality(),
                            dd.getPrice(), dd.getYearBuilt());
                    realEstateData.addDwelling(dwelling);
                    ddCount[taz][dt.ordinal()][0]++;
                    vacDwellingsModel++;
                    if (newDdId == SiloUtil.trackDd) {
                        SiloUtil.trackWriter.println("Generated vacant dwelling with following attributes:");
                        SiloUtil.trackWriter.println(realEstateData.getDwelling(newDdId).toString());
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

        List<DwellingType> dwellingTypes = realEstateData.getDwellingTypes().getTypes();

        int[] ddCount = new int[dwellingTypes.size()];
        int[] occCount = new int[DefaultDwellingTypes.DefaultDwellingTypeImpl.values().length];
        for (Dwelling dd: realEstateData.getDwellings()) {
            int id = dd.getResidentId();
            DwellingType tp = dd.getType();
            ddCount[dwellingTypes.indexOf(tp)]++;
            if (id > 0) occCount[dwellingTypes.indexOf(tp)]++;
        }
        for (DefaultDwellingTypes.DefaultDwellingTypeImpl tp: DefaultDwellingTypes.DefaultDwellingTypeImpl.values()) {
            float rate = SiloUtil.rounder(((float) ddCount[tp.ordinal()] - occCount[tp.ordinal()]) * 100 /
                    ((float) ddCount[tp.ordinal()]), 2);
            logger.info("  Vacancy rate for " + tp + ": " + rate + "%");
        }
    }


    private void summarizeVacantJobsByRegion () {
        // write out vacant jobs by region
        logger.info("----Vacant Jobs By Region Start----");
        final int highestRegionId = geoData.getRegions().keySet().stream().max(Comparator.naturalOrder()).get();
        int[] vacantJobsByRegion = new int[highestRegionId + 1];
        for (Zone zone: geoData.getZones().values()) {
            if (vacantJobsByZone.containsKey(zone.getZoneId())) {
                vacantJobsByRegion[zone.getRegion().getId()] +=
                        vacantJobsByZone.get(zone).length;
            }
        }
        for (int region: geoData.getRegions().keySet()) {
            logger.info("----,"+region+","+vacantJobsByRegion[region]);
        }
        logger.info("----Vacant Jobs By Region End----");
        logger.info("----Vacant Jobs By PUMA Start----");
        int[] vacantJobsByPuma = new int[9999999];
        for (Zone zone: geoData.getZones().values()) {
            if (vacantJobsByZone.containsKey(zone.getZoneId())) {
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
        for (Person pp: householdData.getPersons()) {
            if (pp.getGender() == Gender.MALE) {
                continue;
            }
            int age = Math.min(100, pp.getAge());
            roleCounter[age][pp.getRole().ordinal()]++;
        }
        logger.info("Person role distribution for women:");
        logger.info("age,single,married,child");
        for (int i = 0; i <= 100; i++) logger.info(i + "," + roleCounter[i][0] + "," + roleCounter[i][1] + "," +
                roleCounter[i][2]);
    }
}
