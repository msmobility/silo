package de.tum.bgu.msm.io;

import de.tum.bgu.msm.data.household.Household;

import java.util.Map;

public interface HouseholdReader {

    void readData(String fileName);

    void copyData(Map<Integer, Household> householdMap);
}
