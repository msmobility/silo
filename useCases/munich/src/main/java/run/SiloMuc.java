package run;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Implements SILO for the Munich Metropolitan Area
 * @author Rolf Moeckel and Ana Moreno
 * Created on May 12, 2016 in Munich, Germany
 *
 */
public class SiloMuc {

    private static Logger logger = Logger.getLogger(SiloMuc.class);
    
    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0], Implementation.MUNICH);
        long startTime = System.currentTimeMillis();
        try {
            logger.info("Starting SILO land use model for the Munich Metropolitan Area");
            logger.info("Scenario: " + properties.main.scenarioName + ", Simulation start year: " + properties.main.startYear);
            SiloModel model = new SiloModel(properties);
            model.runModel();
            logger.info("Finished SILO.");
        } catch (Exception e) {
            logger.error("Error running SILO.");
            throw new RuntimeException(e);
        } finally {
            SiloUtil.trackingFile("close");
            SummarizeData.resultFile("close");
            SummarizeData.resultFileSpatial("close");
            float endTime = SiloUtil.rounder(((System.currentTimeMillis() - startTime) / 60000), 1);
            int hours = (int) (endTime / 60);
            int min = (int) (endTime - 60 * hours);
            logger.info("Runtime: " + hours + " hours and " + min + " minutes.");
            if (properties.main.trackTime) {
                String fileName = properties.main.trackTimeFile;
                try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
                    out.println("Runtime: " + hours + " hours and " + min + " minutes.");
                    out.close();
                } catch (IOException e) {
                    logger.warn("Could not add run-time statement to time-tracking file.");
                }
            }
        }
    }
}
