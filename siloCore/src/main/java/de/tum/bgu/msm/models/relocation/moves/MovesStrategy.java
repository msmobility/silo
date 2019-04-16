package de.tum.bgu.msm.models.relocation.moves;

public interface MovesStrategy {
    double getMovingProbability(double avgSatisfaction, double currentUtil);
}
