package de.tum.bgu.msm.models.realEstate.pricing;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Random;

/**
 * Updates prices of dwellings based on current demand
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 24 Febuary 2010 in Santa Fe
 **/
public final class PricingModelImpl extends AbstractModel implements PricingModel {

    private final static Logger logger = Logger.getLogger(PricingModelImpl.class);
    private final PricingStrategy strategy;

    private double inflectionLow;
    private double inflectionHigh;
    private double slopeLow;
    private double slopeMain;
    private double slopeHigh;
    private double maxDelta;

    public PricingModelImpl(DataContainer dataContainer, Properties properties, PricingStrategy strategy, Random rnd) {
        super(dataContainer, properties, rnd);
        this.strategy = strategy;
    }

    @Override
    public void setup() {
        inflectionLow = strategy.getLowInflectionPoint();
        inflectionHigh = strategy.getHighInflectionPoint();
        slopeLow = strategy.getLowerSlope();
        slopeMain = strategy.getMainSlope();
        slopeHigh = strategy.getHighSlope();
        maxDelta = strategy.getMaximumChange();
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
        logger.info("  Updating real-estate prices at the end of " + year);

        // get vacancy rate
        double[][] vacRate = dataContainer.getRealEstateDataManager().getVacancyRateByTypeAndRegion();
        List<DwellingType> dwellingTypes = dataContainer.getRealEstateDataManager().getDwellingTypes();
//        HashMap<String, Integer> priceChange = new HashMap<>();

        int[] cnt = new int[dwellingTypes.size()];
        double[] sumOfPrices = new double[dwellingTypes.size()];
        for (Dwelling dd: dataContainer.getRealEstateDataManager().getDwellings()) {
            if (!strategy.shouldUpdatePrice(dd)) {
                continue;
            }
            int dto = dwellingTypes.indexOf(dd.getType());
            float structuralVacancyRate = dd.getType().getStructuralVacancyRate();
            float structuralVacLow = (float) (structuralVacancyRate * inflectionLow);
            float structuralVacHigh = (float) (structuralVacancyRate * inflectionHigh);
            int currentPrice = dd.getPrice();

            int region = dataContainer.getGeoData().getZones().get(dd.getZoneId()).getRegion().getId();
            double changeRate;
            if (vacRate[dto][region] < structuralVacLow) {
                // vacancy is particularly low, prices need to rise steeply
                changeRate = 1 - structuralVacLow * slopeLow +
                        (-structuralVacancyRate * slopeMain + structuralVacLow * slopeMain) +
                        slopeLow * vacRate[dto][region];
            } else if (vacRate[dto][region] < structuralVacHigh) {
                // vacancy is within a normal range, prices change gradually
                changeRate = 1 - structuralVacancyRate * slopeMain + slopeMain * vacRate[dto][region];
            } else {
                // vacancy is very low, prices do not change much anymore
                changeRate = 1 - structuralVacHigh * slopeHigh +
                        (-structuralVacancyRate * slopeMain + structuralVacHigh * slopeMain) +
                        slopeHigh * vacRate[dto][region];
            }
            changeRate = Math.min(changeRate, 1f + maxDelta);
            changeRate = Math.max(changeRate, 1f - maxDelta);
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
        for (DwellingType dt: dwellingTypes) {
            int dto = dwellingTypes.indexOf(dt);
            averagePrice[dto] = sumOfPrices[dto] / cnt[dto];
        }
        dataContainer.getRealEstateDataManager().setAvePriceByDwellingType(averagePrice);
    }
}
