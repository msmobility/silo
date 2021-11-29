package de.tum.bgu.msm.matsim;

import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptorRoutingModule;
import de.tum.bgu.msm.data.Location;
import de.tum.bgu.msm.data.MicroLocation;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix2D;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.api.core.v01.population.Route;
import org.matsim.core.config.Config;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.population.routes.NetworkRoute;
import org.matsim.core.router.*;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.utils.misc.Time;
import org.matsim.facilities.ActivityFacilitiesFactory;
import org.matsim.facilities.ActivityFacilitiesFactoryImpl;
import org.matsim.facilities.ActivityFacility;
import org.matsim.facilities.Facility;

import java.util.*;

/**
 * @author dziemke, nkuehnel
 */
public final class MatsimTravelTimesAndCosts implements TravelTimes {

    private final static Logger logger = Logger.getLogger(MatsimTravelTimesAndCosts.class);

    private MatsimData matsimData;

    private final Map<String, IndexedDoubleMatrix2D> skimsByMode = new HashMap<>();
    private Map<Integer, Zone> zones;

    private TripRouter tripRouter;

    private final Map<String, IndexedDoubleMatrix2D> travelTimesFromRegion = new LinkedHashMap<>();
    private final Map<String, IndexedDoubleMatrix2D> travelTimesToRegion = new LinkedHashMap<>();
    private Collection<Region> regions;

    private final Config config;

    public MatsimTravelTimesAndCosts(Config config) {
        this.config = config;
    }

    public void initialize(GeoData geoData, MatsimData matsimData) {
        this.zones = geoData.getZones();
        this.matsimData = matsimData;
        regions = geoData.getRegions().values();
    }

    public void update(MatsimData matsimData) {
        this.matsimData = matsimData;
        this.tripRouter = matsimData.createTripRouter();
        this.skimsByMode.clear();
        this.travelTimesFromRegion.clear();
        this.travelTimesToRegion.clear();
        updateSkims();
        updateRegionalTravelTimes();
    }

    private void updateSkims() {
        logger.info("Updating car and pt skim.");
        getPeakSkim(TransportMode.car);
        getPeakSkim(TransportMode.pt);
    }

    private void updateRegionalTravelTimes() {
        logger.info("Updating minimal zone to region travel times...");
        IndexedDoubleMatrix2D travelTimesFromRegionCar = new IndexedDoubleMatrix2D(regions, zones.values());
        IndexedDoubleMatrix2D travelTimesToRegionCar = new IndexedDoubleMatrix2D(zones.values(), regions);
        IndexedDoubleMatrix2D travelTimesFromRegionPt = new IndexedDoubleMatrix2D(regions, zones.values());
        IndexedDoubleMatrix2D travelTimesToRegionPt = new IndexedDoubleMatrix2D(zones.values(), regions);

        regions.parallelStream().forEach( r -> {
            for(Zone zone: zones.values()) {
                int zoneId = zone.getZoneId();
                double minFromCar = Double.MAX_VALUE;
                double minToCar = Double.MAX_VALUE;
                double minFromPt = Double.MAX_VALUE;
                double minToPt = Double.MAX_VALUE;

                for (Zone zoneInRegion : r.getZones()) {
                    double travelTimeFromRegionCar = getPeakSkim(TransportMode.car).getIndexed(zoneInRegion.getZoneId(), zoneId);
                    if (travelTimeFromRegionCar < minFromCar) {
                        minFromCar = travelTimeFromRegionCar;
                    }
                    double travelTimeToRegionCar = getPeakSkim(TransportMode.car).getIndexed(zoneId, zoneInRegion.getZoneId());
                    if (travelTimeToRegionCar < minToCar) {
                        minToCar = travelTimeToRegionCar;
                    }
                    double travelTimeFromRegionPt = getPeakSkim(TransportMode.pt).getIndexed(zoneInRegion.getZoneId(), zoneId);
                    if (travelTimeFromRegionPt < minFromPt) {
                        minFromPt = travelTimeFromRegionPt;
                    }
                    double travelTimeToRegionPt = getPeakSkim(TransportMode.pt).getIndexed(zoneId, zoneInRegion.getZoneId());
                    if (travelTimeToRegionPt < minToPt) {
                        minToPt = travelTimeToRegionPt;
                    }
                }
                travelTimesFromRegionCar.setIndexed(r.getId(), zoneId, minFromCar);
                travelTimesToRegionCar.setIndexed(zoneId, r.getId(), minToCar);
                travelTimesFromRegionPt.setIndexed(r.getId(), zoneId, minFromPt);
                travelTimesToRegionPt.setIndexed(zoneId, r.getId(), minToPt);
            }
        });
        travelTimesFromRegion.put(TransportMode.car, travelTimesFromRegionCar);
        travelTimesFromRegion.put(TransportMode.pt, travelTimesFromRegionPt);
        travelTimesToRegion.put(TransportMode.car, travelTimesToRegionCar);
        travelTimesToRegion.put(TransportMode.pt, travelTimesToRegionPt);
    }

