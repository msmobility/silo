package de.tum.bgu.msm.models.relocation.moves;

import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.utils.Sampler;

/**
 * @author Nico
 * Interface specification of housing related utilities/decisions.
 * To be used in a {@link MovesModel}
 */
public interface HousingStrategy<T extends Dwelling> {

    /**
     * Convenience method to allow for some preliminary setup before the actual
     * simulation
     */
    void setup();

    /**
     * Evaluates whether the given household is allowed to live in the given dwelling.
     * Use for housing restrictions/ subsidies.
     * @return true if the household is allowed to live in the dwelling. False otherwise.
     */
    boolean isHouseholdEligibleToLiveHere(Household household, T dd);

    /**
     * Calculates the utility/satisfaction the given household derives from
     * the given dwelling.
     * @return the derived utility
     */
    double calculateHousingUtility(Household hh, T dwelling);

    /**
     * Calculates the selection probability for choosing a dwelling with
     * the given utility
     * @return the selection probability
     */
    double calculateSelectDwellingProbability(double util);

    double calculateSelectRegionProbability(double util);

    /**
     * Entry point for preparations before the next simulation year
     * (e.g. for cached utility calculations).
     */
    void prepareYear();

    /**
     * Calculates the utility/satisfaction the given household derives from
     * from searching for a dwelling in this region.
     * @return the derived utility
     */
    double calculateRegionalUtility(Household household, Region region);

    /**
     * This method duplicates the strategy object for usage in concurrent environments.
     * Implementations should ensure thread safety.
     * @return an independent/ thread safe copy of the strategy
     */
    HousingStrategy duplicate();
}
