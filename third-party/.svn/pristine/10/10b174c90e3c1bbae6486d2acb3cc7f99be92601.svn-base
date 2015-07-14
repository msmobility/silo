package com.pb.sawdust.model.models.provider.hub;

import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.model.models.provider.SimpleDataProvider;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;
import org.junit.Test;

import static com.pb.sawdust.util.Range.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The {@code SimpleDataProviderHubTest} ...
 *
 * @author crf <br/>
 *         Started Oct 4, 2010 10:37:49 PM
 */
public class SimpleDataProviderHubTest extends DataProviderHubTest<String> {
    protected SimpleDataProviderHub<String> simpleProviderHub;

    public static void main(String ... args) {
        TestBase.main();
    }

    @Override
    protected DataProviderHub<String> getProviderHub(int id, int dataLength, Map<String, double[]> sharedData, Map<String, Map<String, double[]>> data) {
        SimpleDataProviderHub<String> hub = (SimpleDataProviderHub<String>) getUninitializedProvider(id,data.keySet());
        for (String key : data.keySet())
            hub.addKeyedProvider(key,new SimpleDataProvider(data.get(key),ArrayTensor.getFactory()));
        hub.addProvider(new SimpleDataProvider(sharedData,ArrayTensor.getFactory()));
        return hub;
    }

    @Override
    protected DataProviderHub<String> getUninitializedProvider(int id, Set<String> keys) {
        if (id == UNSPECIFIED_ID_ID)
            return new SimpleDataProviderHub<String>(keys,ArrayTensor.getFactory());
        else
            return new SimpleDataProviderHub<String>(id,keys,ArrayTensor.getFactory());
    }

    @Override
    protected Set<String> getKeys() {
        Set<String> keys = new HashSet<String>();
        for (int i : range(random.nextInt(3,15)))
            keys.add(random.nextAsciiString(10));
        return keys;
    }

    @Override
    @Before
    public void beforeTest() {
        super.beforeTest();
        simpleProviderHub = (SimpleDataProviderHub<String>) providerHub;
    }

