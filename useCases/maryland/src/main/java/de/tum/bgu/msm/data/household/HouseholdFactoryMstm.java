package de.tum.bgu.msm.data.household;

public class HouseholdFactoryMstm implements HouseholdFactory {

    @Override
    public MarylandHousehold createHousehold(int id, int dwellingID, int autos) {
        return new MarylandHousehold(id, dwellingID, autos);
    }

    @Override
    public MarylandHousehold duplicate(Household original, int nextHouseholdId) {
        final MarylandHousehold marylandHousehold = new MarylandHousehold(nextHouseholdId, -1, original.getAutos());
        return marylandHousehold;
    }
}
