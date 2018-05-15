package de.tum.bgu.msm;

import com.pb.common.datafile.CSVFileWriter;
import com.pb.common.datafile.TableDataFileReader;
import com.pb.common.datafile.TableDataSet;
import com.pb.common.matrix.Matrix;
import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.data.summarizeDataCblcm;
import de.tum.bgu.msm.events.EventTypes;
import de.tum.bgu.msm.events.IssueCounter;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.TableDataFileReader2;
import omx.OmxMatrix;
import omx.hdf5.OmxHdf5Datatype;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.*;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Utilities used by the SILO Model
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 8 December 2009 in Santa Fe
 **/

public class SiloUtil {

    private static Random rand;
    public static int trackHh;
    public static int trackPp;
    public static int trackDd;
    public static int trackJj;
    public static PrintWriter trackWriter;
    private static ResourceBundle rb;
    private static HashMap rbHashMap;

    static Logger logger = Logger.getLogger(SiloUtil.class);

    public static ResourceBundle siloInitialization(String resourceBundleNames, Implementation implementation) {
        File propFile = new File(resourceBundleNames);
        rb = ResourceUtil.getPropertyBundle(propFile);
        Properties.initializeProperties(rb, implementation);
        if (Properties.get().main.runSynPop){
            PropertiesSynPop.initializePropertiesSynPop(rb, implementation);
        }
        rbHashMap = ResourceUtil.changeResourceBundleIntoHashMap(rb);
        SummarizeData.openResultFile(rb);
        SummarizeData.resultFileSpatial("open");

        // create scenarios output directory if it does not exist yet
        createDirectoryIfNotExistingYet(Properties.get().main.baseDirectory + "scenOutput/" + Properties.get().main.scenarioName);
        // copy properties file into scenarios directory
        String[] prop = resourceBundleNames.split("/");

//        copyFile(baseDirectory + resourceBundleNames[0], baseDirectory + "scenOutput/" + scenarioName + "/" + prop[prop.length-1]);
        // I don't see how this can work.  resourceBundleNames[0] is already the full path name, so if you prepend "baseDirectory"
        // and it is not empty, the command cannot possibly work.  It may have worked by accident in the past if everybody
        // had the resourceBundle directly at the JVM file system root.  kai (and possibly already changed by dz before), aug'16
        copyFile(resourceBundleNames, Properties.get().main.baseDirectory + "scenOutput/" + Properties.get().main.scenarioName + "/" + prop[prop.length-1]);

        initializeRandomNumber(Properties.get().main.randomSeed);
        trackingFile("open");
        return rb;
    }


    public static HashMap getRbHashMap() {
        return rbHashMap;
    }


    public static void createDirectoryIfNotExistingYet (String directory) {
        File file = new File (directory);
        if (!file.exists()) {
            logger.info("   Creating Directory: "+directory);
            boolean outputDirectorySuccessfullyCreated = file.mkdir();
            if (!outputDirectorySuccessfullyCreated) logger.error("Could not create scenarios directory " + directory);
        }
    }


    public static void initializeRandomNumber(int seed) {
        if (seed == -1)
            rand = new Random();
        else
            rand = new Random(seed);
    }


    public static Random getRandomObject() {
        return rand;
    }

    public static float getRandomNumberAsFloat() {
        return rand.nextFloat();
    }

    public static double getRandomNumberAsDouble() {
        return rand.nextDouble();
    }


    public static int[] convertIntegerArrayListToArray (ArrayList<Integer> al) {
        Integer[] temp = al.toArray(new Integer[al.size()]);
        int[] list = new int[temp.length];
        for (int i = 0; i < temp.length; i++) list[i] = temp[i];
        return list;
    }


    public static String[] convertStringArrayListToArray (ArrayList<String> al) {
        String[] temp = al.toArray(new String[al.size()]);
        String[] list = new String[temp.length];
        System.arraycopy(temp, 0, list, 0, temp.length);
        return list;
    }


    public static int findPositionInArray (int element, int[] arr){
        // return index position of element in array arr
        int ind = -1;
        for (int a = 0; a < arr.length; a++) if (arr[a] == element) ind = a;
        if (ind == -1) logger.error ("Could not find element " + element +
                " in array (see method <findPositionInArray> in class <SiloUtil>");
        return ind;
    }

    public static int findPositionInArray (String element, String[] arr){
        // return index position of element in array arr
        int ind = -1;
        for (int a = 0; a < arr.length; a++) if (arr[a].equalsIgnoreCase(element)) ind = a;
        if (ind == -1) logger.error ("Could not find element " + element +
                " in array (see method <findPositionInArray> in class <SiloUtil>");
        return ind;
    }


