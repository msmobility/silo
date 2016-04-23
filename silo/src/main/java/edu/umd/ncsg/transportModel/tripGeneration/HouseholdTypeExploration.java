package edu.umd.ncsg.transportModel.tripGeneration;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.util.ResourceUtil;
import com.pb.sawdust.calculator.Function1;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.concurrent.ForkJoinPoolFactory;
import com.pb.sawdust.util.concurrent.IteratorAction;
import edu.umd.ncsg.SiloUtil;
import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.concurrent.ForkJoinPool;

/**
 * This class controls explores new definitions of household types to analyze the household travel survey
 *
 * @author Rolf Moeckel
 * @version 1.0, Jul 2nd, 2014 (College Park, MD)
 * @version 2.0, April 22, 2016 (Munich, Germany)
 * Created by IntelliJ IDEA.
 */

public class HouseholdTypeExploration {

    private static Logger logger = Logger.getLogger(HouseholdTypeExploration.class);
    private ResourceBundle rb;
    private String tripPurp;
    private TripGenerationData tgData;
    final private ArrayList<String> statistics = new ArrayList<>();


    public HouseholdTypeExploration(ResourceBundle rb, TripGenerationData tgData) {
        // Constructor
        this.rb = rb;
        this.tgData = tgData;
    }


    public void run () {
        // main run method

        if (ResourceUtil.getBooleanProperty(rb, "generate.one.dim.array.combos")) CreateOneDimensionalSegmentation.createSegmentations(rb);
        tripPurp = rb.getString("selected.trip.purpose");
        tgData.readHouseholdTravelSurvey(tripPurp);
        exploreAllHouseholdTypeSegmentations();
    }


    private void exploreAllHouseholdTypeSegmentations() {
        // define all possible segmentations of household types and -if there are sufficient records per hh type- calculate their trip rates

        logger.info("  Exploring potential household definitions for trip purpose " + tripPurp);

        int counterTest = 0;
        int size = 7;
        int workers = 5;
        int income = 12;
        int autos;
        if (tgData.getAutoMode().equalsIgnoreCase("autos")) autos = 4;
        else autos = 3;
        int region = 3;
        ArrayList<String> segmentationSize = readSegmentations(size);
        ArrayList<String> segmentationWorkers = readSegmentations(workers);
        ArrayList<String> segmentationIncome = readSegmentations(income);
        ArrayList<String> segmentationAutos = readSegmentations(autos);
        ArrayList<String> segmentationRegion = readSegmentations(region);
        int htsRecords = tgData.getNumberOfHouseholdRecords();

        ArrayList<String> hhDefinitions = new ArrayList<>();
//        String sizeToken = "1-7";
//        String workerToken = "1-5";
//        String incomeToken = "1-1.2-2.3-3.4-12";
//        String autoToken = "1-4";
        for (String sizeToken: segmentationSize) {
            String[] sizePortions = sizeToken.split("\\.");
            for (String workerToken : segmentationWorkers) {
                String[] workerPortions = workerToken.split("\\.");
                for (String incomeToken : segmentationIncome) {
                    String[] incomePortions = incomeToken.split("\\.");
                    for (String autoToken : segmentationAutos) {
                        String[] autoPortions = autoToken.split("\\.");
                        for (String regionToken : segmentationRegion) {
//                            if (!regionToken.equals("1-3")) continue;
                            String[] regionPortions = regionToken.split("\\.");
                            int numCategories = sizePortions.length * workerPortions.length * incomePortions.length *
                                    autoPortions.length * regionPortions.length;
                            if (numCategories > (htsRecords / tgData.getMinNumberOfRecords())) continue;
                            if (numCategories > 80) continue;  // todo: Speeds code up, but shouldn't be done.
                            counterTest++;

                            hhDefinitions.add(counterTest + ":" + numCategories + ":" + sizeToken + ":" + workerToken + ":" +
                                    incomeToken + ":" + autoToken + ":" + regionToken);

//                            counterUseful = analyzeSurvey(counterUseful, numCategories, sizePortions, workerPortions,
//                                    incomePortions, autoPortions, regionPortions);
//                            if (counterAll % 100000 == 0) logger.info("  Completed testing " + counterTest +
//                                    " (Total combinations created thus far " + counterAll + "). Found so far: " +
//                                    counterUseful);
                        }
                    }
                }
            }
        }
        logger.info("  Analyzing " + counterTest + " household type definitions.");

        // Multi-threading code
        Function1<String,Void> surveyAnalysis = new Function1<String,Void>() {
            public Void apply(String hhDefinitionCode) {
                analyzeSurvey(hhDefinitionCode);
                return null;
            }
        };
//
        String[] code = SiloUtil.convertArrayListToStringArray(hhDefinitions);
        Iterator<String> hhTypeDefinitionIterator = ArrayUtil.getIterator(code);
        IteratorAction<String> itTask = new IteratorAction<>(hhTypeDefinitionIterator, surveyAnalysis);
        ForkJoinPool pool = ForkJoinPoolFactory.getForkJoinPool();
        pool.execute(itTask);
        itTask.waitForCompletion();

        // write out statistics
        String fileNameStat = rb.getString("trip.rate.statistics") + "_" + tripPurp + ".csv";
        PrintWriter hhDefinitionStatistics = SiloUtil.openFileForSequentialWriting(fileNameStat, false);
        hhDefinitionStatistics.println("counter,noHhTypes,minNumberOfRecords,aveNumberOfRecords,maxNumberOfRecords,minStdDev," +
                "aveStdDev,maxStdDev,minCoeffOfVar,aveCoeffOfVar,maxCoeffOfVar");
        for (String result: statistics) hhDefinitionStatistics.println(result);
        hhDefinitionStatistics.close();
        String fileName = rb.getString("trip.rate.statistics") + "_" + tripPurp + "_hhDef.csv";
        PrintWriter hhDefinitionsFile = SiloUtil.openFileForSequentialWriting(fileName, false);
        hhDefinitionsFile.println("counter,numTypes,sizeToken,workerToken,incomeToken,autoToken,regionToken");
        for (String definition: hhDefinitions) {
            String def = definition.replace(":", ",");
            hhDefinitionsFile.println(def);
        }
        hhDefinitionsFile.close();
        logger.info("  Exploration concluded.");
    }


