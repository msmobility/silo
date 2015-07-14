package com.pb.sawdust.model.models.provider.hub;

import com.pb.sawdust.model.models.provider.SimpleDataProvider;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.alias.matrix.id.IdDoubleMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;

import com.pb.sawdust.tensor.index.MixedIndex;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.collections.LinkedSetList;
import com.pb.sawdust.util.collections.SetList;
import com.pb.sawdust.util.test.TestBase;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static com.pb.sawdust.util.Range.range;

/**
 * The {@code SimplePolyDataProviderTest} ...
 *
 * @author crf <br/>
 *         Started Oct 21, 2010 12:53:21 PM
 */
public class SimplePolyDataProviderTest extends PolyDataProviderTest<String> {
    protected SimplePolyDataProvider<String> simplePolyProvider;

    public static void main(String ... args) {
        TestBase.main();
    }

    protected void addSubDataTest(List<Class<? extends TestBase>> additionalClassContainer) {
        super.addSubDataTest(additionalClassContainer);
        additionalClassContainer.add(SimplePolyDataProviderParentTest.class);
    }

    @Override
    protected PolyDataProvider<String> getPolyProvider(int id, int dataLength, SetList<String> keys, Map<String,double[]> sharedData, Map<String,Map<String,double[]>> data, Map<String, IdDoubleMatrix<? super String>> polyData) {
        SimplePolyDataProvider<String> pdp = getUninitializedProvider(id,keys);
        for (String key : data.keySet())
            pdp.addKeyedProvider(key,new SimpleDataProvider(data.get(key),ArrayTensor.getFactory()));
        pdp.addProvider(new SimpleDataProvider(sharedData,ArrayTensor.getFactory()));
        for (String variable : polyData.keySet())
            pdp.addPolyData(variable,polyData.get(variable));
        return pdp;
    }

    @Override
    protected SimplePolyDataProvider<String> getUninitializedProvider(int dataId, SetList<String> keys) {
        if (id == UNSPECIFIED_ID_ID)
            return new SimplePolyDataProvider<String>(keys,ArrayTensor.getFactory());
        else
            return new SimplePolyDataProvider<String>(id,keys,ArrayTensor.getFactory());
    }

    @Override
    protected SetList<String> getKeys() {
        SetList<String> keys = new LinkedSetList<String>();
        for (int i : range(random.nextInt(3,15)))
            keys.add(random.nextAsciiString(10));
        return keys;
    }

    @Override
    protected String getBadKey() {
        String key;
        while (keys.contains(key = random.nextAsciiString(12)));
        return key;
    }

    @Override
    @Before
    public void beforeTest() {
        super.beforeTest();
        simplePolyProvider = (SimplePolyDataProvider<String>) providerHub;
    }

    @Test
    public void testAddPolyData() {
        @SuppressWarnings("unchecked") //will be correct type
        IdDoubleMatrix<? super String> pdata = (IdDoubleMatrix<? super String>) getPolyData(keyList,dataLength,true);
        String variable = random.nextAsciiString(25); //too long for anybody else
        simplePolyProvider.addPolyData(variable,pdata);
        assertTrue(TensorUtil.almostEquals(pdata,polyProvider.getPolyData(variable)));
    }

    @Test
    public void testAddPolyDataIds() {
        @SuppressWarnings("unchecked") //will be correct type
        IdDoubleMatrix<? super String> pdata = (IdDoubleMatrix<? super String>) getPolyData(keyList,dataLength,true);
        String variable = random.nextAsciiString(25); //too long for anybody else
        simplePolyProvider.addPolyData(variable,pdata);
        assertEquals(keyList,polyProvider.getPolyData(variable).getIndex().getIndexIds().get(1));
    }

