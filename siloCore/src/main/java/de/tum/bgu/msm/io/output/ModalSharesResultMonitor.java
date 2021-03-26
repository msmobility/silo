package de.tum.bgu.msm.io.output;

import com.google.common.collect.Multiset;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.events.MicroEvent;
import de.tum.bgu.msm.models.modeChoice.CommuteModeChoiceMapping;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ModalSharesResultMonitor implements ResultsMonitor {

    private final static Logger logger = Logger.getLogger(ModalSharesResultMonitor.class);

    private final Properties properties;
    private PrintWriter pw;
    private final DataContainer dataContainer;

    public ModalSharesResultMonitor(DataContainer dataContainer, Properties properties) {
        this.properties = properties;
        this.dataContainer = dataContainer;
    }

    @Override
    public void setup() {
        String pathname = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName + "/siloResults/";
        try {
            File file = new File(pathname + "modalShares.csv");
            file.getParentFile().mkdirs();
            pw = new PrintWriter(file);
            pw.print("year");
            pw.print(",");
            pw.print("zone");
            pw.print(",");
            pw.print("tripsCar");
            pw.print(",");
            pw.print("tripsPt");
            pw.print(",");
            pw.print("tripsOther");
            pw.print(",");
            pw.print("totalTimeCar");
            pw.print(",");
            pw.print("totalTimePt");
            pw.print(",");
            pw.print("totalTimeOther");
            pw.print(",");
            pw.print("doNotTravel");
            pw.println();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void endYear(int year, Multiset<Class<? extends MicroEvent>> eventCounter, List<MicroEvent> events) {

        logger.info("Printing out modal shares by zone");

        Map<Integer, Integer> tripsByCar = new HashMap<>();
        Map<Integer, Integer> tripsByPt = new HashMap<>();
        Map<Integer, Integer>tripsByOther = new HashMap<>();
        Map<Integer, Double> timeByCar = new HashMap<>();
        Map<Integer, Double> timeByPt = new HashMap<>();
        Map<Integer, Double> timeByOther = new HashMap<>();
        Map<Integer, Integer> doNotTravel = new HashMap<>();

        AtomicInteger car = new AtomicInteger();
        AtomicInteger pt = new AtomicInteger();
        AtomicInteger other = new AtomicInteger();

        for (Household household : dataContainer.getHouseholdDataManager().getHouseholds()) {

            household.getAttribute("COMMUTE_MODE_CHOICE_MAPPING").ifPresent(cmcm -> {
                CommuteModeChoiceMapping commuteModeChoiceMapping = (CommuteModeChoiceMapping) cmcm;
                for (Person person : household.getPersons().values()) {
                    CommuteModeChoiceMapping.CommuteMode commuteMode = commuteModeChoiceMapping.getMode(person);
                    Dwelling dd = dataContainer.getRealEstateDataManager().getDwelling(household.getDwellingId());
                    Job jj = dataContainer.getJobDataManager().getJobFromId(person.getJobId());
                    if (commuteMode != null && dd != null && jj != null) {
                        if (commuteMode.mode.equals(TransportMode.car)) {
                            tripsByCar.putIfAbsent(dd.getZoneId(), 0);
                            tripsByCar.put(dd.getZoneId(), tripsByCar.get(dd.getZoneId()) + 1);
                            double timeCar = dataContainer.getTravelTimes().getTravelTime(dd, jj, properties.transportModel.peakHour_s, TransportMode.car);
                            timeByCar.putIfAbsent(dd.getZoneId(), 0.);
                            timeByCar.put(dd.getZoneId(), timeByCar.get(dd.getZoneId()) + timeCar);
                            car.getAndIncrement();
                        } else if (commuteMode.mode.equals(TransportMode.pt)) {
                            tripsByPt.putIfAbsent(dd.getZoneId(), 0);
                            tripsByPt.put(dd.getZoneId(), tripsByPt.get(dd.getZoneId()) + 1);
                            double timePt = dataContainer.getTravelTimes().getTravelTime(dd, jj, properties.transportModel.peakHour_s, TransportMode.pt);
                            timeByPt.putIfAbsent(dd.getZoneId(), 0.);
                            timeByPt.put(dd.getZoneId(), timeByPt.get(dd.getZoneId()) + timePt);
                            pt.getAndIncrement();
                        } else {
                            tripsByOther.putIfAbsent(dd.getZoneId(), 0);
                            tripsByOther.put(dd.getZoneId(), tripsByOther.get(dd.getZoneId()) + 1);
                            double timeCar = dataContainer.getTravelTimes().getTravelTime(dd, jj, properties.transportModel.peakHour_s, TransportMode.car);
                            timeByOther.putIfAbsent(dd.getZoneId(), 0.);
                            timeByOther.put(dd.getZoneId(), timeByOther.get(dd.getZoneId()) + timeCar);
                            other.getAndIncrement();
                        }
                    } else {
                        //not a commuter
                        doNotTravel.putIfAbsent(dd.getZoneId(), 0);
                        doNotTravel.put(dd.getZoneId(), doNotTravel.get(dd.getZoneId()) + 1);
                    }
                }
            });
        }

        logger.info("Modal share of car is " + car.doubleValue()/(car.get() + pt.get() + other.get()));
        logger.info("Modal share of pt is " + pt.doubleValue()/(car.get() + pt.get() + other.get()));
        logger.info("Modal share of other is " + other.doubleValue()/(car.get() + pt.get() + other.get()));

        for (Zone zone : dataContainer.getGeoData().getZones().values()) {
            pw.print(year);
            pw.print(",");
            pw.print(zone.getZoneId());
            pw.print(",");
            pw.print(tripsByCar.getOrDefault(zone.getZoneId(), 0));
            pw.print(",");
            pw.print(tripsByPt.getOrDefault(zone.getZoneId(), 0));
            pw.print(",");
            pw.print(tripsByOther.getOrDefault(zone.getZoneId(), 0));
            pw.print(",");
            pw.print(timeByCar.getOrDefault(zone.getZoneId(), 0.));
            pw.print(",");
            pw.print(timeByPt.getOrDefault(zone.getZoneId(), 0.));
            pw.print(",");
            pw.print(timeByOther.getOrDefault(zone.getZoneId(), 0.));
            pw.print(",");
            pw.print(doNotTravel.getOrDefault(zone.getZoneId(), 0));
            pw.println();
        }
    }

    @Override
    public void endSimulation() {
        pw.close();

    }
}
