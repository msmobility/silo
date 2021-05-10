package de.tum.bgu.msm.syntheticPopulationGenerator.perth;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.common.util.ResourceUtil;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.syntheticPopulationGenerator.SyntheticPopI;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import run.DataBuilder;

import java.io.PrintWriter;
import java.util.*;

/**
 * Generates a simple synthetic population for the perth Study Area
 * @author Rolf Moeckel (TUM) & Sonja Stemler (UWA) & Martin Porebski (UWA)
 * Created on Oct. 31st, 2018 in Munich
 */

public class

SyntheticPopPerth implements SyntheticPopI
{
    protected static final String PROPERTIES_RUN_SP                  = "run.synth.pop.generator";
    protected static final String PROPERTIES_PUMS_PERSONS            = "pums.persons";
    protected static final String PROPERTIES_PUMS_DWELLINGS          = "pums.dwellings";
    // protected static final String PROPERTIES_PUMS_FAMILIES           = "pums.families";
    // protected static final String PROPERTIES_VACANCY_RATES           = "vacancy.rate.by.type";
    // protected static final String PROPERTIES_COUNTY_VACANCY_RATES    = "county.vacancy.rates";
    // protected static final String PROPERTIES_VALIDATE_SYNTH_POP      = "validate.synth.pop";

    protected transient Logger logger = Logger.getLogger(SyntheticPopPerth.class);

    private ResourceBundle rb;
    //protected HashMap<Integer, int[]> tazByWorkZonePuma;
    protected HouseholdDataManager householdData;
    protected RealEstateDataManager realEstateData;
    protected JobDataManager jobData;
    //protected HashMap<Integer, int[]> vacantJobsByZone;
    private String baseDirectory;

    private PrintWriter pwhh;
    private PrintWriter pwpp;
    private PrintWriter pwdd;
    private PrintWriter pwjj;

    // Martin 16/01/19
    private TableDataSet genderPerArea = null;
    private TableDataSet dwellUnoccPerArea = null;
    private ArrayList<Dwelling> dwellingList = new ArrayList();
    private HashMap<Integer, ZoneSA1[]> zoneMap = new HashMap<>();
    private HashMap<Integer, ArrayList<Dwelling>> dwellingsPerZoneMap = new HashMap<>();
    private JobCollection jobCollection = new JobCollection();
    long unassignedWorker = 0;
    long unemployedCount = 0;
    long employedCount = 0;

    // constructor
    public SyntheticPopPerth(ResourceBundle rb) { this.rb = rb; }

    // Run the synthetic population generator
    public void runSP()
    {
        int year = 2011;

        if (!ResourceUtil.getBooleanProperty(rb, PROPERTIES_RUN_SP)) return;

        logger.info("Generating synthetic populations of household/persons, dwellings and jobs");
        baseDirectory = Properties.get().main.baseDirectory;
        DataContainer dataContainer = DataBuilder.buildDataContainer(Properties.get(), null);
        realEstateData = dataContainer.getRealEstateDataManager();
        householdData = dataContainer.getHouseholdDataManager();
        jobData = dataContainer.getJobDataManager();

        // open & preprocess the gender file used for the population distribution into SA1 zones
        openGenderBySA1(year);
        // open the list of vacant dwellings in perth
        dwellUnoccPerArea = SiloUtil.readCSVfile(baseDirectory + "input/perth_specific/dwellingUnoccupiedBySA1_" + year + ".csv");
        // open the files to save the data
        openFilesToWriteSyntheticPopulation(year);

        logger.info("\t ---------- Generating jobs.");
        createJobs(year);

        logger.info("\t ---------- Reading Australian PUMS (ABS) data.");
        processMicroData();

        logger.info("\t ---------- Assigning quality to dwellings.");
        determineDwellingQuality();

        logger.info("\t ---------- Adding vacant/unoccupied dwellings.");
        addVacantDwellings();

        logger.info("\t ---------- Saving the dd and jj files.");
        saveDwellings();
        saveJobs();

        logger.info("\t" + employedCount + " employed and " + unemployedCount + " unemployed");
        logger.info("\t" + unassignedWorker + " unassigned workers");
        /*
        logger.info ("  Total number of households created " + householdData.getHouseholds().size());
        logger.info ("  Total number of persons created    " + householdData.getPersons().size());
        logger.info ("  Total number of dwellings created  " + realEstateData.getDwellings().size());
        logger.info ("  Total number of jobs created       " + jobData.getJobs().size());
        calculateVacancyRate();
        summarizeVacantJobsByRegion();
        summarizeByPersonRelationship();
        SummarizeData.writeData(Properties.get().main.implementation.BASE_YEAR, dataContainer);
        */

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
                    JobSlot job = new JobSlot(jobData.getNextJobId(), zoneSA1, personId, type);
                    jobCollection.addNewJob(job);
                }
            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------------  SA1 Population Distribution
    private void openGenderBySA1(int year)
    {
        // open the file
        genderPerArea = SiloUtil.readCSVfile(baseDirectory + "input/perth_specific/genderBySA1_" + year + ".csv");

        // area codes in the file
        int[] areaCodes = new int[] {49, 50, 51, 52};

        // for each area
        for(int area = 0; area < areaCodes.length; area++)
        {
            // find zones that belong to this area
            ArrayList<ZoneSA1> zones = new ArrayList<>();
            for (int row = 1; row <= genderPerArea.getRowCount(); row++)
            {
                // if zone belongs to this area code
                int areaCode = (int) genderPerArea.getValueAt(row, "AreaCode");
                if(areaCodes[area] == areaCode)
                {
                    // add a new zone
                    ZoneSA1 zone = new ZoneSA1();
                    zone.Zone = (int)(genderPerArea.getValueAt(row, "SA1"));
                    zone.CountMale = (int)(genderPerArea.getValueAt(row, "Male"));
                    zone.CountFemale = (int)(genderPerArea.getValueAt(row, "Female"));
                    zones.add(zone);
                }
            }
            // place the array of zones under appropriate code in the hashmap
            zoneMap.put(areaCodes[area], zones.toArray(new ZoneSA1[zones.size()]));
        }
    }

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

    // -----------------------------------------------------------------------------------------------------------------  Process ABS data
    private void processMicroData()
    {
        // ABS contains 1% data, hence multiply by 100
        int weight = 100;

        // read PUMS data of the Australian Bureau Of Statistics (ABS) for Population
        String pumsFilePersons = baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_PUMS_PERSONS);
        TableDataSet pumsPersons = SiloUtil.readCSVfile(pumsFilePersons);
        String pumsFileDwellings = baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_PUMS_DWELLINGS);
        TableDataSet pumsDwellings = SiloUtil.readCSVfile(pumsFileDwellings);

        // for each row in the dwellings file
        for (int rowDd = 1; rowDd <= pumsDwellings.getRowCount(); rowDd++)
        {
            HashMap<Integer, Family> familyMap = new HashMap<Integer, Family>();

            // get ABS dwelling id from the DWELLING file
            String dwellingId = pumsDwellings.getStringValueAt(rowDd, "ABSHID Dwelling Record Identifier");

            // get the attributes of the dwelling from ABS
            int bedRoomCode = (int) pumsDwellings.getValueAt(rowDd, "BEDRD");
            int mortgageCode = (int) pumsDwellings.getValueAt(rowDd, "MRERD");
            int rentCode = (int) pumsDwellings.getValueAt(rowDd, "RNTRD");
            int typeCode = (int) pumsDwellings.getValueAt(rowDd, "STRD");
            int autoCode = (int) pumsDwellings.getValueAt(rowDd, "VEHRD");
            int geographicAreaCode = (int) pumsDwellings.getValueAt(rowDd, "AREAENUM");

            // convert those dwelling attributes to a SILO format
            int ddBedrooms = convertBedrooms(bedRoomCode);
            int ddMortgage = convertMortgage(mortgageCode);
            int ddRent = convertRent(rentCode);
            int ddType = typeCode;
            int ddQuality = 0;
            int autos = convertAutos(autoCode);

            // ---------------------------------------------------------------------------------------------------------
            // for each row in the PERSON file
            for (int rowPp = 1; rowPp <= pumsPersons.getRowCount(); rowPp++)
            {
                // get ABS ids from the PERSON file
                String p_dwellingId = pumsPersons.getStringValueAt(rowPp, "ABSHID Dwelling Record Identifier");
                int familyId = (int) pumsPersons.getValueAt(rowPp, "ABSFID Family Record Identifier");

                // find the people that live in this dwelling
                if (p_dwellingId.equals(dwellingId))
                {
                    // check if previously added family
                    Family family = familyMap.get(familyId);
                    if (family == null)
                    {
                        // add a new family to the map
                        family = new Family(weight);
                        familyMap.put(familyId, family);
                    }

                    // get the attributes of the family from ABS
                    int ageGroup = (int) pumsPersons.getValueAt(rowPp, "AGEP Age");
                    int sexCode = (int) pumsPersons.getValueAt(rowPp, "SEXP Sex");
                    int incomeCode = (int) pumsPersons.getValueAt(rowPp, "INCP Individual Income (weekly)");
                    int occupationCode = (int) pumsPersons.getValueAt(rowPp, "LFSP Labour Force Status");
                    int relationshipCode = (int) pumsPersons.getValueAt(rowPp, "RLHP Relationship in Household");
                    int industryCode = (int) pumsPersons.getValueAt(rowPp, "INDP Industry of Employment");

                    // convert those family attributes to a SILO format
                    family.age[family.size] = convertAge(ageGroup);
                    family.sex[family.size] = sexCode;
                    family.race[family.size] = 0;
                    family.income[family.size] = convertIncome(incomeCode);
                    family.occupation[family.size] = translateOccupation(occupationCode, family.age[family.size]);
                    family.relationship[family.size] = translateRelationship(relationshipCode);
                    family.industry[family.size] = translateIndustry(industryCode, family.occupation[family.size]);

                    family.size += 1;
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
                Family household = new Family(weight);
                for(Map.Entry<Integer, Family> entry : familyMap.entrySet())
                {
                    // get a family of the dwelling
                    Family family = entry.getValue();
                    // merge the families into one household
                    household.append(family);
                    // save the family and the dwelling
                    // savePumsRecord(weight, geographicAreaCode, ddType, ddBedrooms, ddRent, ddMortgage, ddQuality, autos, family);
                }
                savePumsRecord(weight, geographicAreaCode, ddType, ddBedrooms, ddRent, ddMortgage, ddQuality, autos, household);
            }
        }
    }

    private void savePumsRecord(int weight, int geoZone,
                                 int ddType, int ddBedrooms, int ddRent, int ddMortgage, int ddQuality,
                                 int hhCars, Family family)
    {
        // count how many of each gender there are in this family / household
        int hhCountM = 0;
        int hhCountF = 0;
        for(int g = 0; g < family.size; g++)
        {
            if(family.sex[g] == 1)
                hhCountM++;
            else if(family.sex[g] == 2)
                hhCountF++;
        }

        // replicate the same family x number of times to reconstruct from the 1% data
        for (int count = 0; count < weight; count++)
        {
            int saArea = translateSaZone(geoZone, hhCountM, hhCountF);

            // household attributes
            int newHhId = householdData.getNextHouseholdId();
            int hhSize = family.size;

            // dwelling attributes
            int newDdId = realEstateData.getNextDwellingId();
            int price = getDwellingPrice(ddRent, ddMortgage);

            // create a new dwelling (to be saved later)
            storeDwelling(newDdId, saArea, ddType, newHhId, ddBedrooms, ddQuality, price);

            // save households
            pwhh.println(newHhId + "," + newDdId + "," + saArea + "," + hhSize + "," + hhCars);

            // save people
            for (int s = 0; s < hhSize; s++)
            {
                int newPpId = householdData.getNextPersonId();
                int workplace = jobCollection.assignWorker(newPpId, family.industry[s]);

                pwpp.println(newPpId + "," + newHhId + "," +
                        family.age[s] + "," + family.sex[s]+"," + family.relationship[s] + "," + family.race[s] + "," +
                        family.occupation[s] + "," + workplace + "," + family.income[s]);


                if(family.occupation[s] == 1) employedCount++;
                if(family.occupation[s] == 2) unemployedCount++;
            }
        }
    }

    private void storeDwelling(int newDdId, int saArea, int ddType, int newHhId, int ddBedrooms, int ddQuality, int price)
    {
        // create dwelling
        Dwelling dwelling = new Dwelling(newDdId, saArea, ddType, newHhId, ddBedrooms, ddQuality, price);

        // store it to be saved later
        dwellingList.add(dwelling);

        // store it under the zone to be used for vacant dwellings
        ArrayList<Dwelling> dwls = dwellingsPerZoneMap.get(saArea);
        if(dwls == null)
            dwls = new ArrayList<>();
        dwls.add(dwelling);
        dwellingsPerZoneMap.put(saArea, dwls);

        // add dwelling price to calculate quality later
        addToQuality(saArea, ddType, price, ddBedrooms);
    }

    /*  Translate families from general area codes to more fine-grained
        statistical SA1 zones. Based on the statistical relationship
        between gender population in each zone and a family info in each
        dwelling in each area.
    */
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

    // -----------------------------------------------------------------------------------------------------------------  Quality
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
            DwellingQuality quality = qualityMap.get(dwellingList.get(i).PumaZone);
            if(quality != null)
            {
                Dwelling dwelling = dwellingList.get(i);

                double zoneQuality = quality.getQuality();
                double zoneTypeQuality = quality.getZoneDTypeQuality(dwelling.PumsDdType);

                double dwellingQuality = (dwelling.Price/zoneQuality)*(quality.determineTypeQuality(dwelling.Price, dwelling.BedRooms)/zoneTypeQuality);
                dwellingList.get(i).Quality = (int)(dwellingQuality/160*4)+1;
            }
            else
            {
                dwellingList.get(i).Quality = 0;
                logger.warn("Quality not determined for hhid: " + dwellingList.get(i).NewHhId);
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
                        while(dwellingsSameType.size() < 1 || counter <= 60)
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
                            vaccDwelling.NewDdId = realEstateData.getNextDwellingId();
                            vaccDwelling.PumaZone = zone;
                            vaccDwelling.PumsDdType = ddType;
                            vaccDwelling.NewHhId = -1;
                            vaccDwelling.BedRooms = oocDwelling.BedRooms;
                            vaccDwelling.Price = oocDwelling.Price;
                            vaccDwelling.Quality = oocDwelling.Quality;

                            // add the dwelling to be later saved to the file
                            dwellingList.add(vaccDwelling);
                        }
                    }
                    else
                    {
                        logger.warn("Failed to add dwelling type " + ddType + " in zone " + zone);
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
                if(dwelling.PumsDdType == ddType)
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
            pwdd.println(dwelling.NewDdId + "," + dwelling.PumaZone + "," + dwelling.PumsDdType + "," +
                    dwelling.NewHhId + "," + dwelling.BedRooms + "," + dwelling.Quality + "," + dwelling.Price);
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
            case 1: return 0;
            case 2: return 0;
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
            case 13: return 0;
            case 14: return 0;
            case 15: return 0;
            default: logger.error("Unknown income code " + incomeCode);
                return 0;
        }
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

    private int getDwellingPrice (int rent, int mortgage)
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
    private int convertToInteger(String s)
    {
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
        public int ID = -1;
        public int Zone = -1;
        public int PersonID = -1;
        public int Type = -1;

        public JobSlot() { }

        public JobSlot(int jjId, int jjZone, int jjWorker, int jjType)
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

        public int assignWorker(int jjPersonId, int jjType)
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
                }
                unassignedWorker++;
                // logger.info("Did not find a job for industry: " + jjType);
            }
            return -1;
        }
    }

    private class Family
    {
        public int[] age;
        public int[] sex;
        public int[] race;
        public int[] income;
        public int[] occupation;
        public int[] industry;
        public String[] relationship;

        public int Weight = 0;
        public int size = 0;

        public Family(int weight)
        {
            age = new int[weight];
            sex = new int[weight];
            race = new int[weight];
            income = new int[weight];
            occupation = new int[weight];
            industry = new int[weight];
            relationship = new String[weight];

            Weight = weight;
        }

        public void append(Family family)
        {
            for (int j = 0; j < family.size; j++)
            {
                int i = j+size;
                age[i] = family.age[j];
                sex[i] = family.sex[j];
                race[i] = family.race[j];
                income[i] = family.income[j];
                occupation[i] = family.occupation[j];
                industry[i] = family.industry[j];
                relationship[i] = family.relationship[j];
            }
            size += family.size;
        }
    }

    private class ZoneSA1
    {
        int Zone = -1;
        int CountMale = 0;
        int CountFemale = 0;
        double Probability = 0;

        public ZoneSA1() { }
    }

    private class Dwelling
    {
        public int NewDdId = -1;
        public int PumaZone = -1;
        public int PumsDdType = -1;
        public int NewHhId = -1;
        public int BedRooms = -1;
        public int Quality = -1;
        public int Price = -1;

        public Dwelling() { }

        public Dwelling(int newDdId, int pumaZone, int pumsDdType, int newHhId, int bedRooms, int quality, int price)
        {
            NewDdId = newDdId;
            PumaZone = pumaZone;
            PumsDdType = pumsDdType;
            NewHhId = newHhId;
            BedRooms = bedRooms;
            Quality = quality;
            Price = price;
        }
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




    // -----------------------------------------------------------------------------------------------------------------  Old Methods
    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    /*
    private void createJobs2()
    {
        // method to generate synthetic jobs

        logger.info("  Generating base year jobs");
        TableDataSet jobs = SiloUtil.readCSVfile(Properties.get().jobData.jobControlTotalsFileName);
        new JobType(Properties.get().jobData.jobTypes);

        // jobInventory by [industry][taz]
        // todo: set highest zone id
        // MARTIN 10/01/19 TODO
        // final int highestZoneId = 100;
        final int highestZoneId = 4953;
        float[][] jobInventory = new float[JobType.getNumberOfJobTypes()][highestZoneId + 1];
        tazByWorkZonePuma = new HashMap<>();  // this HashMap has same content as "HashMap tazByPuma", though is kept separately in case external workzones will be defined

        // read employment data
        // For reasons that are not explained in the documentation, some of the PUMA work zones were aggregated to the
        // next higher level. Keep this information.

        for (int row = 1; row <= jobs.getRowCount(); row++)
        {
            int taz = (int) jobs.getValueAt(row, "SMZ");
            //todo relate taz to puma work zone
            int pumaOfWorkZone = taz;
            if (tazByWorkZonePuma.containsKey(pumaOfWorkZone))
            {
                int[] list = tazByWorkZonePuma.get(pumaOfWorkZone);
                int[] newList = SiloUtil.expandArrayByOneElement(list, taz);
                tazByWorkZonePuma.put(pumaOfWorkZone, newList);
            }
            else
            {
                tazByWorkZonePuma.put(pumaOfWorkZone, new int[]{taz});
            }
            for (int jobTp = 0; jobTp < JobType.getNumberOfJobTypes(); jobTp++)
            {
                // jobInventory[jobTp][taz] = jobs.getValueAt(row, JobType.getJobType(jobTp) + "00");
                jobInventory[jobTp][taz] = jobs.getValueAt(row, JobType.getJobType(jobTp) + "11");
            }
        }

        // create base year employment
        //todo: get zone array
        // create an array from hashmap?
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


    private void identifyVacantJobsByZone ()
    {
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
    }*/

/*
    private void processMicroData2()
    {
        // read PUMS data of the Australian Bureau Of Statistics for Population

        logger.info("  Reading Australian PUMS data ---------- ");

        String pumsFileFamilies = baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_PUMS_FAMILIES);
        TableDataSet pumsFamilies = SiloUtil.readCSVfile(pumsFileFamilies);
        String pumsFilePersons = baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_PUMS_PERSONS);
        TableDataSet pumsPersons = SiloUtil.readCSVfile(pumsFilePersons);
        String pumsFileDwellings = baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_PUMS_DWELLINGS);
        TableDataSet pumsDwellings = SiloUtil.readCSVfile(pumsFileDwellings);

        int hhCount = 0;
        int ppCount = 0;
        int ddCount = 0;
        for (int rowHh = 1; rowHh <= pumsFamilies.getRowCount(); rowHh++)
        {
            // String householdId = pumsFamilies.getStringValueAt(rowHh, "ABSFID Family Record Identifier");
            String householdId = pumsFamilies.getStringValueAt(rowHh, "ABSHID Dwelling Record Identifier");

            int hhSize = 0;
            int geographicArea = (int) pumsFamilies.getValueAt(rowHh, "Areaenum Geographic area of enumeration");

            hhCount++;

            // --------------------------------------------------------------------------------------------------------- PP
            boolean personsOfThisHouseholdFound = false;
            int[] age = new int[100];
            int[] sex = new int[100];
            int[] income = new int[100];
            int[] occupation = new int[100];
            int[] race = new int[100];
            int[] industry = new int[100];
            String[] relationship = new String[100];

            for (int rowPp = 1; rowPp <= pumsPersons.getRowCount(); rowPp++)
            {
                // String personId = pumsPersons.getStringValueAt(rowPp, "ABSFID Family Record Identifier");
                String personId = pumsPersons.getStringValueAt(rowPp, "ABSHID Dwelling Record Identifier");

                if (personId.equals(householdId))
                {
                    personsOfThisHouseholdFound = true;
                    ppCount++;

                    // hier weitere Personen Attribute einfügen
                    int ageGroup = (int) pumsPersons.getValueAt(rowPp, "AGEP Age");
                    age[hhSize] = convertAge(ageGroup);
                    sex[hhSize] = (int) pumsPersons.getValueAt(rowPp, "SEXP Sex");
                    int incomeCode = (int) pumsPersons.getValueAt(rowPp, "INCP Individual Income (weekly)");
                    income[hhSize] = convertIncome(incomeCode);

                    // 10/01/19
                    occupation[hhSize] = translateOccupation((int) pumsPersons.getValueAt(rowPp, "LFSP Labour Force Status"), age[hhSize]);

                    //  10/01/19 - NOT NEEDED? Or is it for the below workplace missing?
                    // occupation[hhSize] = (int) pumsPersons.getValueAt(rowPp, "OCC06P Occupation");

                    //  10/01/19 -  was in savePumsRecord() before
                    relationship[hhSize] = translateRelationship((int) pumsPersons.getValueAt(rowPp, "RLHP Relationship in Household"));

                    // int race = (int) pumsPersons.getValueAt(rowPp, "BPLP Country of Birth of Person");
                    industry[hhSize] = (int) pumsPersons.getValueAt(rowPp, "INDP Industry of Employment");
//                    int travelmode = (int) pumsPersons.getValueAt(rowPp, "MTWP Method of Travel to Work");
//                    int moved1 = (int) pumsPersons.getValueAt(rowPp, "REGU1P Region of Usual Residence One Year Ago");
//                    int moved5 = (int) pumsPersons.getValueAt(rowPp, "REGU5P Region of Usual Residence Five Years Ago");
//                    int employed = (int) pumsPersons.getValueAt(rowPp, "LFS06P Labour Force Status");
//                    int married = (int) pumsPersons.getValueAt(rowPp, "MSTP Registered Marital Status");
//                    int hoursWorked = (int) pumsPersons.getValueAt(rowPp, "HRSP Hours Worked");
                    hhSize++;
                }
            }

            // --------------------------------------------------------------------------------------------------------- DD
            boolean dwellingOfThisHouseholdFound = false;
            int bedRooms = 0;
            //int vacancy = 0;
            int mortgage = 0;
            int rent = 0;
            int type = 0;
            int autos = 0;

            for (int rowDd = 1; rowDd <= pumsDwellings.getRowCount(); rowDd++)
            {
                //String dwellingId = pumsDwellings.getStringValueAt(rowDd, "ABSFID Family Record Identifier");
                String dwellingId = pumsDwellings.getStringValueAt(rowDd, "ABSHID Dwelling Record Identifier");
                if (dwellingId.equals(householdId))
                {
                    dwellingOfThisHouseholdFound = true;
                    ddCount++;

                    // hier dwelling attribute einfügen
                    int bedRoomCode = (int) pumsDwellings.getValueAt(rowDd, "BEDRD");
                    bedRooms = convertBedrooms(bedRoomCode);
                    //vacancy = (int) pumsDwellings.getValueAt(rowDd, "DWTD Dwelling Type");
                    int mortgageCode = (int) pumsDwellings.getValueAt(rowDd, "MRERD");
                    mortgage = convertMortgage(mortgageCode);
                    int rentCode = (int) pumsDwellings.getValueAt(rowDd, "RNTRD");
                    rent = convertRent(rentCode);
                    type = (int) pumsDwellings.getValueAt(rowDd, "STRD");
                    int autoCode = (int) pumsDwellings.getValueAt(rowDd, "VEHRD");
                    autos = convertAutos(autoCode);
                }
            }
            if (!personsOfThisHouseholdFound)
                logger.error("Could not find any corresponding persons for household with identifier " + householdId + ".");
            if (!dwellingOfThisHouseholdFound)
                logger.error("Could not find any corresponding dwelling for the household with the identifier " + householdId + ".");

            //todo: is there any quality variable for dwellings? Compare method guessQuality() further below - NO, will be ignored for now
            savePumsRecord2(geographicArea, 100, hhSize, type, bedRooms, autos, rent, mortgage, 4, sex, age, race, occupation, income, relationship);
        }

        logger.info("  Read " + hhCount + " PUMS family records from file: " + pumsFileFamilies);
        logger.info("  Read " + ddCount + " PUMS dwelling records from file: " + pumsFileDwellings);
        logger.info("  Read " + ppCount + " PUMS person records from file: " + pumsFilePersons);
    }
    private void savePumsRecord2(int pumaZone, int weight, int hhSize, int pumsDdType, int bedRooms,
                                 int autos, int rent, int mortgage, int quality, int[] gender, int[] age,
                                 int[] race, int[] occupation, int[] income, String[] relationship)
    {
        //  16/01/19 - Allocate dwellings into zones based on the family information
//        pumaZone = translatePumaZone(pumaZone, weight, gender);

        // todo: need to select TAZ within pumaZone
        for (int count = 0; count < weight; count++)
        {
            // todo: distribute into zones based on the counts
            // todo: prob based on the commute data

            // Write Dwellings
            int newDdId = RealEstateData.getNextDwellingId();
            int newHhId = householdData.getNextHouseholdId();
            //todo: Please check if you want the next step
            int price = getDwellingPrice(rent, mortgage);

            //  16/01/19
            // create a new dwelling
            Dwelling dwelling = new Dwelling(newDdId, pumaZone, pumsDdType, newHhId, bedRooms, quality, price);
            // add the dweling to the lice
            dwellingList.add(dwelling);
            // add dwelling price to calculate quality later
            // addToQuality(pumaZone, price);
            // pwdd.println(newDdId + "," + pumaZone + "," + pumsDdType + "," + newHhId + "," + bedRooms + "," + quality + "," + price);

            // write households
            pwhh.println(newHhId + "," + newDdId + "," + pumaZone + "," + hhSize+"," + autos);

            //  10/01/19 relationships = now is in processMicroData(), not needed anymore?
            // develop plausible method to assign person roles married, single and child - Take from "RLHP Relationship in Household"?
            // String[] personRoles = definePersonRolesInHousehold(hhSize);

            // write people
            for (int s = 0; s < hhSize; s++)
            {
                //  10/01/19 - Added relationship[s]
                int newPpId = householdData.getNextPersonId();
                pwpp.println(newPpId + "," + newHhId + "," + age[s] + "," + gender[s]+"," + relationship[s] + "," + race[s] + "," +
                        occupation[s] + "," + "WORKPLACE_MISSING" + "," + income[s]);
            }
        }
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


/*
    //  10/01/19 - Can delete?
    private String[] definePersonRolesInHousehold (int hhSize )
    {
        // define roles as single, married or child from ABS microdata "RLHP Relationship in Household"
        //  1 Husband, Wife or Partner
        //  2 Lone parent
        //  3 Child under 15
        //  4 Dependent student
        //  5 Non-dependent child
        //  6 Other related individual
        //  7 Non-family member
        //  8 Visitor (from within Australia)
        //  9 Not applicable
        // 10 Overseas visitor

        String[] personRoles = new String[hhSize];
        for (int person = 0; person < hhSize; person++) personRoles[person] = "single";
        return personRoles;
    }

    private Race defineRace(int hispanic, int singleRace)
    {
        // define race: 1 white, 2 black, 3 hispanic, 4 other
        if (hispanic > 1) return Race.hispanic;
        if (singleRace == 1) return Race.white;
        else if (singleRace == 2) return Race.black;
        return Race.other;
    }


    private void addVacantDwellings () {
        // PUMS generates too few vacant dwellings, add vacant dwellings to match vacancy rate

        logger.info("  Adding empty dwellings to match vacancy rate");

        HashMap<String, ArrayList<Integer>> ddPointer = new HashMap<>();
        // summarize vacancy
        final int highestZoneId = geoData.getZones().keySet().stream().max(Comparator.naturalOrder()).get();
        int[][][] ddCount = new int [highestZoneId + 1][DwellingType.values().length][2];
        for (Dwelling dd: realEstateData.getDwellings()) {
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
                    Dwelling dd = realEstateData.getDwelling(ids[selected]);
                    int newDdId = RealEstateData.getNextDwellingId();
                    Dwelling dwelling = DwellingUtils.getFactory().createDwelling(newDdId, zone.getZoneId(), null, -1, dd.getType(), dd.getBedrooms(), dd.getQuality(),
                            dd.getPrice(), 0f, dd.getYearBuilt());
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


    private void calculateVacancyRate () {
        //calculate and log vacancy rate

        int[] ddCount = new int[DwellingType.values().length];
        int[] occCount = new int[DwellingType.values().length];
        for (Dwelling dd: realEstateData.getDwellings()) {
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
    */
}