    public static <T> int findPositionInArray (T element, T array[]) {
        int ind = -1;
        for (int a = 0; a < array.length; a++) if (array[a].equals(element)) ind = a;
        if (ind == -1) logger.error ("Could not find element " + element +
                " in array (see method <findPositionInArray> in class <SiloUtil>");
        return ind;
    }


    public static TableDataSet readCSVfile (String fileName) {
        // read csv file and return as TableDataSet
        File dataFile = new File(fileName);
        TableDataSet dataTable;
        boolean exists = dataFile.exists();
        if (!exists) {
            final String msg = "File not found: " + fileName;

		    logger.error(msg);
        throw new RuntimeException(msg) ;
        }
        try {
            TableDataFileReader reader = TableDataFileReader.createReader(dataFile);
            dataTable = reader.readFile(dataFile);
            reader.close();
        } catch (Exception e) {
            logger.error("Error reading file " + dataFile);
            throw new RuntimeException(e);
        }
        return dataTable;
    }

    public static TableDataSet readCSVfile2 (String fileName) {
        // read csv file and return as TableDataSet
        File dataFile = new File(fileName);
        TableDataSet dataTable;
        boolean exists = dataFile.exists();
        if (!exists) {
            final String msg = "File not found: " + fileName;
            logger.error(msg);
//            System.exit(1);
            throw new RuntimeException(msg) ;
            // from the perspective of the junit testing infrastructure, a "System.exit(...)" is not a test failure ... and thus not detected.  kai, aug'16
        }
        try {
//            TableDataFileReader reader = TableDataFileReader.createReader(dataFile);
//            dataTable = reader.readFile(dataFile);
            TableDataFileReader2 reader = TableDataFileReader2.createReader2(dataFile);
            dataTable = reader.readFile(dataFile);
            reader.close();
        } catch (Exception e) {
            logger.error("Error reading file " + dataFile);
            throw new RuntimeException(e);
        }
        return dataTable;
    }



    public static boolean checkIfFileExists (String fileName) {
        File dataFile = new File(fileName);
        return dataFile.exists();
    }


    public static void trackingFile(String action) {
        // open or close track writer to track persons, households or dwellings

        switch (action) {
            case "open":
                // track households and/or persons
                trackHh = ResourceUtil.getIntegerProperty(rb, "track.household");
                trackPp = ResourceUtil.getIntegerProperty(rb, "track.person");
                trackDd = ResourceUtil.getIntegerProperty(rb, "track.dwelling");
                trackJj = ResourceUtil.getIntegerProperty(rb, "track.job");
                if (trackHh == -1 && trackPp == -1 && trackDd == -1 && trackJj == -1) return;
                String fileName = ResourceUtil.getProperty(rb, "track.file.name");
                String baseDirectory = Properties.get().main.baseDirectory;
                int startYear = Properties.get().main.startYear;
                trackWriter = openFileForSequentialWriting(baseDirectory + fileName + ".txt", startYear != Properties.get().main.implementation.BASE_YEAR);
                if (trackHh != -1) trackWriter.println("Tracking household " + trackHh);
                if (trackPp != -1) trackWriter.println("Tracking person " + trackPp);
                if (trackDd != -1) trackWriter.println("Tracking dwelling " + trackDd);
                if (trackJj != -1) trackWriter.println("Tracking job " + trackJj);
                break;
            case "close":
                if (trackHh != -1 || trackPp != -1 || trackDd != -1 || trackJj != -1) trackWriter.close();
                break;
            default:
                if (trackHh != -1 || trackPp != -1 || trackDd != -1 || trackJj != -1) trackWriter.println(action);
                break;
        }
    }


    public static PrintWriter openFileForSequentialWriting(String fileName, boolean appendFile) {
        // open file and return PrintWriter object

        File outputFile = new File(fileName);
        if(outputFile.getParentFile() != null) {
            outputFile.getParentFile().mkdirs();
        }
        try {
            FileWriter fw = new FileWriter(outputFile, appendFile);
            BufferedWriter bw = new BufferedWriter(fw);
            return new PrintWriter(bw);
        } catch (IOException e) {
            logger.error("Could not open file <" + fileName + ">.");
            return null;
        }
    }


    public static void writeTableDataSet (TableDataSet data, String fileName) {
        try {
            CSVFileWriter cfwWriter = new CSVFileWriter();
            cfwWriter.writeFile(data, new File(fileName));
            cfwWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            logger.error("Could not write TableDataSet to file " + fileName + ".");
        }
    }

