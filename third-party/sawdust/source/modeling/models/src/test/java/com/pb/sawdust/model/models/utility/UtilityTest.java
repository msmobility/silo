package com.pb.sawdust.model.models.utility;

import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.model.models.provider.SimpleDataProvider;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.util.test.TestBase;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

/**
 * The {@code UtilityTest} ...
 *
 * @author crf <br/>
 *         Started Sep 16, 2010 8:25:04 PM
 */
public abstract class UtilityTest extends TestBase {
    protected List<String> variables;
    protected double[] coefficients;
    protected Utility utility;

    abstract protected Utility getUtility(List<String> variables, double[] coefficients);
    abstract protected double[] getUtilityValues(DataProvider data);

    protected DataProvider getDataProvider(Set<String> variables) {
        Map<String,double[]> data = new HashMap<String,double[]>();
        int length = random.nextInt(10,30);
        for (String variable : variables)
            data.put(variable,random.nextDoubles(length));
        //add extra variable to avoid uninitialized issues and to make sure that extra variables don't matter
        data.put(random.nextAsciiString(10),random.nextDoubles(length));
        return new SimpleDataProvider(data,ArrayTensor.getFactory());
    }

    protected List<String> getVariables() {
        return Arrays.asList("a","b","a");
    }

    private Set<String> getUniqueVariables() {
        return new HashSet<String>(variables);
    }

    protected double[] getCoefficients() {
        return random.nextDoubles(variables.size());
    }

    @Before
    public void beforeTest() {
        variables = getVariables();
        coefficients = getCoefficients();
        utility = getUtility(variables,coefficients);
    }

    @Test
    public void testGetVariables() {
        assertEquals(variables,utility.getVariables());
    }

    @Test
    public void testGetVariableSet() {
        assertEquals(new HashSet<String>(variables),utility.getVariableSet());
    }

    @Test
    public void testGetCoefficients() {
        assertArrayAlmostEquals(coefficients,(double[]) utility.getCoefficients().getTensorValues().getArray());
    }

    @Test
    public void testGetUtilities() {
        DataProvider provider = getDataProvider(getUniqueVariables());
        assertArrayAlmostEquals(getUtilityValues(provider),(double[]) utility.getUtilities(provider).getTensorValues().getArray());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetUtilitiesMissingVariable() {
        Set<String> v = getUniqueVariables();
        v.remove(v.iterator().next());
        utility.getUtilities(getDataProvider(v));
    }
}
