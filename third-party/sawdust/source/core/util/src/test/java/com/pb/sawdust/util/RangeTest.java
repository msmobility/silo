package com.pb.sawdust.util;

import com.pb.sawdust.util.test.TestBase;
import com.pb.sawdust.util.array.ArrayUtil;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.List;
import java.util.LinkedList;


/**
 * @author crf <br/>
 *         Started: Jul 18, 2008 4:45:47 PM
 */
public class RangeTest extends TestBase {

    public static void main(String ... args) {
        TestBase.main();
    }
    
    public void testRangeIncrements(int increment) {
        boolean correct = true;
        int last = 0;
        boolean first = true;
        for (int i : new Range(last,last + 2*increment,increment)) {
            if (first) {
                last = i;
                first = false;
            } else {
                correct &= i - last == increment;
                last = i;
            }
        }
        assertTrue(correct);
    }

    @Test
    public void testRangeIncrementOne() {
        testRangeIncrements(1);
    }

    @Test
    public void testRangeIncrementMoreThanOne() {
        testRangeIncrements(3);
    }

    @Test
    public void testRangeIncrementNegativeOne() {
        testRangeIncrements(-1);
    }

    @Test
    public void testRangeIncrementLessThanNegativeOne() {
        testRangeIncrements(-4);
    }

    @Test
    public void testRangeIncorrectModulus() {
        int value = 0;
        for (int i : new Range(0,9,3)) {
            value = i;
        }
        assertEquals(6,value);
    }

    @Test
    public void testRangeIncorrectModulusNegative() {
        int value = 0;
        for (int i : new Range(0,-9,-3)) {
            value = i;
        }
        assertEquals(-6,value);
    }

    @Test(expected= IllegalArgumentException.class)
    public void testRangeInvalidStepSizeZero() {
        new Range(0,1,0);
    }

    @Test(expected= IllegalArgumentException.class)
    public void testRangeInvalidStepSizePositive() {
        new Range(1,0,1);
    }

    @Test(expected= IllegalArgumentException.class)
    public void testRangeInvalidStepSizeNegative() {
        new Range(0,1,-1);
    }

    @Test
    public void testRangeArray() {
        //tests equality between range array and range iterator, then we can use range array to check iterations
        int start = 0;
        int end = 12;
        int step = 2;
        int[] rangeArray = new Range(start,end,step).getRangeArray();
        List<Integer> rangeSpan = new LinkedList<Integer>();
        for (int i : new Range(start,end,step))
            rangeSpan.add(i);
        assertArrayEquals(ArrayUtil.toPrimitive(rangeSpan.toArray(new Integer[rangeSpan.size()])),rangeArray);
    }

    @Test
    public void testRangeSizePositive() {
        int start = 0;
        int end = 5;
        int step = 1;
        int[] rangeArray = new Range(start,end,step).getRangeArray();
        assertEquals(end-start,rangeArray.length);
    }

    @Test
    public void testRangeSizeNegative() {
        int start = 5;
        int end = 0;
        int step = -1;
        int[] rangeArray = new Range(start,end,step).getRangeArray();
        assertEquals(start-end,rangeArray.length);
    }

    @Test
    public void testRangeDefaultStepPositive() {
        int start = 0;
        int end = 5;
        int[] rangeArray = new Range(start,end).getRangeArray();
        assertEquals(1,rangeArray[1] - rangeArray[0]);
    }

    @Test
    public void testRangeDefaultStepNegative() {
        int start = 5;
        int end = 0;
        int[] rangeArray = new Range(start,end).getRangeArray();
        assertEquals(-1,rangeArray[1] - rangeArray[0]);
    }

    @Test
    public void testRangeDefaultStartPositive() {
        int end = 5;
        int[] rangeArray = new Range(end).getRangeArray();
        assertEquals(0,rangeArray[0]);
    }

    @Test
    public void testRangeDefaultStartPositiveStep() {
        int end = 5;
        int[] rangeArray = new Range(end).getRangeArray();
        assertEquals(1,rangeArray[1] - rangeArray[0]);
    }

    @Test
    public void testRangeDefaultStartNegative() {
        int end = -5;
        int[] rangeArray = new Range(end).getRangeArray();
        assertEquals(0,rangeArray[0]);
    }

    @Test
    public void testRangeDefaultStartNegativeStep() {
        int end = -5;
        int[] rangeArray = new Range(end).getRangeArray();
        assertEquals(-1,rangeArray[1] - rangeArray[0]);
    }
}
