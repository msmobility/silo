package de.tum.bgu.msm.syntheticPopulationGenerator.capeTown;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.common.matrix.Matrix;
import de.tum.bgu.msm.common.util.ResourceUtil;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.io.*;
import de.tum.bgu.msm.io.output.*;
import de.tum.bgu.msm.run.DataBuilderCapeTown;
import de.tum.bgu.msm.syntheticPopulationGenerator.capeTown.preparation.MicroDataManager;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobUtils;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.syntheticPopulationGenerator.SyntheticPopI;
import de.tum.bgu.msm.util.concurrent.ConcurrentExecutor;
import omx.OmxFile;
import omx.OmxLookup;
import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.GammaDistributionImpl;
import org.apache.commons.math.stat.Frequency;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;
import java.util.stream.IntStream;

public class SyntheticPopCTrace implements SyntheticPopI {

    private ResourceBundle rb;
    //Options to run de synthetic population
    protected static final String PROPERTIES_CONSTRAINT_BY_CITY_AND_CNTY  = "run.ipu.city.and.county";
    protected static final String PROPERTIES_RUN_IPU                      = "run.ipu.synthetic.pop";
    protected static final String PROPERTIES_RUN_SYNTHETIC_POPULATION     = "run.synth.pop.generator";
    protected static final String PROPERTIES_YEAR_MICRODATA               = "year.micro.data";
    //Routes of the input data
    protected static final String PROPERTIES_MICRODATA_2000_PATH          = "micro.data.2000";
    protected static final String PROPERTIES_MICRODATA_2010_PATH          = "micro.data.2010";
    protected static final String PROPERTIES_MARGINALS_REGIONAL_MATRIX    = "marginals.county";
    protected static final String PROPERTIES_MARGINALS_HOUSEHOLD_MATRIX   = "marginals.municipality";
    protected static final String PROPERTIES_SELECTED_MUNICIPALITIES_LIST = "municipalities.list";
    protected static final String PROPERTIES_RASTER_CELLS                 = "raster.cells.definition";
    protected static final String PROPERTIES_DISTANCE_RASTER_CELLS        = "distanceODmatrix";
    protected static final String PROPERTIES_JOB_DESCRIPTION              = "jobs.dictionary";
    protected static final String PROPERTIES_EDUCATION_DESCRIPTION        = "education.dictionary";
    protected static final String PROPERTIES_SCHOOL_DESCRIPTION           = "school.dictionary";
    protected static final String PROPERTIES_NUMBER_OF_DWELLING_QUALITY_LEVELS = "dwelling.quality.levels.distinguished";
    protected static final String PROPERTIES_TRIP_LENGTH_DISTRIBUTION     = "trip.length.distribution";
    //Routes of input data (if IPU is not performed)
    protected static final String PROPERTIES_WEIGHTS_MATRIX               = "weights.matrix";
    //Parameters of the synthetic population
    protected static final String PROPERTIES_REGION_ATTRIBUTES            = "attributes.region";
    protected static final String PROPERTIES_HOUSEHOLD_ATTRIBUTES         = "attributes.household";
    protected static final String PROPERTIES_HOUSEHOLD_SIZES              = "household.size.brackets";
    protected static final String PROPERTIES_MICRO_DATA_AGES              = "age.brackets";
    protected static final String PROPERTIES_MICRO_DATA_AGES_QUARTER      = "age.brackets.quarter";
    protected static final String PROPERTIES_MICRO_DATA_YEAR_DWELLING     = "year.dwelling";
    protected static final String PROPERTIES_MICRO_DATA_FLOOR_SPACE_DWELLING = "floor.space.dwelling";
    protected static final String PROPERTIES_MAX_ITERATIONS               = "max.iterations.ipu";
    protected static final String PROPERTIES_MAX_ERROR                    = "max.error.ipu";
    protected static final String PROPERTIES_INITIAL_ERROR                = "ini.error.ipu";
    protected static final String PROPERTIES_IMPROVEMENT_ERROR            = "min.improvement.error.ipu";
    protected static final String PROPERTIES_IMPROVEMENT_ITERATIONS       = "iterations.improvement.ipu";
    protected static final String PROPERTIES_INCREASE_ERROR               = "increase.error.ipu";
    protected static final String PROPERTIES_INCOME_GAMMA_PROBABILITY     = "income.probability";
    protected static final String PROPERTIES_INCOME_GAMMA_SHAPE           = "income.gamma.shape";
    protected static final String PROPERTIES_INCOME_GAMMA_RATE            = "income.gamma.rate";
    protected static final String PROPERTIES_JOB_TYPES                    = "employment.types";
    protected static final String PROPERTIES_SCHOOL_TYPES_DE              = "school.types";
    protected static final String PROPERTIES_JOB_ALPHA                    = "employment.choice.alpha";
    protected static final String PROPERTIES_JOB_GAMMA                    = "employment.choice.gamma";
    protected static final String PROPERTIES_UNIVERSITY_ALPHA             = "university.choice.alpha";
    protected static final String PROPERTIES_UNIVERSITY_GAMMA             = "university.choice.gamma";
    protected static final String PROPERTIES_EMPLOYMENT_BY_GENDER_EDU     = "employment.probability";
    //Read the synthetic population
    protected static final String PROPERTIES_HOUSEHOLD_SYN_POP            = "household.file.ascii";
    protected static final String PROPERTIES_PERSON_SYN_POP               = "person.file.ascii";
    protected static final String PROPERTIES_DWELLING_SYN_POP             = "dwelling.file.ascii";
    protected static final String PROPERTIES_JOB_SYN_POP                  = "job.file.ascii";
    protected static final String PROPERTIES_ATRIBUTES_MICRODATA_PP        = "read.attributes.pp";
    protected static final String PROPERTIES_ATRIBUTES_MICRODATA_HH        = "read.attributes.hh";


    protected TableDataSet microDataHousehold;
    protected TableDataSet microDataPerson;
    protected TableDataSet microDataDwelling;
    protected TableDataSet frequencyMatrix;
    protected TableDataSet marginalsCounty;
    protected TableDataSet marginalsMunicipality;
    protected TableDataSet cellsMatrix;

    protected int[] cityID;
    protected int[] countyID;
    protected HashMap<Integer, ArrayList> municipalitiesByCounty;
    protected HashMap<Integer, int[]> cityTAZ;
    private ArrayList<Integer> municipalities;
    private ArrayList<Integer> counties;

    protected String[] attributesCounty;
    protected String[] attributesMunicipality;
    protected int[] ageBracketsPerson;
    protected int[] sizeBracketsDwelling;
    protected int[] yearBracketsDwelling;
    protected int numberofQualityLevels;
    protected TableDataSet counterMunicipality;
    protected TableDataSet errorMunicipality;
    protected TableDataSet errorSummary;

    protected TableDataSet weightsTable;
    protected TableDataSet jobsTable;
    protected TableDataSet educationDegreeTable;
    protected TableDataSet schoolLevelTable;
    HashMap<String, Integer> jobIntTypes;
    protected String[] jobStringTypes;
    protected int[] schoolTypes;

    protected HashMap<Integer, int[]> idVacantJobsByZoneType;
    protected HashMap<Integer, Integer> numberVacantJobsByType;
    protected HashMap<Integer, int[]> idZonesVacantJobsByType;
    protected HashMap<Integer, Integer> numberVacantJobsByZoneByType;
    protected HashMap<Integer, Integer> numberZonesByType;

    protected HashMap<Integer, Integer> numberVacantSchoolsByZoneByType;
    protected HashMap<Integer, int[]> idZonesVacantSchoolsByType;
    protected HashMap<Integer, Integer> numberZonesWithVacantSchoolsByType;
    protected HashMap<Integer, Integer> schoolCapacityByType;

    protected double alphaJob;
    protected double gammaJob;
    protected TableDataSet probabilitiesJob;

    protected Matrix distanceMatrix;
    protected Matrix distanceImpedance;

    protected Map<Integer, Float> tripLengthFrequencyDistribution;

    protected HashMap<Integer, HashMap<String, Integer>> households;
    protected HashMap<Integer, HashMap<Integer, HashMap<String, Integer>>> personsInHouseholds;


    private HashMap<String, HashMap <Integer, Integer>> attributeCodeValues;
    private HashMap<String, String> attributeCodeToControlTotal;
    private HashMap<String, HashMap <Integer, Integer>> attributesControlTotal;
    private HashMap<String, String> attributeCodeToMicroPerson;
    private HashMap<String, String> attributeCodeToMicroHousehold;
    private HashMap<String, HashMap <String, Integer>> attributesMicroPerson;
    private HashMap<String, HashMap <String, Integer>> attributesMicroHousehold;
    private String[] codePersonAttributes;
    private String[] microPersonAttributes;
    private String[] microHouseholdAttributes;
    private String[] codeHouseholdAttributes;

    private Map<Integer, Map<Integer, Float>> tazOriginalToOther;
    private Map<Integer, Map<Integer, Dwelling>> occupiedDwellingsByZone;
    private final MicroDataManager microDataManager;
    protected TableDataSet dataHousehold;
    protected TableDataSet dataPerson;

    static Logger logger = Logger.getLogger(SyntheticPopCTrace.class);
    private DataContainer dataContainer;
    private Properties properties;

    public SyntheticPopCTrace(ResourceBundle rb, Properties properties){
        microDataManager = new MicroDataManager();
        this.rb = rb;
        this.properties = properties;
    }


    public void runSP(){
        //method to create the synthetic population
        if (!ResourceUtil.getBooleanProperty(rb, PROPERTIES_RUN_SYNTHETIC_POPULATION, false)) return;
        logger.info("   Starting to create the synthetic population.");
        readZonalData();
        createDirectoryForOutput();
        //TODO: change to cape town implementation
        dataContainer = DataBuilderCapeTown.getModelDataForCapeTown(properties, null);
        DataBuilderCapeTown.read(properties, dataContainer);
        long startTime = System.nanoTime();
        /*readAttributes();
        readControlTotals();
        int persons = readMicroDataCT();
        createMicroHouseholdsAndMicroPersons(persons);
        checkHouseholdRelationship();
        //runIPUbyCityAndCounty(); //IPU fitting with one geographical constraint. Each municipality is independent of others
        readIPU();
        generateHouseholdsPersonsDwellings(); //Monte Carlo selection process to generate the synthetic population. The synthetic dwellings will be obtained from the same microdata
        generateJobs(); //Generate the jobs by type. Allocated to TAZ level
        assignJobs(); //Workplace allocation
        assignSchools(); //School allocation
        addCars(false);*/
        //readSyntheticPopulation();
        //readAndSummarize();
        checkHouseholdRelationshipObject();
        checkMarriages();
        summarizeData(dataContainer);

        long estimatedTime = System.nanoTime() - startTime;
        logger.info("   Finished creating the synthetic population. Elapsed time: " + estimatedTime);
    }

