package de.tum.bgu.msm.models.realEstate;

import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;

/**
 * Updates prices of dwellings based on current demand
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 24 Febuary 2010 in Santa Fe
 **/

public final class PricingModel extends AbstractModel {

    static Logger logger = Logger.getLogger(PricingModel.class);

    private PricingJSCalculator pricingCalculator;

    private double inflectionLow;
    private double inflectionHigh;
    private double slopeLow;
    private double slopeMain;
    private double slopeHigh;
    private double maxDelta;


    public PricingModel (SiloDataContainer dataContainer) {
        super(dataContainer);
        setupPricingModel();
    }


    private void setupPricingModel() {

        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("PricingCalc"));
        pricingCalculator = new PricingJSCalculator(reader);

        inflectionLow = pricingCalculator.getLowInflectionPoint();
        inflectionHigh = pricingCalculator.getHighInflectionPoint();
        slopeLow = pricingCalculator.getLowerSlope();
        slopeMain = pricingCalculator.getMainSlope();
        slopeHigh = pricingCalculator.getHighSlope();
        maxDelta = pricingCalculator.getMaximumChange();
    }


    public void updatedRealEstatePrices () {
        // updated prices based on current demand
        logger.info("  Updating real-estate prices");

        // get vacancy rate
        double[][] vacRate = dataContainer.getRealEstateData().getVacancyRateByTypeAndRegion();
        List<DwellingType> dwellingTypes = dataContainer.getRealEstateData().getDwellingTypes();
        HashMap<String, Integer> priceChange = new HashMap<>();

        int[] cnt = new int[dwellingTypes.size()];
        double[] sumOfPrices = new double[dwellingTypes.size()];
        for (Dwelling dd: dataContainer.getRealEstateData().getDwellings()) {
            if (dd.getRestriction() != 0) continue;  // dwelling is under affordable-housing constraints, rent cannot be raised
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
                        (-structuralVacancyRate *slopeMain + structuralVacHigh * slopeMain) +
                        slopeHigh * vacRate[dto][region];
            }
            changeRate = Math.min(changeRate, 1f + maxDelta);
            changeRate = Math.max(changeRate, 1f - maxDelta);
            double newPrice = currentPrice * changeRate;

            if (dd.getId() == SiloUtil.trackDd) {
                SiloUtil.trackWriter.println("The monthly costs of dwelling " +
                        dd.getId() + " was changed from " + currentPrice + " to " + newPrice + " (in 2000$).");
            }
            dd.setPrice((int) (newPrice + 0.5));
            cnt[dto]++;
            sumOfPrices[dto] += newPrice;

            String token = dto+"_"+vacRate[dto][region]+"_"+currentPrice+"_"+newPrice;
            if (priceChange.containsKey(token)) {
                priceChange.put(token, (priceChange.get(token) + 1));
            } else {
                priceChange.put(token, 1);
            }

        }
        double[] averagePrice = new double[dwellingTypes.size()];
        for (DwellingType dt: dwellingTypes) {
            int dto = dwellingTypes.indexOf(dt);
            averagePrice[dto] = sumOfPrices[dto] / cnt[dto];
        }
        dataContainer.getRealEstateData().setAvePriceByDwellingType(averagePrice);
    }
}
