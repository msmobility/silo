package com.pb.sawdust.tensor.index;

import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.util.test.TestBase;
import com.pb.sawdust.tensor.slice.Slice;
import com.pb.sawdust.tensor.slice.BaseSlice;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author crf <br/>
 *         Started: Mar 1, 2009 12:06:01 PM
 */
public class SliceIndexTest<I> extends AbstractIndexTest<I> {
    public static final String CONSTRUCTOR_TYPE_KEY = "constructor type";

    private int constructorType;
    private Index<?> baseIndex;
    private Slice[] slices;
    private int[][] referenceIndices;

    public static void main(String ... args) {
        TestBase.main();
    }

    protected Collection<Class<? extends TestBase>> getAdditionalTestClasses() {
        List<Map<String,Object>> context = new LinkedList<Map<String,Object>>();
        for (int i : range(4))
            context.add(buildContext(CONSTRUCTOR_TYPE_KEY,i));
        addClassRunContext(this.getClass(),context);
        return super.getAdditionalTestClasses();
    }

    private Float[][] getUnusedIds(int[] dimensions) {
        Float[][] ids = new Float[dimensions.length][];
        for (int i : range(ids.length)) {
            Float[] subIds = new Float[dimensions[i]];
            for (int j : range(subIds.length))
                subIds[j] = random.nextFloat();
            ids[i] = subIds;
        }
        return ids;
    }

    private Integer[][] getDefaultIds(int[] dimensions) {
        Integer[][] ids = new Integer[dimensions.length][];
        for (int i : range(ids.length)) {
            Integer[] subIds = new Integer[dimensions[i]];
            for (int j : range(subIds.length))
                subIds[j] = j;
            ids[i] = subIds;
        }
        return ids;
    }

    private String[][] getStringIds(int[] dimensions) {
        String[][] ids = new String[dimensions.length][];
        for (int i : range(ids.length)) {
            String[] subIds = new String[dimensions[i]];
            for (int j : range(subIds.length))
                subIds[j] = random.nextAsciiString(random.nextInt(3,8));
            ids[i] = subIds;
        }
        return ids;
    }

    @SuppressWarnings("unchecked") //doing funny stuff with erasure
    protected I[][] getIds() {
        int[] dimensions = new int[random.nextInt(3,8)];
        for (int i : range(dimensions.length))
            dimensions[i] = random.nextInt(3,8);    
        //set reference ids
        int[][] treferenceIndices = new int[dimensions.length][];
        for (int i : range(treferenceIndices.length)) {
            int[] ids = new int[dimensions[i]];
            for (int j : range(ids.length))
                ids[j] = random.nextInt(5);
            treferenceIndices[i] = ids;
        }
        switch (constructorType) {
            case 0 : baseIndex = new BaseIndex<String>(treferenceIndices,getStringIds(dimensions)); break;
            default : baseIndex = new BaseIndex<Float>(treferenceIndices,getUnusedIds(dimensions));
        }
        slices = new Slice[dimensions.length];
        int[] newDimensions = new int[dimensions.length];
        String[][] case0Ids = new String[dimensions.length][];
        for (int i : range(slices.length)) {
            switch (constructorType) {
                case 0 : {
                    int[] randomizedIndices = random.getRandomIndices(dimensions[i]);
                    int randomSize = random.nextInt(2,dimensions[i]);
                    int[] slice = new int[randomSize];
                    System.arraycopy(randomizedIndices,0,slice,0,slice.length);
                    slices[i] = new BaseSlice(slice);
                    case0Ids[i] = new String[randomSize];
                    for (int j : range(randomSize))
                        case0Ids[i][j] = (String) baseIndex.getIndexId(i,randomizedIndices[j]);
                    break;
                }
                default : {
                    newDimensions[i] = random.nextInt(2,8);
                    int[] slice = new int[newDimensions[i]];
                    for (int j : range(slice.length))
                        slice[j] = random.nextInt(dimensions[i]);
                    slices[i] = new BaseSlice(slice);
                }
            }
        }
        referenceIndices = new int[dimensions.length][];
        for (int i : range(referenceIndices.length)) {
            int[] ind = new int[slices[i].getSize()];
            for (int j : range(ind.length))
                ind[j] = slices[i].getValueAt(j);
            referenceIndices[i] = ind;
        }
        switch(constructorType) {
            case 0 : return (I[][]) case0Ids;
            case 3 : return (I[][]) getDefaultIds(newDimensions);
            default : return (I[][]) getStringIds(newDimensions);
        }
    }

    protected int[][] getReferenceIndices(int[] dimensions) {
        return referenceIndices;
    }

    protected Index<I> getIndex(int[][] referenceIndices, I[][] ids) {
        return getIndex(slices,ids);
    }

    @Before
    public void beforeTest() {
        constructorType = (Integer) getTestData(CONSTRUCTOR_TYPE_KEY);
        super.beforeTest();
    }

    @SuppressWarnings("unchecked") //doing funny stuff with erasure
    private Index<I> getIndex(Slice[] slices, I[][] ids) {
        switch (constructorType) {
            case 0 : return (SliceIndex<I>) SliceIndex.getSliceIndex(baseIndex,slices);
            case 1 : return SliceIndex.getSliceIndex(baseIndex,IndexUtil.getIdList(ids),slices);
            case 2 : return SliceIndex.getSliceIndex(baseIndex,ids,slices);
            case 3 : return (SliceIndex<I>) SliceIndex.getDefaultIdSliceIndex(baseIndex,slices);
            default : return null;
        }
    }

