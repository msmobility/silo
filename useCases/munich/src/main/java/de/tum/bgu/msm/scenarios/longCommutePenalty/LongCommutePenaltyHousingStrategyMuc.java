package de.tum.bgu.msm.scenarios.longCommutePenalty;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.accessibility.Accessibility;
import de.tum.bgu.msm.data.accessibility.CommutingTimeProbability;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManagerImpl;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.household.*;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.job.JobMuc;
import de.tum.bgu.msm.data.person.Nationality;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.data.vehicle.VehicleType;
import de.tum.bgu.msm.models.modeChoice.CommuteModeChoice;
import de.tum.bgu.msm.models.modeChoice.CommuteModeChoiceMapping;
import de.tum.bgu.msm.models.modeChoice.SimpleCommuteModeChoice;
import de.tum.bgu.msm.models.relocation.DwellingUtilityStrategy;
import de.tum.bgu.msm.models.relocation.HousingStrategyMuc;
import de.tum.bgu.msm.models.relocation.RegionUtilityStrategyMuc;
import de.tum.bgu.msm.models.relocation.moves.DwellingProbabilityStrategy;
import de.tum.bgu.msm.models.relocation.moves.HousingStrategy;
import de.tum.bgu.msm.models.relocation.moves.RegionProbabilityStrategy;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix1D;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.LongAdder;

import static de.tum.bgu.msm.data.dwelling.RealEstateUtils.RENT_CATEGORIES;

