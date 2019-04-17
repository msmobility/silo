package de.tum.bgu.msm.models.demography.birth;

public interface BirthStrategy {
    double calculateBirthProbability(int personAge, int numberOfChildren);

    double getProbabilityForGirl();
}
