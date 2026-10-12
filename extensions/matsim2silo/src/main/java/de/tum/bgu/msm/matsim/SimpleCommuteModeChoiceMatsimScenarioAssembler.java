package de.tum.bgu.msm.matsim;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Day;
import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.data.vehicle.VehicleType;
import de.tum.bgu.msm.models.modeChoice.CommuteModeChoice;
import de.tum.bgu.msm.models.modeChoice.CommuteModeChoiceMapping;
import de.tum.bgu.msm.modules.tripGeneration.TripGeneration;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;
import org.matsim.analysis.TripsAndLegsWriter;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.*;
import org.matsim.contrib.travelsummary.events2traveldiaries.travelcomponents.Trip;
import org.matsim.core.config.Config;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.router.TripStructureUtils;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.*;

public class SimpleCommuteModeChoiceMatsimScenarioAssembler implements MatsimScenarioAssembler {

    private final static Logger logger = LogManager.getLogger(SimpleMatsimScenarioAssembler.class);

    private final DataContainer dataContainer;
    private final Properties properties;
    private final HandlingOfRandomness handlingOfRandomObject;
    private final CommuteModeChoice commuteModeChoice;
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
        public Scenario assembleScenarioWithPreviousPlans(
                Scenario oldScenario,
        int year,
        TravelTimes travelTimes,
        HashMap<Id<org.matsim.api.core.v01.population.Person>, org.matsim.api.core.v01.population.Person> previousYearPopulation) {

            logger.info("Starting MATSim scenario using previous year's plans.");
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


            // fresh scenario, like in assembleScenario; matsimData's scenario stays untouched
            Scenario scenario = ScenarioUtils.loadScenario(oldScenario.getConfig());
            Population matsimPopulation = scenario.getPopulation();
            PopulationFactory pf = matsimPopulation.getFactory();

            double populationScalingFactor = properties.transportModel.matsimScaleFactor;
            SiloMatsimUtils.checkSiloPropertiesAndMatsimConfigConsistency(oldScenario.getConfig(), properties);

            JobDataManager jobDataManager = dataContainer.getJobDataManager();
            RealEstateDataManager realEstateDataManager = dataContainer.getRealEstateDataManager();

            // 1. should pop size: all currently employed silo persons
            int shouldPop = 0;
            Map<Id<org.matsim.api.core.v01.population.Person>, String> source = new HashMap<>();

            for (Person p : dataContainer.getHouseholdDataManager().getPersons()) {
                if (p.getOccupation() == Occupation.EMPLOYED && p.getJobId() != -2) {
                    shouldPop++;
                }
            }

            shouldPop *= populationScalingFactor;

            // 2. carry over previous-year persons who still exist and still employed
            List<org.matsim.api.core.v01.population.Person> previous = new ArrayList<>(previousYearPopulation.values());
            Collections.shuffle(previous, random);   // so that, if we have too many, the ones dropped are random

            int kept = 0;
            int replanned = 0;
            for (org.matsim.api.core.v01.population.Person matsimPerson : previous) {
                if (kept >= shouldPop) break;        // replaces the old "shouldPop < actualPop" trimming

                Person siloPerson = dataContainer.getHouseholdDataManager()
                        .getPersonFromId(Integer.parseInt(matsimPerson.getId().toString()));

                if (siloPerson == null) continue;    // deceased, for example
                if (siloPerson.getOccupation() != Occupation.EMPLOYED || siloPerson.getJobId() == -2) continue;

                String mode = commuteModeChoice.assignCommuteModeChoice(
                        realEstateDataManager.getDwelling(siloPerson.getHousehold().getDwellingId()),
                        travelTimes, siloPerson.getHousehold()).getMode(siloPerson).mode;

                TripStructureUtils.Trip firstTrip = TripStructureUtils.getTrips(matsimPerson.getSelectedPlan()).getFirst();
                String previousMode = TripStructureUtils.getRoutingModeIdentifier().identifyMainMode(firstTrip.getTripElements());

                boolean changed = homeOrWorkChanged(
                        (int) matsimPerson.getAttributes().getAttribute("dwellingId"),
                        (int) matsimPerson.getAttributes().getAttribute("jobId"),
                        siloPerson)
                        || !Objects.equals(previousMode, mode);

                if (changed) {
                    replanned++;
                    matsimPerson.getPlans().clear();
                    matsimPerson.setSelectedPlan(null);

                    Coordinate c = realEstateDataManager.getDwelling(siloPerson.getHousehold().getDwellingId()).getCoordinate();
                    Coord dwellingCoord = Double.isNaN(c.z) ? new Coord(c.x, c.y) : new Coord(c.x, c.y, c.z);
                    Job job = jobDataManager.getJobFromId(siloPerson.getJobId());
                    Coord jobCoord = getOrRandomlyChooseJobCoordinate(job);
                    createHWHPlanAndAddToAlterEgo(pf, matsimPerson, dwellingCoord, job, jobCoord, mode);
                }
                source.put(matsimPerson.getId(), changed ? "replanned" : "kept");
                matsimPopulation.addPerson(matsimPerson);   // old object, with old or new plan
                kept++;
            }

            // 3. top up with employed silo persons who weren't in last year's population
            int added = 0;
            if (kept < shouldPop) {
                ArrayList<Person> candidates = new ArrayList<>();
                for (Person p : dataContainer.getHouseholdDataManager().getPersons()) {
                    if (p.getOccupation() != Occupation.EMPLOYED || p.getJobId() == -2) continue;
                    if (previousYearPopulation.containsKey(Id.createPersonId(Integer.toString(p.getId())))) continue;
                    candidates.add(p);
                }

                if (candidates.size() < shouldPop - kept) {
                    logger.warn("not enough silo agents to top off matsim for year ", year);
                }

                // this is probably a redundant duplicate check since we calc shouldPop earlier by filtering out unemployed.
                int needed = Math.min(shouldPop - kept, candidates.size());
                for (Person siloPerson : getRandomSample(candidates, needed)) {
                    Household household = siloPerson.getHousehold();
                    int nHhCars = (int) household.getVehicles().stream()
                            .filter(v -> v.getType().equals(VehicleType.CAR)).count();
                    org.matsim.api.core.v01.population.Person matsimPerson =
                            SiloMatsimUtils.createMatsimAlterEgo(pf, siloPerson, nHhCars);
                    source.put(matsimPerson.getId(), "new");
                    matsimPopulation.addPerson(matsimPerson);

                    String mode = commuteModeChoice.assignCommuteModeChoice(
                            realEstateDataManager.getDwelling(household.getDwellingId()),
                            travelTimes, household).getMode(siloPerson).mode;

                    Coordinate c = realEstateDataManager.getDwelling(household.getDwellingId()).getCoordinate();
                    // getDwellingCoord method here
                    Coord dwellingCoord = Double.isNaN(c.z) ? new Coord(c.x, c.y) : new Coord(c.x, c.y, c.z);
                    Job job = jobDataManager.getJobFromId(siloPerson.getJobId());
                    Coord jobCoord = getOrRandomlyChooseJobCoordinate(job);
                    createHWHPlanAndAddToAlterEgo(pf, matsimPerson, dwellingCoord, job, jobCoord, mode);
                    added++;
                }
            }

            logger.info("shouldPop=" + shouldPop + " kept=" + kept + " replanned=" + replanned
                    + " added=" + added + " final=" + matsimPopulation.getPersons().size());

            java.nio.file.Path csvPath = java.nio.file.Paths.get(
                    properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName
                            + "/matsim/" + year + "/assembledPlans.csv");

            try {
                java.nio.file.Files.createDirectories(csvPath.getParent());
                try (java.io.BufferedWriter w = java.nio.file.Files.newBufferedWriter(csvPath)) {
                    w.write("personId,source,nPlans,mode,homeX,homeY,workX,workY,homeEndTime,workEndTime");
                    w.newLine();

                    for (org.matsim.api.core.v01.population.Person p : matsimPopulation.getPersons().values()) {
                        Plan plan = p.getSelectedPlan();
                        if (plan == null) {
                            w.write(p.getId() + "," + source.getOrDefault(p.getId(), "unknown") + "," + p.getPlans().size() + ",,,,,,,");
                            w.newLine();
                            continue;
                        }

                        List<Activity> acts = new ArrayList<>();
                        String legMode = "";
                        for (PlanElement pe : plan.getPlanElements()) {
                            if (pe instanceof Activity a) acts.add(a);
                            else if (pe instanceof Leg l && legMode.isEmpty()) legMode = l.getMode();
                        }
                        Activity home = acts.size() > 0 ? acts.get(0) : null;
                        Activity work = acts.size() > 1 ? acts.get(1) : null;

                        w.write(String.join(",",
                                p.getId().toString(),
                                source.getOrDefault(p.getId(), "unknown"),
                                Integer.toString(p.getPlans().size()),
                                legMode,
                                home != null ? Double.toString(home.getCoord().getX()) : "",
                                home != null ? Double.toString(home.getCoord().getY()) : "",
                                work != null ? Double.toString(work.getCoord().getX()) : "",
                                work != null ? Double.toString(work.getCoord().getY()) : "",
                                home != null ? Double.toString(home.getEndTime().orElse(-1)) : "",
                                work != null ? Double.toString(work.getEndTime().orElse(-1)) : ""));
                        w.newLine();
                    }
                }
                logger.info("Wrote assembled plans to " + csvPath);
            } catch (java.io.IOException e) {
                logger.error("Could not write plans csv", e);
            }

            return scenario;
        }

