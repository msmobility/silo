package de.tum.bgu.msm.scenarios.av;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Location;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.accessibility.CommutingTimeProbability;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdMuc;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.data.vehicle.Car;
import de.tum.bgu.msm.data.vehicle.CarType;
import de.tum.bgu.msm.data.vehicle.VehicleType;
import de.tum.bgu.msm.models.modeChoice.CommuteModeChoice;
import de.tum.bgu.msm.models.modeChoice.CommuteModeChoiceMapping;
import de.tum.bgu.msm.properties.Properties;
import org.matsim.api.core.v01.TransportMode;

import java.util.*;

public class AvAndParkingSimpleModeChoice implements CommuteModeChoice {

    private final Properties properties;
    private final CommutingTimeProbability commutingTimeProbability;
    private final JobDataManager jobDataManager;
    private final GeoData geoData;
    private Random random;
    public final float B_TIME;
    public final float B_PT;
    public final float B_EXP_HOUSING_UTILITY;

    private final ParkingSimpleMoceChoice conventionalVehicleParkingBasedCommuteModeChoice;

    public AvAndParkingSimpleModeChoice(DataContainer dataContainer,
                                        Properties properties, Random random) {
        this.properties = properties;
        this.commutingTimeProbability = dataContainer.getCommutingTimeProbability();
        this.jobDataManager = dataContainer.getJobDataManager();
        this.random = random;
        geoData = dataContainer.getGeoData();
        conventionalVehicleParkingBasedCommuteModeChoice = new ParkingSimpleMoceChoice(dataContainer, properties, random);
        B_TIME = properties.moves.B_TIME;
        B_PT = properties.moves.B_PT;
        B_EXP_HOUSING_UTILITY = properties.moves.B_EXP_HOUSING_UTILITY;
    }

    public AvAndParkingSimpleModeChoice(CommutingTimeProbability commutingTimeProbability, TravelTimes travelTimes,
                                   GeoData geoData, Properties properties, Random random) {
        this.properties = properties;
        this.commutingTimeProbability = commutingTimeProbability;
        this.jobDataManager = null;
        this.random = random;
        this.geoData = geoData;
        B_TIME = properties.moves.B_TIME;
        B_PT = properties.moves.B_PT;
        B_EXP_HOUSING_UTILITY = properties.moves.B_EXP_HOUSING_UTILITY;
        conventionalVehicleParkingBasedCommuteModeChoice = new ParkingSimpleMoceChoice(commutingTimeProbability, travelTimes,
                geoData, properties, random);

    }


    @Override
    public CommuteModeChoiceMapping assignCommuteModeChoice(Location from, TravelTimes travelTimes, Household household) {

        if ( household.getVehicles().stream().filter(vv-> vv.getType().equals(VehicleType.CAR)).filter(vv -> ((Car) vv).getCarType().equals(CarType.AUTONOMOUS)).count() == 0) {
            return conventionalVehicleParkingBasedCommuteModeChoice.assignCommuteModeChoice(from, travelTimes, household);

        } else {
            //as soon as there are avs in the household the workers use them and not convetional cars.
            CommuteModeChoiceMapping commuteModeChoiceMapping = new CommuteModeChoiceMapping(HouseholdUtil.getNumberOfWorkers(household));
            for (Person pp : household.getPersons().values()) {

                if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {
                    Job job = jobDataManager.getJobFromId(pp.getJobId());
                    int ptMinutes = (int) travelTimes.getTravelTime(from, job,
                            job.getStartTimeInSeconds().orElse((int) properties.transportModel.peakHour_s), TransportMode.pt);
                    double commutingTimeProbabilityPt = commutingTimeProbability.getCommutingTimeProbability(ptMinutes, TransportMode.pt);
                    double ptUtility = B_PT + B_TIME * commutingTimeProbabilityPt;
                    int avMinutes = (int) travelTimes.getTravelTime(from, job,
                            job.getStartTimeInSeconds().orElse((int) properties.transportModel.peakHour_s), TransportMode.car);
                    double commutingTimeProbabilityAv = commutingTimeProbability.getCommutingTimeProbability(avMinutes, "av");
                    double avUtility = B_TIME * commutingTimeProbabilityAv;

                    ptUtility = Math.exp(ptUtility);
                    avUtility = Math.exp(avUtility);

                    double probabilityAv = avUtility / (avUtility + ptUtility);
                    if (random.nextDouble() < probabilityAv) {
                        CommuteModeChoiceMapping.CommuteMode avCommuteMode =
                                new CommuteModeChoiceMapping.CommuteMode("av",
                                        Math.pow(commutingTimeProbabilityAv, B_EXP_HOUSING_UTILITY));
                        commuteModeChoiceMapping.assignMode(avCommuteMode, pp);
                    } else {
                        CommuteModeChoiceMapping.CommuteMode ptMode =
                                new CommuteModeChoiceMapping.CommuteMode(TransportMode.pt,
                                        Math.pow(commutingTimeProbabilityPt, B_EXP_HOUSING_UTILITY));
                        commuteModeChoiceMapping.assignMode(ptMode, pp);
                    }
                }
            }
            return commuteModeChoiceMapping;
        }
    }

