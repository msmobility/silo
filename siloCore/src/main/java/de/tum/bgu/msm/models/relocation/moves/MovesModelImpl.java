package de.tum.bgu.msm.models.relocation.moves;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Iterables;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdType;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.vehicle.VehicleType;
import de.tum.bgu.msm.events.impls.household.MoveEvent;
import de.tum.bgu.msm.io.output.YearByYearCsvModelTracker;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.properties.modules.TransportModelPropertiesModule;
import de.tum.bgu.msm.util.concurrent.ConcurrentExecutor;
import de.tum.bgu.msm.utils.SampleException;
import de.tum.bgu.msm.utils.Sampler;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.commons.math3.util.Precision;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;

/**
 * @author Nico
 * Default implementation of the {@link MovesModel} interface. Implements general dwelling search
 * and move logic based on housing satisfaction/utility. The actual utility calculations are defined
 * in the {@link HousingStrategy} argument.
 */
public class MovesModelImpl extends AbstractModel implements MovesModel {

//    public static BufferedWriter fileWriter;

    public static boolean track = false;

    protected final static Logger logger = Logger.getLogger(MovesModelImpl.class);
    private static final int MAX_NUMBER_DWELLINGS = 20;

    private final MovesStrategy movesStrategy;
    private final HousingStrategy housingStrategy;

    private final boolean threaded;

    private final Map<HouseholdType, Double> averageHousingSatisfaction = new ConcurrentHashMap<>();
    private final Map<Integer, Double> satisfactionByHousehold = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> householdsByZone = new HashMap<>();
    private final Map<Integer, Double > sumOfSatisfactionsByZone = new HashMap<>();
    private YearByYearCsvModelTracker relocationTracker;


    public MovesModelImpl(DataContainer dataContainer, Properties properties, MovesStrategy movesStrategy,
                          HousingStrategy housingStrategy, Random random) {
        super(dataContainer, properties, random);
//        try {
//            fileWriter = new BufferedWriter(new FileWriter(new File(properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName + "/indiv_tt.csv")));
//            fileWriter.write("ppId,hhId,ddId,jobId,jobX,jobY,ddX,ddY,jobZone,dwellingZone,min,minSkim,queryTime,ddUtil,areaDDZone,areaJJZone,minFixedTime,minFixedZone,transitSkim,transitIndiv,transitIndivFixedQuery,transitIndivFixedZone");
//            fileWriter.newLine();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        this.movesStrategy = movesStrategy;
        this.housingStrategy = housingStrategy;
        this.threaded = properties.transportModel.travelTimeImplIdentifier == TransportModelPropertiesModule.TravelTimeImplIdentifier.MATSIM;
    }

    @Override
    public void setup() {
        housingStrategy.setup();
        String header = new StringJoiner(",").add("hh").add("oldDdd").add("newDd").add("oldX").add("oldY").add("newX").add("newY").add("oldZone").add("newZone").add("autos").add("licenses").add("workers").toString();
        Path basePath = Paths.get(properties.main.baseDirectory).resolve("scenOutput").resolve(properties.main.scenarioName).resolve("siloResults/relocation");
        relocationTracker = new YearByYearCsvModelTracker(basePath, "relocation", header);
    }

    @Override
    public void prepareYear(int year) {
        housingStrategy.prepareYear();
        track = false;
        calculateAverageHousingUtility();
        track = true;
        relocationTracker.newYear(year);
    }

    @Override
    public List<MoveEvent> getEventsForCurrentYear(int year) {
        final List<MoveEvent> events = new ArrayList<>();
        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()) {
            events.add(new MoveEvent(hh.getId()));
        }
        if (threaded) {
            final int threads = Math.max(properties.main.numberOfThreads - 1, 1);
            UtilityUtils.startThreads(housingStrategy, threads);
            logger.info("=========> Started " + threads + " background threads for dwelling utility evaluation");
        }

