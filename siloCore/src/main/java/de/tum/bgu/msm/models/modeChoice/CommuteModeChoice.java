package de.tum.bgu.msm.models.modeChoice;

import de.tum.bgu.msm.data.Location;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;

public interface CommuteModeChoice {

    /*
    Returns commute mode choice and commute time probability to search for a dwelling at from with fixed job locations
     given by employers in the household
     */
    CommuteModeChoiceMapping assignCommuteModeChoice(Location from, TravelTimes travelTimes, Household household);

    /*
    Returns commute mode choice and commute time probability to search for a region where to move to with fixed job
     locationa given by employers in the household
     */
    CommuteModeChoiceMapping assignRegionalCommuteModeChoice(Region region, TravelTimes travelTimes, Household household);

    /*
    Returns commute mode choice and commute time probability to search for a region where to find a job,
     given a home location
     */
    CommuteModeChoiceMapping assignRegionalCommuteModeChoiceToFindNewJobs(Region jobRegion,
                                                                          Zone homeZone,
                                                                          TravelTimes travelTimes,
                                                                          Person person);
}