    private ArrayList<String> readSegmentations (int dimension) {
        // read possible segmentations for dimension

        String fileName = rb.getString("one.dim.array.combos.files") + "_" + dimension + ".csv";
        TableDataSet dim = SiloUtil.readCSVfile(fileName);
        ArrayList<String> al = new ArrayList<>();
        for (int row = 1; row <= dim.getRowCount(); row++) {
            al.add(dim.getStringValueAt(row, "definition"));
        }
        return al;
    }


    private void analyzeSurvey (String code) {
        // analyze survey for given household type definition

        String[] codeSegments = code.split(":");
        int counter = Integer.parseInt(codeSegments[0]);
        int numCategories = Integer.parseInt(codeSegments[1]);
        String sizeToken = codeSegments[2];
        String[] sizePortions = sizeToken.split("\\.");
        String workerToken = codeSegments[3];
        String[] workerPortions = workerToken.split("\\.");
        String incomeToken = codeSegments[4];
        String[] incomePortions = incomeToken.split("\\.");
        String autoToken = codeSegments[5];
        String[] autoPortions = autoToken.split("\\.");
        String regionToken = codeSegments[6];
        String[] regionPortions = regionToken.split("\\.");
        TableDataSet hhTypeDef = tgData.createHouseholdTypeTableDataSet(numCategories, sizePortions, workerPortions,
                incomePortions, autoPortions, regionPortions);
        int[] hhTypeArray = tgData.defineHouseholdTypeOfEachSurveyRecords(tgData.getAutoMode(), hhTypeDef);
        if (hhTypeArray[0] != -1) {
            processSurvey(hhTypeDef, hhTypeArray, counter);
        }
    }


    private void processSurvey (TableDataSet hhTypeDef, int[] hhTypeArray, int counter) {
        // Read household travel survey

        HashMap<String, Integer[]> tripsByHhTypeAndPurpose = tgData.collectTripFrequencyDistribution(hhTypeArray);
        evaluateTripFrequencies(hhTypeDef, counter, tripsByHhTypeAndPurpose);
    }


