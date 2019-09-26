package de.tum.bgu.msm.transportModel.matsim;

import java.io.File;

import de.tum.bgu.msm.schools.DataContainerWithSchools;
import org.apache.log4j.Logger;
import org.junit.Test;

import de.tum.bgu.msm.DataBuilder;
import de.tum.bgu.msm.ModelBuilderMuc;
import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.io.output.DefaultResultsMonitor;
import de.tum.bgu.msm.io.output.ResultsMonitor;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import junitx.framework.FileAssert;

/**
 * @author dziemke, nico
 */
public class SiloMucTest {
    /**
     * Set to true normally; set to false to be able to inspect files
     */
    private static final boolean CLEANUP_AFTER_TEST = true;

    private static final Logger log = Logger.getLogger(SiloMucTest.class);

    @Test
	public final void testMain() {
		SiloTestUtils.cleanUpMicrodataFiles();
		SiloTestUtils.cleanUpOtherFiles();

        String path = "./test/muc/siloMucTest.properties";
        Properties properties = SiloUtil.siloInitialization(path);

        DataContainerWithSchools dataContainer = DataBuilder.getModelDataForMuc(properties, null);
        DataBuilder.read(properties, dataContainer);

        ModelContainer modelContainer = ModelBuilderMuc.getModelContainerForMuc(dataContainer, properties, null);

        ResultsMonitor resultsMonitor = new DefaultResultsMonitor(dataContainer, properties);
        SiloModel siloModel = new SiloModel(properties, dataContainer, modelContainer, resultsMonitor);
        siloModel.runModel();

        checkDwellings();
        checkHouseholds();
        checkJobs();
        checkPersons();
	}

    private void checkPersons() {
        log.info("checking SILO population file ...");
        final File ref = new File("./test/muc/refOutput/noTransportModel/pp_2013.csv");
        final File actual = new File("./test/muc/scenOutput/test/microData/pp_2013.csv");
        FileAssert.assertEquals("persons are different.", ref, actual);

        if (CLEANUP_AFTER_TEST) {
            actual.delete();
        }
    }

    private void checkJobs() {
        log.info("Checking jobs file ...");
        final File ref = new File("./test/muc/refOutput/noTransportModel/jj_2013.csv");
        final File actual = new File("./test/muc/scenOutput/test/microData/jj_2013.csv");
        FileAssert.assertEquals("jobs are different.", ref, actual);

        if (CLEANUP_AFTER_TEST) {
            actual.delete();
        }
    }

    private void checkHouseholds() {
        log.info("Checking households file ...");
        final File ref = new File("./test/muc/refOutput/noTransportModel/hh_2013.csv");
        final File actual = new File("./test/muc/scenOutput/test/microData/hh_2013.csv");
        FileAssert.assertEquals("households are different.", ref, actual);

        if (CLEANUP_AFTER_TEST) {
            actual.delete();
        }
    }

    private void checkDwellings() {
        log.info("Checking dwellings file ...");
        final File ref = new File("./test/muc/refOutput/noTransportModel/dd_2013.csv");
        final File actual = new File("./test/muc/scenOutput/test/microData/dd_2013.csv");
        FileAssert.assertEquals("dwellings are different.", ref, actual);

        if (CLEANUP_AFTER_TEST) {
            actual.delete();
        }
    }
}