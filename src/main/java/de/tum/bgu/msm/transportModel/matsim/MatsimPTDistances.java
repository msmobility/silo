package de.tum.bgu.msm.transportModel.matsim;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.pt.router.TransitRouterConfig;
import org.matsim.pt.router.TransitRouterNetwork;

import de.tum.bgu.msm.data.PTDistances;

public class MatsimPTDistances implements PTDistances {

	private final TransitRouterNetwork transitRouterNetwork;
	
	public MatsimPTDistances(Config config, Scenario scenario) {
		final TransitRouterConfig transitConfig = new TransitRouterConfig(config);
		transitRouterNetwork = TransitRouterNetwork.createFromSchedule(
				scenario.getTransitSchedule(), transitConfig.getBeelineWalkConnectionDistance());
	}
	
	@Override
	public double getDistanceToNearestPTStop(double xCoord, double yCoord) {
		Coord nearestStopCoord = transitRouterNetwork.getNearestNode(CoordUtils.createCoord(xCoord, yCoord)).stop.getStopFacility().getCoord();
		return CoordUtils.calcEuclideanDistance(CoordUtils.createCoord(xCoord, yCoord), nearestStopCoord);
	}
}