    private void evaluateTripFrequencies (TableDataSet hhTypeDef, int counter,
                                          HashMap<String, Integer[]> tripsByHhTypeAndPurpose) {
        // evaluate statistical significance of calculated trip rates

        int[] numberOfRecords = new int[hhTypeDef.getRowCount() * tripPurposes.values().length];
        float[] variance = new float[hhTypeDef.getRowCount() * tripPurposes.values().length];
        float[] coefficientOfVariation = new float[hhTypeDef.getRowCount() * tripPurposes.values().length];
        int pos = 0;
        for (int hhType: hhTypeDef.getColumnAsInt("hhType")) {
            for (tripPurposes purpose: tripPurposes.values()) {
                String token = String.valueOf(hhType) + "_" + purpose.toString();
                Integer[] thisTripFrequency = tripsByHhTypeAndPurpose.get(token);
                if (SiloUtil.getSum(thisTripFrequency) == 0) continue;
                int[] trpFrq = SiloUtil.convertIntegerToInt(thisTripFrequency);
                int[] tripRecords = convertTripRecordsToArray(trpFrq);
                float mean = SiloUtil.getMean(tripRecords);
                numberOfRecords[pos] = SiloUtil.getSum(trpFrq);
                variance[pos] = SiloUtil.getVariance(tripRecords);
                if (mean != 0) coefficientOfVariation[pos] = (float) (Math.sqrt(variance[pos]) / mean * 100);
                pos++;
            }
        }
        float minStdDev = getSmallestValNonEqualZero(variance);
        if (minStdDev > 0) minStdDev = (float) Math.sqrt(minStdDev);
        float maxStdDev = SiloUtil.getHighestVal(variance);
        if (maxStdDev > 0) maxStdDev = (float) Math.sqrt(maxStdDev);
        float aveStdDev = (float) Math.sqrt(SiloUtil.getWeightedMean(variance, numberOfRecords));
        String result = counter + "," +hhTypeDef.getRowCount() + "," +
                SiloUtil.getSmallestVal(numberOfRecords) + "," + SiloUtil.getMean(numberOfRecords) + "," +
                SiloUtil.getHighestVal(numberOfRecords) + "," +
                minStdDev + "," + aveStdDev + "," + maxStdDev + "," +
                getSmallestValNonEqualZero(coefficientOfVariation) + "," +
                SiloUtil.getWeightedMean(coefficientOfVariation, numberOfRecords) + "," +
                SiloUtil.getHighestVal(coefficientOfVariation);
        synchronized (statistics) {
            statistics.add(result);
        }
    }


    public static float getSmallestValNonEqualZero(float[] array) {
        // return smallest number in array
        float small = Float.MAX_VALUE;
        for (float num: array) if (num != 0) small = Math.min(small, num);
        if (small == Float.MAX_VALUE) small = 0;
        return small;
    }


    private int[] convertTripRecordsToArray (int[] trpFrq) {
        // convert trip frequency array trpFrq into long ArrayList with trips

        ArrayList<Integer> tripRecords = new ArrayList<>();
        for (int numberOfTrips = 0; numberOfTrips < trpFrq.length; numberOfTrips++) {
            for (int trips = 0; trips < trpFrq[numberOfTrips]; trips++) tripRecords.add(numberOfTrips);
        }
        return SiloUtil.convertArrayListToIntArray(tripRecords);
    }


//    private void writeSegmentationFile(int segCounter, TableDataSet hhTypeDef) {
//        // write file that defines household segmentation
//
//        String fileName = rb.getString("household.type.definition") + "_" + segCounter + "_" + tgData.getAutoMode() + ".csv";
//        PrintWriter pw = mstmUtilities.openFileForSequentialWriting(fileName);
//        if (tgData.getAutoMode().equalsIgnoreCase("autos")) {
//            pw.println("hhType,size_l,size_h,workers_l,workers_h,income_l,income_h,autos_l,autos_h," +
//                    "region_l,region_h");
//        } else {
//            pw.println("hhType,size_l,size_h,workers_l,workers_h,income_l,income_h,autoSuff_l,autoSuff_h," +
//                    "region_l,region_h");
//        }
//        for (int hhType: hhTypeDef.getColumnAsInt("hhType")) {
//            pw.println(hhType + "," + hhTypeDef.getIndexedValueAt(hhType, "size_l") + "," +
//                    hhTypeDef.getIndexedValueAt(hhType, "size_h")+ "," +
//                    hhTypeDef.getIndexedValueAt(hhType, "workers_l") + "," +
//                    hhTypeDef.getIndexedValueAt(hhType, "workers_h") + "," +
//                    hhTypeDef.getIndexedValueAt(hhType, "income_l") + "," +
//                    hhTypeDef.getIndexedValueAt(hhType, "income_h")+ "," +
//                    hhTypeDef.getIndexedValueAt(hhType, "autos_l") + "," +
//                    hhTypeDef.getIndexedValueAt(hhType, "autos_h")+ "," +
//                    hhTypeDef.getIndexedValueAt(hhType, "region_l") + "," +
//                    hhTypeDef.getIndexedValueAt(hhType, "region_h"));
//        }
//        pw.close();
//    }

}
