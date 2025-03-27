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
    private Map<String, Float> travelExposureMap = new HashMap<>();
    private double travelNoiseExposure = 0.;
    private double travelNdviExposure = 0.;

    private Map<String, Float> activityExposureMap = new HashMap<>();
    private double activityNoiseExposure = 0.;
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

    public void updateDepartureReturnInMinutes(int departureReturnInMinutes) {
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

    public double getTravelNoiseExposure() {
        return travelNoiseExposure;
    }

    public void updateTravelNoiseExposure(double travelNoiseExposure) {
        this.travelNoiseExposure += travelNoiseExposure;
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

    public Map<String, Float> getTravelExposureMap() { return travelExposureMap; }

    public void updateTravelExposureMap(Map<String, Float> newExposures) {
        newExposures.forEach((k, v) -> travelExposureMap.merge(k, v, (v1, v2) -> v1 + v2));
    }

    public Map<String, Float> getActivityExposureMap() { return activityExposureMap; }

    public void setActivityExposureMap(Map<String, Float> exposureMap) {
        this.activityExposureMap = exposureMap;
    }

    public double getActivityNoiseExposure() { return activityNoiseExposure; }

    public void setActivityNoiseExposure(double activityNoiseExposure) {
        this.activityNoiseExposure = activityNoiseExposure;
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
}
