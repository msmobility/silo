package edu.umd.ncsg.SyntheticPopulationGenerator;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.matrix.Matrix;
import com.pb.common.util.ResourceUtil;
import edu.umd.ncsg.SiloUtil;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ResourceBundle;


/**
 * Generates a simple synthetic population for a study area in Germany
 * @author Ana Moreno (TUM)
 * Created on May 12, 2016 in Munich
 */

public class ExtractDataDE {
    private ResourceBundle rb;
    //Options to run de synthetic population
    protected static final String PROPERTIES_RUN_DEPENDENT                = "run.multiple.resolutions";
    protected static final String PROPERTIES_RUN_IPU                      = "run.ipu";
    protected static final String PROPERTIES_RUN_SYNTHETIC_POPULATION     = "run.synth.pop.generator";
    protected static final String PROPERTIES_YEAR_MICRODATA               = "year.micro.data";
    //Routes of the input data
    protected static final String PROPERTIES_MICRODATA_2000_PATH          = "micro.data.2000";
    protected static final String PROPERTIES_MICRODATA_2010_PATH          = "micro.data.2010";
    protected static final String PROPERTIES_MARGINALS_REGIONAL_MATRIX    = "marginals.region.matrix";
    protected static final String PROPERTIES_MARGINALS_HOUSEHOLD_MATRIX   = "marginals.household.matrix";
    protected static final String PROPERTIES_SELECTED_MUNICIPALITIES_LIST = "municipalities.list";
    protected static final String PROPERTIES_RASTER_CELLS                 = "raster.cells.definition";
    protected static final String PROPERTIES_JOB_DESCRIPTION              = "jobs.dictionary";
    //Routes of input data (if IPU is not performed)
    protected static final String PROPERTIES_WEIGHTS_MATRIX               = "weights.matrix";
    //Parameters of the synthetic population
    protected static final String PROPERTIES_REGION_ATTRIBUTES            = "attributes.region";
    protected static final String PROPERTIES_HOUSEHOLD_ATTRIBUTES         = "attributes.household";
    protected static final String PROPERTIES_REGION_ATTR_GENERATION       = "attributes.region.extra";
    protected static final String PROPERTIES_HOUSEHOLD_ATTR_GENERATION    = "attributes.household.extra";
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
    //Read the synthetic population
    protected static final String PROPERTIES_HOUSEHOLD_SYN_POP            = "household.file.asciiDE";
    protected static final String PROPERTIES_PERSON_SYN_POP               = "person.file.asciiDE";
    protected static final String PROPERTIES_DWELLING_SYN_POP             = "dwelling.file.asciiDE";

    //Shihang
    protected static final String PROPERTIES_TELEWORK_VARIABLES_PP           = "telework.pp.variables";
    protected static final String PROPERTIES_TELEWORK_VARIABLES_HH           = "telework.hh.variables";


    protected TableDataSet microDataHousehold;
    protected TableDataSet microDataPerson;
    protected TableDataSet microDataDwelling;
    protected TableDataSet frequencyMatrix;
    protected TableDataSet marginalsRegionMatrix;
    protected TableDataSet marginalsHouseholdMatrix;
    protected TableDataSet cellsMatrix;
    protected TableDataSet municipalitiesMatrix;

    protected String[] cityIDs;
    protected int[] cityID;
    protected int[] countyID;
    protected String[] countyIDs;

    protected String[] attributesRegion;
    protected String[] attributesHousehold;
    protected int[] ageBracketsPerson;
    protected int[] ageBracketsPersonQuarter;
    protected int[] sizeBracketsDwelling;
    protected int[] yearBracketsDwelling;

    protected TableDataSet weightsTable;
    protected TableDataSet jobsTable;

    protected int maxIterations;
    protected double maxError;
    protected double initialError;
    protected double improvementError;
    protected double iterationError;

    Matrix travelTimeMatrix;
    Matrix numberOfTripsMatrix;


    static Logger logger = Logger.getLogger(SyntheticPopDe.class);


    public ExtractDataDE(ResourceBundle rb) {
        // Constructor
        this.rb = rb;
    }


    public void runSP(){
        //method to create the synthetic population
        if (!ResourceUtil.getBooleanProperty(rb, PROPERTIES_RUN_SYNTHETIC_POPULATION, false)) return;
        logger.info("   Starting to create the synthetic population.");
        long startTime = System.nanoTime();
        readDataTelework(); //Read the micro data from 2010, using the variables that Shihang needs
        long estimatedTime = System.nanoTime() - startTime;
        logger.info("   Finished creating the synthetic population. Elapsed time: " + estimatedTime);
    }