public class LongCommutePenaltyHousingStrategyMuc implements HousingStrategy {
    private final static Logger logger = LogManager.getLogger(HousingStrategyMuc.class);


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
        POWER_OF_POPULATION;
    }

    private static final Normalizer NORMALIZER = Normalizer.VAC_DD;

    private static final int PENALTY_EUR = 200;
    private final int TIME_THRESHOLD_M = 25;
    private final double UTILITY_THRESHOLD = Math.exp(-0.2 * 25);

    private Map<Integer, Double> rentsByRegion;

    private final DataContainer dataContainer;
    private final Properties properties;
    private final CommutingTimeProbability commutingTimeProbability;
    private final Accessibility accessibility;
    private final GeoData geoData;
    private final RealEstateDataManager realEstateDataManager;

    private final TravelTimes travelTimes;

    private final DwellingProbabilityStrategy dwellingProbabilityStrategy;
    private final DwellingUtilityStrategy dwellingUtilityStrategy;

    private final RegionUtilityStrategyMuc regionUtilityStrategyMuc;
    private final RegionProbabilityStrategy regionProbabilityStrategy;

    private final CommuteModeChoice simpleCommuteModeChoice;
    private final LongAdder totalVacantDd = new LongAdder();

    private IndexedDoubleMatrix1D regionalShareForeigners;
    private IndexedDoubleMatrix1D hhByRegion;

    private EnumMap<IncomeCategory, EnumMap<Nationality, Map<Region, Double>>> utilityByIncomeByNationalityByRegion = new EnumMap<>(IncomeCategory.class);

    public LongCommutePenaltyHousingStrategyMuc(DataContainer dataContainer,
                                                Properties properties,
                                                TravelTimes travelTimes,
                                                DwellingProbabilityStrategy dwellingProbabilityStrategy,
                                                DwellingUtilityStrategy dwellingUtilityStrategy,
                                                RegionUtilityStrategyMuc regionUtilityStrategyMuc, RegionProbabilityStrategy regionProbabilityStrategy) {
        this.dataContainer = dataContainer;
        this.properties = properties;
        this.commutingTimeProbability = dataContainer.getCommutingTimeProbability();
        this.accessibility = dataContainer.getAccessibility();
        this.geoData = dataContainer.getGeoData();
        this.realEstateDataManager = dataContainer.getRealEstateDataManager();
        this.travelTimes = travelTimes;
        this.dwellingProbabilityStrategy = dwellingProbabilityStrategy;
        this.dwellingUtilityStrategy = dwellingUtilityStrategy;
        this.regionUtilityStrategyMuc = regionUtilityStrategyMuc;
        this.regionProbabilityStrategy = regionProbabilityStrategy;
        this.simpleCommuteModeChoice  =new SimpleCommuteModeChoice(dataContainer, properties, SiloUtil.provideNewRandom());
    }

    @Override
    public void setup() {
        regionalShareForeigners = new IndexedDoubleMatrix1D(geoData.getRegions().values());
        hhByRegion = new IndexedDoubleMatrix1D(geoData.getRegions().values());
    }

    @Override
    public boolean isHouseholdEligibleToLiveHere(Household household, Dwelling dd) {
        return true;
    }

    @Override
    public double calculateHousingUtility(Household hh, Dwelling dwelling) {
        double ddQualityUtility = convertQualityToUtility(dwelling.getQuality());
        double ddSizeUtility = convertAreaToUtility(dwelling.getBedrooms());
        double ddAutoAccessibilityUtility = convertAccessToUtility(accessibility.getAutoAccessibilityForZone(geoData.getZones().get(dwelling.getZoneId())));
        double transitAccessibilityUtility = convertAccessToUtility(accessibility.getTransitAccessibilityForZone(geoData.getZones().get(dwelling.getZoneId())));
        HouseholdType ht = hh.getHouseholdType();

        //currently this is re-filtering persons to find workers (it was done previously in select region)
        // This way looks more flexible to account for other trips, such as education, though.

        double carToWorkersRatio = Math.min(1., ((double) hh.getVehicles().stream().filter(vv -> vv.getType().equals(VehicleType.CAR)).count() / HouseholdUtil.getNumberOfWorkers(hh)));

        int penaltiesForThisDwelling = 0;
        double travelCostUtility = 1; //do not have effect at the moment;

        double workDistanceUtility = 1;
        CommuteModeChoiceMapping commuteModeChoiceMapping = simpleCommuteModeChoice.assignCommuteModeChoice(dwelling, travelTimes, hh);


        for (Person pp : hh.getPersons().values()) {
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {
                double thisWorkerUtility = commuteModeChoiceMapping.getMode(pp).utility;
                workDistanceUtility *= thisWorkerUtility;
                if (thisWorkerUtility < UTILITY_THRESHOLD){
                    penaltiesForThisDwelling += PENALTY_EUR;
                }
            }
        }



        double ddPriceUtility = convertPriceToUtility(dwelling.getPrice() + penaltiesForThisDwelling, ht.getIncomeCategory());
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
        logger.info("Calculating regional utilities");
        calculateShareOfForeignersByZoneAndRegion();
        totalVacantDd.reset();
        rentsByRegion = dataContainer.getRealEstateDataManager().calculateRegionalPrices();
        for (IncomeCategory incomeCategory : IncomeCategory.values()) {
            EnumMap<Nationality, Map<Region, Double>> utilityByNationalityByRegion = new EnumMap<>(Nationality.class);
            for (Nationality nationality : Nationality.values()) {
                Map<Region, Double> regionUtils = new LinkedHashMap<>();
                for (Region region : geoData.getRegions().values()) {
                    final int averageRegionalRent = rentsByRegion.get(region.getId()).intValue();
                    final float regAcc = (float) convertAccessToUtility(accessibility.getRegionalAccessibility(region));
                    float priceUtil = (float) convertPriceToUtility(averageRegionalRent, incomeCategory);


                    double utility = regionUtilityStrategyMuc.calculateRegionUtility(incomeCategory,
                            nationality, priceUtil, regAcc, (float) regionalShareForeigners.getIndexed(region.getId()));
                    switch (NORMALIZER) {
                        case POPULATION:
                            utility *= hhByRegion.getIndexed(region.getId());
                            break;
                        case POWER_OF_POPULATION:
                            utility *= Math.pow(hhByRegion.getIndexed(region.getId()), 0.5);
                            break;
                        default:
                            //do nothing.
                    }
                    regionUtils.put(region, utility);
                }
                utilityByNationalityByRegion.put(nationality, regionUtils);
            }
            utilityByIncomeByNationalityByRegion.put(incomeCategory, utilityByNationalityByRegion);
        }

        if(NORMALIZER == Normalizer.SHARE_VAC_DD) {
            RealEstateDataManager realEstateDataManager = dataContainer.getRealEstateDataManager();
            for (int region : geoData.getRegions().keySet()) {
                totalVacantDd.add(realEstateDataManager.getNumberOfVacantDDinRegion(region));
            }
        }
    }

    @Override
    public double calculateRegionalUtility(Household household, Region region) {

        JobDataManager jobDataManager = dataContainer.getJobDataManager();

        int penaltyToThisHouseholdAndRegion = 0;

        double thisRegionFactor = 1;
        CommuteModeChoiceMapping commuteModeChoiceMapping = simpleCommuteModeChoice.assignRegionalCommuteModeChoice(region, travelTimes, household);

        for (Person pp : household.getPersons().values()) {
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {
                double thisWorkerUtility = commuteModeChoiceMapping.getMode(pp).utility;
                thisRegionFactor *= thisWorkerUtility;
                if (thisWorkerUtility < UTILITY_THRESHOLD){
                    penaltyToThisHouseholdAndRegion += PENALTY_EUR;
                }
            }
        }

        double util;
        HouseholdType ht = household.getHouseholdType();
        Nationality nationality = ((HouseholdMuc) household).getNationality();
        if (penaltyToThisHouseholdAndRegion == 0){
            util = utilityByIncomeByNationalityByRegion.get(ht.getIncomeCategory()).get(nationality).get(region) * thisRegionFactor;
        } else {
            //recalculate the regional utility due to penalty!
            final int averageRegionalRent = rentsByRegion.get(region.getId()).intValue();
            final float regAcc = (float) convertAccessToUtility(accessibility.getRegionalAccessibility(region));
            float priceUtil = (float) convertPriceToUtility(averageRegionalRent + penaltyToThisHouseholdAndRegion, household.getHouseholdType().getIncomeCategory());
            double value = regionUtilityStrategyMuc.calculateRegionUtility(ht.getIncomeCategory(),
                    nationality, priceUtil, regAcc, (float) regionalShareForeigners.getIndexed(region.getId()));
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
            util = value * thisRegionFactor;
        }
        return normalize(region, util);
    }

    private double normalize(Region region, double baseUtil) {
        switch (NORMALIZER) {
            case SHARE_VAC_DD: {
                return baseUtil * ((float) realEstateDataManager.getNumberOfVacantDDinRegion(region.getId()) / totalVacantDd.doubleValue());
            }
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

    @Override
    public HousingStrategy duplicate() {
        TravelTimes travelTimes = this.travelTimes.duplicate();
        final LongCommutePenaltyHousingStrategyMuc housingStrategyMuc = new LongCommutePenaltyHousingStrategyMuc(dataContainer, properties, travelTimes, dwellingProbabilityStrategy, dwellingUtilityStrategy, regionUtilityStrategyMuc, regionProbabilityStrategy);
        housingStrategyMuc.regionalShareForeigners = this.regionalShareForeigners;
        housingStrategyMuc.hhByRegion = this.hhByRegion;
        housingStrategyMuc.utilityByIncomeByNationalityByRegion = this.utilityByIncomeByNationalityByRegion;
        return housingStrategyMuc;
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

    //TODO: convertAreaToUtility method name is wrong.
    //TODO: implement method to calculate housing utility with area instead of number of rooms
    private double convertAreaToUtility(int area) {
        return (float) area / (float) RealEstateDataManagerImpl.largestNoBedrooms;
    }

    private double convertAccessToUtility(double accessibility) {
        return accessibility / 100f;
    }

    private void calculateShareOfForeignersByZoneAndRegion() {
        final IndexedDoubleMatrix1D hhByZone = new IndexedDoubleMatrix1D(geoData.getZones().values());
        regionalShareForeigners.assign(0);
        hhByRegion.assign(0);
        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()) {
            int zone = -1;
            Dwelling dwelling = dataContainer.getRealEstateDataManager().getDwelling(hh.getDwellingId());
            if (dwelling != null) {
                zone = dwelling.getZoneId();
            }
            final int region = geoData.getZones().get(zone).getRegion().getId();
            hhByZone.setIndexed(zone, hhByZone.getIndexed(zone) + 1);
            hhByRegion.setIndexed(region, hhByRegion.getIndexed(region) + 1);

            if (((HouseholdMuc) hh).getNationality() != Nationality.GERMAN) {
                regionalShareForeigners.setIndexed(region, regionalShareForeigners.getIndexed(region) + 1);
            }
        }

        regionalShareForeigners.assign(hhByRegion, (foreignerShare, numberOfHouseholds) -> {
            if (numberOfHouseholds > 0) {
                return foreignerShare / numberOfHouseholds;
            } else {
                return 0;
            }
        });
    }
}
