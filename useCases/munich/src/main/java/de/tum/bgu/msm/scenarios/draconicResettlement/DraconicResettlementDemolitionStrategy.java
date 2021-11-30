package de.tum.bgu.msm.scenarios.draconicResettlement;

import de.tum.bgu.msm.data.AreaTypes;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.geo.ZoneMuc;
import de.tum.bgu.msm.models.realEstate.demolition.DemolitionStrategy;

public class DraconicResettlementDemolitionStrategy implements DemolitionStrategy {

    private final GeoData geoData;

    public DraconicResettlementDemolitionStrategy(GeoData geoData) {
        this.geoData = geoData;
    }

    @Override
    public double calculateDemolitionProbability(Dwelling dd, int currentYear) {
        final AreaTypes.SGType areaTypeSG = ((ZoneMuc) geoData.getZones().get(dd.getZoneId())).getAreaTypeSG();
        switch (areaTypeSG) {
            case CORE_CITY:
            case MEDIUM_SIZED_CITY:
                return calculateUrbanDomilitionProb(dd, currentYear);
            case TOWN:
            case RURAL:
                return 0.1;
            default:
                throw new RuntimeException("Unknown area type " + areaTypeSG);
        }
    }

    private double calculateUrbanDomilitionProb(Dwelling dd, int currentYear) {
        double vacantModifier = 0.9;
        int yearBuilt = dd.getYearBuilt();
        if(dd.getResidentId() > 0) {
            vacantModifier = 0.1;
        }
        if(yearBuilt < 1949) {
            return vacantModifier * 0.0078;
        } else if(yearBuilt < 1958) {
            return vacantModifier * 0.0148;
        } else if(yearBuilt < 1968) {
            return vacantModifier * 0.0123;
        } else {
            int age = currentYear - dd.getYearBuilt();
            if(age > 31) {
                return vacantModifier * 0.0098;
            } else {
                return vacantModifier * 0.0018;
            }
        }
    }
}
