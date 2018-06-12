package de.tum.bgu.msm.properties.modules;

import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.properties.Properties;

import java.util.ResourceBundle;

public class AccessibilityProperties {

    private static final String AUTO_PEAK_SKIM = "auto.peak.sov.skim.";
    private static final String TRANSIT_PEAK_SKIM = "transit.peak.time.";

    private final ResourceBundle bundle;

    public final float autoOperatingCosts;
    public final boolean usingAutoPeakSkim;
    public final String autoPeakSkim;
    public final boolean usingTransitPeakSkim;
    public final String transitPeakSkim;
    public final float alphaAuto;
    public final float betaAuto;
    public final float alphaTransit;
    public final float betaTransit;
    public final String htsWorkTLFD;

    /**
     * A factor every travel time in the car skim is multiplied with to allow for different time units. Use this to
     * convert input travel times to minutes as expected by SILO.
     * Default = 1.
     */
    public final double skimFileFactorCar;

    /**
     * A factor every travel time in the transit skim is multiplied with to allow for different time units. Use this to
     * convert input travel times to minutes as expected by SILO.
     * Default = 1.
     */
    public final double skimFileFactorTransit;


    public AccessibilityProperties(ResourceBundle bundle) {
        this.bundle = bundle;
        autoOperatingCosts = (float) ResourceUtil.getDoubleProperty(bundle, "auto.operating.costs");
        usingAutoPeakSkim = bundle.containsKey("auto.peak.sov.skim.matrix.name");
        skimFileFactorCar = ResourceUtil.getDoubleProperty(bundle, "skims.factor.car", 1.);
        skimFileFactorTransit = ResourceUtil.getDoubleProperty(bundle, "skims.factor.transit", 1.);

        if(usingAutoPeakSkim) {
            autoPeakSkim = bundle.getString("auto.peak.sov.skim.matrix.name");
        } else {
            autoPeakSkim = "HOVTime";
        }
        usingTransitPeakSkim = bundle.containsKey("transit.peak.time.matrix.name");
        if(usingTransitPeakSkim) {
            transitPeakSkim = bundle.getString("transit.peak.time.matrix.name");
        } else {
            transitPeakSkim = "CheapJrnyTime";
        }
        alphaAuto = (float) ResourceUtil.getDoubleProperty(bundle, "auto.accessibility.alpha");
        betaAuto = (float) ResourceUtil.getDoubleProperty(bundle, "auto.accessibility.beta");
        alphaTransit = (float) ResourceUtil.getDoubleProperty(bundle, "transit.accessibility.a");
        betaTransit = (float) ResourceUtil.getDoubleProperty(bundle, "transit.accessibility.b");
        htsWorkTLFD = bundle.getString("hts.work.tlfd");
    }

    public String autoSkimFile(int year) {
        return Properties.get().main.baseDirectory + "skims/" +  bundle.getString(AUTO_PEAK_SKIM + year);
    }

    public String transitSkimFile(int year) {
        return Properties.get().main.baseDirectory + "skims/" +  bundle.getString(TRANSIT_PEAK_SKIM + year);
    }

}
