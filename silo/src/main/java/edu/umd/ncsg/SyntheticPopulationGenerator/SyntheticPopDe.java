package edu.umd.ncsg.SyntheticPopulationGenerator;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.util.ResourceUtil;
import com.sun.xml.internal.bind.v2.TODO;
import edu.umd.ncsg.SiloMuc;
import edu.umd.ncsg.SiloUtil;
import edu.umd.ncsg.data.*;
import javafx.scene.control.Tab;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.*;

/**
 * Generates a simple synthetic population for a study area in Germany
 * @author Ana Moreno (TUM)
 * Created on May 12, 2016 in Munich
 *
 */
public class SyntheticPopDe {
    private ResourceBundle rb;
    protected static final String PROPERTIES_MICRO_DATA_ALL               = "micro.data.bayern";
    protected static final String PROPERTIES_MICRO_DATA_AGES              = "age.brackets";
    protected static final String PROPERTIES_REGION_ATTRIBUTES_ON         = "activate.region.attributes";
    protected static final String PROPERTIES_RUN_DEPENDENT                = "run.multiple.resolutions";
    protected static final String PROPERTIES_RUN_IPU                      = "run.ipu";

    protected static final String PROPERTIES_RUN_SYNTHETIC_POPULATION     = "run.synth.pop.generator";

    protected static final String PROPERTIES_MARGINALS_REGIONAL_MATRIX    = "marginals.region.matrix";
    protected static final String PROPERTIES_MARGINALS_HOUSEHOLD_MATRIX   = "marginals.household.matrix";
    protected static final String PROPERTIES_MAX_ITERATIONS               = "max.iterations.ipu";
    protected static final String PROPERTIES_MAX_ERROR                    = "max.error.ipu";
    protected static final String PROPERTIES_INITIAL_ERROR                = "ini.error.ipu";
    protected static final String PROPERTIES_IMPROVEMENT_ERROR            = "min.improvement.error.ipu";
    protected static final String PROPERTIES_IMPROVEMENT_ITERATIONS       = "iterations.improvement.ipu";
    protected static final String PROPERTIES_INCREASE_ERROR               = "increase.error.ipu";
    protected static final String PROPERTIES_REGION_ATTRIBUTES            = "attributes.region";
    protected static final String PROPERTIES_HOUSEHOLD_ATTRIBUTES         = "attributes.household";
    protected static final String PROPERTIES_WEIGHTS_MATRIX               = "weights.matrix";
    protected static final String PROPERTIES_RASTER_CELLS                 = "raster.cells.definition";

    protected static final String PROPERTIES_MICRO_DATA_DWELLINGS         = "micro.data.dwellings";
    protected static final String PROPERTIES_JOB_DESCRIPTION              = "jobs.dictionary";

    protected TableDataSet microDataHousehold;
    protected TableDataSet microDataPerson;
    protected TableDataSet frequencyMatrix;
    protected TableDataSet marginalsRegionMatrix;
    protected TableDataSet marginalsHouseholdMatrix;
    protected TableDataSet microDataDwelling;
    protected TableDataSet cellsMatrix;

    String[] cityIDs;

    protected String[] attributesRegion;
    protected String[] attributesHousehold;
    protected int[] ageBracketsPerson;
    //protected String[] listMunicipalities;
    //protected int[] listMunicipality;

    protected TableDataSet weight;
    protected TableDataSet weightsTable;
    protected TableDataSet nonZeroFrequency;
    protected TableDataSet nonZeroNumber;
    protected TableDataSet weightedSumHousehold;
    protected TableDataSet weightedSumRegion;
    protected TableDataSet errorHousehold;
    protected TableDataSet errorRegion;
    protected TableDataSet jobsTable;

    protected int maxIterations;
    protected double maxError;
    protected double initialError;
    protected double improvementError;
    protected double iterationError;
    protected String maxErrorAttribute;
    protected int activateRegion;
    protected float maxErrorIteration;


    static Logger logger = Logger.getLogger(SyntheticPopDe.class);


    public SyntheticPopDe(ResourceBundle rb) {
        // Constructor
        this.rb = rb;
    }


    public void runSP(){
        //method to create the synthetic population
        if (!ResourceUtil.getBooleanProperty(rb, PROPERTIES_RUN_SYNTHETIC_POPULATION, false)) return;
        logger.info("   Starting to create the synthetic population.");
        long startTime = System.nanoTime();
        readDataSynPop(); //Read the micro data and marginals
        if (ResourceUtil.getIntegerProperty(rb,PROPERTIES_RUN_IPU) == 1) {
            if (ResourceUtil.getIntegerProperty(rb, PROPERTIES_RUN_DEPENDENT) == 1) {
                runIPUAreaDependent(); //IPU fitting with two geographical resolutions
            } else {
                runIPUIndependent(); //IPU fitting with one geographical resolution. Each municipality is independent of others
            }
            /*
            //previous code
            prepareFrequencyMatrix();//Obtain the n-dimensional frequency matrix according to the defined types
            performIPU(); //IPU fitting to obtain the expansion factor for each entry of the micro data
            */
            selectHouseholds(); //Monte Carlo selection process to generate the synthetic population
            //readDwellings();
            //selectDwelling();
        } else {
            readIPU();
            selectHouseholds();
        }
        summarizeData.writeOutSyntheticPopulation(rb, SiloUtil.getBaseYear());
        long estimatedTime = System.nanoTime() - startTime;
        logger.info("   Finished creating the synthetic population. Elapsed time: " + estimatedTime);
    }


