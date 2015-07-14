package com.pb.sawdust.tensor.index;

import static com.pb.sawdust.util.Range.range;
import com.pb.sawdust.util.test.TestBase;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.ArrayTensor;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author crf <br/>
 *         Started: Feb 17, 2009 9:51:15 PM
 */
public class CollapsingIndexTest extends IndexTest<String> {
    public static final String INDEX_TYPE_KEY = "index type";

    private int indexType;
    private int[] referenceDimension;
    private int[][] effectiveReferenceIndices;
    private String[][] referenceIds;
    private int collapsedDimension;
    private int collapsedIndex;
    private int[] collapsedDimensions;
    private List<Integer> collapsedDimensionsList;

    public static void main(String ... args) {
        TestBase.main();
    }

    protected Collection<Class<? extends TestBase>> getAdditionalTestClasses() {
        List<Map<String,Object>> context = new LinkedList<Map<String,Object>>();
        for (int i : range(-1,4)) //-1 means index which is not collapsable
            context.add(buildContext(INDEX_TYPE_KEY,i));
        addClassRunContext(this.getClass(),context);
        return super.getAdditionalTestClasses();
    }


    protected String[][] getIds() {
        if (indexType < 1) {
            int collapseCount = 0;
            referenceIds = new String[random.nextInt(2,9)][];
            referenceDimension = new int[referenceIds.length];
            for (int i : range(referenceIds.length)) {
                int size = random.nextBoolean() ? 1 : random.nextInt(2,5);
                if (i == 0 || indexType == -1)
                    size = random.nextInt(2,5);
                else if (i == 1)
                    size = 1;
                String[] subIds = new String[size];
                referenceDimension[i] = subIds.length;
                if (subIds.length == 1)
                    collapseCount++;
                for (int j : range(subIds.length))
                    subIds[j] = random.nextAsciiString(random.nextInt(3,8));
                referenceIds[i] = subIds;
            }
            String[][] ids = new String[referenceIds.length-collapseCount][];
            int counter = 0;
            for (int i : range(referenceIds.length))
                if (referenceIds[i].length > 1)
                    ids[counter++] = referenceIds[i];
            return ids;
        } else if (indexType == 3) {
            collapsedDimensionsList = new LinkedList<Integer>();
            referenceIds = new String[random.nextInt(2,9)][];
            referenceDimension = new int[referenceIds.length];
            for (int i : range(referenceIds.length)) {
                String[] subIds;
                if (i > 0 && random.nextBoolean()) {
                    subIds = new String[1];
                    collapsedDimensionsList.add(i);
                } else {
                    subIds = new String[random.nextInt(2,5)];
                }
                referenceDimension[i] = subIds.length;
                for (int j : range(subIds.length))
                    subIds[j] = random.nextAsciiString(random.nextInt(3,8));
                referenceIds[i] = subIds;
            }
            collapsedDimensions = ArrayUtil.toPrimitive(collapsedDimensionsList.toArray(new Integer[collapsedDimensionsList.size()]));
            String[][] ids = new String[referenceIds.length- collapsedDimensions.length][];
            int counter = 0;
            for (int i : range(referenceIds.length))
                if (!collapsedDimensionsList.contains(i))
                    ids[counter++] = referenceIds[i];
            return ids;
        } else {
            referenceIds = new String[random.nextInt(2,9)][];
            referenceDimension = new int[referenceIds.length];
            for (int i : range(referenceIds.length)) {
                String[] subIds = new String[random.nextInt(2,5)];
                referenceDimension[i] = subIds.length;
                for (int j : range(subIds.length))
                    subIds[j] = random.nextAsciiString(random.nextInt(3,8));
                referenceIds[i] = subIds;
            }
            collapsedDimension = (indexType == 2) ? referenceIds.length-1 : random.nextInt(referenceIds.length);
            collapsedIndex = random.nextInt(referenceIds[collapsedDimension].length);
            String[][] ids = new String[referenceIds.length-1][];
            int counter = 0;
            for (int i : range(referenceIds.length))
                if (i != collapsedDimension)
                    ids[counter++] = referenceIds[i];
            return ids;
        }
    }

