package isuh;

import de.tum.bgu.msm.data.person.Person;
import org.matsim.api.core.v01.Coord;

import java.util.HashMap;
import java.util.Map;

public class IsuhWorker {

    private final Person person;

    private final Coord home;
    private final Coord work;
    private final Map<String, Map<String,Double>> modeAttributes = new HashMap<>();

    public IsuhWorker(Person person, Coord home, Coord work) {
        this.person = person;
        this.home = home;
        this.work = work;
    }

    public Person getPerson() {
        return person;
    }

    public Coord getHomeCoord() {
        return home;
    }

    public Coord getWorkCoord() {
        return work;
    }

    public void setAttributes(String route, Map<String,Double> attributes) {
        modeAttributes.put(route,attributes);
    }


}
