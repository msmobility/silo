package de.tum.bgu.msm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ResourceBundle;

import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;

import com.pb.common.util.ResourceUtil;

import de.tum.bgu.msm.syntheticPopulationGenerator.maryland.SyntheticPopUs;
import de.tum.bgu.msm.data.summarizeData;

/**
 * @author dziemke
 */

public final class SiloMatsim {
	static Logger logger = Logger.getLogger(SiloMatsim.class);

	private ResourceBundle rb;
	private final Properties properties;
	private Config matsimConfig = ConfigUtils.createConfig(); // SILO-MATSim integration-specific

	/**
	 * Option to set the matsim config directly, at this point meant for tests.
	 */
	public SiloMatsim(String args, Config config) {
		SiloUtil.setBaseYear(2000);
		rb = SiloUtil.siloInitialization(args);
		matsimConfig = config ;
		properties = new Properties(rb);
	}	    

	public final void run() {
		// main run method
		long startTime = System.currentTimeMillis();
		try {
			logger.info("Starting SILO program for MATSim");
			logger.info("Scenario: " + SiloUtil.scenarioName + ", Simulation start year: " + SiloUtil.getStartYear());
			SyntheticPopUs sp = new SyntheticPopUs(rb);
			sp.runSP();
			SiloModel model = new SiloModel(rb, matsimConfig, properties );
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

	final ResourceBundle getRb() {
		return this.rb;
	}
}