package de.tum.bgu.msm.health.io;

import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.health.data.DataContainerHealth;
import de.tum.bgu.msm.health.disease.Diseases;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class HealthTransitionTableReader {

    private final static Logger logger = LogManager.getLogger(HealthTransitionTableReader.class);

    public EnumMap<Diseases, Map<String, Double>> readData(DataContainerHealth dataContainer, String path) {
        logger.info("Reading health disease prob table from csv file");

        EnumMap<Diseases, Map<String, Double>> healthDiseaseData = new EnumMap<>(Diseases.class);

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posAge = SiloUtil.findPositionInArray("age", header);
            int posGender= SiloUtil.findPositionInArray("sex", header);
            int posLocation = SiloUtil.findPositionInArray("lsoa21cd", header);
            int posCause= SiloUtil.findPositionInArray("cause", header);
            int posProb= SiloUtil.findPositionInArray("prob", header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int age = Integer.parseInt(lineElements[posAge]);
                Gender gender = Gender.valueOf(Integer.parseInt(lineElements[posGender]));
                String location = lineElements[posLocation];
                Diseases diseases = Diseases.valueOf(lineElements[posCause]);
                double prob = Double.parseDouble(lineElements[posProb]);

                String compositeKey = dataContainer.createTransitionLookupIndex(age,gender,location);

                healthDiseaseData.computeIfAbsent(diseases, k -> new HashMap<>()).put(compositeKey, prob);

            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading health disease prob file: " + path);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        } catch (IllegalArgumentException e){
            logger.warn(e.getMessage());
        }
        logger.info("Finished reading health disease prob table from csv file.");
        return healthDiseaseData;
    }
}
