package de.tum.bgu.msm.models.transportModel.matsim;

import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SkimUtil;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;

import de.tum.bgu.msm.data.Location;
import de.tum.bgu.msm.data.MicroLocation;
import de.tum.bgu.msm.data.Region;

import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.router.TripRouter;
import org.matsim.pt.router.FakeFacility;
import org.matsim.utils.leastcostpathtree.LeastCostPathTree;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.vividsolutions.jts.geom.Coordinate;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class MatsimTravelTimes implements TravelTimes {
	private final static Logger logger = Logger.getLogger(MatsimTravelTimes.class);

	private SkimTravelTimes delegate = new SkimTravelTimes() ;
	private LeastCostPathTree leastCoastPathTree;
	private Network network;
	private TripRouter tripRouter;
	private final Map<Zone, List<Node>> zoneCalculationNodesMap = new HashMap<>();
	private final static int NUMBER_OF_CALC_POINTS = 1;
	private final Map<Id<Node>, Map<Double, Map<Id<Node>, LeastCostPathTree.NodeData>>> treesForNodesByTimes = new HashMap<>();

	private final Table<Zone, Region, Double> travelTimeToRegion = HashBasedTable.create();

	void update(TripRouter tripRouter, Collection<Zone> zones, Network network, LeastCostPathTree leastCoastPathTree) {
		this.tripRouter = tripRouter;
		this.network = network;
		this.leastCoastPathTree = leastCoastPathTree;

		updateZoneConnections(zones);
		this.treesForNodesByTimes.clear();
		SkimUtil.updateTransitSkim(delegate,
				Properties.get().main.startYear, Properties.get());
	}

	private void updateZoneConnections(Collection<Zone> zones) {
	    for (Zone zone : zones) {
            for (int i = 0; i < NUMBER_OF_CALC_POINTS; i++) { // Several points in a given origin zone
            	Coordinate coordinate = zone.getRandomCoordinate();
				Coord originCoord = new Coord(coordinate.x, coordinate.y);
                Node originNode = NetworkUtils.getNearestLink(network, originCoord).getToNode();

				if (!zoneCalculationNodesMap.containsKey(zone)) {
					zoneCalculationNodesMap.put(zone, new LinkedList<>());
				}
				zoneCalculationNodesMap.get(zone).add(originNode);
			}
		}
        logger.trace("There are " + zoneCalculationNodesMap.keySet().size() + " origin zones.");
    }

	private double getZoneToZoneTravelTime(Zone origin, Zone destination, double timeOfDay_s, String mode) {
		if(TransportMode.car.equals(mode)) {
			double sumTravelTime_min = 0.;
			for (Node originNode : zoneCalculationNodesMap.get(origin)) { // Several points in a given origin zone
				Map<Id<Node>, LeastCostPathTree.NodeData> tree;
				if (treesForNodesByTimes.containsKey(originNode.getId())) {
					Map<Double, Map<Id<Node>, LeastCostPathTree.NodeData>> treesForOneNodeByTimes = treesForNodesByTimes.get(originNode.getId());
					if (treesForOneNodeByTimes.containsKey(timeOfDay_s)) {
						tree = treesForOneNodeByTimes.get(timeOfDay_s);
					} else {
						leastCoastPathTree.calculate(network, originNode, timeOfDay_s);
						tree = leastCoastPathTree.getTree();
						treesForOneNodeByTimes.put(timeOfDay_s, tree);
					}
				} else {
					Map<Double, Map<Id<Node>, LeastCostPathTree.NodeData>> treesForOneNodeByTimes = new HashMap<>();
					leastCoastPathTree.calculate(network, originNode, timeOfDay_s);
					tree = leastCoastPathTree.getTree();
					treesForOneNodeByTimes.put(timeOfDay_s, tree);
					treesForNodesByTimes.put(originNode.getId(), treesForOneNodeByTimes);
				}

				for (Node destinationNode : zoneCalculationNodesMap.get(destination)) { // Several points in a given destination zone
					double arrivalTime_s = tree.get(destinationNode.getId()).getTime();
					sumTravelTime_min += ((arrivalTime_s - timeOfDay_s) / 60.);
				}
			}
			return sumTravelTime_min / NUMBER_OF_CALC_POINTS;
		} else {			
			//TODO: reconsider matsim pt travel times. nk apr'18
            return delegate.getTravelTime(origin, destination, timeOfDay_s, mode);
		}
	}

	@Override
	public double getTravelTime(Location origin, Location destination, double timeOfDay_s, String mode) {
		if (origin instanceof MicroLocation && destination instanceof MicroLocation) { // Microlocations case
			Coordinate originCoord = ((MicroLocation) origin).getCoordinate();
			Coordinate destinationCoord = ((MicroLocation) destination).getCoordinate();
			FakeFacility fromFacility = new FakeFacility(new Coord(originCoord.x, originCoord.y));
			FakeFacility toFacility = new FakeFacility(new Coord(destinationCoord.x, destinationCoord.y));
			org.matsim.api.core.v01.population.Person person = null;
			List<? extends PlanElement> trip = tripRouter.calcRoute(mode, fromFacility, toFacility, timeOfDay_s, person);
			double ttime = 0. ;
			for ( PlanElement pe : trip ) {
				if ( pe instanceof Leg) {
					ttime += ((Leg) pe).getTravelTime() ;
				}
			}
			// TODO take care of relevant interaction activities
			return ttime;
		}
		else if (origin instanceof Zone) { // Non-microlocations case
			Zone originZone = (Zone) origin;
			if (destination instanceof Zone) {
				return getZoneToZoneTravelTime(originZone, (Zone) destination, timeOfDay_s, mode);
			} else if (destination instanceof Region) {
				Region destinationRegion = (Region) destination;
				if (travelTimeToRegion.contains(originZone, destinationRegion)) {
					return travelTimeToRegion.get(originZone, destinationRegion);
				}
				double min = Double.MAX_VALUE;
        		for (Zone zoneInRegion : destinationRegion.getZones()) {
        			double travelTime = getZoneToZoneTravelTime(originZone, zoneInRegion, timeOfDay_s, mode);
        			if (travelTime < min) {
        				min = travelTime;
        			}
        		}
        		travelTimeToRegion.put(originZone, destinationRegion, min);
			}
		}
		throw new IllegalArgumentException("The combination with origin of type " + origin.getClass().getName() 
					+ " and destination of type " + destination.getClass().getName() + " is not valid.");
	}

	@Override
	public double getTravelTime(int origin, int destination, double timeOfDay_s, String mode) {
		throw new IllegalArgumentException("Not implemented in MATSim case.");
	}

	@Override
	public double getTravelTimeToRegion(Location origin, Region destination, double timeOfDay_s, String mode) {
		// TODO Auto-generated method stub
		return 0;
	}
}