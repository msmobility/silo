package com.pb.sawdust.util.abacus;

import com.pb.sawdust.util.Range;
import com.pb.sawdust.util.test.TestBase;
import static org.junit.Assert.*;
import static com.pb.sawdust.util.Range.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

/**
 * The {@code AbacusTest} ...
 *
 * @author crf <br/>
 *         Started 5/7/11 4:35 AM
 */
public class AbacusTest extends TestBase {
    public static final String REVERSE_KEY = "reverse key";

    public static void main(String ... args) {
        TestBase.main();
    }

    protected int[] dimensions;
    protected boolean reverse = false;

    protected void addSubDataTest(List<Class<? extends TestBase>> additionalClassContainer) {
        additionalClassContainer.add(AbacusFreshCloneTest.class);
    }

    protected List<Map<String,Object>> getClassContext() {
        List<Map<String,Object>> context = new LinkedList<Map<String,Object>>();
        context.add(buildContext(REVERSE_KEY,true));
        context.add(buildContext(REVERSE_KEY,false));
        return context;
    }

    protected Collection<Class<? extends TestBase>> getAdditionalTestClasses() {
        addClassRunContext(this.getClass(),getClassContext());
        List<Class<? extends TestBase>> adds = new LinkedList<Class<? extends TestBase>>();
        addSubDataTest(adds);
        adds.addAll(super.getAdditionalTestClasses());
        return adds;
    }

    protected Abacus getAbacus() {
        return new Abacus(reverse,dimensions);
    }

    protected void iterateAbacus(Abacus abacus, int nSteps) {
        for (int i : range(nSteps))
            abacus.next();
    }

    protected int[] getNthStep(int n) {
        //innefficient but obvious
        int[] step = new int[dimensions.length];
        Range r = reverse ? range(dimensions.length) : range(dimensions.length-1,-1);
        for (int i : range(n)) {
            for (int d : r) {
                if (++step[d] == dimensions[d])
                    step[d] = 0;
                 else
                    break;
            }
        }
        return step;
    }

    protected void setReverse() {
        reverse = (Boolean) getTestData(REVERSE_KEY);
    }

