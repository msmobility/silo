package de.tum.bgu.msm.data.household;

public class HouseholdFactoryCapeTown implements HouseholdFactory {
    @Override
    public HouseholdCapeTown createHousehold(int id, int dwellingID, int autos) {
        return new HouseholdCapeTown(id, dwellingID, autos);
    }

    @Override
    public Household duplicate(Household original, int nextHouseholdId) {
        return new HouseholdCapeTown(nextHouseholdId, -1, original.getAutos());
    }
}
