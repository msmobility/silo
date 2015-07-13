package com.pb.sawdust.model.models.provider.hub;

import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.alias.matrix.id.IdDoubleMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;
import com.pb.sawdust.tensor.alias.vector.primitive.DoubleVector;
import com.pb.sawdust.tensor.index.MirrorIndex;
import com.pb.sawdust.tensor.index.MixedIndex;
import static com.pb.sawdust.util.Range.*;

import com.pb.sawdust.tensor.index.SliceIndex;
import com.pb.sawdust.tensor.slice.SliceUtil;
import com.pb.sawdust.util.collections.SetList;
import static org.junit.Assert.*;

import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

/**
 * The {@code PolyDataProviderHubTest} ...
 *
 * @author crf <br/>
 *         Started Oct 20, 2010 11:06:53 AM
 */
public abstract class PolyDataProviderTest<K> extends DataProviderHubTest<K> {
    protected PolyDataProvider<K> polyProvider;
    protected SetList<K> keyList;
    protected Map<String,IdDoubleMatrix<? super K>> polyData;

    abstract protected SetList<K> getKeys();
    abstract protected K getBadKey();
    abstract protected PolyDataProvider<K> getPolyProvider(int id, int dataLength, SetList<K> keys, Map<String,double[]> sharedData, Map<K,Map<String,double[]>> data, Map<String,IdDoubleMatrix<? super K>> polyData);
    abstract protected PolyDataProvider<K> getUninitializedProvider(int id, SetList<K> keys);

    protected void addSubDataTest(List<Class<? extends TestBase>> additionalClassContainer) {
        super.addSubDataTest(additionalClassContainer);
        additionalClassContainer.add(PolyDataProviderSubDataTest.class);
    }

    protected PolyDataProvider<K> getUninitializedProvider(int id, Set<K> keys) {
        return getUninitializedProvider(id,(SetList<K>) keys);
    }

    protected PolyDataProvider<K> getProviderHub(int id, int dataLength, Map<String,double[]> sharedData, Map<K,Map<String,double[]>> data) {
        if (polyData == null) //need to initialize this, but only partway through @Before of parent class
            polyData = getPolyData(random.nextInt(3,6)); //make sure not in same realm of variable lengths
        return getPolyProvider(id,dataLength,(SetList<K>) keys,sharedData,data,polyData); //can't use keyList because not initialized yet
    }

