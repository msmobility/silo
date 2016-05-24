package edu.umd.ncsg;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.utils.misc.CRCChecksum;
import org.matsim.utils.eventsfilecomparison.EventsFileComparator;

public class SiloMatsimTest {
	@Rule
	public MatsimTestUtils utils = new MatsimTestUtils();
	
	// TODO
	// * write similar test for SiloMstm on master branch

	/**
	 * This test does only test the downstream coupling: silo data is given to matsim and then iterated.  Possible feedback from matsim
	 * to silo is NOT included in this test (as it was also not included in the ABMTRANS'16 paper).
	 */
	@Test
	@Ignore
	public final void testMain() throws IOException {
		String[] args = {"./test/scenarios/mstm_annapolis/javaFiles/siloMstm_annapolis.properties"}; 

		Config config = ConfigUtils.loadConfig( "./test/scenarios/mstm_annapolis/config/config.xml" ) ;
		// yyyyyy this will all not be passed through.
		
		// reduce number of threads to be on safe side in test (at least until it does not fail any more):
//		config.global().setNumberOfThreads(1);
//		config.parallelEventHandling().setNumberOfThreads(1);
//		config.qsim().setNumberOfThreads(1);
		// yyyyyy this will all not be passed through.

		SiloMatsim siloMatsim = new SiloMatsim(args, config );		

		try {
			siloMatsim.run();
		} catch ( Exception ee ) {
			ee.printStackTrace();
			Assert.fail( "something did not work" ) ;
		}
		
		{
			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./dd_2001.csv");
			long checksum_run = CRCChecksum.getCRCFromFile("./test/scenarios/mstm_annapolis/microData_annapolis/dd_2001.csv");
			assertEquals("Dwelling files are different",  checksum_ref, checksum_run);
		}
		{
			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./hh_2001.csv");
			long checksum_run = CRCChecksum.getCRCFromFile("./test/scenarios/mstm_annapolis/microData_annapolis/hh_2001.csv");
			assertEquals("Household files are different",  checksum_ref, checksum_run);
		}
		{
			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./jj_2001.csv");
			long checksum_run = CRCChecksum.getCRCFromFile("./test/scenarios/mstm_annapolis/microData_annapolis/jj_2001.csv");
			assertEquals("Job files are different",  checksum_ref, checksum_run);
		}
		{
			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./pp_2001.csv");
			long checksum_run = CRCChecksum.getCRCFromFile("./test/scenarios/mstm_annapolis/microData_annapolis/pp_2001.csv");
			assertEquals("Population files are different",  checksum_ref, checksum_run);
		}
		{
			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./regression_test_2001.0.plans.xml.gz");
			long checksum_run = CRCChecksum.getCRCFromFile("./matsimOutput/regression_test_2001/ITERS/it.0/regression_test_2001.0.plans.xml.gz");
			assertEquals("MATSim plans files are different",  checksum_ref, checksum_run);
		}
		
		final String eventsFilenameReference = utils.getInputDirectory() + "./regression_test_2001.0.events.xml.gz";
		final String eventsFilenameNew = "./matsimOutput/regression_test_2001/ITERS/it.0/regression_test_2001.0.events.xml.gz";
		assertEquals("different event files.", EventsFileComparator.compare(eventsFilenameReference, eventsFilenameNew), 0);
	}

	/**
	 * This test does only test the downstream coupling: silo data is given to matsim and then iterated.  Possible feedback from matsim
	 * to silo is NOT included in this test (as it was also not included in the ABMTRANS'16 paper).
	 */
	@Test
	public final void testMainReducedNetwork() throws IOException {
		String[] args = {"./test/scenarios/mstm_annapolis/javaFiles/siloMstm_annapolis.properties"}; 

		Config config = ConfigUtils.loadConfig( "./test/scenarios/mstm_annapolis/config/config_reduced_network.xml" ) ;
		// yyyyyy this will all not be passed through.
		
		// reduce number of threads to be on safe side in test (at least until it does not fail any more):
//		config.global().setNumberOfThreads(1);
//		config.parallelEventHandling().setNumberOfThreads(1);
//		config.qsim().setNumberOfThreads(1);
		// yyyyyy this will all not be passed through.

		SiloMatsim siloMatsim = new SiloMatsim(args, config );		

		try {
			siloMatsim.run();
		} catch ( Exception ee ) {
			ee.printStackTrace();
			Assert.fail( "something did not work" ) ;
		}
		
		{
			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./dd_2001.csv");
			long checksum_run = CRCChecksum.getCRCFromFile("./test/scenarios/mstm_annapolis/microData_annapolis/dd_2001.csv");
			assertEquals("Dwelling files are different",  checksum_ref, checksum_run);
		}
		{
			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./hh_2001.csv");
			long checksum_run = CRCChecksum.getCRCFromFile("./test/scenarios/mstm_annapolis/microData_annapolis/hh_2001.csv");
			assertEquals("Household files are different",  checksum_ref, checksum_run);
		}
		{
			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./jj_2001.csv");
			long checksum_run = CRCChecksum.getCRCFromFile("./test/scenarios/mstm_annapolis/microData_annapolis/jj_2001.csv");
			assertEquals("Job files are different",  checksum_ref, checksum_run);
		}
		{
			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./pp_2001.csv");
			long checksum_run = CRCChecksum.getCRCFromFile("./test/scenarios/mstm_annapolis/microData_annapolis/pp_2001.csv");
			assertEquals("Population files are different",  checksum_ref, checksum_run);
		}
		{
			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./regression_test_2001.0.plans.xml.gz");
			long checksum_run = CRCChecksum.getCRCFromFile("./matsimOutput/regression_test_2001/ITERS/it.0/regression_test_2001.0.plans.xml.gz");
			assertEquals("MATSim plans files are different",  checksum_ref, checksum_run);
		}
		
		final String eventsFilenameReference = utils.getInputDirectory() + "./regression_test_2001.0.events.xml.gz";
		final String eventsFilenameNew = "./matsimOutput/regression_test_2001/ITERS/it.0/regression_test_2001.0.events.xml.gz";
		assertEquals("different event files.", EventsFileComparator.compare(eventsFilenameReference, eventsFilenameNew), 0);
	}

}
