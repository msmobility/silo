package de.tum.bgu.msm.health.io;

import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.health.disease.Diseases;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class HealthTransitionTableReader {

    private final static Logger logger = Logger.getLogger(HealthTransitionTableReader.class);

    public EnumMap<Diseases, EnumMap<Gender,Map<Integer,Double>>> readData(String path) {
        logger.info("Reading health disease prob table from csv file");

        EnumMap<Diseases, EnumMap<Gender,Map<Integer,Double>>> healthDiseaseData = new EnumMap<>(Diseases.class);

        for(Diseases diseases : Diseases.values()) {
            healthDiseaseData.put(diseases,new EnumMap<>(Gender.class));
            for(Gender gender : Gender.values()) {
                healthDiseaseData.get(diseases).put(gender,new LinkedHashMap<>());
            }
        }

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posAge = SiloUtil.findPositionInArray("age", header);
            int posGender= SiloUtil.findPositionInArray("sex", header);
            int posCause= SiloUtil.findPositionInArray("cause_acronym", header);
            int posProb= SiloUtil.findPositionInArray("prob", header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int age = Integer.parseInt(lineElements[posAge]);
                Gender gender = Gender.valueOf(lineElements[posGender]);
                Diseases diseases = Diseases.valueOf(lineElements[posCause]);
                double prob = Double.parseDouble(lineElements[posProb]);

                healthDiseaseData.get(diseases).get(gender).put(age, prob);
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading health disease prob file: " + path);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading health disease prob table from csv file.");
        return healthDiseaseData;
    }

}
