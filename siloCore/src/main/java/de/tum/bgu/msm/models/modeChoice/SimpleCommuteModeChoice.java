package de.tum.bgu.msm.models.modeChoice;

import cern.colt.map.tobject.OpenIntObjectHashMap;
import de.tum.bgu.msm.data.Location;
import de.tum.bgu.msm.data.accessibility.CommutingTimeProbability;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.properties.Properties;
import org.matsim.api.core.v01.TransportMode;

import java.util.*;
import java.util.function.BiFunction;

public class SimpleCommuteModeChoice implements CommuteModeChoice {

    private final Properties properties;
    private final CommutingTimeProbability commutingTimeProbability;
    private final JobDataManager jobDataManager;
    private Random random;

    public SimpleCommuteModeChoice(CommutingTimeProbability commutingTimeProbability, JobDataManager jobDataManager,
                                   Properties properties, Random random) {
        this.properties = properties;
        this.commutingTimeProbability = commutingTimeProbability;
        this.jobDataManager = jobDataManager;
        this.random = random;
    }


    @Override
    public CommuteModeChoiceMapping assignCommuteModeChoice(Location from, TravelTimes travelTimes, Household household) {

        CommuteModeChoiceMapping commuteModeChoiceMapping = new CommuteModeChoiceMapping(HouseholdUtil.getNumberOfWorkers(household));

        Map<Integer, Map<String, Double>> commuteModesByPerson = new HashMap<>();
        TreeMap<Double, Person> personByProbability = new TreeMap<>();

        for (Person pp : household.getPersons().values()) {
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {

                Job job = jobDataManager.getJobFromId(pp.getJobId());

                int ptMinutes = (int) travelTimes.getTravelTime(from, job, job.getStartTimeInSeconds().orElse((int) properties.transportModel.peakHour_s), TransportMode.pt);
                double ptUtility = commutingTimeProbability.getCommutingTimeProbability(ptMinutes);

                if (!pp.hasDriverLicense() || household.getAutos() == 0) {
                    CommuteModeChoiceMapping.CommuteMode ptCommuteMode = new CommuteModeChoiceMapping.CommuteMode(TransportMode.pt, ptUtility);
                    commuteModeChoiceMapping.assignMode(ptCommuteMode, pp);
                } else {
                    int carMinutes = (int) travelTimes.getTravelTime(from, job, job.getStartTimeInSeconds().orElse((int) properties.transportModel.peakHour_s), TransportMode.car);
                    double carUtility = commutingTimeProbability.getCommutingTimeProbability(carMinutes);
                    Map<String, Double> utilityByMode = new HashMap<>();
                    utilityByMode.put(TransportMode.car, carUtility);
                    utilityByMode.put(TransportMode.pt, ptUtility);
                    commuteModesByPerson.put(pp.getId(), utilityByMode);
                    double probabilityAsKey = carUtility / (carUtility + ptUtility);
                    if (personByProbability.containsKey(probabilityAsKey)){
                        //more than one hh member has exactly the same probability, so it would be replaced in the treemap
                        probabilityAsKey += random.nextDouble();
                    }
                    personByProbability.put(probabilityAsKey, pp);
                }
            }
        }

        int counter = household.getAutos();

        for (Map.Entry<Double, Person> personForProbability : personByProbability.entrySet()) {
            Person person = personForProbability.getValue();
            CommuteModeChoiceMapping.CommuteMode commuteMode;
            if(counter == 0) {
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
}