    //TODO REFACTOR SELECT METHODS TO USE GENERICS
    public static int select (double[] probabilities) {
        // select item based on probabilities (for zero-based double array)
       return select(probabilities, getSum(probabilities), rand);
    }

    public static int select(double[] probabilities, Random random) {
        return select(probabilities, getSum(probabilities), random);
    }

    public static int select (double[] probabilities, double sumProb) {
        // select item based on probabilities (for zero-based double array)
        return select(probabilities, getSum(probabilities), rand);
    }

    public static int select (double[] probabilities, double sumProb, Random random) {
        // select item based on probabilities (for zero-based double array)
        double selPos = sumProb * random.nextFloat();
        double sum = 0;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            if (sum > selPos) {
                return i;
            }
        }
        return probabilities.length - 1;
    }

    public static int select (float[] probabilities) {
        // select item based on probabilities (for zero-based float array)
        float selPos = getSum(probabilities) * getRandomNumberAsFloat();
        float sum = 0;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            if (sum > selPos) {
                return i;
            }
        }
        return probabilities.length - 1;
    }

    public static int select (double[] probabilities, int[] id) {
        // select item based on probabilities (for zero-based float array)
        double selPos = getSum(probabilities) * getRandomNumberAsFloat();
        double sum = 0;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            if (sum > selPos) {
                //return i;
                return id[i];
            }
        }
        return id[probabilities.length - 1];
    }


    public static int select (float[] probabilities, int length, int[] name){
        //select item based on probabilities and return the name
        //probabilities and name have more items than the required (max number of required items is set on "length")
        double selPos = getSum(probabilities) * getRandomNumberAsFloat();
        double sum = 0;
        for (int i = 0; i < length; i++) {
            sum += probabilities[i];
            if (sum > selPos) {
                //return i;
                return name[i];
            }
        }
        return name[length - 1];
    }


    public static <T> T select(Map<T, ? extends Number> mappedProbabilities) {
        // select item based on probabilities (for mapped double probabilities)
        return select(mappedProbabilities, getSum(mappedProbabilities.values()));
    }

    public static <T> T select(Map<T, ? extends Number> mappedProbabilities, double sum) {
        // select item based on probabilities (for mapped double probabilities)
        double selectedWeight = rand.nextDouble() * sum;
        double select = 0;
        for (Map.Entry<T, ? extends Number> entry : mappedProbabilities.entrySet()) {
            select += entry.getValue().doubleValue();
            if (select > selectedWeight) {
                return entry.getKey();
            }
        }
        logger.info("Error selecting item from weighted probabilities");
        return null;
    }


    private static double getSum(Collection<? extends Number> values) {
        double sm = 0;
        for (Number value : values) {
            sm += value.doubleValue();
        }
        return sm;
    }

    public static double[] convertProbability (double[] probabilities){
        //method to return the probability in percentage
        double sum = 0;
        for (int row = 0; row < probabilities.length; row++){
            sum++;
        }
        for (int row = 0; row < probabilities.length; row++) {
            probabilities[row] = probabilities[row]/sum*1000;
        }
        return probabilities;
    }


    public static int select (int upperRange) {
        // select item based on equal probabilities between 0 and upperRange
        int selected = (int) (upperRange * getRandomNumberAsFloat());
        return Math.max(1, selected);
    }


    public static int getSum (int[] array) {
        // return sum of all elements in array
        int sum = 0;
        for (int val: array) sum += val;
        return sum;
    }


    public static float getSum (float[] array) {
        // return sum of all elements in array
        float sum = 0;
        for (float val: array) sum += val;
        return sum;
    }


    public static double getSum (double[] array) {
        // return sum of all elements in array
        double sum = 0;
        for (double val: array) sum += val;
        return sum;
    }


    public static Integer getSum (Integer[] array) {
        Integer sm = 0;
        for (Integer value: array) sm += value;
        return sm;
    }


    public static float getSum(float[][][] array) {
        // return array of three-dimensional double array
        float sm = 0;
        for (float[][] anArray : array) {
            for (int i = 0; i < array[0][0].length; i++) {
                for (int j = 0; j < array[0].length; j++) sm += anArray[i][j];
            }
        }
        return sm;
    }


    public static float getMean (int[] values) {
        // calculate mean (average) value

        float sm = 0;
        for (int i: values) sm += i;
        return (sm / values.length);
    }


    public static float getWeightedSum(float[] weights, float[] values){
        // calculate weighted sum given two sets of arrays.
        float ws = 0;
        for (int i = 0; i < weights.length; i++) {
            ws += weights[i]*values[i];
        }
        return ws;
    }


    public static float getWeightedSum(float[] weights, float[] values, int[] positions){
        // optimization to calculate weighted sum given two sets of arrays
        // The two sets of arrays are complete, but only some cells are different than zero.
        // The third array indicates the location of the cells different than zero.
        float ws = 0;
        for (int position : positions) {
            ws += weights[position - 1] * values[position - 1];
        }
        return ws;
    }


    public static float getWeightedSum(double[] weights, float[] values, int[] positions, int total_positions){
        // optimization to calculate weighted sum given two sets of arrays
        // The two sets of arrays are complete, but only some cells are different than zero.
        // The third array indicates the location of the cells different than zero.
        float ws = 0;
        for (int i = 0; i < total_positions; i++) {
            ws += weights[positions[i]-1]*values[positions[i]-1];
        }
        return ws;
    }


    public static float getVariance (int[] values) {
        // calculate sample variance of array values[]

        if (values.length <= 1) {
            logger.error("Cannot calculate variance for array with length " + values.length);
            return 0;
        }
        double sm = 0;
        float mean = getMean(values);
        for (int i: values) {
            sm += Math.pow((i - mean), 2);
        }
        return (float) (sm / (values.length-1));
    }


    public static String[] convertArrayListToStringArray (ArrayList<String> al) {
        String[] list = new String[al.size()];
        for (int i = 0; i < al.size(); i++) list[i] = al.get(i);
        return list;
    }


    public static int[] convertArrayListToIntArray (ArrayList<Integer> al) {
        int[] list = new int[al.size()];
        for (int i = 0; i < al.size(); i++) list[i] = al.get(i);
        return list;
    }


    public static int[] convertIntegerToInt (Integer[] values) {
        int[] intValues = new int[values.length];
        for (int i = 0; i < values.length; i++) intValues[i] = values[i];
        return intValues;
    }


    public static float getMedian (int[] array) {
        // return median value

        if (array.length == 0) return 0;
        Arrays.sort(array);
        float median;
        if (array.length % 2 == 0)
            median = ((float) array[array.length / 2] + (float) array[array.length / 2 - 1]) / 2;
        else
            median = (float) array[array.length / 2];
        return median;
    }


    public static void finish (SiloModelContainer modelContainer) {
        SummarizeData.resultFile("close");
        trackingFile("close");
        if (modelContainer.getDdOverwrite().traceOverwriteDwellings()) modelContainer.getDdOverwrite().finishOverwriteTracer();
        if (IssueCounter.didFindIssues()) logger.warn("Found issues, please check warnings in logging statements.");
    }


    public static float rounder(float value, int digits) {
        // rounds value to digits behind the decimal point

        return Math.round(value * Math.pow(10, digits) + 0.5)/(float) Math.pow(10, digits);
    }

    /**
    scale double value map so that largest value equals maxVal
    **/
    public static void scaleMap (Map<?, Double> doubleMap, float maxVal) {
        double highestValTemp = Double.MIN_VALUE;
        for (double val: doubleMap.values()) {
            highestValTemp = Math.max(val, highestValTemp);
        }
        final double highestVal = highestValTemp;
        doubleMap.replaceAll ((k,v) -> (v * maxVal * 1.) / (highestVal * 1.));
    }

    public static float[] scaleArray (float[] array, float maxVal) {
        // scale float array so that largest value equals maxVal

        float highestVal = Float.MIN_VALUE;
        for (float val: array) highestVal = Math.max(val, highestVal);
        for (int i = 0; i < array.length; i++) array[i] = (float) ((array[i] * maxVal * 1.) / (highestVal * 1.));
        return array;
    }


    public static double[] scaleArray (double[] array, double maxVal) {
        // scale double array so that largest value equals maxVal

        double highestVal = Double.MIN_VALUE;
        for (double val: array) highestVal = Math.max(val, highestVal);
        for (int i = 0; i < array.length; i++) array[i] = (array[i] * maxVal / highestVal);
        return array;
    }


    public static int[] setArrayToValue (int[] anArray, int value) {
        // fill one-dimensional integer array with value

        for (int i = 0; i < anArray.length; i++) anArray[i] = value;
        return anArray;
    }


    public static float[] setArrayToValue (float[] anArray, float value) {
        // fill one-dimensional integer array with value

        for (int i = 0; i < anArray.length; i++) anArray[i] = value;
        return anArray;
    }


    public static long[] setArrayToValue (long[] anArray, long value) {
        // fill one-dimensional integer array with value

        for (int i = 0; i < anArray.length; i++) anArray[i] = value;
        return anArray;
    }


    public static int[][] setArrayToValue (int[][] anArray, int value) {
        // fill two-dimensional integer array with value

        for (int i = 0; i < anArray.length; i++) {
            for (int j = 0; j < anArray[i].length; j++)  anArray[i][j] = value;
        }
        return anArray;
    }


    public static float[][] setArrayToValue (float[][] anArray, float value) {
        // fill two-dimensional float array with value

        for (int i = 0; i < anArray.length; i++) {
            for (int j = 0; j < anArray[i].length; j++) anArray[i][j] = value;
        }
        return anArray;
    }


    public static int[][][] setArrayToValue (int[][][] anArray, int value) {
        // fill three-dimensional integer array with value

        for (int i = 0; i < anArray.length; i++) {
            for (int j = 0; j < anArray[i].length; j++) {
                for (int k = 0; k < anArray[i][j].length; k++) anArray[i][j][k] = value;
            }
        }
        return anArray;
    }


    public static boolean[] createArrayWithValue (int arrayLength, boolean value) {
        // fill one-dimensional boolean array with value

        boolean[] anArray = new boolean[arrayLength];
        for (int i = 0; i < anArray.length; i++) anArray[i] = value;
        return anArray;
    }


    public static float[] createArrayWithValue (int arrayLength, float value) {
        // fill one-dimensional float array with value

        float[] anArray = new float[arrayLength];
        for (int i = 0; i < anArray.length; i++) anArray[i] = value;
        return anArray;
    }


    public static double[] createArrayWithValue (int arrayLength, double value) {
        // fill one-dimensional double array with value

        double[] anArray = new double[arrayLength];
        for (int i = 0; i < anArray.length; i++) anArray[i] = value;
        return anArray;
    }


    public static int[] createArrayWithValue (int arrayLength, int value) {
        // fill one-dimensional boolean array with value
        int[] anArray = new int[arrayLength];
        Arrays.fill(anArray, value);
        return anArray;
    }


    public static int[] expandArrayByOneElement (int[] existing, int addElement) {
        // create new array that has length of existing.length + 1 and copy values into new array
        int[] expanded = new int[existing.length + 1];
        System.arraycopy(existing, 0, expanded, 0, existing.length);
        expanded[expanded.length - 1] = addElement;
        return expanded;
    }


    public static int[] removeOneElementFromZeroBasedArray(int[] array, int elementIndex) {
        // remove elementIndex'th element from array
        if (elementIndex < 0 || elementIndex > array.length-1) logger.error("Cannot remove element "+elementIndex +
                " from zero-based int array with length " + array.length);
        int[] reduced = new int[array.length - 1];
        if (elementIndex == 0) {
            // remove first element
            System.arraycopy(array, 1, reduced, 0, reduced.length);
        } else if (elementIndex == array.length - 1) {
            // remove last element
            System.arraycopy(array, 0, reduced, 0, reduced.length);
        } else {
            System.arraycopy(array, 0, reduced, 0, elementIndex);
            System.arraycopy(array, elementIndex + 1, reduced, elementIndex, reduced.length - elementIndex);
        }
        return reduced;
    }


    public static String[] removeOneElementFromZeroBasedArray(String[] array, int elementIndex) {
        // remove elementIndex'th element from array
        if (elementIndex < 0 || elementIndex > array.length-1) logger.error("Cannot remove element "+elementIndex +
                " from zero-based int array with length " + array.length);
        String[] reduced = new String[array.length - 1];
        if (elementIndex == 0) {
            // remove first element
            System.arraycopy(array, 1, reduced, 0, reduced.length);
        } else if (elementIndex == array.length - 1) {
            // remove last element
            System.arraycopy(array, 0, reduced, 0, reduced.length);
        } else {
            System.arraycopy(array, 0, reduced, 0, elementIndex);
            System.arraycopy(array, elementIndex + 1, reduced, elementIndex, reduced.length - elementIndex);
        }
        return reduced;
    }


    public static double[] removeOneElementFromZeroBasedArray(double[] array, int elementIndex) {
        // remove elementIndex'th element from array
        if (elementIndex < 0 || elementIndex > array.length-1) logger.error("Cannot remove element "+elementIndex +
                " from zero-based int array with length " + array.length);
        double[] reduced = new double[array.length - 1];
        if (elementIndex == 0) {
            // remove first element
            System.arraycopy(array, 1, reduced, 0, reduced.length);
        } else if (elementIndex == array.length - 1) {
            // remove last element
            System.arraycopy(array, 0, reduced, 0, reduced.length);
        } else {
            System.arraycopy(array, 0, reduced, 0, elementIndex);
            System.arraycopy(array, elementIndex + 1, reduced, elementIndex, reduced.length - elementIndex);
        }
        return reduced;
    }


    public static TableDataSet addIntegerColumnToTableDataSet(TableDataSet table, String label){
        int[] dummy3 = SiloUtil.createArrayWithValue(table.getRowCount(),0);
        table.appendColumn(dummy3,label);
        return table;
    }


    public static TableDataSet addIntegerColumnToTableDataSet(TableDataSet table, String label, int length){
        int[] dummy3 = SiloUtil.createArrayWithValue(length,0);
        table.appendColumn(dummy3,label);
        return table;
    }

    public static int getHighestVal(int[] array) {
        // return highest number in int array
        int high = Integer.MIN_VALUE;
        for (int num: array) high = Math.max(high, num);
        return high;
    }


    public static float getHighestVal(float[] array) {
        // return highest number in float array
        float high = Float.MIN_VALUE;
        for (float num: array) high = Math.max(high, num);
        return high;
    }


    public static float getWeightedMean (float[] values, int[] weights) {
        // calculate mean (average) value

        if (values.length != weights.length) logger.error("values[] and weights[] have unequal length in getWeightedMean()");
        float sm = 0;
        for (int i = 0; i < values.length; i++) sm += values[i] * weights[i];
        return (sm / getSum(weights));
    }


    public static int getHighestVal(String[] array) {
        // return highest number in String array
        int high = Integer.MIN_VALUE;
        for (String num: array) high = Math.max(high, Integer.parseInt(num));
        return high;
    }


    public static int getLowestVal(String[] array) {
        // return highest number in String array
        int low = Integer.MAX_VALUE;
        for (String num: array) low = Math.min(low, Integer.parseInt(num));
        return low;
    }


    public static int getLowestVal(int[] array) {
        // return highest number in String array
        int low = Integer.MAX_VALUE;
        for (int num: array) low = Math.min(low, num);
        return low;
    }


    public static boolean containsElement (int[] array, int value) {
        // returns true if array contains value, otherwise false
        boolean found = false;
        for (int i: array) if (i == value) found = true;
        return found;
    }


    public static boolean containsElement (ArrayList<Integer> array, int value) {
        // returns true if array contains value, otherwise false
        boolean found = false;
        for (int i: array) if (i == value) found = true;
        return found;
    }

    public static double sumProduct(double[] a, int[] b){
        double sum = 0;
        for (int i = 0; i < a.length; i++){
            sum = sum + a[i]*b[i];
        }


        return sum;
    }

    public static void deleteFile (String fileName) {
        // delete file with name fileName
        File dataFile = new File(fileName);
        boolean couldDelete = dataFile.delete();
        if (!couldDelete) logger.warn("Could not delete file " + fileName);
    }


    public static void copyFile (String source, String destination) {
        File src = new File (source);
        File dst = new File (destination);
        try {
            Files.copy(src.toPath(), dst.toPath(), REPLACE_EXISTING);
        } catch (Exception e) {
            final String msg = "Unable to copy properties file " + source + " to scenarios output directory.";
		logger.warn(msg);
            throw new RuntimeException(msg) ;
            // need to throw exception since otherwise the code will not fail here but at some point later.  kai, aug'16
        }
    }


    public static void copyDirectoryAndFiles (String sourceDirectory, String destinationDirectory) {
        // source: http://www.mkyong.com/java/how-to-copy-directory-in-java/
        File srcFolder = new File(sourceDirectory);
        File destFolder = new File(destinationDirectory);

        //make sure source exists
        if(!srcFolder.exists()) {
            System.out.println("Directory " + sourceDirectory + " does not exist and could not be copied.");
            System.exit(0);
        }else{
            try {
                copyFolder(srcFolder,destFolder);
            } catch(IOException e){
                e.printStackTrace();
                System.exit(0);
            }
        }
    }


    public static void copyFolder(File src, File dest)
        // source: http://www.mkyong.com/java/how-to-copy-directory-in-java/
            throws IOException {
        if(src.isDirectory()){
            // if directory does not exist, create it
            if(!dest.exists()){
                boolean directoryCreated = dest.mkdir();
                if (!directoryCreated) logger.warn("Could not create directory for copying: " + dest.toString());
            }
            // get list of directory files
            String files[] = src.list();
            for (String file: files) {
                //construct the src and dest file structure
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                copyFolder(srcFile, destFile);
            }
        } else {
            // if file, then copy it
            // Use bytes stream to support all file types
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            //copy the file content in bytes
            while ((length = in.read(buffer)) > 0){
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
        }
    }


    public static Matrix convertOmxToMatrix (OmxMatrix omxMatrix) {
        // convert OMX matrix into java matrix

        OmxHdf5Datatype.OmxJavaType type = omxMatrix.getOmxJavaType();
        String name = omxMatrix.getName();
        int[] dimensions = omxMatrix.getShape();
        if (type.equals(OmxHdf5Datatype.OmxJavaType.FLOAT)) {
            float[][] fArray = (float[][]) omxMatrix.getData();
            Matrix mat = new Matrix(name, name, dimensions[0], dimensions[1]);
            for (int i = 0; i < dimensions[0]; i++)
                for (int j = 0; j < dimensions[1]; j++)
                    mat.setValueAt(i + 1, j + 1, fArray[i][j]);
            return mat;
        } else if (type.equals(OmxHdf5Datatype.OmxJavaType.DOUBLE)) {
            double[][] dArray = (double[][]) omxMatrix.getData();
            Matrix mat = new Matrix(name, name, dimensions[0], dimensions[1]);
            for (int i = 0; i < dimensions[0]; i++)
                for (int j = 0; j < dimensions[1]; j++)
                    mat.setValueAt(i + 1, j + 1, (float) dArray[i][j]);
            return mat;
        } else {
            logger.info("OMX Matrix type " + type.toString() + " not yet implemented. Program exits.");
            System.exit(1);
            return null;
        }
    }


    public static int[] createIndexArray (int[] array) {
        // create indexArray for array

        int[] index = new int[getHighestVal(array) + 1];
        for (int i = 0; i < array.length; i++) {
            index[array[i]] = i;
        }
        return index;
    }


    public static int[] idendifyUniqueValues(int[] array) {
        // return array of unique values found in array
        ArrayList<Integer> values = new ArrayList<>();
        for (int value: array) {
            if (!values.contains(value)) values.add(value);
        }
        return convertIntegerArrayListToArray(values);
    }


    public static TableDataSet initializeTableDataSet(TableDataSet tableDataSet, String[] labels, int[] ids) {
        //method to initialize the error matrix
        tableDataSet.appendColumn(ids, "ID");
        for (String attribute : labels){
            float[] dummy = SiloUtil.createArrayWithValue(tableDataSet.getRowCount(), 0f);
            tableDataSet.appendColumn(dummy, attribute);
        }
        tableDataSet.buildIndex(tableDataSet.getColumnPosition("ID"));
        return tableDataSet;
    }

    static public String customFormat(String pattern, double value ) {
        // function copied from: http://docs.oracle.com/javase/tutorial/java/data/numberformat.html
        // 123456.789 ###,###.###  123,456.789 The pound sign (#) denotes a digit, the comma is a placeholder for the grouping separator, and the period is a placeholder for the decimal separator.
        // 123456.789 ###.##       123456.79   The value has three digits to the right of the decimal point, but the pattern has only two. The format method handles this by rounding up.
        // 123.78     000000.000   000123.780  The pattern specifies leading and trailing zeros, because the 0 character is used instead of the pound sign (#).
        // 12345.67   $###,###.### $12,345.67  The first character in the pattern is the dollar sign ($). Note that it immediately precedes the leftmost digit in the formatted output.
        DecimalFormat myFormatter = new DecimalFormat(pattern);
        return myFormatter.format(value);
    }

    public static int[] obtainColumnFromArray(int[][] array, int rows, int index){
        int[] column = new int[rows];
        for(int i = 0; i < column.length; i++){
            column[i] = array[i][index];
        }
        return column;
    }


static void closeAllFiles (long startTime, ResourceBundle rbLandUse, Properties properties) {
	// run this method whenever SILO closes, regardless of whether SILO completed successfully or SILO crashed
	trackingFile("close");
	SummarizeData.resultFile("close");
	SummarizeData.resultFileSpatial("close");
	float endTime = rounder(((System.currentTimeMillis() - startTime) / 60000), 1);
	int hours = (int) (endTime / 60);
	int min = (int) (endTime - 60 * hours);
	logger.info("Runtime: " + hours + " hours and " + min + " minutes.");
	if (Properties.get().main.trackTime) {
		String fileName = Properties.get().main.trackTimeFile;
		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
			out.println("Runtime: " + hours + " hours and " + min + " minutes.");
			out.close();
		} catch (IOException e) {
			logger.warn("Could not add run-time statement to time-tracking file.");
		}
	}
}


