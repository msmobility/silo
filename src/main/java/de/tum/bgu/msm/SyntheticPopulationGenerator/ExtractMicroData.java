package de.tum.bgu.msm.SyntheticPopulationGenerator;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.SiloUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import javax.measure.unit.SI;


/**
 * Reads the micro data as an input for the synthetic population
 * @author Ana Moreno (TUM)
 * Created on August 1, 2017 in Munich
 *
 */

public class ExtractMicroData {
    private ResourceBundle rb;

    //Routes of the input data
    protected static final String PROPERTIES_MICRODATA_PATH               = "micro.data.2010";

    //Attributes at the person and household level
    protected static final String PROPERTIES_VARIABLES_PP                 = "pp.microData.attributes";
    protected static final String PROPERTIES_VARIABLES_HH                 = "hh.microData.attributes";
    protected static final String PROPERTIES_VARIABLES_DD                 = "dd.microData.attributes";
    protected static final String PROPERTIES_EXCEPTION_MICRODATA          = "microData.exceptions";

    //Conversion tables from microdata categories to control total categories
    protected static final String PROPERTIES_HOUSEHOLD_ATTRIBUTES         = "attributes.municipality";
    protected static final String PROPERTIES_EDUCATION_DESCRIPTION        = "education.dictionary";
    protected static final String PROPERTIES_SCHOOL_DESCRIPTION           = "school.dictionary";
    protected static final String PROPERTIES_HOUSEHOLD_SIZES              = "household.size.brackets";
    protected static final String PROPERTIES_MICRO_DATA_AGES              = "age.brackets";
    protected static final String PROPERTIES_MICRO_DATA_GENDER            = "gender.brackets";
    protected static final String PROPERTIES_MICRO_DATA_OCCUPATION        = "occupation.brackets";
    protected static final String PROPERTIES_MICRO_DATA_NATIONALITY       = "nationality.brackets";
    protected static final String PROPERTIES_MICRO_DATA_DWELLING_USE      = "use.brackets";
    protected static final String PROPERTIES_MICRO_DATA_YEAR_DWELLING     = "year.dwelling";
    protected static final String PROPERTIES_MICRO_DATA_FLOOR_SPACE_DWELLING = "floor.space.dwelling";

    protected TableDataSet microHouseholds;
    protected TableDataSet microPersons;
    protected TableDataSet microDwellings;
    protected TableDataSet frequencyMatrix;
    protected TableDataSet ppVariables;
    protected TableDataSet hhVariables;
    protected TableDataSet ddVariables;
    protected TableDataSet exceptions;
    protected TableDataSet educationDegreeTable;
    protected TableDataSet schoolLevelTable;

    protected int[] householdSizes;
    protected int[] ageBracketsPerson;
    protected int[] genderBrackets;
    protected int[] occupationBrackets;
    protected int[] sizeBracketsDwelling;
    protected int[] yearBracketsDwelling;
    protected int[] usageBracketsDwelling;
    private int[] nationalityBrackets;
    protected int numberofQualityLevels;

    protected String[] attributesMunicipality;

    protected TableDataSet weightsTable;
    protected TableDataSet jobsTable;


    static Logger logger = Logger.getLogger(SyntheticPopDe.class);
    private String[] attributes;



    public ExtractMicroData(ResourceBundle rb) {
        // Constructor
        this.rb = rb;
    }


    public void run(){
        //method to create the synthetic population
        logger.info("   Starting to create the synthetic population.");
        long startTime = System.nanoTime();
        setInputData();
        readMicroData();
        translatePersonMicroData();
        createFrequencyMatrix();
        long estimatedTime = System.nanoTime() - startTime;
        logger.info("   Finished creating the synthetic population. Elapsed time: " + estimatedTime);
    }

