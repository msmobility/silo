package de.tum.bgu.msm.properties.modules;

import de.tum.bgu.msm.properties.PropertiesUtil;

import java.util.ResourceBundle;

public class TrackProperties {

    public final String trackFile;
    public final int trackedHh;
    public final int trackedPp;
    public final int trackedDd;
    public final int trackedJj;

    public TrackProperties(ResourceBundle bundle) {
        PropertiesUtil.newPropertySubmodule("Tracking individuals");
        trackFile = PropertiesUtil.getStringProperty(bundle, "track.file.name", "tracking");
        trackedHh = PropertiesUtil.getIntProperty(bundle, "track.household", -1);
        trackedPp = PropertiesUtil.getIntProperty(bundle, "track.person", -1);
        trackedDd = PropertiesUtil.getIntProperty(bundle, "track.dwelling", -1);
        trackedJj = PropertiesUtil.getIntProperty(bundle, "track.job", -1);
    }
}
