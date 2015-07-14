package com.pb.sawdust.model.models.provider;

import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.basic.ColumnDataTable;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.metadata.TableSchema;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.pb.sawdust.util.Range.*;
import static org.junit.Assert.assertEquals;

import java.util.*;

/**
 * The {@code CompositeDataProviderTest} ...
 *
 * @author crf <br/>
 *         Started Sep 26, 2010 3:22:01 PM
 */
public class CompositeDataProviderTest extends DataProviderTest {
    protected CompositeDataProvider compositeProvider;
    protected Set<DataProvider> subProviders;
    protected DataTable extraProviderTable;
    protected DataProvider extraProvider;

    public static void main(String ... args) {
        TestBase.main();
    }

    protected void addSubDataTest(List<Class<? extends TestBase>> additionalClassContainer) {
        additionalClassContainer.add(CompositeDataProviderSubDataTest.class);
    }

    @Override
    protected DataProvider getProvider(int id, int dataLength, Map<String,double[]> data) {
        subProviders = new HashSet<DataProvider>();
        int providerCount = 3;
        int cutoff = data.size() / providerCount;
        Iterator<String> it = data.keySet().iterator();
        int counter = 0;
        for (int i : range(providerCount)) {
            Map<String,double[]> subData = new HashMap<String,double[]>();
            while (counter < cutoff*(i+1)) {
                String variable = it.next();
                subData.put(variable,data.get(variable));
                counter++;
            }
            if (i == providerCount - 1) { //get remainder
                while (it.hasNext()) {
                    String variable = it.next();
                    subData.put(variable,data.get(variable));
                }
            }
            subProviders.add(new SimpleDataProvider(subData,ArrayTensor.getFactory()));
        }

        extraProviderTable = new ColumnDataTable(new TableSchema("extra"));
        String newVariable;
        while (data.containsKey(newVariable = random.nextAsciiString(9)));
        extraProviderTable.addColumn(newVariable,new double[dataLength],DataType.DOUBLE); //ignored, just for initialization
        extraProvider = new DataTableDataProvider(extraProviderTable,ArrayTensor.getFactory());
        subProviders.add(extraProvider);
        return new CompositeDataProvider(ArrayTensor.getFactory(),subProviders.toArray(new DataProvider[subProviders.size()]));
    }

    @Override
    protected DataProvider getUninitializedProvider(Set<String> variables) {
        //return new CompositeDataProvider(ArrayTensor.getFactory());
        return null;
    }

    @Override
    @Before
    public void beforeTest() {
        super.beforeTest();
        compositeProvider = (CompositeDataProvider) provider;
    }

    @Override
    @Ignore
    @Test
    public void testGetId() {
        //no way to set id
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorUnmatchedProviders() {
        Set<DataProvider> prov = new HashSet<DataProvider>(subProviders);
        //add bad provider
        Map<String,double[]> bad = new HashMap<String,double[]>();
        bad.put(random.nextAsciiString(6),random.nextDoubles(dataLength+1));
        prov.add(new SimpleDataProvider(bad,ArrayTensor.getFactory()));
        new CompositeDataProvider(ArrayTensor.getFactory(),prov.toArray(new DataProvider[subProviders.size()]));
    }

    @Test
    public void testGetVariableDataMutable() {
        String newVariable;
        while (provider.hasVariable(newVariable = random.nextAsciiString(9)));
        double[] data = random.nextDoubles(extraProviderTable.getRowCount());
        extraProviderTable.addColumn(newVariable,data,DataType.DOUBLE);
        assertArrayAlmostEquals(Arrays.copyOfRange(data,0,dataLength),provider.getVariableData(newVariable));
    }

    @Test(expected=IllegalStateException.class)
    public void testGetLengthUnequalDataLengths() {
        String newVariable = random.nextAsciiString(9);
        extraProviderTable.addColumn(newVariable,random.nextDoubles(extraProviderTable.getRowCount()),DataType.DOUBLE);
        extraProviderTable.deleteRow(0);
        provider.getDataLength();
    }

    @Test(expected=IllegalStateException.class)
    public void testGetVariableDataUnequalDataLengths() {
        String newVariable = random.nextAsciiString(9);
        extraProviderTable.addColumn(newVariable,random.nextDoubles(extraProviderTable.getRowCount()),DataType.DOUBLE);
        extraProviderTable.deleteRow(0);
        provider.getVariableData(newVariable);
    }

    @Test(expected=IllegalStateException.class)
    public void testGetVariableSubDataUnequalDataLengths() {
        String newVariable = random.nextAsciiString(9);
        extraProviderTable.addColumn(newVariable,random.nextDoubles(extraProviderTable.getRowCount()),DataType.DOUBLE);
        extraProviderTable.deleteRow(0);
        provider.getVariableData(newVariable,subDataStart,subDataEnd);
    }

    @Test(expected=IllegalStateException.class)
    public void testGetDataUnequalDataLengths() {
        String newVariable = random.nextAsciiString(9);
        extraProviderTable.addColumn(newVariable,random.nextDoubles(extraProviderTable.getRowCount()),DataType.DOUBLE);
        extraProviderTable.deleteRow(0);
        provider.getData(Arrays.asList(newVariable));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddSelf() {
        compositeProvider.addProvider(compositeProvider);
    }

    @Override
    @Test
    public void testGetVariables() {
        Set<String> variables = new HashSet<String>();
        for (DataProvider p : subProviders)
            variables.addAll(p.getVariables());
        assertEquals(variables,provider.getVariables());
    }

    public static class CompositeDataProviderSubDataTest extends DataProviderSubDataTest {
        protected CompositeDataProviderTest cdpParent;

        @Before
        public void beforeTest() {
            super.beforeTest();
            cdpParent = (CompositeDataProviderTest) parent;
        }

        @Override
        @Test
        public void testGetVariables() {
            Set<String> variables = new HashSet<String>();
            for (DataProvider p : cdpParent.subProviders)
                variables.addAll(p.getVariables());
            assertEquals(variables,provider.getVariables());
        }

    }
}
