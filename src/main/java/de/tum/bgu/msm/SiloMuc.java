package de.tum.bgu.msm;

import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.SyntheticPopulationGenerator.SyntheticPopDe;
import de.tum.bgu.msm.data.summarizeData;
import de.tum.bgu.msm.transportModel.MstmTransportModel;
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
    protected static final String PROPERTIES_RUN_TRAVEL_DEMAND_MODEL  = "run.travel.demand.model";
    static Logger logger = Logger.getLogger(SiloMuc.class);


    public static void main(String[] args) {
        // main run method

        SiloUtil.setBaseYear(2000);
        ResourceBundle rb = SiloUtil.siloInitialization(args[0]);
        long startTime = System.currentTimeMillis();
        if (ResourceUtil.getBooleanProperty(rb, PROPERTIES_RUN_SYNTHETIC_POPULATION, false)) {
           SyntheticPopDe sp = new SyntheticPopDe(rb);
            sp.runSP();
           /*ExtractDataDE de = new ExtractDataDE(rb);
            de.runSP();*/
        }
        if (ResourceUtil.getBooleanProperty(rb, PROPERTIES_RUN_SILO, false)) {
            try {
                logger.info("Starting SILO land use model for MUC");
                logger.info("Scenario: " + SiloUtil.scenarioName + ", Simulation start year: " + SiloUtil.getStartYear());
                SiloModel model = new SiloModel(rb);
                model.runModel();
                logger.info("Finished SILO.");
            } catch (Exception e) {
                logger.error("Error running SILO.");
                throw new RuntimeException(e);
            } finally {
                SiloUtil.trackingFile("close");
                summarizeData.resultFile("close");
                summarizeData.resultFileSpatial(rb, "close");
            }
        }
        // if land use model was not selected to run, check if travel demand model shall run only
        if (ResourceUtil.getBooleanProperty(rb, PROPERTIES_RUN_TRAVEL_DEMAND_MODEL, false)) {
            MstmTransportModel tdm = new MstmTransportModel(rb);
            tdm.runTransportModel(-1);  // -1 tells transport model to look up first transport year from properties file
        }
        float endTime = SiloUtil.rounder(((System.currentTimeMillis() - startTime) / 60000), 1);
        int hours = (int) (endTime / 60);
        int min = (int) (endTime - 60 * hours);
        logger.info("Runtime: " + hours + " hours and " + min + " minutes.");
        if (ResourceUtil.getBooleanProperty(rb, SiloModel.PROPERTIES_TRACK_TIME, false)) {
            String fileName = rb.getString(SiloModel.PROPERTIES_TRACK_TIME_FILE);
            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
                out.println("Runtime: " + hours + " hours and " + min + " minutes.");
                out.close();
            } catch (IOException e) {
                logger.warn("Could not add run-time statement to time-tracking file.");
            }
        }
    }
}
