package de.tum.bgu.msm.transportModel.matsim;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.gbl.Gbl;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.router.TripRouter;
import org.matsim.facilities.ActivityFacilitiesFactory;
import org.matsim.facilities.ActivityFacilitiesFactoryImpl;
import org.matsim.facilities.ActivityFacility;
import org.opengis.feature.simple.SimpleFeature;

import de.tum.bgu.msm.data.travelTimes.TravelTimes;

public class MatsimPtTravelTimes implements TravelTimes {
	private final static Logger logger = Logger.getLogger(MatsimPtTravelTimes.class);

	private final TripRouter tripRouter;
	private final Map<Integer,SimpleFeature> zoneFeatureMap;
	private final Network network;
	private final static int NUMBER_OF_CALC_POINTS = 1;
	private final static double TIME_OF_DAY = 8. * 60 * 60.; // TODO
	private final static String mode = TransportMode.pt;

	public MatsimPtTravelTimes(TripRouter tripRouter, Map<Integer,SimpleFeature> zoneFeatureMap, Network network) {
		this.tripRouter = tripRouter;
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
				Node originNode = originLink.getToNode();
				
				if (!zoneCalculationNodesMap.containsKey(zoneId)) {
					zoneCalculationNodesMap.put(zoneId, new LinkedList<Node>());
				}
				zoneCalculationNodesMap.get(zoneId).add(originNode);
			}
		}
		
		logger.info("There are " + zoneFeatureMap.keySet().size() + " origin zones.");
		double sumTravelTime_min = 0.;
		
		for (Node originNode : zoneCalculationNodesMap.get(origin)) { // Several points in a given origin zone
			for (Node destinationNode : zoneCalculationNodesMap.get(destination)) {// several points in a given destination zone
				ActivityFacilitiesFactory activityFacilitiesFactory = new ActivityFacilitiesFactoryImpl();
				ActivityFacility originFacility = activityFacilitiesFactory.createActivityFacility(null, originNode.getCoord());
				ActivityFacility destinationFacility = activityFacilitiesFactory.createActivityFacility(null, destinationNode.getCoord());
				
				Gbl.assertNotNull(tripRouter);
				List<? extends PlanElement> route = tripRouter.calcRoute(mode, originFacility, destinationFacility, TIME_OF_DAY, null);
				
				for (PlanElement pe : route) {
					if (pe instanceof Activity) {
						// Activities as part of route can only be stage/dummy activities; still checking this to be sure...
						Activity activity = (Activity) pe;
						if (tripRouter.getStageActivityTypes().isStageActivity(activity.getType())) {
							sumTravelTime_min += (activity.getEndTime() - activity.getStartTime()) /60.;
						}
					} else if (pe instanceof Leg) {
						sumTravelTime_min += (((Leg) pe).getRoute().getTravelTime() / 60.);
					}
				}
			}
		}
		
		return sumTravelTime_min / NUMBER_OF_CALC_POINTS / NUMBER_OF_CALC_POINTS;
	}
}