    private void setInputData() {
        //method with all the inputs that are required to read externally for this class

        //To exclude from the microData some records
        exceptions = SiloUtil.readCSVfile(rb.getString(PROPERTIES_EXCEPTION_MICRODATA));

        //Variables to read from the microData at the person, household and dwelling level
        ppVariables = SiloUtil.readCSVfile(rb.getString(PROPERTIES_VARIABLES_PP));// variables at the person level
        hhVariables = SiloUtil.readCSVfile(rb.getString(PROPERTIES_VARIABLES_HH)); //variables at the household level
        ddVariables = SiloUtil.readCSVfile(rb.getString(PROPERTIES_VARIABLES_DD)); //variables at the household level

        //Dictionaries to translate categories from microData to SILO
        educationDegreeTable = SiloUtil.readCSVfile(rb.getString(PROPERTIES_EDUCATION_DESCRIPTION));
        schoolLevelTable = SiloUtil.readCSVfile(rb.getString(PROPERTIES_SCHOOL_DESCRIPTION));

        //Brackets to define attributes for IPU
        householdSizes = ResourceUtil.getIntegerArray(rb,PROPERTIES_HOUSEHOLD_SIZES);
        ageBracketsPerson = ResourceUtil.getIntegerArray(rb, PROPERTIES_MICRO_DATA_AGES);
        genderBrackets = ResourceUtil.getIntegerArray(rb, PROPERTIES_MICRO_DATA_GENDER);
        occupationBrackets = ResourceUtil.getIntegerArray(rb,PROPERTIES_MICRO_DATA_OCCUPATION);
        nationalityBrackets = ResourceUtil.getIntegerArray(rb,PROPERTIES_MICRO_DATA_NATIONALITY);
        usageBracketsDwelling = ResourceUtil.getIntegerArray(rb,PROPERTIES_MICRO_DATA_DWELLING_USE);
        yearBracketsDwelling = ResourceUtil.getIntegerArray(rb, PROPERTIES_MICRO_DATA_YEAR_DWELLING);
        sizeBracketsDwelling = ResourceUtil.getIntegerArray(rb, PROPERTIES_MICRO_DATA_FLOOR_SPACE_DWELLING);

        //Attributes list
        attributes = ResourceUtil.getArray(rb, PROPERTIES_HOUSEHOLD_ATTRIBUTES);
    }


