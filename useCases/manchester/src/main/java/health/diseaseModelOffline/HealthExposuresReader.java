package health.diseaseModelOffline;

import de.tum.bgu.msm.data.Mode;
import de.tum.bgu.msm.health.data.PersonHealth;
import de.tum.bgu.msm.utils.SiloUtil;
import health.HealthDataContainerImpl;
import health.PersonFactoryMCRHealth;
import health.PersonHealthMCR;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HealthExposuresReader {

    private final static Logger logger = Logger.getLogger(HealthExposuresReader.class);

    public Map<Integer, PersonHealth> readData(HealthDataContainerImpl dataContainer, String path) {
        logger.info("Reading person micro data with health exposures");
        PersonFactoryMCRHealth ppFactory = new PersonFactoryMCRHealth();
        Map<Integer, PersonHealth> persons = new HashMap<>();

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId = SiloUtil.findPositionInArray("id", header);
            int posMmetWalk = SiloUtil.findPositionInArray("mmetHr_walk", header);
            int posMmetCycle = SiloUtil.findPositionInArray("mmetHr_cycle", header);
            //int posMmetSport = SiloUtil.findPositionInArray("mmetHr_sport", header);
            int posPM2_5 = SiloUtil.findPositionInArray("exposure_normalised_pm25", header);
            int posNO2 = SiloUtil.findPositionInArray("exposure_normalised_no2", header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                PersonHealthMCR pp = (PersonHealthMCR) dataContainer.getHouseholdDataManager().getPersonFromId(Integer.parseInt(lineElements[posId]));
                pp.updateWeeklyMarginalMetHours(Mode.walk, Float.parseFloat(lineElements[posMmetWalk]));
                pp.updateWeeklyMarginalMetHours(Mode.bicycle, Float.parseFloat(lineElements[posMmetCycle]));
                //pp.setWeeklyMarginalMetHoursSport(Float.parseFloat(lineElements[posMmetSport]));

                Map<String, Float> exposureMap = new HashMap<>();
                exposureMap.put("pm2.5", Float.parseFloat(lineElements[posPM2_5]));
                exposureMap.put("no2", Float.parseFloat(lineElements[posNO2]));
                pp.updateWeeklyPollutionExposures(exposureMap);

            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading person health exposure file: " + path);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " persons with exposure.");
        return persons;
    }
}
