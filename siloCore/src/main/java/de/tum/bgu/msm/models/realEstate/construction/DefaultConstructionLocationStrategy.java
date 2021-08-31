package de.tum.bgu.msm.models.realEstate.construction;

import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypes.DefaultDwellingTypeImpl;
import de.tum.bgu.msm.data.dwelling.DwellingType;

public final class DefaultConstructionLocationStrategy implements ConstructionLocationStrategy{

    @Override
    public double calculateConstructionProbability(DwellingType dwellingType, double price, double accessibility) {
        double alpha;
        double gamma;
        if(dwellingType.equals(DefaultDwellingTypeImpl.SFD)) {
            alpha = 0.5;
            gamma = 0.5;
        } else if(dwellingType.equals(DefaultDwellingTypeImpl.SFA)) {
            alpha = 0.4;
            gamma = 0.6;
        } else if(dwellingType.equals(DefaultDwellingTypeImpl.MF234)) {
            alpha = 0.3;
            gamma = 0.7;
        } else if(dwellingType.equals(DefaultDwellingTypeImpl.MF5plus)) {
            alpha = 0.25;
            gamma = 0.75;
        } else if(dwellingType.equals(DefaultDwellingTypeImpl.MH)) {
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
