package com.pb.sawdust.model.models.provider.hub;

import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;

/**
 * The {@code FullCalculationPolyDataProviderTest} ...
 *
 * @author crf <br/>
 *         Started 2/17/11 10:53 AM
 */
public class FullCalculationPolyDataProviderTest extends SimpleCalculationPolyDataProviderTest {
    protected FullCalculationPolyDataProvider<String> fullPolyDataProvider;

    public static void main(String ... args) {
        TestBase.main();
    }

    protected SimpleCalculationPolyDataProvider<String> getCalculationProvider(int id, PolyDataProvider<String> baseProvider) {
        return new FullCalculationPolyDataProvider<String>(id,baseProvider,ArrayTensor.getFactory());
    }

    @Before
    public void beforeTest() {
        super.beforeTest();
        fullPolyDataProvider = (FullCalculationPolyDataProvider<String>) calculationPolyDataProvider;
    }
}
