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

import static de.tum.bgu.msm.data.dwelling.DefaultDwellingTypeImpl.MF234;

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

    private static final int LOW_INCOME = 1;
    private static final int HIGH_INCOME = 2;
    private static final int AVERAGE_INCOME = 0;

    private static final int NO_NOISE = 0;
    private static final int OCCASIONALLY_NOTICEABLE_NOISE = 1;
    private static final int CONSTANT_HUM_NOISE = 2;
    private static final int SOMETIMES_DISTURBING_NOISE = 3;
    private static final int FREQUENTLY_DISTURBING_NOISE = 4;

    private static final int SINGLE_FAMILY = 0;
    private static final int DUPLEX = 1;
    private static final int TOWNHOUSE = 2;
    private static final int HIGHRISE = 4;

    //----------------------------------------------------------------------

    /**
     * Indices:
     * [0] Single Family [1] Duplex [2] Townhouse [3] Walkup [4] Highrise
     * [0] average
     * [1] low inc
     * [2] high inc
     */
    private static double[][] dwellingTypeUtil = {
            {0, -1.0570, -1.1130, -1.6340, -1.7940},
            {0, -0.8783, -0.9566, -0.9895, -0.9514},
            {0, -1.922, -2.035, -2.841, -2.729}
    };


    //----------------------------------------------------------------------



    /**
     * Indices:
     * [0] None [1] Occasionally just noticeable [2] Constant faint hum [3] Sometimes Disturbing [4] Frequently disturbing
     * [0] average
     * [1] low inc
     * [2] high inc
     */
    private static double[][] trafficNoiseUtil = {
            {0, -0.1694, -0.7165, -0.5348, -1.35},
            {0, -0.2758, -0.8354, -0.6934, -1.0250},
            {0, -0.3192, -0.9886, -1.0180, -1.8230}
    };

    //----------------------------------------------------------------------

    //----------------------------------------------------------------------

    private final static double[] rentUtilPer100Increase = {-0.8033, -1.2260, -0.6665};

    private final static double[] travelTimeUtilPer10minIncreaseAvg = {-0.1890, -0.1170, -0.3613};

    private final static double[] travelTimeTransitUtilPer10minIncreaseAvg = {-0.0974, -0.1253, -0.1246};

    private final TravelTimes travelTimes;
    private final JobDataManager jobDataManager;
    private final RealEstateDataManager realEstateDataManager;

    //use delegate for regional and current satisfaction decisions
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
//        int numberOfPersons = household.getHhSize();
////        int numberOfBedrooms = dd.getBedrooms();
////        return (numberOfBedrooms + 1 >= numberOfPersons);
        return true;
    }

    @Override
    public double calculateHousingUtility(Household hh, Dwelling dwelling) {
        NoiseDwelling oldDwelling = (NoiseDwelling) realEstateDataManager.getDwelling(hh.getDwellingId());
        if(oldDwelling == null || oldDwelling.equals(dwelling)) {
            return delegate.calculateHousingUtility(hh, dwelling);
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

    private double calculateUtilityAlternative(Household household, NoiseDwelling oldDwelling, NoiseDwelling newDwelling) {
        final double oldPrice = getNoiseAdjustedPrice(oldDwelling);
        final double newPrice = getNoiseAdjustedPrice(newDwelling);


        double oldCarTravelTime = 0;
        double oldPtTravelTime = 0;
        for (Person pp : household.getPersons().values()) {
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {
                JobMuc workLocation = Objects.requireNonNull((JobMuc) jobDataManager.getJobFromId(pp.getJobId()));
                oldCarTravelTime = (int) travelTimes.getTravelTime(oldDwelling, workLocation, workLocation.getStartTimeInSeconds().get(), TransportMode.car);
                oldPtTravelTime = (int) travelTimes.getTravelTime(oldDwelling, workLocation, workLocation.getStartTimeInSeconds().get(), TransportMode.pt);
                break;
            }
        }

        double newCarTravelTime = 0;
        double newPtTravelTime = 0;
        for (Person pp : household.getPersons().values()) {
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {
                JobMuc workLocation = Objects.requireNonNull((JobMuc) jobDataManager.getJobFromId(pp.getJobId()));
                newCarTravelTime = (int) travelTimes.getTravelTime(newDwelling, workLocation, workLocation.getStartTimeInSeconds().get(), TransportMode.car);
                newPtTravelTime = (int) travelTimes.getTravelTime(newDwelling, workLocation, workLocation.getStartTimeInSeconds().get(), TransportMode.pt);
                break;
            }
        }

        final double util = calculateUtilityForAlternative(household, newDwelling, newPrice-oldPrice, newCarTravelTime-oldCarTravelTime, newPtTravelTime-oldPtTravelTime);
        return util;
    }

    private double getNoiseAdjustedPrice(NoiseDwelling dwelling) {
        double price = dwelling.getPrice() / 0.68;
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
        if (noiseImmission > 50) {
            price -= price * ((noiseImmission - 50) * 0.004);
//            if (noiseImmission > 65) {
//                price *= (1 - LOUD_NOISE_DISCOUNT);
//            } else {
//                price *= (1 - MEDIUM_NOISE_DISCOUNT);
//            }
        }

        double carTravelTime = 0;
        double ptTravelTime = 0;
        for (Person pp : household.getPersons().values()) {
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {
                JobMuc workLocation = Objects.requireNonNull((JobMuc) jobDataManager.getJobFromId(pp.getJobId()));
                carTravelTime = (int) travelTimes.getTravelTime(dwelling, workLocation, workLocation.getStartTimeInSeconds().get(), TransportMode.car);
                ptTravelTime = (int) travelTimes.getTravelTime(dwelling, workLocation, workLocation.getStartTimeInSeconds().get(), TransportMode.pt);
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
//        final DwellingType type = MF234;


        if(type.equals(DefaultDwellingTypeImpl.SFD)) {
            return SINGLE_FAMILY;
        } else if( type.equals(DefaultDwellingTypeImpl.SFA)) {
            return DUPLEX;
        } else if(type.equals(MF234)) {
            return TOWNHOUSE;
        } else if(type.equals(DefaultDwellingTypeImpl.MF5plus)) {
            return HIGHRISE;
        } else {
            //can only happen for mobile home which shouldn't exist in muc
            return HIGHRISE;
        }
    }


    private int translateLdenToNoiseCategory(double lden) {
        if (lden < 30) {
            return NO_NOISE;
        } else if (lden < 50) {
            return OCCASIONALLY_NOTICEABLE_NOISE;
        } else if (lden < 60) {
            return CONSTANT_HUM_NOISE;
        }
//        else if (lden < 65) {
//            return SOMETIMES_DISTURBING_NOISE;
//        }
        else {
            return FREQUENTLY_DISTURBING_NOISE;
        }
    }

    /**
     * Adjusted to euro (1CAD ~ 0.68€)
     */
    private int translateHouseholdType(Household household) {
        final int annualHhIncome = HouseholdUtil.getAnnualHhIncome(household);
        if (annualHhIncome < 15000) {
            return LOW_INCOME;
        } else if (annualHhIncome < 68500) {
            return AVERAGE_INCOME;
        } else {
            return HIGH_INCOME;
        }
    }
}

