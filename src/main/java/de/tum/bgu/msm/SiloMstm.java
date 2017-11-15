package de.tum.bgu.msm;

import de.tum.bgu.msm.data.summarizeData;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.syntheticPopulationGenerator.maryland.SyntheticPopUs;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ResourceBundle;

/**
 * Implements SILO for the Maryland Statewide Transportation Model
 * @author Rolf Moeckel
 * Created on Nov 22, 2013 in Wheaton, MD
 *
 */

public class SiloMstm {
    // main class
    static Logger logger = Logger.getLogger(SiloMstm.class);


    public static void main(String[] args) {
        // main run method

        SiloUtil.setBaseYear(2000);
        ResourceBundle rb = SiloUtil.siloInitialization(args[0]);
        Properties properties = new Properties(rb);
        long startTime = System.currentTimeMillis();
        try {
            logger.info("Starting SILO program for MSTM");
            logger.info("Scenario: " + SiloUtil.scenarioName + ", Simulation start year: " + SiloUtil.getStartYear());
            SyntheticPopUs sp = new SyntheticPopUs(rb);
            sp.runSP();
            SiloModel model = new SiloModel(rb, properties);
            model.runModel(SiloModel.Implementation.MSTM);
            logger.info("Finished SILO.");
        } catch (Exception e) {
            logger.error("Error running SILO.");
            throw new RuntimeException(e);
        } finally {
            SiloUtil.trackingFile("close");
            summarizeData.resultFile("close");
            summarizeData.resultFileSpatial(rb, "close");
            float endTime = SiloUtil.rounder(((System.currentTimeMillis() - startTime) / 60000), 1);
            int hours = (int) (endTime / 60);
            int min = (int) (endTime - 60 * hours);
            logger.info("Runtime: " + hours + " hours and " + min + " minutes.");
            if (properties.getMainProperties().isTrackTime()) {
                String fileName = properties.getMainProperties().getTrackTimeFile();
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
