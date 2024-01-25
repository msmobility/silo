package de.tum.bgu.msm.models.bikeOwnership;

import de.tum.bgu.msm.data.household.Household;

public interface CreateBikeOwnershipModel {
    void run();

    void simulateBikeOwnership(Household hh);
}