    @Test
    public void testAddPolyDataMixed() {
        int[] randomIndices = random.getRandomIndices(keyList.size());
        @SuppressWarnings("unchecked") //will be correct type
        IdDoubleMatrix<Object> pdata = (IdDoubleMatrix<Object>) getPolyData(keyList,dataLength,true);
        List<String> newIds = new LinkedList<String>();
        DoubleMatrix pndata = ArrayTensor.getFactory().doubleMatrix(dataLength,keyList.size());
        for (int i : range(randomIndices.length)) {
            String key = keyList.get(randomIndices[i]);
            newIds.add(key);
            for (int j : range(dataLength))
                pndata.setCell(pdata.getValueById(j,key),j,i);
        }
        String variable = random.nextAsciiString(25); //too long for anybody else
        simplePolyProvider.addPolyData(variable,(IdDoubleMatrix<Object>) pndata.getReferenceTensor(MixedIndex.replaceIds(pndata.getIndex(),1,newIds)));
        assertTrue(TensorUtil.almostEquals(pdata,polyProvider.getPolyData(variable)));
    }

    @Test
    public void testAddPolyDataMixedIds() {
        int[] randomIndices = random.getRandomIndices(keyList.size());
        @SuppressWarnings("unchecked") //will be correct type
        IdDoubleMatrix<Object> pdata = (IdDoubleMatrix<Object>) getPolyData(keyList,dataLength,true);
        List<String> newIds = new LinkedList<String>();
        DoubleMatrix pndata = ArrayTensor.getFactory().doubleMatrix(dataLength,keyList.size());
        for (int i : range(randomIndices.length)) {
            String key = keyList.get(randomIndices[i]);
            newIds.add(key);
            for (int j : range(dataLength))
                pndata.setCell(pdata.getValueById(j,key),j,i);
        }
        String variable = random.nextAsciiString(25); //too long for anybody else
        simplePolyProvider.addPolyData(variable,(IdDoubleMatrix<Object>) pndata.getReferenceTensor(MixedIndex.replaceIds(pndata.getIndex(),1,newIds)));
        assertEquals(keyList,polyProvider.getPolyData(variable).getIndex().getIndexIds().get(1));
    }

    @Test
    public void testAddPolyDataUnrelatedIds() {
        @SuppressWarnings("unchecked") //will be correct type
        IdDoubleMatrix<Object> pdata = (IdDoubleMatrix<Object>) getPolyData(keyList,dataLength,true);
        String variable = random.nextAsciiString(25); //too long for anybody else
        simplePolyProvider.addPolyData(variable,(IdDoubleMatrix<Object>) pdata.getReferenceTensor(MixedIndex.replaceIds(pdata.getIndex(),1,Arrays.asList(ArrayUtil.toIntegerArray(range(0,3*keyList.size(),3).getRangeArray())))));
        assertTrue(TensorUtil.almostEquals(pdata,polyProvider.getPolyData(variable)));
    }

