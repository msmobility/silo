package de.tum.bgu.msm.run.models.relocation.relocation;

import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.models.relocation.moves.DefaultDwellingProbabilityStrategy;
import de.tum.bgu.msm.models.relocation.moves.HousingStrategy;
import de.tum.bgu.msm.models.relocation.moves.RegionProbabilityStrategyImpl;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;

public class HousingStrategyBangkok implements HousingStrategy {
    public HousingStrategyBangkok(DataContainerWithSchools dataContainer, Properties properties, TravelTimes travelTimes, DwellingUtilityStrategyBangkok dwellingUtilityStrategyBangkok, RegionUtilityStrategyBangkok regionUtilityStrategyBangkok, DefaultDwellingProbabilityStrategy defaultDwellingProbabilityStrategy, RegionProbabilityStrategyImpl regionProbabilityStrategy) {


    }

    @Override
    public void setup() {

    }

    @Override
    public boolean isHouseholdEligibleToLiveHere(Household household, Dwelling dd) {
        return false;
    }

    @Override
    public double calculateHousingUtility(Household hh, Dwelling dwelling) {
        return 0;
    }

    @Override
    public double calculateSelectDwellingProbability(double util) {
        return 0;
    }

    @Override
    public double calculateSelectRegionProbability(double util) {
        return 0;
    }

    @Override
    public void prepareYear() {

    }

    @Override
    public double calculateRegionalUtility(Household household, Region region) {
        return 0;
    }

    @Override
    public HousingStrategy duplicate() {
        return null;
    }
}
