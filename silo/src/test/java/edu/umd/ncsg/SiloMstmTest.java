package edu.umd.ncsg;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

public class SiloMstmTest {
	@Rule
	public MatsimTestUtils utils = new MatsimTestUtils();
	
	
	
	/**
	 * This test does NOT test MSTM, despite the name: transport.model.years is set to -1, effectively ignoring the transport model.
	 */
	@Test
	@Ignore
	public final void testMainReduced() {
		// yyyy test writes in part to same directory as other tests (e.g. .../microData_reduced/...), which is not so great.  kai, aug'16
		
		String[] args = {"./test/scenarios/annapolis_reduced/javaFiles/siloMstm_reduced.properties"}; 

		try {
			SiloMstm.main(args);
		} catch ( Exception ee ) {
			ee.printStackTrace();
			Assert.fail( "something did not work" ) ;
		}
		
		// yyyyyy The following passes when the test is run alone, but fails when all tests are run in conjunction.  Not sure what it is ...
		// ... but it is certainly not good that multiple test write in the same directories.  "scenarios" should be something like shared input
		// for big files that we don't want to repeat, but it is not a good place for output.
		// If the SILO directory structure is fairly strongly hardcoded, an option would be that tests clean up after themselves.
		// kai, aug'16
		
//		{
//			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./dd_2001.csv");
//			long checksum_run = CRCChecksum.getCRCFromFile("./test/scenarios/annapolis_reduced/microData_reduced/dd_2001.csv");
//			assertEquals("Dwelling files are different",  checksum_ref, checksum_run);
//		}{
//			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./hh_2001.csv");
//			long checksum_run = CRCChecksum.getCRCFromFile("./test/scenarios/annapolis_reduced/microData_reduced/hh_2001.csv");
//			assertEquals("Household files are different",  checksum_ref, checksum_run);
//		}{
//			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./jj_2001.csv");
//			long checksum_run = CRCChecksum.getCRCFromFile("./test/scenarios/annapolis_reduced/microData_reduced/jj_2001.csv");
//			assertEquals("Job files are different",  checksum_ref, checksum_run);
//		}{
//			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./pp_2001.csv");
//			long checksum_run = CRCChecksum.getCRCFromFile("./test/scenarios/annapolis_reduced/microData_reduced/pp_2001.csv");
//			assertEquals("Population files are different",  checksum_ref, checksum_run);
//		}
		
		// TODO Consider checking accessibilites (currently stored in "testing" directory)
	}
}