package de.tum.bgu.msm.properties.modules;

import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.properties.PropertiesUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

public class AccessibilityProperties {

    private static final String AUTO_PEAK_SKIM = "auto.peak.sov.skim.";
    private static final String TRANSIT_PEAK_SKIM = "transit.peak.time.";

    private final ResourceBundle bundle;

    public final float autoOperatingCosts;
    //public final boolean usingAutoPeakSkim;
    public final String autoPeakSkim;
    //public final boolean usingTransitPeakSkim;
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
    public final Set<Integer> skimYears;

    public final float betaTimeCarExponentialCommutingTime;
    public final float betaTimePtExponentialCommutingTime;

    public AccessibilityProperties(ResourceBundle bundle, int startYear) {
        PropertiesUtil.newPropertySubmodule("Accessibility properties");
        this.bundle = bundle;
        autoOperatingCosts = (float) PropertiesUtil.getDoubleProperty(bundle, "auto.operating.costs", 8.4);
        alphaAuto = (float) PropertiesUtil.getDoubleProperty(bundle, "auto.accessibility.alpha",1.2);
        betaAuto = (float) PropertiesUtil.getDoubleProperty(bundle, "auto.accessibility.beta", -0.3);
        alphaTransit = (float) PropertiesUtil.getDoubleProperty(bundle, "transit.accessibility.a", 1.2);
        betaTransit = (float) PropertiesUtil.getDoubleProperty(bundle, "transit.accessibility.b", -0.3);

        PropertiesUtil.newPropertySubmodule("Accessibility - travel time distribution");
        htsWorkTLFD = PropertiesUtil.getStringProperty(bundle, "hts.work.tlfd", "input/hts_work_tripLengthFrequencyDistribution.csv");

        PropertiesUtil.newPropertySubmodule("Accessibility - skim matrices");
        skimYears = Arrays.stream(PropertiesUtil.getIntPropertyArray(bundle, "skim.years", new int[]{-1}))
                .boxed().collect(Collectors.toSet());

        Set<Integer> skimYearsForInput = new HashSet<>();
        skimYearsForInput.addAll(skimYears);
        skimYearsForInput.add(startYear);
        for (int year : skimYearsForInput){
            if (year != -1){
                PropertiesUtil.getStringProperty(bundle, AUTO_PEAK_SKIM + year, "skim_car_" + year);
                PropertiesUtil.getStringProperty(bundle, TRANSIT_PEAK_SKIM + year, "skim_pt_" + year);
            }
        }
        autoPeakSkim = PropertiesUtil.getStringProperty(bundle, "auto.peak.sov.skim.matrix.name", "travelTimeAuto");
        skimFileFactorCar = PropertiesUtil.getDoubleProperty(bundle, "skims.factor.car", 1.);
        transitPeakSkim = PropertiesUtil.getStringProperty(bundle,"transit.peak.time.matrix.name", "travelTimeTransit");
        skimFileFactorTransit = PropertiesUtil.getDoubleProperty(bundle, "skims.factor.transit", 1/60.);

        betaTimeCarExponentialCommutingTime = (float) PropertiesUtil.getDoubleProperty(bundle, "beta.time.utility.car", -0.01);
        betaTimePtExponentialCommutingTime = (float) PropertiesUtil.getDoubleProperty(bundle, "beta.time.utility.pt", -0.01);
    }

    public String autoSkimFile(int year) {
        return Properties.get().main.baseDirectory + "skims/" +  PropertiesUtil.getStringProperty(bundle, AUTO_PEAK_SKIM + year);
    }

    public String transitSkimFile(int year) {
        return Properties.get().main.baseDirectory + "skims/" +  PropertiesUtil.getStringProperty(bundle, TRANSIT_PEAK_SKIM + year);
    }

}
