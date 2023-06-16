package de.tum.bgu.msm.syntheticPopulationGenerator.sanFrancisco;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.common.util.ResourceUtil;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Location;
import de.tum.bgu.msm.data.MicroLocation;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.accessibility.Accessibility;
import de.tum.bgu.msm.data.accessibility.CommutingTimeProbability;
import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypes;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingUtils;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
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
import de.tum.bgu.msm.io.PersonWriterMstm;
import de.tum.bgu.msm.io.input.GeoDataReader;
import de.tum.bgu.msm.io.output.*;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.run.DataBuilder;
import de.tum.bgu.msm.syntheticPopulationGenerator.SyntheticPopI;
import de.tum.bgu.msm.utils.SampleException;
import de.tum.bgu.msm.utils.Sampler;
import de.tum.bgu.msm.utils.SeededRandomPointsBuilder;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.shape.random.RandomPointsBuilder;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Generates a simple synthetic population for the MSTM Study Area
 *
 * @author Rolf Moeckel (NCSG, University of Maryland)
 * Created on Nov 22, 2013 in Wheaton, MD
 */
public class SyntheticPopUs implements SyntheticPopI {

    protected static final String PROPERTIES_HOUSEHOLD_DISTRIBUTION = "household.distribution";
    private static final String PROPERTIES_BLOCK_GROUPS_PATH = "census.block.shapefile";
    private static final String PROPERTIES_CENSUS_BLOCK_GROUP_CONTROL_TOTALS = "census.block.control.total";

    public static final String PROPERTIES_PUMS_HH_FILE_NAME = "pums.hh";
    public static final String PROPERTIES_PUMS_PP_FILE_NAME = "pums.pp";


    protected transient Logger logger = Logger.getLogger(SyntheticPopUs.class);

    private int microLocCounter = 0;

    protected Set<Integer> pumas;
    protected Set<Integer> simplifiedPumas;
    protected Map<Integer, Set<Zone>> tazByPuma;
    protected Map<Integer, Set<String>> censusBlockGroupsByCensusTracts;
    protected Map<String, Geometry> geometryByCensusBlockGroup;

    // For reasons that are not explained in the documentation, some of the PUMA work zones were aggregated to the
    // next higher level. Keep PUMA work zones separate from more detailed PUMA zones.
    protected Map<Integer, int[]> tazByWorkZonePuma;
    protected TableDataSet hhDistribution;
    protected TableDataSet hhDistributionAtCensusBlockGroup;
    protected Map<Integer, List<Job>> vacantJobsByZone;
    protected Map<Integer, Integer> jobErrorCounter;

    private ResourceBundle rb;
    private final Properties properties;

    private GeoDataMstm geoData;
    private Accessibility accessibility;
    private CommutingTimeProbability commutingTimeProbability;
    private RealEstateDataManager realEstateData;
    private HouseholdDataManager householdData;
    private JobDataManager jobData;
    private SkimTravelTimes travelTimes;
    private PersonfactoryMstm personfactoryMstm = new PersonfactoryMstm();

    public SyntheticPopUs(ResourceBundle rb, Properties properties) {
        this.rb = rb;
        this.properties = properties;
    }

