package de.tum.bgu.msm;

import static org.junit.Assert.*;

import java.io.File;

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
import org.matsim.utils.eventsfilecomparison.EventsFileComparator;

public class SiloMatsimTest {
	private static final Logger log = Logger.getLogger(SiloMatsimTest.class);

	@Rule public MatsimTestUtils utils = new MatsimTestUtils();
	
	// TODO
	// * fix compilcation problems; make sure that current test is still ok -- DONE
	// * define smaller test case
	// * write similar test (for smaller scenario) for SiloMstm on master branch
	// * make matsim vs. mstm configurable vs. resource bundle
	// * merge master branch in siloMatsim and fix possible errors

	/**
	 * This test does only test the downstream coupling: silo data is given to matsim and then iterated.  Possible feedback from matsim
	 * to silo is NOT included in this test (as it was also not included in the ABMTRANS'16 paper).
	 */
	@Test
	@Ignore
	public final void testMainAnnapolis() {
		SiloMstmTest.cleanUp();
		
		String arg = "./test/scenarios/annapolis/javaFiles/siloMatsim_annapolis.properties";

		Config config = ConfigUtils.loadConfig( "./test/scenarios/annapolis/matsim/config.xml" ) ;
		config.controler().setOutputDirectory( utils.getOutputDirectory() ) ;
		
		// reduce number of threads to be on safe side in test (at least until it does not fail any more):
		config.global().setNumberOfThreads(1);
		config.parallelEventHandling().setNumberOfThreads(1);
		config.qsim().setNumberOfThreads(1);

		SiloMatsim siloMatsim = new SiloMatsim(arg, config );

		try {
			siloMatsim.run();
		} catch ( Exception ee ) {
			ee.printStackTrace();
			Assert.fail( "something did not work" ) ;
		}
		// This seems to be running out of resources on travis so I am reducing the IO here:
//		{
//			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./dd_2001.csv");
//			long checksum_run = CRCChecksum.getCRCFromFile("./test/scenarios/mstm_annapolis/microData_annapolis/dd_2001.csv");
//			assertEquals("Dwelling files are different",  checksum_ref, checksum_run);
//		}{
//			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./hh_2001.csv");
//			long checksum_run = CRCChecksum.getCRCFromFile("./test/scenarios/mstm_annapolis/microData_annapolis/hh_2001.csv");
//			assertEquals("Household files are different",  checksum_ref, checksum_run);
//		}{
//			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./jj_2001.csv");
//			long checksum_run = CRCChecksum.getCRCFromFile("./test/scenarios/mstm_annapolis/microData_annapolis/jj_2001.csv");
//			assertEquals("Job files are different",  checksum_ref, checksum_run);
//		}{
//			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./pp_2001.csv");
//			long checksum_run = CRCChecksum.getCRCFromFile("./test/scenarios/mstm_annapolis/microData_annapolis/pp_2001.csv");
//			assertEquals("Population files are different",  checksum_ref, checksum_run);
//		}
		{
			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./regression_test_2001.0.plans.xml.gz");
			long checksum_run = CRCChecksum.getCRCFromFile( utils.getOutputDirectory() + "./matsimOutput/regression_test_2001/ITERS/it.0/regression_test_2001.0.plans.xml.gz");
			assertEquals("MATSim plans files are different",  checksum_ref, checksum_run);
		}
		
		final String eventsFilenameReference = utils.getInputDirectory() + "./regression_test_2001.0.events.xml.gz";
		final String eventsFilenameNew = utils.getOutputDirectory() + "./matsimOutput/regression_test_2001/ITERS/it.0/regression_test_2001.0.events.xml.gz";
		assertEquals("different event files.", EventsFileComparator.compare(eventsFilenameReference, eventsFilenameNew), 0);
	}

	
	/**
	 * This test does only test the downstream coupling: silo data is given to matsim and then iterated.  Possible feedback from matsim
	 * to silo is NOT included in this test (as it was also not included in the ABMTRANS'16 paper).
	 */
	@Ignore
	@Test
	public final void testMainAnnapolisReducedNetwork() {
		String arg = "./test/scenarios/annapolis/javaFiles/siloMatsim_annapolis.properties";

		Config config = ConfigUtils.loadConfig( "./test/scenarios/annapolis/matsim/config_reduced_network.xml" ) ;
		
		// reduce number of threads to be on safe side in test (at least until it does not fail any more):
		config.global().setNumberOfThreads(1);
		config.parallelEventHandling().setNumberOfThreads(1);
		config.qsim().setNumberOfThreads(1);

		SiloMatsim siloMatsim = new SiloMatsim(arg, config );

		try {
			siloMatsim.run();
		} catch ( Exception ee ) {
			ee.printStackTrace();
			Assert.fail( "something did not work" ) ;
		}{
			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./dd_2001.csv");
			long checksum_run = CRCChecksum.getCRCFromFile("./test/scenarios/mstm_annapolis/microData_annapolis/dd_2001.csv");
			assertEquals("Dwelling files are different",  checksum_ref, checksum_run);
		}{
			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./hh_2001.csv");
			long checksum_run = CRCChecksum.getCRCFromFile("./test/scenarios/mstm_annapolis/microData_annapolis/hh_2001.csv");
			assertEquals("Household files are different",  checksum_ref, checksum_run);
		}{
			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./jj_2001.csv");
			long checksum_run = CRCChecksum.getCRCFromFile("./test/scenarios/mstm_annapolis/microData_annapolis/jj_2001.csv");
			assertEquals("Job files are different",  checksum_ref, checksum_run);
		}{
			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./pp_2001.csv");
			long checksum_run = CRCChecksum.getCRCFromFile("./test/scenarios/mstm_annapolis/microData_annapolis/pp_2001.csv");
			assertEquals("Population files are different",  checksum_ref, checksum_run);
		}{
			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./regression_test_2001.0.plans.xml.gz");
			long checksum_run = CRCChecksum.getCRCFromFile("./matsimOutput/regression_test_2001/ITERS/it.0/regression_test_2001.0.plans.xml.gz");
			assertEquals("MATSim plans files are different",  checksum_ref, checksum_run);
		}{
			final String eventsFilenameReference = utils.getInputDirectory() + "./regression_test_2001.0.events.xml.gz";
			final String eventsFilenameNew = "./matsimOutput/regression_test_2001/ITERS/it.0/regression_test_2001.0.events.xml.gz";
			assertEquals("different event files.", EventsFileComparator.compare(eventsFilenameReference, eventsFilenameNew), 0);
		}
		
		// TODO Consider checking accessibilites (currently stored in "testing" directory)
	}
	
	
	/**
	 * This test does only test the downstream coupling: silo data is given to matsim and then iterated.  Possible feedback from matsim
	 * to silo is NOT included in this test (as it was also not included in the ABMTRANS'16 paper).
	 */
	@Test
	public final void testMainReduced() {
		SiloMstmTest.cleanUp();

		String arg = "./test/scenarios/annapolis_reduced/javaFiles/siloMatsim_reduced.properties";

		Config config = ConfigUtils.loadConfig( "./test/scenarios/annapolis_reduced/matsim/config.xml" ) ;

		config.controler().setOutputDirectory(utils.getOutputDirectory() );
		
		// reduce number of threads to be on safe side in test (at least until it does not fail any more):
		config.global().setNumberOfThreads(1);
		config.parallelEventHandling().setNumberOfThreads(1);
		config.qsim().setNumberOfThreads(1);


		try {
			SiloMatsim siloMatsim = new SiloMatsim(arg, config );
			siloMatsim.run();
		} catch ( Exception ee ) {
			ee.printStackTrace();
			Assert.fail( "something did not work" ) ;
		}
		{
			log.info("checking dwellings file ...");
			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./dd_2001.csv");
			final String filename = "./test/scenarios/annapolis_reduced/microData_reduced/dd_2001.csv";
			long checksum_run = CRCChecksum.getCRCFromFile(filename);
			assertEquals("Dwelling files are different",  checksum_ref, checksum_run);
			// clean up: 
			new File( filename ).delete() ;
		}{
			log.info("checking households file ...");
			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./hh_2001.csv");
			final String filename = "./test/scenarios/annapolis_reduced/microData_reduced/hh_2001.csv";
			long checksum_run = CRCChecksum.getCRCFromFile(filename);
			assertEquals("Household files are different",  checksum_ref, checksum_run);
			// clean up: 
			new File( filename ).delete() ;
		}{
			log.info("checking jobs file ...");
			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./jj_2001.csv");
			final String filename = "./test/scenarios/annapolis_reduced/microData_reduced/jj_2001.csv";
			long checksum_run = CRCChecksum.getCRCFromFile(filename);
			assertEquals("Job files are different",  checksum_ref, checksum_run);
			// clean up: 
			new File( filename ).delete() ;
		}{
			log.info("checking SILO population file ...");
			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./pp_2001.csv");
			final String filename = "./test/scenarios/annapolis_reduced/microData_reduced/pp_2001.csv";
			long checksum_run = CRCChecksum.getCRCFromFile(filename);
			assertEquals("Population files are different",  checksum_ref, checksum_run);
			// clean up: 
			new File( filename ).delete() ;
		}
		{
			log.info("checking MATSim plans file ...");

			final String referenceFilename = utils.getInputDirectory() + "./test_reduced_2001.0.plans.xml.gz";
			final String outputFilename = utils.getOutputDirectory() + "./test_reduced_matsim_2001/ITERS/it.0/test_reduced_matsim_2001.0.plans.xml.gz";

			Scenario scRef = ScenarioUtils.createScenario(ConfigUtils.createConfig()) ;
			Scenario scOut = ScenarioUtils.createScenario(ConfigUtils.createConfig()) ;
			
			new PopulationReader(scRef).readFile(referenceFilename);
			new PopulationReader(scOut).readFile(outputFilename);
			
			assertTrue( "MATSim populations are different", PopulationUtils.equalPopulation( scRef.getPopulation(), scOut.getPopulation() ) ) ; 
			
//			long checksum_ref = CRCChecksum.getCRCFromFile( referenceFilename);
//			long checksum_run = CRCChecksum.getCRCFromFile(outputFilename);
//			assertEquals("MATSim plans files are different",  checksum_ref, checksum_run);
		}{
			log.info("checking MATSim events file ...");
			final String eventsFilenameReference = utils.getInputDirectory() + "./test_reduced_2001.0.events.xml.gz";
			final String eventsFilenameNew = utils.getOutputDirectory() + "./test_reduced_matsim_2001/ITERS/it.0/test_reduced_matsim_2001.0.events.xml.gz";
			assertEquals("different event files.", EventsFileComparator.compare(eventsFilenameReference, eventsFilenameNew), 0);
		}
		
		// TODO Consider checking accessibilites (currently stored in "testing" directory)
		
		// clean up after yourself: 
		File dir = new File( utils.getOutputDirectory() ) ;
		IOUtils.deleteDirectory(dir);
		
		SiloMstmTest.cleanUp(); 
	}
}