    private void readDataTelework(){
        //method to read the synthetic population initial data
        logger.info("   Starting to read the micro data");

        //Scanning the file to obtain the number of households and persons in Bavaria
        String pumsFileName = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_MICRODATA_2010_PATH);
        jobsTable = SiloUtil.readCSVfile(rb.getString(PROPERTIES_JOB_DESCRIPTION));
        String recString = "";
        int recCount = 0;
        int hhCountTotal = 0;
        int personCountTotal = 0;
        int hhOutCountTotal = 0;
        int personQuarterCountTotal = 0;
        int quarterCountTotal = 0;
        int movedOut = 0;
        int hhmovedOut = 0;
        int ddIncomplete = 0;
        try {

            BufferedReader in = new BufferedReader(new FileReader(pumsFileName));
            int previousHouseholdNumber = -1;
            while ((recString = in.readLine()) != null) {
                recCount++;
                int householdNumber = 0;
                householdNumber = convertToInteger(recString.substring(2,8)) * 1000 + convertToInteger(recString.substring(8,11));
                if (householdNumber != previousHouseholdNumber) {
                        hhCountTotal++;
                        personCountTotal++;
                        previousHouseholdNumber = householdNumber; // Update the household number

                } else if (householdNumber == previousHouseholdNumber) {
                    personCountTotal++;
                }
            }
            logger.info("  Read " + (personCountTotal) + " person records in " +
                    (hhCountTotal) + " private households from file: " + pumsFileName);
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + pumsFileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }


        TableDataSet microHouseholds = new TableDataSet();
        TableDataSet microPersons = new TableDataSet();
        int[] dummy = SiloUtil.createArrayWithValue(hhCountTotal,0);
        int[] dummy1 = SiloUtil.createArrayWithValue(personCountTotal,0);
        int[] dummy4 = SiloUtil.createArrayWithValue(personCountTotal,0);
        int[] dummy5 = SiloUtil.createArrayWithValue(personCountTotal,0);
        microHouseholds.appendColumn(dummy,"IDhh");
        microPersons.appendColumn(dummy1,"IDpp");
        microPersons.appendColumn(dummy4,"IDhh");
        microPersons.appendColumn(dummy5,"WZ08");


        //Obtain person and household variables and add one column to the microData
        TableDataSet ppVariables = SiloUtil.readCSVfile(rb.getString(PROPERTIES_TELEWORK_VARIABLES_PP));// variables at the person level
        TableDataSet hhVariables = SiloUtil.readCSVfile(rb.getString(PROPERTIES_TELEWORK_VARIABLES_HH)); //variables at the household level
        for (int i = 1; i <= ppVariables.getRowCount(); i++){
            int[] dummy2 = SiloUtil.createArrayWithValue(personCountTotal,0);
            microPersons.appendColumn(dummy2,ppVariables.getStringValueAt(i,"EF"));
        }
        for (int i = 1; i <= hhVariables.getRowCount(); i++){
            int[] dummy2 = SiloUtil.createArrayWithValue(hhCountTotal,0);
            microHouseholds.appendColumn(dummy2,hhVariables.getStringValueAt(i,"EF"));
        }
        for (int i = 1; i <= hhVariables.getRowCount(); i++){
            int[] dummy3 = SiloUtil.createArrayWithValue(personCountTotal,0);
            microPersons.appendColumn(dummy3,hhVariables.getStringValueAt(i,"EF"));
        }


