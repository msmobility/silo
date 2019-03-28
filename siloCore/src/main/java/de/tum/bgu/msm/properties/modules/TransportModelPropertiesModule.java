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
     * Identifier for the transport model {@link TransportModelIdentifier}: MITO, MATSIM, NONE.
     */
    public final TransportModelIdentifier transportModelIdentifier;

    /**
     * Path to mito properties file.
     */
    public final String mitoPropertiesPath;

    /**
     * Events file at the base year for warm start of travel times based on MATSim.
     */
    public final String matsimInitialEventsFile;

    /**
     * Scale factor for MATSim transport model.
     */
    public final double matsimScaleFactor;

    public enum TransportModelIdentifier {
        MITO, MATSIM, NONE;
    }

    public TransportModelPropertiesModule(ResourceBundle bundle) {
        PropertiesUtil.newPropertySubmodule("Transport model properties");
        transportModelYears = Arrays.stream(PropertiesUtil.getIntPropertyArray(bundle, "transport.model.years", new int[]{2024,2037,2050}))
                .boxed().collect(Collectors.toSet());
        peakHour_s = PropertiesUtil.getDoubleProperty(bundle, "peak.hour", 8*60*60);

        PropertiesUtil.newPropertySubmodule("Transport model identifier (MITO, MATSIM, NONE, or empty)");
        transportModelIdentifier = TransportModelIdentifier.valueOf(PropertiesUtil.getStringProperty(bundle, "transport.model", "NONE").toUpperCase());

        PropertiesUtil.newPropertySubmodule("Transport - silo-mito-matsim");
        mitoPropertiesPath = PropertiesUtil.getStringProperty(bundle, "mito.properties.file","mito.properties");

        PropertiesUtil.newPropertySubmodule("Transport - silo-matsim");
        matsimInitialEventsFile = PropertiesUtil.getStringProperty(bundle, "matsim.initial.events", null);
        matsimScaleFactor = PropertiesUtil.getDoubleProperty(bundle, "matsim.scale.factor", 0.01);
    }

}
