package de.tum.bgu.msm.health.io;

import de.tum.bgu.msm.data.Day;
import de.tum.bgu.msm.data.Mode;
import de.tum.bgu.msm.data.Purpose;
import de.tum.bgu.msm.health.data.Trip;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.core.utils.geometry.CoordUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TripReaderHealth {

    private final static Logger logger = LogManager.getLogger(TripReaderHealth.class);


    public Map<Integer, Trip> readData(String path) {
        logger.info("Reading mito trip micro data from csv file");
        Map<Integer, Trip> mitoTrips = new HashMap<>();

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            logger.info("Header of mito trip file: " + String.join(",", header));
            int posId = SiloUtil.findPositionInArray("t.id", header);
            int posPurpose = SiloUtil.findPositionInArray("t.purpose", header);
            int posOriginZone = SiloUtil.findPositionInArray("originZone", header);
            int posOriginType = SiloUtil.findPositionInArray("originType", header);
            int posOriginMicroId = SiloUtil.findPositionInArray("originId", header);
            int posOriginX = SiloUtil.findPositionInArray("originX", header);
            int posOriginY = SiloUtil.findPositionInArray("originY", header);

            int posDestinationZone = SiloUtil.findPositionInArray("destinationZone", header);
            int posDestinationType = SiloUtil.findPositionInArray("destinationType", header);
            int posDestinationMicroId = SiloUtil.findPositionInArray("destinationId", header);
            int posDestinationX = SiloUtil.findPositionInArray("destinationX", header);
            int posDestinationY = SiloUtil.findPositionInArray("destinationY", header);

            int posMode = SiloUtil.findPositionInArray("mode", header);
            int posPerson = SiloUtil.findPositionInArray("p.ID", header);
            int posDepartureTime = SiloUtil.findPositionInArray("departure_time", header);
            int posActivityDuration = SiloUtil.findPositionInArray("activity_duration", header);
            int posDepartureTimeReturn = SiloUtil.findPositionInArray("departure_time_return", header);
            int posDepartureDay = SiloUtil.findPositionInArray("departure_day", header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id = Integer.parseInt(lineElements[posId]);
                Purpose tripPurpose = Purpose.valueOf(lineElements[posPurpose]);

                Trip mitoTrip;
                if(!mitoTrips.containsKey(id)) {
                    mitoTrip = new Trip(id, tripPurpose);
                }else{
                    logger.warn("Trip id: " + id + " already exists in the trip list!");
                    continue;
                }

                if(lineElements[posOriginX].equals("null")||lineElements[posDestinationX].equals("null")){
                    logger.warn("trip id: " + id + "no origin or destination microlocation!");
                }

                mitoTrip.setTripOriginZone(Integer.parseInt(lineElements[posOriginZone]));
                mitoTrip.setTripOriginType(lineElements[posOriginType]);
                if(lineElements[posOriginType].equals("zoneCentroid")){
                    mitoTrip.setTripOriginMicroId(mitoTrip.getTripOriginZone());
                }else {
                    mitoTrip.setTripOriginMicroId(Integer.parseInt(lineElements[posOriginMicroId]));
                }
                mitoTrip.setTripOrigin(CoordUtils.createCoord(Double.parseDouble(lineElements[posOriginX]),Double.parseDouble(lineElements[posOriginY])));

                mitoTrip.setTripDestinationZone(Integer.parseInt(lineElements[posDestinationZone]));
                mitoTrip.setTripDestinationType(lineElements[posDestinationType]);
                if(lineElements[posDestinationType].equals("zoneCentroid")){
                    mitoTrip.setTripDestinationMicroId(mitoTrip.getTripDestinationZone());
                }else {
                    mitoTrip.setTripDestinationMicroId(Integer.parseInt(lineElements[posDestinationMicroId]));
                }
                mitoTrip.setTripDestination(CoordUtils.createCoord(Double.parseDouble(lineElements[posDestinationX]),Double.parseDouble(lineElements[posDestinationY])));

                mitoTrip.setTripMode(Mode.valueOf(lineElements[posMode]));
                mitoTrip.setPerson(Integer.parseInt(lineElements[posPerson]));
                mitoTrip.setDepartureInMinutes(Integer.parseInt(lineElements[posDepartureTime]));
                mitoTrip.setActivityDuration(Integer.parseInt(lineElements[posActivityDuration]));
                String departureTimeReturn = lineElements[posDepartureTimeReturn];
                if(!departureTimeReturn.equals("NA")) {
                    mitoTrip.setDepartureReturnInMinutes(Integer.parseInt(departureTimeReturn));
                }
                mitoTrip.setDepartureDay(Day.valueOf(lineElements[posDepartureDay]));

                mitoTrips.put(id,mitoTrip);
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading mito trip file: " + path);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " mito trips.");
        return mitoTrips;
    }
}
