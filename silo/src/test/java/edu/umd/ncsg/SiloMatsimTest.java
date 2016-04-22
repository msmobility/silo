package edu.umd.ncsg;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.core.utils.misc.CRCChecksum;
import org.matsim.utils.eventsfilecomparison.EventsFileComparator;

public class SiloMatsimTest {
	@Rule
	public MatsimTestUtils utils = new MatsimTestUtils();
	
	// TODO
	// * write similar test for SiloMstm on master branch

	@Test
	public final void testMain() throws IOException {
		String[] args = {"./test/scenarios/mstm_annapolis/javaFiles/siloMstm_annapolis.properties", 
				"./test/scenarios/mstm_annapolis/config/config.xml"};
		SiloMatsim siloMatsim = new SiloMatsim(args);		

		try {
			siloMatsim.run();
		} catch ( Exception ee ) {
			ee.printStackTrace();
			Assert.fail( "something did not work" ) ;
		}
		
		{
			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./dd_2001.csv");
			long checksum_run = CRCChecksum.getCRCFromFile("./scenarios/mstm_annapolis/microData_annapolis/dd_2001.csv");
			assertEquals("plans files are different",  checksum_ref, checksum_run);
		}
		{
			long checksum_ref = CRCChecksum.getCRCFromFile( utils.getInputDirectory() + "./regression_test_2001.0.plans.xml.gz");
			long checksum_run = CRCChecksum.getCRCFromFile("./matsimOutput/regression_test_2001/ITERS/it.0/regression_test_2001.0.plans.xml.gz");
			assertEquals("plans files are different",  checksum_ref, checksum_run);
		}
		
		final String eventsFilenameReference = utils.getInputDirectory() + "./regression_test_2001.0.events.xml.gz";
		final String eventsFilenameNew = "./matsimOutput/regression_test_2001/ITERS/it.0/regression_test_2001.0.events.xml.gz";
		assertEquals("different event files.", EventsFileComparator.compare(eventsFilenameReference, eventsFilenameNew), 0);
	}

}
