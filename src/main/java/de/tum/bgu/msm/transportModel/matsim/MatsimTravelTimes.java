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
	private final static double TIME_OF_DAY = 8. * 60 * 60.; // TODO
	private final Map<Id<Node>, Map<Id<Node>, LeastCostPathTree.NodeData>> treesForNode = new HashMap<>();

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
	public double getTravelTime(int origin, int destination) {
		logger.trace("There are " + zoneCalculationNodesMap.keySet().size() + " origin zones.");
		double sumTravelTime_min = 0.;
		
		for (Node originNode : zoneCalculationNodesMap.get(origin)) { // Several points in a given origin zone
			Map<Id<Node>, LeastCostPathTree.NodeData> tree;
			if(treesForNode.containsKey(originNode.getId())) {
				tree = treesForNode.get(originNode.getId());
			} else {
				leastCoastPathTree.calculate(network, originNode, TIME_OF_DAY);
				tree = leastCoastPathTree.getTree();
				treesForNode.put(originNode.getId(), tree);
			}

			
			for (Node destinationNode : zoneCalculationNodesMap.get(destination)) {// several points in a given destination zone
						
				double arrivalTime = tree.get(destinationNode.getId()).getTime();
				sumTravelTime_min += ((arrivalTime - TIME_OF_DAY) / 60.);
			}
		}
		return sumTravelTime_min / NUMBER_OF_CALC_POINTS / NUMBER_OF_CALC_POINTS;
	}
}