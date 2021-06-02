package de.tum.bgu.msm.scenarios.health.emission;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vehicles.*;

import java.util.ArrayList;
import java.util.List;

public class CreateVehicles {

    private static final Logger logger = Logger.getLogger(CreateVehicles.class);
    //Create scenario
    private final Scenario sc;

    //Create vehicle container
    private Vehicles vehicles = VehicleUtils.createVehiclesContainer();
    private VehicleType type;
    //Generating vehicle type category
    private VehicleType.DoorOperationMode mode = VehicleType.DoorOperationMode.serial;

    public CreateVehicles(Scenario sc) {
        this.sc = sc;
    }

    public void run(String eventFileWithoutEmissions, String individualVehicleFile){
            runVehicleType();
            runVehicle(eventFileWithoutEmissions, individualVehicleFile);

    }

    private void runVehicleType() {
            createVehicleType("HGV",mode,7.2,1.0,1.0,1.0,"BEGIN_EMISSIONSHEAVY_GOODS_VEHICLE;average;average;averageEND_EMISSIONS");

            //LCV is not in vehicle types average
            //createVehicleType("LCV",mode,6.2,1.0,1.0,1.0,"BEGIN_EMISSIONSPASSENGER_CAR;petrol (4S);&gt;=2L;PC-P-Euro-1END_EMISSIONS");
            createVehicleType("pass. car",mode,5.2,1.0,1.0,1.0, "BEGIN_EMISSIONSPASSENGER_CAR;average;average;averageEND_EMISSIONS");
            createVehicleType("ZERO_EMISSION_VEHICLE", mode, 2, 1, 1, 1, "BEGIN_EMISSIONSZERO_EMISSION_VEHICLE;average;average;averageEND_EMISSIONS");
    }

    private void createVehicleType(String name, VehicleType.DoorOperationMode mode, double length, double width, double accessTime, double egressTime, String description) {

            Id<VehicleType> typ1 = Id.create(name, VehicleType.class);
            type = VehicleUtils.getFactory().createVehicleType(typ1);
            type.setDoorOperationMode(mode);
            type.setLength(length);
            type.setWidth(width);
            //type.setAccessTime(accessTime);
            //type.setEgressTime(egressTime);
            type.setDescription(description);
            vehicles.addVehicleType(type);
            sc.getVehicles().addVehicleType(type);
    }

    private void runVehicle(String eventFile, String vehicleFile) {
            //Read in events file and find out every agent by its id
            List<Id<Vehicle>> listOfIds = new ArrayList<>();

            EventsManager eventsManager = EventsUtils.createEventsManager();
            FindAgentsFromEventsHandler findAgentsFromEventsHandler = new FindAgentsFromEventsHandler(listOfIds);
            eventsManager.addHandler(findAgentsFromEventsHandler);
            new MatsimEventsReader(eventsManager).readFile(eventFile);

            logger.info("Found " + listOfIds.size() + " different agents. Assign vehicle types according to their id.");

            for (Id<Vehicle> vehicleId : listOfIds) {
                String id = vehicleId.toString();
                Id<Vehicle> vehId = Id.createVehicleId(id);
                Vehicle vehicle;

                if(id.contains("LD")){
                    vehicle = VehicleUtils.getFactory().createVehicle(vehId, vehicles.getVehicleTypes().get(Id.create("HGV", VehicleType.class)));
                    vehicles.addVehicle(vehicle);
                } else if (id.contains("SD")){
                    vehicle = VehicleUtils.getFactory().createVehicle(vehId, vehicles.getVehicleTypes().get(Id.create("HGV", VehicleType.class)));
                    vehicles.addVehicle(vehicle);
                } else if (id.contains("van")){
                    vehicle = VehicleUtils.getFactory().createVehicle(vehId, vehicles.getVehicleTypes().get(Id.create("HGV", VehicleType.class)));
                    vehicles.addVehicle(vehicle);
                } else if (id.contains("cargoBike")){
                    vehicle = VehicleUtils.getFactory().createVehicle(vehId, vehicles.getVehicleTypes().get(Id.create("ZERO_EMISSION_VEHICLE", VehicleType.class)));
                    vehicles.addVehicle(vehicle);
                } else {
                    vehicle = VehicleUtils.getFactory().createVehicle(vehId, vehicles.getVehicleTypes().get(Id.create("pass. car", VehicleType.class)));
                    vehicles.addVehicle(vehicle);
                }
            }

            new VehicleWriterV1(vehicles).writeFile(vehicleFile);
    }
}
