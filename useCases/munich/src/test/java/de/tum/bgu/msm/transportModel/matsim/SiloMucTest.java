package de.tum.bgu.msm.transportModel.matsim;

import de.tum.bgu.msm.DataBuilder;
import de.tum.bgu.msm.ModelBuilderMuc;
import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.io.output.DefaultResultsMonitor;
import de.tum.bgu.msm.io.output.ResultsMonitor;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.utils.SiloUtil;
import junitx.framework.FileAssert;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

/**
 * @author dziemke, nico
 */
public class SiloMucTest {
    /**
     * Set to true normally; set to false to be able to inspect files
     */
    private static final boolean CLEANUP_AFTER_TEST = false;

    private static final Logger log = Logger.getLogger(SiloMucTest.class);

    @Test
    public final void testMain() {

        runThisTest();

        checkDwellings();
        checkHouseholds();
        checkJobs();
        checkPersons();
    }

    private void runThisTest() {
        SiloTestUtils.cleanUpMicrodataFiles();
        SiloTestUtils.cleanUpOtherFiles();

        String path = "./test/muc/siloMucTest.properties";
        Properties properties = SiloUtil.siloInitialization(path);

        DataContainerWithSchools dataContainer = DataBuilder.getModelDataForMuc(properties, null);
        DataBuilder.read(properties, dataContainer);

        ModelContainer modelContainer = ModelBuilderMuc.getModelContainerForMuc(dataContainer, properties, null);

        ResultsMonitor resultsMonitor = new DefaultResultsMonitor(dataContainer, properties);
        SiloModel siloModel = new SiloModel(properties, dataContainer, modelContainer);
        siloModel.addResultMonitor(resultsMonitor);
        siloModel.runModel();
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
        log.info("Ref file exists: " + ref.exists() + ". Actual file exists: " + actual.exists());
        try {
            log.info("Content is equal: " + FileUtils.contentEquals(ref, actual));
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileAssert.assertEquals("dwellings are different.", ref, actual);

        if (CLEANUP_AFTER_TEST) {
            actual.delete();
        }
    }

    @Ignore
    @Test
    /**
     * Replaces the reference files by the results of the test,
     * so the test will be updated. Do not use if you are not sure.
     * The part of the code that makes this changes is commented
     * to prevent undesired uses.
     */
    public final void resetTestFiles() throws IOException {

        runThisTest();

        File ref = new File("./test/muc/refOutput/noTransportModel/dd_2013.csv");
        File actual = new File("./test/muc/scenOutput/test/microData/dd_2013.csv");
        //Files.copy(actual.toPath(), ref.toPath(), StandardCopyOption.REPLACE_EXISTING);

        ref = new File("./test/muc/refOutput/noTransportModel/hh_2013.csv");
        actual = new File("./test/muc/scenOutput/test/microData/hh_2013.csv");
        //Files.copy(actual.toPath(), ref.toPath(), StandardCopyOption.REPLACE_EXISTING);

        ref = new File("./test/muc/refOutput/noTransportModel/jj_2013.csv");
        actual = new File("./test/muc/scenOutput/test/microData/jj_2013.csv");
        //Files.copy(actual.toPath(), ref.toPath(), StandardCopyOption.REPLACE_EXISTING);

        ref = new File("./test/muc/refOutput/noTransportModel/pp_2013.csv");
        actual = new File("./test/muc/scenOutput/test/microData/pp_2013.csv");
        //Files.copy(actual.toPath(), ref.toPath(), StandardCopyOption.REPLACE_EXISTING);

        checkDwellings();
        checkHouseholds();
        checkJobs();
        checkPersons();

    }


}