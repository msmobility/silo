package com.pb.sawdust.tensor.index;

import com.pb.sawdust.util.test.TestBase;
import static com.pb.sawdust.util.Range.range;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author crf <br/>
 *         Started: Feb 18, 2009 3:51:07 PM
 */
public class IndexUtilTest extends TestBase {
    
    public static void main(String ... args) {
        TestBase.main();
        IndexUtilIndexTest.main();
    }

    @Test
    public void testGetIdList() {
        String[][] ids = new String[random.nextInt(2,8)][];
        List<List<String>> idList = new LinkedList<List<String>>();
        for (int i : range(ids.length)) {
            List<String> idl = new LinkedList<String>();
            String[] ida = new String[random.nextInt(3,7)];
            for (int j : range(ida.length)) {
                String rs = random.nextAsciiString(random.nextInt(4,9));
                idl.add(rs);
                ida[j] = rs;
            }
            idList.add(idl);
            ids[i] = ida;
        }
        assertEquals(idList,IndexUtil.getIdList(ids));
    }

    public static class IndexUtilIndexTest extends IndexTest<String> {
        public static final String INDEX_TYPE_KEY = "index type";
        
        private int indexType;

        public static void main(String ... args) {
            TestBase.main();
        }

        protected Collection<Class<? extends TestBase>> getAdditionalTestClasses() {
            List<Map<String,Object>> context = new LinkedList<Map<String,Object>>();
            for (int i : range(2))
                context.add(buildContext(INDEX_TYPE_KEY,i));
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
                for (int j : range(ids.length))
                    ids[j] = random.nextInt(5);
                rids[i] = ids;
            }
            return rids;
        }

        protected Index<String> getIndex(int[][] referenceIndices, String[][] ids) {
            Float[][] intIds = new Float[ids.length][];
            for (int i : range(intIds.length)) {
                Float[] subIds = new Float[ids[i].length];
                for (int j : range(subIds.length))
                    subIds[j] = random.nextFloat();
                intIds[i] = subIds;
            }
            Index<Float> index = new BaseIndex<Float>(referenceIndices,intIds);
            switch (indexType) {
                case 0 : return IndexUtil.getIdIndex(index,ids);
                case 1 : return IndexUtil.getIdIndex(index,IndexUtil.getIdList(ids));
                default : return null;
            }

        }

        protected String getRandomId() {
            return random.nextAsciiString(10);
        }

        @Before
        public void beforeTest() {
            indexType = (Integer) getTestData(INDEX_TYPE_KEY);
            super.beforeTest();
        }

        @Test(expected=IllegalArgumentException.class)
        public void testConstructorFailureInvalidDimensions() {
            Float[][] intIds = new Float[ids.length+1][];
            for (int i : range(ids.length)) {
                Float[] subIds = new Float[ids[i].length];
                for (int j : range(subIds.length))
                    subIds[j] = random.nextFloat();
                intIds[i] = subIds;
            }
            intIds[intIds.length-1] = new Float[] {4.5f,5.6f};
            Index<Float> index = new BaseIndex<Float>(referenceIndices,intIds);
            switch (indexType) {
                case 0 : IndexUtil.getIdIndex(index,ids); break;
                case 1 : IndexUtil.getIdIndex(index,IndexUtil.getIdList(ids)); break;
            }
        }

        @Test(expected=IllegalArgumentException.class)
        public void testConstructorFailureInvalidDimension() {
            int randomDimension = random.nextInt(ids.length);
            Float[][] intIds = new Float[ids.length][];
            for (int i : range(ids.length)) {
                int change = i == randomDimension ? random.nextInt(1,4) : 0;
                Float[] subIds = new Float[ids[i].length+change];
                for (int j : range(subIds.length))
                    subIds[j] = random.nextFloat();
                intIds[i] = subIds;
            }
            Index<Float> index = new BaseIndex<Float>(referenceIndices,intIds);
            switch (indexType) {
                case 0 : IndexUtil.getIdIndex(index,ids); break;
                case 1 : IndexUtil.getIdIndex(index,IndexUtil.getIdList(ids)); break;
            }
        }
    }
}
