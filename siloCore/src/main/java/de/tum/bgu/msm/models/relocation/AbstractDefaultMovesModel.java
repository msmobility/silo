package de.tum.bgu.msm.models.relocation;

import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdType;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.household.IncomeCategory;
import de.tum.bgu.msm.events.impls.household.MoveEvent;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractDefaultMovesModel extends AbstractModel implements MovesModelI {

    protected final static Logger LOGGER = Logger.getLogger(AbstractDefaultMovesModel.class);

    protected final GeoData geoData;
    protected final Accessibility accessibility;

    private double[] averageHousingSatisfaction;
    private MovesOrNotJSCalculator movesOrNotJSCalculator;

    protected DwellingUtilityJSCalculator dwellingUtilityJSCalculator;

    protected int year;

    public AbstractDefaultMovesModel(SiloDataContainer dataContainer, Accessibility accessibility) {
        super(dataContainer);
        this.geoData = dataContainer.getGeoData();
        this.accessibility = accessibility;
        setupMoveOrNotMove();
        setupEvaluateDwellings();
        setupSelectRegionModel();
        setupSelectDwellingModel();
    }

    protected abstract void setupSelectRegionModel();
    protected abstract void setupSelectDwellingModel();

    protected abstract double calculateDwellingUtilityForHouseholdType(HouseholdType hhType, Dwelling dwelling);
    protected abstract double personalizeDwellingUtilityForThisHousehold(Household household, Dwelling dwelling, int income, double genericUtility);


    @Override
    public EnumMap<HouseholdType,Double> updateUtilitiesOfVacantDwelling(Dwelling dd) {
        // Calculate utility of this dwelling for each household type
        EnumMap<HouseholdType,Double> utilitiesByHouseholdType = new EnumMap<>(HouseholdType.class);
        for (HouseholdType ht : HouseholdType.values()) {
            utilitiesByHouseholdType.put(ht, calculateDwellingUtilityForHouseholdType(ht,  dd));
        }
        return utilitiesByHouseholdType;
    }

    @Override
    public List<MoveEvent> prepareYear(int year) {
        this.year = year;
        final List<MoveEvent> events = new ArrayList<>();
        for (Household hh : dataContainer.getHouseholdData().getHouseholds()) {
            events.add(new MoveEvent(hh.getId()));
        }
        return events;
    }

    @Override
    public boolean handleEvent(MoveEvent event) {

        // simulates (a) if this household moves and (b) where this household moves

        int hhId = event.getHouseholdId();
        Household household = dataContainer.getHouseholdData().getHouseholdFromId(hhId);
        if (household == null) {
            return false;  // Household does not exist anymore
        }
        if (!moveOrNot(household)) {
            return false;                                                             // Step 1: Consider relocation if household is not very satisfied or if household income exceed restriction for low-income dwelling
        }
        int idNewDD = searchForNewDwelling(household);  // Step 2: Choose new dwelling
        if (idNewDD > 0) {
            moveHousehold(household, household.getDwellingId(), idNewDD);    // Step 3: Move household
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
        }
        return false;
    }

    @Override
    public void finishYear(int year) {
    }

    private void setupEvaluateDwellings() {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("DwellingUtilityCalc"));
        dwellingUtilityJSCalculator = new DwellingUtilityJSCalculator(reader);
    }

    private void setupMoveOrNotMove() {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("MovesOrNotCalc"));
        movesOrNotJSCalculator = new MovesOrNotJSCalculator(reader);
    }

    /**
     * Walks through each dwelling and evaluate utility of current resident. For vacant dwellings, expected utility
     * for each household type is calculated.
     */
    private void evaluateAllDwellingUtilities() {
        LOGGER.info("  Evaluating utility of dwellings for current residents and utility of vacant dwellings for all " +
                "household types");

        HouseholdDataManager householdData = dataContainer.getHouseholdData();
        for (Dwelling dd : dataContainer.getRealEstateData().getDwellings()) {
            if (dd.getResidentId() == -1) {
                // dwelling is vacant, evaluate for all household types
                EnumMap<HouseholdType, Double> utilitiesByHhtype = updateUtilitiesOfVacantDwelling(dd);
                dd.setUtilitiesByHouseholdType(utilitiesByHhtype);
            } else {
                // dwelling is occupied, evaluate for the current household
                Household hh = householdData.getHouseholdFromId(dd.getResidentId());
                double util = calculateDwellingUtilityForHouseholdType(hh.getHouseholdType(), dd);
                util = personalizeDwellingUtilityForThisHousehold(hh, dd, HouseholdUtil.getHhIncome(hh), util);
                dd.setUtilOfResident(util);
            }
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
        double currentUtil = dd.getUtilOfResident();

        double[] prop = new double[2];

        prop[0] = movesOrNotJSCalculator.getMovingProbability(averageHousingSatisfaction[hhType.ordinal()], currentUtil);
        prop[1] = 1. - prop[0];

        return SiloUtil.select(prop) == 0;
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

    @Override
    public void calculateAverageHousingSatisfaction() {
        evaluateAllDwellingUtilities();
        averageHousingSatisfaction = new double[HouseholdType.values().length];
        int[] hhCountyByType = new int[HouseholdType.values().length];
        for (Household hh : dataContainer.getHouseholdData().getHouseholds()) {
            double util = dataContainer.getRealEstateData().getDwelling(hh.getDwellingId()).getUtilOfResident();
            int count = hh.getHouseholdType().ordinal();
            averageHousingSatisfaction[count] += util;
            hhCountyByType[count]++;
        }
        for (int hhType = 0; hhType < HouseholdType.values().length; hhType++) {
            averageHousingSatisfaction[hhType] = averageHousingSatisfaction[hhType] / (1. * hhCountyByType[hhType]);
        }
    }

    @Override
    public void moveHousehold(Household hh, int idOldDD, int idNewDD) {
        // if this household had a dwelling in this study area before, vacate old dwelling
        if (idOldDD > 0) {
            dataContainer.getRealEstateData().vacateDwelling(idOldDD);
        }
        dataContainer.getRealEstateData().removeDwellingFromVacancyList(idNewDD);
        hh.setDwelling(idNewDD);
        dataContainer.getRealEstateData().getDwelling(idNewDD).setResidentID(hh.getId());
        if (hh.getId() == SiloUtil.trackHh) {
            SiloUtil.trackWriter.println("Household " +
                    hh.getId() + " moved from dwelling " + idOldDD + " to dwelling " + idNewDD + ".");
        }
    }
}
