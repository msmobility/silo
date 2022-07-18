package de.tum.bgu.msm.scenarios.ev;

import com.google.common.collect.*;
import de.tum.bgu.msm.container.*;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.geo.ZoneMuc;
import de.tum.bgu.msm.data.household.*;
import de.tum.bgu.msm.data.vehicle.Car;
import de.tum.bgu.msm.data.vehicle.CarType;
import de.tum.bgu.msm.data.vehicle.Vehicle;
import de.tum.bgu.msm.events.*;
import de.tum.bgu.msm.io.output.*;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.*;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class EVResultMonitor implements ResultsMonitor {

    private final Properties properties;
    private DataContainer dataContainer;

    private PrintWriter resultWriter;
    private PrintWriter resultWriterSpatial;
    private final static Logger logger = Logger.getLogger(EVResultMonitor.class);

    public EVResultMonitor(ModelContainer modelContainer, DataContainer dataContainer, Properties properties) {
        this.dataContainer = dataContainer;
        this.properties = properties;
    }

    @Override
    public void setup() {
        String pathname = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName + "/siloResults/ev_ownership.csv";
        String pathnameSpatial = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName + "/siloResults/ev_ownership_spatial.csv";
        try {
            resultWriter = new PrintWriter(new File(pathname));
            resultWriter.println("year,hhs,n_autos,n_cv,n_ev");
            resultWriterSpatial = new PrintWriter(new File(pathnameSpatial));
            resultWriterSpatial.println("year,zone,areaType,hhs,n_autos,n_cv,n_ev");
        } catch (FileNotFoundException e) {
            logger.error("Cannot write the result file: " + pathname, e);
        }
    }

    @Override
    public void endYear(int year, Multiset<Class<? extends MicroEvent>> eventCounter, List<MicroEvent> events) {

        int nAutos = 0;
        int nEVs = 0;

        Map<Integer, ZonalAttributes> dataByZone = new HashMap<>();

        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()) {

            for (Vehicle vv : hh.getVehicles()) {
                if (vv instanceof Car) {
                    nAutos++;
                    if (((Car) vv).getCarType().equals(CarType.ELECTRIC)) {
                        nEVs++;
                    }
                }
            }


            int zoneId = dataContainer.getRealEstateDataManager().getDwelling(hh.getDwellingId()).getZoneId();
            dataByZone.putIfAbsent(zoneId, new ZonalAttributes());
            dataByZone.get(zoneId).addHouseholdToThisZone(hh);
        }

        int nCVs = nAutos - nEVs;

        resultWriter.println(year + "," +
                dataContainer.getHouseholdDataManager().getHouseholds().size() + "," +
                nAutos + "," +
                nCVs + "," +
                nEVs);


        for (int zoneId : dataByZone.keySet()){

            ZoneMuc zone = (ZoneMuc) dataContainer.getGeoData().getZones().get(zoneId);

            resultWriterSpatial.println(year + "," + zoneId + "," + zone.getAreaTypeSG().toString() + "," +
                    dataByZone.get(zoneId).nHh + "," +
                    dataByZone.get(zoneId).nAutos + "," +
                    dataByZone.get(zoneId).getCVs() + "," +
                    dataByZone.get(zoneId).nEVs);
        }

        dataByZone.clear();

        if (year % 5 == 0){
            int counter = 0;
            try {
                String path = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName + "/microData/vv_" + year + ".csv";
                SiloUtil.openFileForSequentialWriting(path, false);

                PrintWriter annualMicroDataPw = new PrintWriter(path);
                annualMicroDataPw.println("hh,zone,index,vehId,type,age");

                for (Household household : dataContainer.getHouseholdDataManager().getHouseholds()) {

                    int zoneId = dataContainer.getRealEstateDataManager().getDwelling(household.getDwellingId()).getZoneId();
                    ZoneMuc zone = (ZoneMuc) dataContainer.getGeoData().getZones().get(zoneId);

                    for (Vehicle vehicle : household.getVehicles()){

                        if (vehicle instanceof Car){
                            Car car = (Car) vehicle;

                            annualMicroDataPw.println(household.getId() + "," +
                                    zone.getId() + "," +
                                    counter + "," +
                                    car.getId() + "," +
                                    car.getCarType() + "," +
                                    car.getAge());

                            counter++;
                        }

                    }


                }

                annualMicroDataPw.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void endSimulation() {
        resultWriter.close();
        resultWriterSpatial.close();
    }

    public static class ZonalAttributes {
        int nAutos = 0;
        int nEVs = 0;
        int nHh = 0;

        void addHouseholdToThisZone(Household hh){

            for (Vehicle vv : hh.getVehicles()) {
                if (vv instanceof Car) {
                    nAutos++;
                    if (((Car) vv).getCarType().equals(CarType.ELECTRIC)) {
                        nEVs++;
                    }
                }
            }

            nHh++;
        }

        public int getCVs() {
            return nAutos - nEVs;
        }
    }
}
