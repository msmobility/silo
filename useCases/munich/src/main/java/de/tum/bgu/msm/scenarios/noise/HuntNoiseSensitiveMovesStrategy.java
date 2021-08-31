package de.tum.bgu.msm.scenarios.noise;

import de.tum.bgu.msm.models.relocation.moves.MovesStrategy;

public class HuntNoiseSensitiveMovesStrategy implements MovesStrategy {
    @Override
    public double getMovingProbability(double avgSatisfaction, double currentUtil) {
        return 1 - 1/(1+0.03 * Math.exp(0.5*(avgSatisfaction - currentUtil)));
    }
}
