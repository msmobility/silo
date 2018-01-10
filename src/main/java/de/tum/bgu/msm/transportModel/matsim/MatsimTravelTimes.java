package de.tum.bgu.msm.transportModel.matsim;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.network.NetworkUtils;
import org.matsim.utils.leastcostpathtree.LeastCostPathTree;
import org.opengis.feature.simple.SimpleFeature;

import de.tum.bgu.msm.data.travelTimes.TravelTimes;

public class MatsimTravelTimes implements TravelTimes {
	private final static Logger logger = Logger.getLogger(MatsimTravelTimes.class);

	private final LeastCostPathTree leastCoastPathTree;
	private final Network network;
	private final Map<Integer, List<Node>> zoneCalculationNodesMap = new HashMap<>();
	private final static int NUMBER_OF_CALC_POINTS = 1;
	private final Map<Id<Node>, Map<Double, Map<Id<Node>, LeastCostPathTree.NodeData>>> treesForNodesByTimes = new HashMap<>();

	public MatsimTravelTimes(LeastCostPathTree leastCoastPathTree, Map<Integer,SimpleFeature> zoneFeatureMap, Network network) {
		this.leastCoastPathTree = leastCoastPathTree;
		this.network = network;
		initialize(zoneFeatureMap);
	}

	private void initialize(Map<Integer,SimpleFeature> zoneFeatureMap) {
		for (int zoneId : zoneFeatureMap.keySet()) {

			for (int i = 0; i < NUMBER_OF_CALC_POINTS; i++) { // Several points in a given origin zone
				SimpleFeature originFeature = zoneFeatureMap.get(zoneId);
				Coord originCoord = SiloMatsimUtils.getRandomCoordinateInGeometry(originFeature);
				Link originLink = NetworkUtils.getNearestLink(network, originCoord);
				Node originNode = originLink.getToNode();

				if (!zoneCalculationNodesMap.containsKey(zoneId)) {
					zoneCalculationNodesMap.put(zoneId, new LinkedList<Node>());
				}
				zoneCalculationNodesMap.get(zoneId).add(originNode);
			}
		}
	}

	@Override
	public double getTravelTime(int origin, int destination, double timeOfDay_s) {
		logger.trace("There are " + zoneCalculationNodesMap.keySet().size() + " origin zones.");
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
			
			for (Node destinationNode : zoneCalculationNodesMap.get(destination)) {// several points in a given destination zone
						
				double arrivalTime_s = tree.get(destinationNode.getId()).getTime();
				sumTravelTime_min += ((arrivalTime_s - timeOfDay_s) / 60.);
			}
		}
		return sumTravelTime_min / NUMBER_OF_CALC_POINTS / NUMBER_OF_CALC_POINTS;
	}
}