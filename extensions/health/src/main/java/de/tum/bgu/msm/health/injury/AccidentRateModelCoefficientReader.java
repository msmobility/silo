package de.tum.bgu.msm.health.injury;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AccidentRateModelCoefficientReader {

    private final static Logger logger = Logger.getLogger(AccidentRateModelCoefficientReader.class);
    private AccidentType accidentType;
    private AccidentSeverity accidentSeverity;
    private String path;
    private final Map<String, Double> coefficients = new HashMap<>();
    private final Map<Integer, Double>  timeOfDayDistribution = new HashMap<>();

    public AccidentRateModelCoefficientReader(AccidentType accidentType, AccidentSeverity accidentSeverity, String path) {
        this.accidentType = accidentType;
        this.accidentSeverity = accidentSeverity;
        this.path = path;
    }

    public Map<String, Double> readData() {
        String modelType = accidentType.toString() + "_" + accidentSeverity.toString();
        logger.info("Reading "+ modelType + " model coefficients from csv file");

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posVariable = findPositionInArray("Variable", header);
            int posCoefficient = findPositionInArray(modelType,header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                if(lineElements.length==0){
                    logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
                }
                String variableName = lineElements[posVariable];
                double coefficient = Double.parseDouble(lineElements[posCoefficient]);

                coefficients.put(variableName, coefficient);
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading " + modelType + " file: " + path);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }

        logger.info("Finished reading ");
        return coefficients;
    }

    public Map<Integer, Double> readTimeOfDayData() {
        String modelType = accidentType.toString() + "_" + accidentSeverity.toString();
        logger.info("Reading "+ modelType + " time of day coefficients from csv file");

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posTime = findPositionInArray("Hour", header);
            int posValue = findPositionInArray(modelType,header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int time = Integer.parseInt(lineElements[posTime]);
                double value = Double.parseDouble(lineElements[posValue]);

                timeOfDayDistribution.put(time, value);
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading " + modelType + " file: " + path);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }

        logger.info("Finished reading ");
        return timeOfDayDistribution;
    }

    public Map<Integer, Double> readCasualtyData() {
        String modelType = accidentType.toString();
        logger.info("Reading "+ modelType + " time of day coefficients from csv file");

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posTime = findPositionInArray("Hour", header);
            int posValue = findPositionInArray(modelType,header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int time = Integer.parseInt(lineElements[posTime]);
                double value = Double.parseDouble(lineElements[posValue]);

                timeOfDayDistribution.put(time, value);
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading " + modelType + " file: " + path);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }

        logger.info("Finished reading ");
        return timeOfDayDistribution;
    }


    public static int findPositionInArray (String string, String[] array) {
        int ind = -1;
        for (int a = 0; a < array.length; a++) {
            if (array[a].equalsIgnoreCase(string)) {
                ind = a;
            }
        }
        if (ind == -1) {
            logger.error ("Could not find element " + string);
        }
        return ind;
    }
}
