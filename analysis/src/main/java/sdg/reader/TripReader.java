package sdg.reader;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Mode;
import de.tum.bgu.msm.data.Purpose;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import sdg.SDGCalculator;
import sdg.data.Trip;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class TripReader {

    private final static Logger logger = Logger.getLogger(TripReader.class);

    public void readData(String path, DataContainer dataContainer) {
        logger.info("Reading trip data from mito trip csv");

        String recString = "";
        int recCount = 0;
        int test = 0;
        try {
            Map<Integer,Person> personMap = dataContainer.getHouseholdDataManager().getPersons().stream().collect(Collectors.toMap(Person::getId, pp -> pp));
            BufferedReader in = new BufferedReader(new FileReader(path));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId = SiloUtil.findPositionInArray("id", header);
            int posPurpose = SiloUtil.findPositionInArray("purpose", header);
            int posPersonId = SiloUtil.findPositionInArray("person", header);
            int posDistance = SiloUtil.findPositionInArray("distance", header);
            int posAutoTime = SiloUtil.findPositionInArray("time_auto", header);
            int posBusTime = SiloUtil.findPositionInArray("time_bus", header);
            int posTrainTime = SiloUtil.findPositionInArray("time_train", header);
            int posTramMetroTime = SiloUtil.findPositionInArray("time_tram_metro", header);
            int posMode = SiloUtil.findPositionInArray("mode", header);
            int posMatsimPersonId = SiloUtil.findPositionInArray("matSimPersonId", header);


            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id         = Integer.parseInt(lineElements[posId]);
                Purpose purpose= Purpose.valueOf(lineElements[posPurpose]);
                int personId  = Integer.parseInt(lineElements[posPersonId]);
                double distance;
                if(lineElements[posDistance].equals("NA")){
                    continue;
                }else{
                    distance  = Double.parseDouble(lineElements[posDistance]);
                }
                double autoTime  = Double.parseDouble(lineElements[posAutoTime]);
                double busTime  = Double.parseDouble(lineElements[posBusTime]);
                double trainTime  = Double.parseDouble(lineElements[posTrainTime]);
                double tramMetroTime  = Double.parseDouble(lineElements[posTramMetroTime]);
                Mode mode= Mode.valueOf(lineElements[posMode]);
                int matSimPersonId;
                if(lineElements[posMatsimPersonId].equals("null")){
                    matSimPersonId = -1;
                }else{
                    matSimPersonId = Integer.parseInt(lineElements[posMatsimPersonId]);
                    test++;
                }



                Trip trip = new Trip();
                trip.setId(id);
                trip.setPerson(personMap.get(personId));
                trip.setMatsimPersonId(matSimPersonId);
                trip.setMode(mode);
                trip.setPurpose(purpose);
                switch (mode){
                    case autoDriver:
                        trip.setTripTravelTime(autoTime);
                        break;
                    case autoPassenger:
                        trip.setTripTravelTime(autoTime);
                        break;
                    case bus:
                        trip.setTripTravelTime(busTime);
                        break;
                    case train:
                        trip.setTripTravelTime(trainTime);
                        break;
                    case tramOrMetro:
                        trip.setTripTravelTime(tramMetroTime);
                        break;
                    case walk:
                        trip.setTripTravelTime(distance/3.6);//TODO
                        break;
                    case bicycle:
                        trip.setTripTravelTime(distance/10);//TODO
                        break;
                }

                //SDGCalculator.tripList.add(trip);
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading trip file: " + path);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " trips.");
        logger.info("matsimPerson" + test);
    }
}
