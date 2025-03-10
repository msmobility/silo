package de.tum.bgu.msm.health.data;

import de.tum.bgu.msm.data.*;
import org.matsim.api.core.v01.Coord;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds trip objects for the Microsimulation Transport Orchestrator (MITO)
 *
 * @author Rolf Moeckel
 * Created on Mar 26, 2017 in Munich, Germany
 */
public class Trip implements Id {

    private final int tripId;
    private final Purpose tripPurpose;

    private Coord tripOrigin;
    private Coord tripDestination;

    private int tripOriginZone;
    private int tripDestinationZone;

    private String tripOriginType;
    private String tripDestinationType;

    private int tripOriginMicroId;
    private int tripDestinationMicroId;

    private int person;
    private Mode tripMode;
    private Day departureDay;
    private int departureInMinutes;
    private int departureReturnInMinutes;

    private double activityDuration;

    private int matsimLinks = 0;
    private double matsimConcMetersPm25 = 0.;
    private double matsimConcMetersNo2 = 0.;
    private double matsimTravelTime = 0.;
    private double matsimTravelDistance = 0.;

    private double marginalMetHours = 0.;
    private Map<String, Float> travelRiskMap = new HashMap<>();
    private Map<String, float[]> travelExposureMapByHour = new HashMap<>();
    private float[] travelNoiseExposureByHour = new float[24*7];
    private double travelNdviExposure = 0.;

    private Map<String, float[]> activityExposureMapByHour = new HashMap<>();
    private float[] activityNoiseExposureByHour = new float[24*7];
    private double activityNdviExposure = 0.;

    public Trip(int tripId, Purpose tripPurpose) {
        this.tripId = tripId;
        this.tripPurpose = tripPurpose;
    }

    @Override
    public int getId() {
        return tripId;
    }

    public Purpose getTripPurpose() {
        return tripPurpose;
    }

    public int getPerson() {
        return person;
    }

    public void setPerson(int person) {
        this.person = person;
    }

    public Mode getTripMode() {
        return tripMode;
    }

    public void setTripMode(Mode tripMode) {
        this.tripMode = tripMode;
    }

    public void setDepartureInMinutes(int departureInMinutes) {
        this.departureInMinutes = departureInMinutes;
    }

    public int getDepartureTimeInMinutes() {
        return departureInMinutes;
    }

    public void setDepartureReturnInMinutes(int departureReturnInMinutes) {
        this.departureReturnInMinutes = departureReturnInMinutes;
    }

    public int getDepartureReturnInMinutes() { return departureReturnInMinutes; }

    public int getTripId() {
        return tripId;
    }

    public boolean isHomeBased() {
        return  !this.getTripPurpose().equals(Purpose.RRT) &&
                !this.getTripPurpose().equals(Purpose.NHBW) &&
                !this.getTripPurpose().equals(Purpose.NHBO) &&
                !this.getTripPurpose().equals(Purpose.AIRPORT);
    }

    @Override
    public String toString() {
        return "Trip [id: " + this.tripId + " purpose: " + this.tripPurpose + "]";
    }

    @Override
    public int hashCode() {
        return tripId;
    }

    public double getMarginalMetHours() {
        return marginalMetHours;
    }

    public void updateMarginalMetHours(double mmetHours) {
        this.marginalMetHours += mmetHours;
    }

    public float[] getTravelNoiseExposureByHour() {
        return travelNoiseExposureByHour;
    }

    public float getTravelNoiseExposureSum() {
        float sum = 0;
        for (float num : travelNoiseExposureByHour) {
            sum += num;
        }
        return sum;
    }

    public void updateTravelNoiseExposure(float[] travelNoiseExposure) {
        for(int i=0; i< travelNoiseExposure.length; i++) {
            this.travelNoiseExposureByHour[i] += travelNoiseExposure[i];
        }
    }

    public double getTravelNdviExposure() {
        return travelNdviExposure;
    }

    public void updateTravelNdviExposure(double travelNdviExposure) {
        this.travelNdviExposure += travelNdviExposure;
    }

    public Map<String, Float> getTravelRiskMap() { return travelRiskMap; }

    public void updateTravelRiskMap(Map<String, Float> newRisks) {
        newRisks.forEach((k, v) -> travelRiskMap.merge(k, v, (v1, v2) -> v1 + v2 - v1*v2));
    }

    public Map<String, float[]> getTravelExposureMapByHour() { return travelExposureMapByHour; }

    public void updateTravelExposureMapByHour(Map<String, float[]> newExposures) {
        for (String key : newExposures.keySet()) {
            if (!travelExposureMapByHour.containsKey(key)) {
                travelExposureMapByHour.put(key, newExposures.get(key));
            }else {
                for(int i = 0; i< travelExposureMapByHour.get(key).length; i++) {
                    this.travelExposureMapByHour.get(key)[i] += newExposures.get(key)[i];
                }
            }
        }
    }

