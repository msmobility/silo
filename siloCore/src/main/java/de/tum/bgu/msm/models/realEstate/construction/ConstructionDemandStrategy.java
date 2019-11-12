package de.tum.bgu.msm.models.realEstate.construction;

import de.tum.bgu.msm.data.dwelling.DwellingType;

public interface ConstructionDemandStrategy {
    double calculateConstructionDemand(double vacancyRate, DwellingType dt, int numberOfExistingDwellings);
}