    @Override
    public void runSP() {
        // main method to run the synthetic population generator
        logger.info("Generating synthetic populations of household/persons, dwellings and jobs");
        DataContainer dataContainer = DataBuilder.buildDataContainer(properties, null);
        geoData = (GeoDataMstm) dataContainer.getGeoData();

        String fileName = properties.main.baseDirectory + properties.geo.zonalDataFile;
        String pathShp = properties.main.baseDirectory + properties.geo.zoneShapeFile;
        GeoDataReader geoDataReader = new GeoDataReaderSanFran(geoData);
        geoDataReader.readZoneCsv(fileName);
        geoDataReader.readZoneShapefile(pathShp);

        identifyUniquePUMAzones();
        identifyCensusBlockGroups();
        readControlTotals();

        realEstateData = dataContainer.getRealEstateDataManager();
        householdData = dataContainer.getHouseholdDataManager();
        jobData = dataContainer.getJobDataManager();

        createJobs();
        logger.info("Created jobs.");

        travelTimes = (SkimTravelTimes) dataContainer.getTravelTimes();
        final String carSkimFile = Properties.get().accessibility.autoSkimFile(Properties.get().main.startYear);
        travelTimes.readSkim(TransportMode.car, carSkimFile,
                Properties.get().accessibility.autoPeakSkim, Properties.get().accessibility.skimFileFactorCar);

        accessibility = dataContainer.getAccessibility();
        accessibility.setup();

        commutingTimeProbability = dataContainer.getCommutingTimeProbability();
        commutingTimeProbability.setup();

        processPums();

        logger.info("  Total number of households created " + householdData.getHouseholds().size());
        logger.info("  Total number of persons created    " + householdData.getPersons().size());
        logger.info("  Total number of dwellings created  " + realEstateData.getDwellings().size());
        logger.info("  Total number of jobs created       " + jobData.getJobs().size());
        if (!jobErrorCounter.isEmpty()) {
            logger.warn("  Could not find sufficient number of jobs in these PUMA zones (note that most of these " +
                    "zones are outside the MSTM area):");
            Set<Integer> list = jobErrorCounter.keySet();
            for (Integer puma : list) {
                logger.warn("  -> " + puma + " is missing " + jobErrorCounter.get(puma) + " jobs.");
            }
        } else {
            logger.info("  Succeeded in assigning job to every worker.");
        }
        summarizeData(dataContainer);
        logger.info("  Completed generation of synthetic population");
    }