    public float getTravelExposureSum(String pollutant) {
        float sum = 0;
        for (float num : travelExposureMapByHour.get(pollutant)) {
            sum += num;
        }
        return sum;
    }

    public Map<String, float[]> getActivityExposureMapByHour() { return activityExposureMapByHour; }

    public void setActivityExposureMapByHour(Map<String, float[]> newExposures) {
        this.activityExposureMapByHour = newExposures;
    }

    public float getActivityExposureSum(String pollutant) {
        if(activityExposureMapByHour.get(pollutant)==null || activityExposureMapByHour.get(pollutant).length==0) {
            return 0;
        }

        float sum = 0;
        for (float num : activityExposureMapByHour.get(pollutant)) {
            sum += num;
        }
        return sum;
    }

    public float[] getActivityNoiseExposureByHour() {
        return activityNoiseExposureByHour;
    }

    public float getActivityNoiseExposureSum() {
        float sum = 0;
        for (float num : activityNoiseExposureByHour) {
            sum += num;
        }
        return sum;
    }

    public void setActivityNoiseExposureByHour(float[] activityNoiseExposureByHour) {
        this.activityNoiseExposureByHour = activityNoiseExposureByHour;
    }

    public double getActivityNdviExposure() {
        return activityNdviExposure;
    }

    public void setActivityNdviExposure(double activityNdviExposure) {
        this.activityNdviExposure = activityNdviExposure;
    }

    public double getActivityDuration() { return activityDuration; }

    public void setActivityDuration(double minutes) {
        this.activityDuration = minutes;
    }

    public int getMatsimLinkCount() {
        return matsimLinks;
    }

    public void updateMatsimLinkCount(int linkCount) {
        this.matsimLinks += linkCount;
    }

    public double getMatsimConcMetersPm25() {
        return matsimConcMetersPm25;
    }

    public void updateMatsimConcMetersPm25(double total) {
        this.matsimConcMetersPm25 += total;
    }

    public double getMatsimConcMetersNo2() {
        return matsimConcMetersNo2;
    }

    public void updateMatsimConcMetersNo2(double total) {
        this.matsimConcMetersNo2 += total;
    }

    public double getMatsimTravelTime() {
        return matsimTravelTime;
    }

    public void updateMatsimTravelTime(double matsimTravelTime) {
        this.matsimTravelTime += matsimTravelTime;
    }

    public double getMatsimTravelDistance() {
        return matsimTravelDistance;
    }

    public void updateMatsimTravelDistance(double matsimTravelDistance) {
        this.matsimTravelDistance += matsimTravelDistance;
    }

    public Coord getTripOrigin() {
        return tripOrigin;
    }

    public void setTripOrigin(Coord tripOrigin) {
        this.tripOrigin = tripOrigin;
    }

    public Coord getTripDestination() {
        return tripDestination;
    }

    public void setTripDestination(Coord tripDestination) {
        this.tripDestination = tripDestination;
    }

    public Day getDepartureDay() {
        return departureDay;
    }

    public void setDepartureDay(Day departureDay) {
        this.departureDay = departureDay;
    }

    public int getTripOriginZone() {
        return tripOriginZone;
    }

    public void setTripOriginZone(int tripOriginZone) {
        this.tripOriginZone = tripOriginZone;
    }

    public int getTripDestinationZone() {
        return tripDestinationZone;
    }

    public void setTripDestinationZone(int tripDestinationZone) {
        this.tripDestinationZone = tripDestinationZone;
    }

    public String getTripOriginType() {
        return tripOriginType;
    }

    public void setTripOriginType(String tripOriginType) {
        this.tripOriginType = tripOriginType;
    }

    public String getTripDestinationType() {
        return tripDestinationType;
    }

    public void setTripDestinationType(String tripDestinationType) {
        this.tripDestinationType = tripDestinationType;
    }

    public int getTripOriginMicroId() {
        return tripOriginMicroId;
    }

    public void setTripOriginMicroId(int tripOriginMicroId) {
        this.tripOriginMicroId = tripOriginMicroId;
    }

    public int getTripDestinationMicroId() {
        return tripDestinationMicroId;
    }

    public void setTripDestinationMicroId(int tripDestinationMicroId) {
        this.tripDestinationMicroId = tripDestinationMicroId;
    }


    //TODO: these are methods currently used for Munich health, need to adapt Munich health model
    public void updateTravelExposureMap(Map<String, Float> exposureMap) {
    }

    public void setActivityExposureMap(Map<String, Float> exposureMap) {
    }

    public Map<String, Float> getTravelExposureMap() { return null; }

    public Map<String, Float> getActivityExposureMap() { return null; }

}
