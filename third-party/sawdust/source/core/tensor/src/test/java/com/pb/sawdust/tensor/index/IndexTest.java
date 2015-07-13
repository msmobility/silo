package com.pb.sawdust.tensor.index;

import com.pb.sawdust.util.test.TestBase;
import com.pb.sawdust.util.array.ArrayUtil;
import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.ArrayTensor;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;
import java.lang.reflect.Array;

/**
 * @author crf <br/>
 *         Started: Feb 15, 2009 11:08:59 PM
 */
public abstract class IndexTest<I> extends TestBase {
    protected int[] dimensions;
    protected int[][] referenceIndices;
    protected I[][] ids;
    protected Index<I> index;

    protected abstract I[][] getIds();
    protected abstract int[][] getReferenceIndices(int[] dimensions);
    protected abstract Index<I> getIndex(int[][] referenceIndices, I[][] ids);
    protected abstract I getRandomId();

    @Before
    public void beforeTest() {
        ids = getIds();
        dimensions = new int[ids.length];
        for (int i : range(dimensions.length))
            dimensions[i] = ids[i].length;
        referenceIndices = getReferenceIndices(dimensions);
        index = getIndex(referenceIndices,ids);
    }

    @Test
    public void testSize() {
        assertEquals(dimensions.length,index.size());
    }

