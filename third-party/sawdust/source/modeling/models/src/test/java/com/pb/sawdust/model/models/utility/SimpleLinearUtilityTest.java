package com.pb.sawdust.model.models.utility;

import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.collections.ArraySetList;
import com.pb.sawdust.util.collections.SetList;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Test;

import java.util.Arrays;

/**
 * The {@code SimpleLinearUtilityTest} ...
 *
 * @author crf <br/>
 *         Started Sep 16, 2010 9:27:08 PM
 */
public class SimpleLinearUtilityTest extends LinearUtilityTest {

    public static void main(String ... args) {
        TestBase.main();
    }
    
    @Override
    protected LinearUtility getUtility(SetList<String> variables, double[] coefficients) {
        return new SimpleLinearUtility(variables, Arrays.asList(ArrayUtil.toDoubleArray(coefficients)), ArrayTensor.getFactory());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorMismatchedVariablesAndCoefficients() {
        getUtility(new ArraySetList<String>("a","b","c"),random.nextDoubles(4));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorEmptyVariablesAndCoefficients() {
        getUtility(new ArraySetList<String>(),random.nextDoubles(0));
    }
}
