package de.tum.bgu.msm.syntheticPopulationGenerator.perth;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.syntheticPopulationGenerator.SyntheticPopI;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Generates a simple synthetic population for the perth Study Area
 * from 1% ABS data in 2011.
 * @author Rolf Moeckel (TUM) & Sonja Stemler (UWA) & Martin Porebski (UWA)
 * 13/05/2019 Perth
 */
public class SyntheticPopPerth implements SyntheticPopI {
    // constants
    private int ABS_CODE_MALE = 1;
    private int ABS_CODE_FEMALE = 2;
    private int ABS_CODE_SINGLE = 6;
    private int ABS_CODE_MARRIED = 1;
    private int ABS_CODE_EMPLOYED = 1;
    private int ABS_CODE_UNEMPLOYED = 2;
    private int ABS_CODE_NONPRIVATE_DWELLING = 6;
    private int ABS_AGE_GROUPS = 18; // [1, 18]
    private int ABS_INCOME_GROUPS = 15; // [1, 15]
    private int ABS_INDUSTRY_GROUPS = 23; // [1, 23]
    private int ABS_DWELL_GROUPS = 6; // [1, 6] structure, abs = silo
    private int ABS_BEDROOM_GROUPS = 8; // [0, 7]
    private int ABS_RENT_GROUPS = 22; // [1, 22]
    private int ABS_MORTGAGE_GROUPS = 21; // [1, 21]
    private int ABS_VEHICLE_GROUPS = 7; // [0, 6]
    private int POPULATION_WEIGHT = 100;

    protected static final String PROPERTIES_RUN_SP = "run.synth.pop.generator";
    protected static final String PROPERTIES_PUMS_PERSONS = "pums.persons";
    protected static final String PROPERTIES_PUMS_DWELLINGS = "pums.dwellings";

    protected transient Logger logger = Logger.getLogger(SyntheticPopPerth.class);
    private ResourceBundle rb;
    private String baseDirectory;

    // writers to save data in files
    private PrintWriter pwhh;
    private PrintWriter pwpp;
    private PrintWriter pwdd;
    private PrintWriter pwjj;
    private PrintWriter pwppabs;
    private PrintWriter pwddabs;

    // temporary data holders
    private ArrayList<Family> samplePopulation = new ArrayList<>(); // store information about 1% of the population
    private ArrayList<Family> fullPopulation = new ArrayList<>(); // store information about 100% population
    private ArrayList<Family> tmpNPDPopulation = new ArrayList<>();
    private HashMap<Integer, ZoneSA1[]> zoneMap = new HashMap<>(); // used for the distribution of the population
    private HashMap<Integer, ZoneSA1Jobs> JobDistributionSA1 = new HashMap<>();
    private JobCollection jobCollection = new JobCollection();
    private ArrayList<Dwelling> dwellingList = new ArrayList(); // store all of the dwellings (to be saved to file later)
    private HashMap<Integer, ArrayList<Dwelling>> dwellingsPerZoneMap = new HashMap<>(); // used for unocc private dwellings
    private HashMap<Integer, ZoneQuality> qualityMapSA1 = new HashMap<>(); // used to store average prices per structure
    private TableDataSet qualityMatrix; // used to store dwelling properties x quality matrix

    // statistical counts
    private long unassignedWorker = 0;
    private long unemployedCount = 0;
    private long employedCount = 0;
    private int npdPeopleCount = 0;
    private int npdCount = 0;
    private int clearedDwellings = 0;

    // iterate through IDs instead of data managers
    private long nextID_JJ = 1;
    private long nextID_PP = 1;
    private long nextID_HH = 1;
    private long nextID_DD = 1;

