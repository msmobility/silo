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
import de.tum.bgu.msm.events.impls.household.MoveEvent;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.transportModel.matsim.MatsimTravelTimes;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.concurrent.ConcurrentExecutor;
import de.tum.bgu.msm.utils.SampleException;
import de.tum.bgu.msm.utils.Sampler;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.commons.math3.util.Precision;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Nico
 * Default implementation of the {@link MovesModel} interface. Implements general dwelling search
 * and move logic based on housing satisfaction/utility. The actual utility calculations are defined
 * in the {@link HousingStrategy} argument.
 */
public class MovesModelImpl extends AbstractModel implements MovesModel {

    protected final static Logger logger = Logger.getLogger(MovesModelImpl.class);
    private static final int MAX_NUMBER_DWELLINGS = 20;

    private final MovesStrategy movesStrategy;
    private final HousingStrategy housingStrategy;

    private final boolean threaded;

    private final Map<HouseholdType, Double> averageHousingSatisfaction = new ConcurrentHashMap<>();
    private final Map<Integer, Double> satisfactionByHousehold = new ConcurrentHashMap<>();

    public MovesModelImpl(DataContainer dataContainer, Properties properties, MovesStrategy movesStrategy, HousingStrategy housingStrategy) {
        super(dataContainer, properties);
        this.movesStrategy = movesStrategy;
        this.housingStrategy = housingStrategy;
        this.threaded = dataContainer.getTravelTimes() instanceof MatsimTravelTimes;
    }

    @Override
    public void setup() {
        housingStrategy.setup();
    }

    @Override
    public void prepareYear(int year) {
        housingStrategy.prepareYear();
        calculateAverageHousingUtility();
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
            logger.info("Started " + threads + " background threads for dwelling utility evaluation");
        }

        return events;
    }

    @Override
    public void endYear(int year) {
        UtilityUtils.endYear();
    }

    @Override
    public void endSimulation() {

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

        // Step 1: Consider relocation if household is not very satisfied or if household income exceed restriction for low-income dwelling
        if (!moveOrNot(household)) {
            return false;
        }

        // Step 2: Choose new dwelling
        int idNewDD = searchForNewDwelling(household);

        if (idNewDD > 0) {

            // Step 3: Move household
            dataContainer.getHouseholdDataManager().saveHouseholdMemento(household);
            moveHousehold(household, household.getDwellingId(), idNewDD);
            if (hhId == SiloUtil.trackHh) {
                SiloUtil.trackWriter.println("Household " + hhId + " has moved to dwelling " +
                        household.getDwellingId());
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

    @Override
    public int searchForNewDwelling(Household household) {

        // Step 1: select region
        final GeoData geoData = dataContainer.getGeoData();
        Sampler<Region> regionSampler = new Sampler<>(geoData.getRegions().size(), Region.class, SiloUtil.getRandomObject());
        for (Region region : geoData.getRegions().values()) {
            final double utility = housingStrategy.calculateRegionalUtility(household, region);
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

        Collections.shuffle(vacantDwellings, SiloUtil.getRandomObject());
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
            while (true) {
                if (UtilityUtils.counter.get() == maxNumberOfDwellings) {
                    break;
                }
            }
        }

        Sampler<Dwelling> sampler = new Sampler<>(UtilityUtils.dwellings, UtilityUtils.probabilities, SiloUtil.getRandomObject());
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
        return SiloUtil.getRandomNumberAsDouble() <= prop;
    }


    private void calculateAverageHousingUtility() {
        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        logger.info("Evaluating average housing utility of " + householdDataManager.getHouseholds().size() + " households.");
        satisfactionByHousehold.clear();
        averageHousingSatisfaction.replaceAll((householdType, aDouble) -> 0.);

        final Collection<Household> households = householdDataManager.getHouseholds();
        final int partitionSize = (int) ((double) households.size() / (Properties.get().main.numberOfThreads - 1)) + 1;
        Iterable<List<Household>> partitions = Iterables.partition(households, partitionSize);
        ConcurrentExecutor<Void> executor = ConcurrentExecutor.fixedPoolService(Properties.get().main.numberOfThreads - 1);
        ConcurrentHashMultiset<HouseholdType> hhByType = ConcurrentHashMultiset.create();

        logger.info("Using " + Properties.get().main.numberOfThreads + " threads" +
                " with partitions of size " + partitionSize);

        for (final List<Household> partition : partitions) {
            executor.addTaskToQueue(() -> {
                try {
                    HousingStrategy strategy = housingStrategy.duplicate();
                    for (Household hh : partition) {
                        final HouseholdType householdType = hh.getHouseholdType();
                        hhByType.add(householdType);
                        Dwelling dd = dataContainer.getRealEstateDataManager().getDwelling(hh.getDwellingId());
                        final double util = strategy.calculateHousingUtility(hh, dd);
                        satisfactionByHousehold.put(hh.getId(), util);
                        averageHousingSatisfaction.merge(householdType, util, (oldUtil, newUtil) -> oldUtil + newUtil);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
        dataContainer.getRealEstateDataManager().removeDwellingFromVacancyList(idNewDD);
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

        private static final Queue<UtilityTask> queue = new ConcurrentLinkedQueue<>();
        private static boolean run = false;

        private final static AtomicInteger counter = new AtomicInteger(0);

        private static double[] probabilities;
        private static Dwelling[] dwellings;

        private HousingStrategy strategy;

        private UtilityUtils(HousingStrategy strategy) {
            this.strategy = strategy;
        }

        private static void reset() {
            probabilities = new double[MAX_NUMBER_DWELLINGS];
            dwellings = new Dwelling[MAX_NUMBER_DWELLINGS];
            counter.set(0);
        }

        private static void startThreads(HousingStrategy strategy, int threads) {
            run = true;
            for (int i = 0; i < threads; i++) {
                new UtilityUtils(strategy.duplicate()).start();
            }
        }

        private static void endYear() {
            run = false;
        }

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
                    counter.incrementAndGet();
                }
            }
        }
    }
}