package de.tum.bgu.msm.models.transportModel.matsim;

import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.router.TripRouter;
import org.matsim.facilities.ActivityFacilitiesFactory;
import org.matsim.facilities.ActivityFacilitiesFactoryImpl;
import org.matsim.facilities.ActivityFacility;
import org.opengis.feature.simple.SimpleFeature;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Deprecated // yyyyyy rather than having a separate class for each mode, add the mode to the "getTravelTime" query. kai, apr'18
/* deliberately package */ class MatsimPtTravelTimes implements TravelTimes {
	private final static Logger logger = Logger.getLogger(MatsimPtTravelTimes.class);

	private final TripRouter tripRouter;
	private final Map<Integer, List<Node>> zoneCalculationNodesMap = new HashMap<>();
	private final Network network;
	private final static int NUMBER_OF_CALC_POINTS = 1;
	private final static double TIME_OF_DAY = 8. * 60 * 60.; // TODO
	private final static String mode = TransportMode.pt;
	private final ActivityFacilitiesFactory activityFacilitiesFactory = new ActivityFacilitiesFactoryImpl();
	
	@Deprecated // yyyyyy rather than having a separate class for each mode, add the mode to the "getTravelTime" query. kai, apr'18
	public MatsimPtTravelTimes(TripRouter tripRouter, Map<Integer,SimpleFeature> zoneFeatureMap, Network network) {
		this.tripRouter = tripRouter;
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
					zoneCalculationNodesMap.put(zoneId, new LinkedList<>());
				}
				zoneCalculationNodesMap.get(zoneId).add(originNode);
			}
		}
	}

	@Override
	@Deprecated // yyyyyy rather than having a separate class for each mode, add the mode to the "getTravelTime" query. kai, apr'18
	public double getTravelTime(int origin, int destination, double timeOfDay, String mode) {
		logger.trace("There are " + zoneCalculationNodesMap.keySet().size() + " origin zones.");
		double sumTravelTime_min = 0.;

		for (Node originNode : zoneCalculationNodesMap.get(origin)) { // Several points in a given origin zone
			Id<Link> originLink = originNode.getInLinks().values().iterator().next().getId();
			ActivityFacility originFacility = activityFacilitiesFactory.createActivityFacility(null, originNode.getCoord(), originLink);
			for (Node destinationNode : zoneCalculationNodesMap.get(destination)) {// several points in a given destination zone
				Id<Link> destinationLink = destinationNode.getInLinks().values().iterator().next().getId();
				ActivityFacility destinationFacility = activityFacilitiesFactory.createActivityFacility(null, destinationNode.getCoord(), destinationLink);
				
//				Gbl.assertNotNull(tripRouter);
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
