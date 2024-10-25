package de.tum.bgu.msm.properties.modules;

import de.tum.bgu.msm.properties.PropertiesUtil;

import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class HealthModelProperties {

    public final String truck_plan_file;
    public final String throughTraffic_plan_file;

    public final double matsim_scale_factor_car;
    public final double matsim_scale_factor_bikePed;
    public final String activeModeNetworkFile;
    public final double MAX_WALKSPEED;
    public final double MAX_CYCLESPEED;
    public final String COLD_EMISSION_FILE;

    public final String HOT_EMISSION_FILE;
    public final int HEALTH_MODEL_INTERVAL;
    public final double BACKGROUND_PM25;
    public final double BACKGROUND_NO2;


    public HealthModelProperties(ResourceBundle bundle) {
        PropertiesUtil.newPropertySubmodule("Health model properties");

        truck_plan_file = PropertiesUtil.getStringProperty(bundle, "truck.plan", "input/freight/truck_plans.xml");

        throughTraffic_plan_file = PropertiesUtil.getStringProperty(bundle, "throughTraffic.plan", "iinput/freight/throughTraffic_plans.xml");

        matsim_scale_factor_car = PropertiesUtil.getDoubleProperty(bundle, "matsim.scale.factor.car", 0.1);

        matsim_scale_factor_bikePed = PropertiesUtil.getDoubleProperty(bundle, "matsim.scale.factor.bikePed", 1.0);

        activeModeNetworkFile = PropertiesUtil.getStringProperty(bundle, "matsim.network.activeMode", "input/mito/trafficAssignment/network_active_cleaned.xml");

        MAX_WALKSPEED = PropertiesUtil.getDoubleProperty(bundle, "max.walk.speed.kph", 5.);

        MAX_CYCLESPEED = PropertiesUtil.getDoubleProperty(bundle, "max.cycle.speed.kph", 15.);

        COLD_EMISSION_FILE = PropertiesUtil.getStringProperty(bundle, "cold.emission.file", "input/mito/trafficAssignment/EFA_ColdStart_Vehcat_healthModelWithTruck.txt");
        HOT_EMISSION_FILE =  PropertiesUtil.getStringProperty(bundle, "hot.emission.file", "input/mito/trafficAssignment/EFA_HOT_Vehcat_healthModelWithTruck.txt");

        HEALTH_MODEL_INTERVAL = PropertiesUtil.getIntProperty(bundle, "health.model.interval", 2);

        BACKGROUND_PM25 = PropertiesUtil.getDoubleProperty(bundle, "background.pm25", 9.);//default is munich rates
        BACKGROUND_NO2 = PropertiesUtil.getDoubleProperty(bundle, "background.no2", 17.6);
    }

}
