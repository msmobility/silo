package de.tum.bgu.msm.matsim.accessibility;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.matsim.MatsimData;
import de.tum.bgu.msm.matsim.SiloMatsimUtils;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Geometry;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.contrib.accessibility.AccessibilityConfigGroup;
import org.matsim.contrib.accessibility.Labels;
import org.matsim.contrib.accessibility.FacilityDataExchangeInterface;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.FacilitiesConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.facilities.*;
import org.opengis.feature.simple.SimpleFeature;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class AccessibilityModule {

    private static final Logger logger = Logger.getLogger(AccessibilityModule.class);
    private final MatsimData data;
    private final DataContainer dataContainer;

    private ActivityFacilities zoneRepresentativeCoords;

    public AccessibilityModule(MatsimData data, DataContainer dataContainer) {
        this.data = data;
        this.dataContainer = dataContainer;
    }

    public void setup() {

        logger.warn("Finding coordinates that represent a given zone.");
        zoneRepresentativeCoords = FacilitiesUtils.createActivityFacilities();
        ActivityFacilitiesFactory aff = new ActivityFacilitiesFactoryImpl();
        Map<Integer, Zone> zoneMap = dataContainer.getGeoData().getZones();
        Network network = data.getCarNetwork();
        for (int zoneId : zoneMap.keySet()) {
            Geometry geometry = (Geometry) zoneMap.get(zoneId).getZoneFeature().getDefaultGeometry();
            Coord centroid = CoordUtils.createCoord(geometry.getCentroid().getX(), geometry.getCentroid().getY());
            Node nearestNode = NetworkUtils.getNearestNode(network, centroid); // TODO choose road of certain category
            Coord coord = CoordUtils.createCoord(nearestNode.getCoord().getX(), nearestNode.getCoord().getY());
            ActivityFacility activityFacility = aff.createActivityFacility(Id.create(zoneId, ActivityFacility.class), coord);
            zoneRepresentativeCoords.addActivityFacility(activityFacility);
        }
    }

    public void prepareAccessibility(Scenario scenario, Controler controler) {
        // Opportunities
        Map<Integer, Integer> populationMap = HouseholdUtil.getPopulationByZoneAsMap(dataContainer);
        Map<Id<ActivityFacility>, Integer> zonePopulationMap = new TreeMap<>();
        for (int zoneId : populationMap.keySet()) {
            zonePopulationMap.put(Id.create(zoneId, ActivityFacility.class), populationMap.get(zoneId));
        }
        final ActivityFacilities opportunities = scenario.getActivityFacilities();
        int i = 0;
        for (ActivityFacility activityFacility : zoneRepresentativeCoords.getFacilities().values()) {
            activityFacility.getAttributes().putAttribute(Labels.WEIGHT, zonePopulationMap.get(activityFacility.getId()));
            opportunities.addActivityFacility(activityFacility);
            i++;
        }
        logger.warn(i + " facilities added as opportunities.");

        SiloMatsimUtils.determineExtentOfFacilities(zoneRepresentativeCoords);

        scenario.getConfig().facilities().setFacilitiesSource(FacilitiesConfigGroup.FacilitiesSource.setInScenario);
        // End opportunities

        // Accessibility settings
        AccessibilityConfigGroup acg = ConfigUtils.addOrGetModule(scenario.getConfig(), AccessibilityConfigGroup.class);
        acg.setMeasuringPointsFacilities(zoneRepresentativeCoords);
        //
        Map<Id<ActivityFacility>, Geometry> measurePointGeometryMap = new TreeMap<>();
        Map<Integer, SimpleFeature> zoneFeatureMap = new HashMap<>();
        for (Zone zone : dataContainer.getGeoData().getZones().values()) {
            zoneFeatureMap.put(zone.getId(), zone.getZoneFeature());
        }

        for (Integer zoneId : zoneFeatureMap.keySet()) {
            SimpleFeature feature = zoneFeatureMap.get(zoneId);
            Geometry geometry = (Geometry) feature.getDefaultGeometry();
            measurePointGeometryMap.put(Id.create(zoneId, ActivityFacility.class), geometry);
        }
        acg.setMeasurePointGeometryProvision(AccessibilityConfigGroup.MeasurePointGeometryProvision.fromShapeFile);
        acg.setMeasurePointGeometryMap(measurePointGeometryMap);

        acg.setTileSize_m(1000); // TODO This is only a dummy value here
        //
        acg.setAreaOfAccessibilityComputation(AccessibilityConfigGroup.AreaOfAccesssibilityComputation.fromFacilitiesObject);
        acg.setUseOpportunityWeights(true);
        acg.setWeightExponent(Properties.get().accessibility.alphaAuto); // TODO Need differentiation for different modes
        logger.warn("Properties.get().accessibility.alphaAuto = " + Properties.get().accessibility.alphaAuto);
        acg.setAccessibilityMeasureType(AccessibilityConfigGroup.AccessibilityMeasureType.rawSum);
        // End accessibility settings
        // Accessibility module

        org.matsim.contrib.accessibility.AccessibilityModule module = new org.matsim.contrib.accessibility.AccessibilityModule();
        module.addFacilityDataExchangeListener((FacilityDataExchangeInterface) dataContainer.getAccessibility());
        controler.addOverridingModule(module);
        // End accessibility module
    }
}