    @Override
    public double getTravelTime(Location origin, Location destination, double timeOfDay_s, String mode) {
        List<? extends PlanElement> planElements = getRoute(origin, destination, timeOfDay_s, mode, null);
        double arrivalTime = timeOfDay_s;

        if (!planElements.isEmpty()) {
            final Leg lastLeg = (Leg) planElements.get(planElements.size() - 1);
            arrivalTime = lastLeg.getDepartureTime().seconds() + lastLeg.getTravelTime().seconds();
        }

        double time = arrivalTime - timeOfDay_s;

        //convert to minutes
        time /= 60.;
        return time;
    }

    public double getTravelTime(Location origin, Location destination, double timeOfDay_s, String mode, Person siloPerson) {

        List<? extends PlanElement> planElements = getRoute(origin, destination, timeOfDay_s, mode, siloPerson);
        double arrivalTime = timeOfDay_s;

        if (!planElements.isEmpty()) {
            final Leg lastLeg = (Leg) planElements.get(planElements.size() - 1);
            arrivalTime = lastLeg.getDepartureTime().seconds() + lastLeg.getTravelTime().seconds();
        }

        double time = arrivalTime - timeOfDay_s;

        //convert to minutes
        time /= 60.;
        return time;
    }

    public double getGeneralizedTravelCosts(Location origin, Location destination, double timeOfDay_s, String mode) {
        List<? extends PlanElement> planElements = getRoute(origin, destination, timeOfDay_s, mode, null);
        RoutingModule routingModule = tripRouter.getRoutingModule(mode);
        PlanCalcScoreConfigGroup cnScoringGroup = config.planCalcScore();

        double utility = 0.;
        if (routingModule instanceof NetworkRoutingModule || routingModule instanceof NetworkRoutingInclAccessEgressModule) {
            for (PlanElement pe : planElements) {
                if (pe instanceof Leg) {
                    Route route = ((Leg) pe).getRoute();
                    utility -= ((NetworkRoute) route).getTravelCost();
                    utility += cnScoringGroup.getModes().get(mode).getConstant();
                }
            }
        } else if (routingModule instanceof SwissRailRaptorRoutingModule || routingModule instanceof FreespeedFactorRoutingModule) {
            for (PlanElement pe : planElements) {
                if (pe instanceof Leg) {
                    double time = ((Leg) pe).getTravelTime().seconds();

                    // overrides individual parameters per person; use default scoring parameters
                    //if (Time.getUndefinedTime() != time) {
                        utility += time * (cnScoringGroup.getModes().get(mode).getMarginalUtilityOfTraveling() - cnScoringGroup.getPerforming_utils_hr()) / 3600;
                    //}
                    Double dist = ((Leg) pe).getRoute().getDistance();
                    if (dist != null && dist != 0.) {
                        utility += dist * cnScoringGroup.getModes().get(mode).getMarginalUtilityOfDistance();
                        utility += dist * cnScoringGroup.getModes().get(mode).getMonetaryDistanceRate() * cnScoringGroup.getMarginalUtilityOfMoney();
                    }
                    utility += cnScoringGroup.getModes().get(mode).getConstant();
                }
            }
        } else {
            throw new RuntimeException("Computation of generalized costs not implemented for routing module " + routingModule.toString());
        }
        return -utility;
    }