    @Before
    public void beforeTest() {
        setReverse();
        dimensions = new int[random.nextInt(3,5)];
        for (int i : range(dimensions.length))
            dimensions[i] = random.nextInt(2,4);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorFailureNegativeDimension() {
        dimensions[random.nextInt(dimensions.length)] = random.nextInt(-100,0);
        getAbacus();
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorFailureNoDimension() {
        dimensions = new int[0];
        getAbacus();
    }

    @Test
    public void testGetLength() {
        long length = 1L;
        for (int i : dimensions)
            length *= i;
        assertEquals(length,getAbacus().getLength());
    }

    @Test
    public void testGetStateCount() {
        long length = 1L;
        for (int i : dimensions)
            length *= i;
        assertEquals(length,getAbacus().getStateCount());
    }

    @Test
    public void testNext() {
        Abacus abacus = getAbacus();
        int point = abacus.getLength() == 1 ? 1 : random.nextInt(1,(int) abacus.getLength());
        iterateAbacus(abacus,point);
        assertArrayEquals(getNthStep(point),abacus.next());
    }

    @Test(expected=NoSuchElementException.class)
    public void testNextNoMoreElements() {
        Abacus abacus = getAbacus();
        iterateAbacus(abacus,(int) abacus.getLength());
        abacus.next();
    }

    @Test
    public void testHasNext() {
        Abacus abacus = getAbacus();
        iterateAbacus(abacus,random.nextInt((int) abacus.getLength())-1);
        assertTrue(abacus.hasNext());
    }

    @Test
    public void testHasNextFalse() {
        Abacus abacus = getAbacus();
        iterateAbacus(abacus,(int) abacus.getLength());
        assertFalse(abacus.hasNext());
    }

    @Test
    public void testGetAbacusPoint() {
        Abacus abacus = getAbacus();
        int point = random.nextInt((int) abacus.getLength());
        assertArrayEquals(getNthStep(point),abacus.getAbacusPoint(point));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetAbacusPointTooSmall() {
        getAbacus().getAbacusPoint(random.nextInt(-100,0));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetAbacusPointTooLarge() {
        Abacus abacus = getAbacus();
        abacus.getAbacusPoint(random.nextInt((int) abacus.getLength(),Integer.MAX_VALUE));
    }

    @Test
    public void testSetAbacusAtPosition() {
        Abacus abacus = getAbacus();
        iterateAbacus(abacus,random.nextInt((int) abacus.getLength())); //just to put it  in a new state
        int point = random.nextInt((int) abacus.getLength());
        abacus.setAbacusAtPosition(point);
        assertArrayEquals(getNthStep(point), abacus.next());
    }

    @Test
    public void testSetAbacusAtPosition0Edge() {
        Abacus abacus = getAbacus();
        iterateAbacus(abacus,random.nextInt((int) abacus.getLength())); //just to put it  in a new state
        abacus.setAbacusAtPosition(0);
        assertArrayEquals(getNthStep(0),abacus.next());
    }

    @Test
    public void testSetAbacusAtPositionLengthEdge() {
        Abacus abacus = getAbacus();
        iterateAbacus(abacus,random.nextInt((int) abacus.getLength())); //just to put it  in a new state
        abacus.setAbacusAtPosition(abacus.getLength()-1);
        assertArrayEquals(getNthStep((int) (abacus.getLength()-1)),abacus.next());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSetAbacusAtPositionTooSmall() {
        getAbacus().setAbacusAtPosition(random.nextInt(-100, 0));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSetAbacusAtPositionTooLarge() {
        Abacus abacus = getAbacus();
        abacus.setAbacusAtPosition(random.nextInt((int) abacus.getLength(),Integer.MAX_VALUE));
    }

    @Test
    public void testSetAbacusPoint() {
        Abacus abacus = getAbacus();
        iterateAbacus(abacus,random.nextInt(1,(int) abacus.getLength())); //just to put it  in a new state
        int point = random.nextInt((int) abacus.getLength()-1);
        int[] position = getNthStep(point);
        abacus.setAbacusPoint(position);
        assertArrayEquals(position,abacus.next());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSetAbacusPointTooSmall() {
        getAbacus().setAbacusPoint(new int[dimensions.length - 1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSetAbacusPointTooLarge() {
        getAbacus().setAbacusPoint(new int[dimensions.length + 1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSetAbacusPointBadPointTooSmall() {
        Abacus abacus = getAbacus();
        int point = random.nextInt((int) abacus.getLength()-1);
        int[] position = getNthStep(point);
        position[random.nextInt(dimensions.length)] = -1;
        getAbacus().setAbacusPoint(position);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSetAbacusPointBadPointTooBig() {
        Abacus abacus = getAbacus();
        int point = random.nextInt((int) abacus.getLength()-1);
        int[] position = getNthStep(point);
        int d = random.nextInt(dimensions.length);
        position[d] = dimensions[d];
        getAbacus().setAbacusPoint(position);
    }

    @Test
    public void testGetFreshClone() {
        Abacus abacus = getAbacus();
        iterateAbacus(abacus,random.nextInt(1,(int) abacus.getLength())); //just to put it  in a new state

    }

    public static class AbacusFreshCloneTest extends AbacusTest {
        protected AbacusTest parent;

        protected void addSubDataTest(List<Class<? extends TestBase>> additionalClassContainer) {
            addTestData(REVERSE_KEY,((AbacusTest) getCallingContextInstance()).getTestData(REVERSE_KEY));
        }

        @Override
        @Before
        public void beforeTest() {
            parent = (AbacusTest) getCallingContextInstance();
            parent.beforeTest();
            super.beforeTest();
            setAdditionalTestInformation(" (fresh clone)");
            dimensions = parent.dimensions;
            reverse = parent.reverse;
        }

        @Override
        protected Abacus getAbacus() {
            return parent.getAbacus().freshClone();
        }

        @Ignore
        @Test
        public void testConstructorFailureNegativeDimension() {}

        @Ignore
        @Test
        public void testConstructorFailureNoDimension() { }
    }

}
