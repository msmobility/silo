package de.tum.bgu.msm.matsim;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Day;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.models.modeChoice.CommuteModeChoice;
import de.tum.bgu.msm.models.modeChoice.CommuteModeChoiceMapping;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.core.config.Config;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.Map;

public class SimpleCommuteMatsimScenarioAssembler implements MatsimScenarioAssembler {

    private final static Logger logger = Logger.getLogger(SimpleMatsimScenarioAssembler.class);

    private final DataContainer dataContainer;
    private final Properties properties;

    public SimpleCommuteMatsimScenarioAssembler(DataContainer dataContainer, Properties properties) {
        this.dataContainer = dataContainer;
        this.properties = properties;
    }

    @Override
    public Scenario assembleScenario(Config matsimConfig, int year, TravelTimes travelTimes) {
        logger.info("Starting creating (mode-respecting, home-work-home) MATSim scenario.");
        double populationScalingFactor = properties.transportModel.matsimScaleFactor;
        SiloMatsimUtils.checkSiloPropertiesAndMatsimConfigConsistency(matsimConfig, properties);

        Scenario scenario = ScenarioUtils.loadScenario(matsimConfig);
        Population matsimPopulation = scenario.getPopulation();

        JobDataManager jobDataManager = dataContainer.getJobDataManager();
        RealEstateDataManager realEstateDataManager = dataContainer.getRealEstateDataManager();

        for (Household household: dataContainer.getHouseholdDataManager().getHouseholds()) {
            if (SiloUtil.getRandomNumberAsDouble() > populationScalingFactor) {
                continue;
            }

            for (Person person : household.getPersons().values()) {
                if (person.getOccupation() != Occupation.EMPLOYED || person.getJobId() == -2) { // i.e. person does not work
                    continue;
                }

                PopulationFactory populationFactory = matsimPopulation.getFactory();

                org.matsim.api.core.v01.population.Person matsimAlterEgo = SiloMatsimUtils.createMatsimAlterEgo(populationFactory, person, household.getAutos());
                matsimPopulation.addPerson(matsimAlterEgo);

                Dwelling dwelling = realEstateDataManager.getDwelling(household.getDwellingId());

                Coord dwellingCoord = getOrRandomlyChooseDwellingCoord(dwelling);

                Job job = jobDataManager.getJobFromId(person.getJobId());
                Coord jobCoord = getOrRandomlyChooseJobCoordinate(job);

                createHWHPlanAndAddToAlterEgo(populationFactory, matsimAlterEgo, dwellingCoord, job, jobCoord, TransportMode.car);

            }
        }
        logger.info("Finished creating MATSim scenario.");
        return scenario;
    }

    @Override
    public Map<Day, Scenario> assembleMultiScenarios(Config initialMatsimConfig, int year, TravelTimes travelTimes) {
        return null;
    }

    private Coord getOrRandomlyChooseDwellingCoord(Dwelling dwelling) {
        Coordinate dwellingCoordinate;
        if (dwelling != null && dwelling.getCoordinate() != null) {
            dwellingCoordinate = dwelling.getCoordinate();
        } else {
            // TODO This step should not be done (again) if a random coordinate for the same dwelling has been chosen before, dz 10/20
            dwellingCoordinate = dataContainer.getGeoData().getZones().get(dwelling.getZoneId()).getRandomCoordinate(SiloUtil.getRandomObject());
        }
        return new Coord(dwellingCoordinate.x, dwellingCoordinate.y);
    }

    private Coord getOrRandomlyChooseJobCoordinate(Job job) {
        Coordinate jobCoordinate;
        if (job != null && job.getCoordinate() != null) {
            jobCoordinate = job.getCoordinate();
        } else {
            // TODO This step should not be done (again) if a random coordinate for the same job has been chosen before, dz 10/20
            jobCoordinate = dataContainer.getGeoData().getZones().get(job.getZoneId()).getRandomCoordinate(SiloUtil.getRandomObject());
        }
        return new Coord(jobCoordinate.x, jobCoordinate.y);
    }

    private void createHWHPlanAndAddToAlterEgo(PopulationFactory populationFactory, org.matsim.api.core.v01.population.Person matsimAlterEgo,
                               Coord dwellingCoord, Job job, Coord jobCoord, String transportMode) {
        Plan matsimPlan = populationFactory.createPlan();
        matsimAlterEgo.addPlan(matsimPlan);

        Activity homeActivityMorning = populationFactory.createActivityFromCoord("home", dwellingCoord);
        Integer departureTime = defineDepartureFromHome(job);
        homeActivityMorning.setEndTime(departureTime);
        matsimPlan.addActivity(homeActivityMorning);
        matsimPlan.addLeg(populationFactory.createLeg(transportMode));

        Activity workActivity = populationFactory.createActivityFromCoord("work", jobCoord);
        workActivity.setEndTime(defineWorkEndTime(job, departureTime));
        matsimPlan.addActivity(workActivity);
        matsimPlan.addLeg(populationFactory.createLeg(transportMode));

        Activity homeActivityEvening = populationFactory.createActivityFromCoord("home", dwellingCoord);

        matsimPlan.addActivity(homeActivityEvening);
    }

    /**
     * Defines departure time from home. Note that it actually tries to use job start times if defined. Otherwise
     * randomly draws from a normal distribution around the peak hour with 1 hour standard deviation.
     */
    private Integer defineDepartureFromHome(Job job) {
        return job.getStartTimeInSeconds().orElse(Math.max(0, (int) (properties.transportModel.peakHour_s + SiloUtil.getRandomObject().nextGaussian() * 3600)));
    }

    /**
     * Defines departure time from work. Note that it actually tries to use job duration times if defined. Otherwise
     * randomly draws from a normal distribution with mean of 8 hours with 1 hour standard deviation. The duration
     * is then added to the job starting time.
     */
    private int defineWorkEndTime(Job job, int departureTime) {
        return departureTime + job.getWorkingTimeInSeconds().orElse(Math.max(0, (int) (8*3600 + SiloUtil.getRandomObject().nextGaussian() * 3600)));
    }
}