    private void readMicroData(){
        //method to read the synthetic population initial micro data
        logger.info("   Starting to read the micro data");

        //Scanning the file to obtain the number of households and persons in Bavaria
        String pumsFileName = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_MICRODATA_PATH);
        String recString = "";
        int recCount = 0;
        int hhCount = 0;
        int personCount = 0;
        int hhOutCountTotal = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(pumsFileName));
            int previousHouseholdNumber = -1;
            while ((recString = in.readLine()) != null) {
                recCount++;
                int householdNumber = convertToInteger(recString.substring(2,8)) * 1000 + convertToInteger(recString.substring(8,11));
                int restriction = checkRestrictions(recString);
                if (householdNumber != previousHouseholdNumber & restriction == 1) {
                    hhCount++;
                    personCount++;
                    previousHouseholdNumber = householdNumber; // Update the household number
                } else if (householdNumber == previousHouseholdNumber & restriction == 1) {
                    personCount++;
                }
            }
            logger.info("  Read " + (personCount) + " person records in " +
                    (hhCount) + " private households from file: " + pumsFileName);
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + pumsFileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }

        initializeMicroData(hhCount, personCount);

        //read the micro data and assign the characteristics
        hhCount = 0;
        personCount = 0;
        recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(pumsFileName));
            int previousHouseholdNumber = -1;
            while ((recString = in.readLine()) != null) {
                recCount++;
                int householdNumber = convertToInteger(recString.substring(2,8)) * 1000 + convertToInteger(recString.substring(8,11));
                int restriction = checkRestrictions(recString);
                if (householdNumber != previousHouseholdNumber & restriction == 1) {
                    hhCount++;
                    personCount++;
                    updateMicroHouseholds(hhCount,householdNumber,personCount,recString);
                    updateMicroDwellings(hhCount,recString);
                    updateMicroPersons(personCount,hhCount,householdNumber,recString);
                    previousHouseholdNumber = householdNumber; // Update the household number
                } else if (householdNumber == previousHouseholdNumber & restriction == 1) {
                    personCount++;
                    updateMicroPersons(personCount,hhCount,householdNumber,recString);
                }
            }
            logger.info("  Read " + (personCount) + " person records in " +
                    (hhCount) + " private households from file: " + pumsFileName);
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + pumsFileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }

        String ppFileName = ("input/testing/microPersons.csv");
        SiloUtil.writeTableDataSet(microPersons, ppFileName);
        String hhFileName = ("input/testing/microHouseholds.csv");
        SiloUtil.writeTableDataSet(microHouseholds, hhFileName);
        String ddFileName = ("input/testing/microDwellings.csv");
        SiloUtil.writeTableDataSet(microDwellings, ddFileName);

        logger.info("   Finished reading the micro data");
    }


    private void translatePersonMicroData(){
        //method to translate the categories from the initial micro data to the categories from SILO
        logger.info("   Starting to translate the micro data");

        //convert one by one the records from microPersons
        for (int i = 1; i <= microPersons.getRowCount(); i++){
            int school = (int) microPersons.getValueAt(i,"school");
            microPersons.setValueAt(i,"school", translatefromMicroDataToControlTotal(school,schoolLevelTable));
            int educationDegree = (int) microPersons.getValueAt(i,"educationDegree");
            microPersons.setValueAt(i,"educationDegree", translatefromMicroDataToControlTotal(educationDegree,educationDegreeTable));
            int occupation = (int) microPersons.getValueAt(i,"occupation");
            if (occupation > 1){
                if (school > 0){
                    microPersons.setValueAt(i,"occupation",3);
                } else {
                    microPersons.setValueAt(i,"occupation",2);
                }
            }
        }

        logger.info("   Finished translating the micro data");
    }


    private void createFrequencyMatrix(){
        //create the frequency matrix with all the attributes aggregated at the household level
        logger.info("   Starting to create the frequency matrix");

        //Read the attributes to match and initialize frequency matrix
        initializeAttributesMunicipality();
        //initializeFrequencyMatrix();

        //Update the frequency matrix with the microdata
        for (int i = 1; i <= frequencyMatrix.getRowCount(); i++){


        }
        SiloUtil.writeTableDataSet(frequencyMatrix,"input/testing/frequencyMatrix.csv");






        logger.info("   Finished creating the frequency matrix");
    }

    private void initializeAttributesMunicipality() {
        //Method to create the list of attributes given the generic names and the brackets

        frequencyMatrix = new TableDataSet();
        frequencyMatrix.appendColumn(microHouseholds.getColumnAsInt("id"),"id");

        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < attributes.length; i++){
            map.put(attributes[i],1);
        }
        checkContainsAndAdd("ddSize",sizeBracketsDwelling, map);
        checkContainsAndAdd("ddUse",usageBracketsDwelling, map);
        checkContainsAndAdd("Nat",nationalityBrackets, map);
        checkContainsAndAdd("M_",ageBracketsPerson, map);
        checkContainsAndAdd("F_",ageBracketsPerson, map);
        checkContainsAndAdd("W_",genderBrackets, map);
        checkContainsAndAdd("hhSize",householdSizes, map);
        addIntegerColumnToTableDataSet(frequencyMatrix,"population");
        addIntegerColumnToTableDataSet(frequencyMatrix,"hhTotal");
    }

    private void checkContainsAndAdd(String key, int[] brackets, Map<String, Integer> map) {
        if (map.containsKey(key)){
            for (int i = 0; i < brackets.length; i++){
                String label = key + brackets[i];
                addIntegerColumnToTableDataSet(frequencyMatrix,label);
            }
        }
    }


    private void initializeMicroData(int hhCountTotal, int personCountTotal){
        //method to initialize the TableDataSets from households, persons and dwellings with the labels from the variables to be read

        microHouseholds = new TableDataSet();
        microPersons = new TableDataSet();
        microDwellings = new TableDataSet();
        addIntegerColumnToTableDataSet(microHouseholds,"id", hhCountTotal);
        addIntegerColumnToTableDataSet(microHouseholds,"recordHh");
        addIntegerColumnToTableDataSet(microHouseholds,"firstPerson");
        addIntegerColumnToTableDataSet(microPersons,"id", personCountTotal);
        addIntegerColumnToTableDataSet(microPersons,"idHh");
        addIntegerColumnToTableDataSet(microPersons,"recordHh");
        addIntegerColumnToTableDataSet(microPersons,"recordPp");
        addIntegerColumnToTableDataSet(microDwellings,"id",hhCountTotal);

        for (int i = 1; i <= ppVariables.getRowCount(); i++){
            addIntegerColumnToTableDataSet(microPersons,ppVariables.getStringValueAt(i,"VariableName"));
        }
        for (int i = 1; i <= hhVariables.getRowCount(); i++){
            addIntegerColumnToTableDataSet(microHouseholds,hhVariables.getStringValueAt(i,"VariableName"));
        }
        for (int i = 1; i <= ddVariables.getRowCount(); i++){
            addIntegerColumnToTableDataSet(microDwellings,ddVariables.getStringValueAt(i,"VariableName"));
        }
    }


    private void updateMicroPersons(int personCount, int hhCount, int householdNumber, String recString){
        //method to update the values of the TDS of persons

        microPersons.setValueAt(personCount,"id",personCount);
        microPersons.setValueAt(personCount,"idHh",hhCount);
        microPersons.setValueAt(personCount,"recordHh",householdNumber);
        for (int i = 1; i <= ppVariables.getRowCount(); i++){
            int start = (int) ppVariables.getValueAt(i,"initial");
            int finish = (int) ppVariables.getValueAt(i,"end");
            microPersons.setValueAt(personCount,ppVariables.getStringValueAt(i,"VariableName"),convertToInteger(recString.substring(start,finish)));
        }
    }


    private void updateMicroHouseholds(int hhCount, int householdNumber, int personCount,  String recString){
        //method to update the values of the TDS of households

        microHouseholds.setValueAt(hhCount,"id",hhCount);
        microHouseholds.setValueAt(hhCount,"recordHh",householdNumber);
        microHouseholds.setValueAt(hhCount,"firstPerson",personCount);
        for (int i = 1; i <= hhVariables.getRowCount(); i++){
            int start = (int) hhVariables.getValueAt(i,"initial");
            int finish = (int) hhVariables.getValueAt(i,"end");
            microHouseholds.setValueAt(hhCount,hhVariables.getStringValueAt(i,"VariableName"),convertToInteger(recString.substring(start,finish)));
        }
    }


    private void updateMicroDwellings(int hhCount, String recString){
        //method to update the values of the TDS of dwellings

        microDwellings.setValueAt(hhCount,"id",hhCount);
        for (int i = 1; i <= ddVariables.getRowCount(); i++){
            int start = (int) ddVariables.getValueAt(i,"initial");
            int finish = (int) ddVariables.getValueAt(i,"end");
            microDwellings.setValueAt(hhCount,ddVariables.getStringValueAt(i,"VariableName"),convertToInteger(recString.substring(start,finish)));
        }
    }


    private void initializeFrequencyMatrix(){
        //create the frequency matrix with all the values equal to zero

        frequencyMatrix = new TableDataSet();
        frequencyMatrix.appendColumn(microHouseholds.getColumnAsInt("id"),"id");
        for (int i = 0; i < attributesMunicipality.length; i++){
            addIntegerColumnToTableDataSet(frequencyMatrix,attributesMunicipality[i]);
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

    private int translatefromMicroDataToControlTotal (int input, TableDataSet dictionary){
        //translate 12 education levels to 4
        //jobs is one TableDataSet that is read from a csv file containing the description, ID and types of jobs.
        int output = 0;
        int finish = 0;
        int row = 1;
        while (finish == 0 & row < dictionary.getRowCount()){
            if (input == dictionary.getValueAt(row,"microDataLabel")) {
                finish =1;
                output = (int) dictionary.getValueAt(row,"controlTotalLabel");
            }
            else {
                row++;
            }
        }
        return output;
    }


    private TableDataSet addIntegerColumnToTableDataSet(TableDataSet table, String label){
        int[] dummy3 = SiloUtil.createArrayWithValue(table.getRowCount(),0);
        table.appendColumn(dummy3,label);
        return table;
    }


    private TableDataSet addIntegerColumnToTableDataSet(TableDataSet table, String label, int length){
        int[] dummy3 = SiloUtil.createArrayWithValue(length,0);
        table.appendColumn(dummy3,label);
        return table;
    }


    private int checkRestrictions(String recString){
        int restriction = 1;
        for (int i = 1; i <=exceptions.getRowCount(); i++){
            int start = (int) exceptions.getValueAt(i,"initial");
            int finish = (int) exceptions.getValueAt(i,"end");
            int threshold = (int) exceptions.getValueAt(i,"exceptionIf");
            int value = convertToInteger(recString.substring(start,finish));
            if (threshold == value){
                restriction = 0;
            }
        }
        return restriction;
    }
}
