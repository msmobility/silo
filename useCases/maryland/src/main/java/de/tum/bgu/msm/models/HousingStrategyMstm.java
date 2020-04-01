package de.tum.bgu.msm.models;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.HouseholdDataManagerMstm;
import de.tum.bgu.msm.data.RealEstateDataManagerMstm;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.accessibility.Accessibility;
import de.tum.bgu.msm.data.accessibility.CommutingTimeProbability;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingMstm;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManagerImpl;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.geo.MstmRegion;
import de.tum.bgu.msm.data.geo.MstmZone;
import de.tum.bgu.msm.data.household.*;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.Race;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.models.relocation.moves.DwellingProbabilityStrategy;
import de.tum.bgu.msm.models.relocation.moves.HousingStrategy;
import de.tum.bgu.msm.models.relocation.moves.RegionProbabilityStrategy;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix1D;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix2D;
import org.matsim.api.core.v01.TransportMode;

import java.util.*;
import java.util.concurrent.atomic.LongAdder;

import static de.tum.bgu.msm.data.dwelling.RealEstateUtils.RENT_CATEGORIES;

public class HousingStrategyMstm implements HousingStrategy<DwellingMstm> {



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

    private static final Normalizer NORMALIZER = Normalizer.POWER_OF_POPULATION;

    private final Properties properties;
    private final DataContainer dataContainer;
    private final GeoData geoData;
    private final TravelTimes travelTimes;
    private final Accessibility accessibility;
    private final JobDataManager jobData;
    private final RealEstateDataManagerMstm realEstateDataManager;
    private final CommutingTimeProbability commutingTimeProbability;

    private final DwellingUtilityStrategyMstm dwellingUtilityStrategy;
    private final DwellingProbabilityStrategy dwellingProbabilityStrategy;
    private final RegionUilityStrategyMstm regionUtilityStrategy;
    private final RegionProbabilityStrategy regionProbabilityStrategy;

    private final LongAdder totalVacantDd = new LongAdder();

    private EnumMap<IncomeCategory, EnumMap<Race, Map<Integer, Double>>> utilityByIncomeRaceRegion = new EnumMap<>(IncomeCategory.class);

    private IndexedDoubleMatrix2D zonalRacialComposition;
    private IndexedDoubleMatrix2D regionalRacialComposition;
    private IndexedDoubleMatrix1D hhByRegion;
    private double selectDwellingRaceRelevance;
    private boolean provideRentSubsidyToLowIncomeHh;

    public HousingStrategyMstm(Properties properties,
                               DataContainer dataContainer,
                               TravelTimes travelTimes,
                               DwellingProbabilityStrategy dwellingProbabilityStrategy,
                               DwellingUtilityStrategyMstm dwellingUtilityStrategy,
                               RegionUilityStrategyMstm regionUtilityStrategy, RegionProbabilityStrategy regionProbabilityStrategy) {
        this.properties = properties;
        this.commutingTimeProbability = dataContainer.getCommutingTimeProbability();
        this.dataContainer = dataContainer;
        this.geoData = dataContainer.getGeoData();
        this.travelTimes = travelTimes;
        this.accessibility = dataContainer.getAccessibility();
        this.jobData = dataContainer.getJobDataManager();
        realEstateDataManager = (RealEstateDataManagerMstm) dataContainer.getRealEstateDataManager();
        this.dwellingProbabilityStrategy = dwellingProbabilityStrategy;
        this.dwellingUtilityStrategy = dwellingUtilityStrategy;
        this.regionUtilityStrategy = regionUtilityStrategy;
        this.regionProbabilityStrategy = regionProbabilityStrategy;
    }

    @Override
    public void setup() {
        selectDwellingRaceRelevance = properties.moves.racialRelevanceInZone;
        provideRentSubsidyToLowIncomeHh = properties.moves.provideLowIncomeSubsidy;
        calculateRacialCompositionByZoneAndRegion();
    }

    @Override
    public boolean isHouseholdEligibleToLiveHere(Household household, DwellingMstm dd) {
        // Check if dwelling is restricted, if so check if household is
        // still eligible to live in this dwelling
        // (household income could exceed eligibility criterion)
        if (dd.getRestriction() <= 0) {
            // Dwelling is not income restricted
            return true;
        }
        int msa = ((MstmZone) geoData.getZones().get(dd.getZoneId())).getMsa();
        return HouseholdUtil.getAnnualHhIncome(household) <= (((HouseholdDataManagerMstm) dataContainer.getHouseholdDataManager()).getMedianIncome(msa) * dd.getRestriction());
    }

