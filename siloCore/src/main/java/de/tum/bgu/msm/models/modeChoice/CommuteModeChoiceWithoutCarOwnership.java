package de.tum.bgu.msm.models.modeChoice;

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
import de.tum.bgu.msm.properties.Properties;
import org.matsim.api.core.v01.TransportMode;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class CommuteModeChoiceWithoutCarOwnership implements CommuteModeChoice {

    private final Properties properties;
    private final CommutingTimeProbability commutingTimeProbability;
    private final JobDataManager jobDataManager;
    private final GeoData geoData;
    private Random random;

    public CommuteModeChoiceWithoutCarOwnership(DataContainer dataContainer,
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

        for (Person pp : household.getPersons().values()) {
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {

                Job job = jobDataManager.getJobFromId(pp.getJobId());

                int ptMinutes = (int) travelTimes.getTravelTime(from, job, job.getStartTimeInSeconds().orElse((int) properties.transportModel.peakHour_s), TransportMode.pt);
                double ptUtility = commutingTimeProbability.getCommutingTimeProbability(ptMinutes, TransportMode.pt);


                int carMinutes = (int) travelTimes.getTravelTime(from, job, job.getStartTimeInSeconds().orElse((int) properties.transportModel.peakHour_s), TransportMode.car);
                double carUtility = commutingTimeProbability.getCommutingTimeProbability(carMinutes, TransportMode.car);
                Map<String, Double> utilityByMode = new HashMap<>();

                double probabilityCar = 0;
                if (carUtility == 0 && ptUtility == 0) {
                    probabilityCar = 0.5;
                } else {
                    probabilityCar = carUtility / (carUtility + ptUtility);
                }

                if (random.nextDouble() < probabilityCar) {
                    commuteModeChoiceMapping.assignMode(new CommuteModeChoiceMapping.CommuteMode(TransportMode.car, carUtility), pp);
                } else {
                    commuteModeChoiceMapping.assignMode(new CommuteModeChoiceMapping.CommuteMode(TransportMode.pt, carUtility), pp);

                }
            }
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

                int carMinutes = (int) travelTimes.getTravelTimeFromRegion(region, jobZone, job.getStartTimeInSeconds().orElse((int) properties.transportModel.peakHour_s), TransportMode.car);
                double carUtility = commutingTimeProbability.getCommutingTimeProbability(carMinutes, TransportMode.car);


                Map<String, Double> utilityByMode = new HashMap<>();

                double probabilityCar = 0;
                if (carUtility == 0 && ptUtility == 0) {
                    probabilityCar = 0.5;
                } else {
                    probabilityCar = carUtility / (carUtility + ptUtility);
                }

                if (random.nextDouble() < probabilityCar) {
                    commuteModeChoiceMapping.assignMode(new CommuteModeChoiceMapping.CommuteMode(TransportMode.car, carUtility), pp);
                } else {
                    commuteModeChoiceMapping.assignMode(new CommuteModeChoiceMapping.CommuteMode(TransportMode.pt, carUtility), pp);

                }


            }
        }
        return commuteModeChoiceMapping;
    }
}