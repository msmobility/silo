package de.tum.bgu.msm.transportModel.matsim;

import de.tum.bgu.msm.DataBuilder;
import de.tum.bgu.msm.ModelBuilder;
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
import org.matsim.core.utils.io.IOUtils;
import org.matsim.testcases.MatsimTestUtils;
import org.matsim.utils.eventsfilecomparison.EventsFileComparator;

import java.io.File;
import java.nio.file.Paths;

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
    @Ignore
	public final void testMain() {

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

        ModelContainer modelContainer = ModelBuilder.getModelContainerForMuc(dataContainer, properties, config);

        ResultsMonitor resultsMonitor = new DefaultResultsMonitor(dataContainer, properties);
        SiloModel siloModel = new SiloModel(properties, dataContainer, modelContainer, resultsMonitor);
        siloModel.runModel();

        checkDwellings();
        checkHouseholds();
        checkJobs();
        checkPersons();
        checkPlans();
        checkEvents();

		if (CLEANUP_AFTER_TEST) {
			IOUtils.deleteDirectoryRecursively(Paths.get(dir.getAbsolutePath()));
		}
	}

    private void checkEvents() {
        log.info("Checking MATSim events file ...");
        final String eventsFilenameReference = "./test/muc/refOutput/matsim/test_2013/test_2013.output_events.xml.gz";
        final String eventsFilenameNew = "./test/muc/scenOutput/test/matsim/2013/2013.output_events.xml.gz";
        assertEquals("Different event files.", EventsFileComparator.Result.FILES_ARE_EQUAL, EventsFileComparator.compare(eventsFilenameReference, eventsFilenameNew));
    }

    private void checkPlans() {
        log.info("Checking MATSim plans file ...");

        final String referenceFilename = "./test/muc/refOutput/matsim/test_2013/test_2013.output_plans.xml.gz";
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
}