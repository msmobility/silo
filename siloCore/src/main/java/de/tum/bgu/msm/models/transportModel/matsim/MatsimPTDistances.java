package de.tum.bgu.msm.models.transportModel.matsim;

import de.tum.bgu.msm.data.munich.GeoDataMuc;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.pt.router.TransitRouterConfig;
import org.matsim.pt.router.TransitRouterNetwork;

/* deliberately package */ class MatsimPTDistances {

	private final TransitRouterNetwork transitRouterNetwork;
	private final GeoDataMuc geoData;

	public MatsimPTDistances(Config config, Scenario scenario, GeoDataMuc geoData) {
		final TransitRouterConfig transitConfig = new TransitRouterConfig(config);
		transitRouterNetwork = TransitRouterNetwork.createFromSchedule(
				scenario.getTransitSchedule(), transitConfig.getBeelineWalkConnectionDistance());
		this.geoData = geoData;
	}

	public double getDistanceToNearestPTStop(Coord coord) {
		Coord nearestStopCoord = transitRouterNetwork.getNearestNode(coord).stop.getStopFacility().getCoord();
		return CoordUtils.calcEuclideanDistance(coord, nearestStopCoord);
	}
}
