package de.tum.bgu.msm.scenarios.health;

import de.tum.bgu.msm.data.*;
import org.matsim.api.core.v01.Coord;

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

    private int person;
    private Mode tripMode;
    private int departureInMinutes;
    private int departureReturnInMinutes;

    private double activityDuration;
    private double marginalMetHours = 0.;

    private int matsimLinks = 0;
    private double matsimLinkConcentrationSumPm25 = 0.;
    private double matsimLinkConcentrationSumNo2 = 0.;
    private double matsimTravelTime = 0.;
    private double matsimTravelDistance = 0.;

    private Map<String, Double> travelRiskMap = new HashMap<>();
    private Map<String, Double> travelExposureMap = new HashMap<>();
    private Map<String, Double> activityExposureMap = new HashMap<>();

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

    public Map<String, Double> getTravelRiskMap() { return travelRiskMap; }

    public Map<String, Double> getTravelExposureMap() { return travelExposureMap; }

    public Map<String, Double> getActivityExposureMap() { return activityExposureMap; }

    public void updateTravelRiskMap(Map<String, Double> newRisks) {
        newRisks.forEach((k, v) -> travelRiskMap.merge(k, v, (v1, v2) -> v1 + v2 - v1*v2));
    }

    public void updateTravelExposureMap(Map<String, Double> newExposures) {
        newExposures.forEach((k, v) -> travelExposureMap.merge(k, v, Double::sum));
    }

    public void setActivityExposureMap(Map<String, Double> newExposures) {
        this.activityExposureMap = newExposures;
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

    public double getMatsimLinkConcentrationSumPm25() {
        return matsimLinkConcentrationSumPm25;
    }

    public void updateMatsimLinkConcentrationSumPm25(double total) {
        this.matsimLinkConcentrationSumPm25 += total;
    }

    public double getMatsimLinkConcentrationSumNo2() {
        return matsimLinkConcentrationSumNo2;
    }

    public void updateMatsimLinkConcentrationSumNo2(double total) {
        this.matsimLinkConcentrationSumNo2 += total;
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

}
