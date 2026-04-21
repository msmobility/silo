package run;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.population.routes.PopulationComparison;
import org.matsim.testcases.MatsimTestUtils;
import org.matsim.utils.eventsfilecomparison.ComparisonResult;

import static org.matsim.utils.eventsfilecomparison.ComparisonResult.FILES_ARE_EQUAL;

//@ExtendWith(MatsimTestUtils.class)
public class RunFabilandTest{
	private static final Logger log = LogManager.getLogger( RunFabilandTest.class );

	@RegisterExtension
	public MatsimTestUtils utils = new MatsimTestUtils();

	@Test
	public void testMain(){

		final String inputDirectory = utils.getInputDirectory();

		try {
			String [] args = {"./scenario/test.properties",
					"./scenario/config_cap30_1-l_nes_smc.xml",
//					"--config:controler.outputDirectory", utils.getOutputDirectory(), // has no effect; evidently overwritten by code
					"--config:controler.lastIteration", "1"
			} ;

			RunFabiland.main( args ) ;

			log.info("############################################");
			log.info("############################################");

			{
				final String expected = inputDirectory + "0.output_plans.xml.gz";
				final String actual = "scenario/scenOutput/base/matsim/0/0.output_plans.xml.gz";
				PopulationComparison.Result result = PopulationUtils.comparePopulations( expected, actual );
				Assertions.assertEquals( PopulationComparison.Result.equal, result );
			}
			log.info("############################################");
			log.info("############################################");
			{
				String expected = inputDirectory + "/0.output_events.xml.gz" ;
				String actual = "scenario/scenOutput/base/matsim/0/0.output_events.xml.gz" ;
				ComparisonResult result = EventsUtils.compareEventsFiles( expected, actual );
				Assertions.assertEquals( FILES_ARE_EQUAL, result );
			}

			log.info("############################################");
			log.info("############################################");

			// I do not know why I decided to regression-test the following.
			// --> I think that this has the moved home locations in it.
			{
				final String expected = inputDirectory + "1.0.plans.xml.gz";
				final String actual = "scenario/scenOutput/base/matsim/1/ITERS/it.0/1.0.plans.xml.gz";
				PopulationComparison.Result result = PopulationUtils.comparePopulations( expected, actual );
				Assertions.assertEquals( PopulationComparison.Result.equal, result );
			}
			log.info("############################################");
			log.info("############################################");
			{
				String expected = inputDirectory + "/1.0.events.xml.gz" ;
				String actual = "scenario/scenOutput/base/matsim/1/ITERS/it.0/1.0.events.xml.gz" ;
				ComparisonResult result = EventsUtils.compareEventsFiles( expected, actual );
				Assertions.assertEquals( FILES_ARE_EQUAL, result );
			}

			log.info("############################################");
			log.info("############################################");

			{
				final String expected = inputDirectory + "1.output_plans.xml.gz";
				final String actual = "scenario/scenOutput/base/matsim/1/1.output_plans.xml.gz";
				PopulationComparison.Result result = PopulationUtils.comparePopulations( expected, actual );
				Assertions.assertEquals( PopulationComparison.Result.equal, result );
			}
			log.info("############################################");
			log.info("############################################");
			{
				String expected = inputDirectory + "/1.output_events.xml.gz" ;
				String actual = "scenario/scenOutput/base/matsim/1/1.output_events.xml.gz" ;
				ComparisonResult result = EventsUtils.compareEventsFiles( expected, actual );
				Assertions.assertEquals( FILES_ARE_EQUAL, result );
			}

			log.info("############################################");
			log.info("############################################");

//			{
//				Population expected = PopulationUtils.createPopulation( ConfigUtils.createConfig() ) ;
//				PopulationUtils.readPopulation( expected,  utils.getInputDirectory() + "10.output_plans.xml.gz" );
//
//				Population actual = PopulationUtils.createPopulation( ConfigUtils.createConfig() ) ;
//				PopulationUtils.readPopulation( actual, "scenario/scenOutput/base/matsim/10/10.output_plans.xml.gz" );
//
//				boolean result = PopulationUtils.comparePopulations( expected, actual );
//				Assert.assertTrue( result );
//			}
//			{
//				String expected = utils.getInputDirectory() + "/10.output_events.xml.gz" ;
//				String actual = "scenario/scenOutput/base/matsim/10/10.output_events.xml.gz" ;
//				EventsFileComparator.Result result = EventsUtils.compareEventsFiles( expected, actual );
//				Assert.assertEquals( EventsFileComparator.Result.FILES_ARE_EQUAL, result );
//			}

		} catch ( Exception ee ) {
			log.fatal("there was an exception: \n" + ee ) ;

			ee.printStackTrace();

			// if one catches an exception, then one needs to explicitly fail the test:
			Assertions.fail();
		}


	}
}
