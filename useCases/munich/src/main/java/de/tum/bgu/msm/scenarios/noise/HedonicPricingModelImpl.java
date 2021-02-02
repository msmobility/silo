package de.tum.bgu.msm.scenarios.noise;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.realEstate.pricing.PricingModel;
import de.tum.bgu.msm.models.realEstate.pricing.PricingModelImpl;
import de.tum.bgu.msm.models.realEstate.pricing.PricingStrategy;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class HedonicPricingModelImpl extends AbstractModel implements PricingModel {

    private final static Logger logger = Logger.getLogger(PricingModelImpl.class);

    private final PricingStrategy strategy;
    private final HedonicPricingModelPredictor hedonicPredictor;
    private final OsmAccessibilityCalculator accessibilityCalculator;

    public HedonicPricingModelImpl(DataContainer dataContainer, Properties properties, PricingStrategy strategy, Random rnd, HedonicPricingModelPredictor hedonicPredictor, OsmAccessibilityCalculator accessibilityCalculator) {
        super(dataContainer, properties, rnd);
        this.strategy = strategy;
        this.hedonicPredictor = hedonicPredictor;
        this.accessibilityCalculator = accessibilityCalculator;
    }

    @Override
    public void setup() {
        accessibilityCalculator.calculateAccessibilities(properties.main.baseYear);
        updateRealEstatePrices(properties.main.baseYear);
    }

    @Override
    public void prepareYear(int year) {
    }

    @Override
    public void endYear(int year) {
        updateRealEstatePrices(year);
    }

    @Override
    public void endSimulation() {
    }

    private void updateRealEstatePrices(int year) {
        // updated prices based on current demand
        logger.info("  Updating real-estate prices at " + year);

        // get vacancy rate
        double[][] vacRate = dataContainer.getRealEstateDataManager().getVacancyRateByTypeAndRegion();
        List<DwellingType> dwellingTypes = dataContainer.getRealEstateDataManager().getDwellingTypes().getTypes();

        final Map<Integer, Double> currentRentByRegion = dataContainer.getRealEstateDataManager().calculateRegionalPrices();

        for (Dwelling dd : dataContainer.getRealEstateDataManager().getDwellings()) {
            final double predictPrice = hedonicPredictor.predictPrice(dd);
            dd.setPrice((int) predictPrice);
        }

        final Map<Integer, Double> newRentByRegion = dataContainer.getRealEstateDataManager().calculateRegionalPrices();

        int[] cnt = new int[dwellingTypes.size()];
        double[] sumOfPrices = new double[dwellingTypes.size()];

        for (Dwelling dd : dataContainer.getRealEstateDataManager().getDwellings()) {
            int region = dataContainer.getGeoData().getZones().get(dd.getZoneId()).getRegion().getId();
            double scaleFactor = (double) currentRentByRegion.get(region) / newRentByRegion.get(region);
            int tempPrice = dd.getPrice();
            tempPrice *= scaleFactor;

            int dto = dwellingTypes.indexOf(dd.getType());
            if (!strategy.isPriceUpdateAllowed(dd)) {
                double vacancyRateAtThisRegion = vacRate[dto][region];
                float structuralVacancyRate = dd.getType().getStructuralVacancyRate();
                double changeRate = strategy.getPriceChangeRate(vacancyRateAtThisRegion, structuralVacancyRate);
                tempPrice *= changeRate;

            }
            dd.setPrice((int) (tempPrice + 0.5));
            cnt[dto]++;
            sumOfPrices[dto] += tempPrice;
        }

        double[] averagePrice = new double[dwellingTypes.size()];
        logger.info("Updated average real-estate prices by dwelling type:");
        for (DwellingType dt : dwellingTypes) {
            int dto = dwellingTypes.indexOf(dt);
            averagePrice[dto] = sumOfPrices[dto] / cnt[dto];
            logger.info(dt + ": " + averagePrice[dto]);
        }
        dataContainer.getRealEstateDataManager().setAvePriceByDwellingType(averagePrice);
    }
}