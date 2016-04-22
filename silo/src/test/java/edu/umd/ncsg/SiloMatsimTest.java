package edu.umd.ncsg;

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
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
		System.out.println(utils.getInputDirectory());
		
		String[] args = {"./test/scenarios/mstm_annapolis/javaFiles/siloMstm_annapolis.properties", 
				"./test/scenarios/mstm_annapolis/config/config.xml"};
//		String[] args = {utils.getInputDirectory() + "javaFiles/siloMstm_annapolis.properties"};
		SiloMatsim siloMatsim = new SiloMatsim(args);		
//		ResourceBundle rb = siloMatsim.getRb() ;
//		String outputDir =  rb.getString("matsim.output.directory.root") ;
//		String outputDir = utils.getOutputDirectory();
//		outputDir +=  "/" + rb.getString("matsim.run.id") + "_2001/" ;
//		outputDir +=  "/regression_test/" ;
		

		try {
			siloMatsim.run();
		} catch ( Exception ee ) {
			ee.printStackTrace();
			Assert.fail( "something did not work" ) ;
		}
		
//		long checksum_ref = CRCChecksum.getCRCFromFile("./compare/population_2001.xml");
//		long checksum_run = CRCChecksum.getCRCFromFile("./additional_inout/population_2001.xml");
//		assertEquals(checksum_ref, checksum_run);
		
		final String eventsFilenameReference = utils.getInputDirectory() + "./regression_test_2001.0.events.xml.gz";

//		final String eventsFilenameNew = outputDir + "/ITERS/it.0/run_14_2001.0.events.xml.gz";
		final String eventsFilenameNew = "./matsimOutput/regression_test_2001/ITERS/it.0/regression_test_2001.0.events.xml.gz";
		// yy this is simply what I found in the above "./javaFiles/siloMstm_annapolis.properties"; should probably be changed
		// to something more sensible.  kai, apr'16

		assertEquals("different event files.", EventsFileComparator.compare(eventsFilenameReference, eventsFilenameNew), 0);
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
