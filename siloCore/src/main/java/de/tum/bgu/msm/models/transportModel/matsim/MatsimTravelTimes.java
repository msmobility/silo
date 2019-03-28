package de.tum.bgu.msm.models.transportModel.matsim;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.router.FastAStarLandmarksFactory;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculator.Path;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.utils.leastcostpathtree.LeastCostPathTree;
import org.matsim.utils.leastcostpathtree.LeastCostPathTree.NodeData;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import cern.colt.map.tdouble.OpenIntDoubleHashMap;
import cern.colt.map.tobject.OpenIntObjectHashMap;
import de.tum.bgu.msm.data.Location;
import de.tum.bgu.msm.data.MicroLocation;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;

/**
 * @author dziemke
 */
public final class MatsimTravelTimes implements TravelTimes {
	private final static Logger LOG = Logger.getLogger(MatsimTravelTimes.class);

	private LeastCostPathTree leastCoastPathTree;
	private LeastCostPathCalculator lcpCalculator;
	private Network network;
	private final Map<Zone, List<Node>> zoneCalculationNodesMap = new HashMap<>();
	private final static int NUMBER_OF_CALC_POINTS = 1;
	private final OpenIntObjectHashMap treesForNodes = new OpenIntObjectHashMap();
	Map<Integer, Zone> zones;
	
	private TravelTime travelTime;
	private TravelDisutility travelDisutility;
	
	// Counters
	int nodeNewlyComputedMicro = 0;
	int requests = 0;
	int requestsZones = 0;
	int requestsMicro = 0;
	int zonesComputed = 0;
	int zonesFromCache = 0;
	
	final int maxSize = 2000;
    
	Map<Id<Node>, Integer> nodeMap = new HashMap<>();
	private final Table<Zone, Region, Double> travelTimeToRegion = HashBasedTable.create();

	void update(TravelTime travelTime, TravelDisutility disutility) {
		
		// For zone-based route computation (not so fast, but allows caching)
		this.leastCoastPathTree = new LeastCostPathTree(travelTime, disutility);

		// For location-based route computation (very fast, but no caching)
//        this.lcpCalculator = new FastDijkstraFactory().createPathCalculator(network, disutility, travelTime);
        this.lcpCalculator = new FastAStarLandmarksFactory().createPathCalculator(network, disutility, travelTime);
        
        this.travelTime = travelTime;
        this.travelDisutility = disutility;
		
		this.treesForNodes.clear();
	}

	public void initialize(Map<Integer, Zone> zones, Network network) {
		this.network = network;
		this.zones = zones;
		
		int j = 0;
		for (Id<Node> nodeId : network.getNodes().keySet()) {
			nodeMap.put(nodeId, j);
			j++;
		}
		
		for (Zone zone : zones.values()) {
            for (int i = 0; i < NUMBER_OF_CALC_POINTS; i++) { // Several points in a given origin zone
            	Coordinate coordinate = zone.getRandomCoordinate(); // TODO Check if random coordinate is the best representative
				Coord originCoord = new Coord(coordinate.x, coordinate.y);
                Node originNode = NetworkUtils.getNearestLink(network, originCoord).getToNode();

				if (!zoneCalculationNodesMap.containsKey(zone)) {
					zoneCalculationNodesMap.put(zone, new LinkedList<>());
				}
				zoneCalculationNodesMap.get(zone).add(originNode);
			}
		}
        LOG.warn("There are " + zoneCalculationNodesMap.keySet().size() + " origin zones.");
    }

	private double getZoneToZoneTravelTime(Zone origin, Zone destination, double timeOfDay_s, String mode) {
		LOG.info("Getting zone to zone travel time.");
		switch (mode) {
			case TransportMode.car:
				double sumTravelTime_min = 0.;
				for (Node originNode : zoneCalculationNodesMap.get(origin)) { // Several points in a given origin zone
					OpenIntDoubleHashMap nodeTree;
					int originNodeInt = nodeMap.get(originNode.getId());
					if (treesForNodes.containsKey(originNodeInt)) {  // Node already checked
						zonesFromCache++;
						if (zonesFromCache % 100 == 0) LOG.info("Getting zone from cache. Number of occurrences: " + zonesFromCache);
						nodeTree = (OpenIntDoubleHashMap) treesForNodes.get(originNodeInt);
					} else {
						zonesComputed++;
						if (zonesComputed % 100 == 0) LOG.info("Computing new zone. Number of occurrences: " + zonesComputed);
						leastCoastPathTree.calculate(network, originNode, timeOfDay_s);
						nodeTree = createOnlyTimeTree(leastCoastPathTree.getTree());
						treesForNodes.put(originNodeInt, nodeTree);
					}
	
					for (Node destinationNode : zoneCalculationNodesMap.get(destination)) { // Several points in a given destination zone
						double arrivalTime_s = nodeTree.get(nodeMap.get(destinationNode.getId()));
						sumTravelTime_min += ((arrivalTime_s - timeOfDay_s) / 60.);
					}
				}
				return sumTravelTime_min / NUMBER_OF_CALC_POINTS;
			case TransportMode.pt:
				//TODO: reconsider matsim pt travel times. nk apr'18
//	            return delegate.getTravelTime(origin, destination, timeOfDay_s, mode);
				throw new IllegalArgumentException("Not implemented for PT yet..");
			default:
	        	throw new IllegalArgumentException("Other modes not implemented yet..");
		}
	}
	
