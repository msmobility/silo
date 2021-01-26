package de.tum.bgu.msm.data.household;

public class HouseholdFactoryMstm implements HouseholdFactory {

    @Override
    public HouseholdMstm createHousehold(int id, int dwellingID, int autos) {
        return new HouseholdMstm(id, dwellingID, autos);
    }

    @Override
    public HouseholdMstm duplicate(Household original, int nextHouseholdId) {
        final HouseholdMstm householdMstm = new HouseholdMstm(nextHouseholdId, -1, original.getAutos());
        return householdMstm;
    }
}
