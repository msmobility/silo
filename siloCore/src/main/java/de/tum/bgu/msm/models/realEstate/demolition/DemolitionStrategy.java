package de.tum.bgu.msm.models.realEstate.demolition;

import de.tum.bgu.msm.data.dwelling.Dwelling;

public interface DemolitionStrategy {
    double calculateDemolitionProbability(Dwelling dd, int currentYear);
}
