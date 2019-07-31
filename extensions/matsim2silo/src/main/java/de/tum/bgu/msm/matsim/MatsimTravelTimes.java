package de.tum.bgu.msm.matsim;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Location;
import de.tum.bgu.msm.data.MicroLocation;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix2D;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.config.Config;
import org.matsim.core.router.*;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.facilities.ActivityFacilitiesFactory;
import org.matsim.facilities.ActivityFacilitiesFactoryImpl;
import org.matsim.facilities.ActivityFacility;
import org.matsim.facilities.Facility;

import java.util.*;

/**
 * @author dziemke, nkuehnel
 */
public final class MatsimTravelTimes implements TravelTimes {
    private final static Logger logger = Logger.getLogger(MatsimTravelTimes.class);

    private MatsimData matsimData;

    private final Map<String, IndexedDoubleMatrix2D> skimsByMode = new HashMap<>();
    private Map<Integer, Zone> zones;

    private TripRouter tripRouter;

    private IndexedDoubleMatrix2D travelTimeFromRegion;
    private IndexedDoubleMatrix2D travelTimeToRegion;

    private final Config config;

    public MatsimTravelTimes(Config config) {
        this.config = config;
    }

    public void initialize(DataContainer dataContainer, MatsimData matsimData) {
        final GeoData geoData = dataContainer.getGeoData();
        this.zones = geoData.getZones();
        this.matsimData = matsimData;
        this.travelTimeFromRegion = new IndexedDoubleMatrix2D(geoData.getRegions().values(), geoData.getZones().values());
        this.travelTimeFromRegion.assign(-1);
        this.travelTimeToRegion = new IndexedDoubleMatrix2D(geoData.getZones().values(), geoData.getRegions().values());
        this.travelTimeToRegion.assign(-1);
    }

    public void update(MatsimData matsimData) {
        this.matsimData = matsimData;
        this.tripRouter = matsimData.createTripRouter();
        this.skimsByMode.clear();
        if (this.travelTimeFromRegion != null) {
            this.travelTimeFromRegion.assign(-1);
        }
        if (this.travelTimeToRegion != null) {
            this.travelTimeToRegion.assign(-1);
        }
    }

    // TODO Use travel costs?
    @Override
    public double getTravelTime(Location origin, Location destination, double timeOfDay_s, String mode) {
        Coord originCoord;
        Coord destinationCoord;
        if (origin instanceof MicroLocation && destination instanceof MicroLocation) {
            // Microlocations case
            originCoord = CoordUtils.createCoord(((MicroLocation) origin).getCoordinate());
            destinationCoord = CoordUtils.createCoord(((MicroLocation) destination).getCoordinate());
        } else if (origin instanceof Zone && destination instanceof Zone) {
            // Non-microlocations case
            originCoord = matsimData.getZoneConnectorManager().getCoordsForZone((Zone) origin).get(0);
            destinationCoord = matsimData.getZoneConnectorManager().getCoordsForZone((Zone) destination).get(0);
        } else {
            throw new IllegalArgumentException("Origin and destination have to be consistent in location type!");
        }

        ActivityFacilitiesFactoryImpl activityFacilitiesFactory = new ActivityFacilitiesFactoryImpl();
        Facility fromFacility = ((ActivityFacilitiesFactory) activityFacilitiesFactory).createActivityFacility(Id.create(1, ActivityFacility.class), originCoord);
        Facility toFacility = ((ActivityFacilitiesFactory) activityFacilitiesFactory).createActivityFacility(Id.create(2, ActivityFacility.class), destinationCoord);
        List<? extends PlanElement> planElements = tripRouter.calcRoute(mode, fromFacility, toFacility, timeOfDay_s, null);
        double arrivalTime = timeOfDay_s;

        if (!planElements.isEmpty()) {
            final Leg lastLeg = (Leg) planElements.get(planElements.size() - 1);
            arrivalTime = lastLeg.getDepartureTime() + lastLeg.getTravelTime();
        }

        double time = arrivalTime - timeOfDay_s;

        //convert to minutes
        time /= 60.;
        return time;
    }

    @Override
    public double getTravelTimeFromRegion(Region origin, Zone destination, double timeOfDay_s, String mode) {

        int destinationZone = destination.getZoneId();
        if (travelTimeFromRegion.getIndexed(origin.getId(), destinationZone) > 0) {
            return travelTimeFromRegion.getIndexed(origin.getId(), destinationZone);
        }
        double min = Double.MAX_VALUE;
        for (Zone zoneInRegion : origin.getZones()) {
            double travelTime = getPeakSkim(mode).getIndexed(zoneInRegion.getZoneId(), destinationZone);
            if (travelTime < min) {
                min = travelTime;
            }
        }
        travelTimeFromRegion.setIndexed(origin.getId(), destinationZone, min);
        return min;

    }

    @Override
    public double getTravelTimeToRegion(Zone origin, Region destination, double timeOfDay_s, String mode) {

        if (travelTimeToRegion.getIndexed(origin.getId(), destination.getId()) > 0) {
            return travelTimeFromRegion.getIndexed(origin.getId(), destination.getId());
        }
        double min = Double.MAX_VALUE;
        for (Zone zoneInRegion : destination.getZones()) {
            double travelTime = getPeakSkim(mode).getIndexed(origin.getZoneId(), zoneInRegion.getZoneId());
            if (travelTime < min) {
                min = travelTime;
            }
        }
        travelTimeFromRegion.setIndexed(origin.getId(), destination.getId(), min);
        return min;
    }

    @Override
    public IndexedDoubleMatrix2D getPeakSkim(String mode) {
        if (skimsByMode.containsKey(mode)) {
            return skimsByMode.get(mode);
        } else {
            logger.info("Calculating skim matrix for mode " + mode +
                    " using " + Properties.get().main.numberOfThreads + " threads.");
            IndexedDoubleMatrix2D skim;
            final MatsimSkimCreator matsimSkimCreator = new MatsimSkimCreator(matsimData);
            switch (mode) {
                case TransportMode.car:
                    skim = matsimSkimCreator.createCarSkim(zones.values());
                    break;
                case TransportMode.pt:
                    if (config.transit().isUseTransit()) {
                        skim = matsimSkimCreator.createPtSkim(zones.values());
                        break;
                    } else {
                        logger.warn("No schedule/ network provided for pt.");
                    }
                default:
                    logger.warn("Defaulting to teleportation.");
                    skim = matsimSkimCreator.createTeleportedSkim(this, mode, zones.values());
            }
            skimsByMode.put(mode, skim);
            logger.info("Obtaiend skim for mode " + mode);
            return skim;
        }
    }

    @Override
    public TravelTimes duplicate() {
        logger.warn("Creating another TravelTimes object.");
        MatsimTravelTimes matsimTravelTimes = new MatsimTravelTimes(config);
        matsimTravelTimes.zones = this.zones;
        matsimTravelTimes.update(matsimData);
        matsimTravelTimes.travelTimeFromRegion = this.travelTimeFromRegion.copy();
        matsimTravelTimes.travelTimeToRegion = this.travelTimeToRegion.copy();
        matsimTravelTimes.skimsByMode.putAll(this.skimsByMode);
        return matsimTravelTimes;
    }
}