    protected int[][] getReferenceIndices(int[] dimensions) {
        int[][] rids = new int[referenceDimension.length][];
        for (int i : range(rids.length)) {
            int[] ids = new int[referenceDimension[i]];
            for (int j : range(ids.length)) {
                ids[j] = random.nextInt(5);
            }
            rids[i] = ids;
        }
        effectiveReferenceIndices = ArrayUtil.copyArray(rids);
        if (indexType > 0 && indexType < 3)
            //rids[collapsedDimension] = new int[] {effectiveReferenceIndices[collapsedDimension][collapsedIndex]};
            rids[collapsedDimension] = new int[] {collapsedIndex};
        return rids;
    }

    protected Index<String> getIndex(int[][] referenceIndices, String[][] ids) {
        Index<String> index = new BaseIndex<String>(effectiveReferenceIndices,referenceIds);
        switch (indexType) {
            case -1 : return new CollapsingIndex<String>(index);
            case 0 : return new CollapsingIndex<String>(index);
            case 1 : return new CollapsingIndex<String>(index,collapsedDimension,collapsedIndex);
            case 2 : return new CollapsingIndex<String>(index,collapsedIndex);
            case 3 : return new CollapsingIndex<String>(index,true, collapsedDimensions);
            default : return null;
        }
    }

    protected String getRandomId() {
        return random.nextAsciiString(9);
    }

    @Before
    public void beforeTest() {
        indexType = (Integer) getTestData(INDEX_TYPE_KEY);
        super.beforeTest();
    }

    @Test
    public void testGetIndexReference() {
        int randomDimension = random.nextInt(dimensions.length);
        int randomIndex = random.nextInt(dimensions[randomDimension]);
        assertEquals(randomIndex,index.getIndex(randomDimension,randomIndex));
//        int counter = 0;
//        int ref = 0;
//        for (int i : range(referenceIndices.length))
//            if (referenceIndices[i].length > 1)
//                if (counter++ == randomDimension)
//                    ref = referenceIndices[i][randomIndex];
//        assertEquals(ref,index.getIndex(randomDimension,randomIndex));
    }

    @Test
    public void testGetIndicesReference() {
        int[] randomIndices = new int[dimensions.length];
        for (int i : range(randomIndices.length))
            randomIndices[i] = random.nextInt(dimensions[i]);
        int[] randomRef = new int[referenceDimension.length];
        int counter = 0;
        for (int i : range(randomRef.length))
            if ((indexType < 1 && referenceDimension[i] != 1) || (indexType > 0 && indexType != 3 && i != collapsedDimension) || (indexType == 3 && !collapsedDimensionsList.contains(i)))
                //randomRef[i] = referenceIndices[i][randomIndices[counter++]];
                randomRef[i] = randomIndices[counter++];
            else if (indexType == 3 )
                randomRef[i] = 0;// referenceIndices[i][0];
            else if (indexType > 0)
                randomRef[i] =  collapsedIndex;
            else
                randomRef[i] = 0;
        assertArrayEquals(randomRef,index.getIndices(randomIndices));
    }

    @Test
    public void testIsValidForSuccess() {
        int[] referenceDims = new int[referenceDimension.length];
        for (int i : range(referenceDims.length))
            if (indexType > 0 && indexType < 3 &&  i == collapsedDimension)
                referenceDims[i] = collapsedIndex+1;
            else
                referenceDims[i] = referenceDimension[i];
//        for (int i : range(referenceDims.length))
//            for (int j : effectiveReferenceIndices[i])
//                if (referenceDims[i] <= j)
//                    referenceDims[i] = j+1;

        Tensor<?> m = ArrayTensor.getFactory().byteTensor(referenceDims);
        assertTrue(index.isValidFor(m));
    }

