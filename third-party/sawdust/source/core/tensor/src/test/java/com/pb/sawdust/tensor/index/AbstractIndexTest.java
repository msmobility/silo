package com.pb.sawdust.tensor.index;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * @author crf <br/>
 *         Started: Feb 16, 2009 8:51:55 AM
 */
public abstract class AbstractIndexTest<I> extends IndexTest<I> {
    protected AbstractIndex<I> abstractIndex;

    protected abstract Index<I> getIndex(int[][] referenceIndices, I[][] ids);

    @Before
    public void beforeTest() {
        super.beforeTest();
        abstractIndex = (AbstractIndex<I>) index;
    }

    @Test
    public void testDimensions() {
        assertArrayEquals(dimensions,abstractIndex.dimensions);
    }

//    @Test
//    public void testCheckIndices() {
//        //no asserts - just no exceptions should be thrown
//        int[] indices = new int[dimensions.length];
//        for (int i : range(dimensions.length))
//            indices[i] = random.nextInt(dimensions[i]);
//        abstractIndex.checkIndices(indices);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testCheckIndicesDimensionTooSmall() {
//        abstractIndex.checkIndices(new int[dimensions.length-1]);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testCheckIndicesDimensionTooLarge() {
//        abstractIndex.checkIndices(new int[dimensions.length+1]);
//    }
//
//    @Test(expected=IndexOutOfBoundsException.class)
//    public void testCheckIndicesIndexTooSmall() {
//        int[] indices = new int[dimensions.length];
//        indices[random.nextInt(dimensions.length)] = -1;
//        abstractIndex.checkIndices(indices);
//    }
//
//    @Test(expected=IndexOutOfBoundsException.class)
//    public void testCheckIndicesIndexTooLarge() {
//        int[] indices = new int[dimensions.length];
//        int rand = random.nextInt(dimensions.length);
//        indices[rand] = dimensions[rand];
//        abstractIndex.checkIndices(indices);
//    }

    @Test
    public void testCheckIndex() {
        //no asserts - just no exceptions should be thrown
        int randomDimension = random.nextInt(dimensions.length);
        abstractIndex.checkIndex(randomDimension,random.nextInt(dimensions[randomDimension]));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCheckIndexDimensionTooLow() {
        abstractIndex.checkIndex(-1,0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCheckIndexDimensionTooHigh() {
        abstractIndex.checkIndex(dimensions.length,0);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testCheckIndexIndexTooLow() {
        abstractIndex.checkIndex(random.nextInt(dimensions.length),-1);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testCheckIndexIndexTooHigh() {
        int randomDimension = random.nextInt(dimensions.length);
        abstractIndex.checkIndex(randomDimension,dimensions[randomDimension]);
    }
}
