package com.pb.sawdust.util.abacus;

import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * The {@code RollingAbacusTest} ...
 *
 * @author crf <br/>
 *         Started 5/8/11 7:44 AM
 */
public class RollingAbacusTest extends AbacusTest {
    protected int rollingCount;

    public static void main(String ... args) {
        TestBase.main();
    }

    protected void addSubDataTest(List<Class<? extends TestBase>> additionalClassContainer) {
        additionalClassContainer.add(RollingAbacusFreshCloneTest.class);
    }

    protected int[] getNthStep(int n) {
        //first pull out rolling steps
        //get standard length
        int len = 1;
        for (int i : dimensions)
            len *= i;
        if (n >= len)
            n = n  % len;
        return super.getNthStep(n);
    }

    @Before
    public void beforeTest() {
        super.beforeTest();
        rollingCount = random.nextInt(1,10);
    }

    protected Abacus getAbacus() {
        return new RollingAbacus(reverse,rollingCount,dimensions);
    }

    @Test
    public void testGetLength() {
        long length = 1L;
        for (int i : dimensions)
            length *= i;
        assertEquals(length*rollingCount,getAbacus().getLength());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorFailureLowRollingCount() {
        rollingCount = random.nextInt(-100,1);
        getAbacus();
    }

    public static class RollingAbacusFreshCloneTest extends AbacusFreshCloneTest {
        protected int rollingCount;

        @Override
        @Before
        public void beforeTest() {
            super.beforeTest();
            rollingCount = ((RollingAbacusTest) parent).rollingCount;
        }

        @Ignore
        @Test
        public void testConstructorFailureLowRollingCount() { }

        @Test
        public void testGetLength() {
            long length = 1L;
            for (int i : dimensions)
                length *= i;
            assertEquals(length*rollingCount,getAbacus().getLength());
        }
    }

}
