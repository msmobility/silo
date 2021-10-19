package de.tum.bgu.msm.matsim;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.core.config.Config;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.Collection;

public class SimpleMatsimScenarioAssembler implements MatsimScenarioAssembler {

    private final static Logger logger = Logger.getLogger(SimpleMatsimScenarioAssembler.class);

    private final DataContainer dataContainer;
    private final Properties properties;

    public SimpleMatsimScenarioAssembler(DataContainer dataContainer, Properties properties) {
        this.dataContainer = dataContainer;
        this.properties = properties;
    }

    @Override
    public Scenario assembleScenario(Config matsimConfig, int year, TravelTimes travelTimes) {
        logger.info("Starting creating (simple home-work-home) MATSim scenario.");
        double populationScalingFactor = properties.transportModel.matsimScaleFactor;
        SiloMatsimUtils.checkSiloPropertiesAndMatsimConfigConsistency(matsimConfig, properties);

        Scenario scenario = ScenarioUtils.loadScenario(matsimConfig);
        Population matsimPopulation = scenario.getPopulation();
        PopulationFactory matsimPopulationFactory = matsimPopulation.getFactory();

        Collection<Person> siloPersons = dataContainer.getHouseholdDataManager().getPersons();
        JobDataManager jobDataManager = dataContainer.getJobDataManager();

        for (Person siloPerson : siloPersons) {
            if (SiloUtil.getRandomNumberAsDouble() > populationScalingFactor) {
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
            if (dwelling != null && dwelling.getCoordinate() != null) {
                dwellingCoordinate = dwelling.getCoordinate();
            } else {
                // TODO This step should not be done (again) if a random coordinate for the same dwelling has been chosen before, dz 10/20
                dwellingCoordinate = dataContainer.getGeoData().getZones().get(dwelling.getZoneId()).getRandomCoordinate(SiloUtil.getRandomObject());
            }
            Coord dwellingCoord = new Coord(dwellingCoordinate.x, dwellingCoordinate.y);

            Job job = jobDataManager.getJobFromId(siloWorkplaceId);
            Coordinate jobCoordinate;
            if (job != null && job.getCoordinate() != null) {
                jobCoordinate = job.getCoordinate();
            } else {
                // TODO This step should not be done (again) if a random coordinate for the same job has been chosen before, dz 10/20
                jobCoordinate = dataContainer.getGeoData().getZones().get(job.getZoneId()).getRandomCoordinate(SiloUtil.getRandomObject());
            }
            Coord jobCoord = new Coord(jobCoordinate.x, jobCoordinate.y);

            org.matsim.api.core.v01.population.Person matsimPerson = matsimPopulationFactory.createPerson(Id.createPersonId(siloPerson.getId()));
            matsimPopulation.addPerson(matsimPerson);

            Plan matsimPlan = matsimPopulationFactory.createPlan();
            matsimPerson.addPlan(matsimPlan);

            Activity homeActivityMorning = matsimPopulationFactory.createActivityFromCoord("home", dwellingCoord);
            homeActivityMorning.setEndTime(6 * 3600 + 3 * SiloUtil.getRandomNumberAsDouble() * 3600); // TODO Potentially change later
            matsimPlan.addActivity(homeActivityMorning);
            matsimPlan.addLeg(matsimPopulationFactory.createLeg(TransportMode.car)); // TODO Potentially change later

            Activity workActivity = matsimPopulationFactory.createActivityFromCoord("work", jobCoord);
            workActivity.setEndTime(15 * 3600 + 3 * SiloUtil.getRandomNumberAsDouble() * 3600); // TODO Potentially change later
            matsimPlan.addActivity(workActivity);
            matsimPlan.addLeg(matsimPopulationFactory.createLeg(TransportMode.car)); // TODO Potentially change later

            Activity homeActvitiyEvening = matsimPopulationFactory.createActivityFromCoord("home", dwellingCoord);
            matsimPlan.addActivity(homeActvitiyEvening);
        }
        logger.info("Finished creating MATSim scenario.");
        return scenario;
    }
}