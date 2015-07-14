package com.pb.sawdust.model.models.utility;

import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.util.collections.ArraySetList;
import com.pb.sawdust.util.collections.SetList;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * The {@code LinearUtilityTest} ...
 *
 * @author crf <br/>
 *         Started Sep 16, 2010 9:21:22 PM
 */
public abstract class LinearUtilityTest extends UtilityTest {
    protected SetList<String> linearVariables;
    protected LinearUtility linearUtility;

    abstract protected LinearUtility getUtility(SetList<String> variables, double[] coefficients);

    protected Utility getUtility(List<String> variables, double[] coefficients) {
        return getUtility((SetList<String>) variables,coefficients); //kind of hacky, but this should only be called using SetList variables
    }

    protected SetList<String> getVariables() {
        return new ArraySetList<String>("a","b","c");
    }

    protected double[] getUtilityValues(DataProvider data) {
        double[] result = new double[data.getDataLength()];
        int counter = 0;
        for (String variable : linearVariables) {
            double coefficient = coefficients[counter++];
            double[] vd = data.getVariableData(variable);
            for (int i = 0; i < result.length; i++)
                result[i] += coefficient*vd[i];
        }
        return result;
    }

    @Before
    public void beforeTest() {
        super.beforeTest();
        linearVariables = (SetList<String>) variables;
        linearUtility = (LinearUtility) utility;
    }

    @Test
    public void testGetCoefficient() {
        int ind = random.nextInt(linearVariables.size());
        assertAlmostEquals(coefficients[ind],linearUtility.getCoefficient(linearVariables.get(ind)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetCoefficientNoVariable() {
        String variable = random.nextAsciiString(4);
        while (linearVariables.contains(variable))
            variable = random.nextAsciiString(4);
        linearUtility.getCoefficient(variable);
    }
}
