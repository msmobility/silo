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
import de.tum.bgu.msm.models.modeChoice.CommuteModeChoice;
import de.tum.bgu.msm.models.modeChoice.CommuteModeChoiceMapping;
import de.tum.bgu.msm.models.modeChoice.SimpleCommuteModeChoice;
import de.tum.bgu.msm.properties.Properties;
import org.matsim.api.core.v01.TransportMode;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class AVSimpleCommuteModeChoice implements CommuteModeChoice {

    private final Properties properties;
    private final CommutingTimeProbability commutingTimeProbability;
    private final JobDataManager jobDataManager;
    private final GeoData geoData;
    private Random random;

    private final SimpleCommuteModeChoice conventionalVehicleCommuteModeChoice;

    public AVSimpleCommuteModeChoice(DataContainer dataContainer,
                                   Properties properties, Random random) {
        this.properties = properties;
        this.commutingTimeProbability = dataContainer.getCommutingTimeProbability();
        this.jobDataManager = dataContainer.getJobDataManager();
        this.random = random;
        geoData = dataContainer.getGeoData();
        conventionalVehicleCommuteModeChoice = new SimpleCommuteModeChoice(dataContainer, properties, random);
    }



    @Override
    public CommuteModeChoiceMapping assignCommuteModeChoice(Location from, TravelTimes travelTimes, Household household) {

        if (((HouseholdMuc) household).getAutonomous() == 0){
            return conventionalVehicleCommuteModeChoice.assignCommuteModeChoice(from, travelTimes, household);

        } else {
            CommuteModeChoiceMapping commuteModeChoiceMapping = new CommuteModeChoiceMapping(HouseholdUtil.getNumberOfWorkers(household));
            for (Person pp : household.getPersons().values()) {

                if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {
                    Job job = jobDataManager.getJobFromId(pp.getJobId());
                    int ptMinutes = (int) travelTimes.getTravelTime(from, job, job.getStartTimeInSeconds().orElse((int) properties.transportModel.peakHour_s), TransportMode.pt);
                    int carMinutes = (int) travelTimes.getTravelTime(from, job, job.getStartTimeInSeconds().orElse((int) properties.transportModel.peakHour_s), TransportMode.car);
                    double ptUtility = commutingTimeProbability.getCommutingTimeProbability(ptMinutes, TransportMode.pt);
                    double avUtility = commutingTimeProbability.getCommutingTimeProbability(carMinutes, "av");
                    double probabilityAv = avUtility / (avUtility + ptUtility);

                    if (random.nextDouble() < probabilityAv) {
                        CommuteModeChoiceMapping.CommuteMode avCommuteMode = new CommuteModeChoiceMapping.CommuteMode("av", ptUtility);
                        commuteModeChoiceMapping.assignMode(avCommuteMode,  pp);
                    } else {
                        CommuteModeChoiceMapping.CommuteMode ptMode = new CommuteModeChoiceMapping.CommuteMode(TransportMode.car,ptUtility );
                        commuteModeChoiceMapping.assignMode(ptMode,  pp);
                    }
                }
            }
            return commuteModeChoiceMapping;
        }
    }

    @Override
    public CommuteModeChoiceMapping assignRegionalCommuteModeChoice(Region region, TravelTimes travelTimes, Household household) {

        if (((HouseholdMuc) household).getAutonomous() == 0) {
            return conventionalVehicleCommuteModeChoice.assignRegionalCommuteModeChoice(region, travelTimes, household);
        } else {
            CommuteModeChoiceMapping commuteModeChoiceMapping = new CommuteModeChoiceMapping(HouseholdUtil.getNumberOfWorkers(household));
            for (Person pp : household.getPersons().values()) {

                if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {
                    Job job = jobDataManager.getJobFromId(pp.getJobId());
                    Zone jobZone = geoData.getZones().get(job.getZoneId());
                    int ptMinutes = (int) travelTimes.getTravelTimeFromRegion(region, jobZone, job.getStartTimeInSeconds().orElse((int) properties.transportModel.peakHour_s), TransportMode.pt);
                    int carMinutes = (int) travelTimes.getTravelTimeFromRegion(region, jobZone, job.getStartTimeInSeconds().orElse((int) properties.transportModel.peakHour_s), TransportMode.car);
                    double ptUtility = commutingTimeProbability.getCommutingTimeProbability(ptMinutes, TransportMode.pt);
                    double avUtility = commutingTimeProbability.getCommutingTimeProbability(carMinutes, "av");
                    double probabilityAv = avUtility / (avUtility + ptUtility);

                    if (random.nextDouble() < probabilityAv) {
                        CommuteModeChoiceMapping.CommuteMode avCommuteMode = new CommuteModeChoiceMapping.CommuteMode("av", ptUtility);
                        commuteModeChoiceMapping.assignMode(avCommuteMode, pp);
                    } else {
                        CommuteModeChoiceMapping.CommuteMode ptMode = new CommuteModeChoiceMapping.CommuteMode(TransportMode.car, ptUtility);
                        commuteModeChoiceMapping.assignMode(ptMode, pp);
                    }
                }
            }
            return commuteModeChoiceMapping;
        }
    }
}
