package sdg.data;

import de.tum.bgu.msm.data.Mode;
import de.tum.bgu.msm.data.Purpose;
import de.tum.bgu.msm.data.person.Person;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.vehicles.Vehicle;


public class Trip {
    private int id;
    private Purpose purpose;
    private Mode mode;
    private Person person;
    private int matsimPersonId;
    private double tripTravelTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Purpose getPurpose() {
        return purpose;
    }

    public void setPurpose(Purpose purpose) {
        this.purpose = purpose;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public int getMatsimPersonId() {
        return matsimPersonId;
    }

    public void setMatsimPersonId(int matsimPersonId) {
        this.matsimPersonId = matsimPersonId;
    }

    public double getTripTravelTime() {
        return tripTravelTime;
    }

    public void setTripTravelTime(double tripTravelTime) {
        this.tripTravelTime = tripTravelTime;
    }
}
