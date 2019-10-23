package de.tum.bgu.msm.models.realEstate;

import de.tum.bgu.msm.models.realEstate.pricing.DefaultPricingStrategy;
import de.tum.bgu.msm.models.realEstate.pricing.PricingModel;
import de.tum.bgu.msm.models.realEstate.pricing.PricingStrategy;
import org.junit.Test;

public class PricingStrategyTest {



    @Test
    public final void pricingStrategyTest(){


        PricingStrategy strategy = new DefaultPricingStrategy();

        double structuralVacancy = 0.02;

        double intervalWidth = 0.001;
//        for (int i = 0; i < 100; i++){
//
//            double vacancyRateAtThisRegion = i * intervalWidth;
//            double changeRate = strategy.getPriceChangeRate(vacancyRateAtThisRegion, structuralVacancy);
//            System.out.println(vacancyRateAtThisRegion + "," +  changeRate);
//        }





    }

}
