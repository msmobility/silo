package de.tum.bgu.msm.models.demography.birthday;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.vehicle.Car;
import de.tum.bgu.msm.data.vehicle.Vehicle;
import de.tum.bgu.msm.data.vehicle.VehicleUtil;
import de.tum.bgu.msm.events.impls.person.VehicleBirthdayEvent;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.EventModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class VehicleBirthdayAndRenovationModelImpl extends AbstractModel implements VehicleBirthdayAndRenovationModel {

    public VehicleBirthdayAndRenovationModelImpl(DataContainer dataContainer, Properties properties, Random random) {
        super(dataContainer, properties, random);
    }

    @Override
    public void setup() {
    }

    @Override
    public void prepareYear(int year) {
    }


    public Collection<VehicleBirthdayEvent> getEventsForCurrentYear(int year) {
        List<VehicleBirthdayEvent> events = new ArrayList<>();
        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()) {
            for (Vehicle vv : hh.getVehicles()) {
                events.add(new VehicleBirthdayEvent(hh, vv));
            }
        }
        return events;
    }

    @Override
    public boolean handleEvent(VehicleBirthdayEvent event) {
        return checkVehicleBirthday(event);
    }


    @Override
    public void endYear(int year) {

    }

    @Override
    public void endSimulation() {

    }

    private boolean checkVehicleBirthday(VehicleBirthdayEvent event) {
        // increase age of this person by one year

        if (event.getVehicle() instanceof Car) {
            int age = event.getVehicle().getAge();

            double probability = 0.0077 * age;

            if (SiloUtil.getRandomObject().nextDouble() < probability) {
                event.getHousehold().getVehicles().remove(event.getVehicle());
                event.getHousehold().getVehicles().add(new Car(VehicleUtil.getHighestVehicleIdInHousehold(event.getHousehold()),
                        ((Car) event.getVehicle()).getCarType(), VehicleUtil.getVehicleAgeWhenReplaced()));

            } else {

                ((Car) event.getVehicle()).increaseAgeByOne();
            }
        }

        return true;
    }


}
