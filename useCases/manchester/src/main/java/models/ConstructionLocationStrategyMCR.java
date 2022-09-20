package models;

import de.tum.bgu.msm.data.ManchesterDwellingTypes;
import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypes.DefaultDwellingTypeImpl;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.models.realEstate.construction.ConstructionLocationStrategy;

public final class ConstructionLocationStrategyMCR implements ConstructionLocationStrategy {

    @Override
    public double calculateConstructionProbability(DwellingType dwellingType, double price, double accessibility) {
        double alpha;
        double gamma;
        if(dwellingType.equals(ManchesterDwellingTypes.DwellingTypeManchester.SFD)) {
            alpha = 0.5;
            gamma = 0.5;
        } else if(dwellingType.equals(ManchesterDwellingTypes.DwellingTypeManchester.SFA)) {
            alpha = 0.4;
            gamma = 0.6;
        } else if(dwellingType.equals(ManchesterDwellingTypes.DwellingTypeManchester.MF234)) {
            alpha = 0.3;
            gamma = 0.7;
        } else if(dwellingType.equals(ManchesterDwellingTypes.DwellingTypeManchester.MF5plus)) {
            alpha = 0.25;
            gamma = 0.75;
        } else {
            throw new Error("Undefined dwelling type " + dwellingType + " provided!");
        }

        double priceUtility = price * alpha;
        double accessibilityUtility = accessibility * gamma;
        return priceUtility + accessibilityUtility;
    }
}
