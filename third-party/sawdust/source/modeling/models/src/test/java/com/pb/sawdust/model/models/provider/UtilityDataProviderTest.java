package com.pb.sawdust.model.models.provider;

import com.pb.sawdust.model.models.utility.LinearCompositeUtility;
import com.pb.sawdust.model.models.utility.LinearUtility;
import com.pb.sawdust.model.models.utility.SimpleLinearUtility;
import com.pb.sawdust.model.models.utility.Utility;
import com.pb.sawdust.tensor.ArrayTensor;
import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.util.collections.ArraySetList;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * The {@code UtilityDataProviderClass} ...
 *
 * @author crf <br/>
 *         Started Sep 26, 2010 9:43:17 PM
 */
public class UtilityDataProviderTest extends DataProviderTest {
    protected UtilityDataProvider utilityProvider;
    protected Map<String,Utility> utilityMap;
    protected DataProvider baseProvider;

    protected final String baseProviderVariableExtension = "_source";

    public static void main(String ... args) {
        TestBase.main();
    }

    protected void addSubDataTest(List<Class<? extends TestBase>> additionalClassContainer) {
        additionalClassContainer.add(UtilityDataProviderSubDataTest.class);
    }

    @Before
    public void beforeTest() {
        super.beforeTest();
        utilityProvider = (UtilityDataProvider) provider;
    }


    @Override
    protected DataProvider getProvider(int id, int dataLength, Map<String,double[]> data) {
        Map<String,double[]> newData = new HashMap<String,double[]>(data);
        Set<String> variables = new HashSet<String>(newData.keySet());
        for (String variable : variables)
            newData.put(variable + baseProviderVariableExtension,newData.remove(variable));
        baseProvider = new SimpleDataProvider(newData,ArrayTensor.getFactory());
        //reverse implement utility
        utilityMap = new HashMap<String,Utility>();
        for (String variable : data.keySet())
            utilityMap.put(variable,new SimpleLinearUtility(new ArraySetList<String>(variable + baseProviderVariableExtension), Arrays.asList(1.0),ArrayTensor.getFactory()));
        return new UtilityDataProvider(id,baseProvider,utilityMap,ArrayTensor.getFactory());
    }

    @Override
    protected DataProvider getUninitializedProvider(Set<String> variables) {
        return null;  //no uninitialized utility data providers
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorUtilityVariableMissing() {
        Map<String,Utility> newUtilityMap = new HashMap<String,Utility>(utilityMap);
        String variableToChange = newUtilityMap.keySet().iterator().next();
        LinearUtility utilityToChange = (LinearUtility) newUtilityMap.get(variableToChange);
        String newVariable;
        while ((newVariable = random.nextAsciiString(10)).equals(variableToChange));
        Utility newUt = new LinearCompositeUtility(ArrayTensor.getFactory(),utilityToChange,new SimpleLinearUtility(new ArraySetList<String>(newVariable), Arrays.asList(1.0),ArrayTensor.getFactory()));
        newUtilityMap.put(variableToChange,newUt);
        new UtilityDataProvider(id,baseProvider,newUtilityMap,ArrayTensor.getFactory());
    }

    @Test
    public void testBaseHasVariableTrue() {
        assertTrue(provider.hasVariable(data.keySet().iterator().next()+baseProviderVariableExtension));
    }

    @Test
    public void testBaseGetVariableData() {
        String variable = null;
        int n = random.nextInt(data.size());
        Iterator<String> it = data.keySet().iterator();
        while (n-- > -1)
            variable = it.next();
        assertArrayAlmostEquals(data.get(variable),provider.getVariableData(variable+baseProviderVariableExtension));
    }

    @Test
    public void testBaseVariableSubData() {
        String variable = data.keySet().iterator().next();
        assertArrayAlmostEquals(Arrays.copyOfRange(data.get(variable),subDataStart,subDataEnd),provider.getVariableData(variable+baseProviderVariableExtension,subDataStart,subDataEnd));
    }

    @Test
    public void testGetData() { //make sure it is a mix of base and utility components
        List<String> variables = getVariableList();
        double[][] vData = new double[dataLength][variables.size()];
        int counter = 0;
        for (String variable : variables) {
            double[] d = data.get(variable);
            for (int i : range(d.length))
                vData[i][counter] = d[i];
            counter++;
        }
        for (int i : range(0,variables.size(),2))
            variables.set(i,variables.get(i) + baseProviderVariableExtension);
        assertArrayAlmostEquals(vData,provider.getData(variables).getTensorValues().getArray());
    }

    @Override
    @Test
    public void testGetVariables() {
        Set<String> variables = new HashSet<String>(baseProvider.getVariables());
        variables.addAll(utilityMap.keySet());
        assertEquals(variables,provider.getVariables());
    }

    public static class UtilityDataProviderSubDataTest extends DataProviderSubDataTest {
        protected UtilityDataProviderTest udpParent;

        @Before
        public void beforeTest() {
            super.beforeTest();
            udpParent = (UtilityDataProviderTest) parent;
        }

        @Override
        @Test
        public void testGetVariables() {
            Set<String> variables = new HashSet<String>(udpParent.baseProvider.getVariables());
            variables.addAll(udpParent.utilityMap.keySet());
            assertEquals(variables,provider.getVariables());
        }

    }
}
