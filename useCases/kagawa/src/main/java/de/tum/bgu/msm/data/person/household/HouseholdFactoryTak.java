package de.tum.bgu.msm.data.person.household;

import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdFactory;

public class HouseholdFactoryTak implements HouseholdFactory {

    @Override
    public HouseholdTak createHousehold(int id, int dwellingID, int autos) {
        return new HouseholdTak(id, dwellingID, autos);
    }

    @Override
    public HouseholdTak duplicate(Household original, int nextHouseholdId) {
        HouseholdTak duplicate = new HouseholdTak(nextHouseholdId, -1, original.getAutos());
        duplicate.setAutonomous(((HouseholdTak)original).getAutonomous());
        return duplicate;
    }
}
