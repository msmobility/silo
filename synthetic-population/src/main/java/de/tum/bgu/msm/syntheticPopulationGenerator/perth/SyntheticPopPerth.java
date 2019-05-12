package de.tum.bgu.msm.syntheticPopulationGenerator.perth;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.syntheticPopulationGenerator.SyntheticPopI;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import java.io.PrintWriter;
import java.util.*;
/*import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.job.JobDataManager;
import run.DataBuilder;*/

/**
 * Generates a simple synthetic population for the perth Study Area
 * @author Rolf Moeckel (TUM) & Sonja Stemler (UWA) & Martin Porebski (UWA)
 * Created on Oct. 31st, 2018 in Munich
 */
public class SyntheticPopPerth implements SyntheticPopI
{
    // constants
    private int CODE_MALE = 1;
    private int CODE_FEMALE = 2;

    protected static final String PROPERTIES_RUN_SP                  = "run.synth.pop.generator";
    protected static final String PROPERTIES_PUMS_PERSONS            = "pums.persons";
    protected static final String PROPERTIES_PUMS_DWELLINGS          = "pums.dwellings";

    protected transient Logger logger = Logger.getLogger(SyntheticPopPerth.class);

    private ResourceBundle rb;
    private String baseDirectory;

    // data managers
    /*protected HouseholdDataManager householdData;
    protected RealEstateDataManager realEstateData;
    protected JobDataManager jobData;*/

    // writers to save data in files
    private PrintWriter pwhh;
    private PrintWriter pwpp;
    private PrintWriter pwdd;
    private PrintWriter pwjj;

    // temporary data holders
    private TableDataSet dwellUnoccPerArea = null;
    private ArrayList<Dwelling> dwellingList = new ArrayList();
    private HashMap<Integer, ZoneSA1[]> zoneMap = new HashMap<>(); // used for the distribution of the population
    private HashMap<Integer, ArrayList<Dwelling>> dwellingsPerZoneMap = new HashMap<>();
    //private ArrayList<Dwelling> nonPrivDwells = new ArrayList<>();
    private ArrayList<Family> samplePeople = new ArrayList<>(); // store information about 1% of the population
    private ArrayList<Family> populationPeople = new ArrayList<>(); // store information about 100% population
    private HashMap<Integer, ZoneSA1Jobs> JobDistributionSA1 = new HashMap<>();
    private JobCollection jobCollection = new JobCollection();

    // statistical count
    private long unassignedWorker = 0;
    private long unemployedCount = 0;
    private long employedCount = 0;
    private int nonprivdwellPeopleCount = 0;
    private int nonprivdwellCount = 0;
    private int mergedFams = 0;
    private int clearedDwellings = 0;

    // iterate through IDs instead of data managers
    private long nextID_JJ = 1; // jobData.getNextJobId()
    private long nextID_PP = 1; // householdData.getNextPersonId()
    private long nextID_HH = 1; // householdData.getNextHouseholdId()
    private long nextID_DD = 1; // realEstateData.getNextDwellingId()

    // -----------------------------------------------------------------------------------------------------------------
    // constructor
    public SyntheticPopPerth(ResourceBundle rb) { this.rb = rb; }

    // -----------------------------------------------------------------------------------------------------------------
    // run the synthetic population generator
    public void runSP()
    {
        int year = 2011;
        int jobsToAdd = 21834-3745; // there has to be 21834 unoccupied jobs and there were 3745 already

        if (!ResourceUtil.getBooleanProperty(rb, PROPERTIES_RUN_SP)) return;

        // setup data managers
        /*DataContainer dataContainer = DataBuilder.buildDataContainer(Properties.get());
        realEstateData = dataContainer.getRealEstateDataManager();
        householdData = dataContainer.getHouseholdDataManager();
        jobData = dataContainer.getJobDataManager();*/

        logger.info("Generating synthetic populations of household/persons, dwellings and jobs");
        baseDirectory = Properties.get().main.baseDirectory;

        // open & preprocess the gender file used for the population distribution into SA1 zones
        openPopulationDistribution(year);

        // open the list of vacant dwellings in Perth
        dwellUnoccPerArea = SiloUtil.readCSVfile(baseDirectory + "input/perth_specific/dwellingUnoccupiedBySA1_" + year + ".csv");

        // open the files to save the data
        openFilesToWriteSyntheticPopulation(year);

        logger.info("\t ---------- Generating jobs.");
        createJobs(year);

        logger.info("\t ---------- Reading Australian PUMS (ABS) 1% data.");
        readMicroData();
        logger.info("Married people shall live together.");
        connectMarriedPeople(); // silo needs to have married people living together, done on 1% data
        sortPopulationByFamilySize(samplePeople); // sort by family size, this will aid with population distribution

        logger.info("\t ---------- Generating population.");
        generatePopulation();
        logger.info("Sorting Perth's population by family size.");
        sortPopulationByFamilySize(populationPeople); // again, due to changes (e.g. family mering of non private dwells)

        logger.info("\t ---------- Distributing people and dwellings into SA1 zones.");
        distributeToSA1();

        logger.info("\t ---------- Assigning quality to dwellings.");
        determineDwellingQuality();

        logger.info("\t ---------- Adding vacant/unoccupied dwellings.");
        addVacantDwellings();

        logger.info("\t  ---------- Adding vacant jobs");
        addEmptyJobs(jobsToAdd, (int)nextID_JJ);

        logger.info("\t ---------- Saving the dd and jj files.");
        savePumsRecords();
        saveDwellings();
        saveJobs();

        logger.info("\t ---------- Final stats:");
        logger.info("\t" + nextID_PP + " people " + nextID_HH + " households " + nextID_DD + " dwellings " + nextID_JJ + " jobs.");
        logger.info("\t" + employedCount + " employed and " + unemployedCount + " unemployed.");
        logger.info("\t" + unassignedWorker + " unassigned workers.");
        logger.info("\t" + mergedFams + "00 families/households merged into one because they share a dwelling");
        logger.info("\t" + clearedDwellings + " cleared dwellings");

        logger.info("\t" + nonprivdwellPeopleCount + " people in " + nonprivdwellCount + " non-private dwellings.");
        closeFilesForSyntheticPopulation();
        logger.info("  Completed generation of synthetic population");
    }

    // -----------------------------------------------------------------------------------------------------------------  Job Creation
    private void createJobs(int year)
    {
        // open the file
        TableDataSet jobsTable = SiloUtil.readCSVfile(baseDirectory + "input/perth_specific/industryBySA1_" + year + ".csv");

        int startCol = jobsTable.getColumnPosition("ind_0");

        // for each row (SA1 area)
        for(int row = 1; row <= jobsTable.getRowCount(); row++)
        {
            // for each industry and the corresponding job count for the industry
            for(int col = startCol; col <= jobsTable.getColumnCount(); col++)
            {
                // get how many jobs from this industry there are
                int count = (int)jobsTable.getValueAt(row, col);
                String columnLabel = jobsTable.getColumnLabel(col);

                // get common job properties
                int zoneSA1 = (int) jobsTable.getValueAt(row, "SA1");
                int type = Integer.parseInt(columnLabel.substring("ind_".length())) + 1; // types match but this index starts at 0 whereas ABS starts at 1
                int personId = -1;

                // for each job in this industry
                for(int j = 0; j < count; j++)
                {
                    // save the record
                    JobSlot job = new JobSlot(nextID_JJ++, zoneSA1, personId, type);
                    jobCollection.addNewJob(job);

                    // ------------------------------------------------------------------------------------------------- statistical count to add empty jobs
                    // get the job stats for this zone
                    ZoneSA1Jobs zsj = JobDistributionSA1.get(zoneSA1);
                    if(zsj == null) { zsj = new ZoneSA1Jobs(); }

                    // add the job type to keep the statistical count
                    zsj.AddJob(type);
                    JobDistributionSA1.put(zoneSA1, zsj);
                }
            }
        }
    }

    private void addEmptyJobs(int length, int totalJobCount)
    {
        RandomCollection<Integer> weightedRandom = new RandomCollection<>();

        // calculate weighted probabilities
        for (int zoneSA1 : JobDistributionSA1.keySet())
        {
            ZoneSA1Jobs zsj = JobDistributionSA1.get(zoneSA1);
            zsj.CalculateProbabilities();
            zsj.Probability = (double)((double)zsj.TotalJobs / (double)totalJobCount);
            JobDistributionSA1.put(zoneSA1, zsj);

            weightedRandom.add(zsj.Probability, zoneSA1);
        }

        for(int j=0; j<length; j++)
        {
            int zoneSA1 = weightedRandom.next();
            int type = JobDistributionSA1.get(zoneSA1).WeightedRandom.next();

            jobCollection.addNewJob( new JobSlot(nextID_JJ++, zoneSA1, -1, type));
        }
    }

