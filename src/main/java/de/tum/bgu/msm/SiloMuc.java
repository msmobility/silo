package de.tum.bgu.msm;

import de.tum.bgu.msm.data.summarizeData;
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

    // main class
    public static final String PROPERTIES_RUN_SILO                 = "run.silo.model";
    public static final String PROPERTIES_RUN_SYNTHETIC_POPULATION = "run.synth.pop.generator";
    static Logger logger = Logger.getLogger(SiloMuc.class);


    public static void main(String[] args) {
        // main run method

        SiloUtil.setBaseYear(2011);  // Base year is defined by available input data for synthetic population
        ResourceBundle rb = SiloUtil.siloInitialization(args[0]);
        Properties properties = new Properties(rb);
        long startTime = System.currentTimeMillis();
        try {
            logger.info("Starting SILO land use model for the Munich Metropolitan Area");
            logger.info("Scenario: " + SiloUtil.scenarioName + ", Simulation start year: " + SiloUtil.getStartYear());
            SyntheticPopulationGenerator sp = new SyntheticPopulationGenerator(rb);
            sp.run();
            SiloModel model = new SiloModel(rb, properties);
            model.runModel(SiloModel.Implementation.MUC);
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