    @Override
    public double calculateHousingUtility(Household hh, DwellingMstm dwelling) {

        double ddQualityUtility = convertQualityToUtility(dwelling.getQuality());
        double ddSizeUtility = convertAreaToUtility(dwelling.getBedrooms());
        double ddAutoAccessibilityUtility = convertAccessToUtility(accessibility.getAutoAccessibilityForZone(geoData.getZones().get(dwelling.getZoneId())));
        double transitAccessibilityUtility = convertAccessToUtility(accessibility.getTransitAccessibilityForZone(geoData.getZones().get(dwelling.getZoneId())));
        HouseholdType ht = hh.getHouseholdType();
        double ddPriceUtility = convertPriceToUtility(dwelling.getPrice(), ht.getIncomeCategory());

        double crimeUtility = 1. - ((MstmZone) geoData.getZones().get(dwelling.getZoneId())).getCounty().getCrimeRate();
        double schoolQualityUtility = ((MstmRegion) geoData.getZones().get(dwelling.getZoneId()).getRegion()).getSchoolQuality();

        if (provideRentSubsidyToLowIncomeHh) {
            int price = dwelling.getPrice();
            int income = HouseholdUtil.getAnnualHhIncome(hh);
            if(householdQualifiesForSubsidy(income, geoData.getZones().get(dwelling.getZoneId()).getZoneId(), dwelling.getPrice())) {
                //need to recalculate the generic utility
                if (income > 0) {
                    // income equals -1 if dwelling is vacant right now
                    // housing subsidy program in place
                    int msa = ((MstmZone) geoData.getZones().get(dwelling.getZoneId())).getMsa();
                    if (income < (0.5f * ((HouseholdDataManagerMstm) dataContainer.getHouseholdDataManager()).getMedianIncome(msa)) && price < (0.4f * income / 12f)) {
                        float housingBudget = (income / 12f * 0.18f);  // technically, the housing budget is 30%, but in PUMS data households pay 18% on the average
                        float subsidy = ((RealEstateDataManagerMstm) dataContainer.getRealEstateDataManager()).getMedianRent(msa) - housingBudget;
                        price = Math.max(0, price - (int) (subsidy + 0.5));
                    }
                }
            }
            ddPriceUtility = convertPriceToUtility(price, ht.getIncomeCategory());
        }

        JobDataManager jobData = dataContainer.getJobDataManager();
        double workDistanceUtility = 1;
        Zone originZone = geoData.getZones().get(dwelling.getZoneId());
        for (Person pp : hh.getPersons().values()) {
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {
                Job workLocation = Objects.requireNonNull(jobData.getJobFromId(pp.getJobId()));
                Zone workZone = geoData.getZones().get(workLocation.getZoneId());
                int expectedCommuteTime = (int) travelTimes.getTravelTime(originZone, workZone, 0, TransportMode.car);
                double factorForThisZone = commutingTimeProbability.getCommutingTimeProbability(Math.max(1, expectedCommuteTime), TransportMode.car);
                workDistanceUtility *= factorForThisZone;
            }
        }

        double util = dwellingUtilityStrategy.calculateSelectDwellingUtility(ht, ddSizeUtility, ddPriceUtility,
                ddQualityUtility, ddAutoAccessibilityUtility,
                transitAccessibilityUtility, schoolQualityUtility, crimeUtility, workDistanceUtility);

        Race householdRace = ((HouseholdMstm) hh).getRace();

        double racialShare = 1;

        if (householdRace != Race.other) {
            racialShare = getZonalRacialShare(geoData.getZones().get(dwelling.getZoneId()).getZoneId(), householdRace);
        }
        // multiply by racial share to make zones with higher own racial share more attractive

        double adjustedUtility = Math.pow(util, (1 - selectDwellingRaceRelevance)) *
                Math.pow(racialShare, selectDwellingRaceRelevance);
        return adjustedUtility;
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
        totalVacantDd.reset();
        if (NORMALIZER == Normalizer.SHARE_VAC_DD) {
            for (int region : geoData.getRegions().keySet()) {
                totalVacantDd.add(realEstateDataManager.getNumberOfVacantDDinRegion(region));
            }
        }
        calculateRacialCompositionByZoneAndRegion();
        calculateRegionUtilities();
    }

