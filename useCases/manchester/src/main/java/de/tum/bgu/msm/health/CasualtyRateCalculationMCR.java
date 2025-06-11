package de.tum.bgu.msm.health;

import cern.colt.map.tfloat.OpenIntFloatHashMap;
import de.tum.bgu.msm.health.injury.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.utils.objectattributes.attributable.Attributes;
import routing.components.JctStress;
import routing.components.LinkStress;

import java.util.*;
import java.util.stream.Collectors;

public class CasualtyRateCalculationMCR {
    private static final Logger log = LogManager.getLogger(CasualtyRateCalculationMCR.class);
    private final double SCALEFACTOR;
    private AnalysisEventHandler analzyer;
    private AccidentsContext accidentsContext;
    private Map<String, Double> binaryLogitCoef;
    private Map<String, Double> poissonCoef;
    private Map<Integer, Double> timeOfDayCoef;
    private AccidentType accidentType;
    private AccidentSeverity accidentSeverity;
    private Scenario scenario;
    private double calibrationFactor;

    // todo: testing
    //private Map<Id<Link>, EnumMap<AccidentType, OpenIntFloatHashMap>> severeFatalCasualityRiskByLinkByAccidentTypeByTime = new HashMap<>();

    public CasualtyRateCalculationMCR(double scaleFactor, AccidentsContext accidentsContext, AnalysisEventHandler analzyer, AccidentType accidentType, AccidentSeverity accidentSeverity, String basePath, Scenario scenario, double calibrationFactor) {
        this.SCALEFACTOR = scaleFactor;
        this.accidentsContext = accidentsContext;
        this.analzyer = analzyer;
        this.accidentType = accidentType;
        this.accidentSeverity = accidentSeverity;
        this.binaryLogitCoef = new AccidentRateModelCoefficientReader(accidentType, accidentSeverity, basePath + "binaryModel.csv").readData();
        this.poissonCoef = null;
        this.timeOfDayCoef = null;
        this.scenario = scenario;
        this.calibrationFactor = calibrationFactor;
    }

    protected void run(Collection<? extends Link> links, Random random) {
        for (Link link : links) {
            computeLinkCasualtyFrequency(link, random);
        }

    }

    private void computeLinkCasualtyFrequency(Link link, Random random) {
        double probZeroCrash = 0;
        int val = 0;
        //Random random = new Random();
        //double meanCrash = getMeanCrashPoisson(link);
        //double finalCrashRate = meanCrash*(1-probZeroCrash);

        OpenIntFloatHashMap casualtyRateByTimeOfDay = new OpenIntFloatHashMap();
        for (int hour = 0; hour < 24; hour++) {
            probZeroCrash = calculateProbability(link, hour);
            // downscale
            probZeroCrash = 1 - Math.pow(1 - probZeroCrash, 1.0/5); // 1300-260
            //probZeroCrash= probZeroCrash/5; // this is the annual proba of casualty, need to divide by 365 for online simulation

            // sample
            if(random.nextDouble() < (probZeroCrash/calibrationFactor))
                val = 1;
            else{
                val = 0;
            }
            //casualtyRateByTimeOfDay.put(hour, (float) val);
            casualtyRateByTimeOfDay.put(hour, (float) (probZeroCrash/calibrationFactor));
        }

        switch (accidentSeverity) {
            case SEVEREFATAL:
                this.accidentsContext.getLinkId2info().get(link.getId()).getSevereFatalCasualityExposureByAccidentTypeByTime().put(accidentType, casualtyRateByTimeOfDay);
                break;
            default:
                throw new RuntimeException("Undefined accident severity " + accidentSeverity);
        }
    }

    private OpenIntFloatHashMap computeLinkCasualtyFrequency2(Link link, Random random) {
        double probZeroCrash = 0;
        int val = 0;
        //Random random = new Random();
        //double meanCrash = getMeanCrashPoisson(link);
        //double finalCrashRate = meanCrash*(1-probZeroCrash);

        OpenIntFloatHashMap casualtyRateByTimeOfDay = new OpenIntFloatHashMap();
        for (int hour = 0; hour < 24; hour++) {
            probZeroCrash = calculateUtility2(link, hour);
            // downscale
            probZeroCrash = 1 - Math.pow(1 - probZeroCrash, 1.0/5); // 1300-260
            //probZeroCrash= probZeroCrash/5; // this is the annual proba of casualty, need to divide by 365 for online simulation

            // sample
            if(random.nextDouble() < probZeroCrash)
                val = 1;
            else{
                val = 0;
            }
            casualtyRateByTimeOfDay.put(hour, (float) val);
            //casualtyRateByTimeOfDay.put(hour, (float) probZeroCrash);
        }
        return casualtyRateByTimeOfDay;
    }