    // -----------------------------------------------------------------------------------------------------------------  SA1 Population Distribution
    private void openPopulationDistribution(int year)
    {
        // open the files
        TableDataSet genderPerArea = SiloUtil.readCSVfile(baseDirectory + "input/perth_specific/genderBySA1_" + year + ".csv");
        TableDataSet agePerArea = SiloUtil.readCSVfile(baseDirectory + "input/perth_specific/ageGroupsBySA1_" + year + ".csv");
        TableDataSet genderAgePerArea = SiloUtil.readCSVfile(baseDirectory + "input/perth_specific/genderAgeGroupBySA1_" + year + ".csv");
        TableDataSet incomePerArea = SiloUtil.readCSVfile(baseDirectory + "input/perth_specific/weeklyIncomeBySA1_" + year + ".csv");

        // age group intervals e.g. 5 because of 0-4, 5-9, 10-14, 15-19
        int ageInterval = 5;

        // income intervals: nil, then 10
        int incomeGroups = 11;

        // SA4 area codes in the file
        int[] areaCodes = new int[] {49, 50, 51, 52};

        // for each area
        for(int area = 0; area < areaCodes.length; area++)
        {
            // store zones that belong to this SA4
            // hashmap of SA1 to SA1zone to later easily add more info
            HashMap<Integer, ZoneSA1> SA1zones = new HashMap<>();

            // find gender counts
            for(int row = 1; row <= genderPerArea.getRowCount(); row++)
            {
                int SA4 = (int) genderPerArea.getValueAt(row, "SA4");

                // if zone belongs to the same SA4 area code
                if(areaCodes[area] == SA4)
                {
                    // create a new zone
                    ZoneSA1 zone = new ZoneSA1(ageInterval, incomeGroups);
                    zone.SA4 = SA4;
                    zone.SA1 = (int)(genderPerArea.getValueAt(row, "SA1"));

                    // add gender counts for that zone
                    zone.setMaleCounts((int)(genderPerArea.getValueAt(row, "Male")));
                    zone.setFemaleCounts((int)(genderPerArea.getValueAt(row, "Female")));

                    // save the zone
                    SA1zones.put(zone.SA1, zone);
                }
            }

            // find age group counts
            for(int row = 1; row <= agePerArea.getRowCount(); row++)
            {
                int SA4 = (int) agePerArea.getValueAt(row, "SA4");

                // if zone belongs to the same SA4 area code
                if(areaCodes[area] == SA4)
                {
                    int SA1 = (int)(agePerArea.getValueAt(row, "SA1"));

                    // get the previously created zone
                    ZoneSA1 zone = SA1zones.get(SA1);

                    if(zone != null)
                    {
                        // columns 1 and 2 are SA1 and SA4
                        int otherCols = 3;

                        // traverse all age groups and add them to the zone stats
                        for(int col = otherCols; col <= agePerArea.getColumnCount(); col++)
                        {
                            zone.setAgeGroup((col-otherCols), (int)agePerArea.getValueAt(row, col));
                        }

                        // replace (not needed)
                        SA1zones.put(SA1, zone);
                    }
                    else
                    {
                        logger.error("Zone mismatch at SA1 " + SA1);
                    }
                }
            }

            // find age group counts
            for(int row = 1; row <= genderAgePerArea.getRowCount(); row++)
            {
                int SA4 = (int) genderAgePerArea.getValueAt(row, "SA4");

                // if zone belongs to the same SA4 area code
                if(areaCodes[area] == SA4)
                {
                    int SA1 = (int)(genderAgePerArea.getValueAt(row, "SA1"));

                    // get the previously created zone
                    ZoneSA1 zone = SA1zones.get(SA1);

                    if(zone != null)
                    {
                        // columns 1 and 2 are SA1 and SA4
                        int otherCols = 3;
                        // 2+(44-2)/2
                        int intervals = (otherCols-1) + ((genderAgePerArea.getColumnCount() - (otherCols-1)) /2);

                        // traverse all age groups for both genders
                        for(int col = otherCols; col <= intervals; col++)
                        {
                            zone.setMaleAgeGroup((col-otherCols), (int)genderAgePerArea.getValueAt(row, col));
                            zone.setFemaleAgeGroup((col-otherCols), (int)genderAgePerArea.getValueAt(row, (col+intervals-(otherCols-1))));
                        }

                        // replace (not needed)
                        SA1zones.put(SA1, zone);
                    }
                    else
                    {
                        logger.error("Zone mismatch at SA1 " + SA1);
                    }
                }
            }

            // find income group counts
            for(int row = 1; row <= incomePerArea.getRowCount(); row++)
            {
                int SA4 = (int) incomePerArea.getValueAt(row, "SA4");

                // if zone belongs to the same SA4 area code
                if(areaCodes[area] == SA4)
                {
                    int SA1 = (int)(incomePerArea.getValueAt(row, "SA1"));

                    // get the previously created zone
                    ZoneSA1 zone = SA1zones.get(SA1);

                    if(zone != null)
                    {
                        // columns 1 and 2 are SA1 and SA4
                        int otherCols = 3;
                        // columns not applicable to silo, considered to be nil
                        int nilCols = 5;

                        // traverse all income groups and add them to the zone stats
                        for(int col = otherCols; col <= incomePerArea.getColumnCount(); col++)
                        {
                            if(col < (otherCols+nilCols))
                            {
                                int incIndex = 0;
                                zone.appendToIncomeGroup(incIndex, (int)incomePerArea.getValueAt(row, col));
                            }
                            else
                            {
                                // index 0 (nil) + column index - (irrelevant columns)
                                int incIndex = 1 + col - (otherCols + nilCols);
                                zone.setIncomeGroup(incIndex, (int)incomePerArea.getValueAt(row, col));
                            }
                        }

                        // replace (not needed)
                        SA1zones.put(SA1, zone);
                    }
                    else
                    {
                        logger.error("Zone mismatch at SA1 " + SA1);
                    }
                }
            }

            // place the array of zones under appropriate code in the hashmap
            zoneMap.put(areaCodes[area], SA1zones.values().toArray(new ZoneSA1[0]));
        }
    }
    // -----------------------------------------------------------------------------------------------------------------  Output files
    /*private void openNonPrivateDwellings(int year)
    {
        // open the file
        TableDataSet npdTD = SiloUtil.readCSVfile(baseDirectory + "input/perth_specific/nonPrivateDwellingsBySA1_" + year + ".csv");

        for(int row = 1; row < npdTD.getRowCount(); row++)
        {
            int totalNPDs = (int)npdTD.getValueAt(row, "total");
            int zoneSA1 = (int)npdTD.getValueAt(row, "NPRD Number of Persons Usually Resident in Dwelling");
            for(int d = 1; d < totalNPDs; d++)
            {
                Dwelling npd = new Dwelling();
                npd.zoneSA1 = zoneSA1;
                npd.zoneSA4 = Integer.parseInt(Integer.toString(zoneSA1).substring(0, 2));
                nonPrivDwells.add(npd);
            }
        }
    }*/

    // -----------------------------------------------------------------------------------------------------------------  Output files
    private void openFilesToWriteSyntheticPopulation(int year)
    {
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

    private void closeFilesForSyntheticPopulation()
    {
        pwhh.close();
        pwpp.close();
        pwdd.close();
        pwjj.close();
    }

    /* *-----------------------------------------------------------------------------------------------------------------  Process ABS data
     *  Reads files that contain the information about
     *  sample (1%)  population (people and dwellings).
     * */
    private void readMicroData()
    {
        // read PUMS data of the Australian Bureau Of Statistics (ABS) for Population
        String pumsFilePersons = baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_PUMS_PERSONS);
        TableDataSet pumsPersons = SiloUtil.readCSVfile(pumsFilePersons);
        String pumsFileDwellings = baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_PUMS_DWELLINGS);
        TableDataSet pumsDwellings = SiloUtil.readCSVfile(pumsFileDwellings);

