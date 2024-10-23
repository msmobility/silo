package de.tum.bgu.msm.health.airPollutant.dispersion;

import java.util.List;
import java.util.function.Supplier;

import de.tum.bgu.msm.data.Zone;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import org.matsim.api.core.v01.network.Network;

/**
 * Square grid which holds values
 *
 * @param <T> value of each cell
 */
public final class SquareGrid<T> extends Grid<T> {

    /**
     * New Instance of SquareGrid
     *
     * @param centroidDistance     distance between cell centroids
     * @param initialValueSupplier function to deliver a initial value when cells are created
     * @param bounds               outer bounds of the grid
     */
    public SquareGrid(final double centroidDistance, final Supplier<T> initialValueSupplier, final Geometry bounds) {
        super(centroidDistance, initialValueSupplier, bounds);
    }

    /**
     * New Instance of SquareGrid
     *
     * @param centroidDistance     distance between cell centroids
     * @param initialValueSupplier function to deliver a initial value when cells are created
     * @param bounds               outer bounds of the grid
     */
    public SquareGrid(final double centroidDistance, final Supplier<T> initialValueSupplier, final PreparedGeometry bounds) {
        super(centroidDistance, initialValueSupplier, bounds);
    }

    public SquareGrid(Network network, final double centroidDistance, final Supplier<T> initialValueSupplier, final PreparedGeometry bounds) {
        super(network, centroidDistance, initialValueSupplier, bounds);
    }

    public SquareGrid(Network network, List<Zone> zone, final double centroidDistance, final Supplier<T> initialValueSupplier, final PreparedGeometry bounds) {
        super(network, zone, centroidDistance, initialValueSupplier, bounds);
    }
    @Override
    double getMinX(double forY) {
        return quadTree.getMinEasting() + horizontalCentroidDistance / 2;
    }

    @Override
    double getMinY() {
        return quadTree.getMinNorthing() + horizontalCentroidDistance / 2;
    }

    @Override
    double getCentroidDistanceX() {
        return horizontalCentroidDistance;
    }

    @Override
    double getCentroidDistanceY() {
        return horizontalCentroidDistance;
    }

    @Override
    public double getCellArea() {
        return horizontalCentroidDistance * horizontalCentroidDistance;
    }
}