        return events;
    }

    @Override
    public void endYear(int year) {
        UtilityUtils.endYear();
    }

    @Override
    public void endSimulation() {
        relocationTracker.end();
//        try {
//            fileWriter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * Simulates (a) if this household moves and (b) where this household moves
     */
    @Override
    public boolean handleEvent(MoveEvent event) {

        int hhId = event.getHouseholdId();
        Household household = dataContainer.getHouseholdDataManager().getHouseholdFromId(hhId);
        if (household == null) {
            // Household does not exist anymore
            return false;
        }

        // Step 1: Consider relocation if household is not very satisfied or if
        // household income exceed restriction for low-income dwelling
        if (!moveOrNot(household)) {
            return false;
        }

        final int idOldDd = household.getDwellingId();
        // Step 2: Choose new dwelling
        int idNewDD = searchForNewDwelling(household);

        if (idNewDD > 0) {

            // Step 3: Move household
            dataContainer.getHouseholdDataManager().saveHouseholdMemento(household);
            printMove(household, idOldDd, idNewDD);
            moveHousehold(household, idOldDd, idNewDD);
            if (hhId == SiloUtil.trackHh) {
                SiloUtil.trackWriter.println("Household " + hhId + " has moved to newDwelling " +
                        idOldDd);
            }
            return true;
        } else {
            if (hhId == SiloUtil.trackHh) {
                SiloUtil.trackWriter.println("Household " + hhId + " intended to move but " +
                        "could not find an adequate dwelling.");
            }
            return false;
        }
    }

    private void printMove(Household household, int idOldDd, int idNewDD) {
        final Dwelling oldDwelling = dataContainer.getRealEstateDataManager().getDwelling(idOldDd);
        int oldZoneId = oldDwelling.getZoneId();
        final Dwelling newDwelling = dataContainer.getRealEstateDataManager().getDwelling(idNewDD);
        int newZoneId = newDwelling.getZoneId();
        Coordinate oldCoordinate = oldDwelling.getCoordinate();
        Coordinate newCoordinate = newDwelling.getCoordinate();
        if(oldCoordinate == null) {
            oldCoordinate = new Coordinate(Double.NaN, Double.NaN);
        }
        if(newCoordinate == null) {
            newCoordinate = new Coordinate(Double.NaN, Double.NaN);
        }

        relocationTracker.trackRecord(new StringJoiner(",")
                .add(String.valueOf(household.getId()))
                .add(String.valueOf(idOldDd))
                .add(String.valueOf(idNewDD))
                .add(String.valueOf(oldCoordinate.x))
                .add(String.valueOf(oldCoordinate.y))
                .add(String.valueOf(newCoordinate.x))
                .add(String.valueOf(newCoordinate.y))
                .add(String.valueOf(oldZoneId))
                .add(String.valueOf(newZoneId))
                .add(String.valueOf(household.getVehicles().stream().filter(vv -> vv.getType().equals(VehicleType.CAR)).count()))
                .add(String.valueOf(HouseholdUtil.getHHLicenseHolders(household)))
                .add(String.valueOf(HouseholdUtil.getNumberOfWorkers(household)))
                .toString());
    }

    @Override
    public int searchForNewDwelling(Household household) {

        // Step 1: select region
        final GeoData geoData = dataContainer.getGeoData();
        Sampler<Region> regionSampler = new Sampler<>(geoData.getRegions().size(), Region.class, this.random);
        for (Region region : geoData.getRegions().values()) {
            double utility;
            if (dataContainer.getRealEstateDataManager().getNumberOfVacantDDinRegion(region.getId()) == 0) {
                // if utility it normalized by regional attibutes other than number of vacant dwellings, it could happen
                // that a region is chosen with 0 vacant dwellings. To avoid this case, set utility to 0 if no vacant
                // dwellings are available in that region.
                utility = 0.;
            } else {
                utility = housingStrategy.calculateRegionalUtility(household, region);
            }
            regionSampler.incrementalAdd(region, utility);
        }
        if (regionSampler.getCumulatedProbability() == 0.) {
            return -1;
        }
        Region selectedRegion;
        try {
            selectedRegion = regionSampler.sampleObject();
        } catch (SampleException e) {
            throw new RuntimeException(e);
        }

        // Step 2: select vacant dwelling in selected region
        List<Dwelling> vacantDwellings
                = new ArrayList<>(dataContainer.getRealEstateDataManager().getListOfVacantDwellingsInRegion(selectedRegion.getId()));
        if (vacantDwellings.isEmpty()) {
            return -1;
        }

        /** No household will evaluate more than {@link MAX_NUMBER_DWELLINGS} dwellings */
        int maxNumberOfDwellings = Math.min(MAX_NUMBER_DWELLINGS, vacantDwellings.size());

        UtilityUtils.reset();

        Collections.shuffle(vacantDwellings, this.random);
        for (int i = 0; i < maxNumberOfDwellings; i++) {
            Dwelling dwelling = vacantDwellings.get(i);
            if (housingStrategy.isHouseholdEligibleToLiveHere(household, dwelling)) {
                if (threaded) {
                    UtilityUtils.queue.add(new UtilityTask(i, dwelling, household));
                } else {
                    double util = housingStrategy.calculateHousingUtility(household, dwelling);
                    double prob = housingStrategy.calculateSelectDwellingProbability(util);
                    UtilityUtils.probabilities[i] = prob;
                    UtilityUtils.dwellings[i] = dwelling;
                }
            } else {
                maxNumberOfDwellings--;
            }
        }


        if (threaded) {
            try {
                UtilityUtils.barrier.await();
                UtilityUtils.barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }

        Sampler<Dwelling> sampler = new Sampler<>(UtilityUtils.dwellings, UtilityUtils.probabilities, this.random);
        try {
            return sampler.sampleObject().getId();
        } catch (SampleException e) {
            logger.warn(e.getMessage());
            return -1;
        }
    }

    private boolean moveOrNot(Household household) {
        HouseholdType hhType = household.getHouseholdType();
        Dwelling dd = dataContainer.getRealEstateDataManager().getDwelling(household.getDwellingId());
        if (!housingStrategy.isHouseholdEligibleToLiveHere(household, dd)) {
            return true;
        }
        final double currentUtil = satisfactionByHousehold.get(household.getId());
        final double avgSatisfaction = averageHousingSatisfaction.getOrDefault(hhType, currentUtil);

        final double prop = movesStrategy.getMovingProbability(avgSatisfaction, currentUtil);
        return this.random.nextDouble() <= prop;
    }


    private void calculateAverageHousingUtility() {
        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        logger.info("Evaluating average housing utility of " + householdDataManager.getHouseholds().size() + " households.");
        satisfactionByHousehold.clear();
        averageHousingSatisfaction.replaceAll((householdType, aDouble) -> 0.);
        householdsByZone.clear();
        sumOfSatisfactionsByZone.clear();

        final Collection<Household> households = householdDataManager.getHouseholds();
        ConcurrentHashMultiset<HouseholdType> hhByType = ConcurrentHashMultiset.create();

        int numberOfTasks;

        if (threaded) {
            numberOfTasks = Properties.get().main.numberOfThreads;
        } else {
            numberOfTasks = 1;
        }
        final int partitionSize = (int) ((double) households.size() / (numberOfTasks)) + 1;
        Iterable<List<Household>> partitions = Iterables.partition(households, partitionSize);
        ConcurrentExecutor<Void> executor = ConcurrentExecutor.fixedPoolService(Properties.get().main.numberOfThreads);

        logger.info("=========> Using " + numberOfTasks + " thread(s)" +
                " with partitions of size " + partitionSize);

        for (final List<Household> partition : partitions) {
            HousingStrategy strategy = housingStrategy.duplicate();
            executor.addTaskToQueue(() -> {
                try {
                    for (Household hh : partition) {
                        final HouseholdType householdType = hh.getHouseholdType();
                        hhByType.add(householdType);
                        Dwelling dd = dataContainer.getRealEstateDataManager().getDwelling(hh.getDwellingId());
                        final double util = strategy.calculateHousingUtility(hh, dd);
                        satisfactionByHousehold.put(hh.getId(), util);
                        householdsByZone.put(dd.getZoneId(), householdsByZone.getOrDefault(dd.getZoneId(), 0) + 1);
                        sumOfSatisfactionsByZone.put(dd.getZoneId(), sumOfSatisfactionsByZone.getOrDefault(dd.getZoneId(), 0.) + util);
                        averageHousingSatisfaction.merge(householdType, util, (oldUtil, newUtil) -> oldUtil + newUtil);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return null;
            });
        }
        executor.execute();
        logger.info("Done evaluating average housing utility.");

        averageHousingSatisfaction.replaceAll((householdType, satisfaction) ->
                Precision.round(satisfaction / (1. * hhByType.count(householdType)), 5));
    }

    @Override
    public void moveHousehold(Household hh, int idOldDD, int idNewDD) {
        // if this household had a dwelling in this study area before, vacate old dwelling
        if (idOldDD > 0) {
            dataContainer.getRealEstateDataManager().vacateDwelling(idOldDD);
        }
        try {
            dataContainer.getRealEstateDataManager().removeDwellingFromVacancyList(idNewDD);
        } catch (NullPointerException e){
            logger.warn("eh");
        }
        dataContainer.getRealEstateDataManager().getDwelling(idNewDD).setResidentID(hh.getId());
        hh.setDwelling(idNewDD);
        if (hh.getId() == SiloUtil.trackHh) {
            SiloUtil.trackWriter.println("Household " +
                    hh.getId() + " moved from dwelling " + idOldDD + " to dwelling " + idNewDD + ".");
        }
    }

    private static class UtilityTask {
        private final int id;
        private final Dwelling dwelling;
        private final Household household;

        private UtilityTask(int id, Dwelling dwelling, Household household) {
            this.id = id;
            this.dwelling = dwelling;
            this.household = household;
        }
    }

    private static class UtilityUtils extends Thread {

        /**
         * The queue that each thread listens to to poll for new dwelling evaluations.
         * Tasks are put into the queue from the main thread inside the searchForNewDwelling method
         */
        private static final Queue<UtilityTask> queue = new ConcurrentLinkedQueue<>();

        private static CyclicBarrier barrier;

        /**
         * boolean that is used to stop threads once they're running.
         */
        private static boolean run = false;

        /**
         * static array that is used to store probabilities of dwelling evaluations accessed
         * from different threads
         */
        private static double[] probabilities;

        /**
         * static array that is used to store dwellings of evaluations accessed
         * from different threads
         */
        private static Dwelling[] dwellings;

        /**
         * the strategy for each thread that is used for dwelling evaluation
         */
        private HousingStrategy strategy;

        private UtilityUtils(HousingStrategy strategy) {
            this.strategy = strategy;
        }

        /**
         * reset static probability and dwelling arrays for the next dwelling search.
         * reset the job counter.
         */
        private static void reset() {
            probabilities = new double[MAX_NUMBER_DWELLINGS];
            dwellings = new Dwelling[MAX_NUMBER_DWELLINGS];
        }

        /**
         * start the given number of background threads with the given housing strategy.
         * The strategy will be duplicated for each thread to ensure thread-safety.
         */
        private static void startThreads(HousingStrategy strategy, int threads) {
            run = true;
            barrier = new CyclicBarrier(threads +1);
            for (int i = 0; i < threads; i++) {
                UtilityUtils thread = new UtilityUtils(strategy.duplicate());
                thread.setDaemon(true);
                thread.start();
            }
        }

        /**
         * stop the background threads from running
         */
        private static void endYear() {
            run = false;
        }

        /**
         * when running, a thread polls the queue for utility tasks. for each task
         * a dwelling has to be evaluated. after the evaluation, the probabilities
         * are stored in the static array that is shared among the threads and the
         * jobcounter is incremented
         */
        @Override
        public void run() {
            while (run) {
                final UtilityTask poll = queue.poll();
                if (poll != null) {
                    final Dwelling dwelling = poll.dwelling;
                    final double util = strategy.calculateHousingUtility(poll.household, dwelling);
                    double probability = strategy.calculateSelectDwellingProbability(util);
                    final int i = poll.id;
                    probabilities[i] = probability;
                    dwellings[i] = dwelling;
                } else {
                    try {
                        barrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public Map<Integer, Integer> getHouseholdsByZone() {
        return householdsByZone;
    }

    public Map<Integer, Double> getSumOfSatisfactionsByZone() {
        return sumOfSatisfactionsByZone;
    }
}
