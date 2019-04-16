//package de.tum.bgu.msm.syntheticPopulationGenerator.capeTown;
//
//import com.pb.common.datafile.TableDataSet;
//import com.pb.common.matrix.Matrix;
//import com.pb.common.util.ResourceUtil;
//import de.tum.bgu.msm.container.DataContainer;
//import de.tum.bgu.msm.data.dwelling.*;
//import de.tum.bgu.msm.data.household.Household;
//import de.tum.bgu.msm.data.household.HouseholdData;
//import de.tum.bgu.msm.data.household.HouseholdFactory;
//import de.tum.bgu.msm.data.household.HouseholdUtil;
//import de.tum.bgu.msm.data.job.Job;
//import de.tum.bgu.msm.data.job.JobData;
//import de.tum.bgu.msm.data.job.JobUtils;
//import de.tum.bgu.msm.data.person.*;
//import de.tum.bgu.msm.properties.Properties;
//import de.tum.bgu.msm.syntheticPopulationGenerator.SyntheticPopI;
//import de.tum.bgu.msm.util.concurrent.ConcurrentExecutor;
//import de.tum.bgu.msm.utils.SiloUtil;
//import org.apache.commons.math.MathException;
//import org.apache.commons.math.distribution.GammaDistributionImpl;
//import org.apache.commons.math.stat.Frequency;
//import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
//import org.apache.log4j.Logger;
//
//import java.io.*;
//import java.util.*;
//import java.util.stream.IntStream;
//
//public class SyntheticPopCT implements SyntheticPopI {
//
//    private ResourceBundle rb;
//    //Options to run de synthetic population
//    protected static final String PROPERTIES_CONSTRAINT_BY_CITY_AND_CNTY  = "run.ipu.city.and.county";
//    protected static final String PROPERTIES_RUN_IPU                      = "run.ipu.synthetic.pop";
//    protected static final String PROPERTIES_RUN_SYNTHETIC_POPULATION     = "run.synth.pop.generator";
//    protected static final String PROPERTIES_YEAR_MICRODATA               = "year.micro.data";
//    //Routes of the input data
//    protected static final String PROPERTIES_MICRODATA_2000_PATH          = "micro.data.2000";
//    protected static final String PROPERTIES_MICRODATA_2010_PATH          = "micro.data.2010";
//    protected static final String PROPERTIES_MARGINALS_REGIONAL_MATRIX    = "marginals.county";
//    protected static final String PROPERTIES_MARGINALS_HOUSEHOLD_MATRIX   = "marginals.municipality";
//    protected static final String PROPERTIES_SELECTED_MUNICIPALITIES_LIST = "municipalities.list";
//    protected static final String PROPERTIES_RASTER_CELLS                 = "raster.cells.definition";
//    protected static final String PROPERTIES_DISTANCE_RASTER_CELLS        = "distanceODmatrix";
//    protected static final String PROPERTIES_JOB_DESCRIPTION              = "jobs.dictionary";
//    protected static final String PROPERTIES_EDUCATION_DESCRIPTION        = "education.dictionary";
//    protected static final String PROPERTIES_SCHOOL_DESCRIPTION           = "school.dictionary";
//    protected static final String PROPERTIES_NUMBER_OF_DWELLING_QUALITY_LEVELS = "dwelling.quality.levels.distinguished";
//    //Routes of input data (if IPU is not performed)
//    protected static final String PROPERTIES_WEIGHTS_MATRIX               = "weights.matrix";
//    //Parameters of the synthetic population
//    protected static final String PROPERTIES_REGION_ATTRIBUTES            = "attributes.region";
//    protected static final String PROPERTIES_HOUSEHOLD_ATTRIBUTES         = "attributes.household";
//    protected static final String PROPERTIES_HOUSEHOLD_SIZES              = "household.size.brackets";
//    protected static final String PROPERTIES_MICRO_DATA_AGES              = "age.brackets";
//    protected static final String PROPERTIES_MICRO_DATA_AGES_QUARTER      = "age.brackets.quarter";
//    protected static final String PROPERTIES_MICRO_DATA_YEAR_DWELLING     = "year.dwelling";
//    protected static final String PROPERTIES_MICRO_DATA_FLOOR_SPACE_DWELLING = "floor.space.dwelling";
//    protected static final String PROPERTIES_MAX_ITERATIONS               = "max.iterations.ipu";
//    protected static final String PROPERTIES_MAX_ERROR                    = "max.error.ipu";
//    protected static final String PROPERTIES_INITIAL_ERROR                = "ini.error.ipu";
//    protected static final String PROPERTIES_IMPROVEMENT_ERROR            = "min.improvement.error.ipu";
//    protected static final String PROPERTIES_IMPROVEMENT_ITERATIONS       = "iterations.improvement.ipu";
//    protected static final String PROPERTIES_INCREASE_ERROR               = "increase.error.ipu";
//    protected static final String PROPERTIES_INCOME_GAMMA_PROBABILITY     = "income.probability";
//    protected static final String PROPERTIES_INCOME_GAMMA_SHAPE           = "income.gamma.shape";
//    protected static final String PROPERTIES_INCOME_GAMMA_RATE            = "income.gamma.rate";
//    protected static final String PROPERTIES_JOB_TYPES                    = "employment.types";
//    protected static final String PROPERTIES_SCHOOL_TYPES_DE              = "school.types";
//    protected static final String PROPERTIES_JOB_ALPHA                    = "employment.choice.alpha";
//    protected static final String PROPERTIES_JOB_GAMMA                    = "employment.choice.gamma";
//    protected static final String PROPERTIES_UNIVERSITY_ALPHA             = "university.choice.alpha";
//    protected static final String PROPERTIES_UNIVERSITY_GAMMA             = "university.choice.gamma";
//    protected static final String PROPERTIES_EMPLOYMENT_BY_GENDER_EDU     = "employment.probability";
//    //Read the synthetic population
//    protected static final String PROPERTIES_HOUSEHOLD_SYN_POP            = "household.file.ascii";
//    protected static final String PROPERTIES_PERSON_SYN_POP               = "person.file.ascii";
//    protected static final String PROPERTIES_DWELLING_SYN_POP             = "dwelling.file.ascii";
//    protected static final String PROPERTIES_JOB_SYN_POP                  = "job.file.ascii";
//    protected static final String PROPERTIES_ATRIBUTES_MICRODATA_PP        = "read.attributes.pp";
//    protected static final String PROPERTIES_ATRIBUTES_MICRODATA_HH        = "read.attributes.hh";
//
//
//    protected TableDataSet microDataHousehold;
//    protected TableDataSet microDataPerson;
//    protected TableDataSet microDataDwelling;
//    protected TableDataSet frequencyMatrix;
//    protected TableDataSet marginalsCounty;
//    protected TableDataSet marginalsMunicipality;
//    protected TableDataSet cellsMatrix;
//
//    protected int[] cityID;
//    protected int[] countyID;
//    protected HashMap<Integer, ArrayList> municipalitiesByCounty;
//    protected HashMap<Integer, int[]> cityTAZ;
//    private ArrayList<Integer> municipalities;
//    private ArrayList<Integer> counties;
//
//    protected String[] attributesCounty;
//    protected String[] attributesMunicipality;
//    protected int[] ageBracketsPerson;
//    protected int[] sizeBracketsDwelling;
//    protected int[] yearBracketsDwelling;
//    protected int numberofQualityLevels;
//    protected TableDataSet counterMunicipality;
//    protected TableDataSet errorMunicipality;
//    protected TableDataSet errorSummary;
//
//    protected TableDataSet weightsTable;
//    protected TableDataSet jobsTable;
//    protected TableDataSet educationDegreeTable;
//    protected TableDataSet schoolLevelTable;
//    HashMap<String, Integer> jobIntTypes;
//    protected String[] jobStringTypes;
//    protected int[] schoolTypes;
//
//    protected HashMap<Integer, int[]> idVacantJobsByZoneType;
//    protected HashMap<Integer, Integer> numberVacantJobsByType;
//    protected HashMap<Integer, int[]> idZonesVacantJobsByType;
//    protected HashMap<Integer, Integer> numberVacantJobsByZoneByType;
//    protected HashMap<Integer, Integer> numberZonesByType;
//
//    protected HashMap<Integer, Integer> numberVacantSchoolsByZoneByType;
//    protected HashMap<Integer, int[]> idZonesVacantSchoolsByType;
//    protected HashMap<Integer, Integer> numberZonesWithVacantSchoolsByType;
//    protected HashMap<Integer, Integer> schoolCapacityByType;
//
//    protected double alphaJob;
//    protected double gammaJob;
//    protected TableDataSet probabilitiesJob;
//
//    protected Matrix distanceMatrix;
//    protected Matrix distanceImpedance;
//    protected TableDataSet odMunicipalityFlow;
//    protected TableDataSet odCountyFlow;
//
//    protected HashMap<Integer, HashMap<String, Integer>> households;
//    protected HashMap<Integer, HashMap<Integer, HashMap<String, Integer>>> personsInHouseholds;
//
//
//    private HashMap<String, HashMap <Integer, Integer>> attributeCodeValues;
//    private HashMap<String, String> attributeCodeToControlTotal;
//    private HashMap<String, HashMap <Integer, Integer>> attributesControlTotal;
//    private HashMap<String, String> attributeCodeToMicroPerson;
//    private HashMap<String, String> attributeCodeToMicroHousehold;
//    private HashMap<String, HashMap <String, Integer>> attributesMicroPerson;
//    private HashMap<String, HashMap <String, Integer>> attributesMicroHousehold;
//    private String[] codePersonAttributes;
//    private String[] microPersonAttributes;
//    private String[] microHouseholdAttributes;
//    private String[] codeHouseholdAttributes;
//    //private HashMap<>
//
//    private HashMap<Person, Integer> educationalLevelByPerson;
//
//    static Logger logger = Logger.getLogger(SyntheticPopCT.class);
//    private DataContainer dataContainer;
//
//    public SyntheticPopCT(ResourceBundle rb){
//        this.rb = rb;
//    }
//
//
//    public void runSP(){
//        //method to create the synthetic population
//        if (!ResourceUtil.getBooleanProperty(rb, PROPERTIES_RUN_SYNTHETIC_POPULATION, false)) return;
//        logger.info("   Starting to create the synthetic population.");
//        readZonalData();
//        createDirectoryForOutput();
//        //TODO: change to cape town implementation
//        dataContainer = DataContainerImpl.createEmptySiloDataContainer();
//        long startTime = System.nanoTime();
//        boolean temporaryTokenForTesting = false;  // todo:  These two lines will be removed
//        if (!temporaryTokenForTesting) {           // todo:  after testing is completed
//            //Read entry data from the micro data
//            readAttributes();
//            readControlTotals();
//            int persons = readMicroDataCT();
//            createMicroHouseholdsAndMicroPersons(persons);
//            //checkHouseholdRelationship();
//            //runIPUbyCityAndCounty(); //IPU fitting with one geographical constraint. Each municipality is independent of others
//            readIPU();
//            generateHouseholdsPersonsDwellings(); //Monte Carlo selection process to generate the synthetic population. The synthetic dwellings will be obtained from the same microdata
//            /*generateJobs(); //Generate the jobs by type. Allocated to TAZ level
//            assignJobs(); //Workplace allocation
//            assignSchools(); //School allocation
//            addCars(false);
//            summarizeData.writeOutSyntheticPopulationDE(rb, SiloUtil.getBaseYear());*/
//        } else { //read the synthetic population  // todo: this part will be removed after testing is completed
//            logger.info("Testing mode");
//            //readMicroData2010();
//            //checkHouseholdRelationship();
//            readSyntheticPopulation();
//            //summarizeData.writeOutSyntheticPopulationDE(rb, SiloUtil.getBaseYear(),"_ddPrice_");
//        }
//        long estimatedTime = System.nanoTime() - startTime;
//        logger.info("   Finished creating the synthetic population. Elapsed time: " + estimatedTime);
//    }
//
//    private void readAttributes() {
//        //Read attributes and process dictionary
//
//        String fileName = "input/syntheticPopulation/variablesCTDictionary.csv";
//
//        attributeCodeValues = new HashMap<>();
//        attributesControlTotal = new HashMap<>();
//        attributesMicroPerson = new HashMap<>();
//        attributesMicroHousehold = new HashMap<>();
//        attributeCodeToControlTotal = new HashMap<>();
//        attributeCodeToMicroPerson = new HashMap<>();
//        attributeCodeToMicroHousehold = new HashMap<>();
//        HashMap<Integer, String> attributeOrder = new HashMap<>();
//
//        String recString = "";
//        int recCount = 0;
//        int atCount = 0;
//        try {
//            BufferedReader in = new BufferedReader(new FileReader(fileName));
//            recString = in.readLine();
//
//            // read header
//            String[] header = recString.split(",");
//            int posType    = SiloUtil.findPositionInArray("Type", header);
//            int posLabelCode  = SiloUtil.findPositionInArray("labelCode",header);
//            int posLabelMicroData = SiloUtil.findPositionInArray("labelMicroData",header);
//            int posLabelControlTotal = SiloUtil.findPositionInArray("labelControlTotal",header);
//            int posValueCode = SiloUtil.findPositionInArray("valueCode", header);
//            int posValueInt = SiloUtil.findPositionInArray("valueInt", header);
//            int posValueString = SiloUtil.findPositionInArray("valueString", header);
//            int postIPU = SiloUtil.findPositionInArray("IPU", header);
//            int posImportance = SiloUtil.findPositionInArray("order", header);
//            //int posArea = SiloUtil.findPositionInArray("Area", header);
//
//            // read line
//            while ((recString = in.readLine()) != null) {
//                recCount++;
//                String[] lineElements = recString.split(",");
//
//                String type = lineElements[posType];
//                String labelCode = lineElements[posLabelCode];
//                String labelMicroData = lineElements[posLabelMicroData];
//                String labelControlTotal = lineElements[posLabelControlTotal];
//                int valueCode = Integer.parseInt(lineElements[posValueCode]);
//                int valueInt = Integer.parseInt(lineElements[posValueInt]);
//                String valueString = lineElements[posValueString];
//                boolean ipu = Boolean.parseBoolean(lineElements[postIPU]);
//                int importance = Integer.parseInt(lineElements[posImportance]);
//                //String area = lineElements[posArea];
//
//                //update map of the attributes from the code
//                if (!attributeCodeToControlTotal.containsKey(labelCode)) {
//                    attributeCodeToControlTotal.put(labelCode, labelControlTotal);
//                    if (type.equals("Person")) {
//                        attributeCodeToMicroPerson.put(labelCode, labelMicroData);
//                    } else {
//                        attributeCodeToMicroHousehold.put(labelCode, labelMicroData);
//                    }
//                }
//                if (ipu) {
//                    if (valueCode > -1) {
//                        updateInnerMap(attributeCodeValues, labelCode, valueCode, valueCode);
//                    }
//                    updateInnerMap(attributesControlTotal, labelControlTotal, valueInt, valueCode);
//                }
//                if (type.equals("Person")){
//                    updateInnerMap(attributesMicroPerson, labelMicroData, valueString, valueCode);
//                } else {
//                    updateInnerMap(attributesMicroHousehold, labelMicroData, valueString, valueCode);
//                }
//                if (importance > 0){
//                    attributeOrder.put(importance, labelCode + valueCode);
//                    atCount++;
//                }
//            }
//        } catch (IOException e) {
//            logger.fatal("IO Exception caught reading synpop household file: " + fileName);
//            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
//        }
//
//        codePersonAttributes = new String[attributeCodeToMicroPerson.size()];
//        microPersonAttributes = new String[attributeCodeToMicroPerson.size()];
//
//        attributesMunicipality = new String[atCount + 2];
//        attributesCounty = new String[atCount + 2];
//        int k = 0;
//        for (int j = 1; j <= atCount; j++){
//            String label = attributeOrder.get(j);
//            attributesCounty[atCount - j] = label;
//            attributesMunicipality[atCount - j] = label;
//        }
//        attributesCounty[atCount] = "population";
//        attributesCounty[atCount + 1] = "hhTotal";
//        attributesMunicipality[atCount] = "population";
//        attributesMunicipality[atCount + 1] = "hhTotal";
//
//
//        int i = 0;
//        for (Map.Entry<String, String> pairCode : attributeCodeToMicroPerson.entrySet()){
//            codePersonAttributes[i] = pairCode.getKey();
//            microPersonAttributes[i] = pairCode.getValue();
//            i++;
//        }
//        codeHouseholdAttributes = new String[attributeCodeToMicroHousehold.size()];
//        microHouseholdAttributes = new String[attributeCodeToMicroHousehold.size()];
//        i = 0;
//        for (Map.Entry<String, String> pairCode : attributeCodeToMicroHousehold.entrySet()){
//            codeHouseholdAttributes[i] = pairCode.getKey();
//            microHouseholdAttributes[i] = pairCode.getValue();
//            i++;
//        }
//        logger.info("Finished reading attributes.");
//
//    }
//
//    private void readZonalData() {
//
//        //List of municipalities and counties that are used for IPU and allocation
//        TableDataSet selectedMunicipalities = SiloUtil.readCSVfile(rb.getString(PROPERTIES_SELECTED_MUNICIPALITIES_LIST)); //TableDataSet with all municipalities
//        municipalities = new ArrayList<>();
//        counties = new ArrayList<>();
//        municipalitiesByCounty = new HashMap<>();
//        for (int row = 1; row <= selectedMunicipalities.getRowCount(); row++){
//            if (selectedMunicipalities.getValueAt(row,"Select") == 1f){
//                int city = (int) selectedMunicipalities.getValueAt(row,"ID_city");
//                municipalities.add(city);
//                int county = (int) selectedMunicipalities.getValueAt(row,"ID_county");
//                if (!SiloUtil.containsElement(counties, county)) {
//                    counties.add(county);
//                }
//                if (municipalitiesByCounty.containsKey(county)) {
//                    ArrayList<Integer> citiesInThisCounty = municipalitiesByCounty.get(county);
//                    citiesInThisCounty.add(city);
//                    municipalitiesByCounty.put(county, citiesInThisCounty);
//                } else {
//                    ArrayList<Integer> arrayList = new ArrayList<>();
//                    arrayList.add(city);
//                    municipalitiesByCounty.put(county, arrayList);
//                }
//            }
//        }
//        cityID = SiloUtil.convertArrayListToIntArray(municipalities);
//        countyID = SiloUtil.convertArrayListToIntArray(counties);
//
//
//        //TAZ attributes
//        cellsMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_RASTER_CELLS));
//        cellsMatrix.buildIndex(cellsMatrix.getColumnPosition("ID_cell"));
//        cityTAZ = new HashMap<>();
//        for (int i = 1; i <= cellsMatrix.getRowCount(); i++){
//            int city = (int) cellsMatrix.getValueAt(i,"ID_city");
//            int taz = (int) cellsMatrix.getValueAt(i,"ID_cell");
//            if (cityTAZ.containsKey(city)){
//                int[] previousTaz = cityTAZ.get(city);
//                previousTaz = SiloUtil.expandArrayByOneElement(previousTaz, taz);
//                cityTAZ.put(city, previousTaz);
//            } else {
//                int[] previousTaz = {taz};
//                cityTAZ.put(city,previousTaz);
//            }
//        }
//
//
///*        //Read the skim matrix
//        logger.info("   Starting to read OMX matrix");
//        String omxFileName= ResourceUtil.getProperty(rb,PROPERTIES_DISTANCE_RASTER_CELLS);
//        OmxFile travelTimeOmx = new OmxFile(omxFileName);
//        travelTimeOmx.openReadOnly();
//        distanceMatrix = SiloUtil.convertOmxToMatrix(travelTimeOmx.getMatrix("mat1"));
//        OmxLookup omxLookUp = travelTimeOmx.getLookup("lookup1");
//        int[] externalNumbers = (int[]) omxLookUp.getLookup();
//        distanceMatrix.setExternalNumbersZeroBased(externalNumbers);
//        for (int i = 1; i <= distanceMatrix.getRowCount(); i++){
//            for (int j = 1; j <= distanceMatrix.getColumnCount(); j++){
//                if (i == j) {
//                    distanceMatrix.setValueAt(i,j, 50/1000);
//                } else {
//                    distanceMatrix.setValueAt(i,j, distanceMatrix.getValueAt(i,j)/1000);
//                }
//            }
//        }
//        logger.info("   Read OMX matrix");*/
//    }
//
//
//    private void createDirectoryForOutput() {
//        // create output directories
//        SiloUtil.createDirectoryIfNotExistingYet("microData");
//        SiloUtil.createDirectoryIfNotExistingYet("microData/interimFiles");
//    }
//
//
//
//    private int readMicroDataCT(){
//        //method to read the synthetic population initial data
//        logger.info("   Starting to read the micro data for Cape Town");
//        readHouseholds();
//        readPersons();
//        int persons = checkHouseholdAndPersonCorrespondence();
//        logger.info("   Finished reading the micro data");
//        return persons;
//    }
//
//    private void readControlTotals() {
//        //method to obtain the list of attributes for the IPU with the categories
//        //Read the attributes at the municipality level (household attributes) and the marginals at the municipality level (German: Gemeinden)
//
//        TableDataSet controlTotalsMun = SiloUtil.readCSVfile2(rb.getString(PROPERTIES_MARGINALS_HOUSEHOLD_MATRIX));
//        TableDataSet controlTotalsCounty = SiloUtil.readCSVfile2(rb.getString(PROPERTIES_MARGINALS_REGIONAL_MATRIX));
//
//        initializeMarginals(controlTotalsMun, controlTotalsCounty);
//        processMarginals(controlTotalsMun, controlTotalsCounty);
//
//        SiloUtil.writeTableDataSet(marginalsMunicipality, "input/syntheticPopulation/controlTotalsMunIPU.csv");
//        SiloUtil.writeTableDataSet(marginalsCounty, "input/syntheticPopulation/controlTotalsCountIPU.csv");
//        logger.info("   Finished reading and converting control totals");
//    }
//
//
//    private void initializeMarginals(TableDataSet controlTotals, TableDataSet controlTotalsCounty) {
//        //initialize the marginals
//        marginalsMunicipality = new TableDataSet();
//        marginalsMunicipality.appendColumn(controlTotals.getColumnAsInt("ID_city"),"ID_city");
//        marginalsMunicipality.buildIndex(marginalsMunicipality.getColumnPosition("ID_city"));
//
//        marginalsCounty = new TableDataSet();
//        marginalsCounty.appendColumn(controlTotalsCounty.getColumnAsInt("ID_county"),"ID_county");
//        marginalsCounty.buildIndex(marginalsCounty.getColumnPosition("ID_county"));
//
//        for (Map.Entry<String, HashMap<Integer, Integer>> pairCode : attributeCodeValues.entrySet()) {
//            String attribute = pairCode.getKey();
//            Set<Integer> levels = pairCode.getValue().keySet();
//            Iterator<Integer> iterator = levels.iterator();
//            while(iterator.hasNext()){
//                Integer setElement = iterator.next();
//                String label = attribute + setElement;
//                int[] dummy = SiloUtil.createArrayWithValue(marginalsMunicipality.getRowCount(), 0);
//                marginalsMunicipality.appendColumn(dummy, label);
//                int[] dummy2 = SiloUtil.createArrayWithValue(marginalsCounty.getRowCount(), 0);
//                marginalsCounty.appendColumn(dummy2, label);
//            }
//        }
//    }
//
//    private void processMarginals(TableDataSet controlTotals, TableDataSet controlTotalsCounty) {
//        //update the values inside the marginals municipality tableDataSet
//
//        marginalsCounty.appendColumn(controlTotalsCounty.getColumnAsInt("population"), "population");
//        marginalsCounty.appendColumn(controlTotalsCounty.getColumnAsInt("hhTotal"), "hhTotal");
//        marginalsMunicipality.appendColumn(controlTotals.getColumnAsInt("population"), "population");
//        marginalsMunicipality.appendColumn(controlTotals.getColumnAsInt("hhTotal"), "hhTotal");
//        for (Map.Entry<String, HashMap<Integer, Integer>> pairCode : attributeCodeValues.entrySet()) {
//            String attribute = pairCode.getKey();
//            String attributeControlTotal = attributeCodeToControlTotal.get(attribute);
//            HashMap<Integer, Integer> attributeMap = attributesControlTotal.get(attributeControlTotal);
//            for (Map.Entry<Integer, Integer> pairAttribute : attributeMap.entrySet()) {
//                String labelControlTotal = attributeControlTotal + pairAttribute.getKey();
//                if (pairAttribute.getValue() > -1) {
//                    String labelCode = attribute + pairAttribute.getValue();
//                    for (int i = 1; i <= controlTotals.getRowCount(); i++) {
//                        int value = (int) marginalsMunicipality.getValueAt(i, labelCode);
//                        int newValue = value + (int) controlTotals.getValueAt(i, labelControlTotal);
//                        marginalsMunicipality.setValueAt(i, labelCode, newValue);
//                    }
//                    for (int i = 1; i <= controlTotalsCounty.getRowCount(); i++) {
//                        int value = (int) marginalsCounty.getValueAt(i, labelCode);
//                        int newValue = value + (int) controlTotalsCounty.getValueAt(i, labelControlTotal);
//                        marginalsCounty.setValueAt(i, labelCode, newValue);
//                    }
//                }
//            }
//        }
//    }
//
//
//
//    private int checkHouseholdAndPersonCorrespondence() {
//        //method to remove households from the map that either:
//        //1- have no persons on the person file
//        //2- have different number of persons at the person file than household size
//        int persons = 0;
//        int ppCount = 0;
//        int hhCount = 0;
//        Iterator <Map.Entry<Integer, HashMap<String, Integer>>> it = households.entrySet().iterator();
//        while(it.hasNext()) {
//            Map.Entry<Integer, HashMap<String, Integer>> pair = it.next();
//            int hhId = pair.getKey();
//            int hhSize = pair.getValue().get("hhSizeReal");
//            if (!personsInHouseholds.containsKey(hhId)){
//                it.remove();
//                ppCount = ppCount + hhSize;
//                hhCount++;
//            } else {
//                int members = personsInHouseholds.get(hhId).values().size();
//                if (members != hhSize) {
//                    it.remove();
//                    ppCount = ppCount + hhSize;
//                    hhCount++;
//                } else {
//                    persons = persons + hhSize;
//                }
//            }
//        }
//        logger.info("   " + ppCount + " persons were removed from the sample at " + hhCount + " households.");
//        logger.info("   Microdata contains " + households.size() + " households with " + persons + " persons due to inconsistencies on the micro data.");
//
//        return persons;
//    }
//
//    private void readPersons() {
//
//        String fileName = "input/syntheticPopulation/newPersons.csv";
//        personsInHouseholds = new HashMap<>();
//        HashMap<Integer, HashMap<String, Integer>> noDatas = new HashMap<>();
//
//        String recString = "";
//        int recCount = 0;
//        try {
//            BufferedReader in = new BufferedReader(new FileReader(fileName));
//            recString = in.readLine();
//
//            // read header
//            String[] header = recString.split(",");
//            int posHhId   = SiloUtil.findPositionInArray("new$X.x", header);
//            int posId   = SiloUtil.findPositionInArray("X.y",header);
//            HashMap<String, Integer> positionAttribute = new HashMap<>();
//            for (int i = 0; i < microPersonAttributes.length; i++){
//                positionAttribute.put(microPersonAttributes[i], SiloUtil.findPositionInArray(microPersonAttributes[i], header));
//            }
//
//            // read line
//            while ((recString = in.readLine()) != null) {
//
//                String[] lineElements = recString.split(",");
//                int idHh = Integer.parseInt(lineElements[posHhId]);
//                Integer id = Integer.parseInt(lineElements[posId]);
//                recCount++;
//                HashMap<String, Integer> attributeMap = new HashMap<>();
//                attributeMap.put("hhId", idHh);
//                boolean allData = true;
//                for (int i = 0; i < microPersonAttributes.length; i++){
//                    String attributeCode = codePersonAttributes[i];
//                    String attributeMicro =  microPersonAttributes[i];
//                    String valueMicroData = lineElements[positionAttribute.get(attributeMicro)];
//                    int valueCode = attributesMicroPerson.get(attributeMicro).get(valueMicroData);
//                    attributeMap.put(attributeCode, valueCode);
//                    if (valueCode < 0){
//                        allData = false;
//                    }
//                }
//                if (attributeMap.get("occupation") == -1){
//                    if (attributeMap.get("age") < 19 || attributeMap.get("age") > 65) {
//                        attributeMap.put("occupation", 2);
//                        if (attributeMap.get("nationality") > -1){
//                            allData = true;
//                        }
//                    }
//                }
//                if (allData) {
//                    updatePersonsInHousehold(idHh, id, attributeMap);
//                } else {
//                    noDatas.put(id, attributeMap);
//                }
//            }
//        } catch (IOException e) {
//            logger.fatal("IO Exception caught reading synpop household file: " + fileName);
//            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
//        }
//
//        TableDataSet nos = new TableDataSet();
//        int[] counter = createConsecutiveArray(noDatas.keySet().size());
//        nos.appendColumn(counter,"ids");
//        for (int i = 0; i < microPersonAttributes.length; i++) {
//            String attributeCode = codePersonAttributes[i];
//            int[] dummy = SiloUtil.createArrayWithValue(counter.length, 0);
//            nos.appendColumn(dummy, attributeCode);
//        }
//        int row = 1;
//        Iterator<Integer> iterator = noDatas.keySet().iterator();
//        while (iterator.hasNext()){
//            int id = iterator.next();
//            for (int i = 0; i < microPersonAttributes.length; i++){
//                String attributeCode = codePersonAttributes[i];
//                int value = noDatas.get(id).get(attributeCode);
//                nos.setValueAt(row, attributeCode, value);
//            }
//            nos.setValueAt(row,1,id);
//            row++;
//
//        }
//        SiloUtil.writeTableDataSet(nos, "input/syntheticPopulation/noData.csv");
//        logger.info("Finished reading " + recCount + " persons. ");
//
//    }
//
//    private void readHouseholds() {
//
//        String fileName = "input/syntheticPopulation/newHouseholds.csv";
//
//        households = new HashMap<>();
//        String recString = "";
//        int recCount = 0;
//        try {
//            BufferedReader in = new BufferedReader(new FileReader(fileName));
//            recString = in.readLine();
//
//            // read header
//            String[] header = recString.split(",");
//            int posId = SiloUtil.findPositionInArray("X.x", header);
//            HashMap<String, Integer> positionAttribute = new HashMap<>();
//            for (Map.Entry<String, String> pairCode : attributeCodeToMicroHousehold.entrySet()){
//                String attributeMicro = pairCode.getValue();
//                positionAttribute.put(attributeMicro, SiloUtil.findPositionInArray(attributeMicro, header));
//            }
//
//            // read line
//            while ((recString = in.readLine()) != null) {
//                recCount++;
//                String[] lineElements = recString.split(",");
//                int idhH = Integer.parseInt(lineElements[posId]);
//                HashMap<String, Integer> attributeMap = new HashMap<>();
//                for (Map.Entry<String, String> pairCode : attributeCodeToMicroHousehold.entrySet()){
//                    String attribute = pairCode.getKey();
//                    String attributeMicro =  pairCode.getValue();
//                    String valueMicroData = lineElements[positionAttribute.get(attributeMicro)];
//                    int valueCode = attributesMicroHousehold.get(attributeMicro).get(valueMicroData);
//                    attributeMap.put(attribute, valueCode);
//                }
//                households.put(idhH, attributeMap);
//            }
//        } catch (IOException e) {
//            logger.fatal("IO Exception caught reading synpop household file: " + fileName);
//            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
//        }
//        logger.info("Finished reading " + recCount + " households.");
//
//    }
//
//
//    private void createMicroHouseholdsAndMicroPersons(int personCount){
//        //method to create the micro households with all the values
//        logger.info("   Creating frequency matrix and converting micro data to code values");
//
//        initializeMicroData(personCount);
//        for (Map.Entry<Integer, HashMap<String, Integer>> householdEntry : households.entrySet()){
//            Integer hhId = householdEntry.getKey();
//            updateMicroHouseholds(hhId);
//            updateMicroPersons(hhId);
//        }
//        substituteZeros();
//
//        String fileName = "input/syntheticPopulation/householdsMicroCode.csv";
//        SiloUtil.writeTableDataSet(microDataHousehold, fileName);
//        String personName = "input/syntheticPopulation/personsMicroCode.csv";
//        SiloUtil.writeTableDataSet(microDataPerson, personName);
//        String freqName = "input/syntheticPopulation/frequencyMatrix.csv";
//        SiloUtil.writeTableDataSet(frequencyMatrix, freqName);
//
//    }
//
//    private void substituteZeros() {
//        //method to substitute zeros on the frequency matrix by very little number
//        //required for IPU division by zeros
//
//        for (int i = 1; i <= frequencyMatrix.getRowCount(); i++) {
//            for (int j = 1; j <= frequencyMatrix.getColumnCount(); j++) {
//                if (frequencyMatrix.getValueAt(i, j) == 0) {
//                    frequencyMatrix.setValueAt(i, j, 0.0000001f);
//                }
//            }
//        }
//
//        for (int i = 1; i <= marginalsCounty.getRowCount(); i++){
//            for (int j = 1; j <= marginalsCounty.getColumnCount(); j++){
//                if (marginalsCounty.getValueAt(i, j) == 0){
//                    marginalsCounty.setValueAt(i, j, 0.1f);
//                }
//            }
//        }
//
//        for (int i = 1; i <= marginalsMunicipality.getRowCount(); i++){
//            for (int j = 1; j <= marginalsMunicipality.getColumnCount(); j++){
//                if (marginalsMunicipality.getValueAt(i, j) == 0){
//                    marginalsMunicipality.setValueAt(i, j, 0.1f);
//                }
//            }
//        }
//
//    }
//
//    private void updateMicroHouseholds(Integer hhId) {
//
//        for (int i = 0; i < codeHouseholdAttributes.length; i++) {
//            String labelCode = codeHouseholdAttributes[i];
//            String labelMicro = microHouseholdAttributes[i];
//            int valueMicro = households.get(hhId).get(labelCode);
//            microDataHousehold.setIndexedValueAt(hhId, labelMicro, valueMicro);
//            if (attributeCodeValues.containsKey(labelCode)) {
//                String labelFrequency = labelCode + valueMicro;
//                frequencyMatrix.setIndexedValueAt(hhId, labelFrequency, 1);
//            }
//        }
//    }
//
//
//    private void updateMicroPersons(Integer hhId) {
//
//        HashMap<Integer, HashMap<String, Integer>> persons = personsInHouseholds.get(hhId);
//        frequencyMatrix.setIndexedValueAt(hhId, "population", persons.keySet().size());
//        for (Map.Entry<Integer, HashMap<String, Integer>> attributeMap : persons.entrySet()) {
//            Integer personId = attributeMap.getKey();
//            for (int i = 0; i < codePersonAttributes.length; i++){
//                String labelMicro = microPersonAttributes[i];
//                String labelCode = codePersonAttributes[i];
//                int value = attributeMap.getValue().get(labelCode);
//                microDataPerson.setIndexedValueAt(personId, labelMicro, value);
//                if (attributeCodeValues.containsKey(labelCode) & value > -1) {
//                    String labelFrequency = labelCode + value;
//                    int previousFrequency = (int) frequencyMatrix.getIndexedValueAt(hhId, labelFrequency);
//                    frequencyMatrix.setIndexedValueAt(hhId, labelFrequency, previousFrequency + 1);
//                }
//            }
//        }
//    }
//
//
//    private void initializeMicroData(int personCount) {
//
//        microDataHousehold = new TableDataSet();
//        microDataPerson = new TableDataSet();
//        frequencyMatrix = new TableDataSet();
//
//        int size = households.keySet().size();
//        int[] ids = new int[size];
//        int i = 0;
//        int j = 0;
//        int[] ppIds = new int[personCount];
//        for (Map.Entry<Integer, HashMap<String, Integer>> pairCode : households.entrySet()){
//            ids[i] = pairCode.getKey();
//            for (Map.Entry<Integer, HashMap<String, Integer>> personCode : personsInHouseholds.get(pairCode.getKey()).entrySet()){
//                ppIds[j] = personCode.getKey();
//                j++;
//            }
//            i++;
//        }
//        frequencyMatrix.appendColumn(ids, "ID");
//        frequencyMatrix.appendColumn(SiloUtil.createArrayWithValue(frequencyMatrix.getRowCount(), 1),"hhTotal");
//        frequencyMatrix.appendColumn(SiloUtil.createArrayWithValue(frequencyMatrix.getRowCount(), 0),"population");
//        frequencyMatrix.buildIndex(frequencyMatrix.getColumnPosition("ID"));
//        microDataHousehold.appendColumn(ids, "ID");
//        microDataHousehold.buildIndex(microDataHousehold.getColumnPosition("ID"));
//        microDataPerson.appendColumn(ppIds, "ID");
//        microDataPerson.buildIndex(microDataPerson.getColumnPosition("ID"));
//
//        for (Map.Entry<String, HashMap<Integer, Integer>> pairCode : attributeCodeValues.entrySet()) {
//            String attribute = pairCode.getKey();
//            Set<Integer> levels = pairCode.getValue().keySet();
//            Iterator<Integer> iterator = levels.iterator();
//            while(iterator.hasNext()){
//                Integer setElement = iterator.next();
//                String label = attribute + setElement;
//                int[] dummy = SiloUtil.createArrayWithValue(frequencyMatrix.getRowCount(), 0);
//                frequencyMatrix.appendColumn(dummy, label);
//            }
//        }
//
//        for (Map.Entry<String, HashMap<String, Integer>> pairCode : attributesMicroHousehold.entrySet()){
//            String attribute = pairCode.getKey();
//            int[] dummy = SiloUtil.createArrayWithValue(microDataHousehold.getRowCount(), 0);
//            microDataHousehold.appendColumn(dummy, attribute);
//        }
//
//        for (Map.Entry<String, HashMap<String, Integer>> pairCode : attributesMicroPerson.entrySet()){
//            String attribute = pairCode.getKey();
//            int[] dummy = SiloUtil.createArrayWithValue(microDataPerson.getRowCount(), 0);
//            microDataPerson.appendColumn(dummy, attribute);
//        }
//    }
//
//
//    private void checkHouseholdRelationship(){
//        //method to check how household members are related
//
//        int[] dummy = SiloUtil.createArrayWithValue(microDataHousehold.getRowCount(), 0);
//        int[] dummy1 = SiloUtil.createArrayWithValue(microDataHousehold.getRowCount(), 0);
//        microDataHousehold.appendColumn(dummy,"nonClassifiedMales");
//        microDataHousehold.appendColumn(dummy1,"nonClassifiedFemales");
//        int[] dummy2 = SiloUtil.createArrayWithValue(microDataPerson.getRowCount(), -1);
//        int[] dummy3 = SiloUtil.createArrayWithValue(microDataPerson.getRowCount(), 0);
//        microDataPerson.appendColumn(dummy2, "personRole");
//        microDataPerson.appendColumn(dummy3, "rearrangedRole");
//
//
//        for (int k = 1; k <= microDataHousehold.getRowCount(); k++){
//
//            int hhSize = (int) microDataHousehold.getValueAt(k,"hhSize");
//            int firstMember = (int) microDataHousehold.getValueAt(k, "personCount");
//
//            //single person household -> all set as single directly
//            if (hhSize == 1){
//                microDataPerson.setValueAt(firstMember,"personRole",1); //set as single directly
//
//                //multiperson household
//            } else {
//
//                //Create the maps to store the classified members of the households
//                HashMap<Integer, Integer> childrenInHousehold = new HashMap<>();
//                HashMap<String, HashMap<Integer, Integer>> noClass = new HashMap<>();
//                HashMap<String, HashMap<Integer, Integer>> singles = new HashMap<>();
//                HashMap<String, HashMap<Integer, Integer>> married = new HashMap<>();
//
//                //direct classification of the members of the household
//                for (int j = 0; j < hhSize; j++) {
//                    int row = firstMember + j;
//                    int spouseInHousehold = (int) microDataPerson.getValueAt(row, "spouseInHousehold");
//                    int relationToHead = (int) microDataPerson.getValueAt(row, "personStatus");
//                    int maritalStatus = (int) microDataPerson.getValueAt(row, "marriage");
//                    int age = (int) microDataPerson.getValueAt(row, "age");
//                    int gender = (int) microDataPerson.getValueAt(row, "gender");
//
//                    if (relationToHead == 3) {
//                        childrenInHousehold.put(row, age); //children -> children
//                    } else if (maritalStatus == 2) {
//                        if (spouseInHousehold == 1) {
//                            married = updateInnerMap(married, gender, age, row); //married and spouse in household -> married
//                        } else if (spouseInHousehold == -5) {
//                            singles = updateInnerMap(singles, gender, age, row); //married and spouse not in household -> single
//                        } else {
//                            noClass = updateInnerMap(noClass, gender, age, row); //need further classification at the household level. Look for cohabitation
//                        }
//                    } else if (maritalStatus > 4) {
//                        singles = updateInnerMap(singles, gender, age, row); //same gender married, divorced or widow -> single
//                    } else if (maritalStatus == 3 & spouseInHousehold == -5) {
//                        singles = updateInnerMap(singles, gender, age, row); //widow and spouse not in household -> single
//                    } else {
//                        noClass = updateInnerMap(noClass, gender, age, row); //need further classification at the household level. Look for cohabitation
//                    }
//                }
//                int[] countNotClassified = new int[2];
//                int checkForCohabitation = 0;
//                HashMap<Integer, Integer> notClassifiedMales = noClass.get("male");
//                if (notClassifiedMales != null){
//                    countNotClassified[0] = notClassifiedMales.size();
//                    checkForCohabitation = 1;
//                }
//                HashMap<Integer, Integer> notClassifiedFemales = noClass.get("female");
//                if (notClassifiedFemales != null){
//                    countNotClassified[1] = notClassifiedFemales.size();
//                    checkForCohabitation = 1;
//                }
//                if (checkForCohabitation == 1) {
//                    //look for cohabitation
//                    if (countNotClassified[0] == 1 & countNotClassified[1] == 1) { //one male and one female were not classified
//                        //check age difference for possible marriage
//                        int rowMale = (int) notClassifiedMales.keySet().toArray()[0];
//                        int rowFemale = (int) notClassifiedFemales.keySet().toArray()[0];
//                        int ageMale = notClassifiedMales.get(rowMale);
//                        int ageFemale = notClassifiedFemales.get(rowFemale);
//                        int diffAge = Math.abs(ageFemale - ageMale);
//                        double threshold = (20 - diffAge) / 10;
//                        if (SiloUtil.getRandomNumberAsDouble() < threshold) {
//                            married = updateInnerMap(married, 1, ageMale, rowMale);
//                            married = updateInnerMap(married, 2, ageFemale, rowFemale);
//                        } else {
//                            singles = updateInnerMap(singles, 1, ageMale, rowMale);
//                            singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
//                        }
//                    } else if (countNotClassified[0] == 0 & countNotClassified[1] > 1) { //only females were not classified
//                        //set all of them as single
//                        for (Map.Entry<Integer, Integer> pair : notClassifiedFemales.entrySet()) {
//                            int newRow = pair.getKey();
//                            int newAge = pair.getValue();
//                            singles = updateInnerMap(singles, 2, newAge, newRow);
//                        }
//                    } else if (countNotClassified[1] == 0 & countNotClassified[0] > 1) { //only males were not classified
//                        // set all of them as single
//                        for (Map.Entry<Integer, Integer> pair : notClassifiedMales.entrySet()) {
//                            int newRow = pair.getKey();
//                            int newAge = pair.getValue();
//                            singles = updateInnerMap(singles, 1, newAge, newRow);
//                        }
//                    } else if (countNotClassified[1] - countNotClassified[0] == 1) {  //one female was not classified
//                        //check for a possible single male to get married (408 households with)
//                        int rowFemale = (int) notClassifiedFemales.keySet().toArray()[0];
//                        int ageFemale = notClassifiedFemales.get(rowFemale);
//                        HashMap<Integer, Integer> singleMale = singles.get("male");
//                        if (singleMale == null & notClassifiedMales == null) { //no possible single male to marry -> set as single
//                            singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
//                        } else if (singleMale != null) { //check for marriage with the male with the lowest age difference
//                            int minDiff = 20;
//                            int rowMarried = 0;
//                            int[] rowSingles = new int[singleMale.size()];
//                            for (Map.Entry<Integer, Integer> pair : singleMale.entrySet()) {
//                                int age = pair.getValue();
//                                if (Math.abs(ageFemale - age) < minDiff) {
//                                    minDiff = Math.abs(ageFemale - age);
//                                    rowMarried = pair.getKey();
//                                }
//                            }
//                            if (rowMarried > 0) {
//                                double threshold = (20 - minDiff) / 10;
//                                if (SiloUtil.getRandomNumberAsDouble() < threshold) {
//                                    married = updateInnerMap(married, 1, minDiff, rowMarried);
//                                    married = updateInnerMap(married, 2, ageFemale, rowFemale);
//                                } else {
//                                    singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
//                                }
//                            } else {
//                                singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
//                            }
//                        } else {
//                            int minDiff = 20;
//                            int rowMarried = 0;
//                            for (Map.Entry<Integer, Integer> pair : notClassifiedMales.entrySet()) {
//                                int age = pair.getValue();
//                                if (Math.abs(ageFemale - age) < minDiff) {
//                                    minDiff = Math.abs(ageFemale - age);
//                                    rowMarried = pair.getKey();
//                                }
//                            }
//                            if (rowMarried > 0) {
//                                double threshold = (20 - minDiff) / 10;
//                                if (SiloUtil.getRandomNumberAsDouble() < threshold) {
//                                    married = updateInnerMap(married, 1, minDiff, rowMarried);
//                                    married = updateInnerMap(married, 2, ageFemale, rowFemale);
//                                } else {
//                                    singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
//                                    singles = updateInnerMap(singles, 1, minDiff, rowMarried);
//                                }
//                            } else {
//                                singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
//                                for (int i = 0; i < notClassifiedMales.keySet().toArray().length; i++){
//                                    int rowMale = (int) notClassifiedMales.keySet().toArray()[i];
//                                    singles = updateInnerMap(singles, 1, ageFemale, rowMale);
//                                }
//                            }
//                            if (notClassifiedFemales.keySet().toArray().length > 1){
//                                for (int i = 1; i < notClassifiedFemales.keySet().toArray().length; i++){
//                                    rowFemale = (int) notClassifiedFemales.keySet().toArray()[i];
//                                    singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
//                                }
//                            }
//                        }
//                    } else if (countNotClassified[0] - countNotClassified[1] == 1) { //check for a possible single female to get married (94 households)
//                        int rowMale = (int) notClassifiedMales.keySet().toArray()[0];
//                        int ageMale = notClassifiedMales.get(rowMale);
//                        HashMap<Integer, Integer> singleFemale = singles.get("female");
//                        if (singleFemale == null & notClassifiedFemales == null) { //no possible single female to marry -> set as single
//                            singles = updateInnerMap(singles, 1, ageMale, rowMale);
//                        } else if (singleFemale != null){ //check for marriage with the female with the lowest age difference
//                            int minDiff = 20;
//                            int rowMarried = 0;
//                            for (Map.Entry<Integer, Integer> pair : singleFemale.entrySet()) {
//                                int age = pair.getValue();
//                                if (Math.abs(ageMale - age) < minDiff) {
//                                    minDiff = Math.abs(ageMale - age);
//                                    rowMarried = pair.getKey();
//                                }
//                            }
//                            if (rowMarried > 0) {
//                                double threshold = (20 - minDiff) / 10;
//                                if (SiloUtil.getRandomNumberAsDouble() < threshold) {
//                                    married = updateInnerMap(married, 1, ageMale, rowMale);
//                                    married = updateInnerMap(married, 2, minDiff, rowMarried);
//                                } else {
//                                    singles = updateInnerMap(singles, 1, ageMale, rowMale);
//                                }
//                            } else {
//                                singles = updateInnerMap(singles, 1, ageMale, rowMale);
//                            }
//                        } else {
//                            int minDiff = 20;
//                            int rowMarried = 0;
//                            for (Map.Entry<Integer, Integer> pair : notClassifiedFemales.entrySet()) {
//                                int age = pair.getValue();
//                                if (Math.abs(ageMale - age) < minDiff) {
//                                    minDiff = Math.abs(ageMale - age);
//                                    rowMarried = pair.getKey();
//                                }
//                            }
//                            if (rowMarried > 0) {
//                                double threshold = (20 - minDiff) / 10;
//                                if (SiloUtil.getRandomNumberAsDouble() < threshold) {
//                                    married = updateInnerMap(married, 1, ageMale, rowMale);
//                                    married = updateInnerMap(married, 2, minDiff, rowMarried);
//                                } else {
//                                    singles = updateInnerMap(singles, 1, ageMale, rowMale);
//                                    singles = updateInnerMap(singles, 2, minDiff, rowMarried);
//                                }
//                            } else {
//                                singles = updateInnerMap(singles, 1, ageMale, rowMale);
//                                for (int i = 0; i < notClassifiedFemales.keySet().toArray().length; i++){
//                                    rowMale = (int) notClassifiedFemales.keySet().toArray()[i];
//                                    singles = updateInnerMap(singles, 2, ageMale, rowMale);
//                                }
//                            }
//                            if (notClassifiedMales.keySet().toArray().length > 1){
//                                for (int i = 1; i < notClassifiedMales.keySet().toArray().length; i++){
//                                    rowMale = (int) notClassifiedMales.keySet().toArray()[i];
//                                    singles = updateInnerMap(singles, 1, ageMale, rowMale);
//                                }
//                            }
//                        }
//                    } else {
//                        logger.info("   Case without treatment. Please check this household " + k);
//                    }
//                }
//                setRoles(singles, married, childrenInHousehold, noClass);
//            }
//        }
//        //double checking for persons that are not classified
//        for (int i = 1; i <= microDataPerson.getRowCount(); i++){
//            if (microDataPerson.getValueAt(i,"personRole") == -1){
//                microDataPerson.setValueAt(i, "personRole", 1);
//            }
//        }
//
//        String hhFileName = ("microData/interimFiles/microHouseholds2.csv");
//        SiloUtil.writeTableDataSet(microDataHousehold, hhFileName);
//
//        String ppFileName = ("microData/interimFiles/microPerson2.csv");
//        SiloUtil.writeTableDataSet(microDataPerson, ppFileName);
//    }
//
//    private void runIPUbyCityAndCounty(){
//        //IPU process for independent municipalities (only household attributes)
//        logger.info("   Starting to prepare the data for IPU");
//
//
//        //Read the frequency matrix
//        int[] microDataIds = frequencyMatrix.getColumnAsInt("ID");
//        frequencyMatrix.buildIndex(frequencyMatrix.getColumnPosition("ID"));
//
//
//        //Create the weights table (for all the municipalities)
//        TableDataSet weightsMatrix = new TableDataSet();
//        weightsMatrix.appendColumn(microDataIds,"ID");
//
//        //Create errors by county
//        TableDataSet errorsCounty = new TableDataSet();
//        TableDataSet errorsMunicipality = new TableDataSet();
//        TableDataSet errorsSummary = new TableDataSet();
//        String[] labels = new String[]{"error", "iterations","time"};
//        errorsCounty = initializeErrors(errorsCounty, attributesCounty, countyID);
//        errorsMunicipality = initializeErrors(errorsMunicipality, attributesMunicipality, cityID);
//        errorsSummary = initializeErrors(errorsSummary, labels, countyID);
//
//        //For each county---------------------------------------------
//
//        for (int county : counties) {
//
//            long startTime = System.nanoTime();
//            municipalities = municipalitiesByCounty.get(county);
//
//            //weights, values, control totals
//            Map<Integer, double[]> weightsByMun = Collections.synchronizedMap(new HashMap<>());
//            Map<Integer, double[]> minWeightsByMun = Collections.synchronizedMap(new HashMap<>());
//            Map<String, int[]> valuesByHousehold = Collections.synchronizedMap(new HashMap<>());
//            Map<String, Integer> totalCounty = Collections.synchronizedMap(new HashMap<>());
//            Map<Integer, Map<String, Integer>> totalMunicipality = Collections.synchronizedMap(new HashMap<>());
//            Map<Integer, Map<String, Double>> errorByMun = Collections.synchronizedMap(new HashMap<>());
//            Map<String, Double> errorByRegion = Collections.synchronizedMap(new HashMap<>());
//            double weightedSum0 = 0f;
//
//            //parameters of the IPU
//            int finish = 0;
//            int iteration = 0;
//            double maxError = 0.00001;
//            int maxIterations = 500;
//            double minError = 100000;
//            double initialError = 10000;
//            double improvementError = 0.001;
//            double iterationError = 2;
//            double increaseError = 1.05;
//
//            //initialize errors, considering the first weight (equal to 1)
//            for (String attribute : attributesCounty) {
//                int[] values = new int[frequencyMatrix.getRowCount()];
//                for (int i = 1; i <= frequencyMatrix.getRowCount(); i++) {
//                    values[i - 1] = (int) frequencyMatrix.getValueAt(i, attribute);
//                    if (attribute.equals(attributesCounty[0])) {
//                        weightedSum0 = weightedSum0 + values[i - 1] * municipalities.size();
//                    }
//                }
//                valuesByHousehold.put(attribute, values);
//                int total = (int) marginalsCounty.getIndexedValueAt(county, attribute);
//                totalCounty.put(attribute, total);
//                errorByRegion.put(attribute, 0.);
//            }
//            for (String attribute : attributesMunicipality) {
//                int[] values = new int[frequencyMatrix.getRowCount()];
//                for (int i = 1; i <= frequencyMatrix.getRowCount(); i++) {
//                    values[i - 1] = (int) frequencyMatrix.getValueAt(i, attribute);
//                }
//                valuesByHousehold.put(attribute, values);
//                Iterator<Integer> iterator = municipalities.iterator();
//                while (iterator.hasNext()) {
//                    Integer municipality = iterator.next();
//                    double[] dummy = SiloUtil.createArrayWithValue(frequencyMatrix.getRowCount(), 1.);
//                    weightsByMun.put(municipality, dummy);
//                    double[] dummy1 = SiloUtil.createArrayWithValue(frequencyMatrix.getRowCount(), 1.);
//                    minWeightsByMun.put(municipality, dummy1);
//                    if (totalMunicipality.containsKey(municipality)) {
//                        Map<String, Integer> innerMap = totalMunicipality.get(municipality);
//                        innerMap.put(attribute, (int) marginalsMunicipality.getIndexedValueAt(municipality, attribute));
//                        totalMunicipality.put(municipality, innerMap);
//                        Map<String, Double> inner1 = errorByMun.get(municipality);
//                        inner1.put(attribute, 0.);
//                        errorByMun.put(municipality, inner1);
//                    } else {
//                        HashMap<String, Integer> inner = new HashMap<>();
//                        inner.put(attribute, (int) marginalsMunicipality.getIndexedValueAt(municipality, attribute));
//                        totalMunicipality.put(municipality, inner);
//                        HashMap<String, Double> inner1 = new HashMap<>();
//                        inner1.put(attribute, 0.);
//                        errorByMun.put(municipality, inner1);
//                    }
//                }
//            }
//
//            //for each iteration
//            while (finish == 0 & iteration < maxIterations) {
//
//                //For each municipality, obtain the weight matching each attribute
//                ConcurrentExecutor executor = ConcurrentExecutor.cachedService();
//                Iterator<Integer> iterator = municipalities.iterator();
//                while (iterator.hasNext()) {
//                    Integer municipality = iterator.next();
//                    executor.addTaskToQueue(() -> {
//                        for (String attribute : attributesMunicipality) {
//                            double weightedSumMunicipality = SiloUtil.sumProduct(weightsByMun.get(municipality), valuesByHousehold.get(attribute));
//                            if (weightedSumMunicipality > 0.001) {
//                                double updatingFactor = totalMunicipality.get(municipality).get(attribute) / weightedSumMunicipality;
//                                double[] previousWeights = weightsByMun.get(municipality);
//                                int[] values = valuesByHousehold.get(attribute);
//                                double[] updatedWeights = new double[previousWeights.length];
//                                IntStream.range(0, previousWeights.length).parallel().forEach(id -> updatedWeights[id] = multiplyIfNotZero(previousWeights[id], values[id], updatingFactor));
//                                weightsByMun.put(municipality, updatedWeights);
//                            }
//                        }
//                        return null;
//                    });
//                }
//                executor.execute();
//
//
//                //For each attribute at the region level (landkreise), we obtain the weights
//                double weightedSumRegion = 0;
//                for (String attribute : attributesCounty) {
//                    Iterator<Integer> iterator1 = municipalities.iterator();
//                    while (iterator1.hasNext()) {
//                        Integer municipality = iterator1.next();
//                        weightedSumRegion = weightedSumRegion + SiloUtil.sumProduct(weightsByMun.get(municipality), valuesByHousehold.get(attribute));
//                    }
//                    if (weightedSumRegion > 0.001) {
//                        double updatingFactor = totalCounty.get(attribute) / weightedSumRegion;
//                        Iterator<Integer> iterator2 = municipalities.iterator();
//                        while (iterator2.hasNext()) {
//                            Integer municipality = iterator2.next();
//                            double[] previousWeights = weightsByMun.get(municipality);
//                            int[] values = valuesByHousehold.get(attribute);
//                            double[] updatedWeights = new double[previousWeights.length];
//                            IntStream.range(0, previousWeights.length).parallel().forEach(id -> updatedWeights[id] = multiplyIfNotZero(previousWeights[id], values[id], updatingFactor));
//                            weightsByMun.put(municipality, updatedWeights);
//                        }
//                    }
//                    //logger.info("Attribute " + attribute + ": sum is " + weightedSumRegion);
//                    weightedSumRegion = 0;
//                }
//
//
//                //obtain the errors by municipality
//                double averageErrorIteration = 0.;
//                int counter = 0;
//                ConcurrentExecutor executor1 = ConcurrentExecutor.cachedService();
//                Iterator<Integer> iterator1 = municipalities.iterator();
//                while (iterator1.hasNext()){
//                    Integer municipality = iterator1.next();
//                    Map<String, Double> errorsByMunicipality = Collections.synchronizedMap(new HashMap<>());
//                    executor1.addTaskToQueue(() ->{
//                        for (String attribute : attributesMunicipality){
//                            double weightedSumMunicipality = SiloUtil.sumProduct(weightsByMun.get(municipality), valuesByHousehold.get(attribute));
//                            double errorByAttributeAndMunicipality = 0;
//                            if (totalMunicipality.get(municipality).get(attribute) > 0){
//                                errorByAttributeAndMunicipality = Math.abs((weightedSumMunicipality - totalMunicipality.get(municipality).get(attribute)) / totalMunicipality.get(municipality).get(attribute));
//                                errorsByMunicipality.put(attribute, errorByAttributeAndMunicipality);
//                            }
//                        }
//                        return null;
//                    });
//                    errorByMun.put(municipality, errorsByMunicipality);
//                }
//                executor1.execute();
//                for (int municipality : municipalities) {
//                    averageErrorIteration = averageErrorIteration + errorByMun.get(municipality).values().stream().mapToDouble(Number::doubleValue).sum();
//                    counter = counter + errorByMun.get(municipality).entrySet().size();
//                }
//
//                //obtain errors by county
//                for (String attributeC : attributesCounty) {
//                    double errorByCounty = 0.;
//                    double weightedSumCounty = 0.;
//                    if (totalCounty.get(attributeC) > 0) {
//                        Iterator<Integer> iterator3 = municipalities.iterator();
//                        while (iterator3.hasNext()) {
//                            Integer municipality = iterator3.next();
//                            double weightedSum = SiloUtil.sumProduct(weightsByMun.get(municipality), valuesByHousehold.get(attributeC));
//                            weightedSumCounty += weightedSum;
//                        }
//                        errorByCounty = errorByCounty + Math.abs((weightedSumCounty - totalCounty.get(attributeC)) / totalCounty.get(attributeC));
//                        errorByRegion.put(attributeC, errorByCounty);
//                        averageErrorIteration += errorByCounty;
//                        counter++;
//                    }
//                }
//
//                averageErrorIteration = averageErrorIteration / counter;
//                logger.info("   County " + county + ". Iteration " + iteration + ". Average error: " + averageErrorIteration * 100 + " %.");
//
//                //Stopping criteria: exceeds the maximum number of iterations or the maximum error is lower than the threshold
//                if (averageErrorIteration < maxError) {
//                    finish = 1;
//                    logger.info("   IPU finished after :" + iteration + " iterations with a minimum average error of: " + minError * 100 + " %.");
//                    iteration = maxIterations + 1;
//                } else if ((iteration / iterationError) % 1 == 0) {
//                    if (Math.abs((initialError - averageErrorIteration) / initialError) < improvementError) {
//                        finish = 1;
//                        logger.info("   IPU finished after " + iteration + " iterations because the error does not improve. The minimum average error is: " + minError * 100 + " %.");
//                    } else if (averageErrorIteration == 0) {
//                        finish = 1;
//                        logger.info("   IPU finished after " + iteration + " iterations because the error starts increasing. The minimum average error is: " + minError * 100 + " %.");
//                    } else {
//                        initialError = averageErrorIteration;
//                        iteration = iteration + 1;
//                    }
//                } else if (iteration == maxIterations) {
//                    finish = 1;
//                    logger.info("   IPU finished after the total number of iterations. The minimum average error is: " + minError * 100 + " %.");
//                } else {
//                    iteration = iteration + 1;
//                }
//
//                if (averageErrorIteration < minError) {
//                    for (int municipality : municipalities) {
//                        double[] minW = weightsByMun.get(municipality);
//                        minWeightsByMun.put(municipality, minW);
//                    }
//                    minError = averageErrorIteration;
//                }
//                long estimatedTime = (System.nanoTime() - startTime) / 1000000000;
//                errorsSummary.setIndexedValueAt(county, "error", (float) minError);
//                errorsSummary.setIndexedValueAt(county, "iterations", iteration);
//                errorsSummary.setIndexedValueAt(county, "time", estimatedTime);
//            }
//
//
//            //Write the weights after finishing IPU for each municipality (saved each time over the previous version)
//            for (int municipality : municipalities) {
//                weightsMatrix.appendColumn(minWeightsByMun.get(municipality), Integer.toString(municipality));
//            }
//
//            SiloUtil.writeTableDataSet(weightsMatrix, "input/syntheticPopulation/weights1.csv");
//
//
//            //Copy the errors per attribute
//            for (String attribute : attributesCounty) {
//                errorsCounty.setIndexedValueAt(county, attribute, errorByRegion.get(attribute).floatValue());
//            }
//            for (int municipality : municipalities) {
//                for (String attribute : attributesMunicipality) {
//                    if (totalMunicipality.get(municipality).get(attribute) > 0){
//                        errorsMunicipality.setIndexedValueAt(municipality, attribute, errorByMun.get(municipality).get(attribute).floatValue());
//                    }
//                }
//            }
//
//
//            //Write the weights after finishing IPU for each county
//            SiloUtil.writeTableDataSet(weightsMatrix, rb.getString(PROPERTIES_WEIGHTS_MATRIX));
//            SiloUtil.writeTableDataSet(errorsMunicipality, "microData/interimFiles/errorsHouseholdIPU.csv");
//            SiloUtil.writeTableDataSet(errorsCounty, "microData/interimFiles/errorsRegionIPU.csv");
//            SiloUtil.writeTableDataSet(errorsSummary, "microData/interimFiles/summaryIPU.csv");
//        }
//        //Write the weights final table
//        weightsTable = weightsMatrix;
//        weightsTable.buildIndex(weightsTable.getColumnPosition("ID"));
//
//        logger.info("   IPU finished");
//    }
//
//    private TableDataSet initializeErrors(TableDataSet errors, String[] attributes, int[] ids) {
//        //method to initialize the error matrix
//        errors.appendColumn(ids, "ID");
//        for (String attribute : attributes){
//            float[] dummy = SiloUtil.createArrayWithValue(errors.getRowCount(), 0f);
//            errors.appendColumn(dummy, attribute);
//        }
//        errors.buildIndex(errors.getColumnPosition("ID"));
//        return errors;
//    }
//
//
//    private void readIPU(){
//        //Read entry data for household selection
//        logger.info("   Reading the weights matrix");
//        weightsTable = SiloUtil.readCSVfile2(rb.getString(PROPERTIES_WEIGHTS_MATRIX));
//        weightsTable.buildIndex(weightsTable.getColumnPosition("ID"));
//
//        logger.info("   Finishing reading the results from the IPU");
//    }
//
//
//    private void generateJobs(){
//        //Generate jobs file. The worker ID will be assigned later on the process "assignJobs"
//
//        logger.info("   Starting to generate jobs");
//
//        String[] jobStringType = ResourceUtil.getArray(rb, PROPERTIES_JOB_TYPES);
//        int[] rasterCellsIDs = cellsMatrix.getColumnAsInt("ID_cell");
//        JobData jobData = dataContainer.getJobData();
//
//        //For each municipality
//        for (int municipality = 0; municipality < cityID.length; municipality++) {
//            //logger.info("   Municipality " + cityID[municipality] + ". Starting to generate jobs.");
//
//            //-----------***** Data preparation *****-------------------------------------------------------------------
//            //Create local variables to avoid accessing to the same variable on the parallel processing
//            int municipalityID = cityID[municipality];
//            TableDataSet rasterCellsMatrix = cellsMatrix;
//            rasterCellsMatrix.buildIndex(rasterCellsMatrix.getColumnPosition("ID_cell"));
//
//
//            //obtain the raster cells of the municipality and their weight within the municipality
//            int[] tazInCity = cityTAZ.get(municipalityID);
//
//
//            //generate jobs
//            for (int row = 0; row < jobStringType.length; row++) {
//                String jobType = jobStringType[row];
//                int totalJobs = (int) marginalsMunicipality.getIndexedValueAt(municipalityID, jobType);
//                if (totalJobs > 0.1) {
//                    //Obtain the number of jobs of that type in each TAZ of the municipality
//                    double[] jobsInTaz = new double[tazInCity.length];
//                    for (int i = 0; i < tazInCity.length; i++) {
//                        jobsInTaz[i] = rasterCellsMatrix.getIndexedValueAt(tazInCity[i], jobType);
//                    }
//                    //Create and allocate jobs to TAZs (with replacement)
//                    for (int job = 0; job < totalJobs; job++) {
//                        int[] records = select(jobsInTaz, tazInCity);
//                        jobsInTaz[records[1]] = jobsInTaz[records[1]] - 1;
//                        int id = jobData.getNextJobId();
//                        jobData.addJob(JobUtils.getFactory().createJob(id, records[0], null, -1, jobType)); //(int id, int zone, int workerId, String type)
//                    }
//                }
//            }
//        }
//    }
//
//
//    private void assignJobs(){
//        //Method to allocate workers at workplaces
//        //todo. Things to consider:
//        //If there are no more workplaces of the specific job type, the worker is sent outside the area (workplace = -2; distance = 1000 km)
//        //Workers that also attend school are considered only as workers (educational place is not selected for them)
//
//        //Calculate distance impedance
//        alphaJob = ResourceUtil.getDoubleProperty(rb,PROPERTIES_JOB_ALPHA);
//        gammaJob = ResourceUtil.getDoubleProperty(rb,PROPERTIES_JOB_GAMMA);
//        distanceImpedance = new Matrix(distanceMatrix.getRowCount(), distanceMatrix.getColumnCount());
//        for (int i = 1; i <= distanceMatrix.getRowCount(); i ++){
//            for (int j = 1; j <= distanceMatrix.getColumnCount(); j++){
//                distanceImpedance.setValueAt(i,j,(float) Math.exp(alphaJob * Math.exp(distanceMatrix.getValueAt(i,j) * gammaJob)));
//            }
//        }
//
//
//        //Identify vacant jobs and schools by zone and type
//        identifyVacantJobsByZoneType();
//
//
//        //For validation - obtain the trip length distribution
///*        Frequency commuteDistance = new Frequency();
//        validationCommutersFlow(); //Generates the validation tabledatasets
//        int[] flow = SiloUtil.createArrayWithValue(odMunicipalityFlow.getRowCount(),0);
//        int[] flowR = SiloUtil.createArrayWithValue(odCountyFlow.getRowCount(),0);
//        int count = 0;
//        odMunicipalityFlow.appendColumn(flow,Integer.toString(count));
//        odCountyFlow.appendColumn(flowR,Integer.toString(count));*/
//
//
//        //Produce one array list with workers' ID
//        Collection<Person> persons = dataContainer.getHouseholdData().getPersons();
//        ArrayList<Person> workerArrayList = new ArrayList<>();
//        for (Person person: persons ){
//            if (person.getOccupation() == Occupation.EMPLOYED){
//                workerArrayList.add(person);
//            }
//        }
//        //Randomize the order of the worker array list
//        Collections.shuffle(workerArrayList);
//
//
//        //Job type probabilities
//        probabilitiesJob = SiloUtil.readCSVfile(rb.getString(PROPERTIES_EMPLOYMENT_BY_GENDER_EDU));
//        probabilitiesJob.buildStringIndex(1);
//
//
//        //Start the selection of the jobs in random order to avoid geographical bias
//        logger.info("   Started assigning workplaces");
//        int assignedJobs = 0;
//        RealEstateData realEstate = dataContainer.getRealEstateData();
//        JobData jobData = dataContainer.getJobData();
//        for (Person pp : workerArrayList){
//
//            //Select the zones with vacant jobs for that person, given the job type
//            int selectedJobType = selectJobType(pp);
//
//            int[] keys = idZonesVacantJobsByType.get(selectedJobType);
//            int lengthKeys = numberZonesByType.get(selectedJobType);
//            // if there are still TAZ with vacant jobs in the region, select one of them. If not, assign them outside the area
//            if (lengthKeys > 0) {
//
//                //Select the workplace location (TAZ) for that person given his/her job type
//                Household hh = pp.getHousehold();
//                int origin = realEstate.getDwelling(hh.getDwellingId()).getZoneId();
//                int[] workplace = selectWorkplace(origin, numberVacantJobsByZoneByType, keys, lengthKeys,
//                        distanceImpedance);
//
//                //Assign last vacant jobID from the TAZ
//                int jobID = idVacantJobsByZoneType.get(workplace[0])[numberVacantJobsByZoneByType.get(workplace[0]) - 1];
//
//                //Assign values to job and person
//                jobData.getJobFromId(jobID).setWorkerID(pp.getId());
//                pp.setJobTAZ(jobData.getJobFromId(jobID).getZoneId());
//                pp.setWorkplace(jobID);
//                //pp.setTravelTime(distanceMatrix.getValueAt(pp.getZone(), Job.getJobFromId(jobID).getZone()));
//
//                //For validation OD TableDataSet
///*
//                commuteDistance.addValue((int) distanceMatrix.getValueAt(pp.getZone(), Job.getJobFromId(jobID).getZone()));
//                int homeMun = (int) cellsMatrix.getIndexedValueAt(pp.getZone(), "smallID");
//                int workMun = (int) cellsMatrix.getIndexedValueAt(pp.getWorkplace(), "smallID");
//                int odPair = homeMun * 1000 + workMun;
//                odMunicipalityFlow.setIndexedValueAt(odPair,Integer.toString(count),odMunicipalityFlow.getIndexedValueAt(odPair,Integer.toString(count))+ 1);
//                homeMun = (int) cellsMatrix.getIndexedValueAt(pp.getZone(), "smallCenter");
//                workMun = (int) cellsMatrix.getIndexedValueAt(pp.getWorkplace(), "smallCenter");
//                odPair = homeMun * 1000 + workMun;
//                odCountyFlow.setIndexedValueAt(odPair,Integer.toString(count),odCountyFlow.getIndexedValueAt(odPair,Integer.toString(count))+ 1);
//*/
//
//                //Update counts of vacant jobs
//                numberVacantJobsByZoneByType.put(workplace[0], numberVacantJobsByZoneByType.get(workplace[0]) - 1);
//                numberVacantJobsByType.put(selectedJobType, numberVacantJobsByType.get(selectedJobType) - 1);
//                if (numberVacantJobsByZoneByType.get(workplace[0]) < 1) {
//                    keys[workplace[1]] = keys[numberZonesByType.get(selectedJobType) - 1];
//                    idZonesVacantJobsByType.put(selectedJobType, keys);
//                    numberZonesByType.put(selectedJobType, numberZonesByType.get(selectedJobType) - 1);
//                    if (numberZonesByType.get(selectedJobType) < 1) {
//                        int w = 0;
//                        while (w < jobStringTypes.length & selectedJobType > jobIntTypes.get(jobStringTypes[w])) {
//                            w++;
//                        }
//                        jobIntTypes.remove(jobStringTypes[w]);
//                        jobStringTypes[w] = jobStringTypes[jobStringTypes.length - 1];
//                        jobStringTypes = SiloUtil.removeOneElementFromZeroBasedArray(jobStringTypes, jobStringTypes.length - 1);
//
//                    }
//                }
//                //logger.info("   Job " + assignedJobs + " assigned at " + workplace[0]);
//                assignedJobs++;
//
//            } else { //No more vacant jobs in the study area. This person will work outside the study area
//                pp.setWorkplace(-2);
//                //pp.setTravelTime(1000);
//                logger.info("   No more jobs available of " + selectedJobType + " class. Person " + pp.getId() + " has workplace outside the study area.");
//            }
//        }
//
//
//        //For validation - trip length distribution
//        //checkTripLengthDistribution(commuteDistance, alphaJob, gammaJob, "microData/interimFiles/tripLengthDistributionWork.csv", 1); //Trip length frequency distribution
//        //checkodMatrix(odMunicipalityFlow, alphaJob, gammaJob, count,"microData/interimFiles/odMunicipalityDifference.csv");
//        //SiloUtil.writeTableDataSet(odMunicipalityFlow,"microData/interimFiles/odMunicipalityFlow.csv");
//        //SiloUtil.writeTableDataSet(odCountyFlow,"microData/interimFiles/odRegionFlow.csv");
//        //count++;
//
//    }
//
//
//    private void assignSchools(){
//        //method to assign the school location for students. They should be registered on the microdata as students
//
//        //todo. Things to consider:
//        //The location of the school is stored under "schoolplace location"
//        //Students from Berufschule are considered to be working full-time and therefore they don't attend class
//        //If there are no more school places for the student, they are sent outside the area (schoolplace = -2)
//        //For the following years, we school transition should be accomplished
//
//        logger.info("   Started assigning schools");
//        int count = 0;
//
//        //Calculate distance impedance for students
//        double alphaUniversity = ResourceUtil.getDoubleProperty(rb, PROPERTIES_UNIVERSITY_ALPHA);
//        double gammaUniversity = ResourceUtil.getDoubleProperty(rb, PROPERTIES_UNIVERSITY_GAMMA);
//        Matrix universityDistanceImpedance = new Matrix(distanceMatrix.getRowCount(), distanceMatrix.getColumnCount());
//        Matrix schoolDistanceImpedance = new Matrix(distanceMatrix.getRowCount(), distanceMatrix.getColumnCount());
//        for (int i = 1; i <= distanceMatrix.getRowCount(); i++) {
//            for (int j = 1; j <= distanceMatrix.getColumnCount(); j++) {
//                universityDistanceImpedance.setValueAt(i, j, (float) Math.exp(alphaUniversity * Math.exp(distanceMatrix.getValueAt(i, j) * gammaUniversity)));
//                schoolDistanceImpedance.setValueAt(i, j, distanceMatrix.getValueAt(i, j));
//            }
//        }
//
//
//        //Identify vacant schools by zone and type
//        identifyVacantSchoolsByZoneByType();
//
//
//        //For validation - obtain the trip length distribution
//        Frequency travelSecondary = new Frequency();
//        Frequency travelUniversity = new Frequency();
//        Frequency travelPrimary = new Frequency();
//        validationCommutersFlow(); //Generates the validation tabledatasets
//        int[] flow = SiloUtil.createArrayWithValue(odMunicipalityFlow.getRowCount(),0);
//        odMunicipalityFlow.appendColumn(flow,Integer.toString(count));
//
//
//        //Produce one array list with students' ID
//        Map<Integer, Person> personMap = (Map<Integer, Person>) dataContainer.getHouseholdData().getPersons();
//        ArrayList<Person> studentArrayList = new ArrayList<>();
//        int[] studentsByType2 = new int[schoolTypes.length];
//        for (Map.Entry<Integer, Person> pair : personMap.entrySet()) {
//            int school = pair.getValue().getSchoolType();
//            if (school > 0) { //They are studying
//                studentArrayList.add(pair.getValue());
//                studentsByType2[school - 1] = studentsByType2[school - 1] + 1;
//            }
//        }
//        //Randomize the order of the students
//        Collections.shuffle(studentArrayList);
//
//
//        //Start the selection of schools in random order to avoid geographical bias
//        logger.info("   Started assigning schools");
//        int assignedSchools = 0;
//        RealEstateData realEstate = dataContainer.getRealEstateData();
//        int[] studentsOutside = new int[schoolTypes.length];
//        int[] studentsByType = new int[schoolTypes.length];
//        for (Person pp : studentArrayList) {
//
//            //Select the zones with vacant schools for that person, given the school type
//            int schoolType = pp.getSchoolType();
//            studentsByType[schoolType - 1] = studentsByType[schoolType - 1] + 1;
//            int[] keys = idZonesVacantSchoolsByType.get(schoolType);
//            int lengthKeys = numberZonesWithVacantSchoolsByType.get(schoolType);
//            if (lengthKeys > 0) {//if there are still TAZ with school capacity in the region, select one of them. If not, assign them outside the area
//
//                //Select the school location (which raster cell) for that person given his/her job type
//                int[] schoolPlace = new int[2];
//                Household hh = pp.getHousehold();
//                int origin = realEstate.getDwelling(hh.getDwellingId()).getZoneId();
//                if (schoolType == 3) {
//                    schoolPlace = selectWorkplace(origin, numberVacantSchoolsByZoneByType,
//                            keys, lengthKeys, universityDistanceImpedance);
//                    travelUniversity.addValue((int) distanceMatrix.getValueAt(origin, schoolPlace[0] / 100));
//                } else {
//                    schoolPlace = selectClosestSchool(origin, numberVacantSchoolsByZoneByType,
//                            keys, lengthKeys, schoolDistanceImpedance);
//                    if (schoolType == 1){
//                        travelPrimary.addValue((int) distanceMatrix.getValueAt(origin,schoolPlace[0] / 100));
//                    } else if (schoolType == 2){
//                        travelSecondary.addValue((int) distanceMatrix.getValueAt(origin, schoolPlace[0] / 100));
//                    }
//                }
//
//                //Assign values to job and person
//                pp.setSchoolPlace(schoolPlace[0] / 100);
//                //pp.setTravelTime(distanceMatrix.getValueAt(pp.getZone(), pp.getSchoolPlace()));
//
//                //For validation OD TableDataSet
//                int homeMun = (int) cellsMatrix.getIndexedValueAt(origin, "smallID");
//                int workMun = (int) cellsMatrix.getIndexedValueAt(pp.getSchoolPlace(), "smallID");
//                int odPair = homeMun * 1000 + workMun;
//                odMunicipalityFlow.setIndexedValueAt(odPair,Integer.toString(count),odMunicipalityFlow.getIndexedValueAt(odPair,Integer.toString(count))+ 1);
//
//                //Update counts of vacant school places
//                numberVacantSchoolsByZoneByType.put(schoolPlace[0], numberVacantSchoolsByZoneByType.get(schoolPlace[0]) - 1);
//                if (numberVacantSchoolsByZoneByType.get(schoolPlace[0]) < 1) {
//                    numberVacantSchoolsByZoneByType.put(schoolPlace[0], 0);
//                    keys[schoolPlace[1]] = keys[numberZonesWithVacantSchoolsByType.get(schoolType) - 1];
//                    idZonesVacantSchoolsByType.put(schoolType, keys);
//                    numberZonesWithVacantSchoolsByType.put(schoolType, numberZonesWithVacantSchoolsByType.get(schoolType) - 1);
//                    if (numberZonesWithVacantSchoolsByType.get(schoolType) < 1) {
//                        numberZonesWithVacantSchoolsByType.put(schoolType, 0);
//                    }
//                }
//                assignedSchools++;
//            } else {//No more school capacity in the study area. This person will study outside the area
//                pp.setSchoolPlace(-2); //they attend one school out of the area
//                studentsOutside[schoolType - 1] = studentsOutside[schoolType - 1] + 1;
//            }
//        }
//
//
//        //For validation - trip length distribution
//        checkTripLengthDistribution(travelPrimary, 0, 0, "microData/interimFiles/tripLengthDistributionPrimary.csv", 1);
//        checkTripLengthDistribution(travelSecondary, 0, 0, "microData/interimFiles/tripLengthDistributionSecondary.csv", 1); //Trip length frequency distribution
//        checkTripLengthDistribution(travelUniversity, alphaJob, gammaJob, "microData/interimFiles/tripLengthDistributionUniversity.csv", 1);
//        SiloUtil.writeTableDataSet(odMunicipalityFlow,"microData/interimFiles/odMunicipalityFlow.csv");
//        for (int i = 0; i < schoolTypes.length; i++) {
//            logger.info("  School type: " + schoolTypes[i] + ". " + studentsOutside[schoolTypes[i] - 1] + " students out of " + studentsByType[schoolTypes[i] - 1] + " study outside the area");
//        }
//    }
//
//
//    private void readSyntheticPopulation(){
//        //Read the synthetic population
//
//        logger.info("   Starting to read the synthetic population");
//        String fileEnding = "_" + de.tum.bgu.msm.properties.Properties.get().main.implementation.BASE_YEAR + ".csv";
//        TableDataSet households = SiloUtil.readCSVfile2(rb.getString(PROPERTIES_HOUSEHOLD_SYN_POP) + fileEnding);
//        TableDataSet persons = SiloUtil.readCSVfile2(rb.getString(PROPERTIES_PERSON_SYN_POP) + fileEnding);
//        TableDataSet dwellings = SiloUtil.readCSVfile2(rb.getString(PROPERTIES_DWELLING_SYN_POP) + fileEnding);
//        TableDataSet jobs = SiloUtil.readCSVfile2(rb.getString(PROPERTIES_JOB_SYN_POP) + fileEnding);
//        schoolLevelTable = SiloUtil.readCSVfile(rb.getString(PROPERTIES_SCHOOL_DESCRIPTION));
//        logger.info("   Read input data");
//
//        TableDataSet prices = SiloUtil.readCSVfile2("microData/interimFiles/zoneAttributes_landPrice.csv");
//        prices.buildIndex(prices.getColumnPosition("ID_cell"));
//
//        HouseholdData householdData = dataContainer.getHouseholdData();
//        //Generate the households, dwellings and persons
//        logger.info("   Starting to generate households");
//        HouseholdFactory householdFactory = HouseholdUtil.getFactory();
//        for (int i = 1; i <= households.getRowCount(); i++) {
//            Household hh = householdFactory.createHousehold((int) households.getValueAt(i, "id"), (int) households.getValueAt(i, "dwelling"),
//                    (int) households.getValueAt(i, "autos"));
//            householdData.addHousehold(hh);
//        }
//
//        logger.info("   Starting to generate persons");
//        PersonFactory factory = PersonUtils.getFactory();
//        for (int i = 1; i <= persons.getRowCount(); i++) {
//            Race race = Race.white;
//            if ((int) persons.getValueAt(i,"nationality") > 1){race = Race.black;}
//            int hhID = (int) persons.getValueAt(i, "hhid");
//
//            Person pp = factory.createPerson((int) persons.getValueAt(i, "id"),
//                    (int) persons.getValueAt(i, "age"), Gender.valueOf((int) persons.getValueAt(i, "gender")),
//                    race, Occupation.valueOf((int) persons.getValueAt(i, "occupation")),null,  (int) persons.getValueAt(i, "workplace"),
//                    (int) persons.getValueAt(i, "income"));
//            householdData.addPerson(pp);
//            householdData.addPersonToHousehold(pp, householdData.getHouseholdFromId(hhID));
//            educationalLevelByPerson.put(pp, (int) persons.getValueAt(i, "education"));
//            if (persons.getStringValueAt(i, "relationShip").equals("single")) pp.setRole(PersonRole.SINGLE);
//            else if (persons.getStringValueAt(i, "relationShip").equals("married")) pp.setRole(PersonRole.MARRIED);
//            else pp.setRole(PersonRole.CHILD);
//            if (persons.getValueAt(i,"driversLicense") == 1) pp.setDriverLicense(true);
//            int nationality = (int) persons.getValueAt(i,"nationality");
//            if (nationality == 1) {
//                pp.setNationality(Nationality.GERMAN);
//            } else {
//                pp.setNationality(Nationality.OTHER);
//            }
//            pp.setSchoolType((int) persons.getValueAt(i,"schoolDE"));
//            pp.setWorkplace((int) persons.getValueAt(i,"workplace"));
//        }
//
//        logger.info("   Starting to generate dwellings");
//        RealEstateData realEstate = dataContainer.getRealEstateData();
//        for (int i = 1; i <= dwellings.getRowCount(); i++){
//            int buildingSize = (int) dwellings.getValueAt(i,"building");
//            int zoneId = (int) dwellings.getValueAt(i,"zone");
//            int municipality = (int) cellsMatrix.getIndexedValueAt(zoneId,"ID_city");
///*            float ddType1Prob = marginalsMunicipality.getIndexedValueAt(municipality, "dwelling12");
//            float ddType3Prob = marginalsMunicipality.getIndexedValueAt(municipality, "dwelling37");
//            DwellingType type = guessDwellingType(buildingSize, ddType1Prob, ddType3Prob);*/
//            String ddtype = dwellings.getStringValueAt(i,"type");
//            DefaultDwellingTypeImpl type = guessDwellingType(ddtype);
//            int size = (int) dwellings.getValueAt(i,"floor");
//            int bedrooms = guessBedrooms(size);
//            int quality = (int)dwellings.getValueAt(i,"quality");
//            float brw = prices.getIndexedValueAt(zoneId, ddtype);
//            float price = guessPrice(brw, quality, size);
//            Dwelling dd = DwellingUtils.getFactory().createDwelling((int)dwellings.getValueAt(i,"id"), zoneId,null,
//                    (int)dwellings.getValueAt(i,"hhID"),type,bedrooms,
//                    (int)dwellings.getValueAt(i,"quality"),(int) price,
//                    (int)dwellings.getValueAt(i,"restriction"),(int)dwellings.getValueAt(i,"yearBuilt"));
//            realEstate.addDwelling(dd);
//            dd.setFloorSpace((int)dwellings.getValueAt(i,"floor"));
//            dd.setBuildingSize((int)dwellings.getValueAt(i,"building"));
//            dd.setYearConstructionDE((int)dwellings.getValueAt(i,"year"));
//            dd.setUsage(DwellingUsage.valueOf((int)dwellings.getValueAt(i,"usage")));
//        }
//        logger.info("   Generated households, persons and dwellings");
//
//
//        //Generate the jobs
//        //Starting to generate jobs
//        logger.info("   Starting to generate jobs");
//        JobData jobData = dataContainer.getJobData();
//        for (int i = 1; i <= jobs.getRowCount(); i++) {
//            int zoneId = (int) jobs.getValueAt(i, "zone");
//			jobData.addJob(JobUtils.getFactory().createJob((int) jobs.getValueAt(i, "id"), zoneId, null,
//                    (int) jobs.getValueAt(i, "personId"), jobs.getStringValueAt(i, "type")));
//        }
//        logger.info("   Generated jobs");
//    }
//
//    private float guessPrice(float brw, int quality, int size) {
//
//        float coef = 1;
//        if (quality == 1){
//            coef = 0.7f;
//        } else if (quality == 2){
//            coef = 0.9f;
//        } else if (quality == 4){
//            coef = 1.1f;
//        }
//        float convertToMonth = 0.0057f;
//        return brw * size * coef * convertToMonth + 150;
//    }
//
//    private int guessBedrooms(int size) {
//        int bedrooms = 0;
//        if (size < 40){
//            bedrooms = 0;
//        } else if (size < 60){
//            bedrooms = 1;
//        } else if (size < 80){
//            bedrooms = 2;
//        } else if (size < 100){
//            bedrooms = 3;
//        } else if (size < 120){
//            bedrooms = 4;
//        } else {
//            bedrooms = 5;
//        }
//
//        return bedrooms;
//    }
//
//
//    private void validationCommutersFlow(){
//
//        //For checking
//        //OD matrix from the commuters data, for validation
//        TableDataSet selectedMunicipalities = SiloUtil.readCSVfile(rb.getString(PROPERTIES_SELECTED_MUNICIPALITIES_LIST)); //TableDataSet with all municipalities
//        selectedMunicipalities.buildIndex(selectedMunicipalities.getColumnPosition("ID_city"));
//        int[] allCounties = selectedMunicipalities.getColumnAsInt("smallCenter");
//        TableDataSet observedODFlow = SiloUtil.readCSVfile("input/syntheticPopulation/odMatrixCommuters.csv");
//        observedODFlow.buildIndex(observedODFlow.getColumnPosition("ID_city"));
//        //OD matrix for the core cities, obtained from the commuters data
//        TableDataSet observedCoreODFlow = new TableDataSet();
//        int [] selectedCounties = SiloUtil.idendifyUniqueValues(allCounties);
//        observedCoreODFlow.appendColumn(selectedCounties,"smallCenter");
//        for (int i = 0; i < selectedCounties.length; i++){
//            int[] dummy = SiloUtil.createArrayWithValue(selectedCounties.length,0);
//            observedCoreODFlow.appendColumn(dummy,Integer.toString(selectedCounties[i]));
//        }
//        observedCoreODFlow.buildIndex(observedCoreODFlow.getColumnPosition("smallCenter"));
//        int ini = 0;
//        int end = 0;
//        // We decided to read this file here again, as this method is likely to be removed later, which is why we did not
//        // want to create a global variable for TableDataSet selectedMunicipalities (Ana and Rolf, 29 Mar 2017)
//
//        int[] citySmallID = selectedMunicipalities.getColumnAsInt("smallID");
//        for (int i = 0; i < cityID.length; i++){
//            ini = (int) selectedMunicipalities.getIndexedValueAt(cityID[i],"smallCenter");
//            for (int j = 0; j < cityID.length; j++){
//                end = (int) selectedMunicipalities.getIndexedValueAt(cityID[j],"smallCenter");
//                observedCoreODFlow.setIndexedValueAt(ini,Integer.toString(end),
//                        observedCoreODFlow.getIndexedValueAt(ini,Integer.toString(end)) + observedODFlow.getIndexedValueAt(cityID[i],Integer.toString(cityID[j])));
//            }
//        }
//        //OD flows at the municipality level in one TableDataSet, to facilitate visualization of the deviation between the observed data and the estimated data
//        odMunicipalityFlow = new TableDataSet();
//        int[] cityKeys = new int[citySmallID.length * citySmallID.length];
//        int[] odData = new int[citySmallID.length * citySmallID.length];
//        int k = 0;
//        for (int row = 0; row < citySmallID.length; row++){
//            for (int col = 0; col < citySmallID.length; col++){
//                cityKeys[k] = citySmallID[row] * 1000 + citySmallID[col];
//                odData[k] = (int) observedODFlow.getIndexedValueAt(cityID[row],Integer.toString(cityID[col]));
//                k++;
//            }
//        }
//        int[] initial = SiloUtil.createArrayWithValue(cityKeys.length, 0);
//        odMunicipalityFlow.appendColumn(cityKeys,"ID_od");
//        odMunicipalityFlow.appendColumn(odData,"ObservedFlow");
//        odMunicipalityFlow.appendColumn(initial,"SimulatedFlow");
//        odMunicipalityFlow.buildIndex(odMunicipalityFlow.getColumnPosition("ID_od"));
//
//        //OD flows at the regional level (5 core cities)
//        odCountyFlow = new TableDataSet();
//        int[] regionKeys = new int[selectedCounties.length * selectedCounties.length];
//        int[] regionalFlows = new int[selectedCounties.length * selectedCounties.length];
//        k = 0;
//        for (int row = 0; row < selectedCounties.length; row++){
//            for (int col = 0; col < selectedCounties.length; col++){
//                regionKeys[k] = selectedCounties[row] * 1000 + selectedCounties[col];
//                regionalFlows[k] = (int) observedCoreODFlow.getIndexedValueAt(selectedCounties[row],Integer.toString(selectedCounties[col]));
//                k++;
//            }
//        }
//        int[] initialFlow = SiloUtil.createArrayWithValue(regionKeys.length, 0);
//        odCountyFlow.appendColumn(regionKeys,"ID_od");
//        odCountyFlow.appendColumn(regionalFlows,"ObservedFlow");
//        odCountyFlow.appendColumn(initialFlow,"SimulatedFlow");
//        odCountyFlow.buildIndex(odCountyFlow.getColumnPosition("ID_od"));
//    }
//
//
//    private void generateHouseholdsPersonsDwellings(){
//        //Generate the synthetic population using Monte Carlo (select the households according to the weight)
//        //Once the household is selected, all the characteristics of the household will be copied (including the household members)
//        logger.info("   Starting to generate households and persons.");
//
//
//        //List of households of the micro data
//        int[] microDataIds = frequencyMatrix.getColumnAsInt("ID");
//        int previousHouseholds = 0;
//        int previousPersons = 0;
//
//
//        //Define income distribution
//        double incomeShape = ResourceUtil.getDoubleProperty(rb,PROPERTIES_INCOME_GAMMA_SHAPE);
//        double incomeRate = ResourceUtil.getDoubleProperty(rb,PROPERTIES_INCOME_GAMMA_RATE);
//        double[] incomeProbability = ResourceUtil.getDoubleArray(rb,PROPERTIES_INCOME_GAMMA_PROBABILITY);
//        GammaDistributionImpl gammaDist = new GammaDistributionImpl(incomeShape, 1/incomeRate);
//
//
//        //Driver license probability
//        TableDataSet probabilityDriverLicense = SiloUtil.readCSVfile("input/syntheticPopulation/driverLicenseProb.csv");
//        educationalLevelByPerson = new HashMap<>();
//        generateCountersForValidation();
//
//        //Create hashmaps to store quality of occupied dwellings
//        HashMap<Integer, int[]> ddQuality = new HashMap<>();
//        numberofQualityLevels = ResourceUtil.getIntegerProperty(rb, PROPERTIES_NUMBER_OF_DWELLING_QUALITY_LEVELS);
//        for (int municipality = 0; municipality < cityID.length; municipality++){
//            for (int year : yearBracketsDwelling){
//                int[] probability = SiloUtil.createArrayWithValue(numberofQualityLevels, 0);
//                int key = year * 1000 + cityID[municipality];
//                ddQuality.put(key, probability);
//            }
//        }
//
//
//        RealEstateData realEstate = dataContainer.getRealEstateData();
//        HouseholdData householdData = dataContainer.getHouseholdData();
//        //Selection of households, persons, jobs and dwellings per municipality
//
//        HouseholdFactory householdFactory = HouseholdUtil.getFactory();
//        for (int municipality : municipalities){
//            logger.info("   Municipality " + cityID[municipality] + ". Starting to generate households.");
//
//            int totalHouseholds = (int) marginalsMunicipality.getIndexedValueAt(municipality,"hhTotal");
//            double[] probability = weightsTable.getColumnAsDouble(Integer.toString(municipality));
//            float ddType1Prob = marginalsMunicipality.getIndexedValueAt(municipality,"dwelling12");
//            float ddType3Prob = marginalsMunicipality.getIndexedValueAt(municipality,"dwelling37");
//
//
//            //obtain the raster cells of the municipality and their weight within the municipality
//            int[] tazInCity = cityTAZ.get(municipality);
//            double[] probTaz = new double[tazInCity.length];
//            double tazRemaining = 0;
//            for (int i = 0; i < tazInCity.length; i++){
//                probTaz[i] = frequencyMatrix.getIndexedValueAt(tazInCity[i],"Population");
//                tazRemaining = tazRemaining + probTaz[i];
//            }
//
//
//            double hhRemaining = 0;
//            double[] probabilityPrivate = new double[probability.length]; // Separate private households and group quarters for generation
//            for (int row = 0; row < probability.length; row++){
//                probabilityPrivate[row] = probability[row];
//                hhRemaining = hhRemaining + probability[row];
//            }
//
//            //marginals for the municipality
//            int hhPersons = 0;
//            int hhTotal = 0;
//            int id = 0;
//
//
//            //for all the households that are inside the municipality (we will match perfectly the number of households. The total population will vary compared to the marginals.)
//            for (int row = 0; row < totalHouseholds; row++) {
//
//                //select the household to copy from the micro data(with replacement)
//                int[] records = select(probabilityPrivate, microDataIds, hhRemaining);
//                int hhIdMD = records[0];
//                int hhRowMD = records[1];
//                if (probabilityPrivate[hhRowMD] > 1.0) {
//                    probabilityPrivate[hhRowMD] = probabilityPrivate[hhRowMD] - 1;
//                    hhRemaining = hhRemaining - 1;
//                } else {
//                    hhRemaining = hhRemaining - probabilityPrivate[hhRowMD];
//                    probabilityPrivate[hhRowMD] = 0;
//                }
//
//
//                //Select the taz to allocate the household (without replacement)
//                int[] recordsCell = select(probTaz, tazInCity, tazRemaining);
//                int taz =recordsCell[0];
//
//                //copy the private household characteristics
//                int householdSize = (int) microDataHousehold.getIndexedValueAt(hhIdMD, "hhSize");
//                id = householdData.getNextHouseholdId();
//                int newDdId = RealEstateData.getNextDwellingId();
//                Household household = householdFactory.createHousehold(id, newDdId, 0); //(int id, int dwellingID, int homeZone, int hhSize, int autos)
//                householdData.addHousehold(household);
//                hhTotal++;
//                counterMunicipality = updateCountersHousehold(household, counterMunicipality, municipality);
//
//
//                //copy the household members characteristics
//                PersonFactory factory = PersonUtils.getFactory();
//                for (int rowPerson = 0; rowPerson < householdSize; rowPerson++) {
//                    int idPerson = householdData.getNextPersonId();
//                    int personCounter = (int) microDataHousehold.getIndexedValueAt(hhIdMD, "personCount") + rowPerson;
//                    int age = (int) microDataPerson.getValueAt(personCounter, "age");
//                    Gender gender = Gender.valueOf((int) microDataPerson.getValueAt(personCounter, "gender"));
//                    Occupation occupation = Occupation.valueOf((int) microDataPerson.getValueAt(personCounter, "occupation"));
//                    int income = (int) microDataPerson.getValueAt(personCounter, "income");
//                    try {
//                        income = (int) translateIncome((int) microDataPerson.getValueAt(personCounter, "income"),incomeProbability, gammaDist)
//                                * 12;  //convert monthly income to yearly income
//                    } catch (MathException e) {
//                        e.printStackTrace();
//                    }
//                    Person pers = factory.createPerson(idPerson, age, gender, Race.white, occupation, null, 0, income); //(int id, int hhid, int age, int gender, Race race, int occupation, int workplace, int income)
//                    householdData.addPerson(pers);
//                    householdData.addPersonToHousehold(pers, household);
//                    educationalLevelByPerson.put(pers, (int) microDataPerson.getValueAt(personCounter, "educationLevel"));
//                    PersonRole role = PersonRole.SINGLE; //default value = single
//                    if (microDataPerson.getValueAt(personCounter, "personRole") == 2) { //is married
//                        role = PersonRole.MARRIED;
//                    } else if (microDataPerson.getValueAt(personCounter, "personRole") == 3) { //is children
//                        role = PersonRole.CHILD;
//                    }
//                    pers.setRole(role);
//                    int nationality = (int) microDataPerson.getValueAt(personCounter,"nationality");
//                    if (nationality == 1) {
//                        pers.setNationality(Nationality.GERMAN);
//                    } else {
//                        pers.setNationality(Nationality.OTHER);
//                    }
//                    //int selectedJobType = ec.selectJobType(pers, probabilitiesJob, jobTypes);
//                    //pers.setJobTypeDE(selectedJobType);
//                    pers.setDriverLicense(obtainDriverLicense(pers.getGender().ordinal()-1, pers.getAge(),probabilityDriverLicense));
//                    pers.setSchoolType((int) microDataPerson.getValueAt(personCounter, "schoolType"));
//                    hhPersons++;
//                    counterMunicipality = updateCountersPerson(pers, counterMunicipality, municipality,ageBracketsPerson);
//                }
//
//
//                //Copy the dwelling of that household
//                int bedRooms = 1; //Not on the micro data
//                int price = Math.max((int) microDataDwelling.getIndexedValueAt(hhIdMD, "dwellingRentPrice"), 0); //Copied from micro data
//                int year = (int) microDataDwelling.getIndexedValueAt(hhIdMD, "dwellingYear"); //Not by year. In the data is going to be in classes
//                int floorSpace = (int) microDataDwelling.getIndexedValueAt(hhIdMD, "dwellingFloorSpace");
//                int usage = (int) microDataDwelling.getIndexedValueAt(hhIdMD, "dwellingUsage");
//                int buildingSize = (int) microDataDwelling.getIndexedValueAt(hhIdMD, "dwellingType");
//                int heatingType = (int) microDataDwelling.getIndexedValueAt(hhIdMD, "dwellingHeatingType");
//                int heatingEnergy = (int) microDataDwelling.getIndexedValueAt(hhIdMD, "dwellingHeatingEnergy");
//                int heatingAdditional = (int) microDataDwelling.getIndexedValueAt(hhIdMD, "dwellingAdHeating");
//                int quality = guessQualityDE(heatingType, heatingEnergy, heatingAdditional, year, numberofQualityLevels); //depend on year built and type of heating
//                DefaultDwellingTypeImpl type = guessDwellingType(buildingSize, ddType1Prob, ddType3Prob);
//                int yearVacant = 0;
//                while (year > yearBracketsDwelling[yearVacant]) {yearVacant++;}
//                int key = municipality + yearBracketsDwelling[yearVacant] * 1000;
//                int[] qualityCounts = ddQuality.get(key);
//                qualityCounts[quality - 1]++;
//                ddQuality.put(key, qualityCounts);
//                year = selectDwellingYear(year); //convert from year class to actual 4-digit year
//                Dwelling dwell = DwellingUtils.getFactory().createDwelling(newDdId, taz, null, id, type , bedRooms, quality, price, 0, year); //newDwellingId, raster cell, HH Id, ddType, bedRooms, quality, price, restriction, construction year
//                realEstate.addDwelling(dwell);
//                dwell.setFloorSpace(floorSpace);
//                dwell.setUsage(DwellingUsage.valueOf(usage));
//                dwell.setBuildingSize(buildingSize);
//                counterMunicipality = updateCountersDwelling(dwell,counterMunicipality,municipality,yearBracketsDwelling,sizeBracketsDwelling);
//                realEstate.addDwelling(dwell);
//            }
//            int households = householdData.getHighestHouseholdIdInUse() - previousHouseholds;
//            int persons = householdData.getHighestPersonIdInUse() - previousPersons;
//            previousHouseholds = householdData.getHighestHouseholdIdInUse();
//            previousPersons = householdData.getHighestPersonIdInUse();
//
//
//            //Calculate the errors from the synthesized population at the attributes of the IPU.
//            //Update the tables for all municipalities with the result of this municipality
//
//            //Consider if I need to add also the errors from other attributes. They must be at the marginals file, or one extra file
//            //For county level they should be calculated on a next step, outside this loop.
//            float averageError = 0f;
//            for (String attribute : attributesMunicipality){
//                float error = Math.abs((counterMunicipality.getIndexedValueAt(municipality,attribute) -
//                        marginalsMunicipality.getIndexedValueAt(municipality,attribute)) /
//                        marginalsMunicipality.getIndexedValueAt(municipality,attribute));
//                errorMunicipality.setIndexedValueAt(municipality,attribute,error);
//                averageError = averageError + error;
//                //counterSynPop.setIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute],counterMunicipality.getIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute]));
//                //relativeErrorSynPop.setIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute],errorMunicipality.getIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute]));
//            }
//            averageError = averageError / (1 + attributesMunicipality.length) * 100;
//
//
//            logger.info("   Municipality " + municipality + ". Generated " + hhPersons + " persons in " + hhTotal + " households. The error is " + averageError + " %.");
//            //SiloUtil.writeTableDataSet(counterMunicipality,"microData/interimFiles/counterMun.csv");
//            //SiloUtil.writeTableDataSet(errorMunicipality,"microData/interimFiles/errorMun.csv");
//        }
//        int households = householdData.getHighestHouseholdIdInUse();
//        int persons = householdData.getHighestPersonIdInUse();
//        logger.info("   Finished generating households and persons. A population of " + persons + " persons in " + households + " households was generated.");
//
//
//        //Vacant dwellings--------------------------------------------
//        //They have similar characteristics to the dwellings that are occupied (assume that there is no difference between the occupied and vacant dwellings in terms of quality)
//        int vacantCounter = 0;
//        for (int municipality = 0; municipality < cityID.length; municipality++) {
//
//            logger.info("   Municipality " + cityID[municipality] + ". Starting to generate vacant dwellings.");
//            int municipalityID = cityID[municipality];
//            int vacantDwellings = (int) marginalsMunicipality.getIndexedValueAt(cityID[municipality], "totalDwellingsVacant");
//            TableDataSet rasterCellsMatrix = cellsMatrix;
//
//            //obtain the raster cells of the municipality and their weight within the municipality
//            int[] tazInCity = cityTAZ.get(municipalityID);
//            double[] probTaz = new double[tazInCity.length];
//            double sumProbTaz = 0;
//            for (int i = 0; i < tazInCity.length; i++){
//                probTaz[i] = rasterCellsMatrix.getIndexedValueAt(tazInCity[i],"Population");
//                sumProbTaz = sumProbTaz + probTaz[i];
//            }
//            int rasterCount = 0;
//            for (int row = 1; row <= rasterCellsMatrix.getRowCount(); row++) {
//                if ((int) rasterCellsMatrix.getValueAt(row, "ID_city") == municipalityID) {
//                    rasterCount++;
//                }
//            }
//
//
//            //Probability of floor size for vacant dwellings
//            float [] vacantFloor = new float[sizeBracketsDwelling.length];
//            for (int row = 0; row < sizeBracketsDwelling.length; row++){
//                String name = "vacantDwellings" + sizeBracketsDwelling[row];
//                vacantFloor[row] = marginalsMunicipality.getIndexedValueAt(municipalityID,name)/vacantDwellings;
//            }
//
//            //Probability for year and building size for vacant dwellings
//            float[] vacantSize = new float[yearBracketsDwelling.length * 2];
//            for (int row = 0; row < yearBracketsDwelling.length; row++){
//                String name = "vacantSmallDwellings" + yearBracketsDwelling[row];
//                String name1 = "vacantMediumDwellings" + yearBracketsDwelling[row];
//                vacantSize[row] = marginalsMunicipality.getIndexedValueAt(municipalityID,name) / vacantDwellings;
//                vacantSize[row + yearBracketsDwelling.length] = marginalsMunicipality.getIndexedValueAt(municipalityID,name1) / vacantDwellings;
//            }
//
//            //Select the vacant dwelling and copy characteristics
//            for (int row = 0; row < vacantDwellings; row++) {
//
//                //Allocation
//                int ddCell[] = select(probTaz, tazInCity, sumProbTaz); // I allocate vacant dwellings using the same proportion as occupied dwellings.
//                int zone = ddCell[0];
//
//                //Copy characteristics
//                int newDdId = RealEstateData.getNextDwellingId();
//                int bedRooms = 1; //Not on the micro data
//                int price = 0; //Monte Carlo
//                int[] buildingSizeAndYearBuilt = selectBuildingSizeYear(vacantSize, yearBracketsDwelling);
//                int key = municipalityID + buildingSizeAndYearBuilt[1] * 1000;
//                int quality = select(ddQuality.get(key)) + 1; //Based on the distribution of qualities at the municipality for that construction period
//                int year = selectVacantDwellingYear(buildingSizeAndYearBuilt[1]);
//                int floorSpaceDwelling = selectFloorSpace(vacantFloor, sizeBracketsDwelling);
//                Dwelling dwell = DwellingUtils.getFactory().createDwelling(newDdId, zone, null, -1, DefaultDwellingTypeImpl.MF234, bedRooms, quality, price, 0, year); //newDwellingId, raster cell, HH Id, ddType, bedRooms, quality, price, restriction, construction year
//                realEstate.addDwelling(dwell);
//                dwell.setUsage(DwellingUsage.VACANT); //vacant dwelling = 3; and hhID is equal to -1
//                dwell.setFloorSpace(floorSpaceDwelling);
//                dwell.setBuildingSize(buildingSizeAndYearBuilt[0]);
//                vacantCounter++;
//            }
//            logger.info("   The number of vacant dwellings is: " + vacantCounter);
//        }
//    }
//
//
//    private static double translateIncome (int incomeClass, double[] incomeThresholds, GammaDistributionImpl q) throws MathException {
//        //provide the income value for each person give the income class.
//        //income follows a gamma distribution that was calibrated using the microdata. Income thresholds are calculated for the stiches
//        double income;
//        int finish = 0;
//        double low = 0;
//        double high = 1;
//        if (incomeClass == 90) {
//            income = 0;  // kein Einkommen
///*        } else if (incomeClass == 50) {
//            income = 0; // Selbständige/r Landwirt/in in der Haupttätigkeit
//        } else if (incomeClass == 99) {
//            income = -1; //keine Angabe*/
//        } else {
//            if (incomeClass == 1) {
//                low = 0;
//                high = incomeThresholds[0];
//            } else if (incomeClass == 50){ // Selbständige/r Landwirt/in in der Haupttätigkeit
//                low = 0; //give them a random income following the distribution
//                high = 1;
//            } else if (incomeClass == 99){ //keine Angabe
//                low = 0; //give them a random income following the distribution
//                high = 1;
//            } else if (incomeClass == incomeThresholds.length + 1) {
//                low = incomeThresholds[incomeThresholds.length-1];
//                high = 1;
//            } else {
//                int i = 2;
//                while (finish == 0){
//                    if (incomeClass > i){
//                        i++;
//                    } else {
//                        finish = 1;
//                        low = incomeThresholds[i-2];
//                        high = incomeThresholds[i-1];
//                    }
//                }
//            }
//            double cummulativeProb = SiloUtil.getRandomNumberAsDouble()*(high - low) + low;
//            income = q.inverseCumulativeProbability(cummulativeProb);
//        }
//        return income;
//    }
//
//
//    private static int translateJobType (int personJob, TableDataSet jobs){
//        //translate 100 job descriptions to 4 job types
//        //jobs is one TableDataSet that is read from a csv file containing the description, ID and types of jobs.
//        int job = 0;
//        int finish = 0;
//        int row = 1;
//        while (finish == 0 & row < jobs.getRowCount()){
//            if (personJob == jobs.getValueAt(row,"WZ08Code")) {
//                finish =1;
//                job = (int) jobs.getValueAt(row,"MarginalsCode");
//            }
//            else {
//                row++;
//            }
//        }
//        return job;
//    }
//
//
//    private static int translateEducationLevel (int personEducation, TableDataSet educationLevel){
//        //translate 12 education levels to 4
//        //jobs is one TableDataSet that is read from a csv file containing the description, ID and types of jobs.
//        int education = 0;
//        int finish = 0;
//        int row = 1;
//        while (finish == 0 & row < educationLevel.getRowCount()){
//            if (personEducation == educationLevel.getValueAt(row,"fdz_mz_sufCode")) {
//                finish =1;
//                education = (int) educationLevel.getValueAt(row,"SynPopCode");
//            }
//            else {
//                row++;
//            }
//        }
//        if (education == 0){education = 1;}
//        return education;
//    }
//
//
//    private static int translateSchoolType (int personEducation, TableDataSet schoolType){
//        //translate 12 education levels to 4
//        //jobs is one TableDataSet that is read from a csv file containing the description, ID and types of jobs.
//        int education = 0;
//        int finish = 0;
//        int row = 1;
//        while (finish == 0 & row < schoolType.getRowCount()){
//            if (personEducation == schoolType.getValueAt(row,"fdz_mz_sufCode")) {
//                finish =1;
//                education = (int) schoolType.getValueAt(row,"SynPopCode");
//            }
//            else {
//                row++;
//            }
//        }
//        //if (education == 0){education = 1;}
//        return education;
//    }
//
//
//    private static boolean obtainDriverLicense (int gender, int age, TableDataSet prob){
//        //assign if the person holds a driver license based on the probabilities obtained from MiD data
//        boolean license = false;
//        int finish = 0;
//        int row = 1;
//        int threshold = 0;
//        if (age > 18) {
//            while (finish == 0 & row < prob.getRowCount()) {
//                if (age > prob.getValueAt(row, "ageLimit")) {
//                    row++;
//                } else {
//                    finish = 1;
//                }
//            }
//            if (finish == 0) {
//                row = prob.getRowCount();
//            }
//            if (gender == 0) {
//                threshold = (int) prob.getValueAt(row, "male");
//            } else {
//                threshold = (int) prob.getValueAt(row, "female");
//            }
//            if (SiloUtil.getRandomNumberAsDouble() * 100 < threshold) {
//                license = true;
//            }
//        } //if they are younger than 18, they don't hold driver license
//        return license;
//    }
//
//
//    private static int selectFloorSpace(float[] vacantFloor, int[] sizeBracketsDwelling){
//        //provide the size of the building
//        int floorSpaceDwelling = 0;
//        int floorSpace = SiloUtil.select(vacantFloor);
//        if (floorSpace == 0){
//            floorSpaceDwelling = (int) (30 + SiloUtil.getRandomNumberAsFloat() * 20);
//        } else if (floorSpace == sizeBracketsDwelling.length - 1) {
//            floorSpaceDwelling = (int) (120 + SiloUtil.getRandomNumberAsFloat() * 200);
//        } else {
//            floorSpaceDwelling = (int) SiloUtil.getRandomNumberAsDouble()*(sizeBracketsDwelling[floorSpace]-sizeBracketsDwelling[floorSpace-1]) +
//                    sizeBracketsDwelling[floorSpace - 1];
//        }
//        return floorSpaceDwelling;
//    }
//
//
//    private static int[] selectBuildingSizeYear(float[] vacantSize, int[] yearBracketsDwelling){
//        //provide the size of the building
//        int[] buildingSizeAndYear = new int[2];
//        int yearSize = SiloUtil.select(vacantSize);
//        if (yearSize < yearBracketsDwelling.length){
//            buildingSizeAndYear[0] = 1; //small-size building
//            buildingSizeAndYear[1] = yearBracketsDwelling[yearSize];
//        } else {
//            buildingSizeAndYear[0] = 2; //medium-size building
//            buildingSizeAndYear[1] = yearBracketsDwelling[yearSize - yearBracketsDwelling.length];
//        }
//        return buildingSizeAndYear;
//    }
//
//
//    private static int selectDwellingYear(int yearBuilt){
//        //assign randomly one construction year to the dwelling within the year brackets of the microdata
//        //Ages - 1: before 1919, 2: 1919-1948, 3: 1949-1978, 4: 1979 - 1986; 5: 1987 - 1990; 6: 1991 - 2000; 7: 2001 - 2004; 8: 2005 - 2008, 9: 2009 or later,
//        int selectedYear = 1;
//        float rnd = SiloUtil.getRandomNumberAsFloat();
//        switch (yearBuilt){
//            case 1: selectedYear = 1919;
//                break;
//            case 2: selectedYear = (int) (1919 + rnd * 39);
//                break;
//            case 3: selectedYear = (int) (1949 + rnd * 29);
//                break;
//            case 4: selectedYear = (int) (1979 + rnd * 7);
//                break;
//            case 5: selectedYear = (int) (1987 + rnd * 3);
//                break;
//            case 6: selectedYear = (int) (1991 + rnd * 9);
//                break;
//            case 7: selectedYear = (int) (2001 + rnd * 3);
//                break;
//            case 8: selectedYear = (int) (2005 + rnd * 3);
//                break;
//            case 9: selectedYear = (int) (2009 + rnd * 2);
//                break;
//        }
//        return selectedYear;
//    }
//
//
//    private static int selectVacantDwellingYear(int yearBuilt){
//        //assign randomly one construction year to the dwelling within the year brackets of the microdata -
//        //Ages - 2: Before 1948, 5: 1949 - 1990; 6: 1991 - 2000; 9: 2001 or later
//        int selectedYear = 1;
//        float rnd = SiloUtil.getRandomNumberAsFloat();
//        switch (yearBuilt){
//            case 2: selectedYear = (int) (1919 + rnd * 39);
//                break;
//            case 5: selectedYear = (int) (1949 + rnd * 41);
//                break;
//            case 6: selectedYear = (int) (1991 + rnd * 9);
//                break;
//            case 9: selectedYear = (int) (2001 + rnd * 10);
//                break;
//        }
//        return selectedYear;
//    }
//
//    private static int guessQualityDE(int heatingType, int heatingEnergy, int additionalHeating, int yearBuilt, int numberofQualityLevels){
//        //guess quality of dwelling based on construction year and heating characteristics.
//        //kitchen and bathroom quality are not coded on the micro data
//        int quality = numberofQualityLevels;
//        if (heatingType > 2) quality--; //reduce quality if not central or district heating
//        if (heatingEnergy > 4) quality--; //reduce quality if energy is not gas, electricity or heating oil (i.e. coal, wood, biomass, solar energy)
//        if (additionalHeating == 0) quality++; //increase quality if there is additional heating in the house (regardless the used energy)
//        if (yearBuilt > 0){
//            //Ages - 1: before 1919, 2: 1919-1948, 3: 1949-1978, 4: 1979 - 1986; 5: 1987 - 1990; 6: 1991 - 2000; 7: 2001 - 2004; 8: 2005 - 2008, 9: 2009 or later,
//            float[] deteriorationProbability = {0.9f, 0.8f, 0.6f, 0.3f, 0.12f, 0.08f, 0.05f, 0.04f, 0.04f};
//            float prob = deteriorationProbability[yearBuilt - 1];
//            //attempt to drop quality by age two times (to get some spreading of quality levels)
//            quality = quality - SiloUtil.select(new double[]{1 - prob ,prob});
//            quality = quality - SiloUtil.select(new double[]{1 - prob, prob});
//        }
//        quality = Math.max(quality, 1);      // ensure that quality never drops below 1
//        quality = Math.min(quality, numberofQualityLevels);      // ensure that quality never excess the number of quality levels
//        return quality;
//    }
//
//    private static DefaultDwellingTypeImpl guessDwellingType(int buildingSize, float ddType1Prob, float ddType3Prob){
//        //Guess dwelling type based on the number of dwellings in the building from micro data (buildingSize, from micro data)
//        //and the probability of having 1 dwelling out of having 1 or 2 (distribution in the municipality, from census)
//        //and the probability of having 3-6 dwellings out of having 3-3+ (distribution in the municipality, from census)
//        DefaultDwellingTypeImpl type = DefaultDwellingTypeImpl.MF234;
//        if (buildingSize < 3){
//            if (SiloUtil.getRandomNumberAsFloat() < ddType1Prob){
//                type = DefaultDwellingTypeImpl.SFD;
//            } else {
//                type = DefaultDwellingTypeImpl.SFA;
//            }
//        } else {
//            if (SiloUtil.getRandomNumberAsFloat() < ddType3Prob){
//                type = DefaultDwellingTypeImpl.MF5plus;
//            }
//        }
//
//        return type;
//    }
//
//
//    private static DefaultDwellingTypeImpl guessDwellingType(String ddType){
//        //Guess dwelling type based on the number of dwellings in the building from micro data (buildingSize, from micro data)
//        //and the probability of having 1 dwelling out of having 1 or 2 (distribution in the municipality, from census)
//        //and the probability of having 3-6 dwellings out of having 3-3+ (distribution in the municipality, from census)
//        DefaultDwellingTypeImpl type = DefaultDwellingTypeImpl.MF234;
//        if (ddType == "MF234"){
//
//        } else if (ddType.equals("MF5plus")){
//            type = DefaultDwellingTypeImpl.MF5plus;
//        } else if (ddType.equals("SFD")) {
//            type = DefaultDwellingTypeImpl.SFD;
//        } else if (ddType.equals("SFA")) {
//            type = DefaultDwellingTypeImpl.SFA;
//        }
//        return type;
//    }
//
//    private void identifyVacantJobsByZoneType() {
//        // populate HashMap with Jobs by zone and job type
//        // adapted from SyntheticPopUS
//
//        logger.info("  Identifying vacant jobs by zone");
//        Collection<Job> jobs = dataContainer.getJobData().getJobs();
//
//        idVacantJobsByZoneType = new HashMap<>();
//        numberVacantJobsByType = new HashMap<>();
//        idZonesVacantJobsByType = new HashMap<>();
//        numberZonesByType = new HashMap<>();
//        numberVacantJobsByZoneByType = new HashMap<>();
//        jobStringTypes = ResourceUtil.getArray(rb, PROPERTIES_JOB_TYPES);
//        jobIntTypes = new HashMap<>();
//        for (int i = 0; i < jobStringTypes.length; i++) {
//            jobIntTypes.put(jobStringTypes[i], i);
//        }
//        int[] cellsID = cellsMatrix.getColumnAsInt("ID_cell");
//
//        //create the counter hashmaps
//        for (int i = 0; i < jobStringTypes.length; i++){
//            int type = jobIntTypes.get(jobStringTypes[i]);
//            numberZonesByType.put(type,0);
//            numberVacantJobsByType.put(type,0);
//            for (int j = 0; j < cellsID.length; j++){
//                numberVacantJobsByZoneByType.put(type + cellsID[j] * 100, 0);
//            }
//        }
//        //get the totals
//        for (Job jj: jobs) {
//            if (jj.getWorkerId() == -1) {
//                int type = jobIntTypes.get(jj.getType());
//                int typeZone = type + jj.getZoneId() * 100;
//                //update the set of zones that have ID
//                if (numberVacantJobsByZoneByType.get(typeZone) == 0){
//                    numberZonesByType.put(type, numberZonesByType.get(type) + 1);
//                }
//                //update the number of vacant jobs per job type
//                numberVacantJobsByType.put(type, numberVacantJobsByType.get(type) + 1);
//                numberVacantJobsByZoneByType.put(typeZone, numberVacantJobsByZoneByType.get(typeZone) + 1);
//            }
//        }
//        //create the IDs Hashmaps and reset the counters
//        for (int i = 0; i < jobStringTypes.length; i++){
//            int type = jobIntTypes.get(jobStringTypes[i]);
//            int[] dummy = SiloUtil.createArrayWithValue(numberZonesByType.get(type),0);
//            idZonesVacantJobsByType.put(type,dummy);
//            numberZonesByType.put(type,0);
//            for (int j = 0; j < cellsID.length; j++){
//                int typeZone = type + cellsID[j] * 100;
//                int[] dummy2 = SiloUtil.createArrayWithValue(numberVacantJobsByZoneByType.get(typeZone), 0);
//                idVacantJobsByZoneType.put(typeZone, dummy2);
//                numberVacantJobsByZoneByType.put(typeZone, 0);
//            }
//        }
//        //fill the Hashmaps with IDs
//        for (Job jj: jobs) {
//            if (jj.getWorkerId() == -1) {
//                int type = jobIntTypes.get(jj.getType());
//                int typeZone = jobIntTypes.get(jj.getType()) + jj.getZoneId() * 100;
//                //update the list of job IDs per zone and job type
//                int [] previousJobIDs = idVacantJobsByZoneType.get(typeZone);
//                previousJobIDs[numberVacantJobsByZoneByType.get(typeZone)] = jj.getId();
//                idVacantJobsByZoneType.put(typeZone,previousJobIDs);
//                //update the set of zones that have ID
//                if (numberVacantJobsByZoneByType.get(typeZone) == 0){
//                    int[] previousZones = idZonesVacantJobsByType.get(type);
//                    previousZones[numberZonesByType.get(type)] = typeZone;
//                    idZonesVacantJobsByType.put(type,previousZones);
//                    numberZonesByType.put(type, numberZonesByType.get(type) + 1);
//                }
//                //update the number of vacant jobs per job type
//                numberVacantJobsByZoneByType.put(typeZone, numberVacantJobsByZoneByType.get(typeZone) + 1);
//            }
//        }
//    }
//
//
//    private void identifyVacantSchoolsByZoneByType(){
//        logger.info("   Create vacant schools");
//
//        numberVacantSchoolsByZoneByType = new HashMap<>();
//        numberZonesWithVacantSchoolsByType = new HashMap<>();
//        idZonesVacantSchoolsByType = new HashMap<>();
//        schoolCapacityByType = new HashMap<>();
//        schoolTypes = ResourceUtil.getIntegerArray(rb, PROPERTIES_SCHOOL_TYPES_DE);
//        int[] cellsID = cellsMatrix.getColumnAsInt("ID_cell");
//
//
//        //create the counter hashmaps
//        for (int col = 0; col < schoolTypes.length; col++){
//            int i = schoolTypes[col];
//            for (int j : cellsID){
//                int count = (int) cellsMatrix.getIndexedValueAt(j,"school" + i);
//                numberVacantSchoolsByZoneByType.put(i + j * 100, count);
//                if (count > 0) {
//                    if (idZonesVacantSchoolsByType.containsKey(i)){
//                        numberZonesWithVacantSchoolsByType.put(i,numberZonesWithVacantSchoolsByType.get(i) + 1);
//                        int[] zones = idZonesVacantSchoolsByType.get(i);
//                        zones = SiloUtil.expandArrayByOneElement(zones, i + j * 100);
//                        idZonesVacantSchoolsByType.put(i, zones);
//                        schoolCapacityByType.put(i, schoolCapacityByType.get(i) + count);
//                    } else {
//                        numberZonesWithVacantSchoolsByType.put(i, 1);
//                        int[] zones = {i + j * 100};
//                        idZonesVacantSchoolsByType.put(i, zones);
//                        schoolCapacityByType.put(i,count);
//                    }
//                }
//            }
//        }
//
//
//
//    }
//
//
//    public static String[] expandArrayByOneElement (String[] existing, String addElement) {
//        // create new array that has length of existing.length + 1 and copy values into new array
//        String[] expanded = new String[existing.length + 1];
//        System.arraycopy(existing, 0, expanded, 0, existing.length);
//        expanded[expanded.length - 1] = addElement;
//        return expanded;
//    }
//
//    public static int[] expandArrayByOneElement (int[] existing, int addElement) {
//        // create new array that has length of existing.length + 1 and copy values into new array
//        int[] expanded = new int[existing.length + 1];
//        System.arraycopy(existing, 0, expanded, 0, existing.length);
//        expanded[expanded.length - 1] = addElement;
//        return expanded;
//    }
//
//    private int convertToInteger(String s) {
//        // converts s to an integer value, one or two leading spaces are allowed
//
//        try {
//            return Integer.parseInt(s.trim());
//        } catch (Exception e) {
//            boolean spacesOnly = true;
//            for (int pos = 0; pos < s.length(); pos++) {
//                if (!s.substring(pos, pos+1).equals(" ")) spacesOnly = false;
//            }
//            if (spacesOnly) return -999;
//            else {
//                logger.fatal("String " + s + " cannot be converted into an integer.");
//                return 0;
//            }
//        }
//    }
//
//
//    public static int[] select (double[] probabilities, int[] id) {
//        // select item based on probabilities (for zero-based float array)
//        double sumProb = 0;
//        int[] results = new int[2];
//        for (double val: probabilities) sumProb += val;
//        double selPos = sumProb * SiloUtil.getRandomNumberAsFloat();
//        double sum = 0;
//        for (int i = 0; i < probabilities.length; i++) {
//            sum += probabilities[i];
//            if (sum > selPos) {
//                //return i;
//                results[0] = id[i];
//                results[1] = i;
//                return results;
//            }
//        }
//        results[0] = id[probabilities.length - 1];
//        results[1] = probabilities.length - 1;
//        return results;
//    }
//
//
//    public static int select (int[] probabilities) {
//        // select item based on probabilities (for zero-based float array)
//        double selPos = SiloUtil.getSum(probabilities) * SiloUtil.getRandomNumberAsDouble();
//        double sum = 0;
//        for (int i = 0; i < probabilities.length; i++) {
//            sum += probabilities[i];
//            if (sum > selPos) {
//                return i;
//            }
//        }
//        return probabilities.length - 1;
//    }
//
//
//    public static int[] select (double[] probabilities, int[] id, int sumProb) {
//        // select item based on probabilities (for zero-based float array)
//        int[] results = new int[2];
//        double selPos = sumProb * SiloUtil.getRandomNumberAsFloat();
//        double sum = 0;
//        for (int i = 0; i < probabilities.length; i++) {
//            sum += probabilities[i];
//            if (sum > selPos) {
//                //return i;
//                results[0] = id[i];
//                results[1] = i;
//                return results;
//            }
//        }
//        results[0] = id[probabilities.length - 1];
//        results[1] = probabilities.length - 1;
//        return results;
//    }
//
//    public static int[] select (double[] probabilities, int[] id, double sumProb) {
//        // select item based on probabilities (for zero-based float array)
//        int[] results = new int[2];
//        double selPos = sumProb * SiloUtil.getRandomNumberAsDouble();
//        double sum = 0;
//        for (int i = 0; i < probabilities.length; i++) {
//            sum += probabilities[i];
//            if (sum > selPos) {
//                //return i;
//                results[0] = id[i];
//                results[1] = i;
//                return results;
//            }
//        }
//        results[0] = id[probabilities.length - 1];
//        results[1] = probabilities.length - 1;
//        return results;
//    }
//
//
//    public static int[] select (double[] probabilities, int length, int[] id){
//        //select item based on probabilities and return the name
//        //probabilities and name have more items than the required (max number of required items is set on "length")
//        double sumProb = 0;
//        int[] results = new int[2];
//        for (int i = 0; i < length; i++) {
//            sumProb += probabilities[i];
//        }
//        double selPos = sumProb * SiloUtil.getRandomNumberAsDouble();
//        double sum = 0;
//        for (int i = 0; i < probabilities.length; i++) {
//            sum += probabilities[i];
//            if (sum > selPos) {
//                //return i;
//                results[0] = id[i];
//                results[1] = i;
//                return results;
//            }
//        }
//        results[0] = id[probabilities.length - 1];
//        results[1] = probabilities.length - 1;
//        return results;
//    }
//
//
//    public static int[] select (double[] probabilities, int length, int[] id, double sumProb){
//        //select item based on probabilities and return the name
//        //probabilities and name have more items than the required (max number of required items is set on "length")
//
//        int[] results = new int[2];
//        double selPos = sumProb * SiloUtil.getRandomNumberAsDouble();
//        double sum = 0;
//        for (int i = 0; i < probabilities.length; i++) {
//            sum += probabilities[i];
//            if (sum > selPos) {
//                //return i;
//                results[0] = id[i];
//                results[1] = i;
//                return results;
//            }
//        }
//        results[0] = id[probabilities.length - 1];
//        results[1] = probabilities.length - 1;
//        return results;
//    }
//
//
//    public static TableDataSet updateCountersHousehold (Household household, TableDataSet attributesCount, int mun){
//        /* method to update the counters with the characteristics of the generated private household*/
//        if (household.getHhSize() == 1){
//            attributesCount.setIndexedValueAt(mun,"hhSize1",attributesCount.getIndexedValueAt(mun,"hhSize1") + 1);
//        } else if (household.getHhSize() == 2){
//            attributesCount.setIndexedValueAt(mun,"hhSize2",attributesCount.getIndexedValueAt(mun,"hhSize2") + 1);
//        } else if (household.getHhSize() == 3){
//            attributesCount.setIndexedValueAt(mun,"hhSize3",attributesCount.getIndexedValueAt(mun,"hhSize3") + 1);
//        } else if (household.getHhSize() == 4){
//            attributesCount.setIndexedValueAt(mun,"hhSize4",attributesCount.getIndexedValueAt(mun,"hhSize4") + 1);
//        } else if (household.getHhSize() == 5){
//            attributesCount.setIndexedValueAt(mun,"hhSize5",attributesCount.getIndexedValueAt(mun,"hhSize5") + 1);
//        } else if (household.getHhSize() > 5){
//            attributesCount.setIndexedValueAt(mun,"hhSize5",attributesCount.getIndexedValueAt(mun,"hhSize5") + 1);
//        }
//        attributesCount.setIndexedValueAt(mun,"hhTotal",attributesCount.getIndexedValueAt(mun,"hhTotal") + 1);
//        return attributesCount;
//    }
//
//    public static TableDataSet updateCountersPerson (Person person, TableDataSet attributesCount,int mun, int[] ageBracketsPerson) {
//        /* method to update the counters with the characteristics of the generated person in a private household*/
//        attributesCount.setIndexedValueAt(mun, "population", attributesCount.getIndexedValueAt(mun, "population") + 1);
//        if (person.getNationality() == Nationality.OTHER) {
//            attributesCount.setIndexedValueAt(mun, "foreigners", attributesCount.getIndexedValueAt(mun, "foreigners") + 1);
//        }
//        if (person.getGender() == Gender.MALE) {
//            if (person.getOccupation() == Occupation.EMPLOYED) {
//                attributesCount.setIndexedValueAt(mun, "maleWorkers", attributesCount.getIndexedValueAt(mun, "maleWorkers") + 1);
//            }
//        } else {
//            if (person.getOccupation() == Occupation.EMPLOYED) {
//                attributesCount.setIndexedValueAt(mun, "femaleWorkers", attributesCount.getIndexedValueAt(mun, "femaleWorkers") + 1);
//            }
//        }
//        int age = person.getAge();
//        int row1 = 0;
//        while (age > ageBracketsPerson[row1]) {
//            row1++;
//        }
//        if (person.getGender() == Gender.FEMALE) {
//            String name = "male" + ageBracketsPerson[row1];
//            attributesCount.setIndexedValueAt(mun, name, attributesCount.getIndexedValueAt(mun, name) + 1);
//        } else {
//            String name = "female" + ageBracketsPerson[row1];
//            attributesCount.setIndexedValueAt(mun, name, attributesCount.getIndexedValueAt(mun, name) + 1);
//        }
//        return attributesCount;
//    }
//
//
//    public static TableDataSet updateCountersDwelling (Dwelling dwelling, TableDataSet attributesCount, int mun, int[] yearBrackets, int[] sizeBrackets){
//        /* method to update the counters with the characteristics of the generated dwelling*/
//        if (dwelling.getUsage() == DwellingUsage.OWNED){
//            attributesCount.setIndexedValueAt(mun,"ownDwellings",attributesCount.getIndexedValueAt(mun,"ownDwellings") + 1);
//        } else {
//            attributesCount.setIndexedValueAt(mun,"rentedDwellings",attributesCount.getIndexedValueAt(mun,"rentedDwellings") + 1);
//        }
//        if (dwelling.getBuildingSize() == 1){
//            attributesCount.setIndexedValueAt(mun,"smallDwellings",attributesCount.getIndexedValueAt(mun,"smallDwellings") + 1);
//        } else {
//            attributesCount.setIndexedValueAt(mun,"mediumDwellings",attributesCount.getIndexedValueAt(mun,"mediumDwellings") + 1);
//        }
//        return attributesCount;
//    }
//
//
//    public void writeVectorToCSV(int[] thresholds, double[] frequencies, String outputFile, double a, double g){
//        try {
//
//            PrintWriter pw = new PrintWriter(new FileWriter(outputFile, true));
//            pw.println("alpha,gamma,threshold,frequency,iteration");
//
//            for (int i = 0; i< thresholds.length; i++) {
//                pw.println(a + "," + g + "," + thresholds[i] + "," + frequencies[i]);
//            }
//            pw.flush();
//            pw.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void writeMatrixToCSV(String outputFile, TableDataSet matrix, Double alpha, Double gamma, int count){
//        try {
//
//            PrintWriter pw = new PrintWriter(new FileWriter(outputFile, true));
//
//            for (int i = 1; i<= matrix.getRowCount(); i++) {
//                String line = Integer.toString((int) matrix.getValueAt(i,1));
//                for  (int j = 2; j <= matrix.getColumnCount(); j++){
//                    line = line + "," + Integer.toString((int) matrix.getValueAt(i,j));
//                }
//                line = line + "," + alpha + "," + gamma + "," + count;
//                pw.println(line);
//            }
//            pw.flush();
//            pw.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//
//    public void checkTripLengthDistribution (Frequency travelTimes, double alpha, double gamma, String fileName, double step){
//        //to obtain the trip length distribution
//        int[] timeThresholds1 = new int[79];
//        double[] frequencyTT1 = new double[79];
//        for (int row = 0; row < timeThresholds1.length; row++) {
//            timeThresholds1[row] = row + 1;
//            frequencyTT1[row] = travelTimes.getCumPct(timeThresholds1[row]);
//            //logger.info("Time: " + timeThresholds1[row] + ", cummulated frequency:  " + frequencyTT1[row]);
//        }
//        writeVectorToCSV(timeThresholds1, frequencyTT1, fileName, alpha, gamma);
//
//    }
//
//    public void checkodMatrix (TableDataSet odMatrix, double a, double g, int it, String fileName){
//        //to obtain the square difference between the observed and estimated OD flows
//        double dif = 0;
//        double ind = 0;
//        int count = 0;
//        for (int row = 1; row <= odMatrix.getRowCount(); row++){
//            ind = odMatrix.getValueAt(row,Integer.toString(it)) - odMatrix.getValueAt(row,"ObservedFlow");
//            dif = dif + ind * ind;
//            count++;
//        }
//        try {
//            PrintWriter pw = new PrintWriter(new FileWriter(fileName, true));
//            pw.println(a + "," + g + "," + dif + "," + dif / count + "," + it);
//            pw.flush();
//            pw.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static double[] createArrayDoubles(double initial, double end, int length){
//        //Create one array with specified start, end and number of elements
//
//        double[] array = new double[length + 1];
//        double step = (end - initial) / length;
//        for (int i = 0; i < array.length; i++){
//            array[i] = initial + i * step;
//        }
//        return array;
//
//    }
//
//
//    private void readAndStoreMicroData(){
//        //method to read the synthetic population initial data
//        logger.info("   Starting to read the micro data");
//
//        //Scanning the file to obtain the number of households and persons in Bavaria
//
//        jobsTable = SiloUtil.readCSVfile(rb.getString(PROPERTIES_JOB_DESCRIPTION));
//        schoolLevelTable = SiloUtil.readCSVfile(rb.getString(PROPERTIES_SCHOOL_DESCRIPTION));
//        educationDegreeTable = SiloUtil.readCSVfile(rb.getString(PROPERTIES_EDUCATION_DESCRIPTION));
//        double incomeShape = ResourceUtil.getDoubleProperty(rb,PROPERTIES_INCOME_GAMMA_SHAPE);
//        double incomeRate = ResourceUtil.getDoubleProperty(rb,PROPERTIES_INCOME_GAMMA_RATE);
//        double[] incomeProbability = ResourceUtil.getDoubleArray(rb,PROPERTIES_INCOME_GAMMA_PROBABILITY);
//        GammaDistributionImpl gammaDist = new GammaDistributionImpl(incomeShape, 1/incomeRate);
//        String pumsFileName = Properties.get().main.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_MICRODATA_2010_PATH);
//        String recString = "";
//        int recCount = 0;
//        int hhCountTotal = 0;
//        int personCountTotal = 0;
//        try {
//
//            BufferedReader in = new BufferedReader(new FileReader(pumsFileName));
//            int previousHouseholdNumber = -1;
//            while ((recString = in.readLine()) != null) {
//                recCount++;
//                int householdNumber = 0;
//                int recLander = convertToInteger(recString.substring(0,2));
//                switch (recLander){
//                    case 9:
//                        householdNumber = convertToInteger(recString.substring(2,8)) * 1000 + convertToInteger(recString.substring(8,11));
//                        if (householdNumber != previousHouseholdNumber) {
//                            hhCountTotal++;
//                            personCountTotal++;
//                            previousHouseholdNumber = householdNumber; // Update the household number
//
//                        } else if (householdNumber == previousHouseholdNumber) {
//                            personCountTotal++;
//                        }
//                }
//            }
//            logger.info("  Read " + (personCountTotal) + " person records in " +
//                    (hhCountTotal) + " private households from file: " + pumsFileName);
//        } catch (IOException e) {
//            logger.fatal("IO Exception caught reading synpop household file: " + pumsFileName);
//            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
//        }
//
//
//        TableDataSet microHouseholds = new TableDataSet();
//        TableDataSet microPersons = new TableDataSet();
//        int[] dummy = SiloUtil.createArrayWithValue(hhCountTotal,0);
//        int[] dummy1 = SiloUtil.createArrayWithValue(personCountTotal,0);
//        int[] dummy4 = SiloUtil.createArrayWithValue(personCountTotal,0);
//        int[] dummy5 = SiloUtil.createArrayWithValue(personCountTotal,0);
//        microHouseholds.appendColumn(dummy,"IDhh");
//        microPersons.appendColumn(dummy1,"IDpp");
//        microPersons.appendColumn(dummy4,"IDhh");
//        microPersons.appendColumn(dummy5,"WZ08");
//
//
//        //Obtain person and household variables and add one column to the microData
//        TableDataSet ppVariables = SiloUtil.readCSVfile(rb.getString(PROPERTIES_ATRIBUTES_MICRODATA_PP));// variables at the person level
//        TableDataSet hhVariables = SiloUtil.readCSVfile(rb.getString(PROPERTIES_ATRIBUTES_MICRODATA_HH)); //variables at the household level
//        for (int i = 1; i <= ppVariables.getRowCount(); i++){
//            int[] dummy2 = SiloUtil.createArrayWithValue(personCountTotal,0);
//            microPersons.appendColumn(dummy2,ppVariables.getStringValueAt(i,"EF"));
//        }
//        for (int i = 1; i <= hhVariables.getRowCount(); i++){
//            int[] dummy2 = SiloUtil.createArrayWithValue(hhCountTotal,0);
//            microHouseholds.appendColumn(dummy2,hhVariables.getStringValueAt(i,"EF"));
//            int[] dummy3 = SiloUtil.createArrayWithValue(personCountTotal,0);
//            microPersons.appendColumn(dummy3,hhVariables.getStringValueAt(i,"EF"));
//        }
//
//
//        //read the micro data and assign the characteristics
//        int hhCount = 0;
//        int personCount = 0;
//        try {
//            BufferedReader in = new BufferedReader(new FileReader(pumsFileName));
//            int previousHouseholdNumber = -1;
//            while ((recString = in.readLine()) != null) {
//                recCount++;
//                int recLander = convertToInteger(recString.substring(0,2));
//                switch (recLander){
//                    case 9:
//                        int householdNumber = convertToInteger(recString.substring(2,8)) * 1000 + convertToInteger(recString.substring(8,11));
//                        //Household attributes (only if the number of household differs)
//                        if (householdNumber != previousHouseholdNumber) {
//                            hhCount++;
//                            microHouseholds.setValueAt(hhCount,"IDhh",hhCount);
//                            for (int i = 1; i <= hhVariables.getRowCount(); i++){
//                                int start = (int) hhVariables.getValueAt(i,"initial");
//                                int finish = (int) hhVariables.getValueAt(i,"end");
//                                microHouseholds.setValueAt(hhCount,hhVariables.getStringValueAt(i,"EF"),convertToInteger(recString.substring(start,finish)));
//                            }
//                            previousHouseholdNumber = householdNumber; // Update the household number
//                        }
//                        //Person attributes
//                        personCount++;
//                        microPersons.setValueAt(personCount,"IDpp",personCount);
//                        microPersons.setValueAt(personCount,"IDhh",hhCount);
//                        for (int i = 1; i <= ppVariables.getRowCount(); i++){
//                            int start = (int) ppVariables.getValueAt(i,"initial");
//                            int finish = (int) ppVariables.getValueAt(i,"end");
//                            microPersons.setValueAt(personCount,ppVariables.getStringValueAt(i,"EF"),convertToInteger(recString.substring(start,finish)));
//                        }
//                        for (int i = 1; i <= hhVariables.getRowCount(); i++){
//                            int start = (int) hhVariables.getValueAt(i,"initial");
//                            int finish = (int) hhVariables.getValueAt(i,"end");
//                            microPersons.setValueAt(personCount,hhVariables.getStringValueAt(i,"EF"),convertToInteger(recString.substring(start,finish)));
//                        }
//                        //translate the person categories to our categories
//                        int school = (int) microPersons.getValueAt(personCount,"ppSchool");
//                        if (school > 0) {
//                            microPersons.setValueAt(personCount, "ppSchool", translateEducationLevel(school, schoolLevelTable));
//                        } else {
//                            microPersons.setValueAt(personCount, "ppSchool", 0);
//                        }
//                        if (microPersons.getValueAt(personCount,"ppOccupation") == 1) { // Only employed persons respond to the sector
//                            //microPersons.setValueAt(personCount,"WZ08", translateJobType(Math.round((int) microPersons.getValueAt(personCount,"ppSector1")/10), jobsTable)); //First two digits of the WZ08 job classification in Germany. They are converted to 10 job classes (Zensus 2011 - Erwerbstätige nach Wirtschaftszweig Wirtschafts(unter)bereiche)
//                        } else {
//                            microPersons.setValueAt(personCount,"WZ08",0);
//                        }
//                        int income = (int) microPersons.getValueAt(personCount,"ppIncome");
//                        try{
//                            microPersons.setValueAt(personCount,"ppIncome",(int) translateIncome(income, incomeProbability, gammaDist));
//                        } catch (MathException e){
//                            e.printStackTrace();
//                        }
//                        int education = (int) microPersons.getValueAt(personCount,"ppEducation");
//                        microPersons.setValueAt(personCount,"ppEducation", translateEducationLevel(education, educationDegreeTable));
//                }
//            }
//            logger.info("  Read " + (personCountTotal) + " person records in " +
//                    (hhCountTotal) + " private households from file: " + pumsFileName);
//        } catch (IOException e) {
//            logger.fatal("IO Exception caught reading synpop household file: " + pumsFileName);
//            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
//        }
//
//        SiloUtil.writeTableDataSet(microPersons, "input/summary/microPersonsAna.csv");
//        SiloUtil.writeTableDataSet(microHouseholds, "input/summary/microHouseholdsAna.csv");
//
//        logger.info("   Finished reading the micro data");
//    }
//
//
//    public int selectJobType(Person person) {
//        //given a person, select the job type. It is based on the probabilities
//
//        double[] probabilities = new double[jobStringTypes.length];
//        int[] jobTypes = new int[jobStringTypes.length];
//        //Person and job type values
//        String name = "";
//        if (person.getGender() == Gender.MALE) {
//            name = "maleEducation";
//        } else {
//            name = "femaleEducation";
//        }
//        name = name + educationalLevelByPerson.get(person);
//
//        //if (jobStringTypes.length == probabilitiesJob.getRowCount()) {
//        //    probabilities = probabilitiesJob.getColumnAsDouble(name);
//        //} else {
//        for (int job = 0; job < jobStringTypes.length; job++){
//            jobTypes[job] = jobIntTypes.get(jobStringTypes[job]);
//            probabilities[job] = probabilitiesJob.getStringIndexedValueAt(jobStringTypes[job],name);
//        }
//        //}
//        return new EnumeratedIntegerDistribution(jobTypes, probabilities).sample();
//
//    }
//
//
//    private HashMap<String,HashMap<Integer,Integer>> updateInnerMap(HashMap<String, HashMap<Integer, Integer>> outer, int gender, int age, int row) {
//
//        String key = "male";
//        if (gender == 2) {
//            key = "female";
//        }
//        HashMap<Integer, Integer> inner = outer.get(key);
//        if (inner == null){
//            inner = new HashMap<Integer, Integer>();
//            outer.put(key, inner);
//        }
//        inner.put(row, age);
//        return outer;
//    }
//
//
//    private void setRoles(HashMap<String, HashMap<Integer, Integer>> singles, HashMap<String, HashMap<Integer, Integer>> married,
//                          HashMap<Integer, Integer> childrenInHousehold, HashMap<String, HashMap<Integer, Integer>> noClass) {
//
//        //set children in the household
//        if (childrenInHousehold != null){
//            for (Map.Entry<Integer, Integer> pair : childrenInHousehold.entrySet()){
//                int row = pair.getKey();
//                microDataPerson.setValueAt(row, "personRole", 3);
//            }
//        }
//        //set singles and married in the household
//        String[] keys = {"male", "female"};
//        for (int i = 0; i < keys.length; i++) {
//            String key = keys[i];
//            HashMap<Integer, Integer> inner = singles.get(key);
//            if (inner != null) {
//                for (Map.Entry<Integer, Integer> pair : inner.entrySet()) {
//                    int row = pair.getKey();
//                    microDataPerson.setValueAt(row, "personRole", 1);
//                }
//            }
//            inner = married.get(key);
//            if (inner != null) {
//                for (Map.Entry<Integer, Integer> pair : inner.entrySet()) {
//                    int row = pair.getKey();
//                    microDataPerson.setValueAt(row, "personRole", 2);
//                }
//            }
//            inner = noClass.get(key);
//            if (inner != null) {
//                for (Map.Entry<Integer, Integer> pair : inner.entrySet()) {
//                    int row = pair.getKey();
//                    microDataPerson.setValueAt(row, "rearrangedRole", 1);
//                }
//            }
//        }
//    }
//
//
//    public int[] selectWorkplace(int home, HashMap<Integer, Integer> vacantJobsByZoneByType,
//                                 int[] zoneJobKeys, int lengthZoneKeys, Matrix impedanceMatrix) {
//        //given a person and job type, select the workplace location (raster cell)
//        //it is based on the utility of that job type and each location, multiplied by the number of jobs that remain vacant
//        //it can be directly used for schools, since the utility only checks the distance between the person home and destination
//
//        double[] probabilities = new double[lengthZoneKeys];
//        for (int j = 0; j < lengthZoneKeys; j++){
//            probabilities[j] = impedanceMatrix.getValueAt(home, zoneJobKeys[j] / 100) * vacantJobsByZoneByType.get(zoneJobKeys[j]);
//            //probability = impedance * number of vacant jobs. Impedance is calculated in advance as exp(utility)
//        }
//        return select(probabilities,zoneJobKeys);
//    }
//
//    public int[] selectClosestSchool(int home, HashMap<Integer, Integer> vacantJobsByZoneByType,
//                                     int[] zoneJobKeys, int lengthZoneKeys, Matrix impedanceMatrix) {
//        //given a person and job type, select the workplace location (raster cell)
//        //it is based on the utility of that job type and each location, multiplied by the number of jobs that remain vacant
//        //it can be directly used for schools, since the utility only checks the distance between the person home and destination
//
//
//        int[] min = new int[2];
//        min[0] = zoneJobKeys[0];
//        min[1] = 0;
//        double minDist = impedanceMatrix.getValueAt(home, zoneJobKeys[0] / 100);
//        for (int j = 1; j < lengthZoneKeys; j++) {
//            if (impedanceMatrix.getValueAt(home, zoneJobKeys[j] / 100) < minDist) {
//                min[0] = zoneJobKeys[j];
//                min[1] = j;
//                minDist = impedanceMatrix.getValueAt(home, zoneJobKeys[j] / 100);
//            }
//        }
//        return min;
//    }
//
//
//    private void generateCountersForValidation(){
//        //method to obtain the errors from the generated synthetic population
//        //Create the errors table (for all the municipalities)
//        counterMunicipality = new TableDataSet();
//        errorMunicipality = new TableDataSet();
//        counterMunicipality.appendColumn(cityID,"ID_city");
//        errorMunicipality.appendColumn(cityID,"ID_city");
//        for(int attribute = 0; attribute < attributesMunicipality.length; attribute++) {
//            double[] dummy2 = SiloUtil.createArrayWithValue(cityID.length,0.0);
//            double[] dummy3 = SiloUtil.createArrayWithValue(cityID.length,0.0);
//            counterMunicipality.appendColumn(dummy2, attributesMunicipality[attribute]);
//            errorMunicipality.appendColumn(dummy3, attributesMunicipality[attribute]);
//        }
//        counterMunicipality.buildIndex(counterMunicipality.getColumnPosition("ID_city"));
//        errorMunicipality.buildIndex(errorMunicipality.getColumnPosition("ID_city"));
//
//
//    }
//
///*    private void addCars(boolean flagSkipCreationOfSPforDebugging) {
//        //method to estimate the number of cars per household
//        //it must be run after generating the population
//        CreateCarOwnershipModel createCarOwnershipModel = new CreateCarOwnershipModel(dataContainer);
//        createCarOwnershipModel.run( );
//    }*/
//
//
//    public static int[] createConsecutiveArray (int arrayLength) {
//        // fill one-dimensional boolean array with value
//
//        int[] anArray = new int[arrayLength];
//        for (int i = 0; i < anArray.length; i++) anArray[i] = i;
//        return anArray;
//    }
//
//    private void updateInnerMap(HashMap<String, HashMap<String, Integer>> map, String label ,String valueString, Integer valueCode){
//
//        if (map.containsKey(label)){
//            HashMap<String, Integer> innerMap = map.get(label);
//            innerMap.put(valueString, valueCode);
//            map.put(label, innerMap);
//        } else {
//            HashMap<String, Integer> innerMap = new HashMap<>();
//            innerMap.put(valueString, valueCode);
//            map.put(label, innerMap);
//        }
//    }
//
//
//    private Map<Integer, Map<String, Double>> updateInnerMap(Map<Integer, Map<String, Double>> map, Integer label ,String valueString, double valueCode){
//
//        if (map.containsKey(label)){
//            Map<String, Double> innerMap = map.get(label);
//            innerMap.put(valueString, valueCode);
//            map.put(label, innerMap);
//        } else {
//            Map<String, Double> innerMap = new HashMap<>();
//            innerMap.put(valueString, valueCode);
//            map.put(label, innerMap);
//        }
//        return map;
//    }
//
//    private void updateInnerMap(HashMap<String, HashMap<Integer, Integer>> map, String label, int valueInt, Integer valueCode){
//
//        if (map.containsKey(label)){
//            HashMap<Integer, Integer> innerMap = map.get(label);
//            innerMap.put(valueInt, valueCode);
//            map.put(label, innerMap);
//        } else {
//            HashMap<Integer, Integer> innerMap = new HashMap<>();
//            innerMap.put(valueInt, valueCode);
//            map.put(label, innerMap);
//        }
//
//    }
//
//    private void updatePersonsInHousehold(int hhId, int id, HashMap<String, Integer> attributeMap) {
//        if (personsInHouseholds.containsKey(hhId)) {
//            HashMap<Integer, HashMap<String, Integer>> inner = personsInHouseholds.get(hhId);
//            inner.put(id, attributeMap);
//            personsInHouseholds.put(hhId, inner);
//        } else {
//            HashMap<Integer, HashMap<String, Integer>> inner = new HashMap<>();
//            inner.put(id, attributeMap);
//            personsInHouseholds.put(hhId, inner);
//        }
//    }
//
//    public double multiplyIfNotZero(double x, double y, double f){
//        if (y == 0){
//            return x;
//        } else {
//            return x * f;
//        }
//    }
//}
