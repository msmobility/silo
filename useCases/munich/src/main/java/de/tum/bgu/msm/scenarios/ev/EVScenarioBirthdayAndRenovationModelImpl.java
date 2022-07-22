package de.tum.bgu.msm.scenarios.ev;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.vehicle.Car;
import de.tum.bgu.msm.data.vehicle.CarType;
import de.tum.bgu.msm.data.vehicle.Vehicle;
import de.tum.bgu.msm.data.vehicle.VehicleUtil;
import de.tum.bgu.msm.events.impls.person.VehicleBirthdayEvent;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.EventModel;
import de.tum.bgu.msm.models.demography.birthday.VehicleBirthdayAndRenovationModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class EVScenarioBirthdayAndRenovationModelImpl extends AbstractModel implements VehicleBirthdayAndRenovationModel {

    public EVScenarioBirthdayAndRenovationModelImpl(DataContainer dataContainer, Properties properties, Random random) {
        super(dataContainer, properties, random);
    }

    private int year;

    @Override
    public void setup() {
    }

    @Override
    public void prepareYear(int year) {
        this.year = year;
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


        int yearWhenNewVehiclesWillBeEV = 2030;
        int yearWhenCVsAreNotPermitted = 2050;


        // increase age of this person by one year

        if (event.getVehicle() instanceof Car) {
            int age = event.getVehicle().getAge();

            double probability = 0.0077 * age;

            if (SiloUtil.getRandomObject().nextDouble() < probability) {
                event.getHousehold().getVehicles().remove(event.getVehicle());
                CarType carType;
                if (year < yearWhenNewVehiclesWillBeEV){
                    carType = ((Car) event.getVehicle()).getCarType();
                } else {
                    carType = CarType.ELECTRIC;
                }
                event.getHousehold().getVehicles().add(new Car(VehicleUtil.getHighestVehicleIdInHousehold(event.getHousehold()),
                        carType, VehicleUtil.getVehicleAgeWhenReplaced()));

            } else {
                if (year < yearWhenCVsAreNotPermitted){
                    ((Car) event.getVehicle()).increaseAgeByOne();
                } else {
                    event.getHousehold().getVehicles().remove(event.getVehicle());
                    CarType carType = CarType.ELECTRIC;
                    event.getHousehold().getVehicles().add(new Car(VehicleUtil.getHighestVehicleIdInHousehold(event.getHousehold()),
                            carType, VehicleUtil.getVehicleAgeWhenReplaced()));
                }
            }
        }

        return true;
    }


}