    @Test
    public void testAddProviderGeneral() {
        DataProvider np = new SimpleDataProvider(getDataPiece(dataLength),ArrayTensor.getFactory());
        simpleProviderHub.addProvider(np);
        String variable = random.getRandomValue(np.getVariables());
        assertArrayAlmostEquals(np.getVariableData(variable),simpleProviderHub.getProvider(random.getRandomValue(keys)).getVariableData(variable));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddProviderGeneralWrongSize() {
        simpleProviderHub.addProvider(new SimpleDataProvider(getDataPiece(dataLength+1),ArrayTensor.getFactory()));
    }

    @Test
    public void testAddProviderGeneralUninitialized() {
        SimpleDataProviderHub<String> hub = (SimpleDataProviderHub<String>) getUninitializedProvider(data.keySet());
        if (hub != null) {
            DataProvider np = new SimpleDataProvider(getDataPiece(dataLength+random.nextInt(5,10)),ArrayTensor.getFactory());
            hub.addProvider(np);
            String variable = random.getRandomValue(np.getVariables());
            assertArrayAlmostEquals(np.getVariableData(variable),hub.getProvider(random.getRandomValue(keys)).getVariableData(variable));
        }
    }

    @Test
    public void testAddProviderKeyed() {
        DataProvider np = new SimpleDataProvider(getDataPiece(dataLength),ArrayTensor.getFactory());
        String key = random.getRandomValue(keys);
        simpleProviderHub.addKeyedProvider(key,np);
        String variable = random.getRandomValue(np.getVariables());
        assertArrayAlmostEquals(np.getVariableData(variable),simpleProviderHub.getProvider(key).getVariableData(variable));
    }

    @Test
    public void testAddProviderKeyedFresh() {
        DataProvider np = new SimpleDataProvider(getDataPiece(dataLength),ArrayTensor.getFactory());
        String key = random.nextAsciiString(7);
        simpleProviderHub.addKeyedProvider(key,np);
        String variable = random.getRandomValue(np.getVariables());
        assertArrayAlmostEquals(np.getVariableData(variable),simpleProviderHub.getProvider(key).getVariableData(variable));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddProviderKeyedWrongSize() {
        simpleProviderHub.addKeyedProvider(random.getRandomValue(keys),new SimpleDataProvider(getDataPiece(dataLength+1),ArrayTensor.getFactory()));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddProviderKeyedFreshWrongSize() {
        simpleProviderHub.addKeyedProvider(random.nextAsciiString(7),new SimpleDataProvider(getDataPiece(dataLength+1),ArrayTensor.getFactory()));
    }

    @Test
    public void testAddProviderKeyedUninitialized() {
        SimpleDataProviderHub<String> hub = (SimpleDataProviderHub<String>) getUninitializedProvider(data.keySet());
        if (hub != null) {
            DataProvider np = new SimpleDataProvider(getDataPiece(dataLength+random.nextInt(5,10)),ArrayTensor.getFactory());
            String key = random.getRandomValue(keys);
            hub.addKeyedProvider(key,np);
            String variable = random.getRandomValue(np.getVariables());
            assertArrayAlmostEquals(np.getVariableData(variable),hub.getProvider(key).getVariableData(variable));
        }
    }

    @Test
    public void testAddProvidersKeyed() {
        Map<String,DataProvider> pmap = new HashMap<String,DataProvider>();
        for (int i : range(random.nextInt(3,7)))
            pmap.put(random.getRandomValue(keys),new SimpleDataProvider(getDataPiece(dataLength),ArrayTensor.getFactory()));
        simpleProviderHub.addKeyedProviders(pmap);
        String key = random.getRandomValue(pmap.keySet());
        String variable = random.getRandomValue(pmap.get(key).getVariables());
        assertArrayAlmostEquals(pmap.get(key).getVariableData(variable),simpleProviderHub.getProvider(key).getVariableData(variable));
    }

    @Test
    public void testAddProvidersKeyedFresh() {
        Map<String,DataProvider> pmap = new HashMap<String,DataProvider>();
        for (int i : range(random.nextInt(3,7)))
            pmap.put(random.nextAsciiString(7),new SimpleDataProvider(getDataPiece(dataLength),ArrayTensor.getFactory()));
        simpleProviderHub.addKeyedProviders(pmap);
        String key = random.getRandomValue(pmap.keySet());
        String variable = random.getRandomValue(pmap.get(key).getVariables());
        assertArrayAlmostEquals(pmap.get(key).getVariableData(variable),simpleProviderHub.getProvider(key).getVariableData(variable));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddProvidersKeyedBadLength() {
        Map<String,DataProvider> pmap = new HashMap<String,DataProvider>();
        for (int i : range(random.nextInt(3,7)))
            pmap.put(random.getRandomValue(keys),new SimpleDataProvider(getDataPiece(dataLength),ArrayTensor.getFactory()));
        pmap.put(random.getRandomValue(keys),new SimpleDataProvider(getDataPiece(dataLength+1),ArrayTensor.getFactory()));
        simpleProviderHub.addKeyedProviders(pmap);
    }

    @Test
    public void testAddProvidersKeyedUninitialized() {
        SimpleDataProviderHub<String> hub = (SimpleDataProviderHub<String>) getUninitializedProvider(data.keySet());
        if (hub != null) {
            Map<String,DataProvider> pmap = new HashMap<String,DataProvider>();
            for (int i : range(random.nextInt(3,7)))
                pmap.put(random.getRandomValue(keys),new SimpleDataProvider(getDataPiece(dataLength),ArrayTensor.getFactory()));
            hub.addKeyedProviders(pmap);
            String key = random.getRandomValue(pmap.keySet());
            String variable = random.getRandomValue(pmap.get(key).getVariables());
            assertArrayAlmostEquals(pmap.get(key).getVariableData(variable),hub.getProvider(key).getVariableData(variable));
        }
    }
}
