package edu.umd.ncsg;

import com.pb.common.util.ResourceUtil;
import edu.umd.ncsg.SyntheticPopulationGenerator.syntheticPop;
import edu.umd.ncsg.data.summarizeData;
import org.apache.log4j.Logger;
import org.matsim.contrib.accessibility.AccessibilityConfigGroup;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ResourceBundle;

/**
 * @author dziemke
 */

public class SiloMatsim {
	static Logger logger = Logger.getLogger(SiloMatsim.class);

	public static void main(String[] args) {
		new SiloMatsim(args).run();
	}

	private ResourceBundle rb;
	private Config matsimConfig = ConfigUtils.createConfig(new AccessibilityConfigGroup()); // SILO-MATSim integration-specific

	SiloMatsim(String[] args) {
		this( args, ConfigUtils.loadConfig(args[1]) ) ;
	}	    

	/**
	 * Option to set the matsim config directly, at this point meant for tests.
	 */
	SiloMatsim(String[] args, Config config) {
		SiloUtil.setBaseYear(2000);
		rb = SiloUtil.siloInitialization(args[0]);
		matsimConfig = config ;
	}	    

	void run() {
		// main run method
		long startTime = System.currentTimeMillis();
		try {
			logger.info("Starting SILO program for MSTM");
			logger.info("Scenario: " + SiloUtil.scenarioName + ", Simulation start year: " + SiloUtil.getStartYear());
			syntheticPop sp = new syntheticPop(rb);
			sp.runSP();
			SiloModel model = new SiloModel(rb);
			model.setMatsimConfig(matsimConfig); // SILO-MATSim integration-specific
			model.runModel();
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

	final ResourceBundle getRb() {
		return this.rb;
	}
}