static boolean modelStopper (String action) {
	// provide option for a clean model stop after every simulation period is completed
	String fileName = Properties.get().main.baseDirectory + "status.csv";
	if (action.equalsIgnoreCase("initialize")) {
		PrintWriter pw = openFileForSequentialWriting(fileName, false);
		pw.println("Status");
		pw.println("continue");
		pw.close();
	} else if (action.equalsIgnoreCase("removeFile")) {
		deleteFile (fileName);
	} else {
		TableDataSet status = readCSVfile(fileName);
		if (!status.getStringValueAt(1, "Status").equalsIgnoreCase("continue")) {
		    return true;
        }
	}
	return false;
}


static void summarizeMicroData (int year, SiloModelContainer modelContainer, SiloDataContainer dataContainer) {
	// "static" so it can also be used from SiloModelCBLCM.  nico/kai/dominik, oct'17


	// aggregate micro data

	if (trackHh != -1 || trackPp != -1 || trackDd != -1)
		trackWriter.println("Started simulation for year " + year);
	logger.info("  Summarizing micro data for year " + year);

	SummarizeData.resultFile("Year " + year, false);
	dataContainer.getHouseholdData().summarizePopulation(dataContainer, modelContainer);
	dataContainer.getRealEstateData().summarizeDwellings();
	dataContainer.getJobData().summarizeJobs(dataContainer.getGeoData().getRegions());

	SummarizeData.resultFileSpatial("Year " + year, false);
	SummarizeData.summarizeSpatially(year, modelContainer, dataContainer);
	if (Properties.get().cblcm.createCblcmFiles) {
        summarizeDataCblcm.createCblcmSummaries(year, modelContainer, dataContainer);
    }
	if (Properties.get().main.createHousingEnvironmentImpactFile) {
        SummarizeData.summarizeHousing(year, dataContainer);
    }
	if (Properties.get().main.createPrestoSummary) {
		SummarizeData.summarizePrestoRegion(year, dataContainer);
	}
}


