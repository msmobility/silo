package com.pb.sawdust.tensor.index;

import static com.pb.sawdust.util.Range.range;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Test;

/**
 * @author crf <br/>
 *         Started: Feb 16, 2009 8:54:29 PM
 */
public class StandardIndexTest extends AbstractIndexTest<Integer> {
    public static void main(String ... args) {
        TestBase.main();
    }

    protected Integer[][] getIds() {
        Integer[][] ids = new Integer[random.nextInt(5,10)][];
        for (int i : range(ids.length)) {
            ids[i] = new Integer[random.nextInt(3,7)];
            for (int j : range(ids[i].length))
                ids[i][j] = j;
        }
        return ids;
    }

    protected int[][] getReferenceIndices(int[] dimensions) {
        int[][] rids = new int[dimensions.length][];
        for (int i : range(rids.length)) {
            int[] ids = new int[dimensions[i]];
            for (int j : range(ids.length))
                ids[j] = j;
            rids[i] = ids;
        }
        return rids;
    }

    protected Index<Integer> getIndex(int[][] referenceIndices, Integer[][] ids) {
        int[] dim = new int[referenceIndices.length];
        for (int i : range(dim.length))
            dim[i] = referenceIndices[i].length;
        return new StandardIndex(dim);
    }

    protected Integer getRandomId() {
        return random.nextInt(100,300);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBadDimensionConstructor() {
        int[] newDim = new int[dimensions.length];
        System.arraycopy(dimensions,0,newDim,0,dimensions.length);
        newDim[0] = -1;
        new StandardIndex(newDim);
    }
}
