package edu.umd.ncsg.SyntheticPopulationGenerator;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.util.ResourceUtil;
import edu.umd.ncsg.SiloModel;
import edu.umd.ncsg.SiloUtil;
import edu.umd.ncsg.data.*;
import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.GammaDistributionImpl;
import org.apache.log4j.Logger;

import java.io.*;
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


    protected TableDataSet microDataHousehold;
    protected TableDataSet microDataPerson;
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

    protected TableDataSet weightsTable;
    protected TableDataSet jobsTable;

    protected int maxIterations;
    protected double maxError;
    protected double initialError;
    protected double improvementError;
    protected double iterationError;


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
        if (ResourceUtil.getIntegerProperty(rb,PROPERTIES_YEAR_MICRODATA) == 2000) {
            readDataSynPop(); //Read the micro data from 2000
        } else {
            readDataSynPop2010(); //Read the micro data from 2010
        }
       if (ResourceUtil.getIntegerProperty(rb,PROPERTIES_RUN_IPU) == 1) {
            if (ResourceUtil.getIntegerProperty(rb, PROPERTIES_RUN_DEPENDENT) == 1) {
                runIPUAreaDependent(); //IPU fitting with two geographical resolutions
            } else {
                runIPUIndependent(); //IPU fitting with one geographical resolution. Each municipality is independent of others
            }
            selectHouseholds(); //Monte Carlo selection process to generate the synthetic population. The synthetic dwellings will be obtained from the same microdata
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
        String pumsFileName = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_MICRODATA_2000_PATH);
        String recString = "";
        int recCount = 0;
        int hhCountTotal = 0;
        int personCountTotal = 0;
        int hhOutCountTotal = 0;
        int personQuarterCountTotal = 0;
        int quarterCountTotal = 0;
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
                        if (convertToInteger(recString.substring(313,314)) == 1) { //we match private households AND group quarters
                            if (householdNumber != previousHouseholdNumber) {
                                hhCountTotal++;
                                personCountTotal++;
                                previousHouseholdNumber = householdNumber; // Update the household number
                            } else if (householdNumber == previousHouseholdNumber) {
                                personCountTotal++;
                            }
                        } else {
                            personCountTotal++;
                            hhCountTotal++;
                            quarterCountTotal++;
                            personQuarterCountTotal++;
                        }
                    default:
                        hhOutCountTotal++;
                       break;
                }
            }
            logger.info("  Read " + (personCountTotal - personQuarterCountTotal) + " person records in " +
                    (hhCountTotal - quarterCountTotal) + " private households and " + personQuarterCountTotal +
                    " person records in " + quarterCountTotal + " group quarters in Bavaria from file: " + pumsFileName);
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
        int personTelework[] = new int[personCountTotal];
        int personSubsample[] = new int[personCountTotal];
        int personQuarter[] = new int[personCountTotal];
        int personCount = 0;
        int personHHCount = 0;
        int foreignCount = 0;
        int hhCount = -1;
        //Household variables
        ageBracketsPerson = ResourceUtil.getIntegerArray(rb, PROPERTIES_MICRO_DATA_AGES);
        ageBracketsPersonQuarter = ResourceUtil.getIntegerArray(rb, PROPERTIES_MICRO_DATA_AGES_QUARTER);
        int hhTotal [] = new int[hhCountTotal];
        int hhSingle [] = new int [hhCountTotal];
        int hhMaleWorkers[] = new int[hhCountTotal];
        int hhFemaleWorkers[] = new int[hhCountTotal];
        int hhWorkers[] = new int[hhCountTotal];
        int hhMaleAge[][] = new int[hhCountTotal][ageBracketsPerson.length];
        int hhFemaleAge[][] = new int[hhCountTotal][ageBracketsPerson.length];
        int quarterMaleAge[][] = new int[hhCountTotal][ageBracketsPerson.length];
        int quarterFemaleAge[][] = new int[hhCountTotal][ageBracketsPerson.length];
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
        int hhQuarters[] = new int[hhCountTotal];
        int quarterId[] = new int[hhCountTotal];
        int incomeCounter = 0;
        int householdNumber = 0;
        int quarterCounter = 0;
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
                        //if (convertToInteger(recString.substring(313,314)) == 1) { //we match private households and group quarters
                        //Household characteristics
                        if (householdNumber != previousHouseholdNumber & convertToInteger(recString.substring(313,314)) == 1) {
                            //Update household characteristics when it is different number and private household
                            hhCount++;
                            hhQuarters[hhCount] = 0; //private household
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
                            hhIncome[hhCount] = convertToInteger(recString.substring(341, 343)); //Netto! Has 24 categories. Detail below
                            hhId[hhCount] = convertToInteger(recString.substring(2, 9));
                            previousHouseholdNumber = householdNumber; // Update the household number
                            if (hhCount > 1 & hhCount < hhCountTotal - 1) {
                                hhSizeCount[hhCount - 1] = personHHCount;
                                personCounts[hhCount - 1] = personCount - hhSize[hhCount - 1] + 1;
                                hhForeigners[hhCount - 1] = foreignCount;
                            } else if (hhCount == hhCountTotal - 1) {
                                hhSizeCount[hhCount] = hhSize[hhCount];
                                personCounts[hhCount] = personCountTotal - hhSize[hhCount] + 1;
                                hhForeigners[hhCount - 1] = foreignCount;
                                personCounts[hhCount - 1] = personCount - hhSize[hhCount - 1] + 1;
                            } else {
                                hhSizeCount[hhCount] = hhSize[hhCount];
                                personCounts[hhCount] = hhSize[hhCount];
                                hhForeigners[hhCount] = 1;
                            }
                            personHHCount = 0;
                            incomeCounter = 0;
                            foreignCount = 0;
                        } else if (convertToInteger(recString.substring(313,314)) != 1) { //we have a group quarter
                            hhCount++;
                            quarterCounter++;
                            hhSize[hhCount] = 1; //we put 1 instead of the quarter size because each person in group quarter has its own household
                            hhDwellingType[hhCount] = convertToInteger(recString.substring(476, 477)); // 1: 1-4 apartments, 2: 5-10 apartments, 3: 11 or more, 4: gemainschafts, 6: neubauten
                            hhQuarters[hhCount] = 1; //group quarter
                            hhSizeCategory[hhCount] = "group quarter";
                            hhIncome[hhCount] = convertToInteger(recString.substring(341, 343)); //Netto! Has 24 categories. Detail below
                            hhId[hhCount] = quarterCounter;
                            quarterId[hhCount] = convertToInteger(recString.substring(2, 9));
                            previousHouseholdNumber = householdNumber; // Update the household number
                            if (hhCount > 1 & hhCount < hhCountTotal - 1) {
                                hhSizeCount[hhCount - 1] = personHHCount;
                                personCounts[hhCount - 1] = personCount - hhSize[hhCount - 1] + 1;
                                hhForeigners[hhCount - 1] = foreignCount;
                            } else if (hhCount == hhCountTotal - 1) {
                                hhSizeCount[hhCount] = hhSize[hhCount];
                                personCounts[hhCount] = personCountTotal - hhSize[hhCount] + 1;
                                hhForeigners[hhCount - 1] = foreignCount;
                                personCounts[hhCount - 1] = personCount - hhSize[hhCount - 1] + 1;
                            } else {
                                hhSizeCount[hhCount] = hhSize[hhCount];
                                personCounts[hhCount] = hhSize[hhCount];
                                hhForeigners[hhCount] = 1;
                            }
                            personHHCount = 0;
                            incomeCounter = 0;
                            foreignCount = 0;
                        }
                        //Person characteristics
                        age[personCount] = convertToInteger(recString.substring(25, 27));
                        gender[personCount] = convertToInteger(recString.substring(28, 29)); // 1: male; 2: female
                        occupation[personCount] = convertToInteger(recString.substring(74, 75)); // 1: employed, 8: unemployed, empty: NA
                        personId[personCount] = convertToInteger(recString.substring(2, 11));
                        personHH[personCount] = convertToInteger(recString.substring(2, 9));
                        personIncome[personCount] = convertToInteger(recString.substring(297, 299));
                        personNationality[personCount] = convertToInteger(recString.substring(45, 46)); // 1: only German, 2: dual German citizenship, 8: foreigner; (Marginals consider dual citizens as Germans)
                        personWorkplace[personCount] = convertToInteger(recString.substring(151, 152)); //1: at the municipality, 2: in Berlin, 3: in other municipality of the Bundeslandes, 9: NA
                        personCommuteTime[personCount] = convertToInteger(recString.substring(157, 157)); //1: less than 10 min, 2: 10-30 min, 3: 30-60 min, 4: more than 60 min, 9: NA
                        personTransportationMode[personCount] = convertToInteger(recString.substring(158, 160)); //1: bus, 2: ubahn, 3: eisenbahn, 4: car (driver), 5: carpooled, 6: motorcycle, 7: bike, 8: walk, 9; other, 99: NA
                        personJobStatus[personCount] = convertToInteger(recString.substring(99, 101)); //1: self employed without employees, 2: self employed with employees, 3: family worker, 4: officials judges, 5: worker, 6: home workers, 7: tech trainee, 8: commercial trainee, 9: soldier, 10: basic compulsory military service, 11: zivildienstleistender
                        personJobSector[personCount] = convertToInteger(recString.substring(101, 103)); //Systematische Übersicht der Klassifizierung der Berufe, Ausgabe 1992.
                        personTelework[personCount] =  convertToInteger(recString.substring(141, 142)); //If they telework
                        personSubsample[personCount] =  convertToInteger(recString.substring(497, 498));
                        personQuarter[personCount] = convertToInteger(recString.substring(313,314)); // 1: private household,
                        int row = 0;
                        while (age[personCount] > ageBracketsPerson[row]) {
                            row++;
                        }
                        int row1 = 0;
                        while (age[personCount] > ageBracketsPersonQuarter[row1]) {
                            row1++;
                        }
                        if (gender[personCount] == 1) {
                            if (occupation[personCount] == 1) {
                                hhMaleWorkers[hhCount]++;
                                hhWorkers[hhCount]++;
                            }
                            if (personQuarter[personCount] == 1) {
                                hhMaleAge[hhCount][row]++;
                            } else {
                                quarterMaleAge[hhCount][row1]++;
                            }
                        } else if (gender[personCount] == 2) {
                            if (occupation[personCount] == 1) {
                                hhFemaleWorkers[hhCount]++;
                                hhWorkers[hhCount]++;
                            }
                            if (personQuarter[personCount] == 1) {
                                hhFemaleAge[hhCount][row]++;
                            } else {
                                quarterFemaleAge[hhCount][row1]++;
                            }
                        }
                        if (personNationality[personCount] == 8){
                            foreignCount++;
                        }
                        personCount++;
                        personHHCount++;
                        /*} else {
                            previousHouseholdNumber = householdNumber; // Update the household number
                        }*/
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
        microPersons.appendColumn(personTelework,"telework");
        microPersons.appendColumn(personSubsample,"subsample");
        microPersons.appendColumn(personQuarter,"privateHousehold");
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
        for (int row = 0; row < ageBracketsPersonQuarter.length; row++){
            int[] ageMale = SiloUtil.obtainColumnFromArray(quarterMaleAge,hhCountTotal,row);
            int[] ageFemale = SiloUtil.obtainColumnFromArray(quarterFemaleAge,hhCountTotal,row);
            String nameMale = "maleQuarter" + ageBracketsPersonQuarter[row];
            String nameFemale = "femaleQuarter" + ageBracketsPersonQuarter[row];
            microRecords.appendColumn(ageMale,nameMale);
            microRecords.appendColumn(ageFemale,nameFemale);
        }
        microRecords.appendColumn(hhIncome,"hhIncome");
        microRecords.appendColumn(hhSize,"hhSizeDeclared");
        microRecords.appendColumn(hhSizeCount,"hhSize");
        microRecords.appendColumn(personCounts,"personCount");
        microRecords.appendColumn(hhDwellingType,"hhDwellingType");
        microRecords.appendColumn(hhSizeCategory,"hhSizeCategory");
        microRecords.appendColumn(hhQuarters,"groupQuarters");
        microRecords.appendColumn(quarterId,"microRecord");
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
        for (int row = 0; row < ageBracketsPersonQuarter.length; row++){
            int[] ageMale = SiloUtil.obtainColumnFromArray(quarterMaleAge,hhCountTotal,row);
            int[] ageFemale = SiloUtil.obtainColumnFromArray(quarterFemaleAge,hhCountTotal,row);
            String nameMale = "maleQuarters" + ageBracketsPersonQuarter[row];
            String nameFemale = "femaleQuarters" + ageBracketsPersonQuarter[row];
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
        microRecords1.appendColumn(hhQuarters,"populationQuarters");
        frequencyMatrix = microRecords1;


        String hhFileName = ("scenOutput/microHouseholds.csv");
        SiloUtil.writeTableDataSet(microDataHousehold, hhFileName);

        String freqFileName = ("scenOutput/frequencyMatrix.csv");
        SiloUtil.writeTableDataSet(frequencyMatrix, freqFileName);

        String freqFileName1 = ("scenOutput/microPerson.csv");
        SiloUtil.writeTableDataSet(microDataPerson, freqFileName1);

        logger.info("   Finished reading the micro data");
    }


    private void readDataSynPop2010(){
        //method to read the synthetic population initial data
        logger.info("   Starting to read the micro data");

        //Scanning the file to obtain the number of households and persons in Bavaria
        String pumsFileName = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_MICRODATA_2010_PATH);
        String recString = "";
        int recCount = 0;
        int hhCountTotal = 0;
        int personCountTotal = 0;
        int hhOutCountTotal = 0;
        int personQuarterCountTotal = 0;
        int quarterCountTotal = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(pumsFileName));
            int previousHouseholdNumber = -1;
            while ((recString = in.readLine()) != null) {
                recCount++;
                String recLander = recString.substring(0,2);
                int householdNumber = 0;
                switch (recLander) {
                    case "09": //Record from Bavaria
                        householdNumber = convertToInteger(recString.substring(2,11));
                        if (convertToInteger(recString.substring(34,35)) == 1) { //we match private households AND group quarters
                            if (householdNumber != previousHouseholdNumber) {
                                hhCountTotal++;
                                personCountTotal++;
                                previousHouseholdNumber = householdNumber; // Update the household number
                            } else if (householdNumber == previousHouseholdNumber) {
                                personCountTotal++;
                            }
                        } else { //group quarter
                            personCountTotal++;
                            hhCountTotal++;
                            quarterCountTotal++;
                            personQuarterCountTotal++;
                        }
                    default:
                        hhOutCountTotal++;
                        break;
                }
            }
            logger.info("  Read " + (personCountTotal - personQuarterCountTotal) + " person records in " +
                    (hhCountTotal - quarterCountTotal) + " private households and " + personQuarterCountTotal +
                    " person records in " + quarterCountTotal + " group quarters in Bavaria from file: " + pumsFileName);
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
        int personJobSector[] = new int[personCountTotal];
        int personTelework[] = new int[personCountTotal];
        int personSubsample[] = new int[personCountTotal];
        int personQuarter[] = new int[personCountTotal];
        int personEducation[] = new int[personCountTotal];
        int personStatus[] = new int[personCountTotal];
        int personCount = 0;
        int personHHCount = 0;
        int foreignCount = 0;
        int hhCount = -1;
        jobsTable = SiloUtil.readCSVfile(rb.getString(PROPERTIES_JOB_DESCRIPTION));
        //Household variables
        ageBracketsPerson = ResourceUtil.getIntegerArray(rb, PROPERTIES_MICRO_DATA_AGES);
        ageBracketsPersonQuarter = ResourceUtil.getIntegerArray(rb, PROPERTIES_MICRO_DATA_AGES_QUARTER);
        int hhTotal [] = new int[hhCountTotal];
        int hhSingle [] = new int [hhCountTotal];
        int hhMaleWorkers[] = new int[hhCountTotal];
        int hhFemaleWorkers[] = new int[hhCountTotal];
        int hhWorkers[] = new int[hhCountTotal];
        int hhMaleAge[][] = new int[hhCountTotal][ageBracketsPerson.length];
        int hhFemaleAge[][] = new int[hhCountTotal][ageBracketsPerson.length];
        int quarterMaleAge[][] = new int[hhCountTotal][ageBracketsPerson.length];
        int quarterFemaleAge[][] = new int[hhCountTotal][ageBracketsPerson.length];
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
        int hhQuarters[] = new int[hhCountTotal];
        int quarterId[] = new int[hhCountTotal];
        int incomeCounter = 0;
        int householdNumber = 0;
        int quarterCounter = 0;
        //Dwelling variables
        int yearBracketsDwelling[] = ResourceUtil.getIntegerArray(rb, PROPERTIES_MICRO_DATA_YEAR_DWELLING);
        int sizeBracketsDwelling[] = ResourceUtil.getIntegerArray(rb, PROPERTIES_MICRO_DATA_FLOOR_SPACE_DWELLING);
        int dwOwned[] = new int[hhCountTotal];
        int dwRented[] = new int[hhCountTotal];
        int dwellingUsage[] = new int[hhCountTotal];
        int dwellingSpace[] = new int[hhCountTotal];
        int dwellingYear[] = new int[hhCountTotal];
        int dwSmallYear[][] = new int[hhCountTotal][yearBracketsDwelling.length];
        int dwMediumYear[][] = new int[hhCountTotal][yearBracketsDwelling.length];
        int dwFloorSpace[][] = new int[hhCountTotal][sizeBracketsDwelling.length];
        int dwellingType[] = new int[hhCountTotal];
        int dwellingRent[] = new int[hhCountTotal];
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
                        householdNumber = convertToInteger(recString.substring(2, 11));
                        //if (convertToInteger(recString.substring(313,314)) == 1) { //we match private households and group quarters
                        //Household and dwelling characteristics
                        if (householdNumber != previousHouseholdNumber & convertToInteger(recString.substring(34,35)) == 1) {
                            //Private households

                            //Household characteristics
                            hhCount++;
                            hhId[hhCount] = convertToInteger(recString.substring(2, 11));
                            hhQuarters[hhCount] = 0; //private household
                            hhSize[hhCount] = convertToInteger(recString.substring(26, 28));
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
                            hhIncome[hhCount] = convertToInteger(recString.substring(658, 660)); //Netto income in EUR, 24 categories. 50: from agriculture, 99: not stated
                            if (hhCount > 1 & hhCount < hhCountTotal - 1) {
                                hhSizeCount[hhCount - 1] = personHHCount;
                                personCounts[hhCount - 1] = personCount - hhSize[hhCount - 1] + 1;
                                hhForeigners[hhCount - 1] = foreignCount;
                            } else if (hhCount == hhCountTotal - 1) {
                                hhSizeCount[hhCount] = hhSize[hhCount];
                                personCounts[hhCount] = personCountTotal - hhSize[hhCount] + 1;
                                hhForeigners[hhCount - 1] = foreignCount;
                                personCounts[hhCount - 1] = personCount - hhSize[hhCount - 1] + 1;
                            } else {
                                hhSizeCount[hhCount] = hhSize[hhCount];
                                personCounts[hhCount] = hhSize[hhCount];
                                hhForeigners[hhCount] = 1;
                            }

                            //dwelling characteristics
                            dwellingType[hhCount] = convertToInteger(recString.substring(491, 493)); // 1: 1-2 dwellings; 2: 3-6 dwellings, 3: 7-12 dwellings; 4: 13-20 dwellings, 5: 21+ dwellings, 9: not stated.
                            dwellingUsage[hhCount] = convertToInteger(recString.substring(493, 495)); // 1: owner of the building, 2: owner of the apartment, 3: main tenant, 4: subtenant, 9: not stated.
                            dwellingYear[hhCount] = convertToInteger(recString.substring(500, 502)); // Construction year. 1: before 1919, 2: 1919-1948, 3: 1949-1978, 4: 1979 - 1986; 5: 1987 - 1990; 6: 1991 - 2000; 7: 2001 - 2004; 8: 2005 - 2008, 9: 2009 or later, 99: not stated.
                            dwellingSpace[hhCount] = convertToInteger(recString.substring(495, 498)); // Size of the apartment in square meters (from 10 to 999).
                            dwellingRent[hhCount] = convertToInteger(recString.substring(1081, 1085)); // Monthly rent, in euros (Bruttokaltmiete). For Gesamtmiete, 508-512.
                            int row = 0;
                            while (dwellingYear[hhCount] > yearBracketsDwelling[row]){
                                row++;
                            }
                            if (dwellingType[hhCount] == 1){
                                dwSmallYear[hhCount][row] = 1;
                            } else {
                                dwMediumYear[hhCount][row] = 1; //also includes the not stated.
                            }
                            if (dwellingUsage[hhCount] < 3){
                                dwOwned[hhCount] = 1;
                                dwellingUsage[hhCount] = 1;
                            } else if (dwellingUsage[hhCount] < 5) {
                                dwRented[hhCount] = 1;
                                dwellingUsage[hhCount] = 2;
                            }
                            int row1 = 0;
                            while (dwellingSpace[hhCount] > sizeBracketsDwelling[row1]){
                                row1++;
                            }
                            dwFloorSpace[hhCount][row1] = 1;

                            //Update household number and person counters for the next private household
                            previousHouseholdNumber = householdNumber;
                            personHHCount = 0;
                            incomeCounter = 0;
                            foreignCount = 0;

                        } else if (convertToInteger(recString.substring(313,314)) == 2) {
                            //Group quarter

                            hhCount++;
                            quarterCounter++;
                            hhSize[hhCount] = 1; //we put 1 instead of the quarter size because each person in group quarter has its own household
                            dwellingType[hhCount] = convertToInteger(recString.substring(491, 493)); // 1: 1-2 dwellings; 2: 3-6 dwellings: 3: 7-12 dwellings; 4: 13-20 dwellings, 5: 21+ dwellings, 9: not stated
                            hhQuarters[hhCount] = 1; //group quarter
                            hhSizeCategory[hhCount] = "group quarter";
                            hhIncome[hhCount] = convertToInteger(recString.substring(658, 660)); //Netto income in EUR, 24 categories. 50: from agriculture, 99: not stated
                            hhId[hhCount] = quarterCounter;
                            quarterId[hhCount] = convertToInteger(recString.substring(2, 11)); //we keep the record from the group quarter
                            if (hhCount > 1 & hhCount < hhCountTotal - 1) {
                                hhSizeCount[hhCount - 1] = personHHCount;
                                personCounts[hhCount - 1] = personCount - hhSize[hhCount - 1] + 1;
                                hhForeigners[hhCount - 1] = foreignCount;
                            } else if (hhCount == hhCountTotal - 1) {
                                hhSizeCount[hhCount] = hhSize[hhCount];
                                personCounts[hhCount] = personCountTotal - hhSize[hhCount] + 1;
                                hhForeigners[hhCount - 1] = foreignCount;
                                personCounts[hhCount - 1] = personCount - hhSize[hhCount - 1] + 1;
                            } else {
                                hhSizeCount[hhCount] = hhSize[hhCount];
                                personCounts[hhCount] = hhSize[hhCount];
                                hhForeigners[hhCount] = 1;
                            }

                            //dwelling characteristics
                            dwellingType[hhCount] = convertToInteger(recString.substring(491, 493)); // 1: 1-2 dwellings; 2: 3-6 dwellings, 3: 7-12 dwellings; 4: 13-20 dwellings, 5: 21+ dwellings, 9: not stated.
                            dwellingUsage[hhCount] = convertToInteger(recString.substring(493, 495)); // 1: owner of the building, 2: owner of the apartment, 3: main tenant, 4: subtenant, 9: not stated.
                            dwellingYear[hhCount] = convertToInteger(recString.substring(500, 502)); // Construction year. 1: before 1919, 2: 1919-1948, 3: 1949-1978, 4: 1979 - 1986; 5: 1987 - 1990; 6: 1991 - 2000; 7: 2001 - 2004; 8: 2005 - 2008, 9: 2009 or later, 99: not stated.
                            dwellingSpace[hhCount] = convertToInteger(recString.substring(495, 498)); // Size of the apartment in square meters (from 10 to 999).
                            dwellingRent[hhCount] = convertToInteger(recString.substring(1081, 1085)); // Monthly rent, in euros (Bruttokaltmiete). For Gesamtmiete, 508-512.
                            if (dwellingUsage[hhCount] < 3){
                                dwellingUsage[hhCount] = 1;
                            } else if (dwellingUsage[hhCount] < 5) {
                                dwellingUsage[hhCount] = 2;
                            }
                            //All dwelling characteristics are more related to private households

                            //Update household number and person counters for the next private household
                            previousHouseholdNumber = householdNumber;
                            personHHCount = 0;
                            incomeCounter = 0;
                            foreignCount = 0;
                        }

                        //Person characteristics
                        age[personCount] = convertToInteger(recString.substring(50, 52)); // 0 to 95. 95 includes 95+
                        gender[personCount] = convertToInteger(recString.substring(54, 55)); // 1: male; 2: female
                        occupation[personCount] = convertToInteger(recString.substring(32, 33)); // 1: employed, 2: unemployed, 3: unemployed looking for job, 4: children and retired
                        personId[personCount] = convertToInteger(recString.substring(1159, 1166));
                        personHH[personCount] = convertToInteger(recString.substring(2, 11));
                        personIncome[personCount] = convertToInteger(recString.substring(471, 473)); //Netto income in EUR, 24 categories. 50: from agriculture, 90: any income, 99: not stated
                        personNationality[personCount] = convertToInteger(recString.substring(370, 372)); // 1: only German, 2: dual German citizenship, 8: foreigner; (Marginals consider dual citizens as Germans)
                        personJobSector[personCount] = translateJobType(convertToInteger(recString.substring(163, 165)),jobsTable); //First two digits of the WZ08 job classification in Germany. They are converted to 10 job classes (Zensus 2011 - Erwerbstätige nach Wirtschaftszweig Wirtschafts(unter)bereiche)
                        personTelework[personCount] =  convertToInteger(recString.substring(198, 200)); //If they telework
                        personQuarter[personCount] = convertToInteger(recString.substring(34,35)); // 1: private household, 2: group quarter
                        int education = convertToInteger(recString.substring(323,325)); // 1: High school, 2: Professional school, 3: Fachhochschule, 4: University, 5: Doctorate, 6: preparatory for public administration, 99: not stated                        1: primary school, 2-7: high school, 8-19: professional school, 20: university, 21: doctorate, 99: not stated
                        if (education == 1){
                            personEducation[personCount] = 1;
                        } else if (education < 7){
                            personEducation[personCount] = 2;
                        } else if (education < 9) {
                            personEducation[personCount] = 3;
                        } else if (education == 9) {
                            personEducation[personCount] = 4;
                        } else if (education == 10) {
                            personEducation[personCount] = 5;
                        } else if (education == 11) {
                            personEducation[personCount] = 5;
                        } else {
                            personEducation[personCount] = 99;
                        }
                        int marital = convertToInteger(recString.substring(59, 60)); //1: single, 2: married, 3: widowed, 4: divorced, 5: same sex marriage, 6: same sex widow, 7: same sex divorced
                        if (marital == 2){
                            personStatus[personCount] = 2;
                        } else {
                            personStatus[personCount] = 1;
                        }
                        if (age[personCount] < 16){
                            personStatus[personCount] = 3;
                        }
                        int row = 0;
                        while (age[personCount] > ageBracketsPerson[row]) {
                            row++;
                        }
                        int row1 = 0;
                        while (age[personCount] > ageBracketsPersonQuarter[row1]) {
                            row1++;
                        }
                        if (gender[personCount] == 1) {
                            if (occupation[personCount] == 1) {
                                hhMaleWorkers[hhCount]++;
                                hhWorkers[hhCount]++;
                            }
                            if (personQuarter[personCount] == 1) {
                                hhMaleAge[hhCount][row]++;
                            } else {
                                quarterMaleAge[hhCount][row1]++;
                            }
                        } else if (gender[personCount] == 2) {
                            if (occupation[personCount] == 1) {
                                hhFemaleWorkers[hhCount]++;
                                hhWorkers[hhCount]++;
                            }
                            if (personQuarter[personCount] == 1) {
                                hhFemaleAge[hhCount][row]++;
                            } else {
                                quarterFemaleAge[hhCount][row1]++;
                            }
                        }
                        if (personNationality[personCount] == 8){
                            foreignCount++;
                        }
                        personCount++;
                        personHHCount++;
                        /*} else {
                            previousHouseholdNumber = householdNumber; // Update the household number
                        }*/
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
        microPersons.appendColumn(personJobSector,"jobSector");
        microPersons.appendColumn(personTelework,"telework");
        microPersons.appendColumn(personSubsample,"subsample");
        microPersons.appendColumn(personQuarter,"privateHousehold");
        microPersons.appendColumn(personEducation,"educationLevel");
        microPersons.appendColumn(personStatus,"maritalStatus");
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
        for (int row = 0; row < ageBracketsPersonQuarter.length; row++){
            int[] ageMale = SiloUtil.obtainColumnFromArray(quarterMaleAge,hhCountTotal,row);
            int[] ageFemale = SiloUtil.obtainColumnFromArray(quarterFemaleAge,hhCountTotal,row);
            String nameMale = "maleQuarter" + ageBracketsPersonQuarter[row];
            String nameFemale = "femaleQuarter" + ageBracketsPersonQuarter[row];
            microRecords.appendColumn(ageMale,nameMale);
            microRecords.appendColumn(ageFemale,nameFemale);
        }
        microRecords.appendColumn(hhIncome,"hhIncome");
        microRecords.appendColumn(hhSize,"hhSizeDeclared");
        microRecords.appendColumn(hhSizeCount,"hhSize");
        microRecords.appendColumn(personCounts,"personCount");
        microRecords.appendColumn(hhSizeCategory,"hhSizeCategory");
        microRecords.appendColumn(hhQuarters,"groupQuarters");
        microRecords.appendColumn(quarterId,"microRecord");
        microDataHousehold = microRecords;
        microDataHousehold.buildIndex(microDataHousehold.getColumnPosition("ID"));


        //Copy attributes to the dwelling micro data
        TableDataSet microDwellings = new TableDataSet();
        microDwellings.appendColumn(hhId,"dwellingID");
        for (int row = 0; row < yearBracketsDwelling.length; row++){
            int[] yearDwellingSmall = SiloUtil.obtainColumnFromArray(dwSmallYear,hhCountTotal,row);
            int[] yearDwellingMedium = SiloUtil.obtainColumnFromArray(dwMediumYear,hhCountTotal,row);
            String nameSmall = "smallDwelling" + yearBracketsDwelling[row];
            String nameMedium = "mediumDwelling" + yearBracketsDwelling[row];
            microDwellings.appendColumn(yearDwellingSmall,nameSmall);
            microDwellings.appendColumn(yearDwellingMedium ,nameMedium);
        }
        microDwellings.appendColumn(dwellingType,"dwellingType"); //Number of dwellings in the building.
        microDwellings.appendColumn(dwellingUsage,"dwellingUsage"); //Who lives on the dwelling: owner (1) or tenant (2)
        microDwellings.appendColumn(dwellingYear,"dwellingYear"); //Construction year. It has the categories from the micro data
        microDwellings.appendColumn(dwellingSpace,"dwellingFloorSpace"); //Floor space of the dwelling
        microDwellings.appendColumn(dwellingRent,"dwellingRentPrice"); //Rental price of the dwelling


        //Copy attributes to the frequency matrix (IPU)
        TableDataSet microRecords1 = new TableDataSet();
        microRecords1.appendColumn(hhId,"ID");
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
        for (int row = 0; row < ageBracketsPersonQuarter.length; row++){
            int[] ageMale = SiloUtil.obtainColumnFromArray(quarterMaleAge,hhCountTotal,row);
            int[] ageFemale = SiloUtil.obtainColumnFromArray(quarterFemaleAge,hhCountTotal,row);
            String nameMale = "maleQuarters" + ageBracketsPersonQuarter[row];
            String nameFemale = "femaleQuarters" + ageBracketsPersonQuarter[row];
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
        microRecords1.appendColumn(hhQuarters,"populationQuarters");
        for (int row = 0; row < yearBracketsDwelling.length; row++){
            int[] yearDwellingSmall = SiloUtil.obtainColumnFromArray(dwSmallYear,hhCountTotal,row);
            int[] yearDwellingMedium = SiloUtil.obtainColumnFromArray(dwMediumYear,hhCountTotal,row);
            String nameSmall = "smallDwellings" + yearBracketsDwelling[row];
            String nameMedium = "mediumDwellings" + yearBracketsDwelling[row];
            microRecords1.appendColumn(yearDwellingSmall,nameSmall);
            microRecords1.appendColumn(yearDwellingMedium ,nameMedium);
        }
        microRecords1.appendColumn(dwOwned,"ownDwellings");
        microRecords1.appendColumn(dwRented,"rentedDwellings");
        for (int row = 0; row < sizeBracketsDwelling.length; row++){
            int[] sizeDwelling = SiloUtil.obtainColumnFromArray(dwFloorSpace,hhCountTotal,row);
            String name = "dwellings" + sizeBracketsDwelling[row];
            microRecords1.appendColumn(sizeDwelling,name);
        }
        frequencyMatrix = microRecords1;


        String hhFileName = ("scenOutput/microHouseholds.csv");
        SiloUtil.writeTableDataSet(microDataHousehold, hhFileName);

        String freqFileName = ("scenOutput/frequencyMatrix.csv");
        SiloUtil.writeTableDataSet(frequencyMatrix, freqFileName);

        String freqFileName1 = ("scenOutput/microPerson.csv");
        SiloUtil.writeTableDataSet(microDataPerson, freqFileName1);

        logger.info("   Finished reading the micro data");
    }


    private void runIPUIndependent(){
        //IPU process for independent municipalities (only household attributes)
        logger.info("   Starting to prepare the data for IPU");


        //Read the attributes at the municipality level (household attributes) and the marginals at the municipality level
        attributesHousehold = ResourceUtil.getArray(rb, PROPERTIES_HOUSEHOLD_ATTRIBUTES); //attributes are decided on the properties file
        marginalsHouseholdMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_MARGINALS_HOUSEHOLD_MATRIX));// all the marginals from the municipalities
        marginalsHouseholdMatrix.buildIndex(marginalsHouseholdMatrix.getColumnPosition("ID_city"));
        int[] microDataIds = frequencyMatrix.getColumnAsInt("ID");
        frequencyMatrix.buildIndex(frequencyMatrix.getColumnPosition("ID"));


        //Obtain the municipalities that are used for IPU
        int[] cityIDall = marginalsHouseholdMatrix.getColumnAsInt("ID_city");
        municipalitiesMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_SELECTED_MUNICIPALITIES_LIST)); //array with all municipalities
        int municipalitiesCount = 0;
        for (int row = 1; row <= municipalitiesMatrix.getRowCount(); row++){
            if (municipalitiesMatrix.getValueAt(row,"Select") == 1f){
                municipalitiesCount++;
            }
        }
        cityID = new int[municipalitiesCount];
        cityIDs = new String[municipalitiesCount];
        int rowID = 0;
        for (int row = 1; row <= municipalitiesMatrix.getRowCount(); row++){
            if (municipalitiesMatrix.getValueAt(row,"Select") == 1f){
                cityID[rowID] = (int) municipalitiesMatrix.getValueAt(row,"ID_city");
                cityIDs[rowID] = Integer.toString(cityID[rowID]);
                rowID++;
            }
        }


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
                    sumNonZero[0]++;
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


        //For each municipality, we perform IPU
        for(int municipality = 0; municipality < cityID.length; municipality++){

            logger.info("   Municipality " + cityID[municipality] + ". Starting IPU.");

            //-----------***** Data preparation *****-------------------------------------------------------------------
            //Create local variables to avoid accessing to the same variable on the parallel processing
            int municipalityID = cityID[municipality]; //Municipality that is under review.
            String municipalityIDs = cityIDs[municipality]; //Municipality that is under review.
            String[] attributesHouseholdList = attributesHousehold; //List of attributes.
            TableDataSet microDataMatrix = new TableDataSet(); //Frequency matrix obtained from the micro data.
            microDataMatrix = frequencyMatrix;
            TableDataSet collapsedMicroData = new TableDataSet(); //List of values different than zero, per attribute, from microdata
            collapsedMicroData = nonZero;
            TableDataSet lengthMicroData = new TableDataSet(); //Number of values different than zero, per attribute, from microdata
            lengthMicroData = nonZeroSize;


            //weights: TableDataSet with two columns, the ID of the household from microData and the weights for that municipality
            TableDataSet weights = new TableDataSet();
            weights.appendColumn(microDataIds,"ID");
            double[] dummy = SiloUtil.createArrayWithValue(microDataMatrix.getRowCount(),1.0);
            weights.appendColumn(dummy, municipalityIDs); //the column label is the municipality cityID
            weights.buildIndex(weights.getColumnPosition("ID"));
            TableDataSet minWeights = new TableDataSet();
            minWeights.appendColumn(microDataIds,"ID");
            double[] dummy1 = SiloUtil.createArrayWithValue(microDataMatrix.getRowCount(),1.0);
            minWeights.appendColumn(dummy1,municipalityIDs);
            minWeights.buildIndex(minWeights.getColumnPosition("ID"));


            //marginalsHousehold: TableDataSet that contains in each column the marginal of a household attribute at the municipality level. Only one "real" row
            TableDataSet marginalsHousehold = new TableDataSet();
            int[] dummyw0 = {municipalityID,0};
            marginalsHousehold.appendColumn(dummyw0,"ID_city");
            for(int attribute = 0; attribute < attributesHouseholdList.length; attribute++){
                float[] dummyw1 = {marginalsHouseholdMatrix.getValueAt(marginalsHouseholdMatrix.getIndexedRowNumber(municipalityID),attributesHouseholdList[attribute]),0};
                marginalsHousehold.appendColumn(dummyw1,attributesHouseholdList[attribute]);
            }
            marginalsHousehold.buildIndex(marginalsHousehold.getColumnPosition("ID_city"));


            //weighted sum and errors: TableDataSet that contains in each column the weighted sum (or error) of a household attribute at the municipality level. Only one row
            TableDataSet errorsHousehold = new TableDataSet();
            int[] dummy00 = {municipalityID,0};
            int[] dummy01 = {municipalityID,0};
            errorsHousehold.appendColumn(dummy01,"ID_city");
            for(int attribute = 0; attribute < attributesHouseholdList.length; attribute++){
                double[] dummyA2 = {0,0};
                double[] dummyB2 = {0,0};
                errorsHousehold.appendColumn(dummyB2,attributesHouseholdList[attribute]);
            }
            errorsHousehold.buildIndex(errorsHousehold.getColumnPosition("ID_city"));


            //Calculate the first set of weighted sums and errors, using initial weights equal to 1
            for(int attribute = 0; attribute < attributesHouseholdList.length; attribute++) {
                int positions = (int) lengthMicroData.getValueAt(1, attributesHouseholdList[attribute]);
                float weighted_sum = SiloUtil.getWeightedSum(weights.getColumnAsDouble(municipalityIDs),
                        microDataMatrix.getColumnAsFloat(attributesHouseholdList[attribute]),
                        collapsedMicroData.getColumnAsInt(attributesHouseholdList[attribute]),positions);
                float error = Math.abs((weighted_sum -
                        marginalsHousehold.getIndexedValueAt(municipalityID, attributesHouseholdList[attribute])) /
                        marginalsHousehold.getIndexedValueAt(municipalityID, attributesHouseholdList[attribute]));
                errorsHousehold.setIndexedValueAt(municipalityID,attributesHouseholdList[attribute],error);
            }


            //Stopping criteria
            int maxIterations= ResourceUtil.getIntegerProperty(rb, PROPERTIES_MAX_ITERATIONS,1000);
            double maxError = ResourceUtil.getDoubleProperty(rb, PROPERTIES_MAX_ERROR, 0.0001);
            double improvementError = ResourceUtil.getDoubleProperty(rb, PROPERTIES_IMPROVEMENT_ERROR, 0.001);
            double iterationError = ResourceUtil.getDoubleProperty(rb,PROPERTIES_IMPROVEMENT_ITERATIONS,2);
            double increaseError = ResourceUtil.getDoubleProperty(rb,PROPERTIES_INCREASE_ERROR,1.05);
            double initialError = ResourceUtil.getDoubleProperty(rb, PROPERTIES_INITIAL_ERROR, 1000);


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
                for(int attribute = 0; attribute < attributesHouseholdList.length; attribute++) {
                    //update the weights according to the weighted sum and constraint of this attribute and the weights from the previous attribute
                    weightedSum = SiloUtil.getWeightedSum(weights.getColumnAsDouble(municipalityIDs),
                            microDataMatrix.getColumnAsFloat(attributesHouseholdList[attribute]),
                            collapsedMicroData.getColumnAsInt(attributesHouseholdList[attribute]),
                            (int)lengthMicroData.getValueAt(1,attributesHouseholdList[attribute]));
                    factor = marginalsHousehold.getIndexedValueAt(municipalityID, attributesHouseholdList[attribute]) /
                            weightedSum;
                    for (int row = 0; row < lengthMicroData.getValueAt(1, attributesHouseholdList[attribute]); row++) {
                        position = (int) collapsedMicroData.getIndexedValueAt(microDataIds[row], attributesHouseholdList[attribute]);
                        previousWeight = weights.getValueAt(position, municipalityIDs);
                        weights.setValueAt(position, municipalityIDs, factor * previousWeight);
                    }
                }


                //update the weighted sums and errors of all household attributes, considering the weights after all the attributes
                for (int attributes = 0; attributes < attributesHouseholdList.length; attributes++){
                    weightedSum = SiloUtil.getWeightedSum(weights.getColumnAsDouble(municipalityIDs),
                            microDataMatrix.getColumnAsFloat(attributesHouseholdList[attributes]),
                            collapsedMicroData.getColumnAsInt(attributesHouseholdList[attributes]),
                            (int)lengthMicroData.getValueAt(1,attributesHouseholdList[attributes]));
                    error = Math.abs(weightedSum -
                            marginalsHousehold.getIndexedValueAt(municipalityID, attributesHouseholdList[attributes]))/
                            marginalsHousehold.getIndexedValueAt(municipalityID, attributesHouseholdList[attributes]);
                    errorsHousehold.setIndexedValueAt(municipalityID,attributesHouseholdList[attributes],error);
                    averageErrorIteration += error;
                }
                averageErrorIteration = averageErrorIteration/(attributesHouseholdList.length);


                //Stopping criteria:
                if (averageErrorIteration < maxError){
                    finish = 1;
                    iteration = maxIterations + 1;
                    logger.info("   IPU finished for municipality " + municipalityIDs + " after " + iteration + " iterations.");
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
                    minWeights.setColumnAsFloat(minWeights.getColumnPosition(municipalityIDs),weights.getColumnAsFloat(municipalityIDs));
                    minError = averageErrorIteration;
                }
            }

            //Copy the errors per attribute
            for(int attribute = 0; attribute < attributesHouseholdList.length; attribute++) {
                errorsMatrix.setValueAt(municipality+1,attributesHouseholdList[attribute],errorsHousehold.getIndexedValueAt(municipalityID,attributesHouseholdList[attribute]));
            }

            //Write the weights after finishing IPU for each municipality (saved each time over the previous version)
            weightsMatrix.appendColumn(minWeights.getColumnAsFloat(municipalityIDs),municipalityIDs);
            String freqFileName = ("scenOutput/weigthsMatrix.csv");
            SiloUtil.writeTableDataSet(weightsMatrix, freqFileName);
            String freqFileName2 = ("scenOutput/errorsMatrix.csv");
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


        //Read the attributes at the municipality level (Gemeinden) - Household attributes
        attributesHousehold = ResourceUtil.getArray(rb, PROPERTIES_HOUSEHOLD_ATTRIBUTES); //attributes are decided on the properties file
        marginalsHouseholdMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_MARGINALS_HOUSEHOLD_MATRIX)); //all the marginals from the region
        marginalsHouseholdMatrix.buildIndex(marginalsHouseholdMatrix.getColumnPosition("ID_city"));
        int[] microDataIds = frequencyMatrix.getColumnAsInt("ID");
        frequencyMatrix.buildIndex(frequencyMatrix.getColumnPosition("ID"));


        //Read the attributes at the county level (Landkreise) - Region attributes
        attributesRegion = ResourceUtil.getArray(rb, PROPERTIES_REGION_ATTRIBUTES); //attributes are decided on the properties file
        marginalsRegionMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_MARGINALS_REGIONAL_MATRIX)); //all the marginals from the region
        marginalsRegionMatrix.buildIndex(marginalsRegionMatrix.getColumnPosition("ID_county"));


        //Obtain the municipalities and counties that are used for IPU (they have Selected = 1 on the Municipalities List)
        municipalitiesMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_SELECTED_MUNICIPALITIES_LIST)); //array with all municipalities
        int municipalitiesCounts = 0;
        int countiesCounts = 0;
        if (municipalitiesMatrix.getValueAt(1,"Select") == 1f){
            municipalitiesCounts++;
            countiesCounts++;
        }
        for (int row = 2; row <= municipalitiesMatrix.getRowCount(); row++){
            if (municipalitiesMatrix.getValueAt(row,"Select") == 1f){
                municipalitiesCounts++;
                if (municipalitiesMatrix.getValueAt(row,"ID_county") != municipalitiesMatrix.getValueAt(row - 1,"ID_county")){
                    countiesCounts++;
                }
            }
        }
        cityID = new int[municipalitiesCounts];
        cityIDs = new String[municipalitiesCounts];
        countyID = new int [countiesCounts];
        countyIDs = new String[countiesCounts];
        int [] citiesCountyAux = new int[countiesCounts + 1];
        int [] citiesCounty = new int [countiesCounts];
        int [] cityCountyInitial = new int[countiesCounts];
        int citiesCounter = 0;
        int rowID = 0;
        int rowIDcounty = 0;
        if (municipalitiesMatrix.getValueAt(1,"Select") == 1f){
            cityID[rowID] = (int) municipalitiesMatrix.getValueAt(1,"ID_city");
            cityIDs[rowID] = Integer.toString(cityID[rowID]);
            cityCountyInitial[rowIDcounty] = rowID;
            rowID++;
            citiesCounter++;
            countyID[rowIDcounty] = (int) municipalitiesMatrix.getValueAt(1,"ID_county");
            countyIDs[rowIDcounty] = Integer.toString(countyID[rowIDcounty]);
            rowIDcounty++;
        }
        for (int row = 2; row <= municipalitiesMatrix.getRowCount(); row++){
            if (municipalitiesMatrix.getValueAt(row,"Select") == 1f){
                cityID[rowID] = (int) municipalitiesMatrix.getValueAt(row,"ID_city");
                cityIDs[rowID] = Integer.toString(cityID[rowID]);
                rowID++;
                citiesCounter++;
                if (municipalitiesMatrix.getValueAt(row,"ID_county") != municipalitiesMatrix.getValueAt(row - 1,"ID_county")){
                    countyID[rowIDcounty] = (int) municipalitiesMatrix.getValueAt(row,"ID_county");
                    countyIDs[rowIDcounty] = Integer.toString(countyID[rowIDcounty]);
                    cityCountyInitial[rowIDcounty] = rowID - 1;
                    rowIDcounty++;
                    citiesCountyAux[rowIDcounty - 1] = citiesCounter;
                    citiesCounter = 0;
                }
            }
        }
        citiesCountyAux[rowIDcounty] = citiesCounter + 1;
        for (int row = 0; row < countyID.length; row++){
            citiesCounty[row] = citiesCountyAux[row + 1];
        }


        //Create the collapsed version of the frequency matrix(common for all)
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


        //For each county, we perform IPU
        for (int county = 0; county < countyID.length; county++){

            logger.info("   Starting IPU at municipality " + countyID[county]);

            //-----------***** Data preparation *****-------------------------------------------------------------------
            //Create local variables to avoid accessing to the same variable on the parallel processing
            int [] municipalitiesID = new int[citiesCounty[county]]; //municipalities ID. Only reads the municipalities that have been selected on the list.
            for (int municipality = 0; municipality < municipalitiesID.length; municipality++){
                municipalitiesID[municipality] = cityID[cityCountyInitial[county] + municipality];
            }
            String[] municipalitiesIDs = new String[municipalitiesID.length];
            for (int row = 0; row < municipalitiesID.length; row++){municipalitiesIDs[row] = Integer.toString(municipalitiesID[row]);}
            int regionID = countyID[county];
            String[] attributesHouseholdList = attributesHousehold; //List of attributes at the household level (Gemeinden).
            String[] attributesRegionList = attributesRegion; //List of attributes at the region level (Landkreise).
            TableDataSet microDataMatrix = new TableDataSet(); //Frequency matrix obtained from the micro data.
            microDataMatrix = frequencyMatrix;
            TableDataSet collapsedMicroData = new TableDataSet(); //List of values different than zero, per attribute, from microdata
            collapsedMicroData = nonZero;
            TableDataSet lengthMicroData = new TableDataSet(); //Number of values different than zero, per attribute, from microdata
            lengthMicroData = nonZeroSize;

            //weights: TableDataSet with (one + number of municipalities) columns, the ID of the household from microData and the weights for the municipalities of the county
            TableDataSet weights = new TableDataSet();
            weights.appendColumn(microDataIds,"ID");
            for (int municipality = 0; municipality < municipalitiesID.length; municipality++){
                double[] dummy20 = SiloUtil.createArrayWithValue(microDataMatrix.getRowCount(),1.0);
                weights.appendColumn(dummy20, municipalitiesIDs[municipality]); //the column label is the municipality cityID
            }
            weights.buildIndex(weights.getColumnPosition("ID"));
            TableDataSet minWeights = new TableDataSet();
            minWeights.appendColumn(microDataIds,"ID");
            for (int municipality = 0; municipality < municipalitiesID.length; municipality++){
                double[] dummy20 = SiloUtil.createArrayWithValue(microDataMatrix.getRowCount(),1.0);
                minWeights.appendColumn(dummy20, municipalitiesIDs[municipality]); //the column label is the municipality cityID
            }
            minWeights.buildIndex(weights.getColumnPosition("ID"));


            //marginalsRegion: TableDataSet that contains in each column the marginal of a region attribute at the county level. Only one "real"" row
            TableDataSet marginalsRegion = new TableDataSet();
            int[] dummyw10 = {regionID,0};
            marginalsRegion.appendColumn(dummyw10,"ID_county");
            for(int attribute = 0; attribute < attributesRegionList.length; attribute++){
                float[] dummyw11 = {marginalsRegionMatrix.getValueAt(
                        marginalsRegionMatrix.getIndexedRowNumber(regionID),attributesRegionList[attribute]),0};
                marginalsRegion.appendColumn(dummyw11,attributesRegionList[attribute]);
            }
            marginalsRegion.buildIndex(marginalsRegion.getColumnPosition("ID_county"));


            //weighted sum and errors Region: TableDataSet that contains in each column the error of a region attribute at the county (landkreise) level
            TableDataSet errorsRegion = new TableDataSet();
            int[] dummy01 = {regionID,0};
            errorsRegion.appendColumn(dummy01,"ID_county");
            for (int attribute = 0; attribute < attributesRegionList.length; attribute++){
                float[] dummyQ2 = {0,0};
                errorsRegion.appendColumn(dummyQ2,attributesRegionList[attribute]);
            }
            errorsRegion.buildIndex(errorsRegion.getColumnPosition("ID_county"));


            //Calculate the first set of weighted sums and errors, using initial weights equal to 1
            for (int attribute = 0; attribute < attributesRegionList.length; attribute++){
                float weighted_sum = 0f;
                for (int municipality = 0; municipality < municipalitiesID.length;municipality++) {
                    int positions = (int) lengthMicroData.getValueAt(1, attributesRegionList[attribute]);
                    weighted_sum = SiloUtil.getWeightedSum(weights.getColumnAsDouble(municipalitiesIDs[municipality]),
                            microDataMatrix.getColumnAsFloat(attributesRegionList[attribute]),
                            collapsedMicroData.getColumnAsInt(attributesRegionList[attribute]), positions);
                }
                float error = Math.abs((weighted_sum -
                        marginalsRegion.getIndexedValueAt(regionID, attributesRegionList[attribute])) /
                        marginalsRegion.getIndexedValueAt(regionID, attributesRegionList[attribute]));
                errorsRegion.setIndexedValueAt(regionID, attributesRegionList[attribute], error);
            }


            //marginalsHousehold: TableDataSet that contains in each column the marginal of a household attribute at the municipality level. As many rows as municipalities on the county
            TableDataSet marginalsHousehold = new TableDataSet();
            marginalsHousehold.appendColumn(municipalitiesID,"ID_city");
            for (int attribute = 0; attribute < attributesHouseholdList.length; attribute++){
                float[] dummyw12 = new float[municipalitiesID.length];
                for (int municipality = 0; municipality < municipalitiesID.length; municipality++){
                    dummyw12[municipality] = marginalsHouseholdMatrix.getValueAt(
                            marginalsHouseholdMatrix.getIndexedRowNumber(municipalitiesID[municipality]),attributesHouseholdList[attribute]);
                }
                marginalsHousehold.appendColumn(dummyw12,attributesHouseholdList[attribute]);
            }
            marginalsHousehold.buildIndex(marginalsHousehold.getColumnPosition("ID_city"));


            //weighted sum and errors Household: TableDataSet that contains in each column the error of a household attribute at the municipality level. As many rows as municipalities on the county
            TableDataSet errorsHousehold = new TableDataSet();
            errorsHousehold.appendColumn(municipalitiesID,"ID_city");
            for(int attribute = 0; attribute < attributesHouseholdList.length; attribute++){
                float[] dummyB2 = SiloUtil.createArrayWithValue(marginalsHousehold.getRowCount(),0f);
                errorsHousehold.appendColumn(dummyB2,attributesHouseholdList[attribute]);
            }
            errorsHousehold.buildIndex(errorsHousehold.getColumnPosition("ID_city"));


            //Calculate the first set of weighted sums and errors, using initial weights equal to 1
            for(int attribute = 0; attribute < attributesHouseholdList.length; attribute++) {
                for (int municipality = 0; municipality < municipalitiesID.length; municipality++) {
                    int positions = (int) lengthMicroData.getValueAt(1, attributesHouseholdList[attribute]);
                    float weighted_sum = SiloUtil.getWeightedSum(weights.getColumnAsDouble(municipalitiesIDs[municipality]),
                            microDataMatrix.getColumnAsFloat(attributesHouseholdList[attribute]),
                            collapsedMicroData.getColumnAsInt(attributesHouseholdList[attribute]),positions);
                    float error = Math.abs((weighted_sum -
                            marginalsHousehold.getIndexedValueAt(municipalitiesID[municipality], attributesHouseholdList[attribute])) /
                            marginalsHousehold.getIndexedValueAt(municipalitiesID[municipality], attributesHouseholdList[attribute]));
                    errorsHousehold.setIndexedValueAt(municipalitiesID[municipality],attributesHouseholdList[attribute],error);
                }
            }


            //Stopping criteria (common for all municipalities)
            int maxIterations= ResourceUtil.getIntegerProperty(rb, PROPERTIES_MAX_ITERATIONS,1000);
            double maxError = ResourceUtil.getDoubleProperty(rb, PROPERTIES_MAX_ERROR, 0.0001);
            double improvementError = ResourceUtil.getDoubleProperty(rb, PROPERTIES_IMPROVEMENT_ERROR, 0.001);
            double iterationError = ResourceUtil.getDoubleProperty(rb,PROPERTIES_IMPROVEMENT_ITERATIONS,2);
            double increaseError = ResourceUtil.getDoubleProperty(rb,PROPERTIES_INCREASE_ERROR,1.05);
            double initialError = ResourceUtil.getDoubleProperty(rb, PROPERTIES_INITIAL_ERROR, 1000);


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
                for (int attribute = 0; attribute < attributesRegionList.length; attribute++) {
                    float weighted_sum = 0f;
                    for (int municipality = 0; municipality < municipalitiesID.length; municipality++) {
                        weighted_sum = weighted_sum + SiloUtil.getWeightedSum(weights.getColumnAsDouble(municipalitiesIDs[municipality]),
                                microDataMatrix.getColumnAsFloat(attributesRegionList[attribute]),
                                collapsedMicroData.getColumnAsInt(attributesRegionList[attribute]),
                                (int) lengthMicroData.getValueAt(1, attributesRegionList[attribute]));
                    }
                    factor = marginalsRegion.getIndexedValueAt(regionID, attributesRegionList[attribute]) /
                            weighted_sum;
                    for (int municipality = 0; municipality < municipalitiesID.length; municipality++) {
                        for (int row = 0; row < lengthMicroData.getValueAt(1, attributesRegionList[attribute]); row++) {
                            position = (int) collapsedMicroData.getIndexedValueAt(microDataIds[row], attributesRegionList[attribute]);
                            float previous_weight = weights.getValueAt(position, municipalitiesIDs[municipality]);
                            weights.setValueAt(position, municipalitiesIDs[municipality], factor * previous_weight);
                        }
                    }
                }


                //For each attribute at the municipality level
                for(int attribute = 0; attribute < attributesHouseholdList.length; attribute++) {
                    //logger.info("       Iteration: "+ iteration + ". Starting to calculate weight of the attribute " + attribute + " at the household level.");
                    //update the weights according to the weighted sum and constraint of the household attribute
                    for (int municipality = 0; municipality < municipalitiesID.length; municipality++) {
                        float weighted_sum = SiloUtil.getWeightedSum(weights.getColumnAsDouble(municipalitiesIDs[municipality]),
                                microDataMatrix.getColumnAsFloat(attributesHouseholdList[attribute]),
                                collapsedMicroData.getColumnAsInt(attributesHouseholdList[attribute]),
                                (int) lengthMicroData.getValueAt(1, attributesHouseholdList[attribute]));
                        factor = marginalsHousehold.getIndexedValueAt(municipalitiesID[municipality], attributesHouseholdList[attribute]) /
                                weighted_sum;
                        for (int row = 0; row < lengthMicroData.getValueAt(1, attributesHouseholdList[attribute]); row++) {
                            position = (int) collapsedMicroData.getIndexedValueAt(microDataIds[row], attributesHouseholdList[attribute]);
                            float previous_weight = weights.getValueAt(position, municipalitiesIDs[municipality]);
                            weights.setValueAt(position, municipalitiesIDs[municipality], factor * previous_weight);
                        }
                    }
                }


                //update the weighted sums and errors of the region attributes, given the new weights
                for (int attributes = 0; attributes < attributesRegionList.length; attributes++) {

                    float weighted_sum = 0f;
                    for (int municipality = 0; municipality < municipalitiesID.length; municipality++) {
                        weighted_sum = weighted_sum + SiloUtil.getWeightedSum(weights.getColumnAsDouble(municipalitiesIDs[municipality]),
                                microDataMatrix.getColumnAsFloat(attributesRegionList[attributes]),
                                collapsedMicroData.getColumnAsInt(attributesRegionList[attributes]),
                                (int) lengthMicroData.getValueAt(1, attributesRegionList[attributes]));
                    }
                    error = Math.abs((weighted_sum -
                            marginalsRegion.getValueAt(1, attributesRegionList[attributes])) /
                            marginalsRegion.getValueAt(1, attributesRegionList[attributes]));
                    errorsRegion.setIndexedValueAt(regionID, attributesRegionList[attributes], error);
                }


                //update the weighted sums and errors of the household attributes, given the new weights
                for (int attributes = 0; attributes < attributesHouseholdList.length; attributes++) {
                    //logger.info("   Iteration: "+ iteration + ". Updating weighted sums of " + attributes + " at the household level");
                    for (int municipality = 0; municipality < municipalitiesID.length; municipality++) {
                        float weighted_sum = SiloUtil.getWeightedSum(weights.getColumnAsDouble(municipalitiesIDs[municipality]),
                                microDataMatrix.getColumnAsFloat(attributesHouseholdList[attributes]),
                                collapsedMicroData.getColumnAsInt(attributesHouseholdList[attributes]),
                                (int) lengthMicroData.getValueAt(1, attributesHouseholdList[attributes]));
                        error = Math.abs((weighted_sum -
                                marginalsHousehold.getIndexedValueAt(municipalitiesID[municipality], attributesHouseholdList[attributes])) /
                                marginalsHousehold.getIndexedValueAt(municipalitiesID[municipality], attributesHouseholdList[attributes]));
                        errorsHousehold.setIndexedValueAt(municipalitiesID[municipality], attributesHouseholdList[attributes], error);
                    }
                }


                //Calculate the average error among all the attributes (area and municipalities level). This will serve as one stopping criteria
                int attributesCounter = 1;
                averageErrorIteration = errorsRegion.getIndexedValueAt(regionID, attributesRegionList[0]);
                for (int attribute = 1; attribute < attributesRegionList.length; attribute++) {
                    averageErrorIteration = averageErrorIteration + errorsRegion.getIndexedValueAt(regionID, attributesRegionList[attribute]);
                    attributesCounter++;
                }
                for(int attribute = 0; attribute < attributesHouseholdList.length; attribute++){
                    averageErrorIteration = averageErrorIteration +
                            errorsHousehold.getIndexedValueAt(municipalitiesID[0], attributesHouseholdList[0]);
                    attributesCounter++;
                    for(int municipality = 0; municipality < municipalitiesID.length; municipality++) {
                        averageErrorIteration = averageErrorIteration +
                                errorsHousehold.getIndexedValueAt(municipalitiesID[municipality], attributesHouseholdList[attribute]);
                        attributesCounter++;
                    }
                }
                averageErrorIteration = averageErrorIteration / attributesCounter;
                logger.info("   County " + regionID + ". Iteration " + iteration + ". Average error: " +  averageErrorIteration * 100 + " %.");


                //Stopping criteria: exceeds the maximum number of iterations or the maximum error is lower than the threshold
                if (averageErrorIteration < maxError){
                    finish = 1;
                    logger.info("   IPU finished after :" + iteration + " iterations with a minimum average error of: " + minError * 100 + " %.");
                    iteration = maxIterations + 1;
                }
                else if ((iteration/iterationError) % 1 == 0){
                    if (Math.abs((initialError-averageErrorIteration)/initialError) < improvementError) {
                        finish = 1;
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
            }
            String freqFileName = ("scenOutput/weigthsMatrix.csv");
            SiloUtil.writeTableDataSet(weightsMatrix, freqFileName);

            logger.info("   IPU finished");
        }


        //Write the weights final table
        weightsTable = weightsMatrix;
        weightsTable.buildIndex(weightsTable.getColumnPosition("ID"));
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


        //Read the attributes at the county level (Landkreise)
        attributesRegion = ResourceUtil.getArray(rb, PROPERTIES_REGION_ATTRIBUTES); //attributes are decided on the properties file
        marginalsRegionMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_MARGINALS_REGIONAL_MATRIX)); //all the marginals from the region
        marginalsRegionMatrix.buildIndex(marginalsRegionMatrix.getColumnPosition("ID_county"));


        //List of municipalities that are used for IPU and Synthetic population
        municipalitiesMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_SELECTED_MUNICIPALITIES_LIST)); //array with all municipalities
        int municipalitiesCount = 0;
        for (int row = 1; row <= municipalitiesMatrix.getRowCount(); row++){
            if (municipalitiesMatrix.getValueAt(row,"Select") == 1f){
                municipalitiesCount++;
            }
        }
        cityID = new int[municipalitiesCount];
        cityIDs = new String[municipalitiesCount];
        int rowID = 0;
        for (int row = 1; row <= municipalitiesMatrix.getRowCount(); row++){
            if (municipalitiesMatrix.getValueAt(row,"Select") == 1f){
                cityID[rowID] = (int) municipalitiesMatrix.getValueAt(row,"ID_city");
                cityIDs[rowID] = Integer.toString(cityID[rowID]);
                rowID++;
            }
        }

        logger.info("   Finishing reading the results from the IPU");
    }


    private void selectHouseholds(){
        //Generate the synthetic population using Monte Carlo (select the households according to the weight)
        //Once the household is selected, all the characteristics of the household will be copied (including the household members)
        logger.info("   Starting to generate households and persons.");

        //List of raster cells
        cellsMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_RASTER_CELLS));
        int[] rasterCellsID = cellsMatrix.getColumnAsInt("ID_cell");
        String[] rasterCellsIDs = new String[rasterCellsID.length];
        for (int row = 0; row < rasterCellsID.length; row++){rasterCellsIDs[row] = Integer.toString(rasterCellsID[row]);}
        cellsMatrix.buildIndex(cellsMatrix.getColumnPosition("ID_cell"));


        //List of households of the micro data
        int[] microDataIds = frequencyMatrix.getColumnAsInt("ID");
        int previousHouseholds = 0;
        int previousPersons = 0;


        //Define car probability
        //They depend on household size. The probability is for all Bavaria



        //Define income distribution
        double incomeShape = ResourceUtil.getDoubleProperty(rb,PROPERTIES_INCOME_GAMMA_SHAPE);
        double incomeRate = ResourceUtil.getDoubleProperty(rb,PROPERTIES_INCOME_GAMMA_RATE);
        double[] incomeProbability = ResourceUtil.getDoubleArray(rb,PROPERTIES_INCOME_GAMMA_PROBABILITY);
        GammaDistributionImpl gammaDist = new GammaDistributionImpl(incomeShape, 1/incomeRate);





        //Errors of the synthetic population
        TableDataSet errorsSynPop = new TableDataSet();
        TableDataSet relativeErrorSynPop = new TableDataSet();
        errorsSynPop.appendColumn(cityID,"ID_city");
        relativeErrorSynPop.appendColumn(cityID,"ID_city");
        for(int attribute = 0; attribute < attributesHousehold.length; attribute++) {
            double[] dummy2 = SiloUtil.createArrayWithValue(cityIDs.length,0.0);
            double[] dummy3 = SiloUtil.createArrayWithValue(cityIDs.length,0.0);
            errorsSynPop.appendColumn(dummy2, attributesHousehold[attribute]);
            relativeErrorSynPop.appendColumn(dummy3,attributesHousehold[attribute]);
        }
        errorsSynPop.buildIndex(errorsSynPop.getColumnPosition("ID_city"));
        relativeErrorSynPop.buildIndex(relativeErrorSynPop.getColumnPosition("ID_city"));


        //Selection of households, persons, jobs and dwellings per municipality
        for (int municipality = 0; municipality < cityID.length; municipality++){

            //obtain the raster cells of the municipality and their weight within the municipality
            int rasterCount = 0;
            for (int row = 1; row <= cellsMatrix.getRowCount(); row++){
                if ((int) cellsMatrix.getValueAt(row,"ID_city") == cityID[municipality]){
                    rasterCount++;
                }
            }
            int[] rasterCellsList = new int[rasterCount];
            int finish = 0;
            int rowAux = 1;
            int rasterCellCountsAux = 0;
            float rasterPopulation = 0;
            float[] rasterCellsWeight = new float[rasterCount];
            while (finish == 0){
                if ((int) cellsMatrix.getValueAt(rowAux,"ID_city") == cityID[municipality]){
                    rasterCellsList[rasterCellCountsAux] = (int) cellsMatrix.getValueAt(rowAux,"ID_cell");
                    rasterCellsWeight[rasterCellCountsAux] = cellsMatrix.getValueAt(rowAux,"Population");
                    rasterPopulation = rasterPopulation + cellsMatrix.getValueAt(rowAux,"Population");
                    rasterCellCountsAux++;
                }
                if (rasterCellCountsAux == rasterCellsList.length){
                    finish = 1;
                }
                rowAux++;
            }
            for (int row = 0; row < rasterCellsList.length; row++){
                rasterCellsWeight[row] = rasterCellsWeight[row] / rasterPopulation;
            }


            //select the probabilities of the households from the microData, for that municipality
            int totalHouseholds = (int) marginalsHouseholdMatrix.getIndexedValueAt(cityID[municipality],"hhTotal") +
                    (int) marginalsHouseholdMatrix.getIndexedValueAt(cityID[municipality],"privateQuarters");
            double[] probability = weightsTable.getColumnAsDouble(cityIDs[municipality]);
            //probability = SiloUtil.convertProbability(probability); // I use the weight as it comes. Do not scale to percentage.


            //marginals for the municipality
            int maleWorkers = 0;
            int femaleWorkers = 0;
            int hhMaleAge[] = new int[ageBracketsPerson.length];
            int hhFemaleAge[] = new int[ageBracketsPerson.length];
            int quartersMaleAge[] = new int[ageBracketsPersonQuarter.length];
            int quartersFemaleAge[] = new int[ageBracketsPersonQuarter.length];
            int hhSize1 = 0;
            int hhSize2 = 0;
            int hhSize3 = 0;
            int hhSize4 = 0;
            int hhSize5 = 0;
            int hhSize6 = 0;
            int hhTotal = 0;
            int quartersTotal = 0;
            int hhForeigners = 0;
            int hhPersons = 0;

            //for all the households that are inside the municipality (we will match perfectly the number of households. The total population will vary compared to the marginals.)
            for (int row = 0; row < totalHouseholds; row++) {

                //select the household to copy and allocate it
                int[] records = select(probability, microDataIds);
                int idHHmicroData = records[0];
                int recordHHmicroData = records[1];
                int householdCell = SiloUtil.select(rasterCellsWeight,rasterCellsWeight.length,rasterCellsList);


                //copy the household characteristics
                int householdSize = (int) microDataHousehold.getIndexedValueAt(idHHmicroData, "hhSize");
                int householdWorkers = (int) microDataHousehold.getIndexedValueAt(idHHmicroData, "femaleWorkers") +
                        (int) microDataHousehold.getIndexedValueAt(idHHmicroData, "maleWorkers");
                int householdPrivate = (int) microDataHousehold.getIndexedValueAt(idHHmicroData,"groupQuarters");
                int id = HouseholdDataManager.getNextHouseholdId();
                if (householdPrivate == 0) { //Private household
                    new Household(id, idHHmicroData, householdCell, householdSize, householdWorkers); //(int id, int dwellingID, int homeZone, int hhSize, int autos)
                    hhTotal++;
                    if (householdSize == 1) {
                        hhSize1++;
                    } else if (householdSize == 2) {
                        hhSize2++;
                    } else if (householdSize == 3) {
                        hhSize3++;
                    } else if (householdSize == 4) {
                        hhSize4++;
                    } else if (householdSize == 5) {
                        hhSize5++;
                    } else {
                        hhSize6++;
                    }
                } else { //Person in group quarter. The group quarter is allocated at the AGS of the municipality for the moment
                    new Household(id, idHHmicroData, cityID[municipality], householdSize, householdWorkers); //(int id, int dwellingID, int homeZone, int hhSize, int autos)
                    quartersTotal++;
                }

                //copy all the persons of the household
                for (int rowPerson = 0; rowPerson < householdSize; rowPerson++) {
                    int idPerson = HouseholdDataManager.getNextPersonId();
                    int personCounter = (int) microDataHousehold.getIndexedValueAt(idHHmicroData, "personCount") + rowPerson;
                    int age = (int) microDataPerson.getValueAt(personCounter, "age");
                    int gender = (int) microDataPerson.getValueAt(personCounter, "gender");
                    int occupation = (int) microDataPerson.getValueAt(personCounter, "occupation");
                    int income = 0;
                    try {
                        income = (int) translateIncome((int) microDataPerson.getValueAt(personCounter, "income"),incomeProbability, gammaDist);
                    } catch (MathException e) {
                        e.printStackTrace();
                    }
                    //int workplace = (int) microDataPerson.getValueAt(personCounter,"workplace"); It will be linked after using the trip length distribution
                    if (microDataPerson.getValueAt(personCounter,"nationality") == 8) { //race is equal to other if the person is foreigner.
                        new Person(idPerson, id, age, gender, Race.other, occupation, 0, income); //(int id, int hhid, int age, int gender, Race race, int occupation, int workplace, int income)
                    } else {
                        new Person(idPerson, id, age, gender, Race.white, occupation, 0, income); //(int id, int hhid, int age, int gender, Race race, int occupation, int workplace, int income)
                    }
/*                  Occupation will be generated later using another process
                    if (occupation == 1){
                        //We generate a new job because the person is employed
                        int idJob = JobDataManager.getNextJobId();
                        int jobPerson = translateJobType((int) microDataPerson.getValueAt(personCounter,"jobSector"),jobsTable); //Already translated to the 10 types of employment

                        //new Job(idJob,workplace,-1,JobType.getJobType(jobPerson)); //TODO. Understand job types and how to generate them. We will have data from 10 types of employment
                    }*/
                    hhPersons++;
                    if (microDataPerson.getValueAt(personCounter,"nationality") == 8){
                        hhForeigners++;
                    }
                    if (gender == 1){
                        if (occupation == 1){
                            maleWorkers++;
                        }
                    } else {
                        if (occupation == 1){
                            femaleWorkers++;
                        }
                    }
                    if (householdPrivate == 0){
                        int row1 = 0;
                        while (age > ageBracketsPerson[row1]){
                            row1++;
                        }
                        if (gender == 1){
                            hhMaleAge[row1]++;
                        } else {
                            hhFemaleAge[row1]++;
                        }

                    } else {
                        int row2 = 0;
                        while (age > ageBracketsPersonQuarter[row2]){
                            row2++;
                        }
                        if (gender == 1){
                            quartersMaleAge[row2]++;
                        } else {
                            quartersFemaleAge[row2]++;
                        }
                    }
                }


                //Copy the dwelling of that household
                int newDdId = RealEstateDataManager.getNextDwellingId();
                int pumsDdType = (int) microDataHousehold.getIndexedValueAt(idHHmicroData, "hhDwellingType");
                DwellingType ddType = translateDwellingType(pumsDdType);
                int bedRooms = 1; //marginal data at the municipality level
                int quality = 1; //depend on complete plumbing, complete kitchen and year built.
                int price = 1; //not significant at this point
                int year = 2000; //not significant at this point
                new Dwelling(newDdId, householdCell, id, ddType, bedRooms, quality, price, 0, year); //newDwellingId, raster cell, HH Id, ddType, bedRooms, quality, price, restriction, construction year


/*           //update the probability of the record of being selected on the next draw. It increases the error on population due to round-up errors
                if (probability[recordHHmicroData] > 1) {
                    probability[recordHHmicroData] = probability[recordHHmicroData] - 1;
                } else {
                    probability[recordHHmicroData] = 0;
                }*/
            }
            int households = HouseholdDataManager.getHighestHouseholdIdInUse()-previousHouseholds;
            int persons = HouseholdDataManager.getHighestPersonIdInUse()-previousPersons;

            previousHouseholds = HouseholdDataManager.getHighestHouseholdIdInUse();
            previousPersons = HouseholdDataManager.getHighestPersonIdInUse();


            //Check the error for each attribute, relative and absolute
            errorsSynPop.setIndexedValueAt(cityID[municipality],"hhTotal",hhTotal);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"hhSize1",hhSize1);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"hhSize2",hhSize2);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"hhSize3",hhSize3);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"hhSize4",hhSize4);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"hhSize5",hhSize5);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"hhSize6",hhSize6);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"population",hhPersons);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"foreigners",hhForeigners);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"maleWorkers",maleWorkers);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"femaleWorkers",femaleWorkers);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"male4",hhMaleAge[0]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"male9",hhMaleAge[1]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"male14",hhMaleAge[2]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"male19",hhMaleAge[3]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"male24",hhMaleAge[4]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"male29",hhMaleAge[5]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"male34",hhMaleAge[6]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"male39",hhMaleAge[7]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"male44",hhMaleAge[8]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"male49",hhMaleAge[9]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"male54",hhMaleAge[10]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"male59",hhMaleAge[11]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"male64",hhMaleAge[12]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"male69",hhMaleAge[13]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"male74",hhMaleAge[14]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"male79",hhMaleAge[15]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"male99",hhMaleAge[16]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"female4",hhFemaleAge[0]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"female9",hhFemaleAge[1]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"female14",hhFemaleAge[2]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"female19",hhFemaleAge[3]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"female24",hhFemaleAge[4]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"female29",hhFemaleAge[5]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"female34",hhFemaleAge[6]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"female39",hhFemaleAge[7]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"female44",hhFemaleAge[8]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"female49",hhFemaleAge[9]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"female54",hhFemaleAge[10]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"female59",hhFemaleAge[11]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"female64",hhFemaleAge[12]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"female69",hhFemaleAge[13]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"female74",hhFemaleAge[14]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"female79",hhFemaleAge[15]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"female99",hhFemaleAge[16]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"maleQuarters14",quartersMaleAge[0]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"maleQuarters29",quartersMaleAge[1]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"maleQuarters64",quartersMaleAge[2]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"maleQuarters99",quartersMaleAge[3]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"femaleQuarters14",quartersFemaleAge[0]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"femaleQuarters29",quartersFemaleAge[1]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"femaleQuarters64",quartersFemaleAge[2]);
            errorsSynPop.setIndexedValueAt(cityID[municipality],"femaleQuarters99",quartersFemaleAge[3]);

            float averageError = 0f;
            for (int row = 0; row < attributesHousehold.length; row++){
                float error = errorsSynPop.getIndexedValueAt(cityID[municipality],attributesHousehold[row])-
                        marginalsHouseholdMatrix.getIndexedValueAt(cityID[municipality],attributesHousehold[row]);
                //errorsSynPop.setIndexedValueAt(cityID[municipality],attributesHousehold[row],error);
                float relError = Math.abs(error / marginalsHouseholdMatrix.getIndexedValueAt(cityID[municipality],attributesHousehold[row]));
                relativeErrorSynPop.setIndexedValueAt(cityID[municipality],attributesHousehold[row],relError);
                averageError = averageError + relError;
            }
            averageError = averageError / (1 + attributesHousehold.length) * 100;
            logger.info("   Municipality " + cityID[municipality]+ ". Generated " + persons + " persons in " + households + " households. Average error of " + averageError);

        }
        int households = HouseholdDataManager.getHighestHouseholdIdInUse();
        int persons = HouseholdDataManager.getHighestPersonIdInUse();
        logger.info("   Finished generating households and persons. A population of " + persons + " persons in " + households + " households was generated.");

        //Write the weights after finishing IPU for each municipality (saved each time over the previous version)
        String freqFileName = ("scenOutput/errorAbsSynPop.csv");
        SiloUtil.writeTableDataSet(errorsSynPop, freqFileName);
        String freqFileName2 = ("scenOutput/errorRelSynPop.csv");
        SiloUtil.writeTableDataSet(relativeErrorSynPop, freqFileName2);
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


    private static double translateIncome (int incomeClass, double[] incomeThresholds, GammaDistributionImpl q) throws MathException {
        //provide the income value for each person give the income class.
        //income follows a gamma distribution that was calibrated using the microdata. Income thresholds are calculated for the stiches
        double income;
        int finish = 0;
        double low = 0;
        double high = 1;
        if (incomeClass == 50) {
            income = -1; // Selbständige/r Landwirt/in in der Haupttätigkeit
        } else if (incomeClass == 90) {
            income = 0; // kein Einkommen
        } else if (incomeClass == 99) {
            income = -1; //keine Angabe
        } else {
            if (incomeClass == 1) {
                low = 0;
                high = incomeThresholds[0];
            } else if (incomeClass == incomeThresholds.length + 1) {
                low = incomeThresholds[incomeThresholds.length];
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
            Random rnd = new Random();
            double cummulativeProb = rnd.nextDouble()*(high - low) + low;
            //GammaDistributionImpl q = new GammaDistributionImpl(shape, 1/scale);
            income = q.inverseCumulativeProbability(cummulativeProb);
            //RandomDataImpl rng = new RandomDataImpl();
            //income = rng.nextGamma(shape,scale);
        }
        return income;
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
        double selPos = sumProb * SiloModel.rand.nextFloat();
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

}