    protected DoubleMatrix getPolyData(SetList<K> keys, int length, boolean idKeys) {
        DoubleMatrix polyData = ArrayTensor.getFactory().doubleMatrix(length,keys.size());
        TensorUtil.fill(polyData,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                return random.nextDouble();
            }
        });
        if (idKeys)
            polyData = (DoubleMatrix) polyData.getReferenceTensor(MixedIndex.replaceIds(polyData.getIndex(),1,keys));
        return polyData;
    }

    @SuppressWarnings("unchecked") //will be IdDoubleMatrix<? super K>, so suppress this
    protected Map<String,IdDoubleMatrix<? super K>> getPolyData(int variableCount) {
        Map<String,IdDoubleMatrix<? super K>> polyData = new HashMap<String,IdDoubleMatrix<? super K>>();
        for (int i : range(variableCount))
            polyData.put(random.nextAsciiString(random.nextInt(15,18)),(IdDoubleMatrix<? super K>) getPolyData((SetList<K>) keys,dataLength,true)); //variable name length out of bounds of others, hopefully
        return polyData;
    }

    @Before
    public void beforeTest() {
        super.beforeTest();
        polyProvider = (PolyDataProvider<K>) providerHub;
        keyList = (SetList<K>) keys;
        Set<String> usedVariables = new HashSet<String>();
        for (K key : data.keySet())
            usedVariables.addAll(data.get(key).keySet());
        usedVariables.addAll(sharedData.keySet());
    }

    @Test
    @Override
    public void testGetDataKeys() {
        assertEquals(keyList,polyProvider.getDataKeys());
    }

    @Test
    public void testGetPolyDataVariables() {
        assertEquals(polyData.keySet(),polyProvider.getPolyDataVariables());
    }

    @Test
    public void testGetPolyData() {
        String variable = random.getRandomValue(polyData.keySet());
        assertTrue(TensorUtil.almostEquals(polyData.get(variable),polyProvider.getPolyData(variable)));
    }

    @Test
    public void testGetPolyDataIds() {
        assertEquals(keyList,polyProvider.getPolyData(random.getRandomValue(polyData.keySet())).getIndex().getIndexIds().get(1));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetPolyDataBadVariable() {
        String variable;
        while (polyData.keySet().contains(variable = random.nextAsciiString(random.nextInt(5,8)))); //loop to find free variable
        polyProvider.getPolyData(variable);
    }

    @Test
    public void testGetPolyDataNonPolyVariable() {
        String var = random.getRandomValue(sharedData.keySet());
        double[] d = sharedData.get(var);
        DoubleVector v = ArrayTensor.getFactory().doubleVector(d.length);
        for (int i : range(d.length))
            v.setCell(d[i],i);
        Map<Integer,Integer> newDim = new HashMap<Integer,Integer>();
        newDim.put(1,keyList.size());
        DoubleMatrix result = (DoubleMatrix) v.getReferenceTensor(MirrorIndex.getStandardMirrorIndex(v.getIndex(),newDim)); //first step mirror out
        result = (DoubleMatrix) result.getReferenceTensor(MixedIndex.replaceIds(result.getIndex(),1,keyList)); //second step, mix in keys
        assertTrue(TensorUtil.almostEquals(result,polyProvider.getPolyData(var)));
    }

    @Test
    public void testGetPolyProviderSubData() {
        String variable = random.getRandomValue(polyData.keySet());
        DoubleMatrix m = polyData.get(variable);
        m = (DoubleMatrix) m.getReferenceTensor(SliceIndex.getSliceIndex(m.getIndex(),SliceUtil.range(subDataStart,subDataEnd),SliceUtil.fullSlice(m.size(1))));
        assertTrue(TensorUtil.almostEquals(m,polyProvider.getSubDataHub(subDataStart,subDataEnd).getPolyData(variable)));
    }

    @Test
    public void testGetPolyProviderSubDataIds() {
        assertEquals(keyList,polyProvider.getSubDataHub(subDataStart,subDataEnd).getPolyData(random.getRandomValue(polyData.keySet())).getIndex().getIndexIds().get(1));
    }

    @Test
    public void testGetFullProvider() {
        K key = random.getRandomValue(keyList);
        String variable = random.getRandomValue(polyData.keySet());
        DataProvider provider = polyProvider.getFullProvider(key);
        double[] data = new double[dataLength];
        @SuppressWarnings("unchecked")
        IdDoubleMatrix<Object> polyVariable = (IdDoubleMatrix<Object>) polyData.get(variable);
        int column = polyVariable.getIndex().getIndex(1,key);
        for (int i : range(data.length))
            data[i] = polyVariable.getCell(i,column);
        assertArrayAlmostEquals(provider.getVariableData(variable),data);
    }

    @Test
    public void testGetFullProviderDataLength() {
        assertEquals(dataLength,polyProvider.getFullProvider(random.getRandomValue(keyList)).getDataLength());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetFullProviderBadKey() {
        polyProvider.getFullProvider(getBadKey());
    }

    public static class PolyDataProviderSubDataTest<K> extends PolyDataProviderTest<K> {
        protected PolyDataProviderTest<K> parent;

        protected void addSubDataTest(List<Class<? extends TestBase>> additionalClassContainer) { }

        @SuppressWarnings("unchecked") //generic cast is internally valid, so ok
        protected void beforeTestData() {
            dataLength = parent.subDataEnd - parent.subDataStart;
            keys = getKeys();
            subDataStart = dataLength == 1 ? 0 : random.nextInt(dataLength/2);
            subDataEnd = random.nextInt(dataLength/2,dataLength);
            sharedData = new HashMap<String,double[]>();
            for (String variable : parent.sharedData.keySet())
                sharedData.put(variable, Arrays.copyOfRange(parent.sharedData.get(variable),parent.subDataStart,parent.subDataEnd));
            data = new HashMap<K,Map<String,double[]>>();
            for (K key : parent.data.keySet()) {
                data.put(key,new HashMap<String,double[]>());
                for (String variable : parent.data.get(key).keySet())
                    data.get(key).put(variable,Arrays.copyOfRange(parent.data.get(key).get(variable),parent.subDataStart,parent.subDataEnd));
            }
            polyData = new HashMap<String,IdDoubleMatrix<? super K>>();
            for (String s : parent.polyData.keySet()) {
                IdDoubleMatrix<? super K> m = parent.polyData.get(s);
                polyData.put(s,(IdDoubleMatrix<? super K>) m.getReferenceTensor(SliceIndex.getSliceIndex(m.getIndex(),SliceUtil.range(parent.subDataStart,parent.subDataEnd),SliceUtil.fullSlice(m.size(1)))));
            }
        }

        @Before
        @SuppressWarnings("unchecked") //a <K> will be a <K>, o<K>?
        public void beforeTest() {
            parent = (PolyDataProviderTest<K>) getCallingContextInstance();
            parent.beforeTest();
            super.beforeTest();
            setAdditionalTestInformation(" (subpolydata)");
        }

        @Override
        protected SetList<K> getKeys() {
            return parent.keyList;
        }

        @Override
        protected K getBadKey() {
            return parent.getBadKey();
        }

        @Override
        protected PolyDataProvider<K> getPolyProvider(int id, int dataLength, SetList<K> keys, Map<String, double[]> sharedData, Map<K, Map<String, double[]>> data, Map<String, IdDoubleMatrix<? super K>> polyData) {
            return parent.polyProvider.getSubDataHub(parent.subDataStart,parent.subDataEnd);
        }

        @Override
        protected PolyDataProvider<K> getUninitializedProvider(int id, SetList<K> keys) {
            return null;
        }

        @Ignore
        @Test
        public void testGetId() {
            //cannot get specific id for subdata
        } 

        @Test
        public void testGetAbsoluteStartIndex() {
            assertEquals(parent.subDataStart,providerHub.getAbsoluteStartIndex());
        }
    }
}