    private void identifyCensusBlockGroups() {

        censusBlockGroupsByCensusTracts = new HashMap<>();
        geometryByCensusBlockGroup = new HashMap<>();

        String filePath =Properties.get().main.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_BLOCK_GROUPS_PATH);
        for (SimpleFeature feature : ShapeFileReader.getAllFeatures(filePath)) {
            int taz = Integer.parseInt(feature.getAttribute("TRACTCE").toString());
            String id = feature.getAttribute("GEOID").toString();
            if(censusBlockGroupsByCensusTracts.containsKey(taz)) {
                censusBlockGroupsByCensusTracts.get(taz).add(id);
            } else {
                censusBlockGroupsByCensusTracts.put(taz, Sets.newHashSet(id));
            }
            geometryByCensusBlockGroup.put(id, (Geometry) feature.getDefaultGeometry());
        }
    }


    private void identifyUniquePUMAzones() {
        // walk through list of zones and collect unique PUMA zone IDs within the study area

        tazByPuma = new HashMap<>();
        Set<Integer> alHomePuma = new HashSet<>();
        Set<Integer> alWorkPuma = new HashSet<>();
        for (Zone zone : geoData.getZones().values()) {
            int homePuma = ((MstmZone) zone).getPuma();
            int workPuma = ((MstmZone) zone).getSimplifiedPuma();
            alHomePuma.add(homePuma);
            alWorkPuma.add(workPuma);
            if (tazByPuma.containsKey(homePuma)) {
                tazByPuma.get(homePuma).add(zone);
            } else {
                Set<Zone> zones = Sets.newHashSet(zone);
                tazByPuma.put(homePuma, zones);
            }
        }
        pumas = alHomePuma;
        simplifiedPumas = alWorkPuma;
    }


    private void readControlTotals() {
        logger.info("  Reading control total data for households and dwellings");
        hhDistribution = SiloUtil.readCSVfile(Properties.get().main.baseDirectory +
                ResourceUtil.getProperty(rb, PROPERTIES_HOUSEHOLD_DISTRIBUTION));
        hhDistribution.buildIndex(hhDistribution.getColumnPosition("Id"));

        String filePath =Properties.get().main.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_CENSUS_BLOCK_GROUP_CONTROL_TOTALS);

        hhDistributionAtCensusBlockGroup = SiloUtil.readCSVfile(filePath);
        hhDistributionAtCensusBlockGroup.buildStringIndex(hhDistributionAtCensusBlockGroup.getColumnPosition("id"));
    }


    private void createJobs() {
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
            int taz = (int) jobs.getValueAt(row, "Id");
            int pumaOfWorkZone = ((MstmZone) geoData.getZones().get(taz)).getSimplifiedPuma();
            if (tazByWorkZonePuma.containsKey(pumaOfWorkZone)) {
                int[] list = tazByWorkZonePuma.get(pumaOfWorkZone);
                int[] newList = SiloUtil.expandArrayByOneElement(list, taz);
                tazByWorkZonePuma.put(pumaOfWorkZone, newList);
            } else {
                tazByWorkZonePuma.put(pumaOfWorkZone, new int[]{taz});
            }
            for (int jobTp = 0; jobTp < JobType.getNumberOfJobTypes(); jobTp++) {
                jobInventory[jobTp][taz] = jobs.getValueAt(row, JobType.getJobType(jobTp));
            }
        }

        // create base year employment
        for (int zone : geoData.getZones().keySet()) {
            for (int jobTp = 0; jobTp < JobType.getNumberOfJobTypes(); jobTp++) {
                if (jobInventory[jobTp][zone] > 0) {
                    for (int i = 1; i <= jobInventory[jobTp][zone]; i++) {
                        int id = jobData.getNextJobId();
                        jobData.addJob(JobUtils.getFactory().createJob(id, zone, null, -1, JobType.getJobType(jobTp)));
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


    private void identifyVacantJobsByZone() {
        // populate HashMap with Jobs by zone
        logger.info("  Identifying vacant jobs by zone");
        vacantJobsByZone = new HashMap<>();
        Collection<Job> jobs = jobData.getJobs();
        for (Job jj : jobs) {
            if (jj.getWorkerId() == -1) {
                int zone = jj.getZoneId();
                if (vacantJobsByZone.containsKey(zone)) {
                    vacantJobsByZone.get(zone).add(jj);
                } else {
                    vacantJobsByZone.put(zone, new ArrayList<>());
                }
            }
        }
    }

    private void processPums() {
        // read PUMS data
        logger.info("  Reading PUMS data");
        jobErrorCounter = new HashMap<>();

            Map<Integer, Integer> relationsHipsByPerson = new HashMap<>();
            Map<String, List<Household>> households = new HashMap<>();

            logger.info("  Creating synthetic population");
            String hhFilePath =Properties.get().main.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_PUMS_HH_FILE_NAME);
            String ppFilePath =Properties.get().main.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_PUMS_PP_FILE_NAME);

            readHouseholds(households, hhFilePath);
            readPersons(households, ppFilePath, relationsHipsByPerson);
            definePersonRolesInHouseholds(households, relationsHipsByPerson);
    }

    private void readHouseholds(Map<String, List<Household>> householdsBySerial, String pumsHhFileName) {
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

                String serial = rec[serialIndex];
                int weight = Integer.parseInt(rec[weightIndex]);
                int pumaZone = Integer.parseInt(rec[pumaIndex]);

                if (!pumas.contains(pumaZone)) {
                    continue;
                }

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
                    pumsDdType = Integer.parseInt(rec[pumsDtTypeIndex]);
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
                    bedRooms = Integer.parseInt(rec[bedRoomsIndex]);
                } catch (Exception e) {
                    logger.debug("Household " + serial + " has no valid bedroom number. Skipping.");
                    continue;
                }

                int yearBuilt;
                try {
                    yearBuilt = Integer.parseInt(rec[yearIndex]);
                } catch (Exception e) {
                    logger.debug("Household " + serial + " has no valid year built information. " +
                            "Will ignore in quality evaluation.");
                    yearBuilt = 0;
                }


                int completePlumbing;
                try {
                    completePlumbing = Integer.parseInt(rec[plumbingIndex]);
                } catch (Exception e) {
                    logger.debug("Household " + serial + " has no valid plumbing information. " +
                            "Will assume it is complete.");
                    completePlumbing = 1;
                }

                int completeKitchen;
                try {
                    completeKitchen = Integer.parseInt(rec[kitchenIndex]);
                } catch (Exception e) {
                    logger.debug("Household " + serial + " has no valid kitchen information. " +
                            "Will assume it is complete.");
                    completeKitchen = 1;
                }
                int quality = guessQuality(completePlumbing, completeKitchen, yearBuilt);


                int autos;
                try {
                    autos = Integer.parseInt(rec[carsIndex]);
                } catch (Exception e) {
                    logger.debug("Household " + serial + " has N/A cars. Using value of 0");
                    autos = 0;
                }

                int hhSize = Integer.parseInt(rec[hhSizeIndex]);

                List<Household> households = new ArrayList<>();

                for (int i = 0; i < weight; i++) {
                    //Only Create household if size >0
                    int newHhId;
                    int newDddId = realEstateData.getNextDwellingId();
                    if (hhSize > 0) {
                        newHhId = householdData.getNextHouseholdId();
                        Household hh = householdData.getHouseholdFactory().createHousehold(newHhId, newDddId, autos);
                        households.add(hh);
                        householdData.addHousehold(hh);
                        hhCount++;
                        MicroLocation location = locateDwelling(pumaZone, hhSize);
                        int selectedYear = selectYear(yearBuilt);

                        Dwelling dwelling = DwellingUtils.getFactory().createDwelling(newDddId, location.getZoneId(), location.getCoordinate(), newHhId, ddType, bedRooms, quality, price, selectedYear);
                        realEstateData.addDwelling(dwelling);
                    } else {
                        newHhId = -1;
                    }

                }
                householdsBySerial.put(serial, households);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Created " + hhCount + " households. Out of which " + microLocCounter + " have microlocations.");
    }

    private void readPersons(Map<String, List<Household>> households, String pumsPpFileName, Map<Integer, Integer> relationsHipsByPerson) {
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
            int relationshipIndex = SiloUtil.findPositionInArray("RELSHIPP", header);


            String recString;
            while ((recString = in.readLine()) != null) {
                String[] rec = recString.split(",");
                String serial = rec[serialIndex];
                int age = Integer.parseInt(rec[ageIndex]);
                int gender = Integer.parseInt(rec[genderIndex]);
                int hispCode = Integer.parseInt(rec[hispanicIndex]);
                int raceCode = Integer.parseInt(rec[raceIndex]);

                Race race = defineRace(hispCode, raceCode);

                int occupationCode;
                try {
                    occupationCode = Integer.parseInt(rec[occupationIndex]);
                } catch (Exception e) {
                    occupationCode = 0;
                }
                Occupation occ = translateOccupation(occupationCode);

                int income;
                try {
                    income = Math.max(0, Integer.parseInt(rec[incomeIndex]));
                } catch (Exception e) {
                    income = 0;
                }

                int workState = -1;
                try {
                    workState = Integer.parseInt(rec[workStateIndex]);
                } catch (Exception e) {
                    occ = Occupation.UNEMPLOYED;
                }

                int workPumaZone = -1;
                try {
                    workPumaZone = Integer.parseInt(rec[workPumaZoneIndex]);
                } catch (Exception e) {
                    occ = Occupation.UNEMPLOYED;
                }

                int relationship = Integer.parseInt(rec[relationshipIndex]);

                if (households.containsKey(serial)) {

                    for (Household household : households.get(serial)) {
                        int newPpId = householdData.getNextPersonId();

                        Job workplace = null;
                        int workplaceId = -1;
                        if (occ == Occupation.EMPLOYED) {
                            Dwelling dd = realEstateData.getDwelling(household.getDwellingId());
                            workplace = selectWorkplaceByTripLengthFrequencyDistribution(workPumaZone, workState, dd.getZoneId());
                            if(workplace== null) {
                                workplaceId = -2;
                            }
                        }
                        if (workplace != null) {
                            workplace.setWorkerID(newPpId);  // -2 for jobs outside of the study area
                            workplaceId = workplace.getId();
                        }

                        PersonMstm pp = personfactoryMstm.createPerson(newPpId, age, Gender.valueOf(gender), occ, null, workplaceId, income);
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
        //21 .2017
        //22 .2018
        //23 .2019
        float[] deteriorationProbability = {0, 0.85f, 0.7f, 0.55f, 0.45f, 0.35f, 0.25f, 0.2f, 0.15f, 0.14f,
                0.13f, 0.12f, 0.11f, 0.10f, 0.09f, 0.08f, 0.07f, 0.06f, 0.05f, 0.04f, 0.04f, 0.04f, 0.03f};
        float prob = deteriorationProbability[yearBuilt - 1];
        // attempt drop quality by age two times (to get some spreading of quality levels)
        quality = quality - SiloUtil.select(new double[]{1 - prob, prob});
        quality = quality - SiloUtil.select(new double[]{1 - prob, prob});

        quality = Math.max(quality, 1);      // ensure that quality never drops below 1
        return quality;
    }


    private Race defineRace(int hispanic, int singleRace) {
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


    private int selectYear(int yearBuilt) {
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
            case 1:
                selectedYear = (int) (1930 + rnd * 10);
                break;
            case 2:
                selectedYear = (int) (1940 + rnd * 10);
                break;
            case 3:
                selectedYear = (int) (1950 + rnd * 10);
                break;
            case 4:
                selectedYear = (int) (1960 + rnd * 10);
                break;
            case 5:
                selectedYear = (int) (1970 + rnd * 10);
                break;
            case 6:
                selectedYear = (int) (1980 + rnd * 10);
                break;
            case 7:
                selectedYear = (int) (1990 + rnd * 10);
                break;
            case 8:
                selectedYear = (int) (2000 + rnd * 5);
                break;
            case 9:
                selectedYear = 2005;
                break;
            case 10:
                selectedYear = 2006;
                break;
            case 11:
                selectedYear = 2007;
                break;
            case 12:
                selectedYear = 2008;
                break;
            case 13:
                selectedYear = 2009;
                break;
            case 14:
                selectedYear = 2010;
                break;
            case 15:
                selectedYear = 2011;
                break;
            case 16:
                selectedYear = 2012;
                break;
            case 17:
                selectedYear = 2013;
                break;
            case 18:
                selectedYear = 2014;
                break;
            case 19:
                selectedYear = 2015;
                break;
            case 20:
                selectedYear = 2016;
                break;
        }
        return selectedYear;
    }


    private DefaultDwellingTypes.DefaultDwellingTypeImpl translateDwellingType(int pumsDdType) {
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
        if (pumsDdType == 1) {
            type = DefaultDwellingTypes.DefaultDwellingTypeImpl.MH;
        } else if (pumsDdType == 2) {
            type = DefaultDwellingTypes.DefaultDwellingTypeImpl.SFD;
        } else if (pumsDdType == 3) {
            type = DefaultDwellingTypes.DefaultDwellingTypeImpl.SFA;
        } else if (pumsDdType == 4 || pumsDdType == 5) {
            type = DefaultDwellingTypes.DefaultDwellingTypeImpl.MF234;
        } else if (pumsDdType >= 6 && pumsDdType <= 9) {
            type = DefaultDwellingTypes.DefaultDwellingTypeImpl.MF5plus;
        } else {
            logger.error("Unknown dwelling type " + pumsDdType + " found in PUMS data.");
            type = null;
        }
        return type;
    }


    private MicroLocation locateDwelling(int pumaZone, int hhSize) {
        // select TAZ within PUMA zone
        int[] zones = tazByPuma.get(pumaZone).stream().mapToInt(Location::getZoneId).toArray();
        float[] weights = new float[zones.length];
        for (int i = 0; i < zones.length; i++) {
            weights[i] = hhDistribution.getIndexedValueAt(zones[i], "totalHh");
        }
        if (SiloUtil.getSum(weights) == 0) {
            logger.error("No weights found to allocate dwelling in zone " + pumaZone + ". Check method " +
                    "<locateDwelling> in <SyntheticPopUs.java>");
        }
        int select = SiloUtil.select(weights);
        final int zone = zones[select];

        // select census block group within census tract/zone
        if(!censusBlockGroupsByCensusTracts.containsKey(zone)) {
            logger.debug("no census block group found for tract: " + zone + " will not generate micro location");
            return new MicroLocation() {
                @Override
                public Coordinate getCoordinate() {
                    return null;
                }

                @Override
                public int getZoneId() {
                    return zone;
                }
            };
        }
        String[] censusBlockGroups = censusBlockGroupsByCensusTracts.get(zone).toArray(new String[0]);
        float[] censusBlockGroupWeights = new float[censusBlockGroups.length];
        for (int i = 0; i < censusBlockGroups.length; i++) {
            try {
                censusBlockGroupWeights[i] = hhDistributionAtCensusBlockGroup.getStringIndexedValueAt(censusBlockGroups[i], Math.min(7,hhSize) + "person");
            } catch (IllegalArgumentException e) {
                logger.debug("no census block group found for tract: " + zone + " will not generate micro location");
                return new MicroLocation() {
                    @Override
                    public Coordinate getCoordinate() {
                        return null;
                    }

                    @Override
                    public int getZoneId() {
                        return zone;
                    }
                };
            }
        }
        if (SiloUtil.getSum(censusBlockGroupWeights) == 0) {
            logger.debug("No weights found to allocate dwelling in census tract " + zone + ". Check method " +
                    "<locateDwelling> in <SyntheticPopUs.java>");
            return new MicroLocation() {
                @Override
                public Coordinate getCoordinate() {
                    return null;
                }

                @Override
                public int getZoneId() {
                    return zone;
                }
            };
        }
        int selectCensusBlockGroup = SiloUtil.select(censusBlockGroupWeights);
        final String censusBlockGroup = censusBlockGroups[selectCensusBlockGroup];

        RandomPointsBuilder randomPointsBuilder = new SeededRandomPointsBuilder(new GeometryFactory(), new Random(42));
        randomPointsBuilder.setNumPoints(1);
        final Geometry geometry = geometryByCensusBlockGroup.get(censusBlockGroup);
        randomPointsBuilder.setExtent(geometry);
        Coordinate coordinate = randomPointsBuilder.getGeometry().getCoordinates()[0];
        Point p = MGC.coordinate2Point(coordinate);

        microLocCounter++;
        return new MicroLocation() {
            @Override
            public Coordinate getCoordinate() {
                return new Coordinate(p.getX(), p.getY());
            }

            @Override
            public int getZoneId() {
                return zone;
            }
        };
    }


    private int getDwellingPrice(int rent, int mortgage) {
        // calculate price based on rent and mortgage
        int price;
        if (rent > 0 && mortgage > 0) {
            price = (rent + mortgage) / 2;
        } else if (rent <= 0 && mortgage > 0) {
            price = mortgage;
        } else if (rent > 0) {
            price = rent;
        } else {
            // todo: create reasonable price for dwelling
            price = 500;
        }
        return price;
    }


    private Job selectWorkplaceByTripLengthFrequencyDistribution(int workPumaZone, int workState, int homeTaz) {
        // for some workers, a workzone is not specified in PUMS data. Select workplace based on trip length frequency distribution

        // todo: A couple of issues deserve further attention:
        // - People working in a RMZ are assigned -2 as workplace. Would be good to select external zones
        // - People living in a RMZ but working in a SMZ are disregarded at this point. Therefore, 917,985 jobs remain vacant in base year
        // - Full-time and part-time employees are not distinguished. Every employee occupies one job, even though jobs should be provided as full-time equivalents in MSTM.
        // - Nobody explicitly works from home, though a couple of people will select a job in their home zone. Should be controlled by number of workers working from home.
        // - School/University locations are not assigned as 'workplace' yet. Note that some worker have a job and go to school at the same time.

        if (!simplifiedPumas.contains(workPumaZone) && workPumaZone != 0) {
            return null;  // person does work in puma zone outside of study area
        }

        Map<Zone, Double> zoneProbabilities = new HashMap<>();
        Zone homeZone = geoData.getZones().get(homeTaz);
        for (Zone zone : geoData.getZones().values()) {
            if (vacantJobsByZone.containsKey(zone.getZoneId())) {
                int numberOfJobsInThisZone = vacantJobsByZone.get(zone.getZoneId()).size();
                if (numberOfJobsInThisZone > 0) {
                    int distance = (int) (travelTimes.getTravelTime(homeZone, zone, Properties.get().transportModel.peakHour_s, "car") + 0.5);
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
            return null;
        }

        Zone selectedZone = SiloUtil.select(zoneProbabilities);
        List<Job> jobsInThisZone = vacantJobsByZone.get(selectedZone.getZoneId());
        Sampler<Job> sampler = Sampler.getEvenlyDistributedSampler(jobsInThisZone, 1);
        Job selectedJob;
        try {
            selectedJob = sampler.sampleObject();
            jobsInThisZone.remove(selectedJob);
            if (jobsInThisZone.isEmpty()) {
                vacantJobsByZone.remove(selectedZone.getZoneId());

            }
        } catch (SampleException e) {
            throw new RuntimeException(e);
        }
        return selectedJob;
    }


    private Occupation translateOccupation(int pumsOccupation) {
        // translate PUMS occupation into simpler categories: 0 not employed, not looking for work, 1 employed, 2 unemployed
        switch (pumsOccupation) {
            // 0 . Not in universe (Under 16 years)
            case 0:
                return Occupation.TODDLER;
            // 1 . Employed, at work
            case 1:
                return Occupation.EMPLOYED;
            // 2 . Employed, with a job but not at work during week interviewed (on vacation, leave, etc.)
            case 2:
                return Occupation.EMPLOYED;
            // 3 . Unemployed
            case 3:
                return Occupation.UNEMPLOYED;
            // 4 . Armed Forces, at work
            case 4:
                return Occupation.EMPLOYED;
            // 5 . Armed Forces, with a job but not at work
            case 5:
                return Occupation.EMPLOYED;
            // 6 . Not in labor force
            case 6:
                return Occupation.RETIREE;
            default:
                return Occupation.RETIREE;
        }
    }


    private void definePersonRolesInHouseholds(Map<String, List<Household>> households, Map<Integer, Integer> relationships) {
        // define roles as single, married or chil
        for (List<Household> hhs : households.values()) {
            for (Household hh : hhs) {
                Map<Integer, ? extends Person> persons = hh.getPersons();
                Multiset<Integer> coupleCounter = HashMultiset.create();

                Person householder = null;

                for (Person pp : persons.values()) {
                    //      Householder      husband/wife  unmarried Partner
                    if (relationships.containsKey(pp.getId())) {
                        int relShp = relationships.get(pp.getId());
//                        20 .Reference person
//                        21 .Opposite-sex husband/wife/spouse
//                        22 .Opposite-sex unmarried partner
//                        23 .Same-sex husband/wife/spouse
//                        24 .Same-sex unmarried partner
                        if (relShp == 20 || relShp == 21 || relShp == 22 || relShp == 23 || relShp == 24) {
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
                        if ((relShp == 20 || relShp == 21 || relShp == 22 || relShp == 23 || relShp == 24) && marriedPersons[pp.getGender().ordinal()] > 0) {
                            pp.setRole(PersonRole.MARRIED);
                            marriedPersons[pp.getGender().ordinal()] -= 1;
//                            25 .Biological son or daughter
//                            26 .Adopted son or daughter
//                            27 .Stepson or stepdaughter
//                            30 .Grandchild
//                            35 .Foster child
                        } else if (relShp == 25 || relShp == 26 || relShp == 27 || relShp == 30 || relShp == 35) {
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

    private void summarizeData(DataContainer dataContainer) {

        String filehh = properties.main.baseDirectory
                + properties.householdData.householdFileName
                + "_"
                + properties.main.baseYear
                + ".csv";
        HouseholdWriter hhwriter = new DefaultHouseholdWriter(dataContainer.getHouseholdDataManager().getHouseholds());
        hhwriter.writeHouseholds(filehh);

        String filepp = properties.main.baseDirectory
                + properties.householdData.personFileName
                + "_"
                + properties.main.baseYear
                + ".csv";
        PersonWriter ppwriter = new PersonWriterMstm(dataContainer.getHouseholdDataManager());
        ppwriter.writePersons(filepp);

        String filedd = properties.main.baseDirectory
                + properties.realEstate.dwellingsFileName
                + "_"
                + properties.main.baseYear
                + ".csv";
        DwellingWriter ddwriter = new DefaultDwellingWriter(dataContainer.getRealEstateDataManager().getDwellings());
        ddwriter.writeDwellings(filedd);

        String filejj = properties.main.baseDirectory
                + properties.jobData.jobsFileName
                + "_"
                + properties.main.baseYear
                + ".csv";
        JobWriter jjwriter = new DefaultJobWriter(dataContainer.getJobDataManager().getJobs());
        jjwriter.writeJobs(filejj);
    }
//
//
//    private static class CensusBlockGroup {
//        private final int id;
//        private final int tractId;
//
//        private final Geometry geometry;
//
//    }
}