package de.tum.bgu.msm.models.autoOwnership;

import java.util.Map;

public interface CarOwnershipModel {
    void initialize();
    int[] updateCarOwnership(Map<Integer, int[]> updatedHouseholds);
}
