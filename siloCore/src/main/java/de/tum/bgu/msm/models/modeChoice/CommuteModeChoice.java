package de.tum.bgu.msm.models.modeChoice;

import de.tum.bgu.msm.data.Location;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;

public interface CommuteModeChoice {

    CommuteModeChoiceMapping assignCommuteModeChoice(Location from, TravelTimes travelTimes, Household household);

}
