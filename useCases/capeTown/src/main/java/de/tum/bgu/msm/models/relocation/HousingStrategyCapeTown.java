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
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonCapeTown;
import de.tum.bgu.msm.data.person.RaceCapeTown;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.models.relocation.moves.DwellingProbabilityStrategy;
import de.tum.bgu.msm.models.relocation.moves.HousingStrategy;
import de.tum.bgu.msm.models.relocation.moves.RegionProbabilityStrategy;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix1D;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static de.tum.bgu.msm.data.dwelling.RealEstateUtils.RENT_CATEGORIES;

public class HousingStrategyCapeTown implements HousingStrategy {

    private final static Logger logger = Logger.getLogger(HouseholdCapeTown.class);

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

    private Map<IncomeCategory, Map<RaceCapeTown, Map<Integer, Double>>> utilityByRegionByRaceByIncome = new EnumMap<>(IncomeCategory.class);

    private final Properties properties;

    private final DataContainer dataContainer;
    private final GeoData geoData;
    private final Accessibility accessibility;
    private final TravelTimes travelTimes;

    private final DwellingUtilityStrategyCapeTown ddUtilityStrategy;
    private final DwellingProbabilityStrategy ddProbabilityStrategy;

    private final RegionUtilityStrategy regionUtilityStrategy;
    private final RegionProbabilityStrategy regionProbabilityStrategy;

    private Map<Integer, Map<RaceCapeTown, Double>> personShareByRaceByRegion = new HashMap<>();
    private Map<Integer, Map<RaceCapeTown, Double>> personShareByRaceByZone = new HashMap<>();

    private IndexedDoubleMatrix1D ppByRegion;
    private IndexedDoubleMatrix1D ppByZone;

    public HousingStrategyCapeTown(DataContainer dataContainer, Properties properties, TravelTimes travelTimes, DwellingUtilityStrategyCapeTown ddUtilityStrategy, RegionUtilityStrategy regionUtilityStrategy, DwellingProbabilityStrategy ddProbabilityStrategy, RegionProbabilityStrategy regionProbabilityStrategy) {
        this.dataContainer = dataContainer;
        this.geoData = dataContainer.getGeoData();
        this.accessibility = dataContainer.getAccessibility();
        this.properties = properties;
        this.travelTimes = travelTimes;
        this.ddUtilityStrategy = ddUtilityStrategy;
        this.regionUtilityStrategy = regionUtilityStrategy;
        this.ddProbabilityStrategy = ddProbabilityStrategy;
        this.regionProbabilityStrategy = regionProbabilityStrategy;
    }


    @Override
    public void setup() {
        ppByRegion = new IndexedDoubleMatrix1D(geoData.getRegions().values());
        ppByZone = new IndexedDoubleMatrix1D(geoData.getZones().values());
        calculateRegionalUtilities();
    }

    @Override
    public boolean isHouseholdEligibleToLiveHere(Household household, Dwelling dd) {
        return true;
    }

    @Override
    public double calculateHousingUtility(Household hh, Dwelling dwelling) {
        double ddQualityUtility = convertQualityToUtility(dwelling.getQuality());
        double ddSizeUtility = convertAreaToUtility(dwelling.getBedrooms());
        Zone zone = geoData.getZones().get(dwelling.getZoneId());
        double ddAutoAccessibilityUtility = convertAccessToUtility(accessibility.getAutoAccessibilityForZone(zone));
        double transitAccessibilityUtility = convertAccessToUtility(accessibility.getTransitAccessibilityForZone(zone));
        HouseholdType ht = hh.getHouseholdType();
        double ddPriceUtility = convertPriceToUtility(dwelling.getPrice(), ht.getIncomeCategory());

        //currently this is re-filtering persons to find workers (it was done previously in select region)
        // This way looks more flexible to account for other trips, such as education, though.

        double travelCostUtility = 1; //do not have effect at the moment;

        JobDataManager jobDataManager = dataContainer.getJobDataManager();
        double workDistanceUtility = 1;
        for (Person pp : hh.getPersons().values()) {
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {
                Job workLocation = Objects.requireNonNull(jobDataManager.getJobFromId(pp.getJobId()));
                double travelTime_min = travelTimes.getTravelTime(dwelling, workLocation, properties.transportModel.peakHour_s, TransportMode.car);
                double factorForThisZone = dataContainer.getCommutingTimeProbability().getCommutingTimeProbability(Math.max(1, (int) travelTime_min), TransportMode.car);
                workDistanceUtility *= factorForThisZone;
            }
        }
        double util = ddUtilityStrategy.calculateSelectDwellingUtility(ht, ddSizeUtility, ddPriceUtility,
                ddQualityUtility, ddAutoAccessibilityUtility,
                transitAccessibilityUtility, workDistanceUtility);

        RaceCapeTown householdRace = ((HouseholdCapeTown) hh).getRace();

        double racialShare = 1;

        if (householdRace != RaceCapeTown.OTHER) {
            racialShare = personShareByRaceByZone.get(dwelling.getZoneId()).get(householdRace);
        }
        // multiply by racial share to make zones with higher own racial share more attractive

        return Math.pow(util, (1 - properties.moves.racialRelevanceInZone)) *
                Math.pow(racialShare, properties.moves.racialRelevanceInZone);
    }

