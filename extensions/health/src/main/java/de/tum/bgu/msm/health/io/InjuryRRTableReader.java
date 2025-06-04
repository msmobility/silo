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

public class InjuryRRTableReader {

    private final static Logger logger = LogManager.getLogger(InjuryRRTableReader.class);

    public EnumMap<Diseases, Map<String, Double>> readData(DataContainerHealth dataContainer, String path) {
        logger.info("Reading injury RR by age/gender table from csv file");

        EnumMap<Diseases, Map<String, Double>> healthDiseaseData = new EnumMap<>(Diseases.class);

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posMode = SiloUtil.findPositionInArray("Mode", header);
            int posGender = SiloUtil.findPositionInArray("Gender", header);
            int posAgeGroup = SiloUtil.findPositionInArray("Age_group", header);
            int posRate = SiloUtil.findPositionInArray("Rate", header);
            int posGenderAge = SiloUtil.findPositionInArray("Gender_Age", header);
            int posRR = SiloUtil.findPositionInArray("RR", header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");

                // Mode
                String mode = lineElements[posMode];


                // Gender
                if(lineElements[posGender].equals("Male")) {
                    Gender gender = Gender.MALE;
                }else{
                    Gender gender = Gender.FEMALE;
                }

                // Age group
                String ageGroup = lineElements[posAgeGroup];

                // Replace comma with period for double parsing
                double rate = Double.parseDouble(lineElements[posRate].replace(",", "."));
                String genderAge = lineElements[posGenderAge];
                double rr = Double.parseDouble(lineElements[posRR].replace(",", "."));

                // Store either rate or rr as the probability (assuming rr is the relevant probability)
                //healthDiseaseData.computeIfAbsent(disease, k -> new HashMap<>()).put(compositeKey, rr);
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading health disease prob file: " + path);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        } catch (IllegalArgumentException e) {
            logger.warn(e.getMessage());
        }
        logger.info("Finished reading health disease prob table from csv file.");
        return healthDiseaseData;
    }
}