    private double calculateUtility(Link link, int hour) {
        Attributes attributes = link.getAttributes();

        double utility = 0.0;
        utility += binaryLogitCoef.get("(Intercept)");

        // truck
        double truckHourlyDemand = analzyer.getDemand(link.getId(), "truck", hour) * SCALEFACTOR; //;
        utility += binaryLogitCoef.get("log1p(truck_flow)") *
                Math.log1p(truckHourlyDemand);

        // pedestrian
        double pedHourlyDemand = analzyer.getDemand(link.getId(), "walk", hour);
        utility += binaryLogitCoef.get("log1p(ped_flow)") *
                Math.log1p(pedHourlyDemand);

        // car
        double carHourlyDemand = analzyer.getDemand(link.getId(), "car", hour) * SCALEFACTOR; //* SCALEFACTOR;
        utility += binaryLogitCoef.get("log1p(car_flow)") *
                Math.log1p(carHourlyDemand);

        // bike
        double bikeHourlyDemand = analzyer.getDemand(link.getId(), "bike", hour);
        utility += binaryLogitCoef.get("log(bike_flow + 0.1)") *
                Math.log(bikeHourlyDemand + 0.1);

        // motor
        double motorHourlyDemand = (analzyer.getDemand(link.getId(), "car", hour) + analzyer.getDemand(link.getId(), "truck", hour)) * SCALEFACTOR; //* SCALEFACTOR;
        // todo: do I need to rescale truck flows
        utility += binaryLogitCoef.get("motor_flow") *
                motorHourlyDemand;

        utility += binaryLogitCoef.get("log1p(motor_flow)") *
                Math.log1p(motorHourlyDemand);

        // length
        utility += binaryLogitCoef.get("log(length_sum)") * Math.log(link.getLength());

        // Handle continuous variables without transformation

        utility += binaryLogitCoef.get("bikeStress") * LinkStress.getStress(link, "bike");
        utility += binaryLogitCoef.get("bikeStressJct") * JctStress.getStress(link, "bike");
        utility += binaryLogitCoef.get("walkStressJct") * JctStress.getStress(link, "walk");

        utility += binaryLogitCoef.get("width") * getDoubleAttribute(attributes, "width", 0.0);

        // Handle categorical variables (speed limit)
        double speedLimit = getDoubleAttribute(attributes, "speedLimitMPH", 0.0);
        if (speedLimit < 20) {
            utility += binaryLogitCoef.get("speed_limit<20 MPH");
        } else if (speedLimit >= 20 && speedLimit < 30) {
            utility += binaryLogitCoef.get("speed_limit20 - 29 MPH");
        }

        //
        String roadType = getStringAttribute(link.getAttributes(), "type", "residential"); // todo: what should be the default
        //log.warn("Motorway here ...");

        if (roadType.equals("primary") || roadType.equals("primary_link") || roadType.equals("trunk") || roadType.equals("trunk_link")) {
            utility += binaryLogitCoef.get("roadPrimary/Trunk");
        } else if ((!roadType.equals("motorway")) && (!roadType.equals("motorway_link"))) {
            if (speedLimit < 20) {
                utility += binaryLogitCoef.get("road<20MPH");
            } else if (speedLimit >= 20 && speedLimit < 30) {
                utility += binaryLogitCoef.get("road20 - 29 MPH");
            }
        }
        return utility;
    }


    // Get traffic volumes
    public double getAverageTrafficVolume(String mode, int hour, String osmID) {
        List<Link> links = scenario.getNetwork().getLinks().values().stream()
                .filter(link -> getStringAttribute(link.getAttributes(), "osmID", "unknown").equals(osmID))
                .collect(Collectors.toList());

        double totalVolume = links.stream()
                .mapToDouble(link -> analzyer.getDemand(link.getId(), mode, hour))
                .sum();

        return links.isEmpty() ? 0.0 : totalVolume / links.size();
    }

    // This function can be used with the aggregate network
    // link is the aggregate one

