package de.tum.bgu.msm.utils.concurrent;

import de.tum.bgu.msm.SiloUtil;

import java.util.Random;

public abstract class RandomizableConcurrentFunction implements ConcurrentFunction {

    protected final Random random;

    protected RandomizableConcurrentFunction() {
        this.random = new Random(SiloUtil.getRandomObject().nextLong());
    }

    @Override
    public abstract void execute();
}
