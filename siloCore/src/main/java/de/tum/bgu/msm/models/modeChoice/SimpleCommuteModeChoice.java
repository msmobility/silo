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
import de.tum.bgu.msm.data.vehicle.VehicleType;
import de.tum.bgu.msm.properties.Properties;
import org.matsim.api.core.v01.TransportMode;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class SimpleCommuteModeChoice implements CommuteModeChoice {

    private final Properties properties;
    private final CommutingTimeProbability commutingTimeProbability;
    private final JobDataManager jobDataManager;
    private final GeoData geoData;
    private Random random;
    public final float B_TIME;
    public final float B_PT;
    public final float B_EXP_HOUSING_UTILITY;

    public SimpleCommuteModeChoice(DataContainer dataContainer,
                                   Properties properties, Random random) {
        this.properties = properties;
        this.commutingTimeProbability = dataContainer.getCommutingTimeProbability();
        this.jobDataManager = dataContainer.getJobDataManager();
        this.random = random;
        geoData = dataContainer.getGeoData();
        B_TIME = properties.moves.B_TIME;
        B_PT = properties.moves.B_PT;
        B_EXP_HOUSING_UTILITY = properties.moves.B_EXP_HOUSING_UTILITY;
    }

    public SimpleCommuteModeChoice(CommutingTimeProbability commutingTimeProbability, TravelTimes travelTimes,
                                                GeoData geoData, Properties properties, Random random) {
        this.properties = properties;
        this.commutingTimeProbability = commutingTimeProbability;
        this.jobDataManager = null;
        this.random = random;
        this.geoData = geoData;
        B_TIME = properties.moves.B_TIME;
        B_PT = properties.moves.B_PT;
        B_EXP_HOUSING_UTILITY = properties.moves.B_EXP_HOUSING_UTILITY;
    }


    @Override
    public CommuteModeChoiceMapping assignCommuteModeChoice(Location from, TravelTimes travelTimes, Household household) {

        CommuteModeChoiceMapping commuteModeChoiceMapping = new CommuteModeChoiceMapping(HouseholdUtil.getNumberOfWorkers(household));

        Map<Integer, Map<String, Double>> commuteModesByPerson = new HashMap<>();
        TreeMap<Double, Person> personByProbability = new TreeMap<>();

        for (Person pp : household.getPersons().values()) {
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {

                Job job = jobDataManager.getJobFromId(pp.getJobId());

                int ptMinutes = (int) travelTimes.getTravelTime(from, job,
                        job.getStartTimeInSeconds().orElse((int) properties.transportModel.peakHour_s), TransportMode.pt);
                double commutingTimeProbabilityPt = commutingTimeProbability.getCommutingTimeProbability(ptMinutes, TransportMode.pt);
                double ptUtility = B_PT + B_TIME * commutingTimeProbabilityPt;


                if (!pp.hasDriverLicense() || (int) (household.getVehicles().stream().filter(v-> v.getType().equals(VehicleType.CAR)).count()) == 0) {
                    CommuteModeChoiceMapping.CommuteMode ptCommuteMode =
                            new CommuteModeChoiceMapping.CommuteMode(TransportMode.pt, Math.pow(commutingTimeProbabilityPt, B_EXP_HOUSING_UTILITY));
                    commuteModeChoiceMapping.assignMode(ptCommuteMode, pp);
                } else {
                    int carMinutes = (int) travelTimes.getTravelTime(from, job,
                            job.getStartTimeInSeconds().orElse((int) properties.transportModel.peakHour_s), TransportMode.car);
                    double commutingTimeProbabilityCar = this.commutingTimeProbability.getCommutingTimeProbability(carMinutes, TransportMode.car);
                    double carUtility = B_TIME * commutingTimeProbabilityCar;

                    ptUtility = Math.exp(ptUtility);
                    carUtility = Math.exp(carUtility);

                    Map<String, Double> utilityByMode = new HashMap<>();
                    utilityByMode.put(TransportMode.car, Math.pow(commutingTimeProbabilityCar, B_EXP_HOUSING_UTILITY));
                    utilityByMode.put(TransportMode.pt, Math.pow(commutingTimeProbabilityPt, B_EXP_HOUSING_UTILITY));
                    commuteModesByPerson.put(pp.getId(), utilityByMode);
                    double probabilityAsKey;
                    if(carUtility == 0 && ptUtility == 0) {
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

        int counter =(int) (household.getVehicles().stream().filter(v-> v.getType().equals(VehicleType.CAR)).count()) ;

        for (Map.Entry<Double, Person> personForProbability : personByProbability.descendingMap().entrySet()) {
            Person person = personForProbability.getValue();
            CommuteModeChoiceMapping.CommuteMode commuteMode;
            if(counter == 0) {
                commuteMode = new CommuteModeChoiceMapping.CommuteMode(TransportMode.pt,
                        commuteModesByPerson.get(person.getId()).get(TransportMode.pt));
            } else {
                if (random.nextDouble() < personForProbability.getKey()) {
                    commuteMode =
                            new CommuteModeChoiceMapping.CommuteMode(TransportMode.car,
                                    commuteModesByPerson.get(person.getId()).get(TransportMode.car));
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


                int ptMinutes = (int) travelTimes.getTravelTimeFromRegion(region, jobZone,
                        job.getStartTimeInSeconds().orElse((int) properties.transportModel.peakHour_s), TransportMode.pt);
                double commutingTimeProbabilityPt = commutingTimeProbability.getCommutingTimeProbability(ptMinutes, TransportMode.pt);
                double ptUtility = B_PT + B_TIME * commutingTimeProbabilityPt;

                if (!pp.hasDriverLicense() || (int) (household.getVehicles().stream().filter(v-> v.getType().equals(VehicleType.CAR)).count())  == 0) {
                    CommuteModeChoiceMapping.CommuteMode ptCommuteMode =
                            new CommuteModeChoiceMapping.CommuteMode(TransportMode.pt, Math.pow(commutingTimeProbabilityPt, B_EXP_HOUSING_UTILITY));
                    commuteModeChoiceMapping.assignMode(ptCommuteMode, pp);
                } else {
                    int carMinutes = (int) travelTimes.getTravelTimeFromRegion(region, jobZone,
                            job.getStartTimeInSeconds().orElse((int) properties.transportModel.peakHour_s), TransportMode.car);
                    double commutingTimeProbabilityCar = this.commutingTimeProbability.getCommutingTimeProbability(carMinutes, TransportMode.car);
                    double carUtility = B_TIME * commutingTimeProbabilityCar;
                    Map<String, Double> utilityByMode = new HashMap<>();
                    utilityByMode.put(TransportMode.car, Math.pow(commutingTimeProbabilityCar, B_EXP_HOUSING_UTILITY));
                    utilityByMode.put(TransportMode.pt, Math.pow(commutingTimeProbabilityPt, B_EXP_HOUSING_UTILITY));
                    commuteModesByPerson.put(pp.getId(), utilityByMode);
                    double probabilityAsKey;
                    ptUtility = Math.exp(ptUtility);
                    carUtility = Math.exp(carUtility);
                    if(carUtility == 0 && ptUtility == 0) {
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

        int counter = (int) (household.getVehicles().stream().filter(v-> v.getType().equals(VehicleType.CAR)).count()) ;

        for (Map.Entry<Double, Person> personForProbability : personByProbability.descendingMap().entrySet()) {
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

    @Override
    public CommuteModeChoiceMapping assignRegionalCommuteModeChoiceToFindNewJobs(Region jobRegion, Zone homeZone, TravelTimes travelTimes, Person person) {

        CommuteModeChoiceMapping commuteModeChoiceMapping = new CommuteModeChoiceMapping(1);

        int ptMinutes = (int) travelTimes.getTravelTimeFromRegion(jobRegion, homeZone,
                (int) properties.transportModel.peakHour_s, TransportMode.pt);
        float commutingTimeProbabilityPt = commutingTimeProbability.getCommutingTimeProbability(ptMinutes, TransportMode.pt);
        double ptUtility = B_PT + B_TIME * commutingTimeProbabilityPt;

        int carMinutes = (int) travelTimes.getTravelTimeFromRegion(jobRegion, homeZone,
                (int) properties.transportModel.peakHour_s, TransportMode.car);
        float commutingTimeProbabilityCar = this.commutingTimeProbability.getCommutingTimeProbability(carMinutes, TransportMode.car);
        double carUtility = B_TIME * commutingTimeProbabilityCar;

        ptUtility = Math.exp(ptUtility);
        carUtility = Math.exp(carUtility);

        double probabilityCar = 0;
        if (carUtility == 0 && ptUtility == 0) {
            probabilityCar = 0.5;
        } else {
            probabilityCar = carUtility / (carUtility + ptUtility);
        }


        if (random.nextDouble() < probabilityCar) {
            commuteModeChoiceMapping.assignMode(new CommuteModeChoiceMapping.CommuteMode(TransportMode.car, Math.pow(commutingTimeProbabilityCar, B_EXP_HOUSING_UTILITY)), person);
        } else {
            commuteModeChoiceMapping.assignMode(new CommuteModeChoiceMapping.CommuteMode(TransportMode.pt, Math.pow(commutingTimeProbabilityPt, B_EXP_HOUSING_UTILITY)), person);

        }

        return commuteModeChoiceMapping;
    }
}
