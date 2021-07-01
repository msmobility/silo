package de.tum.bgu.msm.models.realEstate.construction;

import de.tum.bgu.msm.data.dwelling.DwellingType;

public class DefaultConstructionDemandStrategy implements ConstructionDemandStrategy {

    public DefaultConstructionDemandStrategy() {
    }

    @Override
    public double calculateConstructionDemand(double vacancyByRegion, DwellingType dwellingType, int numberOfExistingDwellings) {
        if (vacancyByRegion < 0.) {
            throw new Error("Invalid regional vacancy rate for dwelling type \"" + dwellingType + "\" provided: " + vacancyByRegion);
        }

        // alpha = Structural Vacancy, to be expected to be available in a market at equilibrium to allow households to move
        // alpha = 0.03 means that we should expect 3% vacancy in a perfectly balanced housing market
        var alpha = dwellingType.getStructuralVacancyRate();

        // beta = Development that happens when vacancy rate is at structural vacancy alpha
        // beta = 0.01 means that when the vacancy rate is at the level alpha, 1% additional dwellings are added (if land use permits)
        var beta = 0.02;

        // gamma = Expected development when vacancy drops down to 0% (i.e., max. possible development)
        var gamma = alpha + beta;

        // slope = parameter for decay function for vacancy rates above the structural vacancy
        // values of this function were derived heuristically to make curve look nice
        var slope = -18.33 * Math.log(alpha) - 16.79;

        if (vacancyByRegion > 1){
            vacancyByRegion = 1;
            //this checks that very large vacancy rates (generally in tests with subsamples of sp) do not result in  infinity
        }

        if (vacancyByRegion <= alpha) {
            return gamma + -1 * vacancyByRegion;
        } else {
            return gamma / Math.exp(slope * vacancyByRegion);
        }
    }
}
