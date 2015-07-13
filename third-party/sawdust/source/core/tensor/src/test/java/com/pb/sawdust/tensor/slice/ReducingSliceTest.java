package com.pb.sawdust.tensor.slice;

import org.junit.Test;
import com.pb.sawdust.util.test.TestBase;

/**
 * @author crf <br/>
 *         Started: Feb 27, 2009 12:59:42 PM
 */
public class ReducingSliceTest extends SliceTest {

    public static void main(String ... args) {
        TestBase.main();
    }

    protected int[] getIndices() {
        return new int[] {random.nextInt(100)};
    }

    protected Slice getSlice(int[] indices) {
        return ReducingSlice.reducingSlice(indices[0]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testReducingSliceFactoryNegativeIndex() {
        ReducingSlice.reducingSlice(-1);
    }
}