    @Override
    public double calculateSelectDwellingProbability(double util) {
        return ddProbabilityStrategy.calculateSelectDwellingProbability(util);
    }

    @Override
    public double calculateSelectRegionProbability(double util) {
        return regionProbabilityStrategy.calculateSelectRegionProbability(util);
    }

    @Override
    public void prepareYear() {
        calculateRegionalUtilities();
    }

    @Override
    public double calculateRegionalUtility(Household household, Region region) {
        final RaceCapeTown race = ((HouseholdCapeTown) household).getRace();
        final Double genericUtil =
                utilityByRegionByRaceByIncome.get(household.getHouseholdType()
                        .getIncomeCategory()).get(race).get(region.getId());

        if(genericUtil == null) {
            return 0.;
        }

        double thisRegionFactor = 1;
        double carToWorkersRatio = Math.min(1., ((double) household.getAutos() / HouseholdUtil.getNumberOfWorkers(household)));

        CommutingTimeProbability commutingTimeProbability = dataContainer.getCommutingTimeProbability();

        for (Person pp : household.getPersons().values()) {
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {
                final Job job = dataContainer.getJobDataManager().getJobFromId(pp.getJobId());
                Zone workZone = geoData.getZones().get(job.getZoneId());
                if(carToWorkersRatio <= 0.) {
                    int ptTime = (int) travelTimes.getTravelTimeFromRegion(region, workZone, properties.transportModel.peakHour_s, TransportMode.pt);
                    thisRegionFactor = commutingTimeProbability.getCommutingTimeProbability(Math.max(1, ptTime), TransportMode.car);
                } else if( carToWorkersRatio >= 1.) {
                    int carTime = (int) travelTimes.getTravelTimeFromRegion(region, workZone, properties.transportModel.peakHour_s, TransportMode.car);
                    thisRegionFactor = commutingTimeProbability.getCommutingTimeProbability(Math.max(1, carTime), TransportMode.car);
                } else {
                    int carTime = (int) travelTimes.getTravelTimeFromRegion(region, workZone, properties.transportModel.peakHour_s, TransportMode.car);
                    int ptTime = (int) travelTimes.getTravelTimeFromRegion(region, workZone, properties.transportModel.peakHour_s, TransportMode.pt);
                    double factorCar = commutingTimeProbability.getCommutingTimeProbability(Math.max(1, carTime), TransportMode.car);
                    double factorPt = commutingTimeProbability.getCommutingTimeProbability(Math.max(1, ptTime), TransportMode.car);

                    thisRegionFactor= factorCar * carToWorkersRatio + (1 - carToWorkersRatio) * factorPt;
                }
            }
        }
        return normalize(region, genericUtil*thisRegionFactor);
    }

    @Override
    public HousingStrategy duplicate() {
        TravelTimes ttCopy = travelTimes.duplicate();
        HousingStrategyCapeTown strategy = new HousingStrategyCapeTown(dataContainer, properties, ttCopy, ddUtilityStrategy, regionUtilityStrategy, ddProbabilityStrategy, regionProbabilityStrategy);
        strategy.ppByRegion = ppByRegion;
        strategy.ppByZone = ppByZone;
        strategy.utilityByRegionByRaceByIncome = utilityByRegionByRaceByIncome;
        strategy.personShareByRaceByRegion = personShareByRaceByRegion;
        strategy.personShareByRaceByZone = personShareByRaceByZone;

        return strategy;
    }