    @Test
    public void testAddPolyDataUnrelatedIdsIds() {
        @SuppressWarnings("unchecked") //will be correct type
        IdDoubleMatrix<Object> pdata = (IdDoubleMatrix<Object>) getPolyData(keyList,dataLength,true);
        String variable = random.nextAsciiString(25); //too long for anybody else
        simplePolyProvider.addPolyData(variable,(IdDoubleMatrix<Object>) pdata.getReferenceTensor(MixedIndex.replaceIds(pdata.getIndex(),1,Arrays.asList(ArrayUtil.toIntegerArray(range(0,3*keyList.size(),3).getRangeArray())))));
        assertEquals(keyList,polyProvider.getPolyData(variable).getIndex().getIndexIds().get(1));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddPolyDataLengthTooBig() {
        @SuppressWarnings("unchecked") //will be correct type
        IdDoubleMatrix<? super String> pdata = (IdDoubleMatrix<? super String>) getPolyData(keyList,dataLength+1,true);
        String variable = random.nextAsciiString(25); //too long for anybody else
        simplePolyProvider.addPolyData(variable,pdata);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddPolyDataLengthTooShort() {
        @SuppressWarnings("unchecked") //will be correct type
        IdDoubleMatrix<? super String> pdata = (IdDoubleMatrix<? super String>) getPolyData(keyList,dataLength-1,true);
        String variable = random.nextAsciiString(25); //too long for anybody else
        simplePolyProvider.addPolyData(variable,pdata);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddPolyDataExtraColumn() {
        SetList<String> badKeys = new LinkedSetList<String>(keyList);
        badKeys.add(random.nextAsciiString(25));
        @SuppressWarnings("unchecked") //will be correct type
        IdDoubleMatrix<? super String> pdata = (IdDoubleMatrix<? super String>) getPolyData(badKeys,dataLength,true);
        String variable = random.nextAsciiString(25); //too long for anybody else
        simplePolyProvider.addPolyData(variable,pdata);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddPolyDataMissingColumn() {
        SetList<String> badKeys = new LinkedSetList<String>(keyList);
        badKeys.remove(badKeys.get(0));
        @SuppressWarnings("unchecked") //will be correct type
        IdDoubleMatrix<? super String> pdata = (IdDoubleMatrix<? super String>) getPolyData(badKeys,dataLength,true);
        String variable = random.nextAsciiString(25); //too long for anybody else
        simplePolyProvider.addPolyData(variable,pdata);
    }

    @Test
    public void testAddPolyDataNonId() {
        DoubleMatrix pdata = getPolyData(keyList,dataLength,true);
        String variable = random.nextAsciiString(25); //too long for anybody else
        simplePolyProvider.addPolyData(variable,pdata);
        assertTrue(TensorUtil.almostEquals(pdata,polyProvider.getPolyData(variable)));
    }

    @Test
    public void testAddPolyDataNonIdIds() {
        DoubleMatrix pdata = getPolyData(keyList,dataLength,true);
        String variable = random.nextAsciiString(25); //too long for anybody else
        simplePolyProvider.addPolyData(variable,pdata);
        assertEquals(keyList,polyProvider.getPolyData(variable).getIndex().getIndexIds().get(1));
    }

    @Test
    public void testAddPolyDataMixedNonId() {
        int[] randomIndices = random.getRandomIndices(keyList.size());
        @SuppressWarnings("unchecked") //will be correct type
        IdDoubleMatrix<Object> pdata = (IdDoubleMatrix<Object>) getPolyData(keyList,dataLength,true);
        List<String> newIds = new LinkedList<String>();
        DoubleMatrix pndata = ArrayTensor.getFactory().doubleMatrix(dataLength,keyList.size());
        for (int i : range(randomIndices.length)) {
            String key = keyList.get(randomIndices[i]);
            newIds.add(key);
            for (int j : range(dataLength))
                pndata.setCell(pdata.getValueById(j,key),j,i);
        }
        String variable = random.nextAsciiString(25); //too long for anybody else
        simplePolyProvider.addPolyData(variable,(DoubleMatrix) pndata.getReferenceTensor(MixedIndex.replaceIds(pndata.getIndex(),1,newIds)));
        assertTrue(TensorUtil.almostEquals(pdata,polyProvider.getPolyData(variable)));
    }

    @Test
    public void testAddPolyDataMixedNonIdIds() {
        int[] randomIndices = random.getRandomIndices(keyList.size());
        @SuppressWarnings("unchecked") //will be correct type
        IdDoubleMatrix<Object> pdata = (IdDoubleMatrix<Object>) getPolyData(keyList,dataLength,true);
        List<String> newIds = new LinkedList<String>();
        DoubleMatrix pndata = ArrayTensor.getFactory().doubleMatrix(dataLength,keyList.size());
        for (int i : range(randomIndices.length)) {
            String key = keyList.get(randomIndices[i]);
            newIds.add(key);
            for (int j : range(dataLength))
                pndata.setCell(pdata.getValueById(j,key),j,i);
        }
        String variable = random.nextAsciiString(25); //too long for anybody else
        simplePolyProvider.addPolyData(variable,(DoubleMatrix) pndata.getReferenceTensor(MixedIndex.replaceIds(pndata.getIndex(),1,newIds)));
        assertEquals(keyList,polyProvider.getPolyData(variable).getIndex().getIndexIds().get(1));
    }

    @Test
    public void testAddPolyDataUnrelatedIdsNonId() {
        @SuppressWarnings("unchecked") //will be correct type
        IdDoubleMatrix<Object> pdata = (IdDoubleMatrix<Object>) getPolyData(keyList,dataLength,true);
        String variable = random.nextAsciiString(25); //too long for anybody else
        simplePolyProvider.addPolyData(variable,(DoubleMatrix) pdata.getReferenceTensor(MixedIndex.replaceIds(pdata.getIndex(),1,Arrays.asList(ArrayUtil.toIntegerArray(range(0,3*keyList.size(),3).getRangeArray())))));
        assertTrue(TensorUtil.almostEquals(pdata,polyProvider.getPolyData(variable)));
    }

    @Test
    public void testAddPolyDataUnrelatedIdsNonIdIds() {
        @SuppressWarnings("unchecked") //will be correct type
        IdDoubleMatrix<Object> pdata = (IdDoubleMatrix<Object>) getPolyData(keyList,dataLength,true);
        String variable = random.nextAsciiString(25); //too long for anybody else
        simplePolyProvider.addPolyData(variable,(DoubleMatrix) pdata.getReferenceTensor(MixedIndex.replaceIds(pdata.getIndex(),1,Arrays.asList(ArrayUtil.toIntegerArray(range(0,3*keyList.size(),3).getRangeArray())))));
        assertEquals(keyList,polyProvider.getPolyData(variable).getIndex().getIndexIds().get(1));
    }

    @Test
    public void testAddPolyDataNoId() {
        DoubleMatrix pdata = getPolyData(keyList,dataLength,true);
        DoubleMatrix mdata = ArrayTensor.getFactory().doubleMatrix(pdata.size(0),pdata.size(1));
        TensorUtil.copyTo(pdata,mdata);
        String variable = random.nextAsciiString(25); //too long for anybody else
        simplePolyProvider.addPolyData(variable,mdata);
        assertTrue(TensorUtil.almostEquals(mdata,polyProvider.getPolyData(variable)));
    }

    @Test
    public void testAddPolyDataNoIdIds() {
        DoubleMatrix pdata = getPolyData(keyList,dataLength,true);
        DoubleMatrix mdata = ArrayTensor.getFactory().doubleMatrix(pdata.size(0),pdata.size(1));
        TensorUtil.copyTo(pdata,mdata);
        String variable = random.nextAsciiString(25); //too long for anybody else
        simplePolyProvider.addPolyData(variable,mdata);
        assertEquals(keyList,polyProvider.getPolyData(variable).getIndex().getIndexIds().get(1));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddPolyDataNoIdLengthTooBig() {
        DoubleMatrix pdata = getPolyData(keyList,dataLength+1,true);
        String variable = random.nextAsciiString(25); //too long for anybody else
        simplePolyProvider.addPolyData(variable,pdata);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddPolyDataNoIdLengthTooShort() {
        DoubleMatrix pdata = getPolyData(keyList,dataLength-1,true);
        String variable = random.nextAsciiString(25); //too long for anybody else
        simplePolyProvider.addPolyData(variable,pdata);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddPolyDataNoIdExtraColumn() {
        SetList<String> badKeys = new LinkedSetList<String>(keyList);
        badKeys.add(random.nextAsciiString(25));
        DoubleMatrix pdata = getPolyData(badKeys,dataLength,true);
        String variable = random.nextAsciiString(25); //too long for anybody else
        simplePolyProvider.addPolyData(variable,pdata);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddPolyDataNoIdMissingColumn() {
        SetList<String> badKeys = new LinkedSetList<String>(keyList);
        badKeys.remove(badKeys.get(0));
        DoubleMatrix pdata = getPolyData(badKeys,dataLength,true);
        String variable = random.nextAsciiString(25); //too long for anybody else
        simplePolyProvider.addPolyData(variable,pdata);
    }

    public static class SimplePolyDataProviderParentTest extends SimpleDataProviderHubTest {
        @Override
        protected DataProviderHub<String> getUninitializedProvider(int id, Set<String> keys) {
            if (id == UNSPECIFIED_ID_ID)
                return new SimplePolyDataProvider<String>(new LinkedSetList<String>(keys), ArrayTensor.getFactory());
            else
                return new SimplePolyDataProvider<String>(id,new LinkedSetList<String>(keys), ArrayTensor.getFactory());
        }
    }
}
