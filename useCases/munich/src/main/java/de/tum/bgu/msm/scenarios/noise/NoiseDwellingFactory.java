package de.tum.bgu.msm.scenarios.noise;

import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypes;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingFactory;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.utils.SampleException;
import de.tum.bgu.msm.utils.Sampler;
import org.locationtech.jts.geom.Coordinate;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class NoiseDwellingFactory implements DwellingFactory {

    private enum AgeCategory {
        LESS_THAN_TWO,
        TWO_TO_FIVE,
        FIVE_TO_TEN,
        TEN_TO_FIFTEEN,
        FIFTEEN_TO_TWENTY,
        TWENTY_TO_THIRTY,
        THIRTY_TO_FORTY,
        FORTY_TO_FIFTY,
        FIFTY_TO_SIXTY,
        SIXTY_TO_SEVENTY,
        GREATER_THAN_SEVENTY
    }

    private final static Map<AgeCategory, Sampler<NKHedonicPricingModelState>> probabilityByStateByAgeGroup = new LinkedHashMap<>();

    private final Random random;
    private DwellingFactory delegate;

    public NoiseDwellingFactory(DwellingFactory delegate) {
        this.delegate = delegate;
        this.random = new Random(42);

        initializeStateProbabilities();

     }

    private void initializeStateProbabilities() {

        Sampler<NKHedonicPricingModelState> lessThanTwoSampler = new Sampler<>(NKHedonicPricingModelState.values().length,
                NKHedonicPricingModelState.class, new Random(42));
        lessThanTwoSampler.incrementalAdd(NKHedonicPricingModelState.FIRST_TIME_USE_STATE, 350);
        lessThanTwoSampler.incrementalAdd(NKHedonicPricingModelState.FIRST_TIME_USE_AFTER_RESTORATION_STATE, 1);
        lessThanTwoSampler.incrementalAdd(NKHedonicPricingModelState.WELL_KEPT_STATE, 1);
        lessThanTwoSampler.incrementalAdd(NKHedonicPricingModelState.MODERNIZED_STATE, 0);
        lessThanTwoSampler.incrementalAdd(NKHedonicPricingModelState.NEW_BUILDING_STATE, 40);
        lessThanTwoSampler.incrementalAdd(NKHedonicPricingModelState.RENOVATED_STATE, 0);
        lessThanTwoSampler.incrementalAdd(NKHedonicPricingModelState.RESTORED_STATE, 0);
        probabilityByStateByAgeGroup.put(AgeCategory.LESS_THAN_TWO, lessThanTwoSampler);

        Sampler<NKHedonicPricingModelState> twoToFiveSampler = new Sampler<>(NKHedonicPricingModelState.values().length,
                NKHedonicPricingModelState.class, new Random(42));
        twoToFiveSampler.incrementalAdd(NKHedonicPricingModelState.FIRST_TIME_USE_STATE, 26);
        twoToFiveSampler.incrementalAdd(NKHedonicPricingModelState.FIRST_TIME_USE_AFTER_RESTORATION_STATE, 1);
        twoToFiveSampler.incrementalAdd(NKHedonicPricingModelState.WELL_KEPT_STATE, 5);
        twoToFiveSampler.incrementalAdd(NKHedonicPricingModelState.MODERNIZED_STATE, 0);
        twoToFiveSampler.incrementalAdd(NKHedonicPricingModelState.NEW_BUILDING_STATE, 126);
        twoToFiveSampler.incrementalAdd(NKHedonicPricingModelState.RENOVATED_STATE, 0);
        twoToFiveSampler.incrementalAdd(NKHedonicPricingModelState.RESTORED_STATE, 1);
        probabilityByStateByAgeGroup.put(AgeCategory.TWO_TO_FIVE, twoToFiveSampler);


        Sampler<NKHedonicPricingModelState> fiveToTenSampler = new Sampler<>(NKHedonicPricingModelState.values().length,
                NKHedonicPricingModelState.class, new Random(42));
        fiveToTenSampler.incrementalAdd(NKHedonicPricingModelState.FIRST_TIME_USE_STATE, 2);
        fiveToTenSampler.incrementalAdd(NKHedonicPricingModelState.FIRST_TIME_USE_AFTER_RESTORATION_STATE, 1);
        fiveToTenSampler.incrementalAdd(NKHedonicPricingModelState.WELL_KEPT_STATE, 32);
        fiveToTenSampler.incrementalAdd(NKHedonicPricingModelState.MODERNIZED_STATE, 0);
        fiveToTenSampler.incrementalAdd(NKHedonicPricingModelState.NEW_BUILDING_STATE, 356);
        fiveToTenSampler.incrementalAdd(NKHedonicPricingModelState.RENOVATED_STATE, 5);
        fiveToTenSampler.incrementalAdd(NKHedonicPricingModelState.RESTORED_STATE, 2);
        probabilityByStateByAgeGroup.put(AgeCategory.FIVE_TO_TEN, fiveToTenSampler);


        Sampler<NKHedonicPricingModelState> tenToFifteenSampler = new Sampler<>(NKHedonicPricingModelState.values().length,
                NKHedonicPricingModelState.class, new Random(42));
        tenToFifteenSampler.incrementalAdd(NKHedonicPricingModelState.FIRST_TIME_USE_STATE, 0);
        tenToFifteenSampler.incrementalAdd(NKHedonicPricingModelState.FIRST_TIME_USE_AFTER_RESTORATION_STATE, 0);
        tenToFifteenSampler.incrementalAdd(NKHedonicPricingModelState.WELL_KEPT_STATE, 26);
        tenToFifteenSampler.incrementalAdd(NKHedonicPricingModelState.MODERNIZED_STATE, 2);
        tenToFifteenSampler.incrementalAdd(NKHedonicPricingModelState.NEW_BUILDING_STATE, 38);
        tenToFifteenSampler.incrementalAdd(NKHedonicPricingModelState.RENOVATED_STATE, 5);
        tenToFifteenSampler.incrementalAdd(NKHedonicPricingModelState.RESTORED_STATE, 1);
        probabilityByStateByAgeGroup.put(AgeCategory.TEN_TO_FIFTEEN, tenToFifteenSampler);


        Sampler<NKHedonicPricingModelState> fifteenToTwentySampler = new Sampler<>(NKHedonicPricingModelState.values().length,
                NKHedonicPricingModelState.class, new Random(42));
        fifteenToTwentySampler.incrementalAdd(NKHedonicPricingModelState.FIRST_TIME_USE_STATE, 0);
        fifteenToTwentySampler.incrementalAdd(NKHedonicPricingModelState.FIRST_TIME_USE_AFTER_RESTORATION_STATE, 1);
        fifteenToTwentySampler.incrementalAdd(NKHedonicPricingModelState.WELL_KEPT_STATE, 39);
        fifteenToTwentySampler.incrementalAdd(NKHedonicPricingModelState.MODERNIZED_STATE, 0);
        fifteenToTwentySampler.incrementalAdd(NKHedonicPricingModelState.NEW_BUILDING_STATE, 24);
        fifteenToTwentySampler.incrementalAdd(NKHedonicPricingModelState.RENOVATED_STATE, 7);
        fifteenToTwentySampler.incrementalAdd(NKHedonicPricingModelState.RESTORED_STATE, 2);
        probabilityByStateByAgeGroup.put(AgeCategory.FIFTEEN_TO_TWENTY, fifteenToTwentySampler);


        Sampler<NKHedonicPricingModelState> twentyToThirtySampler = new Sampler<>(NKHedonicPricingModelState.values().length,
                NKHedonicPricingModelState.class, new Random(42));
        twentyToThirtySampler.incrementalAdd(NKHedonicPricingModelState.FIRST_TIME_USE_STATE, 0);
        twentyToThirtySampler.incrementalAdd(NKHedonicPricingModelState.FIRST_TIME_USE_AFTER_RESTORATION_STATE, 11);
        twentyToThirtySampler.incrementalAdd(NKHedonicPricingModelState.WELL_KEPT_STATE, 136);
        twentyToThirtySampler.incrementalAdd(NKHedonicPricingModelState.MODERNIZED_STATE, 12);
        twentyToThirtySampler.incrementalAdd(NKHedonicPricingModelState.NEW_BUILDING_STATE, 19);
        twentyToThirtySampler.incrementalAdd(NKHedonicPricingModelState.RENOVATED_STATE, 43);
        twentyToThirtySampler.incrementalAdd(NKHedonicPricingModelState.RESTORED_STATE, 2);
        probabilityByStateByAgeGroup.put(AgeCategory.TWENTY_TO_THIRTY, twentyToThirtySampler);


        Sampler<NKHedonicPricingModelState> thirtyToFortySampler = new Sampler<>(NKHedonicPricingModelState.values().length,
                NKHedonicPricingModelState.class, new Random(42));
        thirtyToFortySampler.incrementalAdd(NKHedonicPricingModelState.FIRST_TIME_USE_STATE, 0);
        thirtyToFortySampler.incrementalAdd(NKHedonicPricingModelState.FIRST_TIME_USE_AFTER_RESTORATION_STATE, 20);
        thirtyToFortySampler.incrementalAdd(NKHedonicPricingModelState.WELL_KEPT_STATE, 108);
        thirtyToFortySampler.incrementalAdd(NKHedonicPricingModelState.MODERNIZED_STATE, 20);
        thirtyToFortySampler.incrementalAdd(NKHedonicPricingModelState.NEW_BUILDING_STATE, 9);
        thirtyToFortySampler.incrementalAdd(NKHedonicPricingModelState.RENOVATED_STATE, 28);
        thirtyToFortySampler.incrementalAdd(NKHedonicPricingModelState.RESTORED_STATE, 8);
        probabilityByStateByAgeGroup.put(AgeCategory.THIRTY_TO_FORTY, thirtyToFortySampler);


        Sampler<NKHedonicPricingModelState> fortyToFiftySampler = new Sampler<>(NKHedonicPricingModelState.values().length,
                NKHedonicPricingModelState.class, new Random(42));
        fortyToFiftySampler.incrementalAdd(NKHedonicPricingModelState.FIRST_TIME_USE_STATE, 0);
        fortyToFiftySampler.incrementalAdd(NKHedonicPricingModelState.FIRST_TIME_USE_AFTER_RESTORATION_STATE, 35);
        fortyToFiftySampler.incrementalAdd(NKHedonicPricingModelState.WELL_KEPT_STATE, 132);
        fortyToFiftySampler.incrementalAdd(NKHedonicPricingModelState.MODERNIZED_STATE, 40);
        fortyToFiftySampler.incrementalAdd(NKHedonicPricingModelState.NEW_BUILDING_STATE, 33);
        fortyToFiftySampler.incrementalAdd(NKHedonicPricingModelState.RENOVATED_STATE, 88);
        fortyToFiftySampler.incrementalAdd(NKHedonicPricingModelState.RESTORED_STATE, 13);
        probabilityByStateByAgeGroup.put(AgeCategory.FORTY_TO_FIFTY, fortyToFiftySampler);


        Sampler<NKHedonicPricingModelState> fiftyToSixtySampler = new Sampler<>(NKHedonicPricingModelState.values().length,
                NKHedonicPricingModelState.class, new Random(42));
        fiftyToSixtySampler.incrementalAdd(NKHedonicPricingModelState.FIRST_TIME_USE_STATE, 0);
        fiftyToSixtySampler.incrementalAdd(NKHedonicPricingModelState.FIRST_TIME_USE_AFTER_RESTORATION_STATE, 50);
        fiftyToSixtySampler.incrementalAdd(NKHedonicPricingModelState.WELL_KEPT_STATE, 175);
        fiftyToSixtySampler.incrementalAdd(NKHedonicPricingModelState.MODERNIZED_STATE, 28);
        fiftyToSixtySampler.incrementalAdd(NKHedonicPricingModelState.NEW_BUILDING_STATE, 42);
        fiftyToSixtySampler.incrementalAdd(NKHedonicPricingModelState.RENOVATED_STATE, 136);
        fiftyToSixtySampler.incrementalAdd(NKHedonicPricingModelState.RESTORED_STATE, 28);
        probabilityByStateByAgeGroup.put(AgeCategory.FIFTY_TO_SIXTY, fortyToFiftySampler);


        Sampler<NKHedonicPricingModelState> sixtyToSeventySampler = new Sampler<>(NKHedonicPricingModelState.values().length,
                NKHedonicPricingModelState.class, new Random(42));
        sixtyToSeventySampler.incrementalAdd(NKHedonicPricingModelState.FIRST_TIME_USE_STATE, 0);
        sixtyToSeventySampler.incrementalAdd(NKHedonicPricingModelState.FIRST_TIME_USE_AFTER_RESTORATION_STATE, 56);
        sixtyToSeventySampler.incrementalAdd(NKHedonicPricingModelState.WELL_KEPT_STATE, 121);
        sixtyToSeventySampler.incrementalAdd(NKHedonicPricingModelState.MODERNIZED_STATE, 26);
        sixtyToSeventySampler.incrementalAdd(NKHedonicPricingModelState.NEW_BUILDING_STATE, 29);
        sixtyToSeventySampler.incrementalAdd(NKHedonicPricingModelState.RENOVATED_STATE, 98);
        sixtyToSeventySampler.incrementalAdd(NKHedonicPricingModelState.RESTORED_STATE, 11);
        probabilityByStateByAgeGroup.put(AgeCategory.SIXTY_TO_SEVENTY, sixtyToSeventySampler);


        Sampler<NKHedonicPricingModelState> greaterThanSeventySampler = new Sampler<>(NKHedonicPricingModelState.values().length,
                NKHedonicPricingModelState.class, new Random(42));
        greaterThanSeventySampler.incrementalAdd(NKHedonicPricingModelState.FIRST_TIME_USE_STATE, 0);
        greaterThanSeventySampler.incrementalAdd(NKHedonicPricingModelState.FIRST_TIME_USE_AFTER_RESTORATION_STATE, 78);
        greaterThanSeventySampler.incrementalAdd(NKHedonicPricingModelState.WELL_KEPT_STATE, 140);
        greaterThanSeventySampler.incrementalAdd(NKHedonicPricingModelState.MODERNIZED_STATE, 43);
        greaterThanSeventySampler.incrementalAdd(NKHedonicPricingModelState.NEW_BUILDING_STATE, 0);
        greaterThanSeventySampler.incrementalAdd(NKHedonicPricingModelState.RENOVATED_STATE, 107);
        greaterThanSeventySampler.incrementalAdd(NKHedonicPricingModelState.RESTORED_STATE, 17);
        probabilityByStateByAgeGroup.put(AgeCategory.GREATER_THAN_SEVENTY, greaterThanSeventySampler);
    }

    @Override
    public Dwelling createDwelling(int id, int zoneId, Coordinate coordinate, int hhId, DwellingType type, int bedrooms, int quality, int price, int year) {
        final NoiseDwellingIml noiseDwellingIml = new NoiseDwellingIml(delegate.createDwelling(id, zoneId, coordinate, hhId, type, bedrooms, quality, price, year));
        if(type == DefaultDwellingTypes.DefaultDwellingTypeImpl.MF5plus) {
            if(random.nextDouble() < 0.8) {
                noiseDwellingIml.setAttribute("parking_available", 1);
            } else {
                noiseDwellingIml.setAttribute("parking_available", 0);
            }
        } else {
            noiseDwellingIml.setAttribute("parking_available", 1);
        }

        try {

            Sampler<NKHedonicPricingModelState> sampler = null;
            int age = 2011 - year;
            if(age<2) {
                sampler = probabilityByStateByAgeGroup.get(AgeCategory.LESS_THAN_TWO);
            } else if(age < 5) {
                sampler = probabilityByStateByAgeGroup.get(AgeCategory.TWO_TO_FIVE);
            } else if(age < 10) {
                sampler = probabilityByStateByAgeGroup.get(AgeCategory.FIVE_TO_TEN);
            } else if(age < 15) {
                sampler = probabilityByStateByAgeGroup.get(AgeCategory.TEN_TO_FIFTEEN);
            } else if(age < 20) {
                sampler = probabilityByStateByAgeGroup.get(AgeCategory.FIFTEEN_TO_TWENTY);
            } else if(age < 30) {
                sampler = probabilityByStateByAgeGroup.get(AgeCategory.TWENTY_TO_THIRTY);
            } else if(age < 40) {
                sampler = probabilityByStateByAgeGroup.get(AgeCategory.THIRTY_TO_FORTY);
            } else if(age < 50) {
                sampler = probabilityByStateByAgeGroup.get(AgeCategory.FORTY_TO_FIFTY);
            } else if(age<60) {
                sampler = probabilityByStateByAgeGroup.get(AgeCategory.FIFTY_TO_SIXTY);
            } else if( age < 70) {
                sampler = probabilityByStateByAgeGroup.get(AgeCategory.SIXTY_TO_SEVENTY);
            } else {
                sampler = probabilityByStateByAgeGroup.get(AgeCategory.GREATER_THAN_SEVENTY);
            }

            final NKHedonicPricingModelState state = sampler.sampleObject();
            noiseDwellingIml.setAttribute("state", state);
        } catch (SampleException e) {
            e.printStackTrace();
        }

        return noiseDwellingIml;
    }
}
