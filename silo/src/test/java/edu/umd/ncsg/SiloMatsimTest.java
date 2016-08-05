package edu.umd.ncsg;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.utils.eventsfilecomparison.EventsFileComparator;

import jxl.common.Logger;

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
		String[] args = {"./test/scenarios/annapolis/javaFiles/siloMatsim_annapolis.properties"}; 

		Config config = ConfigUtils.loadConfig( "./test/scenarios/annapolis/matsim/config.xml" ) ;
		config.controler().setOutputDirectory( utils.getOutputDirectory() ) ;
		
		// reduce number of threads to be on safe side in test (at least until it does not fail any more):
		config.global().setNumberOfThreads(1);
		config.parallelEventHandling().setNumberOfThreads(1);
		config.qsim().setNumberOfThreads(1);

		SiloMatsim siloMatsim = new SiloMatsim(args, config );		

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
		String[] args = {"./test/scenarios/annapolis/javaFiles/siloMatsim_annapolis.properties"}; 

		Config config = ConfigUtils.loadConfig( "./test/scenarios/annapolis/matsim/config_reduced_network.xml" ) ;
		
		// reduce number of threads to be on safe side in test (at least until it does not fail any more):
		config.global().setNumberOfThreads(1);
		config.parallelEventHandling().setNumberOfThreads(1);
		config.qsim().setNumberOfThreads(1);

		SiloMatsim siloMatsim = new SiloMatsim(args, config );		

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
//	@Ignore
	@Test
	public final void testMainReduced() {
		String[] args = {"./test/scenarios/annapolis_reduced/javaFiles/siloMatsim_reduced.properties"}; 

		Config config = ConfigUtils.loadConfig( "./test/scenarios/annapolis_reduced/matsim/config.xml" ) ;
		
		// reduce number of threads to be on safe side in test (at least until it does not fail any more):
		config.global().setNumberOfThreads(1);
		config.parallelEventHandling().setNumberOfThreads(1);
		config.qsim().setNumberOfThreads(1);

		SiloMatsim siloMatsim = new SiloMatsim(args, config );		

		try {
			siloMatsim.run();
		} catch ( Exception ee ) {
			ee.printStackTrace();
			Assert.fail( "something did not work" ) ;
		}
//		{
//			log.info("checking dwellings file ...");
//			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./dd_2001.csv");
//			long checksum_run = CRCChecksum.getCRCFromFile("./test/scenarios/annapolis_reduced/microData_reduced/dd_2001.csv");
//			assertEquals("Dwelling files are different",  checksum_ref, checksum_run);
//		}{
//			log.info("checking households file ...");
//			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./hh_2001.csv");
//			long checksum_run = CRCChecksum.getCRCFromFile("./test/scenarios/annapolis_reduced/microData_reduced/hh_2001.csv");
//			assertEquals("Household files are different",  checksum_ref, checksum_run);
//		}{
//			log.info("checking jobs file ...");
//			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./jj_2001.csv");
//			long checksum_run = CRCChecksum.getCRCFromFile("./test/scenarios/annapolis_reduced/microData_reduced/jj_2001.csv");
//			assertEquals("Job files are different",  checksum_ref, checksum_run);
//		}{
//			log.info("checking SILO population file ...");
//			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./pp_2001.csv");
//			long checksum_run = CRCChecksum.getCRCFromFile("./test/scenarios/annapolis_reduced/microData_reduced/pp_2001.csv");
//			assertEquals("Population files are different",  checksum_ref, checksum_run);
//		}
		{
			log.info("checking MATSim plans file ...");
			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./test_reduced_2001.0.plans.xml.gz");
			log.info("checksum_ref=" + checksum_ref ) ;
			long checksum_run = CRCChecksum.getCRCFromFile("./matsimOutput/test_reduced_2001/ITERS/it.0/test_reduced_2001.0.plans.xml.gz");
			log.info("checksum_ref=" + checksum_run ) ;
			assertEquals("MATSim plans files are different",  checksum_ref, checksum_run);
		}{
			log.info("checking MATSim events file ...");
			final String eventsFilenameReference = utils.getInputDirectory() + "./test_reduced_2001.0.events.xml.gz";
			final String eventsFilenameNew = "./matsimOutput/test_reduced_2001/ITERS/it.0/test_reduced_2001.0.events.xml.gz";
			assertEquals("different event files.", EventsFileComparator.compare(eventsFilenameReference, eventsFilenameNew), 0);
		}
		
		// TODO Consider checking accessibilites (currently stored in "testing" directory)
	}
}