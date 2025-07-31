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

    public static class DataEntry {
        public final double percentKilled;
        public final double rr;

        public DataEntry(double percentKilled, double rr) {
            this.percentKilled = percentKilled;
            this.rr = rr;
        }
    }

    public Map<String, EnumMap<Gender, Map<String, DataEntry>>> readData(String fileName) {
        logger.info("Reading injury RR + fatalities data from ascii file");

        Map<String, EnumMap<Gender, Map<String, DataEntry>>> dataMap = new HashMap<>();

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posMode = SiloUtil.findPositionInArray("mode", header);
            int posGender = SiloUtil.findPositionInArray("gender", header);
            int posAge = SiloUtil.findPositionInArray("age_group", header);
            int posFatalities = SiloUtil.findPositionInArray("percent_killed", header);
            int posRR = SiloUtil.findPositionInArray("RR", header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                for (int i = 0; i < lineElements.length; i++) {
                    lineElements[i] = lineElements[i].trim().replaceAll("^\"|\"$", "");
                }
                String mode = lineElements[posMode];
                String genderStr = lineElements[posGender];
                Gender gender = "Male".equals(genderStr) ? Gender.MALE : Gender.FEMALE;
                String age = lineElements[posAge];
                Double percentKilled = Double.parseDouble(lineElements[posFatalities]);
                Double rr = Double.parseDouble(lineElements[posRR]);

                // Initialize nested maps if missing
                dataMap.putIfAbsent(mode, new EnumMap<>(Gender.class));
                dataMap.get(mode).putIfAbsent(gender, new HashMap<>());

                // Insert the age → DataEntry mapping
                dataMap.get(mode).get(gender).put(age, new DataEntry(percentKilled, rr));
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading prevalence data file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " prevalence data.");

        return dataMap;
    }
}