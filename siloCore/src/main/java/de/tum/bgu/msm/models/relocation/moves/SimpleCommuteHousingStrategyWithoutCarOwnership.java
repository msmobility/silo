package de.tum.bgu.msm.models.relocation.moves;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.accessibility.Accessibility;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManagerImpl;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdType;
import de.tum.bgu.msm.data.household.IncomeCategory;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.models.modeChoice.CommuteModeChoice;
import de.tum.bgu.msm.models.modeChoice.CommuteModeChoiceMapping;
import de.tum.bgu.msm.models.modeChoice.CommuteModeChoiceWithoutCarOwnership;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix1D;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static de.tum.bgu.msm.data.dwelling.RealEstateUtils.RENT_CATEGORIES;

public class SimpleCommuteHousingStrategyWithoutCarOwnership implements HousingStrategy {

    private final static Logger logger = Logger.getLogger(SimpleCommuteHousingStrategyWithoutCarOwnership.class);

    private enum Normalizer {
        /**
         * Use share of empty dwellings to calculate attraction of region
         */
        SHARE_VAC_DD,
        /**
         * Multiply utility of every region by number of vacant dwellings
         * to steer households towards available dwellings
         * use number of  vacant dwellings to calculate attraction of region
         */
        VAC_DD,
        DAMPENED_VAC_RATE,
        POPULATION,
        POWER_OF_POPULATION
    }

    private static final Normalizer NORMALIZER = Normalizer.VAC_DD;

    private final Properties properties;

    private IndexedDoubleMatrix1D hhByRegion;

    private final DataContainer dataContainer;
    private final RealEstateDataManager realEstateDataManager;
    private final GeoData geoData;
    private final TravelTimes travelTimes;
    private final Accessibility accessibility;

    private final CommuteModeChoice commuteModeChoice;

    private final DwellingUtilityStrategy dwellingUtilityStrategy;
    private final DwellingProbabilityStrategy dwellingProbabilityStrategy;

    private final RegionUtilityStrategy regionUtilityStrategy;
    private final RegionProbabilityStrategy regionProbabilityStrategy;

    private EnumMap<IncomeCategory, Map<Integer, Double>> utilityByIncomeByRegion = new EnumMap<>(IncomeCategory.class);

    public SimpleCommuteHousingStrategyWithoutCarOwnership(DataContainer dataContainer,
                                                           Properties properties,
                                                           TravelTimes travelTimes,
                                                           DwellingUtilityStrategy dwellingUtilityStrategy,
                                                           DwellingProbabilityStrategy dwellingProbabilityStrategy,
                                                           RegionUtilityStrategy regionUtilityStrategy,
                                                           RegionProbabilityStrategy regionProbabilityStrategy) {
        this.dataContainer = dataContainer;
        geoData = dataContainer.getGeoData();
        this.properties = properties;
        this.travelTimes = travelTimes;
        accessibility = dataContainer.getAccessibility();
        this.commuteModeChoice = new CommuteModeChoiceWithoutCarOwnership(dataContainer, properties, SiloUtil.provideNewRandom());
        this.dwellingUtilityStrategy = dwellingUtilityStrategy;
        this.dwellingProbabilityStrategy = dwellingProbabilityStrategy;
        this.regionUtilityStrategy = regionUtilityStrategy;
        this.realEstateDataManager = dataContainer.getRealEstateDataManager();
        this.regionProbabilityStrategy = regionProbabilityStrategy;
    }

    // TODO When consolidated, try to get rid of this second constructor
    public SimpleCommuteHousingStrategyWithoutCarOwnership(DataContainer dataContainer,
                                                           Properties properties,
                                                           TravelTimes travelTimes,
                                                           DwellingUtilityStrategy dwellingUtilityStrategy,
                                                           DwellingProbabilityStrategy dwellingProbabilityStrategy,
                                                           RegionUtilityStrategy regionUtilityStrategy, RegionProbabilityStrategy regionProbabilityStrategy,
                                                           CommuteModeChoice commuteModeChoice) {
        this.dataContainer = dataContainer;
        geoData = dataContainer.getGeoData();
        this.properties = properties;
        this.travelTimes = travelTimes;
        accessibility = dataContainer.getAccessibility();
        this.commuteModeChoice = commuteModeChoice;
        this.dwellingUtilityStrategy = dwellingUtilityStrategy;
        this.dwellingProbabilityStrategy = dwellingProbabilityStrategy;
        this.regionUtilityStrategy = regionUtilityStrategy;
        this.realEstateDataManager = dataContainer.getRealEstateDataManager();
        this.regionProbabilityStrategy = regionProbabilityStrategy;
    }