    @Override
    public CommuteModeChoiceMapping assignRegionalCommuteModeChoice(Region region, TravelTimes travelTimes, Household household) {

        if ( household.getVehicles().stream().filter(vv-> vv.getType().equals(VehicleType.CAR)).filter(vv -> ((Car) vv).getCarType().equals(CarType.AUTONOMOUS)).count() == 0){
            return conventionalVehicleParkingBasedCommuteModeChoice.assignRegionalCommuteModeChoice(region, travelTimes, household);
        } else {
            //as soon as there are avs in the household the workers use them and not convetional cars.
            CommuteModeChoiceMapping commuteModeChoiceMapping = new CommuteModeChoiceMapping(HouseholdUtil.getNumberOfWorkers(household));
            for (Person pp : household.getPersons().values()) {

                if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {
                    Job job = jobDataManager.getJobFromId(pp.getJobId());
                    Zone jobZone = geoData.getZones().get(job.getZoneId());

                    int ptMinutes = (int) travelTimes.getTravelTimeFromRegion(region, jobZone, job.getStartTimeInSeconds().orElse((int) properties.transportModel.peakHour_s), TransportMode.pt);
                    double commutingTimeProbabilityPt = commutingTimeProbability.getCommutingTimeProbability(ptMinutes, TransportMode.pt);
                    double ptUtility = B_PT + B_TIME * commutingTimeProbabilityPt;
                    int avMinutes = (int) travelTimes.getTravelTimeFromRegion(region, jobZone, job.getStartTimeInSeconds().orElse((int) properties.transportModel.peakHour_s), TransportMode.car);
                    double commutingTimeProbabilityAv = commutingTimeProbability.getCommutingTimeProbability(avMinutes, "av");
                    double avUtility = B_TIME * commutingTimeProbabilityAv;

                    ptUtility = Math.exp(ptUtility);
                    avUtility = Math.exp(avUtility);

                    double probabilityAv = avUtility / (avUtility + ptUtility);

                    if (random.nextDouble() < probabilityAv) {
                        CommuteModeChoiceMapping.CommuteMode avCommuteMode =
                                new CommuteModeChoiceMapping.CommuteMode("av",
                                        Math.pow(commutingTimeProbabilityAv, B_EXP_HOUSING_UTILITY));
                        commuteModeChoiceMapping.assignMode(avCommuteMode, pp);
                    } else {
                        CommuteModeChoiceMapping.CommuteMode ptMode =
                                new CommuteModeChoiceMapping.CommuteMode(TransportMode.pt,
                                        Math.pow(commutingTimeProbabilityPt, B_EXP_HOUSING_UTILITY));
                        commuteModeChoiceMapping.assignMode(ptMode, pp);
                    }
                }
            }
            return commuteModeChoiceMapping;
        }
    }

    @Override
    public CommuteModeChoiceMapping assignRegionalCommuteModeChoiceToFindNewJobs(Region jobRegion, Zone homeZone, TravelTimes travelTimes, Person person) {

        //the person passed to this method is not aware of having AVs

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
