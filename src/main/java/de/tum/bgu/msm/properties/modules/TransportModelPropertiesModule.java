package de.tum.bgu.msm.properties.modules;

import com.pb.common.util.ResourceUtil;

import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

public class TransportModelPropertiesModule {

    public final Set<Integer> modelYears;
    public final Set<Integer> skimYears;

    public final boolean runTravelDemandModel;
    public final String demandModelPropertiesPath;

    public final boolean runMatsim;
    public final String matsimZoneShapeFile;
    public final String matsimZoneCRS;

    public TransportModelPropertiesModule(ResourceBundle bundle) {
        modelYears = Arrays.stream(ResourceUtil.getIntegerArray(bundle, "transport.model.years"))
                .boxed().collect(Collectors.toSet());
        skimYears = Arrays.stream(ResourceUtil.getIntegerArray(bundle, "skim.years"))
                .boxed().collect(Collectors.toSet());
        runTravelDemandModel = ResourceUtil.getBooleanProperty(bundle, "mito.run.travel.model", false);
        demandModelPropertiesPath = ResourceUtil.getProperty(bundle, "mito.properties.file");
        runMatsim = ResourceUtil.getBooleanProperty(bundle, "matsim.run.travel.model", false);
        matsimZoneShapeFile = ResourceUtil.getProperty(bundle, "matsim.zones.shapefile");
        matsimZoneCRS = ResourceUtil.getProperty(bundle, "matsim.zones.crs");
    }
}
