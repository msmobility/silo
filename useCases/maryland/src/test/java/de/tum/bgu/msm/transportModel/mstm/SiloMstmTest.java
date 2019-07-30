package de.tum.bgu.msm.transportModel.mstm;

import de.tum.bgu.msm.data.MitoTrip;
import de.tum.bgu.msm.transportModel.SiloTestUtils;
import junitx.framework.FileAssert;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.testcases.MatsimTestUtils;
import de.tum.bgu.msm.run.SiloMstm;

import java.io.File;

public class SiloMstmTest {
    private static final Logger LOG = Logger.getLogger(SiloMstmTest.class);

    private final static String filename_dd = "./test/scenarios/annapolis/scenOutput/test_reduced_mstm/microData/dd_2001.csv";
    private final static String filename_hh = "./test/scenarios/annapolis/scenOutput/test_reduced_mstm/microData/hh_2001.csv";
    private final static String filename_jj = "./test/scenarios/annapolis/scenOutput/test_reduced_mstm/microData/jj_2001.csv";
    private final static String filename_pp = "./test/scenarios/annapolis/scenOutput/test_reduced_mstm/microData/pp_2001.csv";

    @Rule
    public MatsimTestUtils utils = new MatsimTestUtils();

    /**
     * This test does NOT test MSTM, despite the name: transport.model.years is set to -1, effectively ignoring the transport model.
     */
    @Test
    public final void testMain() {
        // yyyy test writes in part to same directory as other tests (e.g. .../microData_reduced/...), which is not so great.  kai, aug'16

        SiloTestUtils.cleanUpMicrodataFiles();
        SiloTestUtils.cleanUpOtherFiles();

        String[] args = {"test/scenarios/annapolis/javaFiles/siloMstm.properties"};

        try {
            SiloMstm.main(args);
        } catch (Exception ee) {
            ee.printStackTrace();
            Assert.fail("something did not work");
        }

        //TODO: apparently this is required for some machines, as the test class of utils is not initialized at this point,
        // resulting in exceptions when trying to get output directory 'ana,nico 07/'17
        try {
            utils.initWithoutJUnitForFixture(this.getClass(), this.getClass().getMethod("testMain", null));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }


        // The following passes when the test is run alone, but fails when all tests are run in conjunction.  Not sure what it is ...
        // ... but it is certainly not good that multiple test write in the same directories.  "scenarios" should be something like shared input
        // for big files that we don't want to repeat, but it is not a good place for output.
        // If the SILO directory structure is fairly strongly hardcoded, an option would be that tests clean up after themselves.
        // kai, aug'16
        // This is for the time being resolved by "forkmode=always" in the silo pom.xml, meaning that each test
        // starts a separate JVM.  kai, aug'16

        FileAssert.assertEquals("dwellings are different.", new File(utils.getInputDirectory() + "./dd_2001.csv"), new File(filename_dd));
        FileAssert.assertEquals("households are different.", new File(utils.getInputDirectory() + "./hh_2001.csv"), new File(filename_hh));
        FileAssert.assertEquals("jobs are different.", new File(utils.getInputDirectory() + "./jj_2001.csv"), new File(filename_jj));
        FileAssert.assertEquals("populations are different.", new File(utils.getInputDirectory() + "./pp_2001.csv"), new File(filename_pp));

        SiloTestUtils.cleanUpMicrodataFiles();
        SiloTestUtils.cleanUpOtherFiles();
    }
}
