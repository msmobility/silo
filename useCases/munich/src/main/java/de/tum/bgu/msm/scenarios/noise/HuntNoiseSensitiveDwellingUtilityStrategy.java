package de.tum.bgu.msm.scenarios.noise;

import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypeImpl;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.job.JobMuc;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.matsim.noise.NoiseDwelling;
import de.tum.bgu.msm.models.relocation.moves.HousingStrategy;
import org.matsim.api.core.v01.TransportMode;

import java.util.Objects;

/**
 * This relocation model is based on a stated preference study by J.D. Hunt
 * <p>
 * Hunt J.D. (2010) Stated Preference Examination of Factors Influencing Residential Attraction.
 * In: Pagliara F., Preston J., Simmonds D. (eds) Residential Location Choice.
 * Advances in Spatial Science (The Regional Science Series).
 * Springer, Berlin, Heidelberg
 *
 * @author Nico
 */
public class HuntNoiseSensitiveDwellingUtilityStrategy implements HousingStrategy {

    private static final double MEDIUM_NOISE_DISCOUNT = 0.056;
    private static final double LOUD_NOISE_DISCOUNT = 0.096;

    //----------------------------------------------------------------------

    /**
     * Indices:
     * [0] Single Family [1] Duplex [2] Townhouse [3] Walkup [4] Highrise
     * [0] average
     * [1] low inc
     * [2] high inc
     */
    static double[][] dwellingTypeUtil = {
            {0, -1.0570, -1.1130, -1.6340, -1.7940},
            {0, -0.8783, -0.9566, -0.9895, -0.9514},
            {0, -1.0570, -1.1130, -1.6340, -1.7940}
    };


    //----------------------------------------------------------------------

    /**
     * Indices:
     * [0] Never bad  [1] Bad 1 day per year [2] Bad 1 day per month [3] Bad 1 day per week
     * [0] average
     * [1] low inc
     * [2] high inc
     */
    static double[][] airQualityUtilAvg = {
            {0, -0.2446, -0.5092, -0.9796},
            {0, -0.2299, -0.5264, -0.8504},
            {0, -0.3073, -0.2879, -1.3150}
    };


    /**
     * Indices:
     * [0] None [1] Occasionally just noticeable [2] Constant faint hum [3] Sometimes Disturbing [4] Frequently disturbing
     * [0] average
     * [1] low inc
     * [2] high inc
     */
    static double[][] trafficNoiseUtil = {
            {0, -0.1694, -0.7165, -0.5348, -1.35},
            {0, -0.2758, -0.8354, -0.6934, -1.0250},
            {0, -0.3192, -0.9886, -1.0180, -1.8230}
    };

    //----------------------------------------------------------------------

    //----------------------------------------------------------------------

    final static double[] rentUtilPer100Increase = {-0.8033, -1.2260, -0.6665};

    final static double[] travelTimeUtilPer10minIncreaseAvg = {-0.1890, -0.1170, -0.3613};

    final static double[] travelTimeTransitUtilPer10minIncreaseAvg = {-0.0974, -0.1253, -0.1246};

    private final TravelTimes travelTimes;
    private final JobDataManager jobDataManager;
    private final RealEstateDataManager realEstateDataManager;

    //use delegate for regional decisions
    private final HousingStrategy delegate;

    public HuntNoiseSensitiveDwellingUtilityStrategy(TravelTimes travelTimes,
                                                     JobDataManager jobDataManager,
                                                     RealEstateDataManager realEstateDataManager, HousingStrategy delegate) {
        this.travelTimes = travelTimes;
        this.jobDataManager = jobDataManager;
        this.realEstateDataManager = realEstateDataManager;
        this.delegate = delegate;
    }

    @Override
    public void setup() {
        delegate.setup();
    }

    @Override
    public boolean isHouseholdEligibleToLiveHere(Household household, Dwelling dd) {
        int numberOfPersons = household.getHhSize();
        int numberOfBedrooms = dd.getBedrooms();
        return (numberOfBedrooms + 1 >= numberOfPersons);
    }

    @Override
    public double calculateHousingUtility(Household hh, Dwelling dwelling) {
        NoiseDwelling oldDwelling = (NoiseDwelling) realEstateDataManager.getDwelling(hh.getDwellingId());
        if(oldDwelling == null || oldDwelling.equals(dwelling)) {
            return calculateCurrentUtility(hh, (NoiseDwelling) dwelling);
        } else {
            return calculateUtilityAlternative(hh, oldDwelling, (NoiseDwelling) dwelling);
        }
    }

    @Override
    public double calculateSelectDwellingProbability(double util) {
        return Math.exp(util);
    }

    @Override
    public double calculateSelectRegionProbability(double util) {
        return delegate.calculateSelectRegionProbability(util);
    }

    @Override
    public void prepareYear() {
        delegate.prepareYear();
    }

    @Override
    public double calculateRegionalUtility(Household household, Region region) {
        return delegate.calculateRegionalUtility(household, region);
    }

    @Override
    public HousingStrategy duplicate() {
        HuntNoiseSensitiveDwellingUtilityStrategy duplicate =
                new HuntNoiseSensitiveDwellingUtilityStrategy(this.travelTimes.duplicate(),
                        jobDataManager, realEstateDataManager, delegate.duplicate());
        return duplicate;
    }

