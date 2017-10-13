package de.tum.bgu.msm.transportModel.matsim;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
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
	private final Map<Integer,SimpleFeature> zoneFeatureMap;
	private final Network network;
	private final static int NUMBER_OF_CALC_POINTS = 1;
	private final static double TIME_OF_DAY = 8. * 60 * 60.; // TODO

	public MatsimTravelTimes(LeastCostPathTree leastCoastPathTree, Map<Integer,SimpleFeature> zoneFeatureMap, Network network) {
		this.leastCoastPathTree = leastCoastPathTree;
		this.zoneFeatureMap = zoneFeatureMap;
		this.network = network;
	}

	@Override
	public double getTravelTimeFromTo(int origin, int destination) {
		
		Map<Integer, List<Node>> zoneCalculationNodesMap = new HashMap<>();
		
		for (int zoneId : zoneFeatureMap.keySet()) {
			
			for (int i = 0; i < NUMBER_OF_CALC_POINTS; i++) { // Several points in a given origin zone
				SimpleFeature originFeature = zoneFeatureMap.get(zoneId);
				Coord originCoord = SiloMatsimUtils.getRandomCoordinateInGeometry(originFeature);
				Link originLink = NetworkUtils.getNearestLink(network, originCoord);
				Node originNode = originLink.getFromNode();
				
				if (!zoneCalculationNodesMap.containsKey(zoneId)) {
					zoneCalculationNodesMap.put(zoneId, new LinkedList<Node>());
				}
				zoneCalculationNodesMap.get(zoneId).add(originNode);
			}
		}
		
		logger.info("There are " + zoneFeatureMap.keySet().size() + " origin zones.");
		
		double sumTravelTime_min = 0.;
		
		for (Node originNode : zoneCalculationNodesMap.get(origin)) { // Several points in a given origin zone
			leastCoastPathTree.calculate(network, originNode, TIME_OF_DAY);
			
			for (Node destinationNode : zoneCalculationNodesMap.get(destination)) {// several points in a given destination zone
						
				double arrivalTime = leastCoastPathTree.getTree().get(destinationNode.getId()).getTime();
				sumTravelTime_min += (float) ((arrivalTime - TIME_OF_DAY) / 60.);
			}
		}
		
		return sumTravelTime_min / NUMBER_OF_CALC_POINTS / NUMBER_OF_CALC_POINTS;
	}
}