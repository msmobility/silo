package de.tum.bgu.msm;

import de.tum.bgu.msm.SyntheticPopulationGenerator.SyntheticPopUs;
import org.apache.log4j.Logger;

import java.util.ResourceBundle;

/**
 * Implements SILO for the Maryland Statewide Transportation Model for CSDMS integration
 * @author Rolf Moeckel
 * Created on Nov 22, 2013 in Wheaton, MD
 * Revised on Apr 17, 2015 in College Park, MD
 *
 */

public class SiloCSDMS {
    // main class
    static Logger logger = Logger.getLogger(SiloCSDMS.class);
    private static SiloModel model;
    private static long startTime;


    public static void main (String args) {
        // main run method

//        SyntheticPopUs sp = new SyntheticPopUs(rb);
//        sp.runSP();
        initialize(args);
        for (int year = SiloUtil.getStartYear(); year < SiloUtil.getEndYear(); year += SiloUtil.getSimulationLength()) {
            update(1d);
        }
        finalizeIt();
    }


    public static void mainOld(String args) {
        // main run method

        SiloUtil.setBaseYear(2000);
        ResourceBundle rb = SiloUtil.siloInitialization(args);
        startTime = System.currentTimeMillis();
        try {
            logger.info("Starting SILO program for MSTM with CSDMS Integration");
            logger.info("Scenario: " + SiloUtil.scenarioName + ", Simulation start year: " + SiloUtil.getStartYear());
            SyntheticPopUs sp = new SyntheticPopUs(rb);
            sp.runSP();
            model = new SiloModel(rb);
            model.runModel(SiloModel.Implementation.MSTM);
            logger.info("Finished SILO.");
        } catch (Exception e) {
            logger.error("Error running SILO.");
            throw new RuntimeException(e);
        } finally {
            model.closeAllFiles(startTime);
        }
    }


    public static void initialize (String configFile) {
        // initialization step for CSDMS

        logger.info("Starting SILO Initialization for MSTM with CSDMS Integration");
        ResourceBundle rb = SiloUtil.siloInitialization(configFile);
        SiloUtil.setBaseYear(2000);
        logger.info("Scenario: " + SiloUtil.scenarioName + ", Simulation start year: " + SiloUtil.getStartYear());
        startTime = System.currentTimeMillis();
        model = new SiloModel(rb);
        model.initialize();
        logger.info("Finished Initialization.");
    }


    public static void update (double dt) {
        // run next simulation period

        try {
            model.runYear(dt);
        } catch (Exception e) {
            logger.error("Error running SILO.");
            model.closeAllFiles(startTime);
            throw new RuntimeException(e);
        }
    }


    public static void finalizeIt () {
        // close model
    	try {
    		model.finishModel();
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			//throw e;
		}
        model.closeAllFiles(startTime);
        logger.info("Finished SILO.");
    }
}
