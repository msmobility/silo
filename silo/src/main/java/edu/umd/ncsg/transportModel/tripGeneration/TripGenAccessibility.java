package edu.umd.ncsg.transportModel.tripGeneration;

import com.pb.common.util.ResourceUtil;
import edu.umd.ncsg.SiloUtil;
import edu.umd.ncsg.data.Accessibility;
import edu.umd.ncsg.data.geoData;
import org.apache.log4j.Logger;

import java.util.ResourceBundle;

/**
 * Calculates and stores accessibilities
 * Author: Rolf Moeckel, University of Maryland
 * Created on 15 December 2014 in College Park, MD
 **/

public class TripGenAccessibility {


    static Logger logger = Logger.getLogger(TripGenAccessibility.class);
    private ResourceBundle rb;
    private float[] autoAccessibilityHouseholds;
    private float[] autoAccessibilityRetail;
    private float[] autoAccessibilityOther;
    private float[] transitAccessibilityOther;
    private int[] householdsByZone;
    private int[] retailEmplByZone;
    private int[] otherEmplByZone;


    public TripGenAccessibility(ResourceBundle rb, int year, int[] householdsByZone, int[] retailEmplByZone, int[] otherEmplByZone) {
        this.rb = rb;
        this.householdsByZone = householdsByZone;
        this.retailEmplByZone = retailEmplByZone;
        this.otherEmplByZone = otherEmplByZone ;
        calculateAccessibilities(year);
    }


    public void calculateAccessibilities (int year) {
        // Calculate Hansen TripGenAccessibility (recalculated every year)

        logger.info("  Calculating accessibilities for " + year);
        float alpha = (float) ResourceUtil.getDoubleProperty(rb, "accessibility.alpha");
        float beta = (float) ResourceUtil.getDoubleProperty(rb, "accessibility.beta");

        int[] zones = geoData.getZones();
        autoAccessibilityHouseholds = new float[zones.length];
        autoAccessibilityRetail = new float[zones.length];
        autoAccessibilityOther = new float[zones.length];
        transitAccessibilityOther = new float[zones.length];
        for (int i = 0; i < zones.length; i++) {
            autoAccessibilityHouseholds[i] = 0;
            autoAccessibilityRetail[i] = 0;
            autoAccessibilityOther[i] = 0;
            transitAccessibilityOther[i] = 0;
            for (int zone : zones) {
                double autoImpedance;
                if (Accessibility.getAutoTravelTime(zones[i], zone) == 0) {      // should never happen for auto
                    autoImpedance = 0;
                } else {
                    autoImpedance = Math.exp(beta * Accessibility.getAutoTravelTime(zones[i], zone));
                }
                double transitImpedance;
                if (Accessibility.getTransitTravelTime(zones[i], zone) == 0) {   // zone is not connected by walk-to-transit
                    transitImpedance = 0;
                } else {
                    transitImpedance = Math.exp(beta * Accessibility.getTransitTravelTime(zones[i], zone));
                }

                autoAccessibilityHouseholds[i] += Math.pow(householdsByZone[geoData.getZoneIndex(zone)], alpha) * autoImpedance;
                autoAccessibilityRetail[i] += Math.pow(retailEmplByZone[geoData.getZoneIndex(zone)], alpha) * autoImpedance;
                autoAccessibilityOther[i] += Math.pow(otherEmplByZone[geoData.getZoneIndex(zone)], alpha) * autoImpedance;
                transitAccessibilityOther[i] += Math.pow(otherEmplByZone[geoData.getZoneIndex(zone)], alpha) * transitImpedance;
            }
        }
        autoAccessibilityHouseholds = SiloUtil.scaleArray(autoAccessibilityHouseholds, 100);
        autoAccessibilityRetail = SiloUtil.scaleArray(autoAccessibilityRetail, 100);
        autoAccessibilityOther = SiloUtil.scaleArray(autoAccessibilityOther, 100);
        transitAccessibilityOther = SiloUtil.scaleArray(transitAccessibilityOther, 100);
    }



    public float getAutoAccessibilityHouseholds(int zone) {
        return autoAccessibilityHouseholds[geoData.getZoneIndex(zone)];
    }

    public float getAutoAccessibilityRetail(int zone) {
        return autoAccessibilityRetail[geoData.getZoneIndex(zone)];
    }

    public float getAutoAccessibilityOther(int zone) {
        return autoAccessibilityOther[geoData.getZoneIndex(zone)];
    }

    public float getTransitAccessibilityOther(int zone) {
        return transitAccessibilityOther[geoData.getZoneIndex(zone)];
    }

}
