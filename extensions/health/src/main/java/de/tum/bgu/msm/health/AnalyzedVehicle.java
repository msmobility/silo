package de.tum.bgu.msm.health;

import org.matsim.api.core.v01.Id;
import org.matsim.vehicles.Vehicle;

public class AnalyzedVehicle extends AnalyzedObject<Vehicle> {

    private double distanceTravelled;
    private double startingTime = 24*60*60;
    private double endTime = 0 * 60 *60;
    private double operatingTime = 0;

    public AnalyzedVehicle(Id<Vehicle> id) {
        super(id);
    }

    public double getDistanceTravelled(){
        return distanceTravelled;
    }

    public void addDistanceTravelled(double length) {
        this.distanceTravelled += length;
    }

    public void registerPointOfTime(double time){
        if (time < startingTime){
            startingTime = time;
        }
        if(time > endTime){
            endTime = time;
        }
    }

    public void addOperatingTime(double time){
        this.operatingTime += time;
    }

    public double getStartingTime() {
        return startingTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public double getOperatingTime() {
        return operatingTime;
    }
}
