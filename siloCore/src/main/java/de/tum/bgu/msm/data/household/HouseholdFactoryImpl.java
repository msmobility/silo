package de.tum.bgu.msm.data.household;

public class HouseholdFactoryImpl implements HouseholdFactory {
    @Override
    public HouseholdImpl createHousehold(int id, int dwellingID, int autos) {
        return new HouseholdImpl(id, dwellingID, autos);
    }

    @Override
    public HouseholdImpl duplicate(Household original, int nextHouseholdId) {
        HouseholdImpl duplicate = new HouseholdImpl(nextHouseholdId, original.getDwellingId(), original.getAutos());
        return duplicate;
    }
}
