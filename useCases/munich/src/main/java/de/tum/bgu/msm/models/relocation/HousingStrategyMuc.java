package de.tum.bgu.msm.models.relocation;

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
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.job.JobMuc;
import de.tum.bgu.msm.data.person.Nationality;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.models.relocation.moves.DwellingProbabilityStrategy;
import de.tum.bgu.msm.models.relocation.moves.HousingStrategy;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix1D;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;

import java.util.*;
import java.util.concurrent.atomic.LongAdder;

import static de.tum.bgu.msm.data.dwelling.RealEstateUtils.RENT_CATEGORIES;

public class HousingStrategyMuc implements HousingStrategy {

    private final static Logger logger = Logger.getLogger(HousingStrategyMuc.class);

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

    private static final Normalizer NORMALIZER = Normalizer.POWER_OF_POPULATION;

    private final DataContainer dataContainer;
    private final Properties properties;
    private final CommutingTimeProbability commutingTimeProbability;
    private final Accessibility accessibility;
    private final GeoData geoData;
    private final RealEstateDataManager realEstateDataManager;

    private final TravelTimes travelTimes;

    private final DwellingProbabilityStrategy dwellingProbabilityStrategy;
    private final DwellingUtilityStrategy dwellingUtilityStrategy;
    private final SelectRegionStrategy selectRegionStrategy;

    private final LongAdder totalVacantDd = new LongAdder();

    private IndexedDoubleMatrix1D regionalShareForeigners;
    private IndexedDoubleMatrix1D hhByRegion;

    private EnumMap<IncomeCategory, EnumMap<Nationality, Map<Region, Double>>> utilityByIncomeByNationalityByRegion = new EnumMap<>(IncomeCategory.class);

