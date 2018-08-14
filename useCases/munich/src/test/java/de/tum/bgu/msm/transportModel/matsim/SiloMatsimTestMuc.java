package de.tum.bgu.msm.transportModel.matsim;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloMatsim;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
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
import org.matsim.testcases.MatsimTestUtils;
import org.matsim.utils.eventsfilecomparison.EventsFileComparator;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author dziemke, nagel
 */
public class SiloMatsimTestMuc {
	private static final Logger log = Logger.getLogger(SiloMatsimTestMuc.class);

	@Rule public MatsimTestUtils utils = new MatsimTestUtils();
	
	@Test @Ignore
	public final void testMain() {
		SiloTestUtils.cleanUpMicrodataFiles();
		SiloTestUtils.cleanUpOtherFiles();

		boolean cleanupAfterTest = true; // Set to true normally; set to false to be able to inspect files
		String arg = "./test/scenarios/munich_new/javaFiles/siloMatsim.properties";
		Config config = ConfigUtils.loadConfig("./test/scenarios/munich_new/matsim_input/config.xml") ;

		//TODO: apparently this is required for some machines, as the test class of utils is not initialized at this point,
		// resulting in exceptions when trying to get output directory 'ana,nico 07/'17
		try {
			utils.initWithoutJUnitForFixture(this.getClass(), this.getClass().getMethod("testMain", null));
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		config.controler().setOutputDirectory(utils.getOutputDirectory());
		config.global().setNumberOfThreads(1);
		config.parallelEventHandling().setNumberOfThreads(1);
		config.qsim().setNumberOfThreads(1);

		try {
			SiloMatsim siloMatsim = new SiloMatsim(arg, config, Implementation.MUNICH);
			siloMatsim.run();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Something did not work") ;
		}
		{
			log.info("Checking dwellings file ...");
			long checksum_ref = CRCChecksum.getCRCFromFile(utils.getInputDirectory() + "./dd_2011.csv");
			final String filename = "./test/scenarios/munich_new/microData_reduced/dd_2011.csv";
			long checksum_run = CRCChecksum.getCRCFromFile(filename);
			assertEquals("Dwelling files are different", checksum_ref, checksum_run);
			if (cleanupAfterTest) new File(filename).delete();
		}{
			log.info("Checking households file ...");
			long checksum_ref = CRCChecksum.getCRCFromFile(utils.getInputDirectory() + "./hh_2011.csv");
			final String filename = "./test/scenarios/munich_new/microData_reduced/hh_2011.csv";
			long checksum_run = CRCChecksum.getCRCFromFile(filename);
			assertEquals("Household files are different", checksum_ref, checksum_run);
			if (cleanupAfterTest) new File(filename).delete();
		}{
			log.info("Checking jobs file ...");
			long checksum_ref = CRCChecksum.getCRCFromFile(utils.getInputDirectory() + "./jj_2011.csv");
			final String filename = "./test/scenarios/munich_new/microData_reduced/jj_2011.csv";
			long checksum_run = CRCChecksum.getCRCFromFile(filename);
			assertEquals("Job files are different", checksum_ref, checksum_run);
			if (cleanupAfterTest) new File(filename).delete();
		}{
			log.info("checking SILO population file ...");
			long checksum_ref = CRCChecksum.getCRCFromFile(utils.getInputDirectory() + "./pp_2011.csv");
			final String filename = "./test/scenarios/munich_new/microData_reduced/pp_2011.csv";
			long checksum_run = CRCChecksum.getCRCFromFile(filename);
			assertEquals("Population files are different", checksum_ref, checksum_run);
			if (cleanupAfterTest) new File(filename).delete();
		}{
			log.info("Checking MATSim plans file ...");

			final String referenceFilename = utils.getInputDirectory() + "./test_matsim_2011.output_plans.xml.gz";
			final String outputFilename = utils.getOutputDirectory() + "./test_matsim_2011/test_matsim_2011.output_plans.xml.gz";

			Scenario scRef = ScenarioUtils.createScenario(ConfigUtils.createConfig()) ;
			Scenario scOut = ScenarioUtils.createScenario(ConfigUtils.createConfig()) ;
			
			new PopulationReader(scRef).readFile(referenceFilename);
			new PopulationReader(scOut).readFile(outputFilename);
			
			assertTrue("MATSim populations are different", PopulationUtils.equalPopulation( scRef.getPopulation(), scOut.getPopulation() ) ) ; 
		}{
			log.info("Checking MATSim events file ...");
			final String eventsFilenameReference = utils.getInputDirectory() + "./test_matsim_2011.output_events.xml.gz";
			final String eventsFilenameNew = utils.getOutputDirectory() + "./test_matsim_2011/test_matsim_2011.output_events.xml.gz";
			assertEquals("Different event files.", EventsFileComparator.Result.FILES_ARE_EQUAL, EventsFileComparator.compare(eventsFilenameReference, eventsFilenameNew));
		}
		
		// TODO Consider checking accessibilities (currently stored in "testing" directory)
		
		if (cleanupAfterTest) {
			File dir = new File(utils.getOutputDirectory());
			IOUtils.deleteDirectoryRecursively(Paths.get(dir.getAbsolutePath()));
			SiloTestUtils.cleanUpMicrodataFiles();
			SiloTestUtils.cleanUpOtherFiles();
		}
	}
}