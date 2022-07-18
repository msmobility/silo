package de.tum.bgu.msm.matsim;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Location;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.accessibility.CommutingTimeProbability;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.data.vehicle.VehicleType;
import de.tum.bgu.msm.models.modeChoice.CommuteModeChoice;
import de.tum.bgu.msm.models.modeChoice.CommuteModeChoiceMapping;
import de.tum.bgu.msm.properties.Properties;
import org.matsim.api.core.v01.TransportMode;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * Copy of SimpleCommuteModeChoice, but with person in argument of travel time request
 */
public class SimpleMatsimCommuteModeChoice implements CommuteModeChoice {

    private final Properties properties;
    private final CommutingTimeProbability commutingTimeProbability;
    private final JobDataManager jobDataManager;
    private final GeoData geoData;
    private Random random;

    public SimpleMatsimCommuteModeChoice(DataContainer dataContainer,
                                         Properties properties, Random random) {
        this.properties = properties;
        this.commutingTimeProbability = dataContainer.getCommutingTimeProbability();
        this.jobDataManager = dataContainer.getJobDataManager();
        this.random = random;
        geoData = dataContainer.getGeoData();
    }


    @Override
    public CommuteModeChoiceMapping assignCommuteModeChoice(Location from, TravelTimes travelTimes, Household household) {

        CommuteModeChoiceMapping commuteModeChoiceMapping = new CommuteModeChoiceMapping(HouseholdUtil.getNumberOfWorkers(household));

        Map<Integer, Map<String, Double>> commuteModesByPerson = new HashMap<>();
        TreeMap<Double, Person> personByProbability = new TreeMap<>();

        for (Person pp : household.getPersons().values()) {
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {

                Job job = jobDataManager.getJobFromId(pp.getJobId());

                // TODO clean up the following line
                int ptMinutes = (int) ((MatsimTravelTimesAndCosts) travelTimes).getTravelTime(from, job, job.getStartTimeInSeconds().orElse((int) properties.transportModel.peakHour_s), TransportMode.pt, pp);
                double ptUtility = commutingTimeProbability.getCommutingTimeProbability(ptMinutes, TransportMode.pt);

                if (!pp.hasDriverLicense() ||  (int) household.getVehicles().stream().filter(vv -> vv.getType().equals(VehicleType.CAR)).count() == 0) {
                    CommuteModeChoiceMapping.CommuteMode ptCommuteMode = new CommuteModeChoiceMapping.CommuteMode(TransportMode.pt, ptUtility);
                    commuteModeChoiceMapping.assignMode(ptCommuteMode, pp);
                } else {
                    // TODO clean up the following line
                    int carMinutes = (int) ((MatsimTravelTimesAndCosts) travelTimes).getTravelTime(from, job, job.getStartTimeInSeconds().orElse((int) properties.transportModel.peakHour_s), TransportMode.car, pp);
                    double carUtility = commutingTimeProbability.getCommutingTimeProbability(carMinutes, TransportMode.car);
                    Map<String, Double> utilityByMode = new HashMap<>();
                    utilityByMode.put(TransportMode.car, carUtility);
                    utilityByMode.put(TransportMode.pt, ptUtility);
                    commuteModesByPerson.put(pp.getId(), utilityByMode);
                    double probabilityAsKey;
                    if (carUtility == 0 && ptUtility == 0) {
                        probabilityAsKey = 0.5;
                    } else {
                        probabilityAsKey = carUtility / (carUtility + ptUtility);
                    }
                    while (personByProbability.containsKey(probabilityAsKey)) {
                        //more than one hh member has exactly the same probability, so it would be replaced in the treemap
                        probabilityAsKey += random.nextDouble();
                    }
                    personByProbability.put(probabilityAsKey, pp);
                }
            }
        }

        int counter =  (int) household.getVehicles().stream().filter(vv -> vv.getType().equals(VehicleType.CAR)).count();

        for (Map.Entry<Double, Person> personForProbability : personByProbability.descendingMap().entrySet()) {
            Person person = personForProbability.getValue();
            CommuteModeChoiceMapping.CommuteMode commuteMode;
            if (counter == 0) {
                commuteMode = new CommuteModeChoiceMapping.CommuteMode(TransportMode.pt, commuteModesByPerson.get(person.getId()).get(TransportMode.pt));
            } else {
                if (random.nextDouble() < personForProbability.getKey()) {
                    commuteMode = new CommuteModeChoiceMapping.CommuteMode(TransportMode.car, commuteModesByPerson.get(person.getId()).get(TransportMode.car));
                    counter--;
                } else {
                    commuteMode = new CommuteModeChoiceMapping.CommuteMode(TransportMode.pt, commuteModesByPerson.get(person.getId()).get(TransportMode.pt));
                }
            }
            commuteModeChoiceMapping.assignMode(commuteMode, person);
        }

        return commuteModeChoiceMapping;
    }

