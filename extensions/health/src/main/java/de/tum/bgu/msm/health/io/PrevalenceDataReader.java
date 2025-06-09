package de.tum.bgu.msm.health.io;

import de.tum.bgu.msm.data.MitoGender;
import de.tum.bgu.msm.data.Mode;
import de.tum.bgu.msm.health.disease.Diseases;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PrevalenceDataReader {
    private final static Logger logger = LogManager.getLogger(PrevalenceDataReader.class);

    public Map<Integer, List<Diseases>> readData(String fileName) {
        logger.info("Reading prevalence data from ascii file");

        //EnumMap<Mode,EnumMap<MitoGender,Map<Integer,Double>>> speedData = new EnumMap<>(Mode.class);
        Map<Integer, List<Diseases>> dataMap = new HashMap<>();

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId = SiloUtil.findPositionInArray("id", header);
            int posDiseases = SiloUtil.findPositionInArray("diseases", header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                Integer id = Integer.parseInt(lineElements[posId]);
                List<String> diseases = Arrays.asList(lineElements[posDiseases].split(" "));
                List<Diseases> diseaseEnums = new ArrayList<>();
                for (String diseaseStr : diseases) {
                    try {
                        diseaseEnums.add(Diseases.valueOf(diseaseStr.trim()));
                    } catch (IllegalArgumentException e) {
                        // handle or ignore invalid names
                    }
                }
                dataMap.put(id, diseaseEnums);
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading prevalence data file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " prevalence data.");

        return dataMap;
    }
}
