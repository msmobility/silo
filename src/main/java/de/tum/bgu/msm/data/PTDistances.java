package de.tum.bgu.msm.data;

import org.matsim.api.core.v01.Coord;

public interface PTDistances {
	
	default double getDistanceToNearestPTStop(double xCoord, double yCoord) {
		throw new RuntimeException("Not implemented!");
	}

	default double getDistanceToNearestPTStop(Coord coord) {
		throw new RuntimeException("Not implemented!");
	}
}