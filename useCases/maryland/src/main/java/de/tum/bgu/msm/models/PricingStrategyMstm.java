package de.tum.bgu.msm.models;

import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingMstm;
import de.tum.bgu.msm.models.realEstate.pricing.DefaultPricingStrategy;

public class PricingStrategyMstm extends DefaultPricingStrategy {

    @Override
    public boolean isPriceUpdateAllowed(Dwelling dd) {
        // dwelling is under affordable-housing constraints,
        // rent cannot be raised
        return ((DwellingMstm)dd).getRestriction() != 0;
    }
}
