package de.tum.bgu.msm.models.realEstate.renovation;

public interface RenovationStrategy {
    double calculateRenovationProbability(int oldQuality, int newQuality);
}
