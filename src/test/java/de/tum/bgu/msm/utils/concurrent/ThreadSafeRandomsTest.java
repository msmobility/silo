package de.tum.bgu.msm.utils.concurrent;

import de.tum.bgu.msm.SiloUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ThreadSafeRandomsTest {

    private final Double[] REF_RESULTS = {0.017514838216565187, 0.7257102896080766,
            0.14966221166481797, 0.4451602785388815, 0.9857491932510895
    };

    @Test
    public void test() {

        List<RandomizableConcurrentFunction> functions = new ArrayList<>();
        SiloUtil.siloInitialization("./test/scenarios/annapolis/javaFiles/siloMstm.properties");

        ConcurrentFunctionExecutor executor = new ConcurrentFunctionExecutor();
        for(int i= 0; i<5; i++) {
           executor.addFunction(new TestFunction(i));
        }
        executor.execute();
    }

    private class TestFunction extends RandomizableConcurrentFunction {

        final int id;

        TestFunction(int id) {
            this.id = id;
        }

        @Override
        public void execute() {
            Assert.assertEquals(REF_RESULTS[id], this.random.nextDouble(), 0.0);
        }
    }
}
