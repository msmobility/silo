package de.tum.bgu.msm.health;

import cern.colt.map.tfloat.OpenIntFloatHashMap;
import de.tum.bgu.msm.health.injury.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.network.Link;

import java.util.Collection;
import java.util.Map;

public class AccidentRateCalculationMCR {
    private static final Logger log = LogManager.getLogger( AccidentRateCalculationMCR.class );
    private final double SCALEFACTOR;
    private AnalysisEventHandler analzyer;
    private AccidentsContext accidentsContext;
    private Map<String, Double> binaryLogitCoef;
    private Map<String, Double> poissonCoef;
    private Map<Integer, Double> timeOfDayCoef;
    private AccidentType accidentType;
    private AccidentSeverity accidentSeverity;


    public AccidentRateCalculationMCR(double scaleFactor, AccidentsContext accidentsContext, AnalysisEventHandler analzyer, AccidentType accidentType, AccidentSeverity accidentSeverity, String basePath) {
        this.SCALEFACTOR = scaleFactor;
        this.accidentsContext = accidentsContext;
        this.analzyer = analzyer;
        this.accidentType = accidentType;
        this.accidentSeverity = accidentSeverity;
        this.binaryLogitCoef = new AccidentRateModelCoefficientReader(accidentType, accidentSeverity, basePath + "binaryModel.csv").readData();
        //this.poissonCoef = new AccidentRateModelCoefficientReader(accidentType, accidentSeverity, basePath + "countModel.csv").readData();
        //this.timeOfDayCoef = new AccidentRateModelCoefficientReader(accidentType, accidentSeverity, basePath + "timeOfDay.csv").readTimeOfDayData();
        this.poissonCoef = null;
        this.timeOfDayCoef = null;
    }

    protected void run(Collection<? extends Link> links) {
        for (Link link : links){
            computeLinkAccidentFrequency(link);
        }
    }

    private void computeLinkAccidentFrequency(Link link) {
        double probZeroCrash = getProbabilityZeroCrashBinaryLogit(link);
        double meanCrash = getMeanCrashPoisson(link);
        double finalCrashRate = meanCrash*(1-probZeroCrash)/3./365.; //convert 3-year accident frequency to one-day frequency

        OpenIntFloatHashMap crashRateByTimeOfDay = new OpenIntFloatHashMap();
        for(int hour : timeOfDayCoef.keySet()){
            crashRateByTimeOfDay.put(hour, (float) (finalCrashRate*timeOfDayCoef.get(hour)));
        }

        switch (accidentSeverity){
            case LIGHT:
                this.accidentsContext.getLinkId2info().get(link.getId()).getLightCasualityExposureByAccidentTypeByTime().put(accidentType, crashRateByTimeOfDay);
                break;
            case SEVEREFATAL:
                this.accidentsContext.getLinkId2info().get(link.getId()).getSevereFatalCasualityExposureByAccidentTypeByTime().put(accidentType, crashRateByTimeOfDay);
                break;
            default:
                throw new RuntimeException("Undefined accident severity " + accidentSeverity);
        }
    }


