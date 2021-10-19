package de.tum.bgu.msm.models.realEstate.pricing;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Updates prices of dwellings based on current demand
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 24 Febuary 2010 in Santa Fe
 **/
public final class PricingModelImpl extends AbstractModel implements PricingModel {

    private final static Logger logger = Logger.getLogger(PricingModelImpl.class);
    private final PricingStrategy strategy;

    public PricingModelImpl(DataContainer dataContainer, Properties properties, PricingStrategy strategy, Random rnd) {
        super(dataContainer, properties, rnd);
        this.strategy = strategy;
    }

    private Map<DwellingType, Map<Integer, Integer>> changeRateDistribution;
    private PrintWriter priceWriter;

    @Override
    public void setup() {

        priceWriter = SiloUtil.openFileForSequentialWriting(properties.main.baseDirectory +
                "/scenOutput/" +
                properties.main.scenarioName +
                "/pricingModel.csv", false);

        priceWriter.println("year,type,rate_bin,count");

    }

    @Override
    public void prepareYear(int year) {

        changeRateDistribution = new HashMap<>();
        for (DwellingType type : dataContainer.getRealEstateDataManager().getDwellingTypes().getTypes()) {
            changeRateDistribution.put(type, new HashMap<>());
        }
    }

    @Override
    public void endYear(int year) {
        updateRealEstatePrices(year);
        changeRateDistribution.keySet().forEach(type-> {
            changeRateDistribution.get(type).keySet().forEach(x -> {
                priceWriter.println(year + "," + type.toString() + "," + x + "," + changeRateDistribution.get(type).get(x));
            });
            });
        priceWriter.flush();
    }

    @Override
    public void endSimulation() {
        priceWriter.close();
    }

    private void updateRealEstatePrices(int year) {
        // updated prices based on current demand
        logger.info("  Updating real-estate prices at the end of " + year);

        // get vacancy rate
        double[][] vacRate = dataContainer.getRealEstateDataManager().getVacancyRateByTypeAndRegion();
        List<DwellingType> dwellingTypes = dataContainer.getRealEstateDataManager().getDwellingTypes().getTypes();
//        HashMap<String, Integer> priceChange = new HashMap<>();

        double[] globalVacRate = dataContainer.getRealEstateDataManager().getAverageVacancyByDwellingType();
        int[] cnt = new int[dwellingTypes.size()];
        double[] sumOfPrices = new double[dwellingTypes.size()];
        for (Dwelling dd: dataContainer.getRealEstateDataManager().getDwellings()) {
            if (!strategy.isPriceUpdateAllowed(dd)) {
                continue;
            }
            int dto = dwellingTypes.indexOf(dd.getType());
            int currentPrice = dd.getPrice();
            int region = dataContainer.getGeoData().getZones().get(dd.getZoneId()).getRegion().getId();
            double vacancyRateAtThisRegion = vacRate[dto][region];
            float structuralVacancyRate = dd.getType().getStructuralVacancyRate();
            double changeRate;
            if (vacancyRateAtThisRegion == 0) {
                changeRate = strategy.getPriceChangeRate(globalVacRate[dto], structuralVacancyRate);
            } else {
                changeRate = strategy.getPriceChangeRate(vacancyRateAtThisRegion, structuralVacancyRate);
            }


            int changeRateInt = (int) Math.round((changeRate - 1) * 100);

            Map<Integer, Integer> changeRatesForThisType = changeRateDistribution.get(dd.getType());
            changeRatesForThisType.putIfAbsent(changeRateInt, 0);
            changeRatesForThisType.put(changeRateInt, changeRatesForThisType.get(changeRateInt)+1);

            double newPrice = currentPrice * changeRate;

            if (dd.getId() == SiloUtil.trackDd) {
                SiloUtil.trackWriter.println("The monthly costs of dwelling " +
                        dd.getId() + " was changed from " + currentPrice + " to " + newPrice +
                        " (in constant currency value without inflation).");
            }
            dd.setPrice((int) (newPrice + 0.5));
            cnt[dto]++;
            sumOfPrices[dto] += newPrice;

//            String token = dto+"_"+vacRate[dto][region]+"_"+currentPrice+"_"+newPrice;
//            if (priceChange.containsKey(token)) {
//                priceChange.put(token, (priceChange.get(token) + 1));
//            } else {
//                priceChange.put(token, 1);
//            }

        }
        double[] averagePrice = new double[dwellingTypes.size()];
        logger.info("Updated average real-estate prices by dwelling type:");
        for (DwellingType dt: dwellingTypes) {
            int dto = dwellingTypes.indexOf(dt);
            averagePrice[dto] = sumOfPrices[dto] / cnt[dto];
            logger.info(dt + ": " + averagePrice[dto]);
        }
        dataContainer.getRealEstateDataManager().setAvePriceByDwellingType(averagePrice);
    }
}
