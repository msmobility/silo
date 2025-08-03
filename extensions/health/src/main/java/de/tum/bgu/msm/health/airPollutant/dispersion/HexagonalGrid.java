package de.tum.bgu.msm.health.airPollutant.dispersion;

import java.util.List;
import java.util.function.Supplier;

import de.tum.bgu.msm.data.Zone;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import org.matsim.api.core.v01.network.Network;

/**
 * Hexagonal Grid which holds values
 *
 * @param <T> value of each cell
 */
public final class HexagonalGrid<T> extends Grid<T> {

    /**
     * New Instance of HexagonalGrid
     *
     * @param horizontalCentroidDistance horizontal distance between cell centroids
     * @param initialValueSupplier       function to deliver a initial value when cells are created
     * @param bounds                     outer bounds of the grid
     */
    public HexagonalGrid(double horizontalCentroidDistance, Supplier<T> initialValueSupplier, Geometry bounds) {
        super(horizontalCentroidDistance, initialValueSupplier, bounds);
    }

    /**
     * New Instance of HexagonalGrid
     *
     * @param horizontalCentroidDistance horizontal distance between cell centroids
     * @param initialValueSupplier       function to deliver a initial value when cells are created
     * @param bounds                     outer bounds of the grid
     */
    public HexagonalGrid(double horizontalCentroidDistance, Supplier<T> initialValueSupplier, PreparedGeometry bounds) {
        super(horizontalCentroidDistance, initialValueSupplier, bounds);
    }

    public HexagonalGrid(Network network, double horizontalCentroidDistance, Supplier<T> initialValueSupplier, PreparedGeometry bounds) {
        super(network,horizontalCentroidDistance, initialValueSupplier, bounds);
    }

    public HexagonalGrid(List<Coordinate> receiverPoints, double horizontalCentroidDistance, Supplier<T> initialValueSupplier, PreparedGeometry bounds) {
        super(receiverPoints,horizontalCentroidDistance, initialValueSupplier, bounds);
    }

    @Override
    double getMinX(double forY) {

        long factor = Math.round((forY - quadTree.getMinNorthing()) / getCentroidDistanceY());
        if ((factor % 2) == 0)
            return quadTree.getMinEasting();
        else
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
        return horizontalCentroidDistance * Math.sqrt(3) / 2.0;
    }

    @Override
    public double getCellArea() {
        // as in https://en.wikipedia.org/wiki/Hexagon#Parameters
        return horizontalCentroidDistance * horizontalCentroidDistance * Math.sqrt(3) / 2.0;
    }
}
