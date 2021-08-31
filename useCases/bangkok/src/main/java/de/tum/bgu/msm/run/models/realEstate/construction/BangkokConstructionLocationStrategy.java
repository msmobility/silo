package de.tum.bgu.msm.run.models.realEstate.construction;

import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypes.DefaultDwellingTypeImpl;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.models.realEstate.construction.ConstructionLocationStrategy;
import de.tum.bgu.msm.run.data.dwelling.BangkokDwellingTypes;

public final class BangkokConstructionLocationStrategy implements ConstructionLocationStrategy {

    @Override
    public double calculateConstructionProbability(DwellingType dwellingType, double price, double accessibility) {
        double alpha;
        double gamma;
        if(dwellingType.equals(BangkokDwellingTypes.DwellingTypeBangkok.DETATCHED_HOUSE_120)) {
            alpha = 0.5;
            gamma = 0.5;
        } else if(dwellingType.equals(BangkokDwellingTypes.DwellingTypeBangkok.DETATCHED_HOUSE_200)) {
            alpha = 0.4;
            gamma = 0.6;
        } else if(dwellingType.equals(BangkokDwellingTypes.DwellingTypeBangkok.LOW_RISE_CONDOMINIUM_30)) {
            alpha = 0.3;
            gamma = 0.7;
        } else if(dwellingType.equals(BangkokDwellingTypes.DwellingTypeBangkok.LOW_RISE_CONDOMINIUM_50)) {
            alpha = 0.25;
            gamma = 0.75;
        } else if(dwellingType.equals(BangkokDwellingTypes.DwellingTypeBangkok.HIGH_RISE_CONDOMINIUM_30)) {
            alpha = 0.2;
            gamma = 0.8;
        } else if(dwellingType.equals(BangkokDwellingTypes.DwellingTypeBangkok.HIGH_RISE_CONDOMINIUM_50)) {
            alpha = 0.2;
            gamma = 0.8;
        } else {
            throw new Error("Undefined dwelling type " + dwellingType + " provided!");
        }

        double priceUtility = price * alpha;
        double accessibilityUtility = accessibility * gamma;
        return priceUtility + accessibilityUtility;
    }
}
