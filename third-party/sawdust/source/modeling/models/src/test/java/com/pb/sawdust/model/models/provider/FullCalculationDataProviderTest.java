package com.pb.sawdust.model.models.provider;

import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;

import java.util.Map;

/**
 * The {@code FullCalculationDataProviderTest} ...
 *
 * @author crf <br/>
 *         Started 2/17/11 11:09 PM
 */
public class FullCalculationDataProviderTest extends SimpleCalculationDataProviderTest {
    protected FullCalculationDataProvider fullCalculationDataProvider;

    public static void main(String ... args) {
        TestBase.main();
    }

    @Override
    protected DataProvider getProvider(int id, int length, Map<String, double[]> data) {
        DataProvider baseProvider = new SimpleDataProvider(data, ArrayTensor.getFactory());
        return new FullCalculationDataProvider(id,baseProvider,ArrayTensor.getFactory());
    }

    @Override
    @Before
    public void beforeTest() {
        super.beforeTest();
        fullCalculationDataProvider = (FullCalculationDataProvider) calculationDataProvider;
    }
}
