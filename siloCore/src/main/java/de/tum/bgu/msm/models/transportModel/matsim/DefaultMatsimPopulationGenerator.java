package de.tum.bgu.msm.models.transportModel.matsim;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.utils.SiloUtil;
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
import org.matsim.core.population.PopulationUtils;

import java.util.Collection;

public class DefaultMatsimPopulationGenerator implements MatsimPopulationGenerator {
    private static final Logger LOG = Logger.getLogger(MatsimTransportModel.class);


    @Override
    Population generatePopulation(Config config, DataContainer dataContainer, double populationScalingFactor) {
        LOG.info("Starting creating a MATSim population.");
        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        Collection<Person> siloPersons = householdDataManager.getPersons();

        Population matsimPopulation = PopulationUtils.createPopulation(config);
        PopulationFactory matsimPopulationFactory = matsimPopulation.getFactory();

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
                dwellingCoordinate = dataContainer.getGeoData().getZones().get(dwelling.getZoneId()).getRandomCoordinate(SiloUtil.getRandomObject());
            }
            Coord dwellingCoord = new Coord(dwellingCoordinate.x, dwellingCoordinate.y);

            Job job = jobDataManager.getJobFromId(siloWorkplaceId);
            Coordinate jobCoordinate;
            if (job != null && job.getCoordinate() != null) {
                jobCoordinate = job.getCoordinate();
            } else {
                jobCoordinate = dataContainer.getGeoData().getZones().get(job.getZoneId()).getRandomCoordinate(SiloUtil.getRandomObject());
            }
            Coord jobCoord = new Coord(jobCoordinate.x, jobCoordinate.y);


            // Note: Do not confuse the SILO Person class with the MATSim Person class here
            org.matsim.api.core.v01.population.Person matsimPerson =
                    matsimPopulationFactory.createPerson(Id.create(siloPerson.getId(), org.matsim.api.core.v01.population.Person.class));
            matsimPopulation.addPerson(matsimPerson);

            Plan matsimPlan = matsimPopulationFactory.createPlan();
            matsimPerson.addPlan(matsimPlan);

            // TODO Add some switch here like "autoGenerateSimplePlans" or similar...
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
}