        // for each row in the dwellings file
        for (int rowDd = 1; rowDd <= pumsDwellings.getRowCount(); rowDd++)
        {
            HashMap<Integer, Family> familyMap = new HashMap<>();

            // get ABS dwelling id from the DWELLING file
            String dwellingId = pumsDwellings.getStringValueAt(rowDd, "ABSHID Dwelling Record Identifier");

            // get the attributes of the dwelling from ABS
            int bedRoomCode = (int) pumsDwellings.getValueAt(rowDd, "BEDRD");
            int mortgageCode = (int) pumsDwellings.getValueAt(rowDd, "MRERD");
            int rentCode = (int) pumsDwellings.getValueAt(rowDd, "RNTRD");
            int typeCode = (int) pumsDwellings.getValueAt(rowDd, "STRD");
            int autoCode = (int) pumsDwellings.getValueAt(rowDd, "VEHRD");
            int geographicAreaCode = (int) pumsDwellings.getValueAt(rowDd, "AREAENUM");

            // create a new dwelling to store the record
            Dwelling dwelling = new Dwelling();

            // convert the dwelling attributes to a SILO format
            dwelling.bedrooms = convertBedrooms(bedRoomCode);
            dwelling.type = typeCode;
            dwelling.quality = 0;
            dwelling.mortgage = convertMortgage(mortgageCode);
            dwelling.rent = convertRent(rentCode);
            dwelling.price = getDwellingPrice(dwelling.rent, dwelling.mortgage);
            dwelling.cars = convertAutos(autoCode);
            dwelling.zoneSA4 = geographicAreaCode;

            // ---------------------------------------------------------------------------------------------------------
            // for each row in the PERSON file
            for(int rowPp = 1; rowPp <= pumsPersons.getRowCount(); rowPp++)
            {
                // get ABS ids from the PERSON file
                String p_dwellingId = pumsPersons.getStringValueAt(rowPp, "ABSHID Dwelling Record Identifier");
                int familyId = (int) pumsPersons.getValueAt(rowPp, "ABSFID Family Record Identifier");

                // find the people that live in this dwelling
                if(p_dwellingId.equals(dwellingId))
                {
                    // check if previously added family
                    Family family = familyMap.get(familyId);
                    if(family == null)
                    {
                        // add a new family to the map
                        family = new Family();
                        family.dwelling = dwelling;
                        familyMap.put(familyId, family);
                    }

                    // get the attributes of the family from ABS
                    int ageGroup = (int) pumsPersons.getValueAt(rowPp, "AGEP Age");
                    int sexCode = (int) pumsPersons.getValueAt(rowPp, "SEXP Sex");
                    int incomeCode = (int) pumsPersons.getValueAt(rowPp, "INCP Individual Income (weekly)");
                    int occupationCode = (int) pumsPersons.getValueAt(rowPp, "LFSP Labour Force Status");
                    int relationshipCode = (int) pumsPersons.getValueAt(rowPp, "RLHP Relationship in Household");
                    int industryCode = (int) pumsPersons.getValueAt(rowPp, "INDP Industry of Employment");

                    // create a new person
                    Person person = new Person();

                    // convert the person attributes to a SILO format
                    person.Age = convertAge(ageGroup);
                    person.Sex = sexCode;
                    person.Race = 0;
                    person.Income = convertIncome(incomeCode);
                    person.WeeklyIncCode = incomeCode2WeeklySILO(incomeCode);
                    person.Occupation = translateOccupation(occupationCode, person.Age);
                    person.Relationship = translateRelationship(relationshipCode);
                    person.Industry = translateIndustry(industryCode, person.Occupation);

                    // add the person to the family
                    family.addPerson(person);
                }
            }

            if(familyMap.size() == 0)
            {
                // vacant dwelling?
                logger.error("Dwelling " + dwellingId + " was not found in the PERSON file.");
            }
            else
            {
                // for each family
                Family household = new Family();

                for(Map.Entry<Integer, Family> entry : familyMap.entrySet())
                {
                    mergedFams++;
                    // get a family of the dwelling
                    Family family = entry.getValue();
                    // merge the families into one household
                    household.append(family);
                }

                // fill in the other details about this household
                household.dwelling = dwelling;
                household.autoFillCount();

                samplePeople.add(household);
            }
        }
    }
    // -----------------------------------------------------------------------------------------------------------------  Process ABS data
    private void connectMarriedPeople()
    {
        int h1, h2;
        for(h1=0; h1<samplePeople.size(); h1++)
        {
            Family household1 = samplePeople.get(h1);

            // if there is a single yet married person
            if(household1.Size == 1 && household1.marriedPeople == 1)
            {
                int hh1Interest = household1.SingleAndLookingFor();

                // find that person a family :)

                // add to existing family
                for(h2=0; h2<samplePeople.size(); h2++)
                {
                    Family household2 = samplePeople.get(h2);

                    // if there is an uneven number of married people living the dwelling/household
                    if(household2.Size > 1 && h2 != h1
                            && household2.marriedPeople%2 != 0
                            && household1.dwelling.zoneSA4 == household2.dwelling.zoneSA4)
                    {
                        int hh2Interest = household2.SingleAndLookingFor();
                        if(hh1Interest != 0 && hh2Interest != 0 && hh1Interest != hh2Interest)
                        {
                            household2.append(household1);
                            household1.emptyTheDwelling();
                            samplePeople.set(h1, household1);
                            samplePeople.set(h2, household2);
                            h2 = samplePeople.size()+50;
                        }
                    }
                }

                // if didn't find a family, match singles
                if(h2 == samplePeople.size())
                {
                    for(h2=0; h2<samplePeople.size(); h2++)
                    {
                        Family household2 = samplePeople.get(h2);
                        if(household2.Size == 1 && household2.marriedPeople == 1)
                        {
                            int hh2Interest = household2.SingleAndLookingFor();
                            if(hh1Interest != 0 && hh2Interest != 0 && hh1Interest != hh2Interest)
                            {
                                household2.append(household1);
                                household1.emptyTheDwelling();
                                samplePeople.set(h1, household1);
                                samplePeople.set(h2, household2);
                                h2 = samplePeople.size()+50;
                            }
                        }
                    }
                }
            }
        }

        for(h1=0; h1<samplePeople.size(); h1++)
        {
            Family household = samplePeople.get(h1);

            // if there is a single yet married person
            if(household.marriedPeople%2 != 0)
            {
                int lookingFor = household.SingleAndLookingFor();
                for(int i=0; i<household.Size; i++)
                {
                    Person person = household.People.get(i);
                    if(person.Relationship.compareTo("married") == 0)
                    {
                        if(person.Sex != lookingFor)
                        {
                            samplePeople.get(h1).People.get(i).Relationship = "single";
                            samplePeople.get(h1).autoFillCount();
                            i = household.Size;
                        }
                    }
                }
            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------------  Sort the population by family
    private void sortPopulationByFamilySize(ArrayList<Family> populationToSort)
    {
        Collections.sort(populationToSort, Collections.reverseOrder());
    }

    private int populationSize(ArrayList<Family> populationToCount)
    {
        int people = 0;
        for(int i=0; i<populationToCount.size(); i++)
        {
            people +=populationToCount.get(i).Size;
        }
        return people;
    }

    // -----------------------------------------------------------------------------------------------------------------  Process ABS data
    private void generatePopulation()
    {
        // create 100% population
        for(int i=0; i<samplePeople.size(); i++)
        {
            generateFromFamily(samplePeople.get(i));
        }
    }

    /* * -----------------------------------------------------------------------------------------------------------------  Process ABS data
    *   For each family (which represents 1%), replicate to create
    *   100% population.
    * */
    private void generateFromFamily(Family family)
    {
        try
        {
            family = family.clone();
            Family syntheticFamily = null;

            // replicate the same family & dwelling x times to reconstruct from the 1% data
            for (int count = 0; count < family.weight; count++)
            {
                // create a dwelling with the same properties
                Dwelling dwelling = family.dwelling.clone();

                // if empty dwelling (it was cleared out because of the gender rewire)
                if(family.Size == 0)
                {
                    clearedDwellings++;
                }
                else
                {
                    boolean addNewDwelling = true;

                    // if a non-private dwelling
                    if(dwelling.type == 6)
                    {
                        nonprivdwellPeopleCount++;

                        //  reduce by factor of 2.6
                        if((nonprivdwellPeopleCount%60) == 0 || count == 0)
                        {
                            nonprivdwellCount++;
                            addNewDwelling = true;
                        }
                        else
                        {
                            addNewDwelling = false;
                        }
                    }

                    if(addNewDwelling)
                    {
                        // add a new dwelling
                        dwelling.ddID = nextID_DD++;
                        dwelling.hhID = nextID_HH++;

                        // create the household
                        Household household = new Household();
                        household.ddID = dwelling.ddID;
                        household.hhID = dwelling.hhID;
                        household.FamilySize = family.Size;
                        household.Cars = dwelling.cars;
                        //household.ZoneSA1 = dwelling.zoneSA1;

                        // create the new family
                        syntheticFamily = new Family();
                        syntheticFamily.dwelling = dwelling;
                        syntheticFamily.household = household;
                    }

                    int peopleMarried = 0;

                    // save the people that live in this dwelling
                    for (int s = 0; s < family.Size; s++)
                    {
                        // fetch the person record
                        Person person = family.People.get(s).clone();

                        // assign new person id, household, dwelling and a job
                        person.ppID = nextID_PP++;
                        person.ddID = syntheticFamily.dwelling.ddID;
                        person.hhID = syntheticFamily.dwelling.hhID;
                        person.jjID = jobCollection.assignWorker(person.ppID, person.Industry);

                        // update family details
                        syntheticFamily.addPerson(person);

                        // stats
                        if(person.Relationship.compareTo("married") == 0) peopleMarried++;
                        if(person.Occupation == 1) employedCount++;
                        if(person.Occupation == 2) unemployedCount++;
                    }

                    // store the family
                    if(addNewDwelling)
                    {
                        populationPeople.add(syntheticFamily);
                    }

                    // if there is an uneven number of married people there is an issue
                    if(count == 0 && peopleMarried >= 1 && peopleMarried%2 != 0) logger.error("SEPARATED MARRIED PEOPLE in dwelling!");
                }
            }
        }
        catch(Exception ex)
        {
            logger.error(ex.getMessage());
        }
    }

    // -----------------------------------------------------------------------------------------------------------------  Quality
    private void distributeToSA1()
    {
        for(int i = 0; i < populationPeople.size(); i++)
        {
            // fetch each family
            Family family = populationPeople.get(i);
            family.autoFillCount();
            int SA4 = family.dwelling.zoneSA4;

            // get the list of SA1 zones to the corresponding SA4 of this family
            ZoneSA1[] zones = zoneMap.get(SA4);

            ArrayList<ZoneSA1> arrayList = new ArrayList<ZoneSA1>(Arrays.asList(zones));
            Collections.sort(arrayList, Collections.reverseOrder());
            zones = arrayList.toArray(new ZoneSA1[0]);

            int[] ageGroups = new int[zones[0].GroupSize];
            for(int j = 0; j < family.Size; j++)
            {
                int ageGroup = zones[0].determineAgeGroup(family.People.get(j).Age);
                ageGroups[ageGroup] += 1;
            }

            int[] incGroups = new int[zones[0].IncGroupSize];
            for(int j = 0; j < family.Size; j++)
            {
                incGroups[family.People.get(j).WeeklyIncCode] += 1;
            }

            // find the zone that best matches
            int highestScore = -1;
            int bestZone = 1;
            for(int z = 0; z < zones.length; z++)
            {
                ZoneSA1 zone = zones[z];

                if(zone.getTotalCount() > 0)
                {
                    int matchPoints = 0, tmpPoints;

                    // age groups only
                    tmpPoints = 0;
                    for(int k = 0; k < ageGroups.length; k++)
                    {
                        if(ageGroups[k] > 0 && ageGroups[k] <= zone.getAgeGroup(k))
                        {
                            tmpPoints += ageGroups[k];
                        }
                    }
                    if(tmpPoints == family.Size)
                    {
                        matchPoints += 50;
                    }

                    // income groups only
                    tmpPoints = 0;
                    for(int k = 0; k < incGroups.length; k++)
                    {
                        if(incGroups[k] > 0 && incGroups[k] <= zone.getIncomeGroup(k))
                        {
                            tmpPoints += incGroups[k];
                        }
                    }
                    if(tmpPoints == family.Size)
                    {
                        matchPoints += 100;
                    }
                    else
                    {
                        matchPoints += tmpPoints;
                    }

                    // genders only
                    if(family.maleCount <= zone.getMaleCounts())
                    {
                        matchPoints += 10;
                    }
                    if(family.femaleCount <= zone.getFemaleCounts())
                    {
                        matchPoints += 10;
                    }

                    if(matchPoints > highestScore)
                    {
                        highestScore = matchPoints;
                        bestZone = z;
                    }
                }
            }

            if(highestScore == -1)
                logger.warn("oh no");

            // get the best zone
            ZoneSA1 selectedZone = zones[bestZone];

            // assign the zone to the family
            family.dwelling.zoneSA1 = selectedZone.SA1;
            family.household.ZoneSA1 = selectedZone.SA1;

            // store the dwelling for the quality & vacant dwellings
            storeDwelling(family.dwelling);

            // update the zone counts
            //logger.info(selectedZone.toString());
            selectedZone.update(family.maleCount, family.femaleCount, ageGroups, incGroups);
            //logger.info(selectedZone.toString());

            // update the map
            zones[bestZone] = selectedZone;
            zoneMap.put(SA4, zones);
        }
    }

    /*  Translate families from general area codes to more fine-grained
        statistical SA1 zones. Based on the statistical relationship
        between gender population in each zone and a family info in each
        dwelling in each area.
    */
    /*
    private int translateSaZone(int pumaZone, int hhCountM, int hhCountF)
    {
        // store probabilities of assignment
        RandomCollection<Integer> rc = new RandomCollection<>();

        // calculate totals to use for probability of assignment
        ZoneSA1[] zones = zoneMap.get(pumaZone);
        int totalM = 0;
        int totalF = 0;
        for(int z = 0; z < zones.length; z++)
        {
            totalM += zones[z].CountMale;
            totalF += zones[z].CountFemale;
        }
        double totalA = totalM + totalF + 1;

        // calculate the probabilities
        for(int z = 0; z < zones.length; z++)
        {
            double weightM = zones[z].CountMale + hhCountM*100;
            double weightF = zones[z].CountFemale + hhCountF*100;

            double probabilityM = (weightM/totalA)*100 + 0.0001;
            double probabilityF = (weightF/totalA)*100 + 0.0001;

            zones[z].Probability = Math.max(probabilityM, probabilityF);
            rc.add(zones[z].Probability, z);
        }

        // assign the household to the zone using weighted probabilities
        int z = rc.next();
        zones[z].CountMale -= hhCountM;
        zones[z].CountFemale -= hhCountF;
        if(zones[z].CountMale < 0 ) zones[z].CountMale = 0;
        if(zones[z].CountFemale < 0 ) zones[z].CountFemale = 0;

        return zones[z].Zone;
    }
    */

    // -----------------------------------------------------------------------------------------------------------------  Quality
    private void storeDwelling(Dwelling dwelling)
    {
        // store the to be saved later
        dwellingList.add(dwelling);

        // store it under the zone to be used for vacant dwellings
        ArrayList<Dwelling> dwls = dwellingsPerZoneMap.get(dwelling.zoneSA1);
        if(dwls == null)
            dwls = new ArrayList<>();
        dwls.add(dwelling);
        dwellingsPerZoneMap.put(dwelling.zoneSA1, dwls);

        // add dwelling price to calculate quality later
        addToQuality(dwelling.zoneSA1, dwelling.type, dwelling.price, dwelling.bedrooms);
    }

    // -----------------------------------------------------------------------------------------------------------------  Quality
    // Store all the data to the appropiate files
    private void savePumsRecords()
    {
        // save people
        for(int i = 0; i < populationPeople.size(); i++)
        {
            // fetch each family
            Family family = populationPeople.get(i);

            // for each member in that family
            for(int j = 0; j < family.Size; j++)
            {
                // get the person
                Person person = family.People.get(j);

                // save the record
                pwpp.println(person.ppID + "," + person.hhID + "," + person.Age + "," + person.Sex + ","
                        + person.Relationship + "," + person.Race + "," +  person.Occupation + ","
                        + person.jjID + "," + person.Income);
            }

            // get the household for this family
            Household household = family.household;

            // save the record
            pwhh.println(household.hhID + "," + household.ddID + "," + household.ZoneSA1 + "," + household.FamilySize + "," + household.Cars);
        }
    }

    //  -----------------------------------------------------------------------------------------------------------------  Quality
    /*  Keep a hashmap that maps each zone to the number of dwellings
        and sums their relative price. This will be used to later
        calculate average for each zone and determine the quality.
     */
    HashMap<Integer, DwellingQuality> qualityMap = new HashMap<Integer, DwellingQuality>();
    private void addToQuality(int zone, int ddType, int price, int rooms)
    {
        DwellingQuality quality = qualityMap.get(zone);
        if (quality == null)
        {
            quality = new DwellingQuality();
        }

        quality.addToZoneQuality(price);
        quality.addToDTypeQuality(ddType, price, rooms);
        qualityMap.put(zone, quality);
    }

    /*  Determine the quality of each dwelling in each zone.
        Based on the properties of the dwelling as well as average quality of the dwelling type
        and the average quality of the zone.
     */
    private void determineDwellingQuality()
    {
        for(int i = 0; i < dwellingList.size(); i++)
        {
            DwellingQuality quality = qualityMap.get(dwellingList.get(i).zoneSA1);
            if(quality != null)
            {
                Dwelling dwelling = dwellingList.get(i);

                double zoneQuality = quality.getQuality();
                double zoneTypeQuality = quality.getZoneDTypeQuality(dwelling.type);

                //double dwellingQuality = (dwelling.price/zoneQuality)*(quality.determineTypeQuality(dwelling.price, dwelling.bedrooms)/zoneTypeQuality);
                dwellingList.get(i).quality = 1;//(int)(dwellingQuality);
            }
            else
            {
                dwellingList.get(i).quality = 0;
                logger.warn("Quality not determined for hhid: " + dwellingList.get(i).hhID);
            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------------  Vacant Dwellings
    /*  Add vacant dwellings based on the given list. The list contains
        the number of empty dwellings of each of type in each SA1 zone.
        Hence copy properties of the same type of a dwelling as the
        empty one to create a vacant dwelling.
     */
    private void addVacantDwellings()
    {
        Random rand = new Random();

        // for each zone
        for(int r = 1; r <= dwellUnoccPerArea.getRowCount(); r++)
        {
            // get the zone
            int zone = (int)dwellUnoccPerArea.getValueAt(r, 2);

            // go through each column (dwelling type) for this zone     // columns: 0 = nothing, 1 = index, 2 = SA1
            for(int c = 3; c <= dwellUnoccPerArea.getColumnCount(); c++)
            {
                // get the type of the dwelling
                int ddType = Integer.parseInt(dwellUnoccPerArea.getColumnLabel(c));

                // get how many of dwellings of that type there are
                int count = (int)dwellUnoccPerArea.getValueAt(r, c);
                if(count > 0)
                {
                    // find dwellings in the same zone
                    ArrayList<Dwelling> dwellings = dwellingsPerZoneMap.get(zone);

                    // find dwellings of the same type
                    ArrayList<Dwelling> dwellingsSameType = findDwellingsOfSameType(dwellings, ddType);

                    // if no dwellings found, try the neighbourhood
                    if(dwellingsSameType.size() < 1)
                    {
                        int counter = 1;

                        while(dwellingsSameType.size() < 1 && counter <= 99)
                        {
                            dwellings = dwellingsPerZoneMap.get(zone+counter);
                            dwellingsSameType = findDwellingsOfSameType(dwellings, ddType);

                            if(dwellingsSameType.size() < 1)
                            {
                                dwellings = dwellingsPerZoneMap.get(zone-counter);
                                dwellingsSameType = findDwellingsOfSameType(dwellings, ddType);
                            }
                            counter++;
                        }
                        if(dwellingsSameType.size() == 0) logger.warn("Failed to find a dwelling for " + ddType + " in zone " + zone);
                    }

                    // if any dwellings found
                    if(dwellingsSameType.size() > 0)
                    {
                        // create and add those new (vacant) dwellings
                        for(int i = 0; i < count; i++)
                        {
                            // randomly select a dwelling to be copied
                            // todo what if there is only 1 dwelling of that type in the zone?
                            Dwelling oocDwelling = dwellingsSameType.get(rand.nextInt(dwellingsSameType.size()));

                            // create a new vacant dwelling using existing dwelling's properties
                            Dwelling vaccDwelling = new Dwelling();
                            vaccDwelling.ddID = nextID_DD++;
                            vaccDwelling.zoneSA1 = zone;
                            vaccDwelling.type = ddType;
                            vaccDwelling.hhID = -1;
                            vaccDwelling.bedrooms = oocDwelling.bedrooms;
                            vaccDwelling.price = oocDwelling.price;
                            vaccDwelling.quality = oocDwelling.quality;

                            // add the dwelling to be later saved to the file
                            dwellingList.add(vaccDwelling);
                        }
                    }
                    else
                    {
                        logger.error("Failed to add dwelling type " + ddType + " in zone " + zone);
                    }
                }
            }
        }
    }
    private ArrayList<Dwelling> findDwellingsOfSameType(ArrayList<Dwelling> dwellings, int ddType)
    {
        // find dwellings of the same type
        ArrayList<Dwelling> dwellingsSameType = new ArrayList<>();
        if(dwellings != null)
        {
            for(int j = 0; j < dwellings.size(); j++)
            {
                Dwelling dwelling = dwellings.get(j);
                if(dwelling.type == ddType)
                {
                    dwellingsSameType.add(dwelling);
                }
            }
        }
        return dwellingsSameType;
    }


    // -----------------------------------------------------------------------------------------------------------------  Save Output
    /*  Write all of the dwellings to the file.
        This is done later (than writing of people & households) because there is post-processing required
        such as adding quality and vacant dwellings. Hence global array is stored and then saved.
    */
    private void saveDwellings()
    {
        for(int i = 0; i < dwellingList.size(); i++)
        {
            Dwelling dwelling = dwellingList.get(i);
            pwdd.println(dwelling.ddID + "," + dwelling.zoneSA1 + "," + dwelling.type + "," +
                    dwelling.hhID + "," + dwelling.bedrooms + "," + dwelling.quality + "," + dwelling.price);
            // pwdd.println("id,zone,type,hhID,bedrooms,quality,monthlyCost");
        }
    }

    /*  Similarly to dwellings, jobs are stored in memory first for further processing.
        Jobs are assigned to workers and then saved later.
    */
    private void saveJobs()
    {
        int vacantJobs = 0;
        int takenJobs = jobCollection.jobSlots.size();

        // save every occupied job
        for(int i = 0; i < jobCollection.jobSlots.size(); i++)
        {
            JobSlot job = jobCollection.jobSlots.get(i);
            pwjj.println(job.ID + "," + job.Zone + "," + job.PersonID + "," + job.Type);
        }

        // save every unoccupied job
        for(Map.Entry<Integer, ArrayList<JobSlot>> entry : jobCollection.jobTypesMap.entrySet())
        {
            ArrayList<JobSlot> jobSlots = entry.getValue();
            vacantJobs += jobSlots.size();
            for(int i = 0; i<jobSlots.size(); i++)
            {
                JobSlot job = jobSlots.get(i);
                pwjj.println(job.ID + "," + job.Zone + "," + job.PersonID + "," + job.Type);
            }
        }

        logger.info("\tThere are " + takenJobs + " occupied jobs and " + vacantJobs
                + " vacant jobs. The job vacancy rate is " + ((double)vacantJobs/(double)takenJobs)*100 + "%.");
    }

    // -----------------------------------------------------------------------------------------------------------------  ABS to SILO translate
    /*  Select actual age from bins provided in the microdata
        Ages: 0-24: 0-24 years singly
                25: 25-29 years
                26: 30–34 years
                27: 35–39 years
                28: 40–44 years
                29: 45–49 years
                30: 50–54 years
                31: 55–59 years
                32: 60–64 years
                33: 65–69 years
                34: 70–74 years
                35: 75–79 years
                36: 80–84 years
                37: 85 years and over
    */
    private int convertAge(int ageGroup)
    {
        int selectedAge = 0;
        float rnd = SiloUtil.getRandomNumberAsFloat();
        if (ageGroup <= 24)
        {
            selectedAge = ageGroup;
        }
        else
        {
            switch (ageGroup)
            {
                case 25: selectedAge = (int) (25 + rnd * 5);
                    break;
                case 26: selectedAge = (int) (30 + rnd * 5);
                    break;
                case 27: selectedAge = (int) (35 + rnd * 5);
                    break;
                case 28: selectedAge = (int) (40 + rnd * 5);
                    break;
                case 29: selectedAge = (int) (45 + rnd * 5);
                    break;
                case 30: selectedAge = (int) (50 + rnd * 5);
                    break;
                case 31: selectedAge = (int) (55 + rnd * 5);
                    break;
                case 32: selectedAge = (int) (60 + rnd * 5);
                    break;
                case 33: selectedAge = (int) (65 + rnd * 5);
                    break;
                case 34: selectedAge = (int) (70 + rnd * 5);
                    break;
                case 35: selectedAge = (int) (75 + rnd * 5);
                    break;
                case 36: selectedAge = (int) (80 + rnd * 5);
                    break;
                case 37: selectedAge = (int) (85 + rnd * 15);
                    break;
            }
        }
        return selectedAge;
    }


    private int convertAutos(int autoCode)
    {
        // select actual number of autos from indicators provided in ABS microdata
        // 0: None
        // 1: 1 motor vehicle
        // 2: 2 motor vehicles
        // 3: 3 motor vehicles
        // 4: 4 or more motor vehicles; set 4 cars as maximum
        // 5: Not stated
        // 6: Not applicable
        if (autoCode <= 4){
            return autoCode;
        } else {
            return 0;
        }
    }


    private int convertBedrooms(int bedroomCode)
    {
        // select actual number of from indicators provided in ABS microdata
        //  0: None (includes bedsitters)
        //  1: 1 bedroom
        //  2: 2 bedrooms
        //  3: 3 bedrooms
        //  4: 4 bedrooms
        //  5: 5 or more bedrooms
        //  6: Not stated = random 5 7
        //  7: Not applicable
        if (bedroomCode < 5)
        {
            return bedroomCode;
        }
        else if (bedroomCode == 5 || bedroomCode == 6)
        {
            float rnd = SiloUtil.getRandomNumberAsFloat();
            return (int) (5 + rnd * 2);
        }
        else {
            return 0;
        }
    }


    private int convertIncome(int incomeCode) {
        // select actual income from bins provided in microdata
        //  1: Negative income
        //  2: Nil income
        //  3: $1–$199
        //  4: $200–$299
        //  5: $300–$399
        //  6: $400–$599
        //  7: $600–$799
        //  8: $800–$999
        //  9: $1,000–$1,249
        // 10: $1,250–$1,499
        // 11: $1,500–$1,999
        // 12: $2,000 or more
        // 13: Not stated
        // 14: Not applicable
        // 15: Overseas visitor
        float rnd = SiloUtil.getRandomNumberAsFloat();
        switch (incomeCode) {
            case 1: case 2: return 0;
            case 3: return (int) ((1 + rnd * 199)*52/12);
            case 4: return (int) ((200 + rnd * 100)*52/12);
            case 5: return (int) ((300 + rnd * 100)*52/12);
            case 6: return (int) ((400 + rnd * 200)*52/12);
            case 7: return (int) ((600 + rnd * 200)*52/12);
            case 8: return (int) ((800 + rnd * 200)*52/12);
            case 9: return (int) ((1000 + rnd * 250)*52/12);
            case 10: return (int) ((1250 + rnd * 250)*52/12);
            case 11: return (int) ((1500 + rnd * 500)*52/12);
            case 12: return (int) ((2000 + rnd * 20000)*52/12);
            case 13: case 14: case 15: return 0;
            default: logger.error("Unknown income code " + incomeCode);
                return 0;
        }
    }

    private int incomeCode2WeeklySILO(int incomeCode) {
        if(incomeCode == 1 || incomeCode == 2 || incomeCode >= 13)
        {
            return 0;
        }
        return incomeCode - 2;
    }

    private int convertMortgage(int mortgageCode)
    {
        // select actual mortgage from bins provided in microdata (from "MRERD")
        //  1: Nil repayments
        //  2: $1–$149
        //  3: $150–$299
        //  4: $300–$449
        //  5: $450–$599
        //  6: $600–$799
        //  7: $800–$999
        //  8: $1,000–$1,199
        //  9: $1,200–$1,399
        // 10: $1,400–$1,599
        // 11: $1,600–$1,799
        // 12: $1,800–$1,999
        // 13: $2,000–$2,199
        // 14: $2,200–$2,399
        // 15: $2,400–$2,599
        // 16: $2,600–$2,999
        // 17: $3,000–$3,999
        // 18: $4,000-$4,999
        // 19: $5,000 and over
        // 20: Not stated
        // 21: Not applicable

        int selectedMortgage = 0;
        float rnd = SiloUtil.getRandomNumberAsFloat();
        switch (mortgageCode) {
            case 1: selectedMortgage = 0;
                break;
            case 2: selectedMortgage = (int) (1 + rnd * 149);
                break;
            case 3: selectedMortgage = (int) (150 + rnd * 149);
                break;
            case 4: selectedMortgage = (int) (300 + rnd * 149);
                break;
            case 5: selectedMortgage = (int) (450 + rnd * 150);
                break;
            case 6: selectedMortgage = (int) (600 + rnd * 100);
                break;
            case 7: selectedMortgage = (int) (800 + rnd * 100);
                break;
            case 8: selectedMortgage = (int) (1000 + rnd * 100);
                break;
            case 9: selectedMortgage = (int) (1200 + rnd * 100);
                break;
            case 10: selectedMortgage = (int) (1400 + rnd * 100);
                break;
            case 11: selectedMortgage = (int) (1600 + rnd * 150);
                break;
            case 12: selectedMortgage = (int) (1800 + rnd * 200);
                break;
            case 13: selectedMortgage = (int) (2000 + rnd * 200);
                break;
            case 14: selectedMortgage = (int) (2200 + rnd * 400);
                break;
            case 15: selectedMortgage = (int) (2400 + rnd * 400);
                break;
            case 16: selectedMortgage = (int) (2600 + rnd * 600);
                break;
            case 17: selectedMortgage = (int) (3000 + rnd * 1000);
                break;
            case 18: selectedMortgage = (int) (4000 + rnd * 1000);
                break;
            case 19: selectedMortgage = (int) (5000 + rnd * 10000);
                break;
            case 20: selectedMortgage = 0;
                // todo: Check if case 20 ever appears - YES 161/5907 records
                break;
            case 21: selectedMortgage = 0;
                // todo: Check if case 21 ever appears - YES 3779 of 5907 records
                break;
        }
        return selectedMortgage;
    }

    private int convertRent (int rentCode)
    {
        // select actual rent from bins provided in microdata (from "RNTRD")
        //  1: Nil payments
        //  2: $1–$74
        //  3: $75–$99
        //  4: $100–$124
        //  5: $125–$149
        //  6: $150–$174
        //  7: $175–$199
        //  8: $200–$224
        //  9: $225–$249
        // 10: $250–$274
        // 11: $275–$299
        // 12: $300–$324
        // 13: $325–$349
        // 14: $350–$374
        // 15: $375–$399
        // 16: $400–$424
        // 17: $425-$449
        // 18: $450-$549
        // 19: $550-$649
        // 20: 650 and over
        // 21: Not stated
        // 22: Not applicable
        int selectedRent = 0;
        float rnd = SiloUtil.getRandomNumberAsFloat();
        switch (rentCode) {
            case 1: selectedRent = 0;
                break;
            case 2: selectedRent = (int) (1 + rnd * 74);
                break;
            case 3: selectedRent = (int) (75 + rnd * 24);
                break;
            case 4: selectedRent = (int) (100 + rnd * 24);
                break;
            case 5: selectedRent = (int) (125 + rnd * 24);
                break;
            case 6: selectedRent = (int) (150 + rnd * 24);
                break;
            case 7: selectedRent = (int) (175 + rnd * 24);
                break;
            case 8: selectedRent = (int) (200 + rnd * 24);
                break;
            case 9: selectedRent = (int) (225 + rnd * 24);
                break;
            case 10: selectedRent = (int) (250 + rnd * 24);
                break;
            case 11: selectedRent = (int) (275 + rnd * 24);
                break;
            case 12: selectedRent = (int) (300 + rnd * 24);
                break;
            case 13: selectedRent = (int) (325 + rnd * 24);
                break;
            case 14: selectedRent = (int) (350 + rnd * 24);
                break;
            case 15: selectedRent = (int) (375 + rnd * 24);
                break;
            case 16: selectedRent = (int) (400 + rnd * 24);
                break;
            case 17: selectedRent = (int) (425 + rnd * 24);
                break;
            case 18: selectedRent = (int) (450 + rnd * 99);
                break;
            case 19: selectedRent = (int) (550 + rnd * 99);
                break;
            case 20: selectedRent = (int) (650 + rnd * 2350);
                break;
            case 21: selectedRent = 0;
                // todo: Check if code 17 ever appears - YES 45 of 5907 records
                break;
            case 22: selectedRent = 0;
                // todo: Check if code 18 ever appears - YES 4507 of 5907 records
                break;
        }
        return selectedRent*52/12;
    }

    private int getDwellingPrice(int rent, int mortgage)
    {
        // calculate price based on rent and mortgage
        int price;
        if (rent > 0 && mortgage > 0) price = (rent + mortgage) / 2;
        else if (rent <= 0 && mortgage > 0) price = mortgage;
        else if (rent > 0 && mortgage <= 0) price = rent;
            // todo: create reasonable price for dwelling - careful with weekly rent and monthly mortgage payments, divide by 4 or times by 12 and divide by 52?!
        else price = 500;
        return price;
    }

    private int translateIndustry(int industryCode, int occupation)
    {
        // if employed
        if(occupation == 1)
        {
            // overseas visitor is converted to not applicable
            if(industryCode != 23)
            {
                // industry codes match as it has been adjusted in the createJob method
                return industryCode;
            }
        }
        else
        {
            if (industryCode != 23 && industryCode != 22)
            {
                logger.warn("possible job for occupation: " + occupation + " in industry " + industryCode);
                unassignedWorker++;
            }
        }

        // default is unemployed
        return -1;
    }

    private int translateOccupation(int pumsOccupation, int personsAge)
    {
        // corresponding occupation integers
        int TODDLER = 0;
        int EMPLOYED = 1;
        int UNEMPLOYED = 2;
        int STUDENT = 3;
        int RETIRED = 4;

        // perth: 1 - Employed
        // perth: 2 - Unemployed
        // perth: 3 - Not in labor force
        // perth: 4 - Not stated
        // perth: 5 - Not applicable
        // perth: 6 - Overseas Visitor
        // RETIREE = Not in labor force
        // EMPLOYED = Employed, at work
        // UNEMPLOYED = Unemployed

        switch (pumsOccupation)
        {
            case 1:
                return EMPLOYED;
            case 2:
            case 6:
                return UNEMPLOYED;
            case 3:
            case 4:
            case 5:
                if (personsAge < 5)
                    return TODDLER;
                else if(personsAge < 22)
                    return STUDENT;
                else if(personsAge < 64)
                    return UNEMPLOYED;
                else
                    return RETIRED;
            default:
                return RETIRED;
        }

    }

    private String translateRelationship(int status)
    {
        // define roles as single, married or child from ABS microdata "RLHP Relationship in Household"
        //  1 Husband, Wife or Partner              MARRIED
        //  2 Lone parent                           SINGLE
        //  3 Child under 15                        CHILD
        //  4 Dependent student                     CHILD
        //  5 Non-dependent child                   SINGLE
        //  6 Other related individual              SINGLE
        //  7 Non-family member                     SINGLE
        //  8 Visitor (from within Australia)       SINGLE
        //  9 Other non-classifiable relationship   SINGLE
        // 10 Not applicable
        // 11 Overseas visitor

        switch (status)
        {
            case 1:
                return "married";
            case 3:
            case 4:
                return "child";
            default:
                return "single";
        }
    }

    // -----------------------------------------------------------------------------------------------------------------  Help Methods
    //  Weighted Random
    public class RandomCollection<E>
    {
        private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
        private final Random random;
        private double total = 0;

        public RandomCollection() {
            this(new Random());
        }
        public RandomCollection(Random random) {
            this.random = random;
        }

        public RandomCollection<E> add(double weight, E result)
        {
            if (weight <= 0) return this;
            total += weight;
            map.put(total, result);
            return this;
        }

        public E next()
        {
            double value = random.nextDouble() * total;
            return map.higherEntry(value).getValue();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------  Objects
    private class JobSlot
    {
        public long ID = -1;
        public int Zone = -1;
        public long PersonID = -1;
        public int Type = -1;

        public JobSlot() { }

        public JobSlot(long jjId, int jjZone, int jjWorker, int jjType)
        {
            ID = jjId;
            Zone = jjZone;
            PersonID = jjWorker;
            Type = jjType;
        }
    }
    private class JobCollection
    {
        ArrayList<JobSlot> jobSlots = new ArrayList<>();
        HashMap<Integer, ArrayList<JobSlot>> jobTypesMap = new HashMap<>();

        public JobCollection() { }

        public void addNewJob(JobSlot job)
        {
            ArrayList<JobSlot> jobSlotsType = jobTypesMap.get(job.Type);
            if(jobSlotsType == null)
            {
                jobSlotsType = new ArrayList<>();
            }
            jobSlotsType.add(job);
            jobTypesMap.put(job.Type, jobSlotsType);
        }

        public void addOccupiedJob(JobSlot job)
        {
            jobSlots.add(job);
            ArrayList<JobSlot> jobSlotsType = jobTypesMap.get(job.Type);
            for(int i = 0; i < jobSlotsType.size(); i++)
            {
                if(jobSlotsType.get(i).ID == job.ID)
                {
                    jobSlotsType.remove(i);
                    break;
                }
            }
        }

        public long assignWorker(long jjPersonId, int jjType)
        {
            if(jjType != -1)
            {
                Random rand = new Random();

                // get the corresponding job types
                ArrayList<JobSlot> jobSlotsType = jobTypesMap.get(jjType);

                // select a random job
                if(jobSlotsType != null)
                {
                    if(jobSlotsType.size() > 0)
                    {
                        JobSlot job = jobSlotsType.get(rand.nextInt(jobSlotsType.size()));
                        job.PersonID = jjPersonId;
                        addOccupiedJob(job);
                        return job.ID;
                    }
                    else
                    {
                        // could be worker from outside of study area (id = -2)
                        // a lot of people do fly-in fly-out, so assign
                        return -2;
                    }
                }
                unassignedWorker++;

                logger.warn("Did not find a job for industry: " + jjType);
            }
            return -1;
        }
    }
    // -----------------------------------------------------------------------------------------------------------------  Objects
    private class Person implements Cloneable
    {
        public int Age;
        public int Sex;
        public int Race;
        public int Income; // monthly income
        public int WeeklyIncCode;
        public int Occupation;
        public int Industry;
        public int ZoneSA1;
        public long ppID;
        public long hhID;
        public long ddID;
        public long jjID;
        public String Relationship;

        public Person() { }

        public Person clone()throws CloneNotSupportedException{ return (Person) super.clone(); }
    }

    // -----------------------------------------------------------------------------------------------------------------  Objects
    public class Household implements Cloneable
    {
        public long hhID;
        public long ddID;
        public int ZoneSA1;
        public int FamilySize;
        public int Cars;

        public Household() { }

        public Household clone()throws CloneNotSupportedException{  return (Household) super.clone(); }
    }

    // -----------------------------------------------------------------------------------------------------------------  Objects
    private class Family implements Cloneable, Comparable<Family>
    {
        public ArrayList<Person> People;

        public Dwelling dwelling = null;
        public Household household = null;

        public int weight;
        public int Size;
        public int maleCount;
        public int femaleCount;
        public int marriedPeople;
        public int marriedMales;
        public int marriedFemales;

        public Family()
        {
            weight = 100;
            Size = 0;
            People = new ArrayList<>();
        }

        // -----------------------------------------------------------------    Clone interface
        public Family clone()throws CloneNotSupportedException{ return (Family) super.clone(); }

        // -----------------------------------------------------------------
        public void append(Family family)
        {
            for (int j = 0; j < family.Size; j++)
            {
                addPerson(family.People.get(j));
            }

            // transfer the cars
            if(dwelling != null && family.dwelling != null)
            {
                dwelling.cars += family.dwelling.cars;
            }

            autoFillCount();
        }

        public void addPerson(Person person)
        {
            People.add(person);
            Size++;
        }

        public void autoFillCount()
        {
            maleCount = 0;
            femaleCount = 0;
            marriedPeople = 0;

            for(int i=0; i<Size; i++)
            {
                // gender
                if(People.get(i).Sex == CODE_MALE)
                {
                    maleCount++;
                }
                else
                {
                    femaleCount++;
                }

                // marriage status
                if(People.get(i).Relationship.compareTo("married") == 0)
                {
                    marriedPeople++;
                }
            }
        }

        public int SingleAndLookingFor()
        {
            marriedMales = 0;
            marriedFemales = 0;
            for(int i=0; i<Size; i++)
            {
                if(People.get(i).Relationship.compareTo("married") == 0)
                {
                    if(People.get(i).Sex == CODE_MALE)
                    {
                        marriedMales++;
                    }
                    else if(People.get(i).Sex == CODE_FEMALE)
                    {
                        marriedFemales++;
                    }
                }
            }

            // if more males than females, then looking for a female
            if(marriedMales > marriedFemales)
            {
                return CODE_FEMALE;
            }
            // if more females than males, then looking for a male
            else if(marriedFemales > marriedMales)
            {
                return  CODE_MALE;
            }
            return 0;
        }

        public void emptyTheDwelling()
        {
            People = null;
            Size = 0;
            marriedPeople = 0;
            maleCount = 0;
            femaleCount = 0;
        }

        // -----------------------------------------------------------------    Comparable used for sorting
        @Override
        public int compareTo(Family o)
        {
            Integer s1 = Size;
            Integer s2 = o.Size;
            return s1.compareTo(s2);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------  Objects
    private class Dwelling implements Cloneable
    {
        public long ddID;
        public long hhID;
        public int zoneSA1;
        public int zoneSA4;
        public int type;
        public int bedrooms;
        public int quality;
        public int price;
        public int mortgage;
        public int rent;
        public int cars;

        public Dwelling() { }

        public Dwelling clone()throws CloneNotSupportedException{ return (Dwelling) super.clone(); }
    }

    private class DwellingQuality
    {
        // store overall quality for the zone
        public double Sum = 0;
        public int N = 0;

        public int Zone = -1;

        HashMap<Integer, DwellingQuality> TypeQuality = new HashMap<>();

        public DwellingQuality() { }

        public double getQuality()
        {
            return Sum / N;
        }

        public double getZoneDTypeQuality(int type)
        {
            return TypeQuality.get(type).getQuality();
        }

        public void addToZoneQuality(int price)
        {
            Sum += price;
            N++;
        }

        public void addToDTypeQuality(int ddType, int price, int rooms)
        {
            DwellingQuality typeQ = TypeQuality.get(ddType);
            if(typeQ == null)
            {
                typeQ = new DwellingQuality();
            }
            typeQ.addTypeQuality(price, rooms);
            TypeQuality.put(ddType, typeQ);
        }

        public void addTypeQuality(int price, int rooms)
        {
            Sum += determineTypeQuality(price, rooms);
            N++;
        }

        public double determineTypeQuality(int price, int rooms)
        {
            if(rooms == 0) rooms = 10;
            return price/rooms;
        }
    }

    private class ZoneSA1 implements Comparable<ZoneSA1>
    {
        // Zone info
        public int SA1;
        public int SA4;
        public double Probability = 0;

        // Gender only
        public int[] GenderTotals;

        // Age groups only
        public int[] AgeGroups;

        // Gender & age combined
        public int[][] GenderAgeGroup;

        // Income groups only
        public int[] IncomeGroups;

        // constants
        private int posM = 0; // male index in the gender arrays
        private int posF = 1; // female index in the gender arrays
        private int ageIntervals; // age group intervals (e.g. by 5);

        public int GroupSize;
        public int IncGroupSize;

        public ZoneSA1(int ageInterval, int incomeGroups)
        {
            // save age group interval
            ageIntervals = ageInterval;
            IncGroupSize = incomeGroups;

            // e.g. 100 (max age interval) / 5 (interval) + 1 (100 and over) = 21
            GroupSize = (100 / ageInterval) + 1;

            AgeGroups = new int[GroupSize];
            GenderAgeGroup = new int[2][GroupSize]; // male age groups, female age groups
            GenderTotals = new int[2]; // male, female
            IncomeGroups = new int[incomeGroups];
        }

        // -----------------------------------------------------------------
        public int getMaleCounts()
        {
            return GenderTotals[posM];
        }

        public int getFemaleCounts()
        {
            return GenderTotals[posF];
        }

        public int getTotalCount()
        {
            return (getMaleCounts() + getFemaleCounts());
        }

        public void setMaleCounts(int totalMC)
        {
            GenderTotals[posM] = totalMC;
        }

        public void setFemaleCounts(int totalFC)
        {
            GenderTotals[posF] = totalFC;
        }

        // -----------------------------------------------------------------
        public int getAgeGroup(int index)
        {
            return AgeGroups[index];
        }

        public void setAgeGroup(int index, int totalAC)
        {
            AgeGroups[index] = totalAC;
        }

        public int determineAgeGroup(int age)
        {
            // round down, since index starts at 0 no need to +1
            return (int)((double)age / (double)ageIntervals);
        }

        // -----------------------------------------------------------------
        public int getMaleAgeGroup(int index)
        {
            return GenderAgeGroup[posM][index];
        }

        public int getFemaleAgeGroup(int index)
        {
            return GenderAgeGroup[posF][index];
        }

        public void setMaleAgeGroup(int index, int totalMC)
        {
            GenderAgeGroup[posM][index] = totalMC;
        }

        public void setFemaleAgeGroup(int index, int totalFC)
        {
            GenderAgeGroup[posF][index] = totalFC;
        }

        // -----------------------------------------------------------------
        public int getIncomeGroup(int index)
        {
            return IncomeGroups[index];
        }

        public void setIncomeGroup(int index, int totalAC)
        {
            IncomeGroups[index] = totalAC;
        }

        public void appendToIncomeGroup(int index, int additional)
        {
            IncomeGroups[index] += additional;
        }
        public int determineIncomeGroup(int income)
        {
            // round down, since index starts at 0 no need to +1
            return 0;
        }

        // -----------------------------------------------------------------
        public void update(int totalCountMale, int totalCountFemale, int[] ageGroups, int[] incGroups)
        {
            // set gender totals
            setMaleCounts(getMaleCounts() - totalCountMale);
            setFemaleCounts(getFemaleCounts() - totalCountFemale);

            // set age groups
            for(int i = 0; i < GroupSize; i++)
            {
                setAgeGroup(i, (getAgeGroup(i) - ageGroups[i]));
            }

            // set inc groups
            for(int i = 0; i < IncGroupSize; i++)
            {
                setIncomeGroup(i, (getIncomeGroup(i) - incGroups[i]));
            }
        }

        // -----------------------------------------------------------------    Comparable used for sorting
        @Override
        public int compareTo(ZoneSA1 o)
        {
            Integer s1 = getTotalCount();
            Integer s2 = o.getTotalCount();
            return s1.compareTo(s2);
        }

        // -----------------------------------------------------------------
        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            StringBuilder sbAgeGroups = new StringBuilder("Age groups: ");
            StringBuilder sbAgeGroupsM= new StringBuilder("Male Age groups: ");
            StringBuilder sbAgeGroupsF = new StringBuilder("Female Age groups: ");

            for(int i = 0; i < GroupSize; i++)
            {
                sbAgeGroups.append(getAgeGroup(i) + ", ");
                sbAgeGroupsM.append(getMaleAgeGroup(i) + ", ");
                sbAgeGroupsF.append(getFemaleAgeGroup(i) + ", ");
            }
            sb.append("Total: " + (getMaleCounts()+getFemaleCounts()) + ", Males: " + getMaleCounts() + ", Females: " + getFemaleCounts());
            sb.append("\n");
            sb.append(sbAgeGroups);
            sb.append("\n");
            sb.append(sbAgeGroupsM);
            sb.append("\n");
            sb.append(sbAgeGroupsF);

            return sb.toString();
        }
    }

    private class ZoneSA1Jobs
    {
        int TotalJobs = 0;
        double Probability = 0;
        HashMap<Integer, ZoneJobProbability> JobType2Counts = new HashMap<>();
        RandomCollection<Integer> WeightedRandom = new RandomCollection<>();

        public ZoneSA1Jobs() { }

        public void AddJob(int jobType)
        {
            // add to the total for this SA1 zone
            TotalJobs += 1;

            // get the corresponding job types
            ZoneJobProbability jp = JobType2Counts.get(jobType);
            if(jp == null) { jp = new ZoneJobProbability(); }

            // add to the total for this job type in this zone
            jp.Count += 1;
            JobType2Counts.put(jobType, jp);
        }

        public void CalculateProbabilities()
        {
            for (int jobType : JobType2Counts.keySet())
            {
                ZoneJobProbability jp = JobType2Counts.get(jobType);
                jp.Probability = (double)((double)jp.Count / (double)TotalJobs);
                JobType2Counts.put(jobType, jp);

                WeightedRandom.add(jp.Probability, jobType);
            }
        }

        private class ZoneJobProbability
        {
            public int Count = 0;
            public double Probability = 0;

            public ZoneJobProbability() { }
        }
    }
}
