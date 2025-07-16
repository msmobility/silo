package de.tum.bgu.msm.health.io;

import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.health.disease.Diseases;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class InjuryRRTableReader {
    private final static Logger logger = LogManager.getLogger(InjuryRRTableReader.class);

    public Map<String, EnumMap<Gender, Map<Integer, Double>>> readData(String fileName) {
        logger.info("Reading prevalence data from ascii file");

        //EnumMap<Mode,EnumMap<MitoGender,Map<Integer,Double>>> speedData = new EnumMap<>(Mode.class);
        Map<String, EnumMap<Gender, Map<Integer, Double>>> dataMap = new HashMap<>();

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posMode = SiloUtil.findPositionInArray("Mode", header);
            int posGender = SiloUtil.findPositionInArray("Gender", header);
            int posAge = SiloUtil.findPositionInArray("Age", header);
            int posRR = SiloUtil.findPositionInArray("RR", header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                String mode = lineElements[posMode];
                Gender gender = "Male".equals(lineElements[posGender]) ? Gender.MALE : Gender.FEMALE;
                Integer age = Integer.parseInt(lineElements[posAge]);
                Double rr = Double.parseDouble(lineElements[posRR]);

                // Initialize nested maps if missing
                dataMap.putIfAbsent(mode, new EnumMap<>(Gender.class));
                dataMap.get(mode).putIfAbsent(gender, new HashMap<>());

                // Insert the age → RR mapping
                dataMap.get(mode).get(gender).put(age, rr);
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading prevalence data file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " prevalence data.");

        return dataMap;
    }
}