    @Override
    public void setup() {
        hhByRegion = new IndexedDoubleMatrix1D(geoData.getRegions().values());
        calculateShareOfForeignersByZoneAndRegion();
    }

    @Override
    public boolean isHouseholdEligibleToLiveHere(Household household, Dwelling dd) {
        return true;
    }

    @Override
    public double calculateHousingUtility(Household hh, Dwelling dwelling) {
        if(dwelling == null) {
            logger.warn("Household " + hh.getId() + " has no dwelling. Setting housing satisfaction to 0");
            return 0;
        }
        double ddQualityUtility = convertQualityToUtility(dwelling.getQuality());
        double ddSizeUtility = convertAreaToUtility(dwelling.getBedrooms());
        double ddAutoAccessibilityUtility = convertAccessToUtility(accessibility.getAutoAccessibilityForZone(geoData.getZones().get(dwelling.getZoneId())));
        double transitAccessibilityUtility = convertAccessToUtility(accessibility.getTransitAccessibilityForZone(geoData.getZones().get(dwelling.getZoneId())));
        HouseholdType ht = hh.getHouseholdType();
        double ddPriceUtility = convertPriceToUtility(dwelling.getPrice(), ht.getIncomeCategory());

        double workDistanceUtility = 1;

        CommuteModeChoiceMapping commuteModeChoiceMapping = commuteModeChoice.assignCommuteModeChoice(dwelling, travelTimes, hh);
        hh.setAttribute("COMMUTE_MODE_CHOICE_MAPPING", commuteModeChoiceMapping);

        for (Person pp : hh.getPersons().values()) {
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {

                workDistanceUtility *= commuteModeChoiceMapping.getMode(pp).utility;

            }
        }
        return dwellingUtilityStrategy.calculateSelectDwellingUtility(ht, ddSizeUtility, ddPriceUtility,
                ddQualityUtility, ddAutoAccessibilityUtility,
                transitAccessibilityUtility, workDistanceUtility);
    }

    @Override
    public double calculateSelectDwellingProbability(double util) {
        return dwellingProbabilityStrategy.calculateSelectDwellingProbability(util);
    }

    @Override
    public double calculateSelectRegionProbability(double util) {
        return regionProbabilityStrategy.calculateSelectRegionProbability(util);
    }

    @Override
    public void prepareYear() {
        calculateShareOfForeignersByZoneAndRegion();
        calculateRegionalUtilities();
    }

    @Override
    public double calculateRegionalUtility(Household household, Region region) {
        double thisRegionFactor = 1;
        CommuteModeChoiceMapping commuteModeChoiceMapping = commuteModeChoice.assignRegionalCommuteModeChoice(region, travelTimes, household);

        for (Person pp : household.getPersons().values()) {
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {
                thisRegionFactor *= commuteModeChoiceMapping.getMode(pp).utility;
            }
        }

        double util = utilityByIncomeByRegion.get(household.getHouseholdType().getIncomeCategory()).get(region.getId()) * thisRegionFactor;
        return normalize(region, util);
    }

    @Override
    public HousingStrategy duplicate() {
        TravelTimes ttCopy = travelTimes.duplicate();
        SimpleCommuteHousingStrategyWithoutCarOwnership strategy = new SimpleCommuteHousingStrategyWithoutCarOwnership(dataContainer, properties, ttCopy,
                dwellingUtilityStrategy, dwellingProbabilityStrategy, regionUtilityStrategy, regionProbabilityStrategy, commuteModeChoice);
        strategy.hhByRegion = hhByRegion;
        strategy.utilityByIncomeByRegion = utilityByIncomeByRegion;
        return strategy;
    }

