package de.tum.bgu.msm.autoOwnership;

import java.util.Map;

public interface CarOwnershipModel {
    void initialize();
    void updateCarOwnership(Map<Integer, int[]> updatedHouseholds);
}
