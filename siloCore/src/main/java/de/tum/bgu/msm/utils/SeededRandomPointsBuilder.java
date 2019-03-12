package de.tum.bgu.msm.utils;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.shape.random.RandomPointsBuilder;

import java.util.Random;

public class SeededRandomPointsBuilder extends RandomPointsBuilder {

    private final Random random;

    public SeededRandomPointsBuilder(GeometryFactory factory, Random random) {
        super(factory);
        this.random = random;
    }

    @Override
    protected Coordinate createRandomCoord(Envelope env) {
        double x = env.getMinX() + env.getWidth() * random.nextDouble();
        double y = env.getMinY() + env.getHeight() * random.nextDouble();
        return createCoord(x, y);
    }
}
