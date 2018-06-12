package de.tum.bgu.msm;

import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.syntheticPopulationGenerator.SyntheticPopulationGenerator;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ResourceBundle;

/**
 * Implements SILO for the Munich Metropolitan Area
 * @author Rolf Moeckel and Ana Moreno
 * Created on May 12, 2016 in Munich, Germany
 *
 */
public class SiloMuc {

    static Logger logger = Logger.getLogger(SiloMuc.class);
    
    public static void main(String[] args) {

        ResourceBundle rb = SiloUtil.siloInitialization(args[0], Implementation.MUNICH);
        long startTime = System.currentTimeMillis();
        try {
            logger.info("Starting SILO land use model for the Munich Metropolitan Area");
            logger.info("Scenario: " + Properties.get().main.scenarioName + ", Simulation start year: " + Properties.get().main.startYear);
            if(Properties.get().main.runSynPop) {
                SyntheticPopulationGenerator sp = new SyntheticPopulationGenerator(rb);
                sp.run();
            }
            SiloModel model = new SiloModel();
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
            if (Properties.get().main.trackTime) {
                String fileName = Properties.get().main.trackTimeFile;
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
