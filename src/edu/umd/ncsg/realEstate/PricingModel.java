package edu.umd.ncsg.realEstate;

import edu.umd.ncsg.SiloUtil;
import edu.umd.ncsg.data.DwellingType;
import edu.umd.ncsg.data.RealEstateDataManager;
import edu.umd.ncsg.data.geoData;
import org.apache.log4j.Logger;
import edu.umd.ncsg.data.Dwelling;
import com.pb.common.util.ResourceUtil;
import com.pb.common.calculator.UtilityExpressionCalculator;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.io.File;

/**
 * Updates prices of dwellings based on current demand
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 24 Febuary 2010 in Santa Fe
 **/

public class PricingModel {

    static Logger logger = Logger.getLogger(PricingModel.class);

    protected static final String PROPERTIES_RealEstate_UEC_FILE                = "RealEstate.UEC.FileName";
    protected static final String PROPERTIES_RealEstate_UEC_DATA_SHEET          = "RealEstate.UEC.DataSheetNumber";
    protected static final String PROPERTIES_RealEstate_UEC_MODEL_SHEET_PRICING = "RealEstate.UEC.ModelSheetNumber.Pricing";
    protected static final String PROPERTIES_RealEstate_STRUCTURAL_VACANCY      = "vacancy.rate.by.type";

    private String uecFileName;
    private int dataSheetNumber;
    private double inflectionLow;
    private double inflectionHigh;
    private double slopeLow;
    private double slopeMain;
    private double slopeHigh;
    private double maxDelta;
    private double[] structuralVacancy;


    public PricingModel (ResourceBundle rb) {

        // read properties
        uecFileName     = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_RealEstate_UEC_FILE);
        dataSheetNumber = ResourceUtil.getIntegerProperty(rb, PROPERTIES_RealEstate_UEC_DATA_SHEET);
        setupPricingModel(rb);
    }


    private void setupPricingModel(ResourceBundle rb) {

        // read properties
        int pricingModelSheetNumber = ResourceUtil.getIntegerProperty(rb, PROPERTIES_RealEstate_UEC_MODEL_SHEET_PRICING);

        // initialize UEC
        UtilityExpressionCalculator pricingModel = new UtilityExpressionCalculator(new File(uecFileName),
                pricingModelSheetNumber,
                dataSheetNumber,
                SiloUtil.getRbHashMap(),
                PricingDMU.class);
        PricingDMU pricingDmu = new PricingDMU();

        int[] availability = {1, 1};
        pricingDmu.setToken(1);
        double utila[] = pricingModel.solve(pricingDmu.getDmuIndexValues(), pricingDmu, availability);
        inflectionLow = utila[0];
        pricingDmu.setToken(2);
        double utilb1[] = pricingModel.solve(pricingDmu.getDmuIndexValues(), pricingDmu, availability);
        inflectionHigh = utilb1[0];
        pricingDmu.setToken(3);
        double utilb2[] = pricingModel.solve(pricingDmu.getDmuIndexValues(), pricingDmu, availability);
        slopeLow = utilb2[0];
        pricingDmu.setToken(4);
        double utill[] = pricingModel.solve(pricingDmu.getDmuIndexValues(), pricingDmu, availability);
        slopeMain = utill[0];
        pricingDmu.setToken(5);
        double utilm[] = pricingModel.solve(pricingDmu.getDmuIndexValues(), pricingDmu, availability);
        slopeHigh = utilm[0];
        pricingDmu.setToken(6);
        double utild[] = pricingModel.solve(pricingDmu.getDmuIndexValues(), pricingDmu, availability);
        maxDelta = utild[0];
        structuralVacancy = ResourceUtil.getDoubleArray(rb, PROPERTIES_RealEstate_STRUCTURAL_VACANCY);
    }


    public void updatedRealEstatePrices (int year, RealEstateDataManager realEstateData) {
        // updated prices based on current demand
        logger.info("  Updating real-estate prices");

        // get vacancy rate
        double[][] vacRate = realEstateData.getVacancyRateByTypeAndRegion();

        HashMap<String, Integer> priceChange = new HashMap<>();

        int[] cnt = new int[DwellingType.values().length];
        double[] sumOfPrices = new double[DwellingType.values().length];
        for (Dwelling dd: Dwelling.getDwellings()) {
            if (dd.getRestriction() != 0) continue;  // dwelling is under affordable-housing constraints, rent cannot be raised
            int dto = dd.getType().ordinal();
            float structuralVacLow = (float) (structuralVacancy[dto] * inflectionLow);
            float structuralVacHigh = (float) (structuralVacancy[dto] * inflectionHigh);
            int currentPrice = dd.getPrice();
            int zn = dd.getZone();
            int region = geoData.getRegionOfZone(zn);
            double changeRate;
            if (vacRate[dto][region] < structuralVacLow) {
                // vacancy is particularly low, prices need to rise steeply
                changeRate = 1 - structuralVacLow * slopeLow +
                        (-structuralVacancy[dto] * slopeMain + structuralVacLow * slopeMain) +
                        slopeLow * vacRate[dto][region];
            } else if (vacRate[dto][region] < structuralVacHigh) {
                // vacancy is within a normal range, prices change gradually
                changeRate = 1 - structuralVacancy[dto] * slopeMain + slopeMain * vacRate[dto][region];
            } else {
                // vacancy is very low, prices do not change much anymore
                changeRate = 1 - structuralVacHigh * slopeHigh +
                        (-structuralVacancy[dto]*slopeMain + structuralVacHigh * slopeMain) +
                        slopeHigh * vacRate[dto][region];
            }
            changeRate = Math.min(changeRate, 1f + maxDelta);
            changeRate = Math.max(changeRate, 1f - maxDelta);
            double newPrice = currentPrice * changeRate;

            if (dd.getId() == SiloUtil.trackDd) SiloUtil.trackWriter.println("The monthly costs of dwelling " +
                    dd.getId() + " was changed from " + currentPrice + " to " + newPrice + " (in 2000$).");
            dd.setPrice((int) (newPrice + 0.5));
            cnt[dto]++;
            sumOfPrices[dto] += newPrice;

            String token = dto+"_"+vacRate[dto][region]+"_"+currentPrice+"_"+newPrice;
            if (priceChange.containsKey(token)) priceChange.put(token, (priceChange.get(token) + 1));
            else priceChange.put(token, 1);

        }
        double[] averagePrice = new double[DwellingType.values().length];
        for (DwellingType dt: DwellingType.values()) {
            int dto = dt.ordinal();
            averagePrice[dto] = sumOfPrices[dto] / cnt[dto];
        }
        realEstateData.setAvePriceByDwellingType(averagePrice);

        PrintWriter pw = SiloUtil.openFileForSequentialWriting(("priceUpdate"+String.valueOf(year)+".csv"), false);
        pw.println("type,regVacRate,oldPrice,newPrice,frequency");
        for (String token: priceChange.keySet()) {
            String[] values = token.split("_");
            for (String val: values) pw.print(val + ",");
            pw.println(priceChange.get(token));
        }
        pw.close();
    }
}