    double calculateUtilityAlternative(Household household, NoiseDwelling oldDwelling, NoiseDwelling newDwelling) {
        final double oldPrice = getNoiseAdjustedPrice(oldDwelling);
        final double newPrice = getNoiseAdjustedPrice(newDwelling);


        double oldCarTravelTime = 0;
        double oldPtTravelTime = 0;
        for (Person pp : household.getPersons().values()) {
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {
                JobMuc workLocation = Objects.requireNonNull((JobMuc) jobDataManager.getJobFromId(pp.getJobId()));
                oldCarTravelTime = (int) travelTimes.getTravelTime(oldDwelling, workLocation, workLocation.getStartTimeInSeconds(), TransportMode.car);
                oldPtTravelTime = (int) travelTimes.getTravelTime(oldDwelling, workLocation, workLocation.getStartTimeInSeconds(), TransportMode.pt);
                break;
            }
        }

        double newCarTravelTime = 0;
        double newPtTravelTime = 0;
        for (Person pp : household.getPersons().values()) {
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {
                JobMuc workLocation = Objects.requireNonNull((JobMuc) jobDataManager.getJobFromId(pp.getJobId()));
                newCarTravelTime = (int) travelTimes.getTravelTime(newDwelling, workLocation, workLocation.getStartTimeInSeconds(), TransportMode.car);
                newPtTravelTime = (int) travelTimes.getTravelTime(newDwelling, workLocation, workLocation.getStartTimeInSeconds(), TransportMode.pt);
                break;
            }
        }

        final double util = calculateUtilityForAlternative(household, newDwelling, newPrice-oldPrice, newCarTravelTime-oldCarTravelTime, newPtTravelTime-oldPtTravelTime);
        return util;
    }

    private double getNoiseAdjustedPrice(NoiseDwelling dwelling) {
        double price = dwelling.getPrice();
        final double noiseImmission = dwelling.getNoiseImmission();
        if (noiseImmission > 55) {
            if (noiseImmission > 65) {
                price *= (1 - LOUD_NOISE_DISCOUNT);
            } else {
                price *= (1 - MEDIUM_NOISE_DISCOUNT);
            }
        }
        return price;
    }

    double calculateCurrentUtility(Household household, NoiseDwelling dwelling) {

        double price = dwelling.getPrice();
        final double noiseImmission = dwelling.getNoiseImmission();
        if (noiseImmission > 55) {
            if (noiseImmission > 65) {
                price *= (1 - LOUD_NOISE_DISCOUNT);
            } else {
                price *= (1 - MEDIUM_NOISE_DISCOUNT);
            }
        }

        double carTravelTime = 0;
        double ptTravelTime = 0;
        for (Person pp : household.getPersons().values()) {
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {
                JobMuc workLocation = Objects.requireNonNull((JobMuc) jobDataManager.getJobFromId(pp.getJobId()));
                carTravelTime = (int) travelTimes.getTravelTime(dwelling, workLocation, workLocation.getStartTimeInSeconds(), TransportMode.car);
                ptTravelTime = (int) travelTimes.getTravelTime(dwelling, workLocation, workLocation.getStartTimeInSeconds(), TransportMode.pt);
                break;
            }
        }

        final double util = calculateUtilityForAlternative(household, dwelling, price, carTravelTime, ptTravelTime);
        return util;
    }

    private double calculateUtilityForAlternative(Household household, NoiseDwelling dwelling,
                                    double priceDiff, double carTravelTimeDiff, double ptTravelTimeDiff) {
        int hhType = translateHouseholdType(household);
        int ddType = translateDwellingType(dwelling);
        int noiseCat = translateLdenToNoiseCategory(dwelling.getNoiseImmission());

        double dwellingUtil = dwellingTypeUtil[hhType][ddType];
        double noiseUtil = trafficNoiseUtil[hhType][noiseCat];
        double priceUtil = (priceDiff / 100.) * rentUtilPer100Increase[hhType];
        double carTravelTimeUtil = travelTimeUtilPer10minIncreaseAvg[hhType] * (carTravelTimeDiff / 10.);
        double ptTravelTimeUtil = travelTimeTransitUtilPer10minIncreaseAvg[hhType] * (ptTravelTimeDiff / 10.);
        final double airQualityUtil = 0;
        return dwellingUtil + airQualityUtil + noiseUtil + priceUtil + carTravelTimeUtil + ptTravelTimeUtil;
    }

    private int translateDwellingType(Dwelling dwelling) {
        final DwellingType type = dwelling.getType();

        if(type.equals(DefaultDwellingTypeImpl.SFD)) {
            return 0;
        } else if( type.equals(DefaultDwellingTypeImpl.SFA)) {
            return 1;
        } else if(type.equals(DefaultDwellingTypeImpl.MF234)) {
            return 2;
        } else if(type.equals(DefaultDwellingTypeImpl.MF5plus)) {
            return 4;
        } else {
            //can only happen for mobile home which shouldn't exist in muc
            return 4;
        }
    }


    private int translateLdenToNoiseCategory(double lden) {
        if (lden < 30) {
            return 0;
        } else if (lden < 45) {
            return 1;
        } else if (lden < 55) {
            return 2;
        } else if (lden < 65) {
            return 3;
        } else {
            return 4;
        }
    }

    private int translateHouseholdType(Household household) {
        final int annualHhIncome = HouseholdUtil.getAnnualHhIncome(household);
        if (annualHhIncome < 20000) {
            return 1;
        } else if (annualHhIncome < 100000) {
            return 0;
        } else {
            return 2;
        }
    }
}

