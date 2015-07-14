package com.pb.sawdust.model.models.provider;

import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Map;
import java.util.Set;

/**
 * The {@code ExpandableDataProviderTest} ...
 *
 * @author crf <br/>
 *         Started Sep 25, 2010 9:49:48 PM
 */
public class ExpandableDataProviderTest extends DataProviderTest {
    protected ExpandableDataProvider expandableProvider;

    public static void main(String ... args) {
        TestBase.main();
    }

    @Before
    public void beforeTest() {
        super.beforeTest();
        expandableProvider = (ExpandableDataProvider) provider;
    }

    @Override
    protected DataProvider getProvider(int id, int length, Map<String, double[]> data) {
        ExpandableDataProvider provider = new ExpandableDataProvider(id,length,ArrayTensor.getFactory());
        for (String variable : data.keySet())
            provider.addVariable(variable,data.get(variable));
        return provider;
    }

    @Override
    protected DataProvider getUninitializedProvider(Set<String> variables) {
        return null; //cannot be uninitialized
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorNegativeLength() {
        getProvider(id,-1,data);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorZeroLength() {
        getProvider(id,0,data);
    } 

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorBadId() {
        //assumes haven't done Integer.MAX_VALUE data ids in between
        getProvider(id+Integer.MAX_VALUE,provider.getDataLength(),data);
    }

    @Test
    public void testAddVariable() {
        double[] vData = random.nextDoubles(dataLength);
        String variable;
        while (data.containsKey(variable = random.nextAsciiString(10)));
        expandableProvider.addVariable(variable,vData);
        assertTrue(provider.hasVariable(variable));
    }

    @Test
    public void testAddVariableData() {
        double[] vData = random.nextDoubles(dataLength);
        String variable;
        while (data.containsKey(variable = random.nextAsciiString(10)));
        expandableProvider.addVariable(variable,vData);
        assertArrayAlmostEquals(vData,provider.getVariableData(variable));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddVariableTooShort() {
        double[] vData = random.nextDoubles(dataLength-random.nextInt(1,dataLength/2));
        String variable;
        while (data.containsKey(variable = random.nextAsciiString(10)));
        expandableProvider.addVariable(variable,vData);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddVariableTooLong() {
        double[] vData = random.nextDoubles(dataLength+random.nextInt(1,dataLength));
        String variable;
        while (data.containsKey(variable = random.nextAsciiString(10)));
        expandableProvider.addVariable(variable,vData);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddVariableDuplicate() {
        double[] vData = random.nextDoubles(dataLength);
        String variable = data.keySet().iterator().next();
        expandableProvider.addVariable(variable,vData);
    }
}
