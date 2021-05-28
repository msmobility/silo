package de.tum.bgu.msm.models.relocation.moves;

public class DefaultMovesStrategy implements MovesStrategy {

    public double getMovingProbability(double householdSatisfaction, double currentDwellingUtility) {
        return 1 - 1/(1+0.03 * Math.exp(10*(householdSatisfaction - currentDwellingUtility)));
    }
}
