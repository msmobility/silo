package de.tum.bgu.msm.data;

import de.tum.bgu.msm.transportModel.tripGeneration.tripPurposes;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to hold trip data
 * @author Rolf Moeckel
 * Created on Jan 15th, 2017
 *
 */
public class Trip implements Serializable {

    static Logger logger = Logger.getLogger(Trip.class);

    private static final Map<Integer, Trip> tripMap = new HashMap<>();

    private int tripId;
    private int personId;
    private tripPurposes purpose;
    private int origin;


    public Trip(int id, int personId, tripPurposes purpose, int origin) {
        this.tripId = id;
        this.personId = personId;
        this.purpose = purpose;
        this.origin = origin;
        tripMap.put(id,this);
    }


    public static Trip[] getTripArray() {
        return tripMap.values().toArray(new Trip[tripMap.size()]);
    }

}
