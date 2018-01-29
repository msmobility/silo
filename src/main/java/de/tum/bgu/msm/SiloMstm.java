package de.tum.bgu.msm;

import de.tum.bgu.msm.data.SummarizeData;
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

        ResourceBundle rb = SiloUtil.siloInitialization(args[0], Implementation.MARYLAND);
        long startTime = System.currentTimeMillis();
        try {
            logger.info("Starting SILO program for MSTM");
            logger.info("Scenario: " + Properties.get().main.scenarioName + ", Simulation start year: " + Properties.get().main.startYear);
            SyntheticPopUs sp = new SyntheticPopUs(rb);
            sp.runSP();
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
