package run;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.testcases.MatsimTestUtils;
import org.matsim.utils.eventsfilecomparison.ComparisonResult;

import static org.matsim.utils.eventsfilecomparison.ComparisonResult.FILES_ARE_EQUAL;

@ExtendWith(MatsimTestUtils.class)
public class RunFabilandTest{
	private static final Logger log = LogManager.getLogger( RunFabilandTest.class );

	public MatsimTestUtils utils = new MatsimTestUtils();

	@Test
	public void testMain(){
		try {
			String [] args = {"./scenario/test.properties",
					"./scenario/config_cap30_1-l_nes_smc.xml",
//					"--config:controler.outputDirectory", utils.getOutputDirectory(), // has no effect; evidently overwritten by code
					"--config:controler.lastIteration", "2"
					// (I made this "2" because with "1" it failed quite often with a failing binary search.  Seems to be fixed
					// with the changed random number generator (see comments in SimpleCommuteModeChoiceMatsimScenarioAssembler),
					// but I do not want to commit again new regression test files.  So leaving it at "2".  kai, jun'23
			} ;

			RunFabiland.main( args ) ;

			log.info("############################################");
			log.info("############################################");

			{
				Population expected = PopulationUtils.createPopulation( ConfigUtils.createConfig() ) ;
				PopulationUtils.readPopulation( expected,  utils.getInputDirectory() + "0.output_plans.xml.gz" );

				Population actual = PopulationUtils.createPopulation( ConfigUtils.createConfig() ) ;
				PopulationUtils.readPopulation( actual, "scenario/scenOutput/base/matsim/0/0.output_plans.xml.gz" );

				boolean result = PopulationUtils.comparePopulations( expected, actual );
				Assert.assertTrue( result );
			}
			{
				String expected = utils.getInputDirectory() + "/0.output_events.xml.gz" ;
				String actual = "scenario/scenOutput/base/matsim/0/0.output_events.xml.gz" ;
				ComparisonResult result = EventsUtils.compareEventsFiles( expected, actual );
				Assert.assertEquals( FILES_ARE_EQUAL, result );
			}

			log.info("############################################");
			log.info("############################################");

			{
				Population expected = PopulationUtils.createPopulation( ConfigUtils.createConfig() ) ;
				PopulationUtils.readPopulation( expected,  utils.getInputDirectory() + "1.0.plans.xml.gz" );

				Population actual = PopulationUtils.createPopulation( ConfigUtils.createConfig() ) ;
				PopulationUtils.readPopulation( actual, "scenario/scenOutput/base/matsim/1/ITERS/it.0/1.0.plans.xml.gz" );

				boolean result = PopulationUtils.comparePopulations( expected, actual );
				Assert.assertTrue( result );
			}
			{
				String expected = utils.getInputDirectory() + "/1.0.events.xml.gz" ;
				String actual = "scenario/scenOutput/base/matsim/1/ITERS/it.0/1.0.events.xml.gz" ;
				ComparisonResult result = EventsUtils.compareEventsFiles( expected, actual );
				Assert.assertEquals( FILES_ARE_EQUAL, result );
			}

			log.info("############################################");
			log.info("############################################");

			{
				Population expected = PopulationUtils.createPopulation( ConfigUtils.createConfig() ) ;
				PopulationUtils.readPopulation( expected,  utils.getInputDirectory() + "1.output_plans.xml.gz" );

				Population actual = PopulationUtils.createPopulation( ConfigUtils.createConfig() ) ;
				PopulationUtils.readPopulation( actual, "scenario/scenOutput/base/matsim/1/1.output_plans.xml.gz" );

				boolean result = PopulationUtils.comparePopulations( expected, actual );
				Assert.assertTrue( result );
			}
			{
				String expected = utils.getInputDirectory() + "/1.output_events.xml.gz" ;
				String actual = "scenario/scenOutput/base/matsim/1/1.output_events.xml.gz" ;
				ComparisonResult result = EventsUtils.compareEventsFiles( expected, actual );
				Assert.assertEquals( FILES_ARE_EQUAL, result );
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

			// if one catches an exception, then one needs to explicitly fail the test:
			Assert.fail();
		}


	}
}
