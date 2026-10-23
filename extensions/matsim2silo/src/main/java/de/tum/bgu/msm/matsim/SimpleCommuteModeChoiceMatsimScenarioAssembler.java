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
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.router.TripStructureUtils;
import org.matsim.core.scenario.ScenarioUtils;

import java.nio.file.Paths;
import java.util.*;

public class SimpleCommuteModeChoiceMatsimScenarioAssembler implements MatsimScenarioAssembler {

    private final static Logger logger = LogManager.getLogger(SimpleCommuteModeChoiceMatsimScenarioAssembler.class);

    private final DataContainer dataContainer;
    private final Properties properties;
    private final HandlingOfRandomness handlingOfRandomObject;
    private final CommuteModeChoice commuteModeChoice;
    private final Random random;

    static final String DWELLING_ID_ATTR = "dwellingId";
    static final String JOB_ID_ATTR = "jobId";

    private record AgentWithMode(org.matsim.api.core.v01.population.Person matsimPerson, String mode) {}

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
            initRandom();

            // fresh scenario, like in assembleScenario; matsimData's scenario stays untouched
            Scenario scenario = ScenarioUtils.loadScenario(oldScenario.getConfig());
            Population matsimPopulation = scenario.getPopulation();
            PopulationFactory pf = matsimPopulation.getFactory();

            double populationScalingFactor = properties.transportModel.matsimScaleFactor;
            SiloMatsimUtils.checkSiloPropertiesAndMatsimConfigConsistency(oldScenario.getConfig(), properties);

            JobDataManager jobDataManager = dataContainer.getJobDataManager();
            RealEstateDataManager realEstateDataManager = dataContainer.getRealEstateDataManager();
            Map<Id<org.matsim.api.core.v01.population.Person>, String> source = new HashMap<>();

            // 1. expected pop size: all currently employed silo persons
            int shouldPop = 0;

            for (Person p : dataContainer.getHouseholdDataManager().getPersons()) {
                if (isEmployedWithJob(p)) {
                    shouldPop++;
                }
            }

            shouldPop *= populationScalingFactor;

            // 2. carry over previous-year persons who still exist and still employed
            List<org.matsim.api.core.v01.population.Person> previous = new ArrayList<>(previousYearPopulation.values());
            Collections.shuffle(previous, random);   // so that, if we have too many, the ones dropped are random

            int kept = 0;
            int replanned = 0;
            int dropped = 0;
            for (org.matsim.api.core.v01.population.Person matsimPerson : previous) {
                if (kept >= shouldPop) break;        // replaces the old "shouldPop < actualPop" trimming

                Person siloPerson = dataContainer.getHouseholdDataManager()
                        .getPersonFromId(Integer.parseInt(matsimPerson.getId().toString()));

                boolean exists = siloPerson != null;
                boolean employed = exists && isEmployedWithJob(siloPerson);

                String mode = null;
                boolean moved = false;
                boolean modeChanged = false;

                if (employed) {
                    mode = determineMode(realEstateDataManager, siloPerson, travelTimes);

                    TripStructureUtils.Trip firstTrip = TripStructureUtils.getTrips(matsimPerson.getSelectedPlan()).getFirst();
                    String previousMode = TripStructureUtils.getRoutingModeIdentifier().identifyMainMode(firstTrip.getTripElements());

                    moved = homeOrWorkChanged(
                            (int) matsimPerson.getAttributes().getAttribute("dwellingId"),
                            (int) matsimPerson.getAttributes().getAttribute("jobId"),
                            siloPerson.getHousehold().getDwellingId(),
                            siloPerson.getJobId());
                    modeChanged = !Objects.equals(mode, previousMode);
                }

                Outcome outcome = classify(exists, employed, moved, modeChanged);
                if (outcome == Outcome.DROP) {
                    dropped++;
                    continue;
                }

                if (outcome == Outcome.REPLAN) {
                    replanned++;
                    matsimPerson.getPlans().clear();
                    matsimPerson.setSelectedPlan(null);
                    setHomeWorkAttributes(matsimPerson, siloPerson);
                    createPlan(realEstateDataManager, jobDataManager, siloPerson, mode, pf, matsimPerson);
                }

                source.put(matsimPerson.getId(), outcome.label);
                matsimPopulation.addPerson(matsimPerson);   // old object, with old or new plan
                kept++;
            }