    private void readDataSynPop(){
        //method to read the synthetic population initial data
        logger.info("   Starting to read the micro data");

        //Scanning the file to obtain the number of households and persons in Bavaria
        String pumsFileName = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_MICRO_DATA_ALL);
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
                String recLander = recString.substring(0,2);
                int householdNumber = 0;
                switch (recLander) {
                    case "09": //Record from Bavaria
                        householdNumber = convertToInteger(recString.substring(7,9));
                        if (convertToInteger(recString.substring(313,314)) == 1) { //we only match private households
                            if (householdNumber != previousHouseholdNumber) {
                                hhCountTotal++;
                                personCountTotal++;
                                previousHouseholdNumber = householdNumber; // Update the household number
                            } else if (householdNumber == previousHouseholdNumber) {
                                personCountTotal++;
                            }
                        }
                    default:
                        hhOutCountTotal++;
                        break;
                }
            }
            logger.info("  Read " + personCountTotal + " person records " + hhCountTotal + " in households in Bavaria from file: " + pumsFileName);
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + pumsFileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }


        //Obtain the household, person and frequency matrix from the records from Bavaria (once the totals are known)
        //Person variables
        int age[] = new int[personCountTotal];
        int gender[] = new int[personCountTotal];
        int occupation[] = new int[personCountTotal];
        int personId[] = new int[personCountTotal];
        int personHH[] = new int[personCountTotal];
        int personIncome [] = new int[personCountTotal];
        int personNationality[] = new int[personCountTotal];
        int personWorkplace[] = new int[personCountTotal];
        int personCommuteTime[] = new int[personCountTotal];
        int personTransportationMode[] = new int[personCountTotal];
        int personJobStatus[] = new int[personCountTotal];
        int personJobSector[] = new int[personCountTotal];
        int personCount = 0;
        int personHHCount = 0;
        int foreignCount = 0;
        int hhCount = -1;
        //Household variables
        ageBracketsPerson = ResourceUtil.getIntegerArray(rb, PROPERTIES_MICRO_DATA_AGES);
        int hhTotal [] = new int[hhCountTotal];
        int hhSingle [] = new int [hhCountTotal];
        int hhMaleWorkers[] = new int[hhCountTotal];
        int hhFemaleWorkers[] = new int[hhCountTotal];
        int hhWorkers[] = new int[hhCountTotal];
        int hhMaleAge[][] = new int[hhCountTotal][ageBracketsPerson.length];
        int hhFemaleAge[][] = new int[hhCountTotal][ageBracketsPerson.length];
        int hhSize[] = new int[hhCountTotal];
        int hhSize1[] = new int[hhCountTotal];
        int hhSize2[] = new int[hhCountTotal];
        int hhSize3[] = new int[hhCountTotal];
        int hhSize4[] = new int[hhCountTotal];
        int hhSize5[] = new int[hhCountTotal];
        int hhSize6[] = new int[hhCountTotal];
        String hhSizeCategory[] = new String[hhCountTotal];
        int hhSizeCount[] = new int[hhCountTotal];
        int hhForeigners[] = new int[hhCountTotal];
        int hhId [] = new int[hhCountTotal];
        int hhIncome[] = new int[hhCountTotal];
        int personCounts[] = new int[hhCountTotal];
        int hhDwellingType[] = new int[hhCountTotal];
        int incomeCounter = 0;
        int householdNumber = 0;
        String personalIncome;
        String sector;


        try {
            BufferedReader in = new BufferedReader(new FileReader(pumsFileName));
            int previousHouseholdNumber = -1;
            while ((recString = in.readLine()) != null) {
                recCount++;
                String recLander = recString.substring(0,2);
                switch (recLander) {
                    case "09": //Record from Bavaria //Record from Bavaria
                        householdNumber = convertToInteger(recString.substring(7, 9));
                        if (convertToInteger(recString.substring(313,314)) == 1) { //we only match private households
                            if (householdNumber != previousHouseholdNumber) {
                                hhCount++;
                                hhSize[hhCount] = convertToInteger(recString.substring(323, 324));
                                hhDwellingType[hhCount] = convertToInteger(recString.substring(476, 477)); // 1: 1-4 apartments, 2: 5-10 apartments, 3: 11 or more, 4: gemainschafts, 6: neubauten
                                hhTotal[hhCount] = 1;
                                if (hhSize[hhCount] == 1) {
                                    hhSingle[hhCount] = 1;
                                    hhSize1[hhCount] = 1;
                                    hhSizeCategory[hhCount] = "hhSize1";
                                } else if (hhSize[hhCount] == 2){
                                    hhSize2[hhCount] = 1;
                                    hhSizeCategory[hhCount] = "hhSize2";
                                }else if (hhSize[hhCount] == 3){
                                    hhSize3[hhCount] = 1;
                                    hhSizeCategory[hhCount] = "hhSize3";
                                }else if (hhSize[hhCount] == 4){
                                    hhSize4[hhCount] = 1;
                                    hhSizeCategory[hhCount] = "hhSize4";
                                }else if (hhSize[hhCount] == 5){
                                    hhSize5[hhCount] = 1;
                                    hhSizeCategory[hhCount] = "hhSize5";
                                }else {
                                    hhSize6[hhCount] = 1;
                                    hhSizeCategory[hhCount] = "hhSize6";
                                }
                                hhIncome[hhCount] = incomeCounter;
                                hhId[hhCount] = convertToInteger(recString.substring(2, 9));
                                previousHouseholdNumber = householdNumber; // Update the household number
                                if (hhCount > 1) {
                                    hhSizeCount[hhCount - 1] = personHHCount;
                                    personCounts[hhCount - 1] = personCounts[hhCount - 1] + hhSize[hhCount];
                                    hhForeigners[hhCount - 1] = foreignCount;
                                } else {
                                    hhSizeCount[hhCount] = hhSize[hhCount];
                                    personCounts[hhCount] = 1;
                                    hhForeigners[hhCount] = 1;
                                }
                                personHHCount = 0;
                                incomeCounter = 0;
                                foreignCount = 0;
                            }
                            age[personCount] = convertToInteger(recString.substring(25, 27));
                            gender[personCount] = convertToInteger(recString.substring(28, 29)); // 1: male; 2: female
                            //logger.info("Gender " + " of person " + hhPersonCounter + " on household " + hhCount + " is: " + gender[personCount] );
                            occupation[personCount] = convertToInteger(recString.substring(74, 75)); // 1: employed, 8: unemployed, empty: NA
                            personId[personCount] = convertToInteger(recString.substring(2, 11));
                            personHH[personCount] = convertToInteger(recString.substring(2, 9));
                            personIncome[personCount] = convertToInteger(recString.substring(297, 299));
                            personalIncome = Integer.toString(personIncome[personCount]);
                            personNationality[personCount] = convertToInteger(recString.substring(45, 46)); // 1: only German, 2: dual German citizenship, 8: foreigner; (Marginals consider dual citizens as Germans)
                            personWorkplace[personCount] = convertToInteger(recString.substring(151, 152)); //1: at the municipality, 2: in Berlin, 3: in other municipality of the Bundeslandes, 9: NA
                            personCommuteTime[personCount] = convertToInteger(recString.substring(157, 157)); //1: less than 10 min, 2: 10-30 min, 3: 30-60 min, 4: more than 60 min, 9: NA
                            personTransportationMode[personCount] = convertToInteger(recString.substring(158, 160)); //1: bus, 2: ubahn, 3: eisenbahn, 4: car (driver), 5: carpooled, 6: motorcycle, 7: bike, 8: walk, 9; other, 99: NA
                            personJobStatus[personCount] = convertToInteger(recString.substring(99, 101)); //1: self employed without employees, 2: self employed with employees, 3: family worker, 4: officials judges, 5: worker, 6: home workers, 7: tech trainee, 8: commercial trainee, 9: soldier, 10: basic compulsory military service, 11: zivildienstleistender
                            personJobSector[personCount] = convertToInteger(recString.substring(101, 103)); //Systematische Übersicht der Klassifizierung der Berufe, Ausgabe 1992.
                            if (personalIncome == "01") {
                                incomeCounter = incomeCounter + 150;
                            } else if (personalIncome == "02"){
                                incomeCounter = incomeCounter + 450;
                            } else if (personalIncome == "03"){
                                incomeCounter = incomeCounter + 800;
                            } else if (personalIncome == "04"){
                                incomeCounter = incomeCounter + 1200;
                            } else if (personalIncome == "05"){
                                incomeCounter = incomeCounter + 1600;
                            } else if (personalIncome == "06"){
                                incomeCounter = incomeCounter + 2000;
                            } else if (personalIncome == "07"){
                                incomeCounter = incomeCounter + 2350;
                            } else if (personalIncome == "08"){
                                incomeCounter = incomeCounter + 2750;
                            } else if (personalIncome == "09"){
                                incomeCounter = incomeCounter + 3250;
                            } else if (personalIncome == "10"){
                                incomeCounter = incomeCounter + 3750;
                            } else if (personalIncome == "11"){
                                incomeCounter = incomeCounter + 4250;
                            } else if (personalIncome == "12"){
                                incomeCounter = incomeCounter + 4750;
                            } else if (personalIncome == "13"){
                                incomeCounter = incomeCounter + 5250;
                            } else if (personalIncome == "14"){
                                incomeCounter = incomeCounter + 5750;
                            } else if (personalIncome == "15"){
                                incomeCounter = incomeCounter + 6250;
                            } else if (personalIncome == "16"){
                                incomeCounter = incomeCounter + 6750;
                            } else if (personalIncome == "17"){
                                incomeCounter = incomeCounter + 7250;
                            } else if (personalIncome == "18"){
                                incomeCounter = incomeCounter + 7750;
                            } else if (personalIncome == "19"){
                                incomeCounter = incomeCounter + 9000;
                            } else if (personalIncome == "20"){
                                incomeCounter = incomeCounter + 11000;
                            } else if (personalIncome == "21"){
                                incomeCounter = incomeCounter + 13750;
                            } else if (personalIncome == "22"){
                                incomeCounter = incomeCounter + 17500;
                            } else if (personalIncome == "23"){
                                incomeCounter = incomeCounter + 27500;
                            } else if (personalIncome == "24"){
                                incomeCounter = incomeCounter + 35000;
                            }
                            int row = 0;
                            while (age[personCount] > ageBracketsPerson[row]) {
                                row++;
                            }
                            if (gender[personCount] == 1) {
                                if (occupation[personCount] == 1) {
                                    hhMaleWorkers[hhCount]++;
                                    hhWorkers[hhCount]++;
                                }
                                hhMaleAge[hhCount][row]++;
                            } else if (gender[personCount] == 2) {
                                if (occupation[personCount] == 1) {
                                    hhFemaleWorkers[hhCount]++;
                                }
                                hhFemaleAge[hhCount][row]++;
                                hhWorkers[hhCount]++;
                            }
                            if (personNationality[personCount] == 8){
                                foreignCount++;
                            }
                            personCount++;
                            personHHCount++;
                        } else {
                            previousHouseholdNumber = householdNumber; // Update the household number
                        }
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + pumsFileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }

        //Copy attributes to the person micro data
        TableDataSet microPersons = new TableDataSet();
        microPersons.appendColumn(personHH,"ID");
        microPersons.appendColumn(age,"age");
        microPersons.appendColumn(gender,"gender");
        microPersons.appendColumn(occupation,"occupation");
        microPersons.appendColumn(personId,"personID");
        microPersons.appendColumn(personIncome,"income");
        microPersons.appendColumn(personNationality,"nationality");
        microPersons.appendColumn(personWorkplace,"workplace");
        microPersons.appendColumn(personCommuteTime,"commuteTime");
        microPersons.appendColumn(personTransportationMode,"commuteMode");
        microPersons.appendColumn(personJobStatus,"jobStatus");
        microPersons.appendColumn(personJobSector,"jobSector");
        microDataPerson = microPersons;
        microDataPerson.buildIndex(microDataPerson.getColumnPosition("personID"));


        //Copy attributes to the household micro data
        TableDataSet microRecords = new TableDataSet();
        microRecords.appendColumn(hhId,"ID");
        microRecords.appendColumn(hhWorkers,"workers");
        microRecords.appendColumn(hhFemaleWorkers,"femaleWorkers");
        microRecords.appendColumn(hhMaleWorkers,"maleWorkers");
        for (int row = 0; row < ageBracketsPerson.length; row++){
            int[] ageMale = SiloUtil.obtainColumnFromArray(hhMaleAge,hhCountTotal,row);
            int[] ageFemale = SiloUtil.obtainColumnFromArray(hhFemaleAge,hhCountTotal,row);
            String nameMale = "male" + ageBracketsPerson[row];
            String nameFemale = "female" + ageBracketsPerson[row];
            microRecords.appendColumn(ageMale,nameMale);
            microRecords.appendColumn(ageFemale,nameFemale);
        }
        microRecords.appendColumn(hhIncome,"hhIncome");
        microRecords.appendColumn(hhSize,"hhSizeDeclared");
        microRecords.appendColumn(hhSizeCount,"hhSize");
        microRecords.appendColumn(personCounts,"personCount");
        microRecords.appendColumn(hhDwellingType,"hhDwellingType");
        microRecords.appendColumn(hhSizeCategory,"hhSizeCategory");
        microDataHousehold = microRecords;
        microDataHousehold.buildIndex(microDataHousehold.getColumnPosition("ID"));


        //Copy attributes to the frequency matrix (IPU)
        TableDataSet microRecords1 = new TableDataSet();
        microRecords1.appendColumn(hhId,"ID");
        //microRecords1.appendColumn(hhWorkers,"workers");
        microRecords1.appendColumn(hhMaleWorkers,"maleWorkers");
        microRecords1.appendColumn(hhFemaleWorkers,"femaleWorkers");

        for (int row = 0; row < ageBracketsPerson.length; row++){
            int[] ageMale = SiloUtil.obtainColumnFromArray(hhMaleAge,hhCountTotal,row);
            int[] ageFemale = SiloUtil.obtainColumnFromArray(hhFemaleAge,hhCountTotal,row);
            String nameMale = "male" + ageBracketsPerson[row];
            String nameFemale = "female" + ageBracketsPerson[row];
            microRecords1.appendColumn(ageMale,nameMale);
            microRecords1.appendColumn(ageFemale,nameFemale);
        }
        microRecords1.appendColumn(hhTotal,"hhTotal");
        microRecords1.appendColumn(hhSingle,"hhSingle");
        microRecords1.appendColumn(hhSize1,"hhSize1");
        microRecords1.appendColumn(hhSize2,"hhSize2");
        microRecords1.appendColumn(hhSize3,"hhSize3");
        microRecords1.appendColumn(hhSize4,"hhSize4");
        microRecords1.appendColumn(hhSize5,"hhSize5");
        microRecords1.appendColumn(hhSize6,"hhSize6");
        microRecords1.appendColumn(hhForeigners,"foreigners");
        microRecords1.appendColumn(hhSize,"population");
        frequencyMatrix = microRecords1;

        /*
        String hhFileName = ("input/syntheticPopulation/DataPrepMicroHouseholds.csv");
        SiloUtil.writeTableDataSet(microDataHousehold, hhFileName);
        String popFileName = ("input/syntheticPopulation/DataPrepMicroPersons.csv");
        SiloUtil.writeTableDataSet(microDataPerson, popFileName);
        */
        String freqFileName = ("input/syntheticPopulation/frequencyMatrix.csv");
        SiloUtil.writeTableDataSet(frequencyMatrix, freqFileName);

        logger.info("   Finished reading the micro data");
    }

    private void runIPUIndependent(){
        //IPU process for independent municipalities (only household attributes)
        logger.info("   Starting to prepare the data for IPU");


        //Read the attributes at the municipality level (household attributes) and the marginals at the municipality level
        attributesHousehold = ResourceUtil.getArray(rb, PROPERTIES_HOUSEHOLD_ATTRIBUTES);
        marginalsHouseholdMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_MARGINALS_HOUSEHOLD_MATRIX));
        int[] microDataIds = frequencyMatrix.getColumnAsInt("ID");
        frequencyMatrix.buildIndex(frequencyMatrix.getColumnPosition("ID"));
        int[] cityID = marginalsHouseholdMatrix.getColumnAsInt("ID_city");
        cityIDs = new String[cityID.length];
        for (int row = 0; row < cityID.length; row++){cityIDs[row] = Integer.toString(cityID[row]);}
        marginalsHouseholdMatrix.buildIndex(marginalsHouseholdMatrix.getColumnPosition("ID_city"));


        //Create the collapsed matrix (common for all municipalities, because it depends on the microData)
        TableDataSet nonZero = new TableDataSet();
        TableDataSet nonZeroSize = new TableDataSet();
        nonZero.appendColumn(microDataIds,"ID");
        int[] dummy0 = {0,0};
        nonZeroSize.appendColumn(dummy0,"ID");
        for(int attribute = 0; attribute < attributesHousehold.length; attribute++) {
            int[] nonZeroVector = new int[microDataIds.length];
            int[] sumNonZero = {0, 0};
            for (int row = 1; row < microDataIds.length + 1; row++) {
                if (frequencyMatrix.getValueAt(row, attributesHousehold[attribute]) != 0) {
                    nonZeroVector[sumNonZero[0]] = row;
                    sumNonZero[0] = sumNonZero[0] + 1;
                }
            }
            nonZero.appendColumn(nonZeroVector, attributesHousehold[attribute]);
            nonZeroSize.appendColumn(sumNonZero, attributesHousehold[attribute]);
        }
        nonZero.buildIndex(nonZero.getColumnPosition("ID"));
        nonZeroSize.buildIndex(nonZeroSize.getColumnPosition("ID"));


        //Create the weights table (for all the municipalities)
        TableDataSet weightsMatrix = new TableDataSet();
        weightsMatrix.appendColumn(microDataIds,"ID");


        //Create the errors table (for all the municipalities, by attribute)
        TableDataSet errorsMatrix = new TableDataSet();
        errorsMatrix.appendColumn(cityIDs,"ID_city");
        for(int attribute = 0; attribute < attributesHousehold.length; attribute++) {
            double[] dummy2 = SiloUtil.createArrayWithValue(cityIDs.length,1.0);
            errorsMatrix.appendColumn(dummy2, attributesHousehold[attribute]);
        }


        //Stopping criteria (common for all municipalities)
        maxIterations= ResourceUtil.getIntegerProperty(rb, PROPERTIES_MAX_ITERATIONS,1000);
        maxError = ResourceUtil.getDoubleProperty(rb, PROPERTIES_MAX_ERROR, 0.0001);
        improvementError = ResourceUtil.getDoubleProperty(rb, PROPERTIES_IMPROVEMENT_ERROR, 0.001);
        iterationError = ResourceUtil.getDoubleProperty(rb,PROPERTIES_IMPROVEMENT_ITERATIONS,2);
        Double increaseError = ResourceUtil.getDoubleProperty(rb,PROPERTIES_INCREASE_ERROR,1.05);


        //For each municipality, we perform IPU
        for(int area = 0; area < cityID.length; area++){

            initialError = ResourceUtil.getDoubleProperty(rb, PROPERTIES_INITIAL_ERROR, 1000);

            //-----------***** Data preparation *****-------------------------------------------------------------------
            //weights: TableDataSet with two columns, the ID of the household from microData and the weights for that municipality
            TableDataSet weights = new TableDataSet();
            weights.appendColumn(microDataIds,"ID");
            double[] dummy = SiloUtil.createArrayWithValue(frequencyMatrix.getRowCount(),1.0);
            weights.appendColumn(dummy, cityIDs[area]); //the column label is the municipality cityID
            weights.buildIndex(weights.getColumnPosition("ID"));

            TableDataSet minWeights = new TableDataSet();
            minWeights.appendColumn(microDataIds,"ID");
            double[] dummy1 = SiloUtil.createArrayWithValue(frequencyMatrix.getRowCount(),1.0);
            minWeights.appendColumn(dummy1,cityIDs[area]);
            minWeights.buildIndex(minWeights.getColumnPosition("ID"));


            //marginalsHousehold: TableDataSet that contains in each column the marginal of a household attribute at the municipality level. Only one "real" row
            TableDataSet marginalsHousehold = new TableDataSet();
            int[] dummyw0 = {cityID[area],0};
            logger.info(cityID[area]);
            marginalsHousehold.appendColumn(dummyw0,"ID_city");
            for(int attribute = 0; attribute < attributesHousehold.length; attribute++){
                float[] dummyw1 = {marginalsHouseholdMatrix.getValueAt(marginalsHouseholdMatrix.getIndexedRowNumber(cityID[area]),attributesHousehold[attribute]),0};
                marginalsHousehold.appendColumn(dummyw1,attributesHousehold[attribute]);
            }
            marginalsHousehold.buildIndex(marginalsHousehold.getColumnPosition("ID_city"));


            //weighted sum and errors: TableDataSet that contains in each column the weighted sum (or error) of a household attribute at the municipality level. Only one row
            TableDataSet weightedSumsHousehold = new TableDataSet();
            TableDataSet errorsHousehold = new TableDataSet();
            int[] dummy00 = {cityID[area],0};
            int[] dummy01 = {cityID[area],0};
            weightedSumsHousehold.appendColumn(dummy00,"ID_city");
            errorsHousehold.appendColumn(dummy01,"ID_city");
            for(int attribute = 0; attribute < attributesHousehold.length; attribute++){
                double[] dummyA2 = {0,0};
                double[] dummyB2 = {0,0};
                weightedSumsHousehold.appendColumn(dummyA2,attributesHousehold[attribute]);
                errorsHousehold.appendColumn(dummyB2,attributesHousehold[attribute]);
            }
            weightedSumsHousehold.buildIndex(weightedSumsHousehold.getColumnPosition("ID_city"));
            errorsHousehold.buildIndex(errorsHousehold.getColumnPosition("ID_city"));


            //Calculate the first set of weighted sums and errors, using initial weights equal to 1
            for(int attribute = 0; attribute < attributesHousehold.length; attribute++) {
                int positions = (int) nonZeroSize.getValueAt(1, attributesHousehold[attribute]);
                float weighted_sum = SiloUtil.getWeightedSum(weights.getColumnAsDouble(cityIDs[area]),
                        frequencyMatrix.getColumnAsFloat(attributesHousehold[attribute]),
                        nonZero.getColumnAsInt(attributesHousehold[attribute]),positions);
                weightedSumsHousehold.setIndexedValueAt(cityID[area],attributesHousehold[attribute],weighted_sum);
                float error = Math.abs((weighted_sum -
                        marginalsHousehold.getIndexedValueAt(cityID[area], attributesHousehold[attribute])) /
                        marginalsHousehold.getIndexedValueAt(cityID[area], attributesHousehold[attribute]));
                errorsHousehold.setIndexedValueAt(cityID[area],attributesHousehold[attribute],error);
            }


            //-----------***** IPU procedure *****-------------------------------------------------------------------
            int iteration = 0;
            int finish = 0;
            float factor = 0;
            int position = 0;
            float previousWeight = 1;
            float weightedSum = 0;
            float error = 0;
            float averageErrorIteration = 0;
            float minError = 10000;

            while(iteration <= maxIterations && finish == 0){

                averageErrorIteration = 0;

                //Calculate weights for each attribute at the municipality level
                for(int attribute = 0; attribute < attributesHousehold.length; attribute++) {
                    //update the weights according to the weighted sum and constraint of this attribute and the weights from the previous attribute
                    weightedSum = SiloUtil.getWeightedSum(weights.getColumnAsDouble(cityIDs[area]),
                            frequencyMatrix.getColumnAsFloat(attributesHousehold[attribute]),
                            nonZero.getColumnAsInt(attributesHousehold[attribute]),
                            (int)nonZeroSize.getValueAt(1,attributesHousehold[attribute]));
                    weightedSumsHousehold.setIndexedValueAt(cityID[area],attributesHousehold[attribute],weightedSum);
                    factor = marginalsHousehold.getIndexedValueAt(cityID[area], attributesHousehold[attribute]) /
                            weightedSumsHousehold.getIndexedValueAt(cityID[area], attributesHousehold[attribute]);
                    for (int row = 0; row < nonZeroSize.getValueAt(1, attributesHousehold[attribute]); row++) {
                        position = (int) nonZero.getIndexedValueAt(microDataIds[row], attributesHousehold[attribute]);
                        previousWeight = weights.getValueAt(position, cityIDs[area]);
                        weights.setValueAt(position, cityIDs[area], factor * previousWeight);
                    }
                }


                //update the weighted sums and errors of all household attributes, considering the weights after all the attributes
                for (int attributes = 0; attributes < attributesHousehold.length; attributes++){
                    weightedSum = SiloUtil.getWeightedSum(weights.getColumnAsDouble(cityIDs[area]),
                            frequencyMatrix.getColumnAsFloat(attributesHousehold[attributes]),
                            nonZero.getColumnAsInt(attributesHousehold[attributes]),
                            (int)nonZeroSize.getValueAt(1,attributesHousehold[attributes]));
                    weightedSumsHousehold.setIndexedValueAt(cityID[area],attributesHousehold[attributes],weightedSum);
                    error = Math.abs(weightedSum -
                            marginalsHousehold.getIndexedValueAt(cityID[area], attributesHousehold[attributes]))/
                            marginalsHousehold.getIndexedValueAt(cityID[area], attributesHousehold[attributes]);
                    errorsHousehold.setIndexedValueAt(cityID[area],attributesHousehold[attributes],error);
                    averageErrorIteration += error;
                }
                averageErrorIteration = averageErrorIteration/(attributesHousehold.length);
                logger.info("   Iteration " + iteration + " completed. Average error: " + averageErrorIteration * 100 + " %.");


                //Stopping criteria:
                if (averageErrorIteration < maxError){
                    finish = 1;
                    iteration = maxIterations + 1;
                    logger.info("   IPU finished for municipality " + cityID[area] + " after " + iteration + " iterations.");
                }
                else if ((iteration/iterationError) % 1 == 0){
                    if (Math.abs((initialError-averageErrorIteration)/initialError) < improvementError) {
                        finish = 1;
                        logger.info("   IPU finished after " + iteration + " iterations because the error does not improve. The minimum average error is: " + minError * 100 + " %.");
                    }
                    else if (averageErrorIteration > minError * increaseError) {
                        finish = 1;
                        logger.info("   IPU finished after " + iteration + " iterations because the error starts increasing. The minimum average error is: " + minError * 100 + " %.");
                    }
                    else {
                        initialError = averageErrorIteration;
                        iteration++;
                    }
                }
                else if (iteration == maxIterations){
                    finish = 1;
                    logger.info("   IPU finished after the total number of iterations. The average error is: " + minError * 100 + " %.");
                }
                else{
                    iteration++;
                }


                //Check if the error is lower than the minimum (at the last iterations fluctuates around the minimum error)
                if (averageErrorIteration < minError){
                    minWeights.setColumnAsFloat(minWeights.getColumnPosition(cityIDs[area]),weights.getColumnAsFloat(cityIDs[area]));
                    minError = averageErrorIteration;
                }
            }

            //Copy the errors per attribute
            for(int attribute = 0; attribute < attributesHousehold.length; attribute++) {
                errorsMatrix.setValueAt(area+1,attributesHousehold[attribute],errorsHousehold.getIndexedValueAt(cityID[area],attributesHousehold[attribute]));
            }

            //Write the weights after finishing IPU for each municipality (saved each time over the previous version)
            weightsMatrix.appendColumn(minWeights.getColumnAsFloat(cityIDs[area]),cityIDs[area]);
            String freqFileName = ("input/syntheticPopulation/weigthsMatrix.csv");
            SiloUtil.writeTableDataSet(weightsMatrix, freqFileName);
            String freqFileName2 = ("input/syntheticPopulation/errorsMatrix.csv");
            SiloUtil.writeTableDataSet(errorsMatrix, freqFileName2);

        }
        //Write the weights final table
        weightsTable = weightsMatrix;
        weightsTable.buildIndex(weightsTable.getColumnPosition("ID"));

        logger.info("   IPU finished");
    }


    private void runIPUAreaDependent(){
        //IPU process for dependent municipalities (household and region attributes)
        //Regions are defined as Landkreise, which is the province with 4 digits
        logger.info("   Starting to prepare the data for IPU");


        //Read the attributes at the region(Landkreis) and municipality level
        //Municipality level
        attributesHousehold = ResourceUtil.getArray(rb, PROPERTIES_HOUSEHOLD_ATTRIBUTES);
        marginalsHouseholdMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_MARGINALS_HOUSEHOLD_MATRIX));
        int[] microDataIds = frequencyMatrix.getColumnAsInt("ID");
        frequencyMatrix.buildIndex(frequencyMatrix.getColumnPosition("ID"));
        int[] municipalitiesID = marginalsHouseholdMatrix.getColumnAsInt("ID_city");
        cityIDs = new String[municipalitiesID.length];
        for (int row = 0; row < municipalitiesID.length; row++){cityIDs[row] = Integer.toString(municipalitiesID[row]);}
        marginalsHouseholdMatrix.buildIndex(marginalsHouseholdMatrix.getColumnPosition("ID_city"));
        //County level
        attributesRegion = ResourceUtil.getArray(rb, PROPERTIES_REGION_ATTRIBUTES);
        marginalsRegionMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_MARGINALS_REGIONAL_MATRIX));
        int[] areaID = marginalsRegionMatrix.getColumnAsInt("ID_county");
        String[] areaIDs = new String[areaID.length];
        for (int row = 0; row < areaID.length; row++){areaIDs[row] = Integer.toString(areaID[row]);}
        marginalsRegionMatrix.buildIndex(marginalsRegionMatrix.getColumnPosition("ID_county"));


        //Create the collapsed matrix (common for all)
        TableDataSet nonZero = new TableDataSet();
        TableDataSet nonZeroSize = new TableDataSet();
        nonZero.appendColumn(microDataIds,"ID");
        int[] dummy0 = {0,0};
        nonZeroSize.appendColumn(dummy0,"ID");
        for (int attribute = 0; attribute < attributesRegion.length; attribute++) {
            int[] nonZeroVector = new int[microDataIds.length];
            int[] sumNonZero = {0, 0};
            for (int row = 1; row < microDataIds.length + 1; row++) {
                if (frequencyMatrix.getValueAt(row, attributesRegion[attribute]) != 0) {
                    nonZeroVector[sumNonZero[0]] = row;
                    sumNonZero[0] = sumNonZero[0] + 1;
                }
            }
            nonZero.appendColumn(nonZeroVector, attributesRegion[attribute]);
            nonZeroSize.appendColumn(sumNonZero, attributesRegion[attribute]);
        }
        for(int attribute = 0; attribute < attributesHousehold.length; attribute++) {
            int[] nonZeroVector = new int[microDataIds.length];
            int[] sumNonZero = {0, 0};
            for (int row = 1; row < microDataIds.length + 1; row++) {
                if (frequencyMatrix.getValueAt(row, attributesHousehold[attribute]) != 0) {
                    nonZeroVector[sumNonZero[0]] = row;
                    sumNonZero[0] = sumNonZero[0] + 1;
                }
            }
            nonZero.appendColumn(nonZeroVector, attributesHousehold[attribute]);
            nonZeroSize.appendColumn(sumNonZero, attributesHousehold[attribute]);
        }
        nonZero.buildIndex(nonZero.getColumnPosition("ID"));
        nonZeroSize.buildIndex(nonZeroSize.getColumnPosition("ID"));


        //Create the weights table (for all the municipalities)
        TableDataSet weightsMatrix = new TableDataSet();
        weightsMatrix.appendColumn(microDataIds,"ID");


        //Stopping criteria (common for all municipalities)
        maxIterations= ResourceUtil.getIntegerProperty(rb, PROPERTIES_MAX_ITERATIONS,1000);
        maxError = ResourceUtil.getDoubleProperty(rb, PROPERTIES_MAX_ERROR, 0.0001);
        initialError = ResourceUtil.getDoubleProperty(rb, PROPERTIES_INITIAL_ERROR, 1000);
        improvementError = ResourceUtil.getDoubleProperty(rb, PROPERTIES_IMPROVEMENT_ERROR, 0.001);
        iterationError = ResourceUtil.getDoubleProperty(rb,PROPERTIES_IMPROVEMENT_ITERATIONS,2);
        Double increaseError = ResourceUtil.getDoubleProperty(rb,PROPERTIES_INCREASE_ERROR,1.05);


        //Count the number of counties and municipalities at each county
        int countyCount = -1;
        int[] countyID = new int[microDataIds.length];
        int previousCounty = 9;
        int[] countyNumbers = new int[microDataIds.length];
        int municipalityCounter = 0;
        int maxMunicipality = 0;
        for (int area = 0; area < municipalitiesID.length; area++){
            int county = (int) marginalsHouseholdMatrix.getIndexedValueAt(municipalitiesID[area],"ID_county");
            //String county = marginalsHouseholdMatrix.getStringIndexedStringValueAt(cityIDs[area],"ID_county");
            if (previousCounty != county){
                countyCount++;
                countyID[countyCount] = county;
                if (countyCount > 0){
                    countyNumbers[countyCount-1] = municipalityCounter;
                } else {
                    countyNumbers[countyCount] = 1;
                }
                previousCounty = county;
                if (municipalityCounter > maxMunicipality){
                    maxMunicipality = municipalityCounter;
                }
                municipalityCounter = 0;
            }
            municipalityCounter++;
        }
        countyNumbers[countyCount] = municipalityCounter; //for the last county
        if (municipalityCounter > maxMunicipality){
            maxMunicipality = municipalityCounter;
        }
        countyCount++;


        //Create table with the number of municipalities per county and the name of each municipality
        TableDataSet counties = new TableDataSet();
        TableDataSet countiesCount = new TableDataSet();
        int[] dummy = SiloUtil.createArrayWithValue(maxMunicipality,1);
        int[] dummy10 = {0,0};
        String[] countyIDs = new String[countyCount];
        for (int row = 0; row < countyCount; row++){countyIDs[row] = Integer.toString(countyID[row]);}
        int municipCounter = 0;
        for (int county = 0; county < countyIDs.length; county++){
            int[] realCounty = new int[maxMunicipality];
            int[] sumCounty = {0,0};
            for (int row = 0; row < countyNumbers[county]; row++){
                realCounty[sumCounty[0]] = (int) marginalsHouseholdMatrix.getIndexedValueAt(municipalitiesID[municipCounter],"ID_city");
                sumCounty[0] = sumCounty[0] + 1;
                municipCounter++;
            }
            counties.appendColumn(realCounty,countyIDs[county]);
            countiesCount.appendColumn(sumCounty,countyIDs[county]);
        }

        /*for (int county = 0; county < countyCount; county++) {
            logger.info("County " + countyIDs[county] + " has " + countiesCount.getValueAt(1,countyIDs[county]) + " municipalities.");
            for (int row = 0; row < countiesCount.getValueAt(1,countyIDs[county]); row++){
                logger.info("Municipality " + counties.getValueAt(row+1,countyIDs[county]) + " is located at county " + countyIDs[county]);
            }
        }*/


        //For each county, we perform IPU (Landkreise)
        for (int county = 0; county < countyCount; county++){


            //-----------***** Data preparation *****-------------------------------------------------------------------
            //municipalities ID
            municipalitiesID = new int[(int) countiesCount.getValueAt(1, countyIDs[county])];
            for (int municipality = 0; municipality < municipalitiesID.length; municipality++){
                municipalitiesID[municipality] = (int) counties.getValueAt(municipality+1,countyIDs[county]);
            }
            String[] municipalitiesIDs = new String[municipalitiesID.length];
            for (int row = 0; row < municipalitiesID.length; row++){municipalitiesIDs[row] = Integer.toString(municipalitiesID[row]);}


            //weights: TableDataSet with (one + number of municipalities) columns, the ID of the household from microData and the weights for the municipalities of the county
            TableDataSet weights = new TableDataSet();
            weights.appendColumn(microDataIds,"ID");
            for (int municipality = 0; municipality < municipalitiesID.length; municipality++){
                double[] dummy20 = SiloUtil.createArrayWithValue(frequencyMatrix.getRowCount(),1.0);
                weights.appendColumn(dummy20, municipalitiesIDs[municipality]); //the column label is the municipality cityID
            }
            weights.buildIndex(weights.getColumnPosition("ID"));
            TableDataSet minWeights = new TableDataSet();
            minWeights.appendColumn(microDataIds,"ID");
            for (int municipality = 0; municipality < municipalitiesID.length; municipality++){
                double[] dummy20 = SiloUtil.createArrayWithValue(frequencyMatrix.getRowCount(),1.0);
                minWeights.appendColumn(dummy20, municipalitiesIDs[municipality]); //the column label is the municipality cityID
            }
            minWeights.buildIndex(weights.getColumnPosition("ID"));


            //marginalsRegion: TableDataSet that contains in each column the marginal of a region attribute at the county level. Only one "real"" row
            TableDataSet marginalsRegion = new TableDataSet();
            int[] dummyw10 = {countyID[county],0};
            marginalsRegion.appendColumn(dummyw10,"ID_county");
            for(int attribute = 0; attribute < attributesRegion.length; attribute++){
                float[] dummyw11 = {marginalsRegionMatrix.getValueAt(
                        marginalsRegionMatrix.getIndexedRowNumber(countyID[county]),attributesRegion[attribute]),0};
                marginalsRegion.appendColumn(dummyw11,attributesRegion[attribute]);
            }
            marginalsRegion.buildIndex(marginalsRegion.getColumnPosition("ID_county"));
            /*for (int attribute = 0; attribute < attributesRegion.length; attribute++){
               logger.info("County "  + countyID[county] + ". The marginal of attribute " + attributesRegion[attribute] + " is " + marginalsRegion.getValueAt(1,attributesRegion[attribute]));
            }*/


            //weighted sum and errors Region: TableDataSet that contains in each column the weighted sum (or error) of a region attribute at the county (landkreise) level
            TableDataSet weightedSumsRegion = new TableDataSet();
            TableDataSet errorsRegion = new TableDataSet();
            int[] dummy00 = {countyID[county],0};
            int[] dummy01 = {countyID[county],0};
            weightedSumsRegion.appendColumn(dummy00,"ID_county");
            errorsRegion.appendColumn(dummy01,"ID_county");
            for (int attribute = 0; attribute < attributesRegion.length; attribute++){
                float[] dummyQ1 = {0,0};
                float[] dummyQ2 = {0,0};
                weightedSumsRegion.appendColumn(dummyQ1,attributesRegion[attribute]);
                errorsRegion.appendColumn(dummyQ2,attributesRegion[attribute]);
            }
            weightedSumsRegion.buildIndex(weightedSumsRegion.getColumnPosition("ID_county"));
            errorsRegion.buildIndex(errorsRegion.getColumnPosition("ID_county"));


            //Calculate the first set of weighted sums and errors, using initial weights equal to 1
            for (int attribute = 0; attribute < attributesRegion.length; attribute++){
                float weighted_sum = 0f;
                for (int municipality = 0; municipality < municipalitiesID.length;municipality++) {
                    int positions = (int) nonZeroSize.getValueAt(1, attributesRegion[attribute]);
                    weighted_sum = SiloUtil.getWeightedSum(weights.getColumnAsDouble(municipalitiesIDs[municipality]),
                            frequencyMatrix.getColumnAsFloat(attributesRegion[attribute]),
                            nonZero.getColumnAsInt(attributesRegion[attribute]), positions);
                }
                weightedSumsRegion.setIndexedValueAt(countyID[county], attributesRegion[attribute], weighted_sum);
                float error = Math.abs((weighted_sum -
                        marginalsRegion.getIndexedValueAt(countyID[county], attributesRegion[attribute])) /
                        marginalsRegion.getIndexedValueAt(countyID[county], attributesRegion[attribute]));
                errorsRegion.setIndexedValueAt(countyID[county], attributesRegion[attribute], error);

            }


            //marginalsHousehold: TableDataSet that contains in each column the marginal of a household attribute at the municipality level. As many rows as municipalities on the county
            TableDataSet marginalsHousehold = new TableDataSet();
            marginalsHousehold.appendColumn(municipalitiesID,"ID_city");
            for (int attribute = 0; attribute < attributesHousehold.length; attribute++){
                float[] dummyw12 = new float[municipalitiesID.length];
                for (int municipality = 0; municipality < municipalitiesID.length; municipality++){
                    dummyw12[municipality] = marginalsHouseholdMatrix.getValueAt(
                            marginalsHouseholdMatrix.getIndexedRowNumber(municipalitiesID[municipality]),attributesHousehold[attribute]);
                }
                marginalsHousehold.appendColumn(dummyw12,attributesHousehold[attribute]);
            }
            marginalsHousehold.buildIndex(marginalsHousehold.getColumnPosition("ID_city"));
            /*for (int attribute = 0; attribute < attributesHousehold.length; attribute++){
                for (int row = 0; row < municipalitiesID.length; row++) {
                    logger.info("Municipality " + municipalitiesID[row] + ". The marginal of attribute " + attributesHousehold[attribute] + " is " + marginalsHousehold.getIndexedValueAt(municipalitiesID[row], attributesHousehold[attribute]));
                }
            }*/


            //weighted sum and errors Household: TableDataSet that contains in each column the weighted sum (or error) of a household attribute at the municipality level. As many rows as municipalities on the county
            TableDataSet weightedSumsHousehold = new TableDataSet();
            TableDataSet errorsHousehold = new TableDataSet();
            weightedSumsHousehold.appendColumn(municipalitiesID,"ID_city");
            errorsHousehold.appendColumn(municipalitiesID,"ID_city");
            for(int attribute = 0; attribute < attributesHousehold.length; attribute++){
                float[] dummyA2 = SiloUtil.createArrayWithValue(marginalsHousehold.getRowCount(),0f);
                float[] dummyB2 = SiloUtil.createArrayWithValue(marginalsHousehold.getRowCount(),0f);
                weightedSumsHousehold.appendColumn(dummyA2,attributesHousehold[attribute]);
                errorsHousehold.appendColumn(dummyB2,attributesHousehold[attribute]);
            }
            weightedSumsHousehold.buildIndex(weightedSumsHousehold.getColumnPosition("ID_city"));
            errorsHousehold.buildIndex(errorsHousehold.getColumnPosition("ID_city"));


            //Calculate the first set of weighted sums and errors, using initial weights equal to 1
            for(int attribute = 0; attribute < attributesHousehold.length; attribute++) {
                for (int municipality = 0; municipality < municipalitiesID.length; municipality++) {
                    int positions = (int) nonZeroSize.getValueAt(1, attributesHousehold[attribute]);
                    float weighted_sum = SiloUtil.getWeightedSum(weights.getColumnAsDouble(municipalitiesIDs[municipality]),
                            frequencyMatrix.getColumnAsFloat(attributesHousehold[attribute]),
                            nonZero.getColumnAsInt(attributesHousehold[attribute]),positions);
                    weightedSumsHousehold.setIndexedValueAt(municipalitiesID[municipality],attributesHousehold[attribute],weighted_sum);
                    float error = Math.abs((weighted_sum -
                            marginalsHousehold.getIndexedValueAt(municipalitiesID[municipality], attributesHousehold[attribute])) /
                            marginalsHousehold.getIndexedValueAt(municipalitiesID[municipality], attributesHousehold[attribute]));
                    errorsHousehold.setIndexedValueAt(municipalitiesID[municipality],attributesHousehold[attribute],error);
                }
            }


            //---------***** IPU procedure *****-----------------------------------------------------------------------
            int iteration = 0;
            int finish = 0;
            float factor = 0f;
            int position = 0;
            float minError = 100000;
            float weightedSum = 0f;
            float error = 0f;

            initialError = ResourceUtil.getDoubleProperty(rb, PROPERTIES_INITIAL_ERROR, 1000);
            while(iteration <= maxIterations && finish == 0) {

                float averageErrorIteration = 0f;
                String maxErrorAttributes = "";


                //For each attribute at the region level (landkreise)
                for (int attribute = 0; attribute < attributesRegion.length; attribute++) {
                    //logger.info("Iteration: " + iteration + ". Starting to calculate attribute " + attributesRegion[attribute] + " at county " + countyIDs[county]);
                    factor = marginalsRegion.getIndexedValueAt(countyID[county], attributesRegion[attribute]) /
                            weightedSumsRegion.getIndexedValueAt(countyID[county], attributesRegion[attribute]);
                    for (int municipality = 0; municipality < municipalitiesID.length; municipality++) {
                        for (int row = 0; row < nonZeroSize.getValueAt(1, attributesRegion[attribute]); row++) {
                            position = (int) nonZero.getIndexedValueAt(microDataIds[row], attributesRegion[attribute]);
                            float previous_weight = weights.getValueAt(position, municipalitiesIDs[municipality]);
                            weights.setValueAt(position, municipalitiesIDs[municipality], factor * previous_weight);
                        }
                    }
                }


                //Update weighted sums and errors for household attributes, given the new weights for region level
                for (int attribute = 0; attribute < attributesHousehold.length; attribute++) {
                    //logger.info("Iteration: " + iteration + ". Updating weighted sums of " + attribute + " at the household level");
                    for (int municipality = 0; municipality < municipalitiesID.length; municipality++) {
                        int positions = (int) nonZeroSize.getValueAt(1, attributesHousehold[attribute]);
                        float weighted_sum = SiloUtil.getWeightedSum(weights.getColumnAsDouble(municipalitiesIDs[municipality]),
                                frequencyMatrix.getColumnAsFloat(attributesHousehold[attribute]),
                                nonZero.getColumnAsInt(attributesHousehold[attribute]), positions);
                        weightedSumsHousehold.setIndexedValueAt(municipalitiesID[municipality], attributesHousehold[attribute], weighted_sum);
                        error = Math.abs((weighted_sum -
                                marginalsHousehold.getIndexedValueAt(municipalitiesID[municipality], attributesHousehold[attribute])) /
                                marginalsHousehold.getIndexedValueAt(municipalitiesID[municipality], attributesHousehold[attribute]));
                        ;
                        errorsHousehold.setIndexedValueAt(municipalitiesID[municipality], attributesHousehold[attribute], error);
                    }
                }


                //For each attribute at the municipality level
                for(int attribute = 0; attribute < attributesHousehold.length; attribute++) {
                    //logger.info("       Iteration: "+ iteration + ". Starting to calculate weight of the attribute " + attribute + " at the household level.");
                    //update the weights according to the weighted sum and constraint of the household attribute
                    for (int municipality = 0; municipality < municipalitiesID.length; municipality++) {
                        factor = marginalsHousehold.getIndexedValueAt(municipalitiesID[municipality], attributesHousehold[attribute]) /
                                weightedSumsHousehold.getIndexedValueAt(municipalitiesID[municipality], attributesHousehold[attribute]);
                        for (int row = 0; row < nonZeroSize.getValueAt(1, attributesHousehold[attribute]); row++) {
                            position = (int) nonZero.getIndexedValueAt(microDataIds[row], attributesHousehold[attribute]);
                            float previous_weight = weights.getValueAt(position, municipalitiesIDs[municipality]);
                            weights.setValueAt(position, municipalitiesIDs[municipality], factor * previous_weight);
                        }
                    }


                    //update the weighted sums and errors of the region attributes, given the new weights
                    for (int attributes = 0; attributes < attributesRegion.length; attributes++) {

                        float weighted_sum = 0f;
                        for (int municipality = 0; municipality < municipalitiesID.length; municipality++) {
                            weighted_sum = weighted_sum + SiloUtil.getWeightedSum(weights.getColumnAsDouble(municipalitiesIDs[municipality]),
                                    frequencyMatrix.getColumnAsFloat(attributesRegion[attributes]),
                                    nonZero.getColumnAsInt(attributesRegion[attributes]),
                                    (int) nonZeroSize.getValueAt(1, attributesRegion[attributes]));
                        }
                        weightedSumsRegion.setIndexedValueAt(countyID[county], attributesRegion[attributes], weighted_sum);
                        error = Math.abs((weighted_sum -
                                marginalsRegion.getValueAt(1, attributesRegion[attributes])) /
                                marginalsRegion.getValueAt(1, attributesRegion[attributes]));
                        errorsRegion.setIndexedValueAt(countyID[county], attributesRegion[attributes], error);
                    }


                    //update the weighted sums and errors of the household attributes, given the new weights
                    for (int attributes = 0; attributes < attributesHousehold.length; attributes++) {
                        //logger.info("   Iteration: "+ iteration + ". Updating weighted sums of " + attributes + " at the household level");
                        for (int municipality = 0; municipality < municipalitiesID.length; municipality++) {
                            float weighted_sum = SiloUtil.getWeightedSum(weights.getColumnAsDouble(municipalitiesIDs[municipality]),
                                    frequencyMatrix.getColumnAsFloat(attributesHousehold[attributes]),
                                    nonZero.getColumnAsInt(attributesHousehold[attributes]),
                                    (int) nonZeroSize.getValueAt(1, attributesHousehold[attributes]));
                            weightedSumsHousehold.setIndexedValueAt(municipalitiesID[municipality], attributesHousehold[attributes], weighted_sum);
                            error = Math.abs((weighted_sum -
                                    marginalsHousehold.getIndexedValueAt(municipalitiesID[municipality], attributesHousehold[attributes])) /
                                    marginalsHousehold.getIndexedValueAt(municipalitiesID[municipality], attributesHousehold[attributes]));
                            errorsHousehold.setIndexedValueAt(municipalitiesID[municipality], attributesHousehold[attributes], error);
                        }
                    }
                }


                //Calculate the average error among all the attributes (area and municipalities level). This will serve as one stopping criteria
                int attributesCounter = 1;
                averageErrorIteration = errorsRegion.getIndexedValueAt(countyID[county], attributesRegion[0]);
                for (int attribute = 1; attribute < attributesRegion.length; attribute++) {
                    averageErrorIteration = averageErrorIteration + errorsRegion.getIndexedValueAt(countyID[county], attributesRegion[attribute]);
                    attributesCounter++;
                }
                for(int attribute = 0; attribute < attributesHousehold.length; attribute++){
                    averageErrorIteration = averageErrorIteration +
                            errorsHousehold.getIndexedValueAt(municipalitiesID[0], attributesHousehold[0]);
                    attributesCounter++;
                    for(int municipality = 0; municipality < municipalitiesID.length; municipality++) {
                        averageErrorIteration = averageErrorIteration +
                                errorsHousehold.getIndexedValueAt(municipalitiesID[municipality], attributesHousehold[attribute]);
                        attributesCounter++;
                    }
                }
                averageErrorIteration = averageErrorIteration / attributesCounter;
                logger.info("   County " + countyIDs[county] + ". Iteration " + iteration + ". Average error: " + averageErrorIteration );

                //String freqFileName = ("input/syntheticPopulation/weigthsMatrix1.csv");
                //SiloUtil.writeTableDataSet(weights, freqFileName);


                //Stopping criteria: exceeds the maximum number of iterations or the maximum error is lower than the threshold
                if (averageErrorIteration < maxError){
                    finish = 1;
                    //initialError = ResourceUtil.getDoubleProperty(rb, PROPERTIES_INITIAL_ERROR, 1000);
                    logger.info("   IPU finished after :" + iteration + " iterations with a minimum average error of: " + minError * 100 + " %.");
                    iteration = maxIterations + 1;
                }
                else if ((iteration/iterationError) % 1 == 0){
                    if (Math.abs((initialError-averageErrorIteration)/initialError) < improvementError) {
                        finish = 1;
                        //initialError = ResourceUtil.getDoubleProperty(rb, PROPERTIES_INITIAL_ERROR, 1000);
                        logger.info("   IPU finished after " + iteration + " iterations because the error does not improve. The minimum average error is: " + minError * 100 + " %.");
                    }
                    else if (averageErrorIteration > minError * increaseError){
                        finish = 1;
                        logger.info("   IPU finished after " + iteration + " iterations because the error starts increasing. The minimum average error is: " + minError * 100 + " %.");
                    }
                    else {
                        initialError = averageErrorIteration;
                        iteration = iteration + 1;
                    }
                }
                else if (iteration == maxIterations) {
                    finish = 1;
                    //initialError = ResourceUtil.getDoubleProperty(rb, PROPERTIES_INITIAL_ERROR, 1000);
                    logger.info("   IPU finished after the total number of iterations. The minimum average error is: " + minError * 100 + " %.");
                }
                else{
                    iteration = iteration + 1;
                }


                //Update the weights with the smallest error (error fluctuates slightly at the last iterations)
                if (averageErrorIteration < minError){
                    for (int municipality = 0; municipality < municipalitiesID.length; municipality++) {
                        minWeights.setColumnAsFloat(weights.getColumnPosition(municipalitiesIDs[municipality]),weights.getColumnAsFloat(municipalitiesIDs[municipality]));
                    }
                    minError = averageErrorIteration;
                }


            } //for the WHILE loop


            //Write the weights after finishing IPU for each municipality (saved each time over the previous version)
            for (int municipality = 0; municipality < municipalitiesID.length; municipality++) {
                weightsMatrix.appendColumn(minWeights.getColumnAsFloat(municipalitiesIDs[municipality]), municipalitiesIDs[municipality]);
                //maxErrorsMatrix.appendColumn(ma);

            }
            String freqFileName = ("input/syntheticPopulation/weigthsMatrix.csv");
            SiloUtil.writeTableDataSet(weightsMatrix, freqFileName);


            logger.info("   IPU finished");
        }


        //Write the weights final table
        weightsTable = weightsMatrix;
        weightsTable.buildIndex(weightsTable.getColumnPosition("ID"));
    }


    private void prepareFrequencyMatrix(){
        //Generate the frequency matrix given the types of household and person and calculate the initial set of weights, weighted sums and errors

        logger.info("   Starting to prepare the data for IPU");
        //Read the attributes at the area level (region attributes) and at the municipality level (household attributes). Read stopping criteria (max iterations and error threshold)
        activateRegion = ResourceUtil.getIntegerProperty(rb, PROPERTIES_REGION_ATTRIBUTES_ON);
        attributesRegion = ResourceUtil.getArray(rb, PROPERTIES_REGION_ATTRIBUTES); //{"Region_1", "Region_2", "Region_3"};
        attributesHousehold = ResourceUtil.getArray(rb, PROPERTIES_HOUSEHOLD_ATTRIBUTES); //{"HH_1","HH_2","Person_1","Person_2","Person_3"};


        //Create the frequency matrix and marginals matrix at the area level (region matrix) and municipality level (household matrix)
            //For each microData record, identify what type of household is and how many persons of each person type has
            //This section will be substituted by the method when the microData from the census is available
            //Read the frequency matrix, marginals at area level and household level, max number of iterations and max error
            //frequencyMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_FREQUENCY_MATRIX));
        if (activateRegion == 0){marginalsRegionMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_MARGINALS_REGIONAL_MATRIX));}
        marginalsHouseholdMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_MARGINALS_HOUSEHOLD_MATRIX));

        //Build indexes for the TableDataSet
        int[] microDataIds = frequencyMatrix.getColumnAsInt("ID");
        frequencyMatrix.buildIndex(frequencyMatrix.getColumnPosition("ID"));
        int[] cityID = marginalsHouseholdMatrix.getColumnAsInt("ID_city");
        cityIDs = new String[cityID.length];
        for (int row = 0; row < cityID.length; row++){cityIDs[row] = Integer.toString(cityID[row]);}
        marginalsHouseholdMatrix.buildIndex(marginalsHouseholdMatrix.getColumnPosition("ID_city"));
        if (activateRegion == 0){
            int[] areaID = marginalsRegionMatrix.getColumnAsInt("ID_area");
            String[] areaIDs = new String[areaID.length];
            for (int row = 0; row < areaID.length; row++){areaIDs[row] = Integer.toString(areaID[row]);}
            marginalsRegionMatrix.buildIndex(marginalsRegionMatrix.getColumnPosition("ID_area"));
        }



        //Create the collapsed matrix to optimize the calculation of the weighted sums
            //nonZero: TableDataSet that contains in each column the positions different than zero for each attribute (column label is the name of the attribute). It is unique across municipalities
            //nonZeroSize: TableDataSet that contains on the first row the number of cells different than zero for each attribute (column label is the name of the attribute). It is unique across municipalities
        TableDataSet nonZero = new TableDataSet();
        TableDataSet nonZeroSize = new TableDataSet();
        nonZero.appendColumn(microDataIds,"ID");
        int[] dummy0 = {0,0};
        nonZeroSize.appendColumn(dummy0,"ID");
        if (activateRegion == 0) {
            for (int attribute = 0; attribute < attributesRegion.length; attribute++) {
                int[] nonZeroVector = new int[microDataIds.length];
                int[] sumNonZero = {0, 0};
                for (int row = 1; row < microDataIds.length + 1; row++) {
                    if (frequencyMatrix.getValueAt(row, attributesRegion[attribute]) != 0) {
                        nonZeroVector[sumNonZero[0]] = row;
                        sumNonZero[0] = sumNonZero[0] + 1;
                    }
                }
                nonZero.appendColumn(nonZeroVector, attributesRegion[attribute]);
                nonZeroSize.appendColumn(sumNonZero, attributesRegion[attribute]);
            }
        }
        for(int attribute = 0; attribute < attributesHousehold.length; attribute++) {
            int[] nonZeroVector = new int[microDataIds.length];
            int[] sumNonZero = {0, 0};
            for (int row = 1; row < microDataIds.length + 1; row++) {
                if (frequencyMatrix.getValueAt(row, attributesHousehold[attribute]) != 0) {
                    nonZeroVector[sumNonZero[0]] = row;
                    sumNonZero[0] = sumNonZero[0] + 1;
                }
            }
            nonZero.appendColumn(nonZeroVector, attributesHousehold[attribute]);
            nonZeroSize.appendColumn(sumNonZero, attributesHousehold[attribute]);
        }
        nonZero.buildIndex(nonZero.getColumnPosition("ID"));
        nonZeroSize.buildIndex(nonZeroSize.getColumnPosition("ID"));


        //create the weights table automatically and fill it with ones
            //weights: TableDataSet that contains in each column the weights for that area (column label is the municipality ID). The number of rows is equal to the number of microData records
        TableDataSet weights = new TableDataSet();
        weights.appendColumn(microDataIds,"ID");
        for(int area = 0; area < cityID.length; area++){
            float[] dummy = SiloUtil.createArrayWithValue(frequencyMatrix.getRowCount(),1f);
            weights.appendColumn(dummy, cityIDs[area]);
        }
        weights.buildIndex(weights.getColumnPosition("ID"));


        //create the weighted sums and errors tables automatically, for area level
            //weightedSumRegion: TableDataSet that contains in each column the weighted sum at the areas level (column label is the region attribute name). The number of rows is equal to the number of rows of the marginals Region
            //errorsRegion: TableDataSet that contains in each column the error between the weighted sum and constrain (marginal) at the areas level (column label is the region attribute name).
        TableDataSet weightedSumsRegion = new TableDataSet();
        TableDataSet errorsRegion = new TableDataSet();
        if (activateRegion == 0) {
            int[] areaID = marginalsRegionMatrix.getColumnAsInt("ID_area");
            String[] areaIDs = new String[areaID.length];
            weightedSumsRegion.appendColumn(areaID, "ID_area");
            errorsRegion.appendColumn(areaID, "ID_area");
            for (int attribute = 0; attribute < attributesRegion.length; attribute++) {
                float[] dummyA1 = SiloUtil.createArrayWithValue(marginalsRegionMatrix.getRowCount(), 0f);
                float[] dummyB1 = SiloUtil.createArrayWithValue(marginalsRegionMatrix.getRowCount(), 0f);
                weightedSumsRegion.appendColumn(dummyA1, attributesRegion[attribute]);
                errorsRegion.appendColumn(dummyB1, attributesRegion[attribute]);
            }
            weightedSumsRegion.buildIndex(weightedSumsRegion.getColumnPosition("ID_area"));
            errorsRegion.buildIndex(errorsRegion.getColumnPosition("ID_area"));

            //Calculate the first set of weighted sums and errors, using initial weights equal to 1
            for (int attribute = 0; attribute < attributesRegion.length; attribute++) {
                float weighted_sum = 0f;
                for (int area = 0; area < marginalsHouseholdMatrix.getRowCount(); area++) {
                    int positions = (int) nonZeroSize.getValueAt(1, attributesRegion[attribute]);
                    weighted_sum = weighted_sum + SiloUtil.getWeightedSum(weights.getColumnAsDouble(cityIDs[area]),
                            frequencyMatrix.getColumnAsFloat(attributesRegion[attribute]),
                            nonZero.getColumnAsInt(attributesRegion[attribute]), positions);
                }
                weightedSumsRegion.setIndexedValueAt(1, attributesRegion[attribute], weighted_sum);
                float error = Math.abs((weightedSumsRegion.getValueAt(1, attributesRegion[attribute]) -
                        marginalsRegionMatrix.getValueAt(1, attributesRegion[attribute])) /
                        marginalsRegionMatrix.getValueAt(1, attributesRegion[attribute]));
                errorsRegion.setIndexedValueAt(1, attributesRegion[attribute], error);
            }
        }


        //create the weighted sums and errors tables automatically, for household level
            //weightedSumHousehold: TableDataSet that contains in each column the weighted sum at each municipality (column label is the household-person attribute name). The number of rows is equal to the number of rows of the marginals Household (# municipalities)
            //errorsHousehold: TableDataSet that contains in each column the error between the weighted sum and constrain (marginal) at each municipality (column label is the household-person attribute name).
        TableDataSet weightedSumsHousehold = new TableDataSet();
        TableDataSet errorsHousehold = new TableDataSet();
        weightedSumsHousehold.appendColumn(cityID,"ID_city");
        errorsHousehold.appendColumn(cityID,"ID_city");
        for(int attribute = 0; attribute < attributesHousehold.length; attribute++){
            float[] dummyA2 = SiloUtil.createArrayWithValue(marginalsHouseholdMatrix.getRowCount(),0f);
            float[] dummyB2 = SiloUtil.createArrayWithValue(marginalsHouseholdMatrix.getRowCount(),0f);
            weightedSumsHousehold.appendColumn(dummyA2,attributesHousehold[attribute]);
            errorsHousehold.appendColumn(dummyB2,attributesHousehold[attribute]);
        }
        weightedSumsHousehold.buildIndex(weightedSumsHousehold.getColumnPosition("ID_city"));
        errorsHousehold.buildIndex(errorsHousehold.getColumnPosition("ID_city"));
        //Calculate the first set of weighted sums and errors, using initial weights equal to 1
        for(int attribute = 0; attribute < attributesHousehold.length; attribute++) {
            for (int area = 0; area < marginalsHouseholdMatrix.getRowCount(); area++) {
                int positions = (int) nonZeroSize.getValueAt(1, attributesHousehold[attribute]);
                float weighted_sum = SiloUtil.getWeightedSum(weights.getColumnAsDouble(cityIDs[area]),
                        frequencyMatrix.getColumnAsFloat(attributesHousehold[attribute]),
                        nonZero.getColumnAsInt(attributesHousehold[attribute]),positions);
                weightedSumsHousehold.setIndexedValueAt(cityID[area],attributesHousehold[attribute],weighted_sum);
                float error = Math.abs((weighted_sum -
                        marginalsHouseholdMatrix.getIndexedValueAt(cityID[area], attributesHousehold[attribute])) /
                        marginalsHouseholdMatrix.getIndexedValueAt(cityID[area], attributesHousehold[attribute]));
                errorsHousehold.setIndexedValueAt(cityID[area],attributesHousehold[attribute],error);
            }
        }


        //Assign the tables that will be used on the next method (performIPU)
            //This may be changed using refractor and only having one name for each table, but I encountered problems if the tables are new
        weight = weights; //contains the weights, for each municipality
        nonZeroFrequency = nonZero; //contains the positions different than zero, for each attribute
        nonZeroNumber = nonZeroSize; //contains the number of positions different than zero, for each attribute
        weightedSumHousehold = weightedSumsHousehold; //contains the weighted sum per household-person attribute, for each municipality
        errorHousehold = errorsHousehold; //contains the error per household-person attribute, for each municipality
        if (activateRegion == 0) {
            weightedSumRegion = weightedSumsRegion;
            errorRegion = errorsRegion;
        }

        logger.info("   Finished preparing the data for IPU");
    }


    private void performIPU(){
        //obtain the expansion factor (weight) of each entry of the micro data, using the marginal sums as constraints
        logger.info("   IPU started");

        //Get the labels for the indexed TableDataSet
        //int[] microDataIds = frequencyMatrix.getColumnAsInt("ID");
        //int[] cityID = marginalsHouseholdMatrix.getColumnAsInt("ID_city");
        //int[] areaID = marginalsRegionMatrix.getColumnAsInt("ID_area");

        maxIterations= ResourceUtil.getIntegerProperty(rb, PROPERTIES_MAX_ITERATIONS,1000);
        maxError = ResourceUtil.getDoubleProperty(rb, PROPERTIES_MAX_ERROR, 0.0001);
        initialError = ResourceUtil.getDoubleProperty(rb, PROPERTIES_INITIAL_ERROR, 1000);
        improvementError = ResourceUtil.getDoubleProperty(rb, PROPERTIES_IMPROVEMENT_ERROR, 0.001);
        iterationError = ResourceUtil.getDoubleProperty(rb,PROPERTIES_IMPROVEMENT_ITERATIONS,2);


        int[] microDataIds = frequencyMatrix.getColumnAsInt("ID");
        int[] cityID = marginalsHouseholdMatrix.getColumnAsInt("ID_city");
        cityIDs = new String[cityID.length];
        for (int row = 0; row < cityID.length; row++){cityIDs[row] = Integer.toString(cityID[row]);}
        weight.buildIndex(weight.getColumnPosition("ID"));


        //Iterative loop. For each iteration, the IPU is performed for all attributes at the area level (region attributes) and at the municipalities level (household attributes)
        int iteration = 0;
        int finish = 0;
        while(iteration <= maxIterations && finish == 0){

            if (activateRegion == 0) {
                //Area level (region attributes)
                //update the weights, weighted sum and error for region attributes (they do not depend on each other because they are exclusive[if region 1 = 1, the other regions are = 0])
                for (int attribute = 0; attribute < attributesRegion.length; attribute++) {
                    logger.info("Iteration: " + iteration + ". Starting to calculate attribute " + attribute + " on the region");
                    float factor = marginalsRegionMatrix.getIndexedValueAt(1, attributesRegion[attribute]) /
                            weightedSumRegion.getIndexedValueAt(1, attributesRegion[attribute]);
                    float weighted_sum = 0f;
                    for (int area = 0; area < marginalsHouseholdMatrix.getRowCount(); area++) {
                        for (int row = 0; row < nonZeroNumber.getValueAt(1, attributesRegion[attribute]); row++) {
                            int position = (int) nonZeroFrequency.getIndexedValueAt(microDataIds[row], attributesRegion[attribute]);
                            //logger.info(position + " area " + cityIDs[area]);
                            //logger.info(weight.getValueAt(position,cityIDs[area]));
                            float previous_weight = weight.getValueAt(position, cityIDs[area]);
                            weight.setValueAt(position, cityIDs[area], factor * previous_weight);
                        }
                        weighted_sum = weighted_sum + SiloUtil.getWeightedSum(weight.getColumnAsDouble(cityIDs[area]),
                                frequencyMatrix.getColumnAsFloat(attributesRegion[attribute]),
                                nonZeroFrequency.getColumnAsInt(attributesRegion[attribute]),
                                (int) nonZeroFrequency.getValueAt(1, attributesRegion[attribute]));
                    }
                    logger.info("Iteration: " + iteration + ". Updating weighted sums of " + attribute + " on the region");
                    weightedSumRegion.setIndexedValueAt(1, attributesRegion[attribute], weighted_sum);
                    float error = Math.abs((weightedSumRegion.getValueAt(1, attributesRegion[attribute]) -
                            marginalsRegionMatrix.getValueAt(1, attributesRegion[attribute])) /
                            marginalsRegionMatrix.getValueAt(1, attributesRegion[attribute]));
                    errorRegion.setIndexedValueAt(1, attributesRegion[attribute], error);
                }


                //Update the weighted sums and errors for household attributes, given the new weights for the area level
                for (int attribute = 0; attribute < attributesHousehold.length; attribute++) {
                    logger.info("Iteration: " + iteration + ". Updating weighted sums of " + attribute + " at the household level");
                    for (int area = 0; area < marginalsHouseholdMatrix.getRowCount(); area++) {
                        int positions = (int) nonZeroNumber.getValueAt(1, attributesHousehold[attribute]);
                        float weighted_sum = SiloUtil.getWeightedSum(weight.getColumnAsDouble(cityIDs[area]),
                                frequencyMatrix.getColumnAsFloat(attributesHousehold[attribute]),
                                nonZeroFrequency.getColumnAsInt(attributesHousehold[attribute]), positions);
                        weightedSumHousehold.setIndexedValueAt(cityID[area], attributesHousehold[attribute], weighted_sum);
                        float error = Math.abs((weighted_sum -
                                marginalsHouseholdMatrix.getIndexedValueAt(cityID[area], attributesHousehold[attribute])) /
                                marginalsHouseholdMatrix.getIndexedValueAt(cityID[area], attributesHousehold[attribute]));
                        ;
                        errorHousehold.setIndexedValueAt(cityID[area], attributesHousehold[attribute], error);
                    }
                }
            }


            //Municipalities level (household attributes)
            //For each attribute at the municipality level
            for(int attribute = 0; attribute < attributesHousehold.length; attribute++){
                //logger.info("       Iteration: "+ iteration + ". Starting to calculate weight of the attribute " + attribute + " at the household level.");
                //update the weights according to the weighted sum and constraint of the household attribute
                for(int area = 0; area < marginalsHouseholdMatrix.getRowCount(); area++) {
                    float factor = marginalsHouseholdMatrix.getIndexedValueAt(cityID[area],attributesHousehold[attribute]) /
                            weightedSumHousehold.getIndexedValueAt(cityID[area],attributesHousehold[attribute]);
                    for(int row = 0; row < nonZeroNumber.getValueAt(1, attributesHousehold[attribute]); row++){
                        int position = (int) nonZeroFrequency.getIndexedValueAt(microDataIds[row],attributesHousehold[attribute]);
                        float previous_weight = weight.getValueAt(position,cityIDs[area]);
                        weight.setValueAt(position,cityIDs[area],factor*previous_weight);
                    }
                }
                if (activateRegion == 0) {
                    //update the weighted sums and errors of the region attributes, given the new weights
                    for (int attributes = 0; attributes < attributesRegion.length; attributes++) {

                        float weighted_sum = 0f;
                        for (int area = 0; area < marginalsHouseholdMatrix.getRowCount(); area++) {
                            weighted_sum = weighted_sum + SiloUtil.getWeightedSum(weight.getColumnAsDouble(cityIDs[area]),
                                    frequencyMatrix.getColumnAsFloat(attributesRegion[attributes]),
                                    nonZeroFrequency.getColumnAsInt(attributesRegion[attributes]),
                                    (int) nonZeroNumber.getValueAt(1, attributesRegion[attributes]));
                        }
                        weightedSumRegion.setIndexedValueAt(1, attributesRegion[attributes], weighted_sum);
                        float error = Math.abs((weightedSumRegion.getValueAt(1, attributesRegion[attributes]) -
                                marginalsRegionMatrix.getValueAt(1, attributesRegion[attributes])) /
                                marginalsRegionMatrix.getValueAt(1, attributesRegion[attributes]));
                        errorRegion.setIndexedValueAt(1, attributesRegion[attributes], error);
                    }
                }

                //update the weighted sums and errors of the household attributes, given the new weights
                for (int attributes = 0; attributes < attributesHousehold.length; attributes++){
                    //logger.info("   Iteration: "+ iteration + ". Updating weighted sums of " + attributes + " at the household level");
                    for(int area = 0; area < marginalsHouseholdMatrix.getRowCount(); area++){
                        float weighted_sum = SiloUtil.getWeightedSum(weight.getColumnAsDouble(cityIDs[area]),
                                frequencyMatrix.getColumnAsFloat(attributesHousehold[attributes]),
                                nonZeroFrequency.getColumnAsInt(attributesHousehold[attributes]),
                                (int)nonZeroNumber.getValueAt(1,attributesHousehold[attributes]));
                        weightedSumHousehold.setIndexedValueAt(cityID[area],attributesHousehold[attributes],weighted_sum);
                        float error = Math.abs((weighted_sum -
                                marginalsHouseholdMatrix.getIndexedValueAt(cityID[area], attributesHousehold[attributes]))/
                                marginalsHouseholdMatrix.getIndexedValueAt(cityID[area], attributesHousehold[attributes]));
                        errorHousehold.setIndexedValueAt(cityID[area],attributesHousehold[attributes],error);
                    }
                }
            }


            //Calculate the maximum error among all the attributes (area and municipalities level). This will serve as one stopping criteria
            if (activateRegion == 0) {
                maxErrorIteration = errorRegion.getIndexedValueAt(1, attributesRegion[0]);
                for (int attribute = 1; attribute < attributesRegion.length; attribute++) {
                    if (errorRegion.getIndexedValueAt(1, attributesRegion[attribute]) > maxErrorIteration) {
                        maxErrorIteration = errorRegion.getIndexedValueAt(1, attributesRegion[attribute]);
                        maxErrorAttribute = "region_" + Integer.toString(attribute);
                    }
                }
            }

            for(int attribute = 0; attribute < attributesHousehold.length; attribute++){
                maxErrorIteration = errorHousehold.getIndexedValueAt(cityID[0], attributesHousehold[0]);
                for(int area = 0; area < marginalsHouseholdMatrix.getRowCount(); area++) {
                    if (errorHousehold.getIndexedValueAt(cityID[area], attributesHousehold[attribute]) > maxErrorIteration) {
                        maxErrorIteration = errorHousehold.getIndexedValueAt(cityID[area], attributesHousehold[attribute]);
                        maxErrorAttribute = "municipality_" + Integer.toString(cityID[area])+ "_household_" + Integer.toString(attribute);
                    }
                }
            }

            logger.info("   Iteration " + iteration + " completed. The maximum error is " + maxErrorIteration + " on the attribute: " + maxErrorAttribute);

            String freqFileName = ("input/syntheticPopulation/weigthsMatrix.csv");
            SiloUtil.writeTableDataSet(weight, freqFileName);

            //Stopping criteria: exceeds the maximum number of iterations or the maximum error is lower than the threshold
            if (maxErrorIteration < maxError){
                finish = 1;
                logger.info("   IPU finished after :" + iteration + " iterations with a maximum error of " + maxErrorIteration);
                iteration = maxIterations + 1;
            }
            else if ((iteration/iterationError) % 1 == 0){
                if (Math.abs((initialError-maxErrorIteration)/initialError) < improvementError) {
                    finish = 1;
                    logger.info("   IPU finished after :" + iteration + " iterations because the error does not improve. The maximum error is: " + maxErrorIteration);
                }
                else {
                    initialError = maxErrorIteration;
                    iteration = iteration + 1;
                }
            }
            else if (iteration == maxIterations) {
                finish = 1;
                logger.info("   IPU finished after the total number of iterations. The maximum error is: " + maxErrorIteration);
            }
            else{
                iteration = iteration + 1;
            }
        }


        //Print the results of IPU on the screen (weights, weighted sums and errors)
        /*
        for (int area = 0; area < marginalsHouseholdMatrix.getRowCount(); area++) {
            for (int row = 0; row < microDataIds.length; row++){
                logger.info("Area: " + cityIDs[area] + ", micro data: " + microDataIds[row] + ", weight: " + weight.getIndexedValueAt(microDataIds[row],cityIDs[area]));
            }
        }
        for (int area = 0; area < marginalsHouseholdMatrix.getRowCount(); area++){
            for(int attribute = 0; attribute < attributesRegion.length; attribute++){
                logger.info("Area: " + cityIDs[area] + ", attribute: " + attributesRegion[attribute] + ", weighted sum: " + weightedSumRegion.getIndexedValueAt(1,attributesRegion[attribute]));
            }
            for(int attribute = 0; attribute < attributesHousehold.length; attribute++){
                logger.info("Area: " + cityIDs[area] + ", attribute: " + attributesHousehold[attribute] + ", weighted sum: " + weightedSumHousehold.getIndexedValueAt(cityID[area],attributesHousehold[attribute]));
            }
        }
        for (int area = 0; area < marginalsHouseholdMatrix.getRowCount(); area++){
            for(int attribute = 0; attribute < attributesRegion.length; attribute++){
                logger.info("Area: " + cityIDs[area] + ", attribute: " + attributesRegion[attribute] + ", error: " + errorRegion.getIndexedValueAt(1,attributesRegion[attribute]));
            }
            for(int attribute = 0; attribute < attributesHousehold.length; attribute++){
                logger.info("Area: " + cityIDs[area] + ", attribute: " + attributesHousehold[attribute] + ", error: " + errorHousehold.getIndexedValueAt(cityID[area],attributesHousehold[attribute]));
            }
        }
        */

        String freqFileName = ("input/syntheticPopulation/weigthsMatrix.csv");
        SiloUtil.writeTableDataSet(weight, freqFileName);

        logger.info("   IPU finished");
    }


    private void readIPU(){
        //Read entry data for household selection
        logger.info("   Reading the weights matrix");
        weightsTable = SiloUtil.readCSVfile(rb.getString(PROPERTIES_WEIGHTS_MATRIX));
        weightsTable.buildIndex(weightsTable.getColumnPosition("ID"));

        //Read the attributes at the municipality level (household attributes) and the marginals at the municipality level
        attributesHousehold = ResourceUtil.getArray(rb, PROPERTIES_HOUSEHOLD_ATTRIBUTES);
        marginalsHouseholdMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_MARGINALS_HOUSEHOLD_MATRIX));
        marginalsHouseholdMatrix.buildIndex(marginalsHouseholdMatrix.getColumnPosition("ID_city"));
        logger.info("   Finishing reading the results from the IPU");
    }


    private void selectHouseholds(){
        //Generate the synthetic population using Monte Carlo (select the households according to the weight)
        //Once the household is selected, all the characteristics of the household will be copied (including the household members)
        logger.info("   Starting to generate households and persons.");

        int[] listMunicipality = marginalsHouseholdMatrix.getColumnAsInt("ID_city");
        String[] listMunicipalities = new String[marginalsHouseholdMatrix.getRowCount()];
        for (int row = 0; row < listMunicipality.length; row++){listMunicipalities[row] = Integer.toString(listMunicipality[row]);}

        //List of raster cells
        cellsMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_RASTER_CELLS));
        int[] cellsID = cellsMatrix.getColumnAsInt("ID_cell");
        String[] cellsIDs = new String[cellsID.length];
        for (int row = 0; row < cellsID.length; row++){cellsIDs[row] = Integer.toString(cellsID[row]);}
        cellsMatrix.buildIndex(cellsMatrix.getColumnPosition("ID_cell"));


        //Define probabilities for the raster cells inside the municipality
        int municipalityCount = -1;
        int previousMunicipality = 9;
        int[] rasterNumbers = new int[10000];
        int rasterCounter = 0;
        int maxRaster = 0;
        for (int row = 1; row <= cellsID.length; row++){
            //logger.info("Cell " + cellsMatrix.getValueAt(row,"ID_cell") + " at " + cellsMatrix.getValueAt(row, "ID_county"));
            int municipality = (int) cellsMatrix.getValueAt(row,"ID_city");
            if (previousMunicipality != municipality){
                municipalityCount++;
                if (municipalityCount > 0){
                    rasterNumbers[municipalityCount - 1] = rasterCounter;
                } else {
                    rasterNumbers[municipalityCount] = 1;
                }
                previousMunicipality = municipality;
                if (rasterCounter > maxRaster){
                    maxRaster = rasterCounter;
                }
                rasterCounter = 0;
            }
            rasterCounter++;
        }
        if (rasterCounter > maxRaster){maxRaster = rasterCounter;} //If the last municipality is the one with the highest number of cells, maxRaster was equal to the second highest number of cells
        rasterNumbers[municipalityCount] = rasterCounter;
        municipalityCount++;


        //Create table with the number of raster cells per municipality, name of each raster cell and its population (used as weight to allocate households on that raster cell)
        TableDataSet rasterCells = new TableDataSet();
        TableDataSet rasterWeights = new TableDataSet();
        TableDataSet rasterCellsCount = new TableDataSet();
        int[] dummyr1 = SiloUtil.createArrayWithValue(maxRaster,1);
        int[] dummyr2 = {0,0};
        int rasterRow = 1;
        for (int municipality = 0; municipality < listMunicipality.length; municipality++){
            int[] realCells = new int[maxRaster];
            int[] sumCells = {0,0};
            double sumWeights = 0;
            double[] weightCells = new double[maxRaster];
            for (int row = 0; row < rasterNumbers[municipality];row++){
                //logger.info((int) cellsMatrix.getValueAt(rasterRow,"ID_cell"));
                realCells[sumCells[0]] = (int) cellsMatrix.getValueAt(rasterRow,"ID_cell");
                weightCells[sumCells[0]] = cellsMatrix.getValueAt(rasterRow,"Population");
                sumWeights = sumWeights + weightCells[sumCells[0]];
                sumCells[0] = sumCells [0] + 1;
                rasterRow++;
            }
            for (int row = 0; row < rasterNumbers[municipality]; row++){
                weightCells[row] = weightCells[row] / sumWeights;
            }
            rasterCells.appendColumn(realCells,listMunicipalities[municipality]);
            rasterCellsCount.appendColumn(sumCells,listMunicipalities[municipality]);
            rasterWeights.appendColumn(weightCells,listMunicipalities[municipality]);
        }


        int[] microDataIds = frequencyMatrix.getColumnAsInt("ID");
        int previousHouseholds = 0;
        int previousPersons = 0;


        //Define car probability
        //They depend on household size. The probability is for all Bavaria



        //Types of job
        jobsTable = SiloUtil.readCSVfile(rb.getString(PROPERTIES_JOB_DESCRIPTION));


        //Selection of households, persons, jobs and dwellings per municipality
        for (int municipality = 0; municipality < listMunicipality.length; municipality++){
            //logger.info("   Municipality " + municipality + " started.");

            //select the probabilities of the raster cells from the matrix
            int totalHouseholds = (int) marginalsHouseholdMatrix.getIndexedValueAt(listMunicipality[municipality],"hhTotal");
            double[] probability = weightsTable.getColumnAsDouble(listMunicipalities[municipality]);
            probability = SiloUtil.convertProbability(probability);

            //for all the households that are inside the municipality (we will match perfectly the number of households. The total population will vary compared to the marginals.)
            //TODO. Consider to add some control variable to the produced population. (i.e. if the population is less than 95% of the total population, add some extra households until population is at least 95% or number of households excess 105 % of households)
            for (int row = 0; row < totalHouseholds; row++) {
                int record = SiloUtil.select(probability, microDataIds);
                int householdSize = (int) microDataHousehold.getIndexedValueAt(record, "hhSize");
                int householdWorkers = (int) microDataHousehold.getIndexedValueAt(record, "femaleWorkers") +
                        (int) microDataHousehold.getIndexedValueAt(record, "maleWorkers");
                int householdCell = SiloUtil.select(rasterWeights.getColumnAsFloat(listMunicipalities[municipality]),
                        (int) rasterCellsCount.getValueAt(1,listMunicipalities[municipality]),
                        rasterCells.getColumnAsInt(listMunicipalities[municipality]));
                int id = HouseholdDataManager.getNextHouseholdId();
                new Household(id, householdCell, householdCell, householdSize, householdWorkers); //(int id, int dwellingID, int homeZone, int hhSize, int autos)
                for (int rowPerson = 0; rowPerson < householdSize; rowPerson++) {
                    int idPerson = HouseholdDataManager.getNextPersonId();
                    int personCounter = (int) microDataHousehold.getIndexedValueAt(record, "personCount") + rowPerson;
                    int age = (int) microDataPerson.getValueAt(personCounter, "age");
                    int gender = (int) microDataPerson.getValueAt(personCounter, "gender");
                    int occupation = (int) microDataPerson.getValueAt(personCounter, "occupation");
                    int income = (int) microDataPerson.getValueAt(personCounter, "income"); // TODO. Change income thresholds from Microcensus to the income thresholds of SILO.
                    int workplace = (int) microDataPerson.getValueAt(personCounter,"workplace"); // TODO. Change workplace to the actual raster cell where the person is working base on commute trip lengths distribution
                    if (microDataPerson.getValueAt(personCounter,"nationality") == 8) { //race is equal to other if the person is foreigner.
                        new Person(idPerson, id, age, gender, Race.other, occupation, workplace, income); //(int id, int hhid, int age, int gender, Race race, int occupation, int workplace, int income)
                    } else {
                        new Person(idPerson, id, age, gender, Race.white, occupation, workplace, income); //(int id, int hhid, int age, int gender, Race race, int occupation, int workplace, int income)
                    }
                    if (occupation == 1){
                        //We generate a new job because the person is employed
                        int idJob = JobDataManager.getNextJobId();
                        int jobPerson = translateJobType(Integer.toString((int) microDataPerson.getValueAt(personCounter,"jobSector")),jobsTable);
                        //new Job(idJob,workplace,-1,JobType.getJobType(jobPerson)); //TODO. Understand job types and how to generate them
                    }

                }
                int newDdId = RealEstateDataManager.getNextDwellingId();
                int pumsDdType = (int) microDataHousehold.getIndexedValueAt(record, "hhDwellingType");
                DwellingType ddType = translateDwellingType(pumsDdType);
                int bedRooms = 1; //marginal data at the municipality level
                int quality = 1; //depend on complete plumbing, complete kitchen and year built.
                int price = 1; //not significant at this point
                int year = 2000; //not significant at this point
                new Dwelling(newDdId, householdCell, id, ddType, bedRooms, quality, price, 0, year); //newDwellingId, raster cell, HH Id, ddType, bedRooms, quality, price, restriction, construction year

            }
            int households = HouseholdDataManager.getHighestHouseholdIdInUse()-previousHouseholds;
            int persons = HouseholdDataManager.getHighestPersonIdInUse()-previousPersons;
            logger.info("   Municipality " + listMunicipality[municipality]+ ". Generated " + persons + " persons in " + households + " households.");
            previousHouseholds = HouseholdDataManager.getHighestHouseholdIdInUse();
            previousPersons = HouseholdDataManager.getHighestPersonIdInUse();
        }
        int households = HouseholdDataManager.getHighestHouseholdIdInUse();
        int persons = HouseholdDataManager.getHighestPersonIdInUse();
        logger.info("   Finished generating households and persons. A population of " + persons + " persons in " + households + " households was generated.");

    }



    private DwellingType translateDwellingType (int pumsDdType) {
        // translate 10 PUMA into 6 MetCouncil Dwelling Types

        // Available in MICRO CENSUS:
//        V 01 . Small building (1-4 apartments)
//        V 02 . Medium buildings (5-10 apartments)
//        V 03 . Big buildings (11 or more apartments)
//        V 04 . Group quarter (Gemeinschafts)
//        V 06 . Neubaten

        DwellingType type;
        if (pumsDdType == 1) type = DwellingType.MF234; //duplexes and buildings 2-4 units
        else if (pumsDdType == 6) type = DwellingType.SFD; //single-family house detached
        //else if (pumsDdType == 3) type = DwellingType.SFA;//single-family house attached or townhouse
        //else if (pumsDdType == 4 || pumsDdType == 5) type = DwellingType.MH; //mobile home
        else if (pumsDdType >= 2 && pumsDdType <= 4) type = DwellingType.MF5plus; //multifamily houses with 5+ units
        else {
            logger.error("Unknown dwelling type " + pumsDdType + " found in PUMS data.");
            type = null;
        }
        return type;
    }


    private static int translateJobType (String personJob, TableDataSet jobs){
        //translate 100 job descriptions to 4 job types
        //jobs is one TableDataSet that is read from a csv file containing the description, ID and types of jobs.
        String job = "OTH";
        int jobClass = 4;
        int finish = 0;
        int row = 1;
        while (finish == 0 & row < jobs.getRowCount()){
            if (personJob == jobs.getStringValueAt(row,"Description")) {
                finish =1;
                job = jobs.getStringValueAt(row,"Type");
            }
            else {
                row++;
            }
        }
        if (job == "RET") {jobClass = 1;}
        else if (job == "OFF") {jobClass = 2;}
        else if (job == "IND") {jobClass = 3;}

        return jobClass;
    }

    private void readDwellings(){
        //Read the entry data from the dwellings of the region of Bavaria.
        logger.info(    "Reading the dwellings data file.");

        //Read the file from Corinna
        microDataDwelling = SiloUtil.readCSVfile(rb.getString(PROPERTIES_MICRO_DATA_DWELLINGS));
        microDataDwelling.buildIndex(microDataDwelling.getColumnPosition("ID"));
    }


    private void selectDwelling(){
        //Based on the entry data, generate the dwellings. Assign the households to the dwellings.
        logger.info("   Starting to generate dwellings.");

        //Check for the different types of dwelling and assign the attributes that are not defined on the microData.

        for(int row = 0; row < microDataDwelling.getRowCount();row++){
            int id = RealEstateDataManager.getNextDwellingId();
            //note: for household Id should be picked from the households randomly (and then removed from the sample) or it is some relationship to keep?

            new Dwelling(id,0,0,DwellingType.MH,0,0,0,0,2000); //(int id, int zone, int hhId, DwellingType type, int bedrooms, int quality, int price, float restriction,int year)
        }
        logger.info("   Finished generating dwellings.");
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


}
