package de.tum.bgu.msm.data.household;

import de.tum.bgu.msm.data.vehicle.Car;
import de.tum.bgu.msm.data.vehicle.VehicleType;

public class HouseholdFactoryBerlinBrandenburg implements HouseholdFactory {

    @Override
    public HouseholdBerlinBrandenburg createHousehold(int id, int dwellingID, int autos) {
        return new HouseholdBerlinBrandenburg(id, dwellingID, autos);
    }

    @Override
    public HouseholdBerlinBrandenburg duplicate(Household original, int nextHouseholdId) {
        HouseholdBerlinBrandenburg duplicate = new HouseholdBerlinBrandenburg(nextHouseholdId, -1, 0);

        original.getVehicles().forEach(vv-> {
            if (vv.getType().equals(VehicleType.CAR)){
                duplicate.getVehicles().add(new Car(vv.getId(), ((Car) vv).getCarType(), vv.getAge()));
            }
        });
        //duplicate.setAutonomous(((HouseholdBerlinBrandenburg)original).getAutonomous());

        return duplicate;
    }
}