    private void checkMarriages() {
        int countMarriage = 0;
        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()){
            int marriedFemale = 0;
            int marriedMale = 0;
            for (Person pp : hh.getPersons().values()){
                if (pp.getRole().equals(PersonRole.MARRIED)){
                    if (pp.getGender().equals(Gender.FEMALE)){
                        marriedFemale++;
                    } else {
                        marriedMale++;
                    }
                }
            }
            if (marriedFemale!= marriedMale){
                logger.info(hh.getId());
                countMarriage++;
            }

        }
        logger.info("Consistency check marriage fail " + countMarriage + " times.");
    }


    private void readAttributes() {
        //Read attributes and process dictionary

        String fileName = "input/syntheticPopulation/variablesCTDictionary.csv";

        attributeCodeValues = new HashMap<>();
        attributesControlTotal = new HashMap<>();
        attributesMicroPerson = new HashMap<>();
        attributesMicroHousehold = new HashMap<>();
        attributeCodeToControlTotal = new HashMap<>();
        attributeCodeToMicroPerson = new HashMap<>();
        attributeCodeToMicroHousehold = new HashMap<>();
        HashMap<Integer, String> attributeOrder = new HashMap<>();
        ageBracketsPerson = new int[]{6,14,18,21,25,30,35,40,45,50,55,60,65,70,75,80,120};

        String recString = "";
        int recCount = 0;
        int atCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posType    = SiloUtil.findPositionInArray("Type", header);
            int posLabelCode  = SiloUtil.findPositionInArray("labelCode",header);
            int posLabelMicroData = SiloUtil.findPositionInArray("labelMicroData",header);
            int posLabelControlTotal = SiloUtil.findPositionInArray("labelControlTotal",header);
            int posValueCode = SiloUtil.findPositionInArray("valueCode", header);
            int posValueInt = SiloUtil.findPositionInArray("valueInt", header);
            int posValueString = SiloUtil.findPositionInArray("valueString", header);
            int postIPU = SiloUtil.findPositionInArray("IPU", header);
            int posImportance = SiloUtil.findPositionInArray("order", header);
            //int posArea = SiloUtil.findPositionInArray("Area", header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");

                String type = lineElements[posType];
                String labelCode = lineElements[posLabelCode];
                String labelMicroData = lineElements[posLabelMicroData];
                String labelControlTotal = lineElements[posLabelControlTotal];
                int valueCode = Integer.parseInt(lineElements[posValueCode]);
                int valueInt = Integer.parseInt(lineElements[posValueInt]);
                String valueString = lineElements[posValueString];
                boolean ipu = Boolean.parseBoolean(lineElements[postIPU]);
                int importance = Integer.parseInt(lineElements[posImportance]);
                //String area = lineElements[posArea];

                //update map of the attributes from the code
                if (!attributeCodeToControlTotal.containsKey(labelCode)) {
                    attributeCodeToControlTotal.put(labelCode, labelControlTotal);
                    if (type.equals("Person")) {
                        attributeCodeToMicroPerson.put(labelCode, labelMicroData);
                    } else {
                        attributeCodeToMicroHousehold.put(labelCode, labelMicroData);
                    }
                }
                if (ipu) {
                    if (valueCode > -1) {
                        updateInnerMap(attributeCodeValues, labelCode, valueCode, valueCode);
                    }
                    updateInnerMap(attributesControlTotal, labelControlTotal, valueInt, valueCode);
                }
                if (type.equals("Person")){
                    updateInnerMap(attributesMicroPerson, labelMicroData, valueString, valueCode);
                } else {
                    updateInnerMap(attributesMicroHousehold, labelMicroData, valueString, valueCode);
                }
                if (importance > 0){
                    attributeOrder.put(importance, labelCode + valueCode);
                    atCount++;
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }

        codePersonAttributes = new String[attributeCodeToMicroPerson.size()];
        microPersonAttributes = new String[attributeCodeToMicroPerson.size()];

        attributesMunicipality = new String[atCount + 2];
        attributesCounty = new String[2];
        int k = 0;
        for (int j = 1; j <= atCount; j++){
            String label = attributeOrder.get(j);
            //attributesCounty[atCount - j] = label;
            attributesMunicipality[atCount - j] = label;
        }
        attributesCounty[0] = "population";
        attributesCounty[1] = "hhTotal";
        attributesMunicipality[atCount] = "population";
        attributesMunicipality[atCount + 1] = "hhTotal";


        int i = 0;
        for (Map.Entry<String, String> pairCode : attributeCodeToMicroPerson.entrySet()){
            codePersonAttributes[i] = pairCode.getKey();
            microPersonAttributes[i] = pairCode.getValue();
            i++;
        }
        codeHouseholdAttributes = new String[attributeCodeToMicroHousehold.size()];
        microHouseholdAttributes = new String[attributeCodeToMicroHousehold.size()];
        i = 0;
        for (Map.Entry<String, String> pairCode : attributeCodeToMicroHousehold.entrySet()){
            codeHouseholdAttributes[i] = pairCode.getKey();
            microHouseholdAttributes[i] = pairCode.getValue();
            i++;
        }
        logger.info("Finished reading attributes.");

    }

    private void readZonalData() {

        //List of municipalities and counties that are used for IPU and allocation
        TableDataSet selectedMunicipalities = SiloUtil.readCSVfile(rb.getString(PROPERTIES_SELECTED_MUNICIPALITIES_LIST)); //TableDataSet with all municipalities
        municipalities = new ArrayList<>();
        counties = new ArrayList<>();
        municipalitiesByCounty = new HashMap<>();
        for (int row = 1; row <= selectedMunicipalities.getRowCount(); row++){
            if (selectedMunicipalities.getValueAt(row,"Select") == 1f){
                int city = (int) selectedMunicipalities.getValueAt(row,"ID_city");
                municipalities.add(city);
                int county = (int) selectedMunicipalities.getValueAt(row,"ID_county");
                if (!SiloUtil.containsElement(counties, county)) {
                    counties.add(county);
                }
                if (municipalitiesByCounty.containsKey(county)) {
                    ArrayList<Integer> citiesInThisCounty = municipalitiesByCounty.get(county);
                    citiesInThisCounty.add(city);
                    municipalitiesByCounty.put(county, citiesInThisCounty);
                } else {
                    ArrayList<Integer> arrayList = new ArrayList<>();
                    arrayList.add(city);
                    municipalitiesByCounty.put(county, arrayList);
                }
            }
        }
        cityID = SiloUtil.convertArrayListToIntArray(municipalities);
        countyID = SiloUtil.convertArrayListToIntArray(counties);


        //TAZ attributes
        cellsMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_RASTER_CELLS));
        cellsMatrix.buildIndex(cellsMatrix.getColumnPosition("ID_cell"));
        cityTAZ = new HashMap<>();
        for (int i = 1; i <= cellsMatrix.getRowCount(); i++){
            int city = (int) cellsMatrix.getValueAt(i,"ID_city");
            int taz = (int) cellsMatrix.getValueAt(i,"ID_cell");
            if (cityTAZ.containsKey(city)){
                int[] previousTaz = cityTAZ.get(city);
                previousTaz = SiloUtil.expandArrayByOneElement(previousTaz, taz);
                cityTAZ.put(city, previousTaz);
            } else {
                int[] previousTaz = {taz};
                cityTAZ.put(city,previousTaz);
            }
        }


        //Read the skim matrix
        logger.info("   Starting to read OMX matrix");
        String omxFileName= ResourceUtil.getProperty(rb,PROPERTIES_DISTANCE_RASTER_CELLS);
        OmxFile travelTimeOmx = new OmxFile(omxFileName);
        travelTimeOmx.openReadOnly();
        distanceMatrix = SiloUtil.convertOmxToMatrix(travelTimeOmx.getMatrix("euclideanDistance"));
        OmxLookup omxLookUp = travelTimeOmx.getLookup("lookup1");
        int[] externalNumbers = (int[]) omxLookUp.getLookup();
        distanceMatrix.setExternalNumbersZeroBased(externalNumbers);
        for (int i = 1; i <= distanceMatrix.getRowCount(); i++){
            for (int j = 1; j <= distanceMatrix.getColumnCount(); j++){
                distanceMatrix.setValueAt(i,j, distanceMatrix.getValueAt(i,j)/1000);
            }
        }
        logger.info("   Read OMX matrix");

        //Read trip length frequency distribution
        logger.info("   Starting to read trip length frequency distribution");
        TableDataSet tripLength = SiloUtil.readCSVfile2(rb.getString(PROPERTIES_TRIP_LENGTH_DISTRIBUTION));
        tripLengthFrequencyDistribution = new HashMap<>();
        for (int i = 1; i <= tripLength.getRowCount(); i++){
            int distance = (int) tripLength.getValueAt(i, "km");
            float value = tripLength.getValueAt(i, "HBW");
            tripLengthFrequencyDistribution.put(distance, value);
        }

    }


    private void createDirectoryForOutput() {
        // create output directories
        SiloUtil.createDirectoryIfNotExistingYet("microData");
        SiloUtil.createDirectoryIfNotExistingYet("microData/interimFiles");
    }



    private int readMicroDataCT(){
        //method to read the synthetic population initial data
        logger.info("   Starting to read the micro data for Cape Town");
        readHouseholds();
        readPersons();
        int persons = checkHouseholdAndPersonCorrespondence();
        logger.info("   Finished reading the micro data");
        return persons;
    }

    private void readControlTotals() {
        //method to obtain the list of attributes for the IPU with the categories
        //Read the attributes at the municipality level (household attributes) and the marginals at the municipality level (German: Gemeinden)

        TableDataSet controlTotalsMun = SiloUtil.readCSVfile2(rb.getString(PROPERTIES_MARGINALS_HOUSEHOLD_MATRIX));
        TableDataSet controlTotalsCounty = SiloUtil.readCSVfile2(rb.getString(PROPERTIES_MARGINALS_REGIONAL_MATRIX));

        initializeMarginals(controlTotalsMun, controlTotalsCounty);
        processMarginals(controlTotalsMun, controlTotalsCounty);

        SiloUtil.writeTableDataSet(marginalsMunicipality, "input/syntheticPopulation/controlTotalsMunIPU.csv");
        SiloUtil.writeTableDataSet(marginalsCounty, "input/syntheticPopulation/controlTotalsCountIPU.csv");
        logger.info("   Finished reading and converting control totals");
    }


    private void initializeMarginals(TableDataSet controlTotals, TableDataSet controlTotalsCounty) {
        //initialize the marginals
        marginalsMunicipality = new TableDataSet();
        marginalsMunicipality.appendColumn(controlTotals.getColumnAsInt("ID_city"),"ID_city");
        marginalsMunicipality.buildIndex(marginalsMunicipality.getColumnPosition("ID_city"));

        marginalsCounty = new TableDataSet();
        marginalsCounty.appendColumn(controlTotalsCounty.getColumnAsInt("ID_county"),"ID_county");
        marginalsCounty.buildIndex(marginalsCounty.getColumnPosition("ID_county"));

        for (Map.Entry<String, HashMap<Integer, Integer>> pairCode : attributeCodeValues.entrySet()) {
            String attribute = pairCode.getKey();
            Set<Integer> levels = pairCode.getValue().keySet();
            Iterator<Integer> iterator = levels.iterator();
            while(iterator.hasNext()){
                Integer setElement = iterator.next();
                String label = attribute + setElement;
                int[] dummy = SiloUtil.createArrayWithValue(marginalsMunicipality.getRowCount(), 0);
                marginalsMunicipality.appendColumn(dummy, label);
                int[] dummy2 = SiloUtil.createArrayWithValue(marginalsCounty.getRowCount(), 0);
                marginalsCounty.appendColumn(dummy2, label);
            }
        }
    }

    private void processMarginals(TableDataSet controlTotals, TableDataSet controlTotalsCounty) {
        //update the values inside the marginals municipality tableDataSet

        marginalsCounty.appendColumn(controlTotalsCounty.getColumnAsInt("population"), "population");
        marginalsCounty.appendColumn(controlTotalsCounty.getColumnAsInt("hhTotal"), "hhTotal");
        marginalsMunicipality.appendColumn(controlTotals.getColumnAsInt("population"), "population");
        marginalsMunicipality.appendColumn(controlTotals.getColumnAsInt("hhTotal"), "hhTotal");
        for (Map.Entry<String, HashMap<Integer, Integer>> pairCode : attributeCodeValues.entrySet()) {
            String attribute = pairCode.getKey();
            String attributeControlTotal = attributeCodeToControlTotal.get(attribute);
            HashMap<Integer, Integer> attributeMap = attributesControlTotal.get(attributeControlTotal);
            for (Map.Entry<Integer, Integer> pairAttribute : attributeMap.entrySet()) {
                String labelControlTotal = attributeControlTotal + pairAttribute.getKey();
                if (pairAttribute.getValue() > -1) {
                    String labelCode = attribute + pairAttribute.getValue();
                    for (int i = 1; i <= controlTotals.getRowCount(); i++) {
                        int value = (int) marginalsMunicipality.getValueAt(i, labelCode);
                        int newValue = value + (int) controlTotals.getValueAt(i, labelControlTotal);
                        marginalsMunicipality.setValueAt(i, labelCode, newValue);
                    }
                    for (int i = 1; i <= controlTotalsCounty.getRowCount(); i++) {
                        int value = (int) marginalsCounty.getValueAt(i, labelCode);
                        int newValue = value + (int) controlTotalsCounty.getValueAt(i, labelControlTotal);
                        marginalsCounty.setValueAt(i, labelCode, newValue);
                    }
                }
            }
        }
    }



    private int checkHouseholdAndPersonCorrespondence() {
        //method to remove households from the map that either:
        //1- have no persons on the person file
        //2- have different number of persons at the person file than household size
        int persons = 0;
        int ppCount = 0;
        int hhCount = 0;
        Iterator <Map.Entry<Integer, HashMap<String, Integer>>> it = households.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<Integer, HashMap<String, Integer>> pair = it.next();
            int hhId = pair.getKey();
            int hhSize = pair.getValue().get("hhSizeReal");
            if (!personsInHouseholds.containsKey(hhId)){
                it.remove();
                ppCount = ppCount + hhSize;
                hhCount++;
            } else {
                int members = personsInHouseholds.get(hhId).values().size();
                if (members != hhSize) {
                    it.remove();
                    ppCount = ppCount + hhSize;
                    hhCount++;
                } else {
                    persons = persons + hhSize;
                }
            }
        }
        logger.info("   " + ppCount + " persons were removed from the sample at " + hhCount + " households.");
        logger.info("   Microdata contains " + households.size() + " households with " + persons + " persons due to inconsistencies on the micro data.");

        return persons;
    }

    private void readPersons() {

        String fileName = "input/syntheticPopulation/newPersons.csv";
        personsInHouseholds = new HashMap<>();
        HashMap<Integer, HashMap<String, Integer>> noDatas = new HashMap<>();

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posHhId   = SiloUtil.findPositionInArray("new$X.x", header);
            int posId   = SiloUtil.findPositionInArray("X.y",header);
            HashMap<String, Integer> positionAttribute = new HashMap<>();
            for (int i = 0; i < microPersonAttributes.length; i++){
                positionAttribute.put(microPersonAttributes[i], SiloUtil.findPositionInArray(microPersonAttributes[i], header));
            }

            // read line
            while ((recString = in.readLine()) != null) {

                String[] lineElements = recString.split(",");
                int idHh = Integer.parseInt(lineElements[posHhId]);
                Integer id = Integer.parseInt(lineElements[posId]);
                recCount++;
                HashMap<String, Integer> attributeMap = new HashMap<>();
                attributeMap.put("hhId", idHh);
                boolean allData = true;
                for (int i = 0; i < microPersonAttributes.length; i++){
                    String attributeCode = codePersonAttributes[i];
                    String attributeMicro =  microPersonAttributes[i];
                    String valueMicroData = lineElements[positionAttribute.get(attributeMicro)];
                    int valueCode = attributesMicroPerson.get(attributeMicro).get(valueMicroData);
                    attributeMap.put(attributeCode, valueCode);
                    if (valueCode < 0){
                        allData = false;
                    }
                }
                if (attributeMap.get("occupation") == -1){
                    if (attributeMap.get("age") < 19 || attributeMap.get("age") > 65) {
                        attributeMap.put("occupation", 2);
                        if (attributeMap.get("nationality") > -1){
                            allData = true;
                        }
                    }
                }
                if (allData) {
                    updatePersonsInHousehold(idHh, id, attributeMap);
                } else {
                    noDatas.put(id, attributeMap);
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }

        TableDataSet nos = new TableDataSet();
        int[] counter = createConsecutiveArray(noDatas.keySet().size());
        nos.appendColumn(counter,"ids");
        for (int i = 0; i < microPersonAttributes.length; i++) {
            String attributeCode = codePersonAttributes[i];
            int[] dummy = SiloUtil.createArrayWithValue(counter.length, 0);
            nos.appendColumn(dummy, attributeCode);
        }
        int row = 1;
        Iterator<Integer> iterator = noDatas.keySet().iterator();
        while (iterator.hasNext()){
            int id = iterator.next();
            for (int i = 0; i < microPersonAttributes.length; i++){
                String attributeCode = codePersonAttributes[i];
                int value = noDatas.get(id).get(attributeCode);
                nos.setValueAt(row, attributeCode, value);
            }
            nos.setValueAt(row,1,id);
            row++;

        }
        SiloUtil.writeTableDataSet(nos, "input/syntheticPopulation/noData.csv");
        logger.info("Finished reading " + recCount + " persons. ");

    }

    private void readHouseholds() {

        String fileName = "input/syntheticPopulation/newHouseholds.csv";

        households = new HashMap<>();
        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId = SiloUtil.findPositionInArray("X.x", header);
            HashMap<String, Integer> positionAttribute = new HashMap<>();
            for (Map.Entry<String, String> pairCode : attributeCodeToMicroHousehold.entrySet()){
                String attributeMicro = pairCode.getValue();
                positionAttribute.put(attributeMicro, SiloUtil.findPositionInArray(attributeMicro, header));
            }

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int idhH = Integer.parseInt(lineElements[posId]);
                HashMap<String, Integer> attributeMap = new HashMap<>();
                for (Map.Entry<String, String> pairCode : attributeCodeToMicroHousehold.entrySet()){
                    String attribute = pairCode.getKey();
                    String attributeMicro =  pairCode.getValue();
                    String valueMicroData = lineElements[positionAttribute.get(attributeMicro)];
                    int valueCode = attributesMicroHousehold.get(attributeMicro).get(valueMicroData);
                    attributeMap.put(attribute, valueCode);
                }
                households.put(idhH, attributeMap);
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " households.");

    }


    private void createMicroHouseholdsAndMicroPersons(int personCount){
        //method to create the micro households with all the values
        logger.info("   Creating frequency matrix and converting micro data to code values");

        initializeMicroData(personCount);
        for (Map.Entry<Integer, HashMap<String, Integer>> householdEntry : households.entrySet()){
            Integer hhId = householdEntry.getKey();
            updateMicroHouseholds(hhId);
            updateMicroPersons(hhId);
        }
        substituteZeros();

        String fileName = "input/syntheticPopulation/householdsMicroCode.csv";
        SiloUtil.writeTableDataSet(microDataHousehold, fileName);
        String personName = "input/syntheticPopulation/personsMicroCode.csv";
        SiloUtil.writeTableDataSet(microDataPerson, personName);
        String freqName = "input/syntheticPopulation/frequencyMatrix.csv";
        SiloUtil.writeTableDataSet(frequencyMatrix, freqName);

    }

    private void substituteZeros() {
        //method to substitute zeros on the frequency matrix by very little number
        //required for IPU division by zeros

        for (int i = 1; i <= frequencyMatrix.getRowCount(); i++) {
            for (int j = 1; j <= frequencyMatrix.getColumnCount(); j++) {
                if (frequencyMatrix.getValueAt(i, j) == 0) {
                    frequencyMatrix.setValueAt(i, j, 0.0000001f);
                }
            }
        }

        for (int i = 1; i <= marginalsCounty.getRowCount(); i++){
            for (int j = 1; j <= marginalsCounty.getColumnCount(); j++){
                if (marginalsCounty.getValueAt(i, j) == 0){
                    marginalsCounty.setValueAt(i, j, 0.1f);
                }
            }
        }

        for (int i = 1; i <= marginalsMunicipality.getRowCount(); i++){
            for (int j = 1; j <= marginalsMunicipality.getColumnCount(); j++){
                if (marginalsMunicipality.getValueAt(i, j) == 0){
                    marginalsMunicipality.setValueAt(i, j, 0.1f);
                }
            }
        }

    }

    private void updateMicroHouseholds(Integer hhId) {

        for (int i = 0; i < codeHouseholdAttributes.length; i++) {
            String labelCode = codeHouseholdAttributes[i];
            String labelMicro = microHouseholdAttributes[i];
            int valueMicro = households.get(hhId).get(labelCode);
            microDataHousehold.setIndexedValueAt(hhId, labelMicro, valueMicro);
            if (attributeCodeValues.containsKey(labelCode)) {
                String labelFrequency = labelCode + valueMicro;
                frequencyMatrix.setIndexedValueAt(hhId, labelFrequency, 1);
            }
        }
        int value = (int) dataHousehold.getIndexedValueAt(hhId, "personCount");
        microDataHousehold.setIndexedValueAt(hhId, "personCount", value);
    }


    private void updateMicroPersons(Integer hhId) {

        HashMap<Integer, HashMap<String, Integer>> persons = personsInHouseholds.get(hhId);
        frequencyMatrix.setIndexedValueAt(hhId, "population", persons.keySet().size());
        for (Map.Entry<Integer, HashMap<String, Integer>> attributeMap : persons.entrySet()) {
            Integer personId = attributeMap.getKey();
            for (int i = 0; i < codePersonAttributes.length; i++){
                String labelMicro = microPersonAttributes[i];
                String labelCode = codePersonAttributes[i];
                int value = attributeMap.getValue().get(labelCode);
                microDataPerson.setIndexedValueAt(personId, labelMicro, value);
                if (attributeCodeValues.containsKey(labelCode) & value > -1) {
                    String labelFrequency = labelCode + value;
                    int previousFrequency = (int) frequencyMatrix.getIndexedValueAt(hhId, labelFrequency);
                    frequencyMatrix.setIndexedValueAt(hhId, labelFrequency, previousFrequency + 1);
                }
            }
        }
    }


    private void initializeMicroData(int personCount) {

        microDataHousehold = new TableDataSet();
        microDataPerson = new TableDataSet();
        frequencyMatrix = new TableDataSet();
        dataHousehold = SiloUtil.readCSVfile2("input/syntheticPopulation/newHouseholds.csv");
        dataHousehold.buildIndex(dataHousehold.getColumnPosition("X.x"));
        dataPerson = SiloUtil.readCSVfile2("input/syntheticPopulation/newPersons.csv");
        dataPerson.buildIndex(dataPerson.getColumnPosition("X.y"));

        int size = households.keySet().size();
        int[] ids = new int[size];
        int i = 0;
        int j = 0;
        int[] ppIds = new int[personCount];
        for (Map.Entry<Integer, HashMap<String, Integer>> pairCode : households.entrySet()){
            ids[i] = pairCode.getKey();
            for (Map.Entry<Integer, HashMap<String, Integer>> personCode : personsInHouseholds.get(pairCode.getKey()).entrySet()){
                ppIds[j] = personCode.getKey();
                j++;
            }
            i++;
        }
        frequencyMatrix.appendColumn(ids, "ID");
        frequencyMatrix.appendColumn(SiloUtil.createArrayWithValue(frequencyMatrix.getRowCount(), 1),"hhTotal");
        frequencyMatrix.appendColumn(SiloUtil.createArrayWithValue(frequencyMatrix.getRowCount(), 0),"population");
        frequencyMatrix.buildIndex(frequencyMatrix.getColumnPosition("ID"));
        microDataHousehold.appendColumn(ids, "ID");
        microDataHousehold.buildIndex(microDataHousehold.getColumnPosition("ID"));
        int[] dummy1 = SiloUtil.createArrayWithValue(microDataHousehold.getRowCount(), 0);
        microDataHousehold.appendColumn(dummy1,"personCount");
        microDataPerson.appendColumn(ppIds, "ID");
        microDataPerson.buildIndex(microDataPerson.getColumnPosition("ID"));

        for (Map.Entry<String, HashMap<Integer, Integer>> pairCode : attributeCodeValues.entrySet()) {
            String attribute = pairCode.getKey();
            Set<Integer> levels = pairCode.getValue().keySet();
            Iterator<Integer> iterator = levels.iterator();
            while(iterator.hasNext()){
                Integer setElement = iterator.next();
                String label = attribute + setElement;
                int[] dummy = SiloUtil.createArrayWithValue(frequencyMatrix.getRowCount(), 0);
                frequencyMatrix.appendColumn(dummy, label);
            }
        }

        for (Map.Entry<String, HashMap<String, Integer>> pairCode : attributesMicroHousehold.entrySet()){
            String attribute = pairCode.getKey();
            int[] dummy = SiloUtil.createArrayWithValue(microDataHousehold.getRowCount(), 0);
            microDataHousehold.appendColumn(dummy, attribute);
        }

        for (Map.Entry<String, HashMap<String, Integer>> pairCode : attributesMicroPerson.entrySet()){
            String attribute = pairCode.getKey();
            int[] dummy = SiloUtil.createArrayWithValue(microDataPerson.getRowCount(), 0);
            microDataPerson.appendColumn(dummy, attribute);
        }
    }


    private void checkHouseholdRelationship(){
        //method to check how household members are related

        int[] dummy = SiloUtil.createArrayWithValue(dataHousehold.getRowCount(), 0);
        int[] dummy1 = SiloUtil.createArrayWithValue(dataHousehold.getRowCount(), 0);
        dataHousehold.appendColumn(dummy,"nonClassifiedMales");
        dataHousehold.appendColumn(dummy1,"nonClassifiedFemales");
        int[] dummy2 = SiloUtil.createArrayWithValue(dataPerson.getRowCount(), -1);
        int[] dummy3 = SiloUtil.createArrayWithValue(dataPerson.getRowCount(), 0);
        dataPerson.appendColumn(dummy2, "personRole");
        dataPerson.appendColumn(dummy3, "rearrangedRole");


        for (int k = 1; k <= microDataHousehold.getRowCount(); k++){

            int hhSize = (int) dataHousehold.getValueAt(k,"DERH_HSIZE");
            int firstMember = (int) dataHousehold.getValueAt(k, "personCount");

            //single person household -> all set as single directly
            if (hhSize == 1){
                dataPerson.setValueAt(firstMember,"personRole",1); //set as single directly

                //multiperson household
            } else {

                //Create the maps to store the classified members of the households
                HashMap<Integer, Integer> childrenInHousehold = new HashMap<>();
                HashMap<String, HashMap<Integer, Integer>> noClass = new HashMap<>();
                HashMap<String, HashMap<Integer, Integer>> singles = new HashMap<>();
                HashMap<String, HashMap<Integer, Integer>> married = new HashMap<>();

                //direct classification of the members of the household
                for (int j = 0; j < hhSize; j++) {
                    int row = firstMember + j;
                    String relationToHead = microDataManager.translateRelation(dataPerson.getStringValueAt(row, "p02_relation"));
                    int age = (int) dataPerson.getValueAt(row, "f02_age");
                    int gender = microDataManager.translateGenderToInt(dataPerson.getStringValueAt(row, "f03_sex"));

                    if (relationToHead.equals("child")) {
                        childrenInHousehold.put(row, age); //children -> children
                    } else if (age < 15) {
                        childrenInHousehold.put(row, age); //children -> children
                    } else{
                        noClass = updateInnerMap(noClass, gender, age, row); //need further classification at the household level. Look for cohabitation
                    }
                }
                int[] countNotClassified = new int[2];
                int checkForCohabitation = 1;
                HashMap<Integer, Integer> notClassifiedMales = noClass.get("male");
                if (notClassifiedMales != null){
                    countNotClassified[0] = notClassifiedMales.size();
                    checkForCohabitation = 1;
                } else {
                    countNotClassified[0] = 0;
                }
                HashMap<Integer, Integer> notClassifiedFemales = noClass.get("female");
                if (notClassifiedFemales != null){
                    countNotClassified[1] = notClassifiedFemales.size();
                    checkForCohabitation = 1;
                } else {
                    countNotClassified[1] = 0;
                }
                if (checkForCohabitation == 1) {
                    //look for cohabitation
                    if (countNotClassified[0] == countNotClassified[1]) { //one male and one female were not classified
                        for (int possibleMales = 1; possibleMales <= countNotClassified[0]; possibleMales++) {
                            //check for a possible single male to get married (408 households with)
                            int rowFemale = (int) notClassifiedFemales.keySet().toArray()[possibleMales - 1];
                            int ageFemale = notClassifiedFemales.get(rowFemale);
                            HashMap<Integer, Integer> singleMale = singles.get("male");
                            if (singleMale == null & notClassifiedMales == null) { //no possible single male to marry -> set as single
                                singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                            } else if (singleMale != null) { //check for marriage with the male with the lowest age difference
                                int minDiff = 20;
                                int rowMarried = 0;
                                int[] rowSingles = new int[singleMale.size()];
                                for (Map.Entry<Integer, Integer> pair : singleMale.entrySet()) {
                                    int age = pair.getValue();
                                    if (Math.abs(ageFemale - age) < minDiff) {
                                        minDiff = Math.abs(ageFemale - age);
                                        rowMarried = pair.getKey();
                                    }
                                }
                                if (rowMarried > 0) {
                                    double threshold = (20 - minDiff) / 10;
                                    if (SiloUtil.getRandomNumberAsDouble() < threshold) {
                                        married = updateInnerMap(married, 1, minDiff, rowMarried);
                                        married = updateInnerMap(married, 2, ageFemale, rowFemale);
                                    } else {
                                        singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                    }
                                } else {
                                    singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                }

                            } else {
                                int minDiff = 20;
                                int rowMarried = 0;
                                for (Map.Entry<Integer, Integer> pair : notClassifiedMales.entrySet()) {
                                    int age = pair.getValue();
                                    if (Math.abs(ageFemale - age) < minDiff) {
                                        minDiff = Math.abs(ageFemale - age);
                                        rowMarried = pair.getKey();
                                    }
                                }
                                if (rowMarried > 0) {
                                    double threshold = (20 - minDiff) / 10;
                                    if (SiloUtil.getRandomNumberAsDouble() < threshold) {
                                        married = updateInnerMap(married, 1, minDiff, rowMarried);
                                        married = updateInnerMap(married, 2, ageFemale, rowFemale);
                                    } else {
                                        singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                        singles = updateInnerMap(singles, 1, minDiff, rowMarried);
                                    }
                                } else {
                                    singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                    for (int i = 0; i < notClassifiedMales.keySet().toArray().length; i++) {
                                        int rowMale = (int) notClassifiedMales.keySet().toArray()[i];
                                        singles = updateInnerMap(singles, 1, ageFemale, rowMale);
                                    }
                                }
                                if (notClassifiedFemales.keySet().toArray().length > 1) {
                                    for (int i = 1; i < notClassifiedFemales.keySet().toArray().length; i++) {
                                        rowFemale = (int) notClassifiedFemales.keySet().toArray()[i];
                                        singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                    }
                                }
                            }
                        }
                    } else if (countNotClassified[0] == 0 & countNotClassified[1] > 1) { //only females were not classified
                        //set all of them as single
                        for (Map.Entry<Integer, Integer> pair : notClassifiedFemales.entrySet()) {
                            int newRow = pair.getKey();
                            int newAge = pair.getValue();
                            singles = updateInnerMap(singles, 2, newAge, newRow);
                        }
                    } else if (countNotClassified[1] == 0 & countNotClassified[0] > 1) { //only males were not classified
                        // set all of them as single
                        for (Map.Entry<Integer, Integer> pair : notClassifiedMales.entrySet()) {
                            int newRow = pair.getKey();
                            int newAge = pair.getValue();
                            singles = updateInnerMap(singles, 1, newAge, newRow);
                        }
                    } else if (countNotClassified[1] > countNotClassified[0]) {  //more females are not classified
                        for (int possibleMales = 1; possibleMales <= countNotClassified[0]; possibleMales++) {
                            //check for a possible single male to get married (408 households with)
                            int rowFemale = (int) notClassifiedFemales.keySet().toArray()[possibleMales - 1];
                            int ageFemale = notClassifiedFemales.get(rowFemale);
                            HashMap<Integer, Integer> singleMale = singles.get("male");
                            if (singleMale == null & notClassifiedMales == null) { //no possible single male to marry -> set as single
                                singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                            } else if (singleMale != null) { //check for marriage with the male with the lowest age difference
                                int minDiff = 20;
                                int rowMarried = 0;
                                int[] rowSingles = new int[singleMale.size()];
                                for (Map.Entry<Integer, Integer> pair : singleMale.entrySet()) {
                                    int age = pair.getValue();
                                    if (Math.abs(ageFemale - age) < minDiff) {
                                        minDiff = Math.abs(ageFemale - age);
                                        rowMarried = pair.getKey();
                                    }
                                }
                                if (rowMarried > 0) {
                                    double threshold = (20 - minDiff) / 10;
                                    if (SiloUtil.getRandomNumberAsDouble() < threshold) {
                                        married = updateInnerMap(married, 1, minDiff, rowMarried);
                                        married = updateInnerMap(married, 2, ageFemale, rowFemale);
                                    } else {
                                        singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                    }
                                } else {
                                    singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                }

                            } else {
                                int minDiff = 20;
                                int rowMarried = 0;
                                for (Map.Entry<Integer, Integer> pair : notClassifiedMales.entrySet()) {
                                    int age = pair.getValue();
                                    if (Math.abs(ageFemale - age) < minDiff) {
                                        minDiff = Math.abs(ageFemale - age);
                                        rowMarried = pair.getKey();
                                    }
                                }
                                if (rowMarried > 0) {
                                    double threshold = (20 - minDiff) / 10;
                                    if (SiloUtil.getRandomNumberAsDouble() < threshold) {
                                        married = updateInnerMap(married, 1, minDiff, rowMarried);
                                        married = updateInnerMap(married, 2, ageFemale, rowFemale);
                                    } else {
                                        singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                        singles = updateInnerMap(singles, 1, minDiff, rowMarried);
                                    }
                                } else {
                                    singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                    for (int i = 0; i < notClassifiedMales.keySet().toArray().length; i++) {
                                        int rowMale = (int) notClassifiedMales.keySet().toArray()[i];
                                        singles = updateInnerMap(singles, 1, ageFemale, rowMale);
                                    }
                                }
                                if (notClassifiedFemales.keySet().toArray().length > 1) {
                                    for (int i = 1; i < notClassifiedFemales.keySet().toArray().length; i++) {
                                        rowFemale = (int) notClassifiedFemales.keySet().toArray()[i];
                                        singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                    }
                                }
                            }
                        }
                    } else if (countNotClassified[0] > countNotClassified[1]) { //check for a possible single female to get married (94 households)
                        for (int possibleFemales = 1; possibleFemales <= countNotClassified[1]; possibleFemales++) {
                            int rowMale = (int) notClassifiedMales.keySet().toArray()[possibleFemales -1];
                            int ageMale = notClassifiedMales.get(rowMale);
                            HashMap<Integer, Integer> singleFemale = singles.get("female");
                            if (singleFemale == null & notClassifiedFemales == null) { //no possible single female to marry -> set as single
                                singles = updateInnerMap(singles, 1, ageMale, rowMale);
                            } else if (singleFemale != null) { //check for marriage with the female with the lowest age difference
                                int minDiff = 20;
                                int rowMarried = 0;
                                for (Map.Entry<Integer, Integer> pair : singleFemale.entrySet()) {
                                    int age = pair.getValue();
                                    if (Math.abs(ageMale - age) < minDiff) {
                                        minDiff = Math.abs(ageMale - age);
                                        rowMarried = pair.getKey();
                                    }
                                }
                                if (rowMarried > 0) {
                                    double threshold = (20 - minDiff) / 10;
                                    if (SiloUtil.getRandomNumberAsDouble() < threshold) {
                                        married = updateInnerMap(married, 1, ageMale, rowMale);
                                        married = updateInnerMap(married, 2, minDiff, rowMarried);
                                    } else {
                                        singles = updateInnerMap(singles, 1, ageMale, rowMale);
                                    }
                                } else {
                                    singles = updateInnerMap(singles, 1, ageMale, rowMale);
                                }
                            } else {
                                int minDiff = 20;
                                int rowMarried = 0;
                                for (Map.Entry<Integer, Integer> pair : notClassifiedFemales.entrySet()) {
                                    int age = pair.getValue();
                                    if (Math.abs(ageMale - age) < minDiff) {
                                        minDiff = Math.abs(ageMale - age);
                                        rowMarried = pair.getKey();
                                    }
                                }
                                if (rowMarried > 0) {
                                    double threshold = (20 - minDiff) / 10;
                                    if (SiloUtil.getRandomNumberAsDouble() < threshold) {
                                        married = updateInnerMap(married, 1, ageMale, rowMale);
                                        married = updateInnerMap(married, 2, minDiff, rowMarried);
                                    } else {
                                        singles = updateInnerMap(singles, 1, ageMale, rowMale);
                                        singles = updateInnerMap(singles, 2, minDiff, rowMarried);
                                    }
                                } else {
                                    singles = updateInnerMap(singles, 1, ageMale, rowMale);
                                    for (int i = 0; i < notClassifiedFemales.keySet().toArray().length; i++) {
                                        rowMale = (int) notClassifiedFemales.keySet().toArray()[i];
                                        singles = updateInnerMap(singles, 2, ageMale, rowMale);
                                    }
                                }
                                if (notClassifiedMales.keySet().toArray().length > 1) {
                                    for (int i = 1; i < notClassifiedMales.keySet().toArray().length; i++) {
                                        rowMale = (int) notClassifiedMales.keySet().toArray()[i];
                                        singles = updateInnerMap(singles, 1, ageMale, rowMale);
                                    }
                                }
                            }
                        }
                    } else {
                        logger.info("   Case without treatment. Please check this household " + k);
                    }
                }
                setRoles(singles, married, childrenInHousehold, noClass);
                //check for only one married person in the household

            }
        }
        //double checking for persons that are not classified
        for (int i = 1; i <= dataPerson.getRowCount(); i++){
            if (dataPerson.getValueAt(i,"personRole") == -1){
                dataPerson.setValueAt(i, "personRole", 1);
            }
        }

        String hhFileName = ("microData/interimFiles/microHouseholds2.csv");
        SiloUtil.writeTableDataSet(dataHousehold, hhFileName);

        String ppFileName = ("microData/interimFiles/microPerson2.csv");
        SiloUtil.writeTableDataSet(dataPerson, ppFileName);
    }


    private void checkHouseholdRelationshipObject(){
        //method to check how household members are related

        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        for (Household hh : householdDataManager.getHouseholds()){

            int hhSize = hh.getHhSize();

            //single person household -> all set as single directly
            if (hhSize == 1){
                for (Integer key : hh.getPersons().keySet()){
                    hh.getPersons().get(key).setRole(PersonRole.SINGLE);
                }
                //multiperson household
            } else {

                //Create the maps to store the classified members of the households
                HashMap<Integer, Integer> childrenInHousehold = new HashMap<>();
                HashMap<String, HashMap<Integer, Integer>> noClass = new HashMap<>();
                HashMap<String, HashMap<Integer, Integer>> singles = new HashMap<>();
                HashMap<String, HashMap<Integer, Integer>> married = new HashMap<>();

                //direct classification of the members of the household
                int row = 0;
                for (Integer key : hh.getPersons().keySet()){
                    Person pp = hh.getPersons().get(key);
                    int age = pp.getAge();
                    int gender = pp.getGender().getCode();
                    if (pp.getRole().equals(PersonRole.CHILD)){
                        childrenInHousehold.put(key, age);
                    } else if (age < 16){
                        childrenInHousehold.put(key, age);
                    } else {
                        noClass = updateInnerMap(noClass, gender, age, key);
                    }
                }
                int[] countNotClassified = new int[2];
                int checkForCohabitation = 1;
                HashMap<Integer, Integer> notClassifiedMales = noClass.get("male");
                if (notClassifiedMales != null){
                    countNotClassified[0] = notClassifiedMales.size();
                    checkForCohabitation = 1;
                } else {
                    countNotClassified[0] = 0;
                }
                HashMap<Integer, Integer> notClassifiedFemales = noClass.get("female");
                if (notClassifiedFemales != null){
                    countNotClassified[1] = notClassifiedFemales.size();
                    checkForCohabitation = 1;
                } else {
                    countNotClassified[1] = 0;
                }
                if (checkForCohabitation == 1) {
                    //look for cohabitation
                    if (countNotClassified[0] == countNotClassified[1]) { //one male and one female were not classified
                        for (int possibleMales = 1; possibleMales <= countNotClassified[0]; possibleMales++) {
                            //check for a possible single male to get married (408 households with)
                            int rowFemale = 0;
                            if (noClass.get("female").size() > possibleMales - 1) {
                                rowFemale = (int) notClassifiedFemales.keySet().toArray()[possibleMales - 1];
                            } else {
                                rowFemale = (int) notClassifiedFemales.keySet().toArray()[0];
                            }
                            int ageFemale = notClassifiedFemales.get(rowFemale);
                            HashMap<Integer, Integer> singleMale = singles.get("male");
                            if (singleMale == null & notClassifiedMales == null) { //no possible single male to marry -> set as single
                                singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                            } else if (singleMale != null) { //check for marriage with the male with the lowest age difference
                                int minDiff = 20;
                                int rowMarried = 0;
                                int[] rowSingles = new int[singleMale.size()];
                                for (Map.Entry<Integer, Integer> pair : singleMale.entrySet()) {
                                    int age = pair.getValue();
                                    if (Math.abs(ageFemale - age) < minDiff) {
                                        minDiff = Math.abs(ageFemale - age);
                                        rowMarried = pair.getKey();
                                    }
                                }
                                if (rowMarried > 0) {
                                    double threshold = (20 - minDiff) / 10;
                                    if (SiloUtil.getRandomNumberAsDouble() < threshold) {
                                        married = updateInnerMap(married, 1, minDiff, rowMarried);
                                        married = updateInnerMap(married, 2, ageFemale, rowFemale);
                                        noClass.get("male").remove(rowMarried);
                                        noClass.get("female").remove(rowFemale);
                                    } else {
                                        singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                        noClass.get("female").remove(rowFemale);
                                    }
                                } else {
                                    singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                    noClass.get("female").remove(rowFemale);
                                }

                            } else {
                                int minDiff = 20;
                                int rowMarried = 0;
                                for (Map.Entry<Integer, Integer> pair : notClassifiedMales.entrySet()) {
                                    int age = pair.getValue();
                                    if (Math.abs(ageFemale - age) < minDiff) {
                                        minDiff = Math.abs(ageFemale - age);
                                        rowMarried = pair.getKey();
                                    }
                                }
                                if (rowMarried > 0) {
                                    double threshold = (20 - minDiff) / 10;
                                    if (SiloUtil.getRandomNumberAsDouble() < threshold) {
                                        married = updateInnerMap(married, 1, minDiff, rowMarried);
                                        married = updateInnerMap(married, 2, ageFemale, rowFemale);
                                        noClass.get("male").remove(rowMarried);
                                        noClass.get("female").remove(rowFemale);
                                    } else {
                                        singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                        noClass.get("female").remove(rowFemale);
                                    }
                                } else {
                                    singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                    noClass.get("female").remove(rowFemale);
/*                                    for (int i = 0; i < notClassifiedMales.keySet().toArray().length; i++) {
                                        int rowMale = (int) notClassifiedMales.keySet().toArray()[i];
                                        singles = updateInnerMap(singles, 1, ageFemale, rowMale);
                                    }*/
                                }
/*                                if (notClassifiedFemales.keySet().toArray().length > 1) {
                                    for (int i = 1; i < notClassifiedFemales.keySet().toArray().length; i++) {
                                        rowFemale = (int) notClassifiedFemales.keySet().toArray()[i];
                                        singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                    }
                                }*/

                            }
                        }
                    } else if (countNotClassified[0] == 0 & countNotClassified[1] > 1) { //only females were not classified
                        //set all of them as single
                        for (Map.Entry<Integer, Integer> pair : notClassifiedFemales.entrySet()) {
                            int newRow = pair.getKey();
                            int newAge = pair.getValue();
                            singles = updateInnerMap(singles, 2, newAge, newRow);
                        }
                    } else if (countNotClassified[1] == 0 & countNotClassified[0] > 1) { //only males were not classified
                        // set all of them as single
                        for (Map.Entry<Integer, Integer> pair : notClassifiedMales.entrySet()) {
                            int newRow = pair.getKey();
                            int newAge = pair.getValue();
                            singles = updateInnerMap(singles, 1, newAge, newRow);
                        }
                    } else if (countNotClassified[1] > countNotClassified[0]) {  //more females are not classified
                        for (int possibleMales = 1; possibleMales <= countNotClassified[0]; possibleMales++) {
                            //check for a possible single male to get married (408 households with)
                            int rowFemale = 0;
                            if (noClass.get("female").size() > possibleMales - 1) {
                                rowFemale = (int) notClassifiedFemales.keySet().toArray()[possibleMales - 1];
                            } else {
                                rowFemale = (int) notClassifiedFemales.keySet().toArray()[0];
                            }
                            int ageFemale = notClassifiedFemales.get(rowFemale);
                            HashMap<Integer, Integer> singleMale = singles.get("male");
                            if (singleMale == null & notClassifiedMales == null) { //no possible single male to marry -> set as single
                                singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                noClass.get("female").remove(rowFemale);
                            } else if (singleMale != null) { //check for marriage with the male with the lowest age difference
                                int minDiff = 20;
                                int rowMarried = 0;
                                int[] rowSingles = new int[singleMale.size()];
                                for (Map.Entry<Integer, Integer> pair : singleMale.entrySet()) {
                                    int age = pair.getValue();
                                    if (Math.abs(ageFemale - age) < minDiff) {
                                        minDiff = Math.abs(ageFemale - age);
                                        rowMarried = pair.getKey();
                                    }
                                }
                                if (rowMarried > 0) {
                                    double threshold = (20 - minDiff) / 10;
                                    if (SiloUtil.getRandomNumberAsDouble() < threshold) {
                                        married = updateInnerMap(married, 1, minDiff, rowMarried);
                                        married = updateInnerMap(married, 2, ageFemale, rowFemale);
                                        noClass.get("male").remove(rowMarried);
                                        noClass.get("female").remove(rowFemale);
                                    } else {
                                        singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                        noClass.get("female").remove(rowFemale);
                                    }
                                } else {
                                    singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                    noClass.get("female").remove(rowFemale);
                                }

                            } else {
                                int minDiff = 20;
                                int rowMarried = 0;
                                for (Map.Entry<Integer, Integer> pair : notClassifiedMales.entrySet()) {
                                    int age = pair.getValue();
                                    if (Math.abs(ageFemale - age) < minDiff) {
                                        minDiff = Math.abs(ageFemale - age);
                                        rowMarried = pair.getKey();
                                    }
                                }
                                if (rowMarried > 0) {
                                    double threshold = (20 - minDiff) / 10;
                                    if (SiloUtil.getRandomNumberAsDouble() < threshold) {
                                        married = updateInnerMap(married, 1, minDiff, rowMarried);
                                        married = updateInnerMap(married, 2, ageFemale, rowFemale);
                                        noClass.get("male").remove(rowMarried);
                                        noClass.get("female").remove(rowFemale);
                                    } else {
                                        singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                        noClass.get("female").remove(rowFemale);
                                    }
                                } else {
                                    singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                    noClass.get("female").remove(rowFemale);
/*                                    for (int i = 0; i < notClassifiedMales.keySet().toArray().length; i++) {
                                        int rowMale = (int) notClassifiedMales.keySet().toArray()[i];
                                        singles = updateInnerMap(singles, 1, ageFemale, rowMale);
                                    }*/
                                }
/*                                if (notClassifiedFemales.keySet().toArray().length > 1) {
                                    for (int i = 1; i < notClassifiedFemales.keySet().toArray().length; i++) {
                                        rowFemale = (int) notClassifiedFemales.keySet().toArray()[i];
                                        singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                    }
                                }*/
                            }
                        }
                    } else if (countNotClassified[0] > countNotClassified[1]) { //check for a possible single female to get married (94 households)
                        for (int possibleFemales = 1; possibleFemales <= countNotClassified[1]; possibleFemales++) {
                            int rowMale = 0;
                            if (noClass.get("male").size() > possibleFemales - 1) {
                                rowMale = (int) notClassifiedMales.keySet().toArray()[possibleFemales - 1];
                            } else {
                                rowMale = (int) notClassifiedMales.keySet().toArray()[0];
                            }
                            int ageMale = notClassifiedMales.get(rowMale);
                            HashMap<Integer, Integer> singleFemale = singles.get("female");
                            if (singleFemale == null & notClassifiedFemales == null) { //no possible single female to marry -> set as single
                                singles = updateInnerMap(singles, 1, ageMale, rowMale);
                            } else if (singleFemale != null) { //check for marriage with the female with the lowest age difference
                                int minDiff = 20;
                                int rowMarried = 0;
                                for (Map.Entry<Integer, Integer> pair : singleFemale.entrySet()) {
                                    int age = pair.getValue();
                                    if (Math.abs(ageMale - age) < minDiff) {
                                        minDiff = Math.abs(ageMale - age);
                                        rowMarried = pair.getKey();
                                    }
                                }
                                if (rowMarried > 0) {
                                    double threshold = (20 - minDiff) / 10;
                                    if (SiloUtil.getRandomNumberAsDouble() < threshold) {
                                        married = updateInnerMap(married, 1, ageMale, rowMale);
                                        married = updateInnerMap(married, 2, minDiff, rowMarried);
                                        noClass.get("male").remove(rowMale);
                                        noClass.get("female").remove(rowMarried);
                                    } else {
                                        singles = updateInnerMap(singles, 1, ageMale, rowMale);
                                    }
                                } else {
                                    singles = updateInnerMap(singles, 1, ageMale, rowMale);
                                }
                            } else {
                                int minDiff = 20;
                                int rowMarried = 0;
                                for (Map.Entry<Integer, Integer> pair : notClassifiedFemales.entrySet()) {
                                    int age = pair.getValue();
                                    if (Math.abs(ageMale - age) < minDiff) {
                                        minDiff = Math.abs(ageMale - age);
                                        rowMarried = pair.getKey();
                                    }
                                }
                                if (rowMarried > 0) {
                                    double threshold = (20 - minDiff) / 10;
                                    if (SiloUtil.getRandomNumberAsDouble() < threshold) {
                                        married = updateInnerMap(married, 1, ageMale, rowMale);
                                        married = updateInnerMap(married, 2, minDiff, rowMarried);
                                        noClass.get("male").remove(rowMale);
                                        noClass.get("female").remove(rowMarried);
                                    } else {
                                        singles = updateInnerMap(singles, 1, ageMale, rowMale);
                                        noClass.get("male").remove(rowMale);
                                    }
                                } else {
                                    singles = updateInnerMap(singles, 1, ageMale, rowMale);
                                    noClass.get("male").remove(rowMale);
/*                                    for (int i = 0; i < notClassifiedFemales.keySet().toArray().length; i++) {
                                        rowMale = (int) notClassifiedFemales.keySet().toArray()[i];
                                        singles = updateInnerMap(singles, 2, ageMale, rowMale);
                                    }*/
                                }
/*                                if (notClassifiedMales.keySet().toArray().length > 1) {
                                    for (int i = 1; i < notClassifiedMales.keySet().toArray().length; i++) {
                                        rowMale = (int) notClassifiedMales.keySet().toArray()[i];
                                        singles = updateInnerMap(singles, 1, ageMale, rowMale);
                                    }
                                }*/
                            }
                        }
                    } else {
                        logger.info("   Case without treatment. Please check this household " + hh.getId());
                    }
                }
                setRolesObject(singles, married, childrenInHousehold, noClass, hh);
            }
        }

    }

    private void runIPUbyCityAndCounty(){
        //IPU process for independent municipalities (only household attributes)
        logger.info("   Starting to prepare the data for IPU");


        //Read the frequency matrix
        int[] microDataIds = frequencyMatrix.getColumnAsInt("ID");
        frequencyMatrix.buildIndex(frequencyMatrix.getColumnPosition("ID"));


        //Create the weights table (for all the municipalities)
        TableDataSet weightsMatrix = new TableDataSet();
        weightsMatrix.appendColumn(microDataIds,"ID");

        //Create errors by county
        TableDataSet errorsCounty = new TableDataSet();
        TableDataSet errorsMunicipality = new TableDataSet();
        TableDataSet errorsSummary = new TableDataSet();
        String[] labels = new String[]{"error", "iterations","time"};
        errorsCounty = initializeErrors(errorsCounty, attributesCounty, countyID);
        errorsMunicipality = initializeErrors(errorsMunicipality, attributesMunicipality, cityID);
        errorsSummary = initializeErrors(errorsSummary, labels, countyID);

        //For each county---------------------------------------------

        for (int county : counties) {

            long startTime = System.nanoTime();
            municipalities = municipalitiesByCounty.get(county);

            //weights, values, control totals
            Map<Integer, double[]> weightsByMun = Collections.synchronizedMap(new HashMap<>());
            Map<Integer, double[]> minWeightsByMun = Collections.synchronizedMap(new HashMap<>());
            Map<String, int[]> valuesByHousehold = Collections.synchronizedMap(new HashMap<>());
            Map<String, Integer> totalCounty = Collections.synchronizedMap(new HashMap<>());
            Map<Integer, Map<String, Integer>> totalMunicipality = Collections.synchronizedMap(new HashMap<>());
            Map<Integer, Map<String, Double>> errorByMun = Collections.synchronizedMap(new HashMap<>());
            Map<String, Double> errorByRegion = Collections.synchronizedMap(new HashMap<>());
            double weightedSum0 = 0f;

            //parameters of the IPU
            int finish = 0;
            int iteration = 0;
            double maxError = 0.00001;
            int maxIterations = 500;
            double minError = 100000;
            double initialError = 10000;
            double improvementError = 0.001;
            double iterationError = 2;
            double increaseError = 1.05;

            //initialize errors, considering the first weight (equal to 1)
            for (String attribute : attributesCounty) {
                int[] values = new int[frequencyMatrix.getRowCount()];
                for (int i = 1; i <= frequencyMatrix.getRowCount(); i++) {
                    values[i - 1] = (int) frequencyMatrix.getValueAt(i, attribute);
                    if (attribute.equals(attributesCounty[0])) {
                        weightedSum0 = weightedSum0 + values[i - 1] * municipalities.size();
                    }
                }
                valuesByHousehold.put(attribute, values);
                int total = (int) marginalsCounty.getIndexedValueAt(county, attribute);
                totalCounty.put(attribute, total);
                errorByRegion.put(attribute, 0.);
            }
            for (String attribute : attributesMunicipality) {
                int[] values = new int[frequencyMatrix.getRowCount()];
                for (int i = 1; i <= frequencyMatrix.getRowCount(); i++) {
                    values[i - 1] = (int) frequencyMatrix.getValueAt(i, attribute);
                }
                valuesByHousehold.put(attribute, values);
                Iterator<Integer> iterator = municipalities.iterator();
                while (iterator.hasNext()) {
                    Integer municipality = iterator.next();
                    double[] dummy = SiloUtil.createArrayWithValue(frequencyMatrix.getRowCount(), 1.);
                    weightsByMun.put(municipality, dummy);
                    double[] dummy1 = SiloUtil.createArrayWithValue(frequencyMatrix.getRowCount(), 1.);
                    minWeightsByMun.put(municipality, dummy1);
                    if (totalMunicipality.containsKey(municipality)) {
                        Map<String, Integer> innerMap = totalMunicipality.get(municipality);
                        innerMap.put(attribute, (int) marginalsMunicipality.getIndexedValueAt(municipality, attribute));
                        totalMunicipality.put(municipality, innerMap);
                        Map<String, Double> inner1 = errorByMun.get(municipality);
                        inner1.put(attribute, 0.);
                        errorByMun.put(municipality, inner1);
                    } else {
                        HashMap<String, Integer> inner = new HashMap<>();
                        inner.put(attribute, (int) marginalsMunicipality.getIndexedValueAt(municipality, attribute));
                        totalMunicipality.put(municipality, inner);
                        HashMap<String, Double> inner1 = new HashMap<>();
                        inner1.put(attribute, 0.);
                        errorByMun.put(municipality, inner1);
                    }
                }
            }

            //for each iteration
            while (finish == 0 & iteration < maxIterations) {

                //For each municipality, obtain the weight matching each attribute
                ConcurrentExecutor executor = ConcurrentExecutor.cachedService();
                Iterator<Integer> iterator = municipalities.iterator();
                while (iterator.hasNext()) {
                    Integer municipality = iterator.next();
                    executor.addTaskToQueue(() -> {
                        for (String attribute : attributesMunicipality) {
                            double weightedSumMunicipality = SiloUtil.sumProduct(weightsByMun.get(municipality), valuesByHousehold.get(attribute));
                            if (weightedSumMunicipality > 0.001) {
                                double updatingFactor = totalMunicipality.get(municipality).get(attribute) / weightedSumMunicipality;
                                double[] previousWeights = weightsByMun.get(municipality);
                                int[] values = valuesByHousehold.get(attribute);
                                double[] updatedWeights = new double[previousWeights.length];
                                IntStream.range(0, previousWeights.length).parallel().forEach(id -> updatedWeights[id] = multiplyIfNotZero(previousWeights[id], values[id], updatingFactor));
                                weightsByMun.put(municipality, updatedWeights);
                            }
                        }
                        return null;
                    });
                }
                executor.execute();


                //For each attribute at the region level (landkreise), we obtain the weights
                double weightedSumRegion = 0;
                for (String attribute : attributesCounty) {
                    Iterator<Integer> iterator1 = municipalities.iterator();
                    while (iterator1.hasNext()) {
                        Integer municipality = iterator1.next();
                        weightedSumRegion = weightedSumRegion + SiloUtil.sumProduct(weightsByMun.get(municipality), valuesByHousehold.get(attribute));
                    }
                    if (weightedSumRegion > 0.001) {
                        double updatingFactor = totalCounty.get(attribute) / weightedSumRegion;
                        Iterator<Integer> iterator2 = municipalities.iterator();
                        while (iterator2.hasNext()) {
                            Integer municipality = iterator2.next();
                            double[] previousWeights = weightsByMun.get(municipality);
                            int[] values = valuesByHousehold.get(attribute);
                            double[] updatedWeights = new double[previousWeights.length];
                            IntStream.range(0, previousWeights.length).parallel().forEach(id -> updatedWeights[id] = multiplyIfNotZero(previousWeights[id], values[id], updatingFactor));
                            weightsByMun.put(municipality, updatedWeights);
                        }
                    }
                    //logger.info("Attribute " + attribute + ": sum is " + weightedSumRegion);
                    weightedSumRegion = 0;
                }


                //obtain the errors by municipality
                double averageErrorIteration = 0.;
                int counter = 0;
                ConcurrentExecutor executor1 = ConcurrentExecutor.cachedService();
                Iterator<Integer> iterator1 = municipalities.iterator();
                while (iterator1.hasNext()){
                    Integer municipality = iterator1.next();
                    Map<String, Double> errorsByMunicipality = Collections.synchronizedMap(new HashMap<>());
                    executor1.addTaskToQueue(() ->{
                        for (String attribute : attributesMunicipality){
                            double weightedSumMunicipality = SiloUtil.sumProduct(weightsByMun.get(municipality), valuesByHousehold.get(attribute));
                            double errorByAttributeAndMunicipality = 0;
                            if (totalMunicipality.get(municipality).get(attribute) > 0){
                                errorByAttributeAndMunicipality = Math.abs((weightedSumMunicipality - totalMunicipality.get(municipality).get(attribute)) / totalMunicipality.get(municipality).get(attribute));
                                errorsByMunicipality.put(attribute, errorByAttributeAndMunicipality);
                            }
                        }
                        return null;
                    });
                    errorByMun.put(municipality, errorsByMunicipality);
                }
                executor1.execute();
                for (int municipality : municipalities) {
                    averageErrorIteration = averageErrorIteration + errorByMun.get(municipality).values().stream().mapToDouble(Number::doubleValue).sum();
                    counter = counter + errorByMun.get(municipality).entrySet().size();
                }

                //obtain errors by county
                for (String attributeC : attributesCounty) {
                    double errorByCounty = 0.;
                    double weightedSumCounty = 0.;
                    if (totalCounty.get(attributeC) > 0) {
                        Iterator<Integer> iterator3 = municipalities.iterator();
                        while (iterator3.hasNext()) {
                            Integer municipality = iterator3.next();
                            double weightedSum = SiloUtil.sumProduct(weightsByMun.get(municipality), valuesByHousehold.get(attributeC));
                            weightedSumCounty += weightedSum;
                        }
                        errorByCounty = errorByCounty + Math.abs((weightedSumCounty - totalCounty.get(attributeC)) / totalCounty.get(attributeC));
                        errorByRegion.put(attributeC, errorByCounty);
                        averageErrorIteration += errorByCounty;
                        counter++;
                    }
                }

                averageErrorIteration = averageErrorIteration / counter;
                logger.info("   County " + county + ". Iteration " + iteration + ". Average error: " + averageErrorIteration * 100 + " %.");

                //Stopping criteria: exceeds the maximum number of iterations or the maximum error is lower than the threshold
                if (averageErrorIteration < maxError) {
                    finish = 1;
                    logger.info("   IPU finished after :" + iteration + " iterations with a minimum average error of: " + minError * 100 + " %.");
                    iteration = maxIterations + 1;
                } else if ((iteration / iterationError) % 1 == 0) {
                    if (Math.abs((initialError - averageErrorIteration) / initialError) < improvementError) {
                        finish = 1;
                        logger.info("   IPU finished after " + iteration + " iterations because the error does not improve. The minimum average error is: " + minError * 100 + " %.");
                    } else if (averageErrorIteration == 0) {
                        finish = 1;
                        logger.info("   IPU finished after " + iteration + " iterations because the error starts increasing. The minimum average error is: " + minError * 100 + " %.");
                    } else {
                        initialError = averageErrorIteration;
                        iteration = iteration + 1;
                    }
                } else if (iteration == maxIterations) {
                    finish = 1;
                    logger.info("   IPU finished after the total number of iterations. The minimum average error is: " + minError * 100 + " %.");
                } else {
                    iteration = iteration + 1;
                }

                if (averageErrorIteration < minError) {
                    for (int municipality : municipalities) {
                        double[] minW = weightsByMun.get(municipality);
                        minWeightsByMun.put(municipality, minW);
                    }
                    minError = averageErrorIteration;
                }
                long estimatedTime = (System.nanoTime() - startTime) / 1000000000;
                errorsSummary.setIndexedValueAt(county, "error", (float) minError);
                errorsSummary.setIndexedValueAt(county, "iterations", iteration);
                errorsSummary.setIndexedValueAt(county, "time", estimatedTime);
            }


            //Write the weights after finishing IPU for each municipality (saved each time over the previous version)
            for (int municipality : municipalities) {
                weightsMatrix.appendColumn(minWeightsByMun.get(municipality), Integer.toString(municipality));
            }

            SiloUtil.writeTableDataSet(weightsMatrix, "input/syntheticPopulation/weights1.csv");


            //Copy the errors per attribute
            for (String attribute : attributesCounty) {
                errorsCounty.setIndexedValueAt(county, attribute, errorByRegion.get(attribute).floatValue());
            }
            for (int municipality : municipalities) {
                for (String attribute : attributesMunicipality) {
                    if (totalMunicipality.get(municipality).get(attribute) > 0){
                        errorsMunicipality.setIndexedValueAt(municipality, attribute, errorByMun.get(municipality).get(attribute).floatValue());
                    }
                }
            }


            //Write the weights after finishing IPU for each county
            SiloUtil.writeTableDataSet(weightsMatrix, rb.getString(PROPERTIES_WEIGHTS_MATRIX));
            SiloUtil.writeTableDataSet(errorsMunicipality, "microData/interimFiles/errorsHouseholdIPU.csv");
            SiloUtil.writeTableDataSet(errorsCounty, "microData/interimFiles/errorsRegionIPU.csv");
            SiloUtil.writeTableDataSet(errorsSummary, "microData/interimFiles/summaryIPU.csv");
        }
        //Write the weights final table
        weightsTable = weightsMatrix;
        weightsTable.buildIndex(weightsTable.getColumnPosition("ID"));

        logger.info("   IPU finished");
    }

    private TableDataSet initializeErrors(TableDataSet errors, String[] attributes, int[] ids) {
        //method to initialize the error matrix
        errors.appendColumn(ids, "ID");
        for (String attribute : attributes){
            float[] dummy = SiloUtil.createArrayWithValue(errors.getRowCount(), 0f);
            errors.appendColumn(dummy, attribute);
        }
        errors.buildIndex(errors.getColumnPosition("ID"));
        return errors;
    }


    private void readIPU(){
        //Read entry data for household selection
        logger.info("   Reading the weights matrix");
        weightsTable = SiloUtil.readCSVfile2(rb.getString(PROPERTIES_WEIGHTS_MATRIX));
        weightsTable.buildIndex(weightsTable.getColumnPosition("ID"));

        logger.info("   Finishing reading the results from the IPU");
    }


    private void generateJobs(){
        //Generate jobs file. The worker ID will be assigned later on the process "assignJobs"

        logger.info("   Starting to generate jobs");

        String[] jobStringType = ResourceUtil.getArray(rb, PROPERTIES_JOB_TYPES);
        JobDataManager jobDataManager = dataContainer.getJobDataManager();

        //For each municipality
        for (int municipality = 0; municipality < cityID.length; municipality++) {
            //logger.info("   Municipality " + cityID[municipality] + ". Starting to generate jobs.");

            //-----------***** Data preparation *****-------------------------------------------------------------------
            //Create local variables to avoid accessing to the same variable on the parallel processing
            int municipalityID = cityID[municipality];
            TableDataSet rasterCellsMatrix = cellsMatrix;
            rasterCellsMatrix.buildIndex(rasterCellsMatrix.getColumnPosition("ID_cell"));


            //obtain the raster cells of the municipality and their weight within the municipality
            int[] tazInCity = cityTAZ.get(municipalityID);


            //generate jobs
            for (int row = 0; row < jobStringType.length; row++) {
                String jobType = jobStringType[row];
                //Obtain the number of jobs of that type in each TAZ of the municipality
                for (int i : tazInCity) {
                    int totalJobs = (int) cellsMatrix.getIndexedValueAt(i, jobType);
                    for (int job = 0; job < totalJobs; job++) {
                        int id = jobDataManager.getNextJobId();
                        jobDataManager.addJob(JobUtils.getFactory().createJob(id, i, null, -1, jobType)); //(int id, int zone, int workerId, String type)
                    }
                }
            }
        }
    }


    private void assignJobs(){
        //Method to allocate workers at workplaces
        //todo. Things to consider:
        //If there are no more workplaces of the specific job type, the worker is sent outside the area (workplace = -2; distance = 1000 km)
        //Workers that also attend school are considered only as workers (educational place is not selected for them)

        //Calculate distance impedance
        distanceImpedance = new Matrix(distanceMatrix.getRowCount(), distanceMatrix.getColumnCount());
        for (int i = 1; i <= distanceMatrix.getRowCount(); i ++){
            for (int j = 1; j <= distanceMatrix.getColumnCount(); j++){
                int distance = Math.round(distanceMatrix.getValueAt(i, j));
                distanceImpedance.setValueAt(i,j,tripLengthFrequencyDistribution.get(distance));
            }
        }


        //Identify vacant jobs and schools by zone and type
        identifyVacantJobsByZoneType();


        //Produce one array list with workers' ID
        Collection<Person> persons = dataContainer.getHouseholdDataManager().getPersons();
        ArrayList<Person> workerArrayList = new ArrayList<>();
        for (Person person: persons ){
            if (person.getOccupation() == Occupation.EMPLOYED){
                workerArrayList.add(person);
            }
        }
        //Randomize the order of the worker array list
        Collections.shuffle(workerArrayList);


        HashMap<Integer, Float> personDistance = new HashMap<>();
        HashMap<Integer, Integer> personHome = new HashMap<>();

        //Start the selection of the jobs in random order to avoid geographical bias
        logger.info("   Started assigning workplaces");
        int assignedJobs = 0;
        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        JobDataManager jobDataManager = dataContainer.getJobDataManager();
        for (Person pp : workerArrayList){

            //Select the zones with vacant jobs for that person, given the job type
            int selectedJobType = 0; //no job types to consider allocation

            int[] keys = idZonesVacantJobsByType.get(selectedJobType);
            int lengthKeys = numberZonesByType.get(selectedJobType);
            // if there are still TAZ with vacant jobs in the region, select one of them. If not, assign them outside the area
            if (lengthKeys > 0) {

                //Select the workplace location (TAZ) for that person given his/her job type
                Household hh = pp.getHousehold();
                int origin = realEstate.getDwelling(hh.getDwellingId()).getZoneId();
                int[] workplace = selectWorkplace(origin, numberVacantJobsByZoneByType, keys, lengthKeys,
                        distanceImpedance);

                //Assign last vacant jobID from the TAZ
                int jobID = idVacantJobsByZoneType.get(workplace[0])[numberVacantJobsByZoneByType.get(workplace[0]) - 1];

                //Assign values to job and person
                jobDataManager.getJobFromId(jobID).setWorkerID(pp.getId());
                int destination = jobDataManager.getJobFromId(jobID).getZoneId();
                pp.setWorkplace(jobID);
                float distance = distanceMatrix.getValueAt(origin, destination);
                personDistance.put(pp.getId(), distance);
                personHome.put(pp.getId(), origin);


                //Update counts of vacant jobs
                numberVacantJobsByZoneByType.put(workplace[0], numberVacantJobsByZoneByType.get(workplace[0]) - 1);
                numberVacantJobsByType.put(selectedJobType, numberVacantJobsByType.get(selectedJobType) - 1);
                if (numberVacantJobsByZoneByType.get(workplace[0]) < 1) {
                    keys[workplace[1]] = keys[numberZonesByType.get(selectedJobType) - 1];
                    idZonesVacantJobsByType.put(selectedJobType, keys);
                    numberZonesByType.put(selectedJobType, numberZonesByType.get(selectedJobType) - 1);
                    if (numberZonesByType.get(selectedJobType) < 1) {
                        int w = 0;
                        while (w < jobStringTypes.length & selectedJobType > jobIntTypes.get(jobStringTypes[w])) {
                            w++;
                        }
                        jobIntTypes.remove(jobStringTypes[w]);
                        jobStringTypes[w] = jobStringTypes[jobStringTypes.length - 1];
                        jobStringTypes = SiloUtil.removeOneElementFromZeroBasedArray(jobStringTypes, jobStringTypes.length - 1);

                    }
                }
                //logger.info("   Job " + assignedJobs + " assigned at " + workplace[0]);
                assignedJobs++;

            } else { //No more vacant jobs in the study area. This person will work outside the study area
                pp.setWorkplace(-2);
                //pp.setTravelTime(1000);
                logger.info("   No more jobs available of " + selectedJobType + " class. Person " + pp.getId() + " has workplace outside the study area.");
            }
        }


        logger.info("   Starting to summarize commute trips");

        try {

            PrintWriter pw = new PrintWriter(new FileWriter("microData/interimFiles/workersCommute3.csv", true));
            pw.println("person,homeLocation,tripLength");

            for (int i : personDistance.keySet()) {
                pw.println(i  + "," + personHome.get(i) + "," + personDistance.get(i));
            }
            pw.flush();
            pw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("   Finishing to summarize commute trips");

    }



    private void generateHouseholdsPersonsDwellings(){
        //Generate the synthetic population using Monte Carlo (select the households according to the weight)
        //Once the household is selected, all the characteristics of the household will be copied (including the household members)
        logger.info("   Starting to generate households and persons.");


        //List of households of the micro data
        int[] microDataIds = frequencyMatrix.getColumnAsInt("ID");
        int previousHouseholds = 0;
        int previousPersons = 0;

        generateCountersForValidation();
        occupiedDwellingsByZone = new HashMap<>();

        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        //Selection of households, persons, jobs and dwellings per municipality

        HouseholdFactory householdFactory = householdDataManager.getHouseholdFactory();
        for (int municipality : municipalities){
            logger.info("   Municipality " + municipality + ". Starting to generate households.");

            int totalHouseholds = (int) marginalsMunicipality.getIndexedValueAt(municipality,"hhTotal");
            double[] probability = weightsTable.getColumnAsDouble(Integer.toString(municipality));

            //obtain the raster cells of the municipality and their weight within the municipality
            int[] tazInCity = cityTAZ.get(municipality);
            double[] probTaz = new double[tazInCity.length];
            double tazRemaining = 0;
            for (int i = 0; i < tazInCity.length; i++){
                probTaz[i] = cellsMatrix.getIndexedValueAt(tazInCity[i],"population");
                tazRemaining = tazRemaining + probTaz[i];
            }


            double hhRemaining = 0;
            double[] probabilityPrivate = new double[probability.length]; // Separate private households and group quarters for generation
            for (int row = 0; row < probability.length; row++){
                probabilityPrivate[row] = probability[row];
                hhRemaining = hhRemaining + probability[row];
            }

            //marginals for the municipality
            int hhPersons = 0;
            int hhTotal = 0;
            int id = 0;


            //for all the households that are inside the municipality (we will match perfectly the number of households. The total population will vary compared to the marginals.)
            for (int row = 0; row < totalHouseholds; row++) {

                //select the household to copy from the micro data(with replacement)
                int[] records = select(probabilityPrivate, microDataIds, hhRemaining);
                int hhIdMD = records[0];
                int hhRowMD = records[1];
                if (probabilityPrivate[hhRowMD] > 1.0) {
                    probabilityPrivate[hhRowMD] = probabilityPrivate[hhRowMD] - 1;
                    hhRemaining = hhRemaining - 1;
                } else {
                    hhRemaining = hhRemaining - probabilityPrivate[hhRowMD];
                    probabilityPrivate[hhRowMD] = 0;
                }


                //Select the taz to allocate the household (without replacement)
                int[] recordsCell = select(probTaz, tazInCity, tazRemaining);
                int taz = recordsCell[0];

                //copy the private household characteristics
                int householdSize = (int) dataHousehold.getIndexedValueAt(hhIdMD, "DERH_HSIZE1");
                int householdAutos = microDataManager.translateCars(dataHousehold.getIndexedStringValueAt(hhIdMD, "H13_MOTORCAR"));
                id = householdDataManager.getNextHouseholdId();
                Household household = householdFactory.createHousehold(id, id, householdAutos); //(int id, int dwellingID, int homeZone, int hhSize, int autos)
                householdDataManager.addHousehold(household);
                hhTotal++;
                counterMunicipality = updateCountersHousehold(household, counterMunicipality, municipality);


                //copy the household members characteristics
                int hhIncome = 0;
                PersonFactoryCapeTown factory = (PersonFactoryCapeTown) householdDataManager.getPersonFactory();
                for (int rowPerson = 0; rowPerson < householdSize; rowPerson++) {
                    int idPerson = householdDataManager.getNextPersonId();
                    int personCounter = (int) dataHousehold.getIndexedValueAt(hhIdMD, "personCount") + rowPerson;
                    int age = (int)dataPerson.getValueAt(personCounter, "f02_age");
                    Gender gender = microDataManager.translateGender((dataPerson.getStringValueAt(personCounter, "f03_sex")));
                    Occupation occupation = microDataManager.translateOccupation(dataPerson.getStringValueAt(personCounter, "derp_employ_status"),
                            dataPerson.getStringValueAt(personCounter, "p17_schoolattend"));
                    int income = microDataManager.translateIncome(dataPerson.getStringValueAt(personCounter, "p16_income"));
                    hhIncome = hhIncome + income;
                    RaceCapeTown raceStr = microDataManager.translateRace(dataPerson.getStringValueAt(personCounter,"p05_pop_group"));
                    PersonRole ppRole = microDataManager.translateRole((int) dataPerson.getValueAt(personCounter, "personRole"), age);
                    PersonCapeTown pers = factory.createPerson(idPerson, age, gender, occupation, ppRole,0, income);
                    householdDataManager.addPerson(pers);
                    householdDataManager.addPersonToHousehold(pers, household);
                    //pers.setEducationLevel((int) microDataPerson.getValueAt(personCounter, "p20_edulevel"));
                    pers.setRace(raceStr);
                    pers.setDriverLicense(false);
                    hhPersons++;
                    counterMunicipality = updateCountersPerson(pers, counterMunicipality, municipality,ageBracketsPerson);
                }


                //Copy the dwelling of that household
                int bedRooms = (int) dataHousehold.getIndexedValueAt(hhIdMD, "H03_TOTROOMS");
                int quality = microDataManager.guessQuality(dataHousehold.getIndexedStringValueAt(hhIdMD,"H07_WATERPIPED"),
                        dataHousehold.getIndexedStringValueAt(hhIdMD,"H10_TOILET"), numberofQualityLevels);
                CapeTownDwellingTypes.DwellingTypeCapeTown type = microDataManager.translateDwellingType(dataHousehold.getIndexedStringValueAt(hhIdMD,"H02_MAINDWELLING"));
                int price = microDataManager.guessDwellingPrice(hhIncome);
                int newDdId = realEstate.getNextDwellingId();
                Dwelling dwell = DwellingUtils.getFactory().createDwelling(newDdId, taz, null, id, type , bedRooms, quality, price, 0); //newDwellingId, raster cell, HH Id, ddType, bedRooms, quality, price, restriction, construction year
                realEstate.addDwelling(dwell);
                dwell.setUsage(microDataManager.translateDwellingUsage(dataHousehold.getIndexedStringValueAt(hhIdMD,"H04_TENURE")));
                if (occupiedDwellingsByZone.containsKey(taz)) {
                    occupiedDwellingsByZone.get(taz).put(dwell.getId(), dwell);
                } else {
                    Map<Integer, Dwelling> dd = new HashMap<>();
                    dd.put(dwell.getId(), dwell);
                    occupiedDwellingsByZone.put(taz,dd);
                }
            }
            int households = householdDataManager.getHighestHouseholdIdInUse() - previousHouseholds;
            int persons = householdDataManager.getHighestPersonIdInUse() - previousPersons;
            previousHouseholds = householdDataManager.getHighestHouseholdIdInUse();
            previousPersons = householdDataManager.getHighestPersonIdInUse();

            logger.info("   Municipality " + municipality + ". Generated " + hhPersons + " persons in " + hhTotal + " households.");
        }
        int households = householdDataManager.getHighestHouseholdIdInUse();
        int persons = householdDataManager.getHighestPersonIdInUse();
        logger.info("   Finished generating households and persons. A population of " + persons + " persons in " + households + " households was generated.");


        //Vacant dwellings--------------------------------------------
        //They have similar characteristics to the dwellings that are occupied (assume that there is no difference between the occupied and vacant dwellings in terms of quality)
/*        int vacantCounter = 0;
        for (int municipality = 0; municipality < cityID.length; municipality++) {

            logger.info("   Municipality " + cityID[municipality] + ". Starting to generate vacant dwellings.");
            int municipalityID = cityID[municipality];
            int vacantDwellings = (int) marginalsMunicipality.getIndexedValueAt(cityID[municipality], "totalDwellingsVacant");
            TableDataSet rasterCellsMatrix = cellsMatrix;

            //obtain the raster cells of the municipality and their weight within the municipality
            int[] tazInCity = cityTAZ.get(municipalityID);
            double[] probTaz = new double[tazInCity.length];
            double sumProbTaz = 0;
            for (int i = 0; i < tazInCity.length; i++){
                probTaz[i] = rasterCellsMatrix.getIndexedValueAt(tazInCity[i],"Population");
                sumProbTaz = sumProbTaz + probTaz[i];
            }
            int rasterCount = 0;
            for (int row = 1; row <= rasterCellsMatrix.getRowCount(); row++) {
                if ((int) rasterCellsMatrix.getValueAt(row, "ID_city") == municipalityID) {
                    rasterCount++;
                }
            }



            //Select the vacant dwelling and copy characteristics
            for (int row = 0; row < vacantDwellings; row++) {

                //Allocation
                int ddCell[] = select(probTaz, tazInCity, sumProbTaz); // I allocate vacant dwellings using the same proportion as occupied dwellings.
                int zone = ddCell[0];

                Dwelling ddToCopy = occupiedDwellingsByZone.get(zone).get(SiloUtil.selectequal(occupiedDwellingsByZone.get(zone)));

                //Copy characteristics
                int newDdId = RealEstateDataManager.getNextDwellingId();
                int bedRooms = ddToCopy.getBedrooms(); //Not on the micro data
                int price = 0; //Monte Carlo
                DwellingType ddtype = ddToCopy.getType();
                int quality = ddToCopy.getQuality();
                Dwelling dwell = DwellingUtils.getFactory().createDwelling(newDdId, zone, null, -1, ddtype, bedRooms, quality, price, 0, 0); //newDwellingId, raster cell, HH Id, ddType, bedRooms, quality, price, restriction, construction year
                realEstate.addDwelling(dwell);
                dwell.setUsage(DwellingUsage.VACANT); //vacant dwelling = 3; and hhID is equal to -1
                vacantCounter++;
            }*/
        //logger.info("   The number of vacant dwellings is: " + vacantCounter);
        //}
    }


    private static double translateIncome (int incomeClass, double[] incomeThresholds, GammaDistributionImpl q) throws MathException {
        //provide the income value for each person give the income class.
        //income follows a gamma distribution that was calibrated using the microdata. Income thresholds are calculated for the stiches
        double income;
        int finish = 0;
        double low = 0;
        double high = 1;
        if (incomeClass == 90) {
            income = 0;  // kein Einkommen
/*        } else if (incomeClass == 50) {
            income = 0; // Selbständige/r Landwirt/in in der Haupttätigkeit
        } else if (incomeClass == 99) {
            income = -1; //keine Angabe*/
        } else {
            if (incomeClass == 1) {
                low = 0;
                high = incomeThresholds[0];
            } else if (incomeClass == 50){ // Selbständige/r Landwirt/in in der Haupttätigkeit
                low = 0; //give them a random income following the distribution
                high = 1;
            } else if (incomeClass == 99){ //keine Angabe
                low = 0; //give them a random income following the distribution
                high = 1;
            } else if (incomeClass == incomeThresholds.length + 1) {
                low = incomeThresholds[incomeThresholds.length-1];
                high = 1;
            } else {
                int i = 2;
                while (finish == 0){
                    if (incomeClass > i){
                        i++;
                    } else {
                        finish = 1;
                        low = incomeThresholds[i-2];
                        high = incomeThresholds[i-1];
                    }
                }
            }
            double cummulativeProb = SiloUtil.getRandomNumberAsDouble()*(high - low) + low;
            income = q.inverseCumulativeProbability(cummulativeProb);
        }
        return income;
    }


    private static int translateEducationLevel (int personEducation, TableDataSet educationLevel){
        //translate 12 education levels to 4
        //jobs is one TableDataSet that is read from a csv file containing the description, ID and types of jobs.
        int education = 0;
        int finish = 0;
        int row = 1;
        while (finish == 0 & row < educationLevel.getRowCount()){
            if (personEducation == educationLevel.getValueAt(row,"fdz_mz_sufCode")) {
                finish =1;
                education = (int) educationLevel.getValueAt(row,"SynPopCode");
            }
            else {
                row++;
            }
        }
        if (education == 0){education = 1;}
        return education;
    }


    private static int selectFloorSpace(float[] vacantFloor, int[] sizeBracketsDwelling){
        //provide the size of the building
        int floorSpaceDwelling = 0;
        int floorSpace = SiloUtil.select(vacantFloor);
        if (floorSpace == 0){
            floorSpaceDwelling = (int) (30 + SiloUtil.getRandomNumberAsFloat() * 20);
        } else if (floorSpace == sizeBracketsDwelling.length - 1) {
            floorSpaceDwelling = (int) (120 + SiloUtil.getRandomNumberAsFloat() * 200);
        } else {
            floorSpaceDwelling = (int) SiloUtil.getRandomNumberAsDouble()*(sizeBracketsDwelling[floorSpace]-sizeBracketsDwelling[floorSpace-1]) +
                    sizeBracketsDwelling[floorSpace - 1];
        }
        return floorSpaceDwelling;
    }


    private static int[] selectBuildingSizeYear(float[] vacantSize, int[] yearBracketsDwelling){
        //provide the size of the building
        int[] buildingSizeAndYear = new int[2];
        int yearSize = SiloUtil.select(vacantSize);
        if (yearSize < yearBracketsDwelling.length){
            buildingSizeAndYear[0] = 1; //small-size building
            buildingSizeAndYear[1] = yearBracketsDwelling[yearSize];
        } else {
            buildingSizeAndYear[0] = 2; //medium-size building
            buildingSizeAndYear[1] = yearBracketsDwelling[yearSize - yearBracketsDwelling.length];
        }
        return buildingSizeAndYear;
    }


    private static int selectDwellingYear(int yearBuilt){
        //assign randomly one construction year to the dwelling within the year brackets of the microdata
        //Ages - 1: before 1919, 2: 1919-1948, 3: 1949-1978, 4: 1979 - 1986; 5: 1987 - 1990; 6: 1991 - 2000; 7: 2001 - 2004; 8: 2005 - 2008, 9: 2009 or later,
        int selectedYear = 1;
        float rnd = SiloUtil.getRandomNumberAsFloat();
        switch (yearBuilt){
            case 1: selectedYear = 1919;
                break;
            case 2: selectedYear = (int) (1919 + rnd * 39);
                break;
            case 3: selectedYear = (int) (1949 + rnd * 29);
                break;
            case 4: selectedYear = (int) (1979 + rnd * 7);
                break;
            case 5: selectedYear = (int) (1987 + rnd * 3);
                break;
            case 6: selectedYear = (int) (1991 + rnd * 9);
                break;
            case 7: selectedYear = (int) (2001 + rnd * 3);
                break;
            case 8: selectedYear = (int) (2005 + rnd * 3);
                break;
            case 9: selectedYear = (int) (2009 + rnd * 2);
                break;
        }
        return selectedYear;
    }


    private static int selectVacantDwellingYear(int yearBuilt){
        //assign randomly one construction year to the dwelling within the year brackets of the microdata -
        //Ages - 2: Before 1948, 5: 1949 - 1990; 6: 1991 - 2000; 9: 2001 or later
        int selectedYear = 1;
        float rnd = SiloUtil.getRandomNumberAsFloat();
        switch (yearBuilt){
            case 2: selectedYear = (int) (1919 + rnd * 39);
                break;
            case 5: selectedYear = (int) (1949 + rnd * 41);
                break;
            case 6: selectedYear = (int) (1991 + rnd * 9);
                break;
            case 9: selectedYear = (int) (2001 + rnd * 10);
                break;
        }
        return selectedYear;
    }


    private void identifyVacantJobsByZoneType() {
        // populate HashMap with Jobs by zone and job type
        // adapted from SyntheticPopUS

        logger.info("  Identifying vacant jobs by zone");
        Collection<Job> jobs = dataContainer.getJobDataManager().getJobs();

        idVacantJobsByZoneType = new HashMap<>();
        numberVacantJobsByType = new HashMap<>();
        idZonesVacantJobsByType = new HashMap<>();
        numberZonesByType = new HashMap<>();
        numberVacantJobsByZoneByType = new HashMap<>();
        jobStringTypes = ResourceUtil.getArray(rb, PROPERTIES_JOB_TYPES);
        jobIntTypes = new HashMap<>();
        for (int i = 0; i < jobStringTypes.length; i++) {
            jobIntTypes.put(jobStringTypes[i], i);
        }
        int[] cellsID = cellsMatrix.getColumnAsInt("ID_cell");

        //create the counter hashmaps
        for (int i = 0; i < jobStringTypes.length; i++){
            int type = jobIntTypes.get(jobStringTypes[i]);
            numberZonesByType.put(type,0);
            numberVacantJobsByType.put(type,0);
            for (int j = 0; j < cellsID.length; j++){
                numberVacantJobsByZoneByType.put(type + cellsID[j] * 100, 0);
            }
        }
        //get the totals
        for (Job jj: jobs) {
            jj.setWorkerID(-1);
            if (jj.getWorkerId() == -1) {
                int type = jobIntTypes.get(jj.getType());
                int typeZone = type + jj.getZoneId() * 100;
                //update the set of zones that have ID
                if (numberVacantJobsByZoneByType.get(typeZone) == 0){
                    numberZonesByType.put(type, numberZonesByType.get(type) + 1);
                }
                //update the number of vacant jobs per job type
                numberVacantJobsByType.put(type, numberVacantJobsByType.get(type) + 1);
                numberVacantJobsByZoneByType.put(typeZone, numberVacantJobsByZoneByType.get(typeZone) + 1);
            }
        }
        //create the IDs Hashmaps and reset the counters
        for (int i = 0; i < jobStringTypes.length; i++){
            int type = jobIntTypes.get(jobStringTypes[i]);
            int[] dummy = SiloUtil.createArrayWithValue(numberZonesByType.get(type),0);
            idZonesVacantJobsByType.put(type,dummy);
            numberZonesByType.put(type,0);
            for (int j = 0; j < cellsID.length; j++){
                int typeZone = type + cellsID[j] * 100;
                int[] dummy2 = SiloUtil.createArrayWithValue(numberVacantJobsByZoneByType.get(typeZone), 0);
                idVacantJobsByZoneType.put(typeZone, dummy2);
                numberVacantJobsByZoneByType.put(typeZone, 0);
            }
        }
        //fill the Hashmaps with IDs
        for (Job jj: jobs) {
            if (jj.getWorkerId() == -1) {
                int type = jobIntTypes.get(jj.getType());
                int typeZone = jobIntTypes.get(jj.getType()) + jj.getZoneId() * 100;
                //update the list of job IDs per zone and job type
                int [] previousJobIDs = idVacantJobsByZoneType.get(typeZone);
                previousJobIDs[numberVacantJobsByZoneByType.get(typeZone)] = jj.getId();
                idVacantJobsByZoneType.put(typeZone,previousJobIDs);
                //update the set of zones that have ID
                if (numberVacantJobsByZoneByType.get(typeZone) == 0){
                    int[] previousZones = idZonesVacantJobsByType.get(type);
                    previousZones[numberZonesByType.get(type)] = typeZone;
                    idZonesVacantJobsByType.put(type,previousZones);
                    numberZonesByType.put(type, numberZonesByType.get(type) + 1);
                }
                //update the number of vacant jobs per job type
                numberVacantJobsByZoneByType.put(typeZone, numberVacantJobsByZoneByType.get(typeZone) + 1);
            }
        }
    }


    public static String[] expandArrayByOneElement (String[] existing, String addElement) {
        // create new array that has length of existing.length + 1 and copy values into new array
        String[] expanded = new String[existing.length + 1];
        System.arraycopy(existing, 0, expanded, 0, existing.length);
        expanded[expanded.length - 1] = addElement;
        return expanded;
    }

    public static int[] expandArrayByOneElement (int[] existing, int addElement) {
        // create new array that has length of existing.length + 1 and copy values into new array
        int[] expanded = new int[existing.length + 1];
        System.arraycopy(existing, 0, expanded, 0, existing.length);
        expanded[expanded.length - 1] = addElement;
        return expanded;
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


    public static int[] select (double[] probabilities, int[] id) {
        // select item based on probabilities (for zero-based float array)
        double sumProb = 0;
        int[] results = new int[2];
        for (double val: probabilities) sumProb += val;
        double selPos = sumProb * SiloUtil.getRandomNumberAsFloat();
        double sum = 0;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            if (sum > selPos) {
                //return i;
                results[0] = id[i];
                results[1] = i;
                return results;
            }
        }
        results[0] = id[probabilities.length - 1];
        results[1] = probabilities.length - 1;
        return results;
    }


    public static int select (int[] probabilities) {
        // select item based on probabilities (for zero-based float array)
        double selPos = SiloUtil.getSum(probabilities) * SiloUtil.getRandomNumberAsDouble();
        double sum = 0;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            if (sum > selPos) {
                return i;
            }
        }
        return probabilities.length - 1;
    }


    public static int[] select (double[] probabilities, int[] id, int sumProb) {
        // select item based on probabilities (for zero-based float array)
        int[] results = new int[2];
        double selPos = sumProb * SiloUtil.getRandomNumberAsFloat();
        double sum = 0;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            if (sum > selPos) {
                //return i;
                results[0] = id[i];
                results[1] = i;
                return results;
            }
        }
        results[0] = id[probabilities.length - 1];
        results[1] = probabilities.length - 1;
        return results;
    }

    public static int[] select (double[] probabilities, int[] id, double sumProb) {
        // select item based on probabilities (for zero-based float array)
        int[] results = new int[2];
        double selPos = sumProb * SiloUtil.getRandomNumberAsDouble();
        double sum = 0;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            if (sum > selPos) {
                //return i;
                results[0] = id[i];
                results[1] = i;
                return results;
            }
        }
        results[0] = id[probabilities.length - 1];
        results[1] = probabilities.length - 1;
        return results;
    }


    public static int[] select (double[] probabilities, int length, int[] id){
        //select item based on probabilities and return the name
        //probabilities and name have more items than the required (max number of required items is set on "length")
        double sumProb = 0;
        int[] results = new int[2];
        for (int i = 0; i < length; i++) {
            sumProb += probabilities[i];
        }
        double selPos = sumProb * SiloUtil.getRandomNumberAsDouble();
        double sum = 0;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            if (sum > selPos) {
                //return i;
                results[0] = id[i];
                results[1] = i;
                return results;
            }
        }
        results[0] = id[probabilities.length - 1];
        results[1] = probabilities.length - 1;
        return results;
    }


    public static int[] select (double[] probabilities, int length, int[] id, double sumProb){
        //select item based on probabilities and return the name
        //probabilities and name have more items than the required (max number of required items is set on "length")

        int[] results = new int[2];
        double selPos = sumProb * SiloUtil.getRandomNumberAsDouble();
        double sum = 0;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            if (sum > selPos) {
                //return i;
                results[0] = id[i];
                results[1] = i;
                return results;
            }
        }
        results[0] = id[probabilities.length - 1];
        results[1] = probabilities.length - 1;
        return results;
    }


    public static TableDataSet updateCountersHousehold (Household household, TableDataSet attributesCount, int mun){
        /* method to update the counters with the characteristics of the generated private household*/
        if (household.getHhSize() == 1){
            attributesCount.setIndexedValueAt(mun,"hhSize1",attributesCount.getIndexedValueAt(mun,"hhSize1") + 1);
        } else if (household.getHhSize() == 2){
            attributesCount.setIndexedValueAt(mun,"hhSize2",attributesCount.getIndexedValueAt(mun,"hhSize2") + 1);
        } else if (household.getHhSize() == 3){
            attributesCount.setIndexedValueAt(mun,"hhSize3",attributesCount.getIndexedValueAt(mun,"hhSize3") + 1);
        } else if (household.getHhSize() == 4){
            attributesCount.setIndexedValueAt(mun,"hhSize4",attributesCount.getIndexedValueAt(mun,"hhSize4") + 1);
        } else if (household.getHhSize() == 5){
            attributesCount.setIndexedValueAt(mun,"hhSize5",attributesCount.getIndexedValueAt(mun,"hhSize5") + 1);
        } else if (household.getHhSize() > 5){
            attributesCount.setIndexedValueAt(mun,"hhSize5",attributesCount.getIndexedValueAt(mun,"hhSize5") + 1);
        }
        attributesCount.setIndexedValueAt(mun,"hhTotal",attributesCount.getIndexedValueAt(mun,"hhTotal") + 1);
        return attributesCount;
    }

    public static TableDataSet updateCountersPerson (Person person, TableDataSet attributesCount,int mun, int[] ageBracketsPerson) {
        /* method to update the counters with the characteristics of the generated person in a private household*/
        attributesCount.setIndexedValueAt(mun, "population", attributesCount.getIndexedValueAt(mun, "population") + 1);
/*        if (person.getNationality() == Nationality.OTHER) {
            attributesCount.setIndexedValueAt(mun, "nationality2", attributesCount.getIndexedValueAt(mun, "nationality2") + 1);
        }*/
/*        if (person.getOccupation() == Occupation.EMPLOYED) {
            attributesCount.setIndexedValueAt(mun, "occupation1", attributesCount.getIndexedValueAt(mun, "occupation1") + 1);
        }
        int age = person.getAge();
        int row1 = 0;
        while (age > ageBracketsPerson[row1]) {
            row1++;
        }
        String name = "age" + ageBracketsPerson[row1];
        attributesCount.setIndexedValueAt(mun, name, attributesCount.getIndexedValueAt(mun, name) + 1);*/

        return attributesCount;
    }




    public void writeVectorToCSV(int[] thresholds, double[] frequencies, String outputFile, double a, double g){
        try {

            PrintWriter pw = new PrintWriter(new FileWriter(outputFile, true));
            pw.println("alpha,gamma,threshold,frequency,iteration");

            for (int i = 0; i< thresholds.length; i++) {
                pw.println(a + "," + g + "," + thresholds[i] + "," + frequencies[i]);
            }
            pw.flush();
            pw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeMatrixToCSV(String outputFile, TableDataSet matrix, Double alpha, Double gamma, int count){
        try {

            PrintWriter pw = new PrintWriter(new FileWriter(outputFile, true));

            for (int i = 1; i<= matrix.getRowCount(); i++) {
                String line = Integer.toString((int) matrix.getValueAt(i,1));
                for  (int j = 2; j <= matrix.getColumnCount(); j++){
                    line = line + "," + Integer.toString((int) matrix.getValueAt(i,j));
                }
                line = line + "," + alpha + "," + gamma + "," + count;
                pw.println(line);
            }
            pw.flush();
            pw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void checkTripLengthDistribution (Frequency travelTimes, double alpha, double gamma, String fileName, double step){
        //to obtain the trip length distribution
        int[] timeThresholds1 = new int[79];
        double[] frequencyTT1 = new double[79];
        for (int row = 0; row < timeThresholds1.length; row++) {
            timeThresholds1[row] = row + 1;
            frequencyTT1[row] = travelTimes.getCumPct(timeThresholds1[row]);
            //logger.info("Time: " + timeThresholds1[row] + ", cummulated frequency:  " + frequencyTT1[row]);
        }
        writeVectorToCSV(timeThresholds1, frequencyTT1, fileName, alpha, gamma);

    }

    public void checkodMatrix (TableDataSet odMatrix, double a, double g, int it, String fileName){
        //to obtain the square difference between the observed and estimated OD flows
        double dif = 0;
        double ind = 0;
        int count = 0;
        for (int row = 1; row <= odMatrix.getRowCount(); row++){
            ind = odMatrix.getValueAt(row,Integer.toString(it)) - odMatrix.getValueAt(row,"ObservedFlow");
            dif = dif + ind * ind;
            count++;
        }
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(fileName, true));
            pw.println(a + "," + g + "," + dif + "," + dif / count + "," + it);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double[] createArrayDoubles(double initial, double end, int length){
        //Create one array with specified start, end and number of elements

        double[] array = new double[length + 1];
        double step = (end - initial) / length;
        for (int i = 0; i < array.length; i++){
            array[i] = initial + i * step;
        }
        return array;

    }


    private HashMap<String,HashMap<Integer,Integer>> updateInnerMap(HashMap<String, HashMap<Integer, Integer>> outer, int gender, int age, int row) {

        String key = "male";
        if (gender == 2) {
            key = "female";
        }
        HashMap<Integer, Integer> inner = outer.get(key);
        if (inner == null){
            inner = new HashMap<Integer, Integer>();
            outer.put(key, inner);
        }
        inner.put(row, age);
        return outer;
    }


    private void setRoles(HashMap<String, HashMap<Integer, Integer>> singles, HashMap<String, HashMap<Integer, Integer>> married,
                          HashMap<Integer, Integer> childrenInHousehold, HashMap<String, HashMap<Integer, Integer>> noClass) {

        //set children in the household
        if (childrenInHousehold != null){
            for (Map.Entry<Integer, Integer> pair : childrenInHousehold.entrySet()){
                int row = pair.getKey();
                dataPerson.setValueAt(row, "personRole", 3);
            }
        }
        //set singles and married in the household
        String[] keys = {"male", "female"};
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            HashMap<Integer, Integer> inner = singles.get(key);
            if (inner != null) {
                for (Map.Entry<Integer, Integer> pair : inner.entrySet()) {
                    int row = pair.getKey();
                    dataPerson.setValueAt(row, "personRole", 1);
                }
            }
            inner = married.get(key);
            if (inner != null) {
                for (Map.Entry<Integer, Integer> pair : inner.entrySet()) {
                    int row = pair.getKey();
                    dataPerson.setValueAt(row, "personRole", 2);
                }
            }
            inner = noClass.get(key);
            if (inner != null) {
                for (Map.Entry<Integer, Integer> pair : inner.entrySet()) {
                    int row = pair.getKey();
                    dataPerson.setValueAt(row, "rearrangedRole", 1);
                }
            }
        }
    }

    private void setRolesObject(HashMap<String, HashMap<Integer, Integer>> singles, HashMap<String, HashMap<Integer, Integer>> married,
                                HashMap<Integer, Integer> childrenInHousehold, HashMap<String, HashMap<Integer, Integer>> noClass, Household household) {

        //set children in the household
        if (childrenInHousehold != null){
            for (Map.Entry<Integer, Integer> pair : childrenInHousehold.entrySet()){
                int row = pair.getKey();
                household.getPersons().get(row).setRole(PersonRole.CHILD);
            }
        }
        //set singles and married in the household
        String[] keys = {"male", "female"};
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            HashMap<Integer, Integer> inner = singles.get(key);
            if (inner != null) {
                for (Map.Entry<Integer, Integer> pair : inner.entrySet()) {
                    int row = pair.getKey();
                    household.getPersons().get(row).setRole(PersonRole.SINGLE);
                }
            }
            inner = married.get(key);
            if (inner != null) {
                for (Map.Entry<Integer, Integer> pair : inner.entrySet()) {
                    int row = pair.getKey();
                    household.getPersons().get(row).setRole(PersonRole.MARRIED);
                }
            }
            inner = noClass.get(key);
            if (inner != null) {
                for (Map.Entry<Integer, Integer> pair : inner.entrySet()) {
                    int row = pair.getKey();
                    household.getPersons().get(row).setRole(PersonRole.SINGLE);
                }
            }
        }
    }


    public int[] selectWorkplace(int home, HashMap<Integer, Integer> vacantJobsByZoneByType,
                                 int[] zoneJobKeys, int lengthZoneKeys, Matrix impedanceMatrix) {
        //given a person and job type, select the workplace location (raster cell)
        //it is based on the utility of that job type and each location, multiplied by the number of jobs that remain vacant
        //it can be directly used for schools, since the utility only checks the distance between the person home and destination

        double[] probabilities = new double[lengthZoneKeys];
        for (int j = 0; j < lengthZoneKeys; j++){
            probabilities[j] = impedanceMatrix.getValueAt(home, zoneJobKeys[j] / 100) * vacantJobsByZoneByType.get(zoneJobKeys[j]);
            //probability = impedance * number of vacant jobs. Impedance is calculated in advance as exp(utility)
        }
        return select(probabilities,zoneJobKeys);
    }

    public int[] selectClosestSchool(int home, HashMap<Integer, Integer> vacantJobsByZoneByType,
                                     int[] zoneJobKeys, int lengthZoneKeys, Matrix impedanceMatrix) {
        //given a person and job type, select the workplace location (raster cell)
        //it is based on the utility of that job type and each location, multiplied by the number of jobs that remain vacant
        //it can be directly used for schools, since the utility only checks the distance between the person home and destination


        int[] min = new int[2];
        min[0] = zoneJobKeys[0];
        min[1] = 0;
        double minDist = impedanceMatrix.getValueAt(home, zoneJobKeys[0] / 100);
        for (int j = 1; j < lengthZoneKeys; j++) {
            if (impedanceMatrix.getValueAt(home, zoneJobKeys[j] / 100) < minDist) {
                min[0] = zoneJobKeys[j];
                min[1] = j;
                minDist = impedanceMatrix.getValueAt(home, zoneJobKeys[j] / 100);
            }
        }
        return min;
    }


    private void generateCountersForValidation(){
        //method to obtain the errors from the generated synthetic population
        //Create the errors table (for all the municipalities)
        counterMunicipality = new TableDataSet();
        errorMunicipality = new TableDataSet();
        counterMunicipality.appendColumn(cityID,"ID_city");
        errorMunicipality.appendColumn(cityID,"ID_city");
        for(int attribute = 0; attribute < attributesMunicipality.length; attribute++) {
            double[] dummy2 = SiloUtil.createArrayWithValue(cityID.length,0.0);
            double[] dummy3 = SiloUtil.createArrayWithValue(cityID.length,0.0);
            counterMunicipality.appendColumn(dummy2, attributesMunicipality[attribute]);
            errorMunicipality.appendColumn(dummy3, attributesMunicipality[attribute]);
        }
        counterMunicipality.buildIndex(counterMunicipality.getColumnPosition("ID_city"));
        errorMunicipality.buildIndex(errorMunicipality.getColumnPosition("ID_city"));


    }


    public static int[] createConsecutiveArray (int arrayLength) {
        // fill one-dimensional boolean array with value

        int[] anArray = new int[arrayLength];
        for (int i = 0; i < anArray.length; i++) anArray[i] = i;
        return anArray;
    }

    private void updateInnerMap(HashMap<String, HashMap<String, Integer>> map, String label ,String valueString, Integer valueCode){

        if (map.containsKey(label)){
            HashMap<String, Integer> innerMap = map.get(label);
            innerMap.put(valueString, valueCode);
            map.put(label, innerMap);
        } else {
            HashMap<String, Integer> innerMap = new HashMap<>();
            innerMap.put(valueString, valueCode);
            map.put(label, innerMap);
        }
    }


    private Map<Integer, Map<String, Double>> updateInnerMap(Map<Integer, Map<String, Double>> map, Integer label ,String valueString, double valueCode){

        if (map.containsKey(label)){
            Map<String, Double> innerMap = map.get(label);
            innerMap.put(valueString, valueCode);
            map.put(label, innerMap);
        } else {
            Map<String, Double> innerMap = new HashMap<>();
            innerMap.put(valueString, valueCode);
            map.put(label, innerMap);
        }
        return map;
    }

    private void updateInnerMap(HashMap<String, HashMap<Integer, Integer>> map, String label, int valueInt, Integer valueCode){

        if (map.containsKey(label)){
            HashMap<Integer, Integer> innerMap = map.get(label);
            innerMap.put(valueInt, valueCode);
            map.put(label, innerMap);
        } else {
            HashMap<Integer, Integer> innerMap = new HashMap<>();
            innerMap.put(valueInt, valueCode);
            map.put(label, innerMap);
        }

    }

    private void updatePersonsInHousehold(int hhId, int id, HashMap<String, Integer> attributeMap) {
        if (personsInHouseholds.containsKey(hhId)) {
            HashMap<Integer, HashMap<String, Integer>> inner = personsInHouseholds.get(hhId);
            inner.put(id, attributeMap);
            personsInHouseholds.put(hhId, inner);
        } else {
            HashMap<Integer, HashMap<String, Integer>> inner = new HashMap<>();
            inner.put(id, attributeMap);
            personsInHouseholds.put(hhId, inner);
        }
    }

    public double multiplyIfNotZero(double x, double y, double f){
        if (y == 0){
            return x;
        } else {
            return x * f;
        }
    }

    private void summarizeData(DataContainer dataContainer){

        String filehh = properties.main.baseDirectory
                + properties.householdData.householdFileName
                + "_"
                + properties.main.baseYear + 100
                + ".csv";
        HouseholdWriter hhwriter = new DefaultHouseholdWriter(dataContainer.getHouseholdDataManager().getHouseholds());
        hhwriter.writeHouseholds(filehh);

        String filepp = properties.main.baseDirectory
                + properties.householdData.personFileName
                + "_"
                + properties.main.baseYear+ 100
                + ".csv";
        PersonWriter ppwriter = new PersonWriterCapeTown(dataContainer.getHouseholdDataManager());
        ppwriter.writePersons(filepp);

        String filedd = properties.main.baseDirectory
                + properties.realEstate.dwellingsFileName
                + "_"
                + properties.main.baseYear+ 100
                + ".csv";
        DwellingWriter ddwriter = new DefaultDwellingWriter(dataContainer.getRealEstateDataManager().getDwellings());
        ddwriter.writeDwellings(filedd);

        String filejj = properties.main.baseDirectory
                + properties.jobData.jobsFileName
                + "_"
                + properties.main.baseYear+ 100
                + ".csv";
        JobWriter jjwriter = new JobWriterMuc(dataContainer.getJobDataManager());
        jjwriter.writeJobs(filejj);


    }
}