            // 3. top up with employed silo persons who weren't in last year's population
            int added = 0;
            if (kept < shouldPop) {
                ArrayList<Person> candidates = new ArrayList<>();
                for (Person p : dataContainer.getHouseholdDataManager().getPersons()) {
                    if (!isEmployedWithJob(p)) continue;
                    if (previousYearPopulation.containsKey(Id.createPersonId(Integer.toString(p.getId())))) continue;
                    candidates.add(p);
                }

                if (candidates.size() < shouldPop - kept) {
                    logger.warn("not enough silo agents to top off matsim for year {}", year);
                }

                // this is probably a redundant duplicate check since we calc shouldPop earlier by filtering out unemployed.
                int needed = Math.min(shouldPop - kept, candidates.size());
                for (Person siloPerson : getRandomSample(candidates, needed)) {
                    AgentWithMode agent = createAndAddAgent(realEstateDataManager, travelTimes, siloPerson, pf, matsimPopulation);
                    source.put(agent.matsimPerson().getId(), "new");
                    createPlan(realEstateDataManager, jobDataManager, siloPerson, agent.mode(), pf, agent.matsimPerson());
                    added++;
                }
            }

            int previousSize = previous.size();
            logger.info("previous=" + previousSize + " shouldPop=" + shouldPop + " kept=" + kept
                    + " replanned=" + replanned + " dropped=" + dropped + " added=" + added
                    + " trimmed=" + (previousSize - kept - dropped) + " final=" + matsimPopulation.getPersons().size());

            java.nio.file.Path csvPath = java.nio.file.Paths.get(
                    properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName
                            + "/matsim/" + year + "/assembledPlans.csv");