        //read the micro data and assign the characteristics
        int hhCount = 0;
        int personCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(pumsFileName));
            int previousHouseholdNumber = -1;
            while ((recString = in.readLine()) != null) {
                recCount++;
                int householdNumber = 0;
                householdNumber = convertToInteger(recString.substring(2,8)) * 1000 + convertToInteger(recString.substring(8,11));
                if (householdNumber != previousHouseholdNumber) {
                    hhCount++;
                    microHouseholds.setValueAt(hhCount,"IDhh",hhCount);
                    for (int i = 1; i <= hhVariables.getRowCount(); i++){
                        int start = (int) hhVariables.getValueAt(i,"initial");
                        int finish = (int) hhVariables.getValueAt(i,"end");
                        microHouseholds.setValueAt(hhCount,hhVariables.getStringValueAt(i,"EF"),convertToInteger(recString.substring(start,finish)));
                    }
/*                    if (microHouseholds.getValueAt(hhCount,"663") > 6){
                        microHouseholds.setValueAt(hhCount,"663",6);
                    }
                    if (microHouseholds.getValueAt(hhCount,"707") > 0 & microHouseholds.getValueAt(hhCount,"707") < 36) {
                        microHouseholds.setValueAt(hhCount, "707", Math.round((microHouseholds.getValueAt(hhCount, "707") + 1) / 3));
                    }*/
                    personCount++;
                    microPersons.setValueAt(personCount,"IDpp",personCount);
                    microPersons.setValueAt(personCount,"IDhh",hhCount);
                    for (int i = 1; i <= ppVariables.getRowCount(); i++){
                        int start = (int) ppVariables.getValueAt(i,"initial");
                        int finish = (int) ppVariables.getValueAt(i,"end");
                        microPersons.setValueAt(personCount,ppVariables.getStringValueAt(i,"EF"),convertToInteger(recString.substring(start,finish)));
                    }
                    for (int i = 1; i <= hhVariables.getRowCount(); i++){
                        int start = (int) hhVariables.getValueAt(i,"initial");
                        int finish = (int) hhVariables.getValueAt(i,"end");
                        microPersons.setValueAt(personCount,hhVariables.getStringValueAt(i,"EF"),convertToInteger(recString.substring(start,finish)));
                    }
/*                    microPersons.setValueAt(personCount,"44",Math.round(microPersons.getValueAt(personCount,"44")/10)*10);
                    microPersons.setValueAt(personCount,"707",microHouseholds.getValueAt(hhCount,"707"));
                    if (microPersons.getValueAt(personCount,"436") > 0 & microPersons.getValueAt(personCount,"436") < 36) {
                        microPersons.setValueAt(personCount, "436", Math.round((microPersons.getValueAt(personCount, "436") + 1) / 3));
                    }*/
                    int education = (int) microPersons.getValueAt(personCount,"312");
                    if (education == 10) {
                        education = 6;
                    } else if (education == 9) {
                        education = 5;
                    } else if (education == 99) {
                        education = 99;
                    } else if (education == 6) {
                        education = 2;
                    } else if (education < 0) {
                        education = 0;
                    } else if (education > 6) {
                        education = 4;
                    } else {
                        education = 1;
                    }
                    microPersons.setValueAt(personCount,"312",education);
                    if (microPersons.getValueAt(personCount,"29") == 1) { // Only employed persons respond to the sector
                        microPersons.setValueAt(personCount,"WZ08", translateJobType(Math.round((int) microPersons.getValueAt(personCount,"137")/10), jobsTable)); //First two digits of the WZ08 job classification in Germany. They are converted to 10 job classes (Zensus 2011 - Erwerbstätige nach Wirtschaftszweig Wirtschafts(unter)bereiche)
                    } else {
                        microPersons.setValueAt(personCount,"WZ08",0);
                    }
                    previousHouseholdNumber = householdNumber; // Update the household number

                } else if (householdNumber == previousHouseholdNumber) {
                    personCount++;
                    microPersons.setValueAt(personCount,"IDpp",personCount);
                    microPersons.setValueAt(personCount,"IDhh",hhCount);
                    for (int i = 1; i <= ppVariables.getRowCount(); i++){
                        int start = (int) ppVariables.getValueAt(i,"initial");
                        int finish = (int) ppVariables.getValueAt(i,"end");
                        microPersons.setValueAt(personCount,ppVariables.getStringValueAt(i,"EF"),convertToInteger(recString.substring(start,finish)));
                    }
                    for (int i = 1; i <= hhVariables.getRowCount(); i++){
                        int start = (int) hhVariables.getValueAt(i,"initial");
                        int finish = (int) hhVariables.getValueAt(i,"end");
                        microPersons.setValueAt(personCount,hhVariables.getStringValueAt(i,"EF"),convertToInteger(recString.substring(start,finish)));
                    }
/*                    microPersons.setValueAt(personCount,"44",Math.round(microPersons.getValueAt(personCount,"44")/10)*10);
                    microPersons.setValueAt(personCount,"707",microHouseholds.getValueAt(hhCount,"707"));
                    if (microPersons.getValueAt(personCount,"436") > 0 & microPersons.getValueAt(personCount,"436") < 36) {
                        microPersons.setValueAt(personCount, "436", Math.round((microPersons.getValueAt(personCount, "436") + 1) / 3));
                    }*/
                    int education = (int) microPersons.getValueAt(personCount,"312");
                    if (education == 10) {
                        education = 6;
                    } else if (education == 9) {
                        education = 5;
                    } else if (education == 99) {
                        education = 99;
                    } else if (education == 6) {
                        education = 2;
                    } else if (education < 0) {
                        education = 0;
                    } else if (education > 6) {
                        education = 4;
                    } else {
                        education = 1;
                    }
                    microPersons.setValueAt(personCount,"312",education);
                    if (microPersons.getValueAt(personCount,"29") == 1) { // Only employed persons respond to the sector
                        microPersons.setValueAt(personCount,"WZ08", translateJobType(Math.round((int) microPersons.getValueAt(personCount,"137")/10), jobsTable)); //First two digits of the WZ08 job classification in Germany. They are converted to 10 job classes (Zensus 2011 - Erwerbstätige nach Wirtschaftszweig Wirtschafts(unter)bereiche)
                    } else {
                        microPersons.setValueAt(personCount,"WZ08",0);
                    }
                }
            }
            logger.info("  Read " + (personCountTotal) + " person records in " +
                    (hhCountTotal) + " private households from file: " + pumsFileName);
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + pumsFileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }

        String hhFileName = ("input/telework/microPersonsAna.csv");
        SiloUtil.writeTableDataSet(microPersons, hhFileName);
        String freqFileName1 = ("input/telework/microHouseholdsAna.csv");
        SiloUtil.writeTableDataSet(microHouseholds, freqFileName1);

        logger.info("   Finished reading the micro data");
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

}
