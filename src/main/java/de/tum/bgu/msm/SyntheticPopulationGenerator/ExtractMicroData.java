package de.tum.bgu.msm.SyntheticPopulationGenerator;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.SiloUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ResourceBundle;

import javafx.scene.control.Tab;
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
    protected static final String PROPERTIES_JOB_DESCRIPTION              = "jobs.dictionary";

    protected TableDataSet microHouseholds;
    protected TableDataSet microPersons;
    protected TableDataSet microDwellings;
    protected TableDataSet frequencyMatrix;
    protected TableDataSet exceptions;

    protected int[] ageBracketsPerson;
    protected int[] ageBracketsPersonQuarter;
    protected int[] sizeBracketsDwelling;
    protected int[] yearBracketsDwelling;

    protected TableDataSet weightsTable;
    protected TableDataSet jobsTable;


    static Logger logger = Logger.getLogger(SyntheticPopDe.class);


    public ExtractMicroData(ResourceBundle rb) {
        // Constructor
        this.rb = rb;
    }


    public void run(){
        //method to create the synthetic population
        logger.info("   Starting to create the synthetic population.");
        long startTime = System.nanoTime();
        readMicroData();
        translateMicroData();
        long estimatedTime = System.nanoTime() - startTime;
        logger.info("   Finished creating the synthetic population. Elapsed time: " + estimatedTime);
    }


    private void readMicroData(){
        //method to read the synthetic population initial micro data
        logger.info("   Starting to read the micro data");

        //Scanning the file to obtain the number of households and persons in Bavaria
        String pumsFileName = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_MICRODATA_PATH);
        exceptions = SiloUtil.readCSVfile(rb.getString(PROPERTIES_EXCEPTION_MICRODATA));
        //jobsTable = SiloUtil.readCSVfile(rb.getString(PROPERTIES_JOB_DESCRIPTION));
        String recString = "";
        int recCount = 0;
        int hhCountTotal = 0;
        int personCountTotal = 0;
        int hhOutCountTotal = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(pumsFileName));
            int previousHouseholdNumber = -1;
            while ((recString = in.readLine()) != null) {
                recCount++;
                int recLander = convertToInteger(recString.substring(0,2));
                int householdNumber = 0;
                switch (recLander) {
                    case 9: //Record from Bavaria
                        householdNumber = convertToInteger(recString.substring(2,8)) * 1000 + convertToInteger(recString.substring(8,11));
                        int restriction = checkRestrictions(recString);
                        if (householdNumber != previousHouseholdNumber & restriction == 1) {
                            hhCountTotal++;
                            personCountTotal++;
                            previousHouseholdNumber = householdNumber; // Update the household number

                        } else if (householdNumber == previousHouseholdNumber) {
                            personCountTotal++;
                        }
                    default:
                        hhOutCountTotal++;
                        break;
                }
            }
            logger.info("  Read " + (personCountTotal) + " person records in " +
                    (hhCountTotal) + " private households from file: " + pumsFileName);
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + pumsFileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }


        microHouseholds = new TableDataSet();
        microPersons = new TableDataSet();
        microDwellings = new TableDataSet();
        addIntegerColumnToTableDataSet(microHouseholds,"id", hhCountTotal);
        addIntegerColumnToTableDataSet(microHouseholds,"recordHh");
        addIntegerColumnToTableDataSet(microPersons,"id", personCountTotal);
        addIntegerColumnToTableDataSet(microPersons,"idHh");
        addIntegerColumnToTableDataSet(microPersons,"recordHh");
        addIntegerColumnToTableDataSet(microPersons,"recordPp");
        addIntegerColumnToTableDataSet(microDwellings,"id",hhCountTotal);


        //Obtain person and household variables and add one column to the microData
        TableDataSet ppVariables = SiloUtil.readCSVfile(rb.getString(PROPERTIES_VARIABLES_PP));// variables at the person level
        TableDataSet hhVariables = SiloUtil.readCSVfile(rb.getString(PROPERTIES_VARIABLES_HH)); //variables at the household level
        TableDataSet ddVariables = SiloUtil.readCSVfile(rb.getString(PROPERTIES_VARIABLES_DD)); //variables at the household level
        for (int i = 1; i <= ppVariables.getRowCount(); i++){
            addIntegerColumnToTableDataSet(microPersons,ppVariables.getStringValueAt(i,"VariableName"));
        }
        for (int i = 1; i <= hhVariables.getRowCount(); i++){
            addIntegerColumnToTableDataSet(microHouseholds,hhVariables.getStringValueAt(i,"VariableName"));
        }
        for (int i = 1; i <= ddVariables.getRowCount(); i++){
            addIntegerColumnToTableDataSet(microDwellings,ddVariables.getStringValueAt(i,"VariableName"));
        }


        //read the micro data and assign the characteristics
        int hhCount = 0;
        int personCount = 0;
        recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(pumsFileName));
            int previousHouseholdNumber = -1;
            while ((recString = in.readLine()) != null) {
                recCount++;
                int recLander = convertToInteger(recString.substring(0,2));
                int householdNumber = 0;
                switch (recLander) {
                    case 9: //Record from Bavaria
                        householdNumber = convertToInteger(recString.substring(2,8)) * 1000 + convertToInteger(recString.substring(8,11));
                        int restriction = checkRestrictions(recString);
                        if (householdNumber != previousHouseholdNumber & restriction == 1) {
                            hhCount++;
                            microHouseholds.setValueAt(hhCount,"id",hhCount);
                            microHouseholds.setValueAt(hhCount,"recordHh",householdNumber);
                            for (int i = 1; i <= hhVariables.getRowCount(); i++){
                                int start = (int) hhVariables.getValueAt(i,"initial");
                                int finish = (int) hhVariables.getValueAt(i,"end");
                                microHouseholds.setValueAt(hhCount,hhVariables.getStringValueAt(i,"VariableName"),convertToInteger(recString.substring(start,finish)));
                            }
                            for (int i = 1; i <= ddVariables.getRowCount(); i++){
                                int start = (int) ddVariables.getValueAt(i,"initial");
                                int finish = (int) ddVariables.getValueAt(i,"end");
                                microDwellings.setValueAt(hhCount,ddVariables.getStringValueAt(i,"VariableName"),convertToInteger(recString.substring(start,finish)));
                            }
                            personCount++;
                            microPersons.setValueAt(personCount,"id",personCount);
                            microPersons.setValueAt(personCount,"idHh",hhCount);
                            microPersons.setValueAt(personCount,"recordHh",householdNumber);
                            for (int i = 1; i <= ppVariables.getRowCount(); i++){
                                int start = (int) ppVariables.getValueAt(i,"initial");
                                int finish = (int) ppVariables.getValueAt(i,"end");
                                microPersons.setValueAt(personCount,ppVariables.getStringValueAt(i,"VariableName"),convertToInteger(recString.substring(start,finish)));
                            }
                            previousHouseholdNumber = householdNumber; // Update the household number

                        } else if (householdNumber == previousHouseholdNumber) {
                            personCount++;
                            microPersons.setValueAt(personCount,"id",personCount);
                            microPersons.setValueAt(personCount,"idHh",hhCount);
                            microPersons.setValueAt(personCount,"recordHh",householdNumber);
                            for (int i = 1; i <= ppVariables.getRowCount(); i++){
                                int start = (int) ppVariables.getValueAt(i,"initial");
                                int finish = (int) ppVariables.getValueAt(i,"end");
                                microPersons.setValueAt(personCount,ppVariables.getStringValueAt(i,"VariableName"),convertToInteger(recString.substring(start,finish)));
                            }
                        }
                    default:
                        hhOutCountTotal++;
                        break;
                }
            }
            logger.info("  Read " + (personCountTotal) + " person records in " +
                    (hhCountTotal) + " private households from file: " + pumsFileName);
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


    private void translateMicroData(){
        //method to translate the categories from the initial micro data to the categories from the control totals
        logger.info("   Starting to translate the micro data");


        logger.info("   Finished translating the micro data");
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

    private static int translateJobType (int personJob, TableDataSet jobs){
        //translate 100 job descriptions to 4 job types
        //jobs is one TableDataSet that is read from a csv file containing the description, ID and types of jobs.
        int job = 0;
        int finish = 0;
        int row = 1;
        while (finish == 0 & row < jobs.getRowCount()){
            if (personJob == jobs.getValueAt(row,"WZ08Code")) {
                finish =1;
                job = (int) jobs.getValueAt(row,"MarginalsCode");
            }
            else {
                row++;
            }
        }
        return job;
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
