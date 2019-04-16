package de.tum.bgu.msm.models.autoOwnership;

import de.tum.bgu.msm.data.household.Household;

public interface CreateCarOwnershipModel {
    void run();

    void simulateCarOwnership(Household hh);
}
