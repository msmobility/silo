package de.tum.bgu.msm.models.transportModel.matsim;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.config.groups.QSimConfigGroup.TrafficDynamics;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.config.groups.VspExperimentalConfigGroup.VspDefaultsCheckingLevel;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.utils.collections.Tuple;
import org.matsim.facilities.ActivityFacilities;
import org.matsim.facilities.ActivityFacility;

import com.pb.common.matrix.Matrix;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.MicroLocation;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.utils.SiloUtil;

/**
 * @author dziemke
 */
public class SiloMatsimUtils {
	private final static Logger LOG = Logger.getLogger(SiloMatsimUtils.class);
		
	public static Config createMatsimConfig(Config initialConfig, String runId,	double populationScalingFactor) {
		LOG.info("Stating creating a MATSim config.");
		Config config = ConfigUtils.loadConfig(initialConfig.getContext());
		config.qsim().setFlowCapFactor(populationScalingFactor);
		
		// According to "NicolaiNagel2013HighResolutionAccessibility (citing Rieser on p.9):
		// Storage_Capacitiy_Factor = Sampling_Rate / ((Sampling_Rate) ^ (1/4))
		config.qsim().setStorageCapFactor(Math.round((populationScalingFactor / (Math.pow(populationScalingFactor, 0.25)) * 100)) / 100.);
		
		String outputDirectoryRoot = initialConfig.controler().getOutputDirectory();
		String outputDirectory = outputDirectoryRoot + "/" + runId + "/";
		config.controler().setRunId(runId);
		config.controler().setOutputDirectory(outputDirectory);
		config.controler().setFirstIteration(0);
		config.controler().setWritePlansInterval(config.controler().getLastIteration());
		config.controler().setWriteEventsInterval(config.controler().getLastIteration());
		config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
	
		config.qsim().setTrafficDynamics(TrafficDynamics.withHoles);
		config.vspExperimental().setWritingOutputEvents(true); // writes final events into toplevel directory
		
		{				
			StrategySettings strategySettings = new StrategySettings();
			strategySettings.setStrategyName("ChangeExpBeta");
			strategySettings.setWeight(0.8);
			config.strategy().addStrategySettings(strategySettings);
		}{
			StrategySettings strategySettings = new StrategySettings();
			strategySettings.setStrategyName("ReRoute");
			strategySettings.setWeight(0.2);
			config.strategy().addStrategySettings(strategySettings);
		}
		
		config.strategy().setFractionOfIterationsToDisableInnovation(0.8);
		config.strategy().setMaxAgentPlanMemorySize(4);
		
		ActivityParams homeActivity = new ActivityParams("home");
		homeActivity.setTypicalDuration(12*60*60);
		config.planCalcScore().addActivityParams(homeActivity);
		
		ActivityParams workActivity = new ActivityParams("work");
		workActivity.setTypicalDuration(8*60*60);
		config.planCalcScore().addActivityParams(workActivity);
		
		config.qsim().setNumberOfThreads(1);
		config.global().setNumberOfThreads(1);
		config.parallelEventHandling().setNumberOfThreads(1);
		config.qsim().setUsingThreadpool(false);
		
		config.vspExperimental().setVspDefaultsCheckingLevel(VspDefaultsCheckingLevel.warn);
	
		LOG.info("Finished creating a MATSim config.");
		return config;
	}