    @SuppressWarnings("unchecked") //doing funny stuff with erasure
    protected I getRandomId() {
        switch (constructorType){
            case 3 : return (I) (Integer) random.nextInt(1000,2000); // out of range
            default : return (I) random.nextAsciiString(10); 
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorFailureSliceCountTooLow() {
        Slice[] wrongSlices = new Slice[slices.length-1];
        System.arraycopy(slices,0,wrongSlices,0,wrongSlices.length);
        getIndex(wrongSlices,ids);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorFailureSliceCountTooHigh() {
        Slice[] wrongSlices = new Slice[slices.length+1];
        System.arraycopy(slices,0,wrongSlices,0,slices.length);
        wrongSlices[wrongSlices.length-1] = new BaseSlice(0);
        getIndex(wrongSlices,ids);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorFailureSliceRepetition() {
        Slice[] wrongSlices = new Slice[slices.length];
        System.arraycopy(slices,0,wrongSlices,0,wrongSlices.length);
        int randomDimension = random.nextInt(slices.length);
        int[] indices = new int[slices[randomDimension].getSize()+1];
        System.arraycopy(slices[randomDimension].getSliceIndices(),0,indices,0,indices.length-1);
        indices[indices.length-1] = indices[0];
        wrongSlices[randomDimension] = new BaseSlice(indices);
        switch(constructorType) {
            case 0 : getIndex(wrongSlices,ids); break;
            default : throw new IllegalArgumentException("invalid test");
        }
    }

    @SuppressWarnings("unchecked") //ok, id is already I[][] instance
    @Test(expected=IllegalArgumentException.class)
    public void testConstructorFailureIdDimensionCountTooSmall() {
        I[][] wrongIds = (I[][]) Array.newInstance(ids.getClass().getComponentType(),ids.length-1);
        System.arraycopy(ids,0,wrongIds,0,wrongIds.length);
        switch(constructorType) {
            case 1 :
            case 2 : getIndex(slices,wrongIds); break;
            default : throw new IllegalArgumentException("invalid test");
        }
    }

    @SuppressWarnings("unchecked") //ok, id is already I[][] instance
    @Test(expected=IllegalArgumentException.class)
    public void testConstructorFailureIdDimensionCountTooBig() {
        I[][] wrongIds = (I[][]) Array.newInstance(ids.getClass().getComponentType(),ids.length+1);
        System.arraycopy(ids,0,wrongIds,0,wrongIds.length-1);
        wrongIds[wrongIds.length-1] = wrongIds[0];
        switch(constructorType) {
            case 1 :
            case 2 : getIndex(slices,wrongIds); break;
            default : throw new IllegalArgumentException("invalid test");
        }
    }

    @SuppressWarnings("unchecked") //ok, id is already I[][] instance
    @Test(expected=IllegalArgumentException.class)
    public void testConstructorFailureIdCountTooSmall() {
        I[][] wrongIds = (I[][]) Array.newInstance(ids.getClass().getComponentType(),ids.length);
        System.arraycopy(ids,0,wrongIds,0,wrongIds.length);
        int randomDimension = random.nextInt(wrongIds.length);
        I[] wids = (I[]) Array.newInstance(wrongIds[randomDimension].getClass().getComponentType(),wrongIds[randomDimension].length-1);
        System.arraycopy(wrongIds[randomDimension],0,wids,0,wids.length);
        wrongIds[randomDimension] = wids;
        switch(constructorType) {
            case 1 :
            case 2 : getIndex(slices,wrongIds); break;
            default : throw new IllegalArgumentException("invalid test");
        }
    }

    @SuppressWarnings("unchecked") //ok, id is already I[][] instance
    @Test(expected=IllegalArgumentException.class)
    public void testConstructorFailureIdCountTooBig() {
        I[][] wrongIds = (I[][]) Array.newInstance(ids.getClass().getComponentType(),ids.length);
        System.arraycopy(ids,0,wrongIds,0,wrongIds.length);
        int randomDimension = random.nextInt(wrongIds.length);
        I[] wids = (I[]) Array.newInstance(wrongIds[randomDimension].getClass().getComponentType(),wrongIds[randomDimension].length+1);
        System.arraycopy(wrongIds[randomDimension],0,wids,0,wids.length-1);
        wids[wids.length-1] = getRandomId();
        wrongIds[randomDimension] = wids;
        switch(constructorType) {
            case 1 :
            case 2 : getIndex(slices,wrongIds); break;
            default : throw new IllegalArgumentException("invalid test");
        }
    }

    @SuppressWarnings("unchecked") //ok, id is already I[][] instance
    @Test(expected=IllegalArgumentException.class)
    public void testConstructorFailureIdRepetition() {
        I[][] wrongIds = (I[][]) Array.newInstance(ids.getClass().getComponentType(),ids.length);
        System.arraycopy(ids,0,wrongIds,0,wrongIds.length);
        int randomDimension = random.nextInt(wrongIds.length);
        I[] wids = (I[]) Array.newInstance(wrongIds[randomDimension].getClass().getComponentType(),wrongIds[randomDimension].length);
        System.arraycopy(wrongIds[randomDimension],0,wids,0,wids.length);
        wids[wids.length-1] = wids[0];
        wrongIds[randomDimension] = wids;
        switch(constructorType) {
            case 1 :
            case 2 : getIndex(slices,wrongIds); break;
            default : throw new IllegalArgumentException("invalid test");
        }
    }

}