            writePlansToCsv(csvPath, source, matsimPopulation);
            return scenario;
        }

    @Override
    public Scenario assembleScenario(Config matsimConfig, int year, TravelTimes travelTimes) {
        logger.info("Starting creating (mode-respecting, home-work-home) MATSim scenario.");

        initRandom();
        double populationScalingFactor = properties.transportModel.matsimScaleFactor;
        SiloMatsimUtils.checkSiloPropertiesAndMatsimConfigConsistency(matsimConfig, properties);
        Scenario scenario = ScenarioUtils.loadScenario(matsimConfig);
        // at this pint - population is still 0!
        Population matsimPopulation = scenario.getPopulation();

        JobDataManager jobDataManager = dataContainer.getJobDataManager();
        RealEstateDataManager realEstateDataManager = dataContainer.getRealEstateDataManager();
        PopulationFactory pf = matsimPopulation.getFactory();
        Map<Id<org.matsim.api.core.v01.population.Person>, String> source = new HashMap<>();

        // first year: scale number of households based on populationScaleFactor
        // for each person in this sampled num of houses, generate a plan with car or other mode
        // decided by probability
        for (Household household: dataContainer.getHouseholdDataManager().getHouseholds()) {
            if ( random.nextDouble() > populationScalingFactor ) {
                // re random see above.  kai, jun'23
                continue;
            }
            for (Person person : household.getPersons().values()) {
                if (!isEmployedWithJob(person)) {
                    continue;
                }
                AgentWithMode agent = createAndAddAgent(realEstateDataManager, travelTimes, person, pf, matsimPopulation);
                source.put(agent.matsimPerson().getId(), "new");
                createPlan(realEstateDataManager, jobDataManager, person, agent.mode(), pf, agent.matsimPerson());
            }
        }

        writePlansToCsv(Paths.get(properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName
                + "/matsim/" + year + "/assembledPlans.csv"), source, matsimPopulation);

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

    private <T> ArrayList<T> getRandomSample(ArrayList<T> source, int n) {
        if (n > source.size()) throw new IllegalArgumentException("n > size");
        List<T> copy = new ArrayList<>(source);
        Collections.shuffle(copy, random);
        return new ArrayList<>(copy.subList(0, n));
    }

    enum Outcome {
        DROP("dropped"), KEEP("kept"), REPLAN("replanned");
        final String label;
        Outcome(String label) { this.label = label; }
    }

    static public Outcome classify(boolean stillExists, boolean employedWithJob,
                            boolean homeOrWorkChanged, boolean modeChanged) {
        if (!stillExists || !employedWithJob) return Outcome.DROP;
        return (homeOrWorkChanged || modeChanged) ? Outcome.REPLAN : Outcome.KEEP;
    }

    static boolean homeOrWorkChanged(int oldDwellingId, int oldJobId, int newDwellingId, int newJobId) {
        return oldDwellingId != newDwellingId || oldJobId != newJobId;
    }


    private void initRandom() {
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
    }


    private void createPlan(RealEstateDataManager realEstateDataManager,
                            JobDataManager jobDataManager, Person siloPerson,
                            String mode, PopulationFactory pf,
                            org.matsim.api.core.v01.population.Person matsimPerson) {

        Coord dwellingCoord = getOrRandomlyChooseDwellingCoord(realEstateDataManager.getDwelling(siloPerson.getHousehold().getDwellingId()));
        Job job = jobDataManager.getJobFromId(siloPerson.getJobId());
        Coord jobCoord = getOrRandomlyChooseJobCoordinate(job);
        createHWHPlanAndAddToAlterEgo(pf, matsimPerson, dwellingCoord, job, jobCoord, mode);

    }

    private void writePlansToCsv(java.nio.file.Path csvPath, Map<Id<org.matsim.api.core.v01.population.Person>, String> source, Population matsimPopulation) {
        try {
            java.nio.file.Files.createDirectories(csvPath.getParent());
            try (java.io.BufferedWriter w = java.nio.file.Files.newBufferedWriter(csvPath)) {
                w.write("personId,source,nPlans,mode,homeX,homeY,workX,workY,homeId,workId,homeEndTime,workEndTime");
                w.newLine();

                for (org.matsim.api.core.v01.population.Person p : matsimPopulation.getPersons().values()) {
                    Plan plan = p.getSelectedPlan();
                    if (plan == null) {
                        w.write(p.getId() + "," + source.getOrDefault(p.getId(), "unknown") + "," + p.getPlans().size() + ",,,,,,,,,");
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

                    String dwellingId = String.valueOf(p.getAttributes().getAttribute("dwellingId"));
                    String jobId      = String.valueOf(p.getAttributes().getAttribute("jobId"));

                    w.write(String.join(",",
                            p.getId().toString(),
                            source.getOrDefault(p.getId(), "unknown"),
                            Integer.toString(p.getPlans().size()),
                            legMode,
                            home != null ? Double.toString(home.getCoord().getX()) : "",
                            home != null ? Double.toString(home.getCoord().getY()) : "",
                            work != null ? Double.toString(work.getCoord().getX()) : "",
                            work != null ? Double.toString(work.getCoord().getY()) : "",
                            dwellingId,
                            jobId,
                            home != null ? Double.toString(home.getEndTime().orElse(-1)) : "",
                            work != null ? Double.toString(work.getEndTime().orElse(-1)) : ""));
                    w.newLine();
                }
            }
            logger.info("Wrote assembled plans to " + csvPath);
        } catch (java.io.IOException e) {
            logger.error("Could not write plans csv", e);
        }
    }

    private AgentWithMode createAndAddAgent(RealEstateDataManager realEstateDataManager, TravelTimes travelTimes, Person siloPerson, PopulationFactory pf, Population matsimPopulation) {
        Household household = siloPerson.getHousehold();
        int nHhCars = (int) household.getVehicles().stream()
                .filter(v -> v.getType().equals(VehicleType.CAR)).count();
        org.matsim.api.core.v01.population.Person matsimPerson =
                SiloMatsimUtils.createMatsimAlterEgo(pf, siloPerson, nHhCars);
        setHomeWorkAttributes(matsimPerson, siloPerson);
        matsimPopulation.addPerson(matsimPerson);

        String mode = determineMode(realEstateDataManager, siloPerson, travelTimes);

        return new AgentWithMode(matsimPerson, mode);
    }

    static boolean isEmployedWithJob(Occupation occupation, int jobId) {
        return occupation == Occupation.EMPLOYED && jobId != -2;
    }

    private boolean isEmployedWithJob(Person p) {          // keeps call sites unchanged
        return isEmployedWithJob(p.getOccupation(), p.getJobId());
    }

    private String determineMode(RealEstateDataManager realEstateDataManager, Person siloPerson, TravelTimes travelTimes) {
        return commuteModeChoice.assignCommuteModeChoice(
                realEstateDataManager.getDwelling(siloPerson.getHousehold().getDwellingId()),
                travelTimes, siloPerson.getHousehold()).getMode(siloPerson).mode;
    }

    private void setHomeWorkAttributes(org.matsim.api.core.v01.population.Person matsimPerson, Person siloPerson) {
        matsimPerson.getAttributes().putAttribute(DWELLING_ID_ATTR, siloPerson.getHousehold().getDwellingId());
        matsimPerson.getAttributes().putAttribute(JOB_ID_ATTR, siloPerson.getJobId());
    }

}
