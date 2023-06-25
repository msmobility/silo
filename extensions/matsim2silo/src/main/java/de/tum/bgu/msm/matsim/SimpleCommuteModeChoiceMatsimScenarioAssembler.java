package de.tum.bgu.msm.matsim;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.data.vehicle.VehicleType;
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
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.Random;

public class SimpleCommuteModeChoiceMatsimScenarioAssembler implements MatsimScenarioAssembler {

    private final static Logger logger = Logger.getLogger(SimpleMatsimScenarioAssembler.class);

    private final DataContainer dataContainer;
    private final Properties properties;
    private final HandlingOfRandomness handlingOfRandomObject;
    private CommuteModeChoice commuteModeChoice;
    private final Random random;
    // yyyy I found this using the regular silo random number sequence.  In consequence, it was using different random numbers every time it was
    // called, in consequence picking different agents from silo.  This is not what we want.  --  It also picked different agents every time it ran.
    // No idea why; by design, the silo rnd number sequence should be deterministic.  However, we also have a randomly occuring binarySearch error, so
    // there must be something random in the code, possibly race conditions in the multithreading.  kai, jun'23
    public enum HandlingOfRandomness{fromSilo, localInstanceFromMatsimWithAlwaysSameSeed }
    // (See above.  I cannot say what of this is truly needed.  kai, jun'23)

    public SimpleCommuteModeChoiceMatsimScenarioAssembler(DataContainer dataContainer, Properties properties, CommuteModeChoice commuteModeChoice){
        this( dataContainer, properties, commuteModeChoice, HandlingOfRandomness.fromSilo );
    }
    public SimpleCommuteModeChoiceMatsimScenarioAssembler(DataContainer dataContainer, Properties properties, CommuteModeChoice commuteModeChoice,
                                                          HandlingOfRandomness handlingOfRandomObject ){
        this.dataContainer = dataContainer;
        this.properties = properties;
        this.commuteModeChoice = commuteModeChoice;

        this.handlingOfRandomObject = handlingOfRandomObject;

        switch( handlingOfRandomObject ) {
            case fromSilo:
                this.random = SiloUtil.getRandomObject();
                break;
            case localInstanceFromMatsimWithAlwaysSameSeed:
                this.random = MatsimRandom.getLocalInstance();
                break;
            default:
                throw new IllegalStateException( "Unexpected value: " + handlingOfRandomObject );
        }

    }