    @Test
    public void testIsValidForFailureDimensionsIncorrect() {
        int[] referenceDims = new int[referenceDimension.length+1];
        for (int i : range(referenceDimension.length))
            if (indexType > 0 && indexType < 3 &&  i == collapsedDimension)
                referenceDims[i] = collapsedIndex+1;
            else
                referenceDims[i] = referenceDimension[i];
//        int[] referenceDims = new int[effectiveReferenceIndices.length+1];
//        for (int i : range(effectiveReferenceIndices.length))
//            for (int j : effectiveReferenceIndices[i])
//                if (referenceDims[i] <= j)
//                    referenceDims[i] = j+1;
        referenceDims[referenceDims.length-1] = 1;
        Tensor<?> m = ArrayTensor.getFactory().byteTensor(referenceDims);
        assertFalse(index.isValidFor(m));
    }

    @Test
    public void testIsValidForFailureDimensionIncorrect() {
        int[] referenceDims = new int[referenceDimension.length];
        for (int i : range(referenceDims.length))
            if (indexType > 0 && indexType < 3 && i == collapsedDimension)
                referenceDims[i] = collapsedIndex+1;
            else
                referenceDims[i] = referenceDimension[i];
//        int[] referenceDims = new int[effectiveReferenceIndices.length];
//        for (int i : range(referenceDims.length))
//            for (int j : effectiveReferenceIndices[i])
//                if (referenceDims[i] <= j)
//                    referenceDims[i] = j+1;
        boolean t = true;
        while (t) {
            int r = random.nextInt(referenceDims.length);
            if (referenceDims[r] > 1) {
                referenceDims[r]--;
                t = false;
            }
        }
        Tensor<?> m = ArrayTensor.getFactory().byteTensor(referenceDims);
        assertFalse(index.isValidFor(m));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorExceptionDimensionTooLow() {
        switch (indexType) {
            case 1 : {
                Index<String> index = new BaseIndex<String>(effectiveReferenceIndices,referenceIds);
                new CollapsingIndex<String>(index,-1,0);
            } break;
            case 3 : {
                Index<String> index = new BaseIndex<String>(effectiveReferenceIndices,referenceIds);
                new CollapsingIndex<String>(index,true,-1);
            } break;
            default : throw new IllegalArgumentException("invalid test");
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorExceptionDimensionTooHigh() {
        switch (indexType) {
            case 1 : {
                Index<String> index = new BaseIndex<String>(effectiveReferenceIndices,referenceIds);
                new CollapsingIndex<String>(index,index.size(),0);
            } break;
            case 3 : {
                Index<String> index = new BaseIndex<String>(effectiveReferenceIndices,referenceIds);
                new CollapsingIndex<String>(index,true,index.size());
            } break;
            default : throw new IllegalArgumentException("invalid test");
        }
    }

    @SuppressWarnings("fallthrough")
    @Test(expected=IllegalArgumentException.class)
    public void testConstructorExceptionIndexTooLow() {
        if (indexType < 1 || indexType > 2)
            throw new IllegalArgumentException("invalid test");
        Index<String> index = new BaseIndex<String>(effectiveReferenceIndices,referenceIds);
        switch (indexType) {
            case 1 : new CollapsingIndex<String>(index,collapsedDimension,-1);
            case 2 : new CollapsingIndex<String>(index,-1);
        }
    }

    @SuppressWarnings("fallthrough")
    @Test(expected=IllegalArgumentException.class)
    public void testConstructorExceptionIndexTooHigh() {
        if (indexType < 1 || indexType > 2)
            throw new IllegalArgumentException("invalid test");
        Index<String> index = new BaseIndex<String>(effectiveReferenceIndices,referenceIds);
        switch (indexType) {
            case 1 : new CollapsingIndex<String>(index,collapsedDimension,effectiveReferenceIndices[collapsedDimension].length);
            case 2 : new CollapsingIndex<String>(index,effectiveReferenceIndices[collapsedDimension].length);
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorExceptionInvalidDimension() {
        if (indexType != 3)
            throw new IllegalArgumentException("invalid test");
        Index<String> index = new BaseIndex<String>(effectiveReferenceIndices,referenceIds);
        for (int i : range(index.size()))
            if (index.size(i) != 1) {
                new CollapsingIndex<String>(index,true,i);
                return;
            }
        throw new IllegalArgumentException("invalid test"); //possibly not testing, but generally will
    }
}
