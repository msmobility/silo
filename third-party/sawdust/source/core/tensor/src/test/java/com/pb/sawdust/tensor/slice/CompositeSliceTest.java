package com.pb.sawdust.tensor.slice;

import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.util.test.TestBase;
import com.pb.sawdust.util.array.ArrayUtil;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

/**
 * @author crf <br/>
 *         Started: Feb 27, 2009 1:10:44 PM
 */
public class CompositeSliceTest extends SliceTest {

    public static void main(String ... args) {
        TestBase.main();
    }

    protected CompositeSlice compositeSlice;
    protected Slice[] slices;

    @Before
    public void beforeTest() {
        super.beforeTest();
        compositeSlice = (CompositeSlice) slice;
    }

    protected int[] getIndices() {
        slices = new Slice[random.nextInt(4,10)];
        int size = 0;
        for (int i : range(slices.length)) {
            Slice s = SliceUtilTest.getRandomSlice();
            slices[i] = s;
            size += s.getSize();
        }
        int[] indices = new int[size];
        int counter = 0;
        for (Slice s : slices)
            for (int i : s)
                indices[counter++] = i;
        return indices;
    }

    protected Slice getSlice(int[] indices) {
        return new CompositeSlice(slices);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCompositeSliceConstructorNoSlice() {
        new CompositeSlice();
    }

    @Test
    public void testAddSlice() {
        Slice s = SliceUtilTest.getRandomSlice();
        int[] newIndices = ArrayUtil.concatenateArrays(indices,s.getSliceIndices());
        compositeSlice.addSlice(s);
        SliceUtilTest.runSliceTests(compositeSlice,newIndices);
    }

    @Test
    public void testAddSlicePosition() {
        int position = random.nextInt(slices.length+1);
        Slice s = SliceUtilTest.getRandomSlice();
        int[] newIndices = new int[slice.getSize()+s.getSize()];
        int counter = 0;
        int positionCounter = 0;
        int[] ind;
        if (position == 0) {
            ind = s.getSliceIndices();
            System.arraycopy(ind,0,newIndices,counter,ind.length);
            counter += ind.length;
        }
        for (Slice ss : slices) {
            ind = ss.getSliceIndices();
            System.arraycopy(ind,0,newIndices,counter,ind.length);
            counter += ind.length;
            positionCounter++;
            if (positionCounter == position) {
                ind = s.getSliceIndices();
                System.arraycopy(ind,0,newIndices,counter,ind.length);
                counter += ind.length;
            }
        }
        compositeSlice.addSlice(s,position);
        SliceUtilTest.runSliceTests(compositeSlice,newIndices);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddSlicePositionTooSmall() {
        Slice s = SliceUtilTest.getRandomSlice();
        compositeSlice.addSlice(s,-1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddSlicePositionTooBig() {
        Slice s = SliceUtilTest.getRandomSlice();
        compositeSlice.addSlice(s,slices.length+1);
    }

    @Test
    public void testRemoveSlice() {
        Slice s = slices[random.nextInt(slices.length)];
        //it removes the first slice, so the next stuff is ok
        int position = 0;
        for (int i : range(slices.length)) {
            if (s == slices[i]) {
                position = i;
                break;
            }
        }
        int[] newIndices = new int[indices.length-s.getSize()];
        int counter = 0;
        int positionCounter = 0;
        for (int i : range(slices.length)) {
            if (positionCounter++ == position)
                continue;
            int[] ind = slices[i].getSliceIndices();
            System.arraycopy(ind,0,newIndices,counter,ind.length);
            counter += ind.length;
        }
        compositeSlice.removeSlice(s);
        SliceUtilTest.runSliceTests(compositeSlice,newIndices);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testRemoveSliceUnknownSlice() {
        compositeSlice.removeSlice(new BaseSlice(SliceUtilTest.getRandomIndices()));
    }

    @Test(expected=IllegalStateException.class)
    public void testRemoveLastSlice() {
        Slice lastSlice = slices[slices.length-1];
        for (int i : range(slices.length-1))
            try {
                compositeSlice.removeSlice(0);
            } catch (IllegalStateException e) {
                return;
            }
        compositeSlice.removeSlice(lastSlice);
    }

    @Test
    public void testRemoveSlicePosition() {
        int position = random.nextInt(slices.length);
        Slice s = slices[position];
        int[] newIndices = new int[indices.length-s.getSize()];
        int counter = 0;
        int positionCounter = 0;
        for (int i : range(slices.length)) {
            if (positionCounter++ == position)
                continue;
            int[] ind = slices[i].getSliceIndices();
            System.arraycopy(ind,0,newIndices,counter,ind.length);
            counter += ind.length;
        }
        compositeSlice.removeSlice(position);
        SliceUtilTest.runSliceTests(compositeSlice,newIndices);
    }

    @Test (expected=IllegalArgumentException.class)
    public void testRemoveSlicePositionTooLow() {
        compositeSlice.removeSlice(-1);
    }

    @Test (expected=IllegalArgumentException.class)
    public void testRemoveSlicePositionTooHigh() {
        compositeSlice.removeSlice(slices.length);
    }

    @Test(expected=IllegalStateException.class)
    public void testRemoveLastSlicePosition() {
        for (int i : range(slices.length-1))
            try {
                compositeSlice.removeSlice(0);
            } catch (IllegalStateException e) {
                return;
            }
        compositeSlice.removeSlice(0);
    }

    @Test
    public void testGetSlices() {
        assertArrayEquals(slices,compositeSlice.getSlices());
    }
}