    @Override
    public Scenario assembleScenario(Config matsimConfig, int year, TravelTimes travelTimes) {
        logger.info("Starting creating (mode-respecting, home-work-home) MATSim scenario.");

        switch( handlingOfRandomObject ){
            case fromSilo:
                break;
            case localInstanceFromMatsimWithAlwaysSameSeed:
                random.setSeed( 4711 );
                // (note that we WANT this with the same random seed for every year when matsim is called.  Could, however, be made dependent on the silo
                // seed, so that with a change of the silo seed it also changes the random seed here.  kai' jun'23)

                // yyyy this will probably not work.  A person at a certain position will not be the same person some silo years later, since
                // intermediate persons may have been deleted and/or added.  In matsim4urbansim, I therefore went through the remembered matsim
                // population, and pulled the corresponding persons from the land use population.  And then added randomly drawn additional persons
                // until the right amount was found (not necessarily the same as in the 1st iteration).  kai, jun'23

                break;
            default:
                throw new IllegalStateException( "Unexpected value: " + handlingOfRandomObject );
        }

        double populationScalingFactor = properties.transportModel.matsimScaleFactor;
        SiloMatsimUtils.checkSiloPropertiesAndMatsimConfigConsistency(matsimConfig, properties);

        Scenario scenario = ScenarioUtils.loadScenario(matsimConfig);
        Population matsimPopulation = scenario.getPopulation();

        JobDataManager jobDataManager = dataContainer.getJobDataManager();
        RealEstateDataManager realEstateDataManager = dataContainer.getRealEstateDataManager();

        for (Household household: dataContainer.getHouseholdDataManager().getHouseholds()) {
//            if (SiloUtil.getRandomNumberAsDouble() > populationScalingFactor) {
            if ( random.nextDouble() > populationScalingFactor ) {
                // re random see above.  kai, jun'23

                continue;
            }

            for (Person person : household.getPersons().values()) {
                if (person.getOccupation() != Occupation.EMPLOYED || person.getJobId() == -2) { // i.e. person does not work
                    continue;
                }

                PopulationFactory pf = matsimPopulation.getFactory();

                final int noHHAUtos = (int) household.getVehicles().stream().filter( vv -> vv.getType().equals( VehicleType.CAR ) ).count();
                org.matsim.api.core.v01.population.Person matsimAlterEgo = SiloMatsimUtils.createMatsimAlterEgo(pf, person, noHHAUtos );
                matsimPopulation.addPerson(matsimAlterEgo);

                Dwelling dwelling = realEstateDataManager.getDwelling(household.getDwellingId());
                CommuteModeChoiceMapping commuteModeChoiceMapping = commuteModeChoice.assignCommuteModeChoice(dwelling, travelTimes, household);

                String mode = commuteModeChoiceMapping.getMode(person).mode;

                if (mode.equals(TransportMode.car)) {
                    Coord dwellingCoord = getOrRandomlyChooseDwellingCoord(dwelling);

                    Job job = jobDataManager.getJobFromId(person.getJobId());
                    Coord jobCoord = getOrRandomlyChooseJobCoordinate(job);

                    createHWHPlanAndAddToAlterEgo(pf, matsimAlterEgo, dwellingCoord, job, jobCoord, TransportMode.car);
                } else {
                    // TODO MATSim expects the plans to be there at least in VspPlansCleaner.notifyBeforeMobsim(VspPlansCleaner.java:54).
                    // Therefore, the intended switch one line below may not work; maybe with other settings
                    // if (!properties.transportModel.onlySimulateCarTrips) {
                        Coord dwellingCoord = getOrRandomlyChooseDwellingCoord(dwelling);

                        Job job = jobDataManager.getJobFromId(person.getJobId());
                        Coord jobCoord = getOrRandomlyChooseJobCoordinate(job);

                        createHWHPlanAndAddToAlterEgo(pf, matsimAlterEgo, dwellingCoord, job, jobCoord, mode);
                    // }
                }
            }
        }
        logger.info("Finished creating MATSim scenario.");
        return scenario;
    }

    private Coord getOrRandomlyChooseDwellingCoord(Dwelling dwelling) {
        Coordinate dwellingCoordinate;
        if (dwelling != null && dwelling.getCoordinate() != null) {
            dwellingCoordinate = dwelling.getCoordinate();
        } else {
            // TODO This step should not be done (again) if a random coordinate for the same dwelling has been chosen before, dz 10/20
            // re random see above.  kai, jun'23
            dwellingCoordinate = dataContainer.getGeoData().getZones().get(dwelling.getZoneId()).getRandomCoordinate(random);
        }
        return new Coord(dwellingCoordinate.x, dwellingCoordinate.y);
    }

    private Coord getOrRandomlyChooseJobCoordinate(Job job) {
        Coordinate jobCoordinate;
        if (job != null && job.getCoordinate() != null) {
            jobCoordinate = job.getCoordinate();
        } else {
            // TODO This step should not be done (again) if a random coordinate for the same job has been chosen before, dz 10/20
            // re random see above.  kai, jun'23
            jobCoordinate = dataContainer.getGeoData().getZones().get(job.getZoneId()).getRandomCoordinate(random);
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
        return job.getStartTimeInSeconds().orElse(Math.max(0, (int) (properties.transportModel.peakHour_s + random.nextGaussian() * 3600)));
        // re random see above.  kai, jun'23
    }

    /**
     * Defines departure time from work. Note that it actually tries to use job duration times if defined. Otherwise
     * randomly draws from a normal distribution with mean of 8 hours with 1 hour standard deviation. The duration
     * is then added to the job starting time.
     */
    private int defineWorkEndTime(Job job, int departureTime) {
        return departureTime + job.getWorkingTimeInSeconds().orElse(Math.max(0, (int) (8*3600 + random.nextGaussian() * 3600)));
        // re random see above.  kai, jun'23
    }
}
