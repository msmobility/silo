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
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.testcases.MatsimTestUtils;
import org.matsim.utils.eventsfilecomparison.EventsFileComparator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author dziemke, nagel
 */
public class SiloMatsimMucTest {

    /**
     * Set to true normally; set to false to be able to inspect files
     */
    private static final boolean CLEANUP_AFTER_TEST = true;


    @Rule
    public MatsimTestUtils utils = new MatsimTestUtils();

    private static final Logger log = Logger.getLogger(SiloMatsimMucTest.class);

    @Test
	public final void testMain() {

        runThisTest();

        checkDwellings();
        checkHouseholds();
        checkJobs();
        checkPersons();
        checkPlans();
        checkEvents();

//		if (CLEANUP_AFTER_TEST) {
//			IOUtils.deleteDirectoryRecursively(Paths.get(dir.getAbsolutePath()));
//          Cannot delete a logfile that it is in use!
//		}
	}


    private void runThisTest(){
        String path = "./test/muc/siloMatsimMucTest.properties";
        Config config = ConfigUtils.loadConfig("./test/muc/matsim_input/config.xml") ;

        try {
            utils.initWithoutJUnitForFixture(this.getClass(), this.getClass().getMethod("testMain", null));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        File dir = new File("./test/muc/scenOutput/test/");

        config.global().setNumberOfThreads(1);
        config.parallelEventHandling().setNumberOfThreads(1);
        config.qsim().setNumberOfThreads(1);

        Properties properties = SiloUtil.siloInitialization(path);

        DataContainerWithSchools dataContainer = DataBuilder.getModelDataForMuc(properties, config);
        DataBuilder.read(properties, dataContainer);

        ModelContainer modelContainer = ModelBuilderMuc.getModelContainerForMuc(dataContainer, properties, config);

        ResultsMonitor resultsMonitor = new DefaultResultsMonitor(dataContainer, properties);
        SiloModel siloModel = new SiloModel(properties, dataContainer, modelContainer);
        siloModel.addResultMonitor(resultsMonitor);
        siloModel.runModel();
    }

    private void checkEvents() {
        log.info("Checking MATSim events file ...");
        final String eventsFilenameReference = "./test/muc/refOutput/matsim/test_2013/2013.output_events.xml.gz";
        final String eventsFilenameNew = "./test/muc/scenOutput/test/matsim/2013/2013.output_events.xml.gz";
        assertEquals("Different event files.", EventsFileComparator.Result.FILES_ARE_EQUAL, EventsFileComparator.compare(eventsFilenameReference, eventsFilenameNew));
    }

    private void checkPlans() {
        log.info("Checking MATSim plans file ...");

        final String referenceFilename = "./test/muc/refOutput/matsim/test_2013/2013.output_plans.xml.gz";
        final String outputFilename = "./test/muc/scenOutput/test/matsim/2013/2013.output_plans.xml.gz";
        Scenario scRef = ScenarioUtils.createScenario(ConfigUtils.createConfig()) ;
        Scenario scOut = ScenarioUtils.createScenario(ConfigUtils.createConfig()) ;

        new PopulationReader(scRef).readFile(referenceFilename);
        new PopulationReader(scOut).readFile(outputFilename);

        assertTrue("MATSim populations are different", PopulationUtils.equalPopulation( scRef.getPopulation(), scOut.getPopulation() ) ) ;
    }

    private void checkPersons() {
        log.info("checking SILO population file ...");
        final File ref = new File("./test/muc/refOutput/matsim/pp_2013.csv");
        final File actual = new File("./test/muc/scenOutput/test/microData/pp_2013.csv");
        FileAssert.assertEquals("persons are different.", ref, actual);

        if (CLEANUP_AFTER_TEST) {
            actual.delete();
        }
    }

    private void checkJobs() {
        log.info("Checking jobs file ...");
        final File ref = new File("./test/muc/refOutput/matsim/jj_2013.csv");
        final File actual = new File("./test/muc/scenOutput/test/microData/jj_2013.csv");
        FileAssert.assertEquals("jobs are different.", ref, actual);

        if (CLEANUP_AFTER_TEST) {
            actual.delete();
        }
    }

    private void checkHouseholds() {
        log.info("Checking households file ...");
        final File ref = new File("./test/muc/refOutput/matsim/hh_2013.csv");
        final File actual = new File("./test/muc/scenOutput/test/microData/hh_2013.csv");
        FileAssert.assertEquals("households are different.", ref, actual);

        if (CLEANUP_AFTER_TEST) {
            actual.delete();
        }
    }

    private void checkDwellings() {
        log.info("Checking dwellings file ...");
        final File ref = new File("./test/muc/refOutput/matsim/dd_2013.csv");
        final File actual = new File("./test/muc/scenOutput/test/microData/dd_2013.csv");
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

        File ref = new File("./test/muc/refOutput/matsim/dd_2013.csv");
        File actual = new File("./test/muc/scenOutput/test/microData/dd_2013.csv");
        //Files.copy(actual.toPath(), ref.toPath(), StandardCopyOption.REPLACE_EXISTING);

        ref = new File("./test/muc/refOutput/matsim/hh_2013.csv");
        actual = new File("./test/muc/scenOutput/test/microData/hh_2013.csv");
        //Files.copy(actual.toPath(), ref.toPath(), StandardCopyOption.REPLACE_EXISTING);

        ref = new File("./test/muc/refOutput/matsim/jj_2013.csv");
        actual = new File("./test/muc/scenOutput/test/microData/jj_2013.csv");
        //Files.copy(actual.toPath(), ref.toPath(), StandardCopyOption.REPLACE_EXISTING);

        ref = new File("./test/muc/refOutput/matsim/pp_2013.csv");
        actual = new File("./test/muc/scenOutput/test/microData/pp_2013.csv");
        //Files.copy(actual.toPath(), ref.toPath(), StandardCopyOption.REPLACE_EXISTING);

        ref = new File("./test/muc/refOutput/matsim/test_2013/2013.output_events.xml.gz");
        actual = new File("./test/muc/scenOutput/test/matsim/2013/2013.output_events.xml.gz");
        //Files.copy(actual.toPath(), ref.toPath(), StandardCopyOption.REPLACE_EXISTING);


        ref = new File("./test/muc/refOutput/matsim/test_2013/2013.output_plans.xml.gz");
        actual = new File("./test/muc/scenOutput/test/matsim/2013/2013.output_plans.xml.gz");
        //Files.copy(actual.toPath(), ref.toPath(), StandardCopyOption.REPLACE_EXISTING);

        checkDwellings();
        checkHouseholds();
        checkJobs();
        checkPersons();
        checkPlans();
        checkEvents();

    }

}