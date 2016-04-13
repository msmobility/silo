package edu.umd.ncsg;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
	// * where to put input files (ask MZ).  Matsim uses the @Rule utils = new MatsimTestUtils() which we could copy.
	// * ask RM if reduced input files can be used
	// * put input files into git repository
	// * make test use these input files
	// * run one year of silo and 10 iterations of matsim (for this test case)
	// * use the matsim EventsFileComparator to compare matsim output
	// * also find some way to test the silo output before matsim is run (use CRCChecksum.getCRCFromFile(...) and apply to all the input files
	//    files to matsim ... or test the objects passed to matsim) --- for this it may be necessary to refactor -->inline the "main" method below.
	//
	// * write similar test for SiloMstm on master branch

	@SuppressWarnings("static-method")
	@Test
	public final void testMain() throws IOException {
		
//		writeDummyFileToTestWhereOutputGoes("test/input/scenarios/mstm_annapolis/hello.csv");
		
//		utils.getInputDirectory();
//		
//		System.out.println(utils.getInputDirectory());
		
		try {
//			String[] args = {"test/input/scenarios/mstm_annapolis/javaFiles/siloMstm_annapolis.properties"};
			String[] args = {"./javaFiles/siloMstm_annapolis.properties"};
			SiloMatsim.main(args);
		} catch ( Exception ee ) {
			Assert.fail( "something did not work" ) ;
		}
		
//		long checksum_ref = CRCChecksum.getCRCFromFile("./compare/population_2001.xml");
//		long checksum_run = CRCChecksum.getCRCFromFile("./additional_inout/population_2001.xml");
//		assertEquals(checksum_ref, checksum_run);
		
		assertEquals("different event files.", EventsFileComparator.compare("./compare/run_14_2001.0.events.xml.gz", 
				"./matsim/run_14_2001/ITERS/it.0/run_14_2001.0.events.xml.gz"), 0);
	}

	
	// just here for testing
	static void writeDummyFileToTestWhereOutputGoes(String outputFile) throws IOException {
		File output = new File(outputFile);
		FileWriter fileWriter = new FileWriter(output);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

		bufferedWriter.write("wargrbserbrebr");
		bufferedWriter.newLine();
		
        try {
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch (IOException ex) {
        	ex.printStackTrace();
        }

        System.out.println("Done with writing the test file.");	
	}
}
