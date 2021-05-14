package models;

import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.models.realEstate.construction.ConstructionLocationStrategy;

public class FabilandConstructionLocationStrategy implements ConstructionLocationStrategy {

    public static final double ALPHA = 0.5;
    public static final double GAMMA = 0.5;

    @Override
    public double calculateConstructionProbability(DwellingType dwellingType, double avgPrice, double autoAccessibility) {
        double priceUtility = avgPrice * ALPHA;
        double accessibilityUtility = autoAccessibility * GAMMA;
        return priceUtility + accessibilityUtility;
    }
}