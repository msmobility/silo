package de.tum.bgu.msm.SyntheticPopulationGenerator;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.SiloUtil;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ExtractMicroDataJP {

    private ResourceBundle rb;

    //Routes of the input data
    protected static final String PROPERTIES_MICRODATA_JP                 = "micro.data";

    //Attributes at the person and household level
    protected static final String PROPERTIES_VARIABLES                    = "microData.attributes";
    protected static final String PROPERTIES_EXCEPTION_MICRODATA          = "microData.exceptions";

    //Conversion tables from microdata categories to control total categories
    protected static final String PROPERTIES_HOUSEHOLD_ATTRIBUTES         = "attributes.municipality";
    protected static final String PROPERTIES_HOUSEHOLD_SIZES              = "household.size.brackets";
    protected static final String PROPERTIES_MICRO_DATA_AGES              = "age.brackets";
    protected static final String PROPERTIES_MICRO_DATA_GENDER            = "gender.brackets";
    protected static final String PROPERTIES_MICRO_DATA_OCCUPATION        = "occupation.brackets";
    protected static final String PROPERTIES_MICRO_DATA_DWELLING_USE      = "use.brackets";
    protected static final String PROPERTIES_MICRO_DATA_TYPE              = "type.brackets";

    protected TableDataSet microHouseholds;
    protected TableDataSet microPersons;
    protected TableDataSet microDwellings;
    protected TableDataSet frequencyMatrix;
    protected TableDataSet exceptions;
    protected TableDataSet educationDegreeTable;
    protected TableDataSet schoolLevelTable;

    protected int[] householdSizes;
    protected int[] ageBracketsPerson;
    protected int[] genderBrackets;
    protected String[] occupationBrackets;
    protected String[] usageBracketsDwelling;
    private String[] typeBracketsDwelling;


    protected String[] attributesMunicipality;

    protected TableDataSet weightsTable;
    protected TableDataSet jobsTable;


    static Logger logger = Logger.getLogger(SyntheticPopDe.class);
    private String[] attributes;
    private TableDataSet variables;
    private HashMap<String, String[]> attributesMicroData;




    public ExtractMicroDataJP(ResourceBundle rb) {
        // Constructor
        this.rb = rb;
    }



    public void run(){
        //method to create the synthetic population
        logger.info("   Starting to create the synthetic population.");
        long startTime = System.nanoTime();
        setInputData();
        setAttributesToCopyFromMicroData();
        readCSVMicroData();
        createFrequencyMatrix();
        long estimatedTime = System.nanoTime() - startTime;
        logger.info("   Finished creating the synthetic population. Elapsed time: " + estimatedTime);
    }


    private void setInputData() {
        //method with all the inputs that are required to read externally for this class

        //To exclude from the microData some records
        //exceptions = SiloUtil.readCSVfile(rb.getString(PROPERTIES_EXCEPTION_MICRODATA));

        //Brackets to define attributes for IPU
        householdSizes = ResourceUtil.getIntegerArray(rb,PROPERTIES_HOUSEHOLD_SIZES);
        ageBracketsPerson = ResourceUtil.getIntegerArray(rb, PROPERTIES_MICRO_DATA_AGES);
        genderBrackets = ResourceUtil.getIntegerArray(rb, PROPERTIES_MICRO_DATA_GENDER);
        occupationBrackets = ResourceUtil.getArray(rb,PROPERTIES_MICRO_DATA_OCCUPATION);
        usageBracketsDwelling = ResourceUtil.getArray(rb,PROPERTIES_MICRO_DATA_DWELLING_USE);
        typeBracketsDwelling = ResourceUtil.getArray(rb,PROPERTIES_MICRO_DATA_TYPE);

        //Attributes list
        attributes = ResourceUtil.getArray(rb, PROPERTIES_HOUSEHOLD_ATTRIBUTES);
        variables = SiloUtil.readCSVfile(rb.getString(PROPERTIES_VARIABLES));
        variables.buildStringIndex(variables.getColumnPosition("VariableNameMicroData"));
    }


    private void setAttributesToCopyFromMicroData() {
        //method to set the attributes to read

        attributesMicroData = new HashMap<>();
        for (int i = 1; i <= variables.getRowCount(); i++){
            String key = variables.getStringValueAt(i,"Type");
            String value = variables.getStringValueAt(i,"VariableNameMicroData");
            if (attributesMicroData.containsKey(key)){
                String[] previous = attributesMicroData.get(key);
                previous = SiloUtil.expandArrayByOneElement(previous, value);
                attributesMicroData.put(key,previous);
            } else {
                String[] previous = new String[1];
                previous[0] = value;
                attributesMicroData.put(key,previous);
            }
        }
    }


    private void readCSVMicroData() {

        TableDataSet microData = SiloUtil.readCSVfile(rb.getString(PROPERTIES_MICRODATA_JP));
        int hhCount = 0;
        int personCount = 0;
        int previoushhID = 0;
        for (int i = 1; i <=microData.getRowCount(); i++){
            if ((int) microData.getValueAt(i,"H_Code") != previoushhID){
                hhCount++;
                previoushhID = (int) microData.getValueAt(i,"H_Code");
            }
        }
        initializeMicroData(hhCount, microData.getRowCount());
        String ppFileName = ("input/testing/microPersons.csv");
        SiloUtil.writeTableDataSet(microPersons, ppFileName);
        String hhFileName = ("input/testing/microHouseholds.csv");
        SiloUtil.writeTableDataSet(microHouseholds, hhFileName);
        String ddFileName = ("input/testing/microDwellings.csv");
        SiloUtil.writeTableDataSet(microDwellings, ddFileName);
        SiloUtil.writeTableDataSet(microData,"input/testing/microDATA.csv");
        hhCount = 0;
        personCount = 0;
        for (int i = 1; i <=microData.getRowCount();i++){
            int householdNumber = (int) microData.getValueAt(i,"H_Code");
            if ( householdNumber != previoushhID){
                hhCount++;
                personCount++;
                microHouseholds.setValueAt(hhCount,"id",hhCount);
                microHouseholds.setValueAt(hhCount,"H_Code",householdNumber);
                microHouseholds.setValueAt(hhCount,"firstPerson",personCount);
                for (int j = 0; j < attributesMicroData.get("Household").length; j++){
                    int value = (int) microData.getValueAt(i,attributesMicroData.get("Household")[j]);
                    microHouseholds.setValueAt(hhCount,attributesMicroData.get("Household")[j],value);
                }
                microDwellings.setValueAt(hhCount,"id",hhCount);
                for (int j = 0; j < attributesMicroData.get("Dwelling").length; j++){
                    int value = (int) microData.getValueAt(i,attributesMicroData.get("Dwelling")[j]);
                    microDwellings.setValueAt(hhCount,attributesMicroData.get("Dwelling")[j],value);
                }
                microPersons.setValueAt(personCount,"id",personCount);
                microPersons.setValueAt(personCount,"idHH",hhCount);
                microPersons.setValueAt(personCount,"H_Code",householdNumber);
                for (int j = 0; j < attributesMicroData.get("Person").length; j++){
                    int value = (int) microData.getValueAt(personCount,attributesMicroData.get("Person")[j]);
                    microPersons.setValueAt(personCount,attributesMicroData.get("Person")[j],value);
                }
                previoushhID = householdNumber;
            } else {
                personCount++;
                microPersons.setValueAt(personCount,"id",personCount);
                microPersons.setValueAt(personCount,"idHH",hhCount);
                microPersons.setValueAt(personCount,"H_Code",householdNumber);
                for (int j = 0; j < attributesMicroData.get("Person").length; j++){
                    int value = (int) microData.getValueAt(personCount,attributesMicroData.get("Person")[j]);
                    microPersons.setValueAt(personCount,attributesMicroData.get("Person")[j],value);
                }
            }
        }
        ppFileName = ("input/testing/microPersons.csv");
        SiloUtil.writeTableDataSet(microPersons, ppFileName);
        hhFileName = ("input/testing/microHouseholds.csv");
        SiloUtil.writeTableDataSet(microHouseholds, hhFileName);
        ddFileName = ("input/testing/microDwellings.csv");
        SiloUtil.writeTableDataSet(microDwellings, ddFileName);
    }


    private void createFrequencyMatrix(){
        //create the frequency matrix with all the attributes aggregated at the household level
        logger.info("   Starting to create the frequency matrix");

        //Read the attributes to match and initialize frequency matrix
        initializeAttributesMunicipality();
        attributesMunicipality = frequencyMatrix.getColumnLabels();
        SiloUtil.writeTableDataSet(frequencyMatrix,"input/testing/frequencyMatrix.csv");

        //Update the frequency matrix with the microdata
        for (int i = 1; i <= frequencyMatrix.getRowCount(); i++){
            //checkContainsAndUpdate(attributesMunicipality[i],);
            frequencyMatrix.setValueAt(i,"hhTotal",1);
            int hhSize = (int) microHouseholds.getValueAt(i,"hhSize");
            updateHhSize(hhSize, i);
            //updateDdUse((int) microDwellings.getValueAt(i,"ddUse"), i);
            for (int j = 0; j < hhSize; j++){
                int row = (int) microHouseholds.getValueAt(i,"firstPerson") + j;
                int age = (int) microPersons.getValueAt(row,"age");
                int gender = (int) microPersons.getValueAt(row,"gender");
                int occupation = (int) microPersons.getValueAt(row,"occupation");
                int nationality = (int) microPersons.getValueAt(row,"nationality");
                updateHhAgeGender(age, gender, i);
                updateHhWorkers(gender,occupation, i);
                updateHhForeigners(nationality, i);
            }
            frequencyMatrix.setValueAt(i,"population",hhSize);
        }
        SiloUtil.writeTableDataSet(frequencyMatrix,"input/testing/frequencyMatrix.csv");

        logger.info("   Finished creating the frequency matrix");
    }


    private void updateHhForeigners(int nationality, int i) {
        if (nationality > 2){
            int value = 1 + (int) frequencyMatrix.getValueAt(i,"Nat1");
            frequencyMatrix.setValueAt(i,"Nat1",value);
        }
    }


    private void updateHhWorkers(int gender, int occupation, int i) {
        if (occupation == 1){
            int value = 1 + (int) frequencyMatrix.getValueAt(i,"W_" + gender);
            frequencyMatrix.setValueAt(i,"W_" + gender,value);
        }
    }


    private void updateHhAgeGender(int age, int gender, int i) {
        int row = 0;
        while (age > ageBracketsPerson[row]) {
            row++;
        }
        if (gender == 1){
            int value = 1 + (int) frequencyMatrix.getValueAt(i,"M_" + ageBracketsPerson[row]);
            frequencyMatrix.setValueAt(i,"M_" + ageBracketsPerson[row],value);
        } else {
            int value = 1 + (int) frequencyMatrix.getValueAt(i,"F_" + ageBracketsPerson[row]);
            frequencyMatrix.setValueAt(i,"F_" + ageBracketsPerson[row],value);
        }
    }




   private void updateDdUse(int ddUse, int i) {
        //Method to update the dwelling use
        //todo. create a dictionary for dduse + read dictionaty + code vlookup on the dictionary + assign to freq matrix

/*        if (ddUse > usageBracketsDwelling[0]){
            frequencyMatrix.setValueAt(i,"ddUse" + usageBracketsDwelling[1], 1);
        } else {
            frequencyMatrix.setValueAt(i,"ddUse" + usageBracketsDwelling[0] , 1);
        }*/
    }

    private void updateHhSize(int hhSize, int i) {
        //Method to update the frequency matrix depending on hhSize
        if (hhSize > householdSizes[householdSizes.length - 1]){
            hhSize = householdSizes[householdSizes.length - 1];
        }
        frequencyMatrix.setValueAt(i,"nHH_"+ hhSize, 1);
    }


    private void initializeAttributesMunicipality() {
        //Method to create the list of attributes given the generic names and the brackets

        frequencyMatrix = new TableDataSet();
        frequencyMatrix.appendColumn(microHouseholds.getColumnAsInt("id"),"id");

        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < attributes.length; i++){
            map.put(attributes[i],1);
        }
        checkContainsAndAdd("ddUse_",usageBracketsDwelling, map);
        checkContainsAndAdd("ddType_",typeBracketsDwelling, map);
        checkContainsAndAdd("M_",ageBracketsPerson, map);
        checkContainsAndAdd("F_",ageBracketsPerson, map);
        checkContainsAndAdd("M_",occupationBrackets, map);
        checkContainsAndAdd("F_",occupationBrackets, map);
        checkContainsAndAdd("nHH_",householdSizes, map);
        addIntegerColumnToTableDataSet(frequencyMatrix,"population");
        addIntegerColumnToTableDataSet(frequencyMatrix,"hhTotal");
    }

    private void checkContainsAndAdd(String key, String[] brackets, Map<String, Integer> map) {
        if (map.containsKey(key)){
            for (int i = 0; i < brackets.length; i++){
                String label = key + brackets[i];
                addIntegerColumnToTableDataSet(frequencyMatrix,label);
            }
        }
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
        addIntegerColumnToTableDataSet(microHouseholds,"H_Code");
        addIntegerColumnToTableDataSet(microHouseholds,"firstPerson");
        addIntegerColumnToTableDataSet(microPersons,"id", personCountTotal);
        addIntegerColumnToTableDataSet(microPersons,"H_Code");
        addIntegerColumnToTableDataSet(microPersons,"idHH");
        addIntegerColumnToTableDataSet(microDwellings,"id",hhCountTotal);

        for (int i = 0; i < attributesMicroData.get("Person").length; i++){
            addIntegerColumnToTableDataSet(microPersons,attributesMicroData.get("Person")[i]);
        }
        for (int i = 0; i < attributesMicroData.get("Household").length; i++){
            addIntegerColumnToTableDataSet(microHouseholds,attributesMicroData.get("Household")[i]);
        }
        for (int i = 0; i < attributesMicroData.get("Dwelling").length; i++){
            addIntegerColumnToTableDataSet(microDwellings,attributesMicroData.get("Dwelling")[i]);
        }

        microHouseholds.buildIndex(microHouseholds.getColumnPosition("id"));
        microPersons.buildIndex(microPersons.getColumnPosition("id"));
        microDwellings.buildIndex(microDwellings.getColumnPosition("id"));
    }


    private void updateMicroPersons(int personCount, int hhCount, int householdNumber, String recString){
        //method to update the values of the TDS of persons

        microPersons.setValueAt(personCount,"id",personCount);
        microPersons.setValueAt(personCount,"idHh",hhCount);
        microPersons.setValueAt(personCount,"recordHh",householdNumber);
        for (int i = 0; i < attributesMicroData.get("Person").length; i++){
            int start = (int) variables.getStringIndexedValueAt(attributesMicroData.get("Person")[i],"initial");
            int finish = (int) variables.getStringIndexedValueAt(attributesMicroData.get("Person")[i],"end");
            microPersons.setValueAt(personCount,attributesMicroData.get("Person")[i],convertToInteger(recString.substring(start,finish)));
        }
    }


    private void updateMicroHouseholds(int hhCount, int householdNumber, int personCount,  String recString){
        //method to update the values of the TDS of households

        microHouseholds.setValueAt(hhCount,"id",hhCount);
        microHouseholds.setValueAt(hhCount,"recordHh",householdNumber);
        microHouseholds.setValueAt(hhCount,"firstPerson",personCount);
        for (int i = 0; i < attributesMicroData.get("Household").length; i++){
            int start = (int) variables.getStringIndexedValueAt(attributesMicroData.get("Household")[i],"initial");
            int finish = (int) variables.getStringIndexedValueAt(attributesMicroData.get("Household")[i],"end");
            microHouseholds.setValueAt(hhCount,attributesMicroData.get("Household")[i],convertToInteger(recString.substring(start,finish)));
        }
    }


    private void updateMicroDwellings(int hhCount, String recString){
        //method to update the values of the TDS of dwellings

        microDwellings.setValueAt(hhCount,"id",hhCount);
        for (int i = 0; i <  attributesMicroData.get("Dwelling").length; i++){
            int start = (int) variables.getStringIndexedValueAt( attributesMicroData.get("Dwelling")[i],"initial");
            int finish = (int) variables.getStringIndexedValueAt(attributesMicroData.get("Dwelling")[i],"end");
            microDwellings.setValueAt(hhCount,attributesMicroData.get("Dwelling")[i],convertToInteger(recString.substring(start,finish)));
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
