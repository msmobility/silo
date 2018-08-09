package de.tum.bgu.msm.transportModel;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * @author dziemke
 */
public class SiloTestUtils {
	private static final Logger log = Logger.getLogger(SiloTestUtils.class) ;
	
	private final static String filename_dd = "./test/scenarios/annapolis/microData_reduced/dd_2001.csv";
	private final static String filename_hh = "./test/scenarios/annapolis/microData_reduced/hh_2001.csv";
	private final static String filename_jj = "./test/scenarios/annapolis/microData_reduced/jj_2001.csv";
	private final static String filename_pp = "./test/scenarios/annapolis/microData_reduced/pp_2001.csv";
	private final static String filename_a0 = "./test/scenarios/annapolis/testing/accessibility_2000.csv";
	private final static String filename_a1 = "./test/scenarios/annapolis/testing/accessibility_2001.csv";
	private final static String filename_gi = "./test/scenarios/annapolis/testing/given_impedance_2000.csv";
	private final static String filename_st = "./test/scenarios/annapolis/status.csv";

	public static void cleanUpMicrodataFiles() {
		log.info("Cleaning up microdata files...");
		new File(filename_dd).delete() ;
		new File(filename_hh).delete() ;
		new File(filename_jj).delete() ;
		new File(filename_pp).delete() ;
	}
	
	public static void cleanUpOtherFiles() {
		log.info("Cleaning up other files ...");
		new File(filename_a0).delete() ;
		new File(filename_a1).delete() ;
		new File(filename_gi).delete() ;
		new File(filename_st).delete() ;
		new File("timeTracker.csv").delete();
		new File("priceUpdate2000.csv").delete();
	}
}