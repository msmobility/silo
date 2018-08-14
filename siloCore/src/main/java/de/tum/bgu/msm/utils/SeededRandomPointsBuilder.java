package de.tum.bgu.msm.utils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.shape.random.RandomPointsBuilder;

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