    private double getProbabilityZeroCrashBinaryLogit(Link link){
        // todo: given that the models are estimated on hourly basis, we may need to getDemand by interval then.

        double utilityZeroCrash = 0.;
        utilityZeroCrash += binaryLogitCoef.get("(Intercept)");

        // bikeStress
        utilityZeroCrash += (double) (link.getAttributes().getAttribute("bikeStress")) * binaryLogitCoef.get("bikeStress");

        // bikeStressJct
        // todo: is this variable link or node-based ?
        utilityZeroCrash += (double) (link.getAttributes().getAttribute("bikeStressJct")) * binaryLogitCoef.get("bikeStressJct");

        // log(length)
        double linkLength = link.getLength();
        utilityZeroCrash += Math.log(linkLength) * binaryLogitCoef.get("log(length_sum)");

        // log(bike+0.1)
        double bikeDailyDemand = analzyer.getDemand(link.getId(),"bike") * SCALEFACTOR / 1000.0; // todo: why 1000 ??
        if(bikeDailyDemand == 0) {
            if (accidentType.equals(AccidentType.BIKEBIKE) | accidentType.equals(AccidentType.BIKECAR)) {
                return 1.;
            }
        }
        utilityZeroCrash += Math.log(bikeDailyDemand + 0.1) * binaryLogitCoef.get("log(bike + 0.1)");

        // motor

        // speed limit
        String speedLimit = link.getAttributes().getAttribute("speed_limit").toString();
        switch (speedLimit){
            case "<20MPH":
                utilityZeroCrash += binaryLogitCoef.get("speed_limit<20 MPH");
                break;
            case "20MPH":
                utilityZeroCrash += binaryLogitCoef.get("speed_limit20MPH");
                break;
            case "20 - 29 MPH":
                utilityZeroCrash += binaryLogitCoef.get("speed_limit20 - 29 MPH");
                break;
            default:
                utilityZeroCrash += 0; // todo: what is default ??

        }

        // car
        double carDailyDemand = analzyer.getDemand(link.getId(),"car") * SCALEFACTOR / 1000.0;

        if(carDailyDemand==0) {
            if (accidentType.equals(AccidentType.CAR) | accidentType.equals(AccidentType.BIKECAR)) {
                return 1.;
            }
        }
        utilityZeroCrash += carDailyDemand * binaryLogitCoef.get("carAADTIn1000");

        // motor

        // walk
        double pedDailyDemand = analzyer.getDemand(link.getId(),"walk") * SCALEFACTOR / 1000.0;
        if(pedDailyDemand==0) {
            if (accidentType.equals(AccidentType.PED)) {
                return 1.;
            }
        }
        utilityZeroCrash += pedDailyDemand * binaryLogitCoef.get("pedestrianAADTIn1000");

        String linkType = link.getAttributes().getAttribute("accidentLinkType").toString();
        switch (linkType) {
            case "motorway":
                utilityZeroCrash += carDailyDemand * binaryLogitCoef.get("carAADTIn1000_motorway");
                utilityZeroCrash += binaryLogitCoef.get("motorway");
                break;
            case "primary":
            case "trunk":
                utilityZeroCrash += carDailyDemand * binaryLogitCoef.get("carAADTIn1000_primary");
                utilityZeroCrash += binaryLogitCoef.get("primary");
                break;
            case "secondary":
                utilityZeroCrash += carDailyDemand * binaryLogitCoef.get("carAADTIn1000_secondary");
                utilityZeroCrash += binaryLogitCoef.get("secondary");
                break;
            case "tertiary":
                utilityZeroCrash += carDailyDemand * binaryLogitCoef.get("carAADTIn1000_tertiary");
                utilityZeroCrash += binaryLogitCoef.get("tertiary");
                break;
            default:
                utilityZeroCrash += carDailyDemand * binaryLogitCoef.get("carAADTIn1000_residential");
                utilityZeroCrash += binaryLogitCoef.get("residential");
        }

        //double intersections = Double.parseDouble(link.getAttributes().getAttribute("intersections").toString());
        double intersections = 0.;
        utilityZeroCrash += intersections * binaryLogitCoef.get("intersections");

        return  Math.exp(utilityZeroCrash) / (1. + Math.exp(utilityZeroCrash));
    }

    private double getMeanCrashPoisson(Link link){
        double meanCrashRate = 0.;
        meanCrashRate += poissonCoef.get("intercept");

        double carDailyDemand = analzyer.getDemand(link.getId(),"car") * SCALEFACTOR / 1000.0;
        meanCrashRate += carDailyDemand * poissonCoef.get("carAADTIn1000");

        double bikeDailyDemand = analzyer.getDemand(link.getId(),"bike") * SCALEFACTOR / 1000.0;
        meanCrashRate += bikeDailyDemand * poissonCoef.get("bikeAADTIn1000");

        double pedDailyDemand = analzyer.getDemand(link.getId(),"walk") * SCALEFACTOR / 1000.0;
        meanCrashRate += pedDailyDemand * poissonCoef.get("pedestrianAADTIn1000");


        String linkType = link.getAttributes().getAttribute("accidentLinkType").toString();
        switch (linkType) {
            case "motorway":
                meanCrashRate += carDailyDemand * poissonCoef.get("carAADTIn1000_motorway");
                meanCrashRate += poissonCoef.get("motorway");
                meanCrashRate += poissonCoef.get("motorwayCalibration");
                break;
            case "primary":
            case "trunk":
                meanCrashRate += carDailyDemand * poissonCoef.get("carAADTIn1000_primary");
                meanCrashRate += poissonCoef.get("primary");
                meanCrashRate += poissonCoef.get("primaryCalibration");
                break;
            case "secondary":
                meanCrashRate += carDailyDemand * poissonCoef.get("carAADTIn1000_secondary");
                meanCrashRate += poissonCoef.get("secondary");
                meanCrashRate += poissonCoef.get("secondaryCalibration");
                break;
            case "tertiary":
                meanCrashRate += carDailyDemand * poissonCoef.get("carAADTIn1000_tertiary");
                meanCrashRate += poissonCoef.get("tertiary");
                meanCrashRate += poissonCoef.get("tertiaryCalibration");
                break;
            default:
                meanCrashRate += carDailyDemand * poissonCoef.get("carAADTIn1000_residential");
                meanCrashRate += poissonCoef.get("residential");
                meanCrashRate += poissonCoef.get("residentialCalibration");
        }

        double linkLength = link.getLength();
        meanCrashRate += Math.log(linkLength) * poissonCoef.get("linkLength_log");

        //double intersections = Double.parseDouble(link.getAttributes().getAttribute("intersections").toString());
        //TODO: number of intersection is either 0,1,2 in the implementation, because not using OSM link
        double intersections = 0.0;
        meanCrashRate += intersections * poissonCoef.get("intersections");

        return Math.exp(meanCrashRate);
    }
}
