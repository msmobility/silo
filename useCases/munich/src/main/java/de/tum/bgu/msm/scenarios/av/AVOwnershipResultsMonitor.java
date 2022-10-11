package de.tum.bgu.msm.scenarios.av;

import com.google.common.collect.Multiset;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdMuc;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.vehicle.Car;
import de.tum.bgu.msm.data.vehicle.CarType;
import de.tum.bgu.msm.data.vehicle.VehicleType;
import de.tum.bgu.msm.events.MicroEvent;
import de.tum.bgu.msm.io.output.ResultsMonitor;
import de.tum.bgu.msm.models.ModelUpdateListener;
import de.tum.bgu.msm.models.carOwnership.SwitchToAutonomousVehicleModelMuc;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class AVOwnershipResultsMonitor implements ResultsMonitor {

    private SwitchToAutonomousVehicleModelMuc switchToAutonomousVehicleModelMuc = null;
    private DataContainer dataContainer;
    private final Properties properties;
    private PrintWriter resultWriter;
    private final static Logger logger = Logger.getLogger(SwitchToAutonomousVehicleModelMuc.class);

    public AVOwnershipResultsMonitor(ModelContainer modelContainer, DataContainer dataContainer, Properties properties) {
        this.dataContainer = dataContainer;
        this.properties = properties;
        for (ModelUpdateListener listener : modelContainer.getModelUpdateListeners()){
            if (listener != null && listener.getClass().equals(SwitchToAutonomousVehicleModelMuc.class)) {
                switchToAutonomousVehicleModelMuc = (SwitchToAutonomousVehicleModelMuc) listener;
                break;
            }
        }

    }


    @Override
    public void setup() {
        if (switchToAutonomousVehicleModelMuc != null){
            String pathname = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName + "/siloResults/avOwnership.csv";
            try {
                resultWriter = new PrintWriter(new File(pathname));
                resultWriter.println("year,hhs,autos,avs,events");
            } catch (FileNotFoundException e) {
                logger.error("Cannot write the result file: " + pathname, e);
            }
        }

    }

    @Override
    public void endYear(int year, Multiset<Class<? extends MicroEvent>> eventCounter, List<MicroEvent> events) {
        Map<String, Integer> summaryForThisYear = switchToAutonomousVehicleModelMuc.getSummaryForThisYear();
        resultWriter.print(year);
        resultWriter.print(",");
        resultWriter.print(summaryForThisYear.get("hh"));
        resultWriter.print(",");
        resultWriter.print(summaryForThisYear.get("autos"));
        resultWriter.print(",");
        resultWriter.print(summaryForThisYear.get("avs"));
        resultWriter.print(",");
        resultWriter.print(summaryForThisYear.get("events"));
        resultWriter.println();
        resultWriter.flush();

        if (properties.transportModel.transportModelYears.contains(year)){
            printOutMicroData(year);
        }

    }

    @Override
    public void endSimulation() {

        resultWriter.close();
        int year = properties.main.endYear;

        printOutMicroData(year);


    }

    private void printOutMicroData(int year) {
        if (switchToAutonomousVehicleModelMuc != null){

            String pathname = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName + "/siloResults/avOwnershipByHh_" + year + ".csv";
            try {
                PrintWriter microDataResultWriter = new PrintWriter(new File(pathname));
                microDataResultWriter.println("person,household,autos,avs,homeZone,jobZone,timeCar");
                for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()){
                    HouseholdMuc householdMuc = (HouseholdMuc) hh;
                    for (Person person : householdMuc.getPersons().values()){
                        if (person.getOccupation() == Occupation.EMPLOYED && person.getJobId() != -2){
                            microDataResultWriter.print(person.getId());
                            microDataResultWriter.print(",");
                            microDataResultWriter.print(householdMuc.getId());
                            microDataResultWriter.print(",");
                            microDataResultWriter.print(householdMuc.getVehicles().stream().filter(vv-> vv.getType().equals(VehicleType.CAR)).count());
                            microDataResultWriter.print(",");
                            microDataResultWriter.print(householdMuc.getVehicles().stream().filter(vv-> vv.getType().equals(VehicleType.CAR)).filter(vv -> ((Car) vv).getCarType().equals(CarType.AUTONOMOUS)).count());
                            microDataResultWriter.print(",");
                            Dwelling dwelling = dataContainer.getRealEstateDataManager().getDwelling(householdMuc.getDwellingId());
                            microDataResultWriter.print(dwelling.getZoneId());
                            microDataResultWriter.print(",");
                            Job job = dataContainer.getJobDataManager().getJobFromId(person.getJobId());
                            microDataResultWriter.print(job.getZoneId());
                            microDataResultWriter.print(",");
                            microDataResultWriter.print(dataContainer.getTravelTimes().getTravelTime(dwelling, job, properties.transportModel.peakHour_s, TransportMode.car));
                            microDataResultWriter.println();
                        }
                    }

                }
                microDataResultWriter.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
