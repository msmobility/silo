package de.tum.bgu.msm.kagawa.pricing;

import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypes;
import de.tum.bgu.msm.models.pricing.PricingStrategyTak;
import org.junit.Test;

public class PricingStrategyTakTest {

    @Test
    public void testPricingModelTak() {
        final PricingStrategyTak pricingStrategyTak = new PricingStrategyTak();
        pricingStrategyTak.getPriceChangeRate(0.09, DefaultDwellingTypes.DefaultDwellingTypeImpl.SFD.getStructuralVacancyRate());
    }
}
