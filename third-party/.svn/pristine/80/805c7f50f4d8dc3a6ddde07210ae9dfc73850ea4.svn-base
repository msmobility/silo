package com.pb.sawdust.tensor.index;

import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.util.test.TestBase;
import com.pb.sawdust.util.Range;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.ArrayTensor;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author crf <br/>
 *         Started: Feb 16, 2009 10:47:39 AM
 */
public class BaseIndexTest extends AbstractIndexTest<String> {
    public static final String CONSTRUCTOR_TYPE_KEY = "constructor type";

    protected int constructorType;

    public static void main(String ... args) {
        TestBase.main();
    }

    protected Collection<Class<? extends TestBase>> getAdditionalTestClasses() {
        List<Map<String,Object>> context = new LinkedList<Map<String,Object>>();
        for (int i : range(6))
            context.add(buildContext(CONSTRUCTOR_TYPE_KEY,i));
        addClassRunContext(this.getClass(),context);
        return super.getAdditionalTestClasses();
    }

    protected String[][] getIds() {
        String[][] ids = new String[random.nextInt(2,5)][];
        for (int i : range(ids.length)) {
            String[] subIds = new String[random.nextInt(2,5)];
            for (int j : range(subIds.length))
                subIds[j] = random.nextAsciiString(random.nextInt(3,8));
            ids[i] = subIds;
        }
        return ids;
    }

    protected int[][] getReferenceIndices(int[] dimensions) {
        int[][] rids = new int[dimensions.length][];
        for (int i : range(rids.length)) {
            int[] ids = new int[dimensions[i]];
            for (int j : range(ids.length)) {
                switch (constructorType) {
                    case 0 :
                    case 1 :
                    case 4 :
                    case 5 : ids[j] = j; break;
                    default : ids[j] = random.nextInt(5);
                }
            }
            rids[i] = ids;
        }
        return rids;
    }

    private Tensor<?> referenceTensor(String[][] ids) {
        int[] dimensions = new int[ids.length];
        for (int i : range(dimensions.length))
            dimensions[i] = ids[i].length;
        return ArrayTensor.getFactory().byteTensor(dimensions);
    }

    protected Index<String> getIndex(int[][] referenceIndices, String[][] ids) {
        switch (constructorType) {
            case 0 : return new BaseIndex<String>(IndexUtil.getIdList(ids));
            case 1 : return new BaseIndex<String>(ids);
            case 2 : return new BaseIndex<String>(referenceIndices,IndexUtil.getIdList(ids));
            case 3 : return new BaseIndex<String>(referenceIndices,ids);
            case 4 : return new BaseIndex<String>(referenceTensor(ids), IndexUtil.getIdList(ids));
            case 5 : return new BaseIndex<String>(referenceTensor(ids),ids);
            default : return null;
        }
    }

    @Before
    public void beforeTest() {
        constructorType = (Integer) getTestData(CONSTRUCTOR_TYPE_KEY);
        super.beforeTest();
    }

    protected String getRandomId() {
        return random.nextAsciiString(10);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBadIds() {
        String[][] ids = getIds();
        int rand = random.nextInt(ids.length);
        ids[rand][0] = ids[rand][1];
        getIndex(getReferenceIndices(getDimensionalityFromIds(ids)),ids);
    }

    @Test //not really a test - just making sure that no exceptions from empty (scalar) ids
    public void testEmptyIds() {
        String[][] ids = new String[0][];
        getIndex(getReferenceIndices(getDimensionalityFromIds(ids)),ids);
    }

    private int[] getDimensionalityFromIds(String[][] ids) {
        int[] dimensionality = new int[ids.length];
        for (int i : Range.range(dimensionality.length))
            dimensionality[i] = ids[i].length;
        return dimensionality;
    }

    @Test(expected=IllegalArgumentException.class)
    public void testEmptyId() {
        if (constructorType == 4 || constructorType == 5)
            throw new IllegalArgumentException("doesn't apply");
        String[][] ids = getIds();
        ids[0] = new String[0];
        getIndex(getReferenceIndices(getDimensionalityFromIds(ids)),ids);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBadReference() {
        if (constructorType != 2 && constructorType != 3)
            throw new IllegalArgumentException("doesn't apply");
        String[][] ids = getIds();
        int[][] rids = getReferenceIndices(getDimensionalityFromIds(ids));
        rids[0] = new int[rids[0].length+1];
        getIndex(rids,ids);
    }
}
