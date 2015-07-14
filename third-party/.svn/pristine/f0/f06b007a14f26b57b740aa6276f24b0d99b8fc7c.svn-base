package com.pb.sawdust.tensor.index;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static com.pb.sawdust.util.Range.range;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.test.TestBase;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.ArrayTensor;

import java.util.*;

/**
 * @author crf <br/>
 *         Started: Sep 30, 2009 8:33:24 AM
 */
public class ExpandingIndexTest<I> extends IndexTest<I> {
    public static final String CONSTRUCTOR_TYPE_KEY = "constructor type";

    private int constructorType;
    private Index<String> baseIndex;
    private int[][] baseReferenceIndices;

    public static void main(String ... args) {
        TestBase.main();
    }

    protected Collection<Class<? extends TestBase>> getAdditionalTestClasses() {
        List<Map<String,Object>> context = new LinkedList<Map<String,Object>>();
        for (int i : range(2))
            context.add(buildContext(CONSTRUCTOR_TYPE_KEY,i));
        addClassRunContext(this.getClass(),context);
        return super.getAdditionalTestClasses();
    }

    @Before
    public void beforeTest() {
        constructorType = (Integer) getTestData(CONSTRUCTOR_TYPE_KEY);
        BaseIndexTest bit = new BaseIndexTest();
        String[][] ids = bit.getIds();
        int[] dimensions = new int[ids.length];
        for (int i : range(dimensions.length))
            dimensions[i] = ids[i].length;
        baseReferenceIndices = bit.getReferenceIndices(dimensions);
        baseIndex = new BaseIndex<String>(baseReferenceIndices,ids);
        super.beforeTest();
    }

    @Override
    @SuppressWarnings("unchecked") //I is Integer/String for the correct types
    protected I[][] getIds() {
        int additionalDimensions = random.nextInt(5);
        switch (constructorType) {
            case 0 : {
                Integer[][] ids = new Integer[baseIndex.size()+additionalDimensions][];
                for (int i : range(baseIndex.size()))
                    ids[i] = ArrayUtil.toIntegerArray(baseReferenceIndices[i]);
                for (int i : range(baseIndex.size(),ids.length))
                    ids[i] = new Integer[] {0};
                return (I[][]) ids;
            }
            default : {
                String[][] ids = new String[baseIndex.size()+additionalDimensions][];
                for (int i : range(baseIndex.size())) {
                    String[] id = new String[baseReferenceIndices[i].length];
                    for (int j : range(id.length))
                        id[j] = baseIndex.getIndexId(i,j);
                    ids[i] = id;
                }
                for (int i : range(baseIndex.size(),ids.length))
                    ids[i] = new String[] {(String) getRandomId()};
                return (I[][]) ids;
            }
        }
    }

    @Override
    protected int[][] getReferenceIndices(int[] dimensions) {
        int[][] ri = new int[dimensions.length][];
        System.arraycopy(baseReferenceIndices,0,ri,0,baseReferenceIndices.length);
        for (int i : range(baseReferenceIndices.length,ri.length))
            ri[i] = new int[] {0};
        return ri;
    }

    @Override
    @SuppressWarnings("unchecked") //cast for I is ok
    protected Index<I> getIndex(int[][] referenceIndices, I[][] ids) {
        switch (constructorType) {
            case 0 : return (Index<I>) ExpandingIndex.getStandardExpandingIndex(baseIndex,referenceIndices.length - baseIndex.size());
            default : {
                int is = baseIndex.size();
                String[] additionalIds = new String[ids.length - is];
                for (int i : range(is,ids.length))
                    additionalIds[i-is] = (String) ids[i][0];
                return (Index<I>) ExpandingIndex.getExpandingIndex(baseIndex,additionalIds);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked") //I is Integer/String for the correct types
    protected I getRandomId() {
        switch (constructorType) {
            case 0 : return (I) (Integer) random.nextInt(1000,2000); // out of range
            default : return (I) random.nextAsciiString(10);
        }
    }

    @Test
    public void testGetIndicesReference() {
        int[] randomIndices = new int[dimensions.length];
        for (int i : range(randomIndices.length))
            randomIndices[i] = random.nextInt(dimensions[i]);
        int[] baseIndices = new int[baseReferenceIndices.length];
        System.arraycopy(randomIndices,0,baseIndices,0,baseIndices.length);
        assertArrayEquals(baseIndex.getIndices(baseIndices), index.getIndices(randomIndices));
    }

    @Test
    public void testIsValidForSuccess() {
        int[] referenceDims = new int[baseReferenceIndices.length];
        for (int i : range(referenceDims.length))
            for (int j : baseReferenceIndices[i])
                if (referenceDims[i] <= j)
                    referenceDims[i] = j+1;
        Tensor<?> m = ArrayTensor.getFactory().byteTensor(referenceDims);
        assertTrue(index.isValidFor(m));
    }
}
