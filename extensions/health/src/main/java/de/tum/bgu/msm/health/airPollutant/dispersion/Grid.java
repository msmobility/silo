package de.tum.bgu.msm.health.airPollutant.dispersion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import org.locationtech.jts.geom.prep.PreparedGeometryFactory;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.utils.collections.QuadTree;

/**
 * Abstract class for regular Grids
 *
 * @param <T> Value of each grid cell
 */
public abstract class Grid<T> {

    private static final org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger(Grid.class);
    private static final GeometryFactory geometryFactory = new GeometryFactory();
    final double horizontalCentroidDistance;
    QuadTree<Cell<T>> quadTree;

    public Grid(final double horizontalCentroidDistance, final Supplier<T> initialValueSupplier, final Geometry bounds) {
        this(horizontalCentroidDistance, initialValueSupplier, new PreparedGeometryFactory().create(bounds));
    }

    public Grid(final double horizontalCentroidDistance, final Supplier<T> initialValueSupplier, final PreparedGeometry bounds) {
        this.horizontalCentroidDistance = horizontalCentroidDistance;
        generateGrid(initialValueSupplier, bounds);
    }

    public Grid(Network network, final double horizontalCentroidDistance, final Supplier<T> initialValueSupplier, final Geometry bounds) {
        this(network, horizontalCentroidDistance, initialValueSupplier, new PreparedGeometryFactory().create(bounds));
    }

    public Grid(Network network, final double horizontalCentroidDistance, final Supplier<T> initialValueSupplier, final PreparedGeometry bounds) {
        this.horizontalCentroidDistance = horizontalCentroidDistance;
        generateRoadReceiverPoints(network, initialValueSupplier, bounds);
    }

    public Grid(List<Coordinate> receiverPoints, final double horizontalCentroidDistance, final Supplier<T> initialValueSupplier, final PreparedGeometry bounds) {
        this.horizontalCentroidDistance = horizontalCentroidDistance;
        generateReceiverPoints(receiverPoints,initialValueSupplier, bounds);
    }

    /**
     * retrieve cell for a given coordinate. Cell with closest centroid will be returne
     *
     * @param coordinate coordinate within a cell
     * @return Cell with closest centroid.
     */
    public Cell<T> getCell(Coordinate coordinate) {

        return quadTree.getClosest(coordinate.x, coordinate.y);
    }

    /**
     * retrieve cells within bounds
     *
     * @param bounds bounds
     * @return all cells which have their centroid withing the given bounds
     */
    public Collection<Cell<T>> getCells(Geometry bounds) {

        return quadTree.getRectangle(
                bounds.getEnvelopeInternal().getMinX(), bounds.getEnvelopeInternal().getMinY(),
                bounds.getEnvelopeInternal().getMaxX(), bounds.getEnvelopeInternal().getMaxY(),
                new ArrayList<>()
        );
    }

    /**
     * retrieve all cells
     * @return all cells of the grid
     */
    public Collection<Cell<T>> getCells() {
        return quadTree.values();
    }

    /**
     * Returns the x-value for the first centroid of each line for a given y
     * @param forY y-value to calculate x for
     * @return x-value
     */
    abstract double getMinX(double forY);

    /**
     * Returns the y-value for the first centroid of each column
     * @return y-value
     */
    abstract double getMinY();

    /**
     * @return horizontal distance between each centroid
     */
    abstract double getCentroidDistanceX();

    /**
     * @return vertical distance between each centroid
     */
    abstract double getCentroidDistanceY();

    /**
     * @return area of one cell (assumes that all cells have the same area)
     */
    public abstract double getCellArea();

    private void generateGrid(Supplier<T> initialValueSupplier, final PreparedGeometry bounds) {

        Envelope envelope = bounds.getGeometry().getEnvelopeInternal();

        quadTree = new QuadTree<>(envelope.getMinX(), envelope.getMinY(), envelope.getMaxX(), envelope.getMaxY());
        generateAllRows(initialValueSupplier, bounds);
    }

    private void generateAllRows(final Supplier<T> initialValueSupplier, final PreparedGeometry bounds) {

        for (double y = getMinY(); y <= quadTree.getMaxNorthing(); y += getCentroidDistanceY()) {
            generateRow(y, initialValueSupplier, bounds);
        }
    }

    private void generateRow(final double y, final Supplier<T> initialValueSupplier, final PreparedGeometry bounds) {

        for (double x = getMinX(y); x <= quadTree.getMaxEasting(); x += getCentroidDistanceX()) {
            Coordinate coord = new Coordinate(x, y);
            if (bounds.contains(geometryFactory.createPoint(coord)))
                quadTree.put(x, y, new Cell<>(coord, initialValueSupplier.get()));
        }
    }

    //TODO: JIBE generate exposure receiver points on road network (network nodes, centroid node of each link)
    private void generateRoadReceiverPoints(Network network, final Supplier<T> initialValueSupplier, final PreparedGeometry bounds) {
        Envelope envelope = bounds.getGeometry().getEnvelopeInternal();

        quadTree = new QuadTree<>(envelope.getMinX(), envelope.getMinY(), envelope.getMaxX(), envelope.getMaxY());

        for(Node node : network.getNodes().values()){
            Coordinate coord = new Coordinate(node.getCoord().getX(), node.getCoord().getY());
            if (bounds.contains(geometryFactory.createPoint(coord)))
                quadTree.put(coord.x, coord.y, new Cell<>(coord, initialValueSupplier.get()));
        }

        for(Link link : network.getLinks().values()){
            Coordinate linkCentroid = new Coordinate(link.getCoord().getX(), link.getCoord().getY());
            if (bounds.contains(geometryFactory.createPoint(linkCentroid)))
                quadTree.put(linkCentroid.x, linkCentroid.y, new Cell<>(linkCentroid, initialValueSupplier.get()));
        }
    }

    private void generateReceiverPoints(List<Coordinate> receiverPoints, Supplier<T> initialValueSupplier, final PreparedGeometry bounds) {
        Envelope envelope = bounds.getGeometry().getEnvelopeInternal();

        quadTree = new QuadTree<>(envelope.getMinX(), envelope.getMinY(), envelope.getMaxX(), envelope.getMaxY());

        int nanCount = 0;
        int outOfBoundsCount = 0;
        List<Coordinate> outOfBoundsCoords = new ArrayList<>();

        for (Coordinate coordinate : receiverPoints){
            if (Double.isNaN(coordinate.x) || Double.isNaN(coordinate.y)) {
                nanCount++;
                continue;
            }
            if (bounds.contains(geometryFactory.createPoint(coordinate))) {
                quadTree.put(coordinate.x, coordinate.y, new Cell<>(coordinate, initialValueSupplier.get()));
            } else {
                outOfBoundsCount++;
                outOfBoundsCoords.add(coordinate);
            }
        }
        logger.warn("generateReceiverPoints: {} coordinates skipped due to NaN values, {} coordinates skipped for being outside bounds.", nanCount, outOfBoundsCount);
        if (outOfBoundsCount > 0) {
            logger.warn("To see the specific coordinates outside bounds, enable DEBUG logging.");
            for (Coordinate c : outOfBoundsCoords) {
                logger.debug("Coordinate outside bounds: {}", c);
            }
        }
    }


    public static class Cell<T> {

        private Coordinate coordinate;
        private T value;

        Cell(Coordinate coordinate, T value) {
            this.coordinate = coordinate;
            this.value = value;
        }

        public Coordinate getCoordinate() {
            return coordinate;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }
    }
}
