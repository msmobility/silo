package de.tum.bgu.msm.transportModel.matsim;

import de.tum.bgu.msm.data.PTDistances;
import de.tum.bgu.msm.data.munich.GeoDataMuc;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.pt.router.TransitRouterConfig;
import org.matsim.pt.router.TransitRouterNetwork;

public class MatsimPTDistances implements PTDistances {

	private final TransitRouterNetwork transitRouterNetwork;
	private final GeoDataMuc geoData;
	
	public MatsimPTDistances(Config config, Scenario scenario, GeoDataMuc geoData) {
		final TransitRouterConfig transitConfig = new TransitRouterConfig(config);
		transitRouterNetwork = TransitRouterNetwork.createFromSchedule(
				scenario.getTransitSchedule(), transitConfig.getBeelineWalkConnectionDistance());
		this.geoData = geoData;
	}
	
	@Override
	public double getDistanceToNearestPTStop(double xCoord, double yCoord) {
		return getDistanceToNearestPTStop(CoordUtils.createCoord(xCoord, yCoord));
	}

	@Override
	public double getDistanceToNearestPTStop(int zone) {
		Coord centroid = geoData.getCentroidOfZone(zone);
		return getDistanceToNearestPTStop(centroid);
	}

	@Override
	public double getDistanceToNearestPTStop(Coord coord) {
		Coord nearestStopCoord = transitRouterNetwork.getNearestNode(coord).stop.getStopFacility().getCoord();
		return CoordUtils.calcEuclideanDistance(coord, nearestStopCoord);
	}
}