	public static Population createMatsimPopulation(Config config, DataContainer dataContainer, double scalingFactor) {
		LOG.info("Starting creating a MATSim population.");
		HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
		Collection<Person> siloPersons = householdDataManager.getPersons();
    	
    	Population matsimPopulation = PopulationUtils.createPopulation(config);
    	PopulationFactory matsimPopulationFactory = matsimPopulation.getFactory();

    	JobDataManager jobDataManager = dataContainer.getJobDataManager();
    	for (Person siloPerson : siloPersons) {
    		if (SiloUtil.getRandomNumberAsDouble() > scalingFactor) {
    			// e.g. if scalingFactor = 0.01, there will be a 1% chance that the loop is not
    			// continued in the next step, i.e. that the person is added to the population
    			continue;
    		}

    		if (siloPerson.getOccupation() != Occupation.EMPLOYED) { // i.e. person does not work
    			continue;
    		}

    		int siloWorkplaceId = siloPerson.getJobId();
    		if (siloWorkplaceId == -2) { // i.e. person has workplace outside study area
    			continue;
    		}

    		Household household = siloPerson.getHousehold();

    		int numberOfWorkers = HouseholdUtil.getNumberOfWorkers(household);
    		int numberOfAutos = household.getAutos();
    		if (numberOfWorkers == 0) {
    			throw new RuntimeException("If there are no workers in the household, the loop must already"
    					+ " have been continued by finding that the given person is not employed!");
    		}
    		if ((double) numberOfAutos/numberOfWorkers < 1.) {
    			if (SiloUtil.getRandomNumberAsDouble() > (double) numberOfAutos/numberOfWorkers) {
    				continue;
    			}
    		}

    		Dwelling dwelling = dataContainer.getRealEstateDataManager().getDwelling(household.getDwellingId());
    		Coordinate dwellingCoordinate;
    		if (dwelling instanceof MicroLocation && ((MicroLocation) dwelling).getCoordinate() != null) {
	    		dwellingCoordinate = ((MicroLocation) dwelling).getCoordinate();
    		} else {
    			dwellingCoordinate = dataContainer.getGeoData().getZones().get(dwelling.getZoneId()).getRandomCoordinate();
    		}
    		Coord dwellingCoord = new Coord(dwellingCoordinate.x, dwellingCoordinate.y);

    		Job job = jobDataManager.getJobFromId(siloWorkplaceId);
    		Coordinate jobCoordinate;
    		if (job instanceof MicroLocation && ((MicroLocation) job).getCoordinate() != null) {
    			jobCoordinate = ((MicroLocation) job).getCoordinate();
    		} else {
    			jobCoordinate = dataContainer.getGeoData().getZones().get(job.getZoneId()).getRandomCoordinate();
    		}
    		Coord jobCoord = new Coord(jobCoordinate.x, jobCoordinate.y);
    		

    		// Note: Do not confuse the SILO Person class with the MATSim Person class here
    		org.matsim.api.core.v01.population.Person matsimPerson = 
    				matsimPopulationFactory.createPerson(Id.create(siloPerson.getId(), org.matsim.api.core.v01.population.Person.class));
    		matsimPopulation.addPerson(matsimPerson);

    		Plan matsimPlan = matsimPopulationFactory.createPlan();
    		matsimPerson.addPlan(matsimPlan);

    		Activity activity1 = matsimPopulationFactory.createActivityFromCoord("home", dwellingCoord);
    		activity1.setEndTime(6 * 3600 + 3 * SiloUtil.getRandomNumberAsDouble() * 3600); // TODO Potentially change later
    		matsimPlan.addActivity(activity1);
    		matsimPlan.addLeg(matsimPopulationFactory.createLeg(TransportMode.car)); // TODO Potentially change later

    		Activity activity2 = matsimPopulationFactory.createActivityFromCoord("work", jobCoord);
    		activity2.setEndTime(15 * 3600 + 3 * SiloUtil.getRandomNumberAsDouble() * 3600); // TODO Potentially change later
    		matsimPlan.addActivity(activity2);
    		matsimPlan.addLeg(matsimPopulationFactory.createLeg(TransportMode.car)); // TODO Potentially change later

    		Activity activity3 = matsimPopulationFactory.createActivityFromCoord("home", dwellingCoord);

    		matsimPlan.addActivity(activity3);
    	}
    	LOG.info("Finished creating a MATSim population.");
    	return matsimPopulation;
    }
	
	public static final Matrix convertTravelTimesToImpedanceMatrix(
			Map<Tuple<Integer, Integer>, Float> travelTimesMap, int rowCount, int columnCount, int year) {
		LOG.info("Converting MATSim travel times to impedance matrix for " + year + ".");
		String name = "travelTimeMatrix";

		Matrix matrix = new Matrix(name, name, rowCount, columnCount);

		// Do not just increment by 1! Some values are missing. So, do not confuse the array index with the array entry!
		for (int i = 1; i <= rowCount; i++) {
			for (int j = 1; j <= columnCount; j++) {
//				Tuple<Integer, Integer> zone2Zone = new Tuple<Integer, Integer>(zones[i], zones[j]);
//				matrix.setValueAt(zones[i], zones[j], travelTimesMap.get(zone2Zone));
				
				Tuple<Integer, Integer> zone2Zone = new Tuple<Integer, Integer>(i, j);
				matrix.setValueAt(i, j, travelTimesMap.getOrDefault(zone2Zone, 0.f));
			}
		}	
		return matrix;
	}
	
	public static void determineExtentOfFacilities(ActivityFacilities activityFacilities) {
		double xmin = Double.MAX_VALUE;
		double xmax = Double.MIN_VALUE;
		double ymin = Double.MAX_VALUE;
		double ymax = Double.MIN_VALUE;
		
		for (ActivityFacility activityFacility : activityFacilities.getFacilities().values()) {	
			double x = activityFacility.getCoord().getX();
			double y = activityFacility.getCoord().getY();
			if (x < xmin) {
				xmin = x;
			}
			if (x > xmax) {
				xmax = x;
			}
			if (y < ymin) {
				ymin = y;
			}
			if (y > ymax) {
				ymax = y;
			}
		}
		LOG.info("Extent of facilities is: xmin = " + xmin + "; xmax = " + xmax + "; ymin = " + ymin + "; ymax = " + ymax);
	}
}