	public double getTravelTime(Location origin, Location destination, double timeOfDay_s, String mode) {
		requests++;
		if (requests % 10000 == 0) LOG.info("New travel time request. Number of occurrences: " + requests);
		
		if (origin instanceof MicroLocation && destination instanceof MicroLocation) { // Microlocations case
			requestsMicro++;
			if (requestsMicro % 10000 == 0) LOG.info("New travel time request for microlocations. Number of occurrences: " + requestsMicro);
			switch (mode) {
				case TransportMode.car:
					Coord originCoord = CoordUtils.createCoord(((MicroLocation) origin).getCoordinate());
					Coord destinationCoord = CoordUtils.createCoord(((MicroLocation) destination).getCoordinate());
					
					// TODO Check if this way of selecting a node is good
					Node originNode = NetworkUtils.getNearestLink(network, originCoord).getToNode();
					
					// TODO Check if this way of selecting a node is good
					Node destinationNode = NetworkUtils.getNearestLink(network, destinationCoord).getToNode();
					Path path = lcpCalculator.calcLeastCostPath(originNode, destinationNode, timeOfDay_s, null, null);
					// TODO Use travel costs?
					// path.travelCost;
					return path.travelTime;
				case TransportMode.pt:
					//TODO: reconsider matsim pt travel times. nk apr'18
		            throw new IllegalArgumentException("Not implemented for PT yet..");
		        default:
		        	throw new IllegalArgumentException("Other modes not implemented yet..");

			}
		}
		else if (origin instanceof Zone) { // Non-microlocations case
			if (destination instanceof Zone) {
				requestsZones++;
				if (requestsZones % 10000 == 0) LOG.info("New travel time request for zones. Number of occurrences: " + requestsZones);
				return getZoneToZoneTravelTime((Zone) origin, (Zone) destination, timeOfDay_s, mode);
			} else if (destination instanceof Region) {
				LOG.warn("Here region...");
//				Region destinationRegion = (Region) destination;
//				if (travelTimeToRegion.contains(originZone, destinationRegion)) {
//					return travelTimeToRegion.get(originZone, destinationRegion);
//				}
//				double min = Double.MAX_VALUE;
//        		for (Zone zoneInRegion : destinationRegion.getZones()) {
//        			double travelTime = getZoneToZoneTravelTime(originZone, zoneInRegion, timeOfDay_s, mode);
//        			if (travelTime < min) {
//        				min = travelTime;
//        			}
//        		}
//        		travelTimeToRegion.put(originZone, destinationRegion, min);
				throw new RuntimeException("to region currently not implemented.");
			}
		} else {
			throw new IllegalArgumentException("The combination with origin of type " + origin.getClass().getName() 
					+ " and destination of type " + destination.getClass().getName() + " is not valid.");
		}
		throw new RuntimeException("Should not arrive here.");
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
	
	private OpenIntDoubleHashMap createOnlyTimeTree(Map<Id<Node>, NodeData> tree) {
		OpenIntDoubleHashMap onlyTimeTree = new OpenIntDoubleHashMap();
		for (Id<Node> nodeId : tree.keySet()) {
			onlyTimeTree.put(nodeMap.get(nodeId), tree.get(nodeId).getTime());
		}
		return onlyTimeTree;
	}

	public TravelTimes duplicate() {
		LOG.warn("Creating another TravelTimes object.");
		MatsimTravelTimes matsimTravelTimes = new MatsimTravelTimes();
		matsimTravelTimes.initialize(zones, network);
		matsimTravelTimes.update(travelTime, travelDisutility);
		return matsimTravelTimes;
	}
}