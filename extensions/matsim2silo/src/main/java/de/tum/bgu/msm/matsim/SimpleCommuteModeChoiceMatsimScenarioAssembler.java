package de.tum.bgu.msm.matsim;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Location;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.TravelTimesWrapper;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.models.modeChoice.CommuteModeChoice;
import de.tum.bgu.msm.models.modeChoice.CommuteModeChoiceMapping;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix2D;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.utils.TravelTimeUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.VspExperimentalConfigGroup;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vehicles.Vehicle;

public class SimpleCommuteModeChoiceMatsimScenarioAssembler implements MatsimScenarioAssembler {

    private final static Logger logger = Logger.getLogger(SimpleMatsimScenarioAssembler.class);

    private final DataContainer dataContainer;
    private CommuteModeChoice commuteModeChoice;

    private final Properties properties;

    public SimpleCommuteModeChoiceMatsimScenarioAssembler(DataContainer dataContainer, Properties properties, CommuteModeChoice commuteModeChoice) {
        this.dataContainer = dataContainer;
        this.commuteModeChoice = commuteModeChoice;
        this.properties = properties;
    }

    private Population generateDemand(TravelTimes travelTimes) {
        logger.info("Starting creating a MATSim population.");

        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();

        Population matsimPopulation = PopulationUtils.createPopulation(ConfigUtils.createConfig());
        PopulationFactory matsimPopulationFactory = matsimPopulation.getFactory();

        JobDataManager jobDataManager = dataContainer.getJobDataManager();

        RealEstateDataManager realEstateDataManager = dataContainer.getRealEstateDataManager();

        for(Household household: householdDataManager.getHouseholds()) {
            if (SiloUtil.getRandomNumberAsDouble() > properties.transportModel.matsimScaleFactor) {
                // e.g. if scalingFactor = 0.01, there will be a 1% chance that the loop is not
                // continued in the next step, i.e. that the person is added to the population
                continue;
            }
            Dwelling dwelling = realEstateDataManager.getDwelling(household.getDwellingId());
            CommuteModeChoiceMapping commuteModeChoiceMapping = commuteModeChoice.assignCommuteModeChoice(
                    dwelling, travelTimes, household);

            for(Person person: household.getPersons().values()) {
                if (person.getOccupation() != Occupation.EMPLOYED || person.getJobId() == -2) { // i.e. person does not work
                    continue;
                }
                if(commuteModeChoiceMapping.getMode(person).mode.equals(TransportMode.car)) {

                    Coordinate dwellingCoordinate;
                    if (dwelling != null && dwelling.getCoordinate() != null) {
                        dwellingCoordinate = dwelling.getCoordinate();
                    } else {
                        dwellingCoordinate = dataContainer.getGeoData().getZones().get(dwelling.getZoneId()).getRandomCoordinate(SiloUtil.getRandomObject());
                    }
                    Coord dwellingCoord = new Coord(dwellingCoordinate.x, dwellingCoordinate.y);

                    Job job = jobDataManager.getJobFromId(person.getJobId());
                    Coordinate jobCoordinate;
                    if (job != null && job.getCoordinate() != null) {
                        jobCoordinate = job.getCoordinate();
                    } else {
                        jobCoordinate = dataContainer.getGeoData().getZones().get(job.getZoneId()).getRandomCoordinate(SiloUtil.getRandomObject());
                    }
                    Coord jobCoord = new Coord(jobCoordinate.x, jobCoordinate.y);

                    org.matsim.api.core.v01.population.Person matsimPerson =
                            matsimPopulationFactory.createPerson(Id.create(person.getId(), org.matsim.api.core.v01.population.Person.class));
                    matsimPopulation.addPerson(matsimPerson);

                    Plan matsimPlan = matsimPopulationFactory.createPlan();
                    matsimPerson.addPlan(matsimPlan);

                    Activity activity1 = matsimPopulationFactory.createActivityFromCoord("home", dwellingCoord);
                    Integer departureTime = defineDepartureFromHome(job);
                    activity1.setEndTime(departureTime);
                    matsimPlan.addActivity(activity1);
                    matsimPlan.addLeg(matsimPopulationFactory.createLeg(TransportMode.car));

                    Activity activity2 = matsimPopulationFactory.createActivityFromCoord("work", jobCoord);
                    activity2.setEndTime(defineWorkEndTime(job, departureTime));
                    matsimPlan.addActivity(activity2);
                    matsimPlan.addLeg(matsimPopulationFactory.createLeg(TransportMode.car));

                    Activity activity3 = matsimPopulationFactory.createActivityFromCoord("home", dwellingCoord);

                    matsimPlan.addActivity(activity3);
                }
            }

        }
        logger.info("Finished creating a MATSim population.");
        return matsimPopulation;
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

    @Override
    public Scenario assembleScenario(Config initialMatsimConfig, int year, TravelTimes travelTimes) {
        Config config = createMatsimConfig(initialMatsimConfig);

//        if(dataContainer.getTravelTimes() instanceof SkimTravelTimes) {
//
//        }
//        if(year == properties.main.startYear) {
//            logger.info("Referring to the simple MATSim scenario assembler for the base year as no travel times are available yet.");
//            return new SimpleMatsimScenarioAssembler(dataContainer, properties).assembleScenario(initialMatsimConfig, year);
//        } else {
//            travelTimes = dataContainer.getTravelTimes();
//        }


        MutableScenario scenario = (MutableScenario) ScenarioUtils.loadScenario(config);
        Population population = generateDemand(travelTimes);
        scenario.setPopulation(population);

        return scenario;
    }

    private Config createMatsimConfig(Config initialConfig) {
        logger.info("Stating creating a MATSim config.");
        Config config = ConfigUtils.loadConfig(initialConfig.getContext());
        config.qsim().setFlowCapFactor(properties.transportModel.matsimScaleFactor * properties.main.scaleFactor);
        config.qsim().setStorageCapFactor(properties.transportModel.matsimScaleFactor * properties.main.scaleFactor);

        // TODO Add some switch here like "autoGenerateSimplePlans" or similar...
        PlanCalcScoreConfigGroup.ActivityParams homeActivity = new PlanCalcScoreConfigGroup.ActivityParams("home");
        homeActivity.setTypicalDuration(12*60*60);
        config.planCalcScore().addActivityParams(homeActivity);

        PlanCalcScoreConfigGroup.ActivityParams workActivity = new PlanCalcScoreConfigGroup.ActivityParams("work");
        workActivity.setTypicalDuration(8*60*60);
        config.planCalcScore().addActivityParams(workActivity);

        config.vspExperimental().setVspDefaultsCheckingLevel(VspExperimentalConfigGroup.VspDefaultsCheckingLevel.warn);

        logger.info("Finished creating a MATSim config.");
        return config;
    }



}


