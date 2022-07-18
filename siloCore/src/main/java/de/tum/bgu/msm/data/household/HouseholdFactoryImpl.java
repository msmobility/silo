package de.tum.bgu.msm.data.household;

import de.tum.bgu.msm.data.vehicle.Car;
import de.tum.bgu.msm.data.vehicle.VehicleType;

public class HouseholdFactoryImpl implements HouseholdFactory {
    @Override
    public HouseholdImpl createHousehold(int id, int dwellingID, int autos) {
        return new HouseholdImpl(id, dwellingID, autos);
    }

    @Override
    public HouseholdImpl duplicate(Household original, int nextHouseholdId) {
        HouseholdImpl duplicate = new HouseholdImpl(nextHouseholdId, original.getDwellingId(), 0);

        original.getVehicles().forEach(vv -> {
            if (vv.getType().equals(VehicleType.CAR)){
                duplicate.getVehicles().add(new Car(vv.getId(), ((Car) vv).getCarType(), vv.getAge()));
            }
        });


        return duplicate;
    }
}
