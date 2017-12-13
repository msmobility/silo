package de.tum.bgu.msm.transportModel.mito;

import de.tum.bgu.msm.SiloMstm;
import de.tum.bgu.msm.transportModel.SiloTestUtils;
import de.tum.bgu.msm.transportModel.matsim.MatsimTestUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

public class SiloMitoTest {

    private static final Logger log = Logger.getLogger(SiloMitoTest.class);


    @Rule
    public MatsimTestUtils utils = new MatsimTestUtils();


    /**
     * This test should test Silo together with Mito.
     */

    @Ignore
    @Test
    public final void testMainReduced() {

        SiloTestUtils.cleanUpMicrodataFiles();
        SiloTestUtils.cleanUpOtherFiles();

        String[] args = {"./test/scenarios/annapolis/javaFiles/siloMito.properties"};

        try {
            SiloMstm.main(args);
        } catch (Exception ee) {
            ee.printStackTrace();
            Assert.fail("something did not work");
        }

        SiloTestUtils.cleanUpMicrodataFiles();
        SiloTestUtils.cleanUpOtherFiles();
    }
}
