package de.tum.bgu.msm.models.construction;

import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.models.realEstate.construction.ConstructionDemandStrategy;

public class ConstructionDemandStrategyMuc implements ConstructionDemandStrategy {

    /**
     * beta = Development that happens when vacancy rate is at structural vacancy alpha
     * beta = 0.01 means that when the vacancy rate is at the level alpha, 1% additional
     * dwellings are added (if land use permits)
     */
    private static final double BETA = 0;

    @Override
    public double calculateConstructionDemand(double vacancyRate, DwellingType dt, int numberOfExistingDwellings) {
        if (vacancyRate < 0.) {
            throw new Error("Invalid regional vacancy rate for dwelling type \"" + dt + "\" provided: " + vacancyRate);
        }

        // alpha = Structural Vacancy, to be expected to be available in a market at equilibrium to allow households to move
        // alpha = 0.03 means that we should expect 3% vacancy in a perfectly balanced housing market
        double alpha = dt.getStructuralVacancyRate();

        // gamma = Expected development when vacancy drops down to 0% (i.e., max. possible development)
        double gamma = alpha + BETA;

        // slope = parameter for decay function for vacancy rates above the structural vacancy
        // values of this function were derived heuristically to make curve look nice
        double slope = -18.33 * Math.log(alpha) - 16.79;

        if (vacancyRate > 1) {
            vacancyRate = 1;
            //this checks that very large vacancy rates (generally in tests with subsamples of sp) do not result in  infinity
        }

        double demandRate;
        if (vacancyRate <= alpha) {
            demandRate = gamma + -1 * vacancyRate;
        } else {
            //the following commented code should go at some point once we agree it is not necessary
            //demandRate = gamma / Math.exp(slope * vacancyRate);
            return 0;
        }

        double maxRate;
        if (numberOfExistingDwellings < 10000 ) {
            maxRate = (-0.47622 * Math.log(numberOfExistingDwellings) + 5.38618) / 100;
        } else {
            maxRate = 0.01;
        }
        return Math.min(demandRate, maxRate);
    }
}
