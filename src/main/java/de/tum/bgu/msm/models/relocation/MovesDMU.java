package de.tum.bgu.msm.models.relocation;

//import java.util.HashMap;

import de.tum.bgu.msm.data.Nationality;
import de.tum.bgu.msm.data.Race;
import org.apache.log4j.Logger;

import com.pb.common.calculator.IndexValues;
import de.tum.bgu.msm.data.HouseholdType;

/**
 * @author Rolf Moeckel (PB Albuquerque)
 * Created on Apr 4, 2011 in Albuquerque, NM
 *
 */
public class MovesDMU {

    protected transient Logger logger = Logger.getLogger(MovesDMU.class);
//    protected HashMap<String, Integer> methodIndexMap;
    
    // uec variables
    private int householdType;
    private int householdRace;
    private int householdNationality;
    private int incomeGroup;
    private double ddPriceUtility;
    private double ddQualityUtility;
    private double ddAreaUtility;
    private double ddAutoAccessibilityUtility;
    private double ddTransitAccessibilityUtility;
    private double ddSchoolQualityUtility;
    private double ddCrimeRateUtility;
    private double ddWorkDistanceUtility;
    private double ddTotalTravelCostsUtility;
    private float[] medianRegionPrice;
    private float[] regionalAccessibility;
    private float[] regionalSchoolQuality;
    private float[] regionalCrimeRate;
    private float[] regionalShareWhite;
    private float[] regionalShareBlack;
    private float[] regionalShareHispanic;
    private float[] regionalShareForeigners;

    private IndexValues dmuIndex;
	
	public MovesDMU() {
		dmuIndex = new IndexValues();
	}

    public void setType (HouseholdType type) {
    	this.householdType = type.ordinal();
    }

    public void setIncomeGroup (int incomeGroup) {
        this.incomeGroup = incomeGroup;
    }

    public void setRace (Race race) {
        this.householdRace = race.ordinal();
    }

    public void setNationality (Nationality nationality) {
        this.householdNationality = nationality.ordinal();
    }

    public void setUtilityDwellingPrice(double ddPrice) {
        this.ddPriceUtility = ddPrice;
    }

    public void setUtilityDwellingQuality (double ddQualityUtility) {
        this.ddQualityUtility = ddQualityUtility;
    }

    public void setUtilityDwellingSize(double ddAreaUtility) {
        this.ddAreaUtility = ddAreaUtility;
    }

    public void setUtilityDwellingAutoAccessibility(double accessibility) {
        this.ddAutoAccessibilityUtility = accessibility;
    }

    public void setUtilityDwellingTransitAccessibility(double accessibility) {
        this.ddTransitAccessibilityUtility = accessibility;
    }

    public void setUtilityDwellingSchoolQuality(double schoolQuality) {
        this.ddSchoolQualityUtility = schoolQuality;
    }

    public void setUtilityDwellingCrimeRate(double crimeRate) {
        this.ddCrimeRateUtility = crimeRate;
    }

    public void setUtilityDwellingToJobDistance(double distToWork) {
        this.ddWorkDistanceUtility = distToWork;
    }

    public void setUtilityDwellingTravelCosts(double travelCosts) {
        this.ddTotalTravelCostsUtility = travelCosts;
    }

    public void setMedianRegionPrice(float[] price) {
        this.medianRegionPrice = price;
    }

    public void setRegionalAccessibility (float[] regionalAccessibility) {
        this.regionalAccessibility = regionalAccessibility;
    }

    public void setRegionalSchoolQuality (float[] regionalSchoolQuality) {
        this.regionalSchoolQuality = regionalSchoolQuality;
    }


    public void setRegionalCrimeRate (float[] regionalCrimeRate) {
        this.regionalCrimeRate = regionalCrimeRate;
    }

    public void setRegionalRace (Race race, float[] regionalRacialShare) {
        if (race == Race.white) {
            this.regionalShareWhite = regionalRacialShare;
        } else if (race == Race.black) {
            this.regionalShareBlack = regionalRacialShare;
        } else if (race == Race.hispanic) {
            this.regionalShareHispanic = regionalRacialShare;
        }
    }

    public void setRegionalNationality(Nationality nationality, float[] regionalNationalityShare){
        if (nationality == Nationality.other){
            this.regionalShareForeigners = regionalNationalityShare;
        }
    }

    public IndexValues getDmuIndexValues() {
        return dmuIndex; 
    }


    // DMU methods - define one of these for every @var in the control file.
	public int getHouseholdType() {
		return householdType;
	}

    public int getHouseholdRace() { return householdRace; }

    public int getHouseholdNationality(){return householdNationality;}

    public int getIncomeGroup() { return incomeGroup; }

    public double getDdPriceUtility() {
        return ddPriceUtility;
    }

    public double getDdQualityUtility() {
        return ddQualityUtility;
    }

    public double getDdAreaUtility() {
        return ddAreaUtility;
    }

    public double getDdAutoAccessibilityUtility() {
        return ddAutoAccessibilityUtility;
    }

    public double getDdTransitAccessibilityUtility() {
        return ddTransitAccessibilityUtility;
    }

    public double getDdSchoolQualityUtility() {
        return ddSchoolQualityUtility;
    }

    public double getDdCrimeRateUtility() {
        return ddCrimeRateUtility;
    }

    public double getDdWorkDistanceUtility() {
        return ddWorkDistanceUtility;
    }

    public double getDdTotalTravelCostsUtility() {
        return ddTotalTravelCostsUtility;
    }

    public float getMedianRegionPrice(int index) {
        return medianRegionPrice[index];
    }

    public float getRegionalAccessibility(int index) {
        return regionalAccessibility[index];
    }

    public float getRegionalSchoolQuality(int index) {
        return regionalSchoolQuality[index];
    }

    public float getRegionalCrimeRate(int index) {
        return regionalCrimeRate[index];
    }

    public float getRegionalShareWhite(int index) {
        return regionalShareWhite[index];
    }

    public float getRegionalShareBlack(int index) {
        return regionalShareBlack[index];
    }

    public float getRegionalShareHispanic(int index) {
        return regionalShareHispanic[index];
    }

    public float getRegionalShareForeigners(int index){return regionalShareForeigners[index];}

}
