package edu.umd.ncsg.transportModel;

import java.util.Collection;
import java.util.Map;
import java.util.Random;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.api.internal.MatsimWriter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.scenario.ScenarioUtils;
import org.opengis.feature.simple.SimpleFeature;

import edu.umd.ncsg.data.Household;
import edu.umd.ncsg.data.HouseholdDataManager;
import edu.umd.ncsg.data.Job;
import edu.umd.ncsg.data.Person;

/**
 * @author dziemke
 */
public class MatsimPopulationCreator {
	
	public static Population createMatsimPopulation(HouseholdDataManager householdDataManager, int year,
			Map<Integer,SimpleFeature>zoneFeatureMap, String crs, boolean writePopulation, double scalingFactor) {
    	Collection<Person> siloPersons = householdDataManager.getPersons();
    	
//    	Collection<SimpleFeature> features = ShapeFileReader.getAllFeatures(zoneShapeFile);
//
//    	Map<Integer,SimpleFeature> featureMap = new HashMap<Integer, SimpleFeature>();
//		for (SimpleFeature feature: features) {
//			int fipsPuma5 = Integer.parseInt(feature.getAttribute("FIPS_PUMA5").toString());
//			featureMap.put(fipsPuma5,feature);
//		}
    	
    	// MATSim "infrastructure"
    	Config matsimConfig = ConfigUtils.createConfig();
    	Scenario matsimScenario = ScenarioUtils.createScenario(matsimConfig);

    	Network matsimNetwork = matsimScenario.getNetwork();
    	Population matsimPopulation = matsimScenario.getPopulation();   
    	PopulationFactory matsimPopulationFactory = matsimPopulation.getFactory();
    	
//    	CoordinateTransformation ct = TransformationFactory.getCoordinateTransformation(
//    			TransformationFactory.WGS84, crs);
    	
//    	Random random = new Random();
    	Random random = MatsimRandom.getLocalInstance() ;
    	// make sure that stream of random variables is reproducible. kai, apr'16
    	
    	for (Person siloPerson : siloPersons) {
    		if (random.nextDouble() > scalingFactor) {
    			// e.g. if scalingFactor = 0.01, there will be a 1% chance that the loop is not
    			// continued in the next step, i.e. that the person is added to the population
    			continue;
    		}

    		if (siloPerson.getOccupation() != 1) { // person does not work
    			continue;
    		}

    		int siloWorkplaceId = siloPerson.getWorkplace();
    		if (siloWorkplaceId == -2) { // person has workplace outside study area
    			continue;
    		}

    		int householdId = siloPerson.getHhId();
    		Household household = Household.getHouseholdFromId(householdId);
    		int numberOfWorkers = household.getNumberOfWorkers();
    		int numberOfAutos = household.getAutos();
    		if (numberOfWorkers == 0) {
    			throw new RuntimeException("If there are no workers in the household, the loop must already"
    					+ " have been continued by finfing that the given person is not employed!");
    		}
    		if ((double) numberOfAutos/numberOfWorkers < 1.) {
    			if (random.nextDouble() > (double) numberOfAutos/numberOfWorkers) {
    				continue;
    			}
    		}


    		int siloPersonId = siloPerson.getId();

    		int siloHomeTazId = siloPerson.getHomeTaz();
//    		int homePuma = geoData.getPUMAofZone(siloHomeTazId);
//    		System.out.println("siloPersonId = " + siloPersonId + "; siloHomeTazId = " + siloHomeTazId);

    		Job job = Job.getJobFromId(siloWorkplaceId);
    		int workZoneId = job.getZone();
//    		int workPuma = geoData.getPUMAofZone(workZoneId);   
//    		System.out.println("siloPersonId = " + siloPersonId + "; siloWorkplaceId = " + siloWorkplaceId);
//    		System.out.println("siloPersonId = " + siloPersonId + "; workZoneId = " + workZoneId);

    		// do not confuse the SILO Person class with the MATSim Person class here
    		org.matsim.api.core.v01.population.Person matsimPerson = 
    				matsimPopulationFactory.createPerson(Id.create(siloPersonId, org.matsim.api.core.v01.population.Person.class));
    		matsimPopulation.addPerson(matsimPerson);

    		Plan matsimPlan = matsimPopulationFactory.createPlan();
    		matsimPerson.addPlan(matsimPlan);

//    		SimpleFeature homeFeature = featureMap.get(homePuma);
    		SimpleFeature homeFeature = zoneFeatureMap.get(siloHomeTazId);
    		Coord homeCoordinates = SiloMatsimUtils.getRandomCoordinateInGeometry(homeFeature);
//    		Activity activity1 = matsimPopulationFactory.createActivityFromCoord("home", ct.transform(homeCoordinates));
    		Activity activity1 = matsimPopulationFactory.createActivityFromCoord("home", homeCoordinates);
    		activity1.setEndTime(6 * 3600 + 3 * random.nextDouble() * 3600); // TODO change
    		matsimPlan.addActivity(activity1);
    		matsimPlan.addLeg(matsimPopulationFactory.createLeg(TransportMode.car)); // TODO maybe change

//    		SimpleFeature workFeature = featureMap.get(workPuma);
    		SimpleFeature workFeature = zoneFeatureMap.get(workZoneId);
    		Coord workCoordinates = SiloMatsimUtils.getRandomCoordinateInGeometry(workFeature);
//    		Activity activity2 = matsimPopulationFactory.createActivityFromCoord("work", ct.transform(workCoordinates));
    		Activity activity2 = matsimPopulationFactory.createActivityFromCoord("work", workCoordinates);
    		activity2.setEndTime(15 * 3600 + 3 * random.nextDouble() * 3600); // TODO change
    		matsimPlan.addActivity(activity2);
    		matsimPlan.addLeg(matsimPopulationFactory.createLeg(TransportMode.car)); // TODO maybe change

//    		Activity activity3 = matsimPopulationFactory.createActivityFromCoord("home", ct.transform(homeCoordinates));
    		Activity activity3 = matsimPopulationFactory.createActivityFromCoord("home", homeCoordinates);
    		matsimPlan.addActivity(activity3);

    	}
    	
    	if (writePopulation == true) {
    		MatsimWriter popWriter = new PopulationWriter(matsimPopulation, matsimNetwork);
    		popWriter.write("./additional_input/population_" + year + ".xml");
    	}
    	
    	return matsimPopulation;
    }
}