package de.tum.bgu.msm.properties.modules;

import de.tum.bgu.msm.properties.PropertiesUtil;

import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

public class TransportModelPropertiesModule {

    /**
     * Peak hour in seconds.
     */
    public final double peakHour_s;

    /**
     * Years in which the transport model is ran.
     */
    public final Set<Integer> transportModelYears;

    /**
     * Identifier for the transport model {@link TransportModelIdentifier}: MITO_MATSIM, MATSIM, NONE.
     */
    public final TransportModelIdentifier transportModelIdentifier;

    /**
     * Identifier for the transport model {@link TransportModelIdentifier}: MITO_MATSIM, MATSIM, NONE.
     */
    public final TravelTimeImplIdentifier travelTimeImplIdentifier;

    /**
     * Path to mito properties file.
     */
    public final String mitoPropertiesPath;

    /**
     * Events file at the base year for warm start of travel times based on MATSim.
     */
    public final String matsimInitialEventsFile;

    /**
     * PLans file at the base year for warm start of travel times based on MATSim.
     */
    public final String matsimInitialPlansFile;

    /**
     * Scale factor for MATSim transport model.
     */
    public final double matsimScaleFactor;

    public enum TransportModelIdentifier {
        MITO_MATSIM, MATSIM, NONE;
    }

    /**
     * Identifier for which data structure to use for travel times.
     */
    public enum TravelTimeImplIdentifier {
        MATSIM, SKIM;
    }

    public TransportModelPropertiesModule(ResourceBundle bundle) {
        PropertiesUtil.newPropertySubmodule("Transport model properties");
        transportModelYears = Arrays.stream(PropertiesUtil.getIntPropertyArray(bundle, "transport.model.years", new int[]{2024,2037,2050}))
                .boxed().collect(Collectors.toSet());
        peakHour_s = PropertiesUtil.getDoubleProperty(bundle, "peak.hour", 8*60*60);

        PropertiesUtil.newPropertySubmodule("Transport model identifier (MITO_MATSIM, MATSIM, NONE, or empty)");
        transportModelIdentifier = TransportModelIdentifier.valueOf(PropertiesUtil.getStringProperty(bundle, "transport.model", "NONE").toUpperCase());

        PropertiesUtil.newPropertySubmodule("Travel time data structure identifier (MATSIM or SKIM)");
        travelTimeImplIdentifier = TravelTimeImplIdentifier.valueOf(PropertiesUtil.getStringProperty(bundle, "travel.time", "SKIM").toUpperCase());

        if(transportModelIdentifier == TransportModelIdentifier.NONE && travelTimeImplIdentifier == TravelTimeImplIdentifier.MATSIM) {
            throw new RuntimeException("Trying to use matsim travel times without a mito/matsim transport model is inconsistent!");
        }

        PropertiesUtil.newPropertySubmodule("Transport - silo-mito-matsim");
        mitoPropertiesPath = PropertiesUtil.getStringProperty(bundle, "mito.properties.file","mito.properties");

        PropertiesUtil.newPropertySubmodule("Transport - silo-matsim");
        matsimInitialEventsFile = PropertiesUtil.getStringProperty(bundle, "matsim.initial.events", null);
        matsimInitialPlansFile = PropertiesUtil.getStringProperty(bundle, "matsim.initial.plans", null);

        matsimScaleFactor = PropertiesUtil.getDoubleProperty(bundle, "matsim.scale.factor", 0.01);
    }

}
