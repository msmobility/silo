package edu.umd.ncsg.SyntheticPopulationGenerator;

import com.google.common.collect.Table;
import com.pb.common.datafile.TableDataSet;
import com.pb.common.matrix.Matrix;
import com.pb.common.util.ResourceUtil;
import com.sun.org.apache.bcel.internal.generic.NEW;
import edu.umd.ncsg.SiloModel;
import edu.umd.ncsg.SiloUtil;
import edu.umd.ncsg.data.*;
import omx.OmxFile;
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
    protected static final String PROPERTIES_EDUCATION_DESCRIPTION        = "education.dictionary";
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
    protected static final String PROPERTIES_JOB_TYPES_DE                 = "employment.types";
    //Read the synthetic population
    protected static final String PROPERTIES_HOUSEHOLD_SYN_POP            = "household.file.asciiDE";
    protected static final String PROPERTIES_PERSON_SYN_POP               = "person.file.asciiDE";
    protected static final String PROPERTIES_DWELLING_SYN_POP             = "dwelling.file.asciiDE";
    protected static final String PROPERTIES_JOB_SYN_POP                  = "job.file.asciiDE";


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
    protected TableDataSet educationsTable;

    protected int maxIterations;
    protected double maxError;
    protected double initialError;
    protected double improvementError;
    protected double iterationError;

    Matrix travelTimeMatrix;
    Matrix numberOfTripsMatrix;


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
/*   if (ResourceUtil.getIntegerProperty(rb,PROPERTIES_RUN_IPU) == 1) {
        if (ResourceUtil.getIntegerProperty(rb, PROPERTIES_RUN_DEPENDENT) == 1) {
            runIPUAreaDependent(); //IPU fitting with two geographical resolutions
        } else {
            runIPUIndependent(); //IPU fitting with one geographical resolution. Each municipality is independent of others
        }
        //readTravelTimes();
        selectHouseholds(); //Monte Carlo selection process to generate the synthetic population. The synthetic dwellings will be obtained from the same microdata
    } else {
        readIPU();
        //readTravelTimes();
        //selectHouseholds();
    }*/
        //generateJobs();
        //assignJobs();
        testJobs();
        summarizeData.writeOutSyntheticPopulationProjectSeminar(rb, SiloUtil.getBaseYear());
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
        int hhRecord[] = new int[hhCountTotal];


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
                            hhId[hhCount] = hhCount + 100000;
                            hhRecord[hhCount] = convertToInteger(recString.substring(2, 11));
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
        microRecords.appendColumn(hhRecord,"record");
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
        int movedOut = 0;
        int hhmovedOut = 0;
        int ddIncomplete = 0;
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
                        if (convertToInteger(recString.substring(34,35)) == 1 & convertToInteger(recString.substring(491,493)) > -5) { //private households that has not moved in the last year
                            if (householdNumber != previousHouseholdNumber) {
                                if (convertToInteger(recString.substring(491,493)) == 9 || convertToInteger(recString.substring(493,495)) == 9 ||
                                        convertToInteger(recString.substring(500,502)) == 99 ){ //incomplete dwelling record (either number of apartments in the building, use or construction year
                                    ddIncomplete++;
                                } else {
                                    hhCountTotal++;
                                    personCountTotal++;
                                    previousHouseholdNumber = householdNumber; // Update the household number
                                }
                            } else if (householdNumber == previousHouseholdNumber) {
                                personCountTotal++;
                            }
                        } else if (convertToInteger(recString.substring(34,35)) == 2) { //group quarter
                            personCountTotal++;
                            hhCountTotal++;
                            quarterCountTotal++;
                            personQuarterCountTotal++;
                        } else {
                            movedOut++;
                            if (householdNumber != previousHouseholdNumber) {
                                hhmovedOut++;
                                previousHouseholdNumber = householdNumber;
                            }
                        }
                    default:
                        hhOutCountTotal++;
                        break;
                }
            }
            logger.info("  Read " + (personCountTotal - personQuarterCountTotal) + " person records in " +
                    (hhCountTotal - quarterCountTotal) + " private households and " + personQuarterCountTotal +
                    " person records in " + quarterCountTotal + " group quarters in Bavaria from file: " + pumsFileName);
            logger.info("   " + movedOut + " persons from Bavaria moved in the last year. " + hhmovedOut + " households are excluded from the analysis.");
            logger.info("   " + ddIncomplete + " incomplete dwelling records. They are excluded from the analysis.");
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
        int personEurostat[] = new int[personCountTotal];
        int personCount = 0;
        int personHHCount = 0;
        int foreignCount = 0;
        int hhCount = -1;
        jobsTable = SiloUtil.readCSVfile(rb.getString(PROPERTIES_JOB_DESCRIPTION));
        educationsTable = SiloUtil.readCSVfile(rb.getString(PROPERTIES_EDUCATION_DESCRIPTION));
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
        int hhRecord[] = new int[hhCountTotal];
        int personCounts[] = new int[hhCountTotal];
        int hhQuarters[] = new int[hhCountTotal];
        int quarterId[] = new int[hhCountTotal];
        int counterNonZero[] = new int[hhCountTotal]; //to give counts from 1 to hhCountTotal
        int householdNumber = 0;
        int quarterCounter = 0;
        //Dwelling variables
        yearBracketsDwelling = ResourceUtil.getIntegerArray(rb, PROPERTIES_MICRO_DATA_YEAR_DWELLING);
        sizeBracketsDwelling = ResourceUtil.getIntegerArray(rb, PROPERTIES_MICRO_DATA_FLOOR_SPACE_DWELLING);
        int dwOwned[] = new int[hhCountTotal];
        int dwRented[] = new int[hhCountTotal];
        int dwSmall[] = new int[hhCountTotal];
        int dwMedium[] = new int[hhCountTotal];
        int dwellingUsage[] = new int[hhCountTotal];
        int dwellingFloorSpace[] = new int[hhCountTotal];
        int dwellingYearConstruction[] = new int[hhCountTotal];
        int dwSmallYear[][] = new int[hhCountTotal][yearBracketsDwelling.length];
        int dwMediumYear[][] = new int[hhCountTotal][yearBracketsDwelling.length];
        int dwFloorSpace[][] = new int[hhCountTotal][sizeBracketsDwelling.length];
        int dwellingBuildingSize[] = new int[hhCountTotal];
        int dwellingRent[] = new int[hhCountTotal];
        ddIncomplete = 0;
        String personalIncome;
        String sector;


        try {
            BufferedReader in = new BufferedReader(new FileReader(pumsFileName));
            int previousHouseholdNumber = -1;
            while ((recString = in.readLine()) != null) {
                recCount++;
                int recLander = convertToInteger(recString.substring(0,2));
                switch (recLander) {
                    case 9: //Record from Bavaria //Record from Bavaria
                        int h1 = convertToInteger(recString.substring(2, 8));
                        int h2 = convertToInteger(recString.substring(8, 9));
                        int h3 = convertToInteger(recString.substring(9, 10));
                        int h4 = convertToInteger(recString.substring(10, 11));
                        if (h2 < 0) h2 = 0;
                        if (h3 < 0) h3 = 0;
                        if (h4 < 0) h4 = 0;
                        householdNumber = h1 * 1000 + h2 * 100 + h3 * 10 + h4;

                        if (convertToInteger(recString.substring(491, 493)) == -5) {
                            //They moved out last year and have no dwelling records.
                            //They are not considered.
                            previousHouseholdNumber = householdNumber;
                        } else {
                            if (convertToInteger(recString.substring(491, 493)) == 9 || convertToInteger(recString.substring(493, 495)) == 9 ||
                                    convertToInteger(recString.substring(500, 502)) == 99) {
                                //Incomplete dwelling record
                                ddIncomplete++;
                            } else {
                                if (householdNumber != previousHouseholdNumber & convertToInteger(recString.substring(34, 35)) == 1) {
                                    //Private household

                                    //Household characteristics
                                    hhCount++;
                                    counterNonZero[hhCount] = hhCount;
                                    h1 = convertToInteger(recString.substring(2, 8));
                                    h2 = convertToInteger(recString.substring(8, 9));
                                    h3 = convertToInteger(recString.substring(9, 10));
                                    h4 = convertToInteger(recString.substring(10, 11));
                                    if (h1 <= 0) h1 = 0;
                                    if (h2 <= 0) h2 = 0;
                                    if (h3 <= 0) h3 = 0;
                                    if (h4 <= 0) h4 = 0;
                                    //logger.info(h1 + " "+h2 + " "+h3 + " "+h4);
                                    householdNumber = h1 * 1000 + h2 * 100 + h3 * 10 + h4;
                                    hhId[hhCount] = hhCount + 100000;
                                    hhRecord[hhCount] = householdNumber;
                                    hhQuarters[hhCount] = 0; //private household
                                    hhSize[hhCount] = convertToInteger(recString.substring(26, 28));
                                    hhTotal[hhCount] = 1;
                                    if (hhSize[hhCount] == 1) {
                                        hhSingle[hhCount] = 1;
                                        hhSize1[hhCount] = 1;
                                        hhSizeCategory[hhCount] = "hhSize1";
                                    } else if (hhSize[hhCount] == 2) {
                                        hhSize2[hhCount] = 1;
                                        hhSizeCategory[hhCount] = "hhSize2";
                                    } else if (hhSize[hhCount] == 3) {
                                        hhSize3[hhCount] = 1;
                                        hhSizeCategory[hhCount] = "hhSize3";
                                    } else if (hhSize[hhCount] == 4) {
                                        hhSize4[hhCount] = 1;
                                        hhSizeCategory[hhCount] = "hhSize4";
                                    } else if (hhSize[hhCount] == 5) {
                                        hhSize5[hhCount] = 1;
                                        hhSizeCategory[hhCount] = "hhSize5";
                                    } else {
                                        hhSize6[hhCount] = 1;
                                        hhSizeCategory[hhCount] = "hhSize6";
                                    }
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
                                    dwellingBuildingSize[hhCount] = convertToInteger(recString.substring(491, 493)); // 1: 1-2 dwellings; 2: 3-6 dwellings, 3: 7-12 dwellings; 4: 13-20 dwellings, 5: 21+ dwellings, 9: not stated.
                                    dwellingUsage[hhCount] = convertToInteger(recString.substring(493, 495)); // 1: owner of the building, 2: owner of the apartment, 3: main tenant, 4: subtenant, 9: not stated.
                                    dwellingYearConstruction[hhCount] = convertToInteger(recString.substring(500, 502)); // Construction year. 1: before 1919, 2: 1919-1948, 3: 1949-1978, 4: 1979 - 1986; 5: 1987 - 1990; 6: 1991 - 2000; 7: 2001 - 2004; 8: 2005 - 2008, 9: 2009 or later, 99: not stated.
                                    dwellingFloorSpace[hhCount] = convertToInteger(recString.substring(495, 498)); // Size of the apartment in square meters (from 10 to 999).
                                    dwellingRent[hhCount] = convertToInteger(recString.substring(1081, 1085)); // Monthly rent, in euros (Bruttokaltmiete). For Gesamtmiete, 508-512
                                    if (dwellingYearConstruction[hhCount] > 0 & dwellingYearConstruction[hhCount] < 10) { //Only consider the ones with
                                        int row = 0;
                                        while (dwellingYearConstruction[hhCount] > yearBracketsDwelling[row]) {
                                            row++;
                                        }
                                        if (dwellingBuildingSize[hhCount] == 1) {
                                            dwSmallYear[hhCount][row] = 1;
                                            dwSmall[hhCount] = 1;
                                        } else if (dwellingBuildingSize[hhCount] > 1) {
                                            dwMediumYear[hhCount][row] = 1; //also includes the not stated.
                                            dwMedium[hhCount] = 1;
                                        }
                                    }
                                    if (dwellingUsage[hhCount] < 3 & dwellingUsage[hhCount] > 0) {
                                        dwOwned[hhCount] = 1;
                                        dwellingUsage[hhCount] = 1;
                                    } else if (dwellingUsage[hhCount] > 0) {
                                        dwRented[hhCount] = 1;
                                        dwellingUsage[hhCount] = 2;
                                    }
                                    int row1 = 0;
                                    while (dwellingFloorSpace[hhCount] > sizeBracketsDwelling[row1]) {
                                        row1++;
                                    }
                                    dwFloorSpace[hhCount][row1] = 1;

                                    //Update household number and person counters for the next private household
                                    previousHouseholdNumber = householdNumber;
                                    personHHCount = 0;
                                    foreignCount = 0;

                                } else if (convertToInteger(recString.substring(34, 35)) == 2) {
                                    //Group quarter
                                    hhCount++;
                                    counterNonZero[hhCount] = hhCount;
                                    quarterCounter++;
                                    hhSize[hhCount] = 1; //we put 1 instead of the quarter size because each person in group quarter has its own household
                                    dwellingBuildingSize[hhCount] = convertToInteger(recString.substring(491, 493)); // 1: 1-2 dwellings; 2: 3-6 dwellings: 3: 7-12 dwellings; 4: 13-20 dwellings, 5: 21+ dwellings, 9: not stated
                                    hhQuarters[hhCount] = 1; //group quarter
                                    hhSizeCategory[hhCount] = "group quarter";
                                    hhId[hhCount] = quarterCounter;
                                    quarterId[hhCount] = householdNumber;
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

                                    //dwelling characteristics are copied, but they should be equal to -1
                                    dwellingBuildingSize[hhCount] = convertToInteger(recString.substring(491, 493)); // 1: 1-2 dwellings; 2: 3-6 dwellings, 3: 7-12 dwellings; 4: 13-20 dwellings, 5: 21+ dwellings, 9: not stated, -1: group quarter.
                                    dwellingUsage[hhCount] = convertToInteger(recString.substring(493, 495)); // 1: owner of the building, 2: owner of the apartment, 3: main tenant, 4: subtenant, 9: not stated.
                                    dwellingYearConstruction[hhCount] = convertToInteger(recString.substring(500, 502)); // Construction year. 1: before 1919, 2: 1919-1948, 3: 1949-1978, 4: 1979 - 1986; 5: 1987 - 1990; 6: 1991 - 2000; 7: 2001 - 2004; 8: 2005 - 2008, 9: 2009 or later, 99: not stated.
                                    dwellingFloorSpace[hhCount] = convertToInteger(recString.substring(495, 498)); // Size of the apartment in square meters (from 10 to 999).
                                    dwellingRent[hhCount] = convertToInteger(recString.substring(1081, 1085)); // Monthly rent, in euros (Bruttokaltmiete). For Gesamtmiete, 508-512.
                                    if (dwellingUsage[hhCount] < 3 & dwellingUsage[hhCount] > 0) {
                                        dwellingUsage[hhCount] = 1;
                                    } else if (dwellingUsage[hhCount] < 5 & dwellingUsage[hhCount] > 0) {
                                        dwellingUsage[hhCount] = 2;
                                    }
                                    //All dwelling characteristics for IPU are for private households rather than group quarters.

                                    //Update household number and person counters for the next private household
                                    previousHouseholdNumber = householdNumber;
                                    personHHCount = 0;
                                    foreignCount = 0;
                                }

                                //Person characteristics
                                age[personCount] = convertToInteger(recString.substring(50, 52)); // 0 to 95. 95 includes 95+
                                gender[personCount] = convertToInteger(recString.substring(54, 55)); // 1: male; 2: female
                                occupation[personCount] = convertToInteger(recString.substring(32, 33)); // 1: employed, 2: unemployed, 3: unemployed looking for job, 4: children and retired
                                personId[personCount] = householdNumber * 100 + convertToInteger(recString.substring(11, 12)) * 10 + convertToInteger(recString.substring(12, 13));
                                personHH[personCount] = householdNumber;
                                personIncome[personCount] = convertToInteger(recString.substring(471, 473)); //Netto income in EUR, 24 categories. 50: from agriculture, 90: any income, 99: not stated
                                personNationality[personCount] = convertToInteger(recString.substring(370, 372)); // 1: only German, 2: dual German citizenship, 8: foreigner; (Marginals consider dual citizens as Germans)
                                personEurostat[personCount] = convertToInteger(recString.substring(35, 36)); //definition of person according to EuroStat.
                                if (occupation[personCount] == 1) { // Only employed persons respond to the sector
                                    personJobSector[personCount] = translateJobType(convertToInteger(recString.substring(163, 165)), jobsTable); //First two digits of the WZ08 job classification in Germany. They are converted to 10 job classes (Zensus 2011 - Erwerbstätige nach Wirtschaftszweig Wirtschafts(unter)bereiche)
                                } else {
                                    personJobSector[personCount] = 0;
                                }
                                personTelework[personCount] = convertToInteger(recString.substring(198, 200)); //If they telework
                                if (personTelework[personCount] < 0) {
                                    personTelework[personCount] = 0;
                                }
                                personQuarter[personCount] = convertToInteger(recString.substring(34, 35)); // 1: private household, 2: group quarter
/*                                int education = convertToInteger(recString.substring(323, 325)); // 0: without beruflichen Abschluss, 1: Lehre, Berufausbildung im dual System, 2: Fachschulabschluss, 3: Abschluss einer Fachakademie, 4: Fachhochschulabschluss, 5: Hochschulabschluss - Uni, 6: Promotion
                                if (education == 10) {
                                    personEducation[personCount] = 6;
                                } else if (education == 9) {
                                    personEducation[personCount] = 5;
                                } else if (education == 99) {
                                    personEducation[personCount] = 99;
                                } else if (education == 6) {
                                    personEducation[personCount] = 2;
                                } else if (education < 0) {
                                    personEducation[personCount] = 0;
                                } else if (education > 6) {
                                    personEducation[personCount] = 4;
                                } else {
                                    personEducation[personCount] = 1;
                                }*/
                                personEducation[personCount] = translateEducationLevel(convertToInteger(recString.substring(323, 325)),educationsTable); // 1: without beruflichen Abschluss, 2: Lehre, Berufausbildung im dual System, Fachschulabschluss, Abschluss einer Fachakademie, 3: Fachhochschulabschluss, 4: Hochschulabschluss - Uni, Promotion, 99: not stated
                                int marital = convertToInteger(recString.substring(59, 60)); //1: single, 2: married, 3: widowed, 4: divorced, 5: same sex marriage, 6: same sex widow, 7: same sex divorced
                                if (marital == 2) {
                                    personStatus[personCount] = 2;
                                } else {
                                    personStatus[personCount] = 1;
                                }
                                if (age[personCount] < 15) {
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
                                    if (personQuarter[personCount] == 1) {
                                        hhMaleAge[hhCount][row]++;
                                        if (occupation[personCount] == 1) {
                                            hhMaleWorkers[hhCount]++;
                                            hhWorkers[hhCount]++;
                                        }
                                    }
                                } else if (gender[personCount] == 2) {
                                    if (personQuarter[personCount] == 1) {
                                        hhFemaleAge[hhCount][row]++;
                                        if (occupation[personCount] == 1) {
                                            hhFemaleWorkers[hhCount]++;
                                            hhWorkers[hhCount]++;
                                        }
                                    }
                                }
                                if (personQuarter[personCount] > 1) { //person in group quarter only counts for age, but they don't have value for workers
                                    if (row1 == 0) {
                                        quarterMaleAge[hhCount][0]++;
                                    } else {
                                        if (gender[personCount] == 1) {
                                            quarterMaleAge[hhCount][1]++;
                                        } else {
                                            quarterFemaleAge[hhCount][1]++;
                                        }
                                    }
                                }
                                if (personNationality[personCount] == 8) {
                                    foreignCount++;
                                }
                                personCount++;
                                personHHCount++;
                            }
                        }
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + pumsFileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }

        //logger.info(hhId[0] + " " + hhId[hhId.length-1] + " " + householdNumber);

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
        microPersons.appendColumn(personEurostat,"euroStat");
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
        microRecords.appendColumn(hhSize,"hhSizeDeclared");
        microRecords.appendColumn(hhSizeCount,"hhSize");
        microRecords.appendColumn(personCounts,"personCount");
        microRecords.appendColumn(hhSizeCategory,"hhSizeCategory");
        microRecords.appendColumn(hhQuarters,"groupQuarters");
        microRecords.appendColumn(quarterId,"microRecord");
        microRecords.appendColumn(dwellingBuildingSize,"dwellingType"); //Number of dwellings in the building.
        microRecords.appendColumn(dwellingUsage,"dwellingUsage"); //Who lives on the dwelling: owner (1) or tenant (2)
        microRecords.appendColumn(dwellingYearConstruction,"dwellingYear"); //Construction year. It has the categories from the micro data
        microRecords.appendColumn(dwellingFloorSpace,"dwellingFloorSpace"); //Floor space of the dwelling
        microRecords.appendColumn(dwellingRent,"dwellingRentPrice"); //Rental price of the dwelling
        microRecords.appendColumn(hhRecord,"recordMicroData");
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
        microDwellings.appendColumn(dwellingBuildingSize,"dwellingType"); //Number of dwellings in the building.
        microDwellings.appendColumn(dwellingUsage,"dwellingUsage"); //Who lives on the dwelling: owner (1) or tenant (2)
        microDwellings.appendColumn(dwellingYearConstruction,"dwellingYear"); //Construction year. It has the categories from the micro data
        microDwellings.appendColumn(dwellingFloorSpace,"dwellingFloorSpace"); //Floor space of the dwelling
        microDwellings.appendColumn(dwellingRent,"dwellingRentPrice"); //Rental price of the dwelling
        microDataDwelling = microDwellings;
        microDataDwelling.buildIndex(microDataDwelling.getColumnPosition("dwellingID"));


        //Copy attributes to the frequency matrix (IPU)
        TableDataSet microRecords1 = new TableDataSet();
        microRecords1.appendColumn(hhId,"ID");
        microRecords1.appendColumn(counterNonZero,"IDnonZero");
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
        microRecords1.appendColumn(dwSmall,"smallDwellings");
        microRecords1.appendColumn(dwMedium,"mediumDwellings");
        frequencyMatrix = microRecords1;


        String hhFileName = ("scenOutput/microHouseholds.csv");
        SiloUtil.writeTableDataSet(microDataHousehold, hhFileName);

        String freqFileName = ("scenOutput/frequencyMatrix.csv");
        SiloUtil.writeTableDataSet(frequencyMatrix, freqFileName);

        String freqFileName1 = ("scenOutput/microPerson.csv");
        SiloUtil.writeTableDataSet(microDataPerson, freqFileName1);

        String freqFileName2 = ("scenOutput/microDwelling.csv");
        SiloUtil.writeTableDataSet(microDwellings, freqFileName2);

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
        int[] nonZeroIds = frequencyMatrix.getColumnAsInt("IDnonZero");
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
        nonZero.appendColumn(nonZeroIds,"IDnonZero");
        int[] dummy0 = {0,0};
        nonZeroSize.appendColumn(dummy0,"IDnonZero");
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
        nonZero.buildIndex(nonZero.getColumnPosition("IDnonZero"));
        nonZeroSize.buildIndex(nonZeroSize.getColumnPosition("IDnonZero"));


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
                        position = (int) collapsedMicroData.getIndexedValueAt(nonZeroIds[row], attributesHouseholdList[attribute]); // I changed from microdataIds to nonZeroIds because the code stopped.
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
                    //logger.info("   Error of " + error + " at attribute " + attributesHouseholdList[attributes]);
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

                //logger.info("       Iteration " + iteration + ". The average error is: " + averageErrorIteration * 100 + " %." );

            }

            //Copy the errors per attribute
            for(int attribute = 0; attribute < attributesHouseholdList.length; attribute++) {
                errorsMatrix.setValueAt(municipality+1,attributesHouseholdList[attribute],errorsHousehold.getIndexedValueAt(municipalityID,attributesHouseholdList[attribute]));
            }

            //Write the weights after finishing IPU for each municipality (saved each time over the previous version)
            weightsMatrix.appendColumn(minWeights.getColumnAsFloat(municipalityIDs),municipalityIDs);
            String freqFileName = ("scenOutput/weigthsMatrix.csv");
            SiloUtil.writeTableDataSet(weightsMatrix, freqFileName);
            String freqFileName2 = ("scenOutput/errorsIPU.csv");
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
        int[] nonZeroIds = frequencyMatrix.getColumnAsInt("IDnonZero");
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
        int initialRow = 1;
        while (municipalitiesMatrix.getValueAt(initialRow,"Select") == 0f){
            initialRow++;
        }
        if (municipalitiesMatrix.getValueAt(initialRow,"Select") == 1f){
            cityID[rowID] = (int) municipalitiesMatrix.getValueAt(initialRow,"ID_city");
            cityIDs[rowID] = Integer.toString(cityID[rowID]);
            cityCountyInitial[rowIDcounty] = rowID;
            rowID++;
            citiesCounter++;
            countyID[rowIDcounty] = (int) municipalitiesMatrix.getValueAt(initialRow,"ID_county");
            countyIDs[rowIDcounty] = Integer.toString(countyID[rowIDcounty]);
            rowIDcounty++;
        }
        for (int row = initialRow + 1; row <= municipalitiesMatrix.getRowCount(); row++){
            if (municipalitiesMatrix.getValueAt(row,"Select") == 1f){
                cityID[rowID] = (int) municipalitiesMatrix.getValueAt(row,"ID_city");
                cityIDs[rowID] = Integer.toString(cityID[rowID]);
                rowID++;
                if (rowID == initialRow + 1) {
                    if (municipalitiesMatrix.getValueAt(row, "ID_county") != municipalitiesMatrix.getValueAt(row - 1, "ID_county")) {
                        countyID[rowIDcounty] = (int) municipalitiesMatrix.getValueAt(row, "ID_county");
                        countyIDs[rowIDcounty] = Integer.toString(countyID[rowIDcounty]);
                        cityCountyInitial[rowIDcounty] = rowID - 1;
                        rowIDcounty++;
                        citiesCountyAux[rowIDcounty - 1] = citiesCounter;
                        citiesCounter = 0;
                    } else {
                        citiesCounter++;
                    }
                } else {
                    citiesCounter++;
                    if (municipalitiesMatrix.getValueAt(row, "ID_county") != municipalitiesMatrix.getValueAt(row - 1, "ID_county")) {
                        countyID[rowIDcounty] = (int) municipalitiesMatrix.getValueAt(row, "ID_county");
                        countyIDs[rowIDcounty] = Integer.toString(countyID[rowIDcounty]);
                        cityCountyInitial[rowIDcounty] = rowID - 1;
                        rowIDcounty++;
                        citiesCountyAux[rowIDcounty - 1] = citiesCounter;
                        citiesCounter = 0;
                    }
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
        nonZero.appendColumn(nonZeroIds,"IDnonZero");
        int[] dummy0 = {0,0};
        nonZeroSize.appendColumn(dummy0,"IDnonZero");
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
        nonZero.buildIndex(nonZero.getColumnPosition("IDnonZero"));
        nonZeroSize.buildIndex(nonZeroSize.getColumnPosition("IDnonZero"));


        //Create the weights table (for all the municipalities)
        TableDataSet weightsMatrix = new TableDataSet();
        weightsMatrix.appendColumn(microDataIds,"ID");


        //Create the errors table (for all the municipalities, by attribute)
        TableDataSet errorsMatrix = new TableDataSet();
        errorsMatrix.appendColumn(cityID,"ID_city");
        for (int attribute = 0; attribute < attributesHousehold.length;attribute++){
            double[] dummy2 = SiloUtil.createArrayWithValue(cityIDs.length,1.0);
            errorsMatrix.appendColumn(dummy2, attributesHousehold[attribute]);
        }
        TableDataSet errorsMatrixRegion = new TableDataSet();
        errorsMatrixRegion.appendColumn(countyID,"ID_county");
        for (int attribute = 0; attribute < attributesRegion.length;attribute++){
            double[] dummy2 = SiloUtil.createArrayWithValue(countyIDs.length,1.0);
            errorsMatrixRegion.appendColumn(dummy2, attributesRegion[attribute]);
        }
        errorsMatrixRegion.buildIndex(errorsMatrixRegion.getColumnPosition("ID_county"));
        errorsMatrix.buildIndex(errorsMatrix.getColumnPosition("ID_city"));


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
                    weighted_sum = weighted_sum + SiloUtil.getWeightedSum(weights.getColumnAsDouble(municipalitiesIDs[municipality]),
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
                            position = (int) collapsedMicroData.getIndexedValueAt(nonZeroIds[row], attributesRegionList[attribute]);
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
                            position = (int) collapsedMicroData.getIndexedValueAt(nonZeroIds[row], attributesHouseholdList[attribute]);
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
                for (int attribute = 0; attribute < attributesRegionList.length; attribute++) {
                    averageErrorIteration = averageErrorIteration + errorsRegion.getIndexedValueAt(regionID, attributesRegionList[attribute]);
                    attributesCounter++;
                }
                for(int attribute = 0; attribute < attributesHouseholdList.length; attribute++){
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


            //Copy the errors per attribute
            for (int attribute = 0; attribute < attributesRegionList.length; attribute++){
                errorsMatrixRegion.setIndexedValueAt(regionID,attributesRegionList[attribute],errorsRegion.getIndexedValueAt(regionID,attributesRegionList[attribute]));
            }
            for (int attribute = 0; attribute < attributesHouseholdList.length; attribute++){
                for (int municipality = 0; municipality < municipalitiesID.length; municipality++){
                    errorsMatrix.setIndexedValueAt(municipalitiesID[municipality],attributesHouseholdList[attribute],errorsHousehold.getIndexedValueAt(municipalitiesID[municipality],attributesHouseholdList[attribute]));
                }
            }

            //Write the weights after finishing IPU for each county
            String freqFileName = ("scenOutput/weigthsMatrix.csv");
            SiloUtil.writeTableDataSet(weightsMatrix, freqFileName);
            String freqFileName2 = ("scenOutput/errorsHouseholdIPU.csv");
            SiloUtil.writeTableDataSet(errorsMatrix, freqFileName2);
            String freqFileName3 = ("scenOutput/errorsRegionIPU.csv");
            SiloUtil.writeTableDataSet(errorsMatrixRegion, freqFileName3);

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


    private void generateJobs(){
        //Generate jobs file. The worker ID will be assigned later on the process "assignJobs"

        logger.info("   Starting to generate jobs");
        String[] jobTypes = ResourceUtil.getArray(rb, PROPERTIES_JOB_TYPES_DE);
        cellsMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_RASTER_CELLS));
        int[] rasterCellsIDs = cellsMatrix.getColumnAsInt("ID_cell");

        //For each municipality
        for (int municipality = 0; municipality < cityID.length; municipality++) {
            logger.info("   Municipality " + cityIDs[municipality] + ". Starting to generate jobs.");

            //-----------***** Data preparation *****-------------------------------------------------------------------
            //Create local variables to avoid accessing to the same variable on the parallel processing
            int municipalityID = cityID[municipality];
            String municipalityIDs = cityIDs[municipality];
            TableDataSet rasterCellsMatrix = cellsMatrix;
            rasterCellsMatrix.buildIndex(rasterCellsMatrix.getColumnPosition("ID_cell"));


            //obtain the raster cells of the municipality and their weight within the municipality
            int rasterCount = 0;
            for (int row = 1; row <= rasterCellsMatrix.getRowCount(); row++){
                if ((int) rasterCellsMatrix.getValueAt(row,"ID_city") == municipalityID){
                    rasterCount++;
                }
            }
            int[] rasterCellsList = new int[rasterCount];
            int finish = 0;
            int rowAux = 1;
            int rasterCellCountsAux = 0;
            while (finish == 0){
                if ((int) rasterCellsMatrix.getValueAt(rowAux,"ID_city") == municipalityID){
                    rasterCellsList[rasterCellCountsAux] = (int) rasterCellsMatrix.getValueAt(rowAux,"ID_cell");
                    rasterCellCountsAux++;
                }
                if (rasterCellCountsAux == rasterCellsList.length){
                    finish = 1;
                }
                rowAux++;
            }

            for (int row = 0; row < jobTypes.length; row++) {
                String jobType = jobTypes[row];
                int totalJobs = (int) marginalsHouseholdMatrix.getIndexedValueAt(municipalityID, jobType);
                //Create jobs according to probability, if there is at least one job
                if (totalJobs > 0.1) {
                    //Obtain the probability for that type of job for the raster cells within the municipality
                    double[] probability = new double[rasterCount];
                    for (int rasterCell = 0; rasterCell < rasterCount; rasterCell++) {
                        probability[rasterCell] = rasterCellsMatrix.getIndexedValueAt(rasterCellsList[rasterCell], jobType);
                    }
                    //Create jobs and assign the gender of the worker (with replacement)
                    for (int jobCounterCell = 0; jobCounterCell < totalJobs; jobCounterCell++) {
                        int[] records = select(probability, rasterCellsList);
                        probability[records[1]] = probability[records[1]] - 1;
                        int id = JobDataManager.getNextJobId();
                        Job job = new Job(id, records[0], 0, jobTypes[row]); //(int id, int zone, int workerId, String type)
                    }
                }
            }

        }

    }


    private void testJobs(){
        //Read my synthetic persons
        TableDataSet persons = SiloUtil.readCSVfile(rb.getString("employment.test"));
        TableDataSet probabilitiesJob = SiloUtil.readCSVfile(rb.getString("employment.probability"));

        int totalPersons = Arrays.stream(persons.getColumnAsInt("weight1")).sum();

        //To check the goodness of fit from Monte Carlo generation and the model in R
        TableDataSet results = new TableDataSet();
        int[] dummy = SiloUtil.createArrayWithValue(totalPersons,0);
        results.appendColumn(dummy, "aux");
        String[] names = {"job","jobMODEL","jobMC","gender","education","age"};
        for (int row = 0; row < names.length; row++){
            int[] dummy1 = SiloUtil.createArrayWithValue(totalPersons, 0);
            results.appendColumn(dummy1,names[row]);
        }
        int[] educationLevel = {0, 1, 4, 5};
        int[] countersJob = new int[probabilitiesJob.getRowCount()*2*educationLevel.length + 1];
        int[] countersJobMODEL = new int[probabilitiesJob.getRowCount()*2*educationLevel.length + 1];
        int[] countersJobMC = new int[probabilitiesJob.getRowCount()*2*educationLevel.length + 1];
        int[] matchesJobMODEL = new int[probabilitiesJob.getRowCount()*2*educationLevel.length + 1];
        int[] matchesJobMC = new int[probabilitiesJob.getRowCount()*2*educationLevel.length + 1];


        EmploymentChoice ec = new EmploymentChoice(rb);
        double[] probability = new double[10];
        String personType = "";
        int aux = 1;
        for (int i = 1; i <= persons.getRowCount(); i++){
            for (int j = 1; j < persons.getValueAt(i,"weight1"); j++) {
                Person pp = new Person((int) persons.getValueAt(i, "IDpp"), (int) persons.getValueAt(i, "IDhh"), (int) persons.getValueAt(i, "age"), (int) persons.getValueAt(i, "gender"), Race.white, (int) persons.getValueAt(i, "employed"), 0, 0);
                pp.setEducationLevel((int) persons.getValueAt(i, "education"));
                pp.setJobClass((int) persons.getValueAt(i, "jobType"));
                int education = 0;
                int educationCol = pp.getEducationLevel() - 1;
/*                switch ((int) persons.getValueAt(i, "education")) {
                    case 2:
                        education = 2;
                        educationCol = 1;
                        break;
                    case 3:
                        education = 1;
                        educationCol = 1;
                        break;
                    case 4:
                        education = 4;
                        educationCol = 2;
                        break;
                    case 5:
                        education = 5;
                        educationCol = 3;
                        break;
                    case 6:
                        education = 5;
                        educationCol = 3;
                        break;
                }*/

                //pp.setMaritalStatus((int)persons.getValueAt(aux,"marriage"));
                int jobPositionMODEL = ec.selectJobTypePerson(pp) + 1;
                if (pp.getGender() == 1) {
                    personType = "maleEducation";
                } else {
                    personType = "femaleEducation";
                }
                personType = personType + pp.getEducationLevel();
                probability = probabilitiesJob.getColumnAsDouble(personType);
                int jobPositionMC = SiloUtil.select(probability) + 1;
                results.setValueAt(aux, "jobMODEL", jobPositionMODEL);
                results.setValueAt(aux, "jobMC", jobPositionMC);
                results.setValueAt(aux,"aux",aux);
                results.setValueAt(aux,"job",pp.getJobClass());
                results.setValueAt(aux,"gender",pp.getGender());
                results.setValueAt(aux,"education",education);
                results.setValueAt(aux,"age",pp.getAge());
                countersJob[(probabilitiesJob.getRowCount()-1)*(pp.getGender() - 1)*(educationLevel.length) + (pp.getJobClass()-1)*(educationLevel.length) + educationCol]++;
                countersJobMODEL[(probabilitiesJob.getRowCount()-1)*(pp.getGender() - 1) *(educationLevel.length) + (jobPositionMODEL-1)*(educationLevel.length) + educationCol]++;
                countersJobMC[(probabilitiesJob.getRowCount()-1)*(pp.getGender() - 1) * (educationLevel.length)+ (jobPositionMC-1)*(educationLevel.length) + educationCol]++;
                if (pp.getJobClass() ==  jobPositionMODEL){
                    matchesJobMODEL[(probabilitiesJob.getRowCount()-1)*(pp.getGender() - 1) *(educationLevel.length) + (jobPositionMODEL-1) * (educationLevel.length) + educationCol]++;
                }
                if (pp.getJobClass() ==  jobPositionMC){
                    matchesJobMC[(probabilitiesJob.getRowCount()-1)*(pp.getGender() - 1) * (educationLevel.length)+ (jobPositionMODEL-1) * (educationLevel.length) + educationCol]++;
                }
                aux++;
                personType = "";
            }
        }
        String freqFileName3 = ("scenOutput/jobTest.csv");
        SiloUtil.writeTableDataSet(results, freqFileName3);
        TableDataSet counts = new TableDataSet();
        counts.appendColumn(countersJob,"jobs");
        counts.appendColumn(countersJobMODEL,"model");
        counts.appendColumn(countersJobMC,"monteCarlo");
        counts.appendColumn(matchesJobMODEL,"matchModel");
        counts.appendColumn(matchesJobMC,"matchMonteCarlo");
        SiloUtil.writeTableDataSet(counts,"scenOutput/jobCounts.csv");
    }


    private void assignJobs(){
        //Read the synthetic population and the travel times
        TableDataSet households = SiloUtil.readCSVfile(rb.getString(PROPERTIES_HOUSEHOLD_SYN_POP));
        TableDataSet persons = SiloUtil.readCSVfile(rb.getString(PROPERTIES_PERSON_SYN_POP));
        TableDataSet dwellings = SiloUtil.readCSVfile(rb.getString(PROPERTIES_DWELLING_SYN_POP));
        TableDataSet jobs = SiloUtil.readCSVfile(rb.getString(PROPERTIES_JOB_SYN_POP));

        //Read raster cells and generate the TableDataSet with the auto travel times
        TableDataSet timesMatrix = new TableDataSet();
        cellsMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_RASTER_CELLS));
        int[] rasterCellsIDs = cellsMatrix.getColumnAsInt("ID_cell");
        timesMatrix.appendColumn(rasterCellsIDs,"ID_cell");
        for (int cell = 0; cell < rasterCellsIDs.length; cell++){
            double[] dummy = SiloUtil.createArrayWithValue(rasterCellsIDs.length,0.0);
            timesMatrix.appendColumn(dummy,Integer.toString(rasterCellsIDs[cell]));
        }
        timesMatrix.buildIndex(timesMatrix.getColumnPosition("ID_cell"));


        //Read the skim matrix
        String omxFileName= "input/syntheticPopulation/travelTimeMatrix.omx";
        OmxFile travelTimeOmx = new OmxFile(omxFileName);
        travelTimeOmx.openReadOnly();
        travelTimeMatrix = SiloUtil.convertOmxToMatrix(travelTimeOmx.getMatrix("mat1"));
        String omxFileName1= "input/syntheticPopulation/tripsMatrix.omx";
        OmxFile numberOfTripsOmx = new OmxFile(omxFileName1);
        numberOfTripsOmx.openReadOnly();
        numberOfTripsMatrix = SiloUtil.convertOmxToMatrix(numberOfTripsOmx.getMatrix("mat1"));
        logger.info("   Read OMX matrix");


        //Generate the households, dwellings and persons
        int aux = 1;
        for (int i = 1; i <= households.getRowCount(); i++){
            Household hh = new Household((int)households.getValueAt(i,"hhID"),(int)households.getValueAt(i,"hhID"),(int)households.getValueAt(i,"zone"),(int)households.getValueAt(i,"hhSize"),(int)households.getValueAt(i,"hhWorkers"));
            for (int j = 1; j <= hh.getHhSize(); j++){
                Person pp = new Person((int)persons.getValueAt(aux,"personID"),(int)persons.getValueAt(aux,"hhID"),(int)persons.getValueAt(aux,"age"),(int)persons.getValueAt(aux,"gender"),Race.white,(int)persons.getValueAt(aux,"occupation"),0,(int)persons.getValueAt(aux,"income"));
                pp.setEducationLevel((int)persons.getValueAt(aux,"education"));
                pp.setMaritalStatus((int)persons.getValueAt(aux,"marriage"));
                pp.setHhSize(hh.getHhSize());
                aux++;
            }
            Dwelling dd = new Dwelling((int)dwellings.getValueAt(i,"id"),(int)dwellings.getValueAt(i,"zone"),(int)dwellings.getValueAt(i,"hhID"),DwellingType.MF5plus,(int)dwellings.getValueAt(i,"bedrooms"),(int)dwellings.getValueAt(i,"quality"),(int)dwellings.getValueAt(i,"monthlyCost"),(int)dwellings.getValueAt(i,"restriction"),(int)dwellings.getValueAt(i,"yearBuilt"));
            dd.setFloorSpace((int)dwellings.getValueAt(i,"floor"));
            dd.setBuildingSize((int)dwellings.getValueAt(i,"building"));
            dd.setYearConstructionDE((int)dwellings.getValueAt(i,"year"));
        }


        //Generate the jobs



        //Assign workplaces depending on trip length and adequacy to the job. The parameters from the model are calibrated to match the trip length distribution from commuters and the OD matrix




        //Original for HEMA
        /*//For each household, we read the persons
        //We check if they are working and, if so, provide a workplace based on the number of work trips from his/her home raster cell to other raster cells (Montecarlo)
        int aux = 1;
        for (int i = 1; i <= households.getRowCount(); i++){
            Household hh = new Household((int)households.getValueAt(i,"hhID"),(int)households.getValueAt(i,"hhID"),(int)households.getValueAt(i,"zone"),(int)households.getValueAt(i,"hhSize"),(int)households.getValueAt(i,"hhWorkers"));
            for (int j = 1; j <= hh.getHhSize(); j++){
                Person pp = new Person((int)persons.getValueAt(aux,"personID"),(int)persons.getValueAt(aux,"hhID"),(int)persons.getValueAt(aux,"age"),(int)persons.getValueAt(aux,"gender"),Race.white,(int)persons.getValueAt(aux,"occupation"),0,(int)persons.getValueAt(aux,"income"));
                pp.setEducationLevel((int)persons.getValueAt(aux,"education"));
                pp.setMaritalStatus((int)persons.getValueAt(aux,"marriage"));
                pp.setHhSize(hh.getHhSize());
                //int jobPosition = ec.selectJobTypePerson(pp); To use the model approach. It gives lower percentage of matches at the individual level.
                if (pp.getGender() == 1) {
                    personType = "maleEducation";
                } else {
                    personType = "femaleEducation";
                }
                probability = probabilitiesJob.getColumnAsDouble(personType + pp.getEducationLevel());
                pp.setJobTypeDE(SiloUtil.select(probability) + 1);
                if (pp.getOccupation() == 1){
                    int rasterCellID = hh.getHomeZone();
                    pp.setZone(rasterCellID);
                    if (rasterCellID < 10000){
                        float[] numberOfTrips =  numberOfTripsMatrix.getRow(rasterCellID).copyValues1D();
                        int workplace = SiloUtil.select(numberOfTrips);
                        pp.setWorkplace(workplace + 1);
                        if (workplace + 1 == rasterCellID) {
                            pp.setTravelTime(5); //Intra-zonal travel time of 5 minutes
                        } else {
                            pp.setTravelTime(travelTimeMatrix.getValueAt(rasterCellID, workplace + 1));
                        }
                        timesMatrix.setIndexedValueAt(rasterCellID,Integer.toString(workplace + 1),1 + timesMatrix.getIndexedValueAt(rasterCellID,Integer.toString(workplace + 1)));
                    } else {
                        pp.setWorkplace(0);
                        pp.setTravelTime(0);
                    }
                } else {
                    pp.setWorkplace(0);
                    pp.setTravelTime(0);
                }
                aux++;
            }
            Dwelling dd = new Dwelling((int)dwellings.getValueAt(i,"id"),(int)dwellings.getValueAt(i,"zone"),(int)dwellings.getValueAt(i,"hhID"),DwellingType.MF5plus,(int)dwellings.getValueAt(i,"bedrooms"),(int)dwellings.getValueAt(i,"quality"),(int)dwellings.getValueAt(i,"monthlyCost"),(int)dwellings.getValueAt(i,"restriction"),(int)dwellings.getValueAt(i,"yearBuilt"));
            dd.setFloorSpace((int)dwellings.getValueAt(i,"floor"));
            dd.setBuildingSize((int)dwellings.getValueAt(i,"building"));
            dd.setYearConstructionDE((int)dwellings.getValueAt(i,"year"));
        }*/
        String freqFileName3 = ("scenOutput/workTripsMatrix.csv");
        SiloUtil.writeTableDataSet(timesMatrix, freqFileName3);
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


        //List of extra attributes that are checked after the generation of households and persons
        //These attributes are added to the attributes of the IPU
        String[] attributesHouseholdGen = ResourceUtil.getArray(rb,PROPERTIES_HOUSEHOLD_ATTR_GENERATION);
        String[] attributesRegionGen = ResourceUtil.getArray(rb,PROPERTIES_REGION_ATTR_GENERATION);
        attributesRegion = ResourceUtil.getArray(rb,PROPERTIES_REGION_ATTRIBUTES);
        String[] attributesExtra = new String[attributesRegion.length + attributesHouseholdGen.length + attributesRegionGen.length];
        if (attributesRegion != null){
            for (int attribute = 0; attribute < attributesRegion.length; attribute++){
                attributesExtra[attribute] = attributesRegion[attribute];
            }
        }
        if (attributesHouseholdGen != null){
            for (int attribute = 0; attribute < attributesHouseholdGen.length; attribute++){
                attributesExtra[attribute + attributesRegion.length] = attributesHouseholdGen[attribute];
            }
        }
        if (attributesRegionGen != null){
            for (int attribute = 0; attribute < attributesRegionGen.length; attribute++){
                attributesExtra[attribute + attributesRegion.length + attributesHouseholdGen.length] = attributesRegionGen[attribute];
            }
        }


        //Define car probability
        //They depend on household size.


        //Define income distribution
        double incomeShape = ResourceUtil.getDoubleProperty(rb,PROPERTIES_INCOME_GAMMA_SHAPE);
        double incomeRate = ResourceUtil.getDoubleProperty(rb,PROPERTIES_INCOME_GAMMA_RATE);
        double[] incomeProbability = ResourceUtil.getDoubleArray(rb,PROPERTIES_INCOME_GAMMA_PROBABILITY);
        GammaDistributionImpl gammaDist = new GammaDistributionImpl(incomeShape, 1/incomeRate);


        //Create the errors table (for all the municipalities)
        TableDataSet counterSynPop = new TableDataSet();
        TableDataSet relativeErrorSynPop = new TableDataSet();
        counterSynPop.appendColumn(cityID,"ID_city");
        relativeErrorSynPop.appendColumn(cityID,"ID_city");
        for(int attribute = 0; attribute < attributesHousehold.length; attribute++) {
            double[] dummy2 = SiloUtil.createArrayWithValue(cityIDs.length,0.0);
            double[] dummy3 = SiloUtil.createArrayWithValue(cityIDs.length,0.0);
            counterSynPop.appendColumn(dummy2, attributesHousehold[attribute]);
            relativeErrorSynPop.appendColumn(dummy3,attributesHousehold[attribute]);
        }
        if (attributesExtra != null){
            for (int attribute = 0; attribute < attributesExtra.length; attribute++){
                double[] dummy2 = SiloUtil.createArrayWithValue(cityID.length,0.0);
                double[] dummy3 = SiloUtil.createArrayWithValue(cityIDs.length,0.0);
                counterSynPop.appendColumn(dummy2, attributesExtra[attribute]);
                relativeErrorSynPop.appendColumn(dummy3,attributesExtra[attribute]);
            }
        }
        counterSynPop.buildIndex(counterSynPop.getColumnPosition("ID_city"));
        relativeErrorSynPop.buildIndex(relativeErrorSynPop.getColumnPosition("ID_city"));


        //Selection of households, persons, jobs and dwellings per municipality
        for (int municipality = 0; municipality < cityID.length; municipality++){
            logger.info("   Municipality " + cityIDs[municipality] + ". Starting to generate households.");

            //-----------***** Data preparation *****-------------------------------------------------------------------
            //Create local variables to avoid accessing to the same variable on the parallel processing
            int municipalityID = cityID[municipality];
            String municipalityIDs = cityIDs[municipality];
            String[] attributesHouseholdIPU = attributesHousehold;
            String[] attributesList = attributesExtra;
            TableDataSet rasterCellsMatrix = cellsMatrix;
            TableDataSet microHouseholds = microDataHousehold;
            TableDataSet microPersons = microDataPerson;
            TableDataSet microDwellings = microDataDwelling;
            int totalHouseholds = (int) marginalsHouseholdMatrix.getIndexedValueAt(municipalityID,"hhTotal");
            int totalQuarters = (int) marginalsHouseholdMatrix.getIndexedValueAt(municipalityID,"privateQuarters");
            double[] probability = weightsTable.getColumnAsDouble(municipalityIDs);
            int[] agePerson = ageBracketsPerson;
            int[] sizeBuilding = sizeBracketsDwelling;
            int[] yearBuilding = yearBracketsDwelling;
            int vacantDwellings = (int) marginalsHouseholdMatrix.getIndexedValueAt(cityID[municipality],"totalDwellingsVacant");


            //Counter and errors for the municipality
            TableDataSet counterMunicipality = new TableDataSet();
            TableDataSet errorMunicipality = new TableDataSet();
            TableDataSet marginals = new TableDataSet();
            int[] dummy = SiloUtil.createArrayWithValue(1,municipalityID);
            int[] dummy1 = SiloUtil.createArrayWithValue(1,municipalityID);
            int[] dummy4 = SiloUtil.createArrayWithValue(1,municipalityID);
            counterMunicipality.appendColumn(dummy,"ID_city");
            errorMunicipality.appendColumn(dummy1,"ID_city");
            marginals.appendColumn(dummy4,"ID_city");
            for (int attribute = 0;attribute < attributesHouseholdIPU.length; attribute++) {
                double[] dummy2 = SiloUtil.createArrayWithValue(1,0.0);
                double[] dummy3 = SiloUtil.createArrayWithValue(1,0.0);
                int[] dummy5 = SiloUtil.createArrayWithValue(1,(int) marginalsHouseholdMatrix.getIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute]));
                counterMunicipality.appendColumn(dummy2, attributesHouseholdIPU[attribute]);
                errorMunicipality.appendColumn(dummy3,attributesHouseholdIPU[attribute]);
                marginals.appendColumn(dummy5,attributesHouseholdIPU[attribute]);
            }
            if (attributesList != null) {
                for (int attribute = 0; attribute < attributesList.length; attribute++) {
                    double[] dummy2 = SiloUtil.createArrayWithValue(1, 0.0);
                    double[] dummy3 = SiloUtil.createArrayWithValue(1, 0.0);
                    counterMunicipality.appendColumn(dummy2, attributesList[attribute]);
                    errorMunicipality.appendColumn(dummy3, attributesList[attribute]);
                }
            }
            counterMunicipality.buildIndex(counterMunicipality.getColumnPosition("ID_city"));
            errorMunicipality.buildIndex(errorMunicipality.getColumnPosition("ID_city"));
            marginals.buildIndex(marginals.getColumnPosition("ID_city"));


            //obtain the raster cells of the municipality and their weight within the municipality
            int rasterCount = 0;
            for (int row = 1; row <= rasterCellsMatrix.getRowCount(); row++){
                if ((int) rasterCellsMatrix.getValueAt(row,"ID_city") == municipalityID){
                    rasterCount++;
                }
            }
            int[] rasterCellsList = new int[rasterCount];
            int finish = 0;
            int rowAux = 1;
            int rasterCellCountsAux = 0;
            //float rasterPopulation = 0;
            double[] rasterCellsWeight = new double[rasterCount];
            while (finish == 0){
                if ((int) rasterCellsMatrix.getValueAt(rowAux,"ID_city") == municipalityID){
                    rasterCellsList[rasterCellCountsAux] = (int) rasterCellsMatrix.getValueAt(rowAux,"ID_cell");
                    rasterCellsWeight[rasterCellCountsAux] = rasterCellsMatrix.getValueAt(rowAux,"Population");
                    //rasterPopulation = rasterPopulation + rasterCellsMatrix.getValueAt(rowAux,"Population");
                    rasterCellCountsAux++;
                }
                if (rasterCellCountsAux == rasterCellsList.length){
                    finish = 1;
                }
                rowAux++;
            }


            //select the probabilities of the households from the microData, for that municipality
            double[] probabilityPrivate = new double[probability.length]; // Separate private households and group quarters for generation
            double[] probabilityQuarter = new double[probability.length];
            for (int row = 0; row < probability.length; row++){
                if ((int) microHouseholds.getValueAt(row + 1,"groupQuarters") == 0){
                    probabilityPrivate[row] = probability[row];
                } else {
                    probabilityQuarter[row] = probability[row];
                }
            }


            //marginals for the municipality
            int hhPersons = 0;
            int hhTotal = 0;
            int quartersTotal = 0;
            int[] timesSelected = new int[probability.length];




            //for all the households that are inside the municipality (we will match perfectly the number of households. The total population will vary compared to the marginals.)
            for (int row = 0; row < totalHouseholds; row++) {

                //select the household to copy and allocate it
                int[] records = select(probabilityPrivate, microDataIds);
                int idHHmicroData = records[0];
                int recordHHmicroData = records[1];
                int[] recordsCell = select(rasterCellsWeight,rasterCellsWeight.length,rasterCellsList);
                int householdCell = recordsCell[0];
                int recordHouseholdCell = recordsCell[1];
                timesSelected[recordHHmicroData]++;




/*                if (probabilityPrivate[recordHHmicroData] > 1.0) {
                    probabilityPrivate[recordHHmicroData] = probabilityPrivate[recordHHmicroData] - 1;
                } else {
                    probabilityPrivate[recordHHmicroData] = 0;
                }
                if (rasterCellsWeight[recordHouseholdCell] > (int) microHouseholds.getIndexedValueAt(idHHmicroData, "hhSize")) {
                    rasterCellsWeight[recordHouseholdCell] = rasterCellsWeight[recordHouseholdCell] - (int) microHouseholds.getIndexedValueAt(idHHmicroData, "hhSize");
                } else {
                    rasterCellsWeight[recordHouseholdCell] = 0;
                }*/
                //update the probability of the record of being selected on the next draw. It increases the correlation between the probability and the number of draws at the end



                //copy the private household characteristics
                int householdSize = (int) microHouseholds.getIndexedValueAt(idHHmicroData, "hhSize");
                int householdWorkers = (int) microHouseholds.getIndexedValueAt(idHHmicroData, "femaleWorkers") +
                        (int) microHouseholds.getIndexedValueAt(idHHmicroData, "maleWorkers");
                int id = HouseholdDataManager.getNextHouseholdId();
                Household household = new Household(id, idHHmicroData, householdCell, householdSize, householdWorkers); //(int id, int dwellingID, int homeZone, int hhSize, int autos)
                hhTotal++;
                counterMunicipality = updateCountersHousehold(household, counterMunicipality, municipalityID);


                //copy the household members characteristics
                for (int rowPerson = 0; rowPerson < householdSize; rowPerson++) {
                    int idPerson = HouseholdDataManager.getNextPersonId();
                    int personCounter = (int) microHouseholds.getIndexedValueAt(idHHmicroData, "personCount") + rowPerson;
                    int age = (int) microPersons.getValueAt(personCounter, "age");
                    int gender = (int) microPersons.getValueAt(personCounter, "gender");
                    int occupation = (int) microPersons.getValueAt(personCounter, "occupation");
                    int income = (int) microPersons.getValueAt(personCounter, "income");
                    try {
                        income = (int) translateIncome((int) microPersons.getValueAt(personCounter, "income"),incomeProbability, gammaDist);
                    } catch (MathException e) {
                        e.printStackTrace();
                    }
                    //int workplace = (int) microDataPerson.getValueAt(personCounter,"workplace"); It will be linked after using the trip length distribution
                    int jobClass = (int) microPersons.getValueAt(personCounter, "jobSector");
                    int educationLevel = (int) microPersons.getValueAt(personCounter, "educationLevel");
                    int maritalStatus = (int) microPersons.getValueAt(personCounter, "maritalStatus");
                    int telework = (int) microPersons.getValueAt(personCounter, "telework");
                    int nationality = (int) microPersons.getValueAt(personCounter, "nationality");
                    Person pers = new Person(idPerson, id, age, gender, Race.white, occupation, 0, income); //(int id, int hhid, int age, int gender, Race race, int occupation, int workplace, int income)
                    pers.setJobClass(jobClass);
                    pers.setEducationLevel(educationLevel);
                    pers.setMaritalStatus(maritalStatus);
                    pers.setNationality(nationality);
                    pers.setTelework(telework);
                    hhPersons++;
                    counterMunicipality = updateCountersPerson(pers, counterMunicipality, municipalityID,agePerson);
                }


                //Copy the dwelling of that household
                int newDdId = RealEstateDataManager.getNextDwellingId();
                int bedRooms = 1; //Not on the micro data
                int quality = 1; //depend on complete plumbing, complete kitchen and year built. Not on the micro data
                int price = 1; //Monte Carlo
                int year = 1; //Not by year. In the data is going to be in classes
                int floorSpace = (int) microDwellings.getIndexedValueAt(idHHmicroData, "dwellingFloorSpace");
                int usage = (int) microDwellings.getIndexedValueAt(idHHmicroData, "dwellingUsage");
                int buildingSize = (int) microDwellings.getIndexedValueAt(idHHmicroData, "dwellingType");
                int yearConstruction =(int) microDwellings.getIndexedValueAt(idHHmicroData, "dwellingYear");
                Dwelling dwell = new Dwelling(newDdId, householdCell, id, DwellingType.MF234 , bedRooms, quality, price, 0, year); //newDwellingId, raster cell, HH Id, ddType, bedRooms, quality, price, restriction, construction year
                dwell.setFloorSpace(floorSpace);
                dwell.setUsage(usage);
                dwell.setBuildingSize(buildingSize);
                dwell.setYearConstructionDE(yearConstruction);
                counterMunicipality = updateCountersDwelling(dwell,counterMunicipality,municipalityID,yearBuilding,sizeBuilding);
            }

/*            //for all persons in group quarters
            for (int row = 0; row < totalQuarters; row++){

                //select the person to copy and allocate it
                int[] records = select(probabilityQuarter, microDataIds);
                int idHHmicroData = records[0];
                int recordHHmicroData = records[1];


                //update the probability of the record of being selected on the next draw. It increases the correlation between the probability and the number of draws at the end
                if (probabilityPrivate[recordHHmicroData] > 1) {
                    probabilityPrivate[recordHHmicroData] = probabilityPrivate[recordHHmicroData] - 1;
                } else {
                    probabilityPrivate[recordHHmicroData] = 0;
                }


                //Group quarter - household characteristics
                int id = HouseholdDataManager.getNextHouseholdId();
                Household household = new Household(id, idHHmicroData, municipalityID, 1, 0); //(int id, int dwellingID, int homeZone, int hhSize, int autos)
                quartersTotal++;

                //person characteristics
                int idPerson = HouseholdDataManager.getNextPersonId();
                int personCounter = (int) microHouseholds.getIndexedValueAt(idHHmicroData, "personCount");
                int age = (int) microPersons.getValueAt(personCounter, "age");
                int gender = (int) microPersons.getValueAt(personCounter, "gender");
                int occupation = (int) microPersons.getValueAt(personCounter, "occupation");
                int income = 0;
                try {
                    income = (int) translateIncome((int) microPersons.getValueAt(personCounter, "income"),incomeProbability, gammaDist);
                } catch (MathException e) {
                    e.printStackTrace();
                }
                //int workplace = (int) microDataPerson.getValueAt(personCounter,"workplace"); It will be linked after using the trip length distribution
                int jobClass = (int) microPersons.getValueAt(personCounter, "jobSector");
                int educationLevel = (int) microPersons.getValueAt(personCounter, "educationLevel");
                int maritalStatus = (int) microPersons.getValueAt(personCounter, "maritalStatus");
                int telework = (int) microPersons.getValueAt(personCounter, "telework");
                int nationality = (int) microPersons.getValueAt(personCounter, "nationality");
                Person pers = new Person(idPerson, id, age, gender, Race.white, occupation, 1, income); //(int id, int hhid, int age, int gender, Race race, int occupation, int workplace, int income)
                pers.setJobClass(jobClass);
                pers.setEducationLevel(educationLevel);
                pers.setMaritalStatus(maritalStatus);
                pers.setNationality(nationality);
                pers.setTelework(telework);
                counterMunicipality = updateCountersPersonQuarter(pers,counterMunicipality,municipalityID);
            }*/

            int households = HouseholdDataManager.getHighestHouseholdIdInUse() - previousHouseholds;
            int persons = HouseholdDataManager.getHighestPersonIdInUse() - previousPersons;
            previousHouseholds = HouseholdDataManager.getHighestHouseholdIdInUse();
            previousPersons = HouseholdDataManager.getHighestPersonIdInUse();


            //Vacant dwellings--------------------------------------------
            //Probability of floor size for vacant dwellings
            float [] vacantFloor = new float[sizeBuilding.length];
            for (int row = 0; row < sizeBuilding.length; row++){
                String name = "vacantDwellings" + sizeBuilding[row];
                vacantFloor[row] = marginalsHouseholdMatrix.getIndexedValueAt(municipalityID,name)/vacantDwellings;
            }

            //Probability for year and building size for vacant dwellings
            float[] vacantSize = new float[yearBuilding.length * 2];
            for (int row = 0; row < yearBuilding.length; row++){
                String name = "vacantSmallDwellings" + yearBuilding[row];
                String name1 = "vacantMediumDwellings" + yearBuilding[row];
                vacantSize[row] = marginalsHouseholdMatrix.getIndexedValueAt(municipalityID,name) / vacantDwellings;
                vacantSize[row + yearBuilding.length] = marginalsHouseholdMatrix.getIndexedValueAt(municipalityID,name1) / vacantDwellings;
            }

            //generate vacant dwellings on the municipality
            for (int row = 0; row < vacantDwellings; row++){
                //Monte Carlo simulation for floor space (sqm). The category is converted to continuous variable between 40 and 130 sqm
                int floorSpace = 0;
                Random r = new Random();
                int floorSpaceCat = SiloUtil.select(vacantFloor);
                if (floorSpaceCat == 0){
                    floorSpace = 40;
                } else if (floorSpaceCat == sizeBuilding.length) {
                    floorSpace = 130;
                } else {
                    floorSpace = r.nextInt(sizeBuilding[floorSpaceCat]-sizeBuilding[floorSpaceCat-1]) +
                            sizeBuilding[floorSpaceCat - 1]; //Assign random number between both brackets
                }

                //Monte Carlo simulation for building size and construction year
                int size = 0;
                int yearConstruction = 0;
                int sizeCat = SiloUtil.select(vacantSize);
                if (sizeCat < yearBuilding.length + 1){
                    size = 1;
                    yearConstruction = sizeCat;
                } else {
                    size = 2;
                    yearConstruction = sizeCat - yearBuilding.length;
                }

                //Assign the characteristics
                int newDdId = RealEstateDataManager.getNextDwellingId();
                int bedRooms = 1; //Not on the micro data
                int quality = 1; //depend on complete plumbing, complete kitchen and year built. Not on the micro data
                int price = 1; //Monte Carlo
                int year = 1;
                int ddCell[] = select(rasterCellsWeight,rasterCellsWeight.length,rasterCellsList); // I allocate vacant dwellings using the same proportion as occupied dwellings.
                Dwelling dwell = new Dwelling(newDdId, ddCell[0], 0, DwellingType.MF234 , bedRooms, quality, price, 0, year); //newDwellingId, raster cell, HH Id, ddType, bedRooms, quality, price, restriction, construction year
                dwell.setFloorSpace(floorSpace);
                dwell.setUsage(3);
                dwell.setBuildingSize(size);
                dwell.setYearConstructionDE(yearConstruction);
                counterMunicipality = updateCountersDwellingVacant(dwell,counterMunicipality,municipalityID,yearBuilding,sizeBuilding);
            }


            //Calculate the errors from the synthesized population at the attributes of the IPU.
            //Update the tables for all municipalities with the result of this municipality

            //Consider if I need to add also the errors from other attributes. They must be at the marginals file, or one extra file
            //For county level they should be calculated on a next step, outside this loop.
            float averageError = 0f;
            for (int attribute = 0; attribute < attributesHouseholdIPU.length; attribute++){
                float error = Math.abs((counterMunicipality.getIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute]) -
                        marginals.getIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute])) /
                        marginals.getIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute]));
                errorMunicipality.setIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute],error);
                averageError = averageError + error;
                counterSynPop.setIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute],counterMunicipality.getIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute]));
                relativeErrorSynPop.setIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute],errorMunicipality.getIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute]));
            }
            averageError = averageError / (1 + attributesHouseholdIPU.length) * 100;
            if (attributesList != null){
                for (int attribute = 0; attribute < attributesList.length;attribute++){
                    counterSynPop.setIndexedValueAt(municipalityID,attributesList[attribute],counterMunicipality.getIndexedValueAt(municipalityID,attributesList[attribute]));
                    relativeErrorSynPop.setIndexedValueAt(municipalityID,attributesList[attribute],errorMunicipality.getIndexedValueAt(municipalityID,attributesList[attribute]));
                }
            }


            logger.info("   Municipality " + municipalityID + ". Generated " + hhPersons + " persons in " + hhTotal + " households and " + quartersTotal + " persons in group quarters. Average error of " + averageError);
            TableDataSet prob = new TableDataSet();
            prob.appendColumn(microDataIds,"ID");
            prob.appendColumn(probabilityPrivate,"probabilityPrivate");
            prob.appendColumn(timesSelected,"numberSelections");
            SiloUtil.writeTableDataSet(prob,"scenOutput/compara.csv");
            SiloUtil.writeTableDataSet(counterMunicipality,"scenOutput/counterMun.csv");
            SiloUtil.writeTableDataSet(errorMunicipality,"scenOutput/errorMun.csv");
        }
        int households = HouseholdDataManager.getHighestHouseholdIdInUse();
        int persons = HouseholdDataManager.getHighestPersonIdInUse();
        logger.info("   Finished generating households and persons. A population of " + persons + " persons in " + households + " households was generated.");


        //Check the errors at the region level


        //Write the files for all municipalities
        String name = ("scenOutput/totalsSynPop.csv");
        SiloUtil.writeTableDataSet(counterSynPop,name);
        String name1 = ("scenOutput/errorsSynPop.csv");
        SiloUtil.writeTableDataSet(relativeErrorSynPop,name1);
    }


    private DwellingType translateDwellingType (int pumsDdType) {
        // translate micro census dwelling types into 6 MetCouncil Dwelling Types

        // Available in MICRO CENSUS:
//        V 01 . Building with 1-2 apartments
//        V 02 . Building with 3-6 apartments
//        V 03 . Building with 1-12 apartments
//        V 04 . Building with 13-20 apartments
//        V 05 . Building with 21+ apartments
//        V 09 . Not stated
//        V -1 . Living in group quarter
//        V -5 . Moved in the last 12 months


        DwellingType type;
        if (pumsDdType == 2) type = DwellingType.MF234; //duplexes and buildings 2-4 units
        else if (pumsDdType == 1) type = DwellingType.SFD; //single-family house detached
        //else if (pumsDdType == 3) type = DwellingType.SFA;//single-family house attached or townhouse
        //else if (pumsDdType == 4 || pumsDdType == 5) type = DwellingType.MH; //mobile home
        else if (pumsDdType >= 3 ) type = DwellingType.MF5plus; //multifamily houses with 5+ units. Assumes that not stated are 5+units
        else if (pumsDdType == -1) type = DwellingType.MF5plus; //multifamily houses with 5+ units. Assumes that group quarters are 5+ units
        else if (pumsDdType == -5) type = DwellingType.MH; //mobile home; //mobile home. Assumes that group quarters are 5+ units
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


    private static int translateEducationLevel (int personEducation, TableDataSet jobs){
        //translate 100 job descriptions to 4 job types
        //jobs is one TableDataSet that is read from a csv file containing the description, ID and types of jobs.
        int education = 0;
        int finish = 0;
        int row = 1;
        while (finish == 0 & row < jobs.getRowCount()){
            if (personEducation == jobs.getValueAt(row,"fdz_mz_sufCode")) {
                finish =1;
                education = (int) jobs.getValueAt(row,"SynPopCode");
            }
            else {
                row++;
            }
        }
        return education;
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
        Random rand = new Random();
        double selPos = sumProb * rand.nextDouble();
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
        Random rand = new Random();
        double selPos = sumProb * rand.nextDouble();
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

    public static double[] convertProbability (double[] probabilities){
        //method to return the probability in percentage
        double sum = 0;
        double[] relProb = new double[probabilities.length];
        for (int row = 0; row < probabilities.length; row++){
            sum = sum + probabilities[row];
        }
        for (int row = 0; row < probabilities.length; row++) {
            relProb[row] = probabilities[row]/sum*1000;
        }
        return relProb;
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
        if (person.getNationality() == 8) {
            attributesCount.setIndexedValueAt(mun, "foreigners", attributesCount.getIndexedValueAt(mun, "foreigners") + 1);
        }
        if (person.getGender() == 1) {
            if (person.getOccupation() == 1) {
                attributesCount.setIndexedValueAt(mun, "maleWorkers", attributesCount.getIndexedValueAt(mun, "maleWorkers") + 1);
            }
        } else {
            if (person.getOccupation() == 1) {
                attributesCount.setIndexedValueAt(mun, "femaleWorkers", attributesCount.getIndexedValueAt(mun, "femaleWorkers") + 1);
            }
        }
        int age = person.getAge();
        int row1 = 0;
        while (age > ageBracketsPerson[row1]) {
            row1++;
        }
        if (person.getGender() == 1) {
            String name = "male" + ageBracketsPerson[row1];
            attributesCount.setIndexedValueAt(mun, name, attributesCount.getIndexedValueAt(mun, name) + 1);
        } else {
            String name = "female" + ageBracketsPerson[row1];
            attributesCount.setIndexedValueAt(mun, name, attributesCount.getIndexedValueAt(mun, name) + 1);
        }
        int education = person.getEducationLevel();
        String name = "education" + education;
        attributesCount.setIndexedValueAt(mun, name, attributesCount.getIndexedValueAt(mun, name) + 1);
        if (person.getMaritalStatus() == 1) {
            attributesCount.setIndexedValueAt(mun, "single", attributesCount.getIndexedValueAt(mun, "single") + 1);
        } else if (person.getMaritalStatus() == 2) {
            attributesCount.setIndexedValueAt(mun, "married", attributesCount.getIndexedValueAt(mun, "married") + 1);
        } else {
            attributesCount.setIndexedValueAt(mun, "children", attributesCount.getIndexedValueAt(mun, "children") + 1);
        }
        int jobSector = person.getJobClass();
        name = "job" + jobSector;
        attributesCount.setIndexedValueAt(mun, name, attributesCount.getIndexedValueAt(mun, name) + 1);
        return attributesCount;
    }

    public static TableDataSet updateCountersPersonQuarter (Person person, TableDataSet attributesCount,int mun){
        /* method to update the counters with the characteristics of the generated person living in a group quarter*/
        if (person.getNationality() == 8){
            attributesCount.setIndexedValueAt(mun,"foreigners",attributesCount.getIndexedValueAt(mun,"foreigners") + 1);
        }
        if (person.getGender() == 1){
            if(person.getOccupation() == 1) {
                attributesCount.setIndexedValueAt(mun,"maleWorkers",attributesCount.getIndexedValueAt(mun,"maleWorkers") + 1);
            }
            if (person.getAge() > 64){
                attributesCount.setIndexedValueAt(mun,"maleQuarters99",attributesCount.getIndexedValueAt(mun,"maleQuarters99") + 1);
            } else {
                attributesCount.setIndexedValueAt(mun,"maleQuarters64",attributesCount.getIndexedValueAt(mun,"maleQuarters64") + 1);
            }
        } else {
            if(person.getOccupation() == 1) {
                attributesCount.setIndexedValueAt(mun,"femaleWorkers",attributesCount.getIndexedValueAt(mun,"femaleWorkers") + 1);
            }
            if (person.getAge() > 64){
                attributesCount.setIndexedValueAt(mun,"femaleQuarters99",attributesCount.getIndexedValueAt(mun,"femaleQuarters99") + 1);
            } else {
                attributesCount.setIndexedValueAt(mun,"maleQuarters64",attributesCount.getIndexedValueAt(mun,"maleQuarters64") + 1); //females and males are on the same group!!
            }
        }
        return attributesCount;
    }

    public static TableDataSet updateCountersDwelling (Dwelling dwelling, TableDataSet attributesCount,int mun, int[] yearBrackets, int[] sizeBrackets){
        /* method to update the counters with the characteristics of the generated dwelling*/
        if (dwelling.getUsage() == 1){
            attributesCount.setIndexedValueAt(mun,"ownDwellings",attributesCount.getIndexedValueAt(mun,"ownDwellings") + 1);
        } else {
            attributesCount.setIndexedValueAt(mun,"rentedDwellings",attributesCount.getIndexedValueAt(mun,"rentedDwellings") + 1);
        }
        int row = 0;
        while (dwelling.getFloorSpace() > sizeBrackets[row]){
            row++;
        }
        String name = "dwellings" + sizeBrackets[row];
        attributesCount.setIndexedValueAt(mun,name,attributesCount.getIndexedValueAt(mun,name) + 1);
        if (dwelling.getYearConstructionDE() > 0 & dwelling.getYearConstructionDE() < 10) {
            int row1 = 0;
            while (dwelling.getYearConstructionDE() > yearBrackets[row1]){
                row1++;
            }
            if (dwelling.getBuildingSize() == 1){
                String name1 = "smallDwellings" + yearBrackets[row1];
                attributesCount.setIndexedValueAt(mun,name1,attributesCount.getIndexedValueAt(mun,name1) + 1);
                attributesCount.setIndexedValueAt(mun,"smallDwellings",attributesCount.getIndexedValueAt(mun,"smallDwellings") + 1);
            } else {
                String name1 = "mediumDwellings" + yearBrackets[row1];
                attributesCount.setIndexedValueAt(mun,name1,attributesCount.getIndexedValueAt(mun,name1) + 1);
                attributesCount.setIndexedValueAt(mun,"mediumDwellings",attributesCount.getIndexedValueAt(mun,"mediumDwellings") + 1);
            }
        }

        return attributesCount;
    }

    public static TableDataSet updateCountersDwellingVacant (Dwelling dwelling, TableDataSet attributesCount,int mun, int[] yearBrackets, int[] sizeBrackets){
        /* method to update the counters with the characteristics of the generated dwelling*/
        int row = 0;
        while (dwelling.getFloorSpace() > sizeBrackets[row]){
            row++;
        }
        String name = "dwellingsVacant" + sizeBrackets[row];
        attributesCount.setIndexedValueAt(mun,name,attributesCount.getIndexedValueAt(mun,name) + 1);
        if (dwelling.getYearConstructionDE() > 0 & dwelling.getYearConstructionDE() < 10) {
            int row1 = 0;
            while (dwelling.getYearConstructionDE() > yearBrackets[row1]){
                row1++;
            }
            if (dwelling.getBuildingSize() == 1){
                String name1 = "smallDwellingsVacant" + yearBrackets[row1];
                attributesCount.setIndexedValueAt(mun,name1,attributesCount.getIndexedValueAt(mun,name1) + 1);
            } else {
                String name1 = "mediumDwellingsVacant" + yearBrackets[row1];
                attributesCount.setIndexedValueAt(mun,name1,attributesCount.getIndexedValueAt(mun,name1) + 1);
            }
        }
        attributesCount.setIndexedValueAt(mun,"vacantDwellings",attributesCount.getIndexedValueAt(mun,"vacantDwellings") + 1);
        return attributesCount;
    }


}
