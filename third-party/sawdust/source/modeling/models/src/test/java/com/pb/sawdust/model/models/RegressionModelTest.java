package com.pb.sawdust.model.models;

import com.pb.sawdust.model.models.utility.SimpleLinearUtility;
import com.pb.sawdust.model.models.utility.Utility;
import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.model.models.provider.SimpleDataProvider;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.collections.LinkedSetList;
import com.pb.sawdust.util.collections.SetList;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

/**
 * The {@code RegressionTest} ...
 *
 * @author crf <br/>
 *         Started Sep 27, 2010 6:46:21 PM
 */
public class RegressionModelTest extends TestBase {
    protected RegressionModel regressionModel;
    protected Utility utility;
    protected DataProvider data;
    protected SetList<String> variables;

    public static void main(String ... args) {
        TestBase.main();
    }


    protected DataProvider getDataProvider(Set<String> variables) {
        Map<String,double[]> data = new HashMap<String,double[]>();
        int length = random.nextInt(10,30);
        for (String variable : variables)
            data.put(variable,random.nextDoubles(length));
        //add extra variable
        data.put(random.nextAsciiString(10),random.nextDoubles(length));
        return new SimpleDataProvider(data, ArrayTensor.getFactory());
    }

    @Before
    public void beforeTest() {
        variables = new LinkedSetList<String>("a","b");
        utility = new SimpleLinearUtility(variables,Arrays.asList(ArrayUtil.toDoubleArray(random.nextDoubles(variables.size()))),ArrayTensor.getFactory());
        data = getDataProvider(variables);
        regressionModel = new RegressionModel(utility);
    }

    @Test
    public void testRunModel() {
        assertTrue(TensorUtil.equals(utility.getUtilities(data),regressionModel.runModel(data)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testRunModelMissingVariable() {
        Set<String> badVars = new HashSet<String> (variables);
        badVars.remove(badVars.iterator().next());
        regressionModel.runModel(getDataProvider(badVars));
    }

}
