package de.tum.bgu.msm.models.relocation;

import com.google.common.collect.EnumMultiset;
import de.tum.bgu.msm.container.SiloDataContainerImpl;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdType;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.household.IncomeCategory;
import de.tum.bgu.msm.events.impls.household.MoveEvent;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.data.accessibility.Accessibility;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractDefaultMovesModel extends AbstractModel implements MovesModelI {

    protected final static Logger logger = Logger.getLogger(AbstractDefaultMovesModel.class);

    protected final GeoData geoData;
    protected final Accessibility accessibility;

    private final EnumMap<HouseholdType, Double> averageHousingSatisfaction = new EnumMap<>(HouseholdType.class);
    private final Map<Integer, Double> satisfactionByHousehold = new HashMap<>();

    private MovesOrNotJSCalculator movesOrNotJSCalculator;

    public AbstractDefaultMovesModel(SiloDataContainerImpl dataContainer, Accessibility accessibility, Properties properties) {
        super(dataContainer, properties);
        this.geoData = dataContainer.getGeoData();
        this.accessibility = accessibility;
        setupMoveOrNotMove();
        setupSelectRegionModel();
    }

    protected abstract void setupSelectRegionModel();


    private void setupMoveOrNotMove() {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("MovesOrNotCalc"));
        movesOrNotJSCalculator = new MovesOrNotJSCalculator(reader);
    }

    protected abstract double calculateHousingUtility(Household hh, Dwelling dwelling);

    protected abstract void calculateRegionalUtilities();


    @Override
    public List<MoveEvent> prepareYear(int year) {
        calculateRegionalUtilities();
        calculateAverageHousingUtility();
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
        return (float) quality / (float) properties.main.qualityLevels;
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


    private void calculateAverageHousingUtility() {
        logger.info("Evaluate average housing utility.");
        HouseholdDataManager householdData = dataContainer.getHouseholdData();

        averageHousingSatisfaction.replaceAll((householdType, aDouble) -> 0.);
        satisfactionByHousehold.clear();

        EnumMultiset<HouseholdType> hhByType = EnumMultiset.create(HouseholdType.class);
        for (Household hh : householdData.getHouseholds()) {
            final HouseholdType householdType = hh.getHouseholdType();
            Dwelling dd = dataContainer.getRealEstateData().getDwelling(hh.getDwellingId());
            double util = calculateHousingUtility(hh, dd);
            satisfactionByHousehold.put(dd.getResidentId(), util);
            averageHousingSatisfaction.merge(householdType, util, (oldUtil, newUtil) -> oldUtil + newUtil);
            hhByType.add(householdType);
        }
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
