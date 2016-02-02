package edu.umd.ncsg.transportModel;

import java.util.Collection;
import java.util.HashMap;
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
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

import edu.umd.ncsg.data.HouseholdDataManager;
import edu.umd.ncsg.data.Job;
import edu.umd.ncsg.data.Person;
import edu.umd.ncsg.data.geoData;

/**
 * @author dziemke
 */
public class MatsimPopulationCreator {
	
	public static Population createMatsimPopulation(HouseholdDataManager householdDataManager, int year, String shapeFile,
			String outputCRS, boolean writePopulation) {
    	Collection<Person> siloPersons = householdDataManager.getPersons();
    	
    	Collection<SimpleFeature> features = ShapeFileReader.getAllFeatures(shapeFile);

    	Map<Integer,SimpleFeature> featureMap = new HashMap<Integer, SimpleFeature>();
		for (SimpleFeature feature: features) {
			int fipsPuma5 = Integer.parseInt(feature.getAttribute("FIPS_PUMA5").toString());
			featureMap.put(fipsPuma5,feature);
		}
    	
    	// MATSim "infrastructure"
    	Config matsimConfig = ConfigUtils.createConfig();
    	Scenario matsimScenario = ScenarioUtils.createScenario(matsimConfig);

    	Network matsimNetwork = matsimScenario.getNetwork();
    	Population matsimPopulation = matsimScenario.getPopulation();   
    	PopulationFactory matsimPopulationFactory = matsimPopulation.getFactory();
    	
    	CoordinateTransformation ct = TransformationFactory.getCoordinateTransformation(
    			TransformationFactory.WGS84, outputCRS);
    	
    	Random random = new Random();
    	
    	int counter = 0;
    	
    	for (Person siloPerson : siloPersons) {
    		
    		// only used every 200th person
    		// --> 1% sample plus assuming that every other person works on a given day accounting for part-time
    		// workers, holiday, sickness, people working at non-peak times (that are not included yet...) etc...
    		// probably not the best/correct way to create a 1% sample // TODO improve
    		// but using this here for the time being to speed things up...
    		counter++;
    		if (counter % 200 == 0) {
    			// a bit non-linearly, this has to be already here as people working outside the study area
    			// should be ignored ("workplace == -2)
    			int siloWorkplaceId = siloPerson.getWorkplace();
				
    			if (siloPerson.getOccupation() == 1 && siloWorkplaceId != -2) {
    				// "occ == 1" means that person has a job
    				// unemployed people are ignored here as we only look at morning and afternoon
    				// work commute for the time being
    				// TODO potentially improve this later

    				int siloPersonId = siloPerson.getId();

    				int siloHomeTazId = siloPerson.getHomeTaz();
    				int homePuma = geoData.getPUMAofZone(siloHomeTazId);
//    				System.out.println("siloPersonId = " + siloPersonId + "; siloHomeTazId = " + siloHomeTazId+ "; homePuma = " + homePuma);

    				Job job = Job.getJobFromId(siloWorkplaceId);
    				int workZoneId = job.getZone();
    				int workPuma = geoData.getPUMAofZone(workZoneId);   
//    				System.out.println("siloPersonId = " + siloPersonId + "; siloWorkplaceId = " + siloWorkplaceId + "; workPuma = " + workPuma);

    				// do not confuse the SILO Person class with the MATSim Person class here
    				org.matsim.api.core.v01.population.Person matsimPerson = 
    						matsimPopulationFactory.createPerson(Id.create(siloPersonId, org.matsim.api.core.v01.population.Person.class));
    				matsimPopulation.addPerson(matsimPerson);

    				Plan matsimPlan = matsimPopulationFactory.createPlan();
    				matsimPerson.addPlan(matsimPlan);

    				SimpleFeature homeFeature = featureMap.get(homePuma);
//    				System.out.println("homePuma = " + homePuma);
    				Coord homeCoordinates = SiloMatsimUtils.getRandomCoordinateInGeometry(homeFeature);
    				Activity activity1 = matsimPopulationFactory.createActivityFromCoord("home", ct.transform(homeCoordinates));
    				activity1.setEndTime(7 * 3600 + 2 * random.nextDouble() * 3600); // TODO change
    				matsimPlan.addActivity(activity1);
    				matsimPlan.addLeg(matsimPopulationFactory.createLeg(TransportMode.car)); // TODO maybe change

    				SimpleFeature workFeature = featureMap.get(workPuma);
    				Coord workCoordinates = SiloMatsimUtils.getRandomCoordinateInGeometry(workFeature);
    				Activity activity2 = matsimPopulationFactory.createActivityFromCoord("work", ct.transform(workCoordinates));
    				activity2.setEndTime(16 * 3600 + 2 * random.nextDouble() * 3600); // TODO change
    				matsimPlan.addActivity(activity2);
    				matsimPlan.addLeg(matsimPopulationFactory.createLeg(TransportMode.car)); // TODO maybe change

    				Activity activity3 = matsimPopulationFactory.createActivityFromCoord("home",ct.transform(homeCoordinates));
    				matsimPlan.addActivity(activity3);
    			}
    		}
    	}
    	
    	if (writePopulation == true) {
    		MatsimWriter popWriter = new PopulationWriter(matsimPopulation, matsimNetwork);
    		popWriter.write("./siloMatsim/population_" + year + ".xml");
    	}
    	
    	return matsimPopulation;
    }
}