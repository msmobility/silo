package com.pb.sawdust.util.abacus;

import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.pb.sawdust.util.Range.range;
import static org.junit.Assert.assertArrayEquals;

/**
 * The {@code LockShiftingAbacusTest} ...
 *
 * @author crf <br/>
 *         Started 5/11/11 9:15 AM
 */
public class LockShiftingAbacusTest extends AbacusTest {
    public static String LOCKSHIFT_TEST_STATE = "lockable test state";

    public static enum LockShiftingAbacusTestState {
        UNLOCKED,ONE_LOCKED,MULTIPLE_LOCKED
    }

    public static void main(String ... args) {
        TestBase.main();
    }

    protected int[] lockShiftDimensions;
    private LockShiftingAbacusTestState state;
    
    
    protected void addSubDataTest(List<Class<? extends TestBase>> additionalClassContainer) {
        additionalClassContainer.add(LockShiftingeAbacusFreshCloneTest.class);
    }

    protected List<Map<String,Object>> getClassContext() {
        List<Map<String,Object>> context = new LinkedList<Map<String,Object>>();
        for (LockShiftingAbacusTestState s : LockShiftingAbacusTestState.values())
            context.add(buildContext(LOCKSHIFT_TEST_STATE,s));
        return context;
    }

    @Override
    protected void setReverse() { }

    @Override
    @Before
    public void beforeTest() {
        super.beforeTest();
        state = (LockShiftingAbacusTestState) getTestData(LOCKSHIFT_TEST_STATE);
        switch ((LockShiftingAbacusTestState) getTestData(LOCKSHIFT_TEST_STATE)) {
            case UNLOCKED : {
                lockShiftDimensions = new int[0];
                break;
            }
            case ONE_LOCKED : {
                lockShiftDimensions = new int[] {random.nextInt(dimensions.length)};
                break;
            }
            case MULTIPLE_LOCKED : {
                lockShiftDimensions = new int[random.nextInt(2,dimensions.length)];
                Arrays.fill(lockShiftDimensions,-1);
                for (int i : range(lockShiftDimensions.length)) {
                    outer: while(true) {
                        int d = random.nextInt(dimensions.length);
                        for (int j : range(i))
                            if (lockShiftDimensions[j] == d)
                                continue outer;
                        lockShiftDimensions[i] = d;
                        break;
                    }
                }
                break;
            }
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorFailureRepeatedLockShift() {
        if (state == LockShiftingAbacusTestState.UNLOCKED ||
            state == LockShiftingAbacusTestState.ONE_LOCKED)
            throw new IllegalArgumentException(); //ignore
        lockShiftDimensions[0] = lockShiftDimensions[1];
        getAbacus();
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorFailureLockShiftTooSmall() {
        if (state == LockShiftingAbacusTestState.UNLOCKED)
            throw new IllegalArgumentException(); //ignore
        lockShiftDimensions[random.nextInt(lockShiftDimensions.length)] = random.nextInt(-100,0);
        getAbacus();
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorFailureLockShiftTooBig() {
        if (state == LockShiftingAbacusTestState.UNLOCKED)
            throw new IllegalArgumentException(); //ignore
        int d  = random.nextInt(lockShiftDimensions.length);
        lockShiftDimensions[d] = random.nextInt(dimensions.length,100);
        getAbacus();
    }


    protected int[] getNthStep(int n) {
        int[] step = new int[dimensions.length];
        for (int i : range(n))
            increment(step);
        return step;
    }

    private void increment(int[] step) {
        //first increment regulars
        boolean needLockShiftCycle = true;
        outer: for (int i : range(dimensions.length-1,-1)) {
            for (int ld : lockShiftDimensions)
                if (i == ld)
                    continue outer;
            step[i]++;
            if (step[i] == dimensions[i]) {
                step[i] = 0;
            } else {
                needLockShiftCycle = false;
                break;
            }
        }

        //now lockshifts
        if (needLockShiftCycle) {
            for (int d : lockShiftDimensions) {
                step[d]++;
                if (step[d] == dimensions[d])
                    step[d] = 0;
                else
                    break;
            }
        }
    }

    protected LockShiftingAbacus getAbacus() {
        return new LockShiftingAbacus(dimensions,lockShiftDimensions);
    }



    public static class LockShiftingeAbacusFreshCloneTest extends AbacusFreshCloneTest {
        protected int[] lockShiftDimensions;

        protected int[] getNthStep(int n) {
            return parent.getNthStep(n);
        }

        protected void addSubDataTest(List<Class<? extends TestBase>> additionalClassContainer) { }

        protected void setReverse() {}

        protected List<Map<String,Object>> getClassContext() {
            List<Map<String,Object>> context = new LinkedList<Map<String,Object>>();
            for (LockShiftingAbacusTestState s : LockShiftingAbacusTestState.values()) {
                context.add(buildContext(LOCKSHIFT_TEST_STATE,s));
            }
            return context;
        }

        @Override
        @Before
        public void beforeTest() {
            super.beforeTest();
            lockShiftDimensions = ((LockShiftingAbacusTest) parent).lockShiftDimensions;
        }
    }
}
