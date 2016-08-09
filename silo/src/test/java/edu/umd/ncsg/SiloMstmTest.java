package edu.umd.ncsg;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.core.utils.misc.CRCChecksum;

public class SiloMstmTest {
	@Rule
	public MatsimTestUtils utils = new MatsimTestUtils();
	
	
	
	/**
	 * This test does NOT test MSTM, despite the name: transport.model.years is set to -1, effectively ignoring the transport model.
	 */
//	@Ignore
	@Test
	public final void testMainReduced() {
		// yyyy test writes in part to same directory as other tests (e.g. .../microData_reduced/...), which is not so great.  kai, aug'16
		
		String[] args = {"./test/scenarios/annapolis_reduced/javaFiles/siloMstm_reduced.properties"}; 

		try {
			SiloMstm.main( args );
		} catch ( Exception ee ) {
			ee.printStackTrace();
			Assert.fail( "something did not work" ) ;
		}
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