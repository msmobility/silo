package com.pb.sawdust.tensor.index;

import com.pb.sawdust.util.test.TestBase;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.assertEquals;

/**
 * @author crf <br/>
 *         Started: Jul 4, 2009 11:38:10 PM
 */
public class ScalarIndexTest extends IndexTest<String> {

    public static void main(String ... args) {
        TestBase.main();
    }

    @Override
    protected String[][] getIds() {
        return new String[0][0];
    }

    @Override
    protected int[][] getReferenceIndices(int[] dimensions) {
        return new int[0][0];
    }

    @Override
    protected Index<String> getIndex(int[][] referenceIndices, String[][] ids) {
        return ScalarIndex.getScalarIndex();
    }

    @Override
    protected String getRandomId() {
        return "";
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDimensionSize() {
        index.size(0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetIndex() {
        index.getIndex(0,"");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetIndexId() {
        index.getIndexId(0,0);
    }

    @Ignore @Test public void testGetIndicesIdsTooSmall() {}
    @Ignore @Test public void testGetIndicesIdInvalid() {}
    @Ignore @Test public void testGetIndexIdIndexTooSmall() {}
    @Ignore @Test public void testGetIndexIdIndexTooLarge() {}
    @Ignore @Test public void testGetIndexIdsIndexedDimensionsTooSmall() {}
    @Ignore @Test public void testGetIndexIdsIndexedIndexTooSmall() {}
    @Ignore @Test public void testGetIndexIdsIndexedIndexTooLarge() {}
    @Ignore @Test public void testGetIndexReference() {}
    @Ignore @Test public void testGetIndexReferenceIndexTooSmall() {}
    @Ignore @Test public void testGetIndexReferenceIndexTooLarge() {}
    @Ignore @Test public void testGetIndicesReferenceDimensionsTooSmall() {}
    @Ignore @Test public void testGetIndicesReferenceDimensionsTooLarge() {}
    @Ignore @Test public void testGetIndicesReferenceIndexTooSmall() {}
    @Ignore @Test public void testGetIndicesReferenceIndexTooLarge() {}
    @Ignore @Test public void testIsValidForFailureDimensionIncorrect() {}    


}