    private double calculateUtility2(Link link, int hour) {
        Attributes attributes = link.getAttributes();

        double utility = 0.0;
        utility += binaryLogitCoef.get("(Intercept)");

        // truck
        double truckHourlyDemand = getAverageTrafficVolume("truck", hour, (String) attributes.getAttribute("osmID")) * SCALEFACTOR;
        //double truckHourlyDemand = analzyer.getDemand(link.getId(), "truck", hour) * SCALEFACTOR; //;
        utility += binaryLogitCoef.get("log1p(truck_flow)") * Math.log1p(truckHourlyDemand);

        // pedestrian
        double pedHourlyDemand = getAverageTrafficVolume("walk", hour, (String) attributes.getAttribute("osmID"));
        //double pedHourlyDemand = analzyer.getDemand(link.getId(), "walk", hour);
        utility += binaryLogitCoef.get("log1p(ped_flow)") *
                Math.log1p(pedHourlyDemand);

        // car
        double carHourlyDemand = getAverageTrafficVolume("car", hour, (String) attributes.getAttribute("osmID")) * SCALEFACTOR;
        //double carHourlyDemand = analzyer.getDemand(link.getId(), "car", hour) * SCALEFACTOR; //* SCALEFACTOR;
        utility += binaryLogitCoef.get("log1p(car_flow)") *
                Math.log1p(carHourlyDemand);

        // bike
        double bikeHourlyDemand = getAverageTrafficVolume("bike", hour, (String) attributes.getAttribute("osmID"));
        //double bikeHourlyDemand = analzyer.getDemand(link.getId(), "bike", hour);
        utility += binaryLogitCoef.get("log(bike_flow + 0.1)") *
                Math.log(bikeHourlyDemand + 0.1);

        // motor
        double motorHourlyDemand = (getAverageTrafficVolume("car", hour, (String) attributes.getAttribute("osmID"))
                + getAverageTrafficVolume("truck", hour, (String) attributes.getAttribute("osmID"))) * SCALEFACTOR;
        //double motorHourlyDemand = (analzyer.getDemand(link.getId(), "car", hour) + analzyer.getDemand(link.getId(), "truck", hour)) * SCALEFACTOR; //* SCALEFACTOR;
        // todo: do I need to rescale truck flows
        utility += binaryLogitCoef.get("motor_flow") *
                motorHourlyDemand;

        utility += binaryLogitCoef.get("log1p(motor_flow)") *
                Math.log1p(motorHourlyDemand);

        // length
        utility += binaryLogitCoef.get("log(length_sum)") * Math.log(link.getLength());

        // Handle continuous variables without transformation

        utility += binaryLogitCoef.get("bikeStress") * ((double) attributes.getAttribute("bikeStress"));
        utility += binaryLogitCoef.get("bikeStressJct") * ((double) attributes.getAttribute("bikeStressJct"));
        utility += binaryLogitCoef.get("walkStressJct") * ((double) attributes.getAttribute("walkStressJct"));

        utility += binaryLogitCoef.get("width") * getDoubleAttribute(attributes, "width", 0.0);

        // Handle categorical variables (speed limit)
        double speedLimit = getDoubleAttribute(attributes, "speedLimitMPH", 0.0);
        if (speedLimit < 20) {
            utility += binaryLogitCoef.get("speed_limit<20 MPH");
        } else if (speedLimit >= 20 && speedLimit < 30) {
            utility += binaryLogitCoef.get("speed_limit20 - 29 MPH");
        }

        //
        String roadType = getStringAttribute(link.getAttributes(), "type", "residential"); // todo: what should be the default
        //log.warn("Motorway here ...");

        if (roadType.equals("primary") || roadType.equals("primary_link") || roadType.equals("trunk") || roadType.equals("trunk_link")) {
            utility += binaryLogitCoef.get("roadPrimary/Trunk");
        } else if ((!roadType.equals("motorway")) && (!roadType.equals("motorway_link"))) {
            if (speedLimit < 20) {
                utility += binaryLogitCoef.get("road<20MPH");
            } else if (speedLimit >= 20 && speedLimit < 30) {
                utility += binaryLogitCoef.get("road20 - 29 MPH");
            }
        }
        return 1.0 / (1.0 + Math.exp(-utility));
    }

    // Helper method to safely get double attributes
    private double getDoubleAttribute(Attributes attributes, String key, double defaultValue) {
        Object value = attributes.getAttribute(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return defaultValue;
    }

    // Helper method to safely get string attributes
    private String getStringAttribute(Attributes attributes, String key, String defaultValue) {
        Object value = attributes.getAttribute(key);
        return value != null ? value.toString() : defaultValue;
    }

    public double calculateProbability(Link link, int hour) {
        double utility = calculateUtility(link, hour);
        return 1.0 / (1.0 + Math.exp(-utility));
    }
}
