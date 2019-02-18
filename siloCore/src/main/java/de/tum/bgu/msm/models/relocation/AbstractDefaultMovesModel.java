package de.tum.bgu.msm.models.relocation;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Iterables;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdType;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.household.IncomeCategory;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.events.impls.household.MoveEvent;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.transportModel.matsim.MatsimTravelTimes;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.concurrent.ConcurrentExecutor;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class AbstractDefaultMovesModel extends AbstractModel implements MovesModelI {

    protected final static Logger logger = Logger.getLogger(AbstractDefaultMovesModel.class);

    protected final GeoData geoData;
    protected final Accessibility accessibility;

    private final Map<HouseholdType, Double> averageHousingSatisfaction = new EnumMap<>(HouseholdType.class);
    private final Map<Integer, Double> satisfactionByHousehold = new ConcurrentHashMap<>();

    private MovesOrNotJSCalculator movesOrNotJSCalculator;

    public AbstractDefaultMovesModel(SiloDataContainer dataContainer, Accessibility accessibility) {
        super(dataContainer);
        this.geoData = dataContainer.getGeoData();
        this.accessibility = accessibility;
        setupMoveOrNotMove();
        setupSelectDwellingModel();
    }

    protected abstract void setupSelectDwellingModel();

    protected abstract void calculateRegionalUtilities();

    /**
     * Calculates housing satisfaction for a household. Must be thread-safe!
     * A thread-safe traveltimes reference is provided.
     * @return The satisfaction / utility the given household gets from living in the given dwelling.
     */
    protected abstract double calculateHousingSatisfaction(Household household, Dwelling dwelling, TravelTimes travelTimes);

    private void setupMoveOrNotMove() {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("MovesOrNotCalc"));
        movesOrNotJSCalculator = new MovesOrNotJSCalculator(reader);
    }

    @Override
    public List<MoveEvent> prepareYear(int year) {
        calculateRegionalUtilities();
        calculateAverageHousingSatisfaction();

        final List<MoveEvent> events = new ArrayList<>();
        for (Household hh : dataContainer.getHouseholdData().getHouseholds()) {
            events.add(new MoveEvent(hh.getId()));
        }
        return events;
    }

    @Override
    public void finishYear(int year) {
    }


    /**
     * Simulates (a) if this household moves and (b) where this household moves
     */
    @Override
    public boolean handleEvent(MoveEvent event) {

        int hhId = event.getHouseholdId();
        Household household = dataContainer.getHouseholdData().getHouseholdFromId(hhId);
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
            moveHousehold(household, household.getDwellingId(), idNewDD);
            dataContainer.getHouseholdData().addHouseholdThatMoved(household);
            if (hhId == SiloUtil.trackHh) {
                SiloUtil.trackWriter.println("Household " + hhId + " has moved to dwelling " +
                        household.getDwellingId());
            }
            return true;
        } else {
            if (hhId == SiloUtil.trackHh)
                SiloUtil.trackWriter.println("Household " + hhId + " intended to move but " +
                        "could not find an adequate dwelling.");
            return false;
        }
    }


    protected double convertPriceToUtility(int price, HouseholdType ht) {

        IncomeCategory incCategory = ht.getIncomeCategory();
        Map<Integer, Float> shares = RealEstateDataManager.getRentPaymentsForIncomeGroup(incCategory);
        // 25 rent categories are defined as <rent/200>, see RealEstateDataManager
        int priceCategory = (int) (price / 200f + 0.5);
        priceCategory = Math.min(priceCategory, RealEstateDataManager.rentCategories);
        double util = 0;
        for (int i = 0; i <= priceCategory; i++) {
            util += shares.get(i);
        }
        // invert utility, as lower price has higher utility
        return (1f - util);
    }

    protected double convertPriceToUtility(int price, IncomeCategory incCategory) {

        Map<Integer, Float> shares = RealEstateDataManager.getRentPaymentsForIncomeGroup(incCategory);
        // 25 rent categories are defined as <rent/200>, see RealEstateDataManager
        int priceCategory = (int) (price / 200f);
        priceCategory = Math.min(priceCategory, RealEstateDataManager.rentCategories);
        double util = 0;
        for (int i = 0; i <= priceCategory; i++) {
            util += shares.get(i);
        }
        // invert utility, as lower price has higher utility
        return (1f - util);
    }

    protected double convertQualityToUtility(int quality) {
        return (float) quality / (float) Properties.get().main.qualityLevels;
    }

    protected double convertAreaToUtility(int area) {
        return (float) area / (float) RealEstateDataManager.largestNoBedrooms;
    }

    protected double convertAccessToUtility(double accessibility) {
        return accessibility / 100f;
    }

    protected Map<Integer, Double> calculateRegionalPrices() {
        final Map<Integer, Zone> zones = geoData.getZones();
        final Map<Integer, List<Dwelling>> dwellingsByRegion =
                dataContainer.getRealEstateData().getDwellings().parallelStream().collect(Collectors.groupingByConcurrent(d ->
                        zones.get(d.getZoneId()).getRegion().getId()));
        final Map<Integer, Double> rentsByRegion = dwellingsByRegion.entrySet().parallelStream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().mapToDouble(Dwelling::getPrice).average().getAsDouble()));
        return rentsByRegion;
    }

    protected boolean moveOrNot(Household household) {
        HouseholdType hhType = household.getHouseholdType();
        Dwelling dd = dataContainer.getRealEstateData().getDwelling(household.getDwellingId());
        if (!isHouseholdEligibleToLiveHere(household, dd)) {
            return true;
        }
        final double currentUtil = satisfactionByHousehold.get(household.getId());
        final double avgSatisfaction = averageHousingSatisfaction.getOrDefault(hhType, currentUtil);
        final double prop = movesOrNotJSCalculator.getMovingProbability(avgSatisfaction, currentUtil);
        return SiloUtil.getRandomNumberAsDouble() <= prop;
    }

    private boolean isHouseholdEligibleToLiveHere(Household hh, Dwelling dd) {
        // Check if dwelling is restricted, if so check if household is still eligible to live in this dwelling (household income could exceed eligibility criterion)
        if (dd.getRestriction() <= 0) {
            // Dwelling is not income restricted
            return true;
        }
        int msa = geoData.getZones().get(dd.getZoneId()).getMsa();
        return HouseholdUtil.getHhIncome(hh) <= (HouseholdDataManager.getMedianIncome(msa) * dd.getRestriction());
    }

    private void calculateAverageHousingSatisfaction() {
        logger.info("Evaluating housing satisfaction.");
        HouseholdDataManager householdData = dataContainer.getHouseholdData();

        averageHousingSatisfaction.replaceAll((householdType, aDouble) -> 0.);
        satisfactionByHousehold.clear();

        final Collection<Household> households = householdData.getHouseholds();
        final int partitionSize = (int) ((double) households.size() / (Properties.get().main.numberOfThreads)) + 1;
        Iterable<List<Household>> partitions = Iterables.partition(households, partitionSize);
        ConcurrentExecutor<Void> executor =
                ConcurrentExecutor.fixedPoolService(Properties.get().main.numberOfThreads);
        ConcurrentHashMultiset<HouseholdType> hhByType = ConcurrentHashMultiset.create();

        for (final List<Household> partition : partitions) {
            executor.addTaskToQueue(() -> {
                TravelTimes travelTimes = null;
                if(dataContainer.getTravelTimes() instanceof SkimTravelTimes) {
                    travelTimes = dataContainer.getTravelTimes();
                } else if (dataContainer.getTravelTimes() instanceof MatsimTravelTimes){
                    travelTimes = ((MatsimTravelTimes)dataContainer.getTravelTimes()).duplicate();
                }
                for (Household hh : partition) {
                    final HouseholdType householdType = hh.getHouseholdType();
                    hhByType.add(householdType);
                    Dwelling dd = dataContainer.getRealEstateData().getDwelling(hh.getDwellingId());
                    final double util = calculateHousingSatisfaction(hh, dd, travelTimes);
                    satisfactionByHousehold.put(hh.getId(), util);
                    averageHousingSatisfaction.merge(householdType, util, (oldUtil, newUtil) -> oldUtil + newUtil);
                }
                return null;
            });
        }
        executor.execute();

        averageHousingSatisfaction.replaceAll((householdType, satisfaction) ->
                satisfaction / (1. * hhByType.count(householdType)));
    }


    @Override
    public void moveHousehold(Household hh, int idOldDD, int idNewDD) {
        // if this household had a dwelling in this study area before, vacate old dwelling
        if (idOldDD > 0) {
            dataContainer.getRealEstateData().vacateDwelling(idOldDD);
        }
        dataContainer.getRealEstateData().removeDwellingFromVacancyList(idNewDD);
        dataContainer.getRealEstateData().getDwelling(idNewDD).setResidentID(hh.getId());
        hh.setDwelling(idNewDD);
        if (hh.getId() == SiloUtil.trackHh) {
            SiloUtil.trackWriter.println("Household " +
                    hh.getId() + " moved from dwelling " + idOldDD + " to dwelling " + idNewDD + ".");
        }
    }
}
