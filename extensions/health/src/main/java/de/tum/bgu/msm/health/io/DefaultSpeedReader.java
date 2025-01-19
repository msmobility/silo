package de.tum.bgu.msm.health.io;

import de.tum.bgu.msm.data.MitoGender;
import de.tum.bgu.msm.data.Mode;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DefaultSpeedReader implements SpeedReader {

    private final static Logger logger = LogManager.getLogger(DefaultSpeedReader.class);

    @Override
    public EnumMap<Mode,EnumMap<MitoGender,Map<Integer,Double>>> readData(String fileName) {
        logger.info("Reading avg mode speed data from ascii file");

        EnumMap<Mode,EnumMap<MitoGender,Map<Integer,Double>>> speedData = new EnumMap<>(Mode.class);

        for(Mode mode : EnumSet.of(Mode.walk,Mode.bicycle)) {
            speedData.put(mode,new EnumMap<>(MitoGender.class));
            for(MitoGender gender : MitoGender.values()) {
                speedData.get(mode).put(gender,new LinkedHashMap<>());
            }
        }

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posAge = SiloUtil.findPositionInArray("age", header);
            int posSex = SiloUtil.findPositionInArray("sex", header);
            int posMode = SiloUtil.findPositionInArray("mode", header);
            int posSpeed = SiloUtil.findPositionInArray("speed", header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                Integer age = Integer.parseInt(lineElements[posAge]);
                MitoGender sex = MitoGender.valueOf(lineElements[posSex]);
                Mode mode = Mode.valueOf(lineElements[posMode]);
                Double speed = Double.parseDouble(lineElements[posSpeed]);
                speedData.get(mode).get(sex).put(age,speed);
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " avg mode speed.");

        return speedData;
    }
}
