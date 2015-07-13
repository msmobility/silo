package com.pb.sawdust.tensor.slice;

import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Test;

/**
 * @author crf <br/>
 *         Started: Feb 27, 2009 1:03:37 PM
 */
public class FullSliceTest extends SliceTest {

    public static void main(String ... args) {
        TestBase.main();
    }

    protected int[] getIndices() {
        return range(random.nextInt(4,35)).getRangeArray();
    }

    protected Slice getSlice(int[] indices) {
        return FullSlice.fullSlice(indices.length);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFullSliceFactoryZeroSize() {
        FullSlice.fullSlice(0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFullSliceFactoryNegativeSize() {
        FullSlice.fullSlice(-1);
    }
}
