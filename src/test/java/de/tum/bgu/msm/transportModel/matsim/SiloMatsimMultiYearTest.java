package de.tum.bgu.msm.transportModel.matsim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.core.utils.misc.CRCChecksum;
import org.matsim.utils.eventsfilecomparison.EventsFileComparator;

import de.tum.bgu.msm.SiloMatsim;
import de.tum.bgu.msm.transportModel.SiloTestUtils;

/**
 * @author dziemke, nagel
 */
public class SiloMatsimMultiYearTest {
	private static final Logger log = Logger.getLogger(SiloMatsimMultiYearTest.class);

	@Rule public MatsimTestUtils utils = new MatsimTestUtils();

	@Test
	public final void testMainMultiYear() {
		SiloTestUtils.cleanUpMicrodataFiles();
		SiloTestUtils.cleanUpOtherFiles();

		boolean cleanupAfterTest = false; // Set to true normally; set to false to be able to inspect files
		String arg = "./test/scenarios/annapolis/javaFiles/siloMatsim_multiYear.properties";
		Config config = ConfigUtils.loadConfig("./test/scenarios/annapolis/matsim_input/config.xml") ;

		//TODO: apparently this is required for some machines, as the test class of utils is not initialized at this point,
		// resulting in exceptions when trying to get output directory 'ana,nico 07/'17
		try {
			utils.initWithoutJUnitForFixture(this.getClass(), this.getClass().getMethod("testMainMultiYear", null));
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		config.controler().setOutputDirectory(utils.getOutputDirectory());
		config.global().setNumberOfThreads(1);
		config.parallelEventHandling().setNumberOfThreads(1);
		config.qsim().setNumberOfThreads(1);

		try {
			SiloMatsim siloMatsim = new SiloMatsim(arg, config);
			siloMatsim.run();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Something did not work") ;
		}{
			log.info("Checking dwellings file ...");
			long checksum_ref = CRCChecksum.getCRCFromFile(utils.getInputDirectory() + "./dd_2000.csv");
			final String filename = "./test/scenarios/annapolis/microData_reduced/dd_2000.csv";
			long checksum_run = CRCChecksum.getCRCFromFile(filename);
			assertEquals("Dwelling files are different", checksum_ref, checksum_run);
			if (cleanupAfterTest) new File(filename).delete();
		}{
			log.info("Checking households file ...");
			long checksum_ref = CRCChecksum.getCRCFromFile(utils.getInputDirectory() + "./hh_2000.csv");
			final String filename = "./test/scenarios/annapolis/microData_reduced/hh_2000.csv";
			long checksum_run = CRCChecksum.getCRCFromFile(filename);
			assertEquals("Household files are different", checksum_ref, checksum_run);
			if (cleanupAfterTest) new File(filename).delete();
		}{
			log.info("Checking jobs file ...");
			long checksum_ref = CRCChecksum.getCRCFromFile(utils.getInputDirectory() + "./jj_2000.csv");
			final String filename = "./test/scenarios/annapolis/microData_reduced/jj_2000.csv";
			long checksum_run = CRCChecksum.getCRCFromFile(filename);
			assertEquals("Job files are different", checksum_ref, checksum_run);
			if (cleanupAfterTest) new File(filename).delete();
		}{
			log.info("checking SILO population file ...");
			long checksum_ref = CRCChecksum.getCRCFromFile(utils.getInputDirectory() + "./pp_2000.csv");
			final String filename = "./test/scenarios/annapolis/microData_reduced/pp_2000.csv";
			long checksum_run = CRCChecksum.getCRCFromFile(filename);
			assertEquals("Population files are different", checksum_ref, checksum_run);
			if (cleanupAfterTest) new File(filename).delete();
		}{
			log.info("Checking dwellings file ...");
			long checksum_ref = CRCChecksum.getCRCFromFile(utils.getInputDirectory() + "./dd_2002.csv");
			final String filename = "./test/scenarios/annapolis/microData_reduced/dd_2002.csv";
			long checksum_run = CRCChecksum.getCRCFromFile(filename);
			assertEquals("Dwelling files are different", checksum_ref, checksum_run);
			if (cleanupAfterTest) new File(filename).delete();
		}{
			log.info("Checking households file ...");
			long checksum_ref = CRCChecksum.getCRCFromFile(utils.getInputDirectory() + "./hh_2002.csv");
			final String filename = "./test/scenarios/annapolis/microData_reduced/hh_2002.csv";
			long checksum_run = CRCChecksum.getCRCFromFile(filename);
			assertEquals("Household files are different", checksum_ref, checksum_run);
			if (cleanupAfterTest) new File(filename).delete();
		}{
			log.info("Checking jobs file ...");
			long checksum_ref = CRCChecksum.getCRCFromFile(utils.getInputDirectory() + "./jj_2002.csv");
			final String filename = "./test/scenarios/annapolis/microData_reduced/jj_2002.csv";
			long checksum_run = CRCChecksum.getCRCFromFile(filename);
			assertEquals("Job files are different", checksum_ref, checksum_run);
			if (cleanupAfterTest) new File(filename).delete();
		}{
			log.info("checking SILO population file ...");
			long checksum_ref = CRCChecksum.getCRCFromFile(utils.getInputDirectory() + "./pp_2002.csv");
			final String filename = "./test/scenarios/annapolis/microData_reduced/pp_2002.csv";
			long checksum_run = CRCChecksum.getCRCFromFile(filename);
			assertEquals("Population files are different", checksum_ref, checksum_run);
			if (cleanupAfterTest) new File(filename).delete();
		}{
			log.info("Checking MATSim plans file for 2000 ...");

			final String referenceFilename = utils.getInputDirectory() + "test_matsim_2000.output_plans.xml.gz";
			final String outputFilename = utils.getOutputDirectory() + "test_matsim_2000/test_matsim_2000.output_plans.xml.gz";
			
			Scenario scRef = ScenarioUtils.createScenario(ConfigUtils.createConfig()) ;
			Scenario scOut = ScenarioUtils.createScenario(ConfigUtils.createConfig()) ;
			
			new PopulationReader(scRef).readFile(referenceFilename);
			new PopulationReader(scOut).readFile(outputFilename);
			
			assertTrue("MATSim populations are different", PopulationUtils.equalPopulation( scRef.getPopulation(), scOut.getPopulation() ) ) ;
		}{
			log.info("Checking MATSim plans file for 2001 ...");

			final String referenceFilename = utils.getInputDirectory() + "test_matsim_2001.output_plans.xml.gz";
			final String outputFilename = utils.getOutputDirectory() + "test_matsim_2001/test_matsim_2001.output_plans.xml.gz";
			
			Scenario scRef = ScenarioUtils.createScenario(ConfigUtils.createConfig()) ;
			Scenario scOut = ScenarioUtils.createScenario(ConfigUtils.createConfig()) ;
			
			new PopulationReader(scRef).readFile(referenceFilename);
			new PopulationReader(scOut).readFile(outputFilename);
			
			assertTrue("MATSim populations are different", PopulationUtils.equalPopulation( scRef.getPopulation(), scOut.getPopulation() ) ) ;
		}{
			log.info("Checking MATSim plans file for 2002 ...");

			final String referenceFilename = utils.getInputDirectory() + "test_matsim_2002.output_plans.xml.gz";
			final String outputFilename = utils.getOutputDirectory() + "test_matsim_2002/test_matsim_2002.output_plans.xml.gz";
			
			Scenario scRef = ScenarioUtils.createScenario(ConfigUtils.createConfig()) ;
			Scenario scOut = ScenarioUtils.createScenario(ConfigUtils.createConfig()) ;
			
			new PopulationReader(scRef).readFile(referenceFilename);
			new PopulationReader(scOut).readFile(outputFilename);
			
			assertTrue("MATSim populations are different", PopulationUtils.equalPopulation( scRef.getPopulation(), scOut.getPopulation() ) ) ;
		}{
			log.info("Checking MATSim events file for 2000 ...");
			final String eventsFilenameReference = utils.getInputDirectory() + "test_matsim_2000.output_events.xml.gz";
			final String eventsFilenameNew = utils.getOutputDirectory() + "test_matsim_2000/test_matsim_2000.output_events.xml.gz";
			assertEquals("Different event files.", EventsFileComparator.Result.FILES_ARE_EQUAL, EventsFileComparator.compare(eventsFilenameReference, eventsFilenameNew));
		}{
			log.info("Checking MATSim events file for 2001 ...");
			final String eventsFilenameReference = utils.getInputDirectory() + "test_matsim_2001.output_events.xml.gz";
			final String eventsFilenameNew = utils.getOutputDirectory() + "test_matsim_2001/test_matsim_2001.output_events.xml.gz";
			assertEquals("Different event files.", EventsFileComparator.Result.FILES_ARE_EQUAL, EventsFileComparator.compare(eventsFilenameReference, eventsFilenameNew));
		}{
			log.info("Checking MATSim events file for 2002 ...");
			final String eventsFilenameReference = utils.getInputDirectory() + "test_matsim_2002.output_events.xml.gz";
			final String eventsFilenameNew = utils.getOutputDirectory() + "test_matsim_2002/test_matsim_2002.output_events.xml.gz";
			assertEquals("Different event files.",EventsFileComparator.Result.FILES_ARE_EQUAL,  EventsFileComparator.compare(eventsFilenameReference, eventsFilenameNew));
		}
		
		if (cleanupAfterTest) {
			File dir = new File(utils.getOutputDirectory());
			IOUtils.deleteDirectoryRecursively(Paths.get(dir.getAbsolutePath()));
			SiloTestUtils.cleanUpMicrodataFiles();
			SiloTestUtils.cleanUpOtherFiles();
		}
	}
}