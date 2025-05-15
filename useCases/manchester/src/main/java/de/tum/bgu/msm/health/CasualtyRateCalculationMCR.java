package de.tum.bgu.msm.health;

import cern.colt.map.tfloat.OpenIntFloatHashMap;
import de.tum.bgu.msm.health.injury.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.network.Link;
import org.matsim.utils.objectattributes.attributable.Attributes;
import routing.components.JctStress;
import routing.components.LinkStress;

import java.util.Collection;
import java.util.Map;

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

    public CasualtyRateCalculationMCR(double scaleFactor, AccidentsContext accidentsContext, AnalysisEventHandler analzyer, AccidentType accidentType, AccidentSeverity accidentSeverity, String basePath) {
        this.SCALEFACTOR = scaleFactor;
        this.accidentsContext = accidentsContext;
        this.analzyer = analzyer;
        this.accidentType = accidentType;
        this.accidentSeverity = accidentSeverity;
        this.binaryLogitCoef = new AccidentRateModelCoefficientReader(accidentType, accidentSeverity, basePath + "binaryModel.csv").readData();
        this.poissonCoef = null;
        this.timeOfDayCoef = null;
    }

    protected void run(Collection<? extends Link> links) {
        for (Link link : links) {
            computeLinkCasualtyFrequency(link);
        }
    }

    private void computeLinkCasualtyFrequency(Link link) {
        double probZeroCrash = 0;
        //double meanCrash = getMeanCrashPoisson(link);
        //double finalCrashRate = meanCrash*(1-probZeroCrash);

        OpenIntFloatHashMap casualtyRateByTimeOfDay = new OpenIntFloatHashMap();
        for (int hour = 0; hour < 24; hour++) {
            probZeroCrash = calculateProbability(link, hour);
            casualtyRateByTimeOfDay.put(hour, (float) probZeroCrash);
        }

        switch (accidentSeverity) {
            case SEVEREFATAL:
                this.accidentsContext.getLinkId2info().get(link.getId()).getSevereFatalCasualityExposureByAccidentTypeByTime().put(accidentType, casualtyRateByTimeOfDay);
                break;
            default:
                throw new RuntimeException("Undefined accident severity " + accidentSeverity);
        }
    }

    private double calculateUtility(Link link, int hour) {
        Attributes attributes = link.getAttributes();
        double utility = binaryLogitCoef.get("(Intercept)");

        // truck
        double truckHourlyDemand = analzyer.getDemand(link.getId(), "truck", hour) * SCALEFACTOR;
        utility += binaryLogitCoef.get("log1p(truck_flow)") *
                Math.log1p(truckHourlyDemand);

        // pedestrian
        double pedHourlyDemand = analzyer.getDemand(link.getId(), "walk", hour) * SCALEFACTOR;
        utility += binaryLogitCoef.get("log1p(ped_flow)") *
                Math.log1p(pedHourlyDemand);

        // car
        double carHourlyDemand = analzyer.getDemand(link.getId(), "car", hour) * SCALEFACTOR;
        utility += binaryLogitCoef.get("log1p(car_flow)") *
                Math.log1p(carHourlyDemand);

        // bike
        double bikeHourlyDemand = analzyer.getDemand(link.getId(), "bike", hour) * SCALEFACTOR;
        utility += binaryLogitCoef.get("log(bike_flow + 0.1)") *
                Math.log(bikeHourlyDemand + 0.1);

        // motor
        double motorHourlyDemand = (analzyer.getDemand(link.getId(), "car", hour) + analzyer.getDemand(link.getId(), "truck", hour)) * SCALEFACTOR;
        // todo: do I need to rescale truck flows
        utility += binaryLogitCoef.get("motor_flow") *
                motorHourlyDemand;

        utility += binaryLogitCoef.get("log1p(motor_flow)") *
                Math.log1p(motorHourlyDemand);

        // length
        utility += binaryLogitCoef.get("log(length_sum)") *
                Math.log(getDoubleAttribute(attributes, "Length", 0.0));

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
        String roadType = link.getAttributes().getAttribute("roadtyp").toString();
        //double speedLimit = Double.parseDouble(link.getAttributes().getAttribute("carSpeedLimitMPH").toString());

        if (roadType.equals("Main Road - Cycling Allowed") || roadType.equals("Main Road Link - Cycling Allowed") ||
                roadType.equals("Trunk Road - Cycling Allowed") || roadType.equals("Trunk Road Link - Cycling Allowed")) {

            // Trunk or Primary → group into "Primary/Trunk"
            //road = "Primary/Trunk";
            utility += binaryLogitCoef.get("roadPrimary/Trunk");

        } else if (roadType.equals("Residential Road - Cycling Allowed") ||
                roadType.equals("Other minor roads")) {

            if (speedLimit < 20) {
                //road = "<20MPH";
                utility += binaryLogitCoef.get("road<20MPH");
            } else if (speedLimit >= 20 && speedLimit < 30) {
                //road = "20 - 29 MPH";
                utility += binaryLogitCoef.get("road20 - 29 MPH");
            }
        }

        return utility;
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
