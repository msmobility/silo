package de.tum.bgu.msm.models;

import de.tum.bgu.msm.data.MelbourneDwellingTypes;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.models.realEstate.construction.ConstructionLocationStrategy;

public final class ConstructionLocationStrategyMEL implements ConstructionLocationStrategy {

    @Override
    public double calculateConstructionProbability(DwellingType dwellingType, double price, double accessibility) {
        double alpha;
        double gamma;
        if(dwellingType.equals(MelbourneDwellingTypes.DwellingTypeMelbourne.SFD)) {
            alpha = 0.5;
            gamma = 0.5;
        } else if(dwellingType.equals(MelbourneDwellingTypes.DwellingTypeMelbourne.SFA)) {
            alpha = 0.4;
            gamma = 0.6;
        } else if(dwellingType.equals(MelbourneDwellingTypes.DwellingTypeMelbourne.FLAT)) {
            alpha = 0.3;
            gamma = 0.7;
        } else if(dwellingType.equals(MelbourneDwellingTypes.DwellingTypeMelbourne.MH)) {
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
