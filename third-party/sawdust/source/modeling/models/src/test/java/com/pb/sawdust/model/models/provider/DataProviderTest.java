package com.pb.sawdust.model.models.provider;

import static com.pb.sawdust.util.Range.*;

import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * The {@code DataProviderTest} ...
 *
 * @author crf <br/>
 *         Started Sep 25, 2010 7:47:18 AM
 */
public abstract class DataProviderTest extends AbstractIdDataTest {

    protected DataProvider provider;
    protected int dataLength;
    protected Map<String,double[]> data;
    protected int subDataStart;
    protected int subDataEnd;

    abstract protected DataProvider getProvider(int id, int dataLength, Map<String,double[]> data);
    abstract protected DataProvider getUninitializedProvider(Set<String> variables);

    protected void addSubDataTest(List<Class<? extends TestBase>> additionalClassContainer) {
        additionalClassContainer.add(DataProviderSubDataTest.class);
    }

    protected Collection<Class<? extends TestBase>> getAdditionalTestClasses() {
        List<Class<? extends TestBase>> adds = new LinkedList<Class<? extends TestBase>>();
        addSubDataTest(adds);
        adds.addAll(super.getAdditionalTestClasses());
        return adds;
    }

    protected Map<String,double[]> getData(int length) {
        Map<String,double[]> data = new HashMap<String,double[]>();
        for (int i : range(random.nextInt(5,10)))
            data.put(random.nextAsciiString(10),random.nextDoubles(length));
        return data;
    }

    protected IdData getConcreteDataId(int id) {
        return provider = getProvider(id,dataLength,data);
    }

    protected void beforeTestData() {
        dataLength = random.nextInt(50,200);
        data = getData(dataLength);
        subDataStart = random.nextInt(dataLength/2);
        subDataEnd = random.nextInt(dataLength/2,dataLength);
    }

    @Before
    public void beforeTest() {
        beforeTestData();
        super.beforeTest();
        provider = (DataProvider) idData;
    }

    @Test
    public void testGetDataLength() {
        assertEquals(dataLength,provider.getDataLength());
    }

    @Test
    public void testHasVariableTrue() {
        assertTrue(provider.hasVariable(data.keySet().iterator().next()));
    }

    @Test
    public void testHasVariableFalse() {
        String variable;
        while (data.containsKey(variable = random.nextAsciiString(10)));
        assertFalse(provider.hasVariable(variable));
    }

