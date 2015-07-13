package com.pb.sawdust.model.models.utility;

import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.collections.ArraySetList;
import com.pb.sawdust.util.collections.LinkedSetList;
import com.pb.sawdust.util.collections.SetList;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * The {@code LinearCompositeUtilityTest} ...
 *
 * @author crf <br/>
 *         Started Sep 17, 2010 12:23:04 AM
 */
public class LinearCompositeUtilityTest extends LinearUtilityTest {

    public static void main(String ... args) {
        TestBase.main();
    }

    private List<LinearUtility> utilities;
    private SetList<String> allVariables;
    private double[] allCoefficients;

    @Before
    public void beforeTest() {
        SetList<String> v1 = new ArraySetList<String>("1a","1b","1c");
        SetList<String> v2 = new ArraySetList<String>("2a","2b");
        SetList<String> v3 = new ArraySetList<String>("1a","3b","3c","3d"); //note that first element is the same as v1[0]
        allVariables = new LinkedSetList<String>();
        allVariables.addAll(v1);
        allVariables.addAll(v2);
        allVariables.addAll(v3);

        double[] c1 = random.nextDoubles(v1.size());
        double[] c2 = random.nextDoubles(v2.size());
        double[] c3 = random.nextDoubles(v3.size());

        allCoefficients = new double[c1.length+c2.length+c3.length-1];
        System.arraycopy(c1,0,allCoefficients,0,c1.length);
        System.arraycopy(c2,0,allCoefficients,c1.length,c2.length);
        System.arraycopy(c3,1,allCoefficients,c1.length+c2.length,c3.length-1);
        allCoefficients[0] += c3[0];

        TensorFactory factory = ArrayTensor.getFactory();
        utilities = new LinkedList<LinearUtility>();
        utilities.add(new SimpleLinearUtility(v1,Arrays.asList(ArrayUtil.toDoubleArray(c1)),factory));
        utilities.add(new SimpleLinearUtility(v2,Arrays.asList(ArrayUtil.toDoubleArray(c2)),factory));
        utilities.add(new SimpleLinearUtility(v3,Arrays.asList(ArrayUtil.toDoubleArray(c3)),factory));

        super.beforeTest();
    }


    protected SetList<String> getVariables() {
        return allVariables;
    }

    protected double[] getCoefficients() {
        return allCoefficients;
    }

    @Override
    protected LinearUtility getUtility(SetList<String> variables, double[] coefficients) {
        return new LinearCompositeUtility(ArrayTensor.getFactory(),utilities.toArray(new LinearUtility[utilities.size()]));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorEmptyUtilities() {
        new LinearCompositeUtility(ArrayTensor.getFactory());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorAllUtilitiesEmpty() {
        new LinearCompositeUtility(ArrayTensor.getFactory(),EmptyUtility.getEmptyLinearUtility(),EmptyUtility.getEmptyLinearUtility());
    }

    @Test
    public void testConstructorWithEmptyUtility() {
        LinearUtility ut = new LinearCompositeUtility(ArrayTensor.getFactory(),EmptyUtility.getEmptyLinearUtility(),linearUtility);
        assertEquals(variables,ut.getVariables());
    }
}