    private void calculateRegionalUtilities() {
        logger.info("Calculating regional utilities");
        utilityByRegionByRaceByIncome.clear();
        calculateRacialSharesByZoneAndRegion();
        final Map<Integer, Double> rentsByRegion = dataContainer.getRealEstateDataManager().calculateRegionalPrices();
        for (IncomeCategory incomeCategory : IncomeCategory.values()) {
            EnumMap<RaceCapeTown, Map<Integer, Double>> utilityByRegionByRace = new EnumMap<>(RaceCapeTown.class);
            for (RaceCapeTown race : RaceCapeTown.values()) {
                Map<Integer, Double> utilityByRegion = new HashMap<>();
                for (Region region : geoData.getRegions().values()) {
                    if (!rentsByRegion.containsKey(region.getId())) {
                        continue;
                    }
                    final int averageRegionalRent = rentsByRegion.get(region.getId()).intValue();
                    final float regAcc = (float) convertAccessToUtility(accessibility.getRegionalAccessibility(region));
                    float priceUtil = (float) convertPriceToUtility(averageRegionalRent, incomeCategory);

                    double util = regionUtilityStrategy.calculateRegionUtility(incomeCategory,
                            race, priceUtil, regAcc, personShareByRaceByRegion.get(region.getId()).get(race));
                    switch (NORMALIZER) {
                        case POPULATION:
                            util *= ppByRegion.getIndexed(region.getId());
                            break;
                        case POWER_OF_POPULATION:
                            util *= Math.pow(ppByRegion.getIndexed(region.getId()), 0.5);
                            break;
                        default:
                            //do nothing.
                    }

                    utilityByRegion.put(region.getId(),
                            util);
                }
                utilityByRegionByRace.put(race, utilityByRegion);
            }
            utilityByRegionByRaceByIncome.put(incomeCategory, utilityByRegionByRace);
        }
    }

    private void calculateRacialSharesByZoneAndRegion() {
        ppByRegion.assign(0);
        ppByZone.assign(0);

        for (Region region : geoData.getRegions().values()) {
            EnumMap<RaceCapeTown, Double> regionalRacialShare = new EnumMap<>(RaceCapeTown.class);
            for (RaceCapeTown race : RaceCapeTown.values()) {
                regionalRacialShare.put(race, 0.);
            }
            personShareByRaceByRegion.put(region.getId(), regionalRacialShare);
            for (Zone zone : region.getZones()) {
                EnumMap<RaceCapeTown, Double> zonalRacialShare = new EnumMap<>(RaceCapeTown.class);
                for (RaceCapeTown race : RaceCapeTown.values()) {
                    zonalRacialShare.put(race, 0.);
                }
                personShareByRaceByZone.put(zone.getZoneId(), zonalRacialShare);
            }
        }

        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()) {
            int zone = -1;
            Dwelling dwelling = dataContainer.getRealEstateDataManager().getDwelling(hh.getDwellingId());
            if (dwelling != null) {
                zone = dwelling.getZoneId();
            }
            final int region = geoData.getZones().get(zone).getRegion().getId();
            for (Person person : hh.getPersons().values()) {
                RaceCapeTown race = ((PersonCapeTown) person).getRace();

                personShareByRaceByRegion.get(region).merge(race, 1., (oldValue, newValue) -> oldValue + newValue);
                personShareByRaceByZone.get(zone).merge(race, 1., (oldValue, newValue) -> oldValue + newValue);

                ppByZone.setIndexed(zone, ppByZone.getIndexed(zone) + 1);
                ppByRegion.setIndexed(region, ppByRegion.getIndexed(region) + 1);
            }
        }

        for (Region region : geoData.getRegions().values()) {
            personShareByRaceByRegion.get(region.getId()).replaceAll((raceCapeTown, count) -> {
                if (ppByRegion.getIndexed(region.getId()) == 0.) {
                    return 0.;
                } else {
                    return count / ppByRegion.getIndexed(region.getId());
                }
            });
            for (Zone zone : region.getZones()) {
                personShareByRaceByZone.get(zone.getZoneId()).replaceAll((raceCapeTown, count) -> {
                    if (ppByZone.getIndexed(zone.getZoneId()) == 0.) {
                        return 0.;
                    } else {
                        return count / ppByZone.getIndexed(zone.getZoneId());
                    }
                });
            }
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

    private double normalize(Region region, double baseUtil) {
        RealEstateDataManager realEstateDataManager = dataContainer.getRealEstateDataManager();
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
}
