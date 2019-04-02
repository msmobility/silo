package de.tum.bgu.msm.transportModel.matsim;

import de.tum.bgu.msm.DataBuilder;
import de.tum.bgu.msm.ModelBuilder;
import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.data.DataContainerMuc;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.core.utils.misc.CRCChecksum;
import org.matsim.testcases.MatsimTestUtils;
import org.matsim.utils.eventsfilecomparison.EventsFileComparator;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author dziemke, nagel
 */
public class SiloMatsimTestMuc {

    /**
     * Set to true normally; set to false to be able to inspect files
     */
    private static final boolean CLEANUP_AFTER_TEST = true;


    @Rule
    public MatsimTestUtils utils = new MatsimTestUtils();

    private static final Logger log = Logger.getLogger(SiloMatsimTestMuc.class);

    @Test
	public final void testMain() {

		SiloTestUtils.cleanUpMicrodataFiles();
		SiloTestUtils.cleanUpOtherFiles();

        String path = "./test/muc/siloMucTest.properties";
		Config config = ConfigUtils.loadConfig("./test/scenarios/munich_new/matsim_input/config.xml") ;

		try {
			utils.initWithoutJUnitForFixture(this.getClass(), this.getClass().getMethod("testMain", null));
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

		config.controler().setOutputDirectory(utils.getOutputDirectory());
		config.global().setNumberOfThreads(1);
		config.parallelEventHandling().setNumberOfThreads(1);
		config.qsim().setNumberOfThreads(1);

        Properties properties = SiloUtil.siloInitialization(path);

        DataContainerMuc dataContainer = DataBuilder.getModelDataForMuc(properties);
        DataBuilder.read(properties, dataContainer);

        ModelContainer modelContainer = ModelBuilder.getModelContainerForMuc(dataContainer, properties, config);

        SiloModel siloModel = new SiloModel(properties, dataContainer, modelContainer);
        siloModel.runModel();

        checkDwellings();
        checkHouseholds();
        checkJobs();
        checkPersons();
        checkPlans();
        checkEvents();

		if (CLEANUP_AFTER_TEST) {
			File dir = new File(utils.getOutputDirectory());
			IOUtils.deleteDirectoryRecursively(Paths.get(dir.getAbsolutePath()));
			SiloTestUtils.cleanUpMicrodataFiles();
			SiloTestUtils.cleanUpOtherFiles();
		}
	}

    private void checkEvents() {
        log.info("Checking MATSim events file ...");
        final String eventsFilenameReference = utils.getInputDirectory() + "./test_matsim_2011.output_events.xml.gz";
        final String eventsFilenameNew = utils.getOutputDirectory() + "./test_matsim_2011/test_matsim_2011.output_events.xml.gz";
        assertEquals("Different event files.", EventsFileComparator.Result.FILES_ARE_EQUAL, EventsFileComparator.compare(eventsFilenameReference, eventsFilenameNew));
    }

    private void checkPlans() {
        log.info("Checking MATSim plans file ...");

        final String referenceFilename = utils.getInputDirectory() + "./test_matsim_2011.output_plans.xml.gz";
        final String outputFilename = utils.getOutputDirectory() + "./test_matsim_2011/test_matsim_2011.output_plans.xml.gz";

        Scenario scRef = ScenarioUtils.createScenario(ConfigUtils.createConfig()) ;
        Scenario scOut = ScenarioUtils.createScenario(ConfigUtils.createConfig()) ;

        new PopulationReader(scRef).readFile(referenceFilename);
        new PopulationReader(scOut).readFile(outputFilename);

        assertTrue("MATSim populations are different", PopulationUtils.equalPopulation( scRef.getPopulation(), scOut.getPopulation() ) ) ;
    }

    private void checkPersons() {
        log.info("checking SILO population file ...");
        long checksum_ref = CRCChecksum.getCRCFromFile(utils.getInputDirectory() + "./pp_2011.csv");
        final String filename = "./test/scenarios/munich_new/microData_reduced/pp_2011.csv";
        long checksum_run = CRCChecksum.getCRCFromFile(filename);
        assertEquals("Population files are different", checksum_ref, checksum_run);
        if (CLEANUP_AFTER_TEST) {
            new File(filename).delete();
        }
    }

    private void checkJobs() {
        log.info("Checking jobs file ...");
        long checksum_ref = CRCChecksum.getCRCFromFile(utils.getInputDirectory() + "./jj_2011.csv");
        final String filename = "./test/scenarios/munich_new/microData_reduced/jj_2011.csv";
        long checksum_run = CRCChecksum.getCRCFromFile(filename);
        assertEquals("Job files are different", checksum_ref, checksum_run);
        if (CLEANUP_AFTER_TEST) {
            new File(filename).delete();
        }
    }

    private void checkHouseholds() {
        log.info("Checking households file ...");
        long checksum_ref = CRCChecksum.getCRCFromFile(utils.getInputDirectory() + "./hh_2011.csv");
        final String filename = "./test/scenarios/munich_new/microData_reduced/hh_2011.csv";
        long checksum_run = CRCChecksum.getCRCFromFile(filename);
        assertEquals("Household files are different", checksum_ref, checksum_run);
        if (CLEANUP_AFTER_TEST) {
            new File(filename).delete();
        }
    }

    private void checkDwellings() {
        log.info("Checking dwellings file ...");
        long checksum_ref = CRCChecksum.getCRCFromFile(utils.getInputDirectory() + "./dd_2011.csv");
        final String filename = "./test/scenarios/munich_new/microData_reduced/dd_2011.csv";
        long checksum_run = CRCChecksum.getCRCFromFile(filename);
        assertEquals("Dwelling files are different", checksum_ref, checksum_run);
        if (CLEANUP_AFTER_TEST) {
            new File(filename).delete();
        }
    }
}