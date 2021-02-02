package de.tum.bgu.msm.scenarios.noise;

import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.matsim.ZoneConnectorManager;
import de.tum.bgu.msm.properties.Properties;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.accessibility.AccessibilityConfigGroup;
import org.matsim.contrib.accessibility.AccessibilityModule;
import org.matsim.contrib.accessibility.FacilityDataExchangeInterface;
import org.matsim.contrib.accessibility.Modes4Accessibility;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.facilities.ActivityFacilities;
import org.matsim.facilities.ActivityFacilitiesFactoryImpl;
import org.matsim.facilities.ActivityFacility;
import org.matsim.facilities.FacilitiesUtils;

public class OsmAccessibilityCalculator implements FacilityDataExchangeInterface {

    private final Config config;
    private final Properties properties;
    private final GeoData geoData;

    private final Envelope envelope = new Envelope();
    private final ActivityFacilities facilities;

    public OsmAccessibilityCalculator(Config config, Properties properties,
                                      GeoData geoData, ZoneConnectorManager zoneConnectorManager) {
        this.config = config;
        this.properties = properties;
        this.geoData = geoData;

        facilities = FacilitiesUtils.createActivityFacilities();

        final ActivityFacilitiesFactoryImpl activityFacilitiesFactory = new ActivityFacilitiesFactoryImpl();
        for (Zone value : geoData.getZones().values()) {
            envelope.expandToInclude(((Geometry) value.getZoneFeature().getDefaultGeometry()).getEnvelopeInternal());
            final Coord coord = zoneConnectorManager.getCoordsForZone(value.getZoneId()).get(0);
            final ActivityFacility activityFacility = activityFacilitiesFactory
                    .createActivityFacility(Id.create(value.getId(), ActivityFacility.class), coord);
            facilities.addActivityFacility(activityFacility);
        }
    }


    public void calculateAccessibilities(int year) {

        String opportunitiesFile = "C:/Users/Nico/tum/facilities_amenities.xml";

        Config config = ConfigUtils.createConfig(this.config.getContext());

        AccessibilityConfigGroup acg = ConfigUtils.addOrGetModule(config, AccessibilityConfigGroup.class);
        acg.setComputingAccessibilityForMode(Modes4Accessibility.car, true);
        acg.setUseParallelization(true);
        acg.setTileSize_m(100); // Must be set even though meaningless in given-facilities case

        acg.setEnvelope(envelope);
        config.controler().setLastIteration(0);

        final String outputDirectoryRoot = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName;
        String outputDirectory = outputDirectoryRoot + "/matsim/" + year + "/accessibility/";
        config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
        config.controler().setRunId(String.valueOf(year));
        config.controler().setOutputDirectory(outputDirectory);
        config.network().setInputFile(this.config.network().getInputFile());
        config.plansCalcRoute().setRoutingRandomness(0.);

        config.facilities().setInputFile(opportunitiesFile);
        acg.setMeasuringPointsFacilities(facilities);
        acg.setAreaOfAccessibilityComputation(AccessibilityConfigGroup.AreaOfAccesssibilityComputation.fromFacilitiesObject);

        final Scenario sc = ScenarioUtils.loadScenario(config);
        Controler controler = new Controler(sc);
        final AccessibilityModule module = new AccessibilityModule();
        module.setConsideredActivityType(null);
        module.addFacilityDataExchangeListener(this);
        controler.addOverridingModule(module);
        controler.run();
    }

    @Override
    public void setFacilityAccessibilities(ActivityFacility activityFacility, Double aDouble, String s, double v) {
        final int id = Integer.parseInt(activityFacility.getId().toString());
        this.geoData.getZones().get(id).getAttributes().put("matsim_accessibility", v);
    }

    @Override
    public void finish() {

    }
}
