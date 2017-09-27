package de.tum.bgu.msm.transportModel.mito;

import de.tum.bgu.msm.SiloMstm;
import de.tum.bgu.msm.transportModel.matsim.MatsimTestUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Created by Nico on 24/07/2017.
 */
public class SiloMitoTest {

    private static final Logger log = Logger.getLogger(SiloMitoTest.class);

    @Rule
    public MatsimTestUtils utils = new MatsimTestUtils();

    public final static String filename_dd = "./test/scenarios/annapolis/microData_reduced/dd_2001.csv";
    public final static String filename_hh = "./test/scenarios/annapolis/microData_reduced/hh_2001.csv";
    public final static String filename_jj = "./test/scenarios/annapolis/microData_reduced/jj_2001.csv";
    public final static String filename_pp = "./test/scenarios/annapolis/microData_reduced/pp_2001.csv";
    public final static String filename_a0 = "./test/scenarios/annapolis/testing/accessibility_2000.csv";
    public final static String filename_a1 = "./test/scenarios/annapolis/testing/accessibility_2001.csv";
    public final static String filename_gi = "./test/scenarios/annapolis/testing/given_impedance_2000.csv";
    public final static String filename_st = "./test/scenarios/annapolis/status.csv";


    public static void cleanUp() {
        log.info("cleaning up ...");
        new File(filename_dd).delete();
        new File(filename_hh).delete();
        new File(filename_jj).delete();
        new File(filename_pp).delete();
        new File(filename_a0).delete();
        new File(filename_a1).delete();
        new File(filename_gi).delete();
        new File(filename_st).delete();
        new File("timeTracker.csv").delete();
        new File("priceUpdate2000.csv").delete();
    }

    /**
     * This test should test Silo together with Mito.
     */
    @Test
    public final void testMainReduced() {
        // yyyy test writes in part to same directory as other tests (e.g. .../microData_reduced/...), which is not so great.  kai, aug'16

        cleanUp();
        String[] args = {"./test/scenarios/annapolis/javaFiles/siloMito.properties"};

        try {
            SiloMstm.main(args);
        } catch (Exception ee) {
            ee.printStackTrace();
            Assert.fail("something did not work");
        }

        cleanUp();
    }
}