static void writeOutTimeTracker (long[][] timeCounter) {
	// write file summarizing run times

	int startYear = Properties.get().main.startYear;
	PrintWriter pw = openFileForSequentialWriting(Properties.get().main.trackTimeFile, startYear != Properties.get().main.implementation.BASE_YEAR);
	if (startYear == Properties.get().main.implementation.BASE_YEAR) {
		pw.print("Year");
		for (EventTypes et : EventTypes.values()) pw.print("," + et.toString());
		pw.print(",setupInOutMigration,setupConstructionOfNewDwellings,updateJobInventory,setupJobChange," +
				"setupListOfEvents,fillMarriageMarket,calcAveHousingSatisfaction,SummarizeData,updateRealEstatePrices," +
				"planIncomeChange,addOverwriteDwellings,updateCarOwnership");
		pw.println();
	}
	for (int year = startYear; year < Properties.get().main.endYear; year ++) {
		pw.print(year);
		for (EventTypes et: EventTypes.values()) {
			float timeInMinutes = timeCounter[et.ordinal()][year] / 60000f;
			pw.print("," + timeInMinutes);
		}
		pw.print("," + timeCounter[EventTypes.values().length][year] / 60000f);       // setup inmigration/outmigration
		pw.print("," + timeCounter[EventTypes.values().length + 1][year] / 60000f);   // setup construction of new dwellings
		pw.print("," + timeCounter[EventTypes.values().length + 2][year] / 60000f);   // update job inventory
		pw.print("," + timeCounter[EventTypes.values().length + 3][year] / 60000f);   // setup job change model
		pw.print("," + timeCounter[EventTypes.values().length + 4][year] / 60000f);   // setup list of events
		pw.print("," + timeCounter[EventTypes.values().length + 5][year] / 60000f);   // fill marriage market
		pw.print("," + timeCounter[EventTypes.values().length + 6][year] / 60000f);   // calculate average housing satisfaction
		pw.print("," + timeCounter[EventTypes.values().length + 7][year] / 60000f);   // summarize data
		pw.print("," + timeCounter[EventTypes.values().length + 8][year] / 60000f);   // update real estate prices
		pw.print("," + timeCounter[EventTypes.values().length + 9][year] / 60000f);   // plan income change
		pw.print("," + timeCounter[EventTypes.values().length + 10][year] / 60000f);  // add dwellings from overwrite
        pw.print("," + timeCounter[EventTypes.values().length + 11][year] / 60000f);  // add or relinquish cars
		pw.println();
	}
	pw.close();
}
}