    public <T> ArrayList<T> getRandomSample(ArrayList<T> source, int n) {
        if (n > source.size()) throw new IllegalArgumentException("n > size");
        List<T> copy = new ArrayList<>(source);
        Collections.shuffle(copy);
        return new ArrayList<>(copy.subList(0, n));
    }

    public boolean homeOrWorkChanged(int matsimPersonDwellingId, int matsimPersonJobId, Person siloPerson) {

        if(siloPerson.getOccupation() == Occupation.UNEMPLOYED)
        {
            // don't then check for job location, just use new job from assembleScenario
            return true;
        }

        boolean homeChanged =
                matsimPersonDwellingId != siloPerson.getHousehold().getDwellingId();

        boolean workChanged =
                matsimPersonJobId != siloPerson.getJobId();

        return homeChanged || workChanged;
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
        // at this pint - population is still 0!
        Population matsimPopulation = scenario.getPopulation();

        JobDataManager jobDataManager = dataContainer.getJobDataManager();
        RealEstateDataManager realEstateDataManager = dataContainer.getRealEstateDataManager();

        // first year: scale number of households based on populationScaleFactor
        // for each person in this sampled num of houses, generate a plan with car or other mode
        // decided by probability
        for (Household household: dataContainer.getHouseholdDataManager().getHouseholds()) {
//            if (SiloUtil.getRandomNumberAsDouble() > populationScalingFactor) {
            if ( random.nextDouble() > populationScalingFactor ) {
                // re random see above.  kai, jun'23

                continue;
            }

            for (Person person : household.getPersons().values()) {
                // does this mean the person doesn't get a plan if they don't work? So no Freizeit Activities?
                if (person.getOccupation() != Occupation.EMPLOYED || person.getJobId() == -2) { // i.e. person does not work
                    continue;
                }

                PopulationFactory pf = matsimPopulation.getFactory();

                final int nHhCars = (int) household.getVehicles().stream().filter( vv -> vv.getType().equals( VehicleType.CAR ) ).count();
                org.matsim.api.core.v01.population.Person matsimAlterEgo = SiloMatsimUtils.createMatsimAlterEgo(pf, person, nHhCars );
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
        // population is now 2877
        // silo persons = 5715
        // appears that person.getOccupation() != Occupation.EMPLOYED || person.getJobId() == -2 filters out around half of the pop.
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

    // create

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
