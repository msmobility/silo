package com.pb.sawdust.model.models.provider.hub;

import com.pb.sawdust.model.models.provider.SimpleDataProvider;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.alias.matrix.id.IdDoubleMatrix;
import com.pb.sawdust.util.collections.LinkedSetList;
import com.pb.sawdust.util.collections.SetList;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.pb.sawdust.util.Range.range;

/**
 * The {@code SubPolyDataProviderTest} ...
 *
 * @author crf <br/>
 *         Started Oct 21, 2010 3:03:23 PM
 */
public class SubPolyDataProviderTest extends PolyDataProviderTest<String> {
    protected SubPolyDataProvider<String> subPolyProvider;
    protected PolyDataProvider<String> parent;

    public static void main(String ... args) {
        TestBase.main();
    }

    protected void addSubDataTest(List<Class<? extends TestBase>> additionalClassContainer) {
        super.addSubDataTest(additionalClassContainer);
        additionalClassContainer.add(SubPolyDataProviderParentTest.class);
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
    protected PolyDataProvider<String> getPolyProvider(int id, int dataLength, SetList<String> keys, Map<String, double[]> sharedData, Map<String, Map<String, double[]>> data, Map<String, IdDoubleMatrix<? super String>> polyData) {
        SimplePolyDataProvider<String> pdp = new SimplePolyDataProvider<String>(keys,ArrayTensor.getFactory());
        for (String key : data.keySet())
            pdp.addKeyedProvider(key,new SimpleDataProvider(data.get(key), ArrayTensor.getFactory()));
        pdp.addProvider(new SimpleDataProvider(sharedData,ArrayTensor.getFactory()));
        for (String variable : polyData.keySet())
            pdp.addPolyData(variable,polyData.get(variable));
        parent = pdp;
        return new SubPolyDataProvider<String>(pdp,0,dataLength);
    }

    @Override
    protected PolyDataProvider<String> getUninitializedProvider(int id, SetList<String> keys) {
        return null;
    }

    @Override
    @Before
    public void beforeTest() {
        super.beforeTest();
        subPolyProvider = (SubPolyDataProvider<String>) polyProvider;
    }

    @Override
    @Ignore
    @Test
    public void testGetId() {
        //no way to set id
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorStartTooSmall() {
        new SubPolyDataProvider<String>(parent,-1,subDataEnd);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorEndTooBig() {
        new SubPolyDataProvider<String>(parent,subDataStart,dataLength+1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorStartEndSwapped() {
        new SubPolyDataProvider<String>(parent,subDataEnd,subDataStart);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorZeroLength() {
        new SubPolyDataProvider<String>(parent,subDataStart,subDataStart);
    }

    public static class SubPolyDataProviderParentTest extends SubDataProviderHubTest {
        @Override
        protected DataProviderHub<String> getProviderHub(int id, int dataLength, Map<String, double[]> sharedData, Map<String, Map<String, double[]>> data) {
            SimplePolyDataProvider<String> pdp = new SimplePolyDataProvider<String>(new LinkedSetList<String>(keys),ArrayTensor.getFactory());
            for (String key : data.keySet())
                pdp.addKeyedProvider(key,new SimpleDataProvider(data.get(key), ArrayTensor.getFactory()));
            pdp.addProvider(new SimpleDataProvider(sharedData,ArrayTensor.getFactory()));
            parent = pdp;
            return new SubPolyDataProvider<String>(pdp,0,dataLength);
        }
    }
}