    @Override
    public double calculateRegionalUtility(Household household, Region region) {
        Race householdRace = ((HouseholdMstm) household).getRace();


        double thisRegionFactor = 1;
        double carToWorkersRatio = Math.min(1., ((double) household.getAutos() / HouseholdUtil.getNumberOfWorkers(household)));

        for (Person pp : household.getPersons().values()) {
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {
                final Job job = dataContainer.getJobDataManager().getJobFromId(pp.getJobId());
                Zone workZone = geoData.getZones().get(job.getZoneId());
                if(carToWorkersRatio <= 0.) {
                    int ptTime = (int) travelTimes.getTravelTimeFromRegion(region, workZone, properties.transportModel.peakHour_s, TransportMode.pt);
                    thisRegionFactor = commutingTimeProbability.getCommutingTimeProbability(Math.max(1, ptTime), TransportMode.pt);
                } else if( carToWorkersRatio >= 1.) {
                    int carTime = (int) travelTimes.getTravelTimeFromRegion(region, workZone, properties.transportModel.peakHour_s, TransportMode.car);
                    thisRegionFactor = commutingTimeProbability.getCommutingTimeProbability(Math.max(1, carTime), TransportMode.car);
                } else {
                    int carTime = (int) travelTimes.getTravelTimeFromRegion(region, workZone, properties.transportModel.peakHour_s, TransportMode.car);
                    int ptTime = (int) travelTimes.getTravelTimeFromRegion(region, workZone, properties.transportModel.peakHour_s, TransportMode.pt);
                    double factorCar = commutingTimeProbability.getCommutingTimeProbability(Math.max(1, carTime), TransportMode.car);
                    double factorPt = commutingTimeProbability.getCommutingTimeProbability(Math.max(1, ptTime), TransportMode.pt);

                    thisRegionFactor= factorCar * carToWorkersRatio + (1 - carToWorkersRatio) * factorPt;
                }
            }
        }
        double util = thisRegionFactor * utilityByIncomeRaceRegion.get(household.getHouseholdType().getIncomeCategory()).get(householdRace).get(region.getId());
        return normalize(region, util);
    }


    @Override
    public HousingStrategy duplicate() {
        TravelTimes travelTimes = this.travelTimes.duplicate();
        final HousingStrategyMstm housingStrategyMstm = new HousingStrategyMstm(properties, dataContainer, travelTimes, dwellingProbabilityStrategy, dwellingUtilityStrategy, regionUtilityStrategy, regionProbabilityStrategy);
        housingStrategyMstm.hhByRegion = this.hhByRegion;
        housingStrategyMstm.regionalRacialComposition = this.regionalRacialComposition;
        housingStrategyMstm.zonalRacialComposition = this.zonalRacialComposition;
        housingStrategyMstm.utilityByIncomeRaceRegion = this.utilityByIncomeRaceRegion;
        return housingStrategyMstm;
    }

    private void calculateRacialCompositionByZoneAndRegion() {
        resetMatrices();
        updateHouseholdInventar();
        scaleMatrices();
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

    private void resetMatrices() {
        zonalRacialComposition = new IndexedDoubleMatrix2D(geoData.getZones().values(), Arrays.asList(Race.values()));
        regionalRacialComposition = new IndexedDoubleMatrix2D(geoData.getRegions().values(), Arrays.asList(Race.values()));
        hhByRegion = new IndexedDoubleMatrix1D(geoData.getZones().values());
        regionalRacialComposition.assign(0);
        zonalRacialComposition.assign(0);
        hhByRegion.assign(0);
    }

    private void updateHouseholdInventar() {
        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()) {
            int zone = -1;
            Dwelling dwelling = dataContainer.getRealEstateDataManager().getDwelling(hh.getDwellingId());
            if (dwelling != null) {
                zone = dwelling.getZoneId();
            }
            final int region = geoData.getZones().get(zone).getRegion().getId();

            final Race race = ((HouseholdMstm) hh).getRace();
            zonalRacialComposition.setIndexed(zone, race.getId(),
                    zonalRacialComposition.getIndexed(zone, race.getId()) + 1);
            double value = regionalRacialComposition.getIndexed(region, race.getId());
            regionalRacialComposition.setIndexed(region, race.getId(), value + 1);

            hhByRegion.setIndexed(region, hhByRegion.getIndexed(region) + 1);
        }
    }

