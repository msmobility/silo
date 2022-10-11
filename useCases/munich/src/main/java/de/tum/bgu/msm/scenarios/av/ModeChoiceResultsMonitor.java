package de.tum.bgu.msm.scenarios.av;

import com.google.common.collect.Multiset;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdMuc;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.vehicle.Car;
import de.tum.bgu.msm.data.vehicle.CarType;
import de.tum.bgu.msm.data.vehicle.VehicleType;
import de.tum.bgu.msm.events.MicroEvent;
import de.tum.bgu.msm.io.output.ResultsMonitor;
import de.tum.bgu.msm.models.modeChoice.CommuteModeChoice;
import de.tum.bgu.msm.models.modeChoice.CommuteModeChoiceMapping;
import de.tum.bgu.msm.properties.Properties;
import org.matsim.api.core.v01.TransportMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class ModeChoiceResultsMonitor implements ResultsMonitor {


    private final Properties properties;
    private PrintWriter pw;
    private final DataContainer dataContainer;

    public ModeChoiceResultsMonitor(DataContainer dataContainer, Properties properties) {
        this.properties = properties;
        this.dataContainer = dataContainer;
    }

    @Override
    public void setup() {
        String pathname = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName + "/siloResults/";
        try {
            File file = new File(pathname + "modeChoiceMicroData.csv");
            file.getParentFile().mkdirs();
            pw = new PrintWriter(file);
            pw.print("year");
            pw.print(",");
            pw.print("pp");
            pw.print(",");
            pw.print("hh");
            pw.print(",");
            pw.print("mode");
            pw.print(",");
            pw.print("utility");
            pw.print(",");
            pw.print("autos");
            pw.print(",");
            pw.print("avs");
            pw.print(",");
            pw.print("homeZone");
            pw.print(",");
            pw.print("workZone");
            pw.print(",");
            pw.print("timeCar");
            pw.print(",");
            pw.print("timePt");
            pw.print(",");
            pw.print("parkingAtHome");
            pw.print(",");
            pw.print("parkingAtWork");
            pw.println();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void endYear(int year, Multiset<Class<? extends MicroEvent>> eventCounter, List<MicroEvent> events) {

        for (Household household : dataContainer.getHouseholdDataManager().getHouseholds()) {

            household.getAttribute("COMMUTE_MODE_CHOICE_MAPPING").ifPresent(cmcm -> {
                CommuteModeChoiceMapping commuteModeChoiceMapping = (CommuteModeChoiceMapping) cmcm;
                for (Person person : household.getPersons().values()){

                    CommuteModeChoiceMapping.CommuteMode mode = commuteModeChoiceMapping.getMode(person);
                    Dwelling dd = dataContainer.getRealEstateDataManager().getDwelling(household.getDwellingId());
                    Job jj = dataContainer.getJobDataManager().getJobFromId(person.getJobId());

                    if (mode != null && jj != null && dd != null){
                        pw.print(year);
                        pw.print(",");
                        pw.print(person.getId());
                        pw.print(",");
                        pw.print(household.getId());
                        pw.print(",");
                        pw.print(mode.mode);
                        pw.print(",");
                        pw.print(mode.utility);
                        pw.print(",");
                        pw.print(household.getVehicles().stream().filter(vv -> vv.getType().equals(VehicleType.CAR)).count());
                        pw.print(",");
                        try {
                            pw.print(household.getVehicles().stream().
                                    filter(vv -> vv.getType().equals(VehicleType.CAR)).
                                    filter(vv-> ((Car) vv).getCarType().equals(CarType.AUTONOMOUS)).count());
                        } catch (ClassCastException e){
                            pw.print("no-avs");
                        }
                        pw.print(",");

                        pw.print(dd.getZoneId());
                        pw.print(",");
                        pw.print(jj.getZoneId());
                        pw.print(",");
                        pw.print(dataContainer.getTravelTimes().getTravelTime(dd, jj, properties.transportModel.peakHour_s, TransportMode.car));
                        pw.print(",");
                        pw.print(dataContainer.getTravelTimes().getTravelTime(dd, jj, properties.transportModel.peakHour_s, TransportMode.pt));
                        pw.print(",");
                        int parkingAtHome = (int) dd.getAttribute("PARKING_SPACES").orElse(-1);
                        pw.print(parkingAtHome);
                        pw.print(",");
                        LocationParkingData parking = (LocationParkingData) dataContainer.getGeoData().getZones().get(jj.getZoneId()).getAttributes().get("PARKING");
                        pw.print(parking.getParkingQuality());
                        pw.print(",");
                        pw.println();

                    }

                }

            });
        }
    }

    @Override
    public void endSimulation() {
        pw.close();

    }
}