    public HousingStrategyMuc(DataContainer dataContainer,
                              Properties properties,
                              TravelTimes travelTimes,
                              DwellingProbabilityStrategy dwellingProbabilityStrategy,
                              DwellingUtilityStrategy dwellingUtilityStrategy,
                              SelectRegionStrategy selectRegionStrategy) {
        this.dataContainer = dataContainer;
        this.properties = properties;
        this.commutingTimeProbability = dataContainer.getCommutingTimeProbability();
        this.accessibility = dataContainer.getAccessibility();
        this.geoData = dataContainer.getGeoData();
        this.realEstateDataManager = dataContainer.getRealEstateDataManager();
        this.travelTimes = travelTimes;
        this.dwellingProbabilityStrategy = dwellingProbabilityStrategy;
        this.dwellingUtilityStrategy = dwellingUtilityStrategy;
        this.selectRegionStrategy = selectRegionStrategy;
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
    public double calculateHousingUtility(Household hh, Dwelling dd) {
        double ddQualityUtility = convertQualityToUtility(dd.getQuality());
        double ddSizeUtility = convertAreaToUtility(dd.getBedrooms());
        double ddAutoAccessibilityUtility = convertAccessToUtility(accessibility.getAutoAccessibilityForZone(geoData.getZones().get(dd.getZoneId())));
        double transitAccessibilityUtility = convertAccessToUtility(accessibility.getTransitAccessibilityForZone(geoData.getZones().get(dd.getZoneId())));
        HouseholdType ht = hh.getHouseholdType();
        double ddPriceUtility = convertPriceToUtility(dd.getPrice(), ht.getIncomeCategory());

        //currently this is re-filtering persons to find workers (it was done previously in select region)
        // This way looks more flexible to account for other trips, such as education, though.

        double travelCostUtility = 1; //do not have effect at the moment;

        double workDistanceUtility = 1;
        JobDataManager jobDataManager = dataContainer.getJobDataManager();
        for (Person pp : hh.getPersons().values()) {
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {
                JobMuc workLocation = Objects.requireNonNull((JobMuc) jobDataManager.getJobFromId(pp.getJobId()));
                int expectedCommuteTime = (int) travelTimes.getTravelTime(dd, workLocation, workLocation.getStartTimeInSeconds(), TransportMode.car);

                int transitTime = (int) travelTimes.getTravelTime(dd, workLocation, workLocation.getStartTimeInSeconds(), TransportMode.pt);

                double factorForThisZone = commutingTimeProbability.getCommutingTimeProbability(Math.max(1, expectedCommuteTime));
                workDistanceUtility *= factorForThisZone;
            }
        }
//        double workdistanceUtilitySkim = 1;

//        Zone origin = geoData.getZones().get(dd.getZoneId());
//            Zone destination = geoData.getZones().get(workLocation.getZoneId());
        // TODO Think about how to apply this for other modes as well
//            int expectedCommuteTimePeak = (int) travelTimes.getTravelTime(dd, workLocation, Properties.get().transportModel.peakHour_s, TransportMode.car);
//            int expectedCommuteTimeZone = (int) travelTimes.getTravelTime(origin, destination, Properties.get().transportModel.peakHour_s, TransportMode.car);
//            int expectedCommuteTimeSkim = (int) travelTimes.getPeakSkim(TransportMode.car).getIndexed(dd.getZoneId(), workLocation.getZoneId());

        return dwellingUtilityStrategy.calculateSelectDwellingUtility(ht, ddSizeUtility, ddPriceUtility,
                ddQualityUtility, ddAutoAccessibilityUtility,
                transitAccessibilityUtility, workDistanceUtility);
    }

    @Override
    public double calculateSelectDwellingProbability(double util) {
        return dwellingProbabilityStrategy.calculateSelectDwellingProbability(util);
    }

    @Override
    public void prepareYear() {
        logger.info("Calculating regional utilities");
        calculateShareOfForeignersByZoneAndRegion();
        totalVacantDd.reset();
        final Map<Integer, Double> rentsByRegion = dataContainer.getRealEstateDataManager().calculateRegionalPrices();
        for (IncomeCategory incomeCategory : IncomeCategory.values()) {
            EnumMap<Nationality, Map<Region, Double>> utilityByNationalityByRegion = new EnumMap<>(Nationality.class);
            for (Nationality nationality : Nationality.values()) {
                Map<Region, Double> regionUtils = new LinkedHashMap<>();
                for (Region region : geoData.getRegions().values()) {
                    final int averageRegionalRent = rentsByRegion.get(region.getId()).intValue();
                    final float regAcc = (float) convertAccessToUtility(accessibility.getRegionalAccessibility(region));
                    float priceUtil = (float) convertPriceToUtility(averageRegionalRent, incomeCategory);


                    double probability = selectRegionStrategy.calculateSelectRegionProbability(incomeCategory,
                            nationality, priceUtil, regAcc, (float) regionalShareForeigners.getIndexed(region.getId()));
                    switch (NORMALIZER) {
                        case POPULATION:
                            probability *= hhByRegion.getIndexed(region.getId());
                            break;
                        case POWER_OF_POPULATION:
                            probability *= Math.pow(hhByRegion.getIndexed(region.getId()), 0.5);
                            break;
                        default:
                            //do nothing.
                    }
                    regionUtils.put(region, probability);
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
        double thisRegionFactor = 1;
        for (Person pp : household.getPersons().values()) {
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {
                Zone workZone = geoData.getZones().get(jobDataManager.getJobFromId(pp.getJobId()).getZoneId());
                int timeFromZoneToRegion = (int) dataContainer.getTravelTimes().getTravelTimeToRegion(
                        workZone, region, properties.transportModel.peakHour_s, TransportMode.car);
                thisRegionFactor = thisRegionFactor * commutingTimeProbability.getCommutingTimeProbability(timeFromZoneToRegion);
            }
        }

        HouseholdType ht = household.getHouseholdType();
        Nationality nationality = ((HouseholdMuc) household).getNationality();
        double baseUtil = utilityByIncomeByNationalityByRegion.get(ht.getIncomeCategory()).get(nationality).get(region);

        baseUtil *= thisRegionFactor;

        // todo: adjust probabilities to make that households tend to move shorter distances (dist to work is already represented)
        return normalize(region, baseUtil);

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
        final HousingStrategyMuc housingStrategyMuc = new HousingStrategyMuc(dataContainer, properties, travelTimes, dwellingProbabilityStrategy, dwellingUtilityStrategy, selectRegionStrategy);
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