    private void calculateShareOfForeignersByZoneAndRegion() {
        final IndexedDoubleMatrix1D hhByZone = new IndexedDoubleMatrix1D(geoData.getZones().values());
        hhByRegion.assign(0);
        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()) {
            int zone;
            Dwelling dwelling = dataContainer.getRealEstateDataManager().getDwelling(hh.getDwellingId());
            if (dwelling != null) {
                zone = dwelling.getZoneId();
            } else {
                logger.warn("Household " + hh.getId() + " refers to non-existing dwelling "
                        + hh.getDwellingId() + ". Should not happen!");
                continue;
            }
            final int region = geoData.getZones().get(zone).getRegion().getId();
            hhByZone.setIndexed(zone, hhByZone.getIndexed(zone) + 1);
            hhByRegion.setIndexed(region, hhByRegion.getIndexed(region) + 1);

        }
    }

    private void calculateRegionalUtilities() {
        logger.info("Calculating regional utilities");
        final Map<Integer, Double> rentsByRegion = dataContainer.getRealEstateDataManager().calculateRegionalPrices();
        for (IncomeCategory incomeCategory : IncomeCategory.values()) {
            Map<Integer, Double> utilityByRegion = new HashMap<>();
            for (Region region : geoData.getRegions().values()) {
                final int averageRegionalRent = rentsByRegion.get(region.getId()).intValue();
                final float regAcc = (float) convertAccessToUtility(accessibility.getRegionalAccessibility(region));
                float priceUtil = (float) convertPriceToUtility(averageRegionalRent, incomeCategory);
                double value = regionUtilityStrategy.calculateSelectRegionProbability(incomeCategory,
                        priceUtil, regAcc, 0);
                switch (NORMALIZER) {
                    case POPULATION:
                        value *= hhByRegion.getIndexed(region.getId());
                        break;
                    case POWER_OF_POPULATION:
                        value *= Math.pow(hhByRegion.getIndexed(region.getId()), 0.5);
                        break;
                    default:
                        //do nothing.
                }
                utilityByRegion.put(region.getId(),
                        value);
            }

            utilityByIncomeByRegion.put(incomeCategory, utilityByRegion);
        }

    }

    private double normalize(Region region, double baseUtil) {
        switch (NORMALIZER) {
            case VAC_DD: {
                return baseUtil * realEstateDataManager.getNumberOfVacantDDinRegion(region.getId());
            }
            case DAMPENED_VAC_RATE: {
                int key = region.getId();
                double x = (double) realEstateDataManager.getNumberOfVacantDDinRegion(key) /
                        (double) realEstateDataManager.getNumberOfVacantDDinRegion(key) * 100d;  // % vacancy
                double y = 1.4186E-03 * Math.pow(x, 3) - 6.7846E-02 * Math.pow(x, 2) + 1.0292 * x + 4.5485E-03;
                y = Math.min(5d, y);                                                // % vacancy assumed to be ready to move in
                if (realEstateDataManager.getNumberOfVacantDDinRegion(key) < 1) {
                    return 0.;
                }
                return baseUtil * (y / 100d * realEstateDataManager.getNumberOfVacantDDinRegion(key));

            }
            case POPULATION: {
                //Is already included in the base util
                return baseUtil;
            }
            case POWER_OF_POPULATION: {
                //Is already included in the base util
                return baseUtil;
            }
            default:
                return baseUtil;
        }
    }

    private double convertPriceToUtility(int price, IncomeCategory incCategory) {

        Map<Integer, Float> shares = dataContainer.getRealEstateDataManager().getRentPaymentsForIncomeGroup(incCategory);
        // 25 rent categories are defined as <rent/200>, see RealEstateDataManager
        int priceCategory = (int) (price / 200f);
        priceCategory = Math.min(priceCategory, RENT_CATEGORIES);
        double util = 0;
        for (int i = 0; i <= priceCategory; i++) {
            util += shares.get(i);
        }
        // invert utility, as lower price has higher utility
        return Math.max(0, 1.f - util);
    }

    private double convertQualityToUtility(int quality) {
        return (float) quality / (float) properties.main.qualityLevels;
    }

    private double convertAreaToUtility(int area) {
        return (float) area / (float) RealEstateDataManagerImpl.largestNoBedrooms;
    }

    private double convertAccessToUtility(double accessibility) {
        return accessibility / 100f;
    }
}
