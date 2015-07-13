package com.pb.sawdust.model.models.provider.hub;

import com.pb.sawdust.model.models.provider.AbstractIdDataTest;
import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.model.models.provider.IdData;
import com.pb.sawdust.util.test.TestBase;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

import static com.pb.sawdust.util.Range.range;

/**
 * The {@code DataProviderHubTest} ...
 *
 * @author crf <br/>
 *         Started Oct 4, 2010 9:54:02 PM
 */
public abstract class DataProviderHubTest<K> extends AbstractIdDataTest {
    protected static final int UNSPECIFIED_ID_ID = Integer.MIN_VALUE;

    protected DataProviderHub<K> providerHub;
    protected Set<K> keys;
    protected int dataLength;
    protected Map<K,Map<String,double[]>> data;
    protected Map<String,double[]> sharedData;
    protected int subDataStart;
    protected int subDataEnd;

    abstract protected DataProviderHub<K> getProviderHub(int id, int dataLength, Map<String,double[]> sharedData, Map<K,Map<String,double[]>> data);
    abstract protected DataProviderHub<K> getUninitializedProvider(int id, Set<K> keys);
    abstract protected Set<K> getKeys();

    protected DataProviderHub<K> getUninitializedProvider(Set<K> keys) {
        return getUninitializedProvider(UNSPECIFIED_ID_ID,keys);
    }

    protected Map<String,double[]> getDataPiece(int length) {
        Map<String,double[]> data = new HashMap<String,double[]>();
        for (int i : range(random.nextInt(5,10)))
            data.put(random.nextAsciiString(10),random.nextDoubles(length));
        return data;
    }

    protected Map<K,Map<String,double[]>> getData(Set<K> keys, int length) {
        Map<K,Map<String,double[]>> data = new HashMap<K,Map<String,double[]>>();
        for (K key : keys)
            data.put(key,getDataPiece(length));
        return data;
    }

    protected IdData getConcreteDataId(int id) {
        return getProviderHub(id,dataLength,sharedData,data);
    }

    protected void addSubDataTest(List<Class<? extends TestBase>> additionalClassContainer) {
        additionalClassContainer.add(DataProviderHubSubDataTest.class);
    }

    protected Collection<Class<? extends TestBase>> getAdditionalTestClasses() {
        List<Class<? extends TestBase>> adds = new LinkedList<Class<? extends TestBase>>();
        addSubDataTest(adds);
        adds.addAll(super.getAdditionalTestClasses());
        return adds;
    }

    protected void beforeTestData() {
        dataLength = random.nextInt(50,200);
        keys = getKeys();
        data = getData(keys,dataLength);
        sharedData = getDataPiece(dataLength);
        subDataStart = random.nextInt(dataLength/2);
        subDataEnd = random.nextInt(dataLength/2,dataLength);
    }

    @Before
    @SuppressWarnings("unchecked") //providerHub will be of type <K>
    public void beforeTest() {
        beforeTestData();
        super.beforeTest();
        providerHub = (DataProviderHub<K>) idData;
    }

    @Test
    public void testGetProvider() {
        //check variables, and then variable data
        K key = random.getRandomValue(keys);
        DataProvider p = providerHub.getProvider(key);
        Set<String> variables = new HashSet<String>(data.get(key).keySet());
        variables.addAll(sharedData.keySet());
        assertEquals(variables,p.getVariables());
        for (String variable : variables)
            assertArrayAlmostEquals(sharedData.containsKey(variable) ? sharedData.get(variable) : data.get(key).get(variable),p.getVariableData(variable));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetProviderBadKey() {
        @SuppressWarnings("unchecked") //doesn't matter here
        K badKey = (K) new Object();
        providerHub.getProvider(badKey);
    }

    @Test
    public void testGetProviderSubData() {
        K key = random.getRandomValue(keys);
        String variable = random.getRandomValue(data.get(key).keySet());
        assertArrayAlmostEquals(Arrays.copyOfRange(data.get(key).get(variable),subDataStart,subDataEnd),providerHub.getSubDataHub(subDataStart,subDataEnd).getProvider(key).getVariableData(variable));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetProviderSubDataUninitialized() {
        DataProviderHub hub = getUninitializedProvider(keys);
        if (hub != null)
            hub.getSubDataHub(0,1);
        else
            throw new IllegalArgumentException("No uninitialize provider available");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetProviderSubDataStartNoLength() {
        providerHub.getSubDataHub(subDataEnd,subDataEnd);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetProviderSubDataStartTooSmall() {
        providerHub.getSubDataHub(-1,subDataEnd);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetProviderSubDataEndTooBig() {
        providerHub.getSubDataHub(subDataStart,dataLength+1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetProviderSubDataStartEndSwitched() {
        providerHub.getSubDataHub(subDataEnd,subDataStart);
    }

    @Test
    public void testGetDataLength() {
        assertEquals(dataLength,providerHub.getDataLength());
    }

    @Test
    public void testGetDataKeys() {
        assertEquals(keys,providerHub.getDataKeys());
    }

    @Test
    public void testGetAbsoluteStartIndex() {
        assertEquals(0,providerHub.getAbsoluteStartIndex());
    }

    public static class DataProviderHubSubDataTest<K> extends DataProviderHubTest<K> {
        protected DataProviderHubTest<K> parent;

        @Override
        protected DataProviderHub<K> getProviderHub(int id, int dataLength, Map<String, double[]> sharedData, Map<K, Map<String, double[]>> data) {
            return parent.providerHub.getSubDataHub(parent.subDataStart,parent.subDataEnd);
        }

        @Override
        protected DataProviderHub<K> getUninitializedProvider(int id, Set<K> keys) {
            //return parent.getUninitializedProvider(id,keys);
            return null;
        }

        @Override
        protected Set<K> getKeys() {
            return parent.keys;
        }

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
        }

        @Before
        @SuppressWarnings("unchecked") //a <K> will be a <K>, o<K>?
        public void beforeTest() {
            parent = (DataProviderHubTest<K>) getCallingContextInstance();
            parent.beforeTest();
            super.beforeTest();
            setAdditionalTestInformation(" (subdata)");
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

        protected void addSubDataTest(List<Class<? extends TestBase>> additionalClassContainer) { } //don't add any
    }

}