    // -----------------------------------------------------------------------------------------------------------------
    // constructor
    public SyntheticPopPerth(ResourceBundle rb)
    {
        this.rb = rb;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // run the synthetic population generator
    public void runSP()
    {
        int year = 2011;
        int jobsToAdd = 21834 - 3745; // there has to be 21834 unoccupied jobs and there were 3745 already

        if (!ResourceUtil.getBooleanProperty(rb, PROPERTIES_RUN_SP)) return;

        logger.info("Generating synthetic populations of households, persons, dwellings and jobs");
        baseDirectory = Properties.get().main.baseDirectory;

        // read the quality matrix
        qualityMatrix = SiloUtil.readCSVfile(baseDirectory + "input/perth_specific/dwellingQuality_" + year + ".csv");

        // open the files to save the data
        openFilesToWriteSyntheticPopulation(year);

        // open & pre-process the gender file used for the population distribution into SA1 zones
        logger.info("\t ---------- Preparing the population distribution data.");
        openPopulationDistribution(year);

        logger.info("\t ---------- Generating jobs.");
        createJobs(year);

        logger.info("\t ---------- Reading Australian ABS 1% data.");
        readMicroData();

        logger.info("Married people shall live together.");
        connectMarriedPeople(); // silo needs to have married people living together, done on 1% data

        sortPopulationByFamilySize(samplePopulation); // sort by family size, this will aid with population distribution

        logger.info("\t ---------- Generating population.");
        generatePopulation();

        logger.info("Sorting Perth's population by family size.");
        sortPopulationByFamilySize(fullPopulation);

        logger.info("Clearing non-private dwellings");
        cleanNonPrivDwellings(year);

        logger.info("Sorting Perth's population by family size.");
        sortPopulationByFamilySize(fullPopulation);

        logger.info("\t ---------- Distributing people and dwellings into SA1 zones.");
        distributeToSA1();

        logger.info("\t ---------- Converting ABS to SILO standard");
        convertToSilo();

        logger.info("\t ---------- Assigning quality to zones.");
        determineZoneQuality();

        logger.info("\t ---------- Adding vacant/unoccupied dwellings.");
        addVacantDwellings(year);

        logger.info("\t  ---------- Adding vacant jobs");
        addEmptyJobs(jobsToAdd, (int)nextID_JJ);

        logger.info("\t ---------- Saving to file (pp dd hh jj).");
        savePopulation();
        saveDwellings();
        saveJobs();

        long hhCount = nextID_HH - 1 - (npdCount - tmpNPDPopulation.size());

        logger.info("\t ---------- Final stats:");
        logger.info("\t" + (nextID_PP-1) + " people " + hhCount + " households " + (nextID_DD-1) + " dwellings " + (nextID_JJ-1) + " jobs.");
        logger.info("\t" + employedCount + " employed and " + unemployedCount + " unemployed.");
        logger.info("\t" + unassignedWorker + " unassigned workers.");
        logger.info("\t" + clearedDwellings + " cleared dwellings and moved " + seperatedMovedPeople + " people.");

        logger.info("\t" + npdPeopleCount + " people in " + npdCount + " non-private dwellings reduced to " + tmpNPDPopulation.size());
        closeFilesForSyntheticPopulation();
        logger.info("  Completed generation of synthetic population");

        // bit of verification
        someStats();

        // copy the output files over and save in timestamped dir
        createDateCopy(year);
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
        TableDataSet peoplePropertiesPerArea = SiloUtil.readCSVfile(baseDirectory + "input/perth_specific/peopleBySA1_" + year + ".csv");
        TableDataSet dwellingPropertiesPerArea = SiloUtil.readCSVfile(baseDirectory + "input/perth_specific/opdBySA1_" + year + ".csv");

        // SA4 area codes in the file
        int[] areaCodes = new int[] {49, 50, 51, 52};

        // for each area
        for(int area = 0; area < areaCodes.length; area++)
        {
            // store zones that belong to this SA4
            // hashmap of SA1 to SA1zone to later easily add more info
            HashMap<Integer, ZoneSA1> SA1zones = new HashMap<>();

            // find people properties
            for(int row = 1; row <= peoplePropertiesPerArea.getRowCount(); row++)
            {
                int SA4 = (int) peoplePropertiesPerArea.getValueAt(row, "SA4");
                int SA1 = (int) peoplePropertiesPerArea.getValueAt(row, "SA1");

                // if zone belongs to the same SA4 area code
                if(areaCodes[area] == SA4)
                {
                    // create a new zone
                    ZoneSA1 zone = new ZoneSA1();
                    zone.SA4 = SA4;
                    zone.SA1 = SA1;

                    // add the total count for this zonze
                    zone.setPeopleCount((int)(peoplePropertiesPerArea.getValueAt(row, "Count")));

                    // add gender counts for this zone
                    zone.setMales((int)(peoplePropertiesPerArea.getValueAt(row, "Male")));
                    zone.setFemales((int)(peoplePropertiesPerArea.getValueAt(row, "Female")));

                    // add totals for age groups
                    for(int ageGroup = 1; ageGroup <= ABS_AGE_GROUPS; ageGroup++)
                    {
                        zone.setAgeGroupByTB(ageGroup, (int)(peoplePropertiesPerArea.getValueAt(row, "age_"+ageGroup)));
                    }

                    // add totals for income groups
                    for(int incGroup = 1; incGroup <= ABS_INCOME_GROUPS; incGroup++)
                    {
                        zone.setIncomeGroupByABS(incGroup, (int)(peoplePropertiesPerArea.getValueAt(row, "income_"+incGroup)));
                    }

                    // add totals for income groups
                    for(int indGroup = 1; indGroup <= ABS_INDUSTRY_GROUPS; indGroup++)
                    {
                        zone.setIndustryGroupByABS(indGroup, (int)(peoplePropertiesPerArea.getValueAt(row, "industry_"+indGroup)));
                    }

                    // save the zone
                    SA1zones.put(zone.SA1, zone);
                }
            }

            // find dwelling properties
            for(int row = 1; row <= dwellingPropertiesPerArea.getRowCount(); row++)
            {
                int SA4 = (int) dwellingPropertiesPerArea.getValueAt(row, "SA4");

                // if zone belongs to the same SA4 area code
                if(areaCodes[area] == SA4)
                {
                    int SA1 = (int)(dwellingPropertiesPerArea.getValueAt(row, "SA1"));

                    // get the previously created zone
                    ZoneSA1 zone = SA1zones.get(SA1);

                    if(zone != null)
                    {
                        // add the total dwelling count in this zone
                        zone.setDwellingCount((int)(dwellingPropertiesPerArea.getValueAt(row, "Count")));

                        // add totals for dwelling structures
                        for(int strType = 1; strType <= ABS_DWELL_GROUPS; strType++)
                        {
                            zone.setStructureTotals(strType, (int)(dwellingPropertiesPerArea.getValueAt(row, "structure_"+strType)));
                        }

                        // add totals for dwelling bedrooms
                        for(int bedType = 0; bedType < ABS_BEDROOM_GROUPS; bedType++)
                        {
                            zone.setBedroomTotalsByABS(bedType, (int)(dwellingPropertiesPerArea.getValueAt(row, "bedroom_"+bedType)));
                        }

                        // add totals for dwelling rent
                        for(int rentCode = 1; rentCode <= ABS_RENT_GROUPS; rentCode++)
                        {
                            zone.setRentTotalsByABS(rentCode, (int)(dwellingPropertiesPerArea.getValueAt(row, "rent_"+rentCode)));
                        }

                        // add totals for dwelling mortgage
                        for(int mortgageCode = 1; mortgageCode <= ABS_MORTGAGE_GROUPS; mortgageCode++)
                        {
                            zone.setMortgageTotalsByABS(mortgageCode, (int)(dwellingPropertiesPerArea.getValueAt(row, "mortgage_"+mortgageCode)));
                        }

                        // add totals for dwelling vehicles
                        for(int vehType = 0; vehType < ABS_VEHICLE_GROUPS; vehType++)
                        {
                            zone.setVehicleTotalsByABS(vehType, (int)(dwellingPropertiesPerArea.getValueAt(row, "vehicles_"+vehType)));
                        }
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
            HashMap<String, Family> familyMap = new HashMap<>();

            // get ABS dwelling id from the DWELLING file
            String dwellingId = pumsDwellings.getStringValueAt(rowDd, "ABSHID Dwelling Record Identifier");

            // get the attributes of the dwelling from ABS and store them for later processing
            Dwelling dwelling = new Dwelling();
            dwelling.codeBedrooms = (int) pumsDwellings.getValueAt(rowDd, "BEDRD"); // convertBedrooms()
            dwelling.codeMortgage = (int) pumsDwellings.getValueAt(rowDd, "MRERD"); // convertMortgage()
            dwelling.codeRent = (int) pumsDwellings.getValueAt(rowDd, "RNTRD"); // convertRent()
            dwelling.codeType = (int) pumsDwellings.getValueAt(rowDd, "STRD"); // direct mapping
            dwelling.codeVehicles = (int) pumsDwellings.getValueAt(rowDd, "VEHRD"); // convertAutos()
            dwelling.SA4 = (int) pumsDwellings.getValueAt(rowDd, "AREAENUM");

            // ---------------------------------------------------------------------------------------------------------
            // for each row in the PERSON file
            for(int rowPp = 1; rowPp <= pumsPersons.getRowCount(); rowPp++)
            {
                // for each person get a dwelling ID
                String p_dwellingId = pumsPersons.getStringValueAt(rowPp, "ABSHID Dwelling Record Identifier");

                // find the people that live in this dwelling
                if(p_dwellingId.equals(dwellingId))
                {
                    // check if previously added family
                    Family family = familyMap.get(p_dwellingId);
                    if(family == null)
                    {
                        // add a new family to the map
                        family = new Family();
                        family.dwelling = dwelling;
                        familyMap.put(p_dwellingId, family);
                    }

                    // get the attributes of the person from ABS & create a new person
                    Person person = new Person();
                    person.codeAge = (int) pumsPersons.getValueAt(rowPp, "AGEP Age");
                    person.codeSex = (int) pumsPersons.getValueAt(rowPp, "SEXP Sex");
                    person.codeIncome = (int) pumsPersons.getValueAt(rowPp, "INCP Individual Income (weekly)");
                    person.codeOccupation = (int) pumsPersons.getValueAt(rowPp, "LFSP Labour Force Status");
                    person.codeRelationship = (int) pumsPersons.getValueAt(rowPp, "RLHP Relationship in Household");
                    person.codeIndustry = (int) pumsPersons.getValueAt(rowPp, "INDP Industry of Employment");

                    // add the person to the family
                    family.addPerson(person);

                    // (unnecessary) update the family in the map
                    familyMap.put(p_dwellingId, family);
                }
            }

            // get the newly formed family
            Family family = familyMap.get(dwellingId);

            // if no members, somethings is wrong
            if(family == null || family.size == 0)
            {
                // vacant dwelling?
                logger.error("Dwelling " + dwellingId + " was not found in the PERSON file.");
            }
            else
            {
                // fill in the other details about this household
                family.dwelling = dwelling;
                family.calculateStats();

                samplePopulation.add(family);
            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------------  Process ABS data
    int seperatedMovedPeople = 0;
    private void connectMarriedPeople()
    {

        int h1, h2;
        for(h1=0; h1<samplePopulation.size(); h1++)
        {
            Family household1 = samplePopulation.get(h1);

            // if there is a single yet married person
            if(household1.size == 1 && household1.countMarried == 1)
            {
                int hh1Interest = household1.SingleAndLookingFor();

                // find that person a family

                // add to existing family
                for(h2=0; h2<samplePopulation.size(); h2++)
                {
                    Family household2 = samplePopulation.get(h2);

                    // if there is an uneven number of married people living the dwelling/household
                    if(household2.size > 1 && h2 != h1
                            && household2.countMarried%2 != 0
                            && household1.dwelling.SA4 == household2.dwelling.SA4)
                    {
                        int hh2Interest = household2.SingleAndLookingFor();
                        if(hh1Interest != 0 && hh2Interest != 0 && hh1Interest != hh2Interest)
                        {
                            seperatedMovedPeople += household1.size;

                            household2.append(household1);
                            household1.emptyTheDwelling();
                            samplePopulation.set(h1, household1);
                            samplePopulation.set(h2, household2);

                            h2 = samplePopulation.size()+50;
                        }
                    }
                }

                // if didn't find a family, match singles
                if(h2 == samplePopulation.size())
                {
                    for(h2=0; h2<samplePopulation.size(); h2++)
                    {
                        Family household2 = samplePopulation.get(h2);
                        if(household2.size == 1 && household2.countMarried == 1)
                        {
                            int hh2Interest = household2.SingleAndLookingFor();
                            if(hh1Interest != 0 && hh2Interest != 0 && hh1Interest != hh2Interest)
                            {
                                seperatedMovedPeople += household1.size;

                                household2.append(household1);
                                household1.emptyTheDwelling();
                                samplePopulation.set(h1, household1);
                                samplePopulation.set(h2, household2);

                                h2 = samplePopulation.size()+50;
                            }
                        }
                    }
                }
            }
        }

        for(h1=0; h1<samplePopulation.size(); h1++)
        {
            Family household = samplePopulation.get(h1);

            // if there is a single yet married person
            if(household.countMarried%2 != 0)
            {
                int lookingFor = household.SingleAndLookingFor();
                for(int i=0; i<household.size; i++)
                {
                    Person person = household.getPerson(i);

                    // if person is married
                    if(person.isMarried())
                    {
                        if(person.codeSex != lookingFor)
                        {
                            samplePopulation.get(h1).people.get(i).codeRelationship = ABS_CODE_SINGLE;
                            samplePopulation.get(h1).calculateStats();
                            i = household.size;
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
            people +=populationToCount.get(i).size;
        }
        return people;
    }

    // -----------------------------------------------------------------------------------------------------------------  Create synthetic population
    private void generatePopulation()
    {
        // create 100% population
        for(int i=0; i<samplePopulation.size(); i++)
        {
            generateFromFamily(samplePopulation.get(i));
        }
    }

    /*   For each family (which represents 1%), replicate to create
     *   100% population.
     * */
    private void generateFromFamily(Family family)
    {
        try
        {
            family = family.clone();
            Family syntheticFamily = null;

            // replicate the same family & dwelling x times to reconstruct from the 1% data
            for (int count = 0; count < POPULATION_WEIGHT; count++)
            {
                // create a dwelling with the same properties
                Dwelling dwelling = family.dwelling.clone();

                // if empty dwelling (it was cleared out because of the gender rewire)
                if(family.size == 0)
                {
                    clearedDwellings++;
                }
                else
                {
                    boolean isNPD = false;

                    // if a non-private dwelling
                    if(dwelling.codeType == ABS_CODE_NONPRIVATE_DWELLING)
                    {
                        npdCount++;
                        isNPD = true;
                    }

                    // add a new dwelling
                    dwelling.ddID = nextID_DD++;
                    dwelling.hhID = nextID_HH++;

                    // create the new family
                    syntheticFamily = new Family();
                    syntheticFamily.dwelling = dwelling;

                    int peopleMarried = 0;

                    // save the people that live in this dwelling
                    for (int s = 0; s < family.size; s++)
                    {
                        // fetch the person record
                        Person person = family.getPerson(s).clone();

                        // assign new person id, household, dwelling and a job
                        person.ppID = nextID_PP++;
                        person.jjID = -1;
                        // person.ddID = syntheticFamily.dwelling.ddID;
                        // person.hhID = syntheticFamily.dwelling.hhID;

                        // update family details
                        syntheticFamily.addPerson(person);

                        // stats
                        if(person.isMarried()) peopleMarried++;
                        if(person.codeOccupation == ABS_CODE_EMPLOYED) employedCount++;
                        if(person.codeOccupation == ABS_CODE_UNEMPLOYED) unemployedCount++;
                    }

                    // store the family
                    if(!isNPD)
                    {
                        fullPopulation.add(syntheticFamily);
                    }
                    else
                    {
                        npdPeopleCount += syntheticFamily.size;
                        tmpNPDPopulation.add(syntheticFamily);
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
    // ----------------------------------------------------------------------------------------------------------------- Non-private dwellings
    private void cleanNonPrivDwellings(int year)
    {
        try
        {
            TableDataSet npdTable = SiloUtil.readCSVfile(baseDirectory + "input/perth_specific/npdBySA1_" + year + ".csv");
            int npdRow = 1;

            int NPDs = 512;
            int maxPerDwell = npdPeopleCount/NPDs+10;
            Random rand = new Random();

            while (tmpNPDPopulation.size() != NPDs)
            {
                int rI1 = rand.nextInt(tmpNPDPopulation.size());
                int rI2 = rand.nextInt(tmpNPDPopulation.size());

                Family fam1 = tmpNPDPopulation.get(rI1);
                Family fam2 = tmpNPDPopulation.get(rI2);

                if(rI1 != rI2 && fam1.size < maxPerDwell && fam2.size < maxPerDwell)
                {
                    Family family1 = fam1.clone();
                    Family family2 = fam2.clone();

                    tmpNPDPopulation.remove(fam1);
                    tmpNPDPopulation.remove(fam2);

                    family1.append(family2);
                    tmpNPDPopulation.add(family1);
                }
            }
            for(int i = 0; i < tmpNPDPopulation.size(); i++)
            {
                Family family = tmpNPDPopulation.get(i).clone();

                // search the file for an SA1 for the non-priv dwelling
                while(true)
                {
                    // get the npd count at a current SA1 row
                    int npdsInSA1 = (int) npdTable.getValueAt(npdRow, 2);

                    // if there are any NPDs in this SA1
                    if(npdsInSA1 > 0)
                    {
                        // set the SA1
                        family.dwelling.SA1 = (int) npdTable.getValueAt(npdRow, 1);
                        // update the table
                        npdTable.setValueAt(npdRow, 2, (npdsInSA1-1));
                        // found an SA1, end the search
                        break;
                    }
                    else
                    {
                        // iterate to the next row
                        npdRow++;
                    }

                    // if run out of rows
                    if(npdRow > npdTable.getRowCount())
                    {
                        break;
                    }
                }

                fullPopulation.add(family);
            }
        }
        catch(Exception ex)
        {
            logger.error(ex.getMessage());
        }


    }

    // ----------------------------------------------------------------------------------------------------------------- Distribute people to SA1s
    private void distributeToSA1()
    {
        // perfect fit
        distributeAttempt(0, false, true);
        // margin
        distributeAttempt(1, false, true);
        // noise margin
        distributeAttempt(0, true, true);
        // minimise the error
        distributeAttempt(0, false, false);

        //logger.info("Moving dwellings around.");
        //distributionTrade();
    }

    private void distributeAttempt(int margin, boolean useNoise, boolean perfectFit)
    {
        int unallocated = 0, allocated = 0;

        for(int i = 0; i < fullPopulation.size(); i++)
        {
            // fetch each family
            Family family = fullPopulation.get(i);

            // if not yet allocated family
            if(family.dwelling.SA1 == -1)
            {
                // make sure that family stats are up to date
                family.calculateStats();
                // get the SA4
                int SA4 = family.dwelling.SA4;

                // get the list of SA1 zones to the corresponding SA4 of this family
                ZoneSA1[] zones = zoneMap.get(SA4);

                // convert to array list and sort
                ArrayList<ZoneSA1> arrayList = new ArrayList<>(Arrays.asList(zones));
                Collections.sort(arrayList, Collections.reverseOrder());
                zones = arrayList.toArray(new ZoneSA1[0]);

                int bestZone = -1;
                int minMisMatchPoints = Integer.MAX_VALUE;

                for(int z = 0; z < zones.length; z++)
                {
                    ZoneSA1 zone = zones[z];

                    // by how many points the family (people) do not fit in the zone
                    int points1 = familyFitInZone(zone, family, margin, useNoise, perfectFit);

                    // by how many points the dwelling does not fit in the zone
                    int points2 = dwellingFitInZone(zone, family.dwelling, useNoise, perfectFit);

                    if(points1 <= 0 && points2 <= 0)
                    {
                        bestZone = z;
                        z = zones.length; // break out of the loop
                    }
                    else if(!perfectFit)
                    {
                        if(points1+points2 < minMisMatchPoints)
                        {
                            minMisMatchPoints = points1+points2;
                            bestZone = z;
                        }
                    }
                }

                if(bestZone == -1)
                {
                    unallocated++;
                }
                else
                {
                    allocated++;

                    // get the best zone
                    ZoneSA1 selectedZone =  zones[bestZone];

                    // assign the zone to the family
                    family.dwelling.SA1 = selectedZone.SA1;

                    // update the zone counts
                    selectedZone.update(family, -1);

                    // update the map
                    zones[bestZone] = selectedZone;
                    zoneMap.put(SA4, zones);
                }
            }
        }
        logger.info("allocated " + allocated + " more households and " + unallocated + " unallocated still remaining");
    }

    private void distributionTrade()
    {
        int moved = 0;

        // for each SA4
        for (ZoneSA1[] zones : zoneMap.values())
        {
            // for each SA1
            for(ZoneSA1 zone1 : zones)
            {
                // if overflow in zone 1
                while(zone1.getDwellingCount() < -2)
                {
                    if(zoneDistributionMove(zones, zone1))
                    {
                        moved++;
                    }
                    else
                    {
                        break;
                    }
                }
            }
        }
        logger.info("Moved " + moved + " dwellings.");
    }

    // returns if successfully moved
    private boolean zoneDistributionMove(ZoneSA1[] zones, ZoneSA1 zone1)
    {
        // find a family in this SA1
        Family family = findFamilyByZone(zone1.SA1);

        // if found
        if(family != null)
        {
            for(ZoneSA1 zone2 : zones)
            {
                // if not the same zone
                if (zone1.SA1 != zone2.SA1)
                {
                    int points1 = familyFitInZone(zone2, family,0, false, false);
                    int points2 = dwellingFitInZone(zone2, family.dwelling, false, false);

                    if(points1+points2 < 111)
                    {
                        family.dwelling.SA1 = zone2.SA1;
                        zone1.update(family, 1);
                        zone2.update(family, -1);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Family findFamilyByZone(int SA1)
    {
        // starting with lowest family sizes
        for(int f = (fullPopulation.size()-1); f >= 0; f--)
        {
            // if matching SA1
            if(fullPopulation.get(f).dwelling.SA1 == SA1)
            {
                // if not a npd
                if(fullPopulation.get(f).dwelling.codeType != ABS_CODE_NONPRIVATE_DWELLING)
                {
                    return fullPopulation.get(f);
                }
            }
        }
        return null;
    }

    private int familyFitInZone(ZoneSA1 zone, Family family, int margin, boolean useNoise, boolean perfectFit)
    {
        int points = 0;

        int marginTotal = margin + (useNoise ? zone.getTotalPopulationMargin() : 0); // if true ? false : true
        int marginSex = margin + (useNoise ? zone.getTotalMarginForCategory(zone.genderTotals) : 0);
        int marginAge = margin + (useNoise ? zone.getTotalMarginForCategory(zone.ageTotals) : 0);
        int marginInc = margin + (useNoise ? zone.getTotalMarginForCategory(zone.incomeTotals) : 0);
        int marginInd = margin + (useNoise ? zone.getTotalMarginForCategory(zone.industryTotals) : 0);

        // if not enough people left in the zone (considering the margin + data noise)
        if((zone.getTotalPopulation()+marginTotal) < family.size)
        {
            // if perfect fit: speed up by skipping the rest
            if(perfectFit) return Integer.MAX_VALUE;

            // how many misplaced people
            points += (family.size - zone.getTotalPopulation())*3;
        }

        // SEX fit
        // if not enough of each gender in the zone
        if((zone.getMales()+marginSex) < family.countMale)
        {
            // if perfect fit: speed up by skipping the rest
            if(perfectFit) return Integer.MAX_VALUE;

            // how many misplaced people
            points += (family.countMale - zone.getMales())*2.5;
        }
        if((zone.getFemales()+marginSex) < family.countFemale)
        {
            // if perfect fit: speed up by skipping the rest
            if(perfectFit) return Integer.MAX_VALUE;

            // how many misplaced people
            points += (family.countFemale - zone.getFemales())*2.5;
        }

        // AGE fit
        for(int a = 0; a < family.countAge.length ; a++)
        {
            // if there are people in this group
            if(family.countAge[a] > 0)
            {
                // if not enough people in this zone in this group (considering the margin + data noise)
                if((zone.getAgeGroupByIndex(a)+marginAge) < family.countAge[a])
                {
                    // if perfect fit: speed up by skipping the rest
                    if(perfectFit) return Integer.MAX_VALUE;

                    // how many misplaced people
                    points += (family.countAge[a] - zone.getAgeGroupByIndex(a))*2;
                }
            }
        }

        // INCOME fit
        for(int a = 0; a < family.countIncome.length ; a++)
        {
            // if there are people in this group
            if(family.countIncome[a] > 0)
            {
                // if not enough people in this zone in this group (considering the margin + data noise)
                if((zone.getIncomeGroupByIndex(a)+marginInc) < family.countIncome[a])
                {
                    // if perfect fit: speed up by skipping the rest
                    if(perfectFit) return Integer.MAX_VALUE;

                    // how many misplaced people
                    points += (family.countIncome[a] - zone.getIncomeGroupByIndex(a))*2;
                }
            }
        }

        // INDUSTRY fit
        for(int a = 0; a < family.countIndustry.length ; a++)
        {
            // if there are people in this group
            if(family.countIndustry[a] > 0)
            {
                // if not enough people in this zone in this group (considering the margin + data noise)
                if((zone.getIndustryGroupByIndex(a)+marginInd) < family.countIndustry[a])
                {
                    // if perfect fit: speed up by skipping the rest
                    if(perfectFit) return Integer.MAX_VALUE;

                    // how many misplaced people
                    points += (family.countIndustry[a] - zone.getIndustryGroupByIndex(a));
                }
            }
        }

        return points;
    }

    private int dwellingFitInZone(ZoneSA1 zone, Dwelling dwelling, boolean useNoise, boolean perfectFit)
    {
        int points = 0;
        int marginCount = (useNoise ? zone.getTotalDwellingMargin() : 0); // if true ? false : true
        int marginStr = (useNoise ? zone.getTotalDwellingMarginForCategory(zone.structureTotals) : 0);
        int marginBed = (useNoise ? zone.getTotalDwellingMarginForCategory(zone.bedroomTotals) : 0);
        int marginRnt = (useNoise ? zone.getTotalDwellingMarginForCategory(zone.rentTotals) : 0);
        int marginMrg = (useNoise ? zone.getTotalDwellingMarginForCategory(zone.mortgageTotals) : 0);
        int marginVeh = (useNoise ? zone.getTotalDwellingMarginForCategory(zone.vehicleTotals) : 0);

        // if the overall count is not ok
        if((zone.getDwellingCount()+marginCount) <= 0)
        {
            // if perfect fit: speed up by skipping the rest
            if(perfectFit) return Integer.MAX_VALUE;

            points += (1 - zone.getDwellingCount())*3;
        }

        // if structure types are not ok
        if((zone.getStructureTotals(dwelling.codeType)+marginStr) <= 0)
        {
            // if perfect fit: speed up by skipping the rest
            if(perfectFit) return Integer.MAX_VALUE;

            points += 1 - zone.getStructureTotals(dwelling.codeType)*2;
        }

        // if bedroom codes are not ok
        if((zone.getBedroomTotalsByABS(dwelling.codeBedrooms)+marginBed) <= 0)
        {
            // if perfect fit: speed up by skipping the rest
            if(perfectFit) return Integer.MAX_VALUE;

            points += 1 - zone.getBedroomTotalsByABS(dwelling.codeBedrooms);
        }

        // if rent codes are not ok
        if((zone.getRentTotalsByABS(dwelling.codeRent)+marginRnt) <= 0)
        {
            // if perfect fit: speed up by skipping the rest
            if(perfectFit) return Integer.MAX_VALUE;

            points += 1 - zone.getRentTotalsByABS(dwelling.codeRent);
        }

        // if mortgage code are not ok
        if((zone.getMortgageTotalsByABS(dwelling.codeMortgage)+marginMrg) <= 0)
        {
            // if perfect fit: speed up by skipping the rest
            if(perfectFit) return Integer.MAX_VALUE;

            points += 1 - zone.getMortgageTotalsByABS(dwelling.codeMortgage);
        }

        // if vehicles are not ok
        if((zone.getVehicleTotalsByABS(dwelling.codeVehicles)+marginVeh) <= 0)
        {
            // if perfect fit: speed up by skipping the rest
            if(perfectFit) return Integer.MAX_VALUE;

            points += 1 - zone.getVehicleTotalsByABS(dwelling.codeVehicles);
        }

        return points*5;
    }

    // -----------------------------------------------------------------------------------------------------------------  Process ABS data
    private void convertToSilo()
    {
        for(int i = 0; i < fullPopulation.size(); i++)
        {
            Family family = fullPopulation.get(i);
            Dwelling dwelling = family.dwelling;

            // convert dwelling properties
            dwelling.ddBedrooms = convertBedrooms(dwelling.codeBedrooms);
            dwelling.ddType = dwelling.codeType;
            dwelling.ddQuality = translateDwellingQuality(dwelling);
            dwelling.ddMortgage = convertMortgage(dwelling.codeMortgage);
            dwelling.ddRent = convertRent(dwelling.codeRent);
            dwelling.ddPrice = getDwellingPrice(dwelling.ddRent, dwelling.ddMortgage);
            dwelling.ddVehicles = convertAutos(dwelling.codeVehicles);

            // save the dwelling (just in case)
            family.dwelling = dwelling;

            // for each person in the family
            for(int p = 0; p < family.size; p++)
            {
                // get the person
                Person person = family.getPerson(p);

                // convert attributes
                person.ppAge = convertAge(person.codeAge);
                person.ppSex = person.codeSex;
                person.ppRace = 0;
                person.ppIncome = convertIncome(person.codeIncome);
                person.ppOccupation = translateOccupation(person.codeOccupation, person.ppAge);
                person.ppRelationship = translateRelationship(person.codeRelationship);
                person.ppIndustry = translateIndustry(person.codeIndustry, person.ppOccupation);

                // assign a job
                person.jjID = jobCollection.assignWorker(person.ppID, person.ppIndustry);

                // save the person (just in case)
                family.setPerson(p, person);
            }

            // add the dwelling for later use in vacant & quality processing
            tmpStoreDwelling(dwelling);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------  Zone Quality
    // needed for unocc priv dwellings!
    private void determineZoneQuality()
    {
        // for each saved dwelling
        for(int i = 0; i<dwellingList.size(); i++)
        {
            int SA1 = dwellingList.get(i).SA1;

            // if allocated dwelling
            if(SA1 > 0)
            {
                // fetch zone quality for SA1 in which this dwelling is
                ZoneQuality zoneQuality = qualityMapSA1.get(dwellingList.get(i).SA1);

                // if nothing yet, create new zone quality
                if(zoneQuality == null)
                {
                    zoneQuality = new ZoneQuality(SA1);
                }

                // add the price (for later calculation)
                zoneQuality.addStructurePrice(dwellingList.get(i).ddType, dwellingList.get(i).ddPrice);

                // add quality if above 1
                if(dwellingList.get(i).ddQuality > 1)
                {
                    zoneQuality.addStructureQuality(dwellingList.get(i).ddType, dwellingList.get(i).ddQuality);
                }

                // save the zone quality (overwrite)
                qualityMapSA1.put(SA1, zoneQuality);
            }
        }

        // calculate the average prices for each SA1
        for (ZoneQuality zoneQuality : qualityMapSA1.values())
        {
            zoneQuality.calculateAverages();
        }

        for(int i = 0; i<dwellingList.size(); i++)
        {
            Dwelling dwelling = dwellingList.get(i);
            if(dwelling.ddQuality == 1)
            {
                dwelling.ddQuality = Math.max(1, (int) Math.round(qualityMapSA1.get(dwelling.SA1).getAverageQualityForStructure(dwelling.ddType)));
            }
        }
    }
    // -----------------------------------------------------------------------------------------------------------------  Vacant Dwellings
    private void tmpStoreDwelling(Dwelling dwelling)
    {
        // store the to be saved later
        dwellingList.add(dwelling);

        // store it under the zone to be used for vacant dwellings
        ArrayList<Dwelling> dwls = dwellingsPerZoneMap.get(dwelling.SA1);
        if(dwls == null)
        {
            dwls = new ArrayList<>();
        }
        dwls.add(dwelling);
        dwellingsPerZoneMap.put(dwelling.SA1, dwls);
    }

    /*  Add vacant dwellings based on the given list. The list contains
        the number of empty dwellings of each of type in each SA1 zone.
        Hence copy properties of the same type of a dwelling as the
        empty one to create a vacant dwelling.
     */
    private void addVacantDwellings(int year)
    {
        TableDataSet dwellUnoccPerArea = SiloUtil.readCSVfile(baseDirectory + "input/perth_specific/dwellingUnoccupiedBySA1_" + year + ".csv");

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
                int ddCodeType = Integer.parseInt(dwellUnoccPerArea.getColumnLabel(c));

                // get how many of dwellings of that type there are
                int count = (int)dwellUnoccPerArea.getValueAt(r, c);
                if(count > 0)
                {
                    // find dwellings in the same zone
                    ArrayList<Dwelling> dwellings = dwellingsPerZoneMap.get(zone);

                    // find dwellings of the same type
                    ArrayList<Dwelling> dwellingsSameType = findDwellingsOfSameType(dwellings, ddCodeType);

                    // if no dwellings found, try the neighbourhood
                    if(dwellingsSameType.size() < 1)
                    {
                        int counter = 1;

                        while(dwellingsSameType.size() < 1 && counter <= 99)
                        {
                            dwellings = dwellingsPerZoneMap.get(zone+counter);
                            dwellingsSameType = findDwellingsOfSameType(dwellings, ddCodeType);

                            if(dwellingsSameType.size() < 1)
                            {
                                dwellings = dwellingsPerZoneMap.get(zone-counter);
                                dwellingsSameType = findDwellingsOfSameType(dwellings, ddCodeType);
                            }
                            counter++;
                        }
                        if(dwellingsSameType.size() == 0) logger.warn("Failed to find a dwelling for " + ddCodeType + " in zone " + zone);
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
                            vaccDwelling.copyFromDwelling(oocDwelling);
                            vaccDwelling.ddID = nextID_DD++;
                            vaccDwelling.hhID = -1;
                            vaccDwelling.SA1 = zone;
                            vaccDwelling.ddType = ddCodeType; // just to make sure
                            vaccDwelling.ddPrice = qualityMapSA1.get(zone).overallAveragePrice;

                            // add the dwelling to be later saved to the file
                            tmpStoreDwelling(vaccDwelling); //dwellingList.add(vaccDwelling);
                        }
                    }
                    else
                    {
                        logger.error("Failed to add dwelling type " + ddCodeType + " in zone " + zone);
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
                if(dwelling.ddType == ddType)
                {
                    dwellingsSameType.add(dwelling);
                }
            }
        }
        return dwellingsSameType;
    }

    // -----------------------------------------------------------------------------------------------------------------  Save to file
    private void saveJobs()
    {
        // heading for jj SILO
        pwjj.println("id,zone,personId,type");

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

    // -----------------------------------------------------------------------------------------------------------------    Save pp hh
    /*
        ABS FILE
            id: unique person ID (same as SILO)
            hhid: unique household ID (same as SILO)
            age: AGEP Age [1 38]                                    see: convertAge
            gender: SEXP Sex [1, 2] (same as SILO)
            relationship: RLHP Relationship in Household [1, 11]
            race: 0 (same as SILO)
            occupation: LFSP Labour Force Status [1 6]
            workplace: unique job ID (same as SILO)
            income: INCP Individual Income (weekly) [1, 15]
            code_industry: INDP Industry of Employment [1, 23]
            pp_industry:
    */
    private void savePopulation()
    {
        // heading for pp & hh SILO
        pwpp.println("id,hhid,age,gender,relationship,race,occupation,workplace,income");
        pwhh.println("id,dwelling,zone,hhSize,autos");
        // heading for pp ABS
        pwppabs.println("id,hhid,age,gender,relationship,race,occupation,workplace,income,code_industry,pp_industry,SA1");

        for(int i = 0; i < fullPopulation.size(); i++)
        {
            Family family = fullPopulation.get(i);
            Dwelling dwelling = family.dwelling;

            // for each person in the family
            for(int p = 0; p < family.size; p++)
            {
                // get the person
                Person person = family.getPerson(p);

                // save the record
                pwpp.println(person.ppID + "," + dwelling.hhID + "," + person.ppAge + "," + person.ppSex + ","
                        + person.ppRelationship + "," + person.ppRace + "," +  person.ppOccupation + ","
                        + person.jjID + "," + person.ppIncome);

                // abs version
                pwppabs.println(person.ppID + "," + dwelling.hhID + "," + person.codeAge + "," + person.codeSex + ","
                        + person.codeRelationship + "," + person.ppRace + "," +  person.codeOccupation + ","
                        + person.jjID + "," + person.codeIncome + "," + person.codeIndustry + "," + person.ppIndustry
                        + "," + dwelling.SA1);
            }

            // save the household
            pwhh.println(dwelling.hhID + "," + dwelling.ddID + "," + dwelling.SA1 + "," + family.size + "," + dwelling.ddVehicles);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------    dd
    public void saveDwellings()
    {
        // heading for dd SILO
        pwdd.println("id,zone,type,hhID,bedrooms,quality,monthlyCost");
        // heading for dd ABS
        pwddabs.println("id,zone,type,hhID,bedrooms,quality,monthlyCost,rent,mortgage,autos");

        for(int i = 0; i < dwellingList.size(); i++)
        {
            Dwelling dwelling = dwellingList.get(i);

            // save the dwelling
            pwdd.println(dwelling.ddID + "," + dwelling.SA1 + "," + dwelling.ddType + "," +
                    dwelling.hhID + "," + dwelling.ddBedrooms + "," + dwelling.ddQuality + "," + dwelling.ddPrice);

            pwddabs.println(dwelling.ddID + "," + dwelling.SA1 + "," + dwelling.codeType + "," +
                    dwelling.hhID + "," + dwelling.codeBedrooms + "," + dwelling.ddQuality + "," + dwelling.ddPrice
                    + "," + dwelling.codeRent + ", " + dwelling.codeMortgage + "," + dwelling.codeVehicles);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    public int ageCodeABS2BinTB(int ageCode)
    {
        //ageCode = ageCode - 1;
        if(ageCode >= 0 && ageCode <= 4) return 1;
        else if(ageCode >= 5 && ageCode <= 9) return 2;
        else if(ageCode >= 10 && ageCode <= 14) return 3;
        else if(ageCode >= 15 && ageCode <= 19) return 4;
        else if(ageCode >= 20 && ageCode <= 24) return 5;
        else return ageCode - 19;
    }

    // -----------------------------------------------------------------------------------------------------------------  ABS to SILO translate
    /*  select actual number of from indicators provided in ABS "BEDD"
            0: None (includes bedsitters)       0
            1: 1 bedroom                        1
            2: 2 bedrooms                       2
            3: 3 bedrooms                       3
            4: 4 bedrooms                       4
            5: 5 or more bedrooms               [5 7]
            6: Not stated                       0
            7: Not applicable                   0
    */
    private int convertBedrooms(int bedroomCode)
    {
        // if [0, 4] direct import the value
        if (bedroomCode <= 4)
        {
            return bedroomCode;
        }
        // if 5 or more then random [5, 7] 7 = max number of bedrooms in Perth
        else if (bedroomCode == 5)
        {
            return randomInteger(5, 7);
        }
        // if not stated then random [0, 7]
        else if(bedroomCode == 6)
        {
            return 0; // randomInteger(0, 7);
        }

        return 0;
    }

    /*  Convert mortgage code to a payment. MRERD Mortgage Repayments (monthly) ranges
             1	Nil repayments
             2	$1-$149
             3	$150-$299
             4	$300-$449
             5	$450-$599
             6	$600-$799
             7	$800-$999
             8	$1,000-$1,199
             9	$1,200-$1,399
             10	$1,400-$1,599
             11	$1,600-$1,799
             12	$1,800-$1,999
             13	$2,000-$2,199
             14	$2,200-$2,399
             15	$2,400-$2,599
             16	$2,600-$2,999
             17	$3,000-$3,999
             18	$4,000-$4,999
             19	$5,000 and over
             20	Not stated
             21	Not applicable
    */
    private int convertMortgage(int mortgageCode)
    {
        int min, max;
        switch (mortgageCode)
        {
            default: case 1: case 20: case 21: min = 0; max = 0; break;
            case 2:  min = 1; max = 149; break;
            case 3:  min = 150; max = 299; break;
            case 4:  min = 300; max = 449; break;
            case 5:  min = 450; max = 599; break;
            case 6:  min = 600; max = 799; break;
            case 7:  min = 800; max = 999; break;
            case 8:  min = 1000; max = 1199; break;
            case 9:  min = 1200; max = 1399; break;
            case 10: min = 1400; max = 1599; break;
            case 11: min = 1600; max = 1799; break;
            case 12: min = 1800; max = 1999; break;
            case 13: min = 2000; max = 2199; break;
            case 14: min = 2200; max = 2399; break;
            case 15: min = 2400; max = 2599; break;
            case 16: min = 2600; max = 2999; break;
            case 17: min = 3000; max = 3999; break;
            case 18: min = 4000; max = 4999; break;
            case 19: min = 5000; max = 15000; break;
        }
        return randomInteger(min, max);
    }

    /*  Convert rent code to monthly payment. RNTRD Rent (weekly) Ranges
            1	Nil payments
            2	$1-$74
            3	$75-$99
            4	$100-$124
            5	$125-$149
            6	$150-$174
            7	$175-$199
            8	$200-$224
            9	$225-$249
            10	$250-$274
            11	$275-$299
            12	$300-$324
            13	$325-$349
            14	$350-$374
            15	$375-$399
            16	$400-$424
            17	$425-$449
            18	$450-$549
            19	$550-$649
            20	$650 and over
            21	Not stated
            22	Not applicable
    */
    private int convertRent (int rentCode)
    {
        int min, max;
        switch (rentCode)
        {
            default: case 1: case 21: case 22: min = 0; max = 0; break;
            case 2:  min = 1; max = 74; break;
            case 3:  min = 75; max = 99; break;
            case 4:  min = 100; max = 124; break;
            case 5:  min = 125; max = 149; break;
            case 6:  min = 150; max = 174; break;
            case 7:  min = 175; max = 199; break;
            case 8:  min = 200; max = 224; break;
            case 9:  min = 225; max = 249; break;
            case 10: min = 250; max = 274; break;
            case 11: min = 275; max = 299; break;
            case 12: min = 300; max = 324; break;
            case 13: min = 325; max = 349; break;
            case 14: min = 350; max = 374; break;
            case 15: min = 375; max = 399; break;
            case 16: min = 400; max = 424; break;
            case 17: min = 425; max = 449; break;
            case 18: min = 450; max = 549; break;
            case 19: min = 550; max = 649; break;
            case 20: min = 650; max = 3000; break;

        }
        // generate random within the bounds and convert to monthly figure
        return randomInteger(min, max)*52/12;
    }

    /*  Based on the rent and mortgage codes determine the monthly cost for the dwelling.
        This is because sometimes rent = mortgage = monthly cost.
        Or because sometimes one is paid but not the other.
    */
    private int getDwellingPrice(int rent, int mortgage)
    {
        int monthlyPrice;

        // if both rent and mortgage are indicated
        if (rent > 0 && mortgage > 0)
        {
            // probably they are the same so take average of the two
            monthlyPrice = (rent + mortgage) / 2;
        }
        // if no rent but only mortgage
        else if (rent <= 0 && mortgage > 0)
        {
            // monthly cost is the monthly mortgage
            monthlyPrice = mortgage;
        }
        // if rent but no mortgage
        else if (rent > 0 && mortgage <= 0)
        {
            // monthly cost is the monthly rent
            monthlyPrice = rent;
        }
        // if both of them are 0
        else
        {
            // probably own the place and are not paying
            monthlyPrice = 0;
        }
        return monthlyPrice;
    }

    /*  Select actual number of autos from indicators provided in ABS microdata
            0: None                                                     0
            1: 1 motor vehicle                                          1
            2: 2 motor vehicles                                         2
            3: 3 motor vehicles                                         3
            4: 4 or more motor vehicles; set 4 cars as maximum          4 or more
            5: Not stated                                               0
            6: Not applicable                                           0
    */
    private int convertAutos(int autoCode)
    {
        // direct import
        if (autoCode <= 4)
        {
            return autoCode;
        }
        // if not stated, do a random integer [0, 4]
        else if (autoCode == 5)
        {
            return 0;//randomInteger(0, 4);
        }
        // else not applicable = 0
        return 0;
    }


    // -----------------------------------------------------------------------------------------------------------------  Dwelling Quality
    private int translateDwellingQuality(Dwelling dwelling)
    {
        int dwellingBedrooms = dwelling.codeBedrooms > 5 ? 5 : dwelling.codeBedrooms;

        // big four ISP. Graph, Syntax, Logic, Alloy - TRansition?
        for(int row = 1; row <= qualityMatrix.getRowCount(); row++)
        {
            if(dwelling.codeType == (int)qualityMatrix.getValueAt(row, "Type"))
            {
                if(dwellingBedrooms == (int)qualityMatrix.getValueAt(row, "Bedrooms"))
                {
                    // up to including weekly rent bin OR up to including monthly mortgage bin
                    if((dwelling.codeRent <= (int)qualityMatrix.getValueAt(row, "Rent Bin"))
                        || (dwelling.codeMortgage <= (int)qualityMatrix.getValueAt(row, "Mortgage Bin")))
                    {
                        return (int) qualityMatrix.getValueAt(row, "Quality") + 1;
                    }
                }
            }
        }
        return 1;
    }


    /*  Select actual age from bins provided in the microdata
            Ages: 1-25: 0-24 years singly		1..5
                    26: 25-29 years				6
                    27: 30–34 years				7
                    28: 35–39 years				8
                    29: 40–44 years				9
                    30: 45–49 years				10
                    31: 50–54 years				11
                    32: 55–59 years				12
                    33: 60–64 years				13
                    34: 65–69 years				14
                    35: 70–74 years				15
                    36: 75–79 years				16
                    37: 80–84 years				17
                    38: 85 years and over		18
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

    /*  Using LFSP Labour Force Status [1 6] and CONVERTED age
            1 - Employed                    1
            2 - Unemployed                  2
            3 - Not in labor force          age[0 5]= 0 age[6 21]= 3 age[22 64]= 2 age[65 n]= 4
            4 - Not stated                  age[0 5]= 0 age[6 21]= 3 age[22 64]= 2 age[65 n]= 4
            5 - Not applicable              age[0 5]= 0 age[6 21]= 3 age[22 64]= 2 age[65 n]= 4
            6 - Overseas Visitor            2
    */
    private int translateOccupation(int pumsOccupation, int personsAge)
    {
        // corresponding occupation integers
        int TODDLER = 0;
        int EMPLOYED = 1; // Employed, at work
        int UNEMPLOYED = 2;
        int STUDENT = 3;
        int RETIRED = 4; // Not in labor force

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
                else if(personsAge < 65)
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
            case 1: return "married";
            case 3: case 4: return "child";
            default: return "single";
        }
    }

    // -----------------------------------------------------------------------------------------------------------------  Output files
    private void openFilesToWriteSyntheticPopulation(int year)
    {
        // SILO
        String filehh = baseDirectory + "/microData/hh_" + year + ".csv";
        String filepp = baseDirectory + "/microData/pp_" + year + ".csv";
        String filedd = baseDirectory + "/microData/dd_" + year + ".csv";
        String filejj = baseDirectory + "/microData/jj_" + year + ".csv";
        pwhh = SiloUtil.openFileForSequentialWriting(filehh, false);
        pwpp = SiloUtil.openFileForSequentialWriting(filepp, false);
        pwdd = SiloUtil.openFileForSequentialWriting(filedd, false);
        pwjj = SiloUtil.openFileForSequentialWriting(filejj, false);

        // ABS
        String fileppABS = baseDirectory + "/microData/abs_pp_" + year + ".csv";
        String fileddABS = baseDirectory + "/microData/abs_dd_" + year + ".csv";
        pwppabs = SiloUtil.openFileForSequentialWriting(fileppABS, false);
        pwddabs = SiloUtil.openFileForSequentialWriting(fileddABS, false);
    }

    private void closeFilesForSyntheticPopulation()
    {
        pwhh.close();
        pwpp.close();
        pwdd.close();
        pwjj.close();

        pwppabs.close();
        pwddabs.close();
    }

    // -----------------------------------------------------------------------------------------------------------------  Stats
    private void someStats()
    {
        int price0s = 0;
        int priceAvg = 0;

        for(int i = 0; i < fullPopulation.size(); i++)
        {
            Family family = fullPopulation.get(i);
            Dwelling dwelling = family.dwelling;


            if(dwelling.ddPrice == 0)
            {
                price0s++;
            }
            else
            {
                priceAvg += dwelling.ddPrice;
            }

            // for each person in the family
            for(int p = 0; p < family.size; p++)
            {
                Person person = family.getPerson(p);
            }
        }

        priceAvg = priceAvg/(fullPopulation.size() - price0s);

        logger.info("Average monthly price: " + priceAvg);
        logger.info("Dwellings with monthly cost zero: " + price0s + ", " + (double)((double)price0s*(double)100/(double)fullPopulation.size()) + "%.");

    }

    // -----------------------------------------------------------------------------------------------------------------  Versioned Output
    private void createDateCopy(int year)
    {
        // date format
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmm");
        String timestamp = dateFormat.format(new Date());

        String sourceDir = baseDirectory + "/microData/";
        String outputDir = baseDirectory + "/microData/microData_" + timestamp + "/";

        if(new File(outputDir).mkdirs())
        {
            try
            {
                copyFile(new File(sourceDir + "pp_" + year + ".csv"), new File(outputDir + "pp_" + year + ".csv"));
                copyFile(new File(sourceDir + "dd_" + year + ".csv"), new File(outputDir + "dd_" + year + ".csv"));
                copyFile(new File(sourceDir + "hh_" + year + ".csv"), new File(outputDir + "hh_" + year + ".csv"));
                copyFile(new File(sourceDir + "jj_" + year + ".csv"), new File(outputDir + "jj_" + year + ".csv"));
                copyFile(new File(sourceDir + "abs_pp_" + year + ".csv"), new File(outputDir + "abs_pp_" + year + ".csv"));
                copyFile(new File(sourceDir + "abs_dd_" + year + ".csv"), new File(outputDir + "abs_dd_" + year + ".csv"));

                logger.info("Timestamp: " + timestamp);
            }
            catch (Exception ex)
            {
                logger.error("Failed to copy files. " + ex.getMessage());
            }
        }
        else
        {
            logger.error("Failed to copy files. Could not create output directory");
        }

    }

    private static void copyFile(File source, File dest) throws IOException
    {
        InputStream is = null;
        OutputStream os = null;
        try
        {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0)
            {
                os.write(buffer, 0, length);
            }
        }
        finally
        {
            is.close();
            os.close();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------  Helper Methods
    /*  Generate a random integer bound by the min and max
        [min, max] e.g. (1,50) => [1, 50] so it can be 1, 2, .., 50
    */
    private int randomInteger(int min, int max)
    {
        if(min == max)
        {
            return min;
        }
        return (int) ((Math.random() * (double) max) + (double) min);
    }

    // -----------------------------------------------------------------------------------------------------------------  Weighted Random
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

    // -----------------------------------------------------------------------------------------------------------------  Dwelling
    private class Dwelling implements Cloneable
    {
        long ddID;
        long hhID;

        // zones
        int SA1 = -1;
        int SA4;

        // abs
        int codeBedrooms;
        int codeMortgage;
        int codeRent;
        int codeType;
        int codeVehicles;

        // silo
        int ddBedrooms;
        int ddMortgage;
        int ddRent;
        int ddType;
        int ddVehicles;
        int ddQuality;
        int ddPrice;


        public Dwelling() { }

        public void addABSCars(int vehiclesABS)
        {
            // 1: 1 motor vehicle
            // 2: 2 motor vehicles
            // 3: 3 motor vehicles
            // 4: 4 or more motor vehicles; set 4 cars as maximum
            // 5: Not stated
            // 6: Not applicable

            // if currently 5 or 6, then it doesnt matter, overwrite
            if(codeVehicles > 4)
            {
                codeVehicles = vehiclesABS;
            }
            else
            {
                // if the new vehicles are 5 or 6, it doesnt matter, keep it the same
                if(vehiclesABS < 5)
                {
                    // otherwise add more vehicles to the household
                    codeVehicles += vehiclesABS;

                    // if exceeds 4, then keep it at 4
                    if(codeVehicles > 4)
                    {
                        codeVehicles = 4;
                    }
                }
            }
        }

        public void copyFromDwelling(Dwelling dwelling)
        {
            // abs
            codeBedrooms = dwelling.codeBedrooms;
            codeMortgage = dwelling.codeMortgage;
            codeRent = dwelling.codeRent;
            codeType = dwelling.codeType;
            codeVehicles = dwelling.codeVehicles;

            // silo
            ddBedrooms = dwelling.ddBedrooms;
            ddMortgage = dwelling.ddMortgage;
            ddRent = dwelling.ddRent;
            ddType = dwelling.ddType;
            ddVehicles = dwelling.ddVehicles;
            ddQuality = dwelling.ddQuality;
            ddPrice = dwelling.ddPrice;
        }

        // -----------------------------------------------------------------    Clone interface
        public Dwelling clone()throws CloneNotSupportedException{ return (Dwelling) super.clone(); }
    }

    // -----------------------------------------------------------------------------------------------------------------  Family / Household
    private class Person implements Cloneable
    {
        public long ppID;
        public long jjID;

        // abs
        public int codeAge;
        public int codeSex;
        public int codeIncome;
        public int codeOccupation;
        public int codeRelationship;
        public int codeIndustry;

        // silo
        public int ppAge;
        public int ppSex;
        public int ppIncome;
        public int ppOccupation;
        public int ppIndustry;
        public int ppRace;
        public String ppRelationship;

        public Person() { }
        // -----------------------------------------------------------------    Person methods
        public boolean isMarried()
        {
            return (codeRelationship == ABS_CODE_MARRIED);
        }

        // -----------------------------------------------------------------    Clone interface
        public Person clone()throws CloneNotSupportedException{ return (Person) super.clone(); }
    }

    // -----------------------------------------------------------------------------------------------------------------  Family / Household
    private class Family implements Cloneable, Comparable<Family>
    {
        public int size;
        public ArrayList<Person> people;
        public Dwelling dwelling;

        // stats
        public int countMale;
        public int countFemale;
        public int countMarried;
        public int countMarriedM;
        public int countMarriedF;
        public int[] countAge;
        public int[] countIncome;
        public int[] countIndustry;

        public Family()
        {
            size = 0;
            people = new ArrayList<>();
        }
        // -----------------------------------------------------------------    Stats for population distribution
        public void calculateStats()
        {
            countMale = 0;
            countFemale = 0;
            countMarried = 0;
            countAge = new int[ABS_AGE_GROUPS];
            countIncome = new int[ABS_INCOME_GROUPS];
            countIndustry = new int[ABS_INDUSTRY_GROUPS];

            for(int i = 0; i < size; i++)
            {
                Person person = getPerson(i);

                // gender
                if(person.codeSex == ABS_CODE_MALE)
                {
                    countMale++;
                }
                else
                {
                    countFemale++;
                }

                // married
                if(person.isMarried())
                {
                    countMarried++;
                }

                // age groups
                countAge[ageCodeABS2BinTB(person.codeAge)-1] += 1;

                // income groups
                countIncome[person.codeIncome-1] += 1;

                // industry groups
                countIndustry[person.codeIndustry-1] += 1;
            }
        }

        // -----------------------------------------------------------------    Person methods
        public void addPerson(Person person)
        {
            people.add(person);
            size++;
        }

        public Person getPerson(int index) { return people.get(index); }

        public void setPerson(int index, Person person) { people.set(index, person); }

        // -----------------------------------------------------------------    Family methods
        public void append(Family family)
        {
            for (int i = 0; i < family.size; i++)
            {
                addPerson(family.getPerson(i));
            }

            // transfer the cars
            if(dwelling != null && family.dwelling != null)
            {
                dwelling.addABSCars(family.dwelling.codeVehicles);
            }

            calculateStats();
        }

        public void emptyTheDwelling()
        {
            people = null;
            size = 0;
            countMale = 0;
            countFemale = 0;
            countMarried = 0;
            countMarriedM = 0;
            countMarriedF = 0;
        }

        // -----------------------------------------------------------------    Fixing marriage
        public int SingleAndLookingFor()
        {
            countMarriedM = 0;
            countMarriedF = 0;

            for(int i = 0; i < size; i++)
            {
                Person person = getPerson(i);

                // if the person is married
                if(person.isMarried())
                {
                    if(person.codeSex == ABS_CODE_MALE)
                    {
                        countMarriedM++;
                    }
                    else
                    {
                        countMarriedF++;
                    }
                }
            }

            // if more males than females, then looking for a female
            if(countMarriedM > countMarriedF)
            {
                return ABS_CODE_FEMALE;
            }
            // if more females than males, then looking for a male
            else if(countMarriedF > countMarriedM)
            {
                return  ABS_CODE_MALE;
            }

            return 0;
        }

        // -----------------------------------------------------------------    Clone interface
        @Override
        public Family clone()throws CloneNotSupportedException{ return (Family) super.clone(); }

        // -----------------------------------------------------------------    Comparable interface
        @Override
        public int compareTo(Family o)
        {
            Integer s1 = size;
            Integer s2 = o.size;
            return s1.compareTo(s2);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------  Population Distribution
    private class ZoneSA1 implements Comparable<ZoneSA1>
    {
        public int SA1;
        public int SA4;

        // people info
        public int peopleCount;
        public int[] genderTotals;
        public int[] ageTotals;
        public int[] incomeTotals;
        public int[] industryTotals;

        // object constants
        private int posM = 0; // male index in the gender arrays
        private int posF = 1; // female index in the gender arrays

        // dwelling info
        public int dwellingCount;
        public int[] structureTotals;
        public int[] bedroomTotals;
        public int[] rentTotals;
        public int[] mortgageTotals;
        public int[] vehicleTotals;

        // constructor
        public ZoneSA1()
        {
            genderTotals = new int[2];
            ageTotals = new int[ABS_AGE_GROUPS];
            incomeTotals = new int[ABS_INCOME_GROUPS];
            industryTotals = new int[ABS_INDUSTRY_GROUPS];

            structureTotals = new int[ABS_DWELL_GROUPS];
            bedroomTotals = new int[ABS_BEDROOM_GROUPS];
            rentTotals = new int[ABS_RENT_GROUPS];
            mortgageTotals = new int[ABS_MORTGAGE_GROUPS];
            vehicleTotals = new int[ABS_VEHICLE_GROUPS];
        }

        public void update(Family family, int sing)
        {
            if(sing > 0)
                sing = 1;
            else if(sing < 0)
                sing = -1;
            else
            {
                sing = 1;
                logger.error("sign needs to be -1 (decrement) or 1 (increment) zone population");
            }

            // count
            addToPeopleCount(sing*family.size);

            // genders
            addMales(sing*family.countMale);
            addFemales(sing*family.countFemale);

            // age
            for(int i = 0; i<family.countAge.length; i++)
                addAgeGroupByIndex(i, (sing*family.countAge[i]));

            // income
            for(int i = 0; i<family.countIncome.length; i++)
                addIncomeGroupByIndex(i, (sing*family.countIncome[i]));

            // industry
            for(int i = 0; i<family.countIndustry.length; i++)
                addIndustryGroupByIndex(i, (sing*family.countIndustry[i]));

            // dwelling properties
            addDwellingCount(sing);
            addStructureTotals(family.dwelling.codeType, sing);
            addBedroomTotalsByABS(family.dwelling.codeBedrooms, sing);
            addRentTotalsByABS(family.dwelling.codeRent, sing);
            addMortgageTotalsByABS(family.dwelling.codeMortgage, sing);
            addVehicleTotalsByABS(family.dwelling.codeVehicles, sing);
        }

        // -----------------------------------------------------------------
        // sum for a specified category
        public int getTotalCountFor(int[] category)
        {
            int sum = 0;
            for(int i = 0; i<category.length; i++)
                sum += category[i];
            return sum;
        }

        // -----------------------------------------------------------------    Total population between cateogries
        // use maximum as the population of the zone
        public int getTotalPopulation() { return getTotalPopulationMax(); }

        // max between different totals (each category has a different total due to noise)
        public int getTotalPopulationMax() { return Math.max(Math.max(Math.max(Math.max(getTotalPopulationGender(), getTotalPopulationAge()), getTotalPopulationInc()), getTotalPopulationInd()), getPeopleCount());  }
        // min between different totals (each category has a different total due to noise)
        public int getTotalPopulationMin() { return Math.min(Math.min(Math.max(Math.min(getTotalPopulationGender(), getTotalPopulationAge()), getTotalPopulationInc()), getTotalPopulationInd()), getPeopleCount());  }
        // noise margin between max and min
        public int getTotalPopulationMargin() { return getTotalPopulationMax() - getTotalPopulationMin(); }
        // noise margin for a specific person category
        public int getTotalMarginForCategory(int[] category) { return getTotalPopulationMax() - getTotalCountFor(category); }

        // totals for each category
        public int getTotalPopulationGender()
        {
            return genderTotals[posM] + genderTotals[posF];
        }
        public int getTotalPopulationAge() { return getTotalCountFor(ageTotals); }
        public int getTotalPopulationInc() { return getTotalCountFor(incomeTotals); }
        public int getTotalPopulationInd() { return getTotalCountFor(industryTotals); }

        // -----------------------------------------------------------------    Zone population count methods
        public void setPeopleCount(int count) { peopleCount = count; }
        public void addToPeopleCount(int count) { peopleCount += count; }
        public int getPeopleCount() { return peopleCount; }

        // -----------------------------------------------------------------    Gender methods
        public void addMales(int count) { genderTotals[posM] += count; }
        public void setMales(int count) { genderTotals[posM] = count; }
        public int getMales() { return genderTotals[posM]; }

        public void addFemales(int count) { genderTotals[posF] += count; }
        public void setFemales(int count) { genderTotals[posF] = count; }
        public int getFemales() { return genderTotals[posF]; }

        // -----------------------------------------------------------------    Age methods
        // ABS code
        public void addAgeGroupByABS(int code, int count) { ageTotals[ageCodeABS2BinTB(code)-1] += count; }
        public void setAgeGroupByABS(int code, int count) { ageTotals[ageCodeABS2BinTB(code)-1] = count; }
        public int getAgeGroupByABS(int code) { return ageTotals[ageCodeABS2BinTB(code)-1]; }

        // TB = this object bin / index
        public void addAgeGroupByTB(int bin, int count) { ageTotals[bin-1] += count; }
        public void setAgeGroupByTB(int bin, int count) { ageTotals[bin-1] = count; }
        public int getAgeGroupByTB(int bin) { return ageTotals[bin-1]; }

        public void addAgeGroupByIndex(int i, int count) { ageTotals[i] += count; }
        public void setAgeGroupByIndex(int i, int count) { ageTotals[i] = count; }
        public int getAgeGroupByIndex(int i) { return ageTotals[i]; }

        // -----------------------------------------------------------------    Income methods
        // TB = this objects bin / index
        public void addIncomeGroupByABS(int code, int count) { incomeTotals[code-1] += count; }
        public void setIncomeGroupByABS(int code, int count) { incomeTotals[code-1] = count; }
        public int getIncomeGroupByABS(int code) { return incomeTotals[code-1]; }

        public void addIncomeGroupByIndex(int i, int count) { incomeTotals[i] += count; }
        public void setIncomeGroupByIndex(int i, int count) { incomeTotals[i] = count; }
        public int getIncomeGroupByIndex(int i) { return incomeTotals[i]; }

        // -----------------------------------------------------------------    Industry methods
        // TB = this objects bin / index
        public void addIndustryGroupByABS(int code, int count) { industryTotals[code-1] += count; }
        public void setIndustryGroupByABS(int code, int count) { industryTotals[code-1] = count; }
        public int getIndustryGroupByABS(int code) { return industryTotals[code-1]; }

        public void addIndustryGroupByIndex(int i, int count) { industryTotals[i] += count; }
        public void setIndustryGroupByIndex(int i, int count) { industryTotals[i] = count; }
        public int getIndustryGroupByIndex(int i) { return industryTotals[i]; }

        // -----------------------------------------------------------------    Dwelling total methods between categories
        // use maximum as the population of the zone
        public int getTotalDwelling() { return getTotalDwellingMax(); }
        // max between different totals (each category has a different total due to noise)
        public int getTotalDwellingMax() { return Math.max(Math.max(Math.max(Math.max(Math.max(getDwellingCount(), getTotalDwellingStructure()), getTotalDwellingBedroom()), getTotalDwellingRent()), getTotalDwellingMortgage()), getTotalDwellingVehicle()); }
        // min between different totals (each category has a different total due to noise)
        public int getTotalDwellingMin()  { return Math.min(Math.min(Math.min(Math.min(Math.min(getDwellingCount(), getTotalDwellingStructure()), getTotalDwellingBedroom()), getTotalDwellingRent()), getTotalDwellingMortgage()), getTotalDwellingVehicle()); }
        // noise margin between max and min
        public int getTotalDwellingMargin() { return getTotalDwellingMax() - getTotalDwellingMin(); }
        // noise margin for a specific person category
        public int getTotalDwellingMarginForCategory(int[] category) { return getTotalDwellingMax() - getTotalCountFor(category); }

        // totals for each category
        public int getTotalDwellingStructure() { return getTotalCountFor(structureTotals); }
        public int getTotalDwellingBedroom() { return getTotalCountFor(bedroomTotals); }
        public int getTotalDwellingRent() { return getTotalCountFor(rentTotals); }
        public int getTotalDwellingMortgage() { return getTotalCountFor(mortgageTotals); }
        public int getTotalDwellingVehicle() { return getTotalCountFor(vehicleTotals); }

        // -----------------------------------------------------------------    Dwelling count methods
        public void addDwellingCount(int count) { dwellingCount += count; }
        public void setDwellingCount(int count) { dwellingCount = count; }
        public int getDwellingCount() { return dwellingCount; }

        // -----------------------------------------------------------------    Dwelling structure methods
        public void addStructureTotals(int code, int count) { structureTotals[code-1] += count; }
        public void setStructureTotals(int code, int count) { structureTotals[code-1] = count; }
        public int getStructureTotals(int code) { return structureTotals[code-1]; }

        // -----------------------------------------------------------------    Dwelling bedroom methods
        public void addBedroomTotalsByABS(int code, int count) { bedroomTotals[code] += count; }
        public void setBedroomTotalsByABS(int code, int count) { bedroomTotals[code] = count; }
        public int getBedroomTotalsByABS(int code) { return bedroomTotals[code]; }

        // -----------------------------------------------------------------    Dwelling rent methods
        public void addRentTotalsByABS(int code, int count) { rentTotals[code-1] += count; }
        public void setRentTotalsByABS(int code, int count) { rentTotals[code-1] = count; }
        public int getRentTotalsByABS(int code) { return rentTotals[code-1]; }

        // -----------------------------------------------------------------    Dwelling mortgage methods
        public void addMortgageTotalsByABS(int code, int count) { mortgageTotals[code-1] += count; }
        public void setMortgageTotalsByABS(int code, int count) { mortgageTotals[code-1] = count; }
        public int getMortgageTotalsByABS(int code) { return mortgageTotals[code-1]; }

        // -----------------------------------------------------------------    Dwelling vehicle methods
        public void addVehicleTotalsByABS(int code, int count) { vehicleTotals[code] += count; }
        public void setVehicleTotalsByABS(int code, int count) { vehicleTotals[code] = count; }
        public int getVehicleTotalsByABS(int code) { return vehicleTotals[code]; }

        // -----------------------------------------------------------------    Clone interface
        @Override
        public ZoneSA1 clone()throws CloneNotSupportedException{ return (ZoneSA1) super.clone(); }

        // -----------------------------------------------------------------    Comparable interface
        @Override
        public int compareTo(ZoneSA1 o)
        {
            Integer s1 = getTotalPopulation();
            Integer s2 = o.getTotalPopulation();
            return s1.compareTo(s2);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------  Jobs
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

    // -----------------------------------------------------------------------------------------------------------------  Zone Quality
    private class ZoneQuality
    {
        public int zone; // can be SA1 or SA2
        public ArrayList<Integer>[] structurePrices;
        public ArrayList<Integer>[] structureQualities;
        public int[] averagePricesPerDwelling;
        public double[] averageQualityPerDwelling;
        public int overallAveragePrice;
        public double overallVerageQuality;

        public ZoneQuality(int newZoneSA)
        {
            // allocate SA1 or SA2 (should be unique)
            zone = newZoneSA;

            // create arrays to store dwelling prices & average prices
            averagePricesPerDwelling = new int[ABS_DWELL_GROUPS];
            averageQualityPerDwelling = new double[ABS_DWELL_GROUPS];
            structurePrices = new ArrayList[ABS_DWELL_GROUPS];
            structureQualities = new ArrayList[ABS_DWELL_GROUPS];

            // initiate the array with ArrayLists
            for(int i=0; i<ABS_DWELL_GROUPS; i++)
            {
                structurePrices[i] = new ArrayList<Integer>();
                structureQualities[i] = new ArrayList<Integer>();
            }
        }

        public void addStructurePrice(int type, int price)
        {
            structurePrices[type-1].add(price);
        }
        public void addStructureQuality(int type, int quality)
        {
            structureQualities[type-1].add(quality);
        }

        public int getAveragePriceForStructure(int type)
        {
            return averagePricesPerDwelling[type-1];
        }
        public double getAverageQualityForStructure(int type)
        {
            return averageQualityPerDwelling[type-1];
        }

        public void calculateAverages()
        {
            int overallSumP = 0, overallSumQ = 0;
            int numberOfDwellsP = 0, numberOfDwellsQ= 0;

            // for each structure type
            for(int i = 0; i < ABS_DWELL_GROUPS; i++)
            {
                int sumP = 0, sumQ = 0;

                // get the prices for this structure type
                ArrayList<Integer> prices = structurePrices[i];
                numberOfDwellsP += prices.size();

                // sum the prices for this structure type
                for (Integer price : prices)
                {
                    sumP += price;
                    overallSumP += price;
                }

                // get the qualities for this structure type
                ArrayList<Integer> qualities = structureQualities[i];
                numberOfDwellsQ += qualities.size();

                // sum the prices for this structure type
                for (Integer quality : qualities)
                {
                    sumQ += quality;
                    overallSumQ += quality;
                }

                // calculate the average price & quality for this structure type
                averagePricesPerDwelling[i] = (prices.size() > 0 ? (sumP/prices.size()) : 0 );
                averageQualityPerDwelling[i] = (qualities.size() > 0 ? (double)((double)sumQ/(double)qualities.size()) : 0);
            }

            overallAveragePrice = (overallSumP/numberOfDwellsP);
            overallVerageQuality = (double)((double)overallSumQ/(double)numberOfDwellsQ);
        }
    }



}