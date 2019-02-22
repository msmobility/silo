package de.tum.bgu.msm.data.household;

public interface HouseholdFactory {

    Household createHousehold(int id, int dwellingID, int autos);

    Household duplicate(Household original, int nextHouseholdId);
}
