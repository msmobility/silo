package de.tum.bgu.msm.scenarios.ev;

import com.google.common.collect.*;
import de.tum.bgu.msm.container.*;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.geo.ZoneMuc;
import de.tum.bgu.msm.data.household.*;
import de.tum.bgu.msm.events.*;
import de.tum.bgu.msm.io.output.*;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.*;

import java.io.*;
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
            nAutos += hh.getAutos();
            nEVs += (int) hh.getAttribute("EV").orElse(0);

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
                PrintWriter annualMicroDataPw = new PrintWriter(properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName + "/siloResults/vv_" + year + ".csv");
                annualMicroDataPw.println("hh,zone,index,vehId,type");

                for (Household household : dataContainer.getHouseholdDataManager().getHouseholds()) {
                    int autos = household.getAutos();
                    int electricAutos = (int) household.getAttribute("EV").orElse(0);
                    int conventionalAutos = autos - electricAutos;


                    int zoneId = dataContainer.getRealEstateDataManager().getDwelling(household.getDwellingId()).getZoneId();
                    ZoneMuc zone = (ZoneMuc) dataContainer.getGeoData().getZones().get(zoneId);

                    for (int i = 0; i < autos; i++){
                        if (i < conventionalAutos){
                            annualMicroDataPw.println(household.getId() + "," +
                                    zone.getId() + "," +
                                    i + "," +
                                    counter + "," +
                                    "conventional");
                        } else {
                            annualMicroDataPw.println(household.getId() + "," +
                                    zone.getId() + "," +
                                    i + "," +
                                    counter + "," +
                                    "electric");
                        }
                        counter++;
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
            nAutos += hh.getAutos();
            nEVs += (int) hh.getAttribute("EV").orElse(0);
            nHh++;
        }

        public int getCVs() {
            return nAutos - nEVs;
        }
    }
}
