package de.tum.bgu.msm.health;

import cern.colt.map.tfloat.OpenIntFloatHashMap;
import de.tum.bgu.msm.health.injury.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.utils.objectattributes.attributable.Attributes;
import routing.components.JctStress;
import routing.components.LinkStress;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class CasualtyRateCalculationOsmMCR {
    private static final Logger log = LogManager.getLogger(CasualtyRateCalculationOsmMCR.class);
    //private final double SCALEFACTOR;
    //private AnalysisEventHandler analyserMotorized;
    //private AnalysisEventHandler analyserNonMotorized;
    private AccidentsContext accidentsContext;
    private Map<String, Double> binaryLogitCoef;
    private Map<String, Double> poissonCoef;
    private Map<Integer, Double> timeOfDayCoef;
    private AccidentType accidentType;
    private AccidentSeverity accidentSeverity;
    //private Scenario scenario;
    //private double calibrationFactor;
    //private int tot_Casualties;
    //private int current_Casualties;

    // todo: testing
    //private Map<Id<Link>, EnumMap<AccidentType, OpenIntFloatHashMap>> severeFatalCasualityRiskByLinkByAccidentTypeByTime = new HashMap<>();

    public CasualtyRateCalculationOsmMCR(AccidentsContext accidentsContext, AccidentType accidentType, AccidentSeverity accidentSeverity, String basePath) {
        //this.SCALEFACTOR = scaleFactor;
        this.accidentsContext = accidentsContext;
        //this.analyserMotorized = analyserMotorized;
        //this.analyserNonMotorized = analyserNonMotorized;
        this.accidentType = accidentType;
        this.accidentSeverity = accidentSeverity;
        this.binaryLogitCoef = new AccidentRateModelCoefficientReader(accidentType, accidentSeverity, basePath + "binaryModel.csv").readData();
        //this.scenario = scenario;
        //this.calibrationFactor = calibrationFactor;
    }

    protected void run(Collection<? extends OsmLink> links, Random random) {
        for (OsmLink link : links) {
            computeLinkCasualtyFrequency(link, random, accidentType, accidentSeverity, 1.);
        }
    }

    private String getMode(AccidentType accidentType){
        String mode;
        if (accidentType.toString().startsWith("BIKE_")) {
            mode = "bike";
        }
        else if (accidentType.toString().startsWith("CAR_")) {
            mode = "car";
        }
        else {
            mode = "walk"; // default case
        }
        return mode;
    }

    private void computeLinkCasualtyFrequency(OsmLink osmLink, Random random, AccidentType accidentType, AccidentSeverity accidentSeverity, double calibrationFactor) {
        OpenIntFloatHashMap casualtyRateByTimeOfDay = new OpenIntFloatHashMap();
        double totalLength = osmLink.getNetworkLinks().stream().mapToDouble(Link::getLength).sum();
        if (totalLength == 0) {
            log.warn("OSM ID {} has no links or zero total length, skipping casualty frequency calculation.", osmLink.osmId);
            return;
        }

        for (int hour = 0; hour < 24; hour++) {
            double probHourlyCrash = calculateProbability(osmLink, hour);
            //probZeroCrash = 1 - Math.pow(1 - probZeroCrash, 1.0 / 5);
            probHourlyCrash = probHourlyCrash / 5;
            float casualtyRate = (float) (probHourlyCrash / calibrationFactor);
            casualtyRateByTimeOfDay.put(hour, casualtyRate);
        }

        for (Link link : osmLink.getNetworkLinks()) {
            double lengthWeight = link.getLength() / totalLength;
            OpenIntFloatHashMap linkCasualtyRateByTime = new OpenIntFloatHashMap();
            for (int hour = 0; hour < 24; hour++) {
                float osmCasualtyRate = casualtyRateByTimeOfDay.get(hour);
                float linkCasualtyRate = (float) (osmCasualtyRate * lengthWeight);
                linkCasualtyRateByTime.put(hour, linkCasualtyRate);
            }
            AccidentLinkInfo linkInfo = accidentsContext.getLinkId2info().computeIfAbsent(link.getId(), id -> new AccidentLinkInfo(id));
            linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().put(accidentType, linkCasualtyRateByTime);
        }


        // testing
        /*
        if(osmLink.osmId== 1111480){
            System.out.println("OSM ID " + osmLink.osmId);
            System.out.println("===============================================================");
            printOsmLinkDetails(osmLink);
            System.out.println("===============================================================");
            printBinaryLogitCoefficients();
            System.out.println("===============================================================");
            for(int hour = 0; hour < 24; hour++) {
                System.out.println("Hour " + hour + ": " + casualtyRateByTimeOfDay.get(hour));
            }
            System.out.println("===============================================================");

            // links
            for(Link link : osmLink.getNetworkLinks()) {
                System.out.println("Link ID " + link.getId().toString());
                System.out.println("===============================================================");
                for(int hour = 0; hour < 24; hour++) {
                    float val = accidentsContext.getLinkId2info().get(link.getId()).getSevereFatalCasualityExposureByAccidentTypeByTime().get(accidentType).get(hour);
                    System.out.println("Hour " + hour + ": " + val);
                }
                System.out.println("===============================================================");
            }
        }

         */
    }

    public void printBinaryLogitCoefficients() {
        System.out.println("Binary Logit Coefficients:");
        System.out.println("--------------------------");
        for (Map.Entry<String, Double> entry : binaryLogitCoef.entrySet()) {
            System.out.printf("%-30s : %.6f%n", entry.getKey(), entry.getValue());
        }
        System.out.println("--------------------------");
    }

    public void printOsmLinkDetails(OsmLink sample) {
        System.out.println("OsmLink ID: " + sample.osmId);
        System.out.println("Road Type: " + sample.roadType);
        System.out.println("One Way?: " + sample.onwysmm); // Assumed 'oneWay' instead of 'onwysmm'
        System.out.println("Speed Limit (MPH): " + sample.speedLimitMPH);
        System.out.println("Bike Allowed: " + sample.bikeAllowed);
        System.out.println("Car Allowed: " + sample.carAllowed);
        System.out.println("Walk Allowed: " + sample.walkAllowed);
        System.out.println("Total Length (m): " + sample.lengthSum);
        System.out.println("Width (m): " + sample.width);
        System.out.println("Bike Stress: " + sample.bikeStress);
        System.out.println("Bike Stress Jct: " + sample.bikeStressJct);
        System.out.println("Walk Stress Jct: " + sample.walkStressJct);

        for (int outputHour = 0; outputHour < 24; outputHour++) {
            System.out.println("Car Hourly Demand (Hour " + outputHour + "): " + sample.carHourlyDemand[outputHour]);
            System.out.println("Truck Hourly Demand (Hour " + outputHour + "): " + sample.truckHourlyDemand[outputHour]);
            System.out.println("Ped Hourly Demand (Hour " + outputHour + "): " + sample.pedHourlyDemand[outputHour]);
            System.out.println("Bike Hourly Demand (Hour " + outputHour + "): " + sample.bikeHourlyDemand[outputHour]);
            System.out.println("Motor Hourly Demand (Hour " + outputHour + "): " + sample.motorHourlyDemand[outputHour]);
            System.out.println("*******");
        }
        System.out.println("------------------------");
    }

    private double calculateUtility(OsmLink link, int hour) {

        double utility = 0.0;
        utility += binaryLogitCoef.get("(Intercept)");

        // truck
        double truckHourlyDemand = link.truckHourlyDemand[hour]; //;
        utility += binaryLogitCoef.get("log1p(truck_flow)") *
                Math.log1p(truckHourlyDemand);

        // pedestrian
        double pedHourlyDemand = link.pedHourlyDemand[hour];
        utility += binaryLogitCoef.get("log1p(ped_flow)") *
                Math.log1p(pedHourlyDemand);

        // car
        double carHourlyDemand = link.carHourlyDemand[hour];
        utility += binaryLogitCoef.get("log1p(car_flow)") *
                Math.log1p(carHourlyDemand);

        // bike
        double bikeHourlyDemand = link.bikeHourlyDemand[hour];
        utility += binaryLogitCoef.get("log(bike_flow + 0.1)") *
                Math.log(bikeHourlyDemand + 0.1);

        // motor
        double motorHourlyDemand = link.carHourlyDemand[hour] + link.truckHourlyDemand[hour];

        utility += binaryLogitCoef.get("motor_flow") *
                motorHourlyDemand;

        utility += binaryLogitCoef.get("log1p(motor_flow)") *
                Math.log1p(motorHourlyDemand);

        // length
        utility += binaryLogitCoef.get("log(length_sum)") * Math.log(link.lengthSum);

        // Handle continuous variables without transformation

        /*
        utility += binaryLogitCoef.get("bikeStress") * LinkStress.getStress(link, "bike");
        utility += binaryLogitCoef.get("bikeStressJct") * JctStress.getStress(link, "bike");
        utility += binaryLogitCoef.get("walkStressJct") * JctStress.getStress(link, "walk");
         */

        // Bike stress (Link)
        double bikeStress = link.bikeStress;
        utility += Double.isNaN(bikeStress) ? 0 : binaryLogitCoef.get("bikeStress") * bikeStress;

        // Bike stress at junction (Jct)
        double bikeStressJct = link.bikeStressJct;
        utility += Double.isNaN(bikeStressJct) ? 0 : binaryLogitCoef.get("bikeStressJct") * bikeStressJct;

        // Walk stress at junction (Jct)
        double walkStressJct = link.walkStressJct;
        utility += Double.isNaN(walkStressJct) ? 0 : binaryLogitCoef.get("walkStressJct") * walkStressJct;

        //
        utility += binaryLogitCoef.get("width") * link.width;

        // Handle categorical variables (speed limit)
        double speedLimit = link.speedLimitMPH;
        if (speedLimit < 20) {
            utility += binaryLogitCoef.get("speed_limit<20 MPH");
        } else if (speedLimit >= 20 && speedLimit < 30) {
            utility += binaryLogitCoef.get("speed_limit20 - 29 MPH");
        }

        //
        String roadType = link.roadType;

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

    /*
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
    }*/

    public double calculateProbability(OsmLink link, int hour) {
        double utility = calculateUtility(link, hour);
        return 1.0 / (1.0 + Math.exp(-utility));
    }
}