    private List<? extends PlanElement> getRoute(Location origin, Location destination, double timeOfDay_s, String mode, Person siloPerson) {
        Coord originCoord;
        Coord destinationCoord;
        if (origin instanceof MicroLocation && destination instanceof MicroLocation) {
            // Microlocations case
            originCoord = CoordUtils.createCoord(((MicroLocation) origin).getCoordinate());
            destinationCoord = CoordUtils.createCoord(((MicroLocation) destination).getCoordinate());
        } else if (origin instanceof Zone && destination instanceof Zone) {
            // Non-microlocations case
            originCoord = matsimData.getZoneConnectorManager().getCoordsForZone(origin.getZoneId()).get(0);
            destinationCoord = matsimData.getZoneConnectorManager().getCoordsForZone(destination.getZoneId()).get(0);
        } else {
            throw new IllegalArgumentException("Origin and destination have to be consistent in location type!");
        }

        Id<Link> fromLink = null;
        Id<Link> toLink = null;
        if(tripRouter.getRoutingModule(mode) instanceof FreespeedFactorRoutingModule) {
            final Network carNetwork = matsimData.getCarNetwork();
            fromLink = NetworkUtils.getNearestLink(carNetwork, originCoord).getId();
            toLink = NetworkUtils.getNearestLink(carNetwork, destinationCoord).getId();
        }

        ActivityFacilitiesFactoryImpl activityFacilitiesFactory = new ActivityFacilitiesFactoryImpl();
        Facility fromFacility = ((ActivityFacilitiesFactory) activityFacilitiesFactory).createActivityFacility(Id.create(1, ActivityFacility.class), originCoord, fromLink);
        Facility toFacility = ((ActivityFacilitiesFactory) activityFacilitiesFactory).createActivityFacility(Id.create(2, ActivityFacility.class), destinationCoord, toLink);

        org.matsim.api.core.v01.population.Person matsimPerson = null;
        if (siloPerson != null) {
            matsimPerson = matsimData.getMatsimPopulation().getPersons().get(Id.createPersonId(siloPerson.getId()));
        }
        return tripRouter.calcRoute(mode, fromFacility, toFacility, timeOfDay_s, matsimPerson, null);
    }

    @Override
    public double getTravelTimeFromRegion(Region origin, Zone destination, double timeOfDay_s, String mode) {
        return travelTimesFromRegion.get(mode).getIndexed(origin.getId(), destination.getZoneId());
    }

    @Override
    public double getTravelTimeToRegion(Zone origin, Region destination, double timeOfDay_s, String mode) {
        return travelTimesToRegion.get(mode).getIndexed(origin.getZoneId(), destination.getId());
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
                    skim = matsimSkimCreator.createCarSkim(zones.values(), Properties.get().main.numberOfThreads,
                            Properties.get().transportModel.peakHour_s);
                    break;
                case TransportMode.pt:
                    if (config.transit().isUseTransit()) {
                        skim = matsimSkimCreator.createPtSkim(zones.values(), Properties.get().main.numberOfThreads,
                                Properties.get().transportModel.peakHour_s);
                        break;
                    } else {
                        logger.warn("No schedule/ network provided for pt. Will use freespeed factor.");
                        skim = matsimSkimCreator.createFreeSpeedFactorSkim(zones.values(),
                                config.plansCalcRoute().getModeRoutingParams().get(TransportMode.pt).getTeleportedModeFreespeedFactor(),
                                Properties.get().main.numberOfThreads, Properties.get().transportModel.peakHour_s);
                        break;
                    }
                default:
                    logger.warn("Defaulting to teleportation.");
                    skim = matsimSkimCreator.createTeleportedSkim(zones.values(), mode, Properties.get().main.numberOfThreads,
                            Properties.get().transportModel.peakHour_s);
            }
            skimsByMode.put(mode, skim);
            logger.info("Obtained skim for mode " + mode);
            return skim;
        }
    }

    @Override
    public TravelTimes duplicate() {
        logger.warn("Creating another TravelTimes object.");
        MatsimTravelTimesAndCosts matsimTravelTimesAndCosts = new MatsimTravelTimesAndCosts(config);
        matsimTravelTimesAndCosts.zones = this.zones;
        matsimTravelTimesAndCosts.regions = this.regions;
        matsimTravelTimesAndCosts.matsimData = matsimData;
        matsimTravelTimesAndCosts.tripRouter = matsimData.createTripRouter();
        matsimTravelTimesAndCosts.skimsByMode.putAll(this.skimsByMode);
        matsimTravelTimesAndCosts.travelTimesFromRegion.putAll(travelTimesFromRegion);
        matsimTravelTimesAndCosts.travelTimesToRegion.putAll(travelTimesToRegion);
        return matsimTravelTimesAndCosts;
    }
}