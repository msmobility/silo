package de.tum.bgu.msm.health.io;

import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SportPAmodelCoefficientReader {

    private final static Logger logger = LogManager.getLogger(SportPAmodelCoefficientReader.class);

    public Map<String,Map<String,Double>> readData(String fileName) {
        logger.info("Reading sport PA model coefficient from csv file");

        Map<String,Map<String,Double>> coef = new HashMap<>();
        coef.put("zero", new HashMap<>());
        coef.put("linear", new HashMap<>());


        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posVariable = SiloUtil.findPositionInArray("variable", header);
            int posZero = SiloUtil.findPositionInArray("zero", header);
            int posLinear = SiloUtil.findPositionInArray("linear", header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                String variable = lineElements[posVariable];
                double linearCoef = Double.parseDouble(lineElements[posLinear]);
                double zeroCoef = Double.parseDouble(lineElements[posZero]);

                coef.get("zero").put(variable, zeroCoef);
                coef.get("linear").put(variable,linearCoef);
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " avg mode speed.");

        return coef;
    }
}
