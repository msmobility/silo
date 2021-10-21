package de.tum.bgu.msm.scenarios.draconicResettlement;

import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypes.DefaultDwellingTypeImpl;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.geo.ZoneImpl;
import de.tum.bgu.msm.models.realEstate.demolition.DemolitionStrategy;

public class DraconicResettlementDemolitionStrategy implements DemolitionStrategy {

    private final GeoData geoData;

    public DraconicResettlementDemolitionStrategy(GeoData geoData) {
        this.geoData = geoData;
    }

    @Override
    public double calculateDemolitionProbability(Dwelling dd, int currentYear) {
        ZoneImpl zone = (ZoneImpl) geoData.getZones().get(dd.getZoneId());
        boolean isRural = true;

        //in this scenario, the rural areas are identified by the fact that development is restricted for all dwelling types
        for (DefaultDwellingTypeImpl ddType : DefaultDwellingTypeImpl.values()){
            if (zone.getDevelopment().isThisDwellingTypeAllowed(ddType)){
                isRural = false;
            }
        }

        if (isRural){
            return 0.1;
        } else {
            return calculateUrbanDomilitionProb(dd, currentYear);
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
