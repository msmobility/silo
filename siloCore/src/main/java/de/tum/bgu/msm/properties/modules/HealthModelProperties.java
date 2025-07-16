package de.tum.bgu.msm.properties.modules;

import de.tum.bgu.msm.properties.PropertiesUtil;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class HealthModelProperties {

    public final String truck_plan_file;
    public final String throughTraffic_plan_file;

    public final double matsim_scale_factor_car;
    public final double matsim_scale_factor_bikePed;
    public final double MAX_WALKSPEED;
    public final double MAX_CYCLESPEED;
    public final String COLD_EMISSION_FILE;

    public final String HOT_EMISSION_FILE;
    public final int HEALTH_MODEL_INTERVAL;
    public final double BACKGROUND_PM25;
    public final double BACKGROUND_NO2;
    public final String basePath;
    public final String diseaseLookupTable;
    public final String avgSpeedFile;
    public final String prevalenceDataFile;
    public final String healthInjuryRRDataFile;
    public final String healthTransitionData;
    public final Boolean adjustByRelativeRisk;
    public final String baseExposureFile;
    public final List<Integer> exposureModelYears;
//    public final String bus_network;
    public final String noiseBarriersFile;
    public final String sportPAmodel;
    public final String busLinkConcentration;
    public final String busLocationConcentration;
    public final String ptPeakSkim;
    public final String ptAccessTimeMatrix;
    public final String ptEgressTimeMatrix;
    public final String ptTotalTravelTimeMatrix;
    public final String ptBusTimeShareMatrix;
    public final double DEFAULT_ROAD_TRAFFIC_INCREMENTAL_PM25;
    public final double DEFAULT_ROAD_TRAFFIC_INCREMENTAL_NO2;


    public HealthModelProperties(ResourceBundle bundle) {
        PropertiesUtil.newPropertySubmodule("Health model properties");

        truck_plan_file = PropertiesUtil.getStringProperty(bundle, "truck.plan", "input/freight/truck_plans.xml");

        throughTraffic_plan_file = PropertiesUtil.getStringProperty(bundle, "throughTraffic.plan", "iinput/freight/throughTraffic_plans.xml");

        matsim_scale_factor_car = PropertiesUtil.getDoubleProperty(bundle, "matsim.scale.factor.car", 0.1);

        matsim_scale_factor_bikePed = PropertiesUtil.getDoubleProperty(bundle, "matsim.scale.factor.bikePed", 1.0);

        MAX_WALKSPEED = PropertiesUtil.getDoubleProperty(bundle, "max.walk.speed.kph", 5.);

        MAX_CYCLESPEED = PropertiesUtil.getDoubleProperty(bundle, "max.cycle.speed.kph", 15.);

        COLD_EMISSION_FILE = PropertiesUtil.getStringProperty(bundle, "cold.emission.file", "input/mito/trafficAssignment/EFA_ColdStart_Vehcat_healthModelWithTruck.txt");
        HOT_EMISSION_FILE =  PropertiesUtil.getStringProperty(bundle, "hot.emission.file", "input/mito/trafficAssignment/EFA_HOT_Vehcat_healthModelWithTruck.txt");

        HEALTH_MODEL_INTERVAL = PropertiesUtil.getIntProperty(bundle, "health.model.interval", 2);

        BACKGROUND_PM25 = PropertiesUtil.getDoubleProperty(bundle, "background.pm25", 11.6);
        BACKGROUND_NO2 = PropertiesUtil.getDoubleProperty(bundle, "background.no2", 17.3);

        //TODO: no PM 2.5 urban traffic site is found in Manchester, so no reference road incremental.
        DEFAULT_ROAD_TRAFFIC_INCREMENTAL_PM25 = PropertiesUtil.getDoubleProperty(bundle, "road.traffic.incremental.pm25", 0.);
        //Bury Whitefield Roadside is defined as reference urban traffic site, annual average NO2 is 25.1 at this site
        // reference road traffic incremental NO2 = 25.1 - 17.3
        DEFAULT_ROAD_TRAFFIC_INCREMENTAL_NO2 = PropertiesUtil.getDoubleProperty(bundle, "road.traffic.incremental.no2", 7.8);

        basePath = PropertiesUtil.getStringProperty(bundle, "healthData.basePath", "input/health/");

        diseaseLookupTable = PropertiesUtil.getStringProperty(bundle, "disease.outcome.lookup", "disease_outcomes_lookup.csv");

        avgSpeedFile = PropertiesUtil.getStringProperty(bundle, "avg.speed.file", "input/maxSpeeds.csv");

        prevalenceDataFile = PropertiesUtil.getStringProperty(bundle, "prev.data.file", "input/health/base_prevalence_id_clean.csv");

        healthInjuryRRDataFile = PropertiesUtil.getStringProperty(bundle, "injury.rr.data.file", "input/accident/injury_relativeRisks.csv");

        healthTransitionData = PropertiesUtil.getStringProperty(bundle, "health.transition.data", "input/health/health_transitions_manchester.csv");

        adjustByRelativeRisk = PropertiesUtil.getBooleanProperty(bundle, "adjust.transition.byRelativeRisk", false);

        baseExposureFile = PropertiesUtil.getStringProperty(bundle, "base.exposure.file", null);

        exposureModelYears = Arrays.stream((PropertiesUtil.getIntPropertyArray(bundle, "exposure.model.years", new int[]{-1}))).boxed().collect(Collectors.toList());

//        bus_network = PropertiesUtil.getStringProperty(bundle, "matsim.network.bus", "input/mito/trafficAssignment/network_transit_hbefa.xml");

        noiseBarriersFile = PropertiesUtil.getStringProperty(bundle, "noise.barriers.file", "input/buildingShapefile/buildings.geojson");

        sportPAmodel = PropertiesUtil.getStringProperty(bundle, "sportPA.model", "input/health/sportPAmodel.csv");

        busLinkConcentration = PropertiesUtil.getStringProperty(bundle, "bus.link.concentration", "input/mito/trafficAssignment/ptSimulation/linkConcentration_bus.csv");

        busLocationConcentration = PropertiesUtil.getStringProperty(bundle, "bus.location.concentration", "input/mito/trafficAssignment/ptSimulation/locationConcentration_bus.csv");

        ptPeakSkim = PropertiesUtil.getStringProperty(bundle, "pt.peak.time", "skims/pt.omx");

        ptTotalTravelTimeMatrix = PropertiesUtil.getStringProperty(bundle, "pt.total.travel.time.matrix.time", "travelTime");

        ptAccessTimeMatrix = PropertiesUtil.getStringProperty(bundle, "pt.access.time.matrix.time", "accessTime");

        ptEgressTimeMatrix = PropertiesUtil.getStringProperty(bundle, "pt.egress.time.matrix.time", "egressTime");

        ptBusTimeShareMatrix = PropertiesUtil.getStringProperty(bundle, "pt.bus.time.share.matrix.time", "busTimeShare");
    }

}
