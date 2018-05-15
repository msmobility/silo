package de.tum.bgu.msm.models.autoOwnership;

import java.util.Map;

public interface CreateCarOwnershipModel {
    void initialize();
    int[] updateCarOwnership(Map<Integer, int[]> updatedHouseholds);
}