    private void scaleMatrices() {
        for (int zone : geoData.getZones().keySet()) {
            final double zonalSum = zonalRacialComposition.viewRow(zone).zSum();
            if (zonalSum > 0) {
                zonalRacialComposition.viewRow(zone).assign(share -> share / zonalSum);
            }
        }

        for (int region : geoData.getRegions().keySet()) {
            final double regSum = regionalRacialComposition.viewRow(region).zSum();
            if (regSum > 0) {
                regionalRacialComposition.viewRow(region).assign(share -> share / regSum);
            }
        }
    }

    private double getZonalRacialShare(int zone, Race race) {
        return zonalRacialComposition.getIndexed(zone, race.getId());
    }


    private void calculateRegionUtilities() {
        // this method calculates generic utilities by household type, race and region and stores them in utilityRegion
        Map<Integer, Double> averagePriceByRegion = dataContainer.getRealEstateDataManager().calculateRegionalPrices();
        Map<Integer, Integer> priceByRegion = new LinkedHashMap<>();
        Map<Integer, Float> accessibilityByRegion = new LinkedHashMap<>();
        Map<Integer, Float> schoolQualityByRegion = new LinkedHashMap<>();
        Map<Integer, Float> crimeRateByRegion = new LinkedHashMap<>();

        for (Region region : geoData.getRegions().values()) {
            final int id = region.getId();
            int price;
            if (averagePriceByRegion.containsKey(id)) {
                price = averagePriceByRegion.get(id).intValue();
            } else {
                price = 0;
            }
            priceByRegion.put(id, price);
            accessibilityByRegion.put(id, (float) convertAccessToUtility(accessibility.getRegionalAccessibility(region)));
            schoolQualityByRegion.put(id, (float) ((MstmRegion) region).getSchoolQuality());
            crimeRateByRegion.put(id, (float) (1f - ((MstmRegion) region).getCrimeRate()));  // invert utility, as lower crime rate has higher utility
        }

        for (IncomeCategory incomeCategory : IncomeCategory.values()) {
            EnumMap<Race, Map<Integer, Double>> utilitiesByRaceRegionForThisIncome = new EnumMap<>(Race.class);
            Map<Integer, Float> priceUtilitiesByRegion = new LinkedHashMap<>();
            for (Race race : Race.values()) {
                Map<Integer, Double> utilitiesByRegionForThisRaceIncome = new LinkedHashMap<>();
                for (Region region : geoData.getRegions().values()) {
                    priceUtilitiesByRegion.put(region.getId(), (float) convertPriceToUtility(priceByRegion.get(region.getId()), incomeCategory));
                    double util = regionUtilityStrategy.calculateRegionUtility(incomeCategory,
                            race, priceUtilitiesByRegion.get(region.getId()), accessibilityByRegion.get(region.getId()),
                            (float) regionalRacialComposition.getIndexed(region.getId(), race.getId()), schoolQualityByRegion.get(region.getId()),
                            crimeRateByRegion.get(region.getId()));

                    switch (NORMALIZER) {
                        case POPULATION:
                            util *= hhByRegion.getIndexed(region.getId());
                            break;
                        case POWER_OF_POPULATION:
                            util *= Math.pow(hhByRegion.getIndexed(region.getId()), 0.5);
                            break;
                        default:
                            //do nothing.
                    }

                    utilitiesByRegionForThisRaceIncome.put(region.getId(),
                            util);

                }
                utilitiesByRaceRegionForThisIncome.put(race, utilitiesByRegionForThisRaceIncome);
            }
            utilityByIncomeRaceRegion.put(incomeCategory, utilitiesByRaceRegionForThisIncome);
        }
    }

    private boolean householdQualifiesForSubsidy(int income, int zone, int price) {
        // households with less than that must receive some welfare
        int assumedIncome = Math.max(income, 15000);
        return provideRentSubsidyToLowIncomeHh &&
                income <= (0.5f * ((HouseholdDataManagerMstm) dataContainer.getHouseholdDataManager()).getMedianIncome(((MstmZone) geoData.getZones().get(zone)).getMsa())) &&
                price <= (0.4f * assumedIncome);
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