    @Test
    public void testDimensionSize() {
        int randomDimension = random.nextInt(dimensions.length);
        assertEquals(dimensions[randomDimension],index.size(randomDimension));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDimensionSizeTooLow() {
        index.size(-1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDimensionSizeToHigh() {
        index.size(dimensions.length);
    }

    @Test
    public void testGetDimensions() {
        assertArrayEquals(dimensions,index.getDimensions());
    }

    @Test
    public void testGetIndexIds() {
        List<List<I>> idList = new LinkedList<List<I>>();
        for (I[] idArray : ids) {
            List<I> idl = new LinkedList<I>();
            idl.addAll(Arrays.asList(idArray));
            idList.add(idl);
        }
        assertEquals(idList,index.getIndexIds());
    }

    @Test
    public void testGetIndex() {
        int randomDimension = random.nextInt(dimensions.length);
        int randomIndex = random.nextInt(dimensions[randomDimension]);
        assertEquals(randomIndex,index.getIndex(randomDimension,ids[randomDimension][randomIndex]));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetIndexDimensionTooLow() {
        index.getIndex(-1,0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetIndexDimensionTooHigh() {
        index.getIndex(dimensions.length,0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetIndexIdNotFound() {
        index.getIndex(0,getRandomId());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetIndices() {
        int[] randomIndices = new int[dimensions.length];
        I[] randomIds = (I[]) Array.newInstance(ArrayUtil.getBaseComponentType(ids),randomIndices.length);
        for (int i : range(randomIndices.length)) {
            randomIndices[i] = random.nextInt(dimensions[i]);
            randomIds[i] = ids[i][randomIndices[i]];
        }
        assertArrayEquals(randomIndices,index.getIndices(randomIds));
    }

    @Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("unchecked")
    public void testGetIndicesIdsTooSmall() {
        I[] randomIds = (I[]) Array.newInstance(ArrayUtil.getBaseComponentType(ids),dimensions.length-1);
        for (int i : range(randomIds.length))
            randomIds[i] = ids[i][random.nextInt(dimensions[i])];
        index.getIndices(randomIds);
    }

    @Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("unchecked")
    public void testGetIndicesIdsTooLarge() {
        I[] randomIds = (I[]) Array.newInstance(ArrayUtil.getBaseComponentType(ids),dimensions.length+1);
        for (int i : range(randomIds.length-1))
            randomIds[i] = ids[i][random.nextInt(dimensions[i])];
        randomIds[randomIds.length-1] = getRandomId();
        index.getIndices(randomIds);
    }

    @Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("unchecked")
    public void testGetIndicesIdInvalid() {
        I[] randomIds = (I[]) Array.newInstance(ArrayUtil.getBaseComponentType(ids),dimensions.length);
        for (int i : range(randomIds.length-1))
            randomIds[i] = ids[i][random.nextInt(dimensions[i])];
        randomIds[randomIds.length-1] = getRandomId();
        index.getIndices(randomIds);
    }

    @Test
    public void testGetIndexId() {
        int randomDimension = random.nextInt(dimensions.length);
        int randomIndex = random.nextInt(dimensions[randomDimension]);
        assertEquals(ids[randomDimension][randomIndex],index.getIndexId(randomDimension,randomIndex));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetIndexIdDimensionTooSmall() {
        index.getIndexId(-1,0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetIndexIdDimensionTooLarge() {
        index.getIndexId(dimensions.length,0);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testGetIndexIdIndexTooSmall() {
        int randomDimension = random.nextInt(dimensions.length);
        index.getIndexId(randomDimension,-1);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testGetIndexIdIndexTooLarge() {
        int randomDimension = random.nextInt(dimensions.length);
        index.getIndexId(randomDimension,dimensions[randomDimension]);
    }

    @Test
    public void testGetIndexIdsIndexed() {
        int[] randomIndices = new int[dimensions.length];
        List<I> randomIds = new LinkedList<I>();
        for (int i : range(randomIndices.length)) {
            randomIndices[i] = random.nextInt(dimensions[i]);
            randomIds.add(ids[i][randomIndices[i]]);
        }
        assertEquals(randomIds,index.getIndexIds(randomIndices));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetIndexIdsIndexedDimensionsTooSmall() {
        int[] randomIndices = new int[dimensions.length-1];
        index.getIndexIds(randomIndices);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetIndexIdsIndexedDimensionsTooLarge() {
        int[] randomIndices = new int[dimensions.length+1];
        index.getIndexIds(randomIndices);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testGetIndexIdsIndexedIndexTooSmall() {
        int[] randomIndices = new int[dimensions.length];
        randomIndices[random.nextInt(dimensions.length)] = -1;
        index.getIndexIds(randomIndices);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testGetIndexIdsIndexedIndexTooLarge() {
        int[] randomIndices = new int[dimensions.length];
        int rand = random.nextInt(dimensions.length);
        randomIndices[rand] = dimensions[rand];
        index.getIndexIds(randomIndices);
    }

    @Test
    public void testGetIndexReference() {
        int randomDimension = random.nextInt(dimensions.length);
        int randomIndex = random.nextInt(dimensions[randomDimension]);
        assertEquals(referenceIndices[randomDimension][randomIndex],index.getIndex(randomDimension,randomIndex));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetIndexReferenceDimensionTooSmall() {
        index.getIndex(-1,0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetIndexReferenceDimensionTooLarge() {
        index.getIndex(dimensions.length,0);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testGetIndexReferenceIndexTooSmall() {
        int randomDimension = random.nextInt(dimensions.length);
        index.getIndex(randomDimension,-1);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testGetIndexReferenceIndexTooLarge() {
        int randomDimension = random.nextInt(dimensions.length);
        index.getIndex(randomDimension,dimensions[randomDimension]);
    }

    @Test
    public void testGetIndicesReference() {
        int[] randomIndices = new int[dimensions.length];
        int[] randomRef = new int[referenceIndices.length];
        for (int i : range(randomIndices.length)) {
            randomIndices[i] = random.nextInt(dimensions[i]);
            randomRef[i] = referenceIndices[i][randomIndices[i]];
        }
        assertArrayEquals(randomRef,index.getIndices(randomIndices));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetIndicesReferenceDimensionsTooSmall() {
        int[] randomIndices = new int[dimensions.length-1];
        index.getIndices(randomIndices);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetIndicesReferenceDimensionsTooLarge() {
        int[] randomIndices = new int[dimensions.length+1];
        index.getIndices(randomIndices);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testGetIndicesReferenceIndexTooSmall() {
        int[] randomIndices = new int[dimensions.length];
        randomIndices[random.nextInt(dimensions.length)] = -1;
        index.getIndices(randomIndices);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testGetIndicesReferenceIndexTooLarge() {
        int[] randomIndices = new int[dimensions.length];
        int rand = random.nextInt(dimensions.length);
        randomIndices[rand] = dimensions[rand];
        index.getIndices(randomIndices);
    }

    @Test
    public void testIsValidForSuccess() {
        int[] referenceDims = new int[referenceIndices.length];
        for (int i : range(referenceDims.length))
            for (int j : referenceIndices[i])
                if (referenceDims[i] <= j)
                    referenceDims[i] = j+1;
        Tensor<?> m = ArrayTensor.getFactory().byteTensor(referenceDims);
        assertTrue(index.isValidFor(m));
    }

    @Test
    public void testIsValidForFailureDimensionsIncorrect() {
        int[] referenceDims = new int[referenceIndices.length+1];
        for (int i : range(referenceIndices.length))
            for (int j : referenceIndices[i])
                if (referenceDims[i] <= j)
                    referenceDims[i] = j+1;
        referenceDims[referenceDims.length-1] = 1;
        Tensor<?> m = ArrayTensor.getFactory().byteTensor(referenceDims);
        assertFalse(index.isValidFor(m));
    }

    @Test
    public void testIsValidForFailureDimensionIncorrect() {
        int[] referenceDims = new int[referenceIndices.length];
        for (int i : range(referenceDims.length))
            for (int j : referenceIndices[i])
                if (referenceDims[i] <= j)
                    referenceDims[i] = j+1;
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

}
