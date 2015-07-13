package com.pb.sawdust.calculator.tensor;

import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.pb.sawdust.util.Range.range;

/**
 * The {@code DefaultElementByElementTensorCalculationTest} ...
 *
 * @author crf <br/>
 *         Started Nov 24, 2010 8:12:12 AM
 */
public class DefaultCellWiseTensorCalculationTest extends AbstractCellWiseTensorCalculationTest {
    public static final String PARALLEL_MODE_KEY = "parallel mode";

    public static void main(String ... args) {
        List<Map<String,Object>> cc = new LinkedList<Map<String,Object>>();
        for (boolean b : new boolean[] {true,false})
            cc.add(buildContext(PARALLEL_MODE_KEY,b));
        addClassRunContext(DefaultCellWiseTensorCalculationTest.class,cc);
        addClassRunContext(getCellWiseTensorMutatingCalculationTestClass(),cc);
        TestBase.main();
    }

    private boolean parallelMode = true;

    @Override
    protected AbstractCellWiseTensorCalculation getCellWiseTensorCalculation(TensorFactory factory) {
        if (parallelMode)
            return new DefaultCellWiseTensorCalculation(factory,2); //parallel limit low enough to force into parallel mode
        else
            return new DefaultCellWiseTensorCalculation(factory);
    }

    @Before
    public void beforeTest() {
        super.beforeTest();
        parallelMode = (Boolean) getTestData(PARALLEL_MODE_KEY);
    }
}
