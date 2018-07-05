package de.tum.bgu.msm.properties.modules;

import de.tum.bgu.msm.properties.PropertiesUtil;

import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

public class TransportModelPropertiesModule {

    public final Set<Integer> modelYears;


    public final boolean runTravelDemandModel;
    public final String demandModelPropertiesPath;

    public final boolean runMatsim;
    public final String matsimZoneShapeFile;
    public final String matsimZoneCRS;
    public final String matsimZoneShapeIdField;


    public TransportModelPropertiesModule(ResourceBundle bundle) {
        PropertiesUtil.printOutModuleTitle("Transport model properties");
        modelYears = Arrays.stream(PropertiesUtil.getIntPropertyArray(bundle, "transport.model.years", new int[]{-1}))
                .boxed().collect(Collectors.toSet());


        PropertiesUtil.printOutModuleTitle("Transport - mito");
        runTravelDemandModel = PropertiesUtil.getBooleanProperty(bundle, "mito.run.travel.model", false);
        demandModelPropertiesPath = PropertiesUtil.getStringProperty(bundle, "mito.properties.file","javaFiles/mito.properties");

        PropertiesUtil.printOutModuleTitle("Transport - matsim");
        runMatsim = PropertiesUtil.getBooleanProperty(bundle, "matsim.run.travel.model", false);
        matsimZoneShapeFile = PropertiesUtil.getStringProperty(bundle, "matsim.zones.shapefile", "input/zonesShapefile/zones.shp");
        matsimZoneCRS = PropertiesUtil.getStringProperty(bundle, "matsim.zones.crs", "EPSG:4326");
        matsimZoneShapeIdField = PropertiesUtil.getStringProperty(bundle, "matsim.zones.sahape.id.field", "id");
    }
}
