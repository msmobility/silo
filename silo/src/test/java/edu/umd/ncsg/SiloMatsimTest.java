package edu.umd.ncsg;

import org.junit.Assert;
import org.junit.Test;

public class SiloMatsimTest {
	// TODO
	// * where to put input files (ask MZ).  Matsim uses the @Rule utils = new MatsimTestUtils() which we could copy.
	// * ask RM if reduced input files can be used
	// * put input files into git repository
	// * make test use these input files
	// * run one year of silo and 10 iterations of matsim (for this test case)
	// * use the matsim EventsFileComparator to compare matsim output
	// * also find some way to test the silo output before matsim is run (use CRCChecksum.getCRCFromFile(...) and apply to all the input files
	//    files to matsim ... or test the objects passed to matsim) --- for this it may be necessary to refactor -->inline the "main" method below.

	@SuppressWarnings("static-method")
	@Test
	public final void testMain() {
		try {
			String[] args = {"pathname"} ;
			SiloMatsim.main(args);
		} catch ( Exception ee ) {
			Assert.fail( "something did not work" ) ;
		}
	}

}
