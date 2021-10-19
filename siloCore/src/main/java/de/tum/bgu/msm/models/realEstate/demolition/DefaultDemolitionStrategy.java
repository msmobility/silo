package de.tum.bgu.msm.models.realEstate.demolition;

import de.tum.bgu.msm.data.dwelling.Dwelling;

public final class DefaultDemolitionStrategy implements DemolitionStrategy {

    public DefaultDemolitionStrategy() {
    }

    public double calculateDemolitionProbability(Dwelling dwelling, int year) {
        var vacantModifier = 0.9;
        if(!dwelling.getUsage().name().equals("VACANT")) {
            vacantModifier = 0.1;
        }
        if(year < 1949) {
            return vacantModifier * 0.0078;
        } else if(year < 1958) {
            return vacantModifier * 0.0148;
        } else if(year < 1968) {
            return vacantModifier * 0.0123;
        } else {
            var age = year - dwelling.getYearBuilt();
            if(age > 31) {
                return vacantModifier * 0.0098;
            } else {
                return vacantModifier * 0.0018;
            }
        }
    }
}
