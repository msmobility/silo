package de.tum.bgu.msm.data.household;

import de.tum.bgu.msm.data.vehicle.Car;
import de.tum.bgu.msm.data.vehicle.VehicleType;

public class HouseholdFactoryMuc implements HouseholdFactory {

    @Override
    public HouseholdMuc createHousehold(int id, int dwellingID, int autos) {
        return new HouseholdMuc(id, dwellingID, autos);
    }

    @Override
    public HouseholdMuc duplicate(Household original, int nextHouseholdId) {
        HouseholdMuc duplicate = new HouseholdMuc(nextHouseholdId, -1, 0);

        original.getVehicles().forEach(vv-> {
            if (vv.getType().equals(VehicleType.CAR)){
                duplicate.getVehicles().add(new Car(vv.getId(), ((Car) vv).getCarType(), vv.getAge()));
            }
        });
        //duplicate.setAutonomous(((HouseholdMuc)original).getAutonomous());

        return duplicate;
    }
}
