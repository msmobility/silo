//package de.tum.bgu.msm.utils.concurrent;
//
//import de.tum.bgu.msm.Implementation;
//import de.tum.bgu.msm.utils.SiloUtil;
//import de.tum.bgu.msm.util.concurrent.ConcurrentExecutor;
//import de.tum.bgu.msm.util.concurrent.RandomizableConcurrentFunction;
//import org.junit.Assert;
//import org.junit.Test;
//
//public class ThreadSafeRandomsTest {
//
//    private final Double[] REF_RESULTS = {0.017514838216565187, 0.7257102896080766,
//            0.14966221166481797, 0.4451602785388815, 0.9857491932510895
//    };
//
//    @Test
//    public void test() {
//        SiloUtil.siloInitialization(Implementation.MARYLAND, "./test/scenarios/annapolis/javaFiles/siloMstm.properties");
//
//        ConcurrentExecutor executor = ConcurrentExecutor.cachedService();
//        for(int i= 0; i<5; i++) {
//           executor.addTaskToQueue(new TestFunction(i));
//        }
//        executor.execute();
//    }
//
//    private class TestFunction extends RandomizableConcurrentFunction {
//
//        final int id;
//
//        TestFunction(int id) {
//            super(SiloUtil.getRandomObject().nextLong());
//            this.id = id;
//        }
//
//        @Override
//        public Void call() {
//            Assert.assertEquals(REF_RESULTS[id], this.random.nextDouble(), 0.0);
//            return null;
//        }
//    }
//}
