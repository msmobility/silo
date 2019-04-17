package de.tum.bgu.msm.data.household;

public class HouseholdFactoryMuc implements HouseholdFactory {

    @Override
    public HouseholdMuc createHousehold(int id, int dwellingID, int autos) {
        return new HouseholdMuc(id, dwellingID, autos);
    }

    @Override
    public HouseholdMuc duplicate(Household original, int nextHouseholdId) {
        HouseholdMuc duplicate = new HouseholdMuc(nextHouseholdId, -1, original.getAutos());
        duplicate.setAutonomous(((HouseholdMuc)original).getAutonomous());
        return duplicate;
    }
}
