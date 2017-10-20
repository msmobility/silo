/*
package de.tum.bgu.msm;

import com.pb.common.datafile.CSVFileWriter;
import com.pb.common.datafile.TableDataFileReader;
import com.pb.common.datafile.TableDataSet;
import com.pb.common.matrix.Matrix;
import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.data.geoDataMstm;
import de.tum.bgu.msm.data.summarizeData;
import de.tum.bgu.msm.events.IssueCounter;
import de.tum.bgu.msm.realEstate.ConstructionOverwrite;
import omx.OmxMatrix;
import omx.hdf5.OmxHdf5Datatype;
import org.apache.log4j.Logger;

import java.io.*;
import static java.nio.file.StandardCopyOption.*;
import java.nio.file.Files;
import java.util.*;

*/
/**
 * Utilities used by the SILO Model
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 8 December 2009 in Santa Fe
 **//*


public class SiloUtilBak {

    protected static final String PROPERTIES_BASE_DIRECTORY                    = "base.directory";
    protected static final String PROPERTIES_RANDOM_SEED                       = "random.seed";
    protected static final String PROPERTIES_SCENARIO_NAME                     = "scenarios.name";
    protected static final String PROPERTIES_TRACKING_FILE_NAME                = "track.file.name";
    public static final String PROPERTIES_START_YEAR                        = "start.year";
    public static final String PROPERTIES_SIMULATION_PERIOD_LENGTH          = "simulation.period.length";
    public static final String PROPERTIES_END_YEAR                          = "end.year";
    public static final String PROPERTIES_GREGORIAN_ITERATOR                = "this.gregorian.iterator";
    public static final String PROPERTIES_INCOME_BRACKETS                   = "income.brackets.hh.types";
    public static final String PROPERTIES_NUMBER_OF_DWELLING_QUALITY_LEVELS = "dwelling.quality.levels.distinguished";

    public static String baseDirectory;
    public static String scenarioName;
    public static TableDataSet zonalData;
    public static int trackHh;
    public static int trackPp;
    public static int trackDd;
    public static int trackJj;
    public static PrintWriter trackWriter;
    public static int gregorianIterator;
    public static int[] incBrackets;
    public static int numberOfQualityLevels;
    private static ResourceBundle rb;
    private static HashMap rbHashMap;

    static Logger logger = Logger.getLogger(SiloUtil.class);
    private static int baseYear;
    public static int startYear;
    public static int simulationLength;
    public static int endYear;

    public SiloUtilBak() {
    }


    public ResourceBundle siloInitialization(String resourceBundleName) {
        // initializes Silo

        File propFile = new File(resourceBundleName);
        rb = ResourceUtil.getPropertyBundle(propFile);
        rbHashMap = ResourceUtil.changeResourceBundleIntoHashMap(rb);

        baseDirectory = ResourceUtil.getProperty(rb, PROPERTIES_BASE_DIRECTORY);
        scenarioName = ResourceUtil.getProperty(rb, PROPERTIES_SCENARIO_NAME);

        // create scenarios output directory if it does not exist yet
        createDirectoryIfNotExistingYet(baseDirectory + "scenOutput/" + scenarioName);
        // copy properties file into scenarios directory
        String[] prop = resourceBundleName.split("/");
//        copyFile(baseDirectory + resourceBundleName, baseDirectory + "scenOutput/" + scenarioName + "/" + prop[prop.length-1]);
        copyFile(resourceBundleName, baseDirectory + "scenOutput/" + scenarioName + "/" + prop[prop.length-1]);

        initializeRandomNumber();
        trackingFile("open");
        //geoData.setInitialData(rb);
        return rb;
    }


    public static HashMap getRbHashMap() {
        return rbHashMap;
    }


    public static void createDirectoryIfNotExistingYet (String directory) {
        // create directory if is does not yet exist
        File file = new File (directory);
        if (!file.exists()) {
            logger.error("Creating Directory: "+directory); // TODO I would not log an error here; at most a warning or an info, dz, apr/16
            boolean outputDirectorySuccessfullyCreated = file.mkdir();
            if (!outputDirectorySuccessfullyCreated) logger.warn("Could not create scenarios directory " + scenarioName);
        }
    }


    private static void initializeRandomNumber() {
        // initialize random number generator
        int seed = ResourceUtil.getIntegerProperty(rb, PROPERTIES_RANDOM_SEED);
        if (seed == -1)
            SiloModel.rand = new Random();
        else
            SiloModel.rand = new Random(seed);
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


    public static TableDataSet readCSVfile (String fileName) {
        // read csv file and return as TableDataSet
        File dataFile = new File(fileName);
        TableDataSet dataTable;
        boolean exists = dataFile.exists();
        if (!exists) {
            logger.error("File not found: " + fileName);
            throw new RuntimeException("file not found");
//            System.exit(1);
            // If code terminates with System.exit(...), from the perspective of a junit test the test has passed! kai, apr'16 
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
                String fileName = ResourceUtil.getProperty(rb, PROPERTIES_TRACKING_FILE_NAME);
                trackWriter = openFileForSequentialWriting(baseDirectory + fileName + ".txt", startYear != baseYear);
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
        try{
            CSVFileWriter cfwWriter = new CSVFileWriter();
            cfwWriter.writeFile(data, new File(fileName));
            cfwWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            logger.error("Could not write TableDataSet to file " + fileName + ".");
        }
    }


    public static int select (double[] probabilities) {
        // select item based on probabilities (for zero-based double array)
        double selPos = getSum(probabilities) * SiloModel.rand.nextDouble();
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
        float selPos = getSum(probabilities) * SiloModel.rand.nextFloat();
        float sum = 0;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            if (sum > selPos) {
                return i;
            }
        }
        return probabilities.length - 1;
    }


    public static int select (int upperRange) {
        // select item based on equal probabilities between 0 and upperRange
        int selected = (int) (upperRange * SiloModel.rand.nextFloat());
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


    public static void finish (ConstructionOverwrite overwrite) {
        summarizeData.resultFile("close");
        trackingFile("close");
        if (overwrite.traceOverwriteDwellings()) overwrite.finishOverwriteTracer();
        if (IssueCounter.didFindIssues()) logger.warn("Found issues, please check warnings in logging statements.");
    }


    public static float rounder(float value, int digits) {
        // rounds value to digits behind the decimal point

        return Math.round(value * Math.pow(10, digits) + 0.5)/(float) Math.pow(10, digits);
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
        for (int i = 0; i < anArray.length; i++) anArray[i] = value;
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


    public static int getHighestVal(int[] array) {
        // return highest number in int array
        int high = Integer.MIN_VALUE;
        for (int num: array) high = Math.max(high, num);
        return high;
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
        for (String num : array) low = Math.min(low, Integer.parseInt(num));
        return low;
    }


    public static boolean containsElement (int[] array, int value) {
        // returns true if array contains value, otherwise false
        boolean found = false;
        for (int i: array) if (i == value) found = true;
        return found;
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
            logger.warn("Unable to copy properties file " + source + " to scenarios output directory.");
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
//            System.out.println("dArray = " + dArray.toString());
            Matrix mat = new Matrix(name, name, dimensions[0], dimensions[1]);
//            System.out.println("matrix = " + mat.toString());
            for (int i = 0; i < dimensions[0]; i++)
                for (int j = 0; j < dimensions[1]; j++) {
                    mat.setValueAt(i + 1, j + 1, (float) dArray[i][j]);
//            		System.out.println("i = " + i + " ; j = " + j + " ; dArray[i][j] = " + dArray[i][j]);
                }
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


    public static void setBaseYear(int year) {
        // base year is the year for which the initial synthetic population had been generated. Start year is the year
        // the current model run starts with. For example, SILO may run from 2000 to 2007 (base year == 2000 and start
        // year == 2000), then the travel model might be run, and SILO picks up from 2007 to 2040 (base year == 2000 and
        // start year == 2007)
        baseYear = year;
    }

    public static int getBaseYear() {
        return baseYear;
    }

    public static int getStartYear() {
        return startYear;
    }

    public static int getSimulationLength() {
        return simulationLength;
    }

    public static int getEndYear() {
        return endYear;
    }
}*/