    @Override
    public CommuteModeChoiceMapping assignRegionalCommuteModeChoice(Region region, TravelTimes travelTimes, Household household) {

        CommuteModeChoiceMapping commuteModeChoiceMapping = new CommuteModeChoiceMapping(HouseholdUtil.getNumberOfWorkers(household));

        Map<Integer, Map<String, Double>> commuteModesByPerson = new HashMap<>();
        TreeMap<Double, Person> personByProbability = new TreeMap<>();


        for (Person pp : household.getPersons().values()) {
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {

                Job job = jobDataManager.getJobFromId(pp.getJobId());
                Zone jobZone = geoData.getZones().get(job.getZoneId());


                int ptMinutes = (int) travelTimes.getTravelTimeFromRegion(region, jobZone, job.getStartTimeInSeconds().orElse((int) properties.transportModel.peakHour_s), TransportMode.pt);
                double ptUtility = commutingTimeProbability.getCommutingTimeProbability(ptMinutes, TransportMode.pt);

                if (!pp.hasDriverLicense() ||  (int) household.getVehicles().stream().filter(vv -> vv.getType().equals(VehicleType.CAR)).count() == 0) {
                    CommuteModeChoiceMapping.CommuteMode ptCommuteMode = new CommuteModeChoiceMapping.CommuteMode(TransportMode.pt, ptUtility);
                    commuteModeChoiceMapping.assignMode(ptCommuteMode, pp);
                } else {
                    int carMinutes = (int) travelTimes.getTravelTimeFromRegion(region, jobZone, job.getStartTimeInSeconds().orElse((int) properties.transportModel.peakHour_s), TransportMode.car);
                    double carUtility = commutingTimeProbability.getCommutingTimeProbability(carMinutes, TransportMode.car);
                    Map<String, Double> utilityByMode = new HashMap<>();
                    utilityByMode.put(TransportMode.car, carUtility);
                    utilityByMode.put(TransportMode.pt, ptUtility);
                    commuteModesByPerson.put(pp.getId(), utilityByMode);
                    double probabilityAsKey;
                    if (carUtility == 0 && ptUtility == 0) {
                        probabilityAsKey = 0.5;
                    } else {
                        probabilityAsKey = carUtility / (carUtility + ptUtility);
                    }
                    while (personByProbability.containsKey(probabilityAsKey)) {
                        //more than one hh member has exactly the same probability, so it would be replaced in the treemap
                        probabilityAsKey += random.nextDouble();
                    }
                    personByProbability.put(probabilityAsKey, pp);
                }
            }
        }

        int counter =  (int) household.getVehicles().stream().filter(vv -> vv.getType().equals(VehicleType.CAR)).count();

        for (Map.Entry<Double, Person> personForProbability : personByProbability.descendingMap().entrySet()) {
            Person person = personForProbability.getValue();
            CommuteModeChoiceMapping.CommuteMode commuteMode;
            if (counter == 0) {
                commuteMode = new CommuteModeChoiceMapping.CommuteMode(TransportMode.pt, commuteModesByPerson.get(person.getId()).get(TransportMode.pt));
            } else {
                if (random.nextDouble() < personForProbability.getKey()) {
                    commuteMode = new CommuteModeChoiceMapping.CommuteMode(TransportMode.car, commuteModesByPerson.get(person.getId()).get(TransportMode.car));
                    counter--;
                } else {
                    commuteMode = new CommuteModeChoiceMapping.CommuteMode(TransportMode.pt, commuteModesByPerson.get(person.getId()).get(TransportMode.pt));
                }
            }
            commuteModeChoiceMapping.assignMode(commuteMode, person);
        }

        return commuteModeChoiceMapping;
    }

    @Override
    public CommuteModeChoiceMapping assignRegionalCommuteModeChoiceToFindNewJobs(Region jobRegion, Zone homeZone, TravelTimes travelTimes, Person person) {
        return null;
    }

}
