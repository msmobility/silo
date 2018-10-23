package de.tum.bgu.msm.data.household;

public class HouseholdFactoryImpl implements HouseholdFactory {
    @Override
    public Household createHousehold(int id, int dwellingID, int autos) {
        return new HouseholdImpl(id, dwellingID, autos);
    }
}