    @Test
    public void testGetVariableData() {
        String variable = null;
        int n = random.nextInt(data.size());
        Iterator<String> it = data.keySet().iterator();
        while (n-- > -1)
            variable = it.next();
        assertArrayAlmostEquals(data.get(variable),provider.getVariableData(variable));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetVariableDataBadVariable() {
        String variable;
        while (data.containsKey(variable = random.nextAsciiString(10)));
        provider.getVariableData(variable);
    }

    @Test(expected=IllegalStateException.class)
    public void testGetVariableDataUninitialized() {
        DataProvider up = getUninitializedProvider(data.keySet());
        if (up != null)
            up.getVariableData(data.keySet().iterator().next());
        else
            throw new IllegalStateException("No uninitialize provider available");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetSubDataEndLessThanStart() {
        provider.getSubData(subDataEnd,subDataStart);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetSubDataZeroLength() {
        provider.getSubData(subDataStart,subDataStart);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetSubDataStartTooSmall() {
        provider.getSubData(-1,subDataEnd);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetSubDataEndTooBig() {
        provider.getSubData(subDataStart,dataLength+1);
    }

    protected List<String> getVariableList() {
        List<String> variables = new LinkedList<String>();
        for (String variable : data.keySet())
            if (random.nextBoolean())
                variables.add(variable);
        if (variables.size() == 0)
            variables.add(data.keySet().iterator().next());
        variables.add(variables.get(0));
        return variables;
    }

    @Test
    public void testGetData() {
        List<String> variables = getVariableList();
        double[][] vData = new double[dataLength][variables.size()];
        int counter = 0;
        for (String variable : variables) {
            double[] d = data.get(variable);
            for (int i : range(d.length))
                vData[i][counter] = d[i];
            counter++;
        }
        assertArrayAlmostEquals(vData,provider.getData(variables).getTensorValues().getArray());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetDataBadVariable() {
        String variable;
        while (data.containsKey(variable = random.nextAsciiString(10)));
        List<String> variables = getVariableList();
        variables.add(variable);
        provider.getVariableData(variable);
    }

    @Test(expected=IllegalStateException.class)
    public void testGetDataUninitialized() {
        DataProvider up = getUninitializedProvider(data.keySet());
        if (up == null)
            throw new IllegalStateException("No uninitialize provider available");
        up.getData(getVariableList());

    }

    @Test
    public void testVariableSubData() {
        String variable = data.keySet().iterator().next();
        assertArrayAlmostEquals(Arrays.copyOfRange(data.get(variable),subDataStart,subDataEnd),provider.getVariableData(variable,subDataStart,subDataEnd));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testVariableSubDataBadVariable() {
        String variable;
        while (data.containsKey(variable = random.nextAsciiString(10)));
        provider.getVariableData(variable,subDataStart,subDataEnd);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testVariableSubDataEndLessThanStart() {
        provider.getVariableData(data.keySet().iterator().next(),subDataEnd,subDataStart);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testVariableSubDataStartTooSmall() {
        int end = random.nextInt(dataLength/2);
        provider.getVariableData(data.keySet().iterator().next(),-1,subDataEnd);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testVariableSubDataEndTooBig() {
        int start = random.nextInt(dataLength/2);
        provider.getVariableData(data.keySet().iterator().next(),subDataStart,dataLength+1);
    }

    @Test(expected=IllegalStateException.class)
    public void testVariableSubDataUninitilized() {
        DataProvider up = getUninitializedProvider(data.keySet());
        if (up == null)
            throw new IllegalStateException("No uninitialize provider available");
        String variable = data.keySet().iterator().next();
        up.getVariableData(variable,subDataStart,subDataEnd);
    }

    @Test
    public void testGetVariables() {
        assertEquals(data.keySet(),provider.getVariables());
    }

    @Test
    public void testGetAbsoluteStartIndex() {
        assertEquals(0,provider.getAbsoluteStartIndex());
    }

    public static class DataProviderSubDataTest extends DataProviderTest {
        protected DataProviderTest parent;

        protected void addSubDataTest(List<Class<? extends TestBase>> additionalClassContainer) { }

        protected void beforeTestData() {
            dataLength = parent.subDataEnd - parent.subDataStart;
            subDataStart = dataLength == 1 ? 0 : random.nextInt(dataLength/2);
            subDataEnd = random.nextInt(dataLength/2,dataLength);
            data = new HashMap<String,double[]>();
            for (String variable : parent.data.keySet())
                data.put(variable,Arrays.copyOfRange(parent.data.get(variable),parent.subDataStart,parent.subDataEnd));
        }

        @Before
        public void beforeTest() {
            parent = (DataProviderTest) getCallingContextInstance();
            parent.beforeTest();
            super.beforeTest();
            setAdditionalTestInformation(" (subdata)");
        }

        @Override
        protected DataProvider getProvider(int id, int dataLength, Map<String, double[]> data) {
            return parent.provider.getSubData(parent.subDataStart,parent.subDataEnd);
        }

        @Override
        protected DataProvider getUninitializedProvider(Set<String> variables) {
            return null;  //not available
        }

        @Test
        public void testGetAbsoluteStartIndex() {
            assertEquals(parent.subDataStart,provider.getAbsoluteStartIndex());
        }

        @Override
        @Test
        @Ignore
        public void testGetId() {
            //cannot set id for